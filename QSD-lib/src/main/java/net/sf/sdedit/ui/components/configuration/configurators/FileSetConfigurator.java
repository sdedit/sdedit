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

import static net.sf.sdedit.util.Utilities.castArray;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.Configurator;
import net.sf.sdedit.ui.components.configuration.DataObject;
import net.sf.sdedit.util.ListModelAdapter;
import net.sf.sdedit.util.UIUtilities;

public class FileSetConfigurator<C extends DataObject> extends
		Configurator<File[], C> implements ListSelectionListener,
		ListDataListener {

	private static final long serialVersionUID = -7878808664490939790L;

	private JList list;

	private JButton addButton;

	private JButton removeButton;

	private ListModelAdapter lma;

	private JLabel descriptionLabel;

	protected String[] fileTypes;

	public FileSetConfigurator(Bean<C> bean, PropertyDescriptor property) {
		super(bean, property);
		setLayout(new BorderLayout());
		lma = new ListModelAdapter();
		list = new JList(lma);
		lma.addListDataListener(this);
		list.addListSelectionListener(this);
		JScrollPane scrollPane = new JScrollPane(list);
		add(scrollPane, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		removeButton = new JButton(Icons.getIcon("minus"));
		UIUtilities.changeIconButton(removeButton);
		addButton = new JButton(Icons.getIcon("plus"));
		UIUtilities.changeIconButton(addButton);

		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);

		descriptionLabel = new JLabel(getAdjustable().info());
		topPanel.add(descriptionLabel, BorderLayout.CENTER);
		topPanel.add(buttonPanel, BorderLayout.EAST);

		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addFiles();
			}
		});
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeFiles();
			}
		});

		removeButton.addActionListener(this);
		removeButton.setEnabled(false);
		setPreferredSize(new Dimension(300, 125));
		this.fileTypes = getProperty().getWriteMethod().getAnnotation(
				Adjustable.class).filetypes();
	}

	@Override
	protected void _actionPerformed(ActionEvent evt) {
		getBean().setValue(getProperty(), castArray(lma.getData(), File.class));
	}

	protected void addFiles() {
		if (fileTypes.length > 0) {
			addFileTypes(fileTypes);
		}
		File dir = null;
		for (File file : getValue()) {
			if (dir == null) {
				dir = file.getParentFile();
			} else {
				if (!file.getParentFile().equals(dir)) {
					dir = null;
					break;
				}
			}
		}
		if (dir != null) {
			fileChooser().setCurrentDirectory(dir);
		}
		int val = fileChooser().showOpenDialog(null);


		if (val == JFileChooser.APPROVE_OPTION) {
			List<File> files = new LinkedList<File>(Arrays.asList(castArray(lma
					.getData(), File.class)));

			for (File selected : fileChooser().getSelectedFiles()) {
				if (!files.contains(selected)) {
					files.add(selected);
				}
			}

			lma.setData(files);
		}
		removeFileTypes(fileTypes);
	}

	protected void removeFiles() {
		// indices are returned in ascending order
		int[] indices = list.getSelectedIndices();
		File[] old = castArray(lma.getData(), File.class);
		List<File> newFiles = new LinkedList<File>();
		int j = 0;
		for (int i = 0; i < old.length; i++) {
			if (j < indices.length && indices[j] == i) {
				j++;
			} else {
				newFiles.add(old[i]);
			}
		}
		lma.setData(newFiles);
	}

	@Override
	protected void _setEnabled(boolean enabled) {
		descriptionLabel.setEnabled(enabled);
		list.setEnabled(enabled);
		addButton.setEnabled(enabled);
		removeButton.setEnabled(enabled && getValue().length > 0
				&& list.getSelectedIndices().length > 0);
	}

	private boolean refresh;

	@Override
	protected void refresh(File[] value) {
		refresh = true;
		lma.setData(Arrays.asList(value));
		refresh = false;
	}

	public void valueChanged(ListSelectionEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int l = getValue().length;
				removeButton.setEnabled(l > 0
						&& list.getSelectedIndices().length > 0);
			}
		});
	}

	public void contentsChanged(ListDataEvent e) {
		if (!refresh) {
			actionPerformed(null);
		}
	}

	public void intervalAdded(ListDataEvent e) {
		if (!refresh) {
			actionPerformed(null);
		}
	}

	public void intervalRemoved(ListDataEvent e) {
		if (!refresh) {
			actionPerformed(null);
		}
	}

	@Override
	public void focus() {
		list.requestFocusInWindow();
	}

}