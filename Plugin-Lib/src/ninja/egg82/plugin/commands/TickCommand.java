package ninja.egg82.plugin.commands;

import ninja.egg82.patterns.Command;

public abstract class TickCommand extends Command {
	//vars
	private long ticks = 0L;
	
	//constructor
	public TickCommand(long ticks) {
		super();
		
		this.ticks = ticks;
	}
	
	//public
	public final long getTicks() {
		return ticks;
	}
	
	//private
	
}
