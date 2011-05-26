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
