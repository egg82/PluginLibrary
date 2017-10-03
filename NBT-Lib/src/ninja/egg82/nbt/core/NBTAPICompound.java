package ninja.egg82.nbt.core;

import de.tr7zw.itemnbtapi.NBTCompound;
import ninja.egg82.exceptions.ArgumentNullException;

public class NBTAPICompound implements INBTCompound {
	//vars
	private NBTCompound compound = null;
	
	//constructor
	public NBTAPICompound(NBTCompound compound) {
		this.compound = compound;
	}
	
	//public
	public boolean hasTag(String name) {
		if (name == null) {
			return false;
		}
		
		return compound.hasKey(name);
	}
	public void removeTag(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		compound.removeKey(name);
	}
	
	public String[] getTagNames() {
		return compound.getKeys().toArray(new String[0]);
	}
	
	public void setBoolean(String name, boolean data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		compound.setBoolean(name, data);
	}
	public boolean getBoolean(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return compound.getBoolean(name);
	}
	public void setByte(String name, byte data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		compound.setByte(name, data);
	}
	public byte getByte(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return compound.getByte(name);
	}
	public void setShort(String name, short data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		compound.setShort(name, data);
	}
	public short getShort(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return compound.getShort(name);
	}
	public void setInt(String name, int data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		compound.setInteger(name, data);
	}
	public int getInt(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return compound.getInteger(name);
	}
	public void setLong(String name, long data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		compound.setLong(name, data);
	}
	public long getLong(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return compound.getLong(name);
	}
	public void setFloat(String name, float data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		compound.setFloat(name, data);
	}
	public float getFloat(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return compound.getFloat(name);
	}
	public void setDouble(String name, double data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		compound.setDouble(name, data);
	}
	public double getDouble(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return compound.getDouble(name);
	}
	public void setString(String name, String data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		compound.setString(name, data);
	}
	public String getString(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return compound.getString(name);
	}
	public void setByteArray(String name, byte[] data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		compound.setByteArray(name, data);
	}
	public byte[] getByteArray(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return compound.getByteArray(name);
	}
	public void setIntArray(String name, int[] data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		compound.setIntArray(name, data);
	}
	public int[] getIntArray(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return compound.getIntArray(name);
	}
	
	public void setObject(String name, Object data) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		compound.setObject(name, data);
	}
	public Object getObject(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return compound.getObject(name, Object.class);
	}
	public <T> T getObject(String name, Class<T> type) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return compound.getObject(name, type);
	}
	
	public INBTCompound addCompound(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return new NBTAPICompound(compound.addCompound(name));
	}
	public INBTCompound getCompound(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		NBTCompound c = compound.getCompound(name);
		return (c != null) ? new NBTAPICompound(c) : null;
	}
	
	public boolean isValidCompound() {
		return true;
	}
	public boolean supportsPrimitiveLists() {
		return false;
	}
	
	//private
	
}
