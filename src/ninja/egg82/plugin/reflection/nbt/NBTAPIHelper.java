package ninja.egg82.plugin.reflection.nbt;

import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.itemnbtapi.NBTEntity;
import de.tr7zw.itemnbtapi.NBTItem;

public class NBTAPIHelper implements INBTHelper {
	//vars
	
	//constructor
	public NBTAPIHelper() {
		
	}
	
	//public
	public void addTag(ItemStack stack, String name, Object data) {
		if (stack == null) {
			throw new RuntimeException("stack cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTItem nbt = new NBTItem(stack);
		nbt.setObject(name, data);
	}
	public void addTag(Entity entity, String name, Object data) {
		if (entity == null) {
			throw new RuntimeException("entity cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTEntity nbt = new NBTEntity(entity);
		nbt.setObject(name, data);
	}
	public void addTag(Block block, String name, Object data) {
		throw new NotImplementedException("This library does not support block NBT tags.");
	}
	
	public void removeTag(ItemStack stack, String name) {
		if (stack == null) {
			throw new RuntimeException("stack cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTItem nbt = new NBTItem(stack);
		nbt.removeKey(name);
	}
	public void removeTag(Entity entity, String name) {
		if (entity == null) {
			throw new RuntimeException("entity cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTEntity nbt = new NBTEntity(entity);
		nbt.removeKey(name);
	}
	public void removeTag(Block block, String name) {
		throw new NotImplementedException("This library does not support block NBT tags.");
	}
	
	public boolean hasTag(ItemStack stack, String name) {
		if (stack == null) {
			return false;
		}
		if (name == null) {
			return false;
		}
		
		NBTItem nbt = new NBTItem(stack);
		return nbt.hasKey(name);
	}
	public boolean hasTag(Entity entity, String name) {
		if (entity == null) {
			return false;
		}
		if (name == null) {
			return false;
		}
		
		NBTEntity nbt = new NBTEntity(entity);
		return nbt.hasKey(name);
	}
	public boolean hasTag(Block block, String name) {
		throw new NotImplementedException("This library does not support block NBT tags.");
	}
	
	public Object getTag(ItemStack stack, String name) {
		if (stack == null) {
			throw new RuntimeException("stack cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTItem nbt = new NBTItem(stack);
		return nbt.getObject(name, Object.class);
	}
	public <T> T getTag(ItemStack stack, String name, Class<T> type) {
		if (stack == null) {
			throw new RuntimeException("stack cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTItem nbt = new NBTItem(stack);
		return nbt.getObject(name, type);
	}
	public Object getTag(Entity entity, String name) {
		if (entity == null) {
			throw new RuntimeException("entity cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTEntity nbt = new NBTEntity(entity);
		return nbt.getObject(name, Object.class);
	}
	public <T> T getTag(Entity entity, String name, Class<T> type) {
		if (entity == null) {
			throw new RuntimeException("entity cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTEntity nbt = new NBTEntity(entity);
		return nbt.getObject(name, type);
	}
	public Object getTag(Block block, String name) {
		throw new NotImplementedException("This library does not support block NBT tags.");
	}
	public <T> T getTag(Block block, String name, Class<T> type) {
		throw new NotImplementedException("This library does not support block NBT tags.");
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	public boolean supportsBlocks() {
		return false;
	}
	
	//private
	
}
