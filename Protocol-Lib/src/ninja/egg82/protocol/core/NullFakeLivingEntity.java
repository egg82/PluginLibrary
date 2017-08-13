package ninja.egg82.protocol.core;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class NullFakeLivingEntity implements IFakeLivingEntity {
	//vars
	
	//constructor
	public NullFakeLivingEntity(Location loc, EntityType type) {
		
	}
	
	//public
	
	//private
	public void addPlayer(Player player) {
		
	}
	public void removePlayer(Player player) {
		
	}
	public void removeAllPlayers() {
		
	}
	
	public void lookTo(Location loc) {
		
	}
	public void moveTo(Location loc) {
		
	}
	public void teleportTo(Location loc) {
		
	}
	public Location getLocation() {
		return null;
	}
	
	public void animate(int animationId) {
		
	}
	public void attack(Damageable entity, double damage) {
		
	}
	
	public void collideF(Collection<IFakeLivingEntity> entities) {
		
	}
	public void collideE(Collection<Entity> entities) {
		
	}
	public void collide(IFakeLivingEntity entity) {
		
	}
	public void collide(Entity entity) {
		
	}
	
	public int getId() {
		return -1;
	}
	public UUID getUuid() {
		return null;
	}
	
	public void destroy() {
		
	}
}
