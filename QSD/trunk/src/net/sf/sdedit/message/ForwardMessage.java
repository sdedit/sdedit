//Copyright (c) 2006 - 2011, Markus Strauch.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.

package net.sf.sdedit.message;

import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.diagram.MessageData;
import net.sf.sdedit.drawable.Arrow;
import net.sf.sdedit.drawable.ArrowStroke;
import net.sf.sdedit.util.Direction;

/**
 * A <tt>ForwardMessage</tt> is a normal message sent from a lifeline to
 * another one. If the receiving lifeline (the callee) is not an actor, it
 * becomes active, or, if it already is active, it occurs twice in the trace of
 * activity, represented by another sub-lifeline attached to the original
 * lifeline.
 * 
 * @author Markus Strauch
 * 
 */
public class ForwardMessage extends Message {
	
	private final int level;

	/**
	 * Creates a new forward message to be drawn.
	 * 
	 * @param caller
	 *            the caller lifeline
	 * @param callee
	 *            the callee lifeline
	 * @param diagram
	 *            the diagram where to draw
	 * @param data
	 *            the data of the message
	 */
	public ForwardMessage(Lifeline caller, Lifeline callee, Diagram diagram,
			MessageData data) {
		super(caller, callee, diagram, data);
		this.level = 0;
		// TODO ? set level for pretty print
		// this.level = caller.getCallLevel();
	}
	
	public int getLevel () {
		return level;
	}

	/**
	 * Returns the answer to this forward message, if there is one, otherwise
	 * <tt>null</tt>. If this is a forward message to an actor, there is no
	 * answer. Otherwise an <tt>AnswerMessage</tt> instance with the same
	 * <tt>MessageData</tt> attached is returned, so the answer found in this
	 * data is an appropriate label of an arrow representing the answer.
	 * 
	 * @return the answer to this forward message, if there is one, otherwise
	 *         <tt>null</tt>
	 */
	public Answer getAnswerMessage() {
		if (getCallee().isAlwaysActive() || getData().isSpawnMessage()
				|| getCallee().isActiveObject()) {
			return null;
		}
		if (getCaller().isAlwaysActive() && getData().returnsInstantly()) {
			return null;
		}
		if (getText().equals("destroy") || getText().startsWith("destroy(")) {
			return new DestructorAnswer(getCallee(), getCaller(), getDiagram(),
					getData(), this);
		}

		return new Answer(getCallee(), getCaller(), getDiagram(), getData(),
				this);
	}

	/**
	 * Returns a flag denoting if this message activates its receiver.
	 * 
	 * @return a flag denoting if this message activates its receiver
	 */
	protected final boolean isActivating() {
		// messages that do not return instantly are always activating
		return !getData().returnsInstantly() 
		// non-spawning messages are activating even if they return instantly
		|| !getData().isSpawnMessage()
				&& !getCaller().isAlwaysActive();
	}

	public void updateView() {
		if (!(getCallee().isAlwaysActive())) {
			extendLifelines(getConfiguration().getSpaceBeforeActivation());
		}
		
		int ih = Arrow.getInnerHeight(this);
		getDiagram().getPaintDevice().announce (ih + diagram.arrowSize / 2);
		extendLifelines(ih);
		if (!getCallee().isAlwaysActive() && isActivating()) {
			getCallee().setActive(true);
		}
		Arrow arrow;
		if (getCaller().getPosition() < getCallee().getPosition()) {
			arrow = new Arrow(this, ArrowStroke.SOLID, Direction.RIGHT, v()
					- ih);
		} else {
			arrow = new Arrow(this, ArrowStroke.SOLID, Direction.LEFT, v() - ih);
		}
		setArrow(arrow);

		getDiagram().getPaintDevice().addSequenceElement(arrow);
	}

	/**
	 * @see net.sf.sdedit.message.Message#getText()
	 */
	public String getText() {
		return getData().getMessage();
	}

}//{{core}}
