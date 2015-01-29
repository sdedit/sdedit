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

import java.io.Serializable;
import java.util.LinkedList;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * A <tt>BreadthFirstSearch</tt> searches through a model of a
 * {@linkplain JTree}.
 * 
 * 
 * @author Markus Strauch
 * 
 */
public class BreadthFirstSearch implements Serializable {

	private static final long serialVersionUID = -5513673019948703483L;

	private TreeModel tree;

	private LinkedList<TreePath> queue;
	
	private final Object root;
	
	private Object node;


	public BreadthFirstSearch(TreeModel tree, Object root) {
		this.tree = tree;
		queue = new LinkedList<TreePath>();
		this.root = root;
		restart();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param tree
	 *            the tree model to be searched
	 */
	public BreadthFirstSearch(TreeModel tree) {
		this(tree, null);
	}
	
	protected Object getRoot () {
		if (root == null) {
			return tree.getRoot();
		}
		return root;
	}

	/**
	 * Returns node currently being visited by this <tt>BreadthFirstSearch</tt>
	 * and visits the next node.
	 * 
	 * @return
	 */
	public TreePath next() {
		if (queue.isEmpty()) {
			return null;
		}
		TreePath next = queue.removeLast();
		node = next.getLastPathComponent();
		int n = tree.getChildCount(node);
		for (int i = 0; i < n; i++) {
			queue.addFirst(next.pathByAddingChild(tree.getChild(node, i)));
		}
		return next;
	}
	
	/**
	 * Changes the state of the search such that descendants of the node
	 * most recently returned by {@linkplain #next()} will not be
	 * searched.
	 */
	public void prune() {
		if (node != null) {
			int n = tree.getChildCount(node);
			for (int i = 0; i < n; i++) {
				queue.removeFirst();
			}
		}
	}
	
	/**
	 * Restarts the search, visits the root, such that the next call to
	 * {@linkplain #next()} will return the root of the tree model.
	 */
	public void restart() {
		queue.clear();
		Object root = getRoot();
		if (root != null) {
			queue.add(new TreePath(root));
		}
	}
}
