package ninja.egg82.bungeecord.handlers;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import ninja.egg82.bungeecord.commands.EventCommand;
import ninja.egg82.bungeecord.enums.MessageHandlerType;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public class EventListener implements Listener {
	//vars
	private ConcurrentHashMap<String, IObjectPool<Class<? extends EventCommand<? extends Event>>>> events = new ConcurrentHashMap<String, IObjectPool<Class<? extends EventCommand<? extends Event>>>>();
	private ConcurrentHashMap<String, IObjectPool<EventCommand<? extends Event>>> initializedEvents = new ConcurrentHashMap<String, IObjectPool<EventCommand<? extends Event>>>();
	
	//constructor
	public EventListener() {
		Plugin plugin = ServiceLocator.getService(Plugin.class);
		plugin.getProxy().getPluginManager().registerListener(plugin, this);
	}
	public void finalize() {
		destroy();
	}
	
	//public
	public boolean addEventHandler(Class<? extends Event> event, Class<? extends EventCommand<? extends Event>> clazz) {
		if (event == null) {
			throw new ArgumentNullException("event");
		}
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		String key = event.getName();
		
		IObjectPool<Class<? extends EventCommand<? extends Event>>> pool = events.get(key);
		if (pool == null) {
			pool = new DynamicObjectPool<Class<? extends EventCommand<? extends Event>>>();
		}
		pool = CollectionUtil.putIfAbsent(events, key, pool);
		if (!pool.contains(clazz)) {
			pool.add(clazz);
			initializedEvents.remove(key);
			return true;
		} else {
			return false;
		}
	}
	public boolean removeEventHandler(Class<? extends EventCommand<? extends Event>> clazz) {
		boolean modified = false;
		for (Entry<String, IObjectPool<Class<? extends EventCommand<? extends Event>>>> kvp : events.entrySet()) {
			if (kvp.getValue().remove(clazz)) {
				initializedEvents.remove(kvp.getKey());
				modified = true;
			}
		}
		return modified;
	}
	public boolean removeEventHandler(Class<? extends Event> event, Class<? extends EventCommand<? extends Event>> clazz) {
		String key = event.getName();
		
		IObjectPool<Class<? extends EventCommand<? extends Event>>> pool = events.get(key);
		if (pool == null) {
			return false;
		}
		
		if (!pool.remove(clazz)) {
			return false;
		} else {
			initializedEvents.remove(key);
			return true;
		}
	}
	public boolean hasEventHandler(Class<? extends Event> event) {
		return events.containsKey(event.getName());
	}
	public void clear() {
		initializedEvents.clear();
		events.clear();
	}
	public void destroy() {
		clear();
		
		Plugin plugin = ServiceLocator.getService(Plugin.class);
		plugin.getProxy().getPluginManager().unregisterListener(this);
	}
	
	public int addEventsFromPackage(String packageName) {
		return addEventsFromPackage(packageName, true);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int addEventsFromPackage(String packageName, boolean recursive) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		int numEvents = 0;
		
		List<Class<? extends EventCommand>> enums = ReflectUtil.getClassesParameterized(EventCommand.class, packageName, recursive, false, false);
		for (Class<? extends EventCommand> c : enums) {
			Class<? extends Event> eventType = null;
			try {
				eventType = (Class<? extends Event>) ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments()[0];
			} catch (Exception ex) {
				continue;
			}
			
			if (addEventHandler(eventType, (Class<EventCommand<? extends Event>>) c)) {
				numEvents++;
			}
		}
		
		return numEvents;
	}
	
	//generic events
	@EventHandler
	public void onAsync(AsyncEvent<?> e) {
		onAnyEvent(e, e.getClass());
	}
	
	//player events
	@EventHandler
	public void onPreLogin(PreLoginEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onLogin(LoginEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPostLogin(PostLoginEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerHandshake(PlayerHandshakeEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onServerConnect(ServerConnectEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onServerDisconnect(ServerDisconnectEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onServerKick(ServerKickEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onServerSwitch(ServerSwitchEvent e) {
		onAnyEvent(e, e.getClass());
	}
	
	//chat events
	@EventHandler
	public void onChat(ChatEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onPermissionCheck(PermissionCheckEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onTabComplete(TabCompleteEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onTabCompleteResponse(TabCompleteResponseEvent e) {
		onAnyEvent(e, e.getClass());
	}
	
	//plugin events
	@EventHandler
	public void onPluginMessage(PluginMessageEvent e) {
		if (ServiceLocator.getService(IMessageHandler.class).getType() == MessageHandlerType.BUNGEE) {
			ServiceLocator.getService(EnhancedBungeeMessageHandler.class).onPluginMessage(e);
		}
		onAnyEvent(e, e.getClass());
	}
	
	//proxy events
	@EventHandler
	public void onProxyPing(ProxyPingEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onProxyReload(ProxyReloadEvent e) {
		onAnyEvent(e, e.getClass());
	}
	
	//server events
	@EventHandler
	public void onServerConnected(ServerConnectedEvent e) {
		onAnyEvent(e, e.getClass());
	}
	@EventHandler
	public void onTargeted(TargetedEvent e) {
		onAnyEvent(e, e.getClass());
	}
	
	//private
	@SuppressWarnings("unchecked")
	private <T extends Event> void onAnyEvent(T event, Class<? extends Event> clazz) {
		String key = clazz.getName();
		
		/*if (EventUtil.isDuplicate(key, event)) {
			return;
		}*/
		
		IObjectPool<EventCommand<? extends Event>> run = initializedEvents.get(key);
		IObjectPool<Class<? extends EventCommand<? extends Event>>> c = events.get(key);
		
		// run might be null, but c will never be as long as the event actually exists
		if (c == null) {
			return;
		}
		
		Exception lastEx = null;
		
		// Lazy initialize. No need to create an event that's never been used
		if (run == null) {
			// Create a new event and store it
			run = new DynamicObjectPool<EventCommand<? extends Event>>();
			
			for (Class<? extends EventCommand<? extends Event>> e : c) {
				try {
					// We would prefer the command to fail here, hence the cast
					run.add((EventCommand<T>) e.newInstance());
				} catch (Exception ex) {
					ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
					lastEx = ex;
				}
			}
			
			run = CollectionUtil.putIfAbsent(initializedEvents, key, run);
		}
		
		for (EventCommand<? extends Event> e : run) {
			((EventCommand<T>) e).setEvent(event);
			try {
				e.start();
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
				lastEx = ex;
			}
		}
		
		if (lastEx != null) {
			throw new RuntimeException(lastEx);
		}
	}
}
