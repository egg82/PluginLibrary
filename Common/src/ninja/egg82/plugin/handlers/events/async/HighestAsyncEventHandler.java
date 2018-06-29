package ninja.egg82.plugin.handlers.events.async;

import ninja.egg82.patterns.Command;

public abstract class HighestAsyncEventHandler<T> extends Command implements IAsyncEventHandler<T> {
	//vars
	protected T event = null;
	
	//constructor
	public HighestAsyncEventHandler() {
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
