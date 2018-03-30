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
import ninja.egg82.bungeecord.enums.BungeeInitType;
import ninja.egg82.bungeecord.handlers.EnhancedBungeeMessageHandler;
import ninja.egg82.bungeecord.handlers.CommandHandler;
import ninja.egg82.bungeecord.handlers.EventListener;
import ninja.egg82.bungeecord.handlers.IMessageHandler;
import ninja.egg82.bungeecord.reflection.offlineplayer.NullRedisBungeeHelper;
import ninja.egg82.bungeecord.reflection.offlineplayer.RedisBungeeHelper;
import ninja.egg82.bungeecord.services.ConfigRegistry;
import ninja.egg82.bungeecord.services.LanguageRegistry;
import ninja.egg82.bungeecord.utils.ConfigUtil;
import ninja.egg82.bungeecord.utils.LanguageUtil;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptionHandlers.NullExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.startup.InitRegistry;
import ninja.egg82.startup.Start;
import ninja.egg82.utils.FileUtil;

public abstract class BasePlugin extends Plugin {
	//vars
	private Logger logger = null;
	private CommandSender consoleSender = null;
	
	private volatile String externalIp = null;
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
		
		IVariableRegistry<String> initRegistry = ServiceLocator.getService(InitRegistry.class);
		initRegistry.setRegister(BungeeInitType.PLUGIN_VERSION, getDescription().getVersion());
		
		ServiceLocator.provideService(ConfigRegistry.class, false);
		ConfigUtil.setRegistry(ServiceLocator.getService(ConfigRegistry.class));
		ServiceLocator.provideService(LanguageRegistry.class, false);
		LanguageUtil.setRegistry(ServiceLocator.getService(LanguageRegistry.class));
		
		ServiceLocator.provideService(CommandHandler.class, false);
		
		serverId = getId();
		if (serverId == null || serverId.isEmpty() || serverId.equalsIgnoreCase("unnamed") || serverId.equalsIgnoreCase("unknown") || serverId.equalsIgnoreCase("default")) {
			serverId = UUID.randomUUID().toString();
			writeProperties();
		}
		
		consoleSender = getProxy().getConsole();
	}
	
	public void onEnable() {
		PluginManager manager = getProxy().getPluginManager();
		
		if (manager.getPlugin("RedisBungee") != null) {
			printInfo(ChatColor.GREEN + "[BungeeLib] Enabling support for RedisBungee.");
			ServiceLocator.provideService(RedisBungeeHelper.class);
		} else {
			printWarning(ChatColor.YELLOW + "[BungeeLib] RedisBungee was not found. Skipping support for it.");
			ServiceLocator.provideService(NullRedisBungeeHelper.class);
		}
		
		ServiceLocator.provideService(EnhancedBungeeMessageHandler.class);
		ServiceLocator.provideService(EventListener.class, false);
	}
	public void onDisable() {
		try {
			ServiceLocator.getService(IMessageHandler.class).close();
		} catch (Exception ex) {
			
		}
		ServiceLocator.getService(EventListener.class).close();
	}
	
	public String getServerIp() {
		if (externalIp == null) {
			externalIp = getExternalIp();
		}
		return externalIp;
	}
	public String getServerId() {
		return serverId;
	}
	
	//private
	protected final void printInfo(String message) {
		if (consoleSender == null) {
			consoleSender = getProxy().getConsole();
		}
		
		if (consoleSender != null) {
			consoleSender.sendMessage(new TextComponent(ChatColor.GRAY + "[INFO] " + ChatColor.RESET + message));
		} else {
			logger.info(message);
		}
	}
	protected final void printWarning(String message) {
		if (consoleSender == null) {
			consoleSender = getProxy().getConsole();
		}
		
		if (consoleSender != null) {
			consoleSender.sendMessage(new TextComponent(ChatColor.YELLOW + "[WARN] " + ChatColor.RESET + message));
		} else {
			logger.warning(message);
		}
	}
	protected final void printError(String message) {
		if (consoleSender == null) {
			consoleSender = getProxy().getConsole();
		}
		
		if (consoleSender != null) {
			consoleSender.sendMessage(new TextComponent(ChatColor.RED + "[ERROR] " + ChatColor.RESET + message));
		} else {
			logger.severe(message);
		}
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
