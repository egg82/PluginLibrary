package ninja.egg82.plugin.utils;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import ninja.egg82.patterns.ExpiringRegistry;
import ninja.egg82.patterns.IRegistry;
import ninja.egg82.utils.ReflectUtil;

public class EventUtil {
	//vars
	private static IRegistry<Event> lastEvents = new ExpiringRegistry<Event>(Event.class, 750L);
	
	//constructor
	public EventUtil() {
		
	}
	
	//public
	public static boolean isDuplicate(String className, Event event) {
		if (className.equals("org.bukkit.event.block.BlockBreakEvent")) {
			BlockBreakEvent lastBlockBreak = getKey(className, BlockBreakEvent.class);
			BlockBreakEvent e = (BlockBreakEvent) event;
			
			if (lastBlockBreak == null) {
				lastEvents.setRegister(event, className);
				return false;
			} else {
				if (!e.getBlock().getLocation().equals(lastBlockBreak.getBlock().getLocation())) {
					lastEvents.setRegister(event, className);
					return false;
				}
				if (e.getPlayer() == null && lastBlockBreak.getPlayer() == null) {
					lastEvents.setRegister(event, className);
					return true;
				} else if (e.getPlayer() == null && lastBlockBreak.getPlayer() != null) {
					lastEvents.setRegister(event, className);
					return false;
				} else if (e.getPlayer() != null && lastBlockBreak.getPlayer() == null) {
					lastEvents.setRegister(event, className);
					return false;
				} else {
					lastEvents.setRegister(event, className);
					return e.getPlayer().getUniqueId().equals(lastBlockBreak.getPlayer().getUniqueId());
				}
			}
		}
		
		return false;
	}
	
	//private
	@SuppressWarnings("unchecked")
	private static <T extends Event> T getKey(String name, Class<T> type) {
		for (Event key : lastEvents.getKeys()) {
			if (name.equals(lastEvents.getRegister(key))) {
				if (!ReflectUtil.doesExtend(type, key.getClass())) {
					try {
						return type.cast(key);
					} catch (Exception ex) {
						throw new RuntimeException("key type cannot be converted to the type specified.", ex);
					}
				} else {
					return (T) key;
				}
			}
		}
		return null;
	}
}
