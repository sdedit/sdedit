package net.sf.sdedit.util.xl;

import java.util.LinkedList;
import java.util.List;

import net.sf.sdedit.util.DOMNode;

public class XLType {

	private Class<?> javaClass;

	private Class<?> returnType;

	private List<Class<?>> argumentTypes;

	private DOMNode typeNode;

	public XLType(DOMNode typeNode, XL xl) {
		this.typeNode = typeNode;

		initialize(xl);
	}

	public String getName() {
		return typeNode.getAttribute("name");
	}

	private void initialize(XL xl) {
		String className = typeNode.getAttribute("name");
		if (className == null || "".equals(className)) {
			throw new XLException(
					"a type node has been defined without a name", null);
		}
		try {
			for (String pkg : xl.getPackageNames()) {
				try {
					javaClass = Class.forName(pkg + "." + className);
					break;
				} catch (RuntimeException re) {
					throw re;
				} catch (Exception ignored) {
					/* empty */
				}
			}
			if (javaClass == null) {
				throw new XLException("no java class found for type "
						+ className, null);
			}
			if (typeNode.getAttribute("return") != null) {
				returnType = Class.forName(typeNode.getAttribute("return").trim());
			}
			// no type-checking now
//				Method executeMethod = Utilities.findMethod(javaClass,
//						"execute", true);
//				if (executeMethod.getReturnType() != returnType) {
//					throw new XLException("return type "
//							+ executeMethod.getReturnType().getName() + " of "
//							+ className + " is not as declared: "
//							+ returnType.getName(), null);
//				}
//			}
			if (!"ignore".equals(typeNode.getAttribute("args"))) {
				if (typeNode.getAttribute("args") != null) {
					argumentTypes = new LinkedList<Class<?>>();
					for (String argType : typeNode.getAttribute("args").split(
							";")) {
						argumentTypes.add(Class.forName(argType.trim()));
					}
				}

			}

		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable t) {
			throw new XLException("fatal error during initialization of type "
					+ className, t);
		}
	}

	public void checkArguments(Object[] arguments) {
		if (argumentTypes != null) {
			if (arguments.length != argumentTypes.size()) {
				throw new XLException("incorrect number of arguments for type "
						+ getName(), null);
			}
			int i = 0;
			for (Class<?> clazz : argumentTypes) {
				if (!clazz.isInstance(arguments[i])) {
					throw new XLException("Incorrect argument type at index "
							+ i + " for type " + getName() + ". Expected: "
							+ clazz.getName() + ", got: "
							+ arguments[i].getClass().getName(), null);
				}
				i++;
			}
		}
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
