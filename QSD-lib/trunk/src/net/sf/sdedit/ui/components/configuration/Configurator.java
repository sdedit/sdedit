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

package net.sf.sdedit.ui.components.configuration;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import net.sf.sdedit.util.UIUtilities;
import net.sf.sdedit.util.Utilities;

/**
 * An abstract base class for the components used by
 * {@linkplain ConfigurationUI} for configuring properties of a
 * {@linkplain Bean}.
 * 
 * @author Markus Strauch
 * 
 * @param <C>
 *            the type of the data object to be configured
 * @param <T>
 *            the type of the property to be configured
 */
public abstract class Configurator<T, C extends DataObject> extends JPanel
		implements PropertyChangeListener, ActionListener {

	protected static JFileChooser _fileChooser;

	protected static JFileChooser fileChooser() {
		if (_fileChooser == null) {
			_fileChooser = new JFileChooser(System.getProperty("user.home"));
			_fileChooser
					.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			_fileChooser.setMultiSelectionEnabled(true);
		}
		return _fileChooser;
	}

	protected static Set<String> fileTypes = new TreeSet<String>();

	private static FileFilter fileFilter = new FileFilter() {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String name = f.getName().toLowerCase();
			int c = name.lastIndexOf('.');
			if (c == -1) {
				return false;
			}
			return fileTypes.contains(name.substring(c + 1));
		}

		@Override
		public String getDescription() {
			return Utilities.join(",", fileTypes);
		}

	};

	public void addFileTypes(String... types) {
		for (String type : types) {
			fileTypes.add(type.toLowerCase());
		}
		if (fileChooser().getChoosableFileFilters().length == 1) {
			fileChooser().addChoosableFileFilter(fileFilter);
		}
	}

	public void removeFileTypes(String... types) {
		for (String type : types) {
			fileTypes.remove(type.toLowerCase());
		}
		if (fileTypes.isEmpty()) {
			fileChooser().resetChoosableFileFilters();
			fileChooser().setFileFilter(fileChooser().getAcceptAllFileFilter());
		}
	}

	static {

	}

	/**
	 * The bean to be configured.
	 */
	private Bean<C> bean;

	/**
	 * The particular property that is configured.
	 */
	private PropertyDescriptor property;

	private String[] dependentProperty;

	private String[] dependentValue;

	private boolean[] isDependencyEquality;

	private volatile boolean isPerformingAction;

	protected Configurator(Bean<C> bean, PropertyDescriptor property) {
		super();
		this.bean = bean;
		this.property = property;
		isPerformingAction = false;
		Adjustable adj = property.getWriteMethod().getAnnotation(
				Adjustable.class);
		if (!adj.depends().equals("")) {
			String[] dependencies = adj.depends().split(",");
			int n = dependencies.length;
			dependentProperty = new String[n];
			dependentValue = new String[n];
			isDependencyEquality = new boolean[n];

			for (int i = 0; i < n; i++) {
				String dependency = dependencies[i];
				String[] split;
				if (dependency.indexOf('!') == -1) {
					isDependencyEquality[i] = true;
					split = dependency.split("=");
				} else {
					isDependencyEquality[i] = false;
					split = dependency.split("!=");
				}
				dependentProperty[i] = split[0].trim();
				dependentValue[i] = split[1].trim();
			}
		}
		bean.addPropertyChangeListener(this);
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return property;
	}

	public int getLabelWidth() {
		return -1;
	}

	public void setLabelWidth(int width) {

	}

	/**
	 * Changes the components for configuring the property such that they
	 * reflect the given value.
	 * 
	 * @param value
	 *            the current value of the property
	 */
	protected abstract void refresh(T value);

	/**
	 * Returns a non-null value that is used as a replacement when a property
	 * has the value <tt>null</tt>.
	 * 
	 * @return a non-null value that is used as a replacement when a property
	 *         has the value <tt>null</tt>
	 */
	@SuppressWarnings("unchecked")
	protected T getNullValue() {
		return (T) NullValueProvider.getNullValue(property.getPropertyType());
	}

	/**
	 * Returns true if there is no boolean property that the configurability of
	 * the property managed by this <tt>Configurator</tt> depends on, or if
	 * there is such a boolean property that is set <tt>true</tt>.
	 * 
	 * @return flag denoting if the boolean property the configurability depends
	 *         on is true
	 */
	public boolean isDependencySatisfied() {
		if (dependentProperty == null) {
			return true;
		}
		for (int i = 0; i < dependentProperty.length; i++) {
			String value = bean.getValue(dependentProperty[i]).toString();
			boolean sat = dependentValue[i].equals(value);
			if (!isDependencyEquality[i]) {
				sat = !sat;
			}
			if (!sat) {
				return false;
			}
		}
		return true;
	}

	public abstract void focus();

	public void setBean(Bean<C> bean) {
		this.bean = bean;
		bean.addPropertyChangeListener(this);
		refresh();
	}

	/**
	 * Returns the underlying <tt>Bean</tt>, of which a property is configured
	 * by this <tt>Configurator</tt>.
	 * 
	 * @return the underlying <tt>Bean</tt>, of which a property is configured
	 *         by this <tt>Configurator</tt>
	 */
	public Bean<C> getBean() {
		return bean;
	}

	/**
	 * Returns the <tt>PropertyDescriptor</tt> for the particular property that
	 * is being configured.
	 * 
	 * @return the <tt>PropertyDescriptor</tt> for the particular property that
	 *         is being configured
	 */
	public PropertyDescriptor getProperty() {
		return property;
	}

	/**
	 * Changes the component(s) used for configuration such that the current
	 * value of the property is reflected
	 */
	@SuppressWarnings("unchecked")
	public void refresh() {
		refresh(getValue());
		setEnabled(isDependencySatisfied());
	}

	/**
	 * This method is called by the underlying {@linkplain Bean} when one of its
	 * properties changes. If that property is the one that is configured by
	 * this <tt>Configurator</tt>, we call {@linkplain #refresh(Object)} with
	 * the new value as a parameter, so it is graphically reflected.
	 * <p>
	 * We always check if the dependency of this <tt>Configurator</tt> is
	 * satisfied, i. e. we disable/enable it, depending on the state of the
	 * property it depends on.
	 * 
	 * @param evt
	 *            encapsulates the property change
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(property.getName())) {
			refresh((T) evt.getNewValue());
		}
		setEnabled(isDependencySatisfied());
	}

	/**
	 * Applies the given value, which means that the property that this
	 * Configurator configures is set to the value.
	 * 
	 * @param value
	 *            the value to be applied
	 */
	protected void applyValue(T value) {
		bean.setValue(property, value);
	}

	/**
	 * Returns the current value of the underlying property, or the non-null
	 * {@linkplain #getNullValue()} if the property has the value <tt>null</tt>.
	 * 
	 * @return the current value of the underlying property, or the non-null
	 *         {@linkplain #getNullValue()} if the property has the value
	 *         <tt>null</tt>
	 */
	@SuppressWarnings("unchecked")
	protected T getValue() {
		T value = (T) getBean().getValue(getProperty().getName());
		return value != null ? value : getNullValue();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		_setEnabled(enabled);
		for (Component comp : getComponents()) {
			comp.setEnabled(enabled);
		}
	}

	public void setEditable(boolean editable) {
		for (Component comp : UIUtilities.getDescendants(this)) {
			UIUtilities.setEditable(comp, editable);
		}
	}

	@Override
	public void setToolTipText(String tooltip) {
		super.setToolTipText(tooltip);

		// for (Component comp : getComponents()) {
		for (Component comp : UIUtilities.getDescendants(this)) {
			if (comp instanceof JComponent) {
				((JComponent) comp).setToolTipText(tooltip);
			}
		}
	}

	public void actionPerformed(final ActionEvent e) {
		// invoke this later to assert that all component models are in
		// the appropriate state (reflecting the action) - we then apply the
		// value to the underlying bean
		if (!isPerformingAction) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					isPerformingAction = true;
					try {
						_actionPerformed(e);
					} finally {
						isPerformingAction = false;
					}

				}
			});
		}
	}

	protected Adjustable getAdjustable() {
		return property.getWriteMethod().getAnnotation(Adjustable.class);
	}

	/**
	 * Change the underlying bean such that the it is consistent with the value
	 * displayed by this Configurator.
	 * 
	 * @param evt
	 */
	protected abstract void _actionPerformed(ActionEvent evt);

	protected abstract void _setEnabled(boolean enabled);

	// /**
	// * Enable or disable the components belonging to this Configurator.
	// *
	// * @param on
	// */
	// protected abstract void _setEnabled (boolean on);
}
