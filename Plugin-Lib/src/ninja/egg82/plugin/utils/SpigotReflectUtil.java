package ninja.egg82.plugin.utils;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.EventCommand;
import ninja.egg82.plugin.commands.MessageCommand;
import ninja.egg82.plugin.commands.PluginCommand;
import ninja.egg82.plugin.commands.TickCommand;
import ninja.egg82.plugin.handlers.CommandHandler;
import ninja.egg82.plugin.handlers.MessageHandler;
import ninja.egg82.plugin.handlers.PermissionsManager;
import ninja.egg82.plugin.handlers.TickHandler;
import ninja.egg82.plugin.reflection.event.IEventListener;
import ninja.egg82.utils.ReflectUtil;

public final class SpigotReflectUtil {
	//vars
	private static String gameVersion = null;
	
	//constructor
	public SpigotReflectUtil() {
		
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
	
	public static int addMessagesFromPackage(String packageName) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		int numMessages = 0;
		
		MessageHandler messageHandler = ServiceLocator.getService(MessageHandler.class);
		
		List<Class<? extends MessageCommand>> enums = ReflectUtil.getClasses(MessageCommand.class, packageName);
		for (Class<? extends MessageCommand> c : enums) {
			String pkg = c.getName();
			pkg = pkg.substring(0, pkg.lastIndexOf('.'));
			
			if (!pkg.equalsIgnoreCase(packageName)) {
				continue;
			}
			
			numMessages++;
			messageHandler.addCommand(c);
		}
		
		return numMessages;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int addEventsFromPackage(String packageName) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		int numEvents = 0;
		
		IEventListener eventListener = ServiceLocator.getService(IEventListener.class);
		
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
	
	public static int addPermissionsFromClass(Class<?> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		int numPermissions = 0;
		
		PermissionsManager permissionsManager = ServiceLocator.getService(PermissionsManager.class);
		
		Object[] enums = ReflectUtil.getStaticFields(clazz);
		String[] permissions = Arrays.copyOf(enums, enums.length, String[].class);
		for (String p : permissions) {
			numPermissions++;
			permissionsManager.addPermission(p);
		}
		
		return numPermissions;
	}
	
	public static int addTicksFromPackage(String packageName) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		int numTicks = 0;
		
		TickHandler tickHandler = ServiceLocator.getService(TickHandler.class);
		
		List<Class<? extends TickCommand>> enums = ReflectUtil.getClasses(TickCommand.class, packageName);
		for (Class<? extends TickCommand> t : enums) {
			String pkg = t.getName();
			pkg = pkg.substring(0, pkg.lastIndexOf('.'));
			
			if (!pkg.equalsIgnoreCase(packageName)) {
				continue;
			}
			
			numTicks++;
			tickHandler.addTickCommand(t);
		}
		
		return numTicks;
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
