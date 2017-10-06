package ninja.egg82.bungeecord.handlers;

import java.util.HashMap;

import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import ninja.egg82.bungeecord.commands.EventCommand;
import ninja.egg82.bungeecord.enums.BungeeInitType;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.startup.InitRegistry;

public class EventListener implements Listener {
	//vars
	private HashMap<String, Class<? extends EventCommand<? extends Event>>> events = new HashMap<String, Class<? extends EventCommand<? extends Event>>>();
	private HashMap<String, EventCommand<? extends Event>> initializedEvents = new HashMap<String, EventCommand<? extends Event>>();
	
	//constructor
	public EventListener() {
		Plugin plugin = ServiceLocator.getService(InitRegistry.class).getRegister(BungeeInitType.PLUGIN, Plugin.class);
		plugin.getProxy().getPluginManager().registerListener(plugin, this);
	}
	
	//public
	public synchronized void setEvent(Class<? extends Event> event, Class<? extends EventCommand<? extends Event>> clazz) {
		if (event == null) {
			throw new ArgumentNullException("event");
		}
		
		String key = event.getName();
		
		if (clazz == null) {
			// Remove event
			initializedEvents.remove(key);
			events.remove(key);
		} else {
			// Add/Replace event
			initializedEvents.remove(key);
			events.put(key, clazz);
		}
	}
	public synchronized boolean hasEvent(Class<? extends Event> event) {
		return events.containsKey(event.getName());
	}
	public synchronized void clear() {
		initializedEvents.clear();
		events.clear();
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
	private synchronized <T extends Event> void onAnyEvent(T event, Class<? extends Event> clazz) {
		String key = clazz.getName();
		
		/*if (EventUtil.isDuplicate(key, event)) {
			return;
		}*/
		
		EventCommand<T> run = (EventCommand<T>) initializedEvents.get(key);
		Class<? extends EventCommand<T>> c = (Class<? extends EventCommand<T>>) events.get(key);
		
		// run might be null, but c will never be as long as the event actually exists
		if (c == null) {
			return;
		}
		
		// Lazy initialize. No need to create an event that's never been used
		if (run == null) {
			// Create a new event and store it
			try {
				run = c.getDeclaredConstructor(clazz).newInstance(event);
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
				return;
			}
			initializedEvents.put(key, run);
		} else {
			// We already have the event initialized, no need to create a new one
			run.setEvent(event);
		}
		
		try {
			run.start();
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw ex;
		}
	}
}
