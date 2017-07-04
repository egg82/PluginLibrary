package ninja.egg82.plugin.reflection.nbt;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public interface INBTHelper {
	//functions
	void addTag(ItemStack stack, String name, Object data);
	void addTag(Entity entity, String name, Object data);
	void addTag(Block block, String name, Object data);
	
	void removeTag(ItemStack stack, String name);
	void removeTag(Entity entity, String name);
	void removeTag(Block block, String name);
	
	boolean hasTag(ItemStack stack, String name);
	boolean hasTag(Entity entity, String name);
	boolean hasTag(Block block, String name);
	
	Object getTag(ItemStack stack, String name);
	Object getTag(Entity entity, String name);
	Object getTag(Block block, String name);
	
	boolean isValidLibrary();
	boolean supportsBlocks();
}
