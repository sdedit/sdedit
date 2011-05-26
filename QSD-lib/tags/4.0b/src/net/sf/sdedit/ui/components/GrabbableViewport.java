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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.event.MouseInputListener;

/**
 * A <tt>GrabbableViewport</tt> is a <tt>JViewport</tt> that scrolls its
 * view when the mouse is dragged. While the mouse is being dragged, its cursor
 * is set to a &quot;grabbing hand&quot;, like in applications such as Acrobat
 * Reader.
 * 
 * @author Markus Strauch
 * 
 */
public class GrabbableViewport extends JViewport implements MouseInputListener {

	private static Cursor HAND = new Cursor(Cursor.MOVE_CURSOR);

	private static Cursor DFLT = new Cursor(Cursor.DEFAULT_CURSOR);
	
	public static void setHandCursorIcon(ImageIcon icon) {
		Image grabbingHand = icon.getImage();
		HAND = Toolkit.getDefaultToolkit().createCustomCursor(grabbingHand,
				new Point(0, 0), "hand cursor");
	}
	
	/**
	 * Constructor.
	 */
	public GrabbableViewport () {
		super ();
	}
	
	private Rectangle rect;

	private Point point;

	private JComponent view;

	public void setView(Component view) {
		super.setView(view);
		if (this.view != view) {
			if (this.view != null) {
				this.view.removeMouseListener(this);
				this.view.removeMouseMotionListener(this);
			}
			if (view != null) {
				view.addMouseListener(this);
				view.addMouseMotionListener(this);
			}
			this.view = (JComponent) view;
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		view.setCursor(HAND);
		//((Component) e.getSource()).setCursor(HAND);
		rect = getViewRect();
		point = screenLocation(e);
	}

	private Point screenLocation(MouseEvent e) {
		Point root = view.getLocationOnScreen();
		Point mouse = e.getPoint();
		if (rect != null && !rect.contains(mouse)) {
			return null;
		}
		Point screenPoint = new Point(root.x + mouse.x, root.y + mouse.y);
		return screenPoint;
	}

	public void mouseReleased(MouseEvent e) {
		view.setCursor(DFLT);
		scrollTo(screenLocation(e));
		clear();
	}

	public void mouseDragged(MouseEvent e) {
		scrollTo(screenLocation(e));
	}

	public void mouseMoved(MouseEvent e) {
	}

	private void scrollTo(Point newPoint) {
		if (point != null && newPoint != null && rect != null) {
			int deltaX = point.x - newPoint.x;
			int deltaY = point.y - newPoint.y;
			rect.x = rect.x + deltaX;
			rect.y = rect.y + deltaY;
			((JComponent) getView()).scrollRectToVisible(rect);
			point = newPoint;
		}
	}

	private void clear() {
		rect = null;
		point = null;
	}
}
