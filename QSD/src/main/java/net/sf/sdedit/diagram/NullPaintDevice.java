//Copyright (c) 2006 - 2015, Markus Strauch.
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
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.drawable.Line;

public class NullPaintDevice implements PaintDevice {

    private Diagram diagram;

    public void addExtraordinary(Drawable drawable) {
    }

    public void append(Drawable drawable) {
    }

    public void announce(int height) {
    }

    public void clear() {
    }

    public void close() {
    }

    public void computeBounds() {
    }

    public Diagram getDiagram() {
        return diagram;
    }

    public Font getFont(boolean bold) {
        return Font.decode("Dialog");
    }

    public int getHeight() {
        return 0;
    }

    public Line getRightBound() {
        return null;
    }

    public int getTextHeight(boolean bold) {
        return 0;
    }

    public int getTextHeight() {
        return 0;
    }

    public int getTextWidth(String text, boolean bold) {
        return 0;
    }

    public int getTextWidth(String text) {
        return 0;
    }

    public int getWidth() {
        return 0;
    }

    public boolean isEmpty() {
        return true;
    }

    public void reinitialize() {
    }

    public void setDiagram(Diagram diagram) {
        this.diagram = diagram;
    }

    public Iterator<Drawable> iterator() {
        return new LinkedList<Drawable>().iterator();
    }

    public Object callSpecial(String method, Object argument) {
        return null;
    }

}
