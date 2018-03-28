package ninja.egg82.bungeecord.handlers;

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

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import ninja.egg82.bungeecord.BasePlugin;
import ninja.egg82.bungeecord.commands.AsyncMessageCommand;
import ninja.egg82.bungeecord.core.BungeeMessageSender;
import ninja.egg82.bungeecord.enums.MessageHandlerType;
import ninja.egg82.bungeecord.enums.SenderType;
import ninja.egg82.bungeecord.utils.ChannelUtil;
import ninja.egg82.concurrent.DynamicConcurrentDeque;
import ninja.egg82.concurrent.IConcurrentDeque;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.tuples.Unit;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public class EnhancedBungeeMessageHandler implements IMessageHandler {
	//vars
	
	// Channel names
	private IConcurrentDeque<String> channelNames = new DynamicConcurrentDeque<String>();
	// Actual server senders
	private IConcurrentDeque<BungeeMessageSender> servers = new DynamicConcurrentDeque<BungeeMessageSender>();
	// Map for storing commands to run when a message is received
	private ConcurrentHashMap<Class<? extends AsyncMessageCommand>, Unit<AsyncMessageCommand>> commands = new ConcurrentHashMap<Class<? extends AsyncMessageCommand>, Unit<AsyncMessageCommand>>();
	
	// Thread pool for message sending thread. The thread count for actually sending messages should never exceed 1 for data consistency
	private ScheduledExecutorService threadPool = null;
	
	// The plugin using this code
	private BasePlugin plugin = ServiceLocator.getService(BasePlugin.class);
	// This server's sender ID, for replying directly to the server
	private volatile String senderId = null;
	
	//constructor
	public EnhancedBungeeMessageHandler() {
		threadPool = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat((plugin != null) ? plugin.getDescription().getName() + "-EnhancedBungee-%d" : "EnhancedBungee-%d").build());
		threadPool.scheduleWithFixedDelay(onBacklogThread, 150L, 150L, TimeUnit.MILLISECONDS);
		
		// Temporary workaround for Mainframe using Bungee libraries when it's not actually running under Bungee
		senderId = (plugin != null) ? plugin.getServerId() : UUID.randomUUID().toString();
		
		if (plugin != null) {
			// Add all servers this Bungee knows about
			for (Entry<String, ServerInfo> kvp : plugin.getProxy().getServers().entrySet()) {
				for (BungeeMessageSender sender : servers) {
					ServerInfo i = sender.getInfo();
					
					if (kvp.getValue().getAddress().getAddress().getHostAddress().equals(i.getAddress().getAddress().getHostAddress()) && kvp.getValue().getAddress().getPort() == i.getAddress().getPort()) {
						return;
					}
				}
				
				servers.add(new BungeeMessageSender(kvp.getValue()));
			}
		}
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
			plugin.getProxy().registerChannel(channelName);
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
			plugin.getProxy().unregisterChannel(channelName);
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
			out.writeByte(SenderType.BUNGEE.getType());
		} catch (Exception ex) {
			throw new RuntimeException("Could not write headers.");
		}
		if (!ChannelUtil.writeAll(out, senderId, serverId, data)) {
			throw new RuntimeException("Could not write headers.");
		}
		
		byte[] message = stream.toByteArray();
		
		for (BungeeMessageSender sender : servers) {
			sender.submit(channelName, message);
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
			
			c.setSender(senderId);
			c.setSenderType(SenderType.BUNGEE);
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
			out.writeByte(SenderType.BUNGEE.getType());
		} catch (Exception ex) {
			throw new RuntimeException("Could not write headers.");
		}
		if (!ChannelUtil.writeAll(out, senderId, "bukkit", data)) {
			throw new RuntimeException("Could not write headers.");
		}
		
		byte[] message = stream.toByteArray();
		
		for (BungeeMessageSender sender : servers) {
			sender.submit(channelName, message);
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
		
		// Clear the channels and commands
		clearChannels();
		clearCommands();
		
		// Close all the connections
		for (BungeeMessageSender sender : servers) {
			sender.close();
		}
		// Clear the connections
		servers.clear();
	}
	
	public void onPluginMessage(PluginMessageEvent e) {
		// Message sender
		String sender = "";
		// The sender type. Bukkit or Bungee?
		SenderType senderType = SenderType.UNKNOWN;
		// The tag - or who this message is meant to be for
		String tag = "bungee";
		// And finally the data
		byte[] data = e.getData();
		
		// Make sure we can read everything
		if (e.getData().length >= 5) {
			// Read headers
			ByteArrayInputStream stream = new ByteArrayInputStream(e.getData());
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
		
		if (tag.equals("bungee") || tag.equals(senderId)) {
			// Message is directed at us
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
				c.setChannelName(e.getTag());
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
		} else {
			// Message isn't directed at us. "Bounce" it and forward the message to all Bukkit servers
			for (BungeeMessageSender s : servers) {
				s.submit(e.getTag(), e.getData());
			}
			
			// Submit a new send task
			threadPool.submit(onSendThread);
		}
	}
	
	public MessageHandlerType getType() {
		return MessageHandlerType.BUNGEE;
	}
	
	//private
	private Runnable onSendThread = new Runnable() {
		public void run() {
			// Iterate connections and flush their queues
			for (BungeeMessageSender s : servers) {
				s.sendAll();
			}
		}
	};
	private Runnable onBacklogThread = new Runnable() {
		public void run() {
			// Iterate connections and flush their queues
			for (BungeeMessageSender s : servers) {
				s.sendAll();
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
