package net.sf.sdedit.util;

import org.w3c.dom.Node;

public class DOMNodeFactory {
    
    private DOMNodeFactory () {
        
    }
    
    public static DOMNode create (Node node) {
        return new DOMNodeAdapter(node);
    }

}
