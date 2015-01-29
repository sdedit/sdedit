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

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyDescriptor;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.Configurator;
import net.sf.sdedit.ui.components.configuration.DataObject;

public class ColorConfigurator<C extends DataObject> extends
		Configurator<Color, C> {

	private JLabel label;

	private JLabel caption;

	public ColorConfigurator(Bean<C> bean, PropertyDescriptor property) {
		super(bean, property);
		setLayout(new GridLayout(1, 2));
		final String info = property.getWriteMethod().getAnnotation(
				Adjustable.class).info();
		caption = new JLabel(info);
		label = new JLabel();
		label.setOpaque(true);
		label.setBackground(getValue());
		label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (label.isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
					Color color = JColorChooser.showDialog(
							ColorConfigurator.this, info, getValue());
					if (color != null) {
						ActionEvent event = new ActionEvent(label, 1, ""
								+ color.getRGB());
						actionPerformed(event);
					}
				}
			}
		});
		add(caption);
		add(label);
	}

	@Override
	protected void _actionPerformed(ActionEvent evt) {
		int rgb = Integer.parseInt(evt.getActionCommand());
		applyValue(new Color(rgb));
	}

	@Override
	protected void _setEnabled(boolean enabled) {
		caption.setEnabled(enabled);
		label.setEnabled(enabled);

	}

	@Override
	protected void refresh(Color value) {
		label.setBackground(value);
	}

	@Override
	public void focus() {
		// TODO Auto-generated method stub
		
	}

}
