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
 * Represents an answer to a {@linkplain ForwardMessage} and is responsible for
 * drawing it on the diagram.
 * 
 * @author Markus Strauch
 * 
 */
public class Answer extends Message
{

    private final ForwardMessage forward;

    /**
     * Creates a new answer to be drawn.
     * 
     * @param caller
     *            the sender of the answer, i. e. the receiver (callee) of the
     *            corresponding forward message
     * @param callee
     *            the receiver of the answer
     * @param diagram
     *            the diagram where to draw
     * @param data
     *            the data of the message, where the answer string is a proper
     *            label for the answer arrow
     * @param forwardMsg
     *            the message that this is the answer to
     */
    public Answer(Lifeline caller, Lifeline callee, Diagram diagram,
            MessageData data, ForwardMessage forwardMsg) {
        super(caller, callee, diagram, data);
        forward = forwardMsg;
    }

    /**
     * Returns the <tt>ForwardMessage</tt> to which this is the answer.
     * 
     * @return the <tt>ForwardMessage</tt> to which this is the answer
     */
    public final ForwardMessage getForwardMessage() {
        return forward;
    }

    public void updateView() {

        getDiagram().getFragmentManager().finishFragmentsNotIncluding(this);
    	if (getCallee().isAlwaysActive()) {
    		return;
    	}
        ArrowStroke stroke;
        if (getCallee().isExternal() || getText().equals("") && getCallee().isAlwaysActive()) {
            stroke = ArrowStroke.NONE;
        } else {
            stroke = ArrowStroke.DASHED;
        }
        Arrow arrow;
        getDiagram().getPaintDevice().announce(Arrow.getInnerHeight(this) +
        	diagram.arrowSize / 2);
        if (getCaller().getPosition() < getCallee().getPosition()) {
            arrow = new Arrow(this, stroke, Direction.RIGHT, v());
        } else {
            arrow = new Arrow(this, stroke, Direction.LEFT, v());
        }
        arrow.setVisible(getText().length()>0 || diagram.returnArrowVisible);
        setArrow(arrow);
        getDiagram().getPaintDevice().addSequenceElement(arrow);
        extendLifelines(arrow.getInnerHeight());
        if (!(getCaller().isAlwaysActive())) {
            terminate();
        }
        
    }

    /**
     * Terminates the caller lifeline (sender of the answer). If it is a
     * sub-lifeline, it will be disposed, main lifelines just become inactive,
     * represented by a dashed line.
     */
    protected final void terminate() {
        getCaller().finish();
        if (getCaller().getRoot() != getCaller()) {
            getCaller().dispose();
        }
    }

    /**
     * @see net.sf.sdedit.message.Message#getText()
     */
    public String getText() {
        return getData().getAnswer();
    }

}
//{{core}}
