// Copyright (c) 2006 - 2016, Markus Strauch.
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

package net.sf.sdedit.config;

import java.awt.Font;

import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.DataObject;

public interface Configuration extends DataObject {

	public int getArrowSize();

	public Font getFont();

	public int getInitialSpace();

	public int getLeftMargin();

	public boolean isLineWrap();

	public int getLowerMargin();

	public int getRightMargin();

	public int getUpperMargin();

	public boolean isVerticallySplit();

	public int getArrowThickness();

	@Adjustable(dflt = 6, min = 2, max = 100, info = "Arrowhead size", category = "Misc")
	public void setArrowSize(int arrowSize);

	@Adjustable(category = "Misc", info = "Diagram font")
	public void setFont(Font font);

	@Adjustable(dflt = 10, min = 8, max = 100, editable = true, info = "Space below lifeline head", category = "Vertical spaces")
	public void setInitialSpace(int initialSpace);

	@Adjustable(dflt = 5, min = 1, max = 999, info = "Left margin", category = "Margins")
	public void setLeftMargin(int leftMargin);

	@Adjustable(info = "Wrap lines", category = "Misc")
	public void setLineWrap(boolean lineWrap);

	@Adjustable(dflt = 5, min = 1, max = 100, info = "Bottom margin", category = "Margins")
	public void setLowerMargin(int lowerMargin);

	@Adjustable(dflt = 5, min = 5, max = 999, editable = true, info = "Right margin", category = "Margins")
	public void setRightMargin(int rightMargin);

	@Adjustable(dflt = 5, min = 1, max = 100, info = "Top margin", category = "Margins")
	public void setUpperMargin(int upperMargin);

	@Adjustable(editable = false, info = "Vertical split", category = "Misc")
	public void setVerticallySplit(boolean verticallySplit);

	@Adjustable(editable = true, info = "Arrow thickness", category = "Line thickness", min = 1)
	public void setArrowThickness(int arrowThickness);

}
// {{core}}
