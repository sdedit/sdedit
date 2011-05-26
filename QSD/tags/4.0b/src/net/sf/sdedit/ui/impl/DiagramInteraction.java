package net.sf.sdedit.ui.impl;

import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Action;

import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.ui.PanelPaintDevicePartner;

public abstract class DiagramInteraction implements PanelPaintDevicePartner {

	private Drawable drawable;

	public void mouseClickedDrawable(MouseEvent e, Drawable drawable) {
		_mouseClickedDrawable(e, drawable);

	}

	protected abstract void _mouseClickedDrawable(MouseEvent e,
			Drawable drawable);

	public boolean mouseEnteredDrawable(Drawable drawable) {
		this.drawable = drawable;
		return _mouseEnteredDrawable(drawable);
	}

	protected abstract boolean _mouseEnteredDrawable(Drawable drawable);

	public void mouseExitedDrawable(Drawable drawable) {
		drawable = null;
		_mouseExitedDrawable(drawable);

	}

	protected abstract boolean _mouseExitedDrawable(Drawable drawable);

	public List<Action> getContextActions() {
		if (drawable == null) {
			return null;
		}
		return _getContextActions(drawable);
	}

	protected abstract List<Action> _getContextActions(Drawable drawable);

}
