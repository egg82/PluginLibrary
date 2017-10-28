package ninja.egg82.protocol.core;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.events.PacketContainer;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.utils.CommandUtil;
import ninja.egg82.protocol.reflection.wrappers.itemFrame.IPacketItemFrameHelper;
import ninja.egg82.protocol.utils.ProtocolReflectUtil;

public class ProtocolLibFakeItemFrame extends ProtocolLibFakeEntity implements IFakeItemFrame {
	//vars
	private IPacketItemFrameHelper packetHelper = ServiceLocator.getService(IPacketItemFrameHelper.class);
	
	private volatile Rotation rotation = null;
	private volatile ItemStack item = null;
	private BlockFace facingDirection = null;
	
	//constructor
	public ProtocolLibFakeItemFrame(ItemFrame frame) {
		super();
		
		if (frame == null) {
			throw new ArgumentNullException("frame");
		}
		
		currentLocation = frame.getLocation().clone();
		id = frame.getEntityId();
		uuid = frame.getUniqueId();
		
		rotation = frame.getRotation();
		item = frame.getItem();
		facingDirection = frame.getFacing();
		
		spawnPacket = null;
		destroyPacket = null;
	}
	public ProtocolLibFakeItemFrame(Location loc, BlockFace facing, ItemStack item) {
		super();
		
		if (loc == null) {
			throw new ArgumentNullException("loc");
		}
		if (facing == null) {
			throw new ArgumentNullException("facing");
		}
		
		this.item = item.clone();
		this.facingDirection = facing;
		currentLocation = loc.clone();
		id = getNextEntityId();
		uuid = UUID.randomUUID();
		
		spawnPacket = packetHelper.spawn(id, uuid, loc, facing);
		destroyPacket = packetHelper.destroy(id);
	}
	
	//public
	public ItemStack getItem() {
		return (item != null) ? item.clone() : item;
	}
	public void setItem(ItemStack item) {
		if (!((this.item == null && item == null) || (this.item != null && this.item.equals(item)))) {
			this.item = item.clone();
		}
		
		PacketContainer updatePacket = packetHelper.updateItem(id, item);
		for (UUID uuid : players) {
			ProtocolReflectUtil.sendPacket(updatePacket, CommandUtil.getPlayerByUuid(uuid));
		}
	}
	
	public Rotation getRotation() {
		return rotation;
	}
	public void setRotation(Rotation rotation) {
		if (rotation == null) {
			rotation = Rotation.NONE;
		}
		
		if (!this.rotation.equals(rotation)) {
			this.rotation = rotation;
		}
		
		PacketContainer updatePacket = packetHelper.updateRotation(id, rotation);
		for (UUID uuid : players) {
			ProtocolReflectUtil.sendPacket(updatePacket, CommandUtil.getPlayerByUuid(uuid));
		}
	}
	
	public BlockFace getFacingDirection() {
		return facingDirection;
	}
	
	public void destroy() {
		removeAllPlayers();
	}
	
	//private
	
}
