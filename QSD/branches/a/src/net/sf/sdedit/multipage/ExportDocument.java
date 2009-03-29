package net.sf.sdedit.multipage;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

public class ExportDocument {
	
	private MultipagePaintDevice mpd;
	
	private Class<? extends Graphics2D> graphicsClass;
	
	private Graphics2D graphics;
	
	public ExportDocument (Class<? extends Graphics2D> graphicsClass,
			MultipagePaintDevice mpd, OutputStream stream, 
			String format,
			String orientation) {
		this.mpd = mpd;
		this.graphicsClass = graphicsClass;
		try {
			graphics = graphicsClass.getConstructor(OutputStream.class,
					Dimension.class).newInstance(stream, mpd.getPageSize());
		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable t) {
			throw new IllegalArgumentException ("Cannot create instance of " +
					graphicsClass.getSimpleName());
		}
		invoke ("setMultiPage", Boolean.TYPE, Boolean.TRUE);
        Properties properties = new Properties();
        properties.setProperty(getStringConstant("ORIENTATION"), orientation);
        properties.setProperty(getStringConstant("PAGE_SIZE"), format);
        invoke ("setProperties", Properties.class, properties);
	}
	
	private void invoke (String methodName) {
		invoke (methodName, null, null);
	}
	
	private String getStringConstant (String name) {
		try {
			Field field = graphicsClass.getField(name);
			return (String) field.get(null);
		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable t) {
			throw new IllegalArgumentException ("cannot resolve string constant " + name);
		}
	}
	
	private <T> void invoke (String methodName, Class<? extends T> argClass, T arg) {
		try {
			Class [] argTypes = argClass == null ? new Class [0] :
				new Class [] {argClass};
			Method method = graphicsClass.getMethod(methodName, argTypes);
			Object [] args = arg == null ? new Object [0] :
				new Object [] {arg};
			method.invoke(graphics, args);
		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IllegalArgumentException ("invocation of " + methodName + " failed");
		}
	}
	
	public void export () throws IOException {
		invoke ("startExport");
		for (MultipagePaintDevice.MultipagePanel panel : mpd.getPanels()) {
			invoke ("openPage", Component.class, panel);
			panel.paintComponent(graphics);
			invoke ("closePage");
		}
		invoke ("endExport");
	}
}
