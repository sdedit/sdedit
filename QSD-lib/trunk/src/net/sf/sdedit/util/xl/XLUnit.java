package net.sf.sdedit.util.xl;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.sf.sdedit.util.DOMNode;

import org.w3c.dom.Element;

public abstract class XLUnit {

    private Properties attributes;

    private DOMNode node;

    private XL xl;

    private XLType type;

    private List<Object> passedArguments;

    private List<Object> outputArguments;

    protected XLUnit() {
        attributes = new Properties();
        passedArguments = new LinkedList<Object>();
        outputArguments = new LinkedList<Object>();
    }

    protected void xlPass(int index, Object argument) {
        if (index > passedArguments.size() - 1) {
            passedArguments.add(null);
        }
        passedArguments.set(index, argument);
    }

    protected void xlOutput(int index, Object argument) {
        if (index > outputArguments.size() - 1) {
            outputArguments.add(null);
        }
        outputArguments.set(index, argument);
    }
    
    protected Object xlReceive(int index) {
        return xl.receive(this, index);
    }
    
    protected Object xlInput(int index) {
        return xl.input(this, index);
    }
    
    protected Object xlGetOutputArgument(int index) {
        return outputArguments.get(index);
    }
    
    protected Object xlGetPassedArgument(int index) {
        return passedArguments.get(index);
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

    protected void executeChildren() throws Exception {
        xl.executeChildren(this);
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

    public XLUnit getPredecessor () {
        DOMNode pred = getNode().getPreviousSibling(Element.class);
        if (pred == null) {
            return null;
        }
        return (XLUnit) pred.getUserObject("XLUnit");
    }
    
    public XLUnit getParent () {
        return (XLUnit) getNode().getParent().getUserObject("XLUnit");
    }
    
    public XLUnit getLastChild () {
        List<DOMNode> nodes = getNode().getChildren(Element.class);
        DOMNode last = nodes.get(nodes.size()-1);
        return (XLUnit) last.getUserObject("XLUnit");
    }

    protected abstract void initialize();

    public abstract void execute() throws Exception;

    // public abstract void processResultFromChild(XLUnit child, Object result)
    // throws Exception;

    public void setNode(DOMNode node) {
        this.node = node;
    }

    public DOMNode getNode() {
        return node;
    }

}
