package ninja.egg82.plugin.utils;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import gnu.trove.map.hash.THashMap;
import ninja.egg82.plugin.commands.PluginCommand;

public class CommandHandler implements ICommandHandler {
	//vars
	private THashMap<String, Class<? extends PluginCommand>> commands = new THashMap<String, Class<? extends PluginCommand>>();
	private THashMap<String, PluginCommand> initializedCommands = new THashMap<String, PluginCommand>();
	private ArrayList<PluginCommand> initializedCommandsList = new ArrayList<PluginCommand>();
	
	//constructor
	public CommandHandler() {
		
	}
	
	//public
	public void addCommand(String command, Class<? extends PluginCommand> commandToRun) {
		if (command == null || command.isEmpty() || commandToRun == null) {
			return;
		}
		
		commands.put(command.toLowerCase(), commandToRun);
	}
	public void removeCommand(String command) {
		String lowerCommand = command.toLowerCase();
		
		commands.remove(lowerCommand);
		initializedCommands.computeIfPresent(lowerCommand, (k,v) -> {
			initializedCommandsList.remove(v);
			return null;
		});
	}
	public void clearCommands() {
		commands.clear();
	}
	public boolean hasCommand(String command) {
		return commands.containsKey(command.toLowerCase());
	}
	
	public void runCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		PluginCommand c = initializedCommands.computeIfAbsent(command.getName().toLowerCase(), (k) -> {
			return initializeCommand(k, commands.get(k));
		});
		
		if (c == null) {
			return;
		}
		
		c.setSender(sender);
		c.setCommand(command);
		c.setLabel(label);
		c.setArgs(args);
		c.start();
	}
	
	public PluginCommand[] getInitializedCommands() {
		return initializedCommandsList.toArray(new PluginCommand[0]);
	}
	
	//private
	private PluginCommand initializeCommand(String name, Class<? extends PluginCommand> command) {
		if (command == null) {
			return null;
		}
		
		PluginCommand run = null;
		
		try {
			run = command.newInstance();
		} catch (Exception ex) {
			return null;
		}
		
		initializedCommands.put(name, run);
		initializedCommandsList.add(run);
		return run;
	}
}