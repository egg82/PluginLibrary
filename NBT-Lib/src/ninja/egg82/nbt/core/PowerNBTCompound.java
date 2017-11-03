package ninja.egg82.nbt.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTCompound.NBTEntrySet;
import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.api.NBTList.NBTIterator;
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
	public PowerNBTCompound(byte[] serialized) {
		ByteArrayInputStream stream = new ByteArrayInputStream(serialized);
		
		try {
			this.compound = (NBTCompound) manager.read(stream);
		} catch (Exception ex) {
			throw new RuntimeException("Cannot convert serialized data to NBT compound.", ex);
		}
	}
	public PowerNBTCompound(String fromString) {
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
			throw new ArgumentNullException("name");
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
			throw new ArgumentNullException("name");
		}
		
		NBTCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public boolean getBoolean(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return readCompound().getBoolean(name);
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
		compound.put(name, tryUnwrap(data));
		writeCompound(compound);
	}
	public Object getObject(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return tryWrap(readCompound().get(name));
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
	
	public INBTList addList(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		NBTCompound compound = readCompound();
		PowerNBTList retVal = new PowerNBTList(this, compound.list(name));
		writeCompound(compound);
		return retVal;
	}
	public INBTList getList(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return new PowerNBTList(this, readCompound().getList(name));
	}
	
	public byte[] serialize() {
		ByteArrayOutputStream retVal = new ByteArrayOutputStream();
		
		try {
			manager.write(retVal, readCompound());
		} catch (Exception ex) {
			return new byte[0];
		}
		
		return retVal.toByteArray();
	}
	public String toString() {
		return toMojangson(readCompound());
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
	
	private Object tryWrap(Object obj) {
		if (obj == null) {
			return null;
		}
		
		if (ReflectUtil.doesExtend(NBTCompound.class, obj.getClass())) {
			return new PowerNBTCompound(this, (NBTCompound) obj);
		} else if (ReflectUtil.doesExtend(NBTList.class, obj.getClass())) {
			return new PowerNBTList(this, (NBTList) obj);
		}
		return obj;
	}
	private Object tryUnwrap(Object obj) {
		if (obj == null) {
			return null;
		}
		
		if (ReflectUtil.doesExtend(PowerNBTCompound.class, obj.getClass())) {
			return ((PowerNBTCompound) obj).getSelf();
		} else if (ReflectUtil.doesExtend(PowerNBTList.class, obj.getClass())) {
			return ((PowerNBTList) obj).getSelf();
		}
		
		return obj;
	}
	
	private String toMojangson(NBTCompound compound) {
		StringBuilder sb = new StringBuilder().append('{');
		
		for (NBTEntrySet.NBTIterator i = compound.entrySet().iterator(); i.hasNext();) {
			NBTEntrySet.NBTIterator.NBTEntry kvp = i.next();
			Object v = kvp.getValue();
			
			sb.append(kvp.getKey()).append(':');
			if (v instanceof byte[]) {
				sb.append(Arrays.toString((byte[]) v).replaceAll("\\s+", ""));
			} else if (v instanceof int[]) {
				sb.append(Arrays.toString((int[]) v).replaceAll("\\s+", ""));
			} else if (v instanceof NBTCompound) {
				sb.append(toMojangson((NBTCompound) v));
			} else if (v instanceof NBTList) {
				sb.append(toMojangson((NBTList) v));
			} else if (v instanceof String) {
				sb.append("\"" + v + "\"");
			} else {
				sb.append(v);
			}
			
			if (i.hasNext()) {
				sb.append(',');
			}
		}
		
		sb.append('}');
		return sb.toString();
	}
	private String toMojangson(NBTList list) {
		StringBuilder sb = new StringBuilder().append('[');
		
		for (NBTIterator i = list.iterator(); i.hasNext();) {
			Object v = i.next();
			
			if (v instanceof byte[]) {
				sb.append(Arrays.toString((byte[]) v).replaceAll("\\s+", ""));
			} else if (v instanceof int[]) {
				sb.append(Arrays.toString((int[]) v).replaceAll("\\s+", ""));
			} else if (v instanceof NBTCompound) {
				sb.append(toMojangson((NBTCompound) v));
			} else if (v instanceof NBTList) {
				sb.append(toMojangson((NBTList) v));
			} else if (v instanceof String) {
				sb.append("\"" + v + "\"");
			} else {
				sb.append(v);
			}
			
			if (i.hasNext()) {
				sb.append(',');
			}
		}
		
		sb.append(']');
		return sb.toString();
	}
}
