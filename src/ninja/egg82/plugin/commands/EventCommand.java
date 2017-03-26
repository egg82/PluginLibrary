package ninja.egg82.plugin.commands;

import org.bukkit.event.Event;

import ninja.egg82.patterns.Command;

public abstract class EventCommand extends Command {
	//vars
	protected Event event = null;
	
	//constructor
	public EventCommand() {
		super(0);
	}
	
	//public
	public void setEvent(Event event) {
		this.event = event;
	}
	
	//private
	
}