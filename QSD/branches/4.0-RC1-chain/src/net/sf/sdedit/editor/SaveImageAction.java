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

import static net.sf.sdedit.editor.Shortcuts.EXPORT_IMAGE;
import static net.sf.sdedit.editor.Shortcuts.getShortcut;
import static net.sf.sdedit.ui.components.buttons.ManagedAction.ICON_NAME;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Action;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.DiagramDataProvider;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.ui.ImagePaintDevice;
import net.sf.sdedit.ui.UserInterface;
import net.sf.sdedit.ui.components.buttons.ManagedAction;
import net.sf.sdedit.ui.impl.DiagramTab;
import net.sf.sdedit.util.UIUtilities;

@SuppressWarnings("serial")
public class SaveImageAction extends TabAction<DiagramTab> {

	private boolean firstImageSaved;

	public SaveImageAction(UserInterface ui) {
		super(DiagramTab.class, ui);
		putValue(ICON_NAME, "image");
		putValue(ManagedAction.ID, "EXPORT_PNG");
		putValue(Action.NAME, getShortcut(EXPORT_IMAGE) + "&Export as PNG...");
		putValue(Action.SHORT_DESCRIPTION, "Export the diagram as a PNG image");

	}

	@Override
	protected void _actionPerformed(DiagramTab tab, ActionEvent e) {
		try {
			saveImage(tab);
		} catch (IOException ioe) {
			ui.errorMessage(ioe, null, null);
		}

	}

	/**
	 * Saves the current diagram as a PNG image file whose name is chosen by the
	 * user. Asks for confirmation, if a file would be overwritten.
	 * 
	 * @throws IOException
	 *             if the image file cannot be written due to an i/o error
	 */
	void saveImage(DiagramTab tab) throws IOException {
		ImagePaintDevice ipd = new ImagePaintDevice();
		DiagramDataProvider ddp = tab.getProvider();
		Configuration conf = tab.getConfiguration().getDataObject();
		try {
			new Diagram(conf, ddp, ipd).generate();
		} catch (RuntimeException re) {
			throw re;
		} catch (DiagramError de) {
			ui.errorMessage(de, null, "The diagram source has errors.");
			return;
		}
		Image image = ipd.getImage();
		if (image != null) {
			File current = null;
			if (!firstImageSaved) {
				current = tab.getFile();
				if (current != null) {
					current = current.getParentFile();
				}
				firstImageSaved = true;
			}
			String currentFile = null;
			if (tab.getFile() != null) {
				currentFile = UIUtilities.affixType(tab.getFile(), "png")
						.getName();
			}
			File[] files = tab.getFileHandler().getFiles(false, false,
					"save as PNG", currentFile, "PNG image", "png");
			File imageFile = files != null ? files[0] : null;
			if (imageFile != null
					&& (!imageFile.exists() || 1 == ui
							.confirmOrCancel("Overwrite existing file "
									+ imageFile.getName() + "?"))) {
				ipd.saveImage("PNG", imageFile);
				ui.message("Exported image as\n" + imageFile.getAbsolutePath());
			}
		}
	}

}
