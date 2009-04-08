package net.sf.sdedit.ui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;

public class JTreeSearcherUI extends JPanel implements ActionListener {

	private JTreeSearcher searcher;

	private JButton button;
	
	private JTree tree;

	private JTextField textField;

	public JTreeSearcherUI(JTreeSearcher searcher, JTree tree) {
		this.searcher = searcher;
		button = new JButton("Search");
		textField = new JTextField();
		setLayout(new BorderLayout());
		add(textField, BorderLayout.CENTER);
		add(button, BorderLayout.WEST);
		textField.addActionListener(this);
		button.addActionListener(this);
		this.tree = tree;
	}

	public JTreeSearcherUI(JTreeSearcher searcher) {
		this(searcher, null);
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
