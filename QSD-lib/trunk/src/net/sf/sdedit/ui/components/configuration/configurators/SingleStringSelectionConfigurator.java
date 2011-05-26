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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyDescriptor;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ToolTipManager;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import net.sf.sdedit.ui.components.AutoCompletionComboboxEditor;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.DataObject;

public class SingleStringSelectionConfigurator<C extends DataObject> extends
		StringConfigurator<C> implements ActionListener, ListCellRenderer, StringSelectionReceiver {

	protected JComboBox comboBox;

	private BasicComboBoxRenderer renderer;

	public SingleStringSelectionConfigurator(Bean<C> bean, PropertyDescriptor property) {
		super(bean, property);
		renderer = new BasicComboBoxRenderer();
		initialize();
		
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		JComponent comp = (JComponent) renderer.getListCellRendererComponent(
				list, value, index, isSelected, cellHasFocus);

		if (isSelected) {
			if (-1 < index) {
				list.setToolTipText((String) comboBox.getItemAt(index));
			}
		}
		return comp;
	}

	private void initialize() {
		comboBox = new JComboBox();
		
		comboBox.setEditable(true);
		AutoCompletionComboboxEditor editor = new AutoCompletionComboboxEditor(comboBox);
		
		
		comboBox.setEditor(editor);
		comboBox.setRenderer(this);

		ToolTipManager.sharedInstance().registerComponent(comboBox);

		comboBox.addActionListener(this);
		getBottomPanel().setLayout(new BorderLayout());
		getBottomPanel().add(comboBox, BorderLayout.CENTER);
		reinitialize(true);
	}
	
	public void reinitialize () {
		reinitialize(false);
	}

	private void reinitialize(boolean init) {
		String oldValue = null;
		if (!getValue().equals(getNullValue())) {
			oldValue = getValue();
		}
		comboBox.removeAllItems();
		Set<String> choices = getBean().getStringsForProperty(getProperty());
		if (choices.isEmpty() && init && oldValue != null) {
			// store the old value, waiting for string selections to
			// be provided soon after...
			choices.add(oldValue);
		}
		for (String choice : choices) {
			comboBox.addItem(choice);
		}
		if (oldValue != null) {
			comboBox.setSelectedItem(oldValue);
		}
		if (choices.isEmpty()) {
			applyValue(getNullValue());
		}
	}

	@Override
	protected void refresh(String value) {
		comboBox.setSelectedItem(value);
	}

	protected void _actionPerformed(ActionEvent e) {
		String value = (String) comboBox.getSelectedItem();
		applyValue(value);
	}

	protected void _setEnabled(boolean enabled) {
		comboBox.setEnabled(enabled);
	}

	@Override
	public void focus() {
		comboBox.requestFocus();
		
	}
}
