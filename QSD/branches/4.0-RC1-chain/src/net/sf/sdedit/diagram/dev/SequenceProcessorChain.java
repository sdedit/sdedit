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

package net.sf.sdedit.diagram.dev;

import java.util.LinkedList;

import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.error.SyntaxError;
import net.sf.sdedit.util.Bijection;

public class SequenceProcessorChain {
    
    private final Diagram diagram;
    
    private LinkedList<SequenceProcessor<?>> chain;
    
    private Bijection<Drawable,Object>drawableBijection = new Bijection<Drawable, Object>();
    
    private Object state;
    
    public SequenceProcessorChain (Diagram diagram) {
        this.diagram = diagram;
        this.chain = new LinkedList<SequenceProcessor<?>>();
    }
    
    public void addSequenceProcessor (SequenceProcessor<?> processor) {
        chain.addLast(processor);
    }
    
    public Diagram getDiagram () {
        return diagram;
    }
    
    protected void addDrawable (Drawable drawable) {
        drawableBijection.add(drawable, state);
    }
    

    
    public void callNext (SequenceEntity element, Object state) throws SyntaxError, SemanticError {
        this.state = state;
        boolean match = false;
        while (!match) {
            if (chain.isEmpty()) {
                throw new IllegalStateException("no more processors available");
            }
            SequenceProcessor<?> sequenceProcessor = chain.removeFirst();
            if (sequenceProcessor.getElementClass().isAssignableFrom(element.getClass())) {
                match = true;
                sequenceProcessor.setElement(element);
                sequenceProcessor.processElement();
            }
        }
    }

}
