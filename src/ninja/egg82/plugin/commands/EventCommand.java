package ninja.egg82.plugin.commands;

import org.bukkit.event.Event;

import ninja.egg82.patterns.command.Command;

public class EventCommand extends Command {
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