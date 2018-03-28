package ninja.egg82.plugin.core;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.event.Event;

import ninja.egg82.concurrent.DynamicConcurrentDeque;
import ninja.egg82.concurrent.IConcurrentDeque;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.events.IEventCommand;
import ninja.egg82.utils.CollectionUtil;

public class CoreEventHandler {
	//vars
	private ConcurrentHashMap<String, IConcurrentDeque<Class<? extends IEventCommand<? extends Event>>>> events = new ConcurrentHashMap<String, IConcurrentDeque<Class<? extends IEventCommand<? extends Event>>>>();
	private ConcurrentHashMap<String, IConcurrentDeque<IEventCommand<? extends Event>>> initializedEvents = new ConcurrentHashMap<String, IConcurrentDeque<IEventCommand<? extends Event>>>();
	
	//constructor
	public CoreEventHandler() {
		
	}
	
	//public
	public boolean addEventHandler(Class<? extends Event> event, Class<? extends IEventCommand<? extends Event>> clazz) {
		String key = event.getName();
		
		IConcurrentDeque<Class<? extends IEventCommand<? extends Event>>> pool = events.get(key);
		if (pool == null) {
			pool = new DynamicConcurrentDeque<Class<? extends IEventCommand<? extends Event>>>();
		}
		pool = CollectionUtil.putIfAbsent(events, key, pool);
		if (pool.contains(clazz)) {
			return false;
		}
		
		pool.add(clazz);
		initializedEvents.remove(key);
		return true;
	}
	public boolean removeEventHandler(Class<? extends IEventCommand<? extends Event>> clazz) {
		boolean modified = false;
		for (Entry<String, IConcurrentDeque<Class<? extends IEventCommand<? extends Event>>>> kvp : events.entrySet()) {
			if (kvp.getValue().remove(clazz)) {
				initializedEvents.remove(kvp.getKey());
				modified = true;
			}
		}
		return modified;
	}
	public boolean removeEventHandler(Class<? extends Event> event, Class<? extends IEventCommand<? extends Event>> clazz) {
		String key = event.getName();
		
		IConcurrentDeque<Class<? extends IEventCommand<? extends Event>>> pool = events.get(key);
		if (pool == null) {
			return false;
		}
		
		if (!pool.remove(clazz)) {
			return false;
		}
		
		initializedEvents.remove(key);
		return true;
	}
	public boolean hasEventHandler(Class<? extends Event> event) {
		return (event != null && events.containsKey(event.getName())) ? true : false;
	}
	public void clear() {
		initializedEvents.clear();
		events.clear();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Event> void onAnyEvent(T event, Class<? extends Event> clazz) {
		String key = clazz.getName();
		
		IConcurrentDeque<IEventCommand<? extends Event>> run = initializedEvents.get(key);
		IConcurrentDeque<Class<? extends IEventCommand<? extends Event>>> c = events.get(key);
		
		// run might be null, but c will never be as long as the event actually exists
		if (c == null) {
			return;
		}
		
		Exception lastEx = null;
		
		// Lazy initialize. No need to create an event that's never been used
		if (run == null) {
			// Create a new event and store it
			run = new DynamicConcurrentDeque<IEventCommand<? extends Event>>();
			
			for (Class<? extends IEventCommand<? extends Event>> e : c) {
				try {
					// We would prefer the command to fail here, hence the cast
					run.add(e.newInstance());
				} catch (Exception ex) {
					ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
					lastEx = ex;
				}
			}
			
			run = CollectionUtil.putIfAbsent(initializedEvents, key, run);
		}
		
		for (IEventCommand<? extends Event> e : run) {
			((IEventCommand<T>) e).setEvent(event);
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
	
	//private
	
}
