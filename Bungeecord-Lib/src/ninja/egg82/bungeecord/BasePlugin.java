package ninja.egg82.bungeecord;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.egg82.bungeecord.core.OfflinePlayerRegistry;
import ninja.egg82.bungeecord.core.OfflinePlayerReverseRegistry;
import ninja.egg82.bungeecord.enums.BungeeInitType;
import ninja.egg82.bungeecord.handlers.CommandHandler;
import ninja.egg82.bungeecord.handlers.EventListener;
import ninja.egg82.bungeecord.services.ConfigRegistry;
import ninja.egg82.bungeecord.services.LanguageRegistry;
import ninja.egg82.bungeecord.utils.ConfigUtil;
import ninja.egg82.bungeecord.utils.LanguageUtil;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptionHandlers.NullExceptionHandler;
import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.startup.InitRegistry;
import ninja.egg82.startup.Start;
import ninja.egg82.utils.FileUtil;

public class BasePlugin extends Plugin {
	//vars
	private Logger logger = null;
	private String serverId = null;
	
	//constructor
	public BasePlugin() {
		super();
		
		Start.init();
		
		ServiceLocator.provideService(this);
		ServiceLocator.provideService(NullExceptionHandler.class);
	}
	
	//public
	public void onLoad() {
		logger = getLogger();
		ServiceLocator.provideService(logger);
		logger.addHandler((Handler) ServiceLocator.getService(IExceptionHandler.class));
		
		IRegistry<String> initRegistry = ServiceLocator.getService(InitRegistry.class);
		initRegistry.setRegister(BungeeInitType.PLUGIN_VERSION, getDescription().getVersion());
		
		ServiceLocator.provideService(ConfigRegistry.class, false);
		ConfigUtil.setRegistry(ServiceLocator.getService(ConfigRegistry.class));
		ServiceLocator.provideService(LanguageRegistry.class, false);
		LanguageUtil.setRegistry(ServiceLocator.getService(LanguageRegistry.class));
		
		ServiceLocator.provideService(OfflinePlayerRegistry.class);
		ServiceLocator.provideService(OfflinePlayerReverseRegistry.class);
		
		ServiceLocator.provideService(CommandHandler.class, false);
		
		serverId = getId();
		if (serverId == null || serverId.isEmpty() || serverId.equalsIgnoreCase("unnamed") || serverId.equalsIgnoreCase("unknown") || serverId.equalsIgnoreCase("default")) {
			serverId = UUID.randomUUID().toString();
			writeProperties();
		}
	}
	
	public void onEnable() {
		ServiceLocator.provideService(EventListener.class, false);
	}
	public void onDisable() {
		
	}
	
	public String getServerId() {
		return serverId;
	}
	
	//private
	protected final void printInfo(String message) {
		logger.info(ChatColor.GRAY + "[INFO] " + ChatColor.RESET + message);
	}
	protected final void printWarning(String message) {
		logger.warning(ChatColor.YELLOW + "[WARN] " + ChatColor.RESET + message);
	}
	protected final void printError(String message) {
		logger.severe(ChatColor.RED + "[ERROR] " + ChatColor.RESET + message);
	}
	
	private String getId() {
		File propertiesFile = new File(getProxy().getPluginsFolder().getParent(), "config.yml");
		String path = propertiesFile.getAbsolutePath();
		
		if (!FileUtil.pathExists(path) || !FileUtil.pathIsFile(path)) {
			return null;
		}
		
		try {
			FileUtil.open(path);
			
			String[] lines = toString(FileUtil.read(path, 0L), Charset.forName("UTF-8")).replaceAll("\r", "").split("\n");
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].trim().startsWith("stats:")) {
					return lines[i].substring(6).trim();
				}
			}
		} catch (Exception ex) {
			
		}
		
		return null;
	}
	private void writeProperties() {
		File propertiesFile = new File(getProxy().getPluginsFolder().getParent(), "config.yml");
		String path = propertiesFile.getAbsolutePath();
		
		if (!FileUtil.pathExists(path) || !FileUtil.pathIsFile(path)) {
			return;
		}
		
		try {
			FileUtil.open(path);
			
			String[] lines = toString(FileUtil.read(path, 0L), Charset.forName("UTF-8")).replaceAll("\r", "").split("\n");
			boolean found = false;
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].trim().startsWith("stats:")) {
					found = true;
					lines[i] = "stats: " + serverId;
				}
			}
			if (!found) {
				ArrayList<String> temp = new ArrayList<String>(Arrays.asList(lines));
				temp.add("stats: " + serverId);
				lines = temp.toArray(new String[0]);
			}
			
			FileUtil.erase(path);
			FileUtil.write(path, toBytes(String.join(FileUtil.LINE_SEPARATOR, lines), Charset.forName("UTF-8")), 0L);
			FileUtil.close(path);
		} catch (Exception ex) {
			
		}
	}
	
	private byte[] toBytes(String input, Charset enc) {
		return input.getBytes(enc);
	}
	private String toString(byte[] input, Charset enc) {
		return new String(input, enc);
	}
}
