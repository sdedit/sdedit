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

package net.sf.sdedit.editor;

import static net.sf.sdedit.editor.Shortcuts.CLEAR;
import static net.sf.sdedit.editor.Shortcuts.CLOSE;
import static net.sf.sdedit.editor.Shortcuts.CLOSE_ALL;
import static net.sf.sdedit.editor.Shortcuts.DIAGRAM_CONFIGURATION;
import static net.sf.sdedit.editor.Shortcuts.FILTER;
import static net.sf.sdedit.editor.Shortcuts.FULL_SCREEN;
import static net.sf.sdedit.editor.Shortcuts.GLOBAL_CONFIGURATION;
import static net.sf.sdedit.editor.Shortcuts.HELP;
import static net.sf.sdedit.editor.Shortcuts.NARROW;
import static net.sf.sdedit.editor.Shortcuts.NEW;
import static net.sf.sdedit.editor.Shortcuts.PRINT;
import static net.sf.sdedit.editor.Shortcuts.QUIT;
import static net.sf.sdedit.editor.Shortcuts.REDO;
import static net.sf.sdedit.editor.Shortcuts.REDRAW;
import static net.sf.sdedit.editor.Shortcuts.SPLIT_LEFT_RIGHT;
import static net.sf.sdedit.editor.Shortcuts.SPLIT_TOP_BOTTOM;
import static net.sf.sdedit.editor.Shortcuts.UNDO;
import static net.sf.sdedit.editor.Shortcuts.WIDEN;
import static net.sf.sdedit.editor.Shortcuts.getShortcut;
import static net.sf.sdedit.ui.components.buttons.ManagedAction.ICON_NAME;

import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.BadLocationException;

import net.sf.sdedit.Constants;
import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.drawable.Arrow;
import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.drawable.Rectangle;
import net.sf.sdedit.message.ForwardMessage;
import net.sf.sdedit.multipage.MultipageExporter;
import net.sf.sdedit.server.Exporter;
import net.sf.sdedit.ui.Tab;
import net.sf.sdedit.ui.UserInterface;
import net.sf.sdedit.ui.components.PrettyPrinter;
import net.sf.sdedit.ui.components.buttons.Activator;
import net.sf.sdedit.ui.components.buttons.ManagedAction;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.impl.DiagramTab;
import net.sf.sdedit.ui.impl.DiagramTextTab;
import net.sf.sdedit.util.Utilities;

@SuppressWarnings("serial")
public final class Actions implements Constants {

	private Editor editor;

	private UserInterface ui;

	public final Activator canConfigureActivator;

	public final Activator nonEmptyDiagramActivator;

	public final Activator noDiagramErrorActivator;

	public final Activator canNarrowActivator;

	public final Activator textTabActivator;

	public final Activator diagramTabActivator;

	public final Activator verticalSplitPossibleActivator;

	public final Activator horizontalSplitPossibleActivator;

	public final Activator supportsFullScreenActivator;

	public final Activator canCloseActivator;

	public final Activator nextActivator;

	public final Activator previousActivator;

	public final Activator homeActivator;

	final Action clearAction;

	final Action configureGloballyAction;

	final Action configureDiagramAction;

	final Action helpAction;

	final Action helpOnMultithreadingAction;

	final Action asyncNotesAction;

	final Action newDiagramAction;

	final Action closeAllAction;

	final Action closeTabAction;

	final Action quitAction;

	final Action redrawAction;

	final Action widenAction;

	final Action narrowAction;

	final Action redoAction;

	final Action undoAction;

	final Action showAboutDialogAction;

	final Action fullScreenAction;

	final Action filterAction;

	final Action serverAction;

	final Action splitLeftRightAction;

	final Action splitTopBottomAction;

	final Action copyBitmapToClipBoardAction;

	final Action copyVectorGraphicsToClipBoardAction;

	final Action previousAction;

	final Action nextAction;

	final Action homeAction;

	final Action prettyPrintAction;

