package ninja.egg82.plugin;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import ninja.egg82.plugin.reflection.nbt.NBTAPIHelper;
import ninja.egg82.plugin.reflection.nbt.NullNBTHelper;
import ninja.egg82.plugin.reflection.nbt.PowerNBTHelper;
import ninja.egg82.plugin.reflection.protocol.NullFakeBlockHelper;
import ninja.egg82.plugin.reflection.protocol.NullFakeEntityHelper;
import ninja.egg82.plugin.reflection.protocol.ProtocolLibFakeBlockHelper;
import ninja.egg82.plugin.reflection.protocol.ProtocolLibFakeEntityHelper;
import ninja.egg82.plugin.utils.VersionUtil;
import ninja.egg82.startup.InitRegistry;
import ninja.egg82.startup.Start;

public class BasePlugin extends JavaPlugin {
	//vars
	private CommandHandler commandHandler = null;
	private String gameVersion = Bukkit.getVersion();
	private Logger logger = null;
	private CommandSender consoleSender = null;
	
	//constructor
	public BasePlugin() {
		
	}
	
	//public
	public void onLoad() {
		logger = getLogger();
		consoleSender = this.getServer().getConsoleSender();
		
		Start.init();
		
		PluginManager manager = getServer().getPluginManager();
		
		gameVersion = gameVersion.substring(gameVersion.indexOf('('));
		gameVersion = gameVersion.substring(gameVersion.indexOf(' ') + 1, gameVersion.length() - 1);
		gameVersion = gameVersion.trim().replace('_', '.');
		
		IRegistry initRegistry = (IRegistry) ServiceLocator.getService(InitRegistry.class);
		initRegistry.setRegister("game.version", String.class, gameVersion);
		initRegistry.setRegister("plugin", JavaPlugin.class, this);
		initRegistry.setRegister("plugin.version", String.class, getDescription().getVersion());
		initRegistry.setRegister("plugin.manager", PluginManager.class, getServer().getPluginManager());
		initRegistry.setRegister("plugin.scheduler", BukkitScheduler.class, getServer().getScheduler());
		initRegistry.setRegister("plugin.logger", Logger.class, getLogger());
		
		reflect(gameVersion, "ninja.egg82.plugin.reflection.player");
		reflect(gameVersion, "ninja.egg82.plugin.reflection.entity");
		reflect(gameVersion, "ninja.egg82.plugin.reflection.protocol.wrappers.entityLiving");
		reflect(gameVersion, "ninja.egg82.plugin.reflection.protocol.wrappers.block");
		
		if (manager.getPlugin("ProtocolLib") != null) {
			ServiceLocator.provideService(ProtocolLibFakeEntityHelper.class);
			ServiceLocator.provideService(ProtocolLibFakeBlockHelper.class);
		} else {
			ServiceLocator.provideService(NullFakeEntityHelper.class);
			ServiceLocator.provideService(NullFakeBlockHelper.class);
		}
		
		if (manager.getPlugin("PowerNBT") != null) {
			ServiceLocator.provideService(PowerNBTHelper.class);
		} else if (manager.getPlugin("ItemNBTAPI") != null) {
			ServiceLocator.provideService(NBTAPIHelper.class);
		} else {
			ServiceLocator.provideService(NullNBTHelper.class);
		}
		
		ServiceLocator.provideService(PermissionsManager.class, false);
		ServiceLocator.provideService(CommandHandler.class, false);
		ServiceLocator.provideService(TickHandler.class, false);
		
		commandHandler = ServiceLocator.getService(CommandHandler.class);
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
	public final List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return commandHandler.tabComplete(sender, command, label, args);
	}
	
	//private
	protected final void info(String message) {
		if (consoleSender == null) {
			consoleSender = getServer().getConsoleSender();
		}
		
		if (consoleSender != null) {
			consoleSender.sendMessage(ChatColor.GRAY + "[INFO] " + ChatColor.RESET + message);
		} else {
			logger.info(message);
		}
	}
	protected final void warning(String message) {
		if (consoleSender == null) {
			consoleSender = getServer().getConsoleSender();
		}
		
		if (consoleSender != null) {
			consoleSender.sendMessage(ChatColor.YELLOW + "[WARN] " + ChatColor.RESET + message);
		} else {
			logger.warning(message);
		}
	}
	protected final void error(String message) {
		if (consoleSender == null) {
			consoleSender = getServer().getConsoleSender();
		}
		
		if (consoleSender != null) {
			consoleSender.sendMessage(ChatColor.RED + "[ERROR] " + ChatColor.RESET + message);
		} else {
			logger.severe(message);
		}
	}
	
	private void reflect(String version, String pkg) {
		reflect(version, pkg, true);
	}
	private void reflect(String version, String pkg, boolean lazyInitialize) {
		Class<?> bestMatch = VersionUtil.getBestMatch(version, pkg);
		
		if (bestMatch != null) {
			ServiceLocator.provideService(bestMatch, lazyInitialize);
		}
	}
}