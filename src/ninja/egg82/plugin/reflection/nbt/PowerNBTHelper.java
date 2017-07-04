package ninja.egg82.plugin.reflection.nbt;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTManager;

public class PowerNBTHelper implements INBTHelper {
	//vars
	private NBTManager manager = PowerNBT.getApi();
	
	//constructor
	public PowerNBTHelper() {
		
	}
	
	//public
	public void addTag(ItemStack stack, String name, Object data) {
		if (stack == null) {
			return;
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = manager.read(stack);
		if (compound == null) {
			compound = new NBTCompound();
		}
		compound.put(name, data);
		manager.write(stack, compound);
	}
	public void addTag(Entity entity, String name, Object data) {
		if (entity == null) {
			return;
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = manager.read(entity);
		if (compound == null) {
			compound = new NBTCompound();
		}
		compound.put(name, data);
		manager.write(entity, compound);
	}
	public void addTag(Block block, String name, Object data) {
		if (block == null) {
			return;
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = manager.read(block);
		if (compound == null) {
			compound = new NBTCompound();
		}
		compound.put(name, data);
		manager.write(block, compound);
	}
	
	public void removeTag(ItemStack stack, String name) {
		if (stack == null) {
			return;
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = manager.read(stack);
		if (compound == null) {
			return;
		}
		compound.remove(name);
		manager.write(stack, compound);
	}
	public void removeTag(Entity entity, String name) {
		if (entity == null) {
			return;
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = manager.read(entity);
		if (compound == null) {
			return;
		}
		compound.remove(name);
		manager.write(entity, compound);
	}
	public void removeTag(Block block, String name) {
		if (block == null) {
			return;
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = manager.read(block);
		if (compound == null) {
			return;
		}
		compound.remove(name);
		manager.write(block, compound);
	}
	
	public boolean hasTag(ItemStack stack, String name) {
		if (stack == null) {
			return false;
		}
		if (name == null) {
			return false;
		}
		
		NBTCompound compound = manager.read(stack);
		if (compound == null) {
			return false;
		}
		return compound.containsKey(name);
	}
	public boolean hasTag(Entity entity, String name) {
		if (entity == null) {
			return false;
		}
		if (name == null) {
			return false;
		}
		
		NBTCompound compound = manager.read(entity);
		if (compound == null) {
			return false;
		}
		return compound.containsKey(name);
	}
	public boolean hasTag(Block block, String name) {
		if (block == null) {
			return false;
		}
		if (name == null) {
			return false;
		}
		
		NBTCompound compound = manager.read(block);
		if (compound == null) {
			return false;
		}
		return compound.containsKey(name);
	}
	
	public Object getTag(ItemStack stack, String name) {
		if (stack == null) {
			return null;
		}
		if (name == null) {
			return null;
		}
		
		NBTCompound compound = manager.read(stack);
		if (compound == null) {
			return null;
		}
		return compound.get(name);
	}
	public Object getTag(Entity entity, String name) {
		if (entity == null) {
			return null;
		}
		if (name == null) {
			return null;
		}
		
		NBTCompound compound = manager.read(entity);
		if (compound == null) {
			return null;
		}
		return compound.get(name);
	}
	public Object getTag(Block block, String name) {
		if (block == null) {
			return null;
		}
		if (name == null) {
			return null;
		}
		
		NBTCompound compound = manager.read(block);
		if (compound == null) {
			return null;
		}
		return compound.get(name);
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	public boolean supportsBlocks() {
		return true;
	}
	
	//private
	
}
