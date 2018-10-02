package ninja.egg82.velocity.core;

import java.util.ArrayList;
import java.util.List;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;

import ninja.egg82.analytics.exceptions.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.handlers.async.AsyncCommandHandler;

public class VelocityCommand implements Command {
    // vars
    private Class<? extends AsyncCommandHandler> command = null;
    private volatile AsyncCommandHandler initializedCommand = null;

    // constructor
    public VelocityCommand(Class<? extends AsyncCommandHandler> command) {
        this.command = command;
    }

    // public
    public Class<? extends AsyncCommandHandler> getCommand() {
        return command;
    }

    public void execute(CommandSource sender, String[] args) {
        initializeCommand(sender, args);

        if (initializedCommand == null) {
            return;
        }

        initializedCommand.start();
    }
    public void undo(CommandSource sender, String[] args) {
        if (initializedCommand == null) {
            return;
        }

        initializedCommand.setSender(new VelocitySender(sender));
        initializedCommand.setArgs(args);

        initializedCommand.undo();
    }
    public List<String> suggest(CommandSource sender, String[] args) {
        initializeCommand(sender, args);

        if (initializedCommand == null) {
            return null;
        }

        return new ArrayList<String>(initializedCommand.tabComplete());
    }

    // private
    private void initializeCommand(CommandSource sender, String[] args) {
        // Lazy initialize. No need to create a command until it's actually going to be
        // used
        if (initializedCommand == null) {
            // Create a new command and store it
            try {
                initializedCommand = command.newInstance();
            } catch (Exception ex) {
                IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                if (handler != null) {
                    handler.sendException(ex);
                }
                throw new RuntimeException(ex);
            }
        }

        initializedCommand.setSender(new VelocitySender(sender));
        initializedCommand.setArgs(args);
    }
}
