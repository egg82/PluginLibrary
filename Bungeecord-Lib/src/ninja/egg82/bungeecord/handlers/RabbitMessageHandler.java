package ninja.egg82.bungeecord.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.AMQP.BasicProperties;

import ninja.egg82.bungeecord.BasePlugin;
import ninja.egg82.bungeecord.commands.AsyncMessageCommand;
import ninja.egg82.bungeecord.core.RabbitMessageQueueData;
import ninja.egg82.bungeecord.enums.MessageHandlerType;
import ninja.egg82.bungeecord.enums.SenderType;
import ninja.egg82.concurrent.DynamicConcurrentDeque;
import ninja.egg82.concurrent.FixedConcurrentDeque;
import ninja.egg82.concurrent.IConcurrentDeque;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.tuples.Unit;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;
import ninja.egg82.utils.ThreadUtil;

public class RabbitMessageHandler implements IMessageHandler {
	//vars
	
	// Rabbit connection
	private Connection conn = null;
	// Channel - one per plugin
	private volatile Channel channel = null;
	
	// Queue names. Called "channels" because it's easier for plugin devs to understand
	private IConcurrentDeque<String> channelNames = new DynamicConcurrentDeque<String>();
	// Message backlog/queue - for storing messages in case of disconnect
	private IConcurrentDeque<RabbitMessageQueueData> backlog = new FixedConcurrentDeque<RabbitMessageQueueData>(150);
	// Connected state. Atomic because multithreading is HARD
	private AtomicBoolean connected = new AtomicBoolean(false);
	
	// Thread pool for message sending thread. The thread count for actually sending messages should never exceed 1 for data consistency
	private ScheduledExecutorService threadPool = null;
	// Custom thread pool for incoming messages that uses a min + max. Extra threads expire after 2 mins
	private ScheduledExecutorService recvPool = null;
	
	// The plugin using this code
	private BasePlugin plugin = ServiceLocator.getService(BasePlugin.class);
	// Args used for declaring new queues, or "channels"
	private Map<String, Object> queueArgs = new HashMap<String, Object>();
	// The name of the direct exchange to use
	private String exchangeName = "ninja-egg82-plugin-broadcast";
	// This server's sender ID, for replying directly to the server
	private volatile String senderId = null;
	// Properties used for sending messages
	private BasicProperties props = null;
	
	// Map for storing commands to run when a message is received
	private ConcurrentHashMap<Class<? extends AsyncMessageCommand>, Unit<AsyncMessageCommand>> commands = new ConcurrentHashMap<Class<? extends AsyncMessageCommand>, Unit<AsyncMessageCommand>>();
	
	//constructor
	public RabbitMessageHandler(String ip, int port, String username, String password) {
		this(ip, port, username, password, "/");
	}
	public RabbitMessageHandler(String ip, int port, String username, String password, String vHost) {
		if (ip == null) {
			throw new ArgumentNullException("ip");
		}
		if (port <= 0 || port > 65535) {
			throw new IllegalArgumentException("port cannot be <= 0 or > 65535");
		}
		if (username == null) {
			throw new ArgumentNullException("username");
		}
		if (password == null) {
			throw new ArgumentNullException("password");
		}
		
		threadPool = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat((plugin != null) ? plugin.getDescription().getName() + "-RabbitMQ_snd-%d" : "RabbitMQ_snd-%d").build());
		recvPool = ThreadUtil.createScheduledPool(3, Runtime.getRuntime().availableProcessors(), 120L * 1000L, new ThreadFactoryBuilder().setNameFormat((plugin != null) ? plugin.getDescription().getName() + "-RabbitMQ_recv-%d" : "RabbitMQ_recv-%d").build());
		
		// Temporary workaround for Mainframe using Bungee libraries when it's not actually running under Bungee
		senderId = (plugin != null) ? plugin.getServerId() : UUID.randomUUID().toString();
		
		// Need to make sure if something bad happens the queues don't fill up with messages
		queueArgs.put("x-message-ttl", Integer.valueOf(60 * 1000));
		queueArgs.put("x-expires", Integer.valueOf(300 * 1000));
		
		// Add basic properties like reply-to and sender type
		props = new BasicProperties.Builder().replyTo(senderId).type(SenderType.BUNGEE.name()).deliveryMode(Integer.valueOf(2)).build();
		
		// Create a new connection and pre-populate values
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(ip);
		factory.setPort(port);
		factory.setUsername(username);
		factory.setPassword(password);
		factory.setVirtualHost(vHost);
		
