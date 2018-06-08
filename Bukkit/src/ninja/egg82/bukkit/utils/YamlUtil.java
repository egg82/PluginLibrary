package ninja.egg82.bukkit.utils;

import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.utils.FileUtil;

public class YamlUtil {
	//vars
	private static JavaPlugin plugin = null;
	
	//constructor
	public YamlUtil() {
		
	}
	
	//public
	public static YamlConfiguration getOrLoadDefaults(File yamlFile, String resourceName) {
		return getOrLoadDefaults(yamlFile.getAbsolutePath(), resourceName);
	}
	public static YamlConfiguration getOrLoadDefaults(String yamlFile, String resourceName) {
		return getOrLoadDefaults(yamlFile, resourceName, false);
	}
	public static YamlConfiguration getOrLoadDefaults(File yamlFile, String resourceName, boolean saveFile) {
		return getOrLoadDefaults(yamlFile.getAbsolutePath(), resourceName, saveFile);
	}
	public static YamlConfiguration getOrLoadDefaults(String yamlFile, String resourceName, boolean saveFile) {
		if (yamlFile == null || resourceName == null) {
			return YamlConfiguration.loadConfiguration(new StringReader(""));
		}
		
		if (plugin == null) {
			plugin = ServiceLocator.getService(JavaPlugin.class);
		}
		
		YamlConfiguration defaultConfig = null;
		try {
			defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(resourceName)));
		} catch (Exception ex) {
			defaultConfig = YamlConfiguration.loadConfiguration(new StringReader(""));
		}
		if (defaultConfig == null) {
			defaultConfig = YamlConfiguration.loadConfiguration(new StringReader(""));
		}
		
		if (FileUtil.pathExists(yamlFile) && !FileUtil.pathIsFile(yamlFile)) {
			return defaultConfig;
		}
		
		File file = new File(yamlFile);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		boolean changed = false;
		for (String key : defaultConfig.getKeys(true)) {
			if (!config.contains(key)) {
				changed = true;
				if (defaultConfig.isConfigurationSection(key)) {
					config.createSection(key);
				} else {
					config.set(key, defaultConfig.get(key));
				}
			}
		}
		
		if (saveFile) {
			if (!FileUtil.pathExists(yamlFile)) {
				plugin.saveResource(resourceName, true);
			} else {
				if (changed) {
					try {
						config.save(file);
					} catch (Exception ex) {
						
					}
				}
			}
		}
		
		return config;
	}
	
	//private
	
}
