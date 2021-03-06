package ninja.egg82.nbt.core;

import java.io.IOException;
import java.io.OutputStream;

public interface INBTCompound {
	//functions
	boolean hasTag(String name);
	void removeTag(String name);
	
	String[] getTagNames();
	
	void setBoolean(String name, boolean data);
	boolean getBoolean(String name);
	void setByte(String name, byte data);
	byte getByte(String name);
	void setShort(String name, short data);
	short getShort(String name);
	void setInt(String name, int data);
	int getInt(String name);
	void setLong(String name, long data);
	long getLong(String name);
	void setFloat(String name, float data);
	float getFloat(String name);
	void setDouble(String name, double data);
	double getDouble(String name);
	void setString(String name, String data);
	String getString(String name);
	void setByteArray(String name, byte[] data);
	byte[] getByteArray(String name);
	void setIntArray(String name, int[] data);
	int[] getIntArray(String name);
	
	void setObject(String name, Object data);
	Object getObject(String name);
	<T> T getObject(String name, Class<T> type);
	
	INBTCompound addCompound(String name);
	INBTCompound getCompound(String name);
	
	INBTList addList(String name);
	INBTList getList(String name);
	
	byte[] serialize() throws IOException;
	void serialize(OutputStream stream) throws IOException;
	String toString();
	
	boolean isValidCompound();
}
