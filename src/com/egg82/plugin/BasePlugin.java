package com.egg82.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.egg82.patterns.ServiceLocator;
import com.egg82.plugin.enums.CustomServiceType;
import com.egg82.plugin.utils.CommandHandler;
import com.egg82.plugin.utils.EventListener;
import com.egg82.plugin.utils.Logger;
import com.egg82.plugin.utils.PermissionsManager;
import com.egg82.plugin.utils.TickHandler;
import com.egg82.plugin.utils.interfaces.ICommandHandler;
import com.egg82.plugin.utils.interfaces.IEventListener;
import com.egg82.plugin.utils.interfaces.ILogger;
import com.egg82.plugin.utils.interfaces.IPermissionsManager;
import com.egg82.plugin.utils.interfaces.ITickHandler;
import com.egg82.startup.Start;

public class BasePlugin extends JavaPlugin {
	//vars
	protected ILogger logger = null;
	protected ICommandHandler commandHandler = null;
	protected IEventListener eventListener = null;
	protected IPermissionsManager permissionsManager = null;
	protected ITickHandler tickHandler = null;
	
	//constructor
	public BasePlugin() {
		
	}
	
	//public
	public void onLoad() {
		ServiceLocator.provideService(CustomServiceType.LOGGER, Logger.class, false);
		logger = (ILogger) ServiceLocator.getService(CustomServiceType.LOGGER);
		logger.initialize(getLogger());
		
		ServiceLocator.provideService(CustomServiceType.PERMISSIONS_MANAGER, PermissionsManager.class, false);
		permissionsManager = (IPermissionsManager) ServiceLocator.getService(CustomServiceType.PERMISSIONS_MANAGER);
		
		ServiceLocator.provideService(CustomServiceType.COMMAND_HANDLER, CommandHandler.class, false);
		commandHandler = (ICommandHandler) ServiceLocator.getService(CustomServiceType.COMMAND_HANDLER);
		
		ServiceLocator.provideService(CustomServiceType.EVENT_LISTENER, EventListener.class, false);
		eventListener = (IEventListener) ServiceLocator.getService(CustomServiceType.EVENT_LISTENER);
		
		ServiceLocator.provideService(CustomServiceType.TICK_HANDLER, TickHandler.class, false);
		tickHandler = (ITickHandler) ServiceLocator.getService(CustomServiceType.TICK_HANDLER);
		tickHandler.initialize(this, getServer().getScheduler());
		
		Start.init();
	}
	
	public void onEnable() {
		permissionsManager.initialize(getServer().getPluginManager());
		getServer().getPluginManager().registerEvents(eventListener, this);
	}
	public void onDisable() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command event, String label, String[] args) {
		commandHandler.runCommand(sender, event, label, args);
		return commandHandler.hasCommand(event.getName().toLowerCase());
	}
	
	//private
	
}