package ninja.egg82.nbt.utils;

import java.util.Arrays;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.api.NBTCompound.NBTEntrySet;
import me.dpohvar.powernbt.api.NBTList.NBTIterator;
import ninja.egg82.nbt.reflection.powernbt.PowerNBTCompound;
import ninja.egg82.nbt.reflection.powernbt.PowerNBTList;

public class PowerNBTUtil {
	//vars
	
	//constructor
	public PowerNBTUtil() {
		
	}
	
	//public
	public static Object tryWrap(PowerNBTCompound parent, Object obj) {
		if (obj == null) {
			return null;
		}
		
		if (obj instanceof NBTCompound) {
			return new PowerNBTCompound(parent, (NBTCompound) obj);
		} else if (obj instanceof NBTList) {
			return new PowerNBTList(parent, (NBTList) obj);
		}
		return obj;
	}
	public static Object tryWrap(PowerNBTList parent, Object obj) {
		if (obj == null) {
			return null;
		}
		
		if (obj instanceof NBTCompound) {
			return new PowerNBTCompound(parent, (NBTCompound) obj);
		} else if (obj instanceof NBTList) {
			return new PowerNBTList(parent, (NBTList) obj);
		}
		return obj;
	}
	public static Object tryUnwrap(Object obj) {
		if (obj == null) {
			return null;
		}
		
		if (obj instanceof PowerNBTCompound) {
			return ((PowerNBTCompound) obj).getSelf();
		} else if (obj instanceof PowerNBTList) {
			return ((PowerNBTList) obj).getSelf();
		}
		
		return obj;
	}
	
	public static String toMojangson(NBTCompound compound) {
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
	public static String toMojangson(NBTList list) {
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
	
	//private
	
}
