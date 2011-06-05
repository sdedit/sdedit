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
import java.awt.Polygon;

import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.drawable.Strokes.StrokeType;

/**
 * A <tt>Figure</tt> is a drawable representation of an {@linkplain Actor}.
 * 
 * @author Markus Strauch
 * 
 */
public class Figure extends Drawable {
    private String label;

    private int textHeight;

    private int textWidth;

    private boolean underline;

    private int actorHeight;

    private int actorWidth;

    private boolean shouldShadow;

    /**
     * Creates a new <tt>Figure</tt>.
     * 
     * @param actor
     *            the actor that the <tt>Figure</tt> represents
     * @param _label
     *            the label of the figure (appearing below it)
     * @param y
     *            the vertical position where the figure drawing starts
     * @param underline
     *            flag denoting if the figure label is to be underline
     */
    public Figure(Lifeline actor, String _label, int y, boolean underline) {
        setTop(y);
        if (!_label.equals("")) {
            this.label = _label;
        } else {
            label = actor.getName();
        }
        this.underline = underline;
        textHeight = actor.getDiagram().getPaintDevice().getTextHeight();
        textWidth = actor.getDiagram().getPaintDevice().getTextWidth(label);
        int width = Math.max(actor.getDiagram().getConfiguration()
                .getActorWidth(), textWidth);
        setWidth(width);
        actorHeight = actor.getDiagram().getConfiguration().getHeadHeight();
        setHeight(textHeight + actorHeight + 3);
        actorWidth = actor.getDiagram().getConfiguration().getActorWidth();
        shouldShadow = actor.getDiagram().getConfiguration()
                .isShouldShadowParticipants();
    }

    /**
     * @see net.sf.sdedit.drawable.Drawable#drawObject(java.awt.Graphics2D)
     */
    protected void drawObject(Graphics2D g2d) {
        renderActor(g2d, getTop(), getTop() + actorHeight - 2, getLeft()
                + getWidth() / 2, actorWidth);

        g2d.drawString(label, getLeft() + getWidth() / 2 - textWidth / 2,
                getTop() + getHeight() - 3);
        if (underline) {
            g2d.drawLine(getLeft() + getWidth() / 2 - textWidth / 2, getTop()
                    + getHeight() - 2, getLeft() + getWidth() / 2 + textWidth
                    / 2, getTop() + getHeight() - 2);
        }
    }

    private void renderActor(Graphics2D g, int from, int to, int axis, int width) {
        int ofs = 4;
        int thickness = 4;
        int left = axis - width / 2;
        int right = axis + width / 2;
        int height = to - from;
        int arms = (int) (height * 0.4F);
        int headDiameter = (int) (height * 0.3F);
        int legs = (int) (height * 0.6F);

        if (shouldShadow) {

            // the head shadow
            g.fillOval(axis - headDiameter / 2 + ofs, from + ofs, headDiameter,
                    headDiameter);

            g.setStroke(Strokes.getStroke(StrokeType.SOLID, thickness));

            // the body shadow
            g.drawLine(axis + ofs, from + headDiameter, axis + ofs, from + legs);

            // the arms shadow
            g.drawLine(left + ofs, from + arms + ofs, right + ofs, from + arms
                    + ofs);

            // the left leg shadow
            g.drawLine(axis + ofs, from + legs + ofs, left + ofs, to + ofs);

            // the right leg shadow
            g.drawLine(axis + (int) (1.5 * ofs), from + legs + ofs, right
                    + (int) (1.5 * ofs) - 1, to + 1);

            g.setStroke(Strokes.defaultStroke());

        }

        g.setColor(Color.WHITE);
        g.fillOval(axis - headDiameter / 2, from, headDiameter, headDiameter);
        g.setColor(Color.BLACK);
        g.drawOval(axis - headDiameter / 2, from, headDiameter, headDiameter);

        Polygon poly = new Polygon();
        poly.addPoint(axis - thickness / 2, from + headDiameter);
        poly.addPoint(axis - thickness / 2, from + arms);
        poly.addPoint(left, from + arms);
        poly.addPoint(left, from + arms + thickness);
        poly.addPoint(axis - thickness / 2, from + arms + thickness);
        poly.addPoint(axis - thickness / 2, from + legs);
        poly.addPoint(left, to);
        poly.addPoint(left + thickness, to + thickness);
        poly.addPoint(axis, from + legs + thickness);
        poly.addPoint(right, to + thickness);
        poly.addPoint(right + thickness, to);
        poly.addPoint(axis + thickness / 2, from + legs);
        poly.addPoint(axis + thickness / 2, from + arms + thickness);
        poly.addPoint(right + thickness, from + arms + thickness);
        poly.addPoint(right + thickness, from + arms);
        poly.addPoint(axis + thickness / 2, from + arms);
        poly.addPoint(axis + thickness / 2, from + headDiameter);

        g.setColor(Color.WHITE);
        g.fillPolygon(poly);
        g.setColor(Color.BLACK);
        g.drawPolygon(poly);
    }

    /**
     * @see net.sf.sdedit.drawable.Drawable#computeLayoutInformation()
     */
    public void computeLayoutInformation() {
        /* emtpy */
    }
}
// {{core}}
