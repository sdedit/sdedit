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

package net.sf.sdedit.ui.components.configuration;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.Date;

import javax.swing.KeyStroke;

import net.sf.sdedit.ui.components.configuration.configurators.BooleanConfigurator;
import net.sf.sdedit.ui.components.configuration.configurators.ColorConfigurator;
import net.sf.sdedit.ui.components.configuration.configurators.DateConfigurator;
import net.sf.sdedit.ui.components.configuration.configurators.FileConfigurator;
import net.sf.sdedit.ui.components.configuration.configurators.FileSetConfigurator;
import net.sf.sdedit.ui.components.configuration.configurators.FontConfigurator;
import net.sf.sdedit.ui.components.configuration.configurators.FreeStringConfigurator;
import net.sf.sdedit.ui.components.configuration.configurators.KeyStrokeConfigurator;
import net.sf.sdedit.ui.components.configuration.configurators.NumberConfigurator;
import net.sf.sdedit.ui.components.configuration.configurators.SmallStringSelectionConfigurator;
import net.sf.sdedit.ui.components.configuration.configurators.SingleStringSelectionConfigurator;
import net.sf.sdedit.ui.components.configuration.configurators.StringSetConfigurator;

/**
 * A <tt>ConfiguratorFactory</tt> creates specialized
 * {@linkplain Configurator} objects, depending on the type of property to be
 * configured.
 * 
 * @author Markus Strauch
 * 
 * @param <B>
 *            the bean for whose properties this <tt>ConfiguratorFactory</tt>
 *            creates Configurators
 */
public class ConfiguratorFactory<C extends DataObject> {

	/**
	 * Creates a specialized configurator for a property.
	 * 
	 * @param bean
	 *            the bean to which the property belongs
	 * @param property
	 *            the descriptor of the property
	 * @param defaultConfiguration
	 *            a bean with values
	 * 
	 * @return a specialized configurator for the property
	 */
	public Configurator<?, C> createConfigurator(Bean<C> bean,
			PropertyDescriptor property) {
		if (property.getPropertyType().equals(String.class)) {
			Adjustable adj = property.getWriteMethod().getAnnotation(Adjustable.class);
			String[] choices = adj.choices();
			if (choices.length > 0) {
				if (!adj.forceComboBox() && choices.length <= 3) {
					return new SmallStringSelectionConfigurator<C>(bean,
							property);
				} 
				return new SingleStringSelectionConfigurator<C>(bean, property);
			}
			if ( !property.getWriteMethod().getAnnotation(
					Adjustable.class).stringSelectionProvided()) {
				return new FreeStringConfigurator<C>(bean, property);
			}
			return new SingleStringSelectionConfigurator<C>(bean, property);
		}
		if (property.getPropertyType().equals(String[].class)) {
			return new StringSetConfigurator<C>(bean, property);
		}
		if (property.getPropertyType().equals(Integer.TYPE)) {
			return new NumberConfigurator<C>(bean, property);
		}
		if (property.getPropertyType().equals(Boolean.TYPE)) {
			return new BooleanConfigurator<C>(bean, property);
		}
		if (property.getPropertyType().equals(Font.class)) {
			return new FontConfigurator<C>(bean, property);
		}
		if (property.getPropertyType().equals(File.class)) {
			return new FileConfigurator<C>(bean, property);
		}
		if (property.getPropertyType().equals(File[].class)) {
			return new FileSetConfigurator<C>(bean,property);
		}
		if (property.getPropertyType().equals(KeyStroke.class)) {
			return new KeyStrokeConfigurator<C>(bean,property);
		}
		if (property.getPropertyType().equals(Color.class)) {
			return new ColorConfigurator<C>(bean,property);
		}
		if (property.getPropertyType().equals(Date.class)) {
			return new DateConfigurator<C>(bean,property);
		}
		
		throw new IllegalArgumentException("cannot create configurator for "
				+ "property type " + property.getPropertyType().getSimpleName());
	}

}
