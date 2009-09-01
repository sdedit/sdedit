package net.sf.sdedit.editor.plugin;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
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
	public final void loadFile(URL url, final UserInterface ui)
			throws IOException {
		ui.getTabContainer().disableTabHistory();
		final Tab tab = _loadFile(url);
		if (tab == null) {
			return;
		}
		if (url.getProtocol().equals("file")) {
			tab.setFile(Utilities.toFile(url));
		}
		File file = tab.getFile();
		updateFileChooserDirectory(file);

		tab.setClean(true);
		if (file != null && file.exists()) {
			Editor.getEditor().addToRecentFiles(file.getAbsolutePath());
		}
		addTabToUI(file, tab, ui);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ui.getTabContainer().enableTabHistory();
				ui.selectTab(tab);
			}
		});

	}

	protected void addTabToUI(File file, Tab tab, UserInterface ui) {
		ui.addTab(tab);
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
