package net.sf.sdedit.cli;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.sdedit.util.PWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

public class CommandLineBeanFactory<T extends IOptions> implements InvocationHandler {

    private final Class<T> iface;

    private CommandLine commandLine;

    private Map<String, OptionObject> options;
    
    private Map<String, String> methodNames;

    private Options opt;

    public CommandLineBeanFactory(Class<T> iface) {
        this.iface = iface;
        options = new HashMap<String, OptionObject>();
        opt = new Options();
        methodNames = new HashMap<String,String>();
        try {
            initialize();
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException("instrospection of class " + iface.getName() + " failed", e);
        }
        
    }
    
    private boolean check(T obj) {
        boolean success = true;
        for (OptionObject opt : options.values()) {
            Method method = opt.method();
            try {
                method.invoke(obj);
            } catch (IllegalAccessException e) {
                
            } catch (InvocationTargetException e) {
                Throwable t = e.getCause();
                String name = opt.getName();
                System.out.println("Illegal argument for option \"" + name + "\": " + t.getMessage());
                success = false;
            }
        }
        if (!success) {
            System.out.println();
        }
        return success;
    }

    public T parse(String[] args, String parserType) {
        Parser parser;
        if ("gnu".equalsIgnoreCase(parserType)) {
            parser = new GnuParser();
        } else {
            parser = new PosixParser();
        }
        try {
            commandLine = parser.parse(opt, args);
        } catch (ParseException pe) {
            System.out.println(pe.getMessage());
            return null;
        }
        T dataObject = iface.cast(Proxy.newProxyInstance(
                iface.getClassLoader(), new Class[] { iface }, this));
        if (!check(dataObject)) {
            return null;
        }
        return dataObject;
    }
    
    public T parse(String[] args) {
        return parse(args, "posix");
    }

    public void printHelp(String cmd) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(cmd, opt);
    }

    private void initialize() throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(iface);
        PropertyDescriptor[] propertyDescriptors = beanInfo
                .getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor property = propertyDescriptors[i];
            Method method = property.getReadMethod();
            if (method != null && method.isAnnotationPresent(Option.class)) {
                OptionObject optionObject = new OptionObject(property);
                options.put(optionObject.getName(), optionObject);
                methodNames.put(method.getName(), optionObject.getName());
                opt.addOption(optionObject.getOption());
            }
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        if ("toString".equals(method.getName()) && args == null) {
            PWriter writer = PWriter.create();
            for (Method m : iface.getMethods()) {
                if (m.isAnnotationPresent(Option.class)) {
                    Object val;
                    try {
                        val = invoke(proxy, m, args);
                    } catch (Throwable t) {
                        val = t.getMessage();
                    }
                    if (val != null && val.getClass().isArray()) {
                        List<Object> list = new ArrayList<Object>();
                        int n = Array.getLength(val);
                        for (int i = 0; i < n; i++) {
                            Object obj = Array.get(val, i);
                            list.add(obj);
                        }
                        val = list;
                    }
                    writer.println(m.getName() + "=" + val);
                }
            }
            return writer.toString();
        }
        if ("getArgs".equals(method.getName()) && args == null) {
            return commandLine.getArgs();
        }
        String name = methodNames.get(method.getName());
        OptionObject obj = options.get(name);
        Object value = obj.getValue(commandLine);
        if (value == null) {
            String inh = obj.inherit();
            if (inh != null) {
                value = invoke(proxy, options.get(inh).method(), args);
            }
        }
        return value;
    }

}

