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
