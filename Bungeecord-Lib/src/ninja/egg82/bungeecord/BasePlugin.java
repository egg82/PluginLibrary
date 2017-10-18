package ninja.egg82.bungeecord;

import java.util.logging.Handler;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.egg82.bungeecord.core.OfflinePlayerRegistry;
import ninja.egg82.bungeecord.core.OfflinePlayerReverseRegistry;
import ninja.egg82.bungeecord.enums.BungeeInitType;
import ninja.egg82.bungeecord.handlers.CommandHandler;
import ninja.egg82.bungeecord.services.ConfigRegistry;
import ninja.egg82.bungeecord.services.LanguageRegistry;
import ninja.egg82.bungeecord.utils.ConfigUtil;
import ninja.egg82.bungeecord.utils.LanguageUtil;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptionHandlers.NullExceptionHandler;
import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.events.EventHandler;
import ninja.egg82.startup.InitRegistry;
import ninja.egg82.startup.Start;

public class BasePlugin extends Plugin {
	//vars
	private Logger logger = null;
	
	//constructor
	public BasePlugin() {
		super();
		
		Start.init();
		
		ServiceLocator.provideService(this);
		ServiceLocator.provideService(logger);
		
		logger = getLogger();
		ServiceLocator.provideService(NullExceptionHandler.class);
		logger.addHandler((Handler) ServiceLocator.getService(IExceptionHandler.class));
		
		IRegistry<String> initRegistry = ServiceLocator.getService(InitRegistry.class);
		initRegistry.setRegister(BungeeInitType.PLUGIN_VERSION, getDescription().getVersion());
	}
	
	//public
	public void onLoad() {
		ServiceLocator.provideService(ConfigRegistry.class, false);
		ConfigUtil.setRegistry(ServiceLocator.getService(ConfigRegistry.class));
		ServiceLocator.provideService(LanguageRegistry.class, false);
		LanguageUtil.setRegistry(ServiceLocator.getService(LanguageRegistry.class));
		
		ServiceLocator.provideService(OfflinePlayerRegistry.class);
		ServiceLocator.provideService(OfflinePlayerReverseRegistry.class);
		
		ServiceLocator.provideService(CommandHandler.class, false);
	}
	
	public void onEnable() {
		ServiceLocator.provideService(EventHandler.class, false);
	}
	public void onDisable() {
		
	}
	
	//private
	protected final void info(String message) {
		logger.info(ChatColor.GRAY + "[INFO] " + ChatColor.RESET + message);
	}
	protected final void warning(String message) {
		logger.warning(ChatColor.YELLOW + "[WARN] " + ChatColor.RESET + message);
	}
	protected final void error(String message) {
		logger.severe(ChatColor.RED + "[ERROR] " + ChatColor.RESET + message);
	}
}
