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
package net.sf.sdedit.text;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.sdedit.diagram.MessageData;
import net.sf.sdedit.error.SyntaxError;
import net.sf.sdedit.util.Grep;
import net.sf.sdedit.util.Grep.Region;

/**
 * MessageData is data derived from strings representing messages exchanged
 * between objects. These strings have the following format:
 * 
 * <pre>
 *          {caller}[{level},{thread}]:&gt;{answer}={callee}.{message}
 * </pre>
 * 
 * {caller}, {callee} and {message} are strings and are mandatory. {answer}= is
 * a string and is optional. [{level}] is optional, where {level} is an integer
 * number.
 * <p>
 * When the <tt>parse</tt> method has finished, the parts of the string can be
 * fetched via <tt>get</tt> methods. If no level is given, level is 0. If no
 * answer is specified, answer is the empty string.
 * 
 * @author Markus Strauch
 * 
 */
public class TextBasedMessageData extends MessageData {

	private final static String LEVELS = "(" + "\\[(\\d*),(\\d+)\\]" + "|"
			+ "\\[(\\d+)\\]" + "|" + "\\[(\\D.*)\\]" + ")";

	private final static String PREFIX = "(\\(\\d*(,\\d+)?\\))?\\s*(.+)";

	private static final String COLON = "(?<!\\\\):(?!>)";

	private static final String SPAWN = "(?<!\\\\):>";

	private static final String DOT = "(?<!\\\\)\\.";

	private static final String EQ = "(?<!\\\\)=";

	private String string;
	
	private final Map<String,Grep.Region> regions;

	static {
		for (Method method : TextBasedMessageData.class.getMethods()) {
			try {
				method.setAccessible(true);
			} catch (Throwable t) {
				System.out.println(t.getClass().getSimpleName());
			}
		}
	}

	/**
	 * Creates a new <tt>MessageParser</tt> for parsing a string.
	 * 
	 * @param string
	 *            the string to be parsed
	 * @throws SyntaxError
	 *             if the string is not a valid message
	 */
	public TextBasedMessageData(String string) throws SyntaxError {
		super();
		this.string = string;
		this.regions = new HashMap<String, Grep.Region>();
		parse();
	}

	/**
	 * Parses the string, after that the attributes corresponding to the parts
	 * of the string (see {@linkplain TextBasedMessageData} can be fetched via
	 * the <tt>get</tt> methods.
	 * 
	 */
	private void parse() throws SyntaxError {
		boolean success = false;
		if (string.indexOf(':') == -1) {
			throw new SyntaxError(null, "not a valid message - ':' missing");
		}
		success = /* Level, thread, and answer */
		Grep.parseAndSetProperties(this, PREFIX + LEVELS + COLON + "(.*)" + EQ
				+ "(.+?)" + DOT + "(.*)", string, regions, "noteId", "dummy", "caller",
				"dummy", "levelString", "threadString", "levelString",
				"callerMnemonic", "answer", "callee", "message")
				/* Level, thread, no answer */
				|| Grep.parseAndSetProperties(this, PREFIX + LEVELS + COLON
						+ "(.+?)" + DOT + "(.*)", string, regions, "noteId", "dummy",
						"caller", "dummy", "levelString", "threadString",
						"levelString", "callerMnemonic", "callee", "message")
				/* No level/thread, but answer */
				|| Grep.parseAndSetProperties(this, PREFIX + COLON + "(.*)"
						+ EQ + "(.+?)" + DOT + "(.*)", string, regions, "noteId",
						"dummy", "caller", "answer", "callee", "message")
				/* No level/thread, no answer */
				|| Grep.parseAndSetProperties(this, PREFIX + COLON + "(.*?)"
						+ DOT + "(.*)", string, regions, "noteId", "dummy", "caller",
						"callee", "message")
				/* primitive with level */
				|| Grep.parseAndSetProperties(this, PREFIX + LEVELS + COLON
						+ "(.*)", string, regions, "noteId", "dummy", "caller", "dummy",
						"levelString", "threadString", "levelString",
						"callerMnemonic", "message")
				/* primitive without level */
				|| Grep.parseAndSetProperties(this, PREFIX + COLON + "(.*)",
						string, regions, "noteId", "dummy", "caller", "message")
				/* spawn with level */
				|| Grep.parseAndSetProperties(this, PREFIX + LEVELS + SPAWN
						+ "(.+?)" + DOT + "(.*)", string, regions, "noteId", "dummy",
						"spawner", "dummy", "levelString", "threadString",
						"levelString", "callerMnemonic", "callee", "message")
				/* spawn without level */
				|| Grep.parseAndSetProperties(this, PREFIX + SPAWN + "(.+?)"
						+ DOT + "(.*)", string, regions, "noteId", "dummy", "spawner",
						"callee", "message");

		if (!success) {
			throw new SyntaxError(null, "not a valid message");
		}
	}

	@Override
	public void setMessage(String message) {
		if (message.endsWith("&")) {
			setReturnsInstantly(true);
			message = message.substring(0, message.length() - 1);
		}
		super.setMessage(message);
	}

	@Override
	public void setCallee(String callee) {
		if (callee.length() >= 2 && callee.charAt(0) == '{'
				&& callee.charAt(callee.length() - 1) == '}') {
			setCallees(callee.substring(1, callee.length() - 1).split(","));

		} else {
			String[] parts = Grep.parse("(.*)\\[(\\D.*)\\]$", callee);
			if (parts == null) {
				super.setCallee(callee);
			} else {
				super.setCallee(parts[0]);
				super.setCalleeMnemonic(parts[1]);
			}
		}
	}

	/**
	 * Sets the spawning object.
	 * 
	 * @param spawner
	 *            the spawning object
	 */
	public void setSpawner(String spawner) {
		setSpawnMessage(true);
		setCaller(spawner);
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevelString(String level) {
		if (!level.equals("")) {
			// ignore mismatch
			setLevel(Integer.parseInt(level));
		}
	}

	public void setThreadString(String thread) {
		if (!thread.equals("")) {
			setThread(Integer.parseInt(thread));
		}
	}

	public void setDummy(String dummy) {

	}

	public void setNoteId(String noteId) {
		noteId = noteId.trim();
		if (!noteId.equals("")) {
			noteId = noteId.substring(1, noteId.length() - 1);
			String[] ids = noteId.split(",");
			if (ids[0].length() > 0) {
				setNoteNumber(Integer.parseInt(ids[0]));
			}
			if (ids.length > 1) {
				setAnswerNoteNumber(Integer.parseInt(ids[1]));
			}
		}
	}
	
	public Region getRegion (String property) {
	    return regions.get(property);
	}
}
// {{core}}
