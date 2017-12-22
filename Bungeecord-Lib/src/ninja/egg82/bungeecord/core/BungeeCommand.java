package ninja.egg82.bungeecord.core;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import ninja.egg82.bungeecord.commands.PluginCommand;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;

public class BungeeCommand extends Command implements TabExecutor {
	//vars
	private Class<? extends PluginCommand> command = null;
	private PluginCommand initializedCommand = null;
	
	//constructor
	public BungeeCommand(String name, Class<? extends PluginCommand> command) {
		super(name);
		
		this.command = command;
	}
	
	//public
	public void execute(CommandSender sender, String[] args) {
		initializeCommand(sender, args);
		
		if (initializedCommand == null) {
			return;
		}
		
		try {
			initializedCommand.start();
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw ex;
		}
	}
	public void undo(CommandSender sender, String[] args) {
		if (initializedCommand == null) {
			return;
		}
		
		initializedCommand.setSender(sender);
		initializedCommand.setArgs(args);
		
		try {
			initializedCommand.undo();
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw ex;
		}
	}
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		initializeCommand(sender, args);
		
		if (initializedCommand == null) {
			return null;
		}
		
		try {
			return initializedCommand.tabComplete();
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw ex;
		}
	}
	
	//private
	private void initializeCommand(CommandSender sender, String[] args) {
		// Lazy initialize. No need to create a command until it's actually going to be used
		if (initializedCommand == null) {
			// Create a new command and store it
			try {
				initializedCommand = command.newInstance();
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
				throw new RuntimeException(ex);
			}
		}
		
		initializedCommand.setSender(sender);
		initializedCommand.setArgs(args);
	}
}
