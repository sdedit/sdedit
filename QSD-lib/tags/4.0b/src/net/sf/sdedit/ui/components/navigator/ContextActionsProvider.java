package net.sf.sdedit.ui.components.navigator;

import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;

public interface ContextActionsProvider {
	
	public List<Action> getContextActions (JComponent component);

}
