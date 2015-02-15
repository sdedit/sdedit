package net.sf.sdedit.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Proxy implements InvocationHandler {

	private final Object impl;

	private final Class<?> iface;
	
	public Proxy (Object impl, Class<?> iface) {
		this.impl = impl;
		this.iface = iface;
	}

	public Object instantiate() {
		return java.lang.reflect.Proxy.newProxyInstance(iface.getClassLoader(),
				new Class[] { iface }, this);
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		for (Method m : impl.getClass().getMethods()){
			if (method.getName().equals(m.getName()) && m.getParameterTypes().length == args.length) {
				try {
					return m.invoke(impl, args);
				} catch (InvocationTargetException e) {
					if (e.getCause() instanceof RuntimeException) {
						throw (RuntimeException) e.getCause();
					}
				} catch (RuntimeException re) {
					throw re;
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
				
			}
		}
		return null;
	}
	
	
}
