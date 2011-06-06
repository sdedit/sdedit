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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.config.PrintConfiguration;
import net.sf.sdedit.diagram.DiagramDataProvider;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.error.SyntaxError;
import net.sf.sdedit.multipage.MultipageExporter;
import net.sf.sdedit.ui.components.ButtonPanel;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.ConfigurationUI;
import net.sf.sdedit.ui.components.configuration.ConfigurationUIListener;
import net.sf.sdedit.util.Grep;
import net.sf.sdedit.util.OS;
import net.sf.sdedit.util.UIUtilities;
import net.sf.sdedit.util.WindowsRegistry;

public class PrintDialog extends JDialog implements ConfigurationUIListener,
		PropertyChangeListener {
	
	private static final long serialVersionUID = 1L;

	private String fileType;

	private UserInterfaceImpl ui;

	private JScrollPane preview;

	private MultipageExporter exporter;

	private Bean<PrintConfiguration> printerProperties;

	private Bean<PrintConfiguration> copy;

	private JLabel scaleLabel;

	private boolean firstVisible = true;

	private DiagramTab tab;

	public PrintDialog(UserInterfaceImpl ui) {
		super(ui);
		this.ui = ui;
		setModal(true);
		init();
	}

	private void init() {
		JPanel center = new JPanel();
		getContentPane().add(center, BorderLayout.CENTER);
		ButtonPanel buttonPanel = new ButtonPanel();
		buttonPanel.addAction(cancel);
		buttonPanel.addAction(ok, 0, true);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		printerProperties = ConfigurationManager.getPrintConfigurationBean();
		setToCurrentFile();
		ConfigurationUI<PrintConfiguration> cui = new ConfigurationUI<PrintConfiguration>(
				this, printerProperties, null, null, null, null, false);
		cui.hideButtons();
		cui.hideCategoryList();
		printerProperties.addPropertyChangeListener(this);
		if (OS.TYPE == OS.Type.WINDOWS) {
			insertAcrd();
		}
		center.setLayout(new GridLayout(1, 2));
		center.add(cui);
		JPanel right = new JPanel();
		right.setBorder(new TitledBorder("Preview"));
		right.setLayout(new BorderLayout());
		preview = new JScrollPane();
		right.add(preview);
		scaleLabel = new JLabel("Zoom factor: 100 %");
		scaleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		right.add(scaleLabel, BorderLayout.SOUTH);
		center.add(right);
	}

	private void insertAcrd() {
		if (printerProperties.getDataObject().getCommand().equals(
				"/usr/bin/lpr")) {
			String acrd = WindowsRegistry
					.getValue(
							"HKEY_CLASSES_ROOT/Applications/AcroRD32.exe/shell/Read/command",
							null);
			if (acrd != null) {
				acrd = acrd.replace('\\', '/');
				String [] str = Grep.parse("\"(.*?)\".*",acrd);
				if (str != null && str.length == 1) {
					printerProperties.getDataObject().setCommand(str[0].replace('/', '\\'));
					printerProperties.getDataObject().setAction(PrintConfiguration.EXPORT_AND_PRINT);
				}
			}
		}
	}

	public void show(DiagramTab tab, String fileType) {
		this.fileType = fileType.toLowerCase();
		this.tab = tab;
		setTitle("Print or export multi-page " + fileType.toUpperCase()
				+ " document");
		setSize(new Dimension(740, 540));
		UIUtilities.centerWindow(this, ui);
		reinitialize();
		copy = printerProperties.copy();
		if (firstVisible) {
			if (ui.getCurrentFile() != null) {
				printerProperties.getDataObject().setExportFile(
						UIUtilities.affixType(ui.getCurrentFile(), "pdf"));
			}
			firstVisible = false;
		}
		setVisible(true);
	}

	private void setToCurrentFile() {
		File file = ui.getCurrentFile();
		if (file != null) {
			String name = file.getAbsolutePath();
			int dot = name.lastIndexOf('.');
			if (dot >= 0) {
				name = name.substring(0, dot);
			}
			printerProperties.getDataObject().setExportFile(
					new File(name + ".pdf"));
		}
	}

	@SuppressWarnings("serial")
	private Action cancel = new AbstractAction() {
		{
			putValue(Action.NAME, "Cancel");
		}

		public void actionPerformed(ActionEvent e) {
			printerProperties.takeValuesFrom(copy);
			setVisible(false);
		}
	};

	@SuppressWarnings("serial")
	private Action ok = new AbstractAction() {

		{
			putValue(Action.NAME, "OK");
		}

		public void actionPerformed(ActionEvent e) {
			String command = printerProperties.getDataObject().getAction();
			if (command.equals(PrintConfiguration.EXPORT)) {
				export();
			} else if (command.equals(PrintConfiguration.EXPORT_AND_PRINT)) {
				exportAndPrint();
			} else {
				pipe();
			}
			setVisible(false);
		}
	};

	private File export() {
		boolean export = true;
		File exportFile = printerProperties.getDataObject().getExportFile();
		if (exportFile.exists()) {
			export = ui.confirm(exportFile.getName()
					+ " already exists. Overwrite it?");
		}
		if (export) {
			OutputStream stream = null;
			try {
				stream = new FileOutputStream(exportFile);
				exporter.exportTo(new FileOutputStream(exportFile), fileType);
				return exportFile;
			} catch (IOException e) {
				ui.errorMessage(e, null, null);
				return null;
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException ignored) {
						/* ignored */
					}
				}
			}
		}
		return null;

	}

	private void exportAndPrint() {
		File exportFile = export();
		if (exportFile != null) {
			String command = printerProperties.getDataObject().getCommand()
					+ " " + exportFile.getAbsolutePath();
			try {
				Process proc = Runtime.getRuntime().exec(command);
				proc.waitFor();
				if (printerProperties.getDataObject().isEraseExportFile()) {
					exportFile.delete();
				}
			} catch (IOException e) {
				ui.errorMessage(e, null, "Invocation of\n" + command
						+ "\nfailed.");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void pipe() {
		OutputStream stream = null;
		try {
			Process process = Runtime.getRuntime().exec(
					printerProperties.getDataObject().getCommand());
			stream = new BufferedOutputStream(process.getOutputStream());
			exporter.exportTo(stream, fileType);
		} catch (IOException e) {
			ui.errorMessage(e, null, "Piping to printer command failed.");

		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					/* ignored */
				}
			}
		}
	}

	private void reinitialize() {
		Configuration configuration = tab.getConfiguration().getDataObject();
		DiagramDataProvider provider = tab.getProvider();
		exporter = new MultipageExporter(printerProperties.getDataObject(),
				provider, configuration);
		try {
			exporter.init();
		} catch (RuntimeException re) {
			throw re;
		} catch (SemanticError se) {
			/* ignored */
		} catch (SyntaxError se) {
			/* ignored */
		}
		int scale = (int) (100 * exporter.getScale());
		scaleLabel.setText("Zoom factor: " + scale + " %");

		preview.setViewportView(exporter);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String property = evt.getPropertyName();
		if (!property.equals("command") && !property.equals("exportFile")
				&& !property.equals("commandFile")) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					reinitialize();
				}
			});
		}
		if (property.equals("command")) {
			PrintConfiguration prop = printerProperties.getDataObject();
			String command = prop.getAction();
			if (command.equals(PrintConfiguration.EXPORT_AND_PRINT)) {
				prop.setExportFile(new File(System
						.getProperty("java.io.tmpdir"), "temp.pdf"));
			} else if (command.equals(PrintConfiguration.EXPORT)) {
				setToCurrentFile();
			}
		}
		if (property.equals("exportFile")) {
			PrintConfiguration prop = printerProperties.getDataObject();
			if (prop.getExportFile().isDirectory()) {
				if (ui.getCurrentFile() != null) {
					File ef = UIUtilities.affixType(ui.getCurrentFile(), "pdf");
					prop.setExportFile(new File(prop.getExportFile(), ef
							.getName()));
				}
			}
		}
	}

	public void applyConfiguration() {
		/* nothing to do, buttons are not visible, so this is never called */

	}

	public void cancelConfiguration() {
		/* nothing to do, buttons are not visible, so this is never called */

	}
}
