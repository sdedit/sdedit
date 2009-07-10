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
	
	private Object [] arguments;

	private boolean exitOnException;
	
	private XLTypeChecker typeChecker;

	public String[] getPackageNames() {
		return packageNames;
	}

	public XL(DOMNode program, XLTypeChecker typeChecker) {
		this.program = program;
		packageNames = program.getAttribute("packages").split(";");
		globalObjects = new HashMap<String, Object>();
		typeMap = new HashMap<String, XLType>();
		exitOnException = true;
		this.typeChecker = typeChecker;
		readTypes(program);
	}
	
	public XL(DOMNode program) {
		this(program, null);
	}
	
	public void setTypeChecker (XLTypeChecker typeChecker) {
		this.typeChecker = typeChecker;
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
	    this.arguments = arguments;
		executeChildren(null);
	}
	
	protected void executeChildren(XLUnit unit)
			throws Exception {
		DOMNode parent = unit == null ? program.getChild("SCRIPT") : unit.getNode();
		for (DOMNode node : parent.getChildren(Element.class)) {
			execute(node);
		}
	}

	private void execute(DOMNode node) throws Exception {
		XLUnit unit = getUnit(node);
		System.out.println(Utilities.toString(new Date(),"dd.MM.yyyy kk:mm:ss") + " executing " + unit.getClass().getSimpleName());
		//unit.getType().checkArguments(arguments);
		try {
			unit.execute();
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception ex) {
			if (exitOnException) {
				throw ex;
			}
		}
//		unit.setResult(result);
//		if (parent != null) {
//			parent.processResultFromChild(unit, result);
//		}
	}

	public void setExitOnException(boolean exitOnException) {
		this.exitOnException = exitOnException;
	}

	public boolean isExitOnException() {
		return exitOnException;
	}

    protected <T> T input(XLUnit unit, Class<T> cls, int index) {
        if (unit.getPredecessor() != null) {
            return unit.getPredecessor().xlGetOutputArgument(cls, index);
        }
        if (unit.getParent() != null) {
            return unit.getParent().xlGetPassedArgument(cls, index);
        }
        return cls.cast(arguments [index]);
        
        
    }

    protected <T> T receive(XLUnit unit, Class<T> cls, int index) {
        return unit.getLastChild().xlGetOutputArgument(cls, index);        
    }

	public void check(Class<?> unitClass,XLType type) {
		if (typeChecker != null) {
			typeChecker.check(unitClass, type);
		}
		
	}

}
