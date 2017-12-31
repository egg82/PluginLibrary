package ninja.egg82.plugin.core;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.handlers.EventListener;

public class EventExecutorWrapper implements EventExecutor {
	//vars
	
	//constructor
	public EventExecutorWrapper() {
		
	}
	
	//public
	
	//private
	public void execute(Listener listener, Event event) throws EventException {
		ServiceLocator.getService(EventListener.class).dispatchEvent(event);
	}
}
