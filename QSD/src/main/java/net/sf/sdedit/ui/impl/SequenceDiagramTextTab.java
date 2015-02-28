//Copyright (c) 2006 - 2015, Markus Strauch.
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
package net.sf.sdedit.ui.impl;

import java.awt.Font;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.diagram.AbstractPaintDevice;
import net.sf.sdedit.diagram.DiagramDataProvider;
import net.sf.sdedit.diagram.DiagramFactory;
import net.sf.sdedit.diagram.GraphicDevice;
import net.sf.sdedit.diagram.IPaintDevice;
import net.sf.sdedit.diagram.PaintDevice;
import net.sf.sdedit.diagram.SequenceDiagram;
import net.sf.sdedit.diagram.SequenceDiagramFactory;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.error.FatalError;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.text.AbstractTextHandler;
import net.sf.sdedit.text.TextHandler;
import net.sf.sdedit.ui.PanelGraphicDevice;
import net.sf.sdedit.ui.components.configuration.Bean;

public class SequenceDiagramTextTab extends DiagramTextTab {

	private static final long serialVersionUID = 5278509849011224397L;

	public SequenceDiagramTextTab(UserInterfaceImpl ui, Font codeFont,
			Bean<? extends Configuration> configuration) {
		super(ui, codeFont, configuration);
	}

	public DiagramDataProvider createProvider() {
		return new TextHandler(getCode());
	}

	private SequenceDiagram diagram() {
		return (SequenceDiagram) getDiagram();
	}

	@Override
	protected boolean _handleDiagramError(DiagramError error) {
		if (error == null) {
			setError(false, "", -1, -1);
			if (diagram().getFragmentManager().openFragmentsExist()) {
				setError(
						true,
						"Warning: There are open comments. Use [c:<type> <text>]...[/c]",
						-1, -1);
			}

			int noteNumber = diagram().getNextFreeNoteNumber();
			if (noteNumber == 0) {
				setStatus("");
			} else {
				setStatus("Next note number: "
						+ diagram().getNextFreeNoteNumber());
			}
		} else if (!(error instanceof FatalError)) {
			AbstractTextHandler handler = (TextHandler) error.getProvider();
			String prefix = "";
			if (error instanceof SemanticError) {
				prefix = diagram().isThreaded()
						&& diagram().getCallerThread() != -1 ? "Thread "
						+ diagram().getCallerThread() + ": " : "";
			}
			setError(false, prefix + error.getMessage(),
					handler.getLineBegin(), handler.getLineEnd());
		} else {
			return false;
		}
		return true;
	}

	@Override
	public DiagramFactory createFactory(IPaintDevice paintDevice) {
		return new SequenceDiagramFactory(this, paintDevice);
	}

	@Override
	public String getCategory() {
		return "Sequence diagrams";
	}

	@Override
	public AbstractPaintDevice createPaintDevice(GraphicDevice graphicDevice) {
		if (graphicDevice == null) {
			PanelGraphicDevice ppd = new PanelGraphicDevice(true);
			if (getInteraction() != null) {
				ppd.setPartner(getInteraction());
			}
			graphicDevice = ppd;
		}
		return new PaintDevice(graphicDevice);
	}

}
