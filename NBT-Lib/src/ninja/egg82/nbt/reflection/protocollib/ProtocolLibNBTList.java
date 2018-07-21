package ninja.egg82.nbt.reflection.protocollib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.comphenix.protocol.wrappers.nbt.io.NbtBinarySerializer;

import ninja.egg82.nbt.core.INBTCompound;
import ninja.egg82.nbt.core.INBTList;
import ninja.egg82.nbt.utils.NBTReflectUtil;
import ninja.egg82.nbt.utils.ProtocolLibUtil;

public class ProtocolLibNBTList implements INBTList {
	//TODO Finish this
	
	//vars
	private ProtocolLibNBTCompound parentCompound = null;
	private ProtocolLibNBTList parentList = null;
	private NbtList<?> list = null;
	
	//constructor
	public ProtocolLibNBTList(ProtocolLibNBTCompound parent, NbtList<?> list) {
		this.parentCompound = parent;
		this.list = list;
	}
	public ProtocolLibNBTList(ProtocolLibNBTList parent, NbtList<?> list) {
		this.parentList = parent;
		this.list = list;
	}
	public ProtocolLibNBTList(Inventory inventory) {
		List<NbtCompound> items = new ArrayList<NbtCompound>();
		if (inventory != null) {
			for (ItemStack item : inventory.getContents()) {
				items.add(NbtFactory.asCompound(NbtFactory.fromItemTag(item)));
			}
		}
		this.list = NbtFactory.ofList("tag", items);
	}
	public ProtocolLibNBTList(byte[] serialized) throws IOException, ClassCastException {
		try (InputStream stream = new ByteArrayInputStream(serialized); DataInputStream in = new DataInputStream(stream)) {
			this.list = NbtBinarySerializer.DEFAULT.deserializeList(in);
		}
	}
	public ProtocolLibNBTList(InputStream stream) throws IOException, ClassCastException {
		try (DataInputStream in = new DataInputStream(stream)) {
			this.list = NbtBinarySerializer.DEFAULT.deserializeList(in);
		}
	}
	public ProtocolLibNBTList(String fromString) throws ClassCastException {
		this.list = NbtFactory.ofList("tag");
		//this.list = (NbtList) manager.parseMojangson(fromString);
	}
	
	//public
	public int size() {
		return list.size();
	}
	public boolean isEmpty() {
		return (list.size() == 0) ? true : false;
	}
	
	public boolean contains(Object o) {
		Object unwrapped = ProtocolLibUtil.tryUnwrap(o);
		for (Object obj : list) {
			if (obj.equals(unwrapped)) {
				return true;
			}
		}
		return false;
	}
	public boolean containsAll(Collection<?> c) {
		for (Object l : c) {
			if (!contains(l)) {
				return false;
			}
		}
		return true;
	}
	
