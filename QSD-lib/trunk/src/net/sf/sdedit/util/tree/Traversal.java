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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.sf.sdedit.util.Pair;
import net.sf.sdedit.util.collection.DistinctObjectsMap;
import net.sf.sdedit.util.collection.IStack;
import net.sf.sdedit.util.collection.StackImpl;

public class Traversal<T> {

	/**
	 * A stack of pairs associating nodes with their depths (relative to the
	 * root nodes specified via {@linkplain #traverse(Tree, Collection, int)}.
	 */
	private IStack<Pair<T, Integer>> stack;

	/**
	 * Mapping X's last child Y onto X.
	 */
	private DistinctObjectsMap<T, T> lastChildren;

	private TraversalControl<T> control;

	private Tree<T> tree;

	private int maxDepth;

	public Traversal(TraversalControl<T> control) {
		this.control = control;
		stack = new StackImpl<Pair<T, Integer>>();
		lastChildren = new DistinctObjectsMap<T, T>();
	}
	
	public void DESTROY () {
		stack.clear();
		lastChildren.clear();
	}
	
	public void traverse(Tree<T> tree) {
		List<T> roots = Arrays.asList(tree.getChildren(null));
		traverse(tree, roots, Integer.MAX_VALUE);
	}
	
	public void traverse(Tree<T> tree, T root) {
		List<T> list = new LinkedList<T>();
		list.add(root);
		traverse(tree, list, Integer.MAX_VALUE);
	}
	
	public void traverse(Tree<T> tree, Collection<T> roots) {
		traverse(tree, roots, Integer.MAX_VALUE);
	}

	/**
	 * Traverses the given tree, starting at the nodes as specified by the
	 * <tt>roots</tt> collections. All nodes that have a distance of at most
	 * <tt>depth</tt> to any of the <tt>roots</tt> will be traversed.
	 * 
	 * @param tree
	 * @param roots
	 * @param depth
	 */
	@SuppressWarnings("unchecked")
	public void traverse(Tree<T> tree, Collection<T> roots, int depth) {
		this.tree = tree;
		maxDepth = depth;
		stack.clear();
		Object[] initial = roots.toArray();
		for (int i = initial.length - 1; i >= 0; i--) {
			stack.push(new Pair<T, Integer>((T) initial[i], 0));
		}
		while (!stack.isEmpty()) {
			step();
		}
	}

	private void propagateTraversalEnd(T leaf) {
		T node = leaf;
		do {
			node = lastChildren.remove(node);
			if (node != null) {
				control.endTraversal(node, false);
			}
		} while (node != null);
	}

	private void step() {
		Pair<T, Integer> pair = stack.pop();
		T node = pair.getFirst();
		int depth = pair.getSecond();
		control.beginTraversal(node, depth);
		T lastChild = null;
		if (depth < maxDepth) {
			T[] children = tree.getChildren(node);
			for (int i = children.length - 1; i >= 0; i--) {
				T child = children[i];
				if (control.doTraverse(child, depth+1)) {
					if (lastChild == null) {
						lastChild = child;
					}
					stack.push(new Pair<T, Integer>(children[i], depth + 1));
				}
			}
		}
		if (lastChild == null) {
			control.endTraversal(node, true);
			propagateTraversalEnd(node);
		} else {
			lastChildren.put(lastChild, node);
		}
	}
}
