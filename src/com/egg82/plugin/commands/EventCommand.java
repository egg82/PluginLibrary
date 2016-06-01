package com.egg82.plugin.commands;

import org.bukkit.event.Event;

import com.egg82.patterns.ServiceLocator;
import com.egg82.patterns.command.Command;
import com.egg82.plugin.enums.CustomServiceType;
import com.egg82.plugin.utils.interfaces.IPermissionsManager;

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