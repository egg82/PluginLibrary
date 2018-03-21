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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.tuples.Pair;
import ninja.egg82.patterns.tuples.Unit;
import ninja.egg82.plugin.BasePlugin;
import ninja.egg82.plugin.commands.AsyncMessageCommand;
import ninja.egg82.plugin.enums.MessageHandlerType;
import ninja.egg82.plugin.enums.SenderType;
import ninja.egg82.plugin.utils.ChannelUtil;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public class EnhancedBungeeMessageHandler implements IMessageHandler, PluginMessageListener {
	//vars
	private IObjectPool<String> channels = new DynamicObjectPool<String>();
	private ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(ServiceLocator.getService(JavaPlugin.class).getName() + "-Bungee_Enhanced-%d").build());
	private ConcurrentHashMap<Class<? extends AsyncMessageCommand>, Unit<AsyncMessageCommand>> commands = new ConcurrentHashMap<Class<? extends AsyncMessageCommand>, Unit<AsyncMessageCommand>>();
	
	private JavaPlugin plugin = ServiceLocator.getService(JavaPlugin.class);
	
	private IObjectPool<Pair<String, byte[]>> backlog = new DynamicObjectPool<Pair<String, byte[]>>();
	private volatile boolean busy = false;
	
	private String personalId = (ServiceLocator.getService(BasePlugin.class) != null) ? ServiceLocator.getService(BasePlugin.class).getServerId() : UUID.randomUUID().toString();
	
	//constructor
	public EnhancedBungeeMessageHandler() {
		threadPool.scheduleAtFixedRate(onBacklogThread, 150L, 150L, TimeUnit.MILLISECONDS);
	}
	public void finalize() {
		destroy();
	}
	
	//public
	public String getSenderId() {
		return personalId;
	}
	public void setSenderId(String senderId) {
		if (senderId == null) {
			throw new ArgumentNullException("senderId");
		}
		
		this.personalId = senderId;
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
		
		if (!ChannelUtil.writeAll(out, SenderType.BUKKIT.getType(), personalId, serverId, data)) {
			throw new RuntimeException("Could not write headers.");
		}
		
		send(channelName, stream.toByteArray());
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
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		
		if (!ChannelUtil.writeAll(out, SenderType.BUKKIT.getType(), personalId, "bungee", data)) {
			throw new RuntimeException("Could not write headers.");
		}
		
		send(channelName, stream.toByteArray());
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
		
		if (!ChannelUtil.writeAll(out, SenderType.BUKKIT.getType(), personalId, "bukkit", data)) {
			throw new RuntimeException("Could not write headers.");
		}
		
		send(channelName, stream.toByteArray());
	}
	
	public int addMessagesFromPackage(String packageName) {
		return addMessagesFromPackage(packageName, true);
	}
	public int addMessagesFromPackage(String packageName, boolean recursive) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		int numMessages = 0;
		
		List<Class<AsyncMessageCommand>> enums = ReflectUtil.getClasses(AsyncMessageCommand.class, packageName, recursive, false, false);
		for (Class<AsyncMessageCommand> c : enums) {
			if (addCommand(c)) {
				numMessages++;
			}
		}
		
		return numMessages;
	}
	
	public boolean addCommand(Class<? extends AsyncMessageCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		Unit<AsyncMessageCommand> unit = new Unit<AsyncMessageCommand>(null);
		return (CollectionUtil.putIfAbsent(commands, clazz, unit).hashCode() == unit.hashCode()) ? true : false;
	}
	public boolean removeCommand(Class<? extends AsyncMessageCommand> clazz) {
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
		threadPool.shutdownNow();
		
		backlog.clear();
		clearChannels();
		clearCommands();
	}
	
	public void onPluginMessageReceived(String channelName, Player player, byte[] message) {
		SenderType senderType = SenderType.UNKNOWN;
		String sender = "";
		String tag = "bukkit";
		byte[] data = message;
		
		if (message.length >= 5) {
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
		
		if (!tag.equals("bukkit") && !tag.equals(personalId)) {
			return;
		}
		
		Exception lastEx = null;
		for (Entry<Class<? extends AsyncMessageCommand>, Unit<AsyncMessageCommand>> kvp : commands.entrySet()) {
			AsyncMessageCommand c = null;
			
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
	
	private void send(String channelName, byte[] message) {
		if (busy || backlog.size() > 0 || Bukkit.getOnlinePlayers().isEmpty()) {
			backlog.add(new Pair<String, byte[]>(channelName, message));
		} else {
			busy = true;
			threadPool.submit(new Runnable() {
				public void run() {
					sendInternal(Bukkit.getOnlinePlayers().iterator().next(), channelName, message);
				}
			});
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
		sendNext(player);
		
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
	
	private Runnable onBacklogThread = new Runnable() {
		public void run() {
			if (!busy && backlog.size() > 0 && !Bukkit.getOnlinePlayers().isEmpty()) {
				busy = true;
				sendNext(Bukkit.getOnlinePlayers().iterator().next());
			}
		}
	};
}
