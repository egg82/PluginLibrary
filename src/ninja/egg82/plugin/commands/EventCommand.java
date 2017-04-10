package ninja.egg82.plugin.commands;

import org.bukkit.event.Event;

import ninja.egg82.patterns.SynchronousCommand;

public abstract class EventCommand extends SynchronousCommand {
	//vars
	protected Event event = null;
	
	//constructor
	public EventCommand(Event event) {
		super();
		this.event = event;
	}
	
	//public
	
	//private
	
}