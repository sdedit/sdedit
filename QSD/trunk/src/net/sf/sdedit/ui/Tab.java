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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sf.sdedit.editor.Actions;
import net.sf.sdedit.editor.Editor;
import net.sf.sdedit.editor.plugin.FileActionProvider;
import net.sf.sdedit.editor.plugin.FileHandler;
import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.ui.components.FullScreen;
import net.sf.sdedit.ui.components.Stainable;
import net.sf.sdedit.ui.components.StainedListener;
import net.sf.sdedit.ui.components.ZoomPane;
import net.sf.sdedit.ui.components.Zoomable;
import net.sf.sdedit.ui.components.buttons.ActionManager;
import net.sf.sdedit.ui.components.buttons.Activator;
import net.sf.sdedit.ui.impl.TabContainerListener;
import net.sf.sdedit.ui.impl.UserInterfaceImpl;
import net.sf.sdedit.util.Pair;
import net.sf.sdedit.util.Ref;

/**
 * A <tt>Tab</tt> is a panel that constitutes the contents of the <i>Quick
 * Sequence Diagram Editor</i> window. A user may freely switch between tabs
 * (see {@linkplain #activate(ActionManager, FileActionProvider)},
 * {@linkplain #deactivate(ActionManager, FileActionProvider)}).
 * <p>
 * A <tt>Tab</tt> may have an associated {@linkplain FileHandler} for storing
 * the contents of the tab to a file or for loading a file into a tab (see
 * {@linkplain #getFileHandler()}. The file belonging to a tab is returned by
 * {@linkplain #getFile()}.
 * 
 * @author Markus Strauch
 */
