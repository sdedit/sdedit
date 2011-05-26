package net.sf.sdedit.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class Proxy<T> implements InvocationHandler {

    private Class<T> iface;

    private T proxy;

    private Object impl;

    public Proxy(Class<T> iface, Object impl) {
        this.iface = iface;
        this.impl = impl;
    }

    public void setImpl(Object impl) {
        this.impl = impl;
    }

    public T getProxy() {
        if (proxy == null) {
            proxy = (T) java.lang.reflect.Proxy.newProxyInstance(impl
                    .getClass().getClassLoader(), new Class<?>[] { iface },
                    this);
        }
        return proxy;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        return Utilities.invoke(method.getName(), impl, args);
    }

}
