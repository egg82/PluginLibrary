package ninja.egg82.nbt.reflection;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.nbt.core.INBTCompound;
import ninja.egg82.nbt.core.NullNBTCompound;

public class NullNBTHelper implements INBTHelper {
	//vars
	
	//constructor
	public NullNBTHelper() {
		
	}
	
	//public
	public INBTCompound getCompound(ItemStack stack) {
		return new NullNBTCompound();
	}
	public INBTCompound getCompound(Entity entity) {
		return new NullNBTCompound();
	}
	public INBTCompound getCompound(Block block) {
		return new NullNBTCompound();
	}
	public INBTCompound getCompound(String fromString) {
		return new NullNBTCompound();
	}
	public INBTCompound getCompound(byte[] serialized) {
		return new NullNBTCompound();
	}
	
	public boolean isValidLibrary() {
		return false;
	}
	
	//private
	
}
