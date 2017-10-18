package ninja.egg82.protocol.utils;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.protocol.commands.ProtocolEventCommand;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public class ProtocolReflectUtil {
	//vars
	private static ConcurrentHashMap<Class<ProtocolEventCommand>, ProtocolEventCommand> events = new ConcurrentHashMap<Class<ProtocolEventCommand>, ProtocolEventCommand>();
	private static ProtocolManager manager = null;
	
	//constructor
	public ProtocolReflectUtil() {
		
	}
	
	//public
	public static boolean addEventHandler(Class<ProtocolEventCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		if (events.containsKey(clazz)) {
			return false;
		}
		
		ProtocolEventCommand run = getEvent(clazz);
		if (run == null) {
			return false;
		}
		
		if (manager == null) {
			manager = ProtocolLibrary.getProtocolManager();
		}
		
		if (CollectionUtil.putIfAbsent(events, clazz, run).hashCode() == run.hashCode()) {
			manager.addPacketListener(run);
			return true;
		}
		return false;
	}
	public static boolean removeEventHandler(Class<ProtocolEventCommand> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		ProtocolEventCommand run = events.remove(clazz);
		if (run == null) {
			return false;
		}
		
		if (manager == null) {
			manager = ProtocolLibrary.getProtocolManager();
		}
		manager.removePacketListener(run);
		return true;
	}
	
	public static int addEventsFromPackage(String packageName, boolean recursive) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		int numEvents = 0;
		
		if (manager == null) {
			manager = ProtocolLibrary.getProtocolManager();
		}
		
		List<Class<ProtocolEventCommand>> enums = ReflectUtil.getClasses(ProtocolEventCommand.class, packageName, recursive, false, false);
		for (Class<ProtocolEventCommand> c : enums) {
			ProtocolEventCommand run = getEvent(c);
			if (run == null) {
				continue;
			}
			if (CollectionUtil.putIfAbsent(events, c, run).hashCode() != run.hashCode()) {
				continue;
			}
			
			numEvents++;
			manager.addPacketListener(run);
		}
		
		return numEvents;
	}
	
	public static void clear() {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		for (Entry<Class<ProtocolEventCommand>, ProtocolEventCommand> kvp : events.entrySet()) {
			manager.removePacketListener(kvp.getValue());
		}
		events.clear();
	}
	
	//private
	private static ProtocolEventCommand getEvent(Class<ProtocolEventCommand> clazz) {
		ProtocolEventCommand run = null;
		
		try {
			run = clazz.newInstance();
		} catch (Exception ex) {
			return null;
		}
		
		return run;
	}
}
