package net.sf.sdedit.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CollectionState {

	private Object[] state;

	private List<CollectionStateChangeListener> listeners;
	
	public CollectionState () {
		state = new Object [0];
		listeners = new LinkedList<CollectionStateChangeListener>();
	}
	
	public void addListener (CollectionStateChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(CollectionStateChangeListener listener) {
		listeners.remove(listener);
	}
	
	public Object [] getState () {
		return state;
	}

	public void update(Collection<?> newColl) {
		Object[] newState = newColl.toArray();
		int m = state.length;
		int n = newState.length;
		int t = Math.max(m, n);
		List<Integer> added = new LinkedList<Integer>();
		List<Integer> removed = new LinkedList<Integer>();
		List<Integer> changed = new LinkedList<Integer>();
		for (int i = 0; i < t; i++) {
			if (i >= m) {
				added.add(i);
			} else if (i >= n) {
				removed.add(i);
			} else if (state[i].equals(newState[i])) {
				changed.add(i);
			}
		}
		int [] _added = toIntArray(added);
		int [] _removed = toIntArray(removed);
		int [] _changed = toIntArray(changed);
		for (CollectionStateChangeListener listener : listeners) {
			listener.itemsChanged(_changed);
			if (_removed.length > 0) {
				listener.itemsRemoved(_removed);
			} else if (_added.length > 0){
				listener.itemsAdded(_added);
			}
		}
		state = newState;
	}
	
	private static int [] toIntArray (List<Integer> list) {
		int [] arr = new int [list.size()];
		int j = 0;
		for (Integer i : list) {
			arr [j] = i;
			j++;
		}
		return arr;
	}

	public interface CollectionStateChangeListener {

		public void itemsAdded(int[] indices);

		public void itemsRemoved(int[] indices);

		public void itemsChanged(int[] indices);

	}

}
