// Copyright (c) 2006 - 2016, Markus Strauch.
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

package net.sf.sdedit.ui.components;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.util.Utilities;

/**
 * An <tt>ATabbedPane</tt> is an advanced <tt>JTabbedPane</tt> that allows
 * a user to close tabs and that assigns unique names to tabs.
 * <p>
 * Unique names are generated according to this policy: If a tab should be
 * given a name <it>X</it> such that a tab named <it>X</it>
 * already exists, we search for the smallest integer number
 * <it>i&gt;0</it> such that there is no tab with the name <it>X-i</it>.
 * The tab is then named <it>X-i</it>. 
 * 
 * @author Markus Strauch
 * 
 */
public class ATabbedPane extends JTabbedPane {

	private final static long serialVersionUID = 0xAB343921;

	private static Image clean = Icons.getIcon("close").getImage();

	private static Image stain = Icons.getIcon("close2").getImage();

	private List<ATabbedPaneListener> listeners;

	/**
	 * Constructor.
	 * 
	 */
	public ATabbedPane() {
		super(SwingConstants.TOP);
		listeners = new LinkedList<ATabbedPaneListener>();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int tabNumber = getUI().tabForCoordinate(ATabbedPane.this,
						e.getX(), e.getY());
				if (tabNumber < 0)
					return;
				Rectangle rect = ((ACloseTabIcon) ATabbedPane.this
						.getIconAt(tabNumber)).getBounds();
				if (rect.contains(e.getX(), e.getY())) {
					ATabbedPane.this.selectAndCloseTab(tabNumber);
				}
			}
		});
	}

	/**
	 * Returns the title of the tab currently being selected or
	 * the empty string, if no tab is selected.
	 * 
	 * @return the title of the tab currently being selected
	 */
	public String getCurrentTitle() {
		if (getSelectedIndex() == -1) {
			return "";
		}
		return getTitleAt(getSelectedIndex());
	}

	/**
	 * Adds an <tt>ATabbedPaneListener</tt> whose responsibility is to
	 * decide whether a tab will actually be closed on user demand.
	 * 
	 * @param listener an <tt>ATabbedPaneListener</tt>
	 */
	public void addListener(ATabbedPaneListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ATabbedPaneListener listener) {
		listeners.remove(listener);
	}

	private String generateUniqueTabName(String tabName) {
		List<String> titles = new LinkedList<String>();
		for (int i = 0; i < getTabCount(); i++) {
			titles.add(getTitleAt(i));
		}
		return Utilities.findUniqueName(tabName, titles);
	}

	/**
	 * Sets the name of the currently selected tab to the given
	 * <tt>title</tt> or to <tt>title</tt> with an integer number appended,
	 * as described in the class comment: {@linkplain ATabbedPane}.
	 * 
	 * @param title the new name of the currently selected tab
	 */
	public void setTabTitle(String title) {
		int i = getSelectedIndex();
		setTitleAt(i, "");
		title = generateUniqueTabName(title);
		setTitleAt(i, title);
	}

	/**
	 * Adds a tab to this <tt>ATabbedPane</tt> and assigns the given
	 * <tt>title</tt> to it, which may be changed as described in the
	 * class comment ({@linkplain ATabbedPane}) in order to get a unique
	 * title.
	 * 
	 * @param tab the tab to be added
	 * @param title the title of the tab to be added
	 * @return the unique name of the tab after it has been added
	 */
	public String addTab(Component tab, String title) {
		title = generateUniqueTabName(title);
		ACloseTabIcon icon = new ACloseTabIcon(tab, this);
		addTab(title, icon, tab);
		setSelectedIndex(getTabCount() - 1);
		return title;
	}

	/**
	 * Adds a tab to this <tt>ATabbedPane</tt> and assigns the given
	 * <tt>title</tt> to it, which may be changed as described in the
	 * class comment ({@linkplain ATabbedPane}) in order to get a unique
	 * title.
	 * 
	 * @param tab the tab to be added
	 * @param title the title of the tab to be added
	 * @param closeIcon the icon for closing the tab
	 * @return the unique name of the tab after it has been added
	 */
	public String addTab(Component tab, String title, Icon closeIcon) {
		title = generateUniqueTabName(title);
		ACloseTabIcon icon = null;
		if (closeIcon != null) {
			icon = new ACloseTabIcon(tab, this, closeIcon);
		}
		addTab(title, icon, tab);
		setSelectedIndex(getTabCount() - 1);
		return title;
	}

	/**
	 * Removes the currently selected tab. If <tt>oneOpen</tt> is true
	 * and there is only one tab, it will not be removed.
	 * 
	 * @param oneOpen
	 *            flag denoting if at least one tab must stay open
	 * @return flag denoting if the current tab has in fact been removed
	 */
	public boolean removeCurrentTab(boolean oneOpen) {
		if (getTabCount() == 0) {
			return false;
		}
		if (oneOpen && getTabCount() == 1) {
			return false;
		}
		int number = getSelectedIndex();
		if (number != -1) {
			remove(number);
			return true;
		}
		return false;
	}
	
	public void selectByName (String tabName) {
		for (int i = 0; i < getTabCount(); i++) {
			String title = getTitleAt(i);
			if (title.equals(tabName)) {
				setSelectedIndex(i);
				return;
			}
		}
	}

	protected void fireCurrentTabClosing() {
		Component selected = getSelectedComponent();
		for (ATabbedPaneListener listener : listeners) {
			listener.tabClosing(selected);
		}
	}

	protected void selectAndCloseTab(int number) {
		setSelectedIndex(number);
		if (getTabCount() > 1) {
			fireCurrentTabClosing();
		}
	}

	private class ACloseTabIcon implements Icon, StainedListener {

		private int x_pos;

		private int y_pos;

		private Image image;

		/**
		 * Creates a new <tt>CloseTabIcon</tt> for a tab in a
		 * {@linkplain ATabbedPane}.
		 * 
		 * @param tab
		 *            a tab
		 * @param pane
		 *            the tabbed pane
		 * @param icon
		 */
		public ACloseTabIcon(Component tab, ATabbedPane pane, Icon icon) {
			image = ((ImageIcon) icon).getImage();
		}

		/**
		 * Creates a new <tt>CloseTabIcon</tt> for a possibly
		 * &quot;stainable&quot; tab in a {@linkplain ATabbedPane}. If the
		 * <tt>tab</tt>'s class implements {@linkplain Stainable}, the
		 * <tt>CloseTabIcon</tt> registers as a {@linkplain StainedListener}
		 * and will reflect the stained status of the tab by the color of the
		 * icon.
		 * 
		 * @param tab
		 *            a tab
		 * @param pane
		 *            the tabbed pane
		 */
		public ACloseTabIcon(Component tab, ATabbedPane pane) {
			if (tab instanceof Stainable) {
				((Stainable) tab).addStainedListener(this);
			}
			image = clean;
		}

		/**
		 * Changes the icon so it reflects the state of being
		 * stained or clean.
		 * 
		 * @see net.sf.sdedit.ui.components.StainedListener#stainedStatusChanged(boolean)
		 */
		public void stainedStatusChanged(Stainable stainable, boolean stained) {
			image = stained ? stain : clean;
			repaint();
		}

		/**
		 * @see javax.swing.Icon#paintIcon(java.awt.Component,
		 *      java.awt.Graphics, int, int)
		 */
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.drawImage(image, x, y, c);
			this.x_pos = x;
			this.y_pos = y;
		}

		/**
		 * @see javax.swing.Icon#getIconWidth()
		 */
		public int getIconWidth() {
			return image.getWidth(null);
		}

		/**
		 * @see javax.swing.Icon#getIconHeight()
		 */
		public int getIconHeight() {
			return image.getHeight(null);
		}

		/**
		 * Returns a <tt>Rectangle</tt> where this icon is currently being
		 * displayed.
		 * 
		 * @return a <tt>Rectangle</tt> where this icon is currently being
		 *         displayed
		 */
		public Rectangle getBounds() {
			return new Rectangle(x_pos, y_pos, getIconWidth(), getIconHeight());
		}
	}
}
