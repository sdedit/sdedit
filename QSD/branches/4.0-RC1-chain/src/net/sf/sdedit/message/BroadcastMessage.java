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

package net.sf.sdedit.message;

import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.diagram.MessageData;
import net.sf.sdedit.drawable.Arrow;
import net.sf.sdedit.drawable.BroadcastArrow;
import net.sf.sdedit.util.Direction;

public class BroadcastMessage extends ForwardMessage {

	private Lifeline[] otherCallees;

	public static final int FIRST = 1;

	public static final int OTHER = 2;

	public static final int LAST = 3;

	public BroadcastMessage(Lifeline caller, Lifeline callee, Diagram diagram,
			MessageData data) {
		super(caller, callee, diagram, data);
	}

	@Override
	public Answer getAnswerMessage() {
		return null;
	}

	/**
	 * This method must only be called for the last of messages belonging to a
	 * broadcast.
	 * 
	 * @param otherCallees
	 *            the other receivers (beside <tt>this.getCallee()</tt> of the
	 *            broadcast)
	 */
	public void setOtherCallees(Lifeline[] otherCallees) {
		this.otherCallees = otherCallees;
	}

	@Override
	public void updateView() {
		if (getData().getBroadcastType() == FIRST) {
			getDiagram().getPaintDevice().announce(getConfiguration().getSpaceBeforeActivation() + 3 +
					Arrow.getInnerHeight(this) + diagram.arrowSize / 2);
			extendLifelines(getConfiguration().getSpaceBeforeActivation() + 3);
		}
		Arrow arrow;
		if (getCaller().getPosition() < getCallee().getPosition()) {
			arrow = new BroadcastArrow(this, Direction.RIGHT, v());
		} else {
			arrow = new BroadcastArrow(this, Direction.LEFT, v());
		}
		setArrow(arrow);
		if (getData().getBroadcastType() == LAST) {
			extendLifelines(arrow.getInnerHeight());
			for (Lifeline callee : otherCallees) {
				if (!callee.isAlwaysActive() && isActivating()) {
					callee.setActive(true);
				}
			}
			if (!getCallee().isAlwaysActive() && isActivating()) {
				getCallee().setActive(true);
			}

		}
		getDiagram().getPaintDevice().addSequenceElement(arrow);
	}

	@Override
	public boolean isSynchronous() {
		return false;
	}

}
//{{core}}
