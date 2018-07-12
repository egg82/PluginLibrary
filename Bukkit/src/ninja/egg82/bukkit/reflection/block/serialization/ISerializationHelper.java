package ninja.egg82.bukkit.reflection.block.serialization;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;

public interface ISerializationHelper {
	//functions
	void fromCompressedBytes(Location loc, Material type, byte blockData, byte[] data, boolean updatePhysics);
	byte[] toCompressedBytes(BlockState state);
}
