package ninja.egg82.protocol.core;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;

public interface IFakeLivingEntity extends IFakeEntity {
	//functions
	double getHealth();
	void setHealth(double health);
	
	void lookTo(Location loc);
	void moveTo(Location loc);
	void teleportTo(Location loc);
	Location getLocation();
	
	void animate(int animationId);
	void attack(Damageable entity, double damage);
	
	void collideF(Collection<IFakeLivingEntity> entities);
	void collideE(Collection<Entity> entities);
	void collide(IFakeLivingEntity entity);
	void collide(Entity entity);
}
