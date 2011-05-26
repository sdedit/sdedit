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

package net.sf.sdedit.ui.components.configuration;

/**
 * An interface tagged with/extending <tt>DataObject</tt> should only have get-,
 * set- and is-methods allowing access to (virtual) attributes like a Java bean.
 * Set-methods can be annotated with {@linkplain Adjustable}, so the
 * corresponding property will be managed by a {@linkplain Bean}.
 * 
 * @author Markus Strauch
 * 
 */
public interface DataObject {

	/**
	 * Returns the {@linkplain Bean} that is responsible for handling calls to
	 * the methods of an synthetic instance of this interface. This method need
	 * not be implemented by client code, it is implemented by the
	 * {@linkplain Bean} itself.
	 * 
	 * @param <T>
	 *            the interface extending <tt>DataObject</tt>
	 * @param cls
	 *            the corresponding interface class object
	 * @return the {@linkplain Bean} responsible for handling calls to synthetic
	 *         implementations of this interface
	 */
	public <T extends DataObject> Bean<T> getBean(Class<T> cls);
	
	public boolean isA (Class<?> cls);
	
	public <T extends DataObject> T cast (Class<T> cls);
	
	public <T extends DataObject> T copy (Class<T> cls);

}
// {{core}}
