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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.sdedit.util.DocUtil;
import net.sf.sdedit.util.DocUtil.XMLException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A <tt>Bean</tt> provides a single instance of a &quot;data object&quot; that
 * implements the <tt>T</tt> interface which should only define get-, set-, and
 * is-methods like a Java bean. The instance is returned by
 * {@linkplain #getDataObject()}. For all manipulations of the data object's
 * state (invocations of set-methods) the <tt>Bean</tt> immediately sends
 * notifications to all interested <tt>PropertyChangeListener</tt>s. The state
 * of the data object can be loaded and stored, using XML documents (see
 * {@linkplain #load(Document, String)},
 * {@linkplain #store(Document, String, String)}). The values of the data object
 * can also be accessed by passing their corresponding properties as arguments
 * (see {@linkplain #setValue(PropertyDescriptor, Object)},
 * {@linkplain #getValue(String)}).
 * <p>
 * The values returned by the data object managed by a <tt>Bean</tt> are always,
 * provided the bean has been set up/loaded properly, not null (this can be
 * enforced by the <tt>permitNullValues</tt> property set to false), and legal,
 * i. e. string properties for which there is a set of alternative values are
 * always assigned to a legal value.
 * 
 * 
 * @author Markus Strauch
 * 
 * @param <T>
 *            the interface type of the data object
 */
public class Bean<T extends DataObject> implements Serializable,
		InvocationHandler {

	private static final long serialVersionUID = -8567877402350780001L;

	private transient Set<PropertyChangeListener> listeners;

	private transient SortedMap<String, PropertyDescriptor> properties;

	private transient SortedMap<String, String> order;

	// The state of the artificial object accessed by the T proxy
	// (see getDataObject())
	private HashMap<String, Object> values;

	private Class<T> dataClass;

	private transient T dataObject;

	private transient StringSelectionProvider ssp;

	private boolean permitNullValues;

	private Map<String, Set<String>> stringSets;

	private Map<String, String> methodToPropertyNameMap;

	private transient Pattern pattern;

	// flag denoting if PropertyChangeListeners should always be
	// informed about settings of attributes, even when the value
	// of the attribute does not change.
	private boolean alwaysNotifyListeners;

	/**
	 * Creates a new bean that provides a single data object. It is not
	 * permitted to set <tt>null</tt> values for this data object, as long as
	 * {@linkplain #setPermitNullValues(boolean)} is not called.
	 * 
	 * @param dataClass
	 *            the interface type of the data object
	 * @param ssp
	 *            a <tt>StringSelectionProvider</tt> that provides an array of
	 *            strings for methods of the data object which are annotated
	 *            {@linkplain Adjustable#stringSelectionProvided()}
	 */
	public Bean(Class<T> dataClass, StringSelectionProvider ssp) {
		values = new HashMap<String, Object>();
		this.ssp = ssp;
		this.dataClass = dataClass;
		init();
		permitNullValues = false;
		stringSets = new HashMap<String, Set<String>>();
		methodToPropertyNameMap = new HashMap<String, String>();
		alwaysNotifyListeners = false;
	}

	public Class<T> getDataClass() {
		return dataClass;
	}

	private static String norm(String property) {
		return Character.toUpperCase(property.charAt(0))
				+ property.substring(1);
	}

	public Set<PropertyChangeListener> getPropertyChangeListeners() {
		return Collections.checkedSet(listeners, PropertyChangeListener.class);
	}

	private void init() {
		properties = new TreeMap<String, PropertyDescriptor>();
		order = new TreeMap<String, String>();
		listeners = new LinkedHashSet<PropertyChangeListener>();
		pattern = Pattern.compile("get|set|is");
		Class<?> cls = dataClass;
		while (cls != null) {
			try {
				BeanInfo beanInfo = Introspector.getBeanInfo(cls);
				PropertyDescriptor[] propertyDescriptors = beanInfo
						.getPropertyDescriptors();
				for (int i = 0; i < propertyDescriptors.length; i++) {
					PropertyDescriptor property = propertyDescriptors[i];
					if (property.getWriteMethod() != null
							&& property.getWriteMethod().isAnnotationPresent(
									Adjustable.class)) {
						String key = property.getWriteMethod().getAnnotation(
								Adjustable.class).key();
						if (key.equals("")) {
							key = norm(property.getName());
						}
						order.put(key, norm(property.getName()));
						properties.put(norm(property.getName()), property);

						if (getValue(property) == null) {
							// may not be null when called in the process of
							// deserialization
							// (see readObject)
							setValue(property, NullValueProvider
									.getNullValue(property.getPropertyType()));

						}
					}
				}
			} catch (RuntimeException e) {
				throw e;
			} catch (Throwable t) {
				t.printStackTrace();
				throw new IllegalStateException(
						"FATAL: data class introspection was not successful");
			}
			if (cls.getInterfaces() != null && cls.getInterfaces().length > 0) {
				cls = cls.getInterfaces()[0];
			} else {
				cls = null;
			}
		}
	}

	/**
	 * Returns the synthetic data object implementing the data interface
	 * belonging to this {@linkplain Bean}.
	 * 
	 * @return the synthetic data object implementing the data interface
	 *         belonging to this {@linkplain Bean}
	 */
	@SuppressWarnings("unchecked")
	public T getDataObject() {
		if (dataObject == null) {
			dataObject = (T) Proxy.newProxyInstance(dataClass.getClassLoader(),
					new Class[] { dataClass }, this);
		}
		return dataObject;
	}

	/**
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	public final Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String name = method.getName();
		if (name.equals("getBean")) {
			return this;
		}
		if (name.equals("isA")) {
			return ((Class) args[0]).isAssignableFrom(dataClass);
		}
		if (name.equals("cast")) {
			return proxy;
		}
		if (name.equals("copy")) {
			Bean<T> copy = copy();
			return copy.getDataObject();
		}
		if (name.equals("hashCode") && method.getParameterTypes().length == 1) {
			return hashCode();
		}
		if (name.equals("equals") && method.getParameterTypes().length == 1
				&& method.getParameterTypes()[0] == Object.class) {
			DataObject other = (DataObject) args[0];
			return this.equals(other.getBean(DataObject.class));
		}
		String property = methodToPropertyNameMap.get(name);

		if (property == null) {
			Matcher matcher = pattern.matcher(name);
			property = matcher.replaceFirst("");
			methodToPropertyNameMap.put(name, property);
		}
		if (name.charAt(0) == 's') {
			// set-method
			setValue(properties.get(property), args[0]);
			return null;
		}
		return getValue(property);
	}

	/**
	 * Returns the properties of this Bean that are annotated with an
	 * {@linkplain Adjustable} annotation.
	 * 
	 * @return the properties of this Bean that are annotated with an
	 *         {@linkplain Adjustable} annotation
	 */
	public Collection<PropertyDescriptor> getProperties() {
		List<PropertyDescriptor> list = new LinkedList<PropertyDescriptor>();
		for (String property : order.values()) {
			list.add(properties.get(property));
		}
		return list;
	}

	public Collection<PropertyDescriptor> getPrimaryProperties() {
		List<PropertyDescriptor> list = new LinkedList<PropertyDescriptor>();
		Collection<PropertyDescriptor> properties = getProperties();
		for (PropertyDescriptor prop : properties) {
			if (prop.getWriteMethod().getAnnotation(Adjustable.class).primary()) {
				list.add(prop);
			}
		}
		return list;
	}

	/**
	 * Returns the <tt>PropertyDescriptor</tt> for the property with the given
	 * name.
	 * 
	 * @param name
	 *            the name of a property
	 * @return the corresponding <tt>PropertyDescriptor</tt> or <tt>null</tt> if
	 *         there is no property with the name
	 */
	public PropertyDescriptor getProperty(String name) {
		return properties.get(norm(name));
	}

	/**
	 * Adds a listener that is notified when a property is modified via
	 * {@linkplain #setValue(PropertyDescriptor, Object)}.
	 * 
	 * @param listener
	 *            a listener that is notified when a property is modified via
	 *            {@linkplain #setValue(PropertyDescriptor, Object)}
	 */
	public synchronized void addPropertyChangeListener(
			PropertyChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a property change listener.
	 * 
	 * @param listener
	 *            the listener to be removed
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Changes this bean's properties' values such that they are equal to the
	 * given bean's properties' values.
	 * 
	 * @param bean
	 *            another bean
	 */
	public void takeValuesFrom(Bean<T> bean) {
		for (PropertyDescriptor property : getProperties()) {
			setValue(property, bean.getValue(property.getName()));
		}
	}

	/**
	 * Returns a shallow copy of this bean.
	 * 
	 * @return a shallow copy of this bean
	 */
	public Bean<T> copy() {
		Bean<T> copy = new Bean<T>(dataClass, ssp);
		copy.takeValuesFrom(this);
		return copy;
	}

	/**
	 * Returns the current value of the given property, represented by its name.
	 * 
	 * @param property
	 *            the name of a property
	 * @return the current value of the property
	 */
	public final Object getValue(String property) {
		return values.get(norm(property));
	}

	public final Object getValue(PropertyDescriptor pd) {
		return getValue(pd.getName());
	}

	public void load(File file) throws XMLException, IOException {
		load(file.toURI().toURL());
	}

	public void load(URL url) throws XMLException, IOException {
		InputStream stream = url.openStream();
		try {
			Document document = DocUtil.readDocument(stream, "UTF-8");
			load(document, "/configuration/data");
		} finally {
			stream.close();
		}
	}

	public void store(File file) throws XMLException, IOException {
		OutputStream stream = new FileOutputStream(file);
		try {
			Document doc = DocUtil.newDocument();
			doc.appendChild(doc.createElement("configuration"));
			store(doc, "/configuration", "data");
			DocUtil.writeDocument(doc, "UTF-8", stream);
		} finally {
			stream.close();
		}
	}

	/**
	 * Changes this bean's properties' values such that they reflect the values
	 * found in the given document. It remains unchanged if the document does
	 * not contain a subtree corresponding to <tt>pathToElement</tt>.
	 * 
	 * @param document
	 *            a document
	 * @param pathToElement
	 *            XPath to the subtree where the properties' values are
	 *            described
	 * @throws XMLException
	 */
	public void load(Document document, String pathToElement)
			throws XMLException {
		Element elem = (Element) DocUtil.evalXPathAsNode(document,
				pathToElement);
		if (elem != null) {
			BeanConverter converter = new BeanConverter(this, document);
			converter.setValues(elem);
		}
	}

	public void setAlwaysNotifyListeners(boolean notify) {
		alwaysNotifyListeners = notify;
	}

	/**
	 * Stores all properties' current values in a newly created subtree of the
	 * given document.
	 * 
	 * @param document
	 *            the document
	 * @param pathToParent
	 *            XPath to the parent of the root of the subtree
	 * @param elementName
	 *            the name of the root of the subtree
	 * @throws XMLException
	 */
	public void store(Document document, String pathToParent, String elementName)
			throws XMLException {
		Element parent = (Element) DocUtil.evalXPathAsNode(document,
				pathToParent);
		BeanConverter converter = new BeanConverter(this, document);
		Element elem = converter.createElement(elementName);
		parent.appendChild(elem);
	}

	/**
	 * Sets a new value for a property and informs all
	 * <tt>PropertyChangeListener</tt>s about that. If the
	 * <tt>admitNullValues</tt> property is false, a new value of <tt>null</tt>
	 * will be ignored and not set. Furthermore, it is not permitted to set a
	 * string value for a property that has a set of alternative values, if none
	 * of these matches. The illegal value will be silently ignored.
	 * 
	 * @param property
	 *            the descriptor of the property
	 * @param value
	 *            the new value of the property
	 */
	public final void setValue(PropertyDescriptor property, Object value) {
		if (value == null && !permitNullValues) {
			return;
		}
		if (!"".equals(value) && property.getPropertyType() == String.class) {
			Set<String> choices = getStringsForProperty(property);
			if (!choices.isEmpty() && !choices.contains(value)) {
				return;
			}
		}
		String propertyName = norm(property.getName());
		Object oldValue = values.get(propertyName);
		values.put(propertyName, value);
		firePropertyChanged(property, value, oldValue);
	}

	/**
	 * Sends a notification about the change of a property provided both values
	 * are not equal (with respect to the result of <tt>equals(Object)</tt>.
	 * 
	 * @param property
	 *            the descriptor of the property whose value has changed
	 * @param newValue
	 *            the new value of the property
	 * @param oldValue
	 *            the old value of the property
	 */
	private void firePropertyChanged(PropertyDescriptor property,
			Object newValue, Object oldValue) {
		boolean notify;
		if (alwaysNotifyListeners) {
			notify = true;
		} else {
			notify = false;
			if ((newValue != null || oldValue != null)) {
				if (newValue == null || oldValue == null
						|| !newValue.equals(oldValue)) {
					notify = true;
				}
			}
		}
		if (notify) {
			List<PropertyChangeListener> _listeners;
			PropertyChangeEvent event = new PropertyChangeEvent(this, property
					.getName(), oldValue, newValue);
			synchronized (this) {
				_listeners = new LinkedList<PropertyChangeListener>(listeners);
			}
			for (PropertyChangeListener listener : _listeners) {
				listener.propertyChange(event);
			}
		}
	}

	/**
	 * Returns true if and only if <tt>o</tt> is a reference to a bean with the
	 * same properties that have the same values as this bean's properties.
	 * 
	 * @return true if and only if <tt>o</tt> is a reference to a bean with the
	 *         same properties that have the same values as this bean's
	 *         properties
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		Bean<? extends DataObject> bean = (Bean<? extends DataObject>) o;
		if (dataClass != bean.getDataClass()) {
			return false;
		}
		for (PropertyDescriptor property : getProperties()) {
			Object myVal = getValue(property.getName());
			Object yourVal = bean.getValue(property.getName());
			if (myVal == null && yourVal == null) {
				// check next property if both are null
				continue;
			}
			if (myVal == null || yourVal == null) {
				// if one is null, the other is not, so return false
				return false;
			}
			if (!myVal.equals(yourVal)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		StringBuffer code = new StringBuffer();
		for (PropertyDescriptor property : getProperties()) {
			Object val = getValue(property.getName());
			code.append(val);
		}
		return code.hashCode();
	}

	/**
	 * Returns a set of strings representing all alternative values for the
	 * given String property. If there is no alternative, i. e. all values are
	 * possible, the set is empty.
	 * 
	 * @param property
	 *            a String property
	 * @return an array of strings representing all alternative values for the
	 *         given String property
	 */
	public Set<String> getStringsForProperty(PropertyDescriptor property) {
		String propName = norm(property.getName());
		Set<String> strings = stringSets.get(propName);
		if (strings == null) {
			strings = new LinkedHashSet<String>();
			Adjustable adj = property.getWriteMethod().getAnnotation(
					Adjustable.class);
			String[] choices = adj.choices();
			if (choices.length == 0 && adj.stringSelectionProvided()) {
				if (ssp != null) {
					choices = ssp.getStringSelection(property.getName());
				}
			}
			for (String choice : choices) {
				strings.add(choice);
			}
			stringSets.put(norm(property.getName()), strings);
		}
		return strings;
	}

	public void setStringSelectionProvider(StringSelectionProvider ssp) {
		this.ssp = ssp;
	}

	public void clearStringSelection(String propertyName) {
		stringSets.remove(norm(propertyName));
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (PropertyDescriptor property : getProperties()) {
			buffer.append(property.getName() + "=");
			buffer.append(getValue(property.getName()));
			buffer.append("\n");
		}
		return buffer.toString();
	}

	/**
	 * Returns a flag denoting if <tt>null</tt> values can be used as parameters
	 * of the data object's set methods.
	 * 
	 * @return a flag denoting if <tt>null</tt> values can be used as parameters
	 *         of the data object's set methods
	 */
	public boolean isPermitNullValues() {
		return permitNullValues;
	}

	/**
	 * Sets a flag denoting if <tt>null</tt> values can be used as parameters of
	 * the data object's set methods.
	 * 
	 * @param permitNullValues
	 *            a flag denoting if <tt>null</tt> values can be used as
	 *            parameters of the data object's set methods
	 */
	public void setPermitNullValues(boolean permitNullValues) {
		this.permitNullValues = permitNullValues;
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		init();
	}

}
// {{core}}
