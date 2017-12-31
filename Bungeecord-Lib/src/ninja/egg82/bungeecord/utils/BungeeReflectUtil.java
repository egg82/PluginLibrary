package ninja.egg82.bungeecord.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ninja.egg82.bungeecord.handlers.CommandHandler;
import ninja.egg82.bungeecord.handlers.EventListener;
import ninja.egg82.bungeecord.handlers.IMessageHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.utils.ReflectUtil;

public final class BungeeReflectUtil {
	//vars
	
	//constructor
	public BungeeReflectUtil() {
		
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
		ServiceLocator.getService(IMessageHandler.class).clearCommands();
		ServiceLocator.getService(IMessageHandler.class).clearChannels();
		ServiceLocator.getService(CommandHandler.class).clear();
		ServiceLocator.getService(EventListener.class).clear();
	}
	
	//private
	
}
