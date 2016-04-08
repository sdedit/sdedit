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

package net.sf.sdedit.editor.apple;

import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;

import net.sf.sdedit.editor.Editor;
import net.sf.sdedit.ui.impl.UserInterfaceImpl;
import net.sf.sdedit.util.Proxy;
import net.sf.sdedit.util.Utilities;

public class ApplicationListenerImpl extends AppInstaller {

	private Editor editor;

	private UserInterfaceImpl ui;

	private File fileToLoad;

	ApplicationListenerImpl() {
		/* empty */
	}

	@Override
	void install(Editor editor) {
		this.editor = editor;
		this.ui = (UserInterfaceImpl) editor.getUI();

		Object app = Utilities.invoke("getApplication",
				"com.apple.eawt.Application");

		// Application app = Application.getApplication();
		if (app != null) {
			try {
				Utilities.invoke("setEnabledPreferencesMenu", app,
						new Object[] { true });
				Class<?> applicationListenerClass;
				applicationListenerClass = Class
						.forName("com.apple.eawt.ApplicationListener");
				Proxy proxy = new Proxy(this, applicationListenerClass);
				Utilities.invoke("addApplicationListener", app,
						new Object[] { proxy.instantiate() });
				URL iconURL = Utilities.getResource("dock-icon.png");
				ImageIcon imageIcon = new ImageIcon(iconURL);
				Utilities.invoke("setDockIconImage", app,
						new Object[] { imageIcon.getImage() });
			} catch (Throwable t) {
				t.printStackTrace();
				return;
			}

		}
	}

	/*
	 * @see net.sf.sdedit.editor.apple.AppInstaller#fileToLoad()
	 */
	public File fileToLoad() {
		return fileToLoad;
	}

	/*
	 * @see com.apple.eawt.ApplicationListener#handleAbout(com.apple.eawt.ApplicationEvent)
	 */
	public void handleAbout(Object event) {
		ui.showAboutDialog(Utilities.getResource("about.html"));
		Utilities.invoke("setHandled", event, true);
	}

	/*
	 * @see com.apple.eawt.ApplicationListener#handlePreferences(com.apple.eawt.ApplicationEvent)
	 */
	public void handlePreferences(Object event) {
		ui.toFront();
		ui.configure(null);
		Utilities.invoke("setHandled", event, true);
	}

	/*
	 * @see com.apple.eawt.ApplicationListener#handleQuit(com.apple.eawt.ApplicationEvent)
	 */
	public void handleQuit(Object event) {
		ui.toFront();
		editor.quit();
		Utilities.invoke("setHandled", event, false);
	}

	/*
	 * @see com.apple.eawt.ApplicationListener#handlePrintFile(com.apple.eawt.ApplicationEvent)
	 */
	public void handlePrintFile(Object event) {
		Utilities.invoke("setHandled", event, true);
	}

	/*
	 * @see com.apple.eawt.ApplicationListener#handleReOpenApplication(com.apple.eawt.ApplicationEvent)
	 */
	public void handleReOpenApplication(Object event) {
		ui.toFront();
		Utilities.invoke("setHandled", event, true);
	}

	/*
	 * @see com.apple.eawt.ApplicationListener#handleOpenFile(com.apple.eawt.ApplicationEvent)
	 */
	public void handleOpenFile(Object event) {
		String fileName = (String) Utilities.invoke("getFilename", event);
		File file = new File(fileName);
		if (!editor.isSetup()) {
			fileToLoad = file;
		} else {
			try {
				editor.load(file.toURI().toURL());
			} catch (RuntimeException re) {
				throw re;
			} catch (Exception ex) {
				ui.errorMessage(ex, null, null);
			}
		}
		Utilities.invoke("setHandled", event, true);
	}

	/*
	 * @see com.apple.eawt.ApplicationListener#handleOpenApplication(com.apple.eawt.ApplicationEvent)
	 */
	public void handleOpenApplication(Object event) {
		String fileName = (String) Utilities.invoke("getFilename", event);
		if (fileName != null) {
			handleOpenFile(event);
		}
	}

}
