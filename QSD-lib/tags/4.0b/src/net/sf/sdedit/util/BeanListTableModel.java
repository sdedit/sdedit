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
