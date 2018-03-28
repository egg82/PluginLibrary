package ninja.egg82.bungeecord.commands.events;

import ninja.egg82.patterns.ICommand;

public interface IAsyncEventCommand<T> extends ICommand {
	//functions
	T getEvent();
	void setEvent(T event);
}
