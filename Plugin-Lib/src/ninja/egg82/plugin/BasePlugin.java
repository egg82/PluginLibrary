package ninja.egg82.plugin;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptionHandlers.NullExceptionHandler;
import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.enums.SpigotInitType;
import ninja.egg82.plugin.handlers.CommandHandler;
import ninja.egg82.plugin.handlers.PermissionsManager;
import ninja.egg82.plugin.handlers.TickHandler;
import ninja.egg82.plugin.services.ConfigRegistry;
import ninja.egg82.plugin.services.LanguageRegistry;
import ninja.egg82.plugin.utils.ConfigUtil;
import ninja.egg82.plugin.utils.LanguageUtil;
import ninja.egg82.plugin.utils.VersionUtil;
import ninja.egg82.startup.InitRegistry;
import ninja.egg82.startup.Start;

public class BasePlugin extends JavaPlugin {
	//vars
	private CommandHandler commandHandler = null;
	private String gameVersion = null;
	private Logger logger = null;
	private CommandSender consoleSender = null;
	
	//constructor
	public BasePlugin() {
		super();
		
		Start.init();
		
		logger = getLogger();
		ServiceLocator.provideService(NullExceptionHandler.class);
		logger.addHandler((Handler) ServiceLocator.getService(IExceptionHandler.class));
		
		gameVersion = Bukkit.getVersion();
		gameVersion = gameVersion.substring(gameVersion.indexOf('('));
		gameVersion = gameVersion.substring(gameVersion.indexOf(' ') + 1, gameVersion.length() - 1);
		gameVersion = gameVersion.trim().replace('_', '.');
		
		IRegistry<String> initRegistry = ServiceLocator.getService(InitRegistry.class);
		initRegistry.setRegister(SpigotInitType.GAME_VERSION, gameVersion);
		initRegistry.setRegister(SpigotInitType.PLUGIN, this);
		initRegistry.setRegister(SpigotInitType.PLUGIN_VERSION, getDescription().getVersion());
		initRegistry.setRegister(SpigotInitType.PLUGIN_LOGGER, getLogger());
	}
	
	//public
	public void onLoad() {
		consoleSender = this.getServer().getConsoleSender();
		
		reflect(gameVersion, "ninja.egg82.plugin.reflection.player");
		reflect(gameVersion, "ninja.egg82.plugin.reflection.entity");
		
		ServiceLocator.provideService(ConfigRegistry.class, false);
		ConfigUtil.setRegistry(ServiceLocator.getService(ConfigRegistry.class));
		ServiceLocator.provideService(LanguageRegistry.class, false);
		LanguageUtil.setRegistry(ServiceLocator.getService(LanguageRegistry.class));
		
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