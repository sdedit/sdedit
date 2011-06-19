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

package net.sf.sdedit.ui;

import java.io.File;
import java.net.URL;

import javax.swing.Action;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.ui.components.buttons.Activator;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.ConfigurationAction;
import net.sf.sdedit.ui.impl.DiagramTab;
import net.sf.sdedit.ui.impl.DiagramTextTab;
import net.sf.sdedit.ui.impl.TabContainer;

/**
 * Specifies the methods required of a (multi-tabbed) user interface for the
 * Quick Sequence Diagram Editor.
 * 
 * @author Markus Strauch
 * 
 */
public interface UserInterface {
	/**
	 * Adds a listener to this UserInterface.
	 * 
	 * @param listener
	 *            a listener for the UserInterface
	 */
	public void addListener(UserInterfaceListener listener);

	/**
	 * Adds a choosable component to the user interface such that on choosing it
	 * the given action is performed.
	 * 
	 * @param category
	 *            a string denoting the category of the action
	 * @param action
	 *            a performable action
	 * @param activator
	 *            an {@linkplain Activator} that decides whether the action
	 *            resp. its associated button is to be enabled
	 */
	public void addAction(String category, Action action, Activator activator);

	public void addCategory(String category, String icon);

	public void removeAction(String category, Action action);

	public void addConfigurationAction(String category,
			ConfigurationAction<?> action, Activator activator);
	
	/**
	 * Sets the action that is to be performed when the user quits.
	 * 
	 * @param action
	 *            the action to be performed when the user quits
	 */
	public void setQuitAction(Action action);

	/**
	 * Asks the user to confirm something or to cancel the process that lead to
	 * the point where something must be confirmed.
	 * 
	 * @param message
	 *            a message describing what is to be confirmed
	 * @return 1 if the user confirms, 0 if the user disagrees, -1 for cancel
	 */
	public int confirmOrCancel(String message);

	/**
	 * Asks the user for confirmation.
	 * 
	 * @param message
	 *            a message describing what is to be confirmed
	 * @return true iff the user confirms
	 */
	public boolean confirm(String message);

	/**
	 * Asks the user to type some string into an input dialog.
	 * 
	 * @param question
	 *            the question to which the string to be typed is an answer
	 * @param initialValue
	 *            the initial string that is suggested as an answer
	 * @return the string typed in by the user
	 */
	public String getString(String question, String initialValue);

	/**
	 * Shows a window where the preferences can be set
	 * 
	 * @param conf
	 *            the local configuration to be used or null if global
	 *            preferences should be made
	 * 
	 */
	public void configure(Bean<Configuration> conf);

	/**
	 * Displays a message to the user.
	 * 
	 * @param msg
	 *            a message
	 */
	public void message(String msg);

	/**
	 * Makes the user interface visible.
	 */
	public void showUI();

	public void errorMessage(Throwable throwable, String caption, String header);

	/**
	 * Adds an action that can be quickly performed (by just a single click, for
	 * instance).
	 * 
	 * @param quickAction
	 *            an action that can be quickly performed
	 */
	public void addToToolbar(Action action, Activator activator);

	public void addToolbarSeparator();
	
	public void addDefaultTab ();

	/**
	 * Adds a new tab to the user interface which becomes the tab that is
	 * currently selected.
	 * 
	 * @param title
	 *            the title of the tab
	 * @param configuration
	 *            the configuration to be used for the diagram that is displayed
	 *            by the tab (typically a default configuration for empty tabs
	 *            or a loaded configuration for tabs that show diagrams loaded
	 *            from files)
	 * @return the actual unique title of the newly added tab (may differ from
	 *         the original title)
	 */
	public DiagramTextTab addDiagramTextTab(String title,
			Bean<Configuration> configuration, boolean selectIt);

	public String addTab(Tab tab, boolean selectIt);

	/**
	 * Removes the current tab. If the parameter <tt>check</tt> is <tt>true</tt>
	 * and there is no other tab open, the tab cannot be removed.
	 * 
	 * @param check
	 *            flag denoting whether it is to be checked if the
	 *            tab can be closed
	 */
	public boolean closeCurrentTab(boolean check);

	/**
	 * Returns the file that is associated to the text in the current tab, may
	 * be <tt>null</tt> if no such file exists.
	 * 
	 * @return the file that is associated to the text in the current tab
	 */
	public File getCurrentFile();

	/**
	 * Displays a help page.
	 * 
	 * @param title
	 *            the title of the tab where the help page is shown
	 * @param path
	 *            the path to the help document (for example /foo/bar/help.html
	 *            is the path to help.html in the package foo.bar
	 * 
	 */
	public void help(String title, String file, boolean advanced);

	/**
	 * Returns the number of tabs currently open.
	 * 
	 * @return the number of tabs that are currently open
	 */
	public int getNumberOfTabs();

	/**
	 * Shows an about-dialog with a content found at the URL given.
	 * 
	 * @param aboutURL
	 *            a URL with the content of the about dialog
	 */
	public void showAboutDialog(URL aboutURL);

	public void showPrintDialog(String filetype, DiagramTab tab);

	/**
	 * Selects the first tab that shows a diagram associated to the given file
	 * or does nothing, if no such tab exists.
	 * 
	 * @param file
	 *            a diagram file
	 * @return flag denoting if an appropriate tab has been found
	 */
	public boolean selectTabWith(File file);

	public boolean selectTab(Tab tab);
	
	public TabContainer getTabContainer ();

	/**
	 * This method is called when the application is exited. It is <i>not</i>
	 * supposed to call <tt>System.exit()</tt>.
	 */
	public void exit();

	public Tab currentTab();

	public String getOption(String text, String... options);
}
