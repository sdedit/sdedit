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
package net.sf.sdedit.ui.impl;

import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Action;

import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.ui.PanelPaintDevicePartner;

public abstract class DiagramInteraction implements PanelPaintDevicePartner {

	private Drawable drawable;

	public void mouseClickedDrawable(MouseEvent e, Drawable drawable) {
		_mouseClickedDrawable(e, drawable);

	}

	protected abstract void _mouseClickedDrawable(MouseEvent e,
			Drawable drawable);

	public boolean mouseEnteredDrawable(Drawable drawable) {
		this.drawable = drawable;
		return _mouseEnteredDrawable(drawable);
	}

	protected abstract boolean _mouseEnteredDrawable(Drawable drawable);

	public void mouseExitedDrawable(Drawable drawable) {
		drawable = null;
		_mouseExitedDrawable(drawable);

	}

	protected abstract boolean _mouseExitedDrawable(Drawable drawable);

	public List<Action> getContextActions() {
		if (drawable == null) {
			return null;
		}
		return _getContextActions(drawable);
	}

	protected abstract List<Action> _getContextActions(Drawable drawable);

}
