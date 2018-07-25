package ninja.egg82.bungeecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import ninja.egg82.bungeecord.processors.CommandProcessor;
import ninja.egg82.bungeecord.processors.EventProcessor;
import ninja.egg82.bungeecord.messaging.EnhancedBungeeMessageHandler;
import ninja.egg82.bungeecord.reflection.redisBungee.NullRedisBungeeHelper;
import ninja.egg82.bungeecord.reflection.redisBungee.RedisBungeeHelper;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptionHandlers.NullExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.utils.FileUtil;
import ninja.egg82.utils.ThreadUtil;

public abstract class BasePlugin extends Plugin {
	//vars
	private Logger logger = null;
	private CommandSender consoleSender = null;
	
	private String pluginVersion = null;
	private volatile String externalIp = null;
	private String serverId = null;
	
	//constructor
	public BasePlugin() {
		super();
		
		ServiceLocator.provideService(this);
		ServiceLocator.provideService(NullExceptionHandler.class);
	}
	
	//public
	public String getPluginVersion() {
		return pluginVersion;
	}
	
	public void onLoad() {
		serverId = getId();
		if (serverId == null || serverId.isEmpty() || serverId.equalsIgnoreCase("unnamed") || serverId.equalsIgnoreCase("unknown") || serverId.equalsIgnoreCase("default")) {
			serverId = UUID.randomUUID().toString();
			writeProperties();
		}
		
		ThreadUtil.rename(getDescription().getName());
		pluginVersion = getDescription().getVersion();
		
		logger = getLogger();
		ServiceLocator.provideService(logger);
		logger.addHandler((Handler) ServiceLocator.getService(IExceptionHandler.class));
		
		ServiceLocator.provideService(getClass().getClassLoader());
		
		consoleSender = getProxy().getConsole();
		
		ServiceLocator.provideService(CommandProcessor.class, false);
	}
	
	@SuppressWarnings("resource")
	public void onEnable() {
		ServiceLocator.provideService(new EnhancedBungeeMessageHandler(getDescription().getName(), serverId));
		PluginManager manager = getProxy().getPluginManager();
		
		if (manager.getPlugin("RedisBungee") != null) {
			ServiceLocator.provideService(RedisBungeeHelper.class);
		} else {
			ServiceLocator.provideService(NullRedisBungeeHelper.class);
		}
		
		ServiceLocator.provideService(EventProcessor.class, false);
	}
	public void onDisable() {
		ServiceLocator.getService(EventProcessor.class).close();
	}
	
	public final void printInfo(String message) {
		if (consoleSender == null) {
			consoleSender = getProxy().getConsole();
		}
		
		if (consoleSender != null) {
			consoleSender.sendMessage(new TextComponent(ChatColor.GRAY + "[INFO] " + ChatColor.WHITE + "[" + getDescription().getName() + "] " + ChatColor.RESET + message));
		} else {
			logger.info(message);
		}
	}
	public final void printWarning(String message) {
		if (consoleSender == null) {
			consoleSender = getProxy().getConsole();
		}
		
		if (consoleSender != null) {
			consoleSender.sendMessage(new TextComponent(ChatColor.YELLOW + "[WARN] " + ChatColor.WHITE + "[" + getDescription().getName() + "] " + ChatColor.RESET + message));
		} else {
			logger.warning(message);
		}
	}
	public final void printError(String message) {
		if (consoleSender == null) {
			consoleSender = getProxy().getConsole();
		}
		
		if (consoleSender != null) {
			consoleSender.sendMessage(new TextComponent(ChatColor.RED + "[ERROR] " + ChatColor.WHITE + "[" + getDescription().getName() + "] " + ChatColor.RESET + message));
		} else {
			logger.severe(message);
		}
	}
	
	public String getServerIp() {
		if (externalIp == null) {
			externalIp = getExternalIp();
		}
		return externalIp;
	}
	public final String getServerId() {
		return serverId;
	}
	
	//private
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