		// Recover connection
		factory.setAutomaticRecoveryEnabled(true);
		// Recover queues
		factory.setTopologyRecoveryEnabled(true);
		// Heartbeat sent every 5 seconds
		factory.setRequestedHeartbeat(10);
		// Connection timeout after 10 seconds to retry
		factory.setConnectionTimeout(10000);
		// Wait 3 seconds before retrying
		factory.setNetworkRecoveryInterval(3000);
		
		try {
			// SSL with cert trust
			ConnectionFactory sslFactory = factory.clone();
			sslFactory.useSslProtocol("TLSv1.2");
			conn = sslFactory.newConnection(recvPool);
		} catch (Exception ex) {
			try {
				// SSL without cert trust
				ConnectionFactory sslFactory = factory.clone();
				sslFactory.useSslProtocol();
				conn = sslFactory.newConnection(recvPool);
			} catch (Exception ex2) {
				try {
					// Plaintext
					conn = factory.newConnection(recvPool);
				} catch (Exception ex3) {
					ServiceLocator.getService(IExceptionHandler.class).silentException(ex3);
					throw new RuntimeException("Cannot create RabbitMQ connection.", ex3);
				}
			}
		}
		
		// Create a new channel. One per plugin using this library is usually enough
		try {
			channel = conn.createChannel();
			channel.addShutdownListener(onChannelShutdown);
			channel.exchangeDeclare(exchangeName, "direct", true);
			channel.txSelect();
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw new RuntimeException("Cannot create RabbitMQ connection.", ex);
		}
		
		conn.addShutdownListener(onShutdown);
		
