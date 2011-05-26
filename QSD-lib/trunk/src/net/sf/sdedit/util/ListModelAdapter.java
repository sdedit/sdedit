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

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.sf.sdedit.util.CollectionState.CollectionStateChangeListener;

public class ListModelAdapter implements ListModel, CollectionStateChangeListener {
	
	private List<ListDataListener> listeners;
	
	private CollectionState collectionState;
	
	private Object [] data;
	
	public ListModelAdapter () {
		listeners = new LinkedList<ListDataListener>();
		collectionState = new CollectionState();
		collectionState.addListener(this);
	}
	
	public void setData (Collection<?> data) {
		this.data = data.toArray();
		collectionState.update(data);
	}

	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}
	
	public Object [] getData () {
		return data;
	}

	public Object getElementAt(int index) {
		return data[index];
	}

	public int getSize() {
		if (data == null) {
			return 0;
		}
		return data.length;
	}

	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	public void itemsAdded(int[] indices) {
		ListDataEvent lde = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, indices[0], indices[indices.length-1]);
		for (ListDataListener listener : listeners) {
			listener.intervalAdded(lde);			
		}
	}

	public void itemsChanged(int[] indices) {
		for (ListDataListener listener : listeners) {
			for (int i : indices) {
				ListDataEvent lde = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, i, i);
				listener.contentsChanged(lde);
			}
		}
	}

	public void itemsRemoved(int[] indices) {
		ListDataEvent lde = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, indices[0], indices[indices.length-1]);
		for (ListDataListener listener : listeners) {
			listener.intervalRemoved(lde);			
		}
	}

}
