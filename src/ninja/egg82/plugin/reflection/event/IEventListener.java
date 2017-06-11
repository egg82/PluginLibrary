package ninja.egg82.plugin.reflection.event;

import org.bukkit.event.Event;

import ninja.egg82.plugin.commands.EventCommand;

public interface IEventListener {
	//functions
	void setEvent(Class<? extends Event> event, Class<? extends EventCommand> clazz);
	boolean hasEvent(Class<? extends Event> event);
	void clear();
}
