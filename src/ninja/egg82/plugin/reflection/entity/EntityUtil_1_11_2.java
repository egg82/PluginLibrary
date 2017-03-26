package ninja.egg82.plugin.reflection.entity;

import org.bukkit.entity.Entity;

public class EntityUtil_1_11_2 implements IEntityUtil {
	//vars

	//constructor
	public EntityUtil_1_11_2() {
		
	}
	
	//public
	public void addPassenger(Entity bottom, Entity top) {
		if (bottom == null) {
			return;
		}
		
		bottom.addPassenger(top);
	}
	
	//private
	
}
