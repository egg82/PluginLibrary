package ninja.egg82.protocol.reflection.wrappers.entityLiving;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.comphenix.protocol.events.PacketContainer;

public interface IPacketEntityLivingHelper {
	//functions
	PacketContainer spawn(int entityId, UUID uuid, EntityType type, Location spawnLoc);
	PacketContainer spawn(int entityId, UUID uuid, EntityType type, Location spawnLoc, Vector velocity);
	
	PacketContainer destroy(List<Integer> entityIds);
	PacketContainer destroy(int entityId);
	PacketContainer destroy(int[] entityIds);
	
	PacketContainer look(int entityId, float yaw, float pitch);
	PacketContainer headRotation(int entityId, float yaw);
	PacketContainer move(int entityId, Location from, Location to, boolean isFlying);
	PacketContainer teleport(int entityId, Location to, boolean isFlying);
	PacketContainer animate(int entityId, int animationId);
	
	void send(PacketContainer packet, Player player);
	void send(PacketContainer packet, List<Player> players);
	void send(PacketContainer packet, Player[] players);
}
