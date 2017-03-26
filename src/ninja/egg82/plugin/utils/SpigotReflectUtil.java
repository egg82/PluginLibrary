package ninja.egg82.plugin.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.event.Event;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.EventCommand;
import ninja.egg82.plugin.commands.PluginCommand;
import ninja.egg82.plugin.commands.TickCommand;
import ninja.egg82.utils.ReflectUtil;

public class SpigotReflectUtil {
	//vars
	
	//constructor
	public SpigotReflectUtil() {
		
	}
	
	//public
	public static int addServicesFromPackage(String packageName) {
		ArrayList<Class<?>> services = ReflectUtil.getClasses(Object.class, packageName);
		for (Class<?> service : services) {
			ServiceLocator.provideService(service);
		}
		return services.size();
	}
	
	public static int addCommandsFromPackage(ICommandHandler commandHandler, String packageName) {
		int numCommands = 0;
		
		ArrayList<Class<? extends PluginCommand>> enums = ReflectUtil.getClasses(PluginCommand.class, packageName);
		for (Class<? extends PluginCommand> c : enums) {
			String name = c.getSimpleName();
			String pkg = c.getName();
			pkg = pkg.substring(0, pkg.lastIndexOf('.'));
			
			if (!pkg.equalsIgnoreCase(packageName)) {
				continue;
			}
			
			numCommands++;
			commandHandler.addCommand(name.substring(0, name.length() - 7).toLowerCase(), c);
		}
		
		return numCommands;
	}
	
	@SuppressWarnings("unchecked")
	public static int addEventsFromPackage(IEventListener eventListener, String packageName) {
		int numEvents = 0;
		
		ArrayList<Class<? extends EventCommand>> enums = ReflectUtil.getClasses(EventCommand.class, packageName);
		Class<? extends Event> c2 = null;
		for (Class<? extends EventCommand> c : enums) {
			String name = c.getSimpleName();
			String pkg = c.getName();
			pkg = pkg.substring(0, pkg.lastIndexOf('.'));
			
			if (!pkg.equalsIgnoreCase(packageName)) {
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
			eventListener.addEvent(c2, c);
		}
		
		return numEvents;
	}
	
	public static int addPermissionsFromClass(IPermissionsManager permissionsManager, Class<?> clazz) {
		int numPermissions = 0;
		
		Object[] enums = ReflectUtil.getStaticFields(clazz);
		String[] permissions = Arrays.copyOf(enums, enums.length, String[].class);
		for (String p : permissions) {
			numPermissions++;
			permissionsManager.addPermission(p);
		}
		
		return numPermissions;
	}
	
	public static int addTicksFromPackage(ITickHandler tickHandler, String packageName) {
		int numTicks = 0;
		
		ArrayList<Class<? extends TickCommand>> enums = ReflectUtil.getClasses(TickCommand.class, packageName);
		for (Class<? extends TickCommand> t : enums) {
			String name = t.getSimpleName();
			String pkg = t.getName();
			pkg = pkg.substring(0, pkg.lastIndexOf('.'));
			
			if (!pkg.equalsIgnoreCase(packageName)) {
				continue;
			}
			
			numTicks++;
			tickHandler.addTickCommand(name.substring(0, name.length() - 11).toLowerCase(), t);
		}
		
		return numTicks;
	}
	
	//private
	
}
