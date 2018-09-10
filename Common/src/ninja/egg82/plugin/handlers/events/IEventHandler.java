package ninja.egg82.plugin.handlers.events;

import ninja.egg82.patterns.ICommand;

public interface IEventHandler<T> extends ICommand {
    // functions
    T getEvent();

    void setEvent(T event);
}
