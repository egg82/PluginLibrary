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
	@SuppressWarnings({ "deprecation", "boxing" })
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
		WrappedWatchableObject index5 = new WrappedWatchableObject(new WrappedDataWatcherObject(5, Registry.get(Boolean.class, false)), false);
		index5.setDirtyState(true);
		WrappedWatchableObject stack = new WrappedWatchableObject(new WrappedDataWatcherObject(6, Registry.getItemStackSerializer(false)), MinecraftReflection.getMinecraftItemStack(item));
		stack.setDirtyState(true);
		WrappedWatchableObject rotation = new WrappedWatchableObject(new WrappedDataWatcherObject(7, Registry.get(Integer.class, false)), itemRotation.ordinal());
		rotation.setDirtyState(true);
		
		packet.getIntegers()
			.write(0, entityId);
		packet.getWatchableCollectionModifier()
			.write(0, Util.asList(index0, index1, name, nameVisible, silent, index5, stack, rotation));
		
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
		
		WrappedWatchableObject obj = new WrappedWatchableObject(new WrappedDataWatcherObject(6, Registry.getItemStackSerializer(false)), MinecraftReflection.getMinecraftItemStack(item));
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
