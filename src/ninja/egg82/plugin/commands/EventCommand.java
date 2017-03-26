package ninja.egg82.plugin.commands;

import org.bukkit.event.Event;

import ninja.egg82.patterns.Command;

public abstract class EventCommand extends Command {
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