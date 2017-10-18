package ninja.egg82.plugin.reflection.event;

import org.bukkit.event.Event;

import ninja.egg82.plugin.commands.EventCommand;

public interface IEventListener {
	//functions
	boolean addEventHandler(Class<? extends Event> event, Class<EventCommand<? extends Event>> clazz);
	boolean removeEventHandler(Class<EventCommand<? extends Event>> clazz);
	boolean removeEventHandler(Class<? extends Event> event, Class<EventCommand<? extends Event>> clazz);
	boolean hasEventHandler(Class<? extends Event> event);
	void clear();
	
	int addEventsFromPackage(String packageName);
	int addEventsFromPackage(String packageName, boolean recursive);
}