	Actions(Editor _editor) {
		this.editor = _editor;
		ui = editor.getUI();
		if (ui == null) {
			throw new IllegalStateException("ui reference must not be null");
		}

		canConfigureActivator = new TabActivator<DiagramTab>(DiagramTab.class,
				ui) {
			@Override
			protected boolean _isEnabled(DiagramTab tab) {
				return tab.getConfiguration() != null;
			}
		};

		canCloseActivator = new TabActivator<Tab>(Tab.class, ui) {
			@Override
			protected boolean _isEnabled(Tab tab) {
				return tab.canClose();
			}
		};

		nonEmptyDiagramActivator = new TabActivator<DiagramTab>(
				DiagramTab.class, ui) {
			@Override
			protected boolean _isEnabled(DiagramTab tab) {
				return !tab.isEmpty();
			}
		};

		noDiagramErrorActivator = new TabActivator<DiagramTab>(
				DiagramTab.class, ui) {
			@Override
			protected boolean _isEnabled(DiagramTab tab) {
				return !tab.isEmpty() && tab.getDiagramError() == null;
			}
		};

		canNarrowActivator = new TabActivator<DiagramTab>(DiagramTab.class, ui) {
			@Override
			protected boolean _isEnabled(DiagramTab tab) {
				return tab.getConfiguration().getDataObject().getGlue() > 0;
			}
		};

		textTabActivator = new TabActivator<DiagramTextTab>(
				DiagramTextTab.class, ui) {
			@Override
			protected boolean _isEnabled(DiagramTextTab tab) {
				return true;
			}
		};

		diagramTabActivator = new TabActivator<DiagramTab>(DiagramTab.class, ui) {
			@Override
			protected boolean _isEnabled(DiagramTab tab) {
				return true;
			}
		};

		verticalSplitPossibleActivator = new TabActivator<DiagramTextTab>(
				DiagramTextTab.class, ui) {
			@Override
			protected boolean _isEnabled(DiagramTextTab tab) {
				return !tab.getConfiguration().getDataObject()
						.isVerticallySplit();
			}
		};

		horizontalSplitPossibleActivator = new TabActivator<DiagramTextTab>(
				DiagramTextTab.class, ui) {
			@Override
			protected boolean _isEnabled(DiagramTextTab tab) {
				return tab.getConfiguration().getDataObject()
						.isVerticallySplit();
			}
		};

		supportsFullScreenActivator = new TabActivator<Tab>(Tab.class, ui) {
			@Override
			protected boolean _isEnabled(Tab tab) {
				return tab.supportsFullScreen();
			}
		};

		nextActivator = new TabActivator<Tab>(Tab.class, ui) {
			@Override
			protected boolean _isEnabled(Tab tab) {
				return ui.getTabContainer().nextTabExists();
			}
		};

		previousActivator = new TabActivator<Tab>(Tab.class, ui) {
			@Override
			protected boolean _isEnabled(Tab tab) {
				return ui.getTabContainer().previousTabExists();
			}
		};

		homeActivator = new TabActivator<Tab>(Tab.class, ui) {
			@Override
			protected boolean _isEnabled(Tab tab) {
				return tab.canGoHome();
			}
		};

		clearAction = new TabAction<DiagramTextTab>(DiagramTextTab.class, ui) {
			{
				putValue(ICON_NAME, "eraser");
				putValue(Action.SHORT_DESCRIPTION, "Erase the source code");
				putValue(Action.NAME, getShortcut(CLEAR) + "&Erase code");
			}

			protected void _actionPerformed(DiagramTextTab tab, ActionEvent e) {
				tab.setCode("");
			}
		};

		configureGloballyAction = new AbstractAction() {
			{
				putValue(ICON_NAME, "globalsettings");
				putValue(ManagedAction.ID, "GLOBAL_SETTINGS");
				putValue(Action.SHORT_DESCRIPTION, "Edit global preferences");
				putValue(Action.NAME, getShortcut(GLOBAL_CONFIGURATION)
						+ "&Global preferences...");
			}

			public void actionPerformed(ActionEvent e) {
				editor.getUI().configure(null);
			}
		};

		configureDiagramAction = new TabAction<DiagramTab>(DiagramTab.class, ui) {
			{
				putValue(ICON_NAME, "configure");
				putValue(ManagedAction.ID, "LOCAL_SETTINGS");
				putValue(Action.SHORT_DESCRIPTION, "Edit diagram preferences");
				putValue(Action.NAME, getShortcut(DIAGRAM_CONFIGURATION)
						+ "&Diagram preferences...");
			}

			protected void _actionPerformed(DiagramTab tab, ActionEvent e) {
				editor.getUI().configure(tab.getConfiguration());
			}
		};

		helpAction = new AbstractAction() {
			{
				putValue(ICON_NAME, "help");
				putValue(ManagedAction.ID, "HELP");
				putValue(Action.SHORT_DESCRIPTION,
						"Display a comprehensive help page");
				putValue(Action.NAME, Shortcuts.getShortcut(HELP) + "&Help");
			}

			public void actionPerformed(ActionEvent e) {
				editor.getUI().help("Help", "help", true);
			}
		};

		helpOnMultithreadingAction = new AbstractAction() {
			{
				putValue(ICON_NAME, "help");
				putValue(Action.SHORT_DESCRIPTION,
						"Show a help page dedicated to multithreading");
				putValue(Action.NAME, "&Multithreading help");
			}

			public void actionPerformed(ActionEvent e) {
				editor.getUI().help("Multithreading help",
						"multithreading_help", false);
			}
		};

		asyncNotesAction = new AbstractAction() {
			{
				putValue(ICON_NAME, "help");
				putValue(Action.SHORT_DESCRIPTION,
						"Show a help page containing notes on asynchronous messages");
				putValue(Action.NAME, "&Notes on asynchronous messages");
			}

			public void actionPerformed(ActionEvent e) {
				editor.getUI().help("Notes on asynchronous messages", "async",
						false);
			}
		};

		newDiagramAction = new AbstractAction() {
			{
				putValue(ICON_NAME, "new");
				putValue(ManagedAction.ID, "NEW_DIAGRAM");
				putValue(Action.NAME, getShortcut(NEW) + "&New diagram");
				putValue(Action.SHORT_DESCRIPTION,
						"Add a tab for a new diagram");
			}

			public void actionPerformed(ActionEvent e) {
				Bean<Configuration> conf = ConfigurationManager
						.createNewDefaultConfiguration();
				editor.getUI().addDiagramTextTab("untitled", conf, true);
			}

		};

		closeAllAction = new AbstractAction() {
			{
				putValue(ICON_NAME, "close");
				putValue(Action.NAME, getShortcut(CLOSE_ALL) + "Close All");
				putValue(Action.SHORT_DESCRIPTION, "Close all tabs");
			}

			public void actionPerformed(ActionEvent e) {
			    editor.closeAll();
			}
		};

		closeTabAction = new TabAction<Tab>(Tab.class, ui) {
			{
				putValue(ICON_NAME, "close");
				putValue(Action.NAME, getShortcut(CLOSE) + "&Close");
				putValue(Action.SHORT_DESCRIPTION, "Close the current tab");
			}

			@Override
			protected void _actionPerformed(Tab tab, ActionEvent e) {
				tab.close(true);
			}
		};

		quitAction = new AbstractAction() {
			{
				putValue(ICON_NAME, "exit");
				putValue(Action.SHORT_DESCRIPTION, "Quit the application");
				putValue(Action.NAME, getShortcut(QUIT) + "&Quit");
			}

			public void actionPerformed(ActionEvent e) {
				editor.quit();
			}
		};

		redrawAction = new TabAction<DiagramTab>(DiagramTab.class, ui) {
			{
				putValue(ICON_NAME, "reload");
				putValue(ManagedAction.ID, "RELOAD");
				putValue(Action.NAME, getShortcut(REDRAW) + "Re&draw");
				putValue(Action.SHORT_DESCRIPTION, "Redraw the diagram");
			}

			protected void _actionPerformed(DiagramTab tab, ActionEvent e) {
				tab.refresh(true);
			}
		};

		widenAction = new TabAction<DiagramTab>(DiagramTab.class, ui) {
			{
				putValue(ICON_NAME, "widen");
				putValue(Action.SHORT_DESCRIPTION, "Widen the diagram");
				putValue(Action.NAME, getShortcut(WIDEN) + "&Widen");
			}

			protected void _actionPerformed(DiagramTab tab, ActionEvent e) {
				Configuration conf = tab.getConfiguration().getDataObject();
				if (conf != null) {
					conf.setGlue(conf.getGlue()
							+ ConfigurationManager.getGlobalConfiguration()
									.getGlueChangeAmount());
				}
			}
		};

		narrowAction = new TabAction<DiagramTab>(DiagramTab.class, ui) {
			{
				putValue(ICON_NAME, "narrow");
				putValue(Action.SHORT_DESCRIPTION, "Narrow the diagram");
				putValue(Action.NAME, getShortcut(NARROW) + "&Narrow");
			}

			protected void _actionPerformed(DiagramTab tab, ActionEvent e) {
				Configuration conf = tab.getConfiguration().getDataObject();
				if (conf != null) {
					int glue = Math.max(0, conf.getGlue()
							- ConfigurationManager.getGlobalConfiguration()
									.getGlueChangeAmount());
					conf.setGlue(glue);
				}
			}
		};

		redoAction = new TabAction<DiagramTextTab>(DiagramTextTab.class, ui) {
			{
				putValue(Action.NAME, getShortcut(REDO) + "&Redo");
				putValue(Action.SHORT_DESCRIPTION,
						"Redo the typing that has most recently been undone");

				putValue(ICON_NAME, "redo");
			}

			protected void _actionPerformed(DiagramTextTab tab, ActionEvent evt) {
				tab.redo();
			}
		};

		undoAction = new TabAction<DiagramTextTab>(DiagramTextTab.class, ui) {
			{
				putValue(Action.NAME, getShortcut(UNDO) + "&Undo");
				putValue(Action.SHORT_DESCRIPTION,
						"Undo the typing that has most recently been done");

				putValue(ICON_NAME, "undo");
			}

			protected void _actionPerformed(DiagramTextTab tab, ActionEvent evt) {
				tab.undo();
			}
		};

		showAboutDialogAction = new AbstractAction() {
			{
				putValue(Action.NAME, "About");
				putValue(ICON_NAME, "help");
			}

			public void actionPerformed(ActionEvent evt) {
				editor.getUI().showAboutDialog(
						Utilities.getResource("about.html"));
			}
		};

		fullScreenAction = new TabAction<Tab>(Tab.class, ui) {
			{
				putValue(Action.NAME, getShortcut(FULL_SCREEN) + "&Full screen");
				putValue(ICON_NAME, "fullscreen");
				putValue(ManagedAction.ID, "FULL_SCREEN");
				putValue(Action.SHORT_DESCRIPTION,
						"Display the diagram in full-screen mode");
			}

			protected void _actionPerformed(Tab tab, ActionEvent evt) {
				if (tab.supportsFullScreen()) {
					tab.fullScreen();
				}
			}
		};

		filterAction = new TabAction<DiagramTextTab>(DiagramTextTab.class, ui) {
			{
				putValue(Action.NAME, getShortcut(FILTER) + "&Filter...");
				putValue(ICON_NAME, "filter");
				putValue(Action.SHORT_DESCRIPTION,
						"Filter the (selected) text through a command");
			}

			protected void _actionPerformed(DiagramTextTab tab, ActionEvent evt) {
				tab.toggleFilterMode();
			}
		};

		serverAction = new AbstractAction() {
			{
				putValue(Action.NAME, "Start/stop &RT server...");
				putValue(ICON_NAME, "server");
				putValue(Action.SHORT_DESCRIPTION,
						"Start or stop a server that receives diagram specifications through sockets");
			}

			public void actionPerformed(ActionEvent evt) {
				if (editor.isServerRunning()) {
					if (editor.getUI().confirmOrCancel(
							"Stop real-time diagram server?") == 1) {
						editor.shutDownServer();
					}
					return;
				}
				String port = String.valueOf(ConfigurationManager
						.getGlobalConfiguration().getRealtimeServerPort());
				port = editor
						.getUI()
						.getString(
								"Enter the port where"
										+ " the real-time diagram\nserver should listen (0 for any free port):",
								port);
				if (port == null || port.equals("")) {
					return;
				}
				try {
					int p = Integer.parseInt(port);
					int actualPort = editor.startRealtimeServer(p);
					ConfigurationManager.getGlobalConfiguration()
							.setRealtimeServerPort(p);
					editor.getUI().message(
							"Started real-time diagram server@localhost:"
									+ actualPort);
				} catch (Exception e) {
					editor
							.getUI()
							.errorMessage(e, null,
									"The real-time diagram server could not be started.");
				}
			}
		};

		splitLeftRightAction = new TabAction<DiagramTextTab>(
				DiagramTextTab.class, ui) {
			{
				putValue(Action.NAME, getShortcut(SPLIT_LEFT_RIGHT)
						+ "Split view left/right");
				putValue(ManagedAction.ID, "SPLIT_LEFT_RIGHT");
				putValue(ICON_NAME, "view_left_right");
				putValue(Action.SHORT_DESCRIPTION, "Split view left/right");
			}

			protected void _actionPerformed(DiagramTextTab tab, ActionEvent evt) {
				tab.layout(0);
			}
		};

		splitTopBottomAction = new TabAction<DiagramTextTab>(
				DiagramTextTab.class, ui) {
			{
				putValue(Action.NAME, getShortcut(SPLIT_TOP_BOTTOM)
						+ "Split view top/bottom");
				putValue(ManagedAction.ID, "SPLIT_TOP_BOTTOM");
				putValue(ICON_NAME, "view_top_bottom");
				putValue(Action.SHORT_DESCRIPTION, "Split view top/bottom");
			}

			protected void _actionPerformed(DiagramTextTab tab, ActionEvent evt) {
				tab.layout(1);
			}
		};

		copyBitmapToClipBoardAction = new TabAction<DiagramTab>(
				DiagramTab.class, ui) {
			{
				putValue(Action.NAME, "Copy diagram (as bitmap)");
				putValue(ManagedAction.ID, "COPY_BITMAP_TO_CLIPBOARD");
				putValue(ICON_NAME, "image");
				putValue(Action.SHORT_DESCRIPTION, "Copy diagram (as bitmap)");

			}

			protected void _actionPerformed(DiagramTab tab, ActionEvent evt) {
				tab.copyToClipboard(DataFlavor.imageFlavor);
			}
		};

		copyVectorGraphicsToClipBoardAction = new TabAction<DiagramTab>(
				DiagramTab.class, ui) {
			{
				putValue(Action.NAME, "Copy diagram (as vector graphics)");
				putValue(ManagedAction.ID, "COPY_VG_TO_CLIPBOARD");
				putValue(ICON_NAME, "image");
				putValue(Action.SHORT_DESCRIPTION,
						"Copy diagram (as vector graphics)");

			}

			protected void _actionPerformed(DiagramTab tab, ActionEvent evt) {
				tab.copyToClipboard(DiagramTab.EMF_FLAVOR);
			}
		};

		nextAction = new TabAction<Tab>(Tab.class, ui) {

			{
				putValue(Action.NAME, "Go to next tab");
				putValue(ManagedAction.ID, "GO_TO_NEXT_TAB");
				putValue(ICON_NAME, "next");
				putValue(Action.SHORT_DESCRIPTION, "Go to next tab");
			}

			protected void _actionPerformed(Tab tab, ActionEvent evt) {
				ui.getTabContainer().goToNextTab();
			}

		};

		previousAction = new TabAction<Tab>(Tab.class, ui) {

			{
				putValue(Action.NAME, "Go to previous tab");
				putValue(ManagedAction.ID, "GO_TO_PREVIOUS_TAB");
				putValue(ICON_NAME, "previous");
				putValue(Action.SHORT_DESCRIPTION, "Go to previous tab");
			}

			protected void _actionPerformed(Tab tab, ActionEvent evt) {
				ui.getTabContainer().goToPreviousTab();
			}

		};

		homeAction = new TabAction<Tab>(Tab.class, ui) {

			{
				putValue(Action.NAME, "Go home");
				putValue(ManagedAction.ID, "GO_HOME");
				putValue(ICON_NAME, "gohome");
				putValue(Action.SHORT_DESCRIPTION, "Go home");
			}

			protected void _actionPerformed(Tab tab, ActionEvent evt) {
				tab.goHome();
			}

		};

		prettyPrintAction = new TabAction<DiagramTextTab>(DiagramTextTab.class,
				ui) {

			{
				putValue(Action.NAME, "Pretty print");
				putValue(ManagedAction.ID, "PRETTY_PRINT");
				putValue(ICON_NAME, "prettyprint");
				putValue(Action.SHORT_DESCRIPTION, "Pretty print");
			}

			@Override
			protected void _actionPerformed(DiagramTextTab tab, ActionEvent e) {
				final Map<Integer, Integer> levelMap = new HashMap<Integer, Integer>();
				for (Drawable drawable : tab.getDiagram().getPaintDevice()) {
					if (drawable instanceof Arrow) {
						Arrow arrow = (Arrow) drawable;
						if (arrow.getMessage() instanceof ForwardMessage) {
							levelMap.put((Integer) tab.getDiagram()
										.getStateForDrawable(arrow), ((ForwardMessage) arrow.getMessage()).getLevel());
						}
					}
				}
				
				PrettyPrinter prettyPrinter = new PrettyPrinter() {

					public int getAlign(int caretPosition) {
						Integer callLevel = (Integer) levelMap
								.get(caretPosition);
						if (callLevel != null) {
							return 2*callLevel;
						}
						return -1;
					}

				};

				try {
					tab.getTextArea().prettyPrint(prettyPrinter);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}

			}

		};

	}

