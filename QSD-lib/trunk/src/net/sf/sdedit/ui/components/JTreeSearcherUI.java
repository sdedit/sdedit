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
package net.sf.sdedit.ui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;

import net.sf.sdedit.util.UIUtilities;

public class JTreeSearcherUI extends JPanel implements ActionListener {

	private JTreeSearcher searcher;

	private JButton button;
	
	private JTree tree;

	private JTextField textField;

	public JTreeSearcherUI(JTreeSearcher searcher, JTree tree, String caption) {
		this.searcher = searcher;
		button = new JButton(caption);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(button, BorderLayout.CENTER);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(2,0,2,2));
		textField = new JTextField();
		setLayout(new BorderLayout());
		JPanel textPanel = UIUtilities.borderedPanel(textField,2,2,2,0,true);
		add(textPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.WEST);
		textField.addActionListener(this);
		button.addActionListener(this);
		this.tree = tree;
		searcher.register(this);
	}
	
	public void DESTROY () {
		searcher.deregister(this);
		tree = null;
		searcher = null;
		textField.removeActionListener(this);
		button.removeActionListener(this);
		textField = null;
		button = null;
		
	}

	public JTreeSearcherUI(JTreeSearcher searcher, String caption) {
		this(searcher, null, caption);
	}
	
	public JTextField getTextField () {
		return textField;
	}
	
	public JTree getTree () {
		return tree;
	}


	public void actionPerformed(ActionEvent e) {
		e.setSource(this);
		searcher.actionPerformed(e);
	}

	public void setSearchText(String text) {
		textField.setText(text);
	}

	public String getSearchText() {
		return textField.getText();
	}

}
