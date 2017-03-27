package ninja.egg82.plugin.handlers;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;

import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.TickCommand;
import ninja.egg82.startup.InitRegistry;

public final class TickHandler {
	//vars
	private ObjectIntHashMap<Class<? extends TickCommand>> tasks = new ObjectIntHashMap<Class<? extends TickCommand>>();
	
	private JavaPlugin plugin = (JavaPlugin) ((IRegistry) ServiceLocator.getService(InitRegistry.class)).getRegister("plugin");
	private BukkitScheduler scheduler = (BukkitScheduler) ((IRegistry) ServiceLocator.getService(InitRegistry.class)).getRegister("plugin.scheduler");
	
	//constructor
	public TickHandler() {
		
	}
	
	//public
	public synchronized void addTickCommand(Class<? extends TickCommand> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null.");
		}
		
		if (tasks.containsKey(clazz)) {
			return;
		}
		
		TickCommand c = getCommand(clazz);
		if (c == null) {
			return;
		}
		
		long ticks = c.getTicks();
		if (ticks < 1L) {
			ticks = 1L;
		}
		
		int taskId = scheduler.scheduleSyncRepeatingTask(plugin, new TickRunner(c), ticks, ticks);
		if (taskId > -1) {
			tasks.put(clazz, taskId);
		}
	}
	@SuppressWarnings("deprecation")
	public synchronized void addAsyncTickCommand(Class<? extends TickCommand> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null.");
		}
		
		if (tasks.containsKey(clazz)) {
			return;
		}
		
		TickCommand c = getCommand(clazz);
		if (c == null) {
			return;
		}
		
		long ticks = c.getTicks();
		if (ticks < 1L) {
			ticks = 1L;
		}
		
		// Not deprecated. @Deprecated was used as a warning.
		int taskId = scheduler.scheduleAsyncRepeatingTask(plugin, new TickRunner(c), ticks, ticks);
		if (taskId > -1) {
			tasks.put(clazz, taskId);
		}
	}
	public synchronized void addDelayedTickCommand(Class<? extends TickCommand> clazz, long delay) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null.");
		}
		
		if (tasks.containsKey(clazz)) {
			return;
		}
		
		TickCommand c = getCommand(clazz);
		if (c == null) {
			return;
		}
		
		if (delay < 1L) {
			delay = 1L;
		}
		
		int taskId = scheduler.scheduleSyncDelayedTask(plugin, new DelayedTickRunner(c, v -> tasks.remove(v)), delay);
		if (taskId > -1) {
			tasks.put(clazz, taskId);
		}
	}
	@SuppressWarnings("deprecation")
	public synchronized void addAsyncDelayedTickCommand(Class<? extends TickCommand> clazz, long delay) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null.");
		}
		
		if (tasks.containsKey(clazz)) {
			return;
		}
		
		TickCommand c = getCommand(clazz);
		if (c == null) {
			return;
		}
		
		if (delay < 1L) {
			delay = 1L;
		}
		
		// Not deprecated. @Deprecated was used as a warning.
		int taskId = scheduler.scheduleAsyncDelayedTask(plugin, new DelayedTickRunner(c, v -> tasks.remove(v)), delay);
		if (taskId > -1) {
			tasks.put(clazz, taskId);
		}
	}
	public synchronized void removeTickCommand(Class<? extends TickCommand> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null.");
		}
		
		int taskId = tasks.get(clazz);
		tasks.remove(clazz);
		if (taskId > -1) {
			scheduler.cancelTask(taskId);
		}
	}
	public synchronized boolean hasTickCommand(Class<? extends TickCommand> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null.");
		}
		return tasks.containsKey(clazz);
	}
	public synchronized void clear() {
		tasks.forEachKeyValue((k, v) -> {
			scheduler.cancelTask(v);
		});
		tasks.clear();
	}
	
	//private
	private TickCommand getCommand(Class<? extends TickCommand> clazz) {
		TickCommand run = null;
		try {
			run = clazz.newInstance();
		} catch (Exception ex) {
			return null;
		}
		return run;
	}
}