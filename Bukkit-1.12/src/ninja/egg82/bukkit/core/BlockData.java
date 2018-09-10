package ninja.egg82.bukkit.core;

import org.bukkit.Location;
import org.bukkit.Material;

public class BlockData {
    // vars
    private Location location = null;
    private Material type = null;
    private byte blockData = (byte) 0x00;
    private byte[] compressedData = null;

    // constructor
    public BlockData(Location location, Material type, byte blockData, byte[] compressedData) {
        this.location = location;
        this.type = type;
        this.blockData = blockData;
        this.compressedData = compressedData;
    }

    // public
    public Location getLocation() {
        return location;
    }

    public Material getType() {
        return type;
    }

    public byte getBlockData() {
        return blockData;
    }

    public byte[] getCompressedData() {
        return compressedData;
    }

    // private

}
