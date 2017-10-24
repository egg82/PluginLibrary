package ninja.egg82.protocol.utils;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.utils.VersionUtil;
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
	public static void reflect(String version, String pkg) {
		reflect(version, pkg, true);
	}
	public static void reflect(String version, String pkg, boolean lazyInitialize) {
		Class<Object> bestMatch = VersionUtil.getBestMatch(Object.class, version, pkg, false);
		
		if (bestMatch != null) {
			ServiceLocator.provideService(bestMatch, lazyInitialize);
		}
	}
	
	public static void sendPacket(PacketContainer packet, Player player) {
		if (player == null) {
			return;
		}
		
		if (manager == null) {
			manager = ProtocolLibrary.getProtocolManager();
		}
		
		try {
			manager.sendServerPacket(player, packet);
		} catch (Exception ex) {
			
		}
	}
	public static void sendPacket(PacketContainer packet, List<Player> players) {
		if (players == null) {
			throw new ArgumentNullException("players");
		}
		
		sendPacket(packet, players.toArray(new Player[0]));
	}
	public static void sendPacket(PacketContainer packet, Player[] players) {
		if (players == null) {
			throw new ArgumentNullException("players");
		}
		
		if (manager == null) {
			manager = ProtocolLibrary.getProtocolManager();
		}
		
		try {
			for (int i = 0; i < players.length; i++) {
				if (players[i] == null) {
					continue;
				}
				manager.sendServerPacket(players[i], packet);
			}
		} catch (Exception ex) {
			
		}
	}
	
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
