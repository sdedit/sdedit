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
import java.awt.Point;

import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.drawable.Strokes.StrokeType;


public class Cross extends Drawable
{

    private Point[] pts;

    public Cross(Lifeline lifeline) {
        super();
        setWidth(lifeline.getDiagram().getConfiguration().getDestructorWidth());
        setHeight(getWidth());
    }

    /**
     * Returns an array of four points, the first two points are the end points
     * of the first line of the cross, the last two points those of the second
     * line.
     * 
     * @return an array of four points, the first two points are the end points
     *         of the first line of the cross, the last two points those of the
     *         second line.
     */
    public Point[] getPoints() {
        return pts;
    }

    protected void drawObject(Graphics2D g2d) {
        g2d.setStroke(Strokes.getStroke(StrokeType.SOLID, 2));
        g2d.setColor(Color.BLACK);
        g2d.drawLine(pts[0].x, pts[0].y, pts [1].x, pts [1].y);
        g2d.drawLine(pts[2].x, pts[2].y, pts [3].x, pts [3].y);
    }

    public void computeLayoutInformation() {
        int left = getLeft();
        int top = getTop();
        int width = getWidth();
        
        pts = new Point [4];
        pts [0] = new Point (left, top - width / 2 + 3);
        pts [1] = new Point (left + width, top + width / 2 + 3);
        
        pts [2] = new Point (left, top + width / 2 + 3);
        pts [3] = new Point (left + width, top - width / 2 + 3);
    }
}
//{{core}}