	public Object[] toArray() {
		Object[] retVal = list.getValue().toArray();
		for (int i = 0; i < retVal.length; i++) {
			retVal[i] = ProtocolLibUtil.tryWrap(this, retVal[i]);
		}
		return retVal;
	}
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		T[] retVal = list.getValue().toArray(a);
		for (int i = 0; i < retVal.length; i++) {
			retVal[i] = (T) ProtocolLibUtil.tryWrap(this, retVal[i]);
		}
		return retVal;
	}
	
	public void add(int index, Object element) {
		/*list.addClosest(index, ProtocolLibUtil.tryUnwrap(element));
		writeCompound();*/
	}
	public boolean add(Object e) {
		/*boolean retVal = list.addClosest(ProtocolLibUtil.tryUnwrap(e));
		if (retVal) {
			writeCompound();
		}
		return retVal;*/
		return false;
	}
	public Object set(int index, Object element) {
		/*Object retVal = list.set(index, ProtocolLibUtil.tryUnwrap(element));
		writeCompound();
		return retVal;*/
		return null;
	}
	public boolean addAll(Collection<? extends Object> c) {
		boolean retVal = false;
		for (Object o : c) {
			if (add(o)) {
				retVal = true;
			}
		}
		if (retVal) {
			writeCompound();
		}
		return retVal;
	}
	public boolean addAll(int index, Collection<? extends Object> c) {
		for (Object o : c) {
			add(index, o);
			index++;
		}
		writeCompound();
		return true;
	}
	
	public boolean remove(Object o) {
		int size = list.size();
		list.remove(ProtocolLibUtil.tryUnwrap(o));
		if (list.size() < size) {
			writeCompound();
			return true;
		}
		return false;
	}
	public Object remove(int index) {
		/*Object retVal = ProtocolLibUtil.tryWrap(this, list.remove(index));
		writeCompound();
		return retVal;*/
		
		return null;
	}
	public boolean removeAll(Collection<?> c) {
		boolean retVal = false;
		for (Object o : c) {
			if (remove(o)) {
				retVal = true;
			}
		}
		if (retVal) {
			writeCompound();
		}
		return retVal;
	}
	public boolean retainAll(Collection<?> c) {
		/*List<Object> c2 = Arrays.asList(c.toArray());
		
		for (ListIterator<Object> i = c2.listIterator(); i.hasNext(); i.next()) {
			i.set(ProtocolLibUtil.tryUnwrap(i));
		}
		
		boolean retVal = list.retainAll(c2);
		if (retVal) {
			writeCompound();
		}
		return retVal;*/
		return false;
	}
	
	public void clear() {
		if (list.size() > 0) {
			list.getValue().clear();
			writeCompound();
		}
	}
	
	public Object get(int index) {
		return ProtocolLibUtil.tryWrap(this, list.getValue(index));
	}
	
	public int indexOf(Object o) {
		//return list.indexOf(ProtocolLibUtil.tryUnwrap(o));
		return -1;
	}
	public int lastIndexOf(Object o) {
		//return list.lastIndexOf(ProtocolLibUtil.tryUnwrap(o));
		return -1;
	}
	
	public Iterator<Object> iterator() {
		//return new ProtocolLibNBTIterator(this, list.iterator());
		return null;
	}
	public ListIterator<Object> listIterator() {
		//return new ProtocolLibNBTIterator(this, list.iterator());
		return null;
	}
	public ListIterator<Object> listIterator(int index) {
		//return new ProtocolLibNBTIterator(this, list.listIterator(index));
		return null;
	}
	
	public List<Object> subList(int fromIndex, int toIndex) {
		//return new ProtocolLibNBTSubList(this, list.subList(fromIndex, toIndex));
		return null;
	}
	
	public INBTCompound addCompound(int index) {
		/*ProtocolLibNBTCompound retVal = new ProtocolLibNBTCompound(this, NbtFactory.ofCompound("tag"));
		list.add(index, retVal.getSelf());
		writeCompound();
		return retVal;*/
		return null;
	}
	public INBTCompound addCompound() {
		/*ProtocolLibNBTCompound retVal = new ProtocolLibNBTCompound(this, NbtFactory.ofCompound("tag"));
		boolean b = list.add(retVal.getSelf());
		if (b) {
			writeCompound();
		}
		return retVal;*/
		return null;
	}
	public INBTCompound setCompound(int index) {
		/*ProtocolLibNBTCompound retVal = new ProtocolLibNBTCompound(this, NbtFactory.ofCompound("tag"));
		list.set(index, retVal.getSelf());
		writeCompound();
		return retVal;*/
		return null;
	}
	public INBTCompound getCompound(int index) {
		return getObject(index, INBTCompound.class);
	}
	
	public INBTList addList(int index) {
		/*ProtocolLibNBTList retVal = new ProtocolLibNBTList(this, NbtFactory.ofList("tag"));
		list.add(index, retVal.getSelf());
		writeCompound();
		return retVal;*/
		return null;
	}
	public INBTList addList() {
		/*ProtocolLibNBTList retVal = new ProtocolLibNBTList(this, NbtFactory.ofList("tag"));
		boolean b = list.add(retVal.getSelf());
		if (b) {
			writeCompound();
		}
		return retVal;*/
		return null;
	}
	public INBTList setList(int index) {
		/*ProtocolLibNBTList retVal = new ProtocolLibNBTList(this, NbtFactory.ofList("tag"));
		list.set(index, retVal.getSelf());
		writeCompound();
		return retVal;*/
		return null;
	}
	public INBTList getList(int index) {
		return getObject(index, INBTList.class);
	}
	
	public Object getObject(int index) {
		return ProtocolLibUtil.tryWrap(this, list.getValue(index));
	}
	@SuppressWarnings("unchecked")
	public <T> T getObject(int index, Class<T> type) {
		Object retVal = ProtocolLibUtil.tryWrap(this, list.getValue(index));
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
	
	public ProtocolLibNBTCompound getRoot() {
		return (parentCompound != null) ? parentCompound.getRoot() : parentList.getRoot();
	}
	public NbtList<?> getSelf() {
		return list;
	}
	
	public byte[] serialize() throws IOException {
		try (ByteArrayOutputStream retVal = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(retVal)) {
			NbtBinarySerializer.DEFAULT.serialize(list, out);
			return retVal.toByteArray();
		}
	}
	public void serialize(OutputStream stream) throws IOException {
		try (DataOutputStream out = new DataOutputStream(stream)) {
			NbtBinarySerializer.DEFAULT.serialize(list, out);
		}
	}
	public String toString() {
		return ProtocolLibUtil.toMojangson(list);
	}
	
	public boolean isValidList() {
		return true;
	}
	
	//private
	private synchronized void writeCompound() {
		getRoot().writeLast();
	}
}

class ProtocolLibNBTIterator implements ListIterator<Object> {
	//vars
	private ProtocolLibNBTList parentList = null;
	private ListIterator<Object> iterator = null;
	
	//constructor
	public ProtocolLibNBTIterator(ProtocolLibNBTList parentList, ListIterator<Object> iterator) {
		this.parentList = parentList;
		this.iterator = iterator;
	}
	
	//public
	public boolean hasNext() {
		return iterator.hasNext();
	}
	public Object next() {
		return ProtocolLibUtil.tryWrap(parentList, iterator.next());
	}
	public boolean hasPrevious() {
		return iterator.hasPrevious();
	}
	public Object previous() {
		return ProtocolLibUtil.tryWrap(parentList, iterator.previous());
	}
	
