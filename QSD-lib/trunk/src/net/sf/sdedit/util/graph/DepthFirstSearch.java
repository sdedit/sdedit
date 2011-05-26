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
package net.sf.sdedit.util.graph;

import java.util.Collection;

import net.sf.sdedit.util.Pair;
import net.sf.sdedit.util.collection.IStack;
import net.sf.sdedit.util.collection.StackImpl;

public class DepthFirstSearch {
	
	private IStack<Pair<Node,IStack<Edge>>> stack;
	
	private Collection<Node> allNodes;
	
	private static int n = 0;
	
	public DepthFirstSearch (Collection<Node> allNodes) {
		this.allNodes = allNodes;
		stack = new StackImpl<Pair<Node,IStack<Edge>>>();
	}
	
	private void push (Node node) {
		IStack<Edge> nodeStack = new StackImpl<Edge>();
		Pair<Node,IStack<Edge>> entry = new Pair<Node,IStack<Edge>>(node, nodeStack);
		for (Edge edge : node.getEdges()) {
			if (!edge.isVisited()) {
				nodeStack.push(edge);
			}
		}
		stack.push(entry);
	}
	
	public void start () {
		for (Node node : allNodes) {
			if (node.getTRoot() == null) {
				push(node);
				node.setTRoot(node);
				dfs ();
			}
		}
	}
	
	private void dfs () {
		while (!stack.isEmpty()) {
			Pair<Node,IStack<Edge>> entry = stack.peek();
			Node node = entry.getFirst();
			IStack<Edge> subStack = entry.getSecond();
			if (subStack.isEmpty()) {
				stack.pop();
			} else {
				Edge edge = subStack.pop();
				if (!edge.isVisited()) {
					edge.setVisited(true);
					n++;
					if (n % 100000 == 0) {
						System.out.println(n + " edges visited");
					}
					Node next = edge.getNode1() == node ? edge.getNode2() : edge.getNode1();
					if (next.getTRoot() == null) {
						push(next);
						next.setTRoot(node.getTRoot());
					}
				}
			}
		}
	}
}
