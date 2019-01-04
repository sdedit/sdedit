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
package net.sf.sdedit.editor.plugin;

import java.awt.Component;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.editor.Editor;
import net.sf.sdedit.ui.Tab;
import net.sf.sdedit.ui.UserInterface;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.BeanConverter;
import net.sf.sdedit.ui.impl.DiagramTextTab;
import net.sf.sdedit.ui.impl.UserInterfaceImpl;
import net.sf.sdedit.util.DocUtil;
import net.sf.sdedit.util.DocUtil.XMLException;
import net.sf.sdedit.util.Pair;
import net.sf.sdedit.util.Utilities;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractFileHandler implements FileHandler {

	public static JFileChooser FILE_CHOOSER;

	private static boolean sync;

	static {
		FILE_CHOOSER = new JFileChooser();
		FILE_CHOOSER.setCurrentDirectory(new File("."));
		sync = false;
	}

	private File currentFile;
	
	private static final char BOM = '\uFEFF';

	protected AbstractFileHandler() {

	}

	// TODO
	protected UserInterfaceImpl getUI() {
		return (UserInterfaceImpl) Editor.getEditor().getUI();
	}
	
	protected abstract DiagramTextTab createTab (Font editorFont, Bean<? extends Configuration> configuration);

	protected Tab _loadFile(URL file) throws IOException {
		InputStream stream = file.openStream();
		try {
			String encoding = ConfigurationManager.getGlobalConfiguration()
					.getFileEncoding();
			Font editorFont = ConfigurationManager.getGlobalConfiguration()
					.getEditorFont();
			Pair<String, Bean<? extends Configuration>> result = load(stream, encoding);
			DiagramTextTab tab = createTab(editorFont, result.getSecond());
			tab.setCode(result.getFirst());
			return tab;
		} catch (XMLException e) {
			throw new IOException(
					"The file could not be loaded because of an XMLException: "
							+ e.getMessage());
		} finally {
			stream.close();
		}
	}
	
	protected abstract boolean isXML (File file);

	protected boolean _saveFile(Tab tab, File file) throws IOException {
		DiagramTextTab dtab = (DiagramTextTab) tab;

		// // plain text
		Bean<? extends Configuration> configuration;
		if (isXML(file)) {
			configuration = dtab.getConfiguration();
		} else {
			configuration = null;
		}
		OutputStream stream = new FileOutputStream(file);
		String code = dtab.getCode();
		String encoding = ConfigurationManager.getGlobalConfiguration()
				.getFileEncoding();
		try {
			saveDiagram(code, configuration, stream, encoding);
			return true;
		} catch (XMLException e) {
			throw new IOException(
					"The diagram could not be saved because of an XMLException: "
							+ e.getMessage());
		} finally {
			stream.close();
		}
	}

	protected String[] getFileTypesWithDescriptions() {
		String[] types = getFileTypes();
		String[] descriptions = getFileDescriptions();
		if (types.length != descriptions.length) {
			throw new IllegalArgumentException("The file handler defines "
					+ types.length + " file types, but " + descriptions.length
					+ " descriptions");
		}
		String[] result = new String[types.length * 2];
		for (int i = 0; i < types.length; i++) {
			result[i * 2] = descriptions[i];
			result[i * 2 + 1] = types[i];
		}
		return result;
	}

	private static void updateFileChooserDirectory(File file) {
		if (!sync) {
			if (file != null) {
				File parent = file.getParentFile();
				if (parent != null) {
					FILE_CHOOSER.setCurrentDirectory(parent);
				}
			}
			sync = true;
		}
	}

	protected Component getComponent() {
		Tab tab = Editor.getEditor().getUI().currentTab();
		return tab;
	}
	
	protected abstract Bean<? extends Configuration> createNewConfiguration();
	
	/**
	 * Loads a diagram from the text transmitted through the given
	 * <tt>stream</tt>. If the text contains a line that starts with
	 * <tt>&lt;?xml</tt>, it is interpreted as an XML file, containing the
	 * diagram source as a CDATA section along with a configuration. Otherwise
	 * the whole of the text is interpreted as a diagram source, and a default
	 * configuration is used.
	 * 
	 * @param stream
	 *            the stream from where the diagram specification is read
	 * @param encoding
	 *            the encoding of the diagram specification
	 * @return a pair of the diagram source and the configuration to be used for
	 *         generating the diagram
	 * 
	 * @throws IOException
	 * @throws DocUtil.XMLException
	 */
	public Pair<String, Bean<? extends Configuration>> load(InputStream stream,
			String encoding) throws IOException, DocUtil.XMLException {
		InputStreamReader reader = new InputStreamReader(stream, encoding);
		BufferedReader buffered = new BufferedReader(reader);
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		boolean xml = false;
		String line = buffered.readLine();
		boolean firstLine = true;
		while (line != null) {
			if (firstLine && line.length() > 0 && line.charAt(0) == BOM) {
				line = line.substring(1);
			}
			xml |= line.trim().startsWith("<?xml");
			writer.println(line);
			line = buffered.readLine();
		}
		writer.close();
		String source;
		Bean<? extends Configuration> configuration = createNewConfiguration();
		if (xml) {
			InputStream inputStream = new ByteArrayInputStream(stringWriter
					.toString().getBytes(encoding));
			try {
				Document document = DocUtil.readDocument(inputStream, encoding);
				source = DocUtil.evaluateCDATA(document, "/diagram/source");
				Element confElement = (Element) DocUtil.evalXPathAsNode(
						document, "/diagram/configuration");
				BeanConverter converter = new BeanConverter(configuration,
						document);
				converter.setValues(confElement);
			} finally {
				inputStream.close();
			}
		} else {
			source = stringWriter.toString();
		}
		return new Pair<String, Bean<? extends Configuration>>(source, configuration);
	}

	/**
	 * Saves a diagram specification (and a configuration), using a stream.
	 * 
	 * @param source
	 *            the source text of the diagram
	 * @param configuration
	 *            a configuration of the diagram, or null if it is to be saved
	 *            without a configuration (as plain text)
	 * @param stream
	 *            the stream to use for saving the diagram source and
	 *            configuration
	 * @param encoding
	 *            the encoding to be used
	 * 
	 * @throws IOException
	 * @throws XMLException
	 */
	private static void saveDiagram(String source,
			Bean<? extends Configuration> configuration, OutputStream stream,
			String encoding) throws IOException, XMLException {
		if (configuration != null) {
			Document document = DocUtil.newDocument();
			Element root = document.createElement("diagram");
			document.appendChild(root);
			BeanConverter converter = new BeanConverter(configuration, document);
			Element sourceElem = document.createElement("source");
			CDATASection sourceNode = document.createCDATASection(source);
			sourceElem.appendChild(sourceNode);
			root.appendChild(sourceElem);
			Element configurationNode = converter
					.createElement("configuration");
			root.appendChild(configurationNode);
			DocUtil.writeDocument(document, encoding, stream);
		} else {
			OutputStreamWriter osw = new OutputStreamWriter(stream, encoding);
			PrintWriter pw = new PrintWriter(osw);
			pw.print(source);
			pw.flush();
		}
	}

	/**
	 * @see net.sf.sdedit.editor.plugin.FileHandler#loadFile(java.net.URL)
	 */
	public final Tab loadFile(URL url, final UserInterface ui)
			throws IOException {
		if (!"file".equals(url.getProtocol())
				|| !ui.selectTabWith(Utilities.toFile(url))) {

			ui.getTabContainer().disableTabHistory();
			final Tab tab = _loadFile(url);
			if (tab == null) {
				return null;
			}
			if (url.getProtocol().equals("file")) {
				tab.setFile(Utilities.toFile(url));
			}
			
			tab.setClean(true);
			addTabToUI(tab, ui);
			
			SwingUtilities.invokeLater(new Runnable() {
			    
				public void run() {
				    
				    // file processing is deferred (one may load a file in a tab
				    // and immediately thereafter set the file reference to null)
				    // see Actions.getExampleAction
		            if (tab.getFile() != null) {
		                updateFileChooserDirectory(tab.getFile());
		                if (tab.getFile().exists()) {
		                    Editor.getEditor().addToRecentFiles(tab.getFile().getAbsolutePath());
		                }
 		            }
					ui.getTabContainer().enableTabHistory();
					//ui.selectTab(tab);
				}
			});
			return tab;
		}
		return null;

	}

	protected void addTabToUI(Tab tab, UserInterface ui) {
		ui.addTab(tab, true);
	}

	/**
	 * @see net.sf.sdedit.editor.plugin.FileHandler#save(net.sf.sdedit.ui.Tab,
	 *      boolean)
	 */
	public File save(Tab tab, boolean as) throws IOException {
		File file = null;
		if (!as) {
			file = tab.getFile();
		}
		if (file == null) {
			file = selectFileToSave();
		}
		if (file != null) {
			int confirmation = 1;
			if (file.exists() && !file.equals(tab.getFile())) {
				confirmation = tab
						.get_UI()
						.confirmOrCancel(
								"<html>"
										+ file.getName()
										+ " already exists.<br>Do you want to overwrite it?");
			}
			if (confirmation == 1) {
				updateFileChooserDirectory(file);
				currentFile = file;
				if (_saveFile(tab, file)) {
					tab.setFile(file);
					tab.setClean(true);
					Editor.getEditor().addToRecentFiles(file.getAbsolutePath());
					return file;
				}
			}
		}
		return null;
	}

	private String getFileName() {
		if (currentFile != null) {
			return currentFile.getName();
		}
		return "";
	}

	public File selectFileToOpen() {
		File[] files = getFiles(true, false, getOpenDescription());
		if (files != null && files.length > 0) {
			return files[0];
		}
		return null;
	}

	public File[] selectFilesToOpen() {
		return getFiles(true, true, getOpenDescription());
	}

	public File selectFileToSave() {
		File[] files = getFiles(false, false, getSaveAsDescription());
		if (files == null) {
			return null;
		}
		return files[0];
	}

	protected File[] getFiles(boolean open, boolean multiple, String message) {
		return getFiles(open, multiple, message, null, (String[]) null);
	}

	public File[] getFiles(boolean open, boolean multiple, String message,
			String file, String... filter) {
		if (file == null) {
			file = getFileName();
		}
		if (filter == null) {
			filter = getFileTypesWithDescriptions();
		}
		// if (file != null) {
		// fileChooser.setSelectedFile(new File(fileChooser
		// .getCurrentDirectory(), file));
		// }

		return Utilities.chooseFiles(FILE_CHOOSER, getComponent(), open,
				multiple, message, file, filter);
	}
}
