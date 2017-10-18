package ninja.egg82.plugin.handlers;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.TickCommand;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public final class TickHandler {
	//vars
	private ConcurrentHashMap<Class<TickCommand>, Integer> tasks = new ConcurrentHashMap<Class<TickCommand>, Integer>();
	
	private JavaPlugin plugin = ServiceLocator.getService(JavaPlugin.class);
	private BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	
	//constructor
	public TickHandler() {
		
	}
	
	//public
	public int addTickCommand(Class<TickCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		if (tasks.containsKey(clazz)) {
			return -1;
		}
		
		TickCommand c = getCommand(clazz);
		if (c == null) {
			return -1;
		}
		
		long ticks = c.getTicks();
		if (ticks < 1L) {
			ticks = 1L;
		}
		
		int taskId = scheduler.scheduleSyncRepeatingTask(plugin, new TickRunner(c), ticks, ticks);
		if (taskId > -1) {
			int id = CollectionUtil.putIfAbsent(tasks, clazz, taskId);
			if (id != taskId) {
				scheduler.cancelTask(taskId);
				taskId = id;
			}
		}
		return taskId;
	}
	@SuppressWarnings("deprecation")
	public int addAsyncTickCommand(Class<TickCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		if (tasks.containsKey(clazz)) {
			return -1;
		}
		
		TickCommand c = getCommand(clazz);
		if (c == null) {
			return -1;
		}
		
		long ticks = c.getTicks();
		if (ticks < 1L) {
			ticks = 1L;
		}
		
		// Not deprecated. @Deprecated was used as a warning.
		int taskId = scheduler.scheduleAsyncRepeatingTask(plugin, new TickRunner(c), ticks, ticks);
		if (taskId > -1) {
			int id = tasks.put(clazz, taskId);
			if (id != taskId) {
				scheduler.cancelTask(taskId);
				taskId = id;
			}
		}
		return taskId;
	}
	public int removeTickCommand(Class<TickCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		int taskId = tasks.get(clazz);
		tasks.remove(clazz);
		if (taskId > -1) {
			scheduler.cancelTask(taskId);
		}
		return taskId;
	}
	public boolean hasTickCommand(Class<TickCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		return tasks.containsKey(clazz);
	}
	public int getTickCommand(Class<TickCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		Integer id = tasks.get(clazz);
		return (id == null) ? -1 : id;
	}
	public void clear() {
		tasks.forEach((k, v) -> {
			scheduler.cancelTask(v);
		});
		tasks.clear();
	}
	
	public int addTicksFromPackage(String packageName) {
		return addTicksFromPackage(packageName, true);
	}
	public int addTicksFromPackage(String packageName, boolean recursive) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		int numTicks = 0;
		
		List<Class<TickCommand>> enums = ReflectUtil.getClasses(TickCommand.class, packageName, recursive, false, false);
		for (Class<TickCommand> t : enums) {
			if (addTickCommand(t) > -1) {
				numTicks++;
			}
		}
		
		return numTicks;
	}
	
	//private
	private TickCommand getCommand(Class<TickCommand> clazz) {
		TickCommand run = null;
		try {
			run = clazz.newInstance();
		} catch (Exception ex) {
			return null;
		}
		return run;
	}
}