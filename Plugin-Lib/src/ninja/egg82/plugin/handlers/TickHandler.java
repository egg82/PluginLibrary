package ninja.egg82.plugin.handlers;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.AsyncTickCommand;
import ninja.egg82.plugin.commands.TickCommand;
import ninja.egg82.primitive.ints.Object2IntArrayMap;
import ninja.egg82.primitive.ints.Object2IntMap;
import ninja.egg82.utils.ReflectUtil;

public final class TickHandler {
	//vars
	private Object2IntMap<Class<? extends TickCommand>> tasks = new Object2IntArrayMap<Class<? extends TickCommand>>();
	private Object2IntMap<Class<? extends AsyncTickCommand>> asyncTasks = new Object2IntArrayMap<Class<? extends AsyncTickCommand>>();
	
	private JavaPlugin plugin = ServiceLocator.getService(JavaPlugin.class);
	private BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	
	//constructor
	public TickHandler() {
		
	}
	
	//public
	public int addTickCommand(Class<? extends TickCommand> clazz) {
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
		
		int taskId = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				try {
					c.start();
				} catch (Exception ex) {
					ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
					throw ex;
				}
			}
		}, ticks, ticks);
		
		if (taskId > -1) {
			int id = putIfAbsent(tasks, clazz, taskId);
			if (id != taskId) {
				scheduler.cancelTask(taskId);
				taskId = id;
			}
		}
		return taskId;
	}
	@SuppressWarnings("deprecation")
	public int addAsyncTickCommand(Class<? extends AsyncTickCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		if (tasks.containsKey(clazz)) {
			return -1;
		}
		
		AsyncTickCommand c = getAsyncCommand(clazz);
		if (c == null) {
			return -1;
		}
		
		long ticks = c.getTicks();
		if (ticks < 1L) {
			ticks = 1L;
		}
		
		// Not deprecated. @Deprecated was used as a warning.
		int taskId = scheduler.scheduleAsyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				try {
					c.start();
				} catch (Exception ex) {
					ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
					throw ex;
				}
			}
		}, ticks, ticks);
		
		if (taskId > -1) {
			int id = putIfAbsent(asyncTasks, clazz, taskId);
			if (id != taskId) {
				scheduler.cancelTask(taskId);
				taskId = id;
			}
		}
		return taskId;
	}
	public int removeTickCommand(Class<? extends TickCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		int taskId = tasks.removeInt(clazz);
		if (taskId != tasks.defaultReturnValue() && taskId > -1) {
			scheduler.cancelTask(taskId);
		}
		return taskId;
	}
	public int removeAsyncTickCommand(Class<? extends AsyncTickCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		int taskId = asyncTasks.removeInt(clazz);
		if (taskId != tasks.defaultReturnValue() && taskId > -1) {
			scheduler.cancelTask(taskId);
		}
		return taskId;
	}
	public boolean hasTickCommand(Class<? extends TickCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		return tasks.containsKey(clazz);
	}
	public boolean hasAsyncTickCommand(Class<? extends AsyncTickCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		return asyncTasks.containsKey(clazz);
	}
	public int getTickCommand(Class<? extends TickCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		int id = tasks.getInt(clazz);
		return (id == tasks.defaultReturnValue()) ? -1 : id;
	}
	public int getAsyncTickCommand(Class<? extends AsyncTickCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		int id = asyncTasks.getInt(clazz);
		return (id == tasks.defaultReturnValue()) ? -1 : id;
	}
	public void clear() {
		for (Object2IntMap.Entry<Class<? extends TickCommand>> kvp : tasks.object2IntEntrySet()) {
			if (kvp.getIntValue() > -1) {
				scheduler.cancelTask(kvp.getIntValue());
			}
		}
		tasks.clear();
		for (Object2IntMap.Entry<Class<? extends AsyncTickCommand>> kvp : asyncTasks.object2IntEntrySet()) {
			if (kvp.getIntValue() > -1) {
				scheduler.cancelTask(kvp.getIntValue());
			}
		}
		asyncTasks.clear();
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
		
		List<Class<AsyncTickCommand>> enums2 = ReflectUtil.getClasses(AsyncTickCommand.class, packageName, recursive, false, false);
		for (Class<AsyncTickCommand> t : enums2) {
			if (addAsyncTickCommand(t) > -1) {
				numTicks++;
			}
		}
		
		return numTicks;
	}
	
	//private
	private TickCommand getCommand(Class<? extends TickCommand> clazz) {
		TickCommand run = null;
		try {
			run = clazz.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Cannot initialize tick command.", ex);
		}
		return run;
	}
	private AsyncTickCommand getAsyncCommand(Class<? extends AsyncTickCommand> clazz) {
		AsyncTickCommand run = null;
		try {
			run = clazz.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Cannot initialize tick command.", ex);
		}
		return run;
	}
	
	private <K> int putIfAbsent(Object2IntMap<K> map, K key, int newValue) {
		// Avoiding putIfAbsent due to versioning clashes
		final int oldValue = map.getInt(key);
		if (oldValue == map.defaultReturnValue()) {
			map.put(key, newValue);
		}
		return (oldValue != map.defaultReturnValue()) ? oldValue : newValue;
	}
}