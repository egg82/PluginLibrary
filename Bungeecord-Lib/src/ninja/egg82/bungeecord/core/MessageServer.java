package ninja.egg82.bungeecord.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Timer;

import com.rabbitmq.client.AMQP.BasicProperties;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import ninja.egg82.bungeecord.BasePlugin;
import ninja.egg82.bungeecord.enums.RoutingType;
import ninja.egg82.bungeecord.enums.SenderType;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.FixedObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.events.EventArgs;
import ninja.egg82.patterns.events.EventHandler;
import ninja.egg82.patterns.tuples.Triplet;

public class MessageServer {
	//vars
	private final EventHandler<EventArgs> connect = new EventHandler<EventArgs>();
	private final EventHandler<EventArgs> disconnect = new EventHandler<EventArgs>();
	private final EventHandler<MessageEventArgs> message = new EventHandler<MessageEventArgs>();
	private final EventHandler<MessageErrorEventArgs> error = new EventHandler<MessageErrorEventArgs>();
	
	private Connection conn = null;
	private volatile Channel channel = null;
	
	private IObjectPool<String> channelNames = new DynamicObjectPool<String>();
	private IObjectPool<Triplet<String, String, byte[]>> backlog = null;
	private volatile boolean connected = false;
	
	private ThreadPoolExecutor executor = ServiceLocator.getService(ThreadPoolExecutor.class);
	private Lock reconnectLock = new ReentrantLock();
	private volatile Future<?> reconnectThread = null;
	private Lock sendLock = new ReentrantLock();
	private volatile Future<?> sendThread = null;
	private Timer backlogTimer = null;
	
	private BasePlugin plugin = ServiceLocator.getService(BasePlugin.class);
	private Map<String, Object> queueArgs = new HashMap<String, Object>();
	private String exchangeName = "ninja-egg82-plugin-broadcast";
	private String personalId = null;
	private BasicProperties props = null;
	
	//constructor
	public MessageServer() {
		personalId = (plugin != null) ? plugin.getServerId() : UUID.randomUUID().toString();
		
		queueArgs.put("x-message-ttl", 60000);
		queueArgs.put("x-expires", 300000);
		
		props = new BasicProperties.Builder().replyTo(personalId).type(SenderType.BUNGEE.name()).deliveryMode(2).build();
		
		backlogTimer = new Timer(150, onBacklogTimer);
		backlogTimer.setRepeats(true);
	}
	
	//public
	public void connect(String ip, int port, String username, String password) {
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
		
		ConnectionFactory factory = new ConnectionFactory();
		
		factory.setHost(ip);
		factory.setPort(port);
		factory.setUsername(username);
		factory.setPassword(password);
		
		factory.setAutomaticRecoveryEnabled(false);
		factory.setRequestedHeartbeat(2);
		factory.setConnectionTimeout(10000);
		factory.setNetworkRecoveryInterval(3000);
		factory.setTopologyRecoveryEnabled(false);
		
		try {
			// SSL with cert trust
			ConnectionFactory sslFactory = factory.clone();
			sslFactory.useSslProtocol("TLSv1.2");
			conn = sslFactory.newConnection();
		} catch (Exception ex) {
			try {
				// SSL without cert trust
				ConnectionFactory sslFactory = factory.clone();
				sslFactory.useSslProtocol();
				conn = sslFactory.newConnection();
			} catch (Exception ex2) {
				try {
					// Plaintext
					conn = factory.newConnection();
				} catch (Exception ex3) {
					ServiceLocator.getService(IExceptionHandler.class).silentException(ex3);
					throw new RuntimeException("Cannot create RabbitMQ connection.", ex3);
				}
			}
		}
		
		try {
			channel = conn.createChannel();
			channel.addShutdownListener(onChannelShutdown);
			channel.exchangeDeclare(exchangeName, "direct", true);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw new RuntimeException("Cannot create RabbitMQ connection.", ex);
		}
		
		conn.addShutdownListener(onShutdown);
		
		String[] ch = channelNames.toArray(new String[0]);
		channelNames.clear();
		
		for (String c : ch) {
			createChannel(c);
		}
		
		backlog = new FixedObjectPool<Triplet<String, String, byte[]>>(150);
		backlogTimer.start();
		
		connected = true;
		connect.invoke(this, EventArgs.EMPTY);
	}
	public void disconnect() {
		if (!connected) {
			return;
		}
		
		connected = false;
		
		backlogTimer.stop();
		
		sendLock.lock();
		if (sendThread != null) {
			sendThread.cancel(true);
			sendThread = null;
		}
		sendLock.unlock();
		reconnectLock.lock();
		if (reconnectThread != null) {
			reconnectThread.cancel(true);
			reconnectThread = null;
		}
		reconnectLock.unlock();
		
		backlog.clear();
		
		clear();
		
		try {
			conn.close();
		} catch (Exception ex) {
			try {
				conn.abort();
			} catch (Exception ex2) {
				
			}
		}
		
		conn = null;
		disconnect.invoke(this, EventArgs.EMPTY);
	}
	
