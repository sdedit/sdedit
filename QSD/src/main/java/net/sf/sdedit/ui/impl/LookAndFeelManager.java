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

package net.sf.sdedit.ui.impl;

import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

//import com.sun.java.swing.plaf.windows.WindowsTreeUI;

/**
 * For dynamically changing the look and feel of whole frames and dialogs.
 * 
 * @author Markus Strauch
 */
public/* singleton */class LookAndFeelManager extends URLClassLoader {

	private static UIManager.LookAndFeelInfo[] available;

	static {
		available = new UIManager.LookAndFeelInfo[UIManager
				.getInstalledLookAndFeels().length];
		int i = 0;
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			available[i] = info;
			i++;
		}
		//UIManager.put("Tree.expandedIcon",  new WindowsTreeUI.ExpandedIcon()); 
		//UIManager.put("Tree.collapsedIcon", new WindowsTreeUI.CollapsedIcon());

	}



	private LookAndFeelManager() {
		super(new URL[0], Thread.currentThread().getContextClassLoader());
	}

	public static UIManager.LookAndFeelInfo[] getAvailableLookAndFeels() {
		return available;
	}

//	public static void useExternalLAF(File jarFile, String cls)
//			throws MalformedURLException, ClassNotFoundException,
//			InstantiationException, IllegalAccessException,
//			UnsupportedLookAndFeelException {
//		addURL(jarFile.toURI().toURL());
//		Class<?> clazz = loadClass(cls, true);
//		// ClassLoader contextCL =
//		// Thread.currentThread().getContextClassLoader();
//		Thread.currentThread().setContextClassLoader(this);
//		UIManager.installLookAndFeel(clazz.getSimpleName(), cls);
//		UIManager.setLookAndFeel(cls);
//		// Thread.currentThread().setContextClassLoader(contextCL);
//	}


	public static boolean changeTo(String lookAndFeelName) {
		for (LookAndFeelInfo info : available) {
			if (info.getName().equals(lookAndFeelName)) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
					return true;
				} catch (RuntimeException re) {
					throw re;
				} catch (Throwable t) {
					t.printStackTrace();
					return false;
				}

			}
		}
		return false;
	}

}
