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

import java.awt.event.ActionEvent;
import java.beans.PropertyDescriptor;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.SwingUtilities;

import net.sf.sdedit.icons.Icons;

public abstract class ConfigurationAction<T extends DataObject> extends
		AbstractAction implements Runnable {

	private String property;

	private AbstractButton button;

	private boolean listen;

	protected abstract Bean<T> getBean();

	public ConfigurationAction(String property, String name, String tooltip,
			String icon) {
		putValue(Action.NAME, name);
		putValue(Action.SHORT_DESCRIPTION, tooltip);
		putValue(Action.SMALL_ICON, Icons.getIcon(icon));
		this.property = property;
		listen = true;
	}

	public void setButton(AbstractButton button) {
		this.button = button;
	}

	public void update() {
		Bean<T> bean = getBean();
		if (bean != null) {
			Boolean state = (Boolean) bean.getValue(property);
			if (button != null) {
				listen = false;
				button.setSelected(state);
				listen = true;
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (listen) {
			SwingUtilities.invokeLater(this);
		}
	}

	public void run() {
		Boolean state = button.isSelected();
		Bean<T> bean = getBean();
		if (bean != null) {
			PropertyDescriptor pd = bean.getProperty(property);
			bean.setValue(pd, state);
		}
	}
}
