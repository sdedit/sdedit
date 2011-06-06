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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sf.sdedit.ui.Tab;
import net.sf.sdedit.ui.UserInterface;

/**
 * A <tt>TabAction</tt> is an <tt>Action</tt> that is related to
 * a {@linkplain Tab} of a certain type ({@linkplain T}).
 * <p>
 * 
 * @author Markus Strauch
 *
 * @param <T> the type of the {@linkplain Tab} this action is related
 * to
 */
public abstract class TabAction<T extends Tab> extends AbstractAction {
	
	protected final Class<T> tabClass;
	
	protected final UserInterface ui;
	
	public TabAction (Class<T> tabClass, UserInterface ui) {
		if (ui == null) {
			throw new IllegalArgumentException ("ui reference must not be null");
		}
		this.tabClass = tabClass;
		this.ui = ui;
	}
	
	/**
	 * Performs an action related to a type <tt>T</tt> {@linkplain Tab}.
	 * 
	 * @param tab a non-null reference to a {@linkplain Tab}
	 * @param e
	 */
	protected abstract void _actionPerformed(T tab, ActionEvent e);

	public final void actionPerformed(ActionEvent e) {
		Tab tab = ui.currentTab();
		if (tab != null && tabClass.isInstance(tab)) {
			_actionPerformed (tabClass.cast(tab), e);
		}
	}

}
