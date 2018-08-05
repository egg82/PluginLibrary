package ninja.egg82.bukkit.processors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import ninja.egg82.analytics.exceptions.IExceptionHandler;
import ninja.egg82.concurrent.DynamicConcurrentDeque;
import ninja.egg82.concurrent.IConcurrentDeque;
import ninja.egg82.core.CollectionUtil;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.core.sender.Sender;
import ninja.egg82.plugin.handlers.CommandHandler;
import ninja.egg82.utils.ReflectUtil;

public final class CommandProcessor {
	//vars
	private ConcurrentHashMap<String, String> commandAliases = new ConcurrentHashMap<String, String>();
	private ConcurrentHashMap<String, IConcurrentDeque<Class<? extends CommandHandler>>> handlers = new ConcurrentHashMap<String, IConcurrentDeque<Class<? extends CommandHandler>>>();
	private ConcurrentHashMap<String, IConcurrentDeque<CommandHandler>> initializedHandlers = new ConcurrentHashMap<String, IConcurrentDeque<CommandHandler>>();
	
	//constructor
	public CommandProcessor() {
		
	}
	
	//public
	public void addAliases(String command, String... aliases) {
		if (command == null) {
			throw new IllegalArgumentException("command cannot be null.");
		}
		if (aliases == null || aliases.length == 0) {
			return;
		}
		
		for (String alias : aliases) {
			if (alias != null) {
				commandAliases.put(alias, command);
			}
		}
	}
	public void removeAliases(String... aliases) {
		if (aliases == null || aliases.length == 0) {
			return;
		}
		
		for (String alias : aliases) {
			if (alias != null) {
				commandAliases.remove(alias);
			}
		}
	}
	
	public boolean addHandler(String command, Class<? extends CommandHandler> clazz) {
		if (command == null) {
			throw new IllegalArgumentException("command cannot be null.");
		}
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null.");
		}
		
		String key = command.toLowerCase();
		
		IConcurrentDeque<Class<? extends CommandHandler>> pool = handlers.get(key);
		if (pool == null) {
			pool = new DynamicConcurrentDeque<Class<? extends CommandHandler>>();
		}
		pool = CollectionUtil.putIfAbsent(handlers, key, pool);
		
		if (pool.contains(clazz)) {
			return false;
		}
		
