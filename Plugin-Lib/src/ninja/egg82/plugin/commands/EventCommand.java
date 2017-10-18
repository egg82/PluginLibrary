package ninja.egg82.plugin.commands;

import org.bukkit.event.Event;

import ninja.egg82.patterns.SynchronousCommand;

public abstract class EventCommand<T extends Event> extends SynchronousCommand {
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