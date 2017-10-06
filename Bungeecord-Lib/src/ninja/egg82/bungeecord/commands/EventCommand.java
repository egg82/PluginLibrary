package ninja.egg82.bungeecord.commands;

import net.md_5.bungee.api.plugin.Event;
import ninja.egg82.patterns.SynchronousCommand;

public abstract class EventCommand<T extends Event> extends SynchronousCommand {
	//vars
	protected T event = null;
	
	//constructor
	public EventCommand(T event) {
		super();
		this.event = event;
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
