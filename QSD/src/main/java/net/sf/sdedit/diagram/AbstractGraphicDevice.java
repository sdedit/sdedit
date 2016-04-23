//Copyright (c) 2006 - 2016, Markus Strauch.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.
package net.sf.sdedit.diagram;

import java.awt.Font;
import java.awt.font.FontRenderContext;

import net.sf.sdedit.drawable.Drawable;

public abstract class AbstractGraphicDevice implements GraphicDevice {
    
    private Iterable<Drawable> drawables;
    
    private Font plainFont;
    
    private Font boldFont;

    private int width;

    private int height;
    
	private FontRenderContext fontRenderContext;
    
    protected Iterable<Drawable> drawables () {
        return drawables;
    }
    
    protected void setFontRenderContext(boolean antialiasing) {
    	fontRenderContext = new FontRenderContext(null, antialiasing, true);	
    }

    public void initialize(Diagram diagram) {
        this.drawables = diagram.getPaintDevice();    
        plainFont = diagram.getConfiguration().getFont();
        boldFont = new Font(plainFont.getName(), Font.BOLD,
                plainFont.getSize() + 1);
        setFontRenderContext(false);
	}
    
    @Override
	public int getTextHeight(boolean bold) {
		return Math.round(getFont(bold).getLineMetrics("A", fontRenderContext).getHeight());
	}

    @Override
	public int getTextWidth(String text, boolean bold) {
		return (int) Math.ceil(getFont(bold).getStringBounds(text, fontRenderContext).getWidth());
	}

    public Font getFont(boolean bold) {
        return bold ? boldFont : plainFont;
    }
    
    public void close(int width, int height, boolean empty) {
        this.width = width;
        this.height = height;
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