		// Start the flush timer and set the connected state
		threadPool.scheduleAtFixedRate(onBacklogThread, 150L, 150L, TimeUnit.MILLISECONDS);
		connected.set(true);
	}
	
	//public
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		if (senderId == null) {
			throw new ArgumentNullException("senderId");
		}
		
		// Store current queue/"channel" names and clear them. The queues NEED to be re-created
		String[] ch = channelNames.toArray(new String[0]);
		clearChannels();
		
		// Set the new sender ID and update it in the properties for all unsent and new messages
		this.senderId = senderId;
		props = new BasicProperties.Builder().replyTo(senderId).type(SenderType.BUNGEE.name()).deliveryMode(Integer.valueOf(2)).build();
		
		// Re-create the queues with the new name
		for (String c : ch) {
			createChannel(c);
		}
	}
	
	public void createChannel(String channelName) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		
		if (!connected.get()) {
			// Connection closed, throw an exception
			throw new IllegalStateException("Connection has been closed or was never able to be opened.");
		}
		
		if (channelNames.contains(channelName)) {
			// Channel already exists. No need to re-create it
			return;
		}
		
		// Queue name. Shows up on Rabbit's end
		String queueName = senderId + "-" + ((plugin != null) ? plugin.getDescription().getName() : "") + "-" + channelName;
		
		try {
			// Declare the queue
			channel.queueDeclareNoWait(queueName, true, false, false, queueArgs);
			// Bind the queue to accept all bungee broadcasts
			channel.queueBind(queueName, exchangeName, channelName + "-bungee");
			// Bind the queue to accept messages directed at this server
			channel.queueBind(queueName, exchangeName, channelName + "-" + senderId);
			// Add a consumer to the queue
			channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) {
					// Message sender
					String sender = properties.getReplyTo();
					// The sender type. Bukkit or Bungee?
					SenderType senderType = SenderType.valueOf(properties.getType());
					
					// The last exception thrown by the message command handlers
					Exception lastEx = null;
					// Loop the current message handlers
					for (Entry<Class<? extends AsyncMessageCommand>, Unit<AsyncMessageCommand>> kvp : commands.entrySet()) {
						// The current message handler
						AsyncMessageCommand c = null;
						
						// Lazy initialization. Create if not exists, or use the current handler if it does
						if (kvp.getValue().getType() == null) {
							try {
								c = createCommand(kvp.getKey());
								kvp.getValue().setType(c);
							} catch (Exception ex) {
								// Send the exception off to the current available handler handler and store it
								ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
								lastEx = ex;
								continue;
							}
						} else {
							c = kvp.getValue().getType();
						}
						
						// Set data values
						c.setSender(sender);
						c.setSenderType(senderType);
						c.setChannelName(channelName);
						c.setData(body);
						
						// Catch exceptions thrown by the handlers so it doesn't interrupt the loop
						try {
							c.start();
						} catch (Exception ex) {
							// Send the exception off to the current available handler handler and store it
							ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
							lastEx = ex;
						}
					}
					
					// Did an exception get thrown by the handlers? If so, re-throw it here
					if (lastEx != null) {
						throw new RuntimeException("Cannot run message command.", lastEx);
					}
				}
			});
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw new RuntimeException("Cannot create channel.", ex);
		}
		
		channelNames.add(channelName);
	}
	public void destroyChannel(String channelName) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		
		if (!connected.get()) {
			// Connection closed, throw an exception
			throw new IllegalStateException("Connection has been closed or was never able to be opened.");
		}
		
		if (!channelNames.remove(channelName)) {
			// Channel doesn't exist. No need to delete what isn't there
			return;
		}
		
		try {
			// Delete the queue and unbind everything on it
			channel.queueDeleteNoWait(senderId + "-" + channelName, true, false);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw new RuntimeException("Cannot destroy channel.", ex);
		}
	}
	
	public void sendToServer(String serverId, String channelName, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (serverId == null) {
			throw new ArgumentNullException("serverId");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		
		if (!connected.get()) {
			// Connection closed, throw an exception
			throw new IllegalStateException("Connection has been closed or was never able to be opened.");
		}
		if (!channelNames.contains(channelName)) {
			// Channel doesn't exist, throw an exception
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		// Grab a new data object
		RabbitMessageQueueData messageData = new RabbitMessageQueueData(channelName, channelName + "-" + serverId, data);
		// Add the new data to the send queue, tossing the oldest messages if needed
		while (!backlog.offerLast(messageData) && !backlog.isEmpty()) {
			backlog.pollFirst();
		}
		
		// Submit a new send task
		threadPool.submit(onSendThread);
	}
	public void broadcastToBungee(String channelName, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		
		if (!connected.get()) {
			// Connection closed, throw an exception
			throw new IllegalStateException("Connection has been closed or was never able to be opened.");
		}
		if (!channelNames.contains(channelName)) {
			// Channel doesn't exist, throw an exception
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		// Grab a new data object
		RabbitMessageQueueData messageData = new RabbitMessageQueueData(channelName, channelName + "-bungee", data);
		// Add the new data to the send queue, tossing the oldest messages if needed
		while (!backlog.offerLast(messageData) && !backlog.isEmpty()) {
			backlog.pollFirst();
		}
		
		// Submit a new send task
		threadPool.submit(onSendThread);
	}
	public void broadcastToBukkit(String channelName, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		
		if (!connected.get()) {
			// Connection closed, throw an exception
			throw new IllegalStateException("Connection has been closed or was never able to be opened.");
		}
		if (!channelNames.contains(channelName)) {
			// Channel doesn't exist, throw an exception
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		// Grab a new data object
		RabbitMessageQueueData messageData = new RabbitMessageQueueData(channelName, channelName + "-bukkit", data);
		// Add the new data to the send queue, tossing the oldest messages if needed
		while (!backlog.offerLast(messageData) && !backlog.isEmpty()) {
			backlog.pollFirst();
		}
		
		// Submit a new send task
		threadPool.submit(onSendThread);
	}
	
	public int addMessagesFromPackage(String packageName) {
		return addMessagesFromPackage(packageName, true);
	}
	public int addMessagesFromPackage(String packageName, boolean recursive) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		// Number of commands successfully added
		int numMessages = 0;
		
		// Reflect package (recursively, if specified) and get everything that extends AsyncMessageCommand
		List<Class<AsyncMessageCommand>> enums = ReflectUtil.getClasses(AsyncMessageCommand.class, packageName, recursive, false, false);
		// Iterate collection and add the commands
		for (Class<AsyncMessageCommand> c : enums) {
			if (addCommand(c)) {
				// Increment success counter
				numMessages++;
			}
		}
		
		return numMessages;
	}
	public boolean addCommand(Class<? extends AsyncMessageCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		// Create a new unit. This allows us to lazy-initialize commands and only use memory we need
		Unit<AsyncMessageCommand> unit = new Unit<AsyncMessageCommand>(null);
		// Add the new command. If it already existed in the collection then we return false; otherwise we return true
		return (CollectionUtil.putIfAbsent(commands, clazz, unit).hashCode() == unit.hashCode()) ? true : false;
	}
	public boolean removeCommand(Class<? extends AsyncMessageCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		// Remove the old command. If the collection was modified as a result of this operation we return true; else false
		return (commands.remove(clazz) != null) ? true : false;
	}
	
	public void clearCommands() {
		// Pretty easy removal. No need for special rules or logic
		commands.clear();
	}
	public void clearChannels() {
		// Iterate channel names and destroy each channel
		// Because of the nature of ObjectPool no ConcurrentModificationException will be thrown and all channels at time of iteration will be removed
		for (String c : channelNames) {
			try {
				destroyChannel(c);
			} catch (Exception ex) {
				
			}
		}
	}
	public void close() {
		// Set connected state to false, or return if it's already false
		if (!connected.getAndSet(false)) {
			return;
		}
		
		try {
			// Gracefully shut the thread pool down, waiting for 5 seconds if needed
			threadPool.shutdown();
			if (!threadPool.awaitTermination(5000L, TimeUnit.MILLISECONDS)) {
				// Less-than-gracefully kill the thread pool
				threadPool.shutdownNow();
			}
		} catch (Exception ex) {
			
		}
		
		// Clear the backlog, channels, and commands
		backlog.clear();
		clearChannels();
		clearCommands();
		
		// Close the connection gracefully
		try {
			conn.close();
		} catch (Exception ex) {
			// Close the connection forcefully if doing so gracefully fails
			try {
				conn.abort();
			} catch (Exception ex2) {
				// If this exception is ever raised something is really fucked. We'll ignore it.
			}
		}
		
		try {
			// Gracefully shut the recv pool down, waiting for 5 seconds if needed
			recvPool.shutdown();
			if (!recvPool.awaitTermination(5000L, TimeUnit.MILLISECONDS)) {
				// Less-than-gracefully kill the thread pool
				recvPool.shutdownNow();
			}
		} catch (Exception ex) {
			
		}
	}
	
	public MessageHandlerType getType() {
		return MessageHandlerType.RABBIT;
	}
	
	//private
	private Runnable onSendThread = new Runnable() {
		public void run() {
			// Iterate messages to be sent until the queue is empty
			while (!backlog.isEmpty()) {
				if (!connected.get()) {
					// We're no longer connected. Break out of the loop
					break;
				}
				
				// Grab the oldest data first
				RabbitMessageQueueData first = backlog.pollFirst();
				if (first == null) {
					// Data is null, which means we prematurely reached the end of the queue
					break;
				}
				
				// Try to push the data to the Rabbit queue
				try {
					// Publish the data and make sure it was committed
					channel.basicPublish(exchangeName, first.getRoutingKey(), props, first.getData());
					channel.txCommit();
				} catch (Exception ex) {
					// Message send failed. Re-add it to the front of the backlog
					backlog.addFirst(first);
				}
			}
		}
	};
	private Runnable onBacklogThread = new Runnable() {
		public void run() {
			// Check connection state and backlog count
			if (!connected.get() || backlog.isEmpty()) {
				return;
			}
			
			// Iterate messages to be sent until the queue is empty
			while (!backlog.isEmpty()) {
				if (!connected.get()) {
					// We're no longer connected. Break out of the loop
					break;
				}
				
				// Grab the oldest data first
				RabbitMessageQueueData first = backlog.pollFirst();
				if (first == null) {
					// Data is null, which means we prematurely reached the end of the queue
					break;
				}
				
				// Try to push the data to the Rabbit queue
				try {
					// Publish the data and make sure it was committed
					channel.basicPublish(exchangeName, first.getRoutingKey(), props, first.getData());
					channel.txCommit();
				} catch (Exception ex) {
					// Message send failed. Re-add it to the front of the backlog
					backlog.addFirst(first);
				}
			}
		}
	};
	
	private ShutdownListener onShutdown = new ShutdownListener() {
		public void shutdownCompleted(ShutdownSignalException cause) {
			if (!connected.get()) {
				// This was a standard shutdown. No need to panic
				return;
			}
			
			// Whoa. Something bad happened. Log it and throw it
			ServiceLocator.getService(IExceptionHandler.class).silentException(cause);
			throw cause;
		}
	};
	private ShutdownListener onChannelShutdown = new ShutdownListener() {
		public void shutdownCompleted(ShutdownSignalException cause) {
			if (!connected.get()) {
				// This was a standard shutdown. No need to panic
				return;
			}
			
			// Whoa. Something bad happened. Log it and throw it
			ServiceLocator.getService(IExceptionHandler.class).silentException(cause);
			throw cause;
		}
	};
	
	private AsyncMessageCommand createCommand(Class<? extends AsyncMessageCommand> c) {
		// The returned value
		AsyncMessageCommand run = null;
		
		// Try to create a new instance
		try {
			run = c.newInstance();
		} catch (Exception ex) {
			// Re-throw the exception
			throw new RuntimeException("Cannot initialize message command.", ex);
		}
		
		return run;
	}
}
