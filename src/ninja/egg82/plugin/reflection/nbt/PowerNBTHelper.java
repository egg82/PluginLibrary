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
			throw new RuntimeException("stack cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = null;
		try {
			compound = manager.read(stack);
		} catch (Exception ex) {
			
		}
		if (compound == null) {
			compound = new NBTCompound();
		}
		compound.put(name, data);
		try {
			manager.write(stack, compound);
		} catch (Exception ex) {
			
		}
	}
	public void addTag(Entity entity, String name, Object data) {
		if (entity == null) {
			throw new RuntimeException("entity cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = null;
		try {
			compound = manager.read(entity);
		} catch (Exception ex) {
			
		}
		if (compound == null) {
			compound = new NBTCompound();
		}
		compound.put(name, data);
		try {
			manager.write(entity, compound);
		} catch (Exception ex) {
			
		}
	}
	public void addTag(Block block, String name, Object data) {
		if (block == null) {
			throw new RuntimeException("block cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = null;
		try {
			compound = manager.read(block);
		} catch (Exception ex) {
			
		}
		if (compound == null) {
			compound = new NBTCompound();
		}
		compound.put(name, data);
		try {
			manager.write(block, compound);
		} catch (Exception ex) {
			
		}
	}
	
	public void removeTag(ItemStack stack, String name) {
		if (stack == null) {
			throw new RuntimeException("stack cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = null;
		try {
			compound = manager.read(stack);
		} catch (Exception ex) {
			
		}
		if (compound == null) {
			return;
		}
		compound.remove(name);
		try {
			manager.write(stack, compound);
		} catch (Exception ex) {
			
		}
	}
	public void removeTag(Entity entity, String name) {
		if (entity == null) {
			throw new RuntimeException("entity cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = null;
		try {
			compound = manager.read(entity);
		} catch (Exception ex) {
			
		}
		if (compound == null) {
			return;
		}
		compound.remove(name);
		try {
			manager.write(entity, compound);
		} catch (Exception ex) {
			
		}
	}
	public void removeTag(Block block, String name) {
		if (block == null) {
			throw new RuntimeException("block cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = null;
		try {
			compound = manager.read(block);
		} catch (Exception ex) {
			
		}
		if (compound == null) {
			return;
		}
		compound.remove(name);
		try {
			manager.write(block, compound);
		} catch (Exception ex) {
			
		}
	}
	
	public boolean hasTag(ItemStack stack, String name) {
		if (stack == null) {
			return false;
		}
		if (name == null) {
			return false;
		}
		
		NBTCompound compound = null;
		try {
			compound = manager.read(stack);
		} catch (Exception ex) {
			
		}
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
		
		NBTCompound compound = null;
		try {
			compound = manager.read(entity);
		} catch (Exception ex) {
			
		}
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
		
		NBTCompound compound = null;
		try {
			compound = manager.read(block);
		} catch (Exception ex) {
			
		}
		if (compound == null) {
			return false;
		}
		return compound.containsKey(name);
	}
	
	public Object getTag(ItemStack stack, String name) {
		if (stack == null) {
			throw new RuntimeException("stack cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = null;
		try {
			compound = manager.read(stack);
		} catch (Exception ex) {
			
		}
		if (compound == null) {
			return null;
		}
		return compound.get(name);
	}
	public Object getTag(Entity entity, String name) {
		if (entity == null) {
			throw new RuntimeException("entity cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = null;
		try {
			compound = manager.read(entity);
		} catch (Exception ex) {
			
		}
		if (compound == null) {
			return null;
		}
		return compound.get(name);
	}
	public Object getTag(Block block, String name) {
		if (block == null) {
			throw new RuntimeException("block cannot be null.");
		}
		if (name == null) {
			throw new RuntimeException("name cannot be null.");
		}
		
		NBTCompound compound = null;
		try {
			compound = manager.read(block);
		} catch (Exception ex) {
			
		}
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
