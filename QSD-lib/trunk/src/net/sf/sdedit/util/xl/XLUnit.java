package net.sf.sdedit.util.xl;

import java.util.Properties;

import net.sf.sdedit.util.DOMNode;

public abstract class XLUnit {

	private Properties attributes;

	private DOMNode node;

	private XL xl;

	private Object result;

	private XLType type;

	protected XLUnit() {
		attributes = new Properties();
	}

	protected void setXL(XL xl) {
		this.xl = xl;
	}

	public XLType getType() {
		return type;
	}

	protected void setType(XLType type) {
		this.type = type;
	}

	protected void executeChildren(Object... arguments) throws Exception {
		xl.executeChildren(this, arguments);
	}

	protected void setGlobalObject(String name, Object object) {
		xl.setGlobalObject(name, object);
	}

	protected Object getGlobalObject(String name) {
		return xl.getGlobalObject(name);
	}

	public void setAttribute(String name, String value) {
		attributes.setProperty(name, value);
	}

	public String getAttribute(String name) {
		return attributes.getProperty(name);
	}

	protected void setResult(Object result) {
		this.result = result;
	}

	protected abstract void initialize();

	public Object getResult() {
		return result;
	}

	public abstract Object execute(XLUnit predecessor, XLUnit parent,
			Object... arguments) throws Exception;

	public abstract void processResultFromChild(XLUnit child, Object result)
			throws Exception;

	public void setNode(DOMNode node) {
		this.node = node;
	}

	public DOMNode getNode() {
		return node;
	}

}
