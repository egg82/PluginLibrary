package ninja.egg82.plugin.commands;

import org.bukkit.event.Event;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.command.Command;
import ninja.egg82.plugin.enums.CustomServiceType;
import ninja.egg82.plugin.utils.interfaces.IPermissionsManager;

public class EventCommand extends Command {
	//vars
	protected Event event = null;
	
	protected IPermissionsManager permissionsManager = (IPermissionsManager) ServiceLocator.getService(CustomServiceType.PERMISSIONS_MANAGER);
	
	//constructor
	public EventCommand(Event event) {
		super(0);
		this.event = event;
	}
	
	//public
	
	//private
	
}