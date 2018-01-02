package ninja.egg82.plugin.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Timer;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.tuples.Pair;
import ninja.egg82.patterns.tuples.Unit;
import ninja.egg82.plugin.commands.MessageCommand;
import ninja.egg82.plugin.enums.MessageHandlerType;
import ninja.egg82.plugin.enums.SenderType;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public class NativeBungeeMessageHandler implements IMessageHandler, PluginMessageListener {
	//vars
	private IObjectPool<String> channels = new DynamicObjectPool<String>();
	private ConcurrentHashMap<Class<? extends MessageCommand>, Unit<MessageCommand>> commands = new ConcurrentHashMap<Class<? extends MessageCommand>, Unit<MessageCommand>>();
	
	private JavaPlugin plugin = ServiceLocator.getService(JavaPlugin.class);
	
	private IObjectPool<Pair<String, byte[]>> backlog = new DynamicObjectPool<Pair<String, byte[]>>();
	private volatile boolean busy = false;
	private Timer backlogTimer = null;
	
	private String personalId = UUID.randomUUID().toString();
	
	//constructor
	public NativeBungeeMessageHandler() {
		backlogTimer = new Timer(100, onBacklogTimer);
		backlogTimer.setRepeats(true);
		backlogTimer.start();
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
		
		if (!Bukkit.getMessenger().registerIncomingPluginChannel(plugin, channelName, this).isValid()) {
			throw new RuntimeException("Could not register channel.");
		}
		Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, channelName);
		
		channels.add(channelName);
	}
	public void destroyChannel(String channelName) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		
		if (!channels.remove(channelName)) {
			return;
		}
		
		Bukkit.getMessenger().unregisterOutgoingPluginChannel(plugin, channelName);
		Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin, channelName, this);
	}
	
	public void sendToServer(String serverId, String channelName, byte[] data) {
		throw new NotImplementedException("Native messaging cannot send to specific servers.");
	}
	public void broadcastToBungee(String channelName, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		if (!channels.contains(channelName)) {
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		send(channelName, data);
	}
	public void broadcastToBukkit(String channelName, byte[] data) {
		throw new NotImplementedException("Native messaging cannot broadcast to bukkit servers.");
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
		channels.forEach((v) -> {
			destroyChannel(v);
		});
	}
	public void destroy() {
		clearChannels();
		clearCommands();
		
		backlogTimer.stop();
	}
	
	public void onPluginMessageReceived(String channelName, Player player, byte[] message) {
		Exception lastEx = null;
		for (Entry<Class<? extends MessageCommand>, Unit<MessageCommand>> kvp : commands.entrySet()) {
			MessageCommand c = null;
			
			if (kvp.getValue().getType() == null) {
				c = createCommand(kvp.getKey());
				kvp.getValue().setType(c);
			} else {
				c = kvp.getValue().getType();
			}
			
			c.setSender("");
			c.setSenderType(SenderType.UNKNOWN);
			c.setChannelName(channelName);
			c.setData(message);
			
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
	
	private void send(String channelName, byte[] message) {
		if (busy || backlog.size() > 0 || Bukkit.getOnlinePlayers().isEmpty()) {
			backlog.add(new Pair<String, byte[]>(channelName, message));
		} else {
			busy = true;
			new Thread(new Runnable() {
				public void run() {
					sendInternal(Bukkit.getOnlinePlayers().iterator().next(), channelName, message);
				}
			}).start();
		}
	}
	private void sendInternal(Player player, String channelName, byte[] message) {
		Exception lastEx = null;
		try {
			player.sendPluginMessage(plugin, channelName, message);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			lastEx = ex;
		}
		sendNextInternal(player);
		
		if (lastEx != null) {
			throw new RuntimeException("Could not send message.", lastEx);
		}
	}
	
	private void sendNext(Player player) {
		if (backlog.size() == 0) {
			busy = false;
			return;
		}
		
		Pair<String, byte[]> first = backlog.popFirst();
		sendInternal(player, first.getLeft(), first.getRight());
	}
	private void sendNextInternal(Player player) {
		new Thread(new Runnable() {
			public void run() {
				sendNext(player);
			}
		}).start();
	}
	
	private ActionListener onBacklogTimer = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (!busy && backlog.size() > 0 && !Bukkit.getOnlinePlayers().isEmpty()) {
				busy = true;
				sendNext(Bukkit.getOnlinePlayers().iterator().next());
			}
		}
	};
}
