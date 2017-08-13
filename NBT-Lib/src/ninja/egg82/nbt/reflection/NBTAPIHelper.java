package ninja.egg82.nbt.reflection;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.itemnbtapi.NBTEntity;
import de.tr7zw.itemnbtapi.NBTItem;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.nbt.core.INBTCompound;
import ninja.egg82.nbt.core.NBTAPICompound;

public class NBTAPIHelper implements INBTHelper {
	//vars
	
	//constructor
	public NBTAPIHelper() {
		
	}
	
	//public
	public INBTCompound getCompound(ItemStack stack) {
		if (stack == null) {
			throw new ArgumentNullException("stack");
		}
		
		return new NBTAPICompound(new NBTItem(stack));
	}
	public INBTCompound getCompound(Entity entity) {
		if (entity == null) {
			throw new ArgumentNullException("entity");
		}
		
		return new NBTAPICompound(new NBTEntity(entity));
	}
	public INBTCompound getCompound(Block block) {
		throw new NotImplementedException("This library does not support block NBT tags.");
	}
	public INBTCompound getCompound(String filePath) {
		throw new NotImplementedException("This library does not support file NBT tags.");
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	public boolean supportsBlocks() {
		return false;
	}
	public boolean supportsFiles() {
		return false;
	}
	
	//private
	
}
