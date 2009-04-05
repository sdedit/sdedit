package net.sf.sdedit.eclipse;

import java.io.File;
import java.lang.reflect.Method;

public abstract class Eclipse {
	
	public static Eclipse getEclipse () {
		Eclipse eclipse = null;
		try {
			Class<?> eclipseClass = Class.forName("net.sf.sdedit.eclipse.EclipseImpl");
			Method method = eclipseClass.getMethod("getInstance");
			eclipse = (Eclipse) method.invoke(null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return eclipse;
	}
	
	public abstract boolean goToSource (String className, int lineNumber);
	
	public abstract File [] getPluginClasspath ();
	
}
