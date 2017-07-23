package ninja.egg82.plugin.reflection.nbt;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.plugin.core.nbt.INBTCompound;

public interface INBTHelper {
	//functions
	INBTCompound getCompound(ItemStack stack);
	INBTCompound getCompound(Entity entity);
	INBTCompound getCompound(Block block);
	INBTCompound getCompound(String filePath);
	
	boolean isValidLibrary();
	boolean supportsBlocks();
	boolean supportsFiles();
}
