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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A <tt>DistinctObjectsMap</tt> maps objects onto values. It does not use a
 * comparator or the objects' <tt>equals</tt> and <tt>hashCode</tt> methods. Two
 * objects that are not identical are treated as different keys.
 * 
 * @author Markus Strauch
 * 
 * @param <T>
 *            the value type
 */
public class DistinctObjectsMap<K, V> implements Map<K, V>, Serializable {

	private static final long serialVersionUID = 4947237589993035807L;

	protected static class Entry<K, V> implements Map.Entry<K, V> {

		private Map.Entry<Key<K>, V> entry;

		Entry(Map.Entry<Key<K>, V> entry) {
			this.entry = entry;
		}

		public K getKey() {
			return entry.getKey().object;
		}

		public V getValue() {
			return entry.getValue();
		}

		public V setValue(V value) {
			return entry.setValue(value);
		}

	}

	protected static class Key<K> implements Serializable {

		private static final long serialVersionUID = -3034504503343962007L;
		
		protected K object;

		@SuppressWarnings("unchecked")
		public Key(Object object) {
			this.object = (K) object;
		}

		public int hashCode() {
			return System.identityHashCode(object);
		}

		@SuppressWarnings("unchecked")
		public boolean equals(Object otherKey) {
			return object == ((Key<K>) otherKey).object;
		}

		public String toString() {
			return object.toString();
		}
	}

	private HashMap<Key<K>, V> map;

	public DistinctObjectsMap() {
		this(false);
	}

	public DistinctObjectsMap(boolean linked) {
		if (linked) {
			map = new LinkedHashMap<Key<K>, V> ();
		} else {
			map = new HashMap<Key<K>, V>();
		}
	}

	public V put(K k, V v) {
		return map.put(new Key<K>(k), v);
	}

	public String toString() {
		return map.toString();
	}

	public void clear() {
		map.clear();
	}

	public V remove(Object k) {
		return map.remove(new Key<K>(k));
	}

	public int size() {
		return map.size();
	}

	public V get(Object k) {
		return map.get(new Key<K>(k));
	}

	public boolean containsKey(Object key) {
		return map.containsKey(new Key<K>(key));
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		Set<Map.Entry<Key<K>, V>> entries = map.entrySet();
		Set<Map.Entry<K, V>> entrySet = new HashSet<Map.Entry<K, V>>();
		for (Map.Entry<Key<K>, V> entry : entries) {
			entrySet.add(new Entry<K, V>(entry));
		}
		return entrySet;
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	/* 
	 * TODO falsch: liefert nicht alle Schlüssel, nur paarweise verschiedene
	 * bzgl. equals
	 */
	public Set<K> keySet() {
		Set<K> keySet = new HashSet<K>();
		for (Key<K> key : map.keySet()) {
			keySet.add(key.object);
		}
		return keySet;
	}
	
	protected HashMap<Key<K>, V> map () {
		return map;
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			map.put(new Key<K>(entry.getKey()), entry.getValue());
		}
	}

	public Collection<V> values() {
		return map.values();
	}
}