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
package net.sf.sdedit.drawable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import net.sf.sdedit.Constants;

/**
 * A <tt>Drawable</tt> object is a representation of a visible component that
 * occurs in a sequence diagram. This representation is not concerned with
 * semantic aspects of the component, it just provides the information and the
 * behaviour that is necessary to display the component.
 * <p>
 * A {@linkplain #drawObject(Graphics2D)} method must be implemented, so a
 * <tt>Drawable</tt> object is able to draw itself into a <tt>Graphics</tt>
 * context.
 * 
 * @author Markus Strauch
 * 
 */
public abstract class Drawable implements Constants {

	private int top, left, height, width;

	private boolean visible;

	/**
	 * Creates a new <tt>Drawable</tt> with the visibility set to <tt>true</tt>
	 */
	protected Drawable() {
		visible = true;
	}
	
	public void draw (Graphics2D g2d) {
	    Graphics2D g2 = (Graphics2D) g2d.create();
	    drawObject(g2);
	    g2.dispose();
	}

	/**
	 * Draws the sequence diagram element into the given <tt>Graphics2D</tt>
	 * context, using the rectangle starting at {@linkplain #getTop()},
	 * {@linkplain #getLeft()}, sized {@linkplain #getHeight()},
	 * {@linkplain #getWidth()}.
	 * 
	 * @param g2d
	 *            the Graphics2D context to draw this drawable sequence diagram
	 *            element into
	 */
	protected abstract void drawObject(Graphics2D g2d);

	/**
	 * This method is called when and if the left and top positions of the
	 * drawable components of a sequence diagram are fixed. Its purpose is to
	 * use this information to compute even finer layout information, such as
	 * the width of the component.
	 */
	public abstract void computeLayoutInformation();

	public final int getTop() {
		return top;
	}

	public final int getLeft() {
		return left;
	}

	public final int getWidth() {
		return width;
	}

	public final int getHeight() {
		return height;
	}

	public final void setHeight(int height) {
		this.height = height;
	}

	public final void setLeft(int left) {
		this.left = left;
	}

	public final void setTop(int top) {
		this.top = top;
	}

	public final void setWidth(int width) {
		this.width = width;
	}

	public final int getBottom() {
		return top + height;
	}

	public final int getRight() {
		return left + width;
	}

	public final void setRight(int right) {
		setWidth(right - left);
	}

	public final void setBottom(int bottom) {
		setHeight(bottom - getTop());
	}
	
	public java.awt.Rectangle getRectangle () {
		java.awt.Rectangle r = new java.awt.Rectangle();
		r.x = getLeft();
		r.y = getTop();
		r.width = getWidth();
		r.height = getHeight();
		return r;
	}

	/**
	 * Returns <tt>true</tt> if this drawable component's bounds intersect the
	 * rectangle given.
	 * 
	 * @param rectangle
	 *            a rectangle specifiying an area of the sequence diagram
	 * @return true if this drawable component's bounds intersect the rectangle
	 */
	public boolean intersects(java.awt.Rectangle rectangle) {
		return rectangle.intersects(left - 10, top - 10, width + 20,
				height + 20);
	}

	public boolean contains(Point point) {
		int x = point.x;
		int y = point.y;
		return x >= left && x <= left + width && y >= top && y <= top + height;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible
	 *            the visible to set
	 */
	public final void setVisible(boolean visible) {
		this.visible = visible;
	}

	protected static final void drawMultilineString(Graphics2D g,
			String[] string, int x, int y, int simpleHeight, int width,
			Color background) {
		if (background != null) {
			g.setColor(background);
			int height = 2 + string.length * simpleHeight;
			int top;
			top = y - height + 2;
			g.fillRect(x - 1, top, width, height);
		}
		g.setColor(Color.BLACK);
		for (int i = 0; i < string.length; i++) {
			int yy = y - i * simpleHeight;
			g.drawString(string[string.length - 1 - i], x, yy);
		}
	}

}
// {{core}}
