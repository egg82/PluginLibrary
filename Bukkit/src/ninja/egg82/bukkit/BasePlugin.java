package ninja.egg82.bukkit;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import ninja.egg82.bukkit.core.BukkitSender;
import ninja.egg82.bukkit.messaging.EnhancedBungeeMessageHandler;
import ninja.egg82.bukkit.processors.CommandProcessor;
import ninja.egg82.bukkit.processors.EventProcessor;
import ninja.egg82.bukkit.services.ConfigRegistry;
import ninja.egg82.bukkit.utils.VersionUtil;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptionHandlers.NullExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.utils.IPUtil;
import ninja.egg82.utils.FileUtil;
import ninja.egg82.utils.ThreadUtil;

public abstract class BasePlugin extends JavaPlugin {
	//vars
	private CommandProcessor commandProcessor = new CommandProcessor();
	
	private String gameVersion = null;
	private String pluginVersion = null;
	private Logger logger = null;
	private CommandSender consoleSender = null;
	
	private volatile String externalIp = null;
	private String serverId = Bukkit.getServerId().trim();
	
	//constructor
	@SuppressWarnings("resource")
	public BasePlugin() {
		super();
		
		logger = getLogger();
		
		ServiceLocator.provideService(this);
		ServiceLocator.provideService(logger);
		
		if (serverId == null || serverId.isEmpty() || serverId.equalsIgnoreCase("unnamed") || serverId.equalsIgnoreCase("unknown") || serverId.equalsIgnoreCase("default")) {
			serverId = UUID.randomUUID().toString();
			writeProperties();
		}
		
		ServiceLocator.provideService(NullExceptionHandler.class);
		logger.addHandler((Handler) ServiceLocator.getService(IExceptionHandler.class));
		
		ThreadUtil.rename(getName());
		ServiceLocator.provideService(new EnhancedBungeeMessageHandler(getName(), serverId));
		
		gameVersion = Bukkit.getVersion();
		gameVersion = gameVersion.substring(gameVersion.indexOf('('));
		gameVersion = gameVersion.substring(gameVersion.indexOf(' ') + 1, gameVersion.length() - 1);
		gameVersion = gameVersion.trim().replace('_', '.');
		
		pluginVersion = getDescription().getVersion();
	}
	
	//public
	public String getGameVersion() {
		return gameVersion;
	}
	public String getPluginVersion() {
		return pluginVersion;
	}
	
	public void onLoad() {
		ServiceLocator.provideService(getClassLoader());
		
		consoleSender = getServer().getConsoleSender();
		
		reflect(gameVersion, "ninja.egg82.bukkit.reflection.player");
		reflect(gameVersion, "ninja.egg82.bukkit.reflection.entity");
		
		ServiceLocator.provideService(ConfigRegistry.class, false);
		
		ServiceLocator.provideService(commandProcessor);
	}
	
	public void onEnable() {
		ServiceLocator.provideService(EventProcessor.class, false);
	}
	public void onDisable() {
		ServiceLocator.getService(EventProcessor.class).close();
	}
	
	public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (commandProcessor.hasCommand(command.getName())) {
			commandProcessor.runHandlers(new BukkitSender(sender), command.getName(), args);
			return true;
		}
		return false;
	}
	public final List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		Collection<String> retVal = commandProcessor.tabComplete(new BukkitSender(sender), command.getName(), args);
		if (retVal instanceof List) {
			return (List<String>) retVal;
		}
		return new ArrayList<String>(retVal);
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
		if (externalIp == null) {
			externalIp = IPUtil.getExternalIp();
		}
		return externalIp;
	}
	public final String getServerId() {
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
}