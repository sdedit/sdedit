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

package net.sf.sdedit.ui.components.configuration;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.sdedit.ui.components.ATabbedPane;
import net.sf.sdedit.ui.components.ButtonPanel;
import net.sf.sdedit.ui.components.configuration.configurators.StringSelectionReceiver;
import net.sf.sdedit.util.Tooltips;

/**
 * A component for configuring the properties of a {@linkplain Bean}.
 * 
 * @author Markus Strauch
 * 
 * @param <C>
 *            the type of the data object to be configured
 */
@SuppressWarnings("serial")
public class ConfigurationUI<C extends DataObject> extends JPanel {

	private Map<String, JPanel> categoryMap;

	private ConfiguratorFactory<C> configuratorFactory;

	private Map<String, List<Configurator<?, C>>> configurators;

	private Map<String, Integer> labelWidths;

	private JList categoryList;

	private JPanel right;

	private ConfigurationUIListener listener;

	private Bean<C> bean;

	private Bean<C> formerState;

	private Bean<C> defaultBean;

	private ButtonPanel buttonPanel;

	private JPanel categoryListPanel;

	private ATabbedPane tabbedPane;
	
	private boolean editable;

	/**
	 * Constructor.
	 * 
	 * @param listener
	 *            the dialog that contains this configuration component
	 * @param bean
	 *            the bean to be configured
	 * @param defaultBean
	 *            a bean with values that can be restored, or null
	 * @param saveAsDefault
	 *            a string for the button that is associated to the action that
	 *            saves the bean's values as the defaultBean's values,
	 *            optionally followed by '|' and a tool-tip, or null, if no such
	 *            button should be visible
	 * @param loadDefault
	 *            a string for the button that is associated to the action that
	 *            restores the bean's values from the defaultBean's values,
	 *            optionally followed by '|' and a tool-tip, or null, if no such
	 *            button should be visible
	 * @param description
	 *            a description to appear at the top of this configuration
	 *            component
	 */
	public ConfigurationUI(ConfigurationUIListener listener, Bean<C> bean,
			Bean<C> defaultBean, String saveAsDefault, String loadDefault,
			String description, boolean tabbed) {
		super();
		editable = true;
		this.bean = bean;
		formerState = bean.copy();
		this.defaultBean = defaultBean;
		setLayout(new BorderLayout());

		this.listener = listener;

		configuratorFactory = new ConfiguratorFactory<C>();
		configurators = new HashMap<String, List<Configurator<?, C>>>();

		categoryMap = new TreeMap<String, JPanel>();
		if (tabbed) {
			tabbedPane = new ATabbedPane();
			add(tabbedPane, BorderLayout.CENTER);
		} else {
			right = new JPanel();
			right.setLayout(new BorderLayout());
			right.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

			add(right, BorderLayout.CENTER);

			categoryListPanel = new JPanel();
			categoryListPanel.setLayout(new BorderLayout());
			categoryList = new JList();
			JScrollPane listScrollPane = new JScrollPane(categoryList);
			categoryListPanel.add(listScrollPane, BorderLayout.CENTER);
			add(categoryListPanel, BorderLayout.WEST);

			categoryList.addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					String category = (String) categoryList.getSelectedValue();
					if (category != null) {
						JPanel panel = categoryMap.get(category);
						right.removeAll();
						right.add(panel, BorderLayout.CENTER);
						right.updateUI();
					}
				}
			});

			final ListCellRenderer lcr = categoryList.getCellRenderer();
			categoryList.setCellRenderer(new ListCellRenderer() {

				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					JComponent comp = (JComponent) lcr
							.getListCellRendererComponent(list, value, index,
									isSelected, cellHasFocus);
					comp.setBorder(BorderFactory
							.createEmptyBorder(0, 10, 0, 10));
					return comp;
				}

			});
		}

		labelWidths = new HashMap<String, Integer>();

		if (description != null) {
			JLabel descriptionLabel = new JLabel(description);
			descriptionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5,
					5));
			add(descriptionLabel, BorderLayout.NORTH);
		}

		buttonPanel = new ButtonPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.addAction(cancel);
		if (loadDefault != null) {
			String[] split = loadDefault.split("\\|");
			String name = split[0];
			String tooltip = split.length == 1 ? "" : split[1];
			buttonPanel.addAction(restoreDefaultsAction(name, tooltip));
		}
		if (saveAsDefault != null) {
			String[] split = saveAsDefault.split("\\|");
			String name = split[0];
			String tooltip = split.length == 1 ? "" : split[1];
			buttonPanel.addAction(saveAsDefaultAction(name, tooltip));
		}
		buttonPanel.addAction(ok);
		init(bean, defaultBean);
		refreshAll();
	}
	
	public ButtonPanel getButtonPanel () {
		return buttonPanel;
	}
	
	public void focusFirst () {
		getConfigurators().get(0).focus();
	}

	public void hideCategoryList() {
		remove(categoryListPanel);
	}

	public void hideButtons() {
		remove(buttonPanel);
	}
	
	public void addTab (Component tab, String title) {
		tabbedPane.addTab(tab, title);
	}
	
	public void select (String name) {
		if (tabbedPane != null) {
			tabbedPane.selectByName(name);
		} else {
			categoryList.setSelectedValue(name, true);
		}
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (tabbedPane != null) {
			tabbedPane.setEnabled(enabled);
		} else {
			categoryList.setEnabled(enabled);
		}
		for (Configurator<?, C> configurator : getConfigurators()) {
			configurator.setEnabled(enabled
					&& configurator.isDependencySatisfied());
		}
	}
	
	public void setEditable (boolean editable) {
		this.editable = editable;
		for (Configurator<?, C> configurator : getConfigurators()) {
			configurator.setEditable(editable);
		}
	}
	
	public boolean isEditable () {
		return editable;
	}

	private List<Configurator<?, C>> getConfigurators() {
		List<Configurator<?, C>> list = new LinkedList<Configurator<?, C>>();
		for (List<Configurator<?, C>> sublist : configurators.values()) {
			list.addAll(sublist);
		}
		return list;
	}

	public void updateStringSelections() {
		for (Configurator<?, C> configurator : getConfigurators()) {
			if (configurator instanceof StringSelectionReceiver) {
				bean.clearStringSelection(configurator.getPropertyDescriptor()
						.getName());
				((StringSelectionReceiver) configurator).reinitialize();
			}
		}
	}

	/**
	 * Returns for each property category of the bean the panel where the
	 * properties can be configured
	 * 
	 * @return the panels for the bean's property categories
	 */
	public Collection<JPanel> getCategoryPanels() {
		return categoryMap.values();
	}

	private void init(Bean<C> bean, Bean<C> defaultObject) {
		for (PropertyDescriptor property : bean.getProperties()) {
			if (property.getWriteMethod().getAnnotation(Adjustable.class)
					.editable()) {
				add(bean, property, defaultObject);
			}
		}
		for (Map.Entry<String, List<Configurator<?, C>>> entry : configurators
				.entrySet()) {
			int width = labelWidths.get(entry.getKey());
			for (Configurator<?, C> configurator : entry.getValue()) {
				configurator.setLabelWidth(width);
			}
		}
	}

	private void add(Bean<C> bean, PropertyDescriptor property,
			Bean<C> defaultObject) {
		Method writeMethod = property.getWriteMethod();
		if (writeMethod.isAnnotationPresent(Adjustable.class)) {
			Adjustable adj = writeMethod.getAnnotation(Adjustable.class);
			if (adj.editable()) {
				String category = adj.category();
				JPanel categoryPanel = categoryMap.get(category);
				int labelWidth = 0;
				if (categoryPanel == null) {
					categoryPanel = new JPanel();
					categoryPanel.setLayout(new BoxLayout(categoryPanel,
							BoxLayout.Y_AXIS));
					categoryPanel.setBorder(new TitledBorder(category));
					configurators.put(category,
							new LinkedList<Configurator<?, C>>());
					categoryMap.put(category, categoryPanel);
					if (tabbedPane != null) {
						tabbedPane.addTab(categoryPanel, category);

					} else {
						categoryList
								.setListData(categoryMap.keySet().toArray());

						if (categoryMap.keySet().iterator().next().equals(
								category)) {
							right.removeAll();
							right.add(categoryPanel, BorderLayout.CENTER);
						}
						categoryList.setSelectedIndex(0);
					}
				} else {
					labelWidth = labelWidths.get(category);
				}
				Configurator<?, C> configurator = configuratorFactory
						.createConfigurator(bean, property);
				String tooltipKey = bean.getDataClass().getSimpleName() + 
					"." + property.getName();
				String tooltipText = Tooltips.getTooltip(tooltipKey);
				if (tooltipText == null && adj.tooltip().length() > 0) {
					tooltipText = adj.tooltip();
				}
				configurator.setToolTipText(tooltipText);
				labelWidth = Math.max(labelWidth, configurator.getLabelWidth());
				labelWidths.put(category, labelWidth);
				int height = configurator.getPreferredSize().height;
				Dimension size = new Dimension(500, height);
				configurator.setMinimumSize(size);
				configurator.setPreferredSize(size);
				configurator.setMaximumSize(size);
				configurator.setAlignmentX(0F);
				configurators.get(category).add(configurator);
				categoryPanel.add(configurator);
				int gap = 15 * adj.gap();
				categoryPanel.add(Box.createRigidArea(new Dimension(1, gap)));

			}
		} else {
			throw new IllegalArgumentException(
					"No Adjustable annotation present for property "
							+ property.getName());
		}
	}

	public void setBean(Bean<C> bean) {
		this.bean = bean;
		this.formerState = bean.copy();
		for (Configurator<?, C> configurator : getConfigurators()) {
			configurator.setBean(bean);
		}
	}

	private Action ok = new AbstractAction() {
		{
			putValue(Action.NAME, "OK");
			putValue(Action.SHORT_DESCRIPTION,
					"Confirms all changes made since this dialog has been opened");
		}

		public void actionPerformed(ActionEvent e) {
			// parent.setVisible(false);
			listener.applyConfiguration();
		}
	};

	/**
	 * Calls {@linkplain Configurator#refresh()} on all configurators that
	 * belong to this configuration component, so that they will reflect the
	 * values of their corresponding properties thereafter.
	 */
	public void refreshAll() {
		for (Configurator<?, C> configurator : getConfigurators()) {
			configurator.refresh();
		}
	}

	public void apply() {
		formerState.takeValuesFrom(bean);
	}

	public void cancel() {
		bean.takeValuesFrom(formerState);
	}

	private Action cancel = new AbstractAction() {
		{
			putValue(Action.NAME, "Cancel");
			putValue(Action.SHORT_DESCRIPTION,
					"Cancels all changes made since this dialog has been opened");
		}

		public void actionPerformed(ActionEvent e) {
			listener.cancelConfiguration();
			// parent.setVisible(false);
		}
	};

	private Action restoreDefaultsAction(final String name, final String tooltip) {
		return new AbstractAction() {

			{
				putValue(Action.NAME, name);
				putValue(Action.SHORT_DESCRIPTION, tooltip);
			}

			public void actionPerformed(ActionEvent e) {
				if (defaultBean != null) {
					bean.takeValuesFrom(defaultBean);
				}
			}
		};
	}

	private Action saveAsDefaultAction(final String name, final String tooltip) {

		return new AbstractAction() {
			{
				putValue(Action.NAME, name);
				putValue(Action.SHORT_DESCRIPTION, tooltip);
			}

			public void actionPerformed(ActionEvent e) {
				if (defaultBean != null) {
					defaultBean.takeValuesFrom(bean);
				}

			}
		};
	}
}
