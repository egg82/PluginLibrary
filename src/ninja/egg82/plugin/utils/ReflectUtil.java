package ninja.egg82.plugin.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.event.Event;

import ninja.egg82.plugin.commands.EventCommand;
import ninja.egg82.plugin.commands.PluginCommand;
import ninja.egg82.plugin.commands.TickCommand;
import ninja.egg82.plugin.utils.interfaces.ICommandHandler;
import ninja.egg82.plugin.utils.interfaces.IEventListener;
import ninja.egg82.plugin.utils.interfaces.IPermissionsManager;
import ninja.egg82.plugin.utils.interfaces.ITickHandler;
import ninja.egg82.utils.Util;

public class ReflectUtil {
	//vars
	
	//constructor
	public ReflectUtil() {
		
	}
	
	//public
	public static int addCommandsFromPackage(ICommandHandler commandHandler, String commandsPackage) {
		int numCommands = 0;
		
		ArrayList<Class<? extends PluginCommand>> enums = Util.getClasses(PluginCommand.class, commandsPackage);
		for (Class<? extends PluginCommand> c : enums) {
			String name = c.getSimpleName();
			String pkg = c.getName();
			pkg = pkg.substring(0, pkg.lastIndexOf('.'));
			
			if (!pkg.equalsIgnoreCase(commandsPackage)) {
				continue;
			}
			
			numCommands++;
			commandHandler.addCommand(name.substring(0, name.length() - 7).toLowerCase(), c);
		}
		
		return numCommands;
	}
	
	@SuppressWarnings("unchecked")
	public static int addEventsFromPackage(IEventListener eventListener, String eventsPackage) {
		int numEvents = 0;
		
		ArrayList<Class<? extends EventCommand>> enums = Util.getClasses(EventCommand.class, eventsPackage);
		Class<? extends Event> c2 = null;
		for (Class<? extends EventCommand> c : enums) {
			String name = c.getSimpleName();
			String pkg = c.getName();
			pkg = pkg.substring(0, pkg.lastIndexOf('.'));
			
			if (!pkg.equalsIgnoreCase(eventsPackage)) {
				continue;
			}
			
			String eventName = name.substring(0, name.length() - 7);
			
			c2 = null;
			c2 = (Class<? extends Event>) Util.getClassFromName("org.bukkit.event.block." + eventName);
			if (c2 == null) {
				c2 = (Class<? extends Event>) Util.getClassFromName("org.bukkit.event.enchantment." + eventName);
				if (c2 == null) {
					c2 = (Class<? extends Event>) Util.getClassFromName("org.bukkit.event.entity." + eventName);
					if (c2 == null) {
						c2 = (Class<? extends Event>) Util.getClassFromName("org.bukkit.event.hanging." + eventName);
						if (c2 == null) {
							c2 = (Class<? extends Event>) Util.getClassFromName("org.bukkit.event.inventory." + eventName);
							if (c2 == null) {
								c2 = (Class<? extends Event>) Util.getClassFromName("org.bukkit.event.player." + eventName);
								if (c2 == null) {
									c2 = (Class<? extends Event>) Util.getClassFromName("org.bukkit.event.server." + eventName);
									if (c2 == null) {
										c2 = (Class<? extends Event>) Util.getClassFromName("org.bukkit.event.vehicle." + eventName);
										if (c2 == null) {
											c2 = (Class<? extends Event>) Util.getClassFromName("org.bukkit.event.weather." + eventName);
											if (c2 == null) {
												c2 = (Class<? extends Event>) Util.getClassFromName("org.bukkit.event.world." + eventName);
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
		
		Object[] enums = Util.getStaticFields(clazz);
		String[] permissions = Arrays.copyOf(enums, enums.length, String[].class);
		for (String p : permissions) {
			numPermissions++;
			permissionsManager.addPermission(p);
		}
		
		return numPermissions;
	}
	
	public static int addTicksFromPackage(ITickHandler tickHandler, String ticksPackage) {
		int numTicks = 0;
		
		ArrayList<Class<? extends TickCommand>> enums = Util.getClasses(TickCommand.class, ticksPackage);
		for (Class<? extends TickCommand> t : enums) {
			String name = t.getSimpleName();
			String pkg = t.getName();
			pkg = pkg.substring(0, pkg.lastIndexOf('.'));
			
			if (!pkg.equalsIgnoreCase(ticksPackage)) {
				continue;
			}
			
			numTicks++;
			tickHandler.addTickCommand(name.substring(0, name.length() - 11).toLowerCase(), t);
		}
		
		return numTicks;
	}
	
	//private
	
}
