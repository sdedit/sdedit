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
