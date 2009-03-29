// Copyright (c) 2006 - 2008, Markus Strauch.
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

package net.sf.sdedit.ui.components;

import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * A <tt>Zoomable</tt> object is an instance of a subclass of
 * <tt>JComponent</tt> that has a public <tt>paintComponent(Graphics)</tt>
 * method. According to the zoom factor set by the {@linkplain ZoomPane}
 * belonging to this <tt>Zoomable</tt>, the graphics is scaled before we
 * paint on it.
 * 
 * @author Markus Strauch
 * @param <T>
 *            the subtype of <tt>JComponent</tt> that the implementing class
 *            extends
 */
public interface Zoomable<T extends JComponent> {
	
	/**
	 * A <tt>JComponent#paintComponent(Graphics)</tt>, made public.
	 * 
	 * @param g
	 */
	public void paintComponent(Graphics g);

	/**
	 * Returns a JComponent reference to this Zoomable.
	 * 
	 * @return a JComponent reference to this Zoomable
	 */
	public T asJComponent();

	/**
	 * Returns the absolute, unscaled width.
	 * 
	 * @return the absolute, unscaled width
	 */
	public int getAbsoluteWidth();

	/**
	 * Returns the absolute, unscaled height
	 * 
	 * @return the absolute, unscaled height
	 */
	public int getAbsoluteHeight();

	/**
	 * Sets the <tt>ZoomPane</tt> that is used to display this
	 * <tt>Zoomable</tt>.
	 * 
	 * @param zoomPane
	 *            the <tt>ZoomPane</tt> that is used to display this
	 *            <tt>Zoomable</tt>
	 */
	public void setZoomPane(ZoomPane zoomPane);

	/**
	 * Returns the <tt>ZoomPane</tt> that is used to display this
	 * <tt>Zoomable</tt>.
	 * 
	 * @return the <tt>ZoomPane</tt> that is used to display this
	 *         <tt>Zoomable</tt>
	 */
	public ZoomPane getZoomPane();
}
