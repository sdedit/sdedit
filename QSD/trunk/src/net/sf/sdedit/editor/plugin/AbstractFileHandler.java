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
package net.sf.sdedit.editor.plugin;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import net.sf.sdedit.editor.Editor;
import net.sf.sdedit.ui.Tab;
import net.sf.sdedit.ui.UserInterface;
import net.sf.sdedit.ui.impl.UserInterfaceImpl;
import net.sf.sdedit.util.Utilities;

public abstract class AbstractFileHandler implements FileHandler {

	public static JFileChooser FILE_CHOOSER;

	private static boolean sync;

	static {
		FILE_CHOOSER = new JFileChooser();
		FILE_CHOOSER.setCurrentDirectory(new File("."));
		sync = false;
	}

	private File currentFile;

	protected AbstractFileHandler() {

	}

	// TODO
	protected UserInterfaceImpl getUI() {
		return (UserInterfaceImpl) Editor.getEditor().getUI();
	}

	protected abstract boolean _saveFile(Tab tab, File file) throws IOException;

	protected abstract Tab _loadFile(URL file) throws IOException;

	protected String[] getFileTypesWithDescriptions() {
		String[] types = getFileTypes();
		String[] descriptions = getFileDescriptions();
		if (types.length != descriptions.length) {
			throw new IllegalArgumentException("The file handler defines "
					+ types.length + " file types, but " + descriptions.length
					+ " descriptions");
		}
		String[] result = new String[types.length * 2];
		for (int i = 0; i < types.length; i++) {
			result[i * 2] = descriptions[i];
			result[i * 2 + 1] = types[i];
		}
		return result;
	}

	private static void updateFileChooserDirectory(File file) {
		if (!sync) {
			if (file != null) {
				File parent = file.getParentFile();
				if (parent != null) {
					FILE_CHOOSER.setCurrentDirectory(parent);
				}
			}
			sync = true;
		}
	}

	protected Component getComponent() {
		Tab tab = Editor.getEditor().getUI().currentTab();
		return tab;
	}

	/**
	 * @see net.sf.sdedit.editor.plugin.FileHandler#loadFile(java.net.URL)
	 */
	public final Tab loadFile(URL url, final UserInterface ui)
			throws IOException {
		if (!"file".equals(url.getProtocol())
				|| !ui.selectTabWith(Utilities.toFile(url))) {

			ui.getTabContainer().disableTabHistory();
			final Tab tab = _loadFile(url);
			if (tab == null) {
				return null;
			}
			if (url.getProtocol().equals("file")) {
				tab.setFile(Utilities.toFile(url));
			}
			

			tab.setClean(true);

			addTabToUI(tab, ui);
			SwingUtilities.invokeLater(new Runnable() {
			    
				public void run() {
				    
				    // file processing is deferred (one may load a file in a tab
				    // and immediately thereafter set the file reference to null)
				    // see Actions.getExampleAction
		            if (tab.getFile() != null) {
		                updateFileChooserDirectory(tab.getFile());
		                if (tab.getFile().exists()) {
		                    Editor.getEditor().addToRecentFiles(tab.getFile().getAbsolutePath());
		                }
 		            }
					ui.getTabContainer().enableTabHistory();
					ui.selectTab(tab);
				}
			});
			return tab;
		}
		return null;

	}

	protected void addTabToUI(Tab tab, UserInterface ui) {
		ui.addTab(tab, true);
	}

	/**
	 * @see net.sf.sdedit.editor.plugin.FileHandler#save(net.sf.sdedit.ui.Tab,
	 *      boolean)
	 */
	public File save(Tab tab, boolean as) throws IOException {
		File file = null;
		if (!as) {
			file = tab.getFile();
		}
		if (file == null) {
			file = selectFileToSave();
		}
		if (file != null) {
			int confirmation = 1;
			if (file.exists() && !file.equals(tab.getFile())) {
				confirmation = tab
						.get_UI()
						.confirmOrCancel(
								"<html>"
										+ file.getName()
										+ " already exists.<br>Do you want to overwrite it?");
			}
			if (confirmation == 1) {
				updateFileChooserDirectory(file);
				currentFile = file;
				if (_saveFile(tab, file)) {
					tab.setFile(file);
					tab.setClean(true);
					Editor.getEditor().addToRecentFiles(file.getAbsolutePath());
					return file;
				}
			}
		}
		return null;
	}

	private String getFileName() {
		if (currentFile != null) {
			return currentFile.getName();
		}
		return "";
	}

	public File selectFileToOpen() {
		File[] files = getFiles(true, false, getOpenDescription());
		if (files != null && files.length > 0) {
			return files[0];
		}
		return null;
	}

	public File[] selectFilesToOpen() {
		return getFiles(true, true, getOpenDescription());
	}

	public File selectFileToSave() {
		File[] files = getFiles(false, false, getSaveAsDescription());
		if (files == null) {
			return null;
		}
		return files[0];
	}

	protected File[] getFiles(boolean open, boolean multiple, String message) {
		return getFiles(open, multiple, message, null, (String[]) null);
	}

	public File[] getFiles(boolean open, boolean multiple, String message,
			String file, String... filter) {
		if (file == null) {
			file = getFileName();
		}
		if (filter == null) {
			filter = getFileTypesWithDescriptions();
		}
		// if (file != null) {
		// fileChooser.setSelectedFile(new File(fileChooser
		// .getCurrentDirectory(), file));
		// }

		return Utilities.chooseFiles(FILE_CHOOSER, getComponent(), open,
				multiple, message, file, filter);
	}
}
