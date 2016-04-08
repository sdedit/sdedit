//Copyright (c) 2006 - 2016, Markus Strauch.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.
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
