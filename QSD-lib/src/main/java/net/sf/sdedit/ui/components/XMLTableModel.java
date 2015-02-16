//Copyright (c) 2006 - 2015, Markus Strauch.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.
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
