package ninja.egg82.plugin;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.handlers.CommandHandler;
import ninja.egg82.plugin.handlers.EventListener;
import ninja.egg82.plugin.handlers.PermissionsManager;
import ninja.egg82.plugin.handlers.TickHandler;
import ninja.egg82.plugin.reflection.sound.SoundUtil;
import ninja.egg82.plugin.utils.VersionUtil;
import ninja.egg82.startup.InitRegistry;
import ninja.egg82.startup.Start;
import ninja.egg82.utils.ReflectUtil;

public class BasePlugin extends JavaPlugin {
	//vars
	private CommandHandler commandHandler = null;
	
	//constructor
	public BasePlugin() {
		
	}
	
	//public
	public void onLoad() {
		Start.init();
		
		String gameVersion = Bukkit.getVersion();
		gameVersion = gameVersion.substring(gameVersion.indexOf('('));
		gameVersion = gameVersion.substring(gameVersion.indexOf(' ') + 1, gameVersion.length() - 1);
		
		IRegistry initRegistry = (IRegistry) ServiceLocator.getService(InitRegistry.class);
		initRegistry.setRegister("game.version", String.class, gameVersion);
		initRegistry.setRegister("plugin", JavaPlugin.class, this);
		initRegistry.setRegister("plugin.version", String.class, getDescription().getVersion());
		initRegistry.setRegister("plugin.manager", PluginManager.class, getServer().getPluginManager());
		initRegistry.setRegister("plugin.scheduler", BukkitScheduler.class, getServer().getScheduler());
		initRegistry.setRegister("plugin.logger", Logger.class, getLogger());
		
		ServiceLocator.provideService(SoundUtil.class);
		reflect(gameVersion, "ninja.egg82.plugin.reflection.player");
		reflect(gameVersion, "ninja.egg82.plugin.reflection.entity");
		
		ServiceLocator.provideService(PermissionsManager.class, false);
		ServiceLocator.provideService(CommandHandler.class, false);
		ServiceLocator.provideService(TickHandler.class, false);
		
		commandHandler = (CommandHandler) ServiceLocator.getService(CommandHandler.class);
	}
	
	public void onEnable() {
		ServiceLocator.provideService(EventListener.class, false);
	}
	public void onDisable() {
		
	}
	
	public final boolean onCommand(CommandSender sender, Command event, String label, String[] args) {
		if (commandHandler.hasCommand(event.getName())) {
			commandHandler.runCommand(sender, event, label, args);
			return true;
		}
		return false;
	}
	
	//private
	private void reflect(String version, String pkg) {
		List<Class<?>> enums = ReflectUtil.getClasses(Object.class, pkg);
		
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