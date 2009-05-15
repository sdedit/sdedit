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
		if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
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
