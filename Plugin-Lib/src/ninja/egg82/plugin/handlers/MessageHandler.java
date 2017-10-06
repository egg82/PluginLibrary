package ninja.egg82.plugin.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.MessageCommand;
import ninja.egg82.plugin.enums.SpigotInitType;
import ninja.egg82.startup.InitRegistry;

public final class MessageHandler implements PluginMessageListener {
	//vars
	private HashSet<String> channels = new HashSet<String>();
	private HashMap<Class<? extends MessageCommand>, MessageCommand> commands = new HashMap<Class<? extends MessageCommand>, MessageCommand>();
	
	private JavaPlugin plugin = ServiceLocator.getService(InitRegistry.class).getRegister(SpigotInitType.PLUGIN, JavaPlugin.class);
	
	//constructor
	public MessageHandler() {
		
	}
	
	//public
	public synchronized void addChannel(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		if (channels.contains(name)) {
			return;
		}
		
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, name);
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, name, this);
		channels.add(name);
	}
	public synchronized void removeChannel(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		if (!channels.contains(name)) {
			return;
		}
		
		Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, name);
		Bukkit.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, name, this);
		
		channels.remove(name);
	}
	public synchronized boolean hasChannel(String name) {
		if (name == null) {
			return false;
		}
		return channels.contains(name);
	}
	public synchronized void clearChannels() {
		channels.forEach((v) -> {
			Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, v);
			Bukkit.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, v, this);
		});
		channels.clear();
	}
	
	public synchronized void addCommand(Class<? extends MessageCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		if (commands.containsKey(clazz)) {
			return;
		}
		
		commands.put(clazz, null);
	}
	public synchronized void removeCommand(Class<? extends MessageCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		commands.remove(clazz);
	}
	public synchronized void clearCommands() {
		commands.clear();
	}
	
	public synchronized void sendMessage(Player player, String channelName, byte[] data) {
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
	public synchronized void onPluginMessageReceived(String channelName, Player player, byte[] message) {
		Iterator<Entry<Class<? extends MessageCommand>, MessageCommand>> i = commands.entrySet().iterator();
		
		while (i.hasNext()) {
			Entry<Class<? extends MessageCommand>, MessageCommand> kvp = i.next();
			MessageCommand c = null;
			
			if (kvp.getValue() == null) {
				c = createCommand(kvp.getKey(), channelName, player, message);
				kvp.setValue(c);
			} else {
				c = kvp.getValue();
				c.setChannelName(channelName);
				c.setPlayer(player);
				c.setData(message);
			}
			
			try {
				c.start();
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			}
		}
	}
	
	//private
	private synchronized MessageCommand createCommand(Class<? extends MessageCommand> c, String channelName, Player player, byte[] message) {
		MessageCommand run = null;
		
		try {
			run = c.getDeclaredConstructor(String.class, Player.class, byte[].class).newInstance(channelName, player, message);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			return null;
		}
		
		return run;
	}
}
