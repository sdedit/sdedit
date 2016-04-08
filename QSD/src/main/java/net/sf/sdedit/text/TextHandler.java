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
package net.sf.sdedit.text;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.diagram.LifelineFlag;
import net.sf.sdedit.diagram.MessageData;
import net.sf.sdedit.diagram.SequenceDiagram;
import net.sf.sdedit.diagram.SequenceDiagramDataProvider;
import net.sf.sdedit.drawable.Note;
import net.sf.sdedit.error.SyntaxError;
import net.sf.sdedit.util.Grep;
import net.sf.sdedit.util.Grep.Region;
import net.sf.sdedit.util.Pair;

import static net.sf.sdedit.diagram.LifelineFlag.*;

/**
 * A <tt>DiagramDataProvider</tt> implementation, reading a diagram
 * specification from a single string.
 * 
 * @author Markus Strauch
 * 
 */
public class TextHandler extends AbstractTextHandler implements SequenceDiagramDataProvider {

	/* -1 = init, 0 = objects, 1 = messages */
	private int section;

	private String title;

	private String[] description;

	private Map<Lifeline, String> annotations;

	private int objectSectionEnd;
	
	private SequenceDiagram diagram;
	
	private final Grep grep;
	
	private Map<String,String> userData;

	/**
	 * Creates a new <tt>TextHandler</tt> for the given text.
	 * 
	 * @param text
	 *            a diagram specification
	 * 
	 */
	public TextHandler(String text) {
		super(text);
		grep = new Grep(Grep.DEFAULT_UNESCAPE);
		section = -1;
		annotations = new HashMap<Lifeline, String>();
		objectSectionEnd = 0;
		reset();
	}
	
	public SequenceDiagram getDiagram () {
		return diagram;
	}
	
	/**
	 * Sets the diagram instance that corresponds to the specification read by
	 * this <tt>TextHandler</tt>. This method is called inside
	 * {@linkplain SequenceDiagram#generate()}.
	 * 
	 * @param diagram
	 *            the diagram that corresponds to the specification read by this
	 *            <tt>TextHandler</tt>
	 */
	public void setDiagram(SequenceDiagram diagram) {
		this.diagram = diagram;
	}
	
	public Object getState() {
		return getLineBegin();
	}

	public int getObjectSectionEnd() {
		if (section > 0) {
			return objectSectionEnd;
		}
		return getLineNumber();
	}

	/**
	 * Resets the text handler so objects and messages can be read once again.
	 */
	protected void reset() {
		super.reset();
		String[] titleString = grep.parse("(?s).*#!\\[([^\n\r]*?)\\].*", text());
		if (titleString == null) {
			title = null;
		} else {
			title = titleString[0];
		}
		String[] descString = grep.parse("(?s).*#!>>(.*)#!<<.*", text());
		if (descString == null) {
			description = null;
		} else {
			description = descString[0].trim().split("\n");
		}
		for (int i = 0; description != null && i < description.length; i++) {
			description[i] = description[i].trim();
			if (!description[i].startsWith("#!")) {
				description = null;
			} else {
				description[i] = description[i].replaceFirst("#!", "");
			}
		}
		section = -1;
		annotations.clear();
	}

	/**
	 * @see net.sf.sdedit.diagram.SequenceDiagramDataProvider#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	public String[] getDescription() {
		return description;
	}

	/**
	 * Reads the next line in the specification. If another line is present, the
	 * next object reflecting the current line will be returned by a call to
	 * {@linkplain #nextObject()}, {@linkplain #nextMessage()},
	 * {@linkplain #getNote()}, {@linkplain #openFragment()} or
	 * {@linkplain #getEventAssociation()}.
	 * 
	 * @return flag denoting if another object or message line could be read
	 */
	public boolean advance() {
		return advance(true);
	}
	
	private void updateUserData (String txt) {
		if (txt.length() == 0) {
			userData = null;
		} else {
			userData = new HashMap<String, String>();
			for (String part : txt.split(";")) {
				int i = part.indexOf(':');
				if (i > 0) {
					String key = part.substring(0, i).trim();
					key = key.toLowerCase();
					String value = part.substring(i+1).trim();
					if (value.length() == 0) {
						userData.remove(key);
					} else {
						userData.put(key, value);
					}
				}
			}
		}
	}
	
