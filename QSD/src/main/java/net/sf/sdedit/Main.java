// Copyright (c) 2006 - 2015, Markus Strauch.
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

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.SwingUtilities;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.config.SequenceConfiguration;
import net.sf.sdedit.diagram.DiagramFactory;
import net.sf.sdedit.diagram.SDPaintDevice;
import net.sf.sdedit.diagram.SequenceDiagramFactory;
import net.sf.sdedit.editor.DiagramFileHandler;
import net.sf.sdedit.editor.Editor;
import net.sf.sdedit.editor.plugin.Plugin;
import net.sf.sdedit.editor.plugin.PluginRegistry;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.server.Exporter;
import net.sf.sdedit.ui.ImageGraphicsDevice;
import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.util.DocUtil.XMLException;
import net.sf.sdedit.util.OS;
import net.sf.sdedit.util.ObjectFactory;
import net.sf.sdedit.util.Pair;
import net.sf.sdedit.util.Tooltips;
import net.sf.sdedit.util.Utilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * 
 * @author Markus Strauch
 * 
 * @version $Revision$
 * 
 */
public class Main implements Constants {

	static {
		if (OS.TYPE == OS.Type.MAC) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name", "sdedit");
			System.setProperty("com.apple.mrj.application.live-resize", "true");
		}
	}

	public static void main(String[] argv) throws Exception {

		Tooltips.addFile(Utilities.getResource("tooltips"));
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		Options options = createBasicOptions();

		try {
			cmd = parser.parse(options, argv);
			if (cmd != null && cmd.hasOption('p')) {
				String[] plugins = cmd.getOptionValues('p');
				for (String plugin : plugins) {
					Class<?> pluginClass;
					try {
						pluginClass = Class.forName(plugin.trim());
						PluginRegistry.getInstance().addPlugin(
								(Plugin) pluginClass.newInstance());
					} catch (RuntimeException re) {
						throw re;
					} catch (Throwable t) {
						System.err.println("Warning: cannot load plugin class "
								+ plugin + " (" + t.getMessage() + ")");
					}
				}
			}
		} catch (ParseException ignored) {

		}

		options = createBasicOptions();

		addPropertyOptions(
				options,
				ConfigurationManager
						.getDefaultConfigurationBean(SequenceConfiguration.class));

		try {
			cmd = parser.parse(options, argv);
		} catch (ParseException pe) {
			printHelp(createBasicOptions());
			return;
		}

		if (cmd.hasOption('h')) {
			printHelp(options);
			return;
		}

		if (cmd.getOptionValue('o') != null) {
			createImage(cmd);
			System.out
					.println("created image file: " + cmd.getOptionValue('o'));

		} else {
			final String[] files = getInputFiles(cmd);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					Editor editor = Editor.getEditor();
					editor.start();
					if (files.length > 0) {
						boolean loaded = false;
						for (String file : files) {
							File sdFile = new File(file);
							if (sdFile.exists() && sdFile.canRead()
									&& !sdFile.isDirectory()) {
								try {
									loaded = editor
											.load(sdFile.toURI().toURL()) != null;
								} catch (RuntimeException re) {
									throw re;

								} catch (Throwable t) {
									editor.getUI().errorMessage(t, null, null);
								}
							} else {
								System.err.println("Warning: ignoring file "
										+ file);
							}
						}
						if (!loaded) {
							editor.getUI().addDefaultTab();
						}
					} else {
						editor.getUI().addDefaultTab();
					}
				}
			});
		}
	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("<sdedit-start-command> [options] [input-files]",
				options);
	}

	@SuppressWarnings("static-access")
	private static Options createBasicOptions() {

		Options options = new Options();

		Option output = OptionBuilder.hasArg().withArgName("output file")
				.create('o');
		options.addOption(output);

		Option type = OptionBuilder.hasArg().withArgName("output file type")
				.withDescription("one of " + availableTypes()).hasArg()
				.create('t');
		options.addOption(type);

		Option orientation = OptionBuilder.withArgName("orientation").hasArg()
				.withDescription("one of {Portrait, Landscape}").create('r');
		options.addOption(orientation);

		Option format = OptionBuilder.withArgName("page format").hasArg()
				.withDescription("one of {A0, ..., A6}").create('f');
		options.addOption(format);

		Option help = OptionBuilder.withDescription(
				"show long options (for diagram preferences)").create('h');
		options.addOption(help);

		Option plugins = OptionBuilder
				.withDescription("plugin classes to be loaded").hasArgs()
				.create('p');
		options.addOption(plugins);

		return options;
	}

	@SuppressWarnings("static-access")
	private static void addPropertyOptions(Options options,
			Bean<? extends Configuration> conf) {
		for (PropertyDescriptor property : conf.getProperties()) {
			if (property.getWriteMethod().getAnnotation(Adjustable.class)
					.editable()) {
				String name = property.getName();
				String type = property.getPropertyType().getSimpleName();
				String info = property.getWriteMethod()
						.getAnnotation(Adjustable.class).info();
				Option option = OptionBuilder.withArgName(type).hasArg()
						.withDescription(info).withValueSeparator('=')
						.withLongOpt(name).create();
				options.addOption(option);
			}
		}
	}

	private static String[] getInputFiles(CommandLine cmd) {
		return cmd.getArgs();
	}

	private static void createImage(CommandLine cmd) throws IOException,
			XMLException, DiagramError {
		File inFile = new File(getInputFiles(cmd)[0]);
		File outFile = new File(cmd.getOptionValue('o'));
		String type = "png";
		if (cmd.getOptionValue('t') != null) {
			type = cmd.getOptionValue('t').toLowerCase();
		}
		String format = "A4";
		if (cmd.getOptionValue('f') != null) {
			format = cmd.getOptionValue('f').toUpperCase();
		}
		String orientation = "Portrait";
		if (cmd.getOptionValue('r') != null) {
			orientation = cmd.getOptionValue('r').toLowerCase();
			if (orientation.length() > 0) {
				orientation = orientation.substring(0, 1).toUpperCase()
						+ orientation.substring(1);
			}
		}
		InputStream in = null;
		OutputStream out = null;
		in = new FileInputStream(inFile);
		try {
			out = new FileOutputStream(outFile);
			try {
				Pair<String, Bean<? extends Configuration>> pair = new DiagramFileHandler()
						.load(in, ConfigurationManager.getGlobalConfiguration()
								.getFileEncoding());
				String text = pair.getFirst();
				Bean<? extends Configuration> conf = pair.getSecond();
				configure(conf, cmd);
				if (type.equals("png")) {
					ImageGraphicsDevice graphicDevice = new ImageGraphicsDevice();
					SDPaintDevice paintDevice = new SDPaintDevice(graphicDevice);
					DiagramFactory factory = new SequenceDiagramFactory(text,
							paintDevice);
					factory.generateDiagram(conf.getDataObject());
					graphicDevice.writeToStream("png", out);
				} else {
					Exporter graphicDevice = Exporter.getExporter(type,
							orientation, format, out);
					SDPaintDevice paintDevice = new SDPaintDevice(graphicDevice);
					DiagramFactory factory = new SequenceDiagramFactory(text,
							paintDevice);
					factory.generateDiagram(conf.getDataObject());
					graphicDevice.export();
				}
				out.flush();
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}

	}

	private static void configure(Bean<? extends Configuration> conf,
			CommandLine cmd) {
		for (Option option : cmd.getOptions()) {
			if (option.getLongOpt() != null) {
				String name = option.getLongOpt();
				String valueString = cmd.getOptionValue(option.getLongOpt());
				if (valueString != null) {
					PropertyDescriptor property = conf.getProperty(name);
					Object value = ObjectFactory.createFromString(
							property.getPropertyType(), valueString);
					conf.setValue(property, value);
				}
			}
		}
	}

	private static String availableTypes() {
		return "{ps, pdf, emf, svg, png, gif, jpg, bmp}";
	}
}
