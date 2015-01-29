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
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.Timer;

import net.sf.sdedit.ui.components.configuration.ConfigurationAction;
import net.sf.sdedit.util.collection.OntoMap;

public class ActionManager implements ActionListener {

	private Map<String, ManagedAction> actionMap;

	private Map<JComponent, Activator> activatorMap;

	private OntoMap<JComponent, String> actionNames;
	
	private Map<JComponent, String> originalTexts;

	private List<ConfigurationAction<?>> configurationActions;

	private Map<String, Activator> overloadedActivators;

	private Timer timer;

	private boolean timeToEnable;

	public ActionManager() {
		activatorMap = new HashMap<JComponent, Activator>();
		configurationActions = new LinkedList<ConfigurationAction<?>>();
		actionNames = new OntoMap<JComponent, String>(LinkedList.class);
		actionMap = new HashMap<String, ManagedAction>();
		overloadedActivators = new HashMap<String, Activator>();
		originalTexts = new HashMap<JComponent,String>();
		timer = new Timer(250, this);
		timeToEnable = false;
		timer.start();
	}

	private String getId(Action action) {
		String id = (String) action.getValue(ManagedAction.ID);
		if (id != null) {
			return id;
		}
		return (String) action.getValue(Action.NAME);

	}

	private void _activateComponents() {
		for (Map.Entry<JComponent, Activator> entry : activatorMap.entrySet()) {
			JComponent comp = entry.getKey();
			Activator activator = entry.getValue();
			String actionName = actionNames.getImage(comp);
			if (actionName != null
					&& overloadedActivators.containsKey(actionName)) {
				activator = overloadedActivators.get(actionName);
			}
			comp.setEnabled(activator.isEnabled());
		}
		for (ConfigurationAction<?> action : configurationActions) {
			action.update();
		}
	}

	public synchronized void enableComponents() {
		timeToEnable = true;
		timer.restart();
	}

	public synchronized void actionPerformed(ActionEvent e) {
		if (timeToEnable) {
			_activateComponents();
			timeToEnable = false;
		}
	}

	public void registerButton(JComponent comp, Action action,
			Activator activator) {
		if (activator != null) {
			activatorMap.put(comp, activator);
		}
		if (action != null) {
			String name = getId(action);
			actionNames.add(comp, name);
		}
	}

	public void registerConfigurationAction(ConfigurationAction<?> action,
			AbstractButton button) {
		action.setButton(button);
		configurationActions.add(action);
	}

	public Action addAction(Action action) {
		String id = getId(action);
		ManagedAction managedAction = actionMap.get(id);
		if (managedAction == null) {
			managedAction = new ManagedAction(action);
			actionMap.put(id, managedAction);
		}
		return managedAction;
	}

	public void overload(Action overload, Activator activator) {
		String name = getId(overload);
		ManagedAction managedAction = actionMap.get(name);
		if (managedAction == null) {
			throw new IllegalArgumentException(
					"There is no managed action with the name " + name);
		}
		managedAction.overload(overload);
		if (activator != null) {
			overloadedActivators.put(name, activator);
		}
		String newText = (String) overload.getValue(ManagedAction.NEW_TEXT);
		if (newText != null) {
			for (JComponent comp : actionNames.getPreImages(name)) {
				if (comp instanceof AbstractButton) {
					AbstractButton button = (AbstractButton) comp;
					if (button.getText() != null && button.getText().length() > 0)
					{
						originalTexts.put(button, button.getText());
						button.setText(newText.replaceAll("&", ""));
					}
				}
			}
		}
	}

	public void unload(Action overload) {
		String id = getId(overload);
		ManagedAction managedAction = actionMap.get(id);
		if (managedAction == null) {
			throw new IllegalArgumentException(
					"There is no managed action with the name " + id);
		}
		managedAction.unload();
		overloadedActivators.remove(id);
		for (JComponent comp : actionNames.getPreImages(id)) {
			if (comp instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) comp;
				if (button.getText() != null && button.getText().length() > 0)
				{
					button.setText(originalTexts.get(button));
				}
			}
		}
	}

}
