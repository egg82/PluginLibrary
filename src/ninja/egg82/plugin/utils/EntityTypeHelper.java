package ninja.egg82.plugin.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.entity.EntityType;

import ninja.egg82.utils.ReflectUtil;

public final class EntityTypeHelper {
	//vars
	private EntityType[] types = null;
	
	//constructor
	public EntityTypeHelper() {
		Object[] enums = ReflectUtil.getStaticFields(EntityType.class);
		types = Arrays.copyOf(enums, enums.length, EntityType[].class);
	}
	
	//public
	public EntityType[] getAllEntityTypes() {
		return types.clone();
	}
	
	public EntityType[] filter(EntityType[] list, String filter, boolean whitelist) {
		if (list == null) {
			throw new IllegalArgumentException("list cannot be null.");
		}
		if (filter == null) {
			throw new IllegalArgumentException("filter cannot be null.");
		}
		
		filter = filter.toLowerCase();
		
		ArrayList<EntityType> filteredEntityTypes = new ArrayList<EntityType>();
		
		for (EntityType s : list) {
			String name = s.toString().toLowerCase();
			if (whitelist) {
				if (name.contains(filter)) {
					filteredEntityTypes.add(s);
				}
			} else {
				if (!name.contains(filter)) {
					filteredEntityTypes.add(s);
				}
			}
		}
		
		return filteredEntityTypes.toArray(new EntityType[0]);
	}
	
	//private
	
}
