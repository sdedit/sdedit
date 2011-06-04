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

package net.sf.sdedit.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import net.sf.sdedit.diagram.PaintDevice;
import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.drawable.Strokes;


public abstract class Graphics2DPaintDevice extends PaintDevice
{
    /**
     * Creates a graphics context belonging to a back-end that has a size
     * that suffices to display the whole of the diagram. This method should
     * be called after {@linkplain #computeBounds()} has finished and the
     * diagram's dimension is known.
     * 
     * @return a graphics context belonging to a back-end that has a size
     * that suffices to display the whole of the diagram
     */
    protected abstract Graphics2D createGraphics();
    
    /**
     * Creates a small-sized dummy graphics context that is used to
     * gain information about the font metrics (which should be the same
     * as in the final graphics context)
     * 
     * @param bold flag denoting if the bold font is set
     * @return a small-sized dummy graphics context
     */
    protected abstract Graphics2D createDummyGraphics(boolean bold);
    
    private Graphics2D dummy;
    
    private Graphics2D boldDummy;
    
    private Graphics2D g2d;
    
    protected Graphics2DPaintDevice () {
        super ();
    }
    
    /**
     * @see net.sf.sdedit.diagram.IPaintDevice#close()
     */
    @Override
    public void close () {
        super.close();
        g2d = createGraphics();
    }
    
    /**
     * Draws all of the diagram's elements into the graphics context
     * created via {@linkplain #createGraphics()}.
     */
    public void drawAll () {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(Strokes.defaultStroke());
        for (Drawable drawable : this) {
            drawable.draw(g2d);
        }
    }
    
    private Graphics2D dummy() {
        if (dummy == null) {
            dummy = createDummyGraphics(false);
        }
        return dummy;
    }
    
    private Graphics2D boldDummy () {
        if (boldDummy == null) {
            boldDummy = createDummyGraphics(true);
        }
        return boldDummy;
        
    }

    @Override
    public int getTextHeight(boolean bold) {
        return (bold ? boldDummy() : dummy()).getFontMetrics().getHeight();
    }

    @Override
    public int getTextWidth(String text, boolean bold) {
        return (bold ? boldDummy() : dummy()).getFontMetrics().stringWidth(text);
    }
}
//{{core}}
