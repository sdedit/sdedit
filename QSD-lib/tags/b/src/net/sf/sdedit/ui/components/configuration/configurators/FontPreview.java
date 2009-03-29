//Copyright (c) 2006 - 2008, Markus Strauch.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.

package net.sf.sdedit.ui.components.configuration.configurators;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.Timer;

public class FontPreview implements ActionListener, MouseListener, MouseMotionListener
{
    private Timer timer;
    
    private static final int delay = 500;
    
    private JComponent glassPane;
    
    private JTable table;
    
    private JLabel label;
    
    private Point position;
    
    public FontPreview (JDialog dialog, JTable table) {
        this.glassPane = (JComponent) dialog.getGlassPane();
        this.table = table;
        table.addMouseMotionListener(this);
        table.addMouseListener(this);
        timer = new Timer(delay, this);
        label = new JLabel();
        label.setSize(140,60);
        label.setOpaque(true);
        label.setBackground(Color.YELLOW);
        label.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        position = null;
        timer.start();
    }

    public void mouseMoved(MouseEvent e) {
        timer.restart();
        hideLabel();
        position = e.getPoint();
    }
    
    private void hideLabel () {
        label.setVisible(false);
        glassPane.remove(label);
        glassPane.revalidate();
    }
    
    public void actionPerformed (ActionEvent e) {
        if (position == null) {
            return;
        }
        int r = table.rowAtPoint(position);
        if (r == -1) {
            return;
        }
        String fontName = (String) table.getValueAt(r, 0);
        Font font = new Font(fontName, Font.PLAIN, 14);
        label.setText("");
        label.setFont(font);
        label.setText("<html><center>&nbsp;<u>" + fontName + "</u>&nbsp;<br>&nbsp;The quick brown&nbsp;<br>&nbsp;fox jumps over&nbsp;<br>&nbsp;the lazy dog.");
        label.setSize(label.getPreferredSize());
        int x = glassPane.getWidth() / 2 - label.getWidth() / 2;
        int y = glassPane.getHeight() / 2 - label.getHeight() / 2;
        label.setLocation(x, y);
        glassPane.add(label);
        label.setVisible(true);
        glassPane.updateUI();
    }

    public void mouseClicked(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {
        timer.restart();
    }

    public void mouseExited(MouseEvent e) {
        timer.stop();
        hideLabel();
    }

    public void mousePressed(MouseEvent e) {/* empty */}

    public void mouseReleased(MouseEvent e) {/* empty */}

    public void mouseDragged(MouseEvent e) {/* empty */}
}
