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

package net.sf.sdedit.ui;

import java.awt.event.MouseEvent;

import net.sf.sdedit.drawable.Drawable;

/**
 * This interface can be implemented by classes whose instances are using a
 * {@linkplain PanelPaintDevice} and that are interested in the question which
 * drawable component is currently visited by the user's mouse.
 * 
 * @author Markus Strauch
 * 
 */
public interface PanelPaintDevicePartner
{
    /**
     * This method is called when the mouse entered a rectangle belonging to
     * some drawable component.
     * 
     * @param drawable
     *            the drawable component that has been entered by the mouse
     * @return true if the listener is interested in the drawable component, the
     *         PanelPaintDevice will then change the shape of the cursor
     */
    public boolean mouseEnteredDrawable(Drawable drawable);

    /**
     * This method is called when the mouse exited a rectangle belonging to some
     * drawable component.
     * 
     * @param drawable
     *            the drawable component that has been entered by the mouse
     */
    public void mouseExitedDrawable(Drawable drawable);

    /**
     * This method is called when the mouse clicked into the rectangle belonging
     * to some drawable component.
     * 
     * @param drawable
     *            the drawable component that has been clicked
     */
    public void mouseClickedDrawable(MouseEvent mouseEvent, Drawable drawable);
    
    public String getTooltip (Drawable drawable);

}
