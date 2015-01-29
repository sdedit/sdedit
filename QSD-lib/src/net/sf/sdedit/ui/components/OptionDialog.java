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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.sdedit.util.Ref;
import net.sf.sdedit.util.UIUtilities;

/**
 * An option dialog is a modal dialog that asks the user to choose an option by
 * clicking a button.
 * 
 * @author Markus Strauch
 * 
 */
public class OptionDialog extends JDialog {

	private String chosenOption;

	private ButtonPanel buttonPanel;

	private JFrame owner;

	private boolean empty;
	
	private JCheckBox decisionCheckBox;

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the frame of the application
	 * @param title
	 *            the title of the option dialog
	 * @param icon
	 *            the icon to appear on the left
	 * @param text
	 *            the text of the message/question to the user
	 */
	public OptionDialog(JFrame owner, String title, ImageIcon icon, String text) {
		this (owner, title, icon, text, null);
	}


	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the frame of the application
	 * @param title
	 *            the title of the option dialog
	 * @param icon
	 *            the icon to appear on the left
	 * @param text
	 *            the text of the message/question to the user
	 */
	public OptionDialog(JFrame owner, String title, ImageIcon icon, String text, String decision) {
		super(owner);
		this.owner = owner;
		setModal(true);
		setTitle(title);
		init(icon, text, decision);
		empty = false;
	}
	
	

	private void init(ImageIcon icon, String text, String decision) {
		getContentPane().setLayout(new BorderLayout());
		buttonPanel = new ButtonPanel();
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(decision == null ? 1 : 2, 1));
		if (decision != null) {
			decisionCheckBox = new JCheckBox(decision);
			bottomPanel.add(decisionCheckBox);
		}
		bottomPanel.add(buttonPanel);
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		getContentPane().add(center, BorderLayout.CENTER);
		JLabel label = new JLabel();
		label.setBorder(BorderFactory.createEmptyBorder(2, 2, 10, 20));
		label.setText(text);
		center.add(label, BorderLayout.CENTER);
		if (icon != null) {
			JLabel iconLabel = new JLabel(icon);
			iconLabel
					.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
			center.add(iconLabel, BorderLayout.WEST);
		}
	}

	/**
	 * Adds a button corresponding to a possible choice of the user. Buttons are
	 * arranged from the right to the left. If the option text starts with
	 * colons (&quot;:&quot;), there will be a larger gap between the current
	 * button and the button that will come next, depending on the number of
	 * gaps. If an option text ends with &quot;#&quot;, the corresponding option
	 * is the default option.
	 * 
	 * @param optionText
	 *            the text of the option
	 */
	public void addOption(final String optionText) {
		boolean isDefault = false;
		String option = optionText;
		if (option.endsWith("#")) {
			option = option.substring(0, option.length() - 1);
			isDefault = true;
		}
		int i;
		for (i = -1; i < option.length() - 1; i++) {
			if (option.charAt(i + 1) != ':') {
				break;
			}
		}
		final String theOption = option.substring(i + 1);
		Action action = new AbstractAction() {
			{
				putValue(Action.NAME, theOption);
			}

			public void actionPerformed(ActionEvent e) {
				chosenOption = theOption;
				setVisible(false);
			}
		};
		buttonPanel.addAction(action, i * 10, isDefault);
	}
	
	public String getOption () {
		return getOption(null);
	}

	public String getOption(Ref<Boolean> decision) {
		if (empty) {
			throw new IllegalStateException(
					"Refusing to show option dialog because no option has been added");
		}
		pack();
		UIUtilities.centerWindow(this, owner);
		setVisible(true);
		dispose();
		if (decision != null && decisionCheckBox != null) {
			decision.t = decisionCheckBox.isSelected();
		}
		return chosenOption;
	}
}
