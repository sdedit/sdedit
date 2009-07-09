package net.sf.sdedit.util;

import java.util.List;

import org.w3c.dom.Node;

public interface DOMNode {
    
    public List<DOMNode> getChildren ();
    
    public DOMNode getChild (String name);
    
    public DOMNode getParent ();
    
    public String getAttribute (String name);
    
    public List<DOMNode> getChildren (String name);
    
    public boolean isLeaf ();
    
    public String getText();
    
    public DOMNode findNode (String xPath);
    
    public List<DOMNode> findNodes (String xPath);
    
    public String getName ();
    
    public int getLevel();
    
    public List<DOMNode> getDescendantsDFS ();
    
    public String toTreeString();
    
    public void setUserObject (String name, Object object);
    
    public Object getUserObject (String name);
    
    public List<String> getAttributeNames ();
    
    public <T extends Node> List<DOMNode> getChildren (Class<T> nodeClass);
    
    public <T extends Node> DOMNode getPreviousSibling(Class<T> nodeClass);
    

    public <T extends Node> DOMNode getNextSibling(Class<T> nodeClass);
    
    
}
