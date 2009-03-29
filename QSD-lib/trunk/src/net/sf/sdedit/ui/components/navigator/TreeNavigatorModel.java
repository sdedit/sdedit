package net.sf.sdedit.ui.components.navigator;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sf.sdedit.util.tree.BreadthFirstSearch;

public class TreeNavigatorModel implements TreeModel {

	private LinkedList<TreeModelListener> listeners;

	private TreeNavigatorNode root = new TreeNavigatorNode("", null, null);

	public TreeNavigatorModel() {
		listeners = new LinkedList<TreeModelListener>();
	}

	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	public Object getChild(Object parent, int index) {
		return ((TreeNavigatorNode) parent).getChild(index);
	}

	public int getChildCount(Object parent) {
		return ((TreeNavigatorNode) parent).getChildCount();
	}

	public int getIndexOfChild(Object parent, Object child) {
		TreeNavigatorNode[] children = ((TreeNavigatorNode) parent)
				.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i].equals(child)) {
				return i;
			}
		}
		return -1;
	}

	public TreeNavigatorNode getRoot() {
		return root;
	}

	public TreeNavigatorNode getCategoryNode(String category) {
		for (TreeNavigatorNode node : root.getChildren()) {
			if (node.getTitle().equals(category)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Returns the components managed by this
	 * <tt>TreeNavigatorModel</tt> in reverse BFS order
	 * @return
	 */
	public List<JComponent> getComponents() {
		LinkedList<JComponent> result = new LinkedList<JComponent>();
		BreadthFirstSearch bfs = new BreadthFirstSearch(this);
		TreePath path;
		do {
			path = bfs.next();
			if (path != null) {
				TreeNavigatorNode node = (TreeNavigatorNode) path
						.getLastPathComponent();
				if (node.getComponent() != null) {
					result.addFirst(node.getComponent());
				}
			}

		} while (path != null);
		return result;
	}

	public TreeNavigatorNode find(JComponent comp) {
		BreadthFirstSearch bfs = new BreadthFirstSearch(this);
		TreePath path;

		do {
			path = bfs.next();
			if (path != null) {
				TreeNavigatorNode node = (TreeNavigatorNode) path
						.getLastPathComponent();
				if (node.getComponent() == comp) {
					return node;
				}
			}
		} while (path != null);
		return null;
	}

	public void addChild(TreeNavigatorNode parent, TreeNavigatorNode child) {
		int i = parent.getChildCount();
		parent.addChild(child);
		TreeModelEvent tme = new TreeModelEvent(this, parent.getTreePath(),
				new int[] { i }, new Object[] { child });
		for (TreeModelListener l : listeners) {
			l.treeNodesInserted(tme);
		}
	}

	public void removeChild(TreeNavigatorNode parent, TreeNavigatorNode child) {
		int i = getIndexOfChild(parent, child);
		parent.removeChild(child);
		TreeModelEvent tme = new TreeModelEvent(this, parent.getTreePath(),
				new int[] { i }, new Object[] { child });
		for (TreeModelListener l : listeners) {
			l.treeNodesRemoved(tme);
		}
	}

	public void setTitle(TreeNavigatorNode node, String title) {
		node.setTitle(title);
		fireNodeChanged(node);
	}
	
	protected void fireComponentChanged(JComponent changed) {
		TreeNavigatorNode node = find(changed);
		fireNodeChanged(node);
	}
	
	protected void fireNodeChanged (TreeNavigatorNode node) {
		TreePath pathToParent;
		if (node.getParent() == null) {
			pathToParent = new TreePath(getRoot());			
		} else {
			pathToParent = node.getParent().getTreePath();
		}
		int [] c = new int [] {getIndexOfChild(pathToParent.getLastPathComponent(), node)};
		TreeModelEvent tme = new TreeModelEvent(this, pathToParent, c, new Object [] {node});
		for (TreeModelListener l : listeners) {
			l.treeNodesChanged(tme);
		}
	}

	public List<String> getAllTitles() {
		List<String> result = new LinkedList<String>();
		BreadthFirstSearch bfs = new BreadthFirstSearch(this);
		TreePath path;
		do {
			path = bfs.next();
			if (path != null) {
				TreeNavigatorNode node = (TreeNavigatorNode) path
						.getLastPathComponent();
				result.add(node.getTitle());
			}
		} while (path != null);
		return result;
	}

	public boolean isLeaf(Object node) {
		return ((TreeNavigatorNode) node).getChildCount() == 0;
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		/* not supported */
	}



}
