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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import javax.swing.JPanel;

import net.sf.sdedit.ui.components.ATabbedPane;
import net.sf.sdedit.ui.components.ATabbedPaneListener;

@SuppressWarnings("serial")
public class MultiBeanPane extends JPanel implements ConfigurationUIListener,
		PropertyChangeListener, ATabbedPaneListener {

	private ATabbedPane tabbedPane;
	
	private LinkedList<ConfigurationUI<? extends NamedDataObject>> tabs;
	
	private LinkedList<Bean<? extends NamedDataObject>> beans;

	public MultiBeanPane() {
		super();
		setLayout(new BorderLayout());
		tabbedPane = new ATabbedPane();
		tabbedPane.addListener(this);
		add(tabbedPane, BorderLayout.CENTER);
		beans = new LinkedList<Bean<? extends NamedDataObject>>();
		tabs = new LinkedList<ConfigurationUI<? extends NamedDataObject>>();
	}

	public <B extends NamedDataObject> void add(Bean<B> bean) {
		ConfigurationUI<? extends NamedDataObject> ui = new ConfigurationUI<B>(
				this, bean, null, null, null, "", false);
		ui.hideCategoryList();
		tabbedPane.addTab(ui, bean.getDataObject().getName());
		beans.add(bean);
		tabs.add(ui);
	}
	
	public void saveTo (File file) throws IOException {
		OutputStream os = new FileOutputStream(file);
		try {
			BufferedOutputStream bos = new BufferedOutputStream(os);
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(beans);
			bos.flush();
		} finally {
			os.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadFrom (File file) throws IOException {
		InputStream is = new FileInputStream(file);
		LinkedList<Bean<? extends NamedDataObject>> beans = null;
		try {
			BufferedInputStream bis = new BufferedInputStream(is);
			ObjectInputStream ois = new ObjectInputStream(bis);
			try {
				beans = (LinkedList<Bean<? extends NamedDataObject>>) ois.readObject();
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException ("Cannot read ");
			}
			
		} finally {
			is.close();
		}
		for (Bean<? extends NamedDataObject> bean : beans) {
			add(bean);
		}
	}

	public void applyConfiguration() {
		// TODO Auto-generated method stub
	}

	public void cancelConfiguration() {
		// TODO Auto-generated method stub

	}

	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

	}

	public void tabClosing(Component tab) {
		int i = tabs.indexOf(tab);
		tabs.remove(i);
		beans.remove(i);
		tabbedPane.remove(tab);
	}

}
