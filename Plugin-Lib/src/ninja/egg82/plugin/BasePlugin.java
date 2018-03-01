package ninja.egg82.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
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
import ninja.egg82.plugin.core.EventExecutorWrapper;
import ninja.egg82.plugin.core.OfflinePlayerRegistry;
import ninja.egg82.plugin.core.OfflinePlayerReverseRegistry;
import ninja.egg82.plugin.enums.BukkitInitType;
import ninja.egg82.plugin.handlers.EnhancedBungeeMessageHandler;
import ninja.egg82.plugin.handlers.CommandHandler;
import ninja.egg82.plugin.handlers.EventListener;
import ninja.egg82.plugin.handlers.IMessageHandler;
import ninja.egg82.plugin.handlers.PermissionsManager;
import ninja.egg82.plugin.handlers.TickHandler;
import ninja.egg82.plugin.services.ConfigRegistry;
import ninja.egg82.plugin.services.LanguageRegistry;
import ninja.egg82.plugin.utils.ConfigUtil;
import ninja.egg82.plugin.utils.LanguageUtil;
import ninja.egg82.plugin.utils.VersionUtil;
import ninja.egg82.startup.InitRegistry;
import ninja.egg82.startup.Start;
import ninja.egg82.utils.FileUtil;

public abstract class BasePlugin extends JavaPlugin {
	//vars
	private CommandHandler commandHandler = null;
	
	private String gameVersion = null;
	private Logger logger = null;
	private CommandSender consoleSender = null;
	
	private String externalIp = null;
	private String serverId = Bukkit.getServerId().trim();
	
	//constructor
	public BasePlugin() {
		super();
		
		ServiceLocator.provideService(Executors.defaultThreadFactory());
		ServiceLocator.provideService(Executors.newCachedThreadPool());
		
		Start.init();
		
		logger = getLogger();
		
		ServiceLocator.provideService(this);
		ServiceLocator.provideService(logger);
		
		ServiceLocator.provideService(NullExceptionHandler.class);
		logger.addHandler((Handler) ServiceLocator.getService(IExceptionHandler.class));
		
		gameVersion = Bukkit.getVersion();
		gameVersion = gameVersion.substring(gameVersion.indexOf('('));
		gameVersion = gameVersion.substring(gameVersion.indexOf(' ') + 1, gameVersion.length() - 1);
		gameVersion = gameVersion.trim().replace('_', '.');
		
		IRegistry<String> initRegistry = ServiceLocator.getService(InitRegistry.class);
		initRegistry.setRegister(BukkitInitType.GAME_VERSION, gameVersion);
		initRegistry.setRegister(BukkitInitType.PLUGIN_VERSION, getDescription().getVersion());
	}
	
	//public
	public void onLoad() {
		consoleSender = getServer().getConsoleSender();
		
		reflect(gameVersion, "ninja.egg82.plugin.reflection.player");
		reflect(gameVersion, "ninja.egg82.plugin.reflection.entity");
		
		ServiceLocator.provideService(ConfigRegistry.class, false);
		ConfigUtil.setRegistry(ServiceLocator.getService(ConfigRegistry.class));
		ServiceLocator.provideService(LanguageRegistry.class, false);
		LanguageUtil.setRegistry(ServiceLocator.getService(LanguageRegistry.class));
		
		ServiceLocator.provideService(OfflinePlayerRegistry.class);
		ServiceLocator.provideService(OfflinePlayerReverseRegistry.class);
		
		ServiceLocator.provideService(PermissionsManager.class, false);
		ServiceLocator.provideService(CommandHandler.class, false);
		ServiceLocator.provideService(TickHandler.class, false);
		
		commandHandler = ServiceLocator.getService(CommandHandler.class);
		
		if (serverId == null || serverId.isEmpty() || serverId.equalsIgnoreCase("unnamed") || serverId.equalsIgnoreCase("unknown") || serverId.equalsIgnoreCase("default")) {
			serverId = UUID.randomUUID().toString();
			writeProperties();
		}
		externalIp = getExternalIp();
	}
	
	public void onEnable() {
		ServiceLocator.provideService(EnhancedBungeeMessageHandler.class);
		ServiceLocator.provideService(EventExecutorWrapper.class, false);
		ServiceLocator.provideService(EventListener.class, false);
	}
	public void onDisable() {
		ServiceLocator.getService(IMessageHandler.class).destroy();
		ServiceLocator.getService(EventListener.class).destroy();
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
	
	public final void printInfo(String message) {
		if (consoleSender == null) {
			consoleSender = getServer().getConsoleSender();
		}
		
		if (consoleSender != null) {
			consoleSender.sendMessage(ChatColor.GRAY + "[INFO] " + ChatColor.RESET + message);
		} else {
			logger.info(message);
		}
	}
	public final void printWarning(String message) {
		if (consoleSender == null) {
			consoleSender = getServer().getConsoleSender();
		}
		
		if (consoleSender != null) {
			consoleSender.sendMessage(ChatColor.YELLOW + "[WARN] " + ChatColor.RESET + message);
		} else {
			logger.warning(message);
		}
	}
	public final void printError(String message) {
		if (consoleSender == null) {
			consoleSender = getServer().getConsoleSender();
		}
		
		if (consoleSender != null) {
			consoleSender.sendMessage(ChatColor.RED + "[ERROR] " + ChatColor.RESET + message);
		} else {
			logger.severe(message);
		}
	}
	
	public String getServerIp() {
		return externalIp;
	}
	public String getServerId() {
		return serverId;
	}
	
	//private
	private void reflect(String version, String pkg) {
		reflect(version, pkg, true);
	}
	private void reflect(String version, String pkg, boolean lazyInitialize) {
		Class<Object> bestMatch = VersionUtil.getBestMatch(Object.class, version, pkg, false);
		
		if (bestMatch != null) {
			ServiceLocator.provideService(bestMatch, lazyInitialize);
		}
	}
	
	private void writeProperties() {
		File propertiesFile = new File(Bukkit.getWorldContainer(), "server.properties");
		String path = propertiesFile.getAbsolutePath();
		
		if (!FileUtil.pathExists(path) || !FileUtil.pathIsFile(path)) {
			return;
		}
		
		try {
			FileUtil.open(path);
			
			String[] lines = toString(FileUtil.read(path, 0L), Charset.forName("UTF-8")).replaceAll("\r", "").split("\n");
			boolean found = false;
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].trim().startsWith("server-id=")) {
					found = true;
					lines[i] = "server-id=" + serverId;
				}
			}
			if (!found) {
				ArrayList<String> temp = new ArrayList<String>(Arrays.asList(lines));
				temp.add("server-id=" + serverId);
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
	
	private String getExternalIp() {
		URL url = null;
		BufferedReader in = null;
		
		String[] sites = new String[] {
			"http://checkip.amazonaws.com",
			"https://icanhazip.com/",
			"http://www.trackip.net/ip",
			"http://myexternalip.com/raw",
			"http://ipecho.net/plain",
			"https://bot.whatismyipaddress.com/"
		};
		
		for (String addr : sites) {
			try {
				url = new URL(addr);
				in = new BufferedReader(new InputStreamReader(url.openStream()));
				String ip = in.readLine();
				InetAddress.getByName(ip);
				return ip;
			} catch (Exception ex) {
				continue;
			} finally {
				if (in != null) {
					try {
						in.close();
						in = null;
					} catch (Exception ex) {
						
					}
				}
			}
		}
		
		return null;
	}
}