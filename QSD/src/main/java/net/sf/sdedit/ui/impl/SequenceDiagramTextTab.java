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
import net.sf.sdedit.editor.EditorHintFactory;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.error.FatalError;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.error.SequenceDiagramError;
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
			setError(false, "", -1, -1, null);
			if (diagram().getFragmentManager().openFragmentsExist()) {
				setError(
						true,
						"Warning: There are open comments. Use [c:<type> <text>]...[/c]",
						-1, -1, null);
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
					handler.getLineBegin(), handler.getLineEnd(),
					EditorHintFactory.createHint(this,
							(SequenceDiagramError) error));
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
