package ninja.egg82.plugin.utils;

import org.bukkit.configuration.file.YamlConfiguration;

import ninja.egg82.patterns.IRegistry;

public class ConfigUtil {
	//vars
	private static IRegistry<String> configRegistry = null;
	
	//constructor
	public ConfigUtil() {
		
	}
	
	//public
	public static void setRegistry(IRegistry<String> newRegistry) {
		configRegistry = newRegistry;
	}
	public static IRegistry<String> getRegistry() {
		return configRegistry;
	}
	
	public static void fillRegistry(YamlConfiguration config) {
		if (configRegistry == null) {
			return;
		}
		
		for (String key : config.getKeys(true)) {
			if (!config.isConfigurationSection(key)) {
				configRegistry.setRegister(key, config.get(key));
			}
		}
	}
	public static YamlConfiguration toConfig() {
		if (configRegistry == null) {
			return null;
		}
		
		YamlConfiguration retVal = new YamlConfiguration();
		for (String key : configRegistry.getKeys()) {
			retVal.set(key, configRegistry.getRegister(key));
		}
		return retVal;
	}
	
	//private
	
}
