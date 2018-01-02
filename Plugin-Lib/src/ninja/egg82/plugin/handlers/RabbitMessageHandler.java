package ninja.egg82.plugin.handlers;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.tuples.Unit;
import ninja.egg82.plugin.commands.MessageCommand;
import ninja.egg82.plugin.core.MessageEventArgs;
import ninja.egg82.plugin.core.MessageServer;
import ninja.egg82.plugin.enums.MessageHandlerType;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public class RabbitMessageHandler implements IMessageHandler {
	//vars
	private volatile MessageServer server = null;
	
	private BiConsumer<Object, MessageEventArgs> m = (s, e) -> onMessage(s, e);
	
	private ConcurrentHashMap<Class<? extends MessageCommand>, Unit<MessageCommand>> commands = new ConcurrentHashMap<Class<? extends MessageCommand>, Unit<MessageCommand>>();
	
	//constructor
	public RabbitMessageHandler(String ip, int port) {
		if (ip == null) {
			throw new ArgumentNullException("ip");
		}
		if (port <= 0 || port > 65535) {
			throw new IllegalArgumentException("port cannot be <= 0 or > 65535");
		}
		
		server = new MessageServer(ip, port);
		server.onMessage().attach(m);
	}
	public void finalize() {
		destroy();
	}
	
	//public
	public String getSenderId() {
		return server.getPersonalId();
	}
	
	public void createChannel(String channelName) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		
		if (server == null) {
			throw new RuntimeException("Server has not yet been connected.");
		}
		
		server.createChannel(channelName);
	}
	public void destroyChannel(String channelName) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		
		if (server == null) {
			throw new RuntimeException("Server has not yet been connected.");
		}
		
		server.destroyChannel(channelName);
	}
	
	public void sendToServer(String serverId, String channelName, byte[] data) {
		if (serverId == null) {
			throw new ArgumentNullException("serverId");
		}
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		
		if (server == null) {
			throw new RuntimeException("Server has not yet been connected.");
		}
		
		server.sendToServer(channelName, serverId, data);
	}
	public void broadcastToBungee(String channelName, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		
		if (server == null) {
			throw new RuntimeException("Server has not yet been connected.");
		}
		
		server.broadcastToBungee(channelName, data);
	}
	public void broadcastToBukkit(String channelName, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		
		if (server == null) {
			throw new RuntimeException("Server has not yet been connected.");
		}
		
		server.broadcastToBukkit(channelName, data);
	}
	
	public int addMessagesFromPackage(String packageName) {
		return addMessagesFromPackage(packageName, true);
	}
	public int addMessagesFromPackage(String packageName, boolean recursive) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		int numMessages = 0;
		
		List<Class<MessageCommand>> enums = ReflectUtil.getClasses(MessageCommand.class, packageName, recursive, false, false);
		for (Class<MessageCommand> c : enums) {
			if (addCommand(c)) {
				numMessages++;
			}
		}
		
		return numMessages;
	}
	
	public boolean addCommand(Class<? extends MessageCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		Unit<MessageCommand> unit = new Unit<MessageCommand>(null);
		return (CollectionUtil.putIfAbsent(commands, clazz, unit).hashCode() == unit.hashCode()) ? true : false;
	}
	public boolean removeCommand(Class<? extends MessageCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		return (commands.remove(clazz) != null) ? true : false;
	}
	
	public void clearCommands() {
		commands.clear();
	}
	public void clearChannels() {
		if (server == null) {
			return;
		}
		
		server.clear();
	}
	public void destroy() {
		clearChannels();
		clearCommands();
		
		if (server == null) {
			return;
		}
		
		server.onMessage().detatch(m);
		server.destroy();
		server = null;
	}
	
	public MessageHandlerType getType() {
		return MessageHandlerType.RABBIT;
	}
	
	//private
	private void onMessage(Object sender, MessageEventArgs e) {
		Exception lastEx = null;
		for (Entry<Class<? extends MessageCommand>, Unit<MessageCommand>> kvp : commands.entrySet()) {
			MessageCommand c = null;
			
			if (kvp.getValue().getType() == null) {
				c = createCommand(kvp.getKey());
				kvp.getValue().setType(c);
			} else {
				c = kvp.getValue().getType();
			}
			
			c.setSender(e.getSender());
			c.setSenderType(e.getSenderType());
			c.setChannelName(e.getChannelName());
			c.setData(e.getData());
			
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
	
	private MessageCommand createCommand(Class<? extends MessageCommand> c) {
		MessageCommand run = null;
		
		try {
			run = c.newInstance();
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw new RuntimeException("Cannot initialize message command.", ex);
		}
		
		return run;
	}
}
