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
package net.sf.sdedit.multipage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.PrintConfiguration;
import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.DiagramDataProvider;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.error.SyntaxError;
import net.sf.sdedit.ui.components.ZoomPane;

import org.freehep.graphicsio.PageConstants;

@SuppressWarnings("unchecked")
public class MultipageExporter extends JPanel {

	private static Class<? extends Graphics2D> ps;

	private static Class<? extends Graphics2D> pdf;

	
	static {
		
		try {
			ps = (Class<? extends Graphics2D>) Class
					.forName("org.freehep.graphicsio.ps.PSGraphics2D");
			pdf = (Class<? extends Graphics2D>) Class
					.forName("org.freehep.graphicsio.pdf.PDFGraphics2D");
		} catch (RuntimeException re) {
			throw re;
		} catch (ClassNotFoundException ignored) {
			/* empty */
		}
	}

	public static boolean isAvailable() {
		return pdf != null;
	}

	private DiagramDataProvider provider;

	private Configuration configuration;

	private Dimension size;

	private MultipagePaintDevice paintDevice;

	private double scale;

	private Dimension previewSize;

	private PrintConfiguration properties;

	public MultipageExporter(PrintConfiguration properties, DiagramDataProvider provider,
			Configuration configuration) {
		super();
		this.properties = properties;
		this.provider = provider;
		this.configuration = configuration;
		size = PageConstants.getSize(properties.getFormat(), properties
				.getOrientation());
		// 149 / 210
		// 223 / 315
		double wide = 315D;
		if (properties.getOrientation().equals(PageConstants.PORTRAIT)) {
			scale = wide / size.height;
			previewSize = new Dimension(223, 315);
		} else {
			scale = wide / size.width;
			previewSize = new Dimension(315, 223);
		}
	}

	public double getScale() {
		return paintDevice.getScale();
	}

	public void init() throws SyntaxError, SemanticError {
		paintDevice = new MultipagePaintDevice(properties, size);
		new Diagram(configuration, provider, paintDevice).generate();
		int n = paintDevice.getPanels().size();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		int i = 0;
		for (MultipagePaintDevice.MultipagePanel panel : paintDevice
				.getPanels()) {
			i++;
			JPanel wrap = new JPanel();
			wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
			wrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
			wrap.setAlignmentY(0.5F);

			ZoomPane zoomPane = new ZoomPane(false);
			zoomPane.setViewportView(panel);
			zoomPane.setScale(scale);
			zoomPane.setMinimumSize(previewSize);
			zoomPane.setMaximumSize(previewSize);
			zoomPane.setPreferredSize(previewSize);
			zoomPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			wrap.add(zoomPane);

			JLabel label = new JLabel(i + "/" + n);
			label.setAlignmentX(0.5F);

			wrap.add(label);

			add(wrap);
		}
	}

	public void exportTo(OutputStream stream, String type) throws IOException {
		// OutputStream stream = new FileOutputStream(file);
		Class<? extends Graphics2D> gc = type.toLowerCase().equals("pdf") ? pdf
				: ps;
		ExportDocument export = new ExportDocument(gc, paintDevice, stream,
				properties.getFormat(), properties.getOrientation());
		export.export();
	}
}
