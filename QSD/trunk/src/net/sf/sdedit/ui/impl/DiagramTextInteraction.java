package net.sf.sdedit.ui.impl;

import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.util.List;

import javax.swing.Action;

import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.drawable.ExtensibleDrawable;
import net.sf.sdedit.drawable.Note;
import net.sf.sdedit.drawable.Rectangle;
import net.sf.sdedit.editor.Editor;

public class DiagramTextInteraction extends DiagramInteraction {

	private DiagramTextTab tab;

	public DiagramTextInteraction(DiagramTextTab tab) {
		this.tab = tab;
	}

	@Override
	protected List<Action> _getContextActions(Drawable drawable) {
		return null;
	}

	/**
	 * Moves the cursor to the position in the text area where the object or
	 * message corresponding to the drawable instance is declared.
	 * 
	 * @param drawable
	 *            a drawable instance to show the corresponding declaration for
	 */
	@Override
	protected void _mouseClickedDrawable(MouseEvent ev, Drawable drawable) {
		Diagram diag = tab.getDiagram();
		if (diag != null) {
			Integer pos = (Integer) diag.getStateForDrawable(drawable);
			if (pos != null) {
				tab.moveCursorToPosition(pos - 1);
			}
		}
		if (drawable instanceof Note) {
			Note note = (Note) drawable;
			URI link = note.getLink();
			if (link != null) {
				File current = tab.getFile();
				File linked;
				if (current != null) {
					linked = new File(current.toURI().resolve(link));
				} else {
					linked = new File(link);
				}
				if (!tab.get_UI().selectTabWith(linked)) {
					try {
						Editor.getEditor().load(linked.toURI().toURL());
					} catch (RuntimeException e) {
						throw e;
					} catch (Exception e) {
						tab.get_UI().errorMessage(e, null, null);
					}
				}

			}
		}

	}

	/**
	 * Returns true if and only if the given drawable instance is associated by
	 * the most recently used diagram with a DiagramDataProvider state, id est a
	 * position in the text area.
	 * 
	 * @param drawable
	 *            a drawable instance such that the mouse has just entered it
	 * 
	 * @return true if and only if the given drawable instance is associated by
	 *         the most recently used diagram with a position in the text area
	 */
	@Override
	protected boolean _mouseEnteredDrawable(Drawable drawable) {
		Diagram diag = tab.getDiagram();
		if (diag != null && diag.getStateForDrawable(drawable) != null) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean _mouseExitedDrawable(Drawable drawable) {
		return false;
	}

	public String getTooltip(Drawable drawable) {
		String text = null;
		if (drawable instanceof ExtensibleDrawable) {
			ExtensibleDrawable ed = (ExtensibleDrawable) drawable;
			Lifeline lifeline = ed.getLifeline();
			text = lifeline.toString();
			if (text.startsWith("/")) {
				text = text.substring(1);
			}
			if (lifeline.getDiagram().getConfiguration().isThreaded()
					&& !lifeline.isAlwaysActive() && (ed instanceof Rectangle)) {
				text = text + " [thread=" + lifeline.getThread() + "]";
			}

		}
		return text;
	}

}
