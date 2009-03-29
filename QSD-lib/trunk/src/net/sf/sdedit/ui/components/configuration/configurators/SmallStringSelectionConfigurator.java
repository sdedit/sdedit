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

package net.sf.sdedit.ui.components.configuration.configurators;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.DataObject;

public class SmallStringSelectionConfigurator<C extends DataObject> extends
		StringConfigurator<C> implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8434413547241922267L;

	private ButtonGroup buttonGroup;

	public SmallStringSelectionConfigurator(Bean<C> bean,
			PropertyDescriptor property) {
		super(bean, property);
		initialize();
	}

	private void initialize() {
		String[] choices = getProperty().getWriteMethod().getAnnotation(
				Adjustable.class).choices();
		getBottomPanel().setLayout(new FlowLayout(FlowLayout.LEADING));
		buttonGroup = new ButtonGroup();
		for (String choice : choices) {
			JRadioButton button = new JRadioButton(choice);
			buttonGroup.add(button);
			getBottomPanel().add(button);
			button.addActionListener(this);
		}
	}

	@Override
	protected void refresh(String string) {
		for (JRadioButton button : getButtons()) {
			if (button.getText().equals(string)) {
				button.setSelected(true);
				return;
			}
		}
	}

	protected void _actionPerformed(ActionEvent e) {
		JRadioButton button = (JRadioButton) e.getSource();
		applyValue(button.getText());
	}

	private List<JRadioButton> getButtons() {
		List<JRadioButton> list = new LinkedList<JRadioButton>();
		Enumeration<AbstractButton> buttons = buttonGroup.getElements();
		while (buttons.hasMoreElements()) {
			JRadioButton button = (JRadioButton) buttons.nextElement();
			list.add(button);
		}
		return list;
	}

	protected void _setEnabled(boolean enabled) {
		for (JRadioButton button : getButtons()) {
			button.setEnabled(enabled);
		}
	}
}
