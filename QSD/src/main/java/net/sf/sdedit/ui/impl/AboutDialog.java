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

package net.sf.sdedit.ui.impl;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;

import net.sf.sdedit.ui.components.HelpPanel;
import net.sf.sdedit.util.UIUtilities;
import net.sf.sdedit.util.Utilities;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

	private String text;

	private JEditorPane editorPane;
	

	public AboutDialog(JFrame frame, URL aboutURL, String title) {
		super(frame);
		getContentPane().setLayout(new BorderLayout());
		try {
			text = Utilities.toString(aboutURL, Charset.forName("utf-8"));
		} catch (IOException e) {
			throw new IllegalArgumentException("cannot read about text from " + aboutURL);
		}
		text = Utilities.unixEncode(text);
		showPanel();
		setModal(true);
		setTitle(title);
		pack();
		UIUtilities.centerWindow(this);
	}

	private void showPanel() {
		String text = this.text;
		long max = Runtime.getRuntime().maxMemory();
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();

		double used = 1D * (total - free) / (1 << 20);
		double avail = 1D * (max - total + free) / (1 << 20);

		DecimalFormat format = new DecimalFormat("####.#");
		String _used = format.format(used);
		String _avail = format.format(avail);
		String _version = System.getProperty("java.version") + " (" + System.getProperty("os.name") + ")";

		text = text.replaceFirst("_USED_", _used + " MB");
		text = text.replaceFirst("_AVAIL_", _avail + " MB");
		text = text.replaceFirst("_JAVA_", _version);

		if (editorPane != null) {
			getContentPane().remove(editorPane);
		}
		editorPane = new HelpPanel(text).getPane();
		editorPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					AboutDialog.this.setVisible(false);
					AboutDialog.this.dispose();
				}
			}
		});
		getContentPane().add(editorPane, BorderLayout.CENTER);
		editorPane.revalidate();
	}
}
