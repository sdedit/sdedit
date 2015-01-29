package net.sf.sdedit.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public abstract class AbstractTextHandler {
	
	private BufferedReader reader;

	private String text;

	private String rawLine;

	private String currentLine;

	private int lineBegin;

	private int lineEnd;
	
	private int lineNumber;
	
	public AbstractTextHandler (String text) {
		lineBegin = 0;
		lineEnd = -1;
		lineNumber = 0;
		this.text = text;
	}
	
	/**
	 * Returns the index of the first position of the current line in the
	 * specification.
	 * 
	 * @return the index of the first position of the current line in the
	 *         specification
	 */
	public int getLineBegin() {
		return lineBegin;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}

	public String getText() {
		return text;
	}
	
	protected final String currentLine () {
		return currentLine;
	}
	
	protected final void setCurrentLine (String currentLine) {
		this.currentLine = currentLine;
	}
	
	protected final String text() {
		return text;
	}
	
	protected final String rawLine () {
		return rawLine;
	}

	/**
	 * Returns the index of the last position of the current line in the
	 * specification string.
	 * 
	 * @return the index of the last position of the current line in the
	 *         specification string
	 */
	public int getLineEnd() {
		return lineEnd;
	}

	/**
	 * Returns the line that is currently read.
	 * 
	 * @return the line that is currently read
	 */
	public String getCurrentLine() {
		return currentLine;
	}
	
	protected void reset () {
		lineBegin = 0;
		lineEnd = -1;
		currentLine = null;
		rawLine = null;
		reader = new BufferedReader(new StringReader(text));
	}
	
	protected final String readLine () {
		try {
		    if (lineEnd >= 0) {
		        lineBegin = lineEnd + 1;    
		    }
			rawLine = reader.readLine();
			if (rawLine != null) {
				lineNumber++;
				lineEnd = lineBegin + rawLine.length();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return rawLine;
	}
	
	protected final boolean ready () {
		try {
			return reader.ready();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	protected final void reset (int oldBegin, int oldEnd) {
		this.lineBegin = oldBegin;
		this.lineEnd = oldEnd;
	}
	

}
