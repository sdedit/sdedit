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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * An <tt>AutoCompletion</tt> object can be added to a <tt>JTextPane</tt> as
 * a key listener. When the tab key is pressed, and there is a character to the
 * left to the cursor and whitespace to its right, the associated
 * {@linkplain SuggestionProvider}'s (see
 * {@linkplain #AutoCompletion(JTextPane, SuggestionProvider, char...)}) method
 * {@linkplain SuggestionProvider#getSuggestions(String)} is called with the
 * string to the left of the cursor (separated by whitespace or one of the given
 * delimiters) as a parameter. The strings that are returned by this method all
 * have the parameter string as a prefix. If there is at least one string, the
 * prefix is replaced by it. If there are even more, successive strokes of the
 * tab key will cycle through all strings.
 * 
 * @author Markus Strauch
 * 
 */
public class AutoCompletion extends KeyAdapter {

	private ArrayList<String> suggestions;

	private int counter;

	private JTextPane textArea;

	private SuggestionProvider provider;

	private int wordBegin;

	private int wordEnd;

	private State state;

	private char[] delimiters;

	private enum State {

		/**
		 * The key most recently typed was not the trigger key (TAB) or the
		 * cursor is not at an appropriate position or there is no suggestion
		 * for the prefix to the left of the cursor.
		 */
		INIT,

		/**
		 * The most recently typed key was the trigger key (TAB) and there is
		 * more than one suggestion for the prefix to the left of the cursor.
		 * The string to the left of the cursor is one of the suggestions, when
		 * typing the trigger key again, another suggestion will appear.
		 */
		CHOOSING

	}

	/**
	 * Creates a new <tt>AutoCompletion</tt>.
	 * 
	 * @param textPane
	 *            the JTextPane in which text should be substituted NOTE: its
	 *            <tt>getText()</tt> method must return a string with a single
	 *            '\n' as end-of-line character
	 * @param provider
	 *            for providing suggestions of what could be substituted
	 * @param delimiters
	 *            characters that are to be interpreted as the left limit (not
	 *            inclusive) of a prefix that might be substituted
	 */
	public AutoCompletion(JTextPane textPane, SuggestionProvider provider,
			char... delimiters) {
		this.textArea = textPane;
		textPane.addKeyListener(this);
		this.provider = provider;
		suggestions = new ArrayList<String>();
		this.delimiters = delimiters;
		state = State.INIT;
	}

	private boolean isLimit(char c) {
		if (Character.isWhitespace(c)) {
			return true;
		}
		for (char d : delimiters) {
			if (d == c) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isTrigger (KeyEvent e) {
		return e.getKeyCode() == KeyEvent.VK_TAB ||
			e.getKeyCode() == KeyEvent.VK_SPACE && e.isShiftDown() &&
			e.isControlDown();
	}

	private String findPrefix() {
		StringBuffer prefix = new StringBuffer();
		int pos = textArea.getCaretPosition() - 1;
		wordEnd = pos + 1;
		String text = textArea.getText();
		char c;
		while (pos >= 0 && !isLimit((c = text.charAt(pos)))) {
			prefix.insert(0, c);
			pos--;
		}
		wordBegin = pos + 1;
		String pref = prefix.toString().trim();
		return pref;
	}

	private boolean tabPressed() {
		boolean act = false;
		if (state == State.INIT) {
			String prefix = findPrefix();
			if (prefix.length() > 0) {
				suggestions.clear();
				suggestions.addAll(provider.getSuggestions(prefix));
				if (suggestions.size() > 0) {
					replaceBy(suggestions.get(0));
					act = true;
				}
				if (suggestions.size() > 1) {
					counter = 0;
					state = State.CHOOSING;
				}
			}
		} else {
			if (suggestions.size() > 0) {
				// this should actually not be necessary
				act = true;

				counter = (counter + 1) % suggestions.size();
				replaceBy(suggestions.get(counter));
			}
		}
		return act;
	}

	private void replaceBy(final String suggestion) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				textArea.setSelectionStart(wordBegin);
				textArea.setSelectionEnd(wordEnd);
				textArea.replaceSelection(suggestion);
				wordEnd = wordBegin + suggestion.length();
			}
		});
	}

	/**
	 * Implements the behaviour as described in the class comment: 
	 * {@linkplain AutoCompletion}.
	 * 
	 * @param e
	 */
	public void keyPressed(KeyEvent e) {
		if (isTrigger(e)) {
			String text = textArea.getText();
			int i = textArea.getCaretPosition();
			if (text.length() > 0 && !Character.isWhitespace(text.charAt(i - 1))
					&& (i == text.length() || isLimit(text.charAt(i)))) {
				if (tabPressed()) {
					e.consume();
				}
			}
		} else {
			state = State.INIT;
		}
	}

	/**
	 * An interface for objects that provide suggestions how a prefix could be
	 * completed to a known string.
	 */
	public interface SuggestionProvider {

		/**
		 * Returns a list of known strings that have the given string as a
		 * prefix.
		 * 
		 * @param prefix
		 *            the prefix
		 * @return a list of known strings that have the given string as a
		 *         prefix
		 */
		public List<String> getSuggestions(String prefix);

	}
}
