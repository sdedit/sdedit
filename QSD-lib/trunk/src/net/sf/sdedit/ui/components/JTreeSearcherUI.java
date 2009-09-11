package net.sf.sdedit.ui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;

import net.sf.sdedit.util.UIUtilities;

public class JTreeSearcherUI extends JPanel implements ActionListener {

	private JTreeSearcher searcher;

	private JButton button;
	
	private JTree tree;

	private JTextField textField;

	public JTreeSearcherUI(JTreeSearcher searcher, JTree tree, String caption) {
		this.searcher = searcher;
		button = new JButton(caption);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(button, BorderLayout.CENTER);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(2,0,2,2));
		textField = new JTextField();
		setLayout(new BorderLayout());
		JPanel textPanel = UIUtilities.borderedPanel(textField,2,2,2,0,true);
		add(textPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.WEST);
		textField.addActionListener(this);
		button.addActionListener(this);
		this.tree = tree;
		searcher.register(this);
	}
	
	public void DESTROY () {
		searcher.deregister(this);
		tree = null;
		searcher = null;
		textField.removeActionListener(this);
		button.removeActionListener(this);
		textField = null;
		button = null;
		
	}

	public JTreeSearcherUI(JTreeSearcher searcher, String caption) {
		this(searcher, null, caption);
	}
	
	public JTextField getTextField () {
		return textField;
	}
	
	public JTree getTree () {
		return tree;
	}


	public void actionPerformed(ActionEvent e) {
		e.setSource(this);
		searcher.actionPerformed(e);
	}

	public void setSearchText(String text) {
		textField.setText(text);
	}

	public String getSearchText() {
		return textField.getText();
	}

}
