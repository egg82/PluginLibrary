package ninja.egg82.protocol.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.protocol.commands.ProtocolEventCommand;
import ninja.egg82.utils.ReflectUtil;

public class ProtocolReflectUtil {
	//vars
	private static List<Class<? extends ProtocolEventCommand>> events = Collections.synchronizedList(new ArrayList<Class<? extends ProtocolEventCommand>>());
	private static List<ProtocolEventCommand> initializedEvents = Collections.synchronizedList(new ArrayList<ProtocolEventCommand>());
	
	//constructor
	public ProtocolReflectUtil() {
		
	}
	
	//public
	public static void addProtocolEvent(Class<? extends ProtocolEventCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		ProtocolEventCommand run = getEvent(clazz);
		if (run == null) {
			return;
		}
		
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(run);
		
		synchronized (events) {
			events.add(clazz);
		}
		synchronized (initializedEvents) {
			initializedEvents.add(run);
		}
	}
	public static void removeProtocolEvent(Class<? extends ProtocolEventCommand> clazz) {
		int index = -1;
		
		synchronized (events) {
			index = events.indexOf(clazz);
			if (index != -1) {
				events.remove(index);
			}
		}
		if (index == -1) {
			return;
		}
		
		ProtocolEventCommand eventCommand = null;
		synchronized (initializedEvents) {
			eventCommand = initializedEvents.remove(index);
		}
		
		if (eventCommand == null) {
			return;
		}
		ProtocolLibrary.getProtocolManager().removePacketListener(eventCommand);
	}
	
	public static int addProtocolEventsFromPackage(String packageName) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		int numEvents = 0;
		
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		
		List<Class<? extends ProtocolEventCommand>> enums = ReflectUtil.getClasses(ProtocolEventCommand.class, packageName);
		synchronized (events) {
			synchronized (initializedEvents) {
				for (Class<? extends ProtocolEventCommand> c : enums) {
					String pkg = c.getName();
					pkg = pkg.substring(0, pkg.lastIndexOf('.'));
					
					if (!pkg.equalsIgnoreCase(packageName)) {
						continue;
					}
					
					ProtocolEventCommand run = getEvent(c);
					if (run == null) {
						continue;
					}
					
					numEvents++;
					events.add(c);
					initializedEvents.add(run);
					manager.addPacketListener(run);
				}
			}
		}
		
		return numEvents;
	}
	
	public static void clear() {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		synchronized (events) {
			synchronized (initializedEvents) {
				for (ProtocolEventCommand e : initializedEvents) {
					manager.removePacketListener(e);
				}
				initializedEvents.clear();
			}
			events.clear();
		}
	}
	
	//private
	private static ProtocolEventCommand getEvent(Class<? extends ProtocolEventCommand> clazz) {
		ProtocolEventCommand run = null;
		
		try {
			run = clazz.getDeclaredConstructor().newInstance();
		} catch (Exception ex) {
			return null;
		}
		
		return run;
	}
}
