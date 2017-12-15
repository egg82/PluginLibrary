package ninja.egg82.bungeecord.utils;

import net.md_5.bungee.config.Configuration;
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
	
	public static void fillRegistry(Configuration config) {
		if (configRegistry == null) {
			return;
		}
		
		deepSet("", config);
	}
	public static Configuration toConfig() {
		if (configRegistry == null) {
			return null;
		}
		
		Configuration retVal = new Configuration();
		for (String key : configRegistry.getKeys()) {
			retVal.set(key, configRegistry.getRegister(key));
		}
		return retVal;
	}
	
	//private
	private static void deepSet(String currentPath, Configuration config) {
		for (String key : config.getKeys()) {
			if (config.get(key) != null) {
				if (config.get(key) instanceof Configuration) {
					deepSet(currentPath + key + ".", config.getSection(key));
				} else {
					configRegistry.setRegister(currentPath + key, config.get(key));
				}
			}
		}
	}
}
