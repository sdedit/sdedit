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
