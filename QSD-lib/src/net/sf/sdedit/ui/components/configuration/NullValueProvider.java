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
import java.io.File;
import java.util.Date;

import javax.swing.KeyStroke;

import net.sf.sdedit.ui.components.configuration.configurators.KeyStrokeConfigurator;
import net.sf.sdedit.util.ObjectFactory;


public class NullValueProvider {
	
	private NullValueProvider () {
		
	}
	
	public static Object getNullValue (Class<?> objectClass) {
		
		if (objectClass == String.class) {
			return "";
		}
		
		if (objectClass == String[].class) {
			return new String[0];
		}
		
		if (objectClass == Boolean.TYPE) {
			return Boolean.FALSE;
		}
		
		if (objectClass.isPrimitive() || Number.class.isAssignableFrom(objectClass)) {
			return ObjectFactory.createFromString(objectClass, "0");			
		}
		
		if (objectClass == Date.class) {
			return new Date(0);
		}
		
		if (objectClass == Color.class) {
			return Color.WHITE;
		}
		
		if (objectClass == File.class) {
			return new File (System.getProperty("user.home"), "untitled");
		}
		
		if (objectClass == File[].class) {
			return new File[0];
		}
		
		if (objectClass == Font.class) {
			return new Font("Dialog", Font.PLAIN, 12);
		}
		
		if (objectClass == KeyStroke.class) {
			return KeyStrokeConfigurator.NULL_KEYSTROKE;
		}
		
		return null;
		
	}

}
