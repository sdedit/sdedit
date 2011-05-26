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

package net.sf.sdedit.ui.components.configuration;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JPanel;

import net.sf.sdedit.util.DocUtil.XMLException;

@SuppressWarnings("serial")
public class BeanPane extends JPanel implements ConfigurationUIListener {

	private LinkedList<ConfigurationUIListener> listeners;

	private String description;
	
	private Bean<?> bean;
	
	private ConfigurationUI<?> cui;
	
	private boolean tabbed;
	
	private Map<String,Component> additionalTabs;
	
	public BeanPane (String description) {
		this(description, false);
	}

	public BeanPane(String description, boolean tabbed) {
		this.tabbed = tabbed;
		this.description = description;
		listeners = new LinkedList<ConfigurationUIListener>();
		additionalTabs = new TreeMap<String,Component>();
		setLayout(new BorderLayout());
	}
	
	public void addTab (Component tab, String title) {
		additionalTabs.put(title, tab);
	}
	
	public void addListener(ConfigurationUIListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ConfigurationUIListener listener) {
		listeners.remove(listener);
	}

	public <D extends DataObject> Bean<D> load(Class<D> beanClass,
			URL configurationFile) throws XMLException, IOException {
		Bean<D> newBean = new Bean<D>(beanClass, null);
		newBean.load(configurationFile);
		load(newBean);
		return newBean;
	}
	
	public Bean<?> getBean () {
		return bean;
		
	}
	
	public ConfigurationUI<?> getConfigurationUI () {
		return cui;
	}

	public <D extends DataObject> void load(Bean<D> newBean) {
		cui = new ConfigurationUI<D>(this, newBean, null, null,
				null, description, tabbed);
		removeAll();
		cui.hideButtons();
		if (cui.getCategoryPanels().size() == 1) {
			cui.hideCategoryList();
		}
		add(cui, BorderLayout.CENTER);
		for (String name : additionalTabs.keySet()) {
			cui.addTab(additionalTabs.get(name), name);
		}
		
		revalidate();
		this.bean = newBean;
	}

	public void applyConfiguration() {
		// TODO Auto-generated method stub

	}

	public void cancelConfiguration() {
		// TODO Auto-generated method stub

	}

}
