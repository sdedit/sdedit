package net.sf.sdedit.editor;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;

import net.sf.sdedit.config.ExportConfiguration;
import net.sf.sdedit.diagram.DiagramFactory;
import net.sf.sdedit.diagram.SDPaintDevice;
import net.sf.sdedit.server.Exporter;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.ConfigurationUI;
import net.sf.sdedit.ui.components.configuration.ConfigurationUIListener;
import net.sf.sdedit.ui.impl.DiagramTab;
import net.sf.sdedit.util.UIUtilities;

public class ExportDialog extends JDialog implements ConfigurationUIListener, WindowListener, PropertyChangeListener {

	private static final long serialVersionUID = 2916559030760216855L;

	private Bean<ExportConfiguration> configuration;

	private DiagramTab tab;

	private ConfigurationUI<ExportConfiguration> cui;

	private Bean<ExportConfiguration> copy;

	private Map<Integer, File> files;

	public ExportDialog(DiagramTab tab) {
		super(tab.get_UI());
		setTitle("Export diagram");
		this.tab = tab;
		this.addWindowListener(this);
		configuration = new Bean<ExportConfiguration>(ExportConfiguration.class, null);
		configuration.addPropertyChangeListener(this);
		configuration.setPermitNullValues(true);
		getContentPane().setLayout(new BorderLayout());
		setModal(true);
		files = new HashMap<Integer, File>();
		cui = new ConfigurationUI<ExportConfiguration>(this, configuration, null, null, null, null, false);
		cui.hideCategoryList();
		getContentPane().add(cui, BorderLayout.CENTER);
		pack();
	}

	public ExportConfiguration getConfiguration() {
		return configuration.getDataObject();
	}
	
	private File getFile (DiagramTab tab, String type) {
		File file = files.get(tab.getId());
		if (file == null) {
			file = tab.getFile();
		}
		if (file != null && type != null) {
			file = new File(file.getParentFile(), file.getName().replaceAll("(.*)\\..*", "$1") + "." + type);
		}
		return file;
	}

	public void open(DiagramTab tab) {
		this.tab = tab;
		cui.refreshAll();
		copy = configuration.copy();
		UIUtilities.centerWindow(this, this.getOwner());
		configuration.getDataObject().setFile(getFile(tab, configuration.getDataObject().getType()));
		adjustButton();
		setVisible(true);
	}

	@Override
	public void cancelConfiguration() {
		this.setVisible(false);
		this.dispose();
		configuration.takeValuesFrom(copy);
	}

	public void adjustButton() {
		File file = configuration.getDataObject().getFile();
		boolean enable = file != null && (file.getParentFile() == null || file.getParentFile().exists())
				&& !file.isDirectory();
		if (enable) {
			cui.getButtonPanel().enable(cui.getOkAction());
		} else {
			cui.getButtonPanel().disable(cui.getOkAction());
		}
	}

	@Override
	public void applyConfiguration() {
		File file = configuration.getDataObject().getFile();
		if (file == null) {

		} else {
			if (file.exists()) {
				if (tab.get_UI().confirmOrCancel("Overwrite existing file?") < 1) {
					return;
				}
			}
			try {
				FileOutputStream stream = new FileOutputStream(file);
				try {
					Exporter exporter = Exporter.getExporter(configuration.getDataObject().getType(),
							null, null,
							stream);
					SDPaintDevice paintDevice = new SDPaintDevice(exporter);
					DiagramFactory factory = tab.createFactory(paintDevice);
					factory.generateDiagram(tab.getConfiguration().getDataObject());
					exporter.export();
				} finally {
					stream.close();
				}
			} catch (Throwable e) {
				tab.get_UI().errorMessage(e, "Export failed", "Export failed");
			}
		}
		this.setVisible(false);
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (this.isVisible()) {
			if (evt.getPropertyName().equals("type")) {
				String type = (String) evt.getNewValue();
				File file = getFile(tab, type);
				if (file != null) {
					configuration.getDataObject().setFile(file);
				}
			}
			if (evt.getPropertyName().equals("file")) {
				files.put(tab.getId(), (File) evt.getNewValue());
			}
			adjustButton();
		}
	}

}