	final Action getExampleAction(final String name, final String file) {

		return new AbstractAction() {
			{
				putValue(Action.NAME, name);
			}

			public void actionPerformed(ActionEvent e) {
				try {
					URL url = getClass().getResource(
							"/net/sf/sdedit/examples/" + file);
					DiagramTextTab tab = (DiagramTextTab) editor.load(url);
					if (tab != null) {
					    // prevent user from overwriting example file
					    tab.setFile(null); 
					    tab.setTitle(file);
					}
				
				} catch (RuntimeException re) {
					throw re;
				}

				catch (Exception ex) {
					editor.getUI().errorMessage(ex, null,
							"Loading example from classpath failed");
				}
			}
		};
	}

	final Action getRecentFileAction(final String fileName) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, new File(fileName).getName());
				putValue(Action.SHORT_DESCRIPTION, new File(fileName)
						.getAbsolutePath());

			}

			public void actionPerformed(ActionEvent e) {
				try {
					editor.load(new File(fileName).toURI().toURL());
				} catch (RuntimeException re) {
					throw re;
				} catch (Exception ex) {
					Editor.getEditor().getUI().errorMessage(ex, null, null);
				}
			}
		};
	}

	final Action getExportAction() {
		return Exporter.isAvailable() ? new ExportAction(editor) : null;
	}

	final Action getPrintAction(final String filetype) {
		if (!MultipageExporter.isAvailable()) {
			return null;
		}
		return new TabAction<DiagramTab>(DiagramTab.class, ui) {
			{
				putValue(ICON_NAME, filetype);
				putValue(ManagedAction.ID, "MULTI_PAGE_EXPORT");
				putValue(Action.SHORT_DESCRIPTION,
						"Prints or exports the diagram in multi-page "
								+ filetype.toUpperCase() + " format");
				putValue(Action.NAME, getShortcut(PRINT)
						+ "&Print/export multi-page " + filetype.toUpperCase()
						+ "...");
			}

			protected void _actionPerformed(DiagramTab tab, ActionEvent e) {
				ui.showPrintDialog(filetype, tab);
			}
		};
	}
}
