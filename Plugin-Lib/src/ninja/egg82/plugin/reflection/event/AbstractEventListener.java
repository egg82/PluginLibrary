package ninja.egg82.plugin.reflection.event;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.EventCommand;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public abstract class AbstractEventListener implements IEventListener, Listener {
	//vars
	private ConcurrentHashMap<String, IObjectPool<Class<EventCommand<? extends Event>>>> events = new ConcurrentHashMap<String, IObjectPool<Class<EventCommand<? extends Event>>>>();
	private ConcurrentHashMap<String, IObjectPool<EventCommand<? extends Event>>> initializedEvents = new ConcurrentHashMap<String, IObjectPool<EventCommand<? extends Event>>>();
	
	//constructor
	public AbstractEventListener() {
		Bukkit.getServer().getPluginManager().registerEvents(this, ServiceLocator.getService(JavaPlugin.class));
	}
	
	//public
	public boolean addEventHandler(Class<? extends Event> event, Class<EventCommand<? extends Event>> clazz) {
		if (event == null) {
			throw new ArgumentNullException("event");
		}
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		String key = event.getName();
		
		IObjectPool<Class<EventCommand<? extends Event>>> pool = events.get(key);
		if (pool == null) {
			pool = new DynamicObjectPool<Class<EventCommand<? extends Event>>>();
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
	public boolean removeEventHandler(Class<EventCommand<? extends Event>> clazz) {
		boolean modified = false;
		for (Entry<String, IObjectPool<Class<EventCommand<? extends Event>>>> kvp : events.entrySet()) {
			if (kvp.getValue().remove(clazz)) {
				initializedEvents.remove(kvp.getKey());
				modified = true;
			}
		}
		return modified;
	}
	public boolean removeEventHandler(Class<? extends Event> event, Class<EventCommand<? extends Event>> clazz) {
		String key = event.getName();
		
		IObjectPool<Class<EventCommand<? extends Event>>> pool = events.get(key);
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
	protected final <T extends Event> void onAnyEvent(T event, Class<? extends Event> clazz) {
		String key = clazz.getName();
		
		/*if (EventUtil.isDuplicate(key, event)) {
			return;
		}*/
		
		IObjectPool<EventCommand<? extends Event>> run = initializedEvents.get(key);
		IObjectPool<Class<EventCommand<? extends Event>>> c = events.get(key);
		
		// run might be null, but c will never be as long as the event actually exists
		if (c == null) {
			return;
		}
		
		// Lazy initialize. No need to create an event that's never been used
		if (run == null) {
			// Create a new event and store it
			run = new DynamicObjectPool<EventCommand<? extends Event>>();
			
			for (Class<EventCommand<? extends Event>> e : c) {
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
}
