package ninja.egg82.plugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.enums.SpigotInitType;
import ninja.egg82.startup.InitRegistry;

public class TaskUtil {
	//vars
	
	private static JavaPlugin plugin = null;
	
	//constructor
	public TaskUtil() {
		
	}
	
	//public
	public static int runSync(Runnable task) {
		return runSync(task, 0L);
	}
	public static int runSync(Runnable task, long delay) {
		if (task == null) {
			throw new ArgumentNullException("task");
		}
		
		if (plugin == null) {
			plugin = ServiceLocator.getService(InitRegistry.class).getRegister(SpigotInitType.PLUGIN, JavaPlugin.class);
		}
		
		if (delay <= 0L) {
			return Bukkit.getServer().getScheduler().runTask(plugin, new Runnable() {
				public void run() {
					IExceptionHandler exceptionHandler = ServiceLocator.getService(IExceptionHandler.class);
					try {
						task.run();
					} catch (Exception ex) {
						exceptionHandler.silentException(ex);
						throw ex;
					}
				}}).getTaskId();
		} else {
			return Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					IExceptionHandler exceptionHandler = ServiceLocator.getService(IExceptionHandler.class);
					try {
						task.run();
					} catch (Exception ex) {
						exceptionHandler.silentException(ex);
						throw ex;
					}
				}}, delay);
		}
	}
	public static int runAsync(Runnable task) {
		return runAsync(task, 0L);
	}
	@SuppressWarnings("deprecation")
	public static int runAsync(Runnable task, long delay) {
		if (task == null) {
			throw new ArgumentNullException("task");
		}
		
		if (plugin == null) {
			plugin = ServiceLocator.getService(InitRegistry.class).getRegister(SpigotInitType.PLUGIN, JavaPlugin.class);
		}
		
		if (delay <= 0L) {
			return Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					IExceptionHandler exceptionHandler = ServiceLocator.getService(IExceptionHandler.class);
					try {
						task.run();
					} catch (Exception ex) {
						exceptionHandler.silentException(ex);
						throw ex;
					}
				}}).getTaskId();
		} else {
			return Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					IExceptionHandler exceptionHandler = ServiceLocator.getService(IExceptionHandler.class);
					try {
						task.run();
					} catch (Exception ex) {
						exceptionHandler.silentException(ex);
						throw ex;
					}
				}}, delay);
		}
	}
	
	public static void cancelTask(int id) {
		 Bukkit.getServer().getScheduler().cancelTask(id);
	}
	
	//private
	
}
