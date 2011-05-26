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

/**
 * Represents the answer given to a call to an object's destructor. Is in
 * particular responsible for drawing the cross that denotes the destruction
 * resp. the vanishing of the lifeline of the destroyed lifeline.
 * 
 * @author Markus Strauch
 * 
 */
public class DestructorAnswer extends Answer
{

    /**
     * Creates a new destructor answer.
     * 
     * @param caller
     *            the lifeline of the object that destroys the callee
     * @param callee
     *            the lifeline to be destroyed
     * @param diagram
     *            the diagram where to draw
     * @param data
     *            encapsulates the answer
     */
    public DestructorAnswer(Lifeline caller, Lifeline callee, Diagram diagram,
            MessageData data, ForwardMessage forward) {
        super(caller, callee, diagram, data, forward);
    }

    public void updateView() {

        super.updateView();
        
        
        getCaller().destroy();
        //now getCaller().getCross() is not null

        int width = getConfiguration().getDestructorWidth();

        extendLifelines(3);

        getCaller().getCross().setTop(v());

        extendLifelines(width / 2);
    }
}
//{{core}}
