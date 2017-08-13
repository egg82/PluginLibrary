package ninja.egg82.nbt.core;

public class NullCompound implements INBTCompound {
	//vars
	
	//constructor
	public NullCompound() {
		
	}
	
	//public
	public boolean hasTag(String name) {
		return false;
	}
	public void removeTag(String name) {
		
	}
	
	public String[] getTagNames() {
		return null;
	}
	
	public void setByte(String name, byte data) {
		
	}
	public byte getByte(String name) {
		return 0;
	}
	public void setShort(String name, short data) {
		
	}
	public short getShort(String name) {
		return 0;
	}
	public void setInt(String name, int data) {
		
	}
	public int getInt(String name) {
		return 0;
	}
	public void setLong(String name, long data) {
		
	}
	public long getLong(String name) {
		return 0;
	}
	public void setFloat(String name, float data) {
		
	}
	public float getFloat(String name) {
		return 0;
	}
	public void setDouble(String name, double data) {
		
	}
	public double getDouble(String name) {
		return 0;
	}
	public void setString(String name, String data) {
		
	}
	public String getString(String name) {
		return null;
	}
	public void setByteArray(String name, byte[] data) {
		
	}
	public byte[] getByteArray(String name) {
		return null;
	}
	public void setIntArray(String name, int[] data) {
		
	}
	public int[] getIntArray(String name) {
		return null;
	}
	
	public void setObject(String name, Object data) {
		
	}
	public Object getObject(String name) {
		return null;
	}
	public <T> T getObject(String name, Class<T> type) {
		return null;
	}
	
	public INBTCompound addCompound(String name) {
		return new NullCompound();
	}
	public INBTCompound getCompound(String name) {
		return new NullCompound();
	}
	public boolean isValidCompound() {
		return false;
	}
	
	//private
	
}
