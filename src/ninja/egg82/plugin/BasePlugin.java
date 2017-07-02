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
import ninja.egg82.plugin.handlers.PermissionsManager;
import ninja.egg82.plugin.handlers.TickHandler;
import ninja.egg82.plugin.reflection.protocol.NullFakeBlockHelper;
import ninja.egg82.plugin.reflection.protocol.NullFakeEntityHelper;
import ninja.egg82.plugin.reflection.protocol.ProtocolLibFakeBlockHelper;
import ninja.egg82.plugin.reflection.protocol.ProtocolLibFakeEntityHelper;
import ninja.egg82.plugin.utils.EntityTypeHelper;
import ninja.egg82.plugin.utils.MaterialHelper;
import ninja.egg82.plugin.utils.PotionEffectTypeHelper;
import ninja.egg82.plugin.utils.SoundHelper;
import ninja.egg82.plugin.utils.VersionUtil;
import ninja.egg82.startup.InitRegistry;
import ninja.egg82.startup.Start;
import ninja.egg82.utils.ReflectUtil;

public class BasePlugin extends JavaPlugin {
	//vars
	private CommandHandler commandHandler = null;
	private String gameVersion = Bukkit.getVersion();
	
	//constructor
	public BasePlugin() {
		
	}
	
	//public
	public void onLoad() {
		Start.init();
		
		gameVersion = gameVersion.substring(gameVersion.indexOf('('));
		gameVersion = gameVersion.substring(gameVersion.indexOf(' ') + 1, gameVersion.length() - 1);
		
		IRegistry initRegistry = (IRegistry) ServiceLocator.getService(InitRegistry.class);
		initRegistry.setRegister("game.version", String.class, gameVersion);
		initRegistry.setRegister("plugin", JavaPlugin.class, this);
		initRegistry.setRegister("plugin.version", String.class, getDescription().getVersion());
		initRegistry.setRegister("plugin.manager", PluginManager.class, getServer().getPluginManager());
		initRegistry.setRegister("plugin.scheduler", BukkitScheduler.class, getServer().getScheduler());
		initRegistry.setRegister("plugin.logger", Logger.class, getLogger());
		
		ServiceLocator.provideService(SoundHelper.class);
		ServiceLocator.provideService(MaterialHelper.class);
		ServiceLocator.provideService(EntityTypeHelper.class);
		ServiceLocator.provideService(PotionEffectTypeHelper.class);
		reflect(gameVersion, "ninja.egg82.plugin.reflection.player");
		reflect(gameVersion, "ninja.egg82.plugin.reflection.entity");
		reflect(gameVersion, "ninja.egg82.plugin.reflection.protocol.wrappers.entityLiving");
		reflect(gameVersion, "ninja.egg82.plugin.reflection.protocol.wrappers.block");
		
		if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
			ServiceLocator.provideService(ProtocolLibFakeEntityHelper.class);
			ServiceLocator.provideService(ProtocolLibFakeBlockHelper.class);
		} else {
			ServiceLocator.provideService(NullFakeEntityHelper.class);
			ServiceLocator.provideService(NullFakeBlockHelper.class);
		}
		
		ServiceLocator.provideService(PermissionsManager.class, false);
		ServiceLocator.provideService(CommandHandler.class, false);
		ServiceLocator.provideService(TickHandler.class, false);
		
		commandHandler = (CommandHandler) ServiceLocator.getService(CommandHandler.class);
	}
	
	public void onEnable() {
		reflect(gameVersion, "ninja.egg82.plugin.reflection.event", false);
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
		reflect(version, pkg, true);
	}
	private void reflect(String version, String pkg, boolean lazyInitialize) {
		List<Class<?>> enums = ReflectUtil.getClasses(Object.class, pkg);
		
		// Sort by version, ascending
		enums.sort((v1, v2) -> {
			int[] v1Name = VersionUtil.parseVersion(v1.getSimpleName(), '_');
			int[] v2Name = VersionUtil.parseVersion(v2.getSimpleName(), '_');
			
			if (v1Name.length == 0) {
				return -1;
			}
			if (v2Name.length == 0) {
				return 1;
			}
			
			for (int i = 0; i < Math.min(v1Name.length, v2Name.length); i++) {
				if (v1Name[i] < v2Name[i]) {
					return -1;
				} else if (v1Name[i] > v2Name[i]) {
					return 1;
				}
			}
			
			return 0;
		});
		
		int[] currentVersion = VersionUtil.parseVersion(version, '.');
		
		Class<?> bestMatch = null;
		
		// Ascending order means it will naturally try to get the highest possible value (lowest->highest)
		for (Class<?> c : enums) {
			String name = c.getSimpleName();
		    
		    int[] reflectVersion = VersionUtil.parseVersion(name, '_');
		    
		    // Here's where we cap how high we can get, comparing the reflected version to the Bukkit version
		    // True makes the initial assumption that the current reflected version is correct
		    boolean equalToOrLessThan = true;
		    for (int i = 0; i < reflectVersion.length; i++) {
		    	if (currentVersion.length > i) {
		    		if(reflectVersion[i] > currentVersion[i]) {
		    			// We do not, in fact, have the correct version
		    			equalToOrLessThan = false;
		    			break;
		    		} else if (currentVersion[i] > reflectVersion[i]) {
		    			// We definitely have the correct version. At least until a better one comes along
		    			break;
		    		}
		    	} else {
		    		// Nope, this isn't the correct version
		    		equalToOrLessThan = false;
		    		break;
		    	}
		    }
		    if (equalToOrLessThan) {
		    	// Our initial assumption was correct. Use this version until we can find one that's better
		    	bestMatch = c;
		    }
		}
		
		if (bestMatch != null) {
			ServiceLocator.provideService(bestMatch, lazyInitialize);
		}
	}
}