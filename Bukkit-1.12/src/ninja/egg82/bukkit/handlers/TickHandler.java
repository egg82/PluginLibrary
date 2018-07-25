package ninja.egg82.bukkit.handlers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.patterns.Command;
import ninja.egg82.patterns.ServiceLocator;

public abstract class TickHandler extends Command {
	//vars
	private int taskId = -1;
	
	private long delay = 0L;
	private long period = 0L;
	
	//constructor
	public TickHandler(long delay, long period) {
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
	private void startTask() {
		taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(ServiceLocator.getService(Plugin.class), new Runnable() {
			public void run() {
				try {
					start();
				} catch (Exception ex) {
					ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
					throw ex;
				}
			}
		}, delay, period);
	}
}
