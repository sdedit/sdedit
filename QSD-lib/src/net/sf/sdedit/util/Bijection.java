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

package net.sf.sdedit.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @author Markus Strauch
 *
 * @param <P> the type of the pre-images
 * @param <I> the type of the images
 */
public class Bijection<P,I> {
	
	private Map<P,I> map;
	
	private Map<I,P> inverse;
	
	/**
	 * Constructor.
	 */
	public Bijection () {
		map = new HashMap<P,I>();
		inverse = new HashMap<I,P>();
	}
	
	/**
	 * Adds a pair of a pre-image and an image to the bijection.
	 * 
	 * @param preImage the pre-image
	 * @param image the image
	 */
	public void add (P preImage, I image) {
		I img = map.get(preImage);
		if (img != null) {
			inverse.remove(img);
		}
		map.put(preImage, image);
		inverse.put(image, preImage);
	}
	
	public void clear () {
	    map.clear();
	    inverse.clear();
	}
	
	public void removeByPreImage (P preImage) {
		I img = map.remove(preImage);
		inverse.remove(img);
	}
	
	/**
	 * Returns the image of the given pre-image
	 * 
	 * @param preImage
	 * @return
	 */
	public I getImage (P preImage) {
		return map.get(preImage);
	}
	
	public int size () {
	    return map.size();
	}
	
	/**
	 * Returns the pre-image of the given image
	 * 
	 * @param image
	 * @return
	 */
	public P getPreImage (I image) {
		return inverse.get(image);
	}
	
	public String toString () {
		return map.toString() + " || " + inverse.toString();
	}

}
//{{core}}
