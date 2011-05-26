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

import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.Configurator;
import net.sf.sdedit.ui.components.configuration.DataObject;
import net.sf.sdedit.util.base64.Base64;

public class FileConfigurator<C extends DataObject> extends Configurator<File,C> implements 
FocusListener {

	private JTextField fileTextField;

	private JButton browseButton;
	
	private JLabel label;
	
	private String description;
	
	private String [] fileTypes;
	
	private static ImageIcon openIcon = new ImageIcon(
			Base64.decodeBase64EncodedImage(
					"iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAABGdBTUE"
					+ "AAK/INwWK6QAAAAZiS0dEAP8A/wD/oL2nkwAAAAlwSFlzAAAASAAAAE"
					+ "gARslrPgAAAsBJREFUKM+ljU1oHFUAgL83M7ub/ckmWU2KuqbVdtvYS"
					+ "H8oBQUpgaKHiicPikgvgohabxYKPffmScGbBfGqeLCh6EGprZSaFlFT"
					+ "Y6KbzW5S26ab/Zt5M+/Nm/e8eRA8+V0/Pj74H4j/Evuf/YzVlQfi7Jl"
					+ "nnqpU8q/p1HxxYF/t1jffbfDpxycBCP4dvXv2CuVykcFQTr31+uGX9z"
					+ "05+d7ux8efvnHr3vyXl5pvzEyXdv45L5xvfXT8YJUkdYSJ9a7c6C6/u"
					+ "je8Pt+Yev9Ao/bSbL1SwsFacxB//lXz3AcXVj48dXraLl4cJ5h7Yuyd"
					+ "kyeqyBT6vZS5momOzkwMjs5P7SoVC75SFq0zZh+rFI8cmjnz/Cvxb5c"
					+ "+OfF1o/MtwVheMFn10VuSR3TMwvFKebZeLntCoBT4nkCljptrkgeJv1"
					+ "eUi29PPHc1H0vPBJl1WOuIthNmqwG58hib3ZTMgNaQJBmXlwasdgXVy"
					+ "Ul2H6ucKtbVoR+/b4+CzIJ18NNdWGkl7NnxiY0lUQ6ZWAajjGFawJ+e"
					+ "4PeBRzxUuZ+v3Xu034kXA2sd/VFGv6d584Ua+ZJH5Pus33U07zgi7di"
					+ "JoP2L5H6rx05zOBje70sXpFcDa2Hlz4S5+hiVmRy3O4Z+DGttQXPDsN"
					+ "5Jabci+n8Nibsh9FREYDYJzJIXK0urLZlvFNkYCbYin/UtSxRZVGIZD"
					+ "TVhmOIE5Au+8/OugDAtrF0OVlsJC0eq7HkoQARQ2yXo5SxhaOiOa449"
					+ "nBIeBC0FSeiJ23+I4rUls1wq5bpBZ1Ny+MUajZoPgKsIVM1DRiDrIKW"
					+ "PjDKkDJBxgV4YJpmzP+RyHgHKcGdbs3i9j8nAWUeSZESRQkqNlAoZKZ"
					+ "JYI+OUm7/Ktt00S10BwUhmpy9e3sYXYI0jyywmzUh1itGGVKek2mB0h"
					+ "lJGqJ5rU/JGLrb8DdKwhwGMgWXLAAAAInpUWHRTb2Z0d2FyZQAAeNpz"
					+ "TMlPSlXwzE1MTw1KTUypBAAvnAXUrgypTQAAAABJRU5ErkJggg=="));

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
		String text = fileTextField.getText();
		if (!text.equals("")) {
			getBean().setValue(getProperty(), new File(fileTextField.getText()));
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
