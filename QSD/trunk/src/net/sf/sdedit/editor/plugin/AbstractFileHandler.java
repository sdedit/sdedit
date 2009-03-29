package net.sf.sdedit.editor.plugin;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.sf.sdedit.editor.Editor;
import net.sf.sdedit.ui.Tab;
import net.sf.sdedit.ui.UserInterface;
import net.sf.sdedit.ui.impl.LookAndFeelManager;
import net.sf.sdedit.ui.impl.UserInterfaceImpl;
import net.sf.sdedit.util.UIUtilities;
import net.sf.sdedit.util.Utilities;

public abstract class AbstractFileHandler implements FileHandler {

	private static JFileChooser fileChooser;

	private static boolean sync;

	static {
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));
		sync = false;
	}

	private File currentFile;

	protected AbstractFileHandler() {

	}
	
	// TODO
	protected UserInterfaceImpl getUI () {
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
					fileChooser.setCurrentDirectory(parent);
				}
			}
			sync = true;
		}
	}

	private static abstract class MyFileFilter extends FileFilter {
		String suffix;
	}

	protected Component getComponent() {
		Tab tab = Editor.getEditor().getUI().currentTab();
		return tab;
	}

	/**
	 * @see net.sf.sdedit.editor.plugin.FileHandler#loadFile(java.net.URL)
	 */
	public void loadFile(URL url, UserInterface ui) throws IOException {
		Tab tab = _loadFile(url);
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
		addTabToUI (file, tab, ui);
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
								file.getName()
										+ " already exists.\nDo you want to overwrite it?");
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
		fileChooser.setMultiSelectionEnabled(multiple);

		for (FileFilter fileFilter : fileChooser.getChoosableFileFilters()) {
			if (fileFilter instanceof MyFileFilter) {
				fileChooser.removeChoosableFileFilter(fileFilter);
			}
		}

		if (filter.length > 0) {
			for (int i = 0; i < filter.length; i += 2) {
				final String description = filter[i];
				final String suffix = filter[i + 1].toLowerCase();
				MyFileFilter fileFilter = new MyFileFilter() {

					@Override
					public boolean accept(File f) {
						if (f.isDirectory()) {
							return true;
						}
						String name = f.getName().toLowerCase();
						return name.endsWith(suffix);
					}

					@Override
					public String getDescription() {
						return description;
					}
				};
				fileFilter.suffix = suffix;
				fileChooser.addChoosableFileFilter(fileFilter);
			}

			if (file != null) {
				int dot = file.lastIndexOf('.');
				if (dot >= 0) {
					String type = file.substring(dot + 1);
					for (FileFilter _filter : fileChooser
							.getChoosableFileFilters()) {
						if (_filter instanceof MyFileFilter) {
							if (((MyFileFilter) _filter).suffix.equals(type)) {
								fileChooser.setFileFilter(_filter);
								break;
							}

						}
					}
				}
			}
		}
		fileChooser.setDialogTitle(message);
		int ret;
		if (open) {
			ret = fileChooser.showOpenDialog(getComponent());
		} else {
			ret = fileChooser.showSaveDialog(getComponent());
		}
		File[] files;
		if (ret == JFileChooser.APPROVE_OPTION) {
			if (multiple) {
				if (fileChooser.getSelectedFiles() == null
						|| fileChooser.getSelectedFiles().length == 0) {
					files = null;
				} else {
					files = fileChooser.getSelectedFiles();
				}
			} else {
				File selectedFile = fileChooser.getSelectedFile();
				if (!open) {
					FileFilter selectedFilter = fileChooser.getFileFilter();
					if (selectedFilter instanceof MyFileFilter) {
						String type = ((MyFileFilter) selectedFilter).suffix;
						selectedFile = UIUtilities
								.affixType(selectedFile, type);
					}
				}

				files = new File[] { selectedFile };
			}
		} else {
			files = null;
		}
		return files;
	}
}