	public int nextIndex() {
		return iterator.nextIndex();
	}
	public int previousIndex() {
		return iterator.previousIndex();
	}
	
	public void add(Object o) {
		iterator.add(ProtocolLibUtil.tryUnwrap(o));
		writeCompound();
	}
	public void remove() {
		iterator.remove();
		writeCompound();
	}
	public void set(Object o) {
		iterator.set(ProtocolLibUtil.tryUnwrap(o));
		writeCompound();
	}
	
	public ProtocolLibNBTCompound getRoot() {
		return parentList.getRoot();
	}
	
	//private
	private synchronized void writeCompound() {
		getRoot().writeLast();
	}
}
class ProtocolLibNBTSubList implements List<Object> {
	//vars
	private ProtocolLibNBTList parentList = null;
	private List<Object> list = null;
	
	//constructor
	public ProtocolLibNBTSubList(ProtocolLibNBTList parentList, List<Object> list) {
		this.parentList = parentList;
		this.list = list;
	}
	
	//public
	public int size() {
		return list.size();
	}
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public boolean contains(Object o) {
		return list.contains(ProtocolLibUtil.tryUnwrap(o));
	}
	public boolean containsAll(Collection<?> c) {
		for (Object l : c) {
			if (!contains(l)) {
				return false;
			}
		}
		return true;
	}
	
	public Object[] toArray() {
		Object[] retVal = list.toArray();
		for (int i = 0; i < retVal.length; i++) {
			retVal[i] = ProtocolLibUtil.tryWrap(parentList, retVal[i]);
		}
		return retVal;
	}
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		T[] retVal = list.toArray(a);
		for (int i = 0; i < retVal.length; i++) {
			retVal[i] = (T) ProtocolLibUtil.tryWrap(parentList, retVal[i]);
		}
		return retVal;
	}
	
	public void add(int index, Object element) {
		list.add(index, ProtocolLibUtil.tryUnwrap(element));
		writeCompound();
	}
	public boolean add(Object e) {
		boolean retVal = list.add(ProtocolLibUtil.tryUnwrap(e));
		if (retVal) {
			writeCompound();
		}
		return retVal;
	}
	public Object set(int index, Object element) {
		Object retVal = list.set(index, ProtocolLibUtil.tryUnwrap(element));
		writeCompound();
		return retVal;
	}
	public boolean addAll(Collection<? extends Object> c) {
		boolean retVal = false;
		for (Object o : c) {
			if (add(o)) {
				retVal = true;
			}
		}
		if (retVal) {
			writeCompound();
		}
		return retVal;
	}
	public boolean addAll(int index, Collection<? extends Object> c) {
		for (Object o : c) {
			add(index, o);
			index++;
		}
		writeCompound();
		return true;
	}
	
	public boolean remove(Object o) {
		boolean retVal = list.remove(ProtocolLibUtil.tryUnwrap(o));
		if (retVal) {
			writeCompound();
		}
		return retVal;
	}
	public Object remove(int index) {
		Object retVal = ProtocolLibUtil.tryWrap(parentList, list.remove(index));
		writeCompound();
		return retVal;
	}
	public boolean removeAll(Collection<?> c) {
		boolean retVal = false;
		for (Object o : c) {
			if (remove(o)) {
				retVal = true;
			}
		}
		if (retVal) {
			writeCompound();
		}
		return retVal;
	}
	public boolean retainAll(Collection<?> c) {
		List<Object> c2 = Arrays.asList(c.toArray());
		
		for (ListIterator<Object> i = c2.listIterator(); i.hasNext(); i.next()) {
			i.set(ProtocolLibUtil.tryUnwrap(i));
		}
		
		boolean retVal = list.retainAll(c2);
		if (retVal) {
			writeCompound();
		}
		return retVal;
	}
	
	public void clear() {
		if (list.size() > 0) {
			list.clear();
			writeCompound();
		}
	}
	
	public Object get(int index) {
		return ProtocolLibUtil.tryWrap(parentList, list.get(index));
	}
	
	public int indexOf(Object o) {
		return list.indexOf(ProtocolLibUtil.tryUnwrap(o));
	}
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(ProtocolLibUtil.tryUnwrap(o));
	}
	
	public Iterator<Object> iterator() {
		return new ProtocolLibNBTIterator(parentList, list.listIterator());
	}
	public ListIterator<Object> listIterator() {
		return new ProtocolLibNBTIterator(parentList, list.listIterator());
	}
	public ListIterator<Object> listIterator(int index) {
		return new ProtocolLibNBTIterator(parentList, list.listIterator(index));
	}
	
	public List<Object> subList(int fromIndex, int toIndex) {
		return new ProtocolLibNBTSubList(parentList, list.subList(fromIndex, toIndex));
	}
	
	public ProtocolLibNBTCompound getRoot() {
		return parentList.getRoot();
	}
	
	//private
	private synchronized void writeCompound() {
		getRoot().writeLast();
	}
}
