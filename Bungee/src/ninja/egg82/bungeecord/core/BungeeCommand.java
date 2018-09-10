package ninja.egg82.bungeecord.core;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import ninja.egg82.analytics.exceptions.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.handlers.async.AsyncCommandHandler;

public class BungeeCommand extends Command implements TabExecutor {
    // vars
    private Class<? extends AsyncCommandHandler> command = null;
    private volatile AsyncCommandHandler initializedCommand = null;

    // constructor
    public BungeeCommand(String name, Class<? extends AsyncCommandHandler> command) {
        super(name);

        this.command = command;
    }

    // public
    public Class<? extends AsyncCommandHandler> getCommand() {
        return command;
    }

    public void execute(CommandSender sender, String[] args) {
        initializeCommand(sender, args);

        if (initializedCommand == null) {
            return;
        }

        initializedCommand.start();
    }

    public void undo(CommandSender sender, String[] args) {
        if (initializedCommand == null) {
            return;
        }

        initializedCommand.setSender(new BungeeSender(sender));
        initializedCommand.setArgs(args);

        initializedCommand.undo();
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        initializeCommand(sender, args);

        if (initializedCommand == null) {
            return null;
        }

        return initializedCommand.tabComplete();
    }

    // private
    private void initializeCommand(CommandSender sender, String[] args) {
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

        initializedCommand.setSender(new BungeeSender(sender));
        initializedCommand.setArgs(args);
    }
}
