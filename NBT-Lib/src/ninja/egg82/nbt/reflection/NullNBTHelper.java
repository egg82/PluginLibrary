package ninja.egg82.nbt.reflection;

import java.io.IOException;
import java.io.InputStream;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.nbt.core.INBTCompound;
import ninja.egg82.nbt.core.INBTList;
import ninja.egg82.nbt.core.NullNBTCompound;
import ninja.egg82.nbt.core.NullNBTList;

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
	public INBTCompound getCompound(byte[] serialized) throws IOException, ClassCastException {
		return new NullNBTCompound();
	}
	public INBTCompound getCompound(InputStream stream) throws IOException, ClassCastException {
		return new NullNBTCompound();
	}
	public INBTCompound getCompound(String fromString) throws ClassCastException {
		return new NullNBTCompound();
	}
	
	public INBTList getList(Inventory inventory) {
		return new NullNBTList();
	}
	public INBTList getList(byte[] serialized) throws IOException, ClassCastException {
		return new NullNBTList();
	}
	public INBTList getList(InputStream stream) throws IOException, ClassCastException {
		return new NullNBTList();
	}
	public INBTList getList(String fromString) throws ClassCastException {
		return new NullNBTList();
	}
	
	public boolean isValidLibrary() {
		return false;
	}
	
	//private
	
}
