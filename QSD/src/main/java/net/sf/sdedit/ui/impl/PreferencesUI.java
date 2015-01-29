package net.sf.sdedit.ui.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import net.sf.sdedit.Constants;
import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.config.GlobalConfiguration;
import net.sf.sdedit.config.SequenceConfiguration;
import net.sf.sdedit.editor.plugin.Plugin;
import net.sf.sdedit.editor.plugin.PluginRegistry;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.ConfigurationUI;
import net.sf.sdedit.util.UIUtilities;

public class PreferencesUI {

	private JDialog preferencesDialog;

	private JTabbedPane configurationPane;

	private UserInterfaceImpl ui;

	private static class PrefData {

		protected Class<?> cls;

		protected ConfigurationUI<?> ui;

		protected String type;

		protected String title;

	}

	private List<PrefData> prefData;

	private void put(Class<?> cls, ConfigurationUI<?> ui, String type,
			String title) {
		PrefData p = new PrefData();
		p.cls = cls;
		p.ui = ui;
		p.type = type;
		p.title = title;
		prefData.add(p);
	}

	private PrefData get(Class<?> cls, String type) {
		for (PrefData pd : prefData) {
			if (cls == pd.cls && type.equals(pd.type)) {
				return pd;
			}
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PreferencesUI(UserInterfaceImpl ui) {
		this.ui = ui;
		prefData = new ArrayList<PrefData>();

		preferencesDialog = new JDialog(ui);
		preferencesDialog.setTitle("Preferences");
		preferencesDialog.getContentPane().setLayout(new BorderLayout());
		preferencesDialog.setModal(true);
		preferencesDialog.setSize(new Dimension(675, 475));

		configurationPane = new JTabbedPane();
		preferencesDialog.getContentPane().add(configurationPane,
				BorderLayout.CENTER);
		ConfigurationUI<GlobalConfiguration> globalConfigurationUI = new ConfigurationUI<GlobalConfiguration>(
				ui,
				ConfigurationManager.getGlobalConfigurationBean(),
				ConfigurationManager.GLOBAL_DEFAULT,
				null,
				"Restore defaults|Changes the current global preferences so that they are equal to the default preferences",
				"<html>In this tab you can change global preferences. On exit, they are stored in the"
						+ " file <tt>"
						+ Constants.GLOBAL_CONF_FILE.getAbsolutePath()
						+ "</tt>.", false);
		ConfigurationUI<SequenceConfiguration> defaultCUI = new ConfigurationUI<SequenceConfiguration>(
				ui,
				ConfigurationManager
						.getDefaultConfigurationBean(SequenceConfiguration.class),
				ConfigurationManager
						.getInitialDefaultConfigurationBean(SequenceConfiguration.class),
				null,
				"Restore defaults|Changes the initial preferences (to be used for newly created diagrams) such that they are equal to the default settings",
				"<html>This tab is for adjusting the (initial) preferences that are used for"
						+ " newly created diagrams. They are stored along with the global preferences.",
				false);
		ConfigurationUI<SequenceConfiguration> localConfigurationUI = new ConfigurationUI<SequenceConfiguration>(
				ui,
				ConfigurationManager
						.createNewDefaultConfiguration(SequenceConfiguration.class),
				ConfigurationManager
						.getDefaultConfigurationBean(SequenceConfiguration.class),
				"Save as initial|Saves the current diagram's preferences as the initial preferences (to be used for all newly created diagrams)",
				"Restore initial|Changes the current diagram's preferences such that they are equal to the initial preferences",
				"<html>This tab is for changing the preferences for the diagram"
						+ " currently being displayed.<br>They will be stored "
						+ " when the diagram is saved as an <tt>.sdx</tt>-file.",
				false);


		put(GlobalConfiguration.class, globalConfigurationUI, "global",
				"Global preferences");
		put(SequenceConfiguration.class, defaultCUI, "default",
				"Initial diagram preferences");
		put(SequenceConfiguration.class, localConfigurationUI, "current",
				"Current diagram preferences");
		
		for (Plugin plugin : PluginRegistry.getInstance()) {
			Class<? extends Configuration> cc = plugin.getConfigurationClass();
			if (cc != null) {
				ConfigurationUI _ui = new ConfigurationUI(ui, ConfigurationManager.getDefaultConfigurationBean(cc),
						ConfigurationManager.getInitialDefaultConfigurationBean(cc),
				plugin.getText(Plugin.DEFAULT_CONF_SAVE),
				plugin.getText(Plugin.DEFAULT_CONF_RESTORE),
				plugin.getText(Plugin.DEFAULT_CONF_DESCRIPTION),
				false);
				put(cc, _ui, "default", plugin.getText(Plugin.DEFAULT_CONF_TITLE));
				_ui = new ConfigurationUI(ui, ConfigurationManager.createNewDefaultConfiguration(cc),
						ConfigurationManager.getDefaultConfigurationBean(cc),
				plugin.getText(Plugin.LOCAL_CONF_SAVE),
				plugin.getText(Plugin.LOCAL_CONF_RESTORE),
				plugin.getText(Plugin.LOCAL_CONF_DESCRIPTION),
				false);
				put(cc, _ui, "current", plugin.getText(Plugin.LOCAL_CONF_TITLE));
			}
		}

		for (PrefData pd : prefData) {
			pd.ui.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
		}
	}

	public void show(int selectedIndex) {
		configurationPane.removeAll();
		for (PrefData pd : prefData) {
			configurationPane.add(pd.title, pd.ui);
		}
		UIUtilities.centerWindow(preferencesDialog, ui);
		configurationPane.setSelectedIndex(selectedIndex);
		preferencesDialog.setVisible(true);
	}

	public void applyConfiguration() {
		preferencesDialog.setVisible(false);
		for (PrefData pd : prefData) {
			pd.ui.apply();
		}
	}

	public void cancelConfiguration() {
		preferencesDialog.setVisible(false);
		for (PrefData pd : prefData) {
			pd.ui.cancel();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void configure(Bean<? extends Configuration> conf) {
		int sel = 0;
		int i = 0;
		for (PrefData pd : prefData) {
			pd.ui.setEnabled(!"current".equals(pd.type) || conf != null
					&& pd.cls.equals(conf.getDataClass()));
			if ("current".equals(pd.type) && conf != null
					&& pd.cls.equals(conf.getDataClass())) {
				pd.ui.setBean((Bean) conf);
				sel = i;
			}
			i++;
		}
		show(sel);
	}

}
