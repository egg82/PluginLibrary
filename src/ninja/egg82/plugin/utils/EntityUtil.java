package ninja.egg82.plugin.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import ninja.egg82.plugin.core.EntityData;

public class EntityUtil {
	//vars
	
	//constructor
	public EntityUtil() {
		
	}
	
	//public
	public static List<EntityData> getEntities(Location center, int xRadius, int yRadius, int zRadius) {
		if (center == null) {
			throw new IllegalArgumentException("center cannot be null.");
		}
		
		ArrayList<EntityData> entities = new ArrayList<EntityData>();
		for (Entity e : center.getWorld().getNearbyEntities(center, xRadius, yRadius, zRadius)) {
			entities.add(new EntityData(e));
		}
		
		return entities;
	}
	
	//private
	
}
