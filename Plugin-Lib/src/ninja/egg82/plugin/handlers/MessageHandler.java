package ninja.egg82.plugin.handlers;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.tuples.Unit;
import ninja.egg82.plugin.commands.MessageCommand;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public final class MessageHandler implements PluginMessageListener {
	//vars
	private IObjectPool<String> channels = new DynamicObjectPool<String>();
	private ConcurrentHashMap<Class<MessageCommand>, Unit<MessageCommand>> commands = new ConcurrentHashMap<Class<MessageCommand>, Unit<MessageCommand>>();
	
	private JavaPlugin plugin = ServiceLocator.getService(JavaPlugin.class);
	
	//constructor
	public MessageHandler() {
		
	}
	
	//public
	public boolean addChannel(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		if (channels.contains(name)) {
			return false;
		}
		
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, name);
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, name, this);
		channels.add(name);
		return true;
	}
	public boolean removeChannel(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		if (!channels.remove(name)) {
			return false;
		}
		
		Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, name);
		Bukkit.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, name, this);
		return true;
	}
	public boolean hasChannel(String name) {
		if (name == null) {
			return false;
		}
		return channels.contains(name);
	}
	public void clearChannels() {
		channels.forEach((v) -> {
			Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, v);
			Bukkit.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, v, this);
		});
		channels.clear();
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
	
	public void sendMessage(Player player, String channelName, byte[] data) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null || data.length == 0) {
			return;
		}
		if (!channels.contains(channelName)) {
			return;
		}
		
		player.sendPluginMessage(plugin, channelName, data);
	}
	public  void onPluginMessageReceived(String channelName, Player player, byte[] message) {
		Exception lastEx = null;
		for (Entry<Class<MessageCommand>, Unit<MessageCommand>> kvp : commands.entrySet()) {
			MessageCommand c = null;
			
			if (kvp.getValue().getType() == null) {
				c = createCommand(kvp.getKey(), channelName, player, message);
				kvp.getValue().setType(c);
			} else {
				c = kvp.getValue().getType();
				c.setChannelName(channelName);
				c.setPlayer(player);
				c.setData(message);
			}
			
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
	
	//private
	private MessageCommand createCommand(Class<? extends MessageCommand> c, String channelName, Player player, byte[] message) {
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
