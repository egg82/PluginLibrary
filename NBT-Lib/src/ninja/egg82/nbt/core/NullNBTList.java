package ninja.egg82.nbt.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NullNBTList implements INBTList {
	//vars
	
	//constructor
	public NullNBTList() {
		
	}
	
	//public
	public int size() {
		return 0;
	}
	public boolean isEmpty() {
		return true;
	}
	
	public boolean contains(Object o) {
		return false;
	}
	public boolean containsAll(Collection<?> c) {
		return false;
	}
	
	public Object[] toArray() {
		return null;
	}
	public <T> T[] toArray(T[] a) {
		return null;
	}
	
	public void add(int index, Object element) {
		
	}
	public boolean add(Object e) {
		return false;
	}
	public Object set(int index, Object element) {
		return null;
	}
	public boolean addAll(Collection<? extends Object> c) {
		return false;
	}
	public boolean addAll(int index, Collection<? extends Object> c) {
		return false;
	}
	
	public boolean remove(Object o) {
		return false;
	}
	public Object remove(int index) {
		return null;
	}
	public boolean removeAll(Collection<?> c) {
		return false;
	}
	public boolean retainAll(Collection<?> c) {
		return false;
	}
	
	public void clear() {
		
	}
	
	public Object get(int index) {
		return null;
	}
	
	public int indexOf(Object o) {
		return -1;
	}
	public int lastIndexOf(Object o) {
		return -1;
	}
	
	public Iterator<Object> iterator() {
		return null;
	}
	public ListIterator<Object> listIterator() {
		return null;
	}
	public ListIterator<Object> listIterator(int index) {
		return null;
	}
	
	public List<Object> subList(int fromIndex, int toIndex) {
		return null;
	}
	
	public INBTCompound addCompound(int index) {
		return new NullNBTCompound();
	}
	public INBTCompound addCompound() {
		return new NullNBTCompound();
	}
	public INBTCompound setCompound(int index) {
		return new NullNBTCompound();
	}
	public INBTCompound getCompound(int index) {
		return new NullNBTCompound();
	}
	
	public INBTList addList(int index) {
		return new NullNBTList();
	}
	public INBTList addList() {
		return new NullNBTList();
	}
	public INBTList setList(int index) {
		return new NullNBTList();
	}
	public INBTList getList(int index) {
		return new NullNBTList();
	}
	
	public Object getObject(int index) {
		return null;
	}
	public <T> T getObject(int index, Class<T> type) {
		return null;
	}
	
	public boolean isValidList() {
		return false;
	}
	
	//private
	
}
