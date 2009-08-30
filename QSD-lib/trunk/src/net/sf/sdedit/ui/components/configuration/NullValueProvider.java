package net.sf.sdedit.ui.components.configuration;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.Date;

import javax.swing.KeyStroke;

import net.sf.sdedit.ui.components.configuration.configurators.KeyStrokeConfigurator;


public class NullValueProvider {
	
	private NullValueProvider () {
		
	}
	
	public static Object getNullValue (Class<?> objectClass) {
		
		if (objectClass == String.class) {
			return "";
		}
		
		if (objectClass == Boolean.TYPE) {
			return Boolean.FALSE;
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
		
		if (objectClass == Integer.TYPE) {
			return 0;
		}
		
		throw new IllegalArgumentException ("Cannot provide null value for " + objectClass.getName());
		
	}

}
