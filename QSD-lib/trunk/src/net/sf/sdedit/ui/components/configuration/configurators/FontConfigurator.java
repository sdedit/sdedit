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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyDescriptor;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.Configurator;
import net.sf.sdedit.ui.components.configuration.DataObject;

public class FontConfigurator<C extends DataObject> extends Configurator<Font, C>
		implements ActionListener, ChangeListener {

	private static final int strut = 5;

	private JComboBox nameComboBox;

	private JSpinner sizeSpinner;

	private JCheckBox italic;

	private JCheckBox bold;

	private JLabel sampleLabel;
	
	private JLabel label;

	public FontConfigurator(Bean<C> bean, PropertyDescriptor property) {
		super(bean, property);
		initialize();
	}

	private void initialize() {
		setLayout(new GridLayout(2, 1));
		JPanel top = new JPanel();
		add(top);
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		label = new JLabel(getProperty().getWriteMethod().getAnnotation(
				Adjustable.class).info()
				+ ":");
		label.setHorizontalAlignment(SwingConstants.TRAILING);
		label.setPreferredSize(new Dimension(90,
				label.getPreferredSize().height));
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		top.add(label);
		top.add(Box.createRigidArea(new Dimension(strut, 1)));

		nameComboBox = new JComboBox(getAllFontFamilyNames());

		top.add(nameComboBox);

		SpinnerNumberModel model = new SpinnerNumberModel(12, 1, 128, 1);
		sizeSpinner = new JSpinner(model);
		top.add(Box.createRigidArea(new Dimension(strut, 1)));
		top.add(sizeSpinner);
		top.add(Box.createRigidArea(new Dimension(strut, 1)));

		italic = new JCheckBox("italic");
		bold = new JCheckBox("bold");

		top.add(italic);
		top.add(bold);

		sampleLabel = new JLabel();
		add(sampleLabel);

		sizeSpinner.addChangeListener(this);
		nameComboBox.addActionListener(this);
		italic.addActionListener(this);
		bold.addActionListener(this);
	}

	private static String[] getAllFontFamilyNames() {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		return ge.getAvailableFontFamilyNames();
	}

	@Override
	protected void refresh(Font value) {
		nameComboBox.setSelectedItem(value.getFamily());
		sizeSpinner.setValue(value.getSize());
		italic.setSelected(false);
		bold.setSelected(false);
		switch (value.getStyle()) {
		case 1:
			bold.setSelected(true);
			break;
		case 2:
			italic.setSelected(true);
			break;
		case 3:
			italic.setSelected(true);
			bold.setSelected(true);
		}
		sampleLabel.setFont(getSelectedFont());
		sampleLabel.setText("The quick brown fox jumps over the lazy dog.");
	}

	@Override
	protected Font getNullValue() {
		return new Font("Dialog", Font.PLAIN, 12);
	}

	private Font getSelectedFont() {
		String name = (String) nameComboBox.getSelectedItem();
		int size = (Integer) sizeSpinner.getValue();
		int style = (bold.isSelected() ? 1 : 0) + (italic.isSelected() ? 2 : 0);
		Font font = new Font(name, style, size);
		return font;
	}

	public void _actionPerformed(ActionEvent e) {
		Font font = getSelectedFont();
		applyValue(font);
		sampleLabel.setFont(font);
		sampleLabel.setText("The quick brown fox jumps over the lazy dog.");
	}

	public void stateChanged(ChangeEvent e) {
		actionPerformed(null);
	}

	protected void _setEnabled(boolean enabled) {
		nameComboBox.setEnabled(enabled);
		sizeSpinner.setEnabled(enabled);
		italic.setEnabled(enabled);
		bold.setEnabled(enabled);
		label.setEnabled(enabled);
		sampleLabel.setEnabled(enabled);
	}
	
	public int getLabelWidth() {
		return label.getPreferredSize().width;
	}

	@Override
	public void setLabelWidth(int width) {
		label.setPreferredSize (new Dimension(width, label.getPreferredSize().height));
	}
}
