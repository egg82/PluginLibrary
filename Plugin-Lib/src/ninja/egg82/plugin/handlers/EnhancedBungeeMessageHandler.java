package ninja.egg82.plugin.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import ninja.egg82.concurrent.DynamicConcurrentDeque;
import ninja.egg82.concurrent.FixedConcurrentDeque;
import ninja.egg82.concurrent.IConcurrentDeque;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.tuples.Unit;
import ninja.egg82.plugin.BasePlugin;
import ninja.egg82.plugin.commands.AsyncMessageCommand;
import ninja.egg82.plugin.core.BukkitMessageQueueData;
import ninja.egg82.plugin.enums.MessageHandlerType;
import ninja.egg82.plugin.enums.SenderType;
import ninja.egg82.plugin.utils.ChannelUtil;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public class EnhancedBungeeMessageHandler implements IMessageHandler, PluginMessageListener {
	//vars
	
	// Channel names
	private IConcurrentDeque<String> channelNames = new DynamicConcurrentDeque<String>();
	// Map for storing commands to run when a message is received
	private ConcurrentHashMap<Class<? extends AsyncMessageCommand>, Unit<AsyncMessageCommand>> commands = new ConcurrentHashMap<Class<? extends AsyncMessageCommand>, Unit<AsyncMessageCommand>>();
	
	// Thread pool for message sending thread. The thread count for actually sending messages should never exceed 1 for data consistency
	private ScheduledExecutorService threadPool = null;
	
	// The plugin using this code
	private BasePlugin plugin = ServiceLocator.getService(BasePlugin.class);
	// This server's sender ID, for replying directly to the server
	private volatile String senderId = null;
	
	// Message backlog/queue - for storing messages in case of disconnect. We put this here instead of in the wrappers so one server doesn't screw over another if it has no players
	private IConcurrentDeque<BukkitMessageQueueData> backlog = new FixedConcurrentDeque<BukkitMessageQueueData>(150);
	
	//constructor
	public EnhancedBungeeMessageHandler() {
		threadPool = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat((plugin != null) ? plugin.getDescription().getName() + "-EnhancedBungee-%d" : "EnhancedBungee-%d").build());
		threadPool.scheduleWithFixedDelay(onBacklogThread, 150L, 150L, TimeUnit.MILLISECONDS);
		
		// Temporary workaround for Mainframe using Bukkit libraries when it's not actually running under Bukkit
		senderId = (plugin != null) ? plugin.getServerId() : UUID.randomUUID().toString();
	}
	
	//public
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		if (senderId == null) {
			throw new ArgumentNullException("senderId");
		}
		
		this.senderId = senderId;
	}
	
	public void createChannel(String channelName) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		
		if (channelNames.contains(channelName)) {
			// Channel already exists. No need to re-create it
			return;
		}
		
		try {
			// Register the channel
			if (!Bukkit.getMessenger().registerIncomingPluginChannel(plugin, channelName, this).isValid()) {
				throw new Exception("Channel is not valid directly after registration.");
			}
			Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, channelName);
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
		
		if (!channelNames.remove(channelName)) {
			// Channel doesn't exist. No need to delete what isn't there
			return;
		}
		
		try {
			// Unregister the channel
			Bukkit.getMessenger().unregisterOutgoingPluginChannel(plugin, channelName);
			Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin, channelName, this);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw new RuntimeException("Cannot destroy channel.", ex);
		}
	}
	
	public void sendToServer(String serverId, String channelName, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		
		if (!channelNames.contains(channelName)) {
			// Channel doesn't exist, throw an exception
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		
		try {
			out.writeByte(SenderType.BUKKIT.getType());
		} catch (Exception ex) {
			throw new RuntimeException("Could not write headers.");
		}
		if (!ChannelUtil.writeAll(out, senderId, serverId, data)) {
			throw new RuntimeException("Could not write headers.");
		}
		
		// Grab a new data object
		BukkitMessageQueueData messageData = new BukkitMessageQueueData(channelName, stream.toByteArray());
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
		
		if (!channelNames.contains(channelName)) {
			// Channel doesn't exist, throw an exception
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		
		try {
			out.writeByte(SenderType.BUKKIT.getType());
		} catch (Exception ex) {
			throw new RuntimeException("Could not write headers.");
		}
		if (!ChannelUtil.writeAll(out, senderId, "bungee", data)) {
			throw new RuntimeException("Could not write headers.");
		}
		
		// Grab a new data object
		BukkitMessageQueueData messageData = new BukkitMessageQueueData(channelName, stream.toByteArray());
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
		
		if (!channelNames.contains(channelName)) {
			// Channel doesn't exist, throw an exception
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		
		try {
			out.writeByte(SenderType.BUKKIT.getType());
		} catch (Exception ex) {
			throw new RuntimeException("Could not write headers.");
		}
		if (!ChannelUtil.writeAll(out, senderId, "bukkit", data)) {
			throw new RuntimeException("Could not write headers.");
		}
		
		// Grab a new data object
		BukkitMessageQueueData messageData = new BukkitMessageQueueData(channelName, stream.toByteArray());
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
	}
	
	public void onPluginMessageReceived(String channelName, Player player, byte[] message) {
		// Message sender
		String sender = "";
		// The sender type. Bukkit or Bungee?
		SenderType senderType = SenderType.UNKNOWN;
		// The tag - or who this message is meant to be for
		String tag = "bukkit";
		// And finally the data
		byte[] data = message;
		
		// Make sure we can read everything
		if (message.length >= 5) {
			// Read headers
			ByteArrayInputStream stream = new ByteArrayInputStream(message);
			DataInputStream in = new DataInputStream(stream);
			
			try {
				senderType = SenderType.fromType(in.readByte());
				sender = in.readUTF();
				tag = in.readUTF();
				data = new byte[in.available()];
				in.read(data);
			} catch (Exception ex) {
				
			}
		}
		
		if (!tag.equals("bukkit") && !tag.equals(senderId)) {
			// Message isn't directed at us. Skip it
			return;
		}
		
		Exception lastEx = null;
		// Iterate handlers and fire them
		for (Entry<Class<? extends AsyncMessageCommand>, Unit<AsyncMessageCommand>> kvp : commands.entrySet()) {
			AsyncMessageCommand c = null;
			
			// Lazy initialize
			if (kvp.getValue().getType() == null) {
				c = createCommand(kvp.getKey());
				kvp.getValue().setType(c);
			} else {
				c = kvp.getValue().getType();
			}
			
			c.setSender(sender);
			c.setSenderType(senderType);
			c.setChannelName(channelName);
			c.setData(data);
			
			try {
				c.start();
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
				lastEx = ex;
			}
		}
		if (lastEx != null) {
			throw new RuntimeException("Cannot run message command.", lastEx);
		}
	}
	
	public MessageHandlerType getType() {
		return MessageHandlerType.BUNGEE;
	}
	
	//private
	private Runnable onSendThread = new Runnable() {
		public void run() {
			while (!backlog.isEmpty()) {
				if (Bukkit.getOnlinePlayers().isEmpty()) {
					// No players on the server to send messages with. Break out of the loop
					break;
				}
				
				Player player = null;
				try {
					player = Bukkit.getOnlinePlayers().iterator().next();
				} catch (Exception ex) {
					// No players on the server to send messages with. Break out of the loop
					break;
				}
				
				// Grab the oldest data first
				BukkitMessageQueueData first = backlog.pollFirst();
				if (first == null) {
					// Data is null, which means we prematurely reached the end of the queue
					break;
				}
				
				// Try to push the data to the Bukkit queue
				try {
					player.sendPluginMessage(plugin, first.getChannel(), first.getData());
				} catch (Exception ex) {
					// Message send failed. Re-add it to the front of the backlog
					backlog.addFirst(first);
				}
			}
		}
	};
	private Runnable onBacklogThread = new Runnable() {
		public void run() {
			// Check player count and backlog count
			if (backlog.isEmpty() || Bukkit.getOnlinePlayers().isEmpty()) {
				return;
			}
			
			while (!backlog.isEmpty()) {
				if (Bukkit.getOnlinePlayers().isEmpty()) {
					// No players on the server to send messages with. Break out of the loop
					break;
				}
				
				Player player = null;
				try {
					player = Bukkit.getOnlinePlayers().iterator().next();
				} catch (Exception ex) {
					// No players on the server to send messages with. Break out of the loop
					break;
				}
				
				// Grab the oldest data first
				BukkitMessageQueueData first = backlog.pollFirst();
				if (first == null) {
					// Data is null, which means we prematurely reached the end of the queue
					break;
				}
				
				// Try to push the data to the Bukkit queue
				try {
					player.sendPluginMessage(plugin, first.getChannel(), first.getData());
				} catch (Exception ex) {
					// Message send failed. Re-add it to the front of the backlog
					backlog.addFirst(first);
				}
			}
		}
	};
	
	private AsyncMessageCommand createCommand(Class<? extends AsyncMessageCommand> c) {
		AsyncMessageCommand run = null;
		
		try {
			run = c.newInstance();
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw new RuntimeException("Cannot initialize message command.", ex);
		}
		
		return run;
	}
}
