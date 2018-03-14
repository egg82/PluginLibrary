package ninja.egg82.plugin.handlers;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.Registry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.EventCommand;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public class EventListener implements Listener {
	//vars
	private ConcurrentHashMap<String, IObjectPool<Class<? extends EventCommand<? extends Event>>>> events = new ConcurrentHashMap<String, IObjectPool<Class<? extends EventCommand<? extends Event>>>>();
	private ConcurrentHashMap<String, IObjectPool<EventCommand<? extends Event>>> initializedEvents = new ConcurrentHashMap<String, IObjectPool<EventCommand<? extends Event>>>();
	
	private IRegistry<Event> lastEvents = new Registry<Event>(Event.class);
	
	//constructor
	public EventListener() {
		List<Class<Event>> events = ReflectUtil.getClasses(Event.class, "org.bukkit.event", true, false, false);
		
		JavaPlugin plugin = ServiceLocator.getService(JavaPlugin.class);
		PluginManager manager = Bukkit.getServer().getPluginManager();
		for (Class<Event> e : events) {
			manager.registerEvent(e, this, EventPriority.NORMAL, ServiceLocator.getService(EventExecutor.class), plugin, false);
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				lastEvents.clear();
			}
		}, 1L, 1L);
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
		
		List<Class<Event>> events = ReflectUtil.getClasses(Event.class, "org.bukkit.event", true, false, false);
		for (Class<Event> e : events) {
			unregisterEvent(e);
		}
	}
	
	public void dispatchEvent(Event e) {
		onAnyEvent(e, e.getClass());
	}
	public void registerEvent(Class<? extends Event> e) {
		registerEvent(e, EventPriority.NORMAL);
	}
	public void registerEvent(Class<? extends Event> e, EventPriority priority) {
		unregisterEvent(e);
		
		PluginManager manager = Bukkit.getServer().getPluginManager();
		manager.registerEvent(e, this, priority, ServiceLocator.getService(EventExecutor.class), ServiceLocator.getService(JavaPlugin.class), true);
	}
	public void unregisterEvent(Class<? extends Event> e) {
		Method m = ReflectUtil.getMethod("getHandlerList", e);
		
		if (m == null) {
			return;
		}
		
		HandlerList list = null;
		try {
			list = (HandlerList) m.invoke(null, new Object[0]);
		} catch (Exception ex) {
			return;
		}
		
		list.unregister(this);
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
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
				continue;
			}
			
			if (addEventHandler(eventType, (Class<EventCommand<? extends Event>>) c)) {
				numEvents++;
			}
		}
		
		return numEvents;
	}
	
	//private
	@SuppressWarnings("unchecked")
	private <T extends Event> void onAnyEvent(T event, Class<? extends Event> clazz) {
		String key = clazz.getName();
		
		if (isDuplicate(key, event)) {
			return;
		}
		
		IObjectPool<EventCommand<? extends Event>> run = initializedEvents.get(key);
		IObjectPool<Class<? extends EventCommand<? extends Event>>> c = events.get(key);
		
		// run might be null, but c will never be as long as the event actually exists
		if (c == null) {
			return;
		}
		
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
					throw new RuntimeException("Cannot initialize event command.", ex);
				}
			}
			
			run = CollectionUtil.putIfAbsent(initializedEvents, key, run);
		}
		
		Exception lastEx = null;
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
			throw new RuntimeException("Cannot undo command.", lastEx);
		}
	}
	
	private boolean isDuplicate(String className, Event event) {
		if (lastEvents.hasRegister(event)) {
			return true;
		}
		lastEvents.setRegister(event, null);
		return false;
	}
}
