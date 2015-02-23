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

package net.sf.sdedit.taglet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.config.SequenceConfiguration;
import net.sf.sdedit.diagram.DiagramFactory;
import net.sf.sdedit.diagram.PaintDevice;
import net.sf.sdedit.diagram.SequenceDiagramFactory;
import net.sf.sdedit.server.Exporter;
import net.sf.sdedit.text.TextHandler;
import net.sf.sdedit.util.DocUtil;
import net.sf.sdedit.util.PWriter;
import net.sf.sdedit.util.Utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.javadoc.Tag;

/**
 * This is a taglet that generates sequence diagrams from the contents of
 * <tt>@sequence.diagram</tt> tags and inserts them as inline SVG into the
 * javadoc output.
 * <p>
 * If the first line of the tag content is "quoted" it will be used as title for
 * the diagram instead of the default "Sequence Diagram:"
 * <p>
 * <tt>@sequence.diagram</tt> are no inline tags and they can be used inside
 * classes, and inside package documentation.
 * 
 * @sequence.diagram <code>
 * "Processing of <tt>@sequence.diagram</tt> tags"
 * user:Actor
 * javadoc:Javadoc[a] 
 * sourceFile:File 
 * /docFile:File
 * taglet:SequenceTaglet
 * 
 * user:javadoc.generateDocumentation() 
 * [c:loop for all source files] 
 *   javadoc:sourceFile.read() 
 *   javadoc:docFile.new()
 *   [c:loop "sequence.diagram" tag found]
 *     javadoc:output=taglet.getTagletOutput(tag)
 *     javadoc:docFile.append(output)
 *   [/c]
 * [/c]
 * </code>
 * 
 * @author Markus Strauch
 * @author Øystein Lunde
 */
public abstract class SequenceTaglet {

	private static final String tagName = "sequence.diagram";

	/**
	 * Registers an instance of this taglet class.
	 * 
	 * @param tagletMap
	 *            used for registering (maps tag names onto taglets)
	 */
	@SuppressWarnings("unchecked")
	public static void register(@SuppressWarnings("rawtypes") Map tagletMap) {
		SequenceTaglet taglet;
		try {
			taglet = Utilities.newInstance(
					"net.sf.sdedit.taglet.SequenceTaglet7",
					SequenceTaglet.class);
		} catch (Throwable t) {
			taglet = Utilities.newInstance(
					"net.sf.sdedit.taglet.SequenceTaglet6",
					SequenceTaglet.class);
		}
		tagletMap.put(taglet.getName(), taglet);
	}

	protected SequenceTaglet() {
	}

	/**
	 * Creates a sequence diagram image from a part of the contents of a
	 * sequence.diagram tag, saves it in the image directory and returns HTML
	 * code that references the image.
	 * 
	 * 
	 * @param path
	 *            the path to the directory where the diagram image is to be
	 *            stored (usually a path going upwards, i. e. containing ../'s)
	 * @param imageBaseName
	 *            the base name of the image to be stored
	 * @param source
	 *            string array containing the lines of the diagram specification
	 * @return the output that is to appear on the javadoc page (i. e. the
	 *         &lt;img&gt; tag referencing the image
	 * @throws SequenceTagletException
	 *             if the creation of the diagram fails
	 */
	private String generateOutput(String[] source)
			throws SequenceTagletException {
		System.out.println("generate output");
		if (source == null || source.length == 0) {
			return "";
		}
		PWriter writer = PWriter.create();
		writer.setLineSeparator("\n");
		String diagramTitle = null;
		for (String string : source) {
			string = string.trim();
			if (string.startsWith("<") && string.endsWith(">")) {
				continue;
			}
			if (string.matches("^[\"'].*[\"']$")) {
				diagramTitle = string.replaceAll("[\"']", "");
				continue;
			}
			writer.println(string);
		}
		writer.flush();
		writer.close();
		String specification = writer.toString();
		if (specification.length() == 0) {
			return "";
		}
		System.out.println(specification);
		SequenceConfiguration conf = ConfigurationManager
				.createNewDefaultConfiguration(SequenceConfiguration.class)
				.getDataObject();
		conf.setHeadWidth(25);
		conf.setMainLifelineWidth(5);
		conf.setSubLifelineWidth(5);
		conf.setThreaded(true);
		conf.setGlue(3);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Exporter exporter = Exporter.getExporter("svg", "Landscape", "A4",
				stream);
		PaintDevice paintDevice = new PaintDevice(exporter);
		DiagramFactory factory = new SequenceDiagramFactory(specification,
				paintDevice);

		try {
			factory.generateDiagram(conf);
		} catch (Exception e) {

			e.printStackTrace();
			TextHandler handler = (TextHandler) factory.getProvider();
			int error = handler.getLineNumber();
			StringBuffer code = new StringBuffer("<br><tt>");
			for (int i = 0; i < source.length; i++) {
				String html = source[i].replaceAll("&", "&amp;")
						.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
						.replaceAll("\"", "&quot;");
				if (i == error) {
					html = "<FONT COLOR=\"red\"><U><B>" + html
							+ "</B></U></FONT>";
				}
				code.append(html + "<br>");
			}
			throw new SequenceTagletException(
					"Malformed diagram specification: " + e.getMessage(),
					"<DT><HR><B>Sequence Diagram:</B></DT>"
							+ "<DD><B>Could not create sequence diagram: "
							+ "<font color=\"red\">" + e.getMessage()
							+ "</font></B>" + code.toString() + "</DD>");
		}

		try {
			exporter.export();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "";
		}
		ByteArrayInputStream in = new ByteArrayInputStream(stream.toByteArray());
		Document svg;

		try {
			svg = DocUtil.readDocument(in, "utf-8");
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		Element root = svg.getDocumentElement();
		String s = DocUtil.toString(root, true);
		return "<DT><HR><B>" + diagramTitle + ":</B><P></DT>" + "<DD>" + s
				+ "</DD>";

	}

	public String getName() {
		return tagName;
	}

	public boolean inConstructor() {
		return true;
	}

	public boolean inField() {
		return true;
	}

	public boolean inMethod() {
		return true;
	}

	public boolean inOverview() {
		return false;
	}

	public boolean inPackage() {
		return true;
	}

	public boolean inType() {
		return true;
	}

	public boolean isInlineTag() {
		return false;
	}

	protected String makeString(Tag tag) throws SequenceTagletException {
		String output;
		output = generateOutput(tag.text().split("\n"));
		return output;
	}

	protected static class SequenceTagletException extends Exception {

		private static final long serialVersionUID = 1L;
		/**
		 * Appears on the HTML page
		 */
		String output;

		/**
		 * 
		 * @param warning
		 *            the warning that is sent to the doclet
		 * @param output
		 *            the output that appears on the HTML page
		 */
		SequenceTagletException(String warning, String output) {
			super(warning);
			this.output = output;
		}
	}
}