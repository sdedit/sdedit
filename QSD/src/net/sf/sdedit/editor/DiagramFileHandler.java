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
package net.sf.sdedit.editor;

import java.awt.Font;
import java.io.File;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.config.SequenceConfiguration;
import net.sf.sdedit.editor.plugin.AbstractFileHandler;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.impl.DiagramTextTab;
import net.sf.sdedit.ui.impl.SequenceDiagramTextTab;

public class DiagramFileHandler extends AbstractFileHandler {

	public String[] getFileDescriptions() {
		return new String[] { "Plain sequence diagram files (.sd)",
				"Sequence diagram files with preferences (.sdx)" };
	}

	public String[] getFileTypes() {
		return new String[] { "sd", "sdx" };
	}

	public String getOpenActionName() {
		return "&Open diagram...";
	}

	public String getOpenDescription() {
		return "Load sequence diagram file(s)";
	}

	public String getOpenShortCut() {
		return Shortcuts.getShortcut(Shortcuts.OPEN);
	}

	public String getSaveActionName() {
		return "&Save sequence diagram";
	}

	public String getSaveAsActionName() {
		return "S&ave sequence diagram as";
	}

	public String getSaveAsDescription() {
		return "Save sequence diagram as...";
	}

	public String getSaveAsShortCut() {
		return Shortcuts.getShortcut(Shortcuts.SAVE_AS);
	}

	public String getSaveDescription() {
		return "Save the diagram source text";
	}

	public String getSaveShortCut() {
		return Shortcuts.getShortcut(Shortcuts.SAVE);
	}

	public boolean canLoad() {
		return true;
	}

	public boolean canSave() {
		return true;
	}

	public String getOpenID() {
		return "OPEN";
	}

	@Override
	protected DiagramTextTab createTab(Font editorFont,
			Bean<? extends Configuration> configuration) {
		return new SequenceDiagramTextTab(getUI(), editorFont, configuration);
	}
	
	@Override
	protected boolean isXML(File file) {
		return file.getName().toLowerCase().endsWith("sdx");
	}

	@Override
	protected Bean<? extends Configuration> createNewConfiguration() {
		return ConfigurationManager
				.createNewDefaultConfiguration(SequenceConfiguration.class);
	}

}
