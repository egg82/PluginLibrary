package ninja.egg82.plugin.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import ninja.egg82.patterns.command.Command;
import ninja.egg82.plugin.utils.interfaces.ITickHandler;

public class TickHandler implements ITickHandler {
	//vars
	private HashMap<String, Command> commands = new HashMap<String, Command>();
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
	
	public void addTickCommand(String name, Class<? extends Command> commandToRun, long ticks) {
		if (commands.containsKey(name)) {
			removeTickCommand(name);
		}
		
		final Command cmd = getCommand(commandToRun);
		if (cmd == null ) {
			return;
		}
		
		commands.put(name, cmd);
		tasks.put(name, scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				cmd.start();
			}
		}, ticks, ticks));
	}
	@SuppressWarnings("deprecation")
	public void addAsyncTickCommand(String name, Class<? extends Command> commandToRun, long ticks) {
		if (commands.containsKey(name)) {
			removeTickCommand(name);
		}
		
		final Command cmd = getCommand(commandToRun);
		if (cmd == null ) {
			return;
		}
		
		commands.put(name, cmd);
		tasks.put(name, scheduler.scheduleAsyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				cmd.start();
			}
		}, ticks, ticks));
	}
	public void addDelayedTickCommand(String name, Class<? extends Command> commandToRun, long delay) {
		if (commands.containsKey(name)) {
			removeTickCommand(name);
		}
		
		final Command cmd = getCommand(commandToRun);
		if (cmd == null ) {
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
	public void addAsyncDelayedTickCommand(String name, Class<? extends Command> commandToRun, long delay) {
		if (commands.containsKey(name)) {
			removeTickCommand(name);
		}
		
		final Command cmd = getCommand(commandToRun);
		if (cmd == null ) {
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
		if (!commands.containsKey(name)) {
			return;
		}
		
		scheduler.cancelTask(tasks.get(name));
		tasks.remove(name);
		commands.remove(name);
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
	private Command getCommand(Class<? extends Command> command) {
		Command run = null;
		
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