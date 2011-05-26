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
import java.awt.event.ActionEvent;
import java.beans.PropertyDescriptor;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.Configurator;
import net.sf.sdedit.ui.components.configuration.DataObject;
import net.sf.sdedit.util.ListModelAdapter;
import net.sf.sdedit.util.Utilities;

public class StringSetConfigurator<C extends DataObject> extends
		Configurator<String[], C> implements StringSelectionReceiver,
		ListSelectionListener {

	private JList list;

	private ListModelAdapter lma;

	private JLabel descriptionLabel;

	protected String[] fileTypes;

	private boolean refresh;

	public StringSetConfigurator(Bean<C> bean, PropertyDescriptor property) {
		super(bean, property);
		setLayout(new BorderLayout());
		lma = new ListModelAdapter();
		list = new JList(lma);

		list.addListSelectionListener(this);
		JScrollPane scrollPane = new JScrollPane(list);
		add(scrollPane, BorderLayout.CENTER);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);

		descriptionLabel = new JLabel(getAdjustable().info());
		topPanel.add(descriptionLabel, BorderLayout.CENTER);

		setPreferredSize(new Dimension(400, 250));
		initialize(true);
		refresh = false;

	}

	@Override
	protected void _actionPerformed(ActionEvent evt) {
		int[] indices = list.getSelectedIndices();
		String[] strings = Utilities.castArray(lma.getData(), String.class);
		List<String> selectedStrings = new LinkedList<String>();
		for (int i = 0; i < indices.length; i++) {
			selectedStrings.add(strings[indices[i]]);
		}
		getBean().setValue(getProperty(),
				selectedStrings.toArray(new String[0]));

	}

	@Override
	protected void _setEnabled(boolean enabled) {
		list.setEnabled(enabled);

	}

	@Override
	public void focus() {

	}

	@Override
	protected synchronized void refresh(String[] value) {
		refresh = true;
		TreeSet<String> union = new TreeSet<String>();
		String [] data = Utilities.castArray(lma.getData(), String.class);
		for (String d : data) {
			union.add(d);
		}
		for (String v : value) {
			union.add(v);
		}
		String [] allStrings = union.toArray(new String[0]);
		List<Integer> selectedIndicesList = new LinkedList<Integer>();
		
		for (int i = 0; i < value.length; i++) {
			int index = Utilities.indexOf(allStrings, value[i]);
			if (index >= 0) {
				selectedIndicesList.add(index);
			}
		}
		int [] selectedIndices = new int [selectedIndicesList.size()];
		int i = 0;
		for (Integer index : selectedIndicesList) {
			selectedIndices[i] = index;
			i++;
		}
		list.setSelectedIndices(selectedIndices);
		refresh = false;
	}

	public void reinitialize() {
		initialize(false);
	}

	private void initialize(boolean init) {
		TreeSet<String> choices = new TreeSet<String>(getBean()
				.getStringsForProperty(getProperty()));
		TreeSet<String> values = new TreeSet<String>();
		for (String s : getValue()) {
			if (choices.contains(s)) {
				values.add(s);
			}
		}
		lma.setData(choices);
		refresh(values.toArray(new String [0]));
	}

	public synchronized void valueChanged(ListSelectionEvent e) {
		if (!refresh) {
			actionPerformed(null);
		}

	}

}
