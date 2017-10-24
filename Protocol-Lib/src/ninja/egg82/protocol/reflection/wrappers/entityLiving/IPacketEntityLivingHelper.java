package ninja.egg82.protocol.reflection.wrappers.entityLiving;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import com.comphenix.protocol.events.PacketContainer;

import ninja.egg82.protocol.reflection.wrappers.entity.IPacketEntityHelper;

public interface IPacketEntityLivingHelper extends IPacketEntityHelper {
	//functions
	PacketContainer spawn(int entityId, UUID uuid, EntityType type, Location spawnLoc);
	PacketContainer spawn(int entityId, UUID uuid, EntityType type, Location spawnLoc, Vector velocity);
	
	PacketContainer look(int entityId, float yaw, float pitch);
	PacketContainer headRotation(int entityId, float yaw);
	PacketContainer move(int entityId, Location from, Location to, boolean isFlying);
	PacketContainer teleport(int entityId, Location to, boolean isFlying);
	PacketContainer animate(int entityId, int animationId);
	
	public PacketContainer hurt(int entityId);
	public PacketContainer death(int entityId);
}
