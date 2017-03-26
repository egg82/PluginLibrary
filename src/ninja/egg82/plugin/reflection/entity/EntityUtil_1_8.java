package ninja.egg82.plugin.reflection.entity;

import org.bukkit.entity.Entity;

public class EntityUtil_1_8 implements IEntityUtil {
	//vars
	
	//constructor
	public EntityUtil_1_8() {
		
	}
	
	//public
	@SuppressWarnings("deprecation")
	public void addPassenger(Entity bottom, Entity top) {
		if (bottom == null) {
			return;
		}
		
		bottom.setPassenger(top);
	}
	
	//private
}
