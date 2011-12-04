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

import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.ui.Tab;
import net.sf.sdedit.ui.TabListener;
import net.sf.sdedit.ui.components.navigator.ContextActionsProvider;
import net.sf.sdedit.ui.components.navigator.TreeNavigatorPane;
import net.sf.sdedit.ui.components.navigator.TreeNavigatorPaneListener;
import net.sf.sdedit.util.Utilities;

public class TabNavigatorContainer implements TabContainer,
        TabListener,
		TreeNavigatorPaneListener, ContextActionsProvider {

	private TreeNavigatorPane navigator;

	private List<TabContainerListener> listeners;

	private Tab previousTab;
	
	private int id;

	public TabNavigatorContainer(double resizeWeight) {
		navigator = new TreeNavigatorPane(resizeWeight);
		navigator.setContextActionsProvider(this);
		navigator.addListener(this);
		listeners = new LinkedList<TabContainerListener>();
		addCategory("Modeling", Icons.getIcon("text"));
		id = 0;
	}

	public void addCategory(String category, ImageIcon icon) {
		navigator.addRootCategory(category, icon);
	}

	public String addChildTab(Tab tab, Tab parent, boolean selectIt, Tab previousTab) {
		String title = Utilities.findUniqueName(tab.getTitle(), navigator
				.getAllTitles());
		tab.setTitle(title);
		addListener(tab);
		tab.addTabListener(this);
		navigator.addComponent(tab.getTitle(), tab, tab.getIcon(), parent, selectIt, previousTab);
		id++;
		tab.setId(id);
		return title;
	}
	
	public String addChildTab(Tab tab, Tab parent) {
	    return addChildTab(tab, parent, true, null);
	}

	public void addListener(TabContainerListener listener) {
		listeners.add(listener);
	}

	public String addTab(Tab tab, boolean selectIt) {
		return addTabToCategory(tab, "Modeling", selectIt);
	}

	public String addTabToCategory(Tab tab, String category, boolean selectIt) {
		String title = Utilities.findUniqueName(tab.getTitle(), navigator
				.getAllTitles());
		tab.setTitle(title);
		addListener(tab);
		tab.addTabListener(this);
		id++;
		tab.setId(id);
		navigator.addComponent(tab.getTitle(), tab, tab.getIcon(), category, selectIt, null);
		return title;
	}
	
	public String addTabToCategory(Tab tab, String category) {
	    return addTabToCategory(tab, category, true);
	}

	public JComponent getComponent() {
		return navigator;
	}

	public Tab getSelectedTab() {
		Tab tab = (Tab) navigator.getSelectedComponent();
		return tab;
	}

	public int getTabCount() {
		return navigator.getCompCount();
	}

	/**
	 * Returns the tabs in this container in reverse BFS order.
	 */
	public List<Tab> getTabs() {
		List<Tab> result = new LinkedList<Tab>();
		for (JComponent comp : navigator.getAllComponents()) {
			result.add((Tab) comp);
		}
		return result;
	}

	public void remove(Tab tab) {
		if (tab.canClose()) {
			JComponent[] closed = navigator.removeComponent(tab, tab.alsoRemoveDescendants());
			for (Tab closedTab : Utilities.castArray(closed, Tab.class)) {
				listeners.remove(closedTab);
			}
		}
	}

	public void removeListener(TabContainerListener listener) {
		listeners.remove(listener);
	}

	public boolean select(Tab tab) {
		return navigator.setSelectedComponent(tab);
	}

	public synchronized void componentSelected(JComponent component) {
		Tab currentTab = (Tab) component;
		for (TabContainerListener listener : new LinkedList<TabContainerListener>(
				listeners)) {
			listener.tabSelected(previousTab, currentTab);
		}
		previousTab = currentTab;
	}

	public boolean exists(Tab tab) {
		for (Tab existing : getTabs()) {
			if (existing.equals(tab)) {
				return true;
			}
		}
		return false;
	}

	public List<Action> getContextActions(JComponent component) {
		return ((Tab) component).getContextActions();
	}

	public boolean existsCategory(String title) {
		return navigator.existsCategory(title);
	}

	/**
	 * @see net.sf.sdedit.ui.TabListener#titleChanged(net.sf.sdedit.ui.Tab)
	 */
	public void titleChanged(Tab tab) {
		navigator.setTitle(tab, tab.getTitle());
	}

	/**
	 * @see net.sf.sdedit.ui.TabListener#tabIsClosed(net.sf.sdedit.ui.Tab)
	 */
	public void tabIsClosed(Tab tab) {
		/* empty */
	}

	public Tab [] getSelectedTabs() {
		return Utilities.castArray(navigator.getSelectedComponents(), Tab.class);
	}
	
	public List<Tab> getSuccessors(Tab tab) {
		List<Tab> successors = new LinkedList<Tab>();
		for (JComponent comp : navigator.getSuccessors(tab)) {
			successors.add((Tab) comp);
		}
		return successors;
	}

	public List<Tab> getDescendants(Tab root) {
		List<Tab> descendants = new LinkedList<Tab>();
		for (JComponent comp : navigator.getDescendants(root)) {
			descendants.add((Tab) comp);
		}
		return descendants;
	}

	public Tab getParentTab(Tab tab) {
	    return (Tab) navigator.getParentComponent(tab);
	}

	public void goToNextTab() {
		navigator.goToNextComponent();
	}

	public void goToPreviousTab() {
		navigator.goToPreviousComponent();
	}

	public boolean nextTabExists() {
		return navigator.nextComponentExists();
	}

	public boolean previousTabExists() {
		return navigator.previousComponentExists();
	}

	public void disableTabHistory() {
		navigator.disableHistory();
		
	}

	public void enableTabHistory() {
		navigator.enableHistory();
		
	}

}
