package ninja.egg82.nbt.core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface INBTList extends List<Object> {
	//functions
	INBTCompound addCompound();
	INBTCompound addCompound(int index);
	INBTCompound setCompound(int index);
	INBTCompound getCompound(int index);
	
	INBTList addList();
	INBTList addList(int index);
	INBTList setList(int index);
	INBTList getList(int index);
	
	Object getObject(int index);
	<T> T getObject(int index, Class<T> type);
	
	byte[] serialize() throws IOException;
	void serialize(OutputStream stream) throws IOException;
	String toString();
	
	boolean isValidList();
}
