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

import java.util.List;

/**
 * An interface for a node of an undirected graph.
 * 
 * @author Markus Strauch
 */
public interface Node {

	/**
	 * Returns the list of (undirected) {@linkplain Edge}s this <tt>Node</tt> is
	 * incident to.
	 * 
	 * @return the list of (undirected) {@linkplain Edge}s this <tt>Node</tt> is
	 *         incident to
	 */
	public List<Edge> getEdges();

	/**
	 * Returns a representative node that is inside the same component as
	 * this <tt>Node</tt>.
	 * 
	 * @return
	 */
	public Node getTRoot();


	/**
	 * Sets a representative node that is inside the same component as
	 * this <tt>Node</tt>.
	 * 
	 * @return
	 */
	public void setTRoot(Node tRoot);

	
	public String getName();
}
