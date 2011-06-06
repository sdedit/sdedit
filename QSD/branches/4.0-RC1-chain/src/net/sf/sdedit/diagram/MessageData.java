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
package net.sf.sdedit.diagram;

import net.sf.sdedit.message.BroadcastMessage;

/**
 * A <tt>MessageData</tt> object encapsulates all data characterizing a
 * message that is displayed by a sequence diagram.
 * 
 * @author Markus Strauch
 */
public class MessageData {

	private String caller;

	private String callee;

	private int level;

	private int thread;

	private boolean spawnMessage;

	private String answer;

	private String message;

	private String callerMnemonic;

	private String calleeMnemonic;

	private String[] callees;

	private boolean returnsInstantly;

	private int noteNumber;

	private int answerNoteNumber;

	private int broadcastType;
	
	private boolean isStatic;
	
	private boolean isBold;
	
	public MessageData() {
		// default values (some properties are not mandatory, if left out by
		// the user, the default remains assigned
		caller = "";
		callee = "";
		callerMnemonic = "";
		calleeMnemonic = "";
		noteNumber = 0;
		level = 0;
		answer = "";
		message = "";
		callees = new String[0];
		spawnMessage = false;
		thread = -1;
		answerNoteNumber = 0;
		broadcastType = 0;
		isStatic = false;
		isBold = false;
	}
	
	/**
	 * Returns a string representation of the message data.
	 * TODO: Broadcasts, mnemonics, (answer) note number,
	 * escaping
	 */
	public String toString () {
		String string = caller;
		if (level > 0 || thread != -1) {
			string += "[";
		}
		if (level > 0) {
			string += level;
		}
		if (thread != -1) {
			string += "," + thread;
		}
		if (level > 0 || thread != -1) {
			string += "]";
		}
		string += ":";
		if (spawnMessage) {
			string += ">";
		}
		if (answer.length() > 0) {
			string += answer + "=";
		}
		if (callee.length() > 0) {
			string += callee + ".";
		}
		string += message;
		
		if (returnsInstantly) {
			string += "&";
		}
		return string;
	}
	
	public void setStatic (boolean isStatic) {
		this.isStatic = isStatic;
	}
	
	public boolean isStatic () {
		return isStatic;
	}
	
	public void setBold (boolean isBold) {
		this.isBold = isBold;
	}
	
	public boolean isBold () {
		return isBold;
	}

	/**
	 * Returns true if the message starts with &quot;new(&quot; or if it equals
	 * &quot;new&quot;
	 * 
	 * @return true if the message starts with &quot;new(&quot; or if it equals
	 *         &quot;new&quot;
	 */
	public boolean isNewMessage() {
		if (message == null) {
			throw new IllegalStateException("no message present");
		}
		return message.startsWith("new(") || message.equals("new");
	}

	/**
	 * Returns true if the message starts with &quot;destroy(&quot; or if it
	 * equals &quot;destroy&quot;
	 * 
	 * @return true if the message starts with &quot;destroy(&quot; or if it
	 *         equals &quot;destroy&quot;
	 */
	public boolean isDestroyMessage() {
		if (message == null) {
			throw new IllegalStateException("no message present");
		}
		return !callee.equals("")
				&& (message.equals("destroy") || message.startsWith("destroy("));
	}
	
	public boolean isReturning () {
		return callee.equals("") &&
		  message.equals("-") &&
		  answer.equals("") &&
		  level == 0;
	}

	/**
	 * @return the answer
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * @param answer
	 *            the answer to set
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**
	 * @return the callee
	 */
	public String getCallee() {
		return callee;
	}

	/**
	 * @param callee
	 *            the callee to set
	 */
	public void setCallee(String callee) {
		this.callee = callee;
	}

	/**
	 * @return the caller
	 */
	public String getCaller() {
		return caller;
	}

