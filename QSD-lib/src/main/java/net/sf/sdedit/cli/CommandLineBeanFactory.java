//Copyright (c) 2006 - 2016, Markus Strauch.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

import net.sf.sdedit.util.PWriter;

public class CommandLineBeanFactory<T extends IOptions> implements InvocationHandler {

	private final Class<T> iface;

	private CommandLine commandLine;

	private Map<String, OptionObject> options;

	private Map<String, String> methodNames;

	private Map<String, OptionGroup> optionGroups;

	private Options opt;

	public CommandLineBeanFactory(Class<T> iface) {
		this.iface = iface;
		options = new HashMap<String, OptionObject>();
		opt = new Options();
		methodNames = new HashMap<String, String>();
		optionGroups = new HashMap<String, OptionGroup>();
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
		T dataObject = iface.cast(Proxy.newProxyInstance(iface.getClassLoader(), new Class[] { iface }, this));
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
		Map<String,PropertyDescriptor> propertyDescriptors = new HashMap<>();
		LinkedList<Class<?>> types = new LinkedList<>();
		types.add(iface);
		while (!types.isEmpty()) {
			Class<?> type = types.removeLast();
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			for (PropertyDescriptor prop : beanInfo.getPropertyDescriptors()) {
				propertyDescriptors.put(prop.getName(), prop);
			}
			for (Class<?> iface : type.getInterfaces()) {
				if (iface != IOptions.class && IOptions.class.isAssignableFrom(iface)) {
					types.addFirst(iface);
				}
			}
		}
		

		for (PropertyDescriptor property : propertyDescriptors.values()) {
			Method method = property.getReadMethod();
			if (method != null && method.isAnnotationPresent(Option.class)) {
				OptionObject optionObject = new OptionObject(property);
				options.put(optionObject.getName(), optionObject);
				methodNames.put(method.getName(), optionObject.getName());
				org.apache.commons.cli.Option option = optionObject.getOption();
				option.setRequired(optionObject.isRequired());
				opt.addOption(option);
				if (optionObject.getGroup() != null) {
					OptionGroup group = optionGroups.get(optionObject.getGroup());
					if (group == null) {
						group = new OptionGroup();
						optionGroups.put(optionObject.getGroup(), group);
					}
					group.addOption(option);
					if (optionObject.isRequired()) {
						group.setRequired(true);
					}
				}
			}
		}
		for (OptionGroup group : optionGroups.values()) {
			opt.addOptionGroup(group);
		}

	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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
