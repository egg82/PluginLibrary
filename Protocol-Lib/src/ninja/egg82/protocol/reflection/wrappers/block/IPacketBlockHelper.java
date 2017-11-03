package ninja.egg82.protocol.reflection.wrappers.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import com.comphenix.protocol.events.PacketContainer;

public interface IPacketBlockHelper {
	//functions
	PacketContainer blockChange(Location blockLocation, Material newMaterial, short newMetadata);
	PacketContainer blockChange(Location blockLocation, Material newMaterial, short newMetadata, BlockFace facing);
	
	PacketContainer multiBlockChange(Location[] blockLocations, Material newMaterial, short newMetadata);
	PacketContainer multiBlockChange(Location[] blockLocations, Material newMaterial, short newMetadata, BlockFace facing);
	PacketContainer multiBlockChange(Location[] blockLocations, Material[] newMaterials, short[] newMetadata);
	PacketContainer multiBlockChange(Location[] blockLocations, Material[] newMaterials, short[] newMetadata, BlockFace[] facing);
}
