package ninja.egg82.plugin.reflection.entity;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public interface IEntityUtil {
	//functions
	void addPassenger(Entity bottom, Entity top);
	void removePassenger(Entity bottom, Entity top);
	void removeAllPassengers(Entity bottom);
	void damage(Damageable to, DamageCause cause, double damage);
	
	void damage(Entity from, Damageable to, DamageCause cause, double damage);
}
