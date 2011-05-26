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
 * A specialized forward message, representing a call to a constructor.
 * <tt>ConstructorMessage</tt> objects are responsible for making the head of
 * the newly created object appear on the diagram.
 * 
 * @author Markus Strauch
 * 
 */
public class ConstructorMessage extends ForwardMessage
{
    /**
     * Creates a new constructor message.
     * 
     * @param caller
     *            the lifeline that calls the constructor
     * @param callee
     *            the yet hidden lifeline corresponding to the object newly
     *            created
     * @param diagram
     *            the diagram where to draw
     * @param data
     *            encapsulates the data of the constructor message
     */
    public ConstructorMessage(Lifeline caller, Lifeline callee,
            Diagram diagram, MessageData data) {
        super(caller, callee, diagram, data);
    }

    public void updateView() {
    	
        int headHeight = getCallee().getHead().getHeight();

    	getDiagram().getPaintDevice().announce(getConfiguration().getSpaceBeforeConstruction()
    		+ Math.max(0,Arrow.getInnerHeight(this) - headHeight / 2)
    		+ headHeight + getDiagram().getConfiguration().getInitialSpace());
    	
        extendLifelines(getConfiguration().getSpaceBeforeConstruction());
        
        int s = 0;
        Arrow arrow;
        if (getCaller().getPosition() < getCallee().getPosition()) {
            arrow = new Arrow(this, ArrowStroke.SOLID, Direction.RIGHT, v());
        } else {
            arrow = new Arrow(this, ArrowStroke.SOLID, Direction.LEFT, v());
            s=3;
        }
        setArrow(arrow);
        arrow.setSpace(s+getCallee().getHead().getWidth() / 2);
                //- getConfiguration().getMainLifelineWidth() / 2);

        getDiagram().getPaintDevice().addSequenceElement(arrow);


        
        int diff = arrow.getInnerHeight() - headHeight/2;
        if (diff > 0) {
            extendLifelines(diff);
        }

        getCallee().getHead().setTop(v());
        extendLifelines(headHeight/2);

        getCallee().giveBirth();
        
        getCallee().getView().setTop(v());
        getCallee().getView().setHeight(0);
        
        extendLifelines(headHeight/2 + getDiagram().getConfiguration().getInitialSpace());

        if (isActivating()) {
        	getCallee().setActive(true);
        }
    }
}
//{{core}}
