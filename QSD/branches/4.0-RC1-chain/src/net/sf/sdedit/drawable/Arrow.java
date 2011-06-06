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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;

import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.drawable.Strokes.StrokeType;
import net.sf.sdedit.message.Answer;
import net.sf.sdedit.message.BroadcastMessage;
import net.sf.sdedit.message.Message;
import net.sf.sdedit.message.Primitive;
import net.sf.sdedit.util.Direction;

/**
 * An arrow is the graphical representation of a message.
 * 
 * @author Markus Strauch
 */
public class Arrow extends SequenceElement {
	
	private final ArrowStroke stroke;

	private final ArrowHeadType headType;

	private Message message;

	private int space;

	protected Point[] pts;

	protected Point textPoint;
	
	protected final boolean isAnswer;
	
	private int fontStyle;
	
	protected final Color color;
	
	protected Arrow(Message message, Lifeline boundary0, Lifeline boundary1,
			ArrowStroke stroke, Direction align, int y) {
		super(message.getDiagram(), boundary0, boundary1, message.getText()
				.split("\\\\n"), align, y);
		this.message = message;
		this.color = message.getDiagram().arrowColor;
		isAnswer = message instanceof Answer;
		int headSize;
		this.stroke = stroke;
		if (stroke != ArrowStroke.NONE) {
			headSize = diagram.arrowSize;
			headType = message instanceof BroadcastMessage ? ArrowHeadType.ROUNDED
					: message.isSynchronous() ? ArrowHeadType.CLOSED
							: ArrowHeadType.OPEN;
		} else {
			// does not matter, but final field must be initialized
			headType = ArrowHeadType.CLOSED;
			headSize = 0;
		}
		int totalTextHeight = textHeight();
		setHeight(totalTextHeight + configuration().getMessageLabelSpace()
				+ diagram.arrowSize / 2);

		setWidth(headSize + leftPadding() + rightPadding()
				+ diagram.messagePadding + textWidth());
		
		fontStyle = (message.getData().isStatic() && !isAnswer ? Font.ITALIC : 0)
		| (message.getData().isBold() && !isAnswer ? Font.BOLD: 0);
		
	}
	
	protected int textWidth() {
		boolean bold = (fontStyle & Font.BOLD) > 0;
		return textWidth(bold);
	}
	
	protected Font getFont (Font originalFont) {
		if (fontStyle == 0) {
			return originalFont;
		}
		return originalFont.deriveFont(fontStyle);
	}

	/**
	 * Creates a new <tt>Arrow</tt>.
	 * 
	 * @param message
	 *            the message that the arrow represents
	 * @param stroke
	 *            the type of the arrow (solid, dashed, or invisible for
	 *            {@linkplain Primitive} messages
	 * @param align
	 *            denotes the direction of the arrow
	 * @param y
	 *            the vertical position of the arrow
	 */
	public Arrow(Message message, ArrowStroke stroke, Direction align, int y) {
		this(message, message.getCaller(), message.getCallee(), stroke, align,
				y);
	}

	/**
	 * Returns an array of two points, representing the end points of the arrow.
	 * The first entry is the point where the arrow starts, the second is where
	 * the arrow ends.
	 * 
	 * @return an array of two points, representing the end points of the arrow
	 */
	public final Point[] getPoints() {
		return pts;
	}

	/**
	 * Returns the point where the message text is written.
	 * 
	 * @return the point where the message text is written
	 */
	public Point getTextPosition() {
		return textPoint;
	}

	/**
	 * Returns the sum of the height of the text and the space between the text
	 * and the arrow. In other words, this is the height minus the half height
	 * of the arrow head.
	 * 
	 * @return the sum of the height of the text and the space between the text
	 *         and the arrow
	 */
	public int getInnerHeight() {
		return textHeight() + diagram.messageLabelSpace;
	}

	public static int getInnerHeight(Message message) {
		int l = message.getText().split("\\\\n").length;
		return message.getDiagram().getPaintDevice().getTextHeight()
				* l
				+ message.getDiagram().getConfiguration()
						.getMessageLabelSpace();
	}

	/**
	 * Returns the amount of pixels between the end point of this arrow and the
	 * border of the lifeline it reaches. This is 0 for ordinary arrows and a
	 * positive number for constructor and destructor arrows.
	 * 
	 * @return the amount of pixels between the end point of this arrow and the
	 *         left border of the lifeline it reaches
	 */
	public final int getSpace() {
		return space;
	}

	/**
	 * Sets the amount of pixels between the end point of this arrow and the
	 * border of the lifeline it reaches. This is 0 for ordinary arrows and a
	 * positive number for constructor and destructor arrows.
	 * 
	 * @param space
	 *            the amount of pixels between the end point of this arrow and
	 *            the left border of the lifeline it reaches
	 */
	public final void setSpace(int space) {
		this.space = space;
	}

