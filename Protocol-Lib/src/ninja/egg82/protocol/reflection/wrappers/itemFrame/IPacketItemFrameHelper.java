package ninja.egg82.protocol.reflection.wrappers.itemFrame;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.events.PacketContainer;

import ninja.egg82.protocol.reflection.wrappers.entity.IPacketEntityHelper;

public interface IPacketItemFrameHelper extends IPacketEntityHelper {
	//functions
	PacketContainer spawn(int entityId, UUID uuid, Location spawnLoc, BlockFace facingDirection);
	PacketContainer spawnItem(int entityId, String displayName, boolean isDisplayNameVisible, boolean isSilent, ItemStack item, Rotation itemRotation);
	
	PacketContainer updateDisplayName(int entityId, String displayName);
	PacketContainer updateDisplayNameVisible(int entityId, boolean isDisplayNameVisible);
	PacketContainer updateSilent(int entityId, boolean isSilent);
	PacketContainer updateItem(int entityId, ItemStack item);
	PacketContainer updateRotation(int entityId, Rotation itemDotation);
}
