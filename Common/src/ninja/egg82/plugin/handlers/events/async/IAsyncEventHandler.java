package ninja.egg82.plugin.handlers.events.async;

import ninja.egg82.patterns.ICommand;

public interface IAsyncEventHandler<T> extends ICommand {
	//functions
	T getEvent();
	void setEvent(T event);
}
