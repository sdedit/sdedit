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

package net.sf.sdedit.util;

import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.DiagramDataProvider;
import net.sf.sdedit.diagram.IPaintDevice;
import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.diagram.MessageData;
import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.drawable.Line;
import net.sf.sdedit.drawable.Note;
import net.sf.sdedit.drawable.SequenceElement;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.error.SyntaxError;

public class DiagramProviderToText implements DiagramDataProvider {

	private StringWriter stringWriter;

	private PrintWriter printWriter;

	private DiagramDataProvider provider;

	public DiagramProviderToText(DiagramDataProvider provider) {
		this.provider = provider;
		stringWriter = new StringWriter();
		printWriter = new PrintWriter(stringWriter);
	}

	public String getText() {
		String text = stringWriter.toString();
		if (!System.getProperty("line.separator").equals("\n")) {
			text = text.replaceAll(System.getProperty("line.separator"), "\n");
		}
		return text;
	}

	public static String getText(DiagramDataProvider provider) {
		DiagramProviderToText dptt = new DiagramProviderToText(provider);
		IPaintDevice ipd = new NullPaintDevice();
		Configuration conf = ConfigurationManager
				.createNewDefaultConfiguration().getDataObject();
		conf.setThreaded(true);
		Diagram diagram = new Diagram(conf, dptt, ipd);
		try {
			diagram.generate();
		} catch (RuntimeException re) {
			throw re;
		} catch (DiagramError de) {
			de.printStackTrace();
		}
		return dptt.getText();

	}

	private static class NullPaintDevice implements IPaintDevice {

		private Diagram diagram;

		public void addOtherDrawable(Drawable drawable) {
		}

		public void addSequenceElement(SequenceElement elem) {
		}

		public void announce(int height) {
		}

		public void clear() {
		}

		public void close() {
		}

		public void computeAxes(int leftAxis) {
		}

		public void computeBounds() {
		}

		public Diagram getDiagram() {
			return diagram;
		}

		public Font getFont(boolean bold) {
			return Font.decode("Dialog");
		}

		public int getHeight() {
			return 0;
		}

		public Line getRightBound() {
			return null;
		}

		public int getTextHeight(boolean bold) {
			return 0;
		}

		public int getTextHeight() {
			return 0;
		}

		public int getTextWidth(String text, boolean bold) {
			return 0;
		}

		public int getTextWidth(String text) {
			return 0;
		}

		public int getWidth() {
			return 0;
		}

		public boolean isEmpty() {
			return true;
		}

		public void reinitialize() {
		}

		public void setDiagram(Diagram diagram) {
			this.diagram = diagram;
		}

		public void writeToStream(OutputStream stream) throws IOException {
		}

		public Iterator<Drawable> iterator() {
			return new LinkedList<Drawable>().iterator();
		}

	}

	public boolean advance() {
		boolean flag = provider.advance();
		if (!flag) {
			printWriter.println();
		}
		return flag;

	}

	public boolean closeFragment() {
		boolean flag = provider.closeFragment();
		if (flag) {
			printWriter.println("[/c]");
		}
		return flag;
	}

	public String[] getDescription() {
		String[] desc = provider.getDescription();
		if (desc != null) {
			printWriter.println("#!>>");
			for (String string : desc) {
				printWriter.println("#!" + string);
			}
			printWriter.println("#!<<");
		}
		return null;
	}

	public Pair<Lifeline, Integer> getEventAssociation() throws SyntaxError {
		// TODO
		return provider.getEventAssociation();
	}

	public String getFragmentSeparator() {
		String sep = provider.getFragmentSeparator();
		if (sep != null) {
			printWriter.println("--" + sep);
		}
		return sep;
	}

	public Note getNote() throws SyntaxError {
		// TODO
		return provider.getNote();
	}

	public Object getState() {
		return provider.getState();
	}

	public String getTitle() {
		String title = provider.getTitle();
		if (title != null) {
			printWriter.println("#![" + title + "]");
		}
		return title;
	}

	private static String escape(String msg) {
		msg = msg.replaceAll("\\.", "\\\\.");
		msg = msg.replaceAll(":", "\\\\:");
		return msg;

	}

	public MessageData nextMessage() throws SyntaxError {
		MessageData md = provider.nextMessage();
		md.setMessage(escape(md.getMessage()));
		md.setAnswer(escape(md.getAnswer()));
		printWriter.println(md.toString());
		return md;
	}

	public Lifeline nextObject() throws SyntaxError {
		Lifeline ll = provider.nextObject();
		printWriter.println(ll.toString());
		return ll;
	}

	public String openFragment() {
		String of = provider.openFragment();
		if (of != null) {
			printWriter.println(of);
		}
		return of;
	}
	
	public Diagram getDiagram () {
		return provider.getDiagram();
	}

	public void setDiagram(Diagram diagram) {
		provider.setDiagram(diagram);

	}

}
