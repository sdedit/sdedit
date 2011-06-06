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

import net.sf.sdedit.message.Message;
import net.sf.sdedit.util.Direction;

public class BroadcastArrow extends Arrow {

	public BroadcastArrow(Message message, Direction align, int y) {
		super(message, ArrowStroke.SOLID, align, y);
	}

	protected void drawObject(Graphics2D g2d) {
    	ArrowStroke stroke = getStroke();
    	Point [] pts = getPoints();
    	if (getMessage().getData().getBroadcastType() == 1) {
    		drawText(g2d);
    	}
    	g2d.setColor(Color.BLACK);
        int sgn = getAlign() == Direction.LEFT ? 1 : -1;
        if (stroke != ArrowStroke.NONE) {
            g2d.setStroke(stroke == ArrowStroke.DASHED ? dashed() : solid());
            g2d.drawLine(pts[0].x, pts[0].y, pts[1].x, pts[1].y);
            g2d.setStroke(solid());
            drawArrowHead(g2d, pts[1].x, pts[1].y, sgn);
        }
        if (getMessage().getData().getBroadcastType() == 1) {
        	int w = getMessage().getCaller().getRoot() ==
        		getMessage().getCaller() ?
        			 diagram.mainLifelineWidth:
        				 diagram.subLifelineWidth;
        	int cx = pts [0].x + sgn * w / 2;
        	int cy = pts [0].y;
        	g2d.drawArc(cx-6, cy - 4, 12, 6, 0, 180);
        	g2d.drawArc(cx-8, cy - 9, 16, 6, 0, 180);
        	g2d.drawArc(cx-10, cy - 14, 20, 6, 0, 180);
      	}
    }
}
//{{core}}
