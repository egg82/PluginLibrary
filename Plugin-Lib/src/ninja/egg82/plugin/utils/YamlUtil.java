package ninja.egg82.plugin.utils;

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
			return YamlConfiguration.loadConfiguration(new StringReader(""));
		}
		if (defaultConfig == null) {
			return YamlConfiguration.loadConfiguration(new StringReader(""));
		}
		
		if (FileUtil.pathExists(yamlFile) && !FileUtil.pathIsFile(yamlFile)) {
			return defaultConfig;
		}
		if (!FileUtil.pathExists(yamlFile)) {
			try {
				FileUtil.createFile(yamlFile);
			} catch (Exception ex) {
				return defaultConfig;
			}
		}
		
		File file = new File(yamlFile);
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.setDefaults(defaultConfig);
		
		if (saveFile) {
			try {
				config.save(file);
			} catch (Exception ex) {
				
			}
		}
		
		return config;
	}
	
	//private
	
}
