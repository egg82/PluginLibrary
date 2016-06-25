package ninja.egg82.plugin.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import ninja.egg82.plugin.commands.TickCommand;
import ninja.egg82.plugin.utils.interfaces.ITickHandler;

public class TickHandler implements ITickHandler {
	//vars
	private HashMap<String, TickCommand> commands = new HashMap<String, TickCommand>();
	private HashMap<String, Integer> tasks = new HashMap<String, Integer>();
	
	private Plugin plugin = null;
	private BukkitScheduler scheduler = null;
	private boolean initialized = false;
	
	//constructor
	public TickHandler() {
		
	}
	
	//public
	public void initialize(Plugin plugin, BukkitScheduler scheduler) {
		if (scheduler == null || initialized) {
			return;
		}
		
		this.plugin = plugin;
		this.scheduler = scheduler;
		initialized = true;
	}
	public void destroy() {
		
	}
	
	public void addTickCommand(String name, Class<? extends TickCommand> commandToRun) {
		if (commands.containsKey(name)) {
			removeTickCommand(name);
		}
		
		final TickCommand cmd = getCommand(commandToRun);
		if (cmd == null) {
			return;
		}
		
		if (cmd.getTicks() <= 0l) {
			return;
		}
		
		commands.put(name, cmd);
		tasks.put(name, scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				cmd.start();
			}
		}, cmd.getTicks(), cmd.getTicks()));
	}
	@SuppressWarnings("deprecation")
	public void addAsyncTickCommand(String name, Class<? extends TickCommand> commandToRun) {
		if (commands.containsKey(name)) {
			removeTickCommand(name);
		}
		
		final TickCommand cmd = getCommand(commandToRun);
		if (cmd == null) {
			return;
		}
		
		if (cmd.getTicks() <= 0l) {
			return;
		}
		
		commands.put(name, cmd);
		tasks.put(name, scheduler.scheduleAsyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				cmd.start();
			}
		}, cmd.getTicks(), cmd.getTicks()));
	}
	public void addDelayedTickCommand(String name, Class<? extends TickCommand> commandToRun, long delay) {
		if (delay <= 0l) {
			return;
		}
		
		if (commands.containsKey(name)) {
			removeTickCommand(name);
		}
		
		final TickCommand cmd = getCommand(commandToRun);
		if (cmd == null) {
			return;
		}
		
		commands.put(name, cmd);
		tasks.put(name, scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				cmd.start();
				tasks.remove(name);
				commands.remove(name);
			}
		}, delay));
	}
	@SuppressWarnings("deprecation")
	public void addAsyncDelayedTickCommand(String name, Class<? extends TickCommand> commandToRun, long delay) {
		if (delay <= 0l) {
			return;
		}
		
		if (commands.containsKey(name)) {
			removeTickCommand(name);
		}
		
		final TickCommand cmd = getCommand(commandToRun);
		if (cmd == null) {
			return;
		}
		
		commands.put(name, cmd);
		tasks.put(name, scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run() {
				cmd.start();
				tasks.remove(name);
				commands.remove(name);
			}
		}, delay));
	}
	public void removeTickCommand(String name) {
		commands.computeIfPresent(name, (k,v) -> {
			scheduler.cancelTask(tasks.get(k));
			tasks.remove(k);
			return null;
		});
	}
	
	public void clearTickCommands() {
		Iterator<Entry<String, Integer>> i = tasks.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>) i.next();
			scheduler.cancelTask(pair.getValue());
		}
		tasks.clear();
		commands.clear();
	}
	public boolean hasTickCommand(String name) {
		return commands.containsKey(name);
	}
	
	//private
	private TickCommand getCommand(Class<? extends TickCommand> command) {
		TickCommand run = null;
		
		if (command == null) {
			return null;
		}
		
		try {
			run = command.newInstance();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return null;
		}
		
		return run;
	}
}