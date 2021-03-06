package ninja.egg82.nbt.reflection.powernbt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.api.NBTCompound;
import ninja.egg82.nbt.core.INBTCompound;
import ninja.egg82.nbt.core.INBTList;
import ninja.egg82.nbt.utils.NBTReflectUtil;
import ninja.egg82.nbt.utils.PowerNBTUtil;
import me.dpohvar.powernbt.api.NBTManager;

public class PowerNBTCompound implements INBTCompound {
	//vars
	private NBTManager manager = PowerNBT.getApi();
	
	private ItemStack stack = null;
	private Entity entity = null;
	private Block block = null;
	private File file = null;
	
	private PowerNBTCompound parentCompound = null;
	private PowerNBTList parentList = null;
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
	public PowerNBTCompound(File file) {
		this.file = file;
	}
	public PowerNBTCompound(PowerNBTCompound parent, NBTCompound compound) {
		this.parentCompound = parent;
		this.compound = compound;
	}
	public PowerNBTCompound(PowerNBTList parent, NBTCompound compound) {
		this.parentList = parent;
		this.compound = compound;
	}
	public PowerNBTCompound(byte[] serialized) throws IOException, ClassCastException {
		try (ByteArrayInputStream stream = new ByteArrayInputStream(serialized)) {
			this.compound = (NBTCompound) manager.read(stream);
		}
	}
	public PowerNBTCompound(InputStream stream) throws IOException, ClassCastException {
		this.compound = (NBTCompound) manager.read(stream);
	}
	public PowerNBTCompound(String fromString) throws ClassCastException {
		this.compound = (NBTCompound) manager.parseMojangson(fromString);
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
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NBTCompound compound = readCompound();
		compound.remove(name);
		writeCompound(compound);
	}
	
	public String[] getTagNames() {
		return readCompound().keySet().toArray(new String[0]);
	}
	
	public void setBoolean(String name, boolean data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, Boolean.valueOf(data));
		writeCompound(compound);
	}
	public boolean getBoolean(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return readCompound().getBoolean(name);
	}
	public void setByte(String name, byte data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, Byte.valueOf(data));
		writeCompound(compound);
	}
	public byte getByte(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return readCompound().getByte(name);
	}
	public void setShort(String name, short data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, Short.valueOf(data));
		writeCompound(compound);
	}
	public short getShort(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return readCompound().getShort(name);
	}
	public void setInt(String name, int data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, Integer.valueOf(data));
		writeCompound(compound);
	}
	public int getInt(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return readCompound().getInt(name);
	}
	public void setLong(String name, long data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, Long.valueOf(data));
		writeCompound(compound);
	}
	public long getLong(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return readCompound().getLong(name);
	}
	public void setFloat(String name, float data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, Float.valueOf(data));
		writeCompound(compound);
	}
	public float getFloat(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return readCompound().getFloat(name);
	}
	public void setDouble(String name, double data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, Double.valueOf(data));
		writeCompound(compound);
	}
	public double getDouble(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return readCompound().getDouble(name);
	}
	public void setString(String name, String data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public String getString(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return readCompound().getString(name);
	}
	public void setByteArray(String name, byte[] data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public byte[] getByteArray(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return readCompound().getByteArray(name);
	}
	public void setIntArray(String name, int[] data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public int[] getIntArray(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return readCompound().getIntArray(name);
	}
	
	public void setObject(String name, Object data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, PowerNBTUtil.tryUnwrap(data));
		writeCompound(compound);
	}
	public Object getObject(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return PowerNBTUtil.tryWrap(this, readCompound().get(name));
	}
	@SuppressWarnings("unchecked")
	public <T> T getObject(String name, Class<T> type) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		Object retVal = readCompound().get(name);
		if (retVal != null) {
			if (!NBTReflectUtil.doesExtend(type, retVal.getClass())) {
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
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NBTCompound compound = readCompound();
		PowerNBTCompound retVal = new PowerNBTCompound(this, compound.compound(name));
		writeCompound(compound);
		return retVal;
	}
	public INBTCompound getCompound(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return new PowerNBTCompound(this, readCompound().getCompound(name));
	}
	
	public INBTList addList(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NBTCompound compound = readCompound();
		PowerNBTList retVal = new PowerNBTList(this, compound.list(name));
		writeCompound(compound);
		return retVal;
	}
	public INBTList getList(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return new PowerNBTList(this, readCompound().getList(name));
	}
	
	public byte[] serialize() throws IOException {
		try (ByteArrayOutputStream retVal = new ByteArrayOutputStream()) {
			manager.write(retVal, readCompound());
			return retVal.toByteArray();
		}
	}
	public void serialize(OutputStream stream) throws IOException {
		manager.write(stream, readCompound());
	}
	public String toString() {
		return PowerNBTUtil.toMojangson(readCompound());
	}
	
	public boolean isValidCompound() {
		return true;
	}
	
	public PowerNBTCompound getRoot() {
		return (parentCompound != null) ? parentCompound.getRoot() : (parentList != null) ? parentList.getRoot() : this;
	}
	public NBTCompound getSelf() {
		return readCompound();
	}
	public void writeLast() {
		writeCompound(last);
	}
	
	//private
	private synchronized NBTCompound readCompound() {
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
	private synchronized void writeCompound(NBTCompound compound) {
		if (parentCompound != null || parentList != null) {
			getRoot().writeLast();
			return;
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
