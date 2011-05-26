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
package net.sf.sdedit.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import net.sf.sdedit.util.UIUtilities;

public class PropertyTable extends JComponent implements TableModel,
		TableCellRenderer, MouseListener {

	private JTable table;

	private Map<String, String> properties;

	private List<TableModelListener> listeners;

	private List<PropertyChangeListener> pclisteners;

	private Set<String> editableProperties;

	private TableCellRenderer tcr;

	private Map<String, JButton> buttons;

	public PropertyTable(int keyWidth, int valueWidth) {

		setLayout(new BorderLayout());

		buttons = new HashMap<String, JButton>();
		properties = new LinkedHashMap<String, String>();
		listeners = new LinkedList<TableModelListener>();
		pclisteners = new LinkedList<PropertyChangeListener>();
		editableProperties = new HashSet<String>();
		table = new JTable(this);
		table.setTableHeader(null);
		add(table, BorderLayout.WEST);

		UIUtilities.setColumnWidths(table, keyWidth, valueWidth, 100);
		table.setOpaque(false);
		table.setShowGrid(false);
		tcr = table.getDefaultRenderer(String.class);
		table.setDefaultRenderer(String.class, this);
		table.addMouseListener(this);

		// setOpaque(false);

	}

	public void addProperty(String key, String value, boolean editable,
			JButton button) {
		properties.put(key, value);
		if (editable) {
			editableProperties.add(key);
		}
		if (button != null) {
			buttons.put(key, button);
		}
	}

	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pclisteners.add(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		pclisteners.remove(pcl);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getColumnCount() {
		return 3; // buttons.isEmpty() ? 2 : 3;
	}

	public String getColumnName(int columnIndex) {
		return "";
	}

	public int getRowCount() {
		return properties.size();
	}

	protected String getKey(int rowIndex) {
		ArrayList<String> keys = new ArrayList<String>();
		keys.addAll(properties.keySet());
		String key = keys.get(rowIndex);
		return key;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 2) {
			return "";
		}
		String key = getKey(rowIndex);

		if (columnIndex == 0) {
			return key;
		}
		return properties.get(key);

	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex != 1) {
			return false;
		}
		String key = getKey(rowIndex);
		return editableProperties.contains(key);

	}

	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);

	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		String key = getKey(rowIndex);
		String old = properties.get(key);
		properties.put(key, String.valueOf(value));
		PropertyChangeEvent pce = new PropertyChangeEvent(this, key, old, value);
		TableModelEvent tme = new TableModelEvent(this, rowIndex, rowIndex,
				columnIndex);
		for (TableModelListener tml : listeners) {
			tml.tableChanged(tme);
		}
		for (PropertyChangeListener pcl : pclisteners) {
			pcl.propertyChange(pce);
		}

	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (column == 2) {
			String key = getKey(row);
			JButton button = buttons.get(key);
			if (button == null) {
				return new JLabel();
			}
			return button;
		}
		boolean editable = isCellEditable(row, column);
		int borderRight = column == 0 ? 5 : 1;
		JLabel label = (JLabel) tcr.getTableCellRendererComponent(table, value,
				false, false, row, column);
		if (editable) {
			label.setBorder(BorderFactory.createCompoundBorder(

			BorderFactory.createLineBorder(Color.black, 1), BorderFactory
					.createEmptyBorder(13, 0, 13, 0)));
		} else {
			label.setBorder(BorderFactory.createEmptyBorder(14, 1, 14, borderRight));
		}
		label.setOpaque(editable);
		if (column == 0) {
			label.setHorizontalAlignment(SwingConstants.TRAILING);
		} else {
			label.setHorizontalAlignment(SwingConstants.LEADING);
		}
		return label;
	}

	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			int column = table.getSelectedColumn();
			if (column == 2) {
				int row = table.getSelectedRow();
				String key = getKey(row);
				if (key != null) {
					JButton button = buttons.get(key);
					if (button != null) {
						if (button.getAction() != null) {
							button.getAction().actionPerformed(null);
						} else {
							ActionEvent ae = new ActionEvent(button, 0, "");
							for (ActionListener al : button
									.getActionListeners()) {
								al.actionPerformed(ae);
							}
						}
					}
				}
			}

		}

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
