package ninja.egg82.bungeecord.processors;

import java.io.Closeable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import ninja.egg82.bungeecord.core.CoreEventHandler;
import ninja.egg82.bungeecord.messaging.EnhancedBungeeMessageHandler;
import ninja.egg82.bungeecord.messaging.NativeBungeeMessageHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.handlers.events.async.AsyncEventHandler;
import ninja.egg82.plugin.handlers.events.async.HighAsyncEventHandler;
import ninja.egg82.plugin.handlers.events.async.HighestAsyncEventHandler;
import ninja.egg82.plugin.handlers.events.async.IAsyncEventHandler;
import ninja.egg82.plugin.handlers.events.async.LowAsyncEventHandler;
import ninja.egg82.plugin.handlers.events.async.LowestAsyncEventHandler;
import ninja.egg82.utils.ReflectUtil;

public class EventProcessor implements Listener, Closeable {
	//vars
	private CoreEventHandler highestEventHandler = new CoreEventHandler();
	private CoreEventHandler highEventHandler = new CoreEventHandler();
	private CoreEventHandler normalEventHandler = new CoreEventHandler();
	private CoreEventHandler lowEventHandler = new CoreEventHandler();
	private CoreEventHandler lowestEventHandler = new CoreEventHandler();
	
	//constructor
	public EventProcessor() {
		Plugin plugin = ServiceLocator.getService(Plugin.class);
		plugin.getProxy().getPluginManager().registerListener(plugin, this);
	}
	
	//public
	public boolean addHandler(Class<? extends Event> event, Class<? extends IAsyncEventHandler<? extends Event>> clazz) {
		if (event == null) {
			throw new IllegalArgumentException("event cannot be null.");
		}
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null.");
		}
		
		if (ReflectUtil.doesExtend(AsyncEventHandler.class, clazz)) {
			return normalEventHandler.addEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(HighestAsyncEventHandler.class, clazz)) {
			return highestEventHandler.addEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(HighAsyncEventHandler.class, clazz)) {
			return highEventHandler.addEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(LowestAsyncEventHandler.class, clazz)) {
			return lowestEventHandler.addEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(LowAsyncEventHandler.class, clazz)) {
			return lowEventHandler.addEventHandler(event, clazz);
		}
		
