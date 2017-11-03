package ninja.egg82.nbt.reflection;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.nbt.core.INBTCompound;

public interface INBTHelper {
	//functions
	INBTCompound getCompound(ItemStack stack);
	INBTCompound getCompound(Entity entity);
	INBTCompound getCompound(Block block);
	INBTCompound getCompound(String fromString);
	INBTCompound getCompound(byte[] serialized);
	
	boolean isValidLibrary();
}
