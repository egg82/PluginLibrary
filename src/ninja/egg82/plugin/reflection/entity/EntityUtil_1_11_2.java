package ninja.egg82.plugin.reflection.entity;

import java.util.List;

import org.bukkit.entity.Entity;

public final class EntityUtil_1_11_2 implements IEntityUtil {
	//vars

	//constructor
	public EntityUtil_1_11_2() {
		
	}
	
	//public
	public void addPassenger(Entity bottom, Entity top) {
		if (bottom == null) {
			throw new IllegalArgumentException("bottom cannot be null.");
		}
		if (top == null) {
			throw new IllegalArgumentException("top cannot be null.");
		}
		bottom.addPassenger(top);
	}
	public void removePassenger(Entity bottom, Entity top) {
		if (bottom == null) {
			throw new IllegalArgumentException("bottom cannot be null.");
		}
		if (top == null) {
			throw new IllegalArgumentException("top cannot be null.");
		}
		bottom.removePassenger(null);
	}
	public void removeAllPassengers(Entity bottom) {
		if (bottom == null) {
			throw new IllegalArgumentException("bottom cannot be null.");
		}
		
		List<Entity> passengers = bottom.getPassengers();
		for (Entity e : passengers) {
			bottom.removePassenger(e);
		}
	}
	
	//private
	
}
