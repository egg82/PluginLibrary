package ninja.egg82.plugin.commands;

import ninja.egg82.patterns.command.Command;

public class TickCommand extends Command {
	//vars
	protected long ticks = 0;
	
	//constructor
	public TickCommand() {
		super();
	}
	
	//public
	public long getTicks() {
		return ticks;
	}
	
	//private
	
}
