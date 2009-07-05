package net.sf.sdedit.util.xl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.sdedit.util.DOMNode;
import net.sf.sdedit.util.Utilities;

import org.w3c.dom.Element;

public class XL {

	private String[] packageNames;

	private DOMNode program;

	private Map<String, Object> globalObjects;

	private Map<String, XLType> typeMap;

	private boolean exitOnException;

	public String[] getPackageNames() {
		return packageNames;
	}

	public XL(DOMNode program) {
		this.program = program;
		packageNames = program.getAttribute("packages").split(";");
		globalObjects = new HashMap<String, Object>();
		typeMap = new HashMap<String, XLType>();
		exitOnException = true;
		readTypes(program);

	}

	private void readTypes(DOMNode program) {
		for (DOMNode typeNode : program.getChild("META").getChildren(Element.class)) {
			XLType type = new XLType(typeNode, this);
			if (typeMap.put(type.getName(), type) != null) {
				throw new XLException("duplicate type: " + type.getName(), null);
			}
		}
	}

	public void setGlobalObject(String name, Object object) {
		globalObjects.put(name, object);
	}

	public Object getGlobalObject(String name) {
		return globalObjects.get(name);
	}

	private XLUnit getUnit(DOMNode node) {
		XLUnit unit = (XLUnit) node.getUserObject("XLUnit");
		if (unit != null) {
			return unit;
		}
		XLType type = typeMap.get(node.getName());
		if (type == null) {
			throw new XLException(
					"XLType " + node.getName() + " not declared.", null);
		}
		unit = type.newInstance();
		unit.setNode(node);
		unit.setXL(this);
		for (String name : node.getAttributeNames()) {
			unit.setAttribute(name, node.getAttribute(name));
		}
		node.setUserObject("XLUnit", unit);
		unit.initialize();
		return unit;
	}

	public void execute(Object... arguments) throws Exception {
		executeChildren(null, arguments);
	}

	protected void executeChildren(XLUnit unit, Object... arguments)
			throws Exception {
		XLUnit pred = null;
		DOMNode parent = unit == null ? program.getChild("SCRIPT") : unit.getNode();
		for (DOMNode node : parent.getChildren(Element.class)) {
			execute(node, pred, unit, arguments);
			pred = (XLUnit) node.getUserObject("XLUnit");
		}
	}

	private void execute(DOMNode node, XLUnit predecessor, XLUnit parent,
			Object... arguments) throws Exception {
		XLUnit unit = getUnit(node);
		System.out.println(Utilities.toString(new Date(),"dd.MM.yyyy kk:mm:ss") + " executing " + unit.getClass().getSimpleName());
		unit.getType().checkArguments(arguments);
		Object result;
		try {
			result = unit.execute(predecessor, parent, arguments);
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception ex) {
			if (exitOnException) {
				throw ex;
			}
			result = ex;
		}
		unit.setResult(result);
		if (parent != null) {
			parent.processResultFromChild(unit, result);
		}
	}

	public void setExitOnException(boolean exitOnException) {
		this.exitOnException = exitOnException;
	}

	public boolean isExitOnException() {
		return exitOnException;
	}

}
