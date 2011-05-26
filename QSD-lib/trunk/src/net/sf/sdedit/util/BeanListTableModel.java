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

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.util.TableModelAdapter.RowEditor;
import net.sf.sdedit.util.TableModelAdapter.RowExpansion;

public class BeanListTableModel extends TableModelAdapter implements
		RowExpansion, RowEditor {
	
	private static String [] getBeanColumns (List<Bean<?>> beans) {
		
		if (beans.isEmpty()) {
			return new String [0];
		}

		Collection<PropertyDescriptor> properties = beans.get(0).getProperties();
		String [] columns = new String[properties.size()];
		int i = 0;
		for (PropertyDescriptor prop : properties) {
			columns [i++] = prop.getName();
		}
		return columns;
	}
	
	private static Class<?>[] getBeanColumnClasses (List<Bean<?>> beans) {
		if (beans.isEmpty()) {
			return new Class [0];
		}
		Collection<PropertyDescriptor> properties = beans.get(0).getProperties();
		Class<?> [] columns = new Class[properties.size()];
		int i = 0;
		for (PropertyDescriptor prop : properties) {
			columns [i++] = prop.getPropertyType();
		}
		return columns;
	}
	
	private ArrayList<PropertyDescriptor> properties;

	public BeanListTableModel(List<Bean<?>> beans) {
		super(getBeanColumns(beans), getBeanColumnClasses(beans));
		setRowExpansion(this);
		setRowEditor(this);
		properties = new ArrayList<PropertyDescriptor>();
		if (!beans.isEmpty()) {
			properties.addAll(beans.get(0).getProperties());
		}
		
		setData(beans);

	}

	
	public Object[] expand(Object row) {
		Bean<?> bean = Bean.class.cast(row);
		Object [] values = new Object[properties.size()];
		for (int i = 0; i < properties.size(); i++) {
			values [i] = bean.getValue(properties.get(i).getName());
		}
		return values;
		
	}

	
	public boolean isEditable(Object row, int index) {
		return false;
	}

	
	public void setValue(Object row, int index, Object value) {
		// TODO Auto-generated method stub

	}

}
