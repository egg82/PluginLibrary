package ninja.egg82.plugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ninja.egg82.patterns.ServiceLocator;
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
		if (plugin == null) {
			plugin = ServiceLocator.getService(InitRegistry.class).getRegister("plugin", JavaPlugin.class);
		}
		
		if (delay <= 0L) {
			return Bukkit.getServer().getScheduler().runTask(plugin, task).getTaskId();
		} else {
			return Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, delay);
		}
	}
	public static int runAsync(Runnable task) {
		return runAsync(task, 0L);
	}
	@SuppressWarnings("deprecation")
	public static int runAsync(Runnable task, long delay) {
		if (plugin == null) {
			plugin = ServiceLocator.getService(InitRegistry.class).getRegister("plugin", JavaPlugin.class);
		}
		
		if (delay <= 0L) {
			return Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, task).getTaskId();
		} else {
			return Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, task, delay);
		}
	}
	
	public static void cancelTask(int id) {
		 Bukkit.getServer().getScheduler().cancelTask(id);
	}
	
	//private
	
}
