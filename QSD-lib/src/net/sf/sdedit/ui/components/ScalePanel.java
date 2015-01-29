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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.ui.components.buttons.ManagedAction;

/*
 * TODO: parameterize the "scaling curve"
 */

public class ScalePanel extends JPanel implements Observer, MouseInputListener {

	private final static long serialVersionUID = 0xAB343925;

	private static ImageIcon fitHeightIcon = Icons.getIcon("large/fit_height");

	private static ImageIcon fitWidthIcon = Icons.getIcon("large/fit_width");

	private static ImageIcon fitWindowIcon = Icons.getIcon("large/fit_window");

	private static ImageIcon normalSizeIcon = Icons.getIcon("large/normalsize");

	private JSlider scaleSlider;

	private JLabel sliderLabel;

	private Scalable scalable;

	private Point root = null;

	private int initialValue;

	public ScalePanel(boolean showButtons) {
		super();
		setLayout(new BorderLayout());
		sliderLabel = new JLabel();
		sliderLabel.setToolTipText("Zoom factor");
		sliderLabel.setPreferredSize(new Dimension(40, 1));
		sliderLabel.setOpaque(false);
		add(sliderLabel, BorderLayout.WEST);

		scaleSlider = new JSlider(1, 400, 100);
		scaleSlider.setPreferredSize(new Dimension(100, 0));
		scaleSlider.setMaximumSize(new Dimension(100, 0));
		scaleSlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (ScalePanel.this.isEnabled() && e.getClickCount() == 2
						&& e.getButton() == MouseEvent.BUTTON1) {
					scalable.setScale(1);
				}
			}
		});
		scaleSlider.setOpaque(false);

		scaleSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double scale = scaleSlider.getValue() * scaleSlider.getValue()
						/ 40000D;
				scale = Math.max(0.01, scale);
				scalable.setScale(scale);
			}
		});
		add(scaleSlider, BorderLayout.CENTER);

		if (showButtons) {

			JPanel sliderButtonsPanel = new JPanel();

			sliderButtonsPanel.setLayout(new GridLayout(1, 4));
			JButton normalSizeButton = new JButton(normalSizeAction);
			JButton fitHeightButton = new JButton(fitHeightAction);
			JButton fitWidthButton = new JButton(fitWidthAction);
			JButton fitWindowButton = new JButton(fitWindowAction);

			normalSizeButton.setMargin(new Insets(1, 1, 1, 1));
			normalSizeButton.setOpaque(false);

			fitHeightButton.setMargin(new Insets(1, 1, 1, 1));
			fitHeightButton.setOpaque(false);

			fitWidthButton.setMargin(new Insets(1, 1, 1, 1));
			fitWidthButton.setOpaque(false);

			fitWindowButton.setMargin(new Insets(1, 1, 1, 1));
			fitWindowButton.setOpaque(false);

			sliderButtonsPanel.add(normalSizeButton);
			sliderButtonsPanel.add(fitHeightButton);
			sliderButtonsPanel.add(fitWidthButton);
			sliderButtonsPanel.add(fitWindowButton);
			sliderButtonsPanel.setOpaque(false);

			add(sliderButtonsPanel, BorderLayout.EAST);
		}
	}

	public final Action fitWidthAction = new AbstractAction() {

		{
			putValue(Action.SHORT_DESCRIPTION, "Fit width");
			putValue(Action.SMALL_ICON, fitWidthIcon);
			putValue(ManagedAction.ID, "FIT_WIDTH");

		}

		public void actionPerformed(ActionEvent e) {
			fitWidth();
		}
	};

	public final Action fitHeightAction = new AbstractAction() {

		{
			putValue(Action.SHORT_DESCRIPTION, "Fit height");
			putValue(Action.SMALL_ICON, fitHeightIcon);
			putValue(ManagedAction.ID, "FIT_HEIGHT");

		}

		public void actionPerformed(ActionEvent e) {
			fitHeight();
		}
	};

	public final Action fitWindowAction = new AbstractAction() {

		{
			putValue(Action.SHORT_DESCRIPTION, "Fit window");
			putValue(Action.SMALL_ICON, fitWindowIcon);
			putValue(ManagedAction.ID, "FIT_WINDOW");

		}

		public void actionPerformed(ActionEvent e) {
			fitWindow();
		}
	};

	public final Action normalSizeAction = new AbstractAction() {

		{
			putValue(Action.SHORT_DESCRIPTION, "Zoom to 100 %");
			putValue(Action.SMALL_ICON, normalSizeIcon);
			putValue(ManagedAction.ID, "NORMAL_SIZE");

		}

		public void actionPerformed(ActionEvent e) {
			normalsize();
		}
	};

	private void normalsize() {
		if (scalable != null) {
			scalable.setScale(1);
		}
	}

	private void fitWidth() {
		if (scalable != null) {
			scalable.fitWidth();
		}
	}

	private void fitHeight() {
		if (scalable != null) {
			scalable.fitHeight();
		}
	}

	private void fitWindow() {
		if (scalable != null) {
			scalable.fitSize();
		}
	}

	public void setScalable(final Scalable scalable) {
		if (this.scalable != null) {
			this.scalable.asObservable().deleteObservers();
		}
		scalable.asObservable().addObserver(this);
		this.scalable = scalable;
		update(scalable.asObservable(), scalable);
	}

	public JSlider getSlider() {
		return scaleSlider;
	}

	public void mouseClicked(MouseEvent e) { /* empty */
	}

	public void mouseEntered(MouseEvent e) { /* empty */
	}

	public void mouseExited(MouseEvent e) { /* empty */
	}

	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			root = e.getPoint();
			initialValue = scaleSlider.getValue();
		}
	}

	public void mouseReleased(MouseEvent e) {
		root = null;
	}

	@Override
	public void setEnabled(boolean on) {
		super.setEnabled(on);
		scaleSlider.setEnabled(on);
		sliderLabel.setEnabled(on);
	}

	public void mouseDragged(MouseEvent e) {
		if (root != null) {
			int dist = (root.y - e.getPoint().y) / 4;
			if (Math.abs(dist) > 0) {
				int newValue = initialValue + dist;
				newValue = Math.max(newValue, scaleSlider.getMinimum());
				newValue = Math.min(newValue, scaleSlider.getMaximum());
				scaleSlider.setValue(newValue);
			}
		}
	}

	public void mouseMoved(MouseEvent e) { /* empty */
	}

	public synchronized void update(Observable o, Object arg) {
		Scalable _scalable = (Scalable) arg;
		double scale = _scalable.getScale();
		int s = (int) (200 * Math.sqrt(scale));
		String text = (int) (scale * 100) + " %";
		sliderLabel.setText(text);
		scaleSlider.setValue(s);
	}

}