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

package net.sf.sdedit.ui.components.buttons;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;

public class ManagedAction implements Action {
	
	public static final String NEW_TEXT = "NewName";
	
	public static final String ID = "ActionID";
	
	public static final String ICON_NAME = "IconName";
	
	private final Action delegate;
	
	private Action overloaded;
	
	public ManagedAction (Action delegate) {
		this.delegate = delegate;
	}
	
	public void overload (Action overloaded) {
		this.overloaded = overloaded;
	}
	
	public void unload () {
		overloaded = null;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (overloaded != null) {
			overloaded.addPropertyChangeListener(listener);
		} else {
			delegate.addPropertyChangeListener(listener);
		}
	}

	public Object getValue(String key) {
		if (overloaded != null) {
			return overloaded.getValue(key);
		}
		return delegate.getValue(key);
	}

	public boolean isEnabled() {
		if (overloaded != null) {
			return overloaded.isEnabled();
		}
		return delegate.isEnabled();
	}

	public void putValue(String key, Object value) {
		if (overloaded != null) {
			overloaded.putValue(key, value);
		} else {
			delegate.putValue(key, value);
		}

	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (overloaded != null) {
			overloaded.removePropertyChangeListener(listener);
		} else {
			delegate.removePropertyChangeListener(listener);
		}

	}

	public void setEnabled(boolean b) {
		if (overloaded != null) {
			overloaded.setEnabled(b);
		} else {
			delegate.setEnabled(b);
		}

	}

	public void actionPerformed(ActionEvent e) {
		if (overloaded != null) {
			overloaded.actionPerformed(e);
		} else {
			delegate.actionPerformed(e);
		}

	}

}
