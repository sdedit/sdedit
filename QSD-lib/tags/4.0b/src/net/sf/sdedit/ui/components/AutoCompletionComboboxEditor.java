package net.sf.sdedit.ui.components;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import net.sf.sdedit.ui.components.AutoCompletion.SuggestionProvider;

public class AutoCompletionComboboxEditor implements ComboBoxEditor,
SuggestionProvider {
	
	private JComboBox box;
	
	private JTextField textField;
	
	public AutoCompletionComboboxEditor (JComboBox box) {
		this.box = box;
		textField = new JTextField();
		new AutoCompletion(textField,this);
	}

	private String [] getStrings () {
		int n = box.getModel().getSize();
		String [] strings = new String [n];
		for (int i = 0; i < n; i++) {
			strings [i] = box.getModel().getElementAt(i).toString();
		}
		return strings;
	}
	

	public void addActionListener(ActionListener l) {
		textField.addActionListener(l);
		
	}

	public Component getEditorComponent() {
		return textField;
	}

	public Object getItem() {
		return textField.getText();
	}

	public void removeActionListener(ActionListener l) {
		textField.removeActionListener(l);
		
	}

	public void selectAll() {
		textField.selectAll();
		
	}

	public void setItem(Object anObject) {
		textField.setText(anObject == null ? "" : anObject.toString());
		
	}

	public List<String> getSuggestions(String prefix) {
		String regexp = "^" + prefix.replaceAll("\\*", ".*") + ".*$";
		Pattern pattern = Pattern.compile(regexp,Pattern.CASE_INSENSITIVE);
		
		List<String> suggestions = new LinkedList<String>();
		for (String str : getStrings()) {
			if (pattern.matcher(str).matches()) {
				suggestions.add(str);
			}
		}
		return suggestions;
	}
	
	

}
