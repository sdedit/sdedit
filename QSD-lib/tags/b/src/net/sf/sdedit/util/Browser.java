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

package net.sf.sdedit.util;

import java.lang.reflect.Method;
import java.net.URI;

import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * A <tt>HyperlinkListener</tt> implementation that opens a browser for http
 * hyperlinks, using <tt>java.awt.Desktop</tt>, and thus only working when
 * JDK version &gt;= is available at runtime.
 * 
 * @author Markus Strauch
 * 
 */
public class Browser implements HyperlinkListener {

	private static Browser instance;

	private static final Object desktop;

	private static final Method browseMethod;

	private static final Method mailtoMethod;

	private static String jdklt6 = "Browsing is only supported by\nJDK 6 or higher";

	private static String notSupported = "Operation not supported by\nyour environment";

	private static final boolean jdk6;

	private static final boolean supported;

	private Browser() {

	}

	/**
	 * Returns the singleton {@linkplain Browser} instance.
	 * 
	 * @return the singleton {@linkplain Browser} instance
	 */
	public static Browser getBrowser() {
		if (instance == null) {
			instance = new Browser();
		}
		return instance;
	}

	static {
		Object _desktop = null;
		Method _browseMethod = null;
		Method _mailtoMethod = null;
		boolean _jdk6 = false;
		boolean _supported = false;
		try {
			Class<?> desktopClass = Class.forName("java.awt.Desktop");
			_jdk6 = true;
			Method supportedMethod = desktopClass
					.getMethod("isDesktopSupported");
			_supported = (Boolean) supportedMethod.invoke(null);
			if (_supported) {
				Method getDesktopMethod = desktopClass.getMethod("getDesktop");
				_desktop = getDesktopMethod.invoke(null);
				_browseMethod = desktopClass.getMethod("browse", URI.class);
				_mailtoMethod = desktopClass.getMethod("mail", URI.class);
			}
		} catch (Exception e) {
			/* empty */
		}
		jdk6 = _jdk6;
		supported = _supported;
		desktop = _desktop;
		browseMethod = _browseMethod;
		mailtoMethod = _mailtoMethod;
	}

	/**
	 * Opens the browser in order to display the website belonging to the URL
	 * that was clicked.
	 * 
	 * @param e
	 *            event encapsulation the click of the URL
	 */
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getURL().getProtocol() == null ||
				(!e.getURL().getProtocol().equals("http") &&
						!e.getURL().getProtocol().equals("mailto"))) {
			return;
		}
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			if (!jdk6) {
				JOptionPane.showMessageDialog(null, jdklt6);
				return;
			}
			if (desktop == null || !supported) {
				JOptionPane.showMessageDialog(null, notSupported);
				return;
			}
			if (e.getURL().getProtocol().equals("http")) {
				if (browseMethod != null) {
					try {
						URI uri = e.getURL().toURI();
						browseMethod.invoke(desktop, uri);
					} catch (Exception ex) {
						/* empty */
					}
				}
			} else if (e.getURL().getProtocol().equals("mailto")) {
				if (mailtoMethod != null) {
					try {
						URI uri = e.getURL().toURI();
						mailtoMethod.invoke(desktop, uri);
					} catch (Exception ex) {
						/* empty */
					}
				}
			}
		}
	}
}
