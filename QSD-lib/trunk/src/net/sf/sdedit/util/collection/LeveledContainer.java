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

		@Override
		public boolean hasNext() {
			Iterator<T> iterator = iterator();
			if (iterator == null) {
				return false;
			}
			return iterator.hasNext();
		}

		@Override
		public T next() {
			T next = iterator().next();
			if (!iterator().hasNext()) {
				iterator = null;
			}
			return next;
		}

		@Override
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

	@Override
	public Iterator<T> iterator() {
		return new LCIterator();
	}

}
