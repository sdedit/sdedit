// Copyright (c) 2006 - 2011, Markus Strauch.
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// * Redistributions of source code must retain the above copyright notice, 
// this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
// this list of conditions and the following disclaimer in the documentation 
// and/or other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
// THE POSSIBILITY OF SUCH DAMAGE.
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