	private boolean advance(boolean ignoreEmptyLines) {
		try {
			if (!ready()) {
				section = 1;
				return false;
			}
			String line;
			do {
				line = readLine();
				if (line == null) {
					section = 1;
					return false;
				}
				
				if (ignoreEmptyLines) {
					line = line.trim();
				}
				
				if (section == 1 && line.trim().startsWith("#!")) {
					updateUserData(line.trim().substring(2));
				}
				
				if (section == -1 && (line.equals("") || line.startsWith("#"))) {
					continue;
				}

				section = Math.max(0, section);
				if (section == 0) {
					int cmt = line.indexOf("#");
					if (cmt >= 0) {
						line = line.substring(0, cmt).trim();
					}
				}
				if (section == 0 && line.equals("")) {
					objectSectionEnd = getLineNumber() - 1;
					section = 1;
					return false;
				}
			} while (ignoreEmptyLines
					&& (line.equals("") || line.startsWith("#")));
			setCurrentLine(line);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String openFragment() {
		if (section == 0) {
			throw new IllegalStateException("not all objects have been read");
		}
		if (currentLine() == null) {
			throw new IllegalStateException("nothing to read");
		}
		if (currentLine().startsWith("//")) {
			System.err
					.println("Warning: Comments starting with // are deprecated. Use [c:<type> <text>]...[/c].");
			return currentLine().substring(2).trim();
		}
		if (currentLine().endsWith("]") && currentLine().startsWith("[c")) {
			return currentLine();
		}
		return null;
	}

	public String getFragmentSeparator() {
		if (section == 0) {
			throw new IllegalStateException("not all objects have been read");
		}
		if (currentLine() == null) {
			throw new IllegalStateException("nothing to read");
		}
		if (currentLine().startsWith("--")) {
			return currentLine().substring(2);
		}
		return null;
	}

	public boolean closeFragment() {
		if (section == 0) {
			throw new IllegalStateException("not all objects have been read");
		}
		if (currentLine() == null) {
			throw new IllegalStateException("nothing to read");
		}
		return currentLine().equals("\\\\") || currentLine().endsWith("]")
				&& currentLine().startsWith("[/c");
	}

	/**
	 * Returns the {@linkplain MessageData} object made from the current line.
	 * 
	 * @return the {@linkplain MessageData} object made from the current line
	 * @throws SyntaxError
	 *             if the next message cannot be parsed
	 */
	public MessageData nextMessage() throws SyntaxError {
		if (section == 0) {
			throw new IllegalStateException("not all objects have been read");
		}
		if (currentLine() == null) {
			throw new IllegalStateException("nothing to read");
		}
		MessageData data;
		try {
			data = new TextBasedMessageData(currentLine(), grep);
		} catch (SyntaxError e) {
			e.setProvider(this);
			throw e;
		}
		setCurrentLine(null);
		data.setUserData(userData);
		return data;
	}

	/**
	 * Returns the {@linkplain Lifeline} object made from the current line}.
	 * 
	 * @return the {@linkplain Lifeline} object made from the current line
	 * 
	 * @throws SyntaxError
	 *             if the next object declaration is not well-formed
	 */
	public Lifeline nextObject() throws SyntaxError {
		if (section == 1) {
			throw new IllegalStateException(
					"reading objects has already been finished");
		}
		if (currentLine() == null) {
			throw new IllegalStateException("nothing to read");
		}
		if (currentLine().indexOf(':') == -1) {
			throw new SyntaxError(this,
					"not a valid object declaration - ':' missing");
		}
		ArrayList<Region> regions = new ArrayList<Region>();
		String[] parts = grep.parse(
				"(\\/?.+?):([^\\[\\]]+?)\\s*(\\[.*?\\]|)\\s*(\".*\"|)",
				currentLine(), regions);
		if (parts == null || parts.length != 4) {
			String msg;
			if (currentLine().indexOf('.') >= 0) {
				msg = "not a valid object declaration, perhaps you forgot to "
						+ "enter an empty line before the message section";
			} else {
				msg = "not a valid object declaration";
			}
			throw new SyntaxError(this, msg);
		}
		setCurrentLine(null);
		String name = parts[0];
		String type = parts[1];
		String flags = parts[2];
		String label = parts[3];
		if (!label.equals("")) {
			label = label.substring(1, label.length() - 1);
		}
		
		if (flags.indexOf('f') >= 0) {
			throw new SyntaxError(this, "The f flag is not supported anymore. Use v (variable, the inverse of f[ixed]) instead.");
		}

		Set<LifelineFlag> lflags = LifelineFlag.getFlags(flags);
		
		Lifeline lifeline;

		if (name.startsWith("/")) {
			if (type.equals(Lifeline.ACTOR) || LifelineFlag.PROCESS.in(lflags)) {
				throw new SyntaxError(this,
						"processes and actors must be visible");
			}
			lflags.remove(LifelineFlag.THREAD);
			lflags.remove(LifelineFlag.PROCESS);
			lifeline = new Lifeline(name.substring(1), type, label, false,
					lflags,
					diagram);
		} else {

			if (type.equals(Lifeline.ACTOR)) {
				if (ANONYMOUS.in(lflags)) {
					throw new SyntaxError(this, "actors cannot be anonymous");
				}
			}
			if ((type.equals(Lifeline.ACTOR) || PROCESS.in(lflags)) && THREAD.in(lflags)) {
				throw new SyntaxError(this,
						"actors cannot have their own thread");
			}
			if ((type.equals(Lifeline.ACTOR) || PROCESS.in(lflags)) && AUTOMATIC.in(lflags)) {
				throw new SyntaxError(this,
						"actors cannot be (automatically) destroyed");
			}
			
			if (VARIABLE.in(lflags)) {
				throw new SyntaxError(this, "only objects that are created by a constructor can have a variable position");
			}

			lifeline = new Lifeline(parts[0], parts[1], label, true, lflags,
					diagram);
		}
		
		lifeline.setNameRegion(regions.get(0));

		int cmt = rawLine().indexOf("#!");
		if (cmt >= 0 && cmt + 2 < rawLine().length() - 1) {
			String annotation = rawLine().substring(cmt + 2).trim();
			annotations.put(lifeline, annotation);
		}

		return lifeline;
	}

	public String getAnnotation(Lifeline lifeline) {
		String name = annotations.get(lifeline);
		return name;
	}

	/**
	 * If there is a note specified at the current line and the subsequent
	 * lines, a {@linkplain Note} representation is returned, otherwise
	 * <tt>null</tt>
	 * 
	 * @return a note, if one is specified at the current position in text,
	 *         otherwise <tt>null</tt>
	 */
	public Note getNote() throws SyntaxError {
		if (section == 0) {
			throw new IllegalStateException("not all objects have been read");
		}
		if (currentLine() == null) {
			throw new IllegalStateException("nothing to read");
		}
		String[] parts = grep.parse("\\s*(\\*|\\+)(\\d+)\\s*(.+)", currentLine());
		if (parts == null) {
			return null;
		}
		boolean consuming = parts[0].equals("+");
		int number = -1;
		try {
			number = Integer.parseInt(parts[1]);
		} catch (NumberFormatException nfe) {
			/* empty */
		}
		if (number < 0) {
			throw new SyntaxError(this, "bad note number: " + parts[1]);
		}
		String obj = parts[2];
		Lifeline line = diagram.getLifeline(obj);
		if (line == null) {
			throw new SyntaxError(this, obj + " does not exist");
		}
		int oldBegin = getLineBegin();
		int oldEnd = getLineEnd();
		line = line.getRightmost();
		List<String> desc = new LinkedList<String>();
		boolean more;
		do {
			if (!advance(false)) {
				reset(oldBegin, oldEnd);
				throw new SyntaxError(this, "The note is not closed.");
			}
			more = !currentLine().trim().equals(parts[0] + parts[1]);
		} while (more && desc.add(currentLine()));
		if (desc.size() == 0) {
			reset(oldBegin, oldEnd);
			throw new SyntaxError(this, "The note is empty.");
		}
		String[] noteText = desc.toArray(new String[0]);
		URI link = null;
		if (noteText.length == 1) {
			String linkString = noteText[0].trim();

			if (linkString.startsWith("link:")) {
				try {
					linkString = linkString.substring(5).trim();

					link = new URI(linkString);
					if (link.getPath() == null) {
						throw new SyntaxError(this, "Empty path in URI: "
								+ linkString);
					}
					noteText[0] = link.getPath();

				} catch (URISyntaxException e) {
					throw new SyntaxError(this, "Bad URI syntax: "
							+ e.getMessage());
				}
			}
		}
		Note note = new Note(line, number, noteText, consuming);
		note.setLink(link);
		return note;
	}

	/**
	 * If the current line specifies an association of a note to the current
	 * vertical position of a lifeline, this method returns a pair consisting of
	 * the lifeline and the note number.
	 * 
	 * @return a pair of a lifeline and a note number, if the current line
	 *         specifies and association between the note and the lifeline,
	 *         otherwise <tt>null</tt>
	 */
	public Pair<Lifeline, Integer> getEventAssociation() throws SyntaxError {
		if (section == 0) {
			throw new IllegalStateException("not all objects have been read");
		}
		if (currentLine() == null) {
			throw new IllegalStateException("nothing to read");
		}
		String[] parts = grep.parse("\\((\\d+)\\)\\s*(\\w+)", currentLine());
		if (parts == null) {
			return null;
		}
		int number = Integer.parseInt(parts[0]);
		String obj = parts[1];
		Lifeline line = diagram.getLifeline(obj);
		if (line == null) {
			throw new SyntaxError(this, obj + " does not exist");
		}
		return new Pair<Lifeline, Integer>(line, number);
	}

	public boolean pass() {
		return false;
	}

}
// {{core}}