	/**
	 * @see net.sf.sdedit.drawable.Drawable#drawObject(java.awt.Graphics2D)
	 */
    @Override
	protected void drawObject(Graphics2D g2d) {
		Font font = g2d.getFont();
		g2d.setFont(getFont(font));
		drawText(g2d);
		g2d.setFont(font);
        g2d.setColor(color);
		int sgn = getAlign() == Direction.LEFT ? 1 : -1;

		if (stroke != ArrowStroke.NONE) {
			g2d.setStroke(stroke == ArrowStroke.DASHED ? dashed() : solid());
			g2d.drawLine(pts[0].x, pts[0].y, pts[1].x, pts[1].y);
			g2d.setStroke(solid());
			drawArrowHead(g2d, pts[1].x, pts[1].y, sgn);
			if (message.getCaller().isExternal()) {
				int as = diagram.arrowSize;
				int offset = sgn == -1 ? as : 0;
				g2d.fillOval(pts[0].x - offset, pts[0].y - as / 2, as, as);
			}
		}
    }

	protected void drawText(Graphics2D g2d) {
		int t = getMessage().getData().getThread();
		Color back = getMessage().getData().getMessage().length() == 0
				|| !diagram.opaqueText || t == -1 ? null
				: getMessage().getDiagram().threadColors[t];
		drawMultilineString(g2d, textPoint.x, textPoint.y, back);
	}

	/**
	 * Returns the point in the middle of this arrow, the one that serves as an
	 * anchor when a connection to a {@linkplain Note} is made.
	 * 
	 * @return the point in the middle of this arrow
	 */
	public Point getAnchor() {
		return new Point(pts[0].x + (pts[1].x - pts[0].x) / 2, pts[0].y);
	}

	/**
	 * @see net.sf.sdedit.drawable.Drawable#computeLayoutInformation()
	 */
	public void computeLayoutInformation() {
		int left = getLeftEndpoint().getLeft() + getLeftEndpoint().getWidth();
		int right = getRightEndpoint().getLeft();

		Direction align = getAlign();

		if (align == Direction.LEFT) {
			left = left + space;
		} else {
			right = right - space;
		}
		setLeft(left);
		setWidth(Math.max(getWidth(), right - left));

		int x_from = align == Direction.LEFT ? getLeft() + getWidth()
				: getLeft();
		int x_to = align == Direction.LEFT ? getLeft() : getLeft() + getWidth();

		int sgn = align == Direction.LEFT ? 1 : -1;

		int text_x = sgn == 1 ? x_from - diagram.messagePadding - textWidth()
				- rightPadding() : x_from + diagram.messagePadding
				+ leftPadding();
		int v = getTop() + textHeight() + diagram.messageLabelSpace;

		pts = new Point[2];

		pts[0] = new Point(x_from, v);
		pts[1] = new Point(x_to, v);

		textPoint = new Point(text_x, v - diagram.messageLabelSpace);
	}

	/**
	 * Draws the head of a message arrow onto the diagram display.
	 * 
	 * @param g
	 *            the graphics context of the diagram display
	 * @param closed
	 *            flag denoting whether the arrow head is closed (filled) or not
	 * @param x
	 *            the horizontal position where to start drawing the arrow
	 * @param y
	 *            the vertical position of the middle point of the arrow
	 * @param sgn
	 *            1, if the arrow is directed from the right to the left, -1
	 *            otherwise
	 */
	protected final void drawArrowHead(Graphics2D g, int x, int y, int sgn) {
		g.setStroke(Strokes.getStroke(StrokeType.SOLID, 1));
		int size = diagram.arrowSize;
		switch (headType) {
		case CLOSED:
			Polygon p = new Polygon(new int[] { x, x + sgn * size,
					x + sgn * size, x },
					new int[] { y, y - size, y + size, y }, 4);
			g.fillPolygon(p);
			break;
		case OPEN:
			g.drawLine(x, y, x + sgn * size, y - size);
			g.drawLine(x, y, x + sgn * size, y + size);
			break;
		case ROUNDED:
			int left = sgn == -1 ? x - 2 * size : x;
			int top = y - size;
			g.fillArc(left, top, size * 2, size * 2, 90, sgn * 180);
		}

	}

	/**
	 * Returns the <tt>Message</tt> that this arrow represents.
	 * 
	 * @return the <tt>Message</tt> that this arrow represents
	 */
	public final Message getMessage() {
		return message;
	}

	protected final ArrowStroke getStroke() {
		return stroke;
	}
	
	protected final Stroke dashed () {
	    return Strokes.getStroke(StrokeType.DASHED, diagram.arrowThickness);
	}
	
	protected final Stroke solid () {
	    return Strokes.getStroke(StrokeType.SOLID, diagram.arrowThickness);
	}
	
	@Override
	public java.awt.Rectangle getRectangle () {
		java.awt.Rectangle r; 
		if (textPoint != null) {
			r = new java.awt.Rectangle();
			r.x = textPoint.x;
			r.y = textPoint.y;
			r.height = textHeight();
			r.width = textWidth();
		} else {
			r = super.getRectangle();
		}
		return r;
	}
}
//{{core}}