	/**
	 * @param caller
	 *            the caller to set
	 */
	public void setCaller(String caller) {
		this.caller = caller;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	public void setNoteNumber(int number) {
		noteNumber = number;
	}

	public int getNoteNumber() {
		return noteNumber;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the spawnMessage
	 */
	public boolean isSpawnMessage() {
		return spawnMessage;
	}

	/**
	 * @param spawnMessage
	 *            the spawnMessage to set
	 */
	public void setSpawnMessage(boolean spawnMessage) {
		this.spawnMessage = spawnMessage;
	}

	/**
	 * @return the thread
	 */
	public int getThread() {
		return thread;
	}

	/**
	 * @param thread
	 *            the thread to set
	 */
	public void setThread(int thread) {
		this.thread = thread;
	}

	/**
	 * Returns a mnemonic for the callee object, if one is defined in this
	 * message, otherwise an empty string.
	 * 
	 * @return a mnemonic for the callee object
	 */
	public String getCalleeMnemonic() {
		return calleeMnemonic;
	}

	/**
	 * Sets a mnemonic for the callee object.
	 * 
	 * @param calleeMnemonic
	 *            a mnemonic for the callee object
	 */
	public void setCalleeMnemonic(String calleeMnemonic) {
		this.calleeMnemonic = calleeMnemonic;
	}

	/**
	 * Returns the mnemonic identifying this message's caller object, if there
	 * is such a mnemonic, otherwise an empty string.
	 * 
	 * @return the mnemonic identifying this message's caller object
	 */
	public String getCallerMnemonic() {
		return callerMnemonic;
	}

	/**
	 * Sets the mnemonic identifying this message's caller object.
	 * 
	 * @param callerMnemonic
	 *            the mnemonic identifying this message's caller object
	 * 
	 */
	public void setCallerMnemonic(String callerMnemonic) {
		this.callerMnemonic = callerMnemonic;
	}

	public void setReturnsInstantly(boolean on) {
		returnsInstantly = on;
	}

	public boolean returnsInstantly() {
		return returnsInstantly;
	}

	public int getAnswerNoteNumber() {
		return answerNoteNumber;
	}

	public void setAnswerNoteNumber(int answerNoteNumber) {
		this.answerNoteNumber = answerNoteNumber;
	}
	
	/**
	 * Returns an array of length at least 2 if this <tt>MessageData</tt>
	 * specifies a {@linkplain BroadcastMessage}, otherwise an empty array.
	 * 
	 * @return an array of length at least 2 if this <tt>MessageData</tt>
	 *         specifies a {@linkplain BroadcastMessage}, otherwise an empty
	 *         array
	 */
	public String[] getCallees() {
		return callees;
	}

	public void setCallees(String[] callees) {
		this.callees = callees;
	}

	/**
	 * Returns an integer number indicating if this <tt>MessageData</tt>
	 * specifies a message from the sender of a broadcast message to a single
	 * receiver. <TABLE BORDER="1">
	 * <TR>
	 * <TH>value</TH>
	 * <TH>interpretation</TH>
	 * </TR>
	 * <TR>
	 * <TD>0</TD>
	 * <TD>not a part of a broadcast</TD>
	 * </TR>
	 * <TR>
	 * <TD>1</TD>
	 * <TD>the first part of a broadcast</TD>
	 * </TR>
	 * <TR>
	 * <TD>2</TD>
	 * <TD>a part of a broadcast, but<BR>
	 * neither the first nor the last</TD>
	 * </TR>
	 * <TR>
	 * <TD>3</TD>
	 * <TD>the last part of a broadcast</TD>
	 * </TR>
	 * </TABLE>
	 * 
	 * @return an integer number indicating if this <tt>MessageData</tt>
	 *         specifies a message from the sender of a broadcast message to a
	 *         single receiver.
	 */
	public int getBroadcastType() {
		return broadcastType;
	}

	/**
	 * Sets an integer number indicating if this <tt>MessageData</tt>
	 * specifies a message from the sender of a broadcast message to a single
	 * receiver. <TABLE BORDER="1">
	 * <TR>
	 * <TH>value</TH>
	 * <TH>interpretation</TH>
	 * </TR>
	 * <TR>
	 * <TD>0</TD>
	 * <TD>not a part of a broadcast</TD>
	 * </TR>
	 * <TR>
	 * <TD>1</TD>
	 * <TD>the first part of a broadcast</TD>
	 * </TR>
	 * <TR>
	 * <TD>2</TD>
	 * <TD>a part of a broadcast, but<BR>
	 * neither the first nor the last</TD>
	 * </TR>
	 * <TR>
	 * <TD>3</TD>
	 * <TD>the last part of a broadcast</TD>
	 * </TR>
	 * </TABLE>
	 * 
	 * @param broadcastType
	 *            an integer number indicating if this <tt>MessageData</tt>
	 *            specifies a message from the sender of a broadcast message to
	 *            a single receiver
	 */
	public void setBroadcastType(int broadcastType) {
		this.broadcastType = broadcastType;
	}

}
//{{core}}
