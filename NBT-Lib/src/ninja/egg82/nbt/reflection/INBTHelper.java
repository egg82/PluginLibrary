package ninja.egg82.nbt.reflection;

import java.io.IOException;
import java.io.InputStream;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.nbt.core.INBTCompound;
import ninja.egg82.nbt.core.INBTList;

public interface INBTHelper {
	//functions
	INBTCompound getCompound(ItemStack stack);
	INBTCompound getCompound(Entity entity);
	INBTCompound getCompound(Block block);
	INBTCompound getCompound(byte[] serialized) throws IOException, ClassCastException;
	INBTCompound getCompound(InputStream stream) throws IOException, ClassCastException;
	INBTCompound getCompound(String fromString) throws ClassCastException;
	
	INBTList getList(Inventory inventory);
	INBTList getList(byte[] serialized) throws IOException, ClassCastException;
	INBTList getList(InputStream stream) throws IOException, ClassCastException;
	INBTList getList(String fromString) throws ClassCastException;
	
	boolean isValidLibrary();
}
