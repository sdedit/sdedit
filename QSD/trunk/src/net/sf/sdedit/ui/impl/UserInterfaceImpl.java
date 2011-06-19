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
package net.sf.sdedit.ui.impl;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.iharder.dnd.FileDrop;
import net.iharder.dnd.FileDrop.Listener;
import net.sf.sdedit.Constants;
import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.config.GlobalConfiguration;
import net.sf.sdedit.editor.Decisions;
import net.sf.sdedit.editor.Editor;
import net.sf.sdedit.editor.TabActivator;
import net.sf.sdedit.help.HelpTab;
import net.sf.sdedit.icons.Icons;

import net.sf.sdedit.ui.Tab;
import net.sf.sdedit.ui.TabListener;
import net.sf.sdedit.ui.UserInterface;
import net.sf.sdedit.ui.UserInterfaceListener;
import net.sf.sdedit.ui.components.GrabbableViewport;
import net.sf.sdedit.ui.components.MenuBar;
import net.sf.sdedit.ui.components.OptionDialog;
import net.sf.sdedit.ui.components.ScalePanel;
import net.sf.sdedit.ui.components.ToolBar;
import net.sf.sdedit.ui.components.buttons.ActionManager;
import net.sf.sdedit.ui.components.buttons.Activator;
import net.sf.sdedit.ui.components.buttons.ManagedAction;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.ConfigurationAction;
import net.sf.sdedit.ui.components.configuration.ConfigurationUI;
import net.sf.sdedit.ui.components.configuration.ConfigurationUIListener;
import net.sf.sdedit.util.OS;
import net.sf.sdedit.util.Predicate;
import net.sf.sdedit.util.Ref;
import net.sf.sdedit.util.UIUtilities;

