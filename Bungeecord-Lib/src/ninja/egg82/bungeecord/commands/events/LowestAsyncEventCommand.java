package ninja.egg82.bungeecord.commands.events;

import net.md_5.bungee.api.plugin.Event;
import ninja.egg82.patterns.Command;

public abstract class LowestAsyncEventCommand<T extends Event> extends Command implements IAsyncEventCommand<T> {
	//vars
	protected T event = null;
	
	//constructor
	public LowestAsyncEventCommand() {
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
