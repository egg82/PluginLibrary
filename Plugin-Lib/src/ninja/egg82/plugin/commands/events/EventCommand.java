package ninja.egg82.plugin.commands.events;

import org.bukkit.event.Event;

import ninja.egg82.patterns.Command;

public abstract class EventCommand<T extends Event> extends Command implements IEventCommand<T> {
	//vars
	protected T event = null;
	
	//constructor
	public EventCommand() {
		super();
	}
	
	//public
	public T getEvent() {
		return event;
	}
	public void setEvent(T event) {
		this.event = event;
	}
	
	//private
	
}