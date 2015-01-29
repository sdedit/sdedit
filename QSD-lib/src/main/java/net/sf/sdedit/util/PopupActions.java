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
package net.sf.sdedit.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class PopupActions implements MouseListener {

	public static interface ContextHandler {

		public Object getObjectForCurrentContext(JComponent comp);

	}

	/**
	 * A <tt>Provider</tt> implementation holds a single <tt>PopupActions</tt>
	 * instance that is created with some <tt>ContextHandler</tt> when
	 * {@linkplain #getPopupActions(ContextHandler)} is called for the first
	 * time.
	 * <p>
	 * All subsequent calls to this method return the same <tt>PopupActions</tt>
	 * , regardless of the parameter (it may as well be <tt>null</tt>).
	 * 
	 * @author Markus Strauch
	 * 
	 */
	public static interface Provider {

		/**
		 * Returns the single <tt>PopupActions</tt> instance held by this
		 * <tt>Provider</tt>. The instance is created, using the given
		 * <tt>ContextHandler</tt>, when this method is called for the
		 * first time.
		 * 
		 * @param contextHandler
		 * @return
		 */
		public PopupActions getPopupActions(ContextHandler contextHandler);

	}

	@SuppressWarnings("serial")
	public static abstract class Action extends AbstractAction {

		protected abstract boolean beforePopup(Object context);

	}

	private JComponent component;

	private ContextHandler contextHandler;

	private List<Action> actions;

	public PopupActions(JComponent component, ContextHandler contextHandler) {
		this.component = component;
		this.contextHandler = contextHandler;
		actions = new LinkedList<Action>();
		component.addMouseListener(this);
	}

	public void addAction(Action action) {
		actions.add(action);
	}

	public void mouseClicked(MouseEvent e) {
		if (component.isEnabled() && SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
			Object context = contextHandler.getObjectForCurrentContext(component);
			boolean show = false;
			JPopupMenu menu = new JPopupMenu();
			for (Action action : actions) {
				if (action.beforePopup(context)) {
					show = true;
					menu.add(action);
				}
			}
			if (show) {
				menu.show(component, e.getX(), e.getY());
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

}
