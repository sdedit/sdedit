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
