// Copyright (c) 2006 - 2008, Markus Strauch.
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
// THE POSSIBILITY OF SUCH DAMAGE.// Copyright (c) 2006 - 2008, Markus Strauch.

package net.sf.sdedit.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.UIManager;

public class UIUtilities {

	public static void centerWindow(Window window) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int width = window.getWidth();
		int height = window.getHeight();
		int left = Math.max(0, screen.width / 2 - width / 2);
		left = Math.min(left, screen.width - width);
		int top = Math.max(0, screen.height / 2 - height / 2);
		top = Math.min(top, screen.height - height);
		window.setLocation(left, top);
	}

	public static void centerWindow(Window window, Window parent) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int width = window.getWidth();
		int height = window.getHeight();
		int left = Math.max(0, parent.getLocationOnScreen().x
				+ parent.getSize().width / 2 - width / 2);
		left = Math.min(left, screen.width - width);
		int top = Math.max(0, parent.getLocationOnScreen().y
				+ parent.getSize().height / 2 - height / 2);
		top = Math.min(top, screen.height - height);
		window.setLocation(left, top);
	}

	public static void changeIconButton(JButton button) {
		button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		button.setOpaque(false);
		button.setMargin(new Insets(1, 1, 1, 1));
	}

	public static void setGlobalFont(Font font) {

		UIManager.put("Button.font", font);
		UIManager.put("ToggleButton.font", font);
		UIManager.put("RadioButton.font", font);
		UIManager.put("CheckBox.font", font);
		UIManager.put("ColorChooser.font", font);
		UIManager.put("ComboBox.font", font);
		UIManager.put("Label.font", font);
		UIManager.put("List.font", font);
		UIManager.put("MenuBar.font", font);
		UIManager.put("MenuItem.font", font);
		UIManager.put("RadioButtonMenuItem.font", font);
		UIManager.put("CheckBoxMenuItem.font", font);
		UIManager.put("Menu.font", font);
		UIManager.put("PopupMenu.font", font);
		UIManager.put("OptionPane.font", font);
		UIManager.put("Panel.font", font);
		UIManager.put("ProgressBar.font", font);
		UIManager.put("ScrollPane.font", font);
		UIManager.put("Viewport.font", font);
		UIManager.put("TabbedPane.font", font);
		UIManager.put("Table.font", font);
		UIManager.put("TableHeader.font", font);
		UIManager.put("TextField.font", font);
		UIManager.put("PasswordField.font", font);
		UIManager.put("TextArea.font", font);
		UIManager.put("TextPane.font", font);
		UIManager.put("EditorPane.font", font);
		UIManager.put("TitledBorder.font", font);
		UIManager.put("ToolBar.font", font);
		UIManager.put("ToolTip.font", font);
		UIManager.put("Tree.font", font);

	}

	public static List<Component> getDescendants(Container container) {
		List<Component> descs = new LinkedList<Component>();
		collectDescendants(container, descs);
		return descs;
	}

	private static void collectDescendants(Container cont, List<Component> descs) {
		for (Component comp : cont.getComponents()) {
			descs.add(comp);
			if (comp instanceof Container) {
				collectDescendants((Container) comp, descs);
			}
		}
	}

	public static File affixType(File file, String type) {
		String fileName = file.getAbsolutePath();
		int dot = fileName.lastIndexOf('.');
		if (dot == -1) {
			return new File(fileName + "." + type);
		}
		String baseName = fileName.substring(0, dot);
		return new File(baseName + "." + type);
	}
}
