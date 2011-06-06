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

package net.sf.sdedit.diagram;

import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;

import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.drawable.Line;
import net.sf.sdedit.drawable.SequenceElement;

public interface IPaintDevice extends Iterable<Drawable> {
	
	public void setDiagram (Diagram diagram);

	/**
	 * This method is called by the diagram when all objects/lifelines are
	 * known.
	 */
	public void reinitialize();

	public Font getFont(boolean bold);

	public Line getRightBound();

	public void addOtherDrawable(Drawable drawable);

	public void addSequenceElement(SequenceElement elem);

	public boolean isEmpty();

	public void computeAxes(int leftAxis);

	public abstract int getTextWidth(String text, boolean bold);

	public int getTextWidth(String text);

	public int getTextHeight(boolean bold);

	public int getTextHeight();

	public int getWidth();

	public int getHeight();

	public void clear();

	/**
	 * Computes the width and height of this PaintDevice (this is necessary
	 * before a frame and a descriptive text can be set).
	 */
	public void computeBounds();

	/**
	 * This method is called once when no {@linkplain Drawable} object will be
	 * added anymore. Its default implementation is empty.
	 */
	public void close();

	public void announce (int height);

	public Diagram getDiagram();

	public void writeToStream(OutputStream stream) throws IOException;

}
