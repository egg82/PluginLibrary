package ninja.egg82.plugin.core.nbt;

import java.io.File;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTManager;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.utils.ReflectUtil;

public class PowerNBTCompound implements INBTCompound {
	//vars
	private NBTManager manager = PowerNBT.getApi();
	
	private ItemStack stack = null;
	private Entity entity = null;
	private Block block = null;
	private File file = null;
	
	private PowerNBTCompound parent = null;
	private NBTCompound compound = null;
	private NBTCompound last = null;
	
	//constructor
	public PowerNBTCompound(ItemStack stack) {
		this.stack = stack;
	}
	public PowerNBTCompound(Entity entity) {
		this.entity = entity;
	}
	public PowerNBTCompound(Block block) {
		this.block = block;
	}
	public PowerNBTCompound(String filePath) {
		this.file = new File(filePath);
	}
	public PowerNBTCompound(PowerNBTCompound parent, NBTCompound compound) {
		this.parent = parent;
		this.compound = compound;
	}
	
	//public
	public boolean hasTag(String name) {
		if (name == null) {
			return false;
		}
		
		return readCompound().containsKey(name);
	}
	public void removeTag(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		NBTCompound compound = readCompound();
		compound.remove(name);
		writeCompound(compound);
	}
	
	public String[] getTagNames() {
		return readCompound().keySet().toArray(new String[0]);
	}
	
	public void setByte(String name, byte data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public byte getByte(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return readCompound().getByte(name);
	}
	public void setShort(String name, short data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public short getShort(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return readCompound().getShort(name);
	}
	public void setInt(String name, int data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public int getInt(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return readCompound().getInt(name);
	}
	public void setLong(String name, long data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public long getLong(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return readCompound().getLong(name);
	}
	public void setFloat(String name, float data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public float getFloat(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return readCompound().getFloat(name);
	}
	public void setDouble(String name, double data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public double getDouble(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return readCompound().getDouble(name);
	}
	public void setString(String name, String data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public String getString(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return readCompound().getString(name);
	}
	public void setByteArray(String name, byte[] data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public byte[] getByteArray(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return readCompound().getByteArray(name);
	}
	public void setIntArray(String name, int[] data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public int[] getIntArray(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return readCompound().getIntArray(name);
	}
	
	public void setObject(String name, Object data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public Object getObject(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return readCompound().get(name);
	}
	@SuppressWarnings("unchecked")
	public <T> T getObject(String name, Class<T> type) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		Object retVal = readCompound().get(name);
		if (retVal != null) {
			if (!ReflectUtil.doesExtend(type, retVal.getClass())) {
				try {
					retVal = type.cast(retVal);
				} catch (Exception ex) {
					throw new RuntimeException("tag type cannot be converted to the type specified.", ex);
				}
			} else {
				return (T) retVal;
			}
		}
		
		return null;
	}
	
	public INBTCompound addCompound(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		NBTCompound compound = readCompound();
		PowerNBTCompound retVal = new PowerNBTCompound(this, compound.compound(name));
		writeCompound(compound);
		return retVal;
	}
	public INBTCompound getCompound(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return new PowerNBTCompound(this, readCompound().getCompound(name));
	}
	
	public boolean isValidCompound() {
		return true;
	}
	
	public PowerNBTCompound getRoot() {
		return (parent != null) ? parent.getRoot() : this;
	}
	public void writeLast() {
		writeCompound(last);
	}
	
	//private
	private NBTCompound readCompound() {
		try {
			if (stack != null) {
				last = manager.read(stack);
				if (last == null) {
					last = new NBTCompound();
				}
				return last;
			}
			if (entity != null) {
				last = manager.read(entity);
				if (last == null) {
					last = new NBTCompound();
				}
				return last;
			}
			if (block != null) {
				last = manager.read(block);
				if (last == null) {
					last = new NBTCompound();
				}
				return last;
			}
			if (file != null) {
				last = manager.readCompressed(file);
				if (last == null) {
					last = new NBTCompound();
				}
				return last;
			}
		} catch (Exception ex) {
			last = new NBTCompound();
			return last;
		}
		
		last = compound;
		if (last == null) {
			last = new NBTCompound();
		}
		return last;
	}
	private void writeCompound(NBTCompound compound) {
		if (parent != null) {
			getRoot().writeLast();
		}
		
		try {
			if (stack != null) {
				manager.write(stack, compound);
			}
			if (entity != null) {
				manager.write(entity, compound);
			}
			if (block != null) {
				manager.write(block, compound);
			}
			if (file != null) {
				manager.writeCompressed(file, compound);
			}
		} catch (Exception ex) {
			
		}
	}
}
