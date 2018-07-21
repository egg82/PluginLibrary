package ninja.egg82.nbt.reflection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.nbt.core.INBTCompound;
import ninja.egg82.nbt.core.INBTList;
import ninja.egg82.nbt.reflection.powernbt.PowerNBTCompound;
import ninja.egg82.nbt.reflection.powernbt.PowerNBTList;
import ninja.egg82.nbt.utils.NBTFileUtil;

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
	public INBTCompound getCompound(byte[] serialized) throws IOException, ClassCastException {
		if (serialized == null) {
			throw new IllegalArgumentException("serialized cannot be null.");
		}
		
		return new PowerNBTCompound(serialized);
	}
	public INBTCompound getCompound(InputStream stream) throws IOException, ClassCastException {
		if (stream == null) {
			throw new IllegalArgumentException("stream cannot be null.");
		}
		
		return new PowerNBTCompound(stream);
	}
	public INBTCompound getCompound(String fromString) throws ClassCastException {
		if (fromString == null) {
			throw new IllegalArgumentException("fromString cannot be null.");
		}
		
		return new PowerNBTCompound(fromString);
	}
	
	public INBTList getList(Inventory inventory) {
		return new PowerNBTList(inventory);
	}
	public INBTList getList(byte[] serialized) throws IOException, ClassCastException {
		return new PowerNBTList(serialized);
	}
	public INBTList getList(InputStream stream) throws IOException, ClassCastException {
		return new PowerNBTList(stream);
	}
	public INBTList getList(String fromString) throws ClassCastException {
		return new PowerNBTList(fromString);
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	
}
