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

import ninja.egg82.protocol.reflection.wrappers.entity.PacketEntityHelper_1_8;

public class PacketItemFrameHelper_1_8 extends PacketEntityHelper_1_8 implements IPacketItemFrameHelper {
	//vars
	private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
	
	//constructor
	public PacketItemFrameHelper_1_8() {
		
	}
	
	//public
	@SuppressWarnings({ "deprecation", "boxing" })
	public PacketContainer spawn(int entityId, UUID uuid, Location spawnLoc, BlockFace facingDirection) {
		if (uuid == null) {
			throw new IllegalArgumentException("uuid cannot be null.");
		}
		if (spawnLoc == null) {
			throw new IllegalArgumentException("spawnLoc cannot be null.");
		}
		if (facingDirection == null) {
			throw new IllegalArgumentException("facingDirection cannot be null.");
		}
		if (facingDirection != BlockFace.NORTH && facingDirection != BlockFace.EAST && facingDirection != BlockFace.SOUTH && facingDirection != BlockFace.WEST) {
			throw new IllegalStateException("facingDirection must be in one of the four cardinal directions! (N, E, S, W)");
		}
		
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		
		packet.getIntegers()
			.write(0, entityId)
			.write(1, (int) spawnLoc.getX())
			.write(2, (int) spawnLoc.getY())
			.write(3, (int) spawnLoc.getZ())
			.write(4, 0)
			.write(5, 0)
			.write(6, 0)
			.write(7, faceToInt(facingDirection));
		packet.getBytes()
			.write(0, (byte) ((spawnLoc.getPitch() / 360.0f) * 255.0f))
			.write(1, (byte) ((spawnLoc.getYaw() / 360.0f) * 255.0f))
			.write(2, (byte) EntityType.ITEM_FRAME.getTypeId());
		
		return packet;
	}
	@SuppressWarnings("boxing")
	public PacketContainer spawnItem(int entityId, String displayName, boolean isDisplayNameVisible, boolean isSilent, ItemStack item, Rotation itemRotation) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		
		WrappedWatchableObject index0 = new WrappedWatchableObject(new WrappedDataWatcherObject(0, Registry.get(Integer.class, false)), 0);
		index0.setDirtyState(true);
		WrappedWatchableObject index1 = new WrappedWatchableObject(new WrappedDataWatcherObject(1, Registry.get(Integer.class, false)), 300);
		index1.setDirtyState(true);
		WrappedWatchableObject name = new WrappedWatchableObject(new WrappedDataWatcherObject(2, Registry.get(String.class, false)), displayName);
		name.setDirtyState(true);
		WrappedWatchableObject nameVisible = new WrappedWatchableObject(new WrappedDataWatcherObject(3, Registry.get(Boolean.class, false)), isDisplayNameVisible);
		nameVisible.setDirtyState(true);
		WrappedWatchableObject silent = new WrappedWatchableObject(new WrappedDataWatcherObject(4, Registry.get(Boolean.class, false)), isSilent);
		silent.setDirtyState(true);
		WrappedWatchableObject stack = new WrappedWatchableObject(new WrappedDataWatcherObject(8, Registry.getItemStackSerializer(false)), MinecraftReflection.getMinecraftItemStack(item));
		stack.setDirtyState(true);
		WrappedWatchableObject rotation = new WrappedWatchableObject(new WrappedDataWatcherObject(9, Registry.get(Integer.class, false)), itemRotation.ordinal());
		rotation.setDirtyState(true);
		
		packet.getIntegers()
			.write(0, entityId);
		packet.getWatchableCollectionModifier()
			.write(0, Util.asList(index0, index1, name, nameVisible, silent, stack, rotation));
		
		return packet;
	}
	
	@SuppressWarnings("boxing")
	public PacketContainer updateDisplayName(int entityId, String displayName) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		
		WrappedWatchableObject obj = new WrappedWatchableObject(new WrappedDataWatcherObject(2, Registry.get(String.class, false)), displayName);
		obj.setDirtyState(true);
		
		packet.getIntegers()
			.write(0, entityId);
		packet.getWatchableCollectionModifier()
			.write(0, Util.asList(obj));
		
		return packet;
	}
	@SuppressWarnings("boxing")
	public PacketContainer updateDisplayNameVisible(int entityId, boolean isDisplayNameVisible) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		
		WrappedWatchableObject obj = new WrappedWatchableObject(new WrappedDataWatcherObject(3, Registry.get(Boolean.class, false)), isDisplayNameVisible);
		obj.setDirtyState(true);
		
		packet.getIntegers()
			.write(0, entityId);
		packet.getWatchableCollectionModifier()
			.write(0, Util.asList(obj));
		
		return packet;
	}
	@SuppressWarnings("boxing")
	public PacketContainer updateSilent(int entityId, boolean isSilent) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		
		WrappedWatchableObject obj = new WrappedWatchableObject(new WrappedDataWatcherObject(4, Registry.get(Boolean.class, false)), isSilent);
		obj.setDirtyState(true);
		
		packet.getIntegers()
			.write(0, entityId);
		packet.getWatchableCollectionModifier()
			.write(0, Util.asList(obj));
		
		return packet;
	}
	@SuppressWarnings("boxing")
	public PacketContainer updateItem(int entityId, ItemStack item) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		
		WrappedWatchableObject obj = new WrappedWatchableObject(new WrappedDataWatcherObject(8, Registry.getItemStackSerializer(false)), MinecraftReflection.getMinecraftItemStack(item));
		obj.setDirtyState(true);
		
		packet.getIntegers()
			.write(0, entityId);
		packet.getWatchableCollectionModifier()
			.write(0, Util.asList(obj));
		
		return packet;
	}
	@SuppressWarnings("boxing")
	public PacketContainer updateRotation(int entityId, Rotation itemRotation) {
		if (itemRotation == null) {
			throw new IllegalArgumentException("itemRotation cannot be null.");
		}
		
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		
		WrappedWatchableObject obj = new WrappedWatchableObject(new WrappedDataWatcherObject(9, Registry.get(Integer.class, false)), itemRotation.ordinal());
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
