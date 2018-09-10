package ninja.egg82.plugin.handlers.events;

import ninja.egg82.patterns.Command;

public abstract class HighEventHandler<T> extends Command implements IEventHandler<T> {
    // vars
    protected T event = null;

    // constructor
    public HighEventHandler() {
        super();
    }

    // public
    public T getEvent() {
        return event;
    }

    public void setEvent(T event) {
        this.event = event;
    }

    // private

}