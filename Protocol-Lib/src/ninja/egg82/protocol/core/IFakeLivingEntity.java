package ninja.egg82.protocol.core;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface IFakeLivingEntity {
	//functions
	void addPlayer(Player player);
	void removePlayer(Player player);
	void removeAllPlayers();
	
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
	
	int getId();
	UUID getUuid();
	
	void destroy();
}
