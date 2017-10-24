package ninja.egg82.bungeecord.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Iterables;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import ninja.egg82.bungeecord.commands.PluginCommand;
import ninja.egg82.bungeecord.core.BungeeCommand;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public final class CommandHandler {
	//vars
	private Plugin plugin;
	private PluginManager manager = null;
	
	private ConcurrentHashMap<String, String> commandAliases = new ConcurrentHashMap<String, String>();
	private ConcurrentHashMap<String, IObjectPool<BungeeCommand>> bungeeCommands = new ConcurrentHashMap<String, IObjectPool<BungeeCommand>>();
	
	//constructor
	public CommandHandler() {
		plugin = ServiceLocator.getService(Plugin.class);
		manager = plugin.getProxy().getPluginManager();
	}
	
	//public
	public void addAliases(String command, String... aliases) {
		if (command == null) {
			throw new ArgumentNullException("command");
		}
		if (aliases == null || aliases.length == 0) {
			return;
		}
		
		for (String alias : aliases) {
			commandAliases.put(alias, command);
		}
	}
	public void removeAliases(String... aliases) {
		if (aliases == null || aliases.length == 0) {
			return;
		}
		
		for (String alias : aliases) {
			commandAliases.remove(alias);
		}
	}
	
	public boolean addCommandHandler(String command, Class<PluginCommand> clazz) {
		if (command == null) {
			throw new ArgumentNullException("command");
		}
		
		String key = command.toLowerCase();
		
		IObjectPool<BungeeCommand> pool = bungeeCommands.get(key);
		if (pool == null) {
			pool = new DynamicObjectPool<BungeeCommand>();
		}
		pool = CollectionUtil.putIfAbsent(bungeeCommands, key, pool);
		
		for (BungeeCommand c : pool) {
			if (c.getClass().equals(clazz)) {
				return false;
			}
		}
		
		BungeeCommand c = new BungeeCommand(key, clazz);
		pool.add(c);
		manager.registerCommand(plugin, c);
		return true;
	}
	public boolean removeCommandHandler(Class<PluginCommand> clazz) {
		boolean modified = false;
		for (Entry<String, IObjectPool<BungeeCommand>> kvp : bungeeCommands.entrySet()) {
			for (BungeeCommand c : kvp.getValue()) {
				if (c.getClass().equals(clazz)) {
					kvp.getValue().remove(c);
					modified = true;
				}
			}
		}
		return modified;
	}
	public boolean removeCommandHandler(String command, Class<PluginCommand> clazz) {
		String key = command.toLowerCase();
		
		IObjectPool<BungeeCommand> pool = bungeeCommands.get(key);
		if (pool == null) {
			return false;
		}
		
		boolean modified = false;
		for (BungeeCommand c : pool) {
			if (c.getClass().equals(clazz)) {
				pool.remove(c);
				modified = true;
			}
		}
		return modified;
	}
	public boolean hasCommand(String command) {
		return command != null && (bungeeCommands.containsKey(command.toLowerCase()) || commandAliases.containsKey(command.toLowerCase()));
	}
	public void clear() {
		bungeeCommands.forEach((k, v) -> {
			for (BungeeCommand c : v) {
				manager.unregisterCommand(c);
			}
		});
		
		bungeeCommands.clear();
		commandAliases.clear();
	}
	
	/**
	 * Searches a package for any classes that extend PluginCommand and adds them to the handler.
	 * 
	 * @param packageName The package to search.
	 * @param classCommandMap A map with each key pointing to the fully-qualified package + class name and each value pointing to the command name. These are both case-insensitive.
	 * @return The number of successfully-added commands.
	 */
	public int addCommandsFromPackage(String packageName, Map<String, String> classCommandMap) {
		return addCommandsFromPackage(packageName, classCommandMap, true);
	}
	/**
	 * Searches a package for any classes that extend PluginCommand and adds them to the handler.
	 * 
	 * @param packageName The package to search.
	 * @param classCommandMap A map with each key pointing to the fully-qualified package + class name and each value pointing to the command name. These are both case-insensitive.
	 * @param recursive Whether or not to recursively search the package.
	 * @return The number of successfully-added commands.
	 */
	public int addCommandsFromPackage(String packageName, Map<String, String> classCommandMap, boolean recursive) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		if (classCommandMap == null) {
			throw new ArgumentNullException("classCommandMap");
		}
		
		HashMap<String, String> tmp = new HashMap<String, String>();
		for (Entry<String, String> kvp : classCommandMap.entrySet()) {
			tmp.put(kvp.getKey().toLowerCase(), kvp.getValue().toLowerCase());
		}
		classCommandMap = tmp;
		
		int numCommands = 0;
		
		List<Class<PluginCommand>> enums = ReflectUtil.getClasses(PluginCommand.class, packageName, recursive, false, false);
		for (Class<PluginCommand> c : enums) {
			String name = c.getName().toLowerCase();
			String command = classCommandMap.remove(name);
			
			if (command == null) {
				throw new IllegalStateException("\"" + name + "\" not found in command map!");
			}
			
			if (addCommandHandler(command, c)) {
				numCommands++;
			}
		}
		if (!classCommandMap.isEmpty()) {
			throw new IllegalStateException("Command map contains unused values! " + Arrays.toString(classCommandMap.keySet().toArray()));
		}
		
		return numCommands;
	}
	
	public void runCommand(CommandSender sender, String command, String[] args) {
		IObjectPool<BungeeCommand> run = getCommands(sender, command, args);
		
		if (run == null || run.size() == 0) {
			return;
		}
		
		for (BungeeCommand c : run) {
			c.execute(sender, args);
		}
	}
	public void undoInitializedCommands(CommandSender sender, String[] args) {
		bungeeCommands.forEach((k, run) -> {
			for (BungeeCommand c : run) {
				c.undo(sender, args);
			}
		});
	}
	public Iterable<String> tabComplete(CommandSender sender, String command, String[] args) {
		IObjectPool<BungeeCommand> run = getCommands(sender, command, args);
		
		if (run == null || run.size() == 0) {
			return null;
		}
		
		BungeeCommand peek = run.peekFirst();
		if (peek != null && run.size() == 1) {
			return peek.onTabComplete(sender, args);
		}
		
		ArrayList<String> retVal = new ArrayList<String>();
		
		for (BungeeCommand c : run) {
			Iterable<String> complete = c.onTabComplete(sender, args);
			if (complete != null) {
				Iterables.addAll(retVal, complete);
			}
		}
		
		return retVal;
	}
	
	//private
	private IObjectPool<BungeeCommand> getCommands(CommandSender sender, String command, String[] args) {
		String key = command.toLowerCase();
		
		if (commandAliases.containsKey(key)) {
			key = commandAliases.get(key);
		}
		
		return bungeeCommands.get(key);
	}
}