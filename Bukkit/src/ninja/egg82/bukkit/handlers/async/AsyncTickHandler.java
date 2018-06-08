package ninja.egg82.bukkit.handlers.async;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import ninja.egg82.patterns.Command;
import ninja.egg82.patterns.ServiceLocator;

public abstract class AsyncTickHandler extends Command {
	//vars
	private int taskId = -1;
	
	private long delay = 0L;
	private long period = 0L;
	
	//constructor
	public AsyncTickHandler(long delay, long period) {
		super();
		
		if (delay < 0) {
			delay = 0;
		}
		if (period < 1) {
			period = 1;
		}
		
		this.delay = delay;
		this.period = period;
		
		startTask();
	}
	
	//public
	public final boolean isCancelled() {
		return (taskId != -1) ? true : false;
	}
	public final void setCancelled(boolean cancelled) {
		if (cancelled) {
			if (taskId == -1) {
				return;
			}
			Bukkit.getServer().getScheduler().cancelTask(taskId);
		} else {
			if (taskId != -1) {
				return;
			}
			startTask();
		}
	}
	
	//private
	@SuppressWarnings("deprecation")
	private void startTask() {
		taskId = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(ServiceLocator.getService(Plugin.class), new Runnable() {
			public void run() {
				start();
			}
		}, delay, period);
	}
}
