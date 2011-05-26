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

import net.sf.sdedit.Constants;
import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.diagram.MessageData;
import net.sf.sdedit.drawable.Arrow;

/**
 * A <tt>Message</tt> implementation is responsible for drawing a message and
 * changing the diagram's state in order to reflect the effects of a message.
 * 
 * @author Markus Strauch
 * 
 */
public abstract class Message implements Constants {

    private final Lifeline caller;

    private final Lifeline callee;

    protected final Diagram diagram;

    private final MessageData data;

    private final Configuration conf;

    private Arrow arrow;
        
    protected Message(Lifeline sender, Lifeline receiver, Diagram diagram,
            MessageData data) {
        this.caller = sender;
        this.callee = receiver;
        this.diagram = diagram;
        this.data = data;
        conf = diagram.getConfiguration();
    }

    /**
     * Returns a string representation of this message, in particular of the
     * caller and the callee lifeline.
     * 
     * @return a string representation of this message
     */
    public String toString() {
        return "{" + getClass().getSimpleName() + "} " + caller.toString()
                + " --[" + getText() + "]--> "
                + (callee == null ? "" : callee.toString());
    }

    public int getThread() {
        return caller.getThread();
    }

    protected final void setArrow(Arrow arrow) {
        this.arrow = arrow;
    }

    public final Arrow getArrow() {
        return arrow;
    }

    /**
     * Creates some space on the diagram, so that the message arrow and its
     * label can be drawn, then draws them and creates some more space. Changes
     * the state of the diagram so that the effects of the message are
     * reflected. When creating space, the lifelines that do not participate in
     * the message must be drawn according to their current state (via
     * {@linkplain #extendLifelines(int)}.
     * 
     */
    public abstract void updateView();

    /**
     * Returns the diagram on which the message is to be drawn.
     * 
     * @return the diagram on which the message is to be drawn
     */
    public final Diagram getDiagram() {
        return diagram;
    }

    /**
     * Returns the callee of the message (the receiver).
     * 
     * @return the callee of the message (the receiver)
     */
    public final Lifeline getCallee() {
        return callee;
    }

    /**
     * Returns the caller of the message (the sender)
     * 
     * @return the caller of the message (the sender)
     */
    public final Lifeline getCaller() {
        return caller;
    }

    /**
     * Returns the <tt>MessageData</tt> object belonging to this message.
     * 
     * @return the <tt>MessageData</tt> object belonging to this message
     */
    public final MessageData getData() {
        return data;
    }

    /**
     * Returns the diagram configuration.
     * 
     * @return the diagram configuration
     */
    public final Configuration getConfiguration() {
        return conf;
    }

    /**
     * Returns true if at least one of the caller or callee is an actor.
     * 
     * @return true if at least one of the caller or callee is an actor
     */
    public boolean isSynchronous() {
        return !getCallee().isActiveObject() && !data.isSpawnMessage()
                && !(getCaller().isAlwaysActive())
                && !(getCallee().isAlwaysActive());
    }

    /**
     * Draws all lifelines according to their current states, from
     * verticalPosition to verticalPosition+amount.
     * 
     * @param amount
     *            denotes the vertical size of the portion of the lifelines that
     *            is drawn by this method
     */
    protected final void extendLifelines(int amount) {
        diagram.extendLifelines(amount);
    }

    /**
     * Returns the text of the message, the string the message arrow is labeled
     * by.
     * 
     * @return the text of the message, the string the message arrow is labeled
     *         by
     */
    public abstract String getText();

    /**
     * Shorthand method - returns the diagram's current vertical position.
     * 
     * @return the diagram's current vertical position
     */
    protected final int v() {
        return diagram.getVerticalPosition();
    }

}//{{core}}
