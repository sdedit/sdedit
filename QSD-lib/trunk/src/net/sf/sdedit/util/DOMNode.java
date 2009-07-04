package net.sf.sdedit.util;

import java.util.List;

public interface DOMNode {
    
    public List<DOMNode> getChildren ();
    
    public DOMNode getChild (String name);
    
    public DOMNode getParent ();
    
    public String getAttribute (String name);
    
    public List<DOMNode> getChildren (String name);
    
    public String getText();
    
    public DOMNode findNode (String xPath);
    
    public List<DOMNode> findNodes (String xPath);

}
