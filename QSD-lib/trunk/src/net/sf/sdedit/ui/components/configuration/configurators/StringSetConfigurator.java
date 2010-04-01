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
