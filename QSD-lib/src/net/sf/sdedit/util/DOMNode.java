// Copyright (c) 2006 - 2011, Markus Strauch.
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// * Redistributions of source code must retain the above copyright notice, 
// this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
// this list of conditions and the following disclaimer in the documentation 
// and/or other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
// THE POSSIBILITY OF SUCH DAMAGE.
package net.sf.sdedit.util;

import java.util.List;

import org.w3c.dom.Node;

public interface DOMNode {
    
    public List<DOMNode> getChildren ();
    
    public DOMNode getChild (String name);
    
    public DOMNode getParent ();
    
    public void setAttribute(String name, String value);
    
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
