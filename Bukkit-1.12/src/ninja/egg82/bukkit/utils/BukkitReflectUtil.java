package ninja.egg82.bukkit.utils;

import org.bukkit.Bukkit;

public final class BukkitReflectUtil {
	//vars
	private static String gameVersion = null;
	
	//constructor
	public BukkitReflectUtil() {
		
	}
	
	//public
	public static Class<?> getNms(String className) {
		if (className == null) {
			throw new IllegalArgumentException("className cannot be null.");
		}
		
		if (gameVersion == null) {
			gameVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		}
		
		try {
			return Class.forName("net.minecraft.server." + gameVersion + "." + className);
		} catch (Exception ex) {
			return null;
		}
	}
	
	//private
	
}
