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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.sf.sdedit.editor.Editor;
import net.sf.sdedit.editor.TabAction;
import net.sf.sdedit.ui.Tab;
import net.sf.sdedit.ui.UserInterface;
import net.sf.sdedit.ui.components.buttons.Activator;
import net.sf.sdedit.ui.components.buttons.ManagedAction;
import net.sf.sdedit.util.Utilities;

@SuppressWarnings("serial")
public class FileActionProvider {

	private Map<FileHandler, Map<String, Action>> actionMap;

	public FileActionProvider() {
		actionMap = new HashMap<FileHandler, Map<String, Action>>();
	}
	
	private Action openAction;

	public final Activator getOpenActivator =

	new Activator() {

		public boolean isEnabled() {
			return true;
			/*
			Tab tab = Editor.getEditor().getUI().currentTab();
			if (tab == null) {
				return false;
			}
			return tab.getFileHandler() != null && tab.getFileHandler().canLoad();
			*/
		}
	};

	public final Activator getSaveActivator =

	new Activator() {

		public boolean isEnabled() {
			Tab tab = Editor.getEditor().getUI().currentTab();
			if (tab == null) {
				return false;
			}
			return tab.getFileHandler() != null && tab.getFileHandler().canSave();
		}

	};

	public Action getOpenAction(UserInterface ui) {
		if (this.openAction == null) {
			openAction = makeOpenAction(ui);
		}
		return openAction;
	}

	public Action getSaveAction(FileHandler handler, UserInterface ui) {
		Action action = getAction(handler, "SAVE");
		if (action == null) {
			action = makeSaveAction(handler, ui);
			setAction(handler, "SAVE", action);
		}
		return action;
	}

	public Action getSaveAsAction(FileHandler handler, UserInterface ui) {
		Action action = getAction(handler, "SAVE_AS");
		if (action == null) {
			action = makeSaveAsAction(handler, ui);
			setAction(handler, "SAVE_AS", action);
		}
		return action;
	}

	private Action getAction(FileHandler fileHandler, String type) {
		Map<String, Action> types = actionMap.get(fileHandler);
		return types == null ? null : types.get(type);
	}

	private void setAction(FileHandler fileHandler, String type, Action action) {
		Map<String, Action> types = actionMap.get(fileHandler);
		if (types == null) {
			types = new HashMap<String, Action>();
			actionMap.put(fileHandler, types);
		}
		types.put(type, action);
	}

	private Action makeOpenAction(final UserInterface ui) {

		return new AbstractAction() {
			{
				putValue(ManagedAction.ICON_NAME, "open");
				putValue(ManagedAction.ID, "OPEN_FILE");
				putValue(ManagedAction.NEW_TEXT, "Open File");
				putValue(Action.SHORT_DESCRIPTION, "Open File");
				
				/*
				String actionName = handler.getOpenActionName();

				if (handler.getOpenShortCut() != null) {
					actionName = handler.getOpenShortCut() + actionName;
				}
				*/

				putValue(Action.NAME, "Open File");
			}

			public void actionPerformed(ActionEvent e) {
				
				List<String> filters = new ArrayList<String>();
				for (FileHandler h : Editor.getEditor().getFileHandlers()) {
					for (int j = 0; j < h.getFileDescriptions().length; j++) {
						filters.add(h.getFileDescriptions()[j]);
						filters.add(h.getFileTypes()[j]);
					}
				}

				File[] files = Utilities.chooseFiles(AbstractFileHandler.FILE_CHOOSER, ui.currentTab(), true,
						true, "Open file", null, filters.toArray(new String[filters.size()]));
				if (files != null) {
					for (File file : files) {
						try {
							Editor.getEditor().load(file.toURI().toURL());
						} catch (IOException e1) {
							ui.errorMessage(e1, null, null);
						} catch (URISyntaxException e1) {
							ui.errorMessage(e1, null, null);
						}						
					}
				}

			}

		};
	}

	private Action makeSaveAction(final FileHandler handler,
			final UserInterface ui) {
		return new TabAction<Tab>(Tab.class, ui) {
			{
				putValue(ManagedAction.ICON_NAME, "save");
				putValue(ManagedAction.ID, "SAVE");
				putValue(ManagedAction.NEW_TEXT, handler.getSaveActionName());
				putValue(Action.SHORT_DESCRIPTION, handler.getSaveDescription());
				String actionName = handler.getSaveActionName();
				if (handler.getSaveShortCut() != null) {
					actionName = handler.getSaveShortCut() + actionName;
				}
				putValue(Action.NAME, actionName);
			}

			protected void _actionPerformed(Tab tab, ActionEvent e) {
				try {
					handler.save(tab, false);
				} catch (IOException ex) {
					ex.printStackTrace();
					ui.errorMessage(ex, null, null);
				}
			}
		};
	}

	private Action makeSaveAsAction(final FileHandler handler,
			final UserInterface ui) {

		return new TabAction<Tab>(Tab.class, ui) {
			{
				putValue(ManagedAction.ICON_NAME, "saveas");
				putValue(ManagedAction.ID, "SAVE_AS");
				putValue(ManagedAction.NEW_TEXT, handler.getSaveAsActionName());
				putValue(Action.SHORT_DESCRIPTION, handler
						.getSaveAsDescription());
				String actionName = handler.getSaveAsActionName();
				if (handler.getSaveAsShortCut() != null) {
					actionName = handler.getSaveAsShortCut() + actionName;
				}
				putValue(Action.NAME, actionName);
			}

			protected void _actionPerformed(Tab tab, ActionEvent e) {
				try {
					handler.save(tab, true);
				} catch (IOException ex) {
					ex.printStackTrace();
					ui.errorMessage(ex, null, null);
				}
			}
		};
	}
}
