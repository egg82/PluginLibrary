package ninja.egg82.plugin.handlers;

import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ninja.egg82.plugin.commands.PluginCommand;

public final class CommandHandler {
	//vars
	private HashMap<String, Class<? extends PluginCommand>> commands = new HashMap<String, Class<? extends PluginCommand>>();
	private HashMap<String, PluginCommand> initializedCommands = new HashMap<String, PluginCommand>();
	
	//constructor
	public CommandHandler() {
		
	}
	
	//public
	public synchronized void setCommand(String command, Class<? extends PluginCommand> clazz) {
		if (command == null) {
			throw new IllegalArgumentException("command cannot be null.");
		}
		
		String key = command.toLowerCase();
		
		if (clazz == null) {
			// Remove command
			initializedCommands.remove(key);
			commands.remove(key);
		} else {
			// Add/Replace command
			initializedCommands.remove(key);
			commands.put(key, clazz);
		}
	}
	public synchronized boolean hasCommand(String command) {
		return commands.containsKey(command.toLowerCase());
	}
	public synchronized void clear() {
		initializedCommands.clear();
		commands.clear();
	}
	
	public synchronized void runCommand(CommandSender sender, Command command, String label, String[] args) {
		String key = command.getName().toLowerCase();
		
		PluginCommand run = initializedCommands.get(key);
		Class<? extends PluginCommand> c = commands.get(key);
		
		// run might be null, but c will never be as long as the command actually exists
		if (c == null) {
			return;
		}
		
		// Lazy initialize. No need to create a command until it's actually going to be used
		if (run == null) {
			// Create a new command and store it
			try {
				run = c.getDeclaredConstructor(CommandSender.class, Command.class, String.class, String[].class).newInstance(sender, command, label, args);
			} catch (Exception ex) {
				return;
			}
			initializedCommands.put(key, run);
		} else {
			// We already have the command initialized, no need to create a new one
			run.setSender(sender);
			run.setCommand(command);
			run.setLabel(label);
			run.setArgs(args);
		}
		
		run.start();
	}
	public synchronized void undoInitializedCommands(CommandSender sender, String[] args) {
		initializedCommands.forEach((k, run) -> {
			run.setSender(sender);
			run.setArgs(args);
			run.undo();
		});
	}
	public synchronized List<String> tabComplete(CommandSender sender, Command command, String label, String[] args) {
		String key = command.getName().toLowerCase();
		
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
				return null;
			}
			initializedCommands.put(key, run);
		}
		
		return run.tabComplete(sender, command, label, args);
	}
	
	//private
	
}