		initializedHandlers.remove(key);
		return pool.add(clazz);
	}
	public boolean removeHandler(Class<? extends CommandHandler> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null.");
		}
		
		boolean modified = false;
		for (Entry<String, IConcurrentDeque<Class<? extends CommandHandler>>> kvp : handlers.entrySet()) {
			if (kvp.getValue().remove(clazz)) {
				initializedHandlers.remove(kvp.getKey());
				modified = true;
			}
		}
		return modified;
	}
	public boolean removeHandler(String command, Class<? extends CommandHandler> clazz) {
		if (command == null) {
			throw new IllegalArgumentException("command cannot be null.");
		}
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null.");
		}
		
		String key = command.toLowerCase();
		
		IConcurrentDeque<Class<? extends CommandHandler>> pool = handlers.get(key);
		if (pool == null) {
			return false;
		}
		
		if (!pool.remove(clazz)) {
			return false;
		}
		
		initializedHandlers.remove(key);
		return true;
	}
	public boolean hasCommand(String command) {
		return (command != null && (handlers.containsKey(command.toLowerCase()) || commandAliases.containsKey(command.toLowerCase()))) ? true : false;
	}
	public void clear() {
		initializedHandlers.clear();
		handlers.clear();
		commandAliases.clear();
	}
	
	/**
	 * Searches a package for any classes that extend PluginCommand and adds them to the handler.
	 * 
	 * @param packageName The package to search.
	 * @param classCommandMap A map with each key pointing to the fully-qualified package + class name and each value pointing to the command name. These are both case-insensitive.
	 * @return The number of successfully-added commands.
	 */
	public int addHandlersFromPackage(String packageName, Map<String, String> classCommandMap) {
		return addHandlersFromPackage(packageName, classCommandMap, true);
	}
	/**
	 * Searches a package for any classes that extend PluginCommand and adds them to the handler.
	 * 
	 * @param packageName The package to search.
	 * @param classCommandMap A map with each key pointing to the fully-qualified package + class name and each value pointing to the command name. These are both case-insensitive.
	 * @param recursive Whether or not to recursively search the package.
	 * @return The number of successfully-added commands.
	 */
	public int addHandlersFromPackage(String packageName, Map<String, String> classCommandMap, boolean recursive) {
		if (packageName == null) {
			throw new IllegalArgumentException("packageName cannot be null.");
		}
		if (classCommandMap == null) {
			throw new IllegalArgumentException("classCommandMap cannot be null.");
		}
		
		HashMap<String, String> tmp = new HashMap<String, String>();
		for (Entry<String, String> kvp : classCommandMap.entrySet()) {
			tmp.put(kvp.getKey().toLowerCase(), kvp.getValue().toLowerCase());
		}
		classCommandMap = tmp;
		
		int numCommands = 0;
		
		List<Class<CommandHandler>> enums = ReflectUtil.getClasses(CommandHandler.class, packageName, recursive, false, false);
		for (Class<CommandHandler> c : enums) {
			String name = c.getName().toLowerCase();
			String command = classCommandMap.remove(name);
			
			if (command == null) {
				throw new IllegalStateException("\"" + name + "\" not found in command map!");
			}
			
			if (addHandler(command, c)) {
				numCommands++;
			}
		}
		if (!classCommandMap.isEmpty()) {
			throw new IllegalStateException("Command map contains unused values! " + Arrays.toString(classCommandMap.keySet().toArray()));
		}
		
		return numCommands;
	}
	
	public void runHandlers(Sender sender, String commandName, String[] args) {
		IConcurrentDeque<CommandHandler> run = getHandlers(sender, commandName, args);
		
		if (run == null || run.size() == 0) {
			return;
		}
		
		Exception lastEx = null;
		for (CommandHandler c : run) {
			try {
				c.start();
			} catch (Exception ex) {
				IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
				if (handler != null) {
					handler.sendException(ex);
				}
				lastEx = ex;
			}
		}
		if (lastEx != null) {
			throw new RuntimeException("Cannot run command.", lastEx);
		}
	}
	public void undoInitializedHandlers(Sender sender, String[] args) {
		Exception lastEx = null;
		for (Entry<String, IConcurrentDeque<CommandHandler>> kvp : initializedHandlers.entrySet()) {
			for (CommandHandler c : kvp.getValue()) {
				c.setSender(sender);
				c.setCommandName(null);
				c.setArgs(args);
				
				try {
					c.undo();
				} catch (Exception ex) {
					IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
					if (handler != null) {
						handler.sendException(ex);
					}
					lastEx = ex;
				}
			}
		}
		if (lastEx != null) {
			throw new RuntimeException("Cannot undo command.", lastEx);
		}
	}
	public Collection<String> tabComplete(Sender sender, String commandName, String[] args) {
		IConcurrentDeque<CommandHandler> run = getHandlers(sender, commandName, args);
		
		if (run == null || run.size() == 0) {
			return null;
		}
		
		CommandHandler peek = run.peekFirst();
		if (peek != null && run.size() == 1) {
			return peek.tabComplete();
		}
		
		ArrayList<String> retVal = new ArrayList<String>();
		
		for (CommandHandler c : run) {
			Collection<String> complete = null;
			try {
				complete = c.tabComplete();
			} catch (Exception ex) {
				IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
				if (handler != null) {
					handler.sendException(ex);
				}
				throw ex;
			}
			if (complete != null) {
				retVal.addAll(complete);
			}
		}
		
		return retVal;
	}
	
	//private
	private IConcurrentDeque<CommandHandler> getHandlers(Sender sender, String commandName, String[] args) {
		IConcurrentDeque<CommandHandler> run = getHandlers(commandName);
		
		for (CommandHandler cmd : run) {
			cmd.setSender(sender);
			cmd.setCommandName(commandName);
			cmd.setArgs(args);
		}
		
		return run;
	}
	private IConcurrentDeque<CommandHandler> getHandlers(String key) {
		key = key.toLowerCase();
		
		if (commandAliases.containsKey(key)) {
			key = commandAliases.get(key);
		}
		
		IConcurrentDeque<CommandHandler> run = initializedHandlers.get(key);
		IConcurrentDeque<Class<? extends CommandHandler>> c = handlers.get(key);
		
		// run might be null, but c will never be as long as the command actually exists
		if (c == null) {
			return null;
		}
		
		// Lazy initialize. No need to create a command until it's actually going to be used
		if (run == null) {
			// Create a new command and store it
			run = new DynamicConcurrentDeque<CommandHandler>();
			
			for (Class<? extends CommandHandler> e : c) {
				try {
					run.add(e.newInstance());
				} catch (Exception ex) {
					IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
					if (handler != null) {
						handler.sendException(ex);
					}
					throw new RuntimeException("Cannot initialize command.", ex);
				}
			}
			
			run = CollectionUtil.putIfAbsent(initializedHandlers, key, run);
		}
		
		return run;
	}
}