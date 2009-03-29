package net.sf.sdedit.editor.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;

import net.sf.sdedit.ui.Tab;
import net.sf.sdedit.ui.UserInterface;

/**
 * A <tt>FileHandler</tt> is responsible for loading and storing files
 * (diagrams, configurations, ...) used by a certain type of
 * {@linkplain Tab}. (See {@linkplain Tab#getFileHandler()})
 * 
 * @author Markus Strauch
 * 
 */
public interface FileHandler {

	public String[] getFileTypes();

	public String[] getFileDescriptions();

	public String getSaveAsDescription();

	public String getSaveAsActionName();

	public String getSaveAsShortCut();

	public String getOpenID();

	public String getOpenActionName();

	public String getOpenShortCut();

	public String getOpenDescription();

	public String getSaveActionName();

	public String getSaveShortCut();

	public String getSaveDescription();
	
	/**
	 * Opens a file chooser dialog where multiple files to be opened
	 * can be selected.
	 * 
	 * @return a (non-empty) array of files to be opened or null
	 */
	public File[] selectFilesToOpen();
	
	/**
	 * Opens a file chooser dialog where a single file to be loaded
	 * can be selected.
	 * 
	 * @return the file to be opened, or <tt>null</tt>
	 */
	public File selectFileToOpen();

	/**
	 * Opens a file chooser dialog where a file to be saved
	 * can be selected.
	 * 
	 * @return the file to be saved, or <tt>null</tt>
	 */
	public File selectFileToSave();

	/**
	 * Loads the contents of a file from the given URL, creates
	 * a {@linkplain Tab} for displaying and adds it to the user interface
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public void loadFile(URL file, UserInterface ui) throws IOException;

	/**
	 * Saves the contents of the given tab to a file.
	 * 
	 * @param tab
	 * @param as
	 *            a flag denoting if {@linkplain Tab#getFile()} (if present)
	 *            should be overwritten (save tab contents <i>as</i> a new file)
	 * @return a reference to the file that was saved or null if the operation
	 *         was aborted by the user
	 * @throws IOException
	 */
	public File save(Tab tab, boolean as) throws IOException;
	
	public File[] getFiles(boolean open, boolean multiple, String message, String file, String... filter);
		

	public boolean canLoad();

	public boolean canSave();

}
