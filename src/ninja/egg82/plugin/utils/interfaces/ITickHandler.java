package ninja.egg82.plugin.utils.interfaces;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import ninja.egg82.patterns.command.Command;

public interface ITickHandler {
	void initialize(Plugin plugin, BukkitScheduler scheduler);
	void destroy();
	void addTickCommand(String name, Class<? extends Command> commandToRun, long ticks);
	void addDelayedTickCommand(String name, Class<? extends Command> commandToRun, long delay);
	void removeTickCommand(String name);
	void clearTickCommands();
	boolean hasTickCommand(String name);
}