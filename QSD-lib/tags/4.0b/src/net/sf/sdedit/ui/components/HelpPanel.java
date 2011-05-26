// Copyright (c) 2006 - 2008, Markus Strauch.
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
package net.sf.sdedit.ui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkListener;

import net.sf.sdedit.util.Browser;

/**
 * Utility class that allows to load an html file and display it by
 * a <tt>JEditorPane</tt> that can be embedded into a grapical user interface.
 * 
 * @author Markus Strauch
 *
 */
public class HelpPanel
{
    private JEditorPane pane;
    
    /**
     * Creates a new <tt>Help</tt> object for displaying some html page.
     * 
     * @param page URL of the html page
     */
    public HelpPanel (URL page, HyperlinkListener listener)
    {
    	this();
    	
        if (listener != null) {
        	
            pane.addHyperlinkListener(listener);
            
        }
        try
        {
        	
        	pane.setPage(page);
        } catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }
    
    private HelpPanel () {
        pane = new JEditorPane() {
        	
        	@Override
        	public void paintComponent (Graphics g) {
        		((Graphics2D) g).setRenderingHints(new RenderingHints(
						RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON));
        		super.paintComponent(g);
        	}
        };
        pane.setContentType("text/html; charset=ISO-8859-1");
        pane.setEditable(false);
        pane.addHyperlinkListener(Browser.getBrowser());
    }
    
    public HelpPanel (String text) {
    	this();
    	pane.setText(text);
    }
    

    
    /**
     * Returns the <tt>JEditorPane</tt> on which the help page is displayed and
     * which can be embedded into your graphical user interface.
     * 
     * @return the <tt>JEditorPane</tt> on which the help page is displayed
     */
    public JEditorPane getPane ()
    {
        return pane;
    }
    
    

}
