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

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.KeyStroke;

import net.sf.sdedit.util.Utilities;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BeanConverter {

	private Bean<? extends DataObject> bean;

	private Document document;

	public BeanConverter(Bean<? extends DataObject> bean, Document document) {
		this.document = document;
		this.bean = bean;
	}

	public Element createElement(String name) {
		Element root = document.createElement(name);
		for (PropertyDescriptor property : bean.getProperties()) {
			root.appendChild(convertProperty(property));
		}
		return root;
	}

	public void setValues(Element root) {
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				setElementValue((Element) children.item(i));
			}
		}
	}

	private Element convertProperty(PropertyDescriptor property) {
		Element elem = document.createElement("property");
		elem.setAttribute("name", property.getName());
		Object value = bean.getValue(property.getName());
		if (value instanceof String) {
			CDATASection cdata = document.createCDATASection((String) value);
			elem.appendChild(cdata);
		} else if (value instanceof String[]) {
			CDATASection cdata = document.createCDATASection(Utilities.join(
					";;", (String[]) value));
			elem.appendChild(cdata);
		} else if (value instanceof Integer) {
			elem.setAttribute("value", value.toString());
		} else if (value instanceof Boolean) {
			elem.setAttribute("value", value.toString());
		} else if (value instanceof Font) {
			Font font = (Font) value;
			elem.setAttribute("family", font.getFamily());
			elem.setAttribute("style", String.valueOf(font.getStyle()));
			elem.setAttribute("size", String.valueOf(font.getSize()));
		} else if (value instanceof File) {
			elem.setAttribute("value", ((File) value).getAbsolutePath());
		} else if (value instanceof File[]) {
			ArrayList<String> strings = new ArrayList<String>();
			for (File file : (File[]) value) {
				strings.add(file.getAbsolutePath());
			}
			CDATASection cdata = document.createCDATASection(Utilities.join(
					";;", strings.toArray(new String[strings.size()])));
			elem.appendChild(cdata);
		} else if (value instanceof KeyStroke) {
			elem.setAttribute("value", value.toString());
		} else if (value instanceof Color) {
			elem.setAttribute("value", "" + ((Color) value).getRGB());
		} else if (value instanceof Date) {
			elem.setAttribute("value", "" + ((Date) value).getTime());
		} else {
			throw new IllegalArgumentException("unknown property type: "
					+ value.getClass().getName());
		}
		return elem;
	}

	private void setElementValue(Element element) {
		String name = element.getAttribute("name");
		PropertyDescriptor property = bean.getProperty(name);
		if (property == null) {
			System.err.println("Warning: " + name + " is not a property");
		} else {
			Object value = null;
			Class<?> type = property.getPropertyType();
			if (type.equals(String.class)) {
				value = element.getTextContent();
			} else if (type.equals(String[].class)) {
				String text = element.getTextContent();
				value = text.split(";;");
			} else if (type.equals(Integer.TYPE)) {
				value = parseInt(element.getAttribute("value"));
			} else if (type.equals(Boolean.TYPE)) {
				value = parseBool(element.getAttribute("value"));
			} else if (type.equals(Font.class)) {
				String family = element.getAttribute("family");
				int style = parseInt(element.getAttribute("style"));
				int size = parseInt(element.getAttribute("size"));
				if (size == 0) {
					size = 12;
				}
				value = new Font(family, style, size);
			} else if (type.equals(File.class)) {
				String fileName = element.getAttribute("value");
				value = new File(fileName);
			} else if (type.equals(File[].class)) {
				String text = element.getTextContent().trim();
				if (text.length() == 0) {
					value = new File[0];
				} else {
					String[] strings = element.getTextContent().split(";;");
					File[] files = new File[strings.length];
					for (int i = 0; i < strings.length; i++) {
						files[i] = new File(strings[i]);
					}
					value = files;
				}
			} else if (type.equals(KeyStroke.class)) {
				String keyStrokeDescription = element.getAttribute("value");
				value = KeyStroke.getKeyStroke(keyStrokeDescription);
			} else if (type.equals(Color.class)) {
				String rgb = element.getAttribute("value");
				value = new Color(Integer.parseInt(rgb));
			} else if (type.equals(Date.class)) {
				String time = element.getAttribute("value");
				value = new Date(Long.parseLong(time));
			}
			if (value == null) {
				System.err.println("Warning: a value for property " + name
						+ " could not be created");
			} else {
				bean.setValue(property, value);
			}
		}
	}

	private static int parseInt(String string) {
		int number = 0;
		try {
			number = Integer.valueOf(string);
		} catch (NumberFormatException e) {
			/* ignored */
		}
		return number;
	}

	private static boolean parseBool(String string) {
		boolean value = false;
		try {
			value = Boolean.valueOf(string);
		} catch (RuntimeException re) {
			/* ignored */
		}
		return value;
	}
}