		return false;
	}
	public boolean removeHandler(Class<? extends IAsyncEventHandler<? extends Event>> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null.");
		}
		
		if (ReflectUtil.doesExtend(AsyncEventHandler.class, clazz)) {
			return normalEventHandler.removeEventHandler(clazz);
		} else if (ReflectUtil.doesExtend(HighestAsyncEventHandler.class, clazz)) {
			return highestEventHandler.removeEventHandler(clazz);
		} else if (ReflectUtil.doesExtend(HighAsyncEventHandler.class, clazz)) {
			return highEventHandler.removeEventHandler(clazz);
		} else if (ReflectUtil.doesExtend(LowestAsyncEventHandler.class, clazz)) {
			return lowestEventHandler.removeEventHandler(clazz);
		} else if (ReflectUtil.doesExtend(LowAsyncEventHandler.class, clazz)) {
			return lowEventHandler.removeEventHandler(clazz);
		}
		
		return false;
	}
	public boolean removeHandler(Class<? extends Event> event, Class<? extends IAsyncEventHandler<? extends Event>> clazz) {
		if (event == null) {
			throw new IllegalArgumentException("event cannot be null.");
		}
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null.");
		}
		
		if (ReflectUtil.doesExtend(AsyncEventHandler.class, clazz)) {
			return normalEventHandler.removeEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(HighestAsyncEventHandler.class, clazz)) {
			return highestEventHandler.removeEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(HighAsyncEventHandler.class, clazz)) {
			return highEventHandler.removeEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(LowestAsyncEventHandler.class, clazz)) {
			return lowestEventHandler.removeEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(LowAsyncEventHandler.class, clazz)) {
			return lowEventHandler.removeEventHandler(event, clazz);
		}
		
		return false;
	}
	public boolean hasHandler(Class<? extends Event> event) {
		if (event == null) {
			return false;
		}
		
		if (normalEventHandler.hasEventHandler(event)) {
			return true;
		} else if (highestEventHandler.hasEventHandler(event)) {
			return true;
		} else if (highEventHandler.hasEventHandler(event)) {
			return true;
		} else if (lowestEventHandler.hasEventHandler(event)) {
			return true;
		} else if (lowEventHandler.hasEventHandler(event)) {
			return true;
		}
		
		return false;
	}
	public void clear() {
		highestEventHandler.clear();
		highEventHandler.clear();
		normalEventHandler.clear();
		lowEventHandler.clear();
		lowestEventHandler.clear();
	}
	public void close() {
		clear();
		
		Plugin plugin = ServiceLocator.getService(Plugin.class);
		plugin.getProxy().getPluginManager().unregisterListener(this);
	}
	
	public int addHandlersFromPackage(String packageName) {
		return addHandlersFromPackage(packageName, true);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int addHandlersFromPackage(String packageName, boolean recursive) {
		if (packageName == null) {
			throw new IllegalArgumentException("packageName cannot be null.");
		}
		
		int numEvents = 0;
		
		List<Class<? extends IAsyncEventHandler>> enums = ReflectUtil.getClassesParameterized(IAsyncEventHandler.class, packageName, recursive, false, false);
		for (Class<? extends IAsyncEventHandler> c : enums) {
			Class<? extends Event> eventType = null;
			try {
				eventType = (Class<? extends Event>) ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments()[0];
			} catch (Exception ex) {
				continue;
			}
			
			if (addHandler(eventType, (Class<IAsyncEventHandler<? extends Event>>) c)) {
				numEvents++;
			}
		}
		
		return numEvents;
	}
	
	//generic events
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onAsyncHighest(AsyncEvent<?> e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onAsyncHigh(AsyncEvent<?> e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onAsync(AsyncEvent<?> e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onAsyncLow(AsyncEvent<?> e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onAsyncLowest(AsyncEvent<?> e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	//player events
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPreLoginHighest(PreLoginEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onPreLoginHigh(PreLoginEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPreLogin(PreLoginEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onPreLoginLow(PreLoginEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPreLoginLowest(PreLoginEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onLoginHighest(LoginEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onLoginHigh(LoginEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onLogin(LoginEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onLoginLow(LoginEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onLoginLowest(LoginEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPostLoginHighest(PostLoginEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onPostLoginHigh(PostLoginEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPostLogin(PostLoginEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onPostLoginLow(PostLoginEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPostLoginLowest(PostLoginEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerHandshakeHighest(PlayerHandshakeEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerHandshakeHigh(PlayerHandshakeEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerHandshake(PlayerHandshakeEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerHandshakeLow(PlayerHandshakeEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerHandshakeLowest(PlayerHandshakeEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerDisconnectHighest(PlayerDisconnectEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerDisconnectHigh(PlayerDisconnectEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerDisconnectLow(PlayerDisconnectEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerDisconnectLowest(PlayerDisconnectEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onServerConnectHighest(ServerConnectEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onServerConnectHigh(ServerConnectEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onServerConnect(ServerConnectEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onServerConnectLow(ServerConnectEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onServerConnectLowest(ServerConnectEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onServerDisconnectHighest(ServerDisconnectEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onServerDisconnectHigh(ServerDisconnectEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onServerDisconnect(ServerDisconnectEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onServerDisconnectLow(ServerDisconnectEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onServerDisconnectLowest(ServerDisconnectEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onServerKickHighest(ServerKickEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onServerKickHigh(ServerKickEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onServerKick(ServerKickEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onServerKickLow(ServerKickEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onServerKickLowest(ServerKickEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onServerSwitchHighest(ServerSwitchEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onServerSwitchHigh(ServerSwitchEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onServerSwitch(ServerSwitchEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onServerSwitchLow(ServerSwitchEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onServerSwitchLowest(ServerSwitchEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	//chat events
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onChatHighest(ChatEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onChatHigh(ChatEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onChat(ChatEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onChatLow(ChatEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onChatLowest(ChatEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPermissionCheckHighest(PermissionCheckEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onPermissionCheckHigh(PermissionCheckEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPermissionCheck(PermissionCheckEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onPermissionCheckLow(PermissionCheckEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPermissionCheckLowest(PermissionCheckEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onTabCompleteHighest(TabCompleteEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onTabCompleteHigh(TabCompleteEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onTabComplete(TabCompleteEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onTabCompleteLow(TabCompleteEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onTabCompleteLowest(TabCompleteEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onTabCompleteResponseHighest(TabCompleteResponseEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onTabCompleteResponseHigh(TabCompleteResponseEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onTabCompleteResponse(TabCompleteResponseEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onTabCompleteResponseLow(TabCompleteResponseEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onTabCompleteResponseLowest(TabCompleteResponseEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	//plugin events
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPluginMessageHighest(PluginMessageEvent e) {
		if (ServiceLocator.getService(EnhancedBungeeMessageHandler.class) != null) {
			ServiceLocator.getService(EnhancedBungeeMessageHandler.class).onPluginMessage(e);
		}
		if (ServiceLocator.getService(NativeBungeeMessageHandler.class) != null) {
			ServiceLocator.getService(NativeBungeeMessageHandler.class).onPluginMessage(e);
		}
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onPluginMessageHigh(PluginMessageEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPluginMessage(PluginMessageEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onPluginMessageLow(PluginMessageEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPluginMessageLowest(PluginMessageEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	//proxy events
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onProxyPingHighest(ProxyPingEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onProxyPingHigh(ProxyPingEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onProxyPing(ProxyPingEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onProxyPingLow(ProxyPingEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onProxyPingLowest(ProxyPingEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onProxyReloadHighest(ProxyReloadEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onProxyReloadHigh(ProxyReloadEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onProxyReload(ProxyReloadEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onProxyReloadLow(ProxyReloadEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onProxyReloadLowest(ProxyReloadEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	//server events
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onServerConnectedHighest(ServerConnectedEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onServerConnectedHigh(ServerConnectedEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onServerConnected(ServerConnectedEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onServerConnectedLow(ServerConnectedEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onServerConnectedLowest(ServerConnectedEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onTargetedHighest(TargetedEvent e) {
		onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onTargetedHigh(TargetedEvent e) {
		onAnyEvent(EventPriority.HIGH, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onTargeted(TargetedEvent e) {
		onAnyEvent(EventPriority.NORMAL, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void onTargetedLow(TargetedEvent e) {
		onAnyEvent(EventPriority.LOW, e, e.getClass());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onTargetedLowest(TargetedEvent e) {
		onAnyEvent(EventPriority.LOWEST, e, e.getClass());
	}
	
	//private
	private <T extends Event> void onAnyEvent(byte priority, T event, Class<? extends Event> clazz) {
		if (priority == EventPriority.NORMAL) {
			normalEventHandler.onAnyEvent(event, clazz);
		} else if (priority == EventPriority.HIGHEST) {
			highestEventHandler.onAnyEvent(event, clazz);
		} else if (priority == EventPriority.HIGH) {
			highEventHandler.onAnyEvent(event, clazz);
		} else if (priority == EventPriority.LOWEST) {
			lowestEventHandler.onAnyEvent(event, clazz);
		} else if (priority == EventPriority.LOW) {
			lowEventHandler.onAnyEvent(event, clazz);
		}
	}
}
