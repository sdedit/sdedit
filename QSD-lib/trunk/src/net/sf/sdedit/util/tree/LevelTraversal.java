package net.sf.sdedit.util.tree;

import java.util.LinkedList;

public class LevelTraversal<T> {

	private TraversalControl<T> traversalControl;
	
	private class Node {
		
		int level;
		
		T t;
		
	}

	public LevelTraversal(TraversalControl<T> traversalControl) {
		this.traversalControl = traversalControl;
	}

	public void traverse(Tree<T> tree) {
		LinkedList<Node> queue = new LinkedList<Node>();
		for (T t : tree.getChildren(null)) {
			if (traversalControl.doTraverse(t, 0)) {
				Node node = new Node();
				node.level=0;
				node.t = t;
				queue.addFirst(node);
			}
		}
		while (!queue.isEmpty()) {
			Node node = queue.removeLast();
			traversalControl.beginTraversal(node.t, node.level);
			boolean leaf = true;
			for (T t : tree.getChildren(node.t)) {
				if (traversalControl.doTraverse(t, node.level+1)) {
					leaf = false;
					Node newNode = new Node ();
					newNode.level = node.level + 1;
					newNode.t = t;
					queue.addFirst(node);
				}
			}
			traversalControl.endTraversal(node.t, leaf);
		}
	}

}
