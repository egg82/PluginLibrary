package ninja.egg82.bukkit.reflection.entity;

import java.util.List;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public interface IEntityHelper {
	//functions
	void addPassenger(Entity bottom, Entity top);
	void removePassenger(Entity bottom, Entity top);
	void removeAllPassengers(Entity bottom);
	List<Entity> getPassengers(Entity bottom);
	void damage(Damageable to, DamageCause cause, double damage);
	
	void damage(Entity from, Damageable to, DamageCause cause, double damage);
}
