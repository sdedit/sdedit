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

/**
 * An interface for an edge of an undirected graph, ready for being traversed
 * by a {@linkplain DepthFirstSearch}.
 * 
 * @author Markus Strauch
 *
 */
public interface Edge {
	
	/**
	 * Returns one endpoint of this <tt>Edge</tt>.
	 * 
	 * @return
	 */
	public Node getNode1 ();
	
	/**
	 * Returns the other endpoint of this <tt>Edge</tt>. 
	 * 
	 * @return
	 */
	public Node getNode2 ();
	
	/**
	 * Sets a flag denoting if this <tt>Edge</tt> has already been visited
	 * by a {@linkplain DepthFirstSearch}.
	 * 
	 * @param visited
	 */
	public void setVisited (boolean visited);
	
	/**
	 * Returns a flag denoting if this <tt>Edge</tt> has already been visited
	 * by a {@linkplain DepthFirstSearch}.
	 * 
	 * @return
	 */
	public boolean isVisited ();

}
