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
package net.sf.sdedit.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import net.sf.sdedit.Constants;

public class ImagePaintDevice extends Graphics2DPaintDevice implements
		Constants {

	private final static RenderingHints AALIAS = new RenderingHints(
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	private Image image;

	private boolean antialias;

	public ImagePaintDevice() {
		this(true);
	}

	public ImagePaintDevice(boolean antialias) {
		this.antialias = antialias;
	}

	public Image getImage() {
		return image;
	}

	public void writeToStream(String type, OutputStream stream)
			throws IOException {
		drawAll();
		ImageIO.write((RenderedImage) image, type, stream);
	}

	public void saveImage(String type, String fileName) throws IOException {
		saveImage(type, new File(fileName));
	}

	public void saveImage(String type, File file) throws IOException {
		OutputStream os = new FileOutputStream(file);
		try {
			writeToStream(type, os);
		} finally {
			os.close();
		}
	}

	@Override
	protected Graphics2D createDummyGraphics(boolean bold) {
		Image img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		Font font = getFont(bold);
		g2d.setFont(font);
		return g2d;
	}

	@Override
	protected Graphics2D createGraphics() {
		// if (getDiagram().isThreaded()) {
		image = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		// } else {
		// image = new BufferedImage(getWidth(), getHeight(),
		// BufferedImage.TYPE_USHORT_GRAY);
		// }
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		if (antialias) {
			g2d.setRenderingHints(AALIAS);
		}
		g2d.setFont(getFont(false));
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		return g2d;
	}
}
// {{core}}
