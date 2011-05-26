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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is intended for write methods of {@linkplain Bean}
 * subclasses. If a write method is annotated, its corresponding property is
 * declared to be &quot;adjustable&quot;, i. e., it will be loaded and stored
 * when a {@linkplain Bean} object is loaded/stored, and it can be configured by
 * user interaction, when the {@linkplain Bean} object is the model of a
 * {@linkplain ConfigurationUI}.
 * 
 * @author Markus Strauch
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Adjustable {

	/**
	 * The minimum value of a number property.
	 * 
	 * @return the minimum value of a number property
	 */
	int min() default Integer.MIN_VALUE;

	/**
	 * The value that is added to/taken from a number property when it is
	 * increased/decreased.
	 * 
	 * @return the value that is added to/taken from a number property when it
	 *         is increased/decreased
	 */
	int step() default 1;

	/**
	 * The maximum value of a number property.
	 * 
	 * @return the maximum value of a number property
	 */
	int max() default Integer.MAX_VALUE;

	/**
	 * The default value of a number property.
	 * 
	 * @return the default value of a number property
	 */
	int dflt() default 0;

	/**
	 * A string that informs about the adjustable property, used by
	 * {@linkplain ConfigurationUI} in order to describe the property to a user.
	 * 
	 * @return a string that informs about the adjustable property
	 */
	String info();

	/**
	 * The name of the category to which a property belongs.
	 * 
	 * @return the name of the category to which a property belongs
	 */
	String category();

	/**
	 * Flag denoting if the property is editable
	 * 
	 * @return flag denoting if the property is editable
	 */
	boolean editable() default true;

	/**
	 * An array of possible values for a String property.
	 * 
	 * @return an array of possible values for a String property
	 */
	String[] choices() default {};

	/**
	 * Flag denoting if the potential values of the (String) property must be
	 * from a list provided by a {@linkplain StringSelectionProvider}.
	 * 
	 * @return flag denoting if the potential values of the (String) property
	 *         must be from a list provided by a
	 *         {@linkplain StringSelectionProvider}.
	 */
	boolean stringSelectionProvided() default false;

	/**
	 * A comma-separated list of name-value-pairs, separated by '=' or '!=',
	 * representing that this adjustable property is enabled if and only if all
	 * of the equations are satisfied. The name in an (in-)equation stands for a
	 * property, and the value is a boolean (true or false) denoting if the
	 * property's configurator is enabled.
	 * 
	 * @return a comma-separated string consisting of assignments to properties
	 *         that must hold, otherwise this property is not editable
	 * 
	 */
	String depends() default "";

	/**
	 * Only relevant for <tt>File</tt> properties, denotes whether the file is
	 * to be opened or to be written to.
	 * 
	 * @return a flag denoting if the file to be opened or to be written to
	 */
	boolean openFile() default true;
	
	String[] filetypes() default {};

	/**
	 * A string used for sorting the properties. If it is empty, the name of the
	 * property is used as the key.
	 * 
	 * @return a string used for sorting the properties
	 */
	String key() default "";

	boolean forceComboBox() default false;

	String tooltip() default "";

	int gap() default 1;
	
	boolean primary() default false;
	
	String datePattern() default "";

}
//{{core}}
//{{core}}
