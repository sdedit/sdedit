package net.sf.sdedit.util.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import net.sf.sdedit.util.Utilities;
import net.sf.sdedit.util.collection.DistinctObjectsMap.Key;

public class DistinctObjectsSet<E> implements Set<E>, Serializable {

	private DistinctObjectsMap<E, Boolean> backend;

	public static DistinctObjectsSet<Object> createFromArray(Object[] array) {
		DistinctObjectsSet<Object> set = new DistinctObjectsSet<Object>();
		for (Object obj : array) {
			set.add(obj);
		}
		return set;
	}

	public DistinctObjectsSet() {
		backend = new DistinctObjectsMap<E, Boolean>();
	}

	public boolean add(E e) {
		return backend.put(e, true) != null;
	}

	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for (E e : c) {
			changed |= !add(e);
		}
		return changed;
	}

	public void clear() {
		backend.clear();
	}

	public boolean contains(Object o) {
		return backend.get(o) != null;
	}

	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;

	}

	public boolean isEmpty() {
		return backend.isEmpty();
	}

	public Iterator<E> iterator() {
		return new Iter();
	}
	
	protected class Iter implements Iterator<E> {
		
		Iterator<Key<E>> iter;
		
		Iter () {
			iter = backend.map().keySet().iterator();
		}

		public boolean hasNext() {
			return iter.hasNext();
		}

		public E next() {
			return iter.next().object;
		}

		public void remove() {
			iter.remove();
		}
		
	}
	
	public boolean remove(Object o) {
		return backend.remove(o) != null;
	}

	public boolean removeAll(Collection<?> c) {
		boolean removed = false;
		for (Object o : c) {
			removed |= remove(o);
		}
		return removed;
	}

	@SuppressWarnings("unchecked")
	public boolean retainAll(Collection<?> c) {
		int s = size();
		clear();
		addAll((Collection<? extends E>) c);
		return size() != s;
	}

	public int size() {
		return backend.size();
	}
	
	public Object[] toArray() {
		ArrayList<Object> arr = new ArrayList<Object>();
		for (Key<E> key : backend.map().keySet()) {
			arr.add(key.object);
		}
		return arr.toArray();
	}

	public String toString() {
		return Arrays.toString(toArray());
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		return (T[]) Utilities.castArray(toArray(), a.getClass()
				.getComponentType());
	}

}
