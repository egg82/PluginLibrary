package ninja.egg82.protocol.reflection.wrappers.itemFrame;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.utility.Util;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.protocol.reflection.wrappers.entity.PacketEntityHelper_1_8;

public class PacketItemFrameHelper_1_9 extends PacketEntityHelper_1_8 implements IPacketItemFrameHelper {
	//vars
	private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
	
	//constructor
	public PacketItemFrameHelper_1_9() {
		
	}
	
	//public
	@SuppressWarnings("deprecation")
	public PacketContainer spawn(int entityId, UUID uuid, Location spawnLoc, BlockFace facingDirection) {
		if (uuid == null) {
			throw new ArgumentNullException("uuid");
		}
		if (spawnLoc == null) {
			throw new ArgumentNullException("spawnLoc");
		}
		if (facingDirection == null) {
			throw new ArgumentNullException("facingDirection");
		}
		if (facingDirection != BlockFace.NORTH && facingDirection != BlockFace.EAST && facingDirection != BlockFace.SOUTH && facingDirection != BlockFace.WEST) {
			throw new IllegalStateException("facingDirection must be in one of the four cardinal directions! (N, E, S, W)");
		}
		
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		
		packet.getIntegers()
			.write(0, entityId)
			.write(1, faceToInt(facingDirection));
		packet.getUUIDs()
			.write(0, uuid);
		packet.getDoubles()
			.write(0, spawnLoc.getX())
			.write(1, spawnLoc.getY())
			.write(2, spawnLoc.getZ());
		packet.getBytes()
			.write(0, (byte) ((spawnLoc.getPitch() / 360.0f) * 255.0f))
			.write(1, (byte) ((spawnLoc.getYaw() / 360.0f) * 255.0f))
			.write(2, (byte) EntityType.ITEM_FRAME.getTypeId());
		packet.getShorts()
			.write(0, (short) 0)
			.write(1, (short) 0)
			.write(2, (short) 0);
		
		return packet;
	}
	public PacketContainer updateItem(int entityId, ItemStack item) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		
		WrappedWatchableObject obj = new WrappedWatchableObject(new WrappedDataWatcherObject(6, Registry.getItemStackSerializer(false)), MinecraftReflection.getMinecraftItemStack(item));
		obj.setDirtyState(true);
		
		packet.getIntegers()
			.write(0, entityId);
		packet.getWatchableCollectionModifier()
			.write(0, Util.asList(obj));
		
		return packet;
	}
	public PacketContainer updateRotation(int entityId, Rotation itemRotation) {
		if (itemRotation == null) {
			throw new ArgumentNullException("itemRotation");
		}
		
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		
		WrappedWatchableObject obj = new WrappedWatchableObject(new WrappedDataWatcherObject(7, Registry.get(Integer.class, false)), itemRotation.ordinal());
		obj.setDirtyState(true);
		
		packet.getIntegers()
			.write(0, entityId);
		packet.getWatchableCollectionModifier()
			.write(0, Util.asList(obj));
		
		return packet;
	}
	
	//private
	private int faceToInt(BlockFace face) {
		if (face == BlockFace.SOUTH) {
			return 0;
		} else if (face == BlockFace.WEST) {
			return 1;
		} else if (face == BlockFace.NORTH) {
			return 2;
		} else {
			return 3;
		}
	}
}
