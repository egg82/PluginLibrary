package ninja.egg82.plugin.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.handlers.CommandHandler;
import ninja.egg82.plugin.handlers.MessageHandler;
import ninja.egg82.plugin.handlers.PermissionsManager;
import ninja.egg82.plugin.handlers.TickHandler;
import ninja.egg82.plugin.reflection.event.IEventListener;
import ninja.egg82.utils.ReflectUtil;

public final class BukkitReflectUtil {
	//vars
	private static String gameVersion = null;
	
	//constructor
	public BukkitReflectUtil() {
		
	}
	
	//public
	public static int addServicesFromPackage(String packageName) {
		return addServicesFromPackage(packageName, true);
	}
	public static int addServicesFromPackage(String packageName, boolean recursive) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		List<Class<Object>> services = ReflectUtil.getClasses(Object.class, packageName, recursive, false, false);
		for (Class<Object> service : services) {
			ServiceLocator.provideService(service);
		}
		return services.size();
	}
	
	public static Map<String, String> getCommandMapFromPackage(String pkg, String classNamePrefix, String classNameSuffix) {
		return getCommandMapFromPackage(pkg, true, classNamePrefix, classNameSuffix);
	}
	public static Map<String, String> getCommandMapFromPackage(String pkg, boolean recursive, String classNamePrefix, String classNameSuffix) {
		if (pkg == null) {
			throw new ArgumentNullException("pkg");
		}
		
		if (classNamePrefix == null) {
			classNamePrefix = "";
		} else {
			classNamePrefix = classNamePrefix.toLowerCase();
		}
		if (classNameSuffix == null) {
			classNameSuffix = "";
		} else {
			classNameSuffix = classNameSuffix.toLowerCase();
		}
		
		HashMap<String, String> retVal = new HashMap<String, String>();
		int minLength = classNamePrefix.length() + classNameSuffix.length();
		
		List<Class<Object>> commands = ReflectUtil.getClasses(Object.class, pkg, recursive, false, false);
		for (Class<Object> c : commands) {
			String n = c.getSimpleName().toLowerCase();
			String p = c.getName();
			p = p.substring(0, p.lastIndexOf('.'));
			
			if (n.length() <= minLength) {
				continue;
			}
			
			String cn = n;
			if (!classNamePrefix.isEmpty()) {
				if (!cn.startsWith(classNamePrefix)) {
					continue;
				}
				cn = cn.substring(classNamePrefix.length());
			}
			if (!classNameSuffix.isEmpty()) {
				if (!cn.endsWith(classNameSuffix)) {
					continue;
				}
				cn = cn.substring(0, cn.length() - classNameSuffix.length());
			}
			
			retVal.put(p + "." + n, cn);
		}
		
		return retVal;
	}
	
	public static void clearAll() {
		ServiceLocator.getService(MessageHandler.class).clearCommands();
		ServiceLocator.getService(MessageHandler.class).clearChannels();
		ServiceLocator.getService(CommandHandler.class).clear();
		ServiceLocator.getService(IEventListener.class).clear();
		ServiceLocator.getService(PermissionsManager.class).clear();
		ServiceLocator.getService(TickHandler.class).clear();
	}
	
	public static Class<?> getNms(String className) {
		if (className == null) {
			throw new ArgumentNullException("className");
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
