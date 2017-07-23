package ninja.egg82.plugin.reflection.nbt;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.plugin.core.nbt.INBTCompound;
import ninja.egg82.plugin.core.nbt.NullCompound;

public class NullNBTHelper implements INBTHelper {
	//vars
	
	//constructor
	public NullNBTHelper() {
		
	}

	@Override
	public INBTCompound getCompound(ItemStack stack) {
		return new NullCompound();
	}
	public INBTCompound getCompound(Entity entity) {
		return new NullCompound();
	}
	public INBTCompound getCompound(Block block) {
		return new NullCompound();
	}
	public INBTCompound getCompound(String filePath) {
		return new NullCompound();
	}
	
	public boolean isValidLibrary() {
		return false;
	}
	public boolean supportsBlocks() {
		return false;
	}
	public boolean supportsFiles() {
		return false;
	}
	
	//public
	
	
	//private
	
}
