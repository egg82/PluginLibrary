package ninja.egg82.plugin.reflection.entity;

import org.bukkit.entity.Entity;

public final class EntityUtil_1_8 implements IEntityUtil {
	//vars
	
	//constructor
	public EntityUtil_1_8() {
		
	}
	
	//public
	@SuppressWarnings("deprecation")
	public void addPassenger(Entity bottom, Entity top) {
		if (bottom == null) {
			throw new IllegalArgumentException("bottom cannot be null.");
		}
		if (top == null) {
			throw new IllegalArgumentException("top cannot be null.");
		}
		
		bottom.setPassenger(top);
	}
	public void removePassenger(Entity bottom, Entity top) {
		if (bottom == null) {
			throw new IllegalArgumentException("bottom cannot be null.");
		}
		if (top == null) {
			throw new IllegalArgumentException("top cannot be null.");
		}
		
		bottom.eject();
	}
	public void removeAllPassengers(Entity bottom) {
		if (bottom == null) {
			throw new IllegalArgumentException("bottom cannot be null.");
		}
		
		bottom.eject();
	}
	
	//private
}
