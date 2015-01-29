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

package net.sf.sdedit.ui.components.configuration.configurators;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyDescriptor;

import javax.swing.JTextField;

import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.DataObject;

public class FreeStringConfigurator<C extends DataObject> extends
		StringConfigurator<C> implements FocusListener, KeyListener {

	private JTextField textField;

	public FreeStringConfigurator(Bean<C> bean, PropertyDescriptor property) {
		super(bean, property);

		initialize();
	}

	private void initialize() {
		getBottomPanel().setLayout(new BorderLayout());
		textField = new JTextField(getValue());
		getBottomPanel().add(textField, BorderLayout.CENTER);
		// textField.addFocusListener(this);
		textField.addKeyListener(this);
	}

	@Override
	protected void refresh(String value) {
		textField.setText(value);
	}

	protected void _actionPerformed(ActionEvent e) {
		int c = textField.getCaretPosition();
		applyValue(textField.getText());
		textField.setCaretPosition(c);
	}

	protected void _setEnabled(boolean enabled) {
		if (textField != null) {
			textField.setEnabled(enabled);
		}
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent arg0) {
		actionPerformed(null);
	}

	@Override
	public void focus() {
		textField.selectAll();
		textField.requestFocusInWindow();

	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent e) {
		actionPerformed(null);

	}

}
