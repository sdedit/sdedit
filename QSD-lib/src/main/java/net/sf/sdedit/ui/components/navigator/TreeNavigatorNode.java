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
package net.sf.sdedit.ui.components.navigator;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.tree.TreePath;

public class TreeNavigatorNode {
	
	private String title;
	
	private Icon icon;
	
	private JComponent component;
	
	private LinkedList<TreeNavigatorNode> children;
	
	private TreeNavigatorNode parent;
	
	public TreeNavigatorNode (String title, Icon icon, JComponent component) {
		children = new LinkedList<TreeNavigatorNode>();
		this.title = title;
		this.icon = icon;
		this.component = component;
	}
	
	public TreePath getTreePath () {
		if (parent != null) {
			return parent.getTreePath().pathByAddingChild(this);
		} else {
			return new TreePath(this);
		}
	}
	
	public void setTitle (String title) {
		this.title = title;
	}
	
	public TreeNavigatorNode getParent () {
		return parent;
	}
	
	public String getTitle () {
		return title;
	}
	
	public Icon getIcon () {
		return icon;
	}
	
	public JComponent getComponent () {
		return component;
	}
	
	public int addChild (TreeNavigatorNode child, TreeNavigatorNode previousSibling) {
	    int pos;
	    if (previousSibling == null) {
	        pos = children.size();
	        children.add(child);
	    } else {
	        pos = children.indexOf(previousSibling) + 1;
	        if (pos < children.size() - 1) {
	            List<TreeNavigatorNode> tail = new LinkedList<TreeNavigatorNode>();
	            for (TreeNavigatorNode node : children.subList(pos, children.size())) {
	                tail.add(node);
	            }
	            children.set(pos, child);
	            children.add(null);
	            int i = 0;
	            for (TreeNavigatorNode tailNode : tail) {
	                children.set(pos+i+1, tailNode);
	            }
	        }
	    }
		child.parent = this;
		return pos;
	}
	
	public void removeChild (TreeNavigatorNode child) {
		children.remove(child);
	}
	
	public void removeChildByComponent (JComponent component) {
		ListIterator<TreeNavigatorNode> iter = children.listIterator();
		while (iter.hasNext()) {
			TreeNavigatorNode next = iter.next();
			if (next.getComponent() == component) {
				iter.remove();
				break;
			}
		}
		return;
	}
	
	public boolean equals (Object o) {
	    TreeNavigatorNode tnn = (TreeNavigatorNode) o;
	    return tnn.component == component;
	}
	
	public TreeNavigatorNode [] getChildren () {
		return children.toArray(new TreeNavigatorNode [children.size()]);
	}
	
	public TreeNavigatorNode getChild (int index) {
		return children.get(index);
	}
	
	public int getChildCount () {
		return children.size();
	}

}
