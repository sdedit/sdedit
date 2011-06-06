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
import java.awt.Stroke;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.drawable.Strokes.StrokeType;

public class LabeledBox extends Drawable {

    private String label;

    private boolean underlined;

    private int headWidth;

    private int headHeight;

    private int textWidth;

    private int padding;

    private Stroke stroke;

    private Lifeline lifeline;

    public LabeledBox(Lifeline lifeline, String _label, int y,
            boolean anonymous, boolean underlined) {
        setTop(y);
        this.underlined = underlined;
        this.lifeline = lifeline;
        if (lifeline.isExternal()) {
            label = "";
        } else if (!_label.equals("")) {
            label = _label;
        } else if (anonymous) {
            label = ":" + lifeline.getType();
        } else {
            label = lifeline.getName() + ":" + lifeline.getType();
        }
        Configuration conf = lifeline.getDiagram().getConfiguration();
        headWidth = conf.getHeadWidth();
        headHeight = conf.getHeadHeight();
        padding = conf.getHeadLabelPadding();
        textWidth = lifeline.getDiagram().getPaintDevice().getTextWidth(label);
        if (lifeline.isExternal()) {
            setWidth(lifeline.getDiagram().mainLifelineWidth);
        } else {
            setWidth(2 + Math.max(2 * padding + textWidth, headWidth));
        }
        stroke = lifeline.isAlwaysActive() ? Strokes.getStroke(
                StrokeType.SOLID, 2) : Strokes.getStroke(StrokeType.SOLID, 1);
        setHeight(headHeight + 4);

    }

    public boolean isVisible() {
        return !lifeline.isExternal() && super.isVisible();
    }

    protected void drawObject(Graphics2D g2d) {
        int axis = getLeft() + getWidth() / 2;
        int top = getTop();
        int width = getWidth();

        if (lifeline.getDiagram().getConfiguration()
                .isShouldShadowParticipants()) {
            g2d.fillRect(axis - width / 2 + 2, top + 2, width + 2,
                    headHeight + 2);
        }

        g2d.setColor(lifeline.getDiagram().getConfiguration()
                .getLabeledBoxBgColor());
        g2d.fillRect(axis - width / 2, top, width, headHeight);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(stroke);
        g2d.drawRect(axis - width / 2, top, width, headHeight);
        g2d.setStroke(Strokes.defaultStroke());
        int left = axis - textWidth / 2;

        int baseLine = top + headHeight / 2;

        if (underlined) {
            g2d.drawLine(left, baseLine + 2, left + textWidth, baseLine + 2);
        }
        g2d.drawString(label, left, baseLine);
    }

    public void computeLayoutInformation() {
        /* empty */
    }
}
// {{core}}
