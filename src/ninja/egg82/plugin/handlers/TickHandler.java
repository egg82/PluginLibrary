package ninja.egg82.plugin.handlers;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.TickCommand;
import ninja.egg82.plugin.enums.SpigotInitType;
import ninja.egg82.startup.InitRegistry;

public final class TickHandler {
	//vars
	private HashMap<Class<? extends TickCommand>, Integer> tasks = new HashMap<Class<? extends TickCommand>, Integer>();
	
	private JavaPlugin plugin = ServiceLocator.getService(InitRegistry.class).getRegister(SpigotInitType.PLUGIN, JavaPlugin.class);
	private BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	
	//constructor
	public TickHandler() {
		
	}
	
	//public
	public synchronized void addTickCommand(Class<? extends TickCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
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
			throw new ArgumentNullException("clazz");
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
			throw new ArgumentNullException("clazz");
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
			throw new ArgumentNullException("clazz");
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
			throw new ArgumentNullException("clazz");
		}
		
		int taskId = tasks.get(clazz);
		tasks.remove(clazz);
		if (taskId > -1) {
			scheduler.cancelTask(taskId);
		}
	}
	public synchronized boolean hasTickCommand(Class<? extends TickCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		return tasks.containsKey(clazz);
	}
	public synchronized void clear() {
		tasks.forEach((k, v) -> {
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