package ninja.egg82.plugin;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import ninja.egg82.enums.ServiceType;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.enums.SpigotReflectType;
import ninja.egg82.plugin.enums.SpigotRegType;
import ninja.egg82.plugin.enums.SpigotServiceType;
import ninja.egg82.plugin.reflection.sound.SoundUtil;
import ninja.egg82.plugin.utils.CommandHandler;
import ninja.egg82.plugin.utils.EventListener;
import ninja.egg82.plugin.utils.Logger;
import ninja.egg82.plugin.utils.PermissionsManager;
import ninja.egg82.plugin.utils.TickHandler;
import ninja.egg82.plugin.utils.interfaces.ICommandHandler;
import ninja.egg82.plugin.utils.interfaces.IEventListener;
import ninja.egg82.plugin.utils.interfaces.ILogger;
import ninja.egg82.plugin.utils.interfaces.IPermissionsManager;
import ninja.egg82.plugin.utils.interfaces.ITickHandler;
import ninja.egg82.registry.Registry;
import ninja.egg82.registry.interfaces.IRegistry;
import ninja.egg82.startup.Start;
import ninja.egg82.utils.Util;

public class BasePlugin extends JavaPlugin {
	//vars
	protected ILogger logger = null;
	protected ICommandHandler commandHandler = null;
	protected IEventListener eventListener = null;
	protected IPermissionsManager permissionsManager = null;
	protected ITickHandler tickHandler = null;
	protected IRegistry initReg = null;
	
	//constructor
	public BasePlugin() {
		
	}
	
	//public
	public void onLoad() {
		Start.init();
		
		initReg = (IRegistry) ServiceLocator.getService(ServiceType.INIT_REGISTRY);
		try {
			initReg.setRegister(SpigotRegType.GAME_VERSION, Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3]);
		} catch (Exception ex) {
			
		}
		initReg.setRegister(SpigotRegType.PLUGIN_VERSION, getDescription().getVersion());
		initReg.setRegister(SpigotRegType.PLUGIN, this);
		
		ServiceLocator.provideService(SpigotServiceType.REFLECT_REGISTRY, Registry.class, false);
		IRegistry reflectReg = (IRegistry) ServiceLocator.getService(SpigotServiceType.REFLECT_REGISTRY);
		reflectReg.setRegister(SpigotReflectType.SOUND, new SoundUtil());
		ref(reflectReg, SpigotReflectType.PLAYER, (String) initReg.getRegister(SpigotRegType.GAME_VERSION), "ninja.egg82.plugin.reflection.player");
		
		ServiceLocator.provideService(SpigotServiceType.LOGGER, Logger.class, false);
		logger = (ILogger) ServiceLocator.getService(SpigotServiceType.LOGGER);
		logger.initialize(getLogger());
		
		ServiceLocator.provideService(SpigotServiceType.PERMISSIONS_MANAGER, PermissionsManager.class, false);
		permissionsManager = (IPermissionsManager) ServiceLocator.getService(SpigotServiceType.PERMISSIONS_MANAGER);
		
		ServiceLocator.provideService(SpigotServiceType.COMMAND_HANDLER, CommandHandler.class, false);
		commandHandler = (ICommandHandler) ServiceLocator.getService(SpigotServiceType.COMMAND_HANDLER);
		
		ServiceLocator.provideService(SpigotServiceType.EVENT_LISTENER, EventListener.class, false);
		eventListener = (IEventListener) ServiceLocator.getService(SpigotServiceType.EVENT_LISTENER);
		
		ServiceLocator.provideService(SpigotServiceType.TICK_HANDLER, TickHandler.class, false);
		tickHandler = (ITickHandler) ServiceLocator.getService(SpigotServiceType.TICK_HANDLER);
		tickHandler.initialize(this, getServer().getScheduler());
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
	private void ref(IRegistry reflectReg, String regType, String version, String pkg) {
		ArrayList<Class<?>> enums = Util.getClasses(Object.class, pkg);
		for (Class<?> c : enums) {
			String name = c.getSimpleName();
			String pkg2 = c.getName();
			pkg2 = pkg2.substring(0, pkg2.lastIndexOf('.'));
			
			if (!pkg2.equalsIgnoreCase(pkg)) {
				continue;
			}
			
			String vers = null;
			if (name.substring(name.length() - 4).charAt(0) == '_') {
				vers = name.substring(name.length() - 3);
			} else {
				vers = name.substring(name.length() - 4);
			}
			
			if (version.contains(vers)) {
				try {
					reflectReg.setRegister(regType, c.newInstance());
				} catch (Exception ex) {
					
				}
			}
		}
	}
}