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

import net.sf.sdedit.diagram.Lifeline;

/**
 * An <tt>ExtensibleDrawable</tt> represents the state of a lifeline during a
 * sequence. It is being extended - i. e. its height grows - as long as the
 * state remains the same. If the state changes, the <tt>ExtensibleDrawable</tt>
 * is closed and another instance is used for representing the state.
 * 
 * @author Markus Strauch
 * 
 */
public abstract class ExtensibleDrawable extends Drawable {

    /**
     * The lifeline of which the state is represented.
     */
    private final Lifeline lifeline;

    /**
     * Creates a new <tt>ExtensibleDrawable</tt> with an initial height of 0.
     * 
     * @param width
     *            the drawable width
     * @param lifeline
     *            the lifeline of which the state is represented by the
     *            <tt>ExtensibleDrawable</tt>
     */
    protected ExtensibleDrawable(int width, Lifeline lifeline) {
        setWidth(width);
        setHeight(0);
        this.lifeline = lifeline;
        if (lifeline != null) {
        	lifeline.addView(this);
        }
    }
    
    public boolean isVisible () {
    	if (lifeline == null) {
    		return super.isVisible();
    	} 
    	return !lifeline.isExternal() && super.isVisible();
    }

    /**
     * Returns the lifeline of which the state is represented.
     * 
     * @return the lifeline of which the state is represented
     */
    public Lifeline getLifeline() {
        return lifeline;
    }
    
    /**
     * Increases this <tt>ExtensibleDrawable</tt>'s height by the specified
     * amount.
     * 
     * @param amount
     *            the amount of pixels by which the height is to be increased
     */
    public final void extend(final int amount) {
        setHeight(getHeight() + amount);
    }

    /**
     * @see net.sf.sdedit.drawable.Drawable#computeLayoutInformation()
     */
    public void computeLayoutInformation() {
        /* empty */
    }

    /**
     * Draws as much of this <tt>ExtensibleDrawable</tt> as lies between the
     * specified vertical positions.
     * 
     * @param g2d
     *            the graphics context to draw into
     * @param from
     *            the vertical position where to start drawing, if it is -1,
     *            drawing is started at {@linkplain #getTop()}
     * @param to
     *            the vertical position where to end drawing, if it is -1,
     *            drawing ends at {@linkplain #getBottom()}
     */
    //public abstract void drawPartially(Graphics2D g2d, int from, int to);
}
//{{core}}
