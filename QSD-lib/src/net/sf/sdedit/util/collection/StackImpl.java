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

package net.sf.sdedit.util.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;


/**
 * Implementation of a {@linkplain IStack} with a <tt>LinkedList</tt> used
 * internally.
 * 
 * @author Markus Strauch
 *
 * @param <T>
 */
public class StackImpl<T> implements IStack<T>, Serializable {
	
	private static final long serialVersionUID = 7978799805762768700L;
	
	private LinkedList<T> list;
	
	public StackImpl () {
		list = new LinkedList<T>();
	}
	
	/**
	 * Creates a new stack, with the given initialContents. The first
	 * element of initialContents will be on the bottom of the stack, the
	 * last element will be on top of it.
	 * 
	 * @param initialContents
	 */
	public StackImpl(Collection<T> initialContents) {
		this();
		list.addAll(initialContents);
	}
	
	public void clear () {
		list.clear();
	}
	
	public void push (T t) {
		list.addLast(t);
	}
	
	public T pop () {
		return list.removeLast();
	}
	
	public T peek () {
		return list.getLast();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public int size() {
		return list.size();
	}
	
	public String toString () {
		return list.toString();
	}
}
