package ninja.egg82.nbt.reflection;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.nbt.core.INBTCompound;
import ninja.egg82.nbt.core.PowerNBTCompound;
import ninja.egg82.utils.FileUtil;

public class PowerNBTHelper implements INBTHelper {
	//vars
	
	//constructor
	public PowerNBTHelper() {
		
	}
	
	//public
	public INBTCompound getCompound(ItemStack stack) {
		if (stack == null) {
			throw new ArgumentNullException("stack");
		}
		
		return new PowerNBTCompound(stack);
	}
	public INBTCompound getCompound(Entity entity) {
		if (entity == null) {
			throw new ArgumentNullException("entity");
		}
		
		return new PowerNBTCompound(entity);
	}
	public INBTCompound getCompound(Block block) {
		if (block == null) {
			throw new ArgumentNullException("block");
		}
		
		return new PowerNBTCompound(block);
	}
	public INBTCompound getCompound(String filePath) {
		if (filePath == null) {
			throw new ArgumentNullException("filePath");
		}
		
		if (!FileUtil.pathExists(filePath)) {
			throw new IllegalArgumentException("filePath does not exist.");
		}
		if (!FileUtil.pathIsFile(filePath)) {
			throw new IllegalArgumentException("filePath is not a file.");
		}
		
		return new PowerNBTCompound(filePath);
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	public boolean supportsBlocks() {
		return true;
	}
	public boolean supportsFiles() {
		return true;
	}
	
	//private
	
}
