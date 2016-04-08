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

package net.sf.sdedit.util;

import java.awt.Desktop;
import java.net.URI;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * A <tt>HyperlinkListener</tt> implementation that opens a browser for http
 * hyperlinks, using <tt>java.awt.Desktop</tt>.
 * 
 * @author Markus Strauch
 * 
 */
public class Browser implements HyperlinkListener {

	public Browser() {

	}

	/**
	 * Opens the browser in order to display the website belonging to the URL
	 * that was clicked.
	 * 
	 * @param e
	 *            event encapsulation the click of the URL
	 */
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getURL().getProtocol() == null
				|| (!e.getURL().getProtocol().equals("http") && !e.getURL().getProtocol().equals("mailto"))) {
			return;
		}
		try {
			Desktop desktop = Desktop.getDesktop();
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				URI uri = e.getURL().toURI();
				if (e.getURL().getProtocol().equals("http")) {
					desktop.browse(uri);
				} else if (e.getURL().getProtocol().equals("mailto")) {
					desktop.mail(uri);
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
