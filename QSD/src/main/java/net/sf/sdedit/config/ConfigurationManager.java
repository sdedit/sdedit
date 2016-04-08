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

package net.sf.sdedit.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.sdedit.Constants;
import net.sf.sdedit.editor.plugin.Plugin;
import net.sf.sdedit.editor.plugin.PluginRegistry;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.util.DocUtil;
import net.sf.sdedit.util.DocUtil.XMLException;
import net.sf.sdedit.util.Utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

@SuppressWarnings("unchecked")
public final class ConfigurationManager {

	private ConfigurationManager() {
		/* empty */
	}

	/**
	 * The global default configuration, loaded from the class path. It should
	 * not be changed.
	 */
	public static final Bean<GlobalConfiguration> GLOBAL_DEFAULT;

	public static final Bean<PrintConfiguration> PRINT_DEFAULT;

	private static final Bean<GlobalConfiguration> global;

	/**
	 * The default sequence diagram configuration, loaded from the class path.
	 * It should not be changed.
	 */
	private static final Map<String, Bean<? extends Configuration>> DEFAULT_CONF;

	private static final Map<String, Bean<? extends Configuration>> local;

	private static final Bean<PrintConfiguration> print;

	static {
		List<Class<? extends Configuration>> confClasses = new ArrayList<Class<? extends Configuration>>();
		confClasses.add(SequenceConfiguration.class);
		
		for (Plugin plugin : PluginRegistry.getInstance()) {
			if (plugin.getConfigurationClass() != null) {
				confClasses.add(plugin.getConfigurationClass());
			}
		}
		
		GLOBAL_DEFAULT = new Bean<GlobalConfiguration>(
				GlobalConfiguration.class, new GlobalConfigurationStrings());
		DEFAULT_CONF = new HashMap<String, Bean<? extends Configuration>>();
		
		for (Class<? extends Configuration> cls : confClasses) {
			@SuppressWarnings("rawtypes")
			Bean bean = new Bean(cls, null);
			DEFAULT_CONF.put(cls.getName(), bean);
		}
		
		PRINT_DEFAULT = new Bean<PrintConfiguration>(PrintConfiguration.class,
				null);
		URL url = Utilities.getResource("default.conf");
		try {
			getValuesFromURL(url, GLOBAL_DEFAULT, DEFAULT_CONF, PRINT_DEFAULT);
		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IllegalStateException();
		}
		local = new HashMap<String, Bean<? extends Configuration>>();
		global = GLOBAL_DEFAULT.copy();
		for (Entry<String, Bean<? extends Configuration>> entry : DEFAULT_CONF
				.entrySet()) {
			local.put(entry.getKey(), entry.getValue().copy());
		}
		print = PRINT_DEFAULT.copy();
		try {
			URL globalUrl = Constants.GLOBAL_CONF_FILE.toURI().toURL();
			getValuesFromURL(globalUrl, global, local, print);
		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable t) {
			/* ignored */
		}
	}

	public static Bean<GlobalConfiguration> getGlobalConfigurationBean() {
		return global;
	}

	public static Bean<PrintConfiguration> getPrintConfigurationBean() {
		return print;
	}

	public static PrintConfiguration getPrintConfiguration() {
		return print.getDataObject();
	}

	public static GlobalConfiguration getGlobalConfiguration() {
		return global.getDataObject();
	}
	
	public static <T extends Configuration> Bean<T> getInitialDefaultConfigurationBean(
			Class<T> cls) {
		return DEFAULT_CONF.get(cls.getName()).getDataObject().cast(cls).getBean(cls);
	}	

	public static <T extends Configuration> Bean<T> getDefaultConfigurationBean(
			Class<T> cls) {
		return local.get(cls.getName()).getDataObject().cast(cls).getBean(cls);
	}

	public static <T extends Configuration> T getDefaultConfiguration(
			Class<T> cls) {
		return local.get(cls.getName()).getDataObject().cast(cls);
	}

	public static <T extends Configuration> Bean<T> createNewDefaultConfiguration(
			Class<T> cls) {
		return getDefaultConfigurationBean(cls).copy();
	}

	private static String getElementName(Bean<?> bean) {
		if (bean.getDataObject().isA(PrintConfiguration.class)) {
			return "printer-settings";
		}
		if (bean.getDataObject().isA(GlobalConfiguration.class)) {
			return "global-settings";
		}
		if (bean.getDataObject().isA(SequenceConfiguration.class)) {
			return "default-settings";
		}
		return bean.getDataClass().getSimpleName();
	}

	public static void storeConfigurations() throws IOException {
		OutputStream stream = new FileOutputStream(Constants.GLOBAL_CONF_FILE);
		try {
			Document document = DocUtil.newDocument();
			Element root = document.createElement("sdedit-configuration");
			document.appendChild(root);
			global.store(document, "/sdedit-configuration",
					getElementName(global));
			for (Entry<String, Bean<? extends Configuration>> entry : local
					.entrySet()) {
				Bean<? extends Configuration> conf = entry.getValue();
				conf.store(document, "/sdedit-configuration",
						getElementName(conf));
			}
			print.store(document, "/sdedit-configuration",
					getElementName(print));
			DocUtil.writeDocument(document, "UTF-8", stream);
		} catch (RuntimeException re) {
			throw re;
		} catch (IOException ex) {
			throw ex;
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			stream.close();
		}
	}

	private static boolean getValuesFromURL(URL url,
			Bean<GlobalConfiguration> global,
			Map<String, Bean<? extends Configuration>> defaultConf,
			Bean<PrintConfiguration> print) throws IOException, XMLException {
		InputStream stream = null;
		try {
			stream = url.openStream();
			Document document = DocUtil.readDocument(stream, "UTF-8");
			global.load(document, "/sdedit-configuration/"
					+ getElementName(global));
			if (print != null) {
				print.load(document, "/sdedit-configuration/"
						+ getElementName(print));
			}
			for (Entry<String, Bean<? extends Configuration>> entry : defaultConf
					.entrySet()) {
				Bean<? extends Configuration> conf = entry.getValue();
				conf.load(document, "/sdedit-configuration/"
						+ getElementName(conf));
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return true;
	}
}
// {{core}}
