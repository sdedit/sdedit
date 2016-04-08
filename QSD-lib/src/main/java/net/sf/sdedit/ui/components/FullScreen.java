// Copyright (c) 2006 - 2016, Markus Strauch.
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
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import net.sf.sdedit.icons.Icons;

public class FullScreen extends JFrame implements KeyListener, ActionListener {

	private final static long serialVersionUID = 0xAB343922;

	private ZoomPane zoomPane;

	private GraphicsDevice gd;

	private ScalePanel scalePanel;

	private boolean locked;

	private JPanel closePanel;

	private Timer hideClosePanelTimer;

	private static final ImageIcon noFullScreenIcon = Icons.getIcon("large/nofullscreen");

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == hideClosePanelTimer) {
			if (!locked && closePanel.isVisible()) {
				closePanel.setVisible(false);
				getContentPane().repaint();
			}
		} else {
			returnFromFullScreenMode();
		}
	}

	private MouseMotionListener mml = new MouseMotionAdapter() {
		@Override
		public void mouseMoved(MouseEvent e) {
			hideClosePanelTimer.restart();
			closePanel.setVisible(true);
		}
	};

	private MouseListener ml = new MouseAdapter() {

		@Override
		public void mouseEntered(MouseEvent e) {
			locked = true;
		}

		@Override
		public void mouseExited(MouseEvent e) {
			locked = false;
		}
	};

	public FullScreen() {
		super();
		gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		zoomPane = new ZoomPane();
		zoomPane.setRoot ((JComponent) getGlassPane());
		getContentPane().setLayout(new BorderLayout());
		JButton close = new JButton(noFullScreenIcon);
		close.setMargin(new Insets(0, 0, 0, 0));
		close.addActionListener(this);
		getContentPane().add(zoomPane, BorderLayout.CENTER);
		addKeyListener(this);
		setUndecorated(true);
		scalePanel = new ScalePanel(true);
		scalePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		scalePanel.getSlider().addMouseListener(ml);
		scalePanel.setScalable(zoomPane);

		closePanel = new JPanel();
		closePanel.setLayout(new BorderLayout());
		closePanel.add(close, BorderLayout.EAST);
		closePanel.add(scalePanel, BorderLayout.CENTER);
		JPanel glass = (JPanel) getGlassPane();
		glass.setVisible(true);
		glass.setLayout(new BorderLayout());
		JPanel top = new JPanel();
		top.setOpaque(false);
		top.setLayout(new BorderLayout());
		glass.add(top, BorderLayout.NORTH);
		top.add(closePanel, BorderLayout.EAST);
		hideClosePanelTimer = new Timer(1000, this);
		hideClosePanelTimer.start();
	}

	public ZoomPane getZoomPane() {
		return zoomPane;
	}

	public ScalePanel getScalePanel() {
		return scalePanel;
	}

	/**
	 * Displays a zoomable component in full screen mode.
	 * 
	 * @param comp
	 *            the component to be displayed in full screen mode
	 * @param originalWidth
	 *            the original (unscaled) width of the component
	 * @param originalHeight
	 *            the original (unscaled) height of the component
	 */
	public void display(Zoomable<? extends JComponent> comp) {
		gd.setFullScreenWindow(this);
		setVisible(true);
		comp.asJComponent().removeMouseMotionListener(mml);
		comp.asJComponent().addMouseMotionListener(mml);
		zoomPane.setViewportView(comp);
		closePanel.setVisible(false);
		locked = false;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				requestFocus();
			}
		});
	}

	private void returnFromFullScreenMode() {
		gd.setFullScreenWindow(null);
		setVisible(false);
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE
				|| KeyEvent.getKeyText(e.getKeyCode()).equals("F9")) {
			returnFromFullScreenMode();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

}
