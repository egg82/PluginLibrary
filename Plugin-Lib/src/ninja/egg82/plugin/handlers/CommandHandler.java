package ninja.egg82.plugin.handlers;

import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.PluginCommand;

public final class CommandHandler {
	//vars
	private HashMap<String, Class<? extends PluginCommand>> commands = new HashMap<String, Class<? extends PluginCommand>>();
	private HashMap<String, String> commandAliases = new HashMap<String, String>();
	private HashMap<String, PluginCommand> initializedCommands = new HashMap<String, PluginCommand>();
	
	//constructor
	public CommandHandler() {
		
	}
	
	//public
	public synchronized void setCommand(String command, Class<? extends PluginCommand> clazz) {
		setCommand(command, clazz, null);
	}
	public synchronized void setCommand(String command, Class<? extends PluginCommand> clazz, String[] aliases) {
		if (command == null) {
			throw new ArgumentNullException("command");
		}
		
		String key = command.toLowerCase();
		
		if (clazz == null) {
			// Remove command
			initializedCommands.remove(key);
			commands.remove(key);
			
			for (String k : commandAliases.keySet()) {
				if (commandAliases.get(k).equals(key)) {
					commandAliases.remove(k);
				}
			}
		} else {
			// Add/Replace command
			initializedCommands.remove(key);
			commands.put(key, clazz);
			
			for (String k : commandAliases.keySet()) {
				if (commandAliases.get(k).equals(key)) {
					commandAliases.remove(k);
				}
			}
			if (aliases != null) {
				for (int i = 0; i < aliases.length; i++) {
					commandAliases.put(aliases[i].toLowerCase(), key);
				}
			}
		}
	}
	public synchronized boolean hasCommand(String command) {
		return command != null && (commands.containsKey(command.toLowerCase()) || commandAliases.containsKey(command.toLowerCase()));
	}
	public synchronized void clear() {
		initializedCommands.clear();
		commands.clear();
		commandAliases.clear();
	}
	
	public synchronized void runCommand(CommandSender sender, Command command, String label, String[] args) {
		PluginCommand run = getCommand(sender, command, label, args);
		
		if (run == null) {
			return;
		}
		
		try {
			run.start();
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw ex;
		}
	}
	public synchronized void undoInitializedCommands(CommandSender sender, String[] args) {
		initializedCommands.forEach((k, run) -> {
			run.setSender(sender);
			run.setArgs(args);
			
			try {
				run.undo();
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
				throw ex;
			}
		});
	}
	public synchronized List<String> tabComplete(CommandSender sender, Command command, String label, String[] args) {
		PluginCommand run = getCommand(sender, command, label, args);
		
		if (run == null) {
			return null;
		}
		
		try {
			return run.tabComplete(sender, command, label, args);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw ex;
		}
	}
	
	//private
	private synchronized PluginCommand getCommand(CommandSender sender, Command command, String label, String[] args) {
		String key = command.getName().toLowerCase();
		
		if (commandAliases.containsKey(key)) {
			key = commandAliases.get(key);
		}
		
		PluginCommand run = initializedCommands.get(key);
		Class<? extends PluginCommand> c = commands.get(key);
		
		// run might be null, but c will never be as long as the command actually exists
		if (c == null) {
			return null;
		}
		
		// Lazy initialize. No need to create a command until it's actually going to be used
		if (run == null) {
			// Create a new command and store it
			try {
				run = c.getDeclaredConstructor(CommandSender.class, Command.class, String.class, String[].class).newInstance(sender, command, label, args);
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
				return null;
			}
			initializedCommands.put(key, run);
		} else {
			// We already have the command initialized, no need to create a new one
			run.setSender(sender);
			run.setCommand(command);
			run.setLabel(label);
			run.setArgs(args);
		}
		
		return run;
	}
}