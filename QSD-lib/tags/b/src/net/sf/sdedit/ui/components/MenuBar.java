// Copyright (c) 2006 - 2008, Markus Strauch.
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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * This is a slightly advanced <tt>JMenuBar</tt> subclass that allows to
 * structure a menu by means of category names, so a user is not required to
 * build <tt>JMenu</tt>s. Furthermore, <tt>MenuBar</tt> provides a
 * convenient way to define mnemonics for menu entries (by using the
 * '&amp;'-notation as known from Qt).
 * <p>
 * When an <tt>Action</tt> or <tt>JMenuItem</tt> is added to the
 * <tt>MenuBar</tt>, a title string must be specified. If the string has a
 * prefix that starts with '[' and ends with ']', the substring between these
 * square brackets represents the accelerator key for the action or item. The
 * appropriate key for the string is found as described here: <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/javax/swing/KeyStroke.html#getKeyStroke(java.lang.String)">
 * <tt>javax.swing.Keystroke#getKeyStroke(java.lang.String)</tt></a>.
 * <p>
 * If a title string contains an '&amp;', the following character is used as the
 * mnemonic for the entry or action. This also applies to categories/sub-menus.
 * 
 * @author Markus Strauch
 */
public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = 0x1ED0BAA9;

	private Map<String, JMenu> categories;

	private Map<String, Action> nameMap;

	/**
	 * Creates a new empty <tt>MenuBar</tt>.
	 * 
	 */
	public MenuBar() {
		super();
		categories = new HashMap<String, JMenu>();
		nameMap = new HashMap<String, Action>();
	}

	/**
	 * Returns an action that has formerly been added by
	 * {@linkplain #addAction(String, Action)} or
	 * {@linkplain #addAction(String, Action, int)}.
	 * 
	 * @param name
	 *            the name of the action
	 * @return the action with the name, or <tt>null</tt>
	 */
	public Action getActionByName(String name) {
		return nameMap.get(name);
	}

	/**
	 * Creates an instance of a subclass of <tt>JMenuItem</tt> with the given
	 * title (possibly specifying an accelerator key or a mnemonic as described
	 * here: {@linkplain MenuBar}). The item is not added to any menu.
	 * 
	 * @param <T>
	 *            the type of the <tt>JMenuItem</tt> to be created
	 * @param title
	 *            the title of the item
	 * @param itemType
	 *            the type of the <tt>JMenuItem</tt>, represented as a
	 *            <tt>Class</tt> object.
	 * @return an instance of a subclass of <tt>JMenuItem</tt>, bearing the
	 *         given title
	 */
	public static <T extends JMenuItem> T makeMenuItem(String title,
			Class<T> itemType) {
		return makeMenuItem(title, title, String.class, itemType);
	}

	private static <T extends JMenuItem> T makeMenuItem(String title,
			Object arg, Class<?> argType, Class<T> itemType) {
		try {
			KeyStroke acc = null;
			if (title.charAt(0) == '[') {
				int r = title.indexOf(']');
				String stroke = title.substring(1, r);
				acc = KeyStroke.getKeyStroke(stroke);
			}
			Constructor<T> constructor = itemType.getConstructor(argType);
			int amp = title.indexOf('&');

			T item = constructor.newInstance(new Object[] { arg });
			item.setText(getCaption(title));

			if (amp < title.length() - 1) {
				item.setMnemonic(title.charAt(amp + 1));
			}
			if (acc != null) {
				item.setAccelerator(acc);
			}
			return item;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError();
		}
	}

	private static String getCaption(String caption) {
		// remove the prefix between square brackets
		String _caption = caption;
		if (caption.charAt(0) == '[') {
			int r = caption.indexOf(']');
			_caption = caption.substring(r + 1);
		}
		String c = "";
		// delete the first occurrence of '&'
		int amp = _caption.indexOf('&');
		if (amp > 0) {
			c = _caption.substring(0, amp);
		}
		if (amp < _caption.length() - 1) {
			c += _caption.substring(amp + 1);
		}
		return c;
	}

	/**
	 * Removes an action from a category.
	 * 
	 * @param category
	 *            the name of the category, containing all '&amp;'-characters
	 * @param action
	 *            the action to be removed from the category
	 */
	public void removeAction(String category, Action action) {
		JMenu categoryMenu = categories.get(getCaption(category));
		if (categoryMenu == null) {
			return;
		}
		for (int i = 0; i < categoryMenu.getItemCount(); i++) {
			JMenuItem item = categoryMenu.getItem(i);
			if (item.getAction() == action) {
				categoryMenu.remove(i);
				return;
			}
		}
	}

	/**
	 * Adds an <tt>Action</tt> to a (sub-)menu of this <tt>MenuBar</tt>.
	 * The <tt>Action</tt> must have a name (as set via
	 * <tt>Action.putValue(Action.NAME,name)</tt>), which may specify a
	 * mnemonic and accelerator key as described in the class comment:
	 * {@linkplain #MenuBar}.
	 * <p>
	 * Example: Let <tt>a</tt> be an action to be added to the submenu
	 * &quot;foo&quot; of &quot;bar&quot;. This can be done so:
	 * <tt>addAction(&quot;foo.bar&quot;,a,0)</tt>. If &quot;foo&quot; and
	 * &quot;bar&quot; should have &quot;f&quot; resp. &quot;b&quot; as
	 * mnemonics, you would call
	 * <tt>addAction(&quot;&amp;foo.&amp;bar&quot;,a,0)</tt>
	 * 
	 * @param category
	 *            the string describing the category, where names belonging to
	 *            adjacent category levels are separated by '.'. Category names
	 *            may include '&' for mnemonics.
	 * @param action
	 *            the action to be added
	 * @param pos
	 *            the position of the action in the (sub-)menu
	 * @return the <tt>JMenuItem</tt> instance associated to the action
	 * @throws IllegalArgumentException
	 *             if an illegal (sub-)category is specified
	 */
	public JMenuItem addAction(String category, Action action, int pos) {
		JMenu categoryMenu = categories.get(getCaption(category));

		if (categoryMenu == null) {
			int dot = category.lastIndexOf('.');
			if (dot == -1) {
				categoryMenu = makeMenuItem(category, JMenu.class);
				add(categoryMenu);
				categories.put(getCaption(category), categoryMenu);
			} else {
				String subCategory = category.substring(dot + 1);
				String supCategory = category.substring(0, dot);
				JMenu supMenu = categories.get(getCaption(supCategory));
				if (supMenu == null) {
					throw new IllegalArgumentException("menu for category "
							+ supCategory + " must"
							+ " exist before a subcategory " + subCategory
							+ " can be added");
				}
				categoryMenu = makeMenuItem(subCategory, JMenu.class);
				supMenu.add(categoryMenu);
				categories.put(getCaption(category), categoryMenu);
			}

		}
		JMenuItem item = null;
		if (action != null) {
			nameMap.put((String) action.getValue(Action.NAME), action);
			item = makeMenuItem((String) action.getValue(Action.NAME), action,
					Action.class, JMenuItem.class);
			if (pos == -1) {
				categoryMenu.add(item);
			} else {
				categoryMenu.insert(item, pos);
			}
		}
		return item;
	}

	/**
	 * Adds a new (sub-)menu to this <tt>MenuBar</tt>. Menus can be
	 * arbitrarily nested. When a menu for a category on the n n-th (n &gt;= 1)
	 * level is added, the category string must contain n substrings, separated
	 * by '.', describing all higher categories in descending order and finally
	 * the n-th category. All submenus for the categories on the levels 1, ...,
	 * n-1 must already be present.
	 * 
	 * @param category
	 *            the category of the (sub-)menu (represented by a string with
	 *            '.''s as described above)
	 * @param icon
	 *            an icon for the (sub-)menu or <tt>null</tt>
	 */
	public void addMenu(String category, ImageIcon icon) {
		int dot = category.lastIndexOf('.');
		JMenu categoryMenu;
		if (dot == -1) {
			categoryMenu = makeMenuItem(category, JMenu.class);
			if (icon != null) {
				categoryMenu.setIcon(icon);
			}
			add(categoryMenu);
			categories.put(getCaption(category), categoryMenu);
		} else {
			String subCategory = category.substring(dot + 1);
			String supCategory = category.substring(0, dot);
			JMenu supMenu = categories.get(getCaption(supCategory));

			if (supMenu == null) {
				throw new IllegalArgumentException("menu for category "
						+ supCategory + " must"
						+ " exist before a subcategory " + subCategory
						+ " can be added");
			}
			categoryMenu = makeMenuItem(subCategory, JMenu.class);
			supMenu.add(categoryMenu);
			categories.put(getCaption(category), categoryMenu);
		}
		if (icon != null) {
			categoryMenu.setIcon(icon);
		}
	}

	/**
	 * Adds an action in the given category. See also
	 * {@linkplain #addAction(String, Action, int)}.
	 * 
	 * @param category
	 *            the category of the action
	 * @param action
	 *            the action to be added
	 * @return the <tt>JMenuItem</tt> associated to the action
	 * @throws IllegalArgumentException
	 *             if the category is part of a non-existing higher level
	 *             category
	 */
	public JMenuItem addAction(String category, Action action) {
		return addAction(category, action, -1);
	}

	/**
	 * Adds an item to the given category. See also
	 * {@linkplain #addAction(String, Action, int)}.
	 * 
	 * @param category
	 *            the category of the action
	 * @param item
	 *            the item to be added
	 * @throws IllegalArgumentException
	 *             if the category is part of a non-existing higher level
	 *             category
	 */
	public void addItem(String category, JMenuItem item) {
		// TODO insert category menu if needed
		JMenu categoryMenu = categories.get(getCaption(category));
		categoryMenu.add(item);
	}

	/**
	 * @see javax.swing.JMenuBar#updateUI()
	 */
	@Override
	public void updateUI() {
		super.updateUI();
		if (categories != null) {
			for (JMenu menu : categories.values()) {
				menu.updateUI();
				for (int i = 0; i < menu.getItemCount(); i++) {
					menu.getItem(i).updateUI();
				}
			}
		}
	}
}
