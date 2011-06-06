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

package net.sf.sdedit.error;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import net.sf.sdedit.editor.EditorHint;
import net.sf.sdedit.ui.impl.DiagramTextTab;

public class AddObjectHint extends EditorHint {

	private String objectName;

	private int lineNumber;

	public AddObjectHint(DiagramTextTab tab, String objectName, int lineNumber) {
		super(tab, "Create " + objectName);
		this.objectName = objectName;
		this.lineNumber = lineNumber;
	}

	@Override
	public void execute() {
		String code = getTab().getCode();
		int cursor = getTab().getCursorPosition();
		try {
			StringReader reader = new StringReader(code);
			StringWriter writer = new StringWriter();
			BufferedReader bReader = new BufferedReader(reader);
			PrintWriter pWriter = new PrintWriter(writer);
			int l = 0;
			String line = bReader.readLine();
			while (line != null) {
				if (l == lineNumber) {
					pWriter.println(objectName + ":Object");
				}
				pWriter.println(line);
				l++;
				line = bReader.readLine();
			}
			getTab().setCode(writer.toString());
			reader.close();
			writer.close();
		} catch (IOException ignored) {
			/* empty */
		}
		getTab().moveCursorToPosition(
				cursor + objectName.length() + 8);
	}
}
