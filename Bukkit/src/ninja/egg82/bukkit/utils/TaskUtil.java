package ninja.egg82.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;

public class TaskUtil {
	//vars
	
	//constructor
	public TaskUtil() {
		
	}
	
	//public
	public static int runSync(Runnable task) {
		return runSync(task, 0L);
	}
	public static int runSync(Runnable task, long delay) {
		if (task == null) {
			throw new IllegalArgumentException("task cannot be null.");
		}
		
		if (delay <= 0L) {
			return Bukkit.getServer().getScheduler().runTask(ServiceLocator.getService(Plugin.class), new Runnable() {
				public void run() {
					try {
						task.run();
					} catch (Exception ex) {
						ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
						throw ex;
					}
				}}).getTaskId();
		}
		
		return Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ServiceLocator.getService(Plugin.class), new Runnable() {
			public void run() {
				try {
					task.run();
				} catch (Exception ex) {
					ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
					throw ex;
				}
			}}, delay);
	}
	public static int runAsync(Runnable task) {
		return runAsync(task, 0L);
	}
	@SuppressWarnings("deprecation")
	public static int runAsync(Runnable task, long delay) {
		if (task == null) {
			throw new IllegalArgumentException("task cannot be null.");
		}
		
		if (delay <= 0L) {
			return Bukkit.getServer().getScheduler().runTaskAsynchronously(ServiceLocator.getService(Plugin.class), new Runnable() {
				public void run() {
					try {
						task.run();
					} catch (Exception ex) {
						ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
						throw ex;
					}
				}}).getTaskId();
		}
		
		return Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(ServiceLocator.getService(Plugin.class), new Runnable() {
			public void run() {
				try {
					task.run();
				} catch (Exception ex) {
					ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
					throw ex;
				}
			}}, delay);
	}
	
	public static void cancelTask(int id) {
		 Bukkit.getServer().getScheduler().cancelTask(id);
	}
	
	//private
	
}