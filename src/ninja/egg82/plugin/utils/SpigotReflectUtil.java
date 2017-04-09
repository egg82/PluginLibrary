package ninja.egg82.plugin.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.event.Event;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.EventCommand;
import ninja.egg82.plugin.commands.PluginCommand;
import ninja.egg82.plugin.commands.TickCommand;
import ninja.egg82.plugin.handlers.CommandHandler;
import ninja.egg82.plugin.handlers.EventListener;
import ninja.egg82.plugin.handlers.PermissionsManager;
import ninja.egg82.plugin.handlers.TickHandler;
import ninja.egg82.utils.ReflectUtil;

public final class SpigotReflectUtil {
	//vars
	
	//constructor
	public SpigotReflectUtil() {
		
	}
	
	//public
	public static int addServicesFromPackage(String packageName) {
		List<Class<?>> services = ReflectUtil.getClasses(Object.class, packageName);
		for (Class<?> service : services) {
			ServiceLocator.provideService(service);
		}
		return services.size();
	}
	
	public static int addCommandsFromPackage(String packageName) {
		int numCommands = 0;
		
		CommandHandler commandHandler = (CommandHandler) ServiceLocator.getService(CommandHandler.class);
		
		List<Class<? extends PluginCommand>> enums = ReflectUtil.getClasses(PluginCommand.class, packageName);
		for (Class<? extends PluginCommand> c : enums) {
			String name = c.getSimpleName();
			String pkg = c.getName();
			pkg = pkg.substring(0, pkg.lastIndexOf('.'));
			
			if (!pkg.equalsIgnoreCase(packageName)) {
				continue;
			}
			if (name.length() < 7) {
				continue;
			}
			
			numCommands++;
			commandHandler.setCommand(name.substring(0, name.length() - 7).toLowerCase(), c);
		}
		
		return numCommands;
	}
	
	@SuppressWarnings("unchecked")
	public static int addEventsFromPackage(String packageName) {
		int numEvents = 0;
		
		EventListener eventListener = (EventListener) ServiceLocator.getService(EventListener.class);
		
		List<Class<? extends EventCommand>> enums = ReflectUtil.getClasses(EventCommand.class, packageName);
		Class<? extends Event> c2 = null;
		for (Class<? extends EventCommand> c : enums) {
			String name = c.getSimpleName();
			String pkg = c.getName();
			pkg = pkg.substring(0, pkg.lastIndexOf('.'));
			
			if (!pkg.equalsIgnoreCase(packageName)) {
				continue;
			}
			if (name.length() < 7) {
				continue;
			}
			
			String eventName = name.substring(0, name.length() - 7);
			
			c2 = null;
			c2 = (Class<? extends Event>) ReflectUtil.getClassFromName("org.bukkit.event.block." + eventName);
			if (c2 == null) {
				c2 = (Class<? extends Event>) ReflectUtil.getClassFromName("org.bukkit.event.enchantment." + eventName);
				if (c2 == null) {
					c2 = (Class<? extends Event>) ReflectUtil.getClassFromName("org.bukkit.event.entity." + eventName);
					if (c2 == null) {
						c2 = (Class<? extends Event>) ReflectUtil.getClassFromName("org.bukkit.event.hanging." + eventName);
						if (c2 == null) {
							c2 = (Class<? extends Event>) ReflectUtil.getClassFromName("org.bukkit.event.inventory." + eventName);
							if (c2 == null) {
								c2 = (Class<? extends Event>) ReflectUtil.getClassFromName("org.bukkit.event.player." + eventName);
								if (c2 == null) {
									c2 = (Class<? extends Event>) ReflectUtil.getClassFromName("org.bukkit.event.server." + eventName);
									if (c2 == null) {
										c2 = (Class<? extends Event>) ReflectUtil.getClassFromName("org.bukkit.event.vehicle." + eventName);
										if (c2 == null) {
											c2 = (Class<? extends Event>) ReflectUtil.getClassFromName("org.bukkit.event.weather." + eventName);
											if (c2 == null) {
												c2 = (Class<? extends Event>) ReflectUtil.getClassFromName("org.bukkit.event.world." + eventName);
												if (c2 == null) {
													continue;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			numEvents++;
			eventListener.setEvent(c2, c);
		}
		
		return numEvents;
	}
	
	public static int addPermissionsFromClass(Class<?> clazz) {
		int numPermissions = 0;
		
		PermissionsManager permissionsManager = (PermissionsManager) ServiceLocator.getService(PermissionsManager.class);
		
		Object[] enums = ReflectUtil.getStaticFields(clazz);
		String[] permissions = Arrays.copyOf(enums, enums.length, String[].class);
		for (String p : permissions) {
			numPermissions++;
			permissionsManager.addPermission(p);
		}
		
		return numPermissions;
	}
	
	public static int addTicksFromPackage(String packageName) {
		int numTicks = 0;
		
		TickHandler tickHandler = (TickHandler) ServiceLocator.getService(TickHandler.class);
		
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
		((CommandHandler) ServiceLocator.getService(CommandHandler.class)).clear();
		((EventListener) ServiceLocator.getService(EventListener.class)).clear();
		((PermissionsManager) ServiceLocator.getService(PermissionsManager.class)).clear();
		((TickHandler) ServiceLocator.getService(TickHandler.class)).clear();
	}
	
	//private
	
}
