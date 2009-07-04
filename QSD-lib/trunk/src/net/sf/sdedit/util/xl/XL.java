package net.sf.sdedit.util.xl;



import java.util.HashMap;
import java.util.Map;

import net.sf.sdedit.util.DOMNode;

import org.w3c.dom.Element;

public class XL {
	
	private String unitPackage;
	
	private DOMNode program;
	
	private Map<String,Object> globalObjects;
	
	private boolean exitOnException;
	
	public String getUnitPackage() {
		return unitPackage;
	}

	public void setUnitPackage(String unitPackage) {
		this.unitPackage = unitPackage;
	}

	public XL (DOMNode program) {
		this.program = program;
		globalObjects = new HashMap<String,Object>();
		exitOnException = true;

	}
	
	protected void setGlobalObject (String name, Object object) {
		globalObjects.put(name, object);
	}
	
	protected Object getGlobalObject (String name) {
		return globalObjects.get(name);
	}
	
	private XLUnit getUnit (DOMNode node) {
		XLUnit unit = (XLUnit) node.getUserObject("XLUnit");
		if (unit != null) {
			return unit;
		}
		String qName = unitPackage + "." + node.getName();
		try {
			unit = XLUnit.class.cast(Class.forName(qName).newInstance());
			unit.setNode(node);
			unit.setXL(this);
			for (String name : node.getAttributeNames()) {
				unit.setAttribute(name, node.getAttribute(name));
			}
			node.setUserObject("XLUnit", unit);
			unit.initialize();
			return unit;

			
		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IllegalArgumentException("cannot create unit " + node.getName());
		}
	}
	
	public void execute (Object... arguments) throws Exception {
		executeChildren(null, arguments);
	}
	
	protected void executeChildren (XLUnit unit, Object... arguments) throws Exception {
		XLUnit pred = null;
		DOMNode parent = unit == null ? program : unit.getNode();
		for (DOMNode node : parent.getChildren(Element.class)) {
			execute(node, pred, unit, arguments);
			pred = (XLUnit) node.getUserObject("XLUnit");
		}
	}
	
	private void execute (DOMNode node, XLUnit predecessor, XLUnit parent, Object... arguments) 
	throws Exception {
		XLUnit unit = getUnit(node);
		System.out.println("executing " + unit.getClass().getSimpleName());
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
