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
import net.sf.sdedit.drawable.ArrowStroke;
import net.sf.sdedit.util.Direction;

/**
 * A Primitive is not a message in the strict sense, it is an action that
 * happens inside a method. Graphically, it is represented in a fashion similar
 * to a message, but without the arrow.
 * 
 * @author Markus Strauch
 */
public final class Primitive extends ForwardMessage {
	/**
	 * Creates a Primitive object.
	 * 
	 * @param caller
	 *            the lifeline where the method of which the Primitive is a part
	 *            of is executed
	 * @param diagram
	 *            the diagram
	 * @param data
	 *            encapsulates the description of the primitive action
	 */
	public Primitive(Lifeline caller, Diagram diagram, MessageData data) {
		super(caller, null, diagram, data);
	}

	public void updateView() {
		
		/*
		 PROPOSED IMPLEMENTATION:
		 
		if (!isVoid() && !isSynchronizing()) {
			if (!(getCaller().isAlwaysActive())) {
				extendLifelines(getConfiguration().getSpaceBeforeActivation());
			}
		}
		getCaller().setActive(true);
		if (!isVoid()) {
			if (!isSynchronizing()) {
				final Direction direction = getCaller().getDirection() == Direction.LEFT ? Direction.LEFT
						: Direction.RIGHT;
				final Arrow arrow = new Arrow(this, ArrowStroke.NONE, direction,
						v());
				setArrow(arrow);
				getDiagram().getPaintDevice().addSequenceElement(arrow);
				extendLifelines(arrow.getInnerHeight());
			} else {
				getDiagram().toggleWaitingStatus(getCaller().getThread());
			}
		}
		
		*/
		
		if (!isVoid()) {
			getDiagram().getPaintDevice().announce(getConfiguration().getSpaceBeforeActivation() +
					Arrow.getInnerHeight(this));
			if (!(getCaller().isAlwaysActive())) {
				extendLifelines(getConfiguration().getSpaceBeforeActivation());
			}
		}
		getCaller().setActive(true);
		if (!isVoid()) {
			final Direction direction = getCaller().getDirection() == Direction.LEFT ? Direction.LEFT
					: Direction.RIGHT;
			final Arrow arrow = new Arrow(this, ArrowStroke.NONE, direction,
					v());
			setArrow(arrow);
			getDiagram().getPaintDevice().addSequenceElement(arrow);
			extendLifelines(arrow.getInnerHeight());
		}
	}

	/**
	 * Obviously, there is no answer to a primitive action, so this method
	 * always returns <tt>null</tt>.
	 * 
	 * @return <tt>null</tt>
	 */
	@Override
	public final Answer getAnswerMessage() {
		return null;
	}

	public final boolean isVoid() {
		return getData().getMessage().equals("_");
	}
	
	public final boolean isSynchronizing () {
		return !getCaller().isAlwaysActive() && getData().getMessage().equals("!");
	}
}
//{{core}}
