package ninja.egg82.plugin.reflection.entity;

import org.bukkit.entity.Entity;

public interface IEntityUtil {
	//functions
	void addPassenger(Entity bottom, Entity top);
	void removePassenger(Entity bottom, Entity top);
	void removeAllPassengers(Entity botto);
}
