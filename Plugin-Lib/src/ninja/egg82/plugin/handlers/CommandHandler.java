package ninja.egg82.plugin.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.PluginCommand;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public final class CommandHandler {
	//vars
	private ConcurrentHashMap<String, String> commandAliases = new ConcurrentHashMap<String, String>();
	private ConcurrentHashMap<String, IObjectPool<Class<PluginCommand>>> commands = new ConcurrentHashMap<String, IObjectPool<Class<PluginCommand>>>();
	private ConcurrentHashMap<String, IObjectPool<PluginCommand>> initializedCommands = new ConcurrentHashMap<String, IObjectPool<PluginCommand>>();
	
	//constructor
	public CommandHandler() {
		
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
		
		IObjectPool<Class<PluginCommand>> pool = commands.get(key);
		if (pool == null) {
			pool = new DynamicObjectPool<Class<PluginCommand>>();
		}
		pool = CollectionUtil.putIfAbsent(commands, key, pool);
		if (!pool.contains(clazz)) {
			pool.add(clazz);
			initializedCommands.remove(key);
			return true;
		} else {
			return false;
		}
	}
	public boolean removeCommandHandler(Class<PluginCommand> clazz) {
		boolean modified = false;
		for (Entry<String, IObjectPool<Class<PluginCommand>>> kvp : commands.entrySet()) {
			if (kvp.getValue().remove(clazz)) {
				initializedCommands.remove(kvp.getKey());
				modified = true;
			}
		}
		return modified;
	}
	public boolean removeCommandHandler(String command, Class<PluginCommand> clazz) {
		String key = command.toLowerCase();
		
		IObjectPool<Class<PluginCommand>> pool = commands.get(key);
		if (pool == null) {
			return false;
		}
		
		if (!pool.remove(clazz)) {
			return false;
		} else {
			initializedCommands.remove(key);
			return true;
		}
	}
	public boolean hasCommand(String command) {
		return command != null && (commands.containsKey(command.toLowerCase()) || commandAliases.containsKey(command.toLowerCase()));
	}
	public void clear() {
		initializedCommands.clear();
		commands.clear();
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
	
	public void runCommand(CommandSender sender, Command command, String label, String[] args) {
		IObjectPool<PluginCommand> run = getCommands(sender, command, label, args);
		
		if (run == null || run.size() == 0) {
			return;
		}
		
		Exception lastEx = null;
		for (PluginCommand c : run) {
			try {
				c.start();
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
				lastEx = ex;
			}
		}
		if (lastEx != null) {
			throw new RuntimeException("Cannot run command.", lastEx);
		}
	}
	public void undoInitializedCommands(CommandSender sender, String[] args) {
		Exception lastEx = null;
		for (Entry<String, IObjectPool<PluginCommand>> kvp : initializedCommands.entrySet()) {
			for (PluginCommand c : kvp.getValue()) {
				c.setSender(sender);
				c.setCommand(null);
				c.setCommandName(null);
				c.setLabel(null);
				c.setArgs(args);
				
				try {
					c.undo();
				} catch (Exception ex) {
					ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
					lastEx = ex;
				}
			}
		}
		if (lastEx != null) {
			throw new RuntimeException("Cannot undo command.", lastEx);
		}
	}
	public List<String> tabComplete(CommandSender sender, Command command, String label, String[] args) {
		IObjectPool<PluginCommand> run = getCommands(sender, command, label, args);
		
		if (run == null || run.size() == 0) {
			return null;
		}
		
		PluginCommand peek = run.peekFirst();
		if (peek != null && run.size() == 1) {
			return peek.tabComplete();
		}
		
		ArrayList<String> retVal = new ArrayList<String>();
		
		for (PluginCommand c : run) {
			List<String> complete = null;
			try {
				complete = c.tabComplete();
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
				throw ex;
			}
			if (complete != null) {
				retVal.addAll(complete);
			}
		}
		
		return retVal;
	}
	
	//private
	private IObjectPool<PluginCommand> getCommands(CommandSender sender, Command command, String label, String[] args) {
		IObjectPool<PluginCommand> run = getCommands(command.getName());
		
		for (PluginCommand cmd : run) {
			cmd.setSender(sender);
			cmd.setCommand(command);
			cmd.setCommandName(command.getName());
			cmd.setLabel(label);
			cmd.setArgs(args);
		}
		
		return run;
	}
	private IObjectPool<PluginCommand> getCommands(String key) {
		key = key.toLowerCase();
		
		if (commandAliases.containsKey(key)) {
			key = commandAliases.get(key);
		}
		
		IObjectPool<PluginCommand> run = initializedCommands.get(key);
		IObjectPool<Class<PluginCommand>> c = commands.get(key);
		
		// run might be null, but c will never be as long as the command actually exists
		if (c == null) {
			return null;
		}
		
		// Lazy initialize. No need to create a command until it's actually going to be used
		if (run == null) {
			// Create a new command and store it
			run = new DynamicObjectPool<PluginCommand>();
			
			for (Class<PluginCommand> e : c) {
				try {
					run.add(e.newInstance());
				} catch (Exception ex) {
					ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
					throw new RuntimeException("Cannot initialize command.", ex);
				}
			}
			
			run = CollectionUtil.putIfAbsent(initializedCommands, key, run);
		}
		
		return run;
	}
}