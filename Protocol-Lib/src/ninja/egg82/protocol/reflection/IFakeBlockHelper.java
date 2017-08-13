package ninja.egg82.protocol.reflection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface IFakeBlockHelper {
	//functions
	void updateBlock(Player player, Location blockLocation, Material newMaterial);
	void updateBlock(Player player, Location blockLocation, Material newMaterial, short newMetadata);
	void updateBlock(Player[] players, Location blockLocation, Material newMaterial);
	void updateBlock(Player[] players, Location blockLocation, Material newMaterial, short newMetadata);
	
	void updateBlocks(Player player, Location[] blockLocations, Material newMaterial);
	void updateBlocks(Player player, Location[] blockLocations, Material newMaterial, short newMetadata);
	void updateBlocks(Player player, Location[] blockLocations, Material[] newMaterials);
	void updateBlocks(Player player, Location[] blockLocations, Material[] newMaterials, short[] newMetadata);
	void updateBlocks(Player[] players, Location[] blockLocations, Material newMaterial);
	void updateBlocks(Player[] players, Location[] blockLocations, Material newMaterial, short newMetadata);
	void updateBlocks(Player[] players, Location[] blockLocations, Material[] newMaterials);
	void updateBlocks(Player[] players, Location[] blockLocations, Material[] newMaterials, short[] newMetadata);
	
	boolean isValidLibrary();
}
