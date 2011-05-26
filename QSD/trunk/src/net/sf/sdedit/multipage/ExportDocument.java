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
