package net.sf.sdedit.eclipse;

import java.io.File;
import java.lang.reflect.Method;

public abstract class Eclipse {

	public static Eclipse getEclipse() {
		Eclipse eclipse = null;
		try {
			Class<?> eclipseClass = Class
					.forName("net.sf.sdedit.eclipse.EclipseImpl");
			Method method = eclipseClass.getMethod("getInstance");
			eclipse = (Eclipse) method.invoke(null);
		} catch (RuntimeException re) {
			throw re;
		} catch (ClassNotFoundException cnfe) {
			/* ignored */
		} catch (Throwable t) {
			throw new IllegalStateException(t);
		}

		return eclipse;
	}

	public abstract boolean goToSource(String className, int lineNumber);

	public abstract File[] getPluginClasspath();
	
	public abstract void synchronize ();

}