@SuppressWarnings("serial")
public final class UserInterfaceImpl extends JFrame implements Constants,
		UserInterface, TabContainerListener, HyperlinkListener,
		ConfigurationUIListener, TabListener, Listener {

	private JFileChooser fileChooser;

	private TabContainer tabContainer;

	private JPanel bottomPanel;

	private MenuBar menuBar;

	private List<UserInterfaceListener> listeners;

	private ScalePanel scalePanel;

	private PrintDialog printDialog;

	private ToolBar toolbar;

	private JTabbedPane configurationPane;

	private JDialog preferencesDialog;

	private ConfigurationUI<GlobalConfiguration> globalConfigurationUI;

	private ConfigurationUI<Configuration> defaultCUI;

	private ConfigurationUI<Configuration> localConfigurationUI;

	static {
		if (OS.TYPE != OS.Type.WINDOWS) {
			GrabbableViewport.setHandCursorIcon(Icons.getIcon("grabbing"));
		}
		ToolTipManager.sharedInstance().setDismissDelay(
				1000 * ConfigurationManager.getGlobalConfiguration()
						.getTooltipDismissDelay());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sd.edit.ui.UserInterface#showAboutDialog(java.net.URL)
	 */
	public void showAboutDialog(URL aboutURL) {
		System.gc();
		new AboutDialog(this, aboutURL).setVisible(true);
	}

	private ActionManager actionManager() {
		return Editor.getEditor().getActionManager();
	}
	
	public ToolBar getToolbar () {
		return toolbar;
	}

	public UserInterfaceImpl() {
		super();

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		preferencesDialog = new JDialog(this);
		preferencesDialog.setTitle("Preferences");
		preferencesDialog.getContentPane().setLayout(new BorderLayout());
		preferencesDialog.setModal(true);
		preferencesDialog.setSize(new Dimension(675, 475));

		configurationPane = new JTabbedPane();
		preferencesDialog.getContentPane().add(configurationPane,
				BorderLayout.CENTER);
		globalConfigurationUI = new ConfigurationUI<GlobalConfiguration>(
				this,
				ConfigurationManager.getGlobalConfigurationBean(),
				ConfigurationManager.GLOBAL_DEFAULT,
				null,
				"Restore defaults|Changes the current global preferences so that they are equal to the default preferences",
				"<html>In this tab you can change global preferences. On exit, they are stored in the"
						+ " file <tt>"
						+ Constants.GLOBAL_CONF_FILE.getAbsolutePath()
						+ "</tt>.", false);
		globalConfigurationUI.setBorder(BorderFactory.createEmptyBorder(15, 15,
				0, 15));
		defaultCUI = new ConfigurationUI<Configuration>(
				this,
				ConfigurationManager.getDefaultConfigurationBean(),
				ConfigurationManager.LOCAL_DEFAULT,
				null,
				"Restore defaults|Changes the initial preferences (to be used for newly created diagrams) such that they are equal to the default settings",
				"<html>This tab is for adjusting the (initial) preferences that are used for"
						+ " newly created diagrams. They are stored along with the global preferences.",
				false);
		defaultCUI.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

		localConfigurationUI = new ConfigurationUI<Configuration>(
				this,
				ConfigurationManager.createNewDefaultConfiguration(),
				ConfigurationManager.getDefaultConfigurationBean(),
				"Save as initial|Saves the current diagram's preferences as the initial preferences (to be used for all newly created diagrams)",
				"Restore initial|Changes the current diagram's preferences such that they are equal to the initial preferences",
				"<html>This tab is for changing the preferences for the diagram"
						+ " currently being displayed.<br>They will be stored "
						+ " when the diagram is saved as an <tt>.sdx</tt>-file.",
				false);
		localConfigurationUI.setBorder(BorderFactory.createEmptyBorder(15, 15,
				0, 15));

		configurationPane.add("Global preferences", globalConfigurationUI);
		configurationPane.add("Initial diagram preferences", defaultCUI);
		configurationPane.addTab("Current diagram preferences",
				localConfigurationUI);

		listeners = new LinkedList<UserInterfaceListener>();
		menuBar = new MenuBar();
		toolbar = new ToolBar();
		toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		toolbar.setFloatable(false);

		new FileDrop(this, this);

	}

	public void addListener(UserInterfaceListener listener) {
		listeners.add(listener);
	}

	public void addCategory(String category, String icon) {
		ImageIcon imageIcon = icon == null || icon.equals("") ? null : Icons
				.getIcon(icon);
		menuBar.addMenu(category, imageIcon);
	}

	public void addAction(String category, Action action, Activator activator) {
		if (action != null) {
			action = actionManager().addAction(action);
			String iconName = (String) action.getValue(ManagedAction.ICON_NAME);
			if (iconName != null) {
				Icon icon = Icons.getIcon(iconName);
				action.putValue(Action.SMALL_ICON, icon);
			}
		}
		JMenuItem item = menuBar.addAction(category, action, -1);
		registerComponent(item, action, activator);

	}

	public void addConfigurationAction(String category,
			ConfigurationAction<?> action, Activator activator) {
		JCheckBoxMenuItem item = MenuBar.makeMenuItem(action.getValue(
				Action.NAME).toString(), JCheckBoxMenuItem.class);
		menuBar.addItem(category, item);
		item.setToolTipText(action.getValue(Action.SHORT_DESCRIPTION)
				.toString());
		item.setIcon((Icon) action.getValue(Action.SMALL_ICON));

		registerComponent(item, action, activator);
		item.addActionListener(action);
		actionManager().registerConfigurationAction(action, item);
	}

	void registerComponent(JComponent comp, Action action, Activator activator) {
		actionManager().registerButton(comp, action, activator);
	}

	void enableComponents() {
		actionManager().enableComponents();
	}

	public void help(String title, String file, boolean advanced) {
		if (!tabContainer.existsCategory("Help pages")) {
			tabContainer.addCategory("Help pages", Icons.getIcon("help"));
		}
		HelpTab tab = HelpTab.getHelpTab(this, file, advanced);
		if (tabContainer.exists(tab)) {
			tabContainer.select(tab);
		} else {
			tab.setTitle(title);
			tabContainer.addTabToCategory(tab, "Help pages", true);
		}
	}

	public void fireHyperlinkClicked(String hyperlink) {
		for (UserInterfaceListener listener : listeners) {
			listener.hyperlinkClicked(hyperlink);
		}
	}

	private final TabActivator<DiagramTab> nonEmptyDiagramActivator = new TabActivator<DiagramTab>(
			DiagramTab.class, this) {
		@Override
		protected boolean _isEnabled(DiagramTab tab) {
			return !tab.isEmpty();
		}
	};

	private final TabActivator<Tab> zoomActivator = new TabActivator<Tab>(
			Tab.class, this) {
		@Override
		protected boolean _isEnabled(Tab tab) {
			return tab.canZoom();
		}
	};

	public void showUI() {
		setIconImage(Icons.getIcon("icon").getImage());
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System
				.getProperty("user.home")));
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());

		tabContainer = new TabNavigatorContainer(0.2);
		tabContainer.addListener(this);

		pane.add(tabContainer.getComponent(), BorderLayout.CENTER);

		pane.add(toolbar, BorderLayout.NORTH);

		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		scalePanel = new ScalePanel(false);
		Dimension spSize = new Dimension(140, 24);

		scalePanel.setMaximumSize(spSize);
		scalePanel.setMinimumSize(spSize);
		scalePanel.setPreferredSize(spSize);

		scalePanel.setOpaque(false);
		addToolbarSeparator();
		toolbar.add(scalePanel);

		registerComponent(scalePanel, null, zoomActivator);

		addToolbarSeparator();

		addToToolbar(scalePanel.normalSizeAction, zoomActivator);
		addToToolbar(scalePanel.fitHeightAction, zoomActivator);
		addToToolbar(scalePanel.fitWidthAction, zoomActivator);
		addToToolbar(scalePanel.fitWindowAction, zoomActivator);

		pane.add(bottomPanel, BorderLayout.SOUTH);

		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

		int width = (int) (0.8 * screenWidth);
		int height = (int) (0.8 * screenHeight);

		int left = (int) (0.1 * screenWidth);
		int top = (int) (0.1 * screenHeight);

		setSize(width, height);
		setLocation(left, top);

		setJMenuBar(menuBar);

		// printDialog.loadProfiles();
		ConfigurationManager.getGlobalConfigurationBean()
				.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						enableComponents();
						if (evt.getPropertyName().equals("tooltipDismissDelay")) {
							ToolTipManager.sharedInstance().setDismissDelay(
									ConfigurationManager
											.getGlobalConfiguration()
											.getTooltipDismissDelay() * 1000);
						}
					}
				});

		enableComponents();
		setTitle("Quick Sequence Diagram Editor");
		setVisible(true);

	}

	/**
	 * This method is called when the state of the ATabbedPane changes, i. e.
	 * when the tab to be displayed changes.
	 * 
	 * @param e
	 */
	public void stateChanged(ChangeEvent e) {

	}

	public String addTab(Tab tab, boolean selectIt) {
		return tabContainer.addTab(tab, selectIt);
	}

	public DiagramTextTab addDiagramTextTab(String tabTitle,
			Bean<Configuration> configuration, boolean selectIt) {
		DiagramTextTab tab = new DiagramTextTab(this, ConfigurationManager
				.getGlobalConfiguration().getEditorFont(), configuration);
		tab.setTitle(tabTitle);
		addTab(tab, selectIt);
		enableComponents();
		return tab;
	}

	public Tab currentTab() {
		return tabContainer.getSelectedTab();
	}

	public void updateToolbarAction(Action action) {
		toolbar.updateAction(action);
	}

	public File getCurrentFile() {
		return currentTab().getFile();
	}

	public boolean closeCurrentTab(boolean check) {
		boolean flag = currentTab().close(check);
		enableComponents();
		return flag;
	}

	public void addToToolbar(Action _action, Activator activator) {
		Action action = actionManager().addAction(_action);
		JButton button = toolbar.addAction(action);
		registerComponent(button, action, activator);
	}

	public void addToolbarSeparator() {
		toolbar.addSeparator();
	}

	// public static Action makeToolbarAction(final Action action) {
	//
	// return new AbstractAction() {
	// {
	// putValue(Action.NAME, action.getValue(Action.NAME));
	// putValue(Action.SHORT_DESCRIPTION, action
	// .getValue(Action.SHORT_DESCRIPTION));
	// String iconName = (String) action.getValue(Actions.ICON_NAME);
	// if (iconName != null) {
	// Icon icon = Icons.getIcon("large/" + iconName);
	// putValue(Action.SMALL_ICON, icon);
	// } else {
	// putValue(Action.SMALL_ICON, action
	// .getValue(Action.SMALL_ICON));
	// }
	// }
	//
	// public void actionPerformed(ActionEvent e) {
	// action.actionPerformed(e);
	// }
	// };
	// }

	public void configure(Bean<Configuration> conf) {

		if (conf != null) {
			localConfigurationUI.setBean(conf);
			localConfigurationUI.setEnabled(true);
		} else {
			localConfigurationUI.setEnabled(false);
		}

		configurationPane.setSelectedIndex(conf != null ? 2 : 0);

		UIUtilities.centerWindow(preferencesDialog, this);
		preferencesDialog.setVisible(true);
	}

	public void setQuitAction(final Action action) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				action.actionPerformed(new ActionEvent(this,
						ActionEvent.ACTION_PERFORMED, ""));
			}
		});
	}

	public int confirmOrCancel(String message) {
		String option = getOption(message, "Cancel", "No", "Yes#");
		switch (option.charAt(0)) {
		case 'Y':
			return 1;
		case 'N':
			return 0;
		default:
			return -1;
		}
	}

	public boolean confirm(String message) {
		return confirmOrCancel(message) == 1;
	}

	public void message(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	public int getNumberOfTabs() {
		return tabContainer.getTabCount();
	}

	public String getString(String question, String initialValue) {
		return JOptionPane.showInputDialog(this, question, initialValue);
	}

	// TODO
	// change into addToggleAction
	public void addPredicateAction(String category, String name,
			String description, String tooltip, Icon icon,
			final Predicate predicate, boolean initialValue) {
		
		menuBar.addToggleAction(category, name, description, tooltip, icon, predicate, initialValue);

		
	}

	/**
	 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
	 */
	public void hyperlinkUpdate(HyperlinkEvent event) {

		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			if (event.getURL().toString().startsWith("http")) {
				return;
			}
			if (event.getURL().toString().endsWith("sdx")) {
				String file = event.getURL().toString();
				file = file.substring(file.lastIndexOf('/') + 1);
				fireHyperlinkClicked("example:" + file);
			} else if (event.getURL().toString().indexOf('#') > 0) {
				try {
					JEditorPane pane = (JEditorPane) event.getSource();
					pane.setPage(event.getURL());
				} catch (Exception e) {
					/* empty */
				}
			} else if (event.getURL().toString().endsWith("html")) {

				String file = event.getURL().toString();
				file = file.substring(file.lastIndexOf('/') + 1);
				fireHyperlinkClicked("help:" + file);
			}
		}
	}

	public void removeAction(String category, Action action) {
		menuBar.removeAction(category, action);
	}

	public boolean selectTabWith(File file) {
		for (Tab tab : tabContainer.getTabs()) {
			if (file.equals(tab.getFile())) {
				tabContainer.select(tab);
				return true;
			}
		}
		return false;
	}

	public void selectTabById(int id) {
		for (Tab tab : tabContainer.getTabs()) {
			if (tab.getId() == id) {
				tabContainer.select(tab);
				return;
			}
		}
	}

	public boolean selectTab(Tab tab) {
		return tabContainer.select(tab);
	}

	public void showPrintDialog(String filetype, DiagramTab tab) {
		if (printDialog == null) {
			printDialog = new PrintDialog(this);
		}
		printDialog.show(tab, filetype);
	}

	public void exit() {
		setVisible(false);
	}

	public ScalePanel getScalePanel() {
		return scalePanel;
	}

	public String getOption(String text, String... options) {
		return UIUtilities.getOption(this, text, options);
	}

	public String getOptionWithDecision(String text, int decisionKey,
			String... options) {
		String decision = Decisions.getDecisionString(decisionKey);
		String choice = null;
		if (decision != null) {
			OptionDialog optionDialog = new OptionDialog(this,
					"Please choose an option", Icons.getIcon("question"), text,
					decision);
			for (String option : options) {
				optionDialog.addOption(option);
			}
			Ref<Boolean> dec = new Ref<Boolean>();
			choice = optionDialog.getOption(dec);
			if (dec.t) {
				Decisions.setUnused(decisionKey);
			}
		}
		return choice;
	}

	public void applyConfiguration() {
		preferencesDialog.setVisible(false);
		defaultCUI.apply();
		localConfigurationUI.apply();
		globalConfigurationUI.apply();
	}

	public void cancelConfiguration() {
		preferencesDialog.setVisible(false);
		defaultCUI.cancel();
		localConfigurationUI.cancel();
		globalConfigurationUI.cancel();
	}

	public void errorMessage(Throwable throwable, String caption, String header) {
		String _caption = caption == null ? "Error" : caption;
		String message = header == null ? "" : header;
		if (throwable != null) {
			throwable.printStackTrace();
			if (message.length() > 0) {
				message += "\n\n";
			}
			message += "An exception of type "
					+ throwable.getClass().getSimpleName() + "\n";
			message += "has occurred with the message:\n";
			message += throwable.getMessage();
			if (throwable.getCause() != null) {
				message += "\n\nThe exception was caused by a "
						+ throwable.getCause().getClass().getSimpleName();
				message += "\nwith the message "
						+ throwable.getCause().getMessage();
			}
		}
		JOptionPane.showMessageDialog(this, message, _caption,
				JOptionPane.ERROR_MESSAGE);
	}
	
	public void addDefaultTab () {
        addDiagramTextTab(
                "untitled",
                ConfigurationManager
                        .createNewDefaultConfiguration(), true);
	}

	public void tabIsClosed(Tab tab) {
		tabContainer.remove(tab);
		if (tabContainer.getTabCount() == 0) {
		    addDefaultTab();

		}
	}

	public void tabClosing(Tab tab) {
	    
	}

	public void tabSelected(Tab previous, Tab current) {

		Action closeAction = menuBar.getActionByName("[F12]&Close");
		if (closeAction != null) {
			closeAction.setEnabled(tabContainer.getTabCount() > 1);
		}

		for (UserInterfaceListener listener : listeners) {
			listener.tabChanged(previous, current);
		}

		if (current.getZoomPane() != null) {
			scalePanel.setScalable(current.getZoomPane());
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				enableComponents();
				String title = tabContainer.getSelectedTab().getTitle();
				if (title == null) {
					setTitle("Quick Sequence Diagram Editor");
				} else {
					setTitle(title + " - Quick Sequence Diagram Editor");
				}
			}
		});

	}

	public TabContainer getTabContainer() {
		return tabContainer;
	}

	public void titleChanged(Tab tab) {
		setTitle(tab.getTitle() + " - Quick Sequence Diagram Editor");
	}
	
	/**
	 * @see net.iharder.dnd.FileDrop.Listener#filesDropped(java.io.File[])
	 */
	public void filesDropped(File[] files) {
		for (File file : files) {
			try {
				Editor.getEditor().load(file.toURI().toURL());
			} catch (IOException e) {
				errorMessage(e, null, null);
			} catch (URISyntaxException e) {
				errorMessage(e, null, null);
			}
		}
	}
}
