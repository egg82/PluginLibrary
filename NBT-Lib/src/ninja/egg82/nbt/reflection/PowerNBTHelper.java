package ninja.egg82.nbt.reflection;

import java.io.File;

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
	public INBTCompound getCompound(File file) {
		if (file == null) {
			throw new ArgumentNullException("file");
		}
		
		file = file.getAbsoluteFile();
		
		if (!FileUtil.pathExists(file)) {
			throw new IllegalArgumentException("file does not exist.");
		}
		if (!FileUtil.pathIsFile(file)) {
			throw new IllegalArgumentException("file is not a file.");
		}
		
		return new PowerNBTCompound(file);
	}
	public INBTCompound getCompound(byte[] serialized) {
		if (serialized == null) {
			throw new ArgumentNullException("serialized");
		}
		
		return new PowerNBTCompound(serialized);
	}
	public INBTCompound getCompound(String fromString) {
		if (fromString == null) {
			throw new ArgumentNullException("fromString");
		}
		
		return new PowerNBTCompound(fromString);
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	
}
