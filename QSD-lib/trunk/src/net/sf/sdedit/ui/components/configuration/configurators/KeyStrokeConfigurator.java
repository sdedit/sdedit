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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyDescriptor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.Configurator;
import net.sf.sdedit.ui.components.configuration.DataObject;

public class KeyStrokeConfigurator<C extends DataObject> extends
		Configurator<KeyStroke, C> implements KeyListener, FocusListener {

	private JTextField textField;

	private JLabel label;

	private JButton button;

	private Color bg;

	public static final KeyStroke NULL_KEYSTROKE = KeyStroke.getKeyStroke('#');

	public KeyStrokeConfigurator(Bean<C> bean, PropertyDescriptor property) {
		super(bean, property);
		label = new JLabel(property.getWriteMethod().getAnnotation(
				Adjustable.class).info());
		label.setPreferredSize(new Dimension(75, 1));
		label.setHorizontalTextPosition(SwingConstants.RIGHT);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
		setLayout(new BorderLayout());
		add(label, BorderLayout.WEST);
		textField = new JTextField();
		add(textField, BorderLayout.CENTER);
		textField.addKeyListener(this);

		button = new JButton(Icons.getIcon("eraser"));
		button.setOpaque(false);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ActionEvent event = new ActionEvent(this, 1, NULL_KEYSTROKE
						.toString());
				KeyStrokeConfigurator.this.actionPerformed(event);
			}
		});
		button.setMargin(new Insets(1, 1, 1, 1));
		button.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		add(button, BorderLayout.EAST);
		textField.setEditable(false);
		textField.addFocusListener(this);
		bg = textField.getBackground();

	}

	@Override
	protected void _actionPerformed(ActionEvent evt) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke(evt.getActionCommand());
		applyValue(keyStroke);

	}

	@Override
	protected void _setEnabled(boolean enabled) {
		textField.setEnabled(enabled);
		label.setEnabled(enabled);
		button.setEnabled(enabled);
	}

	@Override
	protected void refresh(KeyStroke value) {
		if (value == getNullValue()) {
			textField.setText("");
		} else {
			textField.setText(value.toString());
		}
	}

	public void keyPressed(final KeyEvent e) {
		ActionEvent event = new ActionEvent(this, 1, KeyStroke.getKeyStroke(
				e.getKeyCode(), e.getModifiers(), false).toString());
		actionPerformed(event);
	}

	public synchronized void keyReleased(final KeyEvent e) {
		/* empty */
	}

	public void keyTyped(KeyEvent e) {
		/* empty */
	}

	public void focusGained(FocusEvent e) {
		textField.setBackground(Color.WHITE);
	}

	public void focusLost(FocusEvent e) {
		textField.setBackground(bg);
	}

	@Override
	public void focus() {
		textField.requestFocusInWindow();
		
	}

}