	public void clear() {
		for (String c : channelNames) {
			try {
				destroyChannel(c);
			} catch (Exception ex) {
				
			}
		}
	}
	
	public boolean isConnected() {
		return connected;
	}
	public boolean isBusy() {
		return (sendThread == null) ? false : true;
	}
	
	public EventHandler<EventArgs> onConnect() {
		return connect;
	}
	public EventHandler<EventArgs> onDisconnect() {
		return disconnect;
	}
	public final EventHandler<MessageEventArgs> onMessage() {
		return message;
	}
	public final EventHandler<MessageErrorEventArgs> onError() {
		return error;
	}
	
	public String getPersonalId() {
		return personalId;
	}
	public void setPersonalId(String personalId) {
		if (personalId == null) {
			throw new ArgumentNullException("personalId");
		}
		
		String[] ch = channelNames.toArray(new String[0]);
		clear();
		
		this.personalId = personalId;
		props = new BasicProperties.Builder().replyTo(personalId).type(SenderType.BUNGEE.name()).deliveryMode(2).build();
		
		for (String c : ch) {
			createChannel(c);
		}
	}
	
	public void createChannel(String channelName) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		
		if (!connected) {
			throw new IllegalStateException("Connection has been closed or was never able to be opened.");
		}
		
		if (channelNames.contains(channelName)) {
			return;
		}
		
		String queueName = personalId + "-" + ((plugin != null) ? plugin.getDescription().getName() : "") + "-" + channelName;
		
		try {
			channel.queueDeclareNoWait(queueName, true, false, false, queueArgs);
			// Create a queue that takes all bungee broadcasts
			channel.queueBind(queueName, exchangeName, channelName + "-bungee");
			// Create a queue specifically for this server
			channel.queueBind(queueName, exchangeName, channelName + "-" + personalId);
			channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) {
					message.invoke(this, new MessageEventArgs(properties.getReplyTo(), SenderType.valueOf(properties.getType()), channelName, body));
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
		
		if (!connected) {
			throw new IllegalStateException("Connection has been closed or was never able to be opened.");
		}
		
		if (!channelNames.remove(channelName)) {
			return;
		}
		
		try {
			channel.queueDeleteNoWait(personalId + "-" + channelName, true, false);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw new RuntimeException("Cannot destroy channel.", ex);
		}
	}
	
