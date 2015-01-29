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

package net.sf.sdedit.editor;

import net.sf.sdedit.ui.Tab;
import net.sf.sdedit.ui.UserInterface;
import net.sf.sdedit.ui.components.buttons.Activator;

/**
 * A <tt>TabActivator</tt> is an {@linkplain Activator} related to a certain
 * type of {@linkplain Tab}.
 * <p>
 * If the tab currently selected (see {@linkplain UserInterface#currentTab()})
 * is not of that type, {@linkplain #isEnabled()} returns <tt>false</tt>.
 * Otherwise the result of {@linkplain #_isEnabled(Tab)}.
 * 
 * @author Markus Strauch
 * 
 */
public abstract class TabActivator<T extends Tab> implements Activator {

	protected Class<T> tabClass;

	protected UserInterface ui;

	public TabActivator(Class<T> tabClass, UserInterface ui) {
		if (ui == null) {
			throw new IllegalArgumentException("ui reference must not be null");
		}
		this.tabClass = tabClass;
		this.ui = ui;
	}

	public final boolean isEnabled() {
		Tab tab = ui.currentTab();
		if (tab != null && tabClass.isInstance(tab)) {
			return _isEnabled(tabClass.cast(tab));
		}
		return false;
	}

	protected abstract boolean _isEnabled(T tab);

}
