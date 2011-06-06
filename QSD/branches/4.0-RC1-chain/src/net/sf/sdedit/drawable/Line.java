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


public class Line extends ExtensibleDrawable {
    
    private boolean mainLine;

	public Line(int width, Lifeline lifeline) {
		super(width, lifeline);
	}

	/*
	public void draw(Graphics2D g2d) {
		drawPartially(g2d, -1, -1);
	}
	*/
	
	public void setMainLine (boolean mainLine) {
	    this.mainLine = mainLine;
	}
	
	public boolean isMainLine () {
	    return mainLine;
	}

	protected void drawObject(Graphics2D g2d) {
	    if (mainLine) {
	        int top = getTop();
	        int bottom = getBottom();
	        g2d.setColor(Color.BLACK);
	        g2d.setStroke(Strokes.getStroke(StrokeType.DOTTED, getLifeline().getDiagram().lifelineThickness));
	        g2d.drawLine(getLeft(), top, getLeft(), bottom);
	    }
		
		/*
		if (from == -1) {
			top = getTop();
		} else {
			top = Math.max(from, getTop());
		}
		if (to == -1) {
			bottom = getBottom();
		} else {
			bottom = Math.min(to, getTop() + getHeight());
		}
		*/


	}

	@Override
	public boolean contains(Point point) {
		int x = point.x;
		int y = point.y;
		return x >= getLeft() - 5 && x <= getRight() + 5 && y >= getTop()
				&& y <= getBottom();
	}
}
//{{core}}
