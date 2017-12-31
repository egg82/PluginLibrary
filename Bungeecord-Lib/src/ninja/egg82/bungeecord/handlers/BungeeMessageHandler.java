package ninja.egg82.bungeecord.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.egg82.bungeecord.commands.MessageCommand;
import ninja.egg82.bungeecord.core.BungeeMessageSender;
import ninja.egg82.bungeecord.enums.MessageHandlerType;
import ninja.egg82.bungeecord.enums.SenderType;
import ninja.egg82.bungeecord.utils.ChannelUtil;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.tuples.Unit;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public class BungeeMessageHandler implements IMessageHandler {
	//vars
	private IObjectPool<String> channels = new DynamicObjectPool<String>();
	private IObjectPool<BungeeMessageSender> servers = new DynamicObjectPool<BungeeMessageSender>();
	private ConcurrentHashMap<Class<MessageCommand>, Unit<MessageCommand>> commands = new ConcurrentHashMap<Class<MessageCommand>, Unit<MessageCommand>>();
	
	private Plugin plugin = ServiceLocator.getService(Plugin.class);
	
	private String personalId = UUID.randomUUID().toString();
	
	//constructor
	public BungeeMessageHandler() {
		for (Entry<String, ServerInfo> kvp : plugin.getProxy().getServers().entrySet()) {
			addServer(kvp.getValue());
		}
	}
	public void finalize() {
		destroy();
	}
	
	//public
	public String getSenderId() {
		return personalId;
	}
	
	public void createChannel(String channelName) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		
		if (channels.contains(channelName)) {
			return;
		}
		
		plugin.getProxy().registerChannel(channelName);
		
		channels.add(channelName);
	}
	public void destroyChannel(String channelName) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		
		if (!channels.remove(channelName)) {
			return;
		}
		
		plugin.getProxy().unregisterChannel(channelName);
	}
	
	public void sendToServer(String serverId, String channelName, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		if (!channels.contains(channelName)) {
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		
		if (!ChannelUtil.writeAll(out, SenderType.BUNGEE.getType(), personalId, serverId, data)) {
			throw new RuntimeException("Could not write headers.");
		}
		
		byte[] message = stream.toByteArray();
		
		for (BungeeMessageSender sender : servers) {
			sender.send(channelName, message);
		}
	}
	public void broadcastToBungee(String channelName, byte[] data) {
		Exception lastEx = null;
		for (Entry<Class<MessageCommand>, Unit<MessageCommand>> kvp : commands.entrySet()) {
			MessageCommand c = null;
			
			if (kvp.getValue().getType() == null) {
				c = createCommand(kvp.getKey());
				kvp.getValue().setType(c);
			} else {
				c = kvp.getValue().getType();
			}
			
			c.setSender(personalId);
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
		if (!channels.contains(channelName)) {
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		
		if (!ChannelUtil.writeAll(out, SenderType.BUNGEE.getType(), personalId, "bukkit", data)) {
			throw new RuntimeException("Could not write headers.");
		}
		
		byte[] message = stream.toByteArray();
		
		for (BungeeMessageSender sender : servers) {
			sender.send(channelName, message);
		}
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
	
	public boolean addCommand(Class<MessageCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		Unit<MessageCommand> unit = new Unit<MessageCommand>(null);
		return (CollectionUtil.putIfAbsent(commands, clazz, unit).hashCode() == unit.hashCode()) ? true : false;
	}
	public boolean removeCommand(Class<MessageCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		return (commands.remove(clazz) != null) ? true : false;
	}
	
	public void clearCommands() {
		commands.clear();
	}
	public void clearChannels() {
		channels.forEach((v) -> {
			destroyChannel(v);
		});
	}
	public void destroy() {
		clearChannels();
		clearCommands();
		
		for (BungeeMessageSender sender : servers) {
			sender.destroy();
		}
		servers.clear();
	}
	
	public void onPluginMessage(PluginMessageEvent e) {
		SenderType senderType = SenderType.UNKNOWN;
		String sender = "";
		String tag = "bungee";
		byte[] data = e.getData();
		
		if (e.getData().length >= 5) {
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
		
		if (tag.equals("bungee") || tag.equals(personalId)) {
			Exception lastEx = null;
			for (Entry<Class<MessageCommand>, Unit<MessageCommand>> kvp : commands.entrySet()) {
				MessageCommand c = null;
				
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
			for (BungeeMessageSender s : servers) {
				s.send(e.getTag(), e.getData());
			}
		}
	}
	
	public MessageHandlerType getType() {
		return MessageHandlerType.BUNGEE;
	}
	
	//private
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
	
	private void addServer(ServerInfo info) {
		for (BungeeMessageSender sender : servers) {
			ServerInfo i = sender.getInfo();
			
			if (info.getAddress().getAddress().getHostAddress().equals(i.getAddress().getAddress().getHostAddress()) && info.getAddress().getPort() == i.getAddress().getPort()) {
				return;
			}
		}
		
		servers.add(new BungeeMessageSender(info));
	}
	/*private void removeServer(ServerInfo info) {
		for (Iterator<BungeeMessageSender> i = servers.iterator(); i.hasNext();) {
			ServerInfo i2 = i.next().getInfo();
			
			if (info.getAddress().getAddress().getHostAddress().equals(i2.getAddress().getAddress().getHostAddress()) && info.getAddress().getPort() == i2.getAddress().getPort()) {
				i.remove();
				return;
			}
		}
	}*/
}
