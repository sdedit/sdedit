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

package net.sf.sdedit.ui.components.configuration.configurators;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyDescriptor;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.Configurator;
import net.sf.sdedit.ui.components.configuration.DataObject;

public class FileConfigurator<C extends DataObject> extends Configurator<File,C> implements 
FocusListener {

	private static final long serialVersionUID = -8346750584632429872L;

	private JTextField fileTextField;

	private JButton browseButton;
	
	private JLabel label;
	
	private String description;
	
	private String [] fileTypes;
	
	private static ImageIcon openIcon = Icons.getIcon("open");

	public FileConfigurator(Bean<C> bean, PropertyDescriptor property) {
		super(bean, property);
		setLayout(new BorderLayout());
		description = getAdjustable().info();
		label = new JLabel(description + ":");
		add(label, BorderLayout.WEST);
		browseButton = new JButton (openIcon);
		browseButton.setBorder(BorderFactory.createEmptyBorder(1,4,1,1));
		browseButton.setOpaque(true);
		browseButton.setMargin(new Insets(1, 1, 1, 1));
		add(browseButton, BorderLayout.EAST);
		fileTextField = new JTextField();
		fileTextField.addActionListener(this);
		fileTextField.addFocusListener(this);
		fileTextField.setText("");
		add(fileTextField, BorderLayout.CENTER);
		browseButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				browse();
			}
		});
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		this.fileTypes = getProperty().getWriteMethod().getAnnotation(
				Adjustable.class).filetypes();
	}
	
	public void setFile(java.io.File file) {
		fileTextField.setText(file.getAbsolutePath());
	}

	public File getFile() {
		return new File(fileTextField.getText());
	}

	private void browse() {
		File current = getFile();
		if (current != null && current.getParentFile() != null) {
			fileChooser().setCurrentDirectory(current.getParentFile());
		}
		if (fileTypes.length > 0) {
			addFileTypes(fileTypes);
		}
		int val = fileChooser().showOpenDialog(null);
		if (val == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser().getSelectedFile();
			if (file != null) {
				refresh(file);
				_actionPerformed(null);
			}
		}
		removeFileTypes(fileTypes);
	}

	@Override
	protected void _actionPerformed(ActionEvent evt) {
		String text = fileTextField.getText().trim();
		if (!text.equals("")) {
			getBean().setValue(getProperty(), new File(fileTextField.getText()));
		} else {
			getBean().setValue(getProperty(), null);
		}
	}

	@Override
	protected void refresh(File value) {
		// A work-around that is a strategy against the NullPointerException
		// that occurs when null-values from a copy are restored.
		//
		// As in PrintDialog -> cancel
		fileTextField.setText(value == null ? "" : value.getAbsolutePath());
	}
	
	@Override
	public int getLabelWidth() {
		return label.getPreferredSize().width;
	}

	@Override
	public void setLabelWidth(int width) {
		label.setPreferredSize (new Dimension(width, label.getPreferredSize().height));
	}

	public void focusGained(FocusEvent e) { /* empty */ }

	public void focusLost(FocusEvent e) {
		actionPerformed(null);
	}

	@Override
	protected void _setEnabled(boolean enabled) {/* empty */}

	@Override
	public void focus() {
		fileTextField.requestFocusInWindow();
		
	}
}
