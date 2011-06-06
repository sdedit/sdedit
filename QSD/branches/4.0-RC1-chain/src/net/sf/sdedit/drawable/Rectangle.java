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

import java.awt.Color;
import java.awt.Graphics2D;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.drawable.Strokes.StrokeType;

public class Rectangle extends ExtensibleDrawable
{
    private final Color color;

    private final String thread;

    private final Configuration configuration;

    public Rectangle(int width, Lifeline lifeline) {
        super(width, lifeline);
        thread = String.valueOf(getLifeline().getThread());
        int l = lifeline.getDiagram().getConfiguration().isColorizeThreads() && !lifeline.getDiagram().getConfiguration().isSlackMode() ? 
        		lifeline.getDiagram().threadColors.length
                : 1;
        color = lifeline.isAlwaysActive() ? Color.WHITE
                : lifeline.getDiagram().threadColors[lifeline.getThread() % l];
        configuration = lifeline.getDiagram().getConfiguration();
    }
    
    public final Color getColor () {
        return color;
    }

    protected void drawObject(Graphics2D g2d) {
        drawPartially(g2d, -1, -1);

    }
    
    private void drawPartially (Graphics2D g2d, int from, int to) {
        int top, height;
        if (from == -1) {
            top = getTop();
            height = getHeight();
        } else {
            top = Math.max(getTop(), from);
            int remainingHeight = getHeight() - top + getTop();
            height = Math.min(remainingHeight, to - from);
        }
        g2d.setStroke(Strokes.getStroke(StrokeType.SOLID, getLifeline().getDiagram().activationBarBorderThickness));
        g2d.setColor(color);
        g2d.fillRect(getLeft(), top, getWidth(), height);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(getLeft(), top, getWidth(), height);

        if (getLifeline().getDiagram().isThreaded()
                && !getLifeline().isAlwaysActive()
                && configuration.isThreadNumbersVisible()) {
            g2d.drawString(thread, getLeft() + 1, getTop() + 1
                    + g2d.getFontMetrics().getHeight());
        }

    }
}
//{{core}}
