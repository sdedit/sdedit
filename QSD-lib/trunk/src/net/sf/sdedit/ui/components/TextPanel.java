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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextPanel extends JPanel implements KeyListener, ActionListener {
    
    private JLabel label;
    
    private JTextField textField;
    
    private JButton button;
    
    private List<TextPanelListener> listeners;
    
    public TextPanel () {
        super();
        setLayout(new BorderLayout());
        label = new JLabel();
        textField = new JTextField();
        button = new JButton();
        add(label, BorderLayout.WEST);
        add(textField, BorderLayout.CENTER);
        add(button, BorderLayout.EAST);
        button.addActionListener(this);
        textField.addActionListener(this);
        textField.addKeyListener(this);
        listeners = new LinkedList<TextPanelListener>();
    }
    
    public void addTextPanelListener (TextPanelListener listener) {
        listeners.add(listener);
    }
    
    public void setLabel (String label) {
        this.label.setText(label);
    }
    
    public void setText (String text) {
        textField.removeKeyListener(this);
        textField.setText(text);
        textField.addKeyListener(this);
    }
    
    public void setButtonCaption (String caption) {
        button.setText(caption);
    }
    
    public String getText () {
        return textField.getText();
    }

    public void keyPressed(KeyEvent e) {
        /* empty */
    }

    public void keyReleased(KeyEvent e) {
        /* empty */
    }

    public void keyTyped(KeyEvent e) {
        for (TextPanelListener tpl : listeners) {
            tpl.textChanged(this, getText());
        }
    }

    public void actionPerformed(ActionEvent e) {
        for (TextPanelListener tpl : listeners) {
            tpl.textEntered(this, getText());
        }
    }

}
