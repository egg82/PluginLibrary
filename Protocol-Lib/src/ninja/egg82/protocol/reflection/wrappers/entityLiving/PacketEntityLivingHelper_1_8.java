package ninja.egg82.protocol.reflection.wrappers.entityLiving;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.protocol.reflection.wrappers.entity.PacketEntityHelper_1_8;

public class PacketEntityLivingHelper_1_8 extends PacketEntityHelper_1_8 implements IPacketEntityLivingHelper {
	//vars
	private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
	
	//constructor
	public PacketEntityLivingHelper_1_8() {
		
	}
	
	//public
	public PacketContainer spawn(int entityId, UUID uuid, EntityType type, Location spawnLoc) {
		return spawn(entityId, uuid, type, spawnLoc, new Vector());
	}
	@SuppressWarnings({ "deprecation", "boxing" })
	public PacketContainer spawn(int entityId, UUID uuid, EntityType type, Location spawnLoc, Vector velocity) {
		if (uuid == null) {
			throw new ArgumentNullException("uuid");
		}
		if (type == null) {
			throw new ArgumentNullException("type");
		}
		if (spawnLoc == null) {
			throw new ArgumentNullException("spawnLoc");
		}
		if (velocity == null) {
			throw new ArgumentNullException("velocity");
		}
		
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		
		packet.getIntegers()
			.write(0, entityId)
			.write(1, (int) type.getTypeId())
			.write(2, (int) spawnLoc.getX())
			.write(3, (int) spawnLoc.getY())
			.write(4, (int) spawnLoc.getZ())
			.write(5, (int) velocity.getX())
			.write(6, (int) velocity.getY())
			.write(7, (int) velocity.getZ());
		packet.getBytes()
			.write(0, (byte) ((spawnLoc.getYaw() / 360.0f) * 255.0f))
			.write(1, (byte) ((spawnLoc.getPitch() / 360.0f) * 255.0f))
			.write(2, (byte) ((spawnLoc.getPitch() / 360.0f) * 255.0f));
		
		return packet;
	}
	
	@SuppressWarnings("boxing")
	public PacketContainer look(int entityId, float yaw, float pitch) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_LOOK);
		
		packet.getIntegers()
			.write(0, entityId);
		packet.getBytes()
			.write(0, (byte) ((yaw / 360.0f) * 255.0f))
			.write(1, (byte) ((pitch / 360.0f) * 255.0f));
		
		return packet;
	}
	@SuppressWarnings("boxing")
	public PacketContainer headRotation(int entityId, float yaw) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
		
		packet.getIntegers()
			.write(0, entityId);
		packet.getBytes()
			.write(0, (byte) ((yaw / 360.0f) * 255.0f));
		
		return packet;
	}
	@SuppressWarnings("boxing")
	public PacketContainer move(int entityId, Location from, Location to, boolean isFlying) {
		if (from == null) {
			throw new ArgumentNullException("from");
		}
		if (to == null) {
			throw new ArgumentNullException("to");
		}
		
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.REL_ENTITY_MOVE);
		
		packet.getIntegers()
			.write(0, entityId);
		packet.getBytes()
			.write(0, (byte) ((to.getX() - from.getX()) / 32))
			.write(1, (byte) ((to.getY() - from.getY()) / 32))
			.write(2, (byte) ((to.getZ() - from.getZ()) / 32));
		packet.getBooleans()
			.write(0, !isFlying);
		
		return packet;
	}
	@SuppressWarnings("boxing")
	public PacketContainer teleport(int entityId, Location to, boolean isFlying) {
		if (to == null) {
			throw new ArgumentNullException("to");
		}
		
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
		
		packet.getIntegers()
			.write(0, entityId)
			.write(1, (int) to.getX())
			.write(2, (int) to.getY())
			.write(3, (int) to.getZ());
		packet.getBytes()
			.write(0, (byte) ((to.getYaw() / 360.0f) * 255.0f))
			.write(1, (byte) ((to.getPitch() / 360.0f) * 255.0f));
		packet.getBooleans()
			.write(0, !isFlying);
		
		return packet;
	}
	@SuppressWarnings("boxing")
	public PacketContainer animate(int entityId, int animationId) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ANIMATION);
		
		packet.getIntegers()
			.write(0, entityId);
		packet.getBytes()
			.write(0, (byte) animationId);
		
		return packet;
	}
	
	@SuppressWarnings("boxing")
	public PacketContainer hurt(int entityId) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_STATUS);
		
		packet.getIntegers()
			.write(0, entityId);
		packet.getBytes()
			.write(0, (byte) 2);
		return packet;
	}
	@SuppressWarnings("boxing")
	public PacketContainer death(int entityId) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_STATUS);
		
		packet.getIntegers()
			.write(0, entityId);
		packet.getBytes()
			.write(0, (byte) 3);
		return packet;
	}
	
	//private
	
}
