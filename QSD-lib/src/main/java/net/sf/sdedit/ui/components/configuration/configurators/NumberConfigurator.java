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

package net.sf.sdedit.ui.components.configuration.configurators;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyDescriptor;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.Configurator;
import net.sf.sdedit.ui.components.configuration.DataObject;

public class NumberConfigurator<C extends DataObject> extends Configurator<Integer,C> 
implements ChangeListener
{
	private JSpinner spinner;
	
	private int min;
	
	private int max;
	
	private JLabel label;
	
	public NumberConfigurator(Bean<C> bean, PropertyDescriptor property) {
		super(bean, property);
		initialize();
	}
	
	private void initialize () {
		setLayout(new BorderLayout());
		Adjustable adjustable = getProperty().getWriteMethod().getAnnotation(Adjustable.class);
		min = adjustable.min();
		max = adjustable.max();
        SpinnerNumberModel model = new SpinnerNumberModel(
                adjustable.min(), adjustable.min(), adjustable.max(),
                adjustable.step());
        spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(100,20));
        spinner.addChangeListener(this);
        label = new JLabel(adjustable.info());
        label.setBorder(BorderFactory.createEmptyBorder(0,4,0,0));
        add(spinner, BorderLayout.WEST);
        add(label, BorderLayout.CENTER);
	}

	@Override
	protected void refresh(Integer value) {
		if (value < min) {
			value = min;
		} else if (value > max) {
			value = max;
		}
		spinner.setValue(value);
	}

	@Override
	protected void _actionPerformed(ActionEvent e) {
		int value = (Integer) spinner.getValue();
		applyValue(value);
	}
	
	public void stateChanged(ChangeEvent e) {
		actionPerformed(null);
	}
	
	@Override
	protected Integer getNullValue() {
		return getProperty().getWriteMethod().getAnnotation(Adjustable.class).dflt();
	}

	protected void _setEnabled(boolean enabled) {
		spinner.setEnabled(enabled);
	}

	@Override
	public void focus() {
		spinner.requestFocusInWindow();
		
	}
}
