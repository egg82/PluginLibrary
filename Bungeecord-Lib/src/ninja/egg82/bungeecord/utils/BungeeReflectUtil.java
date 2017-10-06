package ninja.egg82.bungeecord.utils;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import net.md_5.bungee.api.plugin.Event;
import ninja.egg82.bungeecord.commands.EventCommand;
import ninja.egg82.bungeecord.commands.PluginCommand;
import ninja.egg82.bungeecord.handlers.CommandHandler;
import ninja.egg82.bungeecord.handlers.EventListener;
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
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		List<Class<?>> services = ReflectUtil.getClasses(Object.class, packageName);
		for (Class<?> service : services) {
			ServiceLocator.provideService(service);
		}
		return services.size();
	}
	
	public static int addCommandsFromPackage(String packageName) {
		return addCommandsFromPackage(packageName, null);
	}
	public static int addCommandsFromPackage(String packageName, Map<String, String[]> aliasMap) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		int numCommands = 0;
		
		CommandHandler commandHandler = ServiceLocator.getService(CommandHandler.class);
		
		List<Class<? extends PluginCommand>> enums = ReflectUtil.getClasses(PluginCommand.class, packageName);
		for (Class<? extends PluginCommand> c : enums) {
			String name = c.getSimpleName().toLowerCase();
			String pkg = c.getName();
			pkg = pkg.substring(0, pkg.lastIndexOf('.'));
			
			if (!pkg.equalsIgnoreCase(packageName)) {
				continue;
			}
			if (name.length() < 7) {
				continue;
			}
			
			numCommands++;
			commandHandler.setCommand((name.substring(name.length() - 7).equals("command")) ? name.substring(0, name.length() - 7) : name, c, (aliasMap != null) ? aliasMap.get(name) : null);
		}
		
		return numCommands;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int addEventsFromPackage(String packageName) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		int numEvents = 0;
		
		EventListener eventListener = ServiceLocator.getService(EventListener.class);
		
		List<Class<? extends EventCommand>> enums = ReflectUtil.getClasses(EventCommand.class, packageName);
		for (Class<? extends EventCommand> c : enums) {
			String pkg = c.getName();
			pkg = pkg.substring(0, pkg.lastIndexOf('.'));
			
			if (!pkg.equalsIgnoreCase(packageName)) {
				continue;
			}
			
			Class<? extends Event> eventType = null;
			try {
				eventType = (Class<? extends Event>) ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments()[0];
			} catch (Exception ex) {
				continue;
			}
			
			numEvents++;
			eventListener.setEvent(eventType, (Class<? extends EventCommand<? extends Event>>) c);
		}
		
		return numEvents;
	}
	
	public static void clearAll() {
		ServiceLocator.getService(CommandHandler.class).clear();
		ServiceLocator.getService(EventListener.class).clear();
	}
	
	//private
	
}
