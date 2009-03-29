package net.sf.sdedit.ui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;

import net.sf.sdedit.util.Utilities;

public class JTreeSearcherUI extends JPanel implements ActionListener {

	private JTreeSearcher searcher;

	private int treeNumber;

	private JButton button;

	private JTextField textField;

	public JTreeSearcherUI(JTreeSearcher searcher, JTree tree) {
		this.searcher = searcher;
		if (tree != null) {
			treeNumber = Utilities.indexOf(searcher.getTrees(), tree);
		} else {
			treeNumber = -1;
		}
		button = new JButton("Search");
		textField = new JTextField();
		setLayout(new BorderLayout());
		add(textField, BorderLayout.CENTER);
		add(button, BorderLayout.WEST);
		textField.addActionListener(this);
		button.addActionListener(this);
	}

	public JTreeSearcherUI(JTreeSearcher searcher) {
		this(searcher, null);
	}

	public int getTreeNumber() {
		return treeNumber;
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
