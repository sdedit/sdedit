package com.apple.eawt;

import javax.swing.JOptionPane;

public class Application {

	static {
		JOptionPane.showMessageDialog(null,
				"class Application loaded from user class path");
	}

	public static Application getApplication() {
		return null;
	}

	public void setEnabledPreferencesMenu(boolean b) {
				
	}

	public void addApplicationListener(Object o) {
		
	}
}
