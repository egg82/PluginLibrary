package ninja.egg82.bungeecord.utils;

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
	
	public static <T> T getObject(String key, Class<T> objectType) {
		if (configRegistry == null) {
			return null;
		}
		return configRegistry.getRegister(key, objectType);
	}
	
	//private
	
}
