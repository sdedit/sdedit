package net.sf.sdedit.util.xl;

import java.util.LinkedList;
import java.util.List;

import net.sf.sdedit.util.DOMNode;

public class XLType {

	private Class<?> javaClass;

	private List<Class<?>> outputTypes;

	private List<Class<?>> inputTypes;

	private List<Class<?>> passedTypes;

	private List<Class<?>> receivedTypes;

	private DOMNode typeNode;

	public XLType(DOMNode typeNode, XL xl) {
		this.typeNode = typeNode;
		outputTypes = new LinkedList<Class<?>>();
		inputTypes = new LinkedList<Class<?>>();
		passedTypes = new LinkedList<Class<?>>();
		receivedTypes = new LinkedList<Class<?>>();

		initialize(xl);
	}

	public String getName() {
		return typeNode.getAttribute("name");
	}

	private void fillTypeList(List<Class<?>> typeList, String typeString) {
		for (String type : typeString.split(";")) {
			try {
				typeList.add(Class.forName(type));
			} catch (ClassNotFoundException e) {
				throw new XLException("XLType " + type + " not found.", e);
			}
		}
	}
	
	private Class<?> getJavaClass (XL xl, String name) {
		try {
			javaClass = Class.forName(name);
			xl.check(javaClass, this);
			return javaClass;
	
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception ignored) {
			/* empty */
		}
		return null;
	}

	private void initialize(XL xl) {
		String className = typeNode.getAttribute("name");
		if (className == null || "".equals(className)) {
			throw new XLException(
					"a type node has been defined without a name", null);
		}
		try {

			if (typeNode.getAttribute("input") != null) {
				fillTypeList(inputTypes, typeNode.getAttribute("input"));
			}
			if (typeNode.getAttribute("output") != null) {
				fillTypeList(outputTypes, typeNode.getAttribute("output"));
			}
			if (typeNode.getAttribute("pass") != null) {
				fillTypeList(passedTypes, typeNode.getAttribute("pass"));
			}
			if (typeNode.getAttribute("receive") != null) {
				fillTypeList(receivedTypes, typeNode.getAttribute("receive"));
			}
			if (typeNode.getAttribute("class") != null) {
				javaClass = getJavaClass(xl, typeNode.getAttribute("class"));				
			} else {
				for (String pkg : xl.getPackageNames()) {
					javaClass = getJavaClass(xl, pkg + "." + className);
					if (javaClass != null) {
						break;
					}
				}
			}
			if (javaClass == null) {
				throw new XLException("no java class found for type "
						+ className, null);
			}

		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable t) {
			throw new XLException("fatal error during initialization of type "
					+ className, t);
		}
	}

	public List<Class<?>> getInputTypes() {
		return inputTypes;
	}

	public List<Class<?>> getOutputTypes() {
		return outputTypes;
	}

	public List<Class<?>> getPassedTypes() {
		return passedTypes;
	}

	public List<Class<?>> getReceivedTypes() {
		return receivedTypes;
	}

	public DOMNode getTypeNode() {
		return typeNode;
	}

	public XLUnit newInstance() {
		try {
			XLUnit unit = XLUnit.class.cast(javaClass.newInstance());
			unit.setType(this);
			return unit;
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new XLException("cannot instantiate type "
					+ javaClass.getName(), e);
		}
	}

}
