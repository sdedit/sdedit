package net.sf.sdedit.ui.components.navigator;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import net.sf.sdedit.ui.components.Stainable;
import net.sf.sdedit.ui.components.StainedListener;

class TreeNavigatorControl implements TreeSelectionListener, StainedListener,
		MouseListener {

	private TreeNavigatorModel treeModel;

	private TreeNavigatorPane navigator;

	private TreePath[] formerSelection;
	
	private boolean ignoreValueChanged;

	private ContextActionsProvider contextActionsProvider;

	protected TreeNavigatorControl(TreeNavigatorPane navigator,
			TreeNavigatorModel treeModel) {
		this.navigator = navigator;
		this.treeModel = treeModel;
		formerSelection = new TreePath[0];
	}

	protected void setContextActionsProvider(ContextActionsProvider provider) {
		this.contextActionsProvider = provider;
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		if (ignoreValueChanged) {
			return;
		}
		JComponent comp;
		JComponent firstComp = null;
		TreePath[] paths = navigator.getTree().getSelectionPaths();
		if (paths != null) {
			for (TreePath path : navigator.getTree().getSelectionPaths()) {
				TreeNavigatorNode node = (TreeNavigatorNode) path
						.getLastPathComponent();
				comp = node.getComponent();
				if (comp == null) {
					ignoreValueChanged = true;
					navigator.getTree().setSelectionPaths(formerSelection);
					ignoreValueChanged = false;
					return;
				}
				if (firstComp == null) {
					firstComp = comp;					
				}
			}
			if (firstComp != null) {
				navigator.setSelectedComponent(firstComp, false, true);
				formerSelection = navigator.getTree().getSelectionPaths();
			}
		}
	}

	public void stainedStatusChanged(Stainable stainable, boolean stained) {
		JComponent changed = JComponent.class.cast(stainable);
		treeModel.fireComponentChanged(changed);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (navigator.getSelectedComponent() != null
				&& contextActionsProvider != null
				&& SwingUtilities.isRightMouseButton(e)) {
			List<Action> actions = contextActionsProvider
					.getContextActions(navigator.getSelectedComponent());
			if (actions != null && !actions.isEmpty()) {
				JPopupMenu menu = null;
				menu = new JPopupMenu();
				for (Action action : actions) {
					menu.add(action);
				}
				menu.show((Component) e.getSource(), e.getX(), e.getY());
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

}
