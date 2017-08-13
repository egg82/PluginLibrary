package ninja.egg82.plugin.commands;

import ninja.egg82.patterns.SynchronousCommand;

public abstract class TickCommand extends SynchronousCommand {
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
	public void setTicks(long ticks) {
		this.ticks = ticks;
	}
	
	//private
	
}
