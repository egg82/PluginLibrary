package ninja.egg82.plugin.reflection.protocol;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Damageable;
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
	
	void collide(List<IFakeLivingEntity> entities);
	void collide(IFakeLivingEntity entity);
	
	int getId();
	UUID getUuid();
	
	void destroy();
}
