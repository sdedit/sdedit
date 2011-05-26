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

package net.sf.sdedit.util.tree;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sf.sdedit.util.collection.DistinctObjectsMap;

public class TreeAdapter<T> implements TreeModel {
	
	public static enum DisplayMode {
		
		ALL_NODES,
		
		NO_LEAVES
		
	}

	private Tree<T> tree;

	private DistinctObjectsMap<T, Node> nodes;

	private Node root;

	private List<TreeModelListener> listeners;
	
	private WeakHashMap<Node, T[]> children;
	
	private DisplayMode displayMode;
	
	private Comparator<T> nodeComparator;
	
	public class Node {

		T t;

		public Node(T t) {
			this.t = t;
		}

		public T getObject() {
			return t;
		}

		@Override
		public String toString() {
			return t == null ? "null" : t.toString();
		}

	}

	public Tree<T> getTree() {
		return tree;
	}
	
	public void setNodeComparator(Comparator<T> comparator) {
	    this.nodeComparator = comparator;
	}

	private Node getNode(T t) {
		if (t == null) {
			return root;
		}

		Node node = nodes.get(t);

		if (node == null) {
			node = new Node(t);
			nodes.put(t, node);
		}
		return node;
	}

	public TreePath getPathTo(T t) {
		LinkedList<T> path = new LinkedList<T>();
		while (t != null) {
			path.addFirst(t);
			t = tree.getParent(t);
		}
		path.addFirst(null);
		LinkedList<Node> treePath = new LinkedList<Node>();
		for (T node : path) {
			treePath.add(getNode(node));
		}
		return new TreePath(treePath.toArray());
	}
	
	public T getObjectForPath (TreePath path) {
		Node node = (Node) path.getLastPathComponent();
		return node.t;
	}

	public TreeAdapter(Tree<T> tree) {
		this.tree = tree;
		nodes = new DistinctObjectsMap<T, Node>();
		root = new Node(null);
		listeners = new LinkedList<TreeModelListener>();
		children = new WeakHashMap<Node, T[]>();
		displayMode = DisplayMode.ALL_NODES;
	}
	
	public void setDisplayMode (DisplayMode newMode) {
		if (!newMode.equals(displayMode)) {
			this.displayMode = newMode;
			invalidateAll();
			fireTreeStructureChanged();

		}
	}
	
	@SuppressWarnings("unchecked")
	private TreeModelEvent translate (TreeModelEvent tme) {
		Object [] children = tme.getChildren();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				children [i] = getNode((T) children[i]);
			}
		}
		return new TreeModelEvent(tme.getSource(),tme.getTreePath(),tme.getChildIndices(),children);
	}
	
	public void fireTreeNodesRemoved (TreeModelEvent tme) {
		
		for (TreeModelListener tml : listeners) {
			tml.treeNodesRemoved(translate(tme));
		}
	}
	
	public void fireTreeNodesInserted (TreeModelEvent tme) {
		for (TreeModelListener tml : listeners) {
			tml.treeNodesInserted(translate(tme));
		}
	}
	public void fireTreeNodesChanged (TreeModelEvent tme) {
		for (TreeModelListener tml : listeners) {
			tml.treeNodesChanged(translate(tme));
		}
	}
	
	public void fireTreeStructureChanged (TreeModelEvent tme) {
		for (TreeModelListener tml : listeners) {
			tml.treeStructureChanged(tme);
		}
	}
	
	
	
	
	
	
	public void fireTreeStructureChanged () {
        TreeModelEvent tme = new TreeModelEvent(this,
                new Object[] { getRoot() });
        for (TreeModelListener tml : listeners) {
            tml.treeStructureChanged(tme);
        }
	}
	
	public void DESTROY () {
		nodes.clear();
		root = null;
		tree = null;
		listeners.clear();
		children.clear();
	}
	
	public void invalidateAll () {
		children.clear();
	}
	
	public void invalidate (T t) {
		children.remove(nodes.get(t));
	}
	
	private T [] _children(Node node) {
		T [] result = children.get(node);
		if (result == null) {
			result = tree.getChildren(node.t);
			if (nodeComparator != null) {
			    Arrays.sort(result, nodeComparator);
			}
			children.put(node, result);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private T [] children(Node node, boolean ignoreDisplayMode) {
		if (ignoreDisplayMode || displayMode == DisplayMode.ALL_NODES) {
			return _children(node);
		} else {
			List<T> cl = new LinkedList<T>();
			T [] children = _children(node);
			for (T child : children) {
				Node c = getNode(child);
				if (_children(c).length > 0) {
					cl.add(child);
				}
			}
			children = (T[]) Array.newInstance(children.getClass().getComponentType(), cl.size());
			return cl.toArray(children);
			
		}
	}
	
	public T [] children(Node node) {
		return children(node, false);
	}

	public void addTreeModelListener(TreeModelListener arg0) {
		listeners.add(arg0);
	}

	public Object getChild(Object arg0, int arg1) {
		Node node = (Node) arg0;
		return getNode(children(node)[arg1]);
	}

	public int getChildCount(Object arg0) {
		Node node = (Node) arg0;
		return children(node).length;
	}

	public int getIndexOfChild(Object arg0, Object arg1) {
		int n = getChildCount(arg0);
		for (int i = 0; i < n; i++) {
			if (arg1 == getChild(arg0, i)) {
				return i;
			}
		}
		return -1;
	}

	// public String toString() {
	// StringWriter sw = new StringWriter();
	// PrintWriter pw = new PrintWriter(sw);
	// for (int i = 0; i < getChildCount(this); i++) {
	// walk((T) getChild(this, i), pw, 0);
	// }
	// return sw.toString();
	//
	// }
	//
	// private void walk(T t, PrintWriter printWriter, int level) {
	// for (int i = 0; i < level; i++) {
	// printWriter.print(" ");
	// }
	// printWriter.println(t);
	// for (int i = 0; i < getChildCount(t); i++) {
	// walk((T) getChild(t, i), printWriter, level + 1);
	// }
	//
	// }

	public Object getRoot() {
		return root;
	}

	public boolean isLeaf(Object arg0) {
		return children((Node) arg0, true).length == 0;
	}

	public void removeTreeModelListener(TreeModelListener arg0) {
		listeners.remove(arg0);
	}

	public void valueForPathChanged(TreePath arg0, Object arg1) {

	}

}
