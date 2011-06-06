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

package net.sf.sdedit.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import net.sf.sdedit.Constants;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.util.DocUtil;
import net.sf.sdedit.util.Utilities;
import net.sf.sdedit.util.DocUtil.XMLException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class ConfigurationManager {

	private ConfigurationManager () {
		/* empty */
	}
	
	/**
	 * The global default configuration, loaded from the class path. It should
	 * not be changed.
	 */
	public static final Bean<GlobalConfiguration> GLOBAL_DEFAULT;

	/**
	 * The default diagram configuration, loaded from the class path. It should
	 * not be changed.
	 */
	public static final Bean<Configuration> LOCAL_DEFAULT;
	
	public static final Bean<PrintConfiguration> PRINT_DEFAULT;
	
	private static final Bean<GlobalConfiguration> global; 
	
	private static final Bean<Configuration> local;
	
	private static final Bean<PrintConfiguration> print;

	static {
		GLOBAL_DEFAULT = new Bean<GlobalConfiguration>(GlobalConfiguration.class,new GlobalConfigurationStrings());
		LOCAL_DEFAULT = new Bean<Configuration>(Configuration.class,null);
		PRINT_DEFAULT = new Bean<PrintConfiguration>(PrintConfiguration.class, null);
		URL url = Utilities.getResource("default.conf");
		try {
			getValuesFromURL(url, GLOBAL_DEFAULT, LOCAL_DEFAULT, PRINT_DEFAULT);
		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IllegalStateException();
		}
		global = GLOBAL_DEFAULT.copy();
		local = LOCAL_DEFAULT.copy();
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
	
	public static Bean<GlobalConfiguration> getGlobalConfigurationBean () {
		return global;
	}
	
	public static Bean<PrintConfiguration> getPrintConfigurationBean () {
		return print;
	}
	
	public static PrintConfiguration getPrintConfiguration () {
		return print.getDataObject();
	}
	
	public static GlobalConfiguration getGlobalConfiguration() {
		return global.getDataObject();
	}
	
	public static Bean<Configuration> getDefaultConfigurationBean () {
		return local;
	}
	
	public static Configuration getDefaultConfiguration() {
		return local.getDataObject();
	}
	
	public static Bean<Configuration> createNewDefaultConfiguration () {
		return local.copy();
	}
	
	public static void storeConfigurations() throws IOException {
		OutputStream stream = new FileOutputStream(Constants.GLOBAL_CONF_FILE);
		try {
			Document document = DocUtil.newDocument();
			Element root = document.createElement("sdedit-configuration");
			document.appendChild(root);
			global.store(document, "/sdedit-configuration", "global-settings");
			local.store(document,
					"/sdedit-configuration", "default-settings");
			print.store(document, 
					"/sdedit-configuration", "printer-settings");
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

	private static boolean getValuesFromURL(URL url, Bean<GlobalConfiguration> global,
			Bean<Configuration> local, Bean<PrintConfiguration> print) throws IOException, XMLException {
		InputStream stream = null;
		try {
			stream = url.openStream();
			Document document = DocUtil.readDocument(stream, "UTF-8");
			global.load(document, "/sdedit-configuration/global-settings");
			local.load(document, "/sdedit-configuration/default-settings");
			if (print != null) {
				print.load(document, "/sdedit-configuration/printer-settings");
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return true;
	}
}
//{{core}}
