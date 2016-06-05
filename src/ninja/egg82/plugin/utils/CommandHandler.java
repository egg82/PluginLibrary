package ninja.egg82.plugin.utils;

import java.util.HashMap;

import org.bukkit.command.CommandSender;

import ninja.egg82.plugin.commands.PluginCommand;
import ninja.egg82.plugin.utils.interfaces.ICommandHandler;

public class CommandHandler implements ICommandHandler {
	//vars
	private HashMap<String, Class<? extends PluginCommand>> commands = new HashMap<String, Class<? extends PluginCommand>>();
	
	//constructor
	public CommandHandler() {
		
	}
	
	//public
	public void initialize() {
		
	}
	public void destroy() {
		
	}
	
	public void addCommand(String command, Class<? extends PluginCommand> commandToRun) {
		if (command == null || command.isEmpty() || commandToRun == null) {
			return;
		}
		
		commands.put(command.toLowerCase(), commandToRun);
	}
	public void removeCommand(String command) {
		commands.remove(command.toLowerCase());
	}
	public void clearCommands() {
		commands.clear();
	}
	
	public void runCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		Class<? extends PluginCommand> get = commands.get(command.getName().toLowerCase());
		PluginCommand run = null;
		
		if (get == null) {
			return;
		}
		
		try {
			run = get.getDeclaredConstructor(CommandSender.class, org.bukkit.command.Command.class, String.class, String[].class).newInstance(sender, command, label, args);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return;
		}
		
		run.start();
	}
	
	public boolean hasCommand(String command) {
		return commands.containsKey(command.toLowerCase());
	}
	
	//private
	
}