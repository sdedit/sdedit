package net.sf.sdedit.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class Proxy implements InvocationHandler {

    private final Object impl;

    private final Class<?> iface;

    public Proxy(Object impl, Class<?> iface) {
        this.impl = impl;
        this.iface = iface;
    }

    public Object instantiate() {
        return java.lang.reflect.Proxy.newProxyInstance(iface.getClassLoader(),
                new Class[] { iface }, this);
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        return Utilities.invoke(method.getName(), impl, args);
    }

}
