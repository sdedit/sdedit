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
import net.sf.sdedit.drawable.LoopArrow;
import net.sf.sdedit.util.Direction;

/**
 * A specialized forward message, directed from a lifeline onto itself, or - to
 * be precise - onto a its next sub-lifeline.
 * 
 * @author Markus Strauch
 * 
 */
public class MessageToSelf extends ForwardMessage
{

    /**
     * Creates a <tt>MessageToSelf</tt>, representing a message directed from
     * a lifeline onto its next sub-lifeline.
     * 
     * @param caller
     *            the lifeline that sends the message
     * @param callee
     *            its next sub-lifeline, representing another occurence in the
     *            activity trace, and receiving the message
     * @param diagram
     *            the diagram where to draw
     * @param data
     *            encapsulates the data of the message
     */
    public MessageToSelf(Lifeline caller, Lifeline callee, Diagram diagram,
            MessageData data) {
        super(caller, callee, diagram, data);
    }

    /**
     * Returns an answer from the sub-lifeline to the sender lifeline. In the
     * answer, the roles of caller and callee change.
     * 
     * @return an answer from the sub-lifeline to the sender lifeline
     */
    public AnswerToSelf getAnswerMessage() {
        if (isSynchronous()) {
            return new AnswerToSelf(getCallee(), getCaller(), getDiagram(),
                    getData(), this);
        }
        return null;
    }
    
    @Override
    public void updateView() {
    	getDiagram().getPaintDevice().announce(getConfiguration().getSpaceBeforeActivation() +
    			Arrow.getInnerHeight(this) + diagram.arrowSize / 2);
        extendLifelines(getConfiguration().getSpaceBeforeSelfMessage());
        Direction align = getCallee().getDirection();
        if (align == Direction.CENTER) {
        	// This is true for instantly returning self-messages directed onto
        	// the root lifeline
        	align = Direction.RIGHT;
        }
        Arrow arrow = new LoopArrow(this, ArrowStroke.SOLID,
                align, v());
        setArrow(arrow);
        extendLifelines(arrow.getInnerHeight());
        getCallee().setActive(true);
        getDiagram().getPaintDevice().addSequenceElement(arrow);
    }
}
//{{core}}
