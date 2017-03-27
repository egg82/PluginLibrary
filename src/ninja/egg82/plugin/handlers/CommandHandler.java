package ninja.egg82.plugin.handlers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import ninja.egg82.plugin.commands.PluginCommand;

public final class CommandHandler {
	//vars
	private UnifiedMap<String, Class<? extends PluginCommand>> commands = new UnifiedMap<String, Class<? extends PluginCommand>>();
	
	//constructor
	public CommandHandler() {
		
	}
	
	//public
	public synchronized void setCommand(String command, Class<? extends PluginCommand> clazz) {
		if (command == null) {
			throw new IllegalArgumentException("command cannot be null.");
		}
		
		if (clazz == null) {
			commands.remove(command.toLowerCase());
		} else {
			commands.put(command.toLowerCase(), clazz);
		}
	}
	public synchronized boolean hasCommand(String command) {
		return commands.containsKey(command.toLowerCase());
	}
	public synchronized void clear() {
		commands.clear();
	}
	
	public synchronized void runCommand(CommandSender sender, Command command, String label, String[] args) {
		Class<? extends PluginCommand> c = commands.get(command.getName().toLowerCase());
		
		if (c == null) {
			return;
		}
		
		PluginCommand run = null;
		try {
			run = c.getDeclaredConstructor(CommandSender.class, Command.class, String.class, String[].class).newInstance(sender, command, label, args);
		} catch (Exception ex) {
			return;
		}
		run.start();
	}
	
	//private
	
}