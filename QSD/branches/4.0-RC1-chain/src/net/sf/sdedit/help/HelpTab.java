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
package net.sf.sdedit.help;

import java.awt.BorderLayout;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import net.sf.sdedit.editor.plugin.FileHandler;
import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.ui.Tab;
import net.sf.sdedit.ui.components.AdvancedHelpPanel;
import net.sf.sdedit.ui.components.HelpPanel;
import net.sf.sdedit.ui.components.Zoomable;
import net.sf.sdedit.ui.components.buttons.Activator;
import net.sf.sdedit.ui.impl.UserInterfaceImpl;
import net.sf.sdedit.util.Pair;
import net.sf.sdedit.util.Utilities;

@SuppressWarnings("serial")
public class HelpTab extends Tab {

	private static Map<String, HelpTab> tabs;

	private static List<HelpTabFactory> factories;

	static {
		tabs = new HashMap<String, HelpTab>();
		factories = new LinkedList<HelpTabFactory>();
		factories.add(new HelpTabFactoryImpl());

	}

	public static void addHelpTabFactory(HelpTabFactory factory) {
		factories.add(factory);
	}

	public static class HelpTabFactoryImpl implements HelpTabFactory {

		public JComponent getHelpTabComponent(UserInterfaceImpl ui,
				String file, boolean advanced) {
			URL helpURL = Utilities.getResource(file + ".html");
			if (helpURL != null) {
				if (advanced) {
					return new AdvancedHelpPanel(helpURL, ui);
				} else {
					return new JScrollPane(new HelpPanel(helpURL, ui).getPane());
				}
			}
			return null;
		}
	}

	private String title;

	/**
	 * 
	 * @param ui
	 * @param resource
	 *            one of {
	 * @return
	 */
	public static HelpTab getHelpTab(UserInterfaceImpl ui, String resource,
			boolean advanced) {
		HelpTab tab = tabs.get(resource);
		if (tab == null) {
			for (HelpTabFactory factory : factories) {
				JComponent comp = factory.getHelpTabComponent(ui, resource,
						advanced);
				if (comp != null) {
					String title = resource;
					int slash = resource.lastIndexOf('/');
					if (slash > 0) {
						title = resource.substring(slash+1);
					}
					tab = new HelpTab(ui, title, comp);
					tabs.put(resource, tab);
					break;
				}
			}
		}
		return tab;
	}

	private HelpTab(UserInterfaceImpl ui, String title, JComponent comp) {
		super(ui);
		this.title = title;
		getContentPanel().setLayout(new BorderLayout());
		getContentPanel().add(comp, BorderLayout.CENTER);
	}

	public boolean equals(Object o) {
		if (HelpTab.class.isInstance(o)) {
			return HelpTab.class.cast(o).title.equals(title);
		}
		return false;
	}

	public int hashCode() {
		return title.hashCode();
	}

	public Icon getIcon() {
		return Icons.getIcon("help");
	}

	@Override
	public FileHandler getFileHandler() {
		return null;
	}

	@Override
	protected List<Pair<Action, Activator>> getOverloadedActions() {
		return null;
	}

	@Override
	protected Zoomable<? extends JComponent> getZoomable() {
		return null;
	}

	@Override
	protected void _getContextActions(List<Action> actionList) {
		/* empty */
	}

	@Override
	public boolean canClose() {
		return true;
	}

	@Override
	public boolean canGoHome() {
		return false;
	}

}
