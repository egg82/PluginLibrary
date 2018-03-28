package ninja.egg82.plugin.utils;

import ninja.egg82.patterns.registries.IRegistry;

public class LanguageUtil {
	//vars
	private static IRegistry<String, String> languageRegistry = null;
	
	//constructor
	public LanguageUtil() {
		
	}
	
	//public
	public static void setRegistry(IRegistry<String, String> newRegistry) {
		languageRegistry = newRegistry;
	}
	public static IRegistry<String, String> getRegistry() {
		return languageRegistry;
	}
	
	public static String getString(String key) {
		if (languageRegistry == null) {
			return null;
		}
		return languageRegistry.getRegister(key);
	}
	
	//private
	
}
