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
package net.sf.sdedit.ui.components;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.ui.components.buttons.ManagedAction;

@SuppressWarnings("serial")
public class ToolBar extends JToolBar {

	private Map<String, JButton> buttonMap;
	

	public ToolBar() {
		super();
		buttonMap = new HashMap<String, JButton>();
	}
	
	private static String getId(Action action) {
		String id = (String) action.getValue(ManagedAction.ID);
		if (id == null) {
			throw new IllegalArgumentException("no id defined"
					+ " for action with name " + action.getValue(Action.NAME));
		}
		return id;
	}

	@SuppressWarnings("serial")
	private static Action makeToolbarAction(final Action action) {

		return new AbstractAction() {
			{
				putValue(Action.NAME, action.getValue(Action.NAME));
				putValue(Action.SHORT_DESCRIPTION, action
						.getValue(Action.SHORT_DESCRIPTION));
				String iconName = (String) action
						.getValue(ManagedAction.ICON_NAME);
				if (iconName != null) {
					Icon icon = Icons.getIcon("large/" + iconName);
					putValue(Action.SMALL_ICON, icon);
				} else {
					putValue(Action.SMALL_ICON, action
							.getValue(Action.SMALL_ICON));
				}
			}

			public void actionPerformed(ActionEvent e) {
				action.actionPerformed(e);
			}
		};
	}
	
	public void disableAction (String id) {
				
	}

	public void updateAction(Action action) {

		JButton button = buttonMap.get(getId(action));
		if (button == null) {
			throw new IllegalArgumentException(
					"no button for action with name "
							+ action.getValue(Action.NAME));
		}
		button.setAction(makeToolbarAction(action));

	}

	public JButton addAction(Action action) {
		// Action action = actionManager.addAction(_action);
		JButton button = super.add(makeToolbarAction(action));
		button.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 5));
		button.setOpaque(false);
		button.setMargin(new Insets(1, 1, 1, 1));
		buttonMap.put(getId(action), button);
		
		return button;
		// registerComponent(button, action, activator);
	}

	public void addSeparator() {
		JSeparator sep = new JSeparator();
		sep.setOrientation(SwingConstants.VERTICAL);
		Dimension size = new Dimension(10, 25);
		sep.setMinimumSize(size);
		sep.setPreferredSize(size);
		sep.setMaximumSize(size);
		super.add(sep);
	}

}
