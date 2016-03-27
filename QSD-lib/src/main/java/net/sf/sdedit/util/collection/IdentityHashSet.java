package net.sf.sdedit.util.collection;

import java.util.AbstractSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

public class IdentityHashSet<E> extends AbstractSet<E> {

	private Map<E,Object> map;
	
	private static final Object VALUE = new Object();
	
	public IdentityHashSet() {
		map = new IdentityHashMap<E, Object>();
	}
	
	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	@Override
	public boolean add(E e) {
		return map.put(e, VALUE) == null;
	}

	@Override
	public boolean remove(Object o) {
		return map.remove(o) == VALUE;
	}

	@Override
	public void clear() {
		map.clear();
	}

}
