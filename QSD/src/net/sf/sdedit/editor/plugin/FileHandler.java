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

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
	 * a {@linkplain Tab} for displaying and adds it to the user interface.
	 * 
	 * 
	 * 
	 * @param file
	 * @return the tab for displaying the file or null if the file cannot
	 * be loaded
	 * @throws IOException
	 */
	public Tab loadFile(URL file, UserInterface ui) throws IOException;

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
