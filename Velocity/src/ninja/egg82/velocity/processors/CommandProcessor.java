package ninja.egg82.velocity.processors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Iterables;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;

import ninja.egg82.analytics.exceptions.IExceptionHandler;
import ninja.egg82.concurrent.DynamicConcurrentDeque;
import ninja.egg82.concurrent.IConcurrentDeque;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.handlers.async.AsyncCommandHandler;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;
import ninja.egg82.velocity.BasePlugin;
import ninja.egg82.velocity.core.VelocityCommand;

public final class CommandProcessor {
    // vars
    private BasePlugin plugin = ServiceLocator.getService(BasePlugin.class);
    private CommandManager manager = null;

    private ConcurrentHashMap<String, String> commandAliases = new ConcurrentHashMap<String, String>();
    private ConcurrentHashMap<String, IConcurrentDeque<VelocityCommand>> velocityCommands = new ConcurrentHashMap<String, IConcurrentDeque<VelocityCommand>>();

    // constructor
    public CommandProcessor() {
        manager = plugin.getProxy().getCommandManager();
    }

    // public
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

    public boolean addHandler(String command, Class<? extends AsyncCommandHandler> clazz) {
        if (command == null) {
            throw new IllegalArgumentException("command cannot be null.");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }

        String key = command.toLowerCase();

        IConcurrentDeque<VelocityCommand> pool = velocityCommands.get(key);
        if (pool == null) {
            pool = new DynamicConcurrentDeque<VelocityCommand>();
        }
        pool = CollectionUtil.putIfAbsent(velocityCommands, key, pool);

        for (VelocityCommand c : pool) {
            if (c.getCommand().equals(clazz)) {
                return false;
            }
        }

        VelocityCommand c = new VelocityCommand(clazz);
        manager.register(c, key);
        return pool.add(c);
    }
    public boolean removeHandler(Class<? extends AsyncCommandHandler> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }

        boolean modified = false;
        for (Entry<String, IConcurrentDeque<VelocityCommand>> kvp : velocityCommands.entrySet()) {
            for (VelocityCommand c : kvp.getValue()) {
                if (c.getCommand().equals(clazz)) {
                    kvp.getValue().remove(c);
                    modified = true;
                }
            }
        }
        return modified;
    }
    public boolean removeHandler(String command, Class<? extends AsyncCommandHandler> clazz) {
        if (command == null) {
            throw new IllegalArgumentException("command cannot be null.");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }

        String key = command.toLowerCase();

        IConcurrentDeque<VelocityCommand> pool = velocityCommands.get(key);
        if (pool == null) {
            return false;
        }

        boolean modified = false;
        for (VelocityCommand c : pool) {
            if (c.getClass().equals(clazz)) {
                pool.remove(c);
                modified = true;
            }
        }
        return modified;
    }
    public boolean hasCommand(String command) {
        return (command != null && (velocityCommands.containsKey(command.toLowerCase()) || commandAliases.containsKey(command.toLowerCase()))) ? true : false;
    }

    public void clear() {
        for (Entry<String, IConcurrentDeque<VelocityCommand>> kvp : velocityCommands.entrySet()) {
            manager.unregister(kvp.getKey());
        }

        velocityCommands.clear();
        commandAliases.clear();
    }

    /**
     * Searches a package for any classes that extend PluginCommand and adds them to
     * the handler.
     * 
     * @param packageName     The package to search.
     * @param classCommandMap A map with each key pointing to the fully-qualified
     *                        package + class name and each value pointing to the
     *                        command name. These are both case-insensitive.
     * @return The number of successfully-added commands.
     */
    public int addHandlersFromPackage(String packageName, Map<String, String> classCommandMap) {
        return addHandlersFromPackage(packageName, classCommandMap, true);
    }
    /**
     * Searches a package for any classes that extend PluginCommand and adds them to
     * the handler.
     * 
     * @param packageName     The package to search.
     * @param classCommandMap A map with each key pointing to the fully-qualified
     *                        package + class name and each value pointing to the
     *                        command name. These are both case-insensitive.
     * @param recursive       Whether or not to recursively search the package.
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

        List<Class<AsyncCommandHandler>> enums = ReflectUtil.getClasses(AsyncCommandHandler.class, packageName, recursive, false, false);
        for (Class<AsyncCommandHandler> c : enums) {
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

    public void runHandlers(CommandSource sender, String command, String[] args) {
        IConcurrentDeque<VelocityCommand> run = getHandlers(command);

        if (run == null || run.size() == 0) {
            return;
        }

        Exception lastEx = null;
        for (VelocityCommand c : run) {
            try {
                c.execute(sender, args);
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
    public void undoInitializedCommands(CommandSource sender, String[] args) {
        Exception lastEx = null;
        for (Entry<String, IConcurrentDeque<VelocityCommand>> kvp : velocityCommands.entrySet()) {
            for (VelocityCommand c : kvp.getValue()) {
                try {
                    c.undo(sender, args);
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
    public Iterable<String> tabComplete(CommandSource sender, String command, String[] args) {
        IConcurrentDeque<VelocityCommand> run = getHandlers(command);

        if (run == null || run.size() == 0) {
            return null;
        }

        VelocityCommand peek = run.peekFirst();
        if (peek != null && run.size() == 1) {
            return peek.suggest(sender, args);
        }

        ArrayList<String> retVal = new ArrayList<String>();

        for (VelocityCommand c : run) {
            Iterable<String> complete = null;
            try {
                complete = c.suggest(sender, args);
            } catch (Exception ex) {
                IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                if (handler != null) {
                    handler.sendException(ex);
                }
                throw ex;
            }
            if (complete != null) {
                Iterables.addAll(retVal, complete);
            }
        }

        return retVal;
    }

    // private
    private IConcurrentDeque<VelocityCommand> getHandlers(String command) {
        String key = command.toLowerCase();

        if (commandAliases.containsKey(key)) {
            key = commandAliases.get(key);
        }

        return velocityCommands.get(key);
    }
}
