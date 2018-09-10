package ninja.egg82.plugin.handlers.async;

import java.util.Collection;

import ninja.egg82.patterns.Command;
import ninja.egg82.plugin.core.sender.Sender;

public abstract class AsyncCommandHandler extends Command {
    // vars
    protected Sender sender = null;
    protected String commandName = null;
    protected String[] args = null;

    protected Object handle = null;

    // constructor
    public AsyncCommandHandler() {
        super();
    }

    // public
    public final Sender getSender() {
        return sender;
    }

    public final void setSender(Sender sender) {
        this.sender = sender;
    }

    public final String getCommandName() {
        return commandName;
    }

    public final void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public final String[] getArgs() {
        return args;
    }

    public final void setArgs(String[] args) {
        this.args = args;
    }

    public final Object getHandle() {
        return handle;
    }

    public final void setHandle(Object handle) {
        this.handle = handle;
    }

    public final void undo() {
        onUndo();
    }

    public Collection<String> tabComplete() {
        return null;
    }

    // private
    protected abstract void onUndo();
}