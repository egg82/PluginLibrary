package ninja.egg82.bukkit.reflection.block.serialization;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

public interface ISerializationHelper {
    // functions
    Block fromCompressedBytes(Location loc, Material type, byte blockData, byte[] data, boolean updatePhysics);

    byte[] toCompressedBytes(BlockState state);

    byte[] toCompressedBytes(BlockState state, ItemStack[] inventory);

    byte[] toCompressedBytes(BlockState state, int compressionLevel);

    byte[] toCompressedBytes(BlockState state, ItemStack[] inventory, int compressionLevel);
}
