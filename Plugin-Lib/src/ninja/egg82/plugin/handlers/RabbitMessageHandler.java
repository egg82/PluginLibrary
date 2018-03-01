package ninja.egg82.plugin.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.Timer;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.FixedObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.events.EventArgs;
import ninja.egg82.patterns.tuples.Unit;
import ninja.egg82.plugin.commands.AsyncMessageCommand;
import ninja.egg82.plugin.core.MessageErrorEventArgs;
import ninja.egg82.plugin.core.MessageEventArgs;
import ninja.egg82.plugin.core.MessageServer;
import ninja.egg82.plugin.enums.MessageHandlerType;
import ninja.egg82.plugin.enums.RoutingType;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public class RabbitMessageHandler implements IMessageHandler {
	//vars
	private volatile MessageServer server = null;
	
	private IObjectPool<String> channels = new DynamicObjectPool<String>();
	private ConcurrentHashMap<Class<? extends AsyncMessageCommand>, Unit<AsyncMessageCommand>> commands = new ConcurrentHashMap<Class<? extends AsyncMessageCommand>, Unit<AsyncMessageCommand>>();
	
	private String ip = null;
	private int port = 0;
	private String username = null;
	private String password = null;
	
	private ThreadPoolExecutor executor = ServiceLocator.getService(ThreadPoolExecutor.class);
	private volatile Future<?> reconnectThread = null;
	private Timer resendTimer = null;
	private IObjectPool<MessageErrorEventArgs> resendMessages = new FixedObjectPool<MessageErrorEventArgs>(100);
	
	//constructor
	public RabbitMessageHandler(String ip, int port, String username, String password) {
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
		
		this.ip = ip;
		this.port = port;
		this.username = username;
		this.password = password;
		
		server = new MessageServer();
		server.onDisconnect().attach((s, e) -> onDisconnect(s, e));
		server.onMessage().attach((s, e) -> onMessage(s, e));
		server.onError().attach((s, e) -> onError(s, e));
		
		server.connect(ip, port, username, password);
		
		resendTimer = new Timer(1000, onResendTimer);
		resendTimer.setRepeats(true);
		resendTimer.start();
	}
	public void finalize() {
		destroy();
	}
	
	//public
	public String getSenderId() {
		return server.getPersonalId();
	}
	public void setSenderId(String senderId) {
		server.setPersonalId(senderId);
	}
	
	public void createChannel(String channelName) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		
		if (server == null) {
			throw new RuntimeException("Server has not yet been connected.");
		}
		
		try {
			server.createChannel(channelName);
		} catch (Exception ex) {
			throw ex;
		}
		
		channels.add(channelName);
	}
	public void destroyChannel(String channelName) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		
		if (server == null) {
			throw new RuntimeException("Server has not yet been connected.");
		}
		
		try {
			server.destroyChannel(channelName);
		} catch (Exception ex) {
			throw ex;
		}
		
		channels.remove(channelName);
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
		
		if (channels.contains(channelName)) {
			try {
				server.createChannel(channelName);
			} catch (Exception ex) {
				
			}
		}
		
		try {
			server.sendToServer(channelName, serverId, data);
		} catch (Exception ex) {
			if (channels.contains(channelName)) {
				onError(this, new MessageErrorEventArgs(channelName, RoutingType.SEND_TO_SERVER, serverId, data, ex));
			}
		}
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
		
		if (channels.contains(channelName)) {
			try {
				server.createChannel(channelName);
			} catch (Exception ex) {
				
			}
		}
		
		try {
			server.broadcastToBungee(channelName, data);
		} catch (Exception ex) {
			if (channels.contains(channelName)) {
				onError(this, new MessageErrorEventArgs(channelName, RoutingType.BROADCAST_TO_BUNGEE, data, ex));
			}
		}
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
		
		if (channels.contains(channelName)) {
			try {
				server.createChannel(channelName);
			} catch (Exception ex) {
				
			}
		}
		
		try {
			server.broadcastToBukkit(channelName, data);
		} catch (Exception ex) {
			if (channels.contains(channelName)) {
				onError(this, new MessageErrorEventArgs(channelName, RoutingType.BROADCAST_TO_BUKKIT, data, ex));
			}
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
		if (server == null) {
			return;
		}
		
		server.clear();
	}
	public void destroy() {
		clearCommands();
		
		if (server == null) {
			return;
		}
		
		resendTimer.stop();
		if (reconnectThread != null) {
			reconnectThread.cancel(true);
		}
		
		server.disconnect();
		
		clearChannels();
		
		server = null;
	}
	
	public MessageHandlerType getType() {
		return MessageHandlerType.RABBIT;
	}
	
	//private
	private ActionListener onResendTimer = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (server == null || server.isConnected() || reconnectThread != null) {
				return;
			}
			
			MessageErrorEventArgs eventArgs = null;
			
			do {
				eventArgs = resendMessages.popFirst();
				
				if (eventArgs == null) {
					return;
				}
				
				if (!channels.contains(eventArgs.getChannelName())) {
					continue;
				}
				try {
					server.createChannel(eventArgs.getChannelName());
				} catch (Exception ex) {
					
				}
				
				try {
					if (eventArgs.getRoutingType() == RoutingType.BROADCAST_TO_BUKKIT) {
						server.broadcastToBukkit(eventArgs.getChannelName(), eventArgs.getData());
					} else if (eventArgs.getRoutingType() == RoutingType.BROADCAST_TO_BUNGEE) {
						server.broadcastToBungee(eventArgs.getChannelName(), eventArgs.getData());
					} else {
						server.sendToServer(eventArgs.getChannelName(), eventArgs.getServerId(), eventArgs.getData());
					}
				} catch (Exception ex) {
					onError(this, new MessageErrorEventArgs(eventArgs.getChannelName(), eventArgs.getRoutingType(), eventArgs.getData(), ex));
				}
			} while (eventArgs != null);
		}
	};
	
	private void onDisconnect(Object sender, EventArgs e) {
		if (server != null && server.isConnected() && reconnectThread == null) {
			reconnectThread = executor.submit(new Runnable() {
				public void run() {
					boolean good = true;
					
					do {
						good = true;
						
						try {
							server.connect(ip, port, username, password);
						} catch (Exception ex) {
							good = false;
						}
						
						if (good) {
							for (String channel : channels) {
								try {
									createChannel(channel);
								} catch (Exception ex) {
									
								}
							}
						} else {
							try {
								Thread.sleep(1000L);
							} catch (Exception ex) {
								
							}
						}
					} while (!good);
					
					reconnectThread = null;
				}
			});
		}
	}
	
	private void onMessage(Object sender, MessageEventArgs e) {
		Exception lastEx = null;
		for (Entry<Class<? extends AsyncMessageCommand>, Unit<AsyncMessageCommand>> kvp : commands.entrySet()) {
			AsyncMessageCommand c = null;
			
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
	private void onError(Object sender, MessageErrorEventArgs e) {
		ServiceLocator.getService(IExceptionHandler.class).silentException(e.getError());
		e.getError().printStackTrace();
		
		if (!channels.contains(e.getChannelName())) {
			return;
		}
		
		boolean good = true;
		do {
			good = true;
			
			try {
				resendMessages.add(e);
			} catch (Exception ex) {
				resendMessages.popFirst();
				good = false;
			}
		} while (!resendMessages.isEmpty() && !good);
	}
	
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
