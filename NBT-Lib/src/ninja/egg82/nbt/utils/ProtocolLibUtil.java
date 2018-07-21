package ninja.egg82.nbt.utils;

import java.util.Arrays;
import java.util.Iterator;

import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtList;

import ninja.egg82.nbt.reflection.protocollib.ProtocolLibNBTCompound;
import ninja.egg82.nbt.reflection.protocollib.ProtocolLibNBTList;

public class ProtocolLibUtil {
	//vars
	
	//constructor
	public ProtocolLibUtil() {
		
	}
	
	//public
	public static Object tryWrap(ProtocolLibNBTCompound parent, Object obj) {
		if (obj == null) {
			return null;
		}
		
		if (obj instanceof NbtCompound) {
			return new ProtocolLibNBTCompound(parent, (NbtCompound) obj);
		} else if (obj instanceof NbtList) {
			return new ProtocolLibNBTList(parent, (NbtList<?>) obj);
		}
		return obj;
	}
	public static Object tryWrap(ProtocolLibNBTList parent, Object obj) {
		if (obj == null) {
			return null;
		}
		
		if (obj instanceof NbtCompound) {
			return new ProtocolLibNBTCompound(parent, (NbtCompound) obj);
		} else if (obj instanceof NbtList) {
			return new ProtocolLibNBTList(parent, (NbtList<?>) obj);
		}
		return obj;
	}
	public static Object tryUnwrap(Object obj) {
		if (obj == null) {
			return null;
		}
		
		if (obj instanceof ProtocolLibNBTCompound) {
			return ((ProtocolLibNBTCompound) obj).getSelf();
		} else if (obj instanceof ProtocolLibNBTList) {
			return ((ProtocolLibNBTList) obj).getSelf();
		}
		
		return obj;
	}
	
	public static String toMojangson(NbtCompound compound) {
		StringBuilder sb = new StringBuilder().append('{');
		
		for (Iterator<NbtBase<?>> i = compound.iterator(); i.hasNext();) {
			NbtBase<?> kvp = i.next();
			Object v = kvp.getValue();
			
			sb.append(kvp.getName()).append(':');
			if (v instanceof byte[]) {
				sb.append(Arrays.toString((byte[]) v).replaceAll("\\s+", ""));
			} else if (v instanceof int[]) {
				sb.append(Arrays.toString((int[]) v).replaceAll("\\s+", ""));
			} else if (v instanceof NbtCompound) {
				sb.append(toMojangson((NbtCompound) v));
			} else if (v instanceof NbtList) {
				sb.append(toMojangson((NbtList<?>) v));
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
	public static String toMojangson(NbtList<?> list) {
		StringBuilder sb = new StringBuilder().append('[');
		
		for (Iterator<?> i = list.iterator(); i.hasNext();) {
			Object v = i.next();
			
			if (v instanceof byte[]) {
				sb.append(Arrays.toString((byte[]) v).replaceAll("\\s+", ""));
			} else if (v instanceof int[]) {
				sb.append(Arrays.toString((int[]) v).replaceAll("\\s+", ""));
			} else if (v instanceof NbtCompound) {
				sb.append(toMojangson((NbtCompound) v));
			} else if (v instanceof NbtList) {
				sb.append(toMojangson((NbtList<?>) v));
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
	
	//private
	
}
