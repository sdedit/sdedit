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

package net.sf.sdedit.ui.impl;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import net.sf.sdedit.ui.components.HelpPanel;

public class AboutDialog extends JDialog implements ActionListener {
	//private static int width = 450;

	//private static int height = 190;

	public AboutDialog(JFrame frame, URL aboutURL) {
		super(frame);
		getContentPane().setLayout(new BorderLayout());
		String text = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					aboutURL.openStream()));
			for (;;) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				text += line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		long max = Runtime.getRuntime().maxMemory();
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		
		double used = 1D * (total - free) / (1 << 20);
		double avail = 1D * (max - total + free) / (1 << 20);
		
	    DecimalFormat format = new DecimalFormat("####.#");
	    String _used = format.format(used);
	    String _avail = format.format(avail);
		
		text = text.replaceFirst("_USED_", _used + " MB");
		text = text.replaceFirst("_AVAIL_", _avail + " MB");
		
		getContentPane()
				.add(new HelpPanel(text).getPane(), BorderLayout.CENTER);
		JButton close = new JButton("Close");
		close.addActionListener(this);
		getContentPane().add(close, BorderLayout.SOUTH);
		setModal(true);
		setTitle("About sdedit");
		pack();
		setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2
				- getWidth() / 2, Toolkit.getDefaultToolkit().getScreenSize().height
				/ 2 - getHeight() / 2);
	}

	public void actionPerformed(ActionEvent e) {
		setVisible(false);
		dispose();
	}
}
