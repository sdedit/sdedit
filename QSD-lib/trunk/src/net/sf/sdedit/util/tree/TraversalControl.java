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

public interface TraversalControl<T> {

	/**
	 * Returns a flag denoting if the node <tt>t</tt> is to be traversed.
	 * 
	 * @param t
	 *            a node of the tree being traversed
	 * @param level
	 *            the distance from the root that is an ancestor of <tt>t</tt>
	 *            to t
	 */
	public boolean doTraverse(T t, int level);

	/**
	 * This method is called by a {@linkplain Traversal} before the node
	 * <tt>t</tt> is traversed.
	 * 
	 * 
	 * @param t
	 *            a node, before it is traversed
	 * @param level
	 *            the distance from the root that is an ancestor of <tt>t</tt>
	 *            to t
	 */
	public void beginTraversal(T t, int level);

	/**
	 * This method is called by a {@linkplain Traversal} after the node
	 * <tt>t</tt> and all of its descendants have been traversed.
	 * 
	 * @param t
	 *            a node, after it has been traversed
	 * @param leaf
	 *            a flag denoting if <tt>t</tt> is a leaf
	 */
	public void endTraversal(T t, boolean leaf);

}
