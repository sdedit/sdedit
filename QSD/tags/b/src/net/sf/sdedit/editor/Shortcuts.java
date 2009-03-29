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

package net.sf.sdedit.editor;

import net.sf.sdedit.ui.components.MenuBar;
import net.sf.sdedit.util.OS;

/**
 * Utility class that manages strings describing shortcut key combinations for
 * invoking actions that appear in a menu.
 * 
 * @author Markus Strauch
 * 
 */
public class Shortcuts {

	public static final int CLEAR = 0;

	public static final int GLOBAL_CONFIGURATION = 1;

	public static final int DIAGRAM_CONFIGURATION = 2;

	public static final int HELP = 3;

	public static final int OPEN = 4;

	public static final int NEW = 5;

	public static final int CLOSE_ALL = 6;

	public static final int CLOSE = 7;

	public static final int QUIT = 8;

	public static final int SAVE = 9;

	public static final int SAVE_AS = 10;

	public static final int EXPORT_IMAGE = 11;

	public static final int WIDEN = 12;

	public static final int NARROW = 13;

	public static final int UNDO = 14;

	public static final int REDO = 15;

	public static final int FULL_SCREEN = 16;

	public static final int FILTER = 17;

	public static final int SPLIT_LEFT_RIGHT = 18;

	public static final int SPLIT_TOP_BOTTOM = 19;

	public static final int PRINT = 20;

	public static final int REDRAW = 21;

	public static final int EXPORT = 22;
	
	public static final int ENABLE_THREADS = 23;

	private static final int MAX = 23;

	private static final Shortcuts instance;

	static {
		instance = new Shortcuts();
	}

	/**
	 * Returns a description of the shortcut for an action identified by a
	 * certain key (see this class' <tt>public static final</tt> attributes),
	 * enclosed by square brackets and thus fitting the requirements for
	 * names of actions that are being added to a {@linkplain MenuBar}.
	 * <p>
	 * See also <a
	 * href="http://java.sun.com/j2se/1.5.0/docs/api/javax/swing/KeyStroke.html#getKeyStroke(java.lang.String)">
	 * <tt>javax.swing.Keystroke#getKeyStroke(java.lang.String)</tt></a>.
	 * 
	 * @param key
	 *            a key identifying an action
	 * @return a description of the shortcut for an action identified by a
	 *         certain key, enclosed by square brackets
	 */
	public static final String getShortcut(int key) {
		if (key < 0 || key > MAX) {
			throw new IllegalArgumentException("Shortcut key out of range: "
					+ key);
		}
		String shortcut = instance.shortcuts[key];
		if (shortcut == null) {
			throw new IllegalStateException("No shortcut defined for key "
					+ key);
		}
		return "[" + shortcut + "]";
	}

	private static final String ctrl(String str) {
		String ctrl = OS.TYPE == OS.Type.MAC ? "meta" : "control";
		return ctrl + " " + str;
	}

	private String[] shortcuts;

	private Shortcuts() {
		shortcuts = new String[MAX + 1];
		init();
		if (OS.TYPE == OS.Type.MAC) {
			initForMac();
		}
	}

	private void init() {
		shortcuts[CLEAR] = ctrl("shift X");
		shortcuts[GLOBAL_CONFIGURATION] = ctrl("shift G");
		shortcuts[DIAGRAM_CONFIGURATION] = ctrl("shift D");
		shortcuts[HELP] = "F1";
		shortcuts[OPEN] = ctrl("O");
		shortcuts[NEW] = ctrl("T");
		shortcuts[CLOSE_ALL] = ctrl("shift W");
		shortcuts[CLOSE] = ctrl("W");
		shortcuts[QUIT] = ctrl("shift Q");
		shortcuts[SAVE] = ctrl("S");
		shortcuts[SAVE_AS] = ctrl("shift S");
		shortcuts[EXPORT_IMAGE] = ctrl("shift E");
		shortcuts[WIDEN] = ctrl("shift K");
		shortcuts[NARROW] = ctrl("shift J");
		shortcuts[REDO] = ctrl("Y");
		shortcuts[UNDO] = ctrl("Z");
		shortcuts[FULL_SCREEN] = "F9";
		shortcuts[FILTER] = "F6";
		shortcuts[SPLIT_LEFT_RIGHT] = "?";
		shortcuts[SPLIT_TOP_BOTTOM] = "?";
		shortcuts[EXPORT] = ctrl("shift E");
		shortcuts[PRINT] = ctrl("P");
		shortcuts[REDRAW] = "F5";
		shortcuts[ENABLE_THREADS] = ctrl("shift M");
	}

	private void initForMac() {
		shortcuts[REDO] = "meta shift Y";
		shortcuts [UNDO] = "meta shift Z";
		shortcuts[QUIT] = "meta q";
	}
}
