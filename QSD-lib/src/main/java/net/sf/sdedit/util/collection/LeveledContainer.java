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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.sdedit.util.Utilities;

public class LeveledContainer<T> implements Iterable<T> {

	protected class LCIterator implements Iterator<T> {

		private Integer[] _levels;

		private int index;

		private Iterator<T> iterator;

		protected LCIterator() {
			_levels = levels.keySet().toArray(new Integer[0]);
			if (reverse) {
				_levels = Utilities.reverse(_levels);
			}
			index = 0;
		}

		protected Iterator<T> iterator() {
			if (iterator == null) {
				if (index < _levels.length) {
					iterator = levels.get(_levels[index]).iterator();
					index++;
				}
			}
			return iterator;
		}

		public boolean hasNext() {
			Iterator<T> iterator = iterator();
			if (iterator == null) {
				return false;
			}
			return iterator.hasNext();
		}

		public T next() {
			T next = iterator().next();
			if (!iterator().hasNext()) {
				iterator = null;
			}
			return next;
		}

		public void remove() {
			iterator().remove();

		}

	}

	private boolean reverse;

	private Map<Integer, List<T>> levels;

	public LeveledContainer() {
		this(false);

	}

	protected List<T> list(int level) {
		List<T> l = levels.get(level);
		if (l == null) {
			l = new LinkedList<T>();
			levels.put(level, l);
		}
		return l;
	}

	/**
	 * Determines the order of elements returned by {@linkplain #iterator()}.
	 * 
	 * @param reverse
	 */
	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	public void add(int level, T t) {
		list(level).add(t);
	}

	public void remove(int level, T t) {
		List<T> list = list(level);
		if (list != null) {
			list.remove(t);
			if (list.isEmpty()) {
				levels.remove(level);
			}
		}
	}

	public LeveledContainer(boolean reverse) {
		this.reverse = reverse;
		levels = new TreeMap<Integer, List<T>>();
	}

	public Iterator<T> iterator() {
		return new LCIterator();
	}

}
