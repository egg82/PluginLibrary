package ninja.egg82.plugin;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.reflection.sound.SoundUtil;
import ninja.egg82.plugin.utils.CommandHandler;
import ninja.egg82.plugin.utils.EventListener;
import ninja.egg82.plugin.utils.ICommandHandler;
import ninja.egg82.plugin.utils.IEventListener;
import ninja.egg82.plugin.utils.ILogger;
import ninja.egg82.plugin.utils.IPermissionsManager;
import ninja.egg82.plugin.utils.ITickHandler;
import ninja.egg82.plugin.utils.Logger;
import ninja.egg82.plugin.utils.PermissionsManager;
import ninja.egg82.plugin.utils.TickHandler;
import ninja.egg82.plugin.utils.VersionUtil;
import ninja.egg82.startup.InitRegistry;
import ninja.egg82.startup.Start;
import ninja.egg82.utils.ReflectUtil;

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
		
		String gameVersion = Bukkit.getVersion();
		gameVersion = gameVersion.substring(gameVersion.indexOf('('));
		gameVersion = gameVersion.substring(gameVersion.indexOf(' ') + 1, gameVersion.length() - 1);
		
		initReg = (IRegistry) ServiceLocator.getService(InitRegistry.class);
		//initReg.setRegister("game.version", String.class, Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3]);
		initReg.setRegister("game.version", String.class, gameVersion);
		initReg.setRegister("plugin.version", String.class, getDescription().getVersion());
		initReg.setRegister("plugin", JavaPlugin.class, this);
		
		ServiceLocator.provideService(SoundUtil.class);
		reflect(gameVersion, "ninja.egg82.plugin.reflection.player");
		reflect(gameVersion, "ninja.egg82.plugin.reflection.entity");
		
		ServiceLocator.provideService(Logger.class, false);
		logger = (ILogger) ServiceLocator.getService(Logger.class);
		logger.initialize(getLogger());
		
		ServiceLocator.provideService(PermissionsManager.class, false);
		permissionsManager = (IPermissionsManager) ServiceLocator.getService(IPermissionsManager.class);
		
		ServiceLocator.provideService(CommandHandler.class, false);
		commandHandler = (ICommandHandler) ServiceLocator.getService(ICommandHandler.class);
		
		ServiceLocator.provideService(EventListener.class, false);
		eventListener = (IEventListener) ServiceLocator.getService(IEventListener.class);
		
		ServiceLocator.provideService(TickHandler.class, false);
		tickHandler = (ITickHandler) ServiceLocator.getService(ITickHandler.class);
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
	private void reflect(String version, String pkg) {
		ArrayList<Class<?>> enums = ReflectUtil.getClasses(Object.class, pkg);
		
		int[] currentVersion = VersionUtil.parseVersion(version, '.');
		
		Class<?> bestMatch = null;
		
		for (Class<?> c : enums) {
			String name = c.getSimpleName();
			String pkg2 = c.getName();
			pkg2 = pkg2.substring(0, pkg2.lastIndexOf('.'));
			
			if (!pkg2.equalsIgnoreCase(pkg)) {
				continue;
			}
		    
		    int[] reflectVersion = VersionUtil.parseVersion(name, '_');
		    
		    for (int i = 0; i < Math.min(currentVersion.length, reflectVersion.length); i++) {
		    	if (currentVersion[i] > reflectVersion[i]) {
		    		bestMatch = c;
		    		break;
		    	}
		    }
		}
		
		if (bestMatch != null) {
			ServiceLocator.provideService(bestMatch);
		}
	}
}