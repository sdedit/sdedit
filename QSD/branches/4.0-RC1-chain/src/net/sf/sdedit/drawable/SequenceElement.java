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

import net.sf.sdedit.Constants;
import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.util.Direction;

public abstract class SequenceElement extends Drawable implements Constants {
	private String[] label;

	private ExtensibleDrawable leftEndpoint;

	private ExtensibleDrawable leftLimit;

	private ExtensibleDrawable rightEndpoint;

	private ExtensibleDrawable rightLimit;

	protected final Diagram diagram;

	private Direction align;

	private int rightPadding;

	private int leftPadding;

	protected final Configuration configuration;

	protected SequenceElement(Diagram diagram, Lifeline boundary0,
			Lifeline boundary1, String[] label, Direction align, int y) {
		setTop(y);
		this.align = align;
		this.diagram = diagram;
		configuration = configuration();
		this.label = label;
		findEndpoints(boundary0, boundary1);
		computePadding();
	}

	protected final Diagram diagram() {
		return diagram;
	}

	protected final int rightPadding() {
		return rightPadding;
	}

	protected final int leftPadding() {
		return leftPadding;
	}

	protected final Configuration configuration() {
		return diagram.getConfiguration();
	}

	/**
	 * Returns {@linkplain Direction#RIGHT}, if the sequence element is arranged
	 * to the right of its anchor, otherwise {@linkplain Direction#LEFT}. For
	 * arrows the anchor is the caller lifeline, for notes it is the lifeline to
	 * which the note is attached.
	 * 
	 * @return {@linkplain Direction#RIGHT}, if the sequence element is arranged
	 *         to the right of its anchor, otherwise {@linkplain Direction#LEFT}
	 */
	public final Direction getAlign() {
		return align;
	}

	private void computePadding() {
		int main = diagram.mainLifelineWidth;
		int sub = diagram.subLifelineWidth;

		if (leftEndpoint != null) {
			Lifeline left = leftEndpoint.getLifeline();
			if (left != null) {
				Lifeline rightMost = left.getRightmost();
				if (left == rightMost) {
					leftPadding = 0;
				} else if (rightMost.getDirection() == Direction.CENTER) {
					leftPadding = main + (left.getSideLevel() - 1) * sub;
				} else if (left.getDirection() == Direction.LEFT) {
					leftPadding = main + (left.getSideLevel() - 1) * sub
							+ rightMost.getSideLevel() * sub;
				} else {
					leftPadding = (rightMost.getSideLevel() - left
							.getSideLevel())
							* sub;
				}
			} else {
				leftPadding = 0;
			}
		} else {
			leftPadding = 0;
		}

		if (rightEndpoint != null) {

			Lifeline right = rightEndpoint.getLifeline();
			if (right != null) {
				Lifeline leftMost = right.getLeftmost();
				if (right == leftMost) {
					rightPadding = 0;
				} else if (leftMost.getDirection() == Direction.CENTER) {
					rightPadding = main + (right.getSideLevel() - 1) * sub;
				} else if (right.getDirection() == Direction.RIGHT) {
					rightPadding = main + (right.getSideLevel() - 1) * sub
							+ leftMost.getSideLevel() * sub;
				} else {
					rightPadding = (leftMost.getSideLevel() - right
							.getSideLevel())
							* sub;
				}
			} else {
				rightPadding = 0;
			}
		} else {
			rightPadding = 0;
		}
	}

	public int getSpace() {
		return 0;
	}

	public ExtensibleDrawable getLeftEndpoint() {
		return leftEndpoint;
	}

	public ExtensibleDrawable getRightEndpoint() {
		return rightEndpoint;
	}

	protected void setLeftEndpoint(ExtensibleDrawable leftEndpoint) {
		this.leftEndpoint = leftEndpoint;
	}

	protected void setRightEndpoint(ExtensibleDrawable rightEndpoint) {
		this.rightEndpoint = rightEndpoint;
	}

	protected int textWidth() {
		return textWidth(false);
	}
	
	protected int textWidth(boolean bold) {
		int width = 0;
		for (int i = 0; i < label.length; i++) {
			width = Math.max(width, diagram().getPaintDevice().getTextWidth(
					label[i], bold));
		}
		return width;
	}

	protected int textHeight() {
		return diagram().getPaintDevice().getTextHeight() * label.length;
	}

	private void findEndpoints(final Lifeline boundary0,
			final Lifeline boundary1) {

		if (boundary1 == null) {
			if (boundary0.getDirection() == Direction.LEFT) {
				leftEndpoint = boundary0.getLeftNeighbour().getView();
				rightEndpoint = boundary0.getView();
			} else {
				leftEndpoint = boundary0.getView();
				if (boundary0.getPosition() < diagram.getNumberOfLifelines() - 1) {
					rightEndpoint = boundary0.getRightNeighbour().getView();

				} else {
					rightEndpoint = diagram.getPaintDevice().getRightBound();
				}
			}
		} else {
			if (boundary0.getPosition() < boundary1.getPosition()) {
				leftEndpoint = boundary0.getView();
				rightEndpoint = boundary1.getView();
			} else {
				leftEndpoint = boundary1.getView();
				rightEndpoint = boundary0.getView();
			}
		}
	}

	protected void drawMultilineString(Graphics2D g, int x, int y,
			Color background) {
		drawMultilineString(g, label, x, y, diagram().getPaintDevice()
				.getTextHeight(), textWidth(), background);
	}

	public final ExtensibleDrawable getLeftLimit() {
		return leftLimit;
	}

	public final void setLeftLimit(ExtensibleDrawable leftLimit) {
		this.leftLimit = leftLimit;
	}

	public final ExtensibleDrawable getRightLimit() {
		return rightLimit;
	}

	public final void setRightLimit(ExtensibleDrawable rightLimit) {
		this.rightLimit = rightLimit;
	}
}
// {{core}}
