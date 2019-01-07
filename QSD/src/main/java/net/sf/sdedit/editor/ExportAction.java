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

package net.sf.sdedit.editor;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import net.sf.sdedit.Constants;
import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.ui.components.buttons.ManagedAction;
import net.sf.sdedit.ui.impl.DiagramTab;

public class ExportAction extends TabAction<DiagramTab> implements Constants {

	private static final long serialVersionUID = -8534175701798362565L;

	private ExportDialog exportDialog;

	public ExportAction(Editor editor) {
		super(DiagramTab.class, editor.getUI());
		putValue(Action.NAME, Shortcuts.getShortcut(Shortcuts.EXPORT) + "E&xport...");
		putValue(ManagedAction.ICON_NAME, "image");
		putValue(ManagedAction.ID, "EXPORT");
		putValue(Action.SHORT_DESCRIPTION, "Export diagram as bitmap or vector graphics");
	}

	protected void _actionPerformed(DiagramTab tab, ActionEvent e) {
		Diagram diagram = tab.getDiagram();
		if (diagram == null) {
			return;
		}
		if (exportDialog == null) {
			exportDialog = new ExportDialog(tab);
			exportDialog.getConfiguration().setType("png");		
			exportDialog.getConfiguration().setFile(null);
		}
		exportDialog.open(tab);

	}

}
