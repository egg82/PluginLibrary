package ninja.egg82.plugin.commands.events;

import ninja.egg82.patterns.ICommand;

public interface IEventCommand<T> extends ICommand {
	//functions
	T getEvent();
	void setEvent(T event);
}
