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
	
	private List<TreeNavigatorNode> children;
	
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
	
	public void addChild (TreeNavigatorNode child) {
		children.add(child);
		child.parent = this;
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
