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
	
	public void setData(Collection<?> data) {
		this.rawData = data.toArray();
		this.data = new Object[data.size()][columns.length];
		int i = 0;
		for (Object row : rawData) {
			Object [] expanded = rowExpansion.expand(row);
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
		rowEditor.isEditable(data[rowIndex], columnIndex);

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
