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
import java.util.HashMap;
import java.util.Map;

public class OntoMap<P, I> implements Serializable {

	private static final long serialVersionUID = 7466889579477428610L;

	private Map<P, I> map;

	private Map<I, Collection<?>> reverse;

	private Class<? extends Collection<?>> collectionClass;

	@SuppressWarnings("unchecked")
	public OntoMap(Class<?> collectionClass) {
		if (!Collection.class.isAssignableFrom(collectionClass)) {
			throw new IllegalArgumentException ("no collection class: " + collectionClass.getName());
		}
		this.collectionClass = (Class<? extends Collection<?>>) collectionClass;
		map = new HashMap<P, I>();
		reverse = new HashMap<I, Collection<?>>();
	}

	public Collection<P> getPreImages() {
		return map.keySet();
	}
	
	public Collection<I> getImages () {
		return reverse.keySet();
	}
	
	public void clear () {
		map.clear();
		reverse.clear();
	}
	
	public boolean containsImage (I image) {
		return reverse.containsKey(image);
	}

	private Collection<?> newCollection() {
		try {
			Collection<?> c = collectionClass.newInstance();
			return c;
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception t) {
			throw new IllegalStateException(t);
		}
	}

	@SuppressWarnings("unchecked")
	public void add(P preImage, I image) {
		map.put(preImage, image);
		Collection<?> preImages = reverse.get(image);
		if (preImages == null) {
			preImages = newCollection();
			reverse.put(image, preImages);
		}
		((Collection<P>) preImages).add(preImage);
	}
	
	public void removeAllPreimages (I image) {
		reverse.remove(image);
	}

	public void remove(P preImage) {
		Collection<?> preImages = reverse.get(getImage(preImage));
		if (preImages != null) {
			preImages.remove(preImage);
		}
	}

	public I getImage(P preImage) {
		return map.get(preImage);
	}
	
	public Collection<Map.Entry<P,I>> entries () {
		return map.entrySet();
	}

	@SuppressWarnings("unchecked")
	public Collection<P> getPreImages(I image) {
		Collection<?> preImages = reverse.get(image);
		if (preImages == null) {
			return (Collection<P>) newCollection();
		}
		return (Collection<P>) preImages;
	}
}
