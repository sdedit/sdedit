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

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sf.sdedit.util.CollectionState.CollectionStateChangeListener;

/**
 * Provides a read-only <tt>TableModel</tt> based on a collection.
 * The collection may freely be changed (see {@linkplain #setData(Collection)}),
 * then <tt>TableModelEvent</tt>s will be sent to listeners, reflecting 
 * objects that have been inserted, updated or removed from the collection,
 * as compared to the former one.
 * 
 * @author Markus Strauch
 *
 */
public class TableModelAdapter implements TableModel,
		CollectionStateChangeListener {

	private Object[][] data;
	
	private Object[] rawData;

	private String[] columns;

	private Class<?>[] classes;

	private List<TableModelListener> listeners;

	private CollectionState collectionState;
	
	private RowExpansion rowExpansion;
	
	private RowEditor rowEditor;
	
	private Object master;
	
	public TableModelAdapter(String[] columns, Class<?>[] classes, RowExpansion rowExpansion,
			RowEditor rowEditor) {
		this.columns = columns;
		this.classes = classes;
		listeners = new LinkedList<TableModelListener>();
		collectionState = new CollectionState();
		collectionState.addListener(this);
		this.rowExpansion = rowExpansion;
		this.rowEditor = rowEditor;
		
	}
	
	public TableModelAdapter(String [] columns, Class<?>[] classes) {
		this(columns, classes, null, null);
	}
	
	public void setMaster (Object master) {
		this.master = master;
	}
	
	public Object getMaster () {
		return master;
	}
	
	public Object getRawDataAt (int i) {
		return rawData [i];
	}
	
	public void setRowExpansion (RowExpansion rowExpansion) {
		this.rowExpansion = rowExpansion;
	}
	
	public void setRowEditor(RowEditor rowEditor) {
		this.rowEditor = rowEditor;
	}
	
	private Object [] expand (Object row) {
	    if (rowExpansion == null) {
	        return new Object[]{row};
	    }
	    return rowExpansion.expand(row);
	}
	
	public void setData(Collection<?> data) {
		this.rawData = data.toArray();
		this.data = new Object[data.size()][columns.length];
		int i = 0;
		for (Object row : rawData) {
			Object [] expanded = expand(row);
			for (int j = 0; j < expanded.length; j++) {
				this.data[i][j] = expanded[j];
			}
			i++;
		}
		collectionState.update(data);
	}

	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return classes[columnIndex];
	}

	public int getColumnCount() {
		return classes.length;
	}

	public String getColumnName(int columnIndex) {
		return columns[columnIndex];
	}

	public int getRowCount() {
		return data == null ? 0 : data.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return rowEditor != null &&
		rowEditor.isEditable(rawData[rowIndex], columnIndex);

	}

	public void removeTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		rowEditor.setValue(rawData[rowIndex], columnIndex, value);
		data [rowIndex] = rowExpansion.expand(rawData[rowIndex]);
		

	}

	/**
	 * @see net.sf.sdedit.util.CollectionState.CollectionStateChangeListener#itemsAdded(int[])
	 */
	public void itemsAdded(int[] indices) {
		TableModelEvent tme = new TableModelEvent(this, indices[0],
				indices[indices.length - 1], TableModelEvent.ALL_COLUMNS,
				TableModelEvent.INSERT);
		for (TableModelListener listener : listeners) {
			listener.tableChanged(tme);
		}
	}

	/**
	 * @see net.sf.sdedit.util.CollectionState.CollectionStateChangeListener#itemsChanged(int[])
	 */
	public void itemsChanged(int[] indices) {
		for (TableModelListener listener : listeners) {
			for (int i : indices) {
				TableModelEvent tme = new TableModelEvent(this, i, i,
						TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
				listener.tableChanged(tme);
			}
		}
	}

	/**
	 * @see net.sf.sdedit.util.CollectionState.CollectionStateChangeListener#itemsRemoved(int[])
	 */
	public void itemsRemoved(int[] indices) {
		TableModelEvent tme = new TableModelEvent(this, indices[0],
				indices[indices.length - 1], TableModelEvent.ALL_COLUMNS,
				TableModelEvent.DELETE);
		for (TableModelListener listener : listeners) {
			listener.tableChanged(tme);
		}
	}

	
	public static final RowExpansion DEFAULT_ROW_EXPANSION = new RowExpansion() {

		public Object[] expand(Object row) {
			if (row instanceof Object[]) {
				return (Object[]) row;
			}
			return ((Collection<?>) row).toArray();
		}

	};

	public static interface RowExpansion {

		public Object[] expand(Object row);

	}
	
	public static interface RowEditor {
		
		public boolean isEditable (Object row, int index);
		
		public void setValue (Object row, int index, Object value);
		
	}

}
