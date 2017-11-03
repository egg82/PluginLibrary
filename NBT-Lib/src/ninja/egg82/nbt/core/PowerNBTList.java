package ninja.egg82.nbt.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;
import ninja.egg82.utils.ReflectUtil;

public class PowerNBTList implements INBTList {
	//vars
	private PowerNBTCompound parentCompound = null;
	private PowerNBTList parentList = null;
	private NBTList list = null;
	
	//constructor
	public PowerNBTList(PowerNBTCompound parent, NBTList list) {
		this.parentCompound = parent;
		this.list = list;
	}
	public PowerNBTList(PowerNBTList parent, NBTList list) {
		this.parentList = parent;
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
		return list.contains(tryUnwrap(o));
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
			retVal[i] = tryWrap(retVal[i]);
		}
		return retVal;
	}
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		T[] retVal = list.toArray(a);
		for (int i = 0; i < retVal.length; i++) {
			retVal[i] = (T) tryWrap(retVal[i]);
		}
		return retVal;
	}
	
	public void add(int index, Object element) {
		list.add(index, tryUnwrap(element));
		writeCompound();
	}
	public boolean add(Object e) {
		boolean retVal = list.add(tryUnwrap(e));
		if (retVal) {
			writeCompound();
		}
		return retVal;
	}
	public Object set(int index, Object element) {
		Object retVal = list.set(index, tryUnwrap(element));
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
		boolean retVal = list.remove(tryUnwrap(o));
		if (retVal) {
			writeCompound();
		}
		return retVal;
	}
	public Object remove(int index) {
		Object retVal = tryWrap(list.remove(index));
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
			i.set(tryUnwrap(i));
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
		return tryWrap(list.get(index));
	}
	
	public int indexOf(Object o) {
		return list.indexOf(tryUnwrap(o));
	}
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(tryUnwrap(o));
	}
	
	public Iterator<Object> iterator() {
		return new PowerNBTIterator(this, list.listIterator());
	}
	public ListIterator<Object> listIterator() {
		return new PowerNBTIterator(this, list.listIterator());
	}
	public ListIterator<Object> listIterator(int index) {
		return new PowerNBTIterator(this, list.listIterator(index));
	}
	
	public List<Object> subList(int fromIndex, int toIndex) {
		return new PowerNBTSubList(this, list.subList(fromIndex, toIndex));
	}
	
	public INBTCompound addCompound(int index) {
		PowerNBTCompound retVal = new PowerNBTCompound(this, new NBTCompound());
		list.add(index, retVal.getSelf());
		writeCompound();
		return retVal;
	}
	public INBTCompound addCompound() {
		PowerNBTCompound retVal = new PowerNBTCompound(this, new NBTCompound());
		boolean b = list.add(retVal.getSelf());
		if (b) {
			writeCompound();
		}
		return retVal;
	}
	public INBTCompound setCompound(int index) {
		PowerNBTCompound retVal = new PowerNBTCompound(this, new NBTCompound());
		list.set(index, retVal.getSelf());
		writeCompound();
		return retVal;
	}
	public INBTCompound getCompound(int index) {
		return getObject(index, INBTCompound.class);
	}
	
	public INBTList addList(int index) {
		PowerNBTList retVal = new PowerNBTList(this, new NBTList());
		list.add(index, retVal.getSelf());
		writeCompound();
		return retVal;
	}
	public INBTList addList() {
		PowerNBTList retVal = new PowerNBTList(this, new NBTList());
		boolean b = list.add(retVal.getSelf());
		if (b) {
			writeCompound();
		}
		return retVal;
	}
	public INBTList setList(int index) {
		PowerNBTList retVal = new PowerNBTList(this, new NBTList());
		list.set(index, retVal.getSelf());
		writeCompound();
		return retVal;
	}
	public INBTList getList(int index) {
		return getObject(index, INBTList.class);
	}
	
	public Object getObject(int index) {
		return tryWrap(list.get(index));
	}
	@SuppressWarnings("unchecked")
	public <T> T getObject(int index, Class<T> type) {
		Object retVal = tryWrap(list.get(index));
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
	
	public PowerNBTCompound getRoot() {
		return (parentCompound != null) ? parentCompound.getRoot() : parentList.getRoot();
	}
	public NBTList getSelf() {
		return list;
	}
	
	public boolean isValidList() {
		return true;
	}
	
	//private
	private synchronized void writeCompound() {
		getRoot().writeLast();
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
}

class PowerNBTIterator implements ListIterator<Object> {
	//vars
	private PowerNBTList parentList = null;
	private ListIterator<Object> iterator = null;
	
	//constructor
	public PowerNBTIterator(PowerNBTList parentList, ListIterator<Object> iterator) {
		this.parentList = parentList;
		this.iterator = iterator;
	}
	
	//public
	public boolean hasNext() {
		return iterator.hasNext();
	}
	public Object next() {
		return tryWrap(iterator.next());
	}
	public boolean hasPrevious() {
		return iterator.hasPrevious();
	}
	public Object previous() {
		return tryWrap(iterator.previous());
	}
	
	public int nextIndex() {
		return iterator.nextIndex();
	}
	public int previousIndex() {
		return iterator.previousIndex();
	}
	
	public void add(Object o) {
		iterator.add(tryUnwrap(o));
		writeCompound();
	}
	public void remove() {
		iterator.remove();
		writeCompound();
	}
	public void set(Object o) {
		iterator.set(tryUnwrap(o));
		writeCompound();
	}
	
	public PowerNBTCompound getRoot() {
		return parentList.getRoot();
	}
	
	//private
	private synchronized void writeCompound() {
		getRoot().writeLast();
	}
	
	private Object tryWrap(Object obj) {
		if (obj == null) {
			return null;
		}
		
		if (ReflectUtil.doesExtend(NBTCompound.class, obj.getClass())) {
			return new PowerNBTCompound(parentList, (NBTCompound) obj);
		} else if (ReflectUtil.doesExtend(NBTList.class, obj.getClass())) {
			return new PowerNBTList(parentList, (NBTList) obj);
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
}
class PowerNBTSubList implements List<Object> {
	//vars
	private PowerNBTList parentList = null;
	private List<Object> list = null;
	
	//constructor
	public PowerNBTSubList(PowerNBTList parentList, List<Object> list) {
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
		return list.contains(tryUnwrap(o));
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
			retVal[i] = tryWrap(retVal[i]);
		}
		return retVal;
	}
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		T[] retVal = list.toArray(a);
		for (int i = 0; i < retVal.length; i++) {
			retVal[i] = (T) tryWrap(retVal[i]);
		}
		return retVal;
	}
	
	public void add(int index, Object element) {
		list.add(index, tryUnwrap(element));
		writeCompound();
	}
	public boolean add(Object e) {
		boolean retVal = list.add(tryUnwrap(e));
		if (retVal) {
			writeCompound();
		}
		return retVal;
	}
	public Object set(int index, Object element) {
		Object retVal = list.set(index, tryUnwrap(element));
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
		boolean retVal = list.remove(tryUnwrap(o));
		if (retVal) {
			writeCompound();
		}
		return retVal;
	}
	public Object remove(int index) {
		Object retVal = tryWrap(list.remove(index));
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
			i.set(tryUnwrap(i));
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
		return tryWrap(list.get(index));
	}
	
	public int indexOf(Object o) {
		return list.indexOf(tryUnwrap(o));
	}
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(tryUnwrap(o));
	}
	
	public Iterator<Object> iterator() {
		return new PowerNBTIterator(parentList, list.listIterator());
	}
	public ListIterator<Object> listIterator() {
		return new PowerNBTIterator(parentList, list.listIterator());
	}
	public ListIterator<Object> listIterator(int index) {
		return new PowerNBTIterator(parentList, list.listIterator(index));
	}
	
	public List<Object> subList(int fromIndex, int toIndex) {
		return new PowerNBTSubList(parentList, list.subList(fromIndex, toIndex));
	}
	
	public PowerNBTCompound getRoot() {
		return parentList.getRoot();
	}
	
	//private
	private synchronized void writeCompound() {
		getRoot().writeLast();
	}
	
	private Object tryWrap(Object obj) {
		if (obj == null) {
			return null;
		}
		
		if (ReflectUtil.doesExtend(NBTCompound.class, obj.getClass())) {
			return new PowerNBTCompound(parentList, (NBTCompound) obj);
		} else if (ReflectUtil.doesExtend(NBTList.class, obj.getClass())) {
			return new PowerNBTList(parentList, (NBTList) obj);
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
}
