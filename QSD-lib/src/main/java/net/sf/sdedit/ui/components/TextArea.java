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

package net.sf.sdedit.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import net.sf.sdedit.util.PopupActions;
import net.sf.sdedit.util.Utilities;
import net.sf.sdedit.util.PopupActions.ContextHandler;

/**
 * A <tt>TextArea</tt> is an advanced <tt>JTextArea</tt> with error marks and
 * undo/redo function.
 * 
 * @author Markus Strauch
 */
public class TextArea extends JTextPane implements UndoableEditListener,
		Highlighter.HighlightPainter, PopupActions.Provider {

	private final static long serialVersionUID = 0xAB343920;

	private Highlighter highlighter;

	private UndoManager undoManager;

	private Object highlight;

	private static final Color ERROR_COLOR = Color.RED;

	private String EOL;

	private PopupActions popupActions;

	private static class LineInfo {

		int lineBegin;

		int spacesOld;

		int spacesNew;

		public String toString () {
			return "lineBegin=" + lineBegin + ", spacesOld=" + spacesOld + ", spacesNew=" + spacesNew;
		}
		
	}

	//
	// UndoableEditListener method
	//

	/**
	 * Adds the <tt>UndoableEdit</tt> that has happened to the
	 * <tt>UndoManager</tt> associated with this <tt>TextArea</tt>.
	 * 
	 * @param evt
	 *            the undoable edit event encapsulating the
	 *            <tt>UndoableEdit</tt>
	 */
	public void undoableEditHappened(UndoableEditEvent evt) {
		undoManager.addEdit(evt.getEdit());
	}

	/**
	 * Allocates a new <tt>TextArea</tt>.
	 */
	@SuppressWarnings("serial")
	public TextArea() {
		undoManager = new UndoManager();
		highlighter = new DefaultHighlighter();
		setHighlighter(highlighter);
		getActionMap().put("Undo", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				undo();
			}
		});
		getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
		getActionMap().put("Redo", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				redo();
			}
		});
		getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
		EOL = (String) getDocument().getProperty(
				DefaultEditorKit.EndOfLineStringProperty);
		getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty,
				"\n");
		getDocument().addUndoableEditListener(this);
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		super.paintComponent(g);
	}

	/**
	 * Undoes the last action, if any has been performed.
	 */
	public void undo() {
		try {
			if (undoManager.canUndo()) {
				undoManager.undo();
				for (KeyListener kl : getKeyListeners()) {
					kl.keyTyped(null);
					kl.keyReleased(null);
				}
			}
		} catch (CannotUndoException e) {
		}
	}

	/**
	 * Redoes the last action, if any has been performed.
	 */
	public void redo() {
		try {
			if (undoManager.canRedo()) {
				undoManager.redo();
				for (KeyListener kl : getKeyListeners()) {
					kl.keyTyped(null);
					kl.keyReleased(null);
				}
			}
		} catch (CannotRedoException e) {
		}
	}

	/**
	 * Marks the characters between the two specified positions as erroneous.
	 * Before this another maybe existing mark will be removed. If the
	 * <tt>from</tt> position is less than 0, an error mark - if present - will
	 * be removed.
	 * 
	 * @param from
	 *            the position of the first character
	 * @param to
	 *            the position of the last character
	 */
	public void markError(final int from, final int to) {
		if (highlight != null) {
			highlighter.removeHighlight(highlight);
		}
		if (from >= 0) {
			try {
				highlight = highlighter.addHighlight(from, to, this);
			} catch (BadLocationException ble) {
				ble.printStackTrace();
			}
		}
	}

	public void prettyPrint(PrettyPrinter prettyPrinter) throws BadLocationException {
		List<LineInfo> lineInfos = new LinkedList<LineInfo>();
		String text = getText();
		int l = text.length();
		int spaces = 0;
		int lineBegin = 0;
		boolean begin = true;
		for (int i = 0; i <= l; i++) {
			char c = i == l ? (char) 0 : text.charAt(i);
			if (i == l || c == (char) 10) {
				begin = true;
				LineInfo info = new LineInfo();
				info.lineBegin = lineBegin;
				info.spacesOld = spaces;
				info.spacesNew = prettyPrinter.getAlign(lineBegin+1); // TODO
				lineInfos.add(info);
				System.out.println(info);
				lineBegin = i + 1;
				spaces = 0;
			} else if (begin) {
				if (' ' == c) {
					spaces++;
				} else {
					begin = false;
				}
			}
		}
		int spacesAdded = 0;
		for (LineInfo info : lineInfos) {
			if (info.spacesNew >= 0) {
				int diff = Math.abs(info.spacesOld - info.spacesNew);
				if (info.spacesOld < info.spacesNew) {
					getDocument().insertString(info.lineBegin + spacesAdded, Utilities.pad(' ', diff), null);
					spacesAdded += diff;
				} else if (info.spacesOld > info.spacesNew) {
					getDocument().remove(info.lineBegin + spacesAdded, diff);
					spacesAdded -= diff;
				}
			}
		}
	}

	/**
	 * Returns the number of the line the cursor is in.
	 * 
	 * @return the number of the line the cursor is in
	 */
	public int getCaretLine() {
		final int caret = getCaretPosition();
		final String text = getText();
		int line = 0;
		for (int i = 0; i < caret; i++) {
			char c = text.charAt(i);
			if (c == '\n') {
				line++;
			}
		}
		return line;
	}

	/**
	 * Returns the text displayed by this <tt>TextArea</tt>, optionally
	 * formatted such that end-of-line characters match the platform's
	 * behaviour.
	 * 
	 * @param format
	 *            flag denoting if end-of-line characters should be represented
	 *            as usual for the platform (<tt>true</tt>) or by '\n' (
	 *            <tt>false</tt>).
	 * @return the text displayed by this <tt>TextArea</tt>
	 */
	private String getText(boolean format) {
		String text;
		if (format) {
			getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty,
					EOL);
			text = super.getText();
			getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty,
					"\n");
		} else {
			text = super.getText();
		}
		return text;
	}

	/**
	 * Returns the text displayed by this <tt>TextArea</tt>, with a single '\n'
	 * used as an end-of-line character.
	 * 
	 * @return the text displayed by this <tt>TextArea</tt>
	 */
	public String getText() {
		return getText(false);
	}

	/**
	 * Returns the index of the character at the beginning of the current line
	 * (where the cursor is)
	 * 
	 * @return the index of the character at the beginning of the current line
	 *         (where the cursor is)
	 */
	public int getCurrentLineBegin() {
		final int caret = getCaretPosition();
		final String text = getText();
		int i = Math.min(text.length() - 1, caret);
		for (; i >= 0; i--) {
			final char c = text.charAt(i);
			if (c != '\n' && c != '\r') {
				break;
			}
		}
		for (; i >= 0; i--) {
			final char c = text.charAt(i);
			if (c == '\n' || c == '\r') {
				return i + 2;
			}
		}
		return 0;
	}

	// paint a thick line under one line of text, from r extending rightward
	// to
	// x2
	private void paintLine(Graphics g, Rectangle r, int x2) {
		int ytop = r.y + r.height - 3;
		// g.fillRect(r.x, ytop, x2 - r.x, 3);

		for (int x = r.x; x < x2 - r.x; x += 2) {
			g.drawLine(x, ytop, x + 1, ytop + 1);
			g.drawLine(x + 1, ytop + 1, x + 2, ytop);
		}
	}

	// paint thick lines under a block of text
	/**
	 * @see Highlighter.HighlightPainter#paint(Graphics, int, int, Shape,
	 *      JTextComponent)
	 */
	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {

		Rectangle r0 = null, r1 = null, rbounds = bounds.getBounds();
		int xmax = rbounds.x + rbounds.width; // x coordinate of right
		// edge
		try { // convert positions to pixel coordinates
			r0 = c.modelToView(p0);
			r1 = c.modelToView(p1);
		} catch (BadLocationException ex) {
			return;
		}
		if ((r0 == null) || (r1 == null))
			return;

		g.setColor(ERROR_COLOR);

		// special case if p0 and p1 are on the same line
		if (r0.y == r1.y) {
			paintLine(g, r0, r1.x);
			return;
		}

		// first line, from p1 to end-of-line
		paintLine(g, r0, xmax);

		// all the full lines in between, if any (assumes that all lines
		// have
		// the same height--not a good assumption with
		// JEditorPane/JTextPane)
		r0.y += r0.height; // move r0 to next line
		r0.x = rbounds.x; // move r0 to left edge
		while (r0.y < r1.y) {
			paintLine(g, r0, xmax);
			r0.y += r0.height; // move r0 to next line
		}

		// last line, from beginning-of-line to p1
		paintLine(g, r0, r1.x);
	}

	public PopupActions getPopupActions(ContextHandler contextHandler) {
		if (popupActions == null) {
			popupActions = new PopupActions(this, contextHandler);
		}
		return popupActions;
	}

}
