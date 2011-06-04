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

import java.awt.Graphics2D;

import net.sf.sdedit.diagram.IPaintDevice;

public class Text extends Drawable
{
    private String [] text;
    
    private int simpleHeight;
    
    private IPaintDevice device;
    
    public Text (String [] text, IPaintDevice device) {
        this.text = text;
        this.device = device;
        simpleHeight = device.getTextHeight(true);
        setHeight(text.length * simpleHeight + 4);
        int w = 0;
        for (int i = 0; i < text.length; i++) {
            w = Math.max(w, device.getTextWidth(text[i], true));
        }
        setWidth(w);
    }

    @Override
    public void computeLayoutInformation() {
        
    }

    @Override
    protected void drawObject(Graphics2D g2d) {
        g2d.setFont(device.getFont(true));
        drawMultilineString(g2d, text, getLeft(), getBottom() -4 , 
                simpleHeight, getWidth(), null);
    }

}
//{{core}}