	public void sendToServer(String channelName, String serverId, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (serverId == null) {
			throw new ArgumentNullException("serverId");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		
		if (!channelNames.contains(channelName)) {
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		try {
			backlog.add(new Triplet<String, String, byte[]>(channelName, channelName + "-" + serverId, data));
		} catch (Exception ex) {
			throw new RuntimeException("Could not add message to send queue.", ex);
		}
		
		if (sendLock.tryLock()) {
			if (connected && (sendThread == null || sendThread.isCancelled() || sendThread.isDone())) {
				sendThread = executor.submit(onSendThread);
			}
			
			sendLock.unlock();
		}
	}
	public void broadcastToBungee(String channelName, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		
		if (!channelNames.contains(channelName)) {
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		try {
			backlog.add(new Triplet<String, String, byte[]>(channelName, channelName + "-bungee", data));
		} catch (Exception ex) {
			throw new RuntimeException("Could not add message to send queue.", ex);
		}
		
		if (sendLock.tryLock()) {
			if (connected && (sendThread == null || sendThread.isCancelled() || sendThread.isDone())) {
				sendThread = executor.submit(onSendThread);
			}
			
			sendLock.unlock();
		}
	}
	public void broadcastToBukkit(String channelName, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		
		if (!channelNames.contains(channelName)) {
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		try {
			backlog.add(new Triplet<String, String, byte[]>(channelName, channelName + "-bukkit", data));
		} catch (Exception ex) {
			throw new RuntimeException("Could not add message to send queue.", ex);
		}
		
		if (sendLock.tryLock()) {
			if (connected && (sendThread == null || sendThread.isCancelled() || sendThread.isDone())) {
				sendThread = executor.submit(onSendThread);
			}
			
			sendLock.unlock();
		}
	}
	
	//private
	private Runnable onSendThread = new Runnable() {
		public void run() {
			while (!backlog.isEmpty()) {
				if (connected) {
					if (reconnectLock.tryLock()) {
						if (reconnectThread != null) {
							reconnectLock.unlock();
							break;
						}
						reconnectLock.unlock();
					} else {
						break;
					}
				} else {
					break;
				}
				
				Triplet<String, String, byte[]> first = backlog.popFirst();
				if (first == null) {
					break;
				}
				
				String c = first.getLeft();
				String key = first.getCenter();
				byte[] data = first.getRight();
				
				try {
					channel.basicPublish(exchangeName, key, props, data);
				} catch (Exception ex) {
					if (key.endsWith("-bukkit")) {
						error.invoke(this, new MessageErrorEventArgs(c, RoutingType.BROADCAST_TO_BUKKIT, data, ex));
					} else if (key.endsWith("-bungee")) {
						error.invoke(this, new MessageErrorEventArgs(c, RoutingType.BROADCAST_TO_BUNGEE, data, ex));
					} else {
						error.invoke(this, new MessageErrorEventArgs(c, RoutingType.SEND_TO_SERVER, key.substring(c.length() + 1), data, ex));
					}
				}
			}
			
			sendLock.lock();
			sendThread = null;
			sendLock.unlock();
		}
	};
	private ActionListener onBacklogTimer = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (backlog.isEmpty()) {
				return;
			}
			
			if (sendLock.tryLock()) {
				if (connected && (sendThread == null || sendThread.isCancelled() || sendThread.isDone())) {
					sendThread = executor.submit(onSendThread);
				}
				
				sendLock.unlock();
			}
		}
	};
	
	private ShutdownListener onShutdown = new ShutdownListener() {
		public void shutdownCompleted(ShutdownSignalException cause) {
			if (!connected) {
				return;
			}
			
			sendLock.lock();
			if (sendThread != null) {
				sendThread.cancel(true);
				sendThread = null;
			}
			sendLock.unlock();
			
			try {
				conn.close();
			} catch (Exception ex) {
				try {
					conn.abort();
				} catch (Exception ex2) {
					
				}
			}
			
			conn = null;
			backlog.clear();
			disconnect.invoke(this, EventArgs.EMPTY);
		}
	};
	private ShutdownListener onChannelShutdown = new ShutdownListener() {
		public void shutdownCompleted(ShutdownSignalException cause) {
			if (connected) {
				if (reconnectLock.tryLock()) {
					if (reconnectThread != null) {
						reconnectLock.unlock();
						return;
					}
					reconnectLock.unlock();
				} else {
					return;
				}
			} else {
				return;
			}
			
			reconnectThread = executor.submit(new Runnable() {
				public void run() {
					ServiceLocator.getService(IExceptionHandler.class).silentException(cause);
					cause.printStackTrace();
					
					boolean good = true;
					
					do {
						good = true;
						
						if (conn != null) {
							try {
								channel = conn.createChannel();
								channel.addShutdownListener(onChannelShutdown);
								channel.exchangeDeclare(exchangeName, "direct", true);
							} catch (Exception ex) {
								good = false;
								try {
									Thread.sleep(1000L);
								} catch (Exception ex2) {
									
								}
							}
						} else {
							good = false;
							try {
								Thread.sleep(1000L);
							} catch (Exception ex2) {
								
							}
						}
					} while (!good);
					
					String[] ch = channelNames.toArray(new String[0]);
					clear();
					
					for (String c : ch) {
						try {
							createChannel(c);
						} catch (Exception ex) {
							
						}
					}
					
					reconnectLock.lock();
					reconnectThread = null;
					reconnectLock.unlock();
				}
			});
			reconnectLock.unlock();
		}
	};
}
