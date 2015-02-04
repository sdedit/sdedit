package net.sf.sdedit.ui.components;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import net.sf.sdedit.util.DocUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLTableModel implements TableModel {

	private List<Element> elements;

	private List<String> names;

	public XMLTableModel(Document document) {
		elements = new ArrayList<Element>();
		Element root = document.getDocumentElement();
		Set<String> names = new LinkedHashSet<String>();
		for (Element child : DocUtil.select(root, "*", Element.class)) {
			elements.add(child);
			for (Element nipote : DocUtil.select(child, "*", Element.class)) {
				names.add(nipote.getNodeName());
			}
		}
		this.names = new ArrayList<String>(names);
	}

	public int getRowCount() {
		return elements.size();
	}

	public int getColumnCount() {
		return names.size();
	}

	public String getColumnName(int columnIndex) {
		return names.get(columnIndex);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}
	
	public Element getElement (int i) {
		return elements.get(i);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Element row = elements.get(rowIndex);
		String name = getColumnName(columnIndex);
		Element value = DocUtil.selectFirst(row, name, Element.class);
		return value == null ? null : value.getTextContent();
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	public void addTableModelListener(TableModelListener l) {
	}

	public void removeTableModelListener(TableModelListener l) {
	}

	public void addColumn(TableColumn aColumn) {
	}

}