@SuppressWarnings("serial")
public abstract class Tab extends JPanel implements Stainable,
		TabContainerListener {

	private static ImageIcon cleanIcon;

	private static ImageIcon dirtyIcon;

	private File file;

	private List<TabListener> listeners;

	private static FullScreen fullScreen;

	private boolean clean;

	private List<StainedListener> stainedListeners;

	private final UserInterfaceImpl ui;

	private JPanel contentPanel;

	private JPanel statusPanel;

	private JLabel cleanLabel;

	private JPanel bottom;

	private String title;

	private int id;

	static {
		cleanIcon = Icons.getIcon("close");
		dirtyIcon = Icons.getIcon("close2");
		fullScreen = new FullScreen();
	}

	protected Tab(UserInterfaceImpl ui) {
		this.ui = ui;
		setLayout(new BorderLayout());
		contentPanel = new JPanel();
		bottom = new JPanel();
		add(contentPanel, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		bottom.setLayout(new BorderLayout());
		cleanLabel = new JLabel(cleanIcon);
		cleanLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && canClose()) {
					close(true);
				}
			}
		});
		bottom.add(cleanLabel, BorderLayout.WEST);
		statusPanel = new JPanel();
		bottom.add(statusPanel, BorderLayout.CENTER);
		listeners = new LinkedList<TabListener>();
		addTabListener(ui);
		stainedListeners = new LinkedList<StainedListener>();
		clean = true;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Tab> getChildren() {
		return ui.getTabContainer().getSuccessors(this);
	}

	public Tab getParentTab() {
		return ui.getTabContainer().getParentTab(this);
	}

	public int getId() {
		return id;
	}

	public boolean alsoRemoveDescendants() {
		return false;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		boolean changed;
		if (title != null) {
			changed = !title.equals(this.title);
		} else {
			changed = this.title != null;
		}
		this.title = title;
		if (changed) {
			for (TabListener listener : listeners) {
				listener.titleChanged(this);
			}
		}
	}

	public final JPanel getContentPanel() {
		return contentPanel;
	}

	public final JPanel getStatusPanel() {
		return statusPanel;
	}

	/**
	 * @see net.sf.sdedit.ui.impl.TabContainerListener#tabClosing(net.sf.sdedit.ui.Tab)
	 */
	public final void tabClosing(Tab tab) {
		if (tab == this) {
			close(true);
		}
	}

	/**
	 * @see net.sf.sdedit.ui.impl.TabContainerListener#tabSelected(net.sf.sdedit.ui.Tab,
	 *      net.sf.sdedit.ui.Tab)
	 */
	public void tabSelected(Tab previous, Tab current) {

	}

	public abstract Icon getIcon();

	public void fullScreen() {
		fullScreen.display(getZoomable());
	}

	public boolean supportsFullScreen() {
		return getZoomable() != null;
	}

	protected abstract Zoomable<? extends JComponent> getZoomable();

	public void activate(ActionManager actionManager,
			FileActionProvider faProvider) {

		if (getOverloadedActions() != null) {
			for (Pair<Action, Activator> action : getOverloadedActions()) {
				actionManager.overload(action.getFirst(), action.getSecond());
			}
		}

		if (getFileHandler() != null) {

			actionManager.overload(faProvider.getSaveAction(getFileHandler(),
					ui), faProvider.getSaveActivator);

			actionManager.overload(faProvider.getSaveAsAction(getFileHandler(),
					ui), faProvider.getSaveActivator);
		}
	}

	public UserInterfaceImpl get_UI() {
		return ui;
	}

	public void deactivate(ActionManager actionManager,
			FileActionProvider faProvider) {
		if (getOverloadedActions() != null) {
			for (Pair<Action, Activator> action : getOverloadedActions()) {
				actionManager.unload(action.getFirst());
			}
		}

		if (getFileHandler() != null) {
			actionManager
					.unload(faProvider.getSaveAction(getFileHandler(), ui));
			actionManager.unload(faProvider.getSaveAsAction(getFileHandler(),
					ui));
		}
	}

	public boolean isClean() {
		return clean;
	}

	public void setClean(boolean clean) {
		if (clean != this.clean) {
			for (StainedListener stainedListener : stainedListeners) {
				stainedListener.stainedStatusChanged(this, !clean);
			}
			if (clean) {
				cleanLabel.setIcon(cleanIcon);
			} else {
				cleanLabel.setIcon(dirtyIcon);
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					bottom.revalidate();
					bottom.repaint();
				}
			});
			this.clean = clean;
		}
	}

	public void addStainedListener(StainedListener listener) {
		stainedListeners.add(listener);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		if (file != null) {
			setTitle(file.getName());
		}
	}

	public boolean isReadyToBeClosed(Ref<Boolean> noToAll) {
		if (isClean() || getFileHandler() == null) {
			return true;
		}
		Editor editor = Editor.getEditor();
		String option;
		if (noToAll != null) {
			option = editor.getUI().getOption(
					"<html>There are unsaved changes. "
							+ "Do you want<br>to save them?", "Cancel", "No",
					"::::Yes#", "No to all");
			noToAll.t = false;

		} else {
			option = editor.getUI().getOption(
					"<html>There are unsaved changes. "
							+ "Do you want<br>to save them?", "Cancel", "No",
					"::::Yes#");

		}
		if (option.equals("Yes")) {
			File file = getFile();
			if (file == null) {
				try {
					file = getFileHandler().save(this, false);
				} catch (IOException ioe) {
					editor.getUI().errorMessage(ioe, null, null);
				}
			}
			return file != null;
		}
		if (option.equals("No")) {
			return true;
		}
		if (option.equals("No to all")) {
			noToAll.t = true;
			return true;
		}
		// Cancel
		return false;
	}

	/**
	 * This method is called when a tab is to be closed, caused by
	 * {@linkplain Actions#closeTabAction}, a click onto the close icon or by
	 * automatically closing all tabs the are left open.
	 * 
	 * @param check
	 *            a flag denoting if we must check first if the tab can be
	 *            closed
	 */
	public boolean close(boolean check) {
		if (!check || isReadyToBeClosed(null)) {
			for (TabListener listener : listeners) {
				listener.tabIsClosed(this);
			}
			listeners.clear();
			stainedListeners.clear();
			return true;
		}
		return false;
	}

	public void addTabListener(TabListener listener) {
		listeners.add(listener);
	}

	public final List<Action> getContextActions() {
		List<Action> list = new LinkedList<Action>();
		if (canClose()) {
			list.add(closeAction);
		}
		_getContextActions(list);
		return list;
	}

	public abstract boolean canClose();

	protected Action closeAction = new AbstractAction() {

		{
			putValue(NAME, "Close");
			putValue(SMALL_ICON, Icons.getIcon("close"));
		}

		public void actionPerformed(ActionEvent e) {
		    if (Tab.this.canClose()) {
		        Tab.this.close(true);    
		    }
			
		}
	};

	public ZoomPane getZoomPane() {
		return null;
	}

	public boolean canZoom() {
		return getZoomPane() != null;
	}

	public abstract boolean canGoHome();

	public void goHome() {

	}

	protected abstract void _getContextActions(List<Action> actionList);

	protected abstract List<Pair<Action, Activator>> getOverloadedActions();

	public abstract FileHandler getFileHandler();

}
