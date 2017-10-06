package ninja.egg82.bungeecord.handlers;

import java.util.HashMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import ninja.egg82.bungeecord.commands.PluginCommand;
import ninja.egg82.bungeecord.core.BungeeCommand;
import ninja.egg82.bungeecord.enums.BungeeInitType;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.startup.InitRegistry;

public final class CommandHandler {
	//vars
	private Plugin plugin = null;
	private PluginManager manager = null;
	
	private HashMap<String, BungeeCommand> bungeeCommands = new HashMap<String, BungeeCommand>();
	private HashMap<String, String> commandAliases = new HashMap<String, String>();
	
	//constructor
	public CommandHandler() {
		plugin = ServiceLocator.getService(InitRegistry.class).getRegister(BungeeInitType.PLUGIN, Plugin.class);
		manager = plugin.getProxy().getPluginManager();
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
			BungeeCommand c = bungeeCommands.remove(key);
			if (c != null) {
				manager.unregisterCommand(c);
			}
			
			for (String k : commandAliases.keySet()) {
				if (commandAliases.get(k).equals(key)) {
					commandAliases.remove(k);
				}
			}
		} else {
			// Add/Replace command
			BungeeCommand c = bungeeCommands.remove(key);
			if (c != null) {
				manager.unregisterCommand(c);
			}
			c = new BungeeCommand(key, clazz);
			manager.registerCommand(plugin, c);
			bungeeCommands.put(key, c);
			
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
		return command != null && (bungeeCommands.containsKey(command.toLowerCase()) || commandAliases.containsKey(command.toLowerCase()));
	}
	public synchronized void clear() {
		bungeeCommands.forEach((k, v) -> {
			manager.unregisterCommand(v);
		});
		
		bungeeCommands.clear();
		commandAliases.clear();
	}
	
	public synchronized void runCommand(CommandSender sender, String command, String[] args) {
		BungeeCommand run = bungeeCommands.get(command.toLowerCase());
		
		if (run == null) {
			return;
		}
		
		run.execute(sender, args);
	}
	public synchronized void undoInitializedCommands(CommandSender sender, String[] args) {
		bungeeCommands.forEach((k, run) -> {
			run.undo(sender, args);
		});
	}
	public synchronized Iterable<String> tabComplete(CommandSender sender, String command, String[] args) {
		BungeeCommand run = bungeeCommands.get(command.toLowerCase());
		
		if (run == null) {
			return null;
		}
		
		return run.onTabComplete(sender, args);
	}
	
	//private
	
}