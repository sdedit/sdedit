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
package net.sf.sdedit;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.DiagramDataProvider;
import net.sf.sdedit.diagram.IPaintDevice;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.error.SyntaxError;
import net.sf.sdedit.text.TextHandler;
import net.sf.sdedit.ui.ImagePaintDevice;
import net.sf.sdedit.ui.components.configuration.Bean;

public class SimpleDiagram {

	private String text;

	private Bean<Configuration> configuration;
	
	private IPaintDevice paintDevice;
	
	public SimpleDiagram(String text) {
		this.text = text;
		configuration = ConfigurationManager.createNewDefaultConfiguration();
	}

	public Configuration getConfiguration() {
		return configuration.getDataObject();
	}

	public void storeConfiguration() throws IOException {
		ConfigurationManager.LOCAL_DEFAULT.takeValuesFrom(configuration);
		ConfigurationManager.storeConfigurations();
	}
	
	public void saveToFile (String name, String format) throws SemanticError, SyntaxError, IOException {
		getImagePaintDevice().saveImage(format, name);
	}
	
	public Image toImage () throws SemanticError, SyntaxError {
		return getImagePaintDevice().getImage();
	}
	
	public static void main (String argv []) throws Throwable {
		InputStream in;
		OutputStream out;
		if (argv.length > 2) {
			throw new IllegalArgumentException ("Too many arguments.");
		}
		if (argv.length > 0) {
			in = new FileInputStream (argv [0]);
		} else {
			in = System.in;
		}
		if (argv.length == 2) {
			out = new FileOutputStream (argv [1]);
		} else {
			out = System.out;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		String line = reader.readLine();
		while (line != null) {
			pw.println(line);
			line = reader.readLine();
		}
		SimpleDiagram sd = new SimpleDiagram(sw.toString());
		sd.getImagePaintDevice().writeToStream("PNG", out);
		in.close();
		out.close();
	}
	
    private ImagePaintDevice getImagePaintDevice() throws SemanticError, SyntaxError {
		if (paintDevice instanceof ImagePaintDevice) {
			return (ImagePaintDevice) paintDevice;
		}
		DiagramDataProvider diagramDataProvider =
			new TextHandler(text);
		paintDevice = new ImagePaintDevice();
		Diagram diagram = new Diagram(configuration.getDataObject(), diagramDataProvider,
				paintDevice);
		diagram.generate();
		((ImagePaintDevice) paintDevice).drawAll();
		return (ImagePaintDevice) paintDevice;
	}
}

//{{core}}