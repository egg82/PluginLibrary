package ninja.egg82.plugin.reflection.nbt;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class NullNBTHelper implements INBTHelper {
	//vars
	
	//constructor
	public NullNBTHelper() {
		
	}
	
	//public
	public void addTag(ItemStack stack, String name, Object data) {
		
	}
	public void addTag(Entity entity, String name, Object data) {
		
	}
	public void addTag(Block block, String name, Object data) {
		
	}
	
	public void removeTag(ItemStack stack, String name) {
		
	}
	public void removeTag(Entity entity, String name) {
		
	}
	public void removeTag(Block block, String name) {
		
	}
	
	public boolean hasTag(ItemStack stack, String name) {
		return false;
	}
	public boolean hasTag(Entity entity, String name) {
		return false;
	}
	public boolean hasTag(Block block, String name) {
		return false;
	}
	
	public Object getTag(ItemStack stack, String name) {
		return null;
	}
	public <T> T getTag(ItemStack stack, String name, Class<T> type) {
		return null;
	}
	public Object getTag(Entity entity, String name) {
		return null;
	}
	public <T> T getTag(Entity entity, String name, Class<T> type) {
		return null;
	}
	public Object getTag(Block block, String name) {
		return null;
	}
	public <T> T getTag(Block block, String name, Class<T> type) {
		return null;
	}
	
	public boolean isValidLibrary() {
		return false;
	}
	public boolean supportsBlocks() {
		return false;
	}
	
	//private
	
}
