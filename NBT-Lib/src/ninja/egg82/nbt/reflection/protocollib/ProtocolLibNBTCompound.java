package ninja.egg82.nbt.reflection.protocollib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.io.NbtBinarySerializer;

import ninja.egg82.nbt.core.INBTCompound;
import ninja.egg82.nbt.core.INBTList;
import ninja.egg82.nbt.utils.NBTReflectUtil;
import ninja.egg82.nbt.utils.ProtocolLibUtil;

public class ProtocolLibNBTCompound implements INBTCompound {
	//TODO Finish this
	
	//vars
	private ItemStack stack = null;
	private Entity entity = null;
	private Block block = null;
	private File file = null;
	
	private ProtocolLibNBTCompound parentCompound = null;
	private ProtocolLibNBTList parentList = null;
	private NbtCompound compound = null;
	private NbtCompound last = null;
	
	//constructor
	public ProtocolLibNBTCompound(ItemStack stack) {
		this.stack = stack;
	}
	public ProtocolLibNBTCompound(Entity entity) {
		this.entity = entity;
	}
	public ProtocolLibNBTCompound(Block block) {
		this.block = block;
	}
	public ProtocolLibNBTCompound(File file) {
		this.file = file;
	}
	public ProtocolLibNBTCompound(ProtocolLibNBTCompound parent, NbtCompound compound) {
		this.parentCompound = parent;
		this.compound = compound;
	}
	public ProtocolLibNBTCompound(ProtocolLibNBTList parent, NbtCompound compound) {
		this.parentList = parent;
		this.compound = compound;
	}
	public ProtocolLibNBTCompound(byte[] serialized) throws IOException, ClassCastException {
		try (InputStream stream = new ByteArrayInputStream(serialized); DataInputStream in = new DataInputStream(stream)) {
			this.compound = NbtBinarySerializer.DEFAULT.deserializeCompound(in);
		}
	}
	public ProtocolLibNBTCompound(InputStream stream) throws IOException, ClassCastException {
		try (DataInputStream in = new DataInputStream(stream)) {
			this.compound = NbtBinarySerializer.DEFAULT.deserializeCompound(in);
		}
	}
	public ProtocolLibNBTCompound(String fromString) throws ClassCastException {
		this.compound = NbtFactory.ofCompound("tag");
		//this.compound = (NbtCompound) manager.parseMojangson(fromString);
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
		
		NbtCompound compound = readCompound();
		compound.remove(name);
		writeCompound(compound);
	}
	
	public String[] getTagNames() {
		return readCompound().getKeys().toArray(new String[0]);
	}
	
	public void setBoolean(String name, boolean data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NbtCompound compound = readCompound();
		compound.putObject(name, Boolean.valueOf(data));
		writeCompound(compound);
	}
	public boolean getBoolean(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return ((Boolean) readCompound().getObject(name)).booleanValue();
	}
	public void setByte(String name, byte data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NbtCompound compound = readCompound();
		compound.put(name, data);
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
		
		NbtCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public short getShort(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return readCompound().getShort(name).shortValue();
	}
	public void setInt(String name, int data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NbtCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public int getInt(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return readCompound().getInteger(name);
	}
	public void setLong(String name, long data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NbtCompound compound = readCompound();
		compound.put(name, data);
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
		
		NbtCompound compound = readCompound();
		compound.put(name, data);
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
		
		NbtCompound compound = readCompound();
		compound.put(name, data);
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
		
		NbtCompound compound = readCompound();
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
		
		NbtCompound compound = readCompound();
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
		
		NbtCompound compound = readCompound();
		compound.put(name, data);
		writeCompound(compound);
	}
	public int[] getIntArray(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return readCompound().getIntegerArray(name);
	}
	
	public void setObject(String name, Object data) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NbtCompound compound = readCompound();
		compound.putObject(name, ProtocolLibUtil.tryUnwrap(data));
		writeCompound(compound);
	}
	public Object getObject(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return ProtocolLibUtil.tryWrap(this, readCompound().getObject(name));
	}
	@SuppressWarnings("unchecked")
	public <T> T getObject(String name, Class<T> type) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		Object retVal = readCompound().getObject(name);
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
		
		NbtCompound compound = readCompound();
		ProtocolLibNBTCompound retVal = new ProtocolLibNBTCompound(this, compound.getCompoundOrDefault(name));
		writeCompound(compound);
		return retVal;
	}
	public INBTCompound getCompound(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return new ProtocolLibNBTCompound(this, readCompound().getCompound(name));
	}
	
	public INBTList addList(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		NbtCompound compound = readCompound();
		ProtocolLibNBTList retVal = new ProtocolLibNBTList(this, compound.getListOrDefault(name));
		writeCompound(compound);
		return retVal;
	}
	public INBTList getList(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return new ProtocolLibNBTList(this, readCompound().getList(name));
	}
	
	public byte[] serialize() throws IOException {
		try (ByteArrayOutputStream retVal = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(retVal)) {
			NbtBinarySerializer.DEFAULT.serialize(readCompound(), out);
			return retVal.toByteArray();
		}
	}
	public void serialize(OutputStream stream) throws IOException {
		try (DataOutputStream out = new DataOutputStream(stream)) {
			NbtBinarySerializer.DEFAULT.serialize(readCompound(), out);
		}
	}
	public String toString() {
		return ProtocolLibUtil.toMojangson(readCompound());
	}
	
	public boolean isValidCompound() {
		return true;
	}
	
	public ProtocolLibNBTCompound getRoot() {
		return (parentCompound != null) ? parentCompound.getRoot() : (parentList != null) ? parentList.getRoot() : this;
	}
	public NbtCompound getSelf() {
		return readCompound();
	}
	public void writeLast() {
		writeCompound(last);
	}
	
	//private
	private synchronized NbtCompound readCompound() {
		try {
			if (stack != null) {
				last = NbtFactory.asCompound(NbtFactory.fromItemTag(stack));
				if (last == null) {
					last = NbtFactory.ofCompound("tag");
				}
				return last;
			}
			if (entity != null) {
				NbtFactory.fromNMS(BukkitConverters.getEntityConverter(entity.getWorld()).getGeneric(MinecraftReflection.getCraftEntityClass(), entity), "tag");
				if (last == null) {
					last = NbtFactory.ofCompound("tag");
				}
				return last;
			}
			if (block != null) {
				last = NbtFactory.readBlockState(block);
				if (last == null) {
					last = NbtFactory.ofCompound("tag");
				}
				return last;
			}
			if (file != null) {
				last = NbtFactory.fromFile(file.getAbsolutePath());
				if (last == null) {
					last = NbtFactory.ofCompound("tag");
				}
				return last;
			}
		} catch (Exception ex) {
			last = NbtFactory.ofCompound("tag");
			return last;
		}
		
		last = compound;
		if (last == null) {
			last = NbtFactory.ofCompound("tag");
		}
		return last;
	}
	private synchronized void writeCompound(NbtCompound compound) {
		if (parentCompound != null || parentList != null) {
			getRoot().writeLast();
			return;
		}
		
		try {
			if (stack != null) {
				NbtFactory.setItemTag(stack, compound);
			}
			if (entity != null) {
				//manager.write(entity, compound); // ??? How do??
			}
			if (block != null) {
				NbtFactory.writeBlockState(block, compound);
			}
			if (file != null) {
				NbtFactory.toFile(compound, file.getAbsolutePath());
			}
		} catch (Exception ex) {
			
		}
	}
}
