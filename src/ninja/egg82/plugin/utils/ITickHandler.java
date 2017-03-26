package ninja.egg82.plugin.utils;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import ninja.egg82.plugin.commands.TickCommand;

public interface ITickHandler {
	//functions
	void initialize(Plugin plugin, BukkitScheduler scheduler);
	void destroy();
	void addTickCommand(String name, Class<? extends TickCommand> commandToRun);
	void addAsyncTickCommand(String name, Class<? extends TickCommand> commandToRun);
	void addDelayedTickCommand(String name, Class<? extends TickCommand> commandToRun, long delay);
	void addAsyncDelayedTickCommand(String name, Class<? extends TickCommand> commandToRun, long delay);
	void removeTickCommand(String name);
	void clearTickCommands();
	boolean hasTickCommand(String name);
}