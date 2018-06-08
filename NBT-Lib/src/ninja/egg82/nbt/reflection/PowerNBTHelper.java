package ninja.egg82.nbt.reflection;

import java.io.File;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.nbt.core.INBTCompound;
import ninja.egg82.nbt.core.NBTFileUtil;
import ninja.egg82.nbt.core.PowerNBTCompound;

public class PowerNBTHelper implements INBTHelper {
	//vars
	
	//constructor
	public PowerNBTHelper() {
		
	}
	
	//public
	public INBTCompound getCompound(ItemStack stack) {
		if (stack == null) {
			throw new IllegalArgumentException("stack cannot be null.");
		}
		
		return new PowerNBTCompound(stack);
	}
	public INBTCompound getCompound(Entity entity) {
		if (entity == null) {
			throw new IllegalArgumentException("entity cannot be null.");
		}
		
		return new PowerNBTCompound(entity);
	}
	public INBTCompound getCompound(Block block) {
		if (block == null) {
			throw new IllegalArgumentException("block cannot be null.");
		}
		
		return new PowerNBTCompound(block);
	}
	public INBTCompound getCompound(File file) {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null.");
		}
		
		file = file.getAbsoluteFile();
		
		if (!NBTFileUtil.pathExists(file)) {
			throw new IllegalArgumentException("file does not exist.");
		}
		if (!NBTFileUtil.pathIsFile(file)) {
			throw new IllegalArgumentException("file is not a file.");
		}
		
		return new PowerNBTCompound(file);
	}
	public INBTCompound getCompound(byte[] serialized) {
		if (serialized == null) {
			throw new IllegalArgumentException("serialized cannot be null.");
		}
		
		return new PowerNBTCompound(serialized);
	}
	public INBTCompound getCompound(String fromString) {
		if (fromString == null) {
			throw new IllegalArgumentException("fromString cannot be null.");
		}
		
		return new PowerNBTCompound(fromString);
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	
}
