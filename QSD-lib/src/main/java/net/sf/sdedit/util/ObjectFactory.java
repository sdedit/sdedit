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

package net.sf.sdedit.util;

import java.awt.Font;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for creating objects from strings.
 * 
 * @author Markus Strauch
 * 
 */
public final class ObjectFactory {

	private static final Map<Class<?>, Constructor<?>> stringConstructorMap;

	static {
		stringConstructorMap = new HashMap<Class<?>, Constructor<?>>();
	}

	private ObjectFactory() {
		/* empty */
	}

	/**
	 * Creates an instance of some class from a string by calling the string
	 * constructor of the class with the string as a parameter.
	 * 
	 * @param cls
	 *            the class of which an instance is to be created, if it is a
	 *            primitive type, an instance of the corresponding class will be
	 *            created
	 * @param string
	 *            the string from which the instance is to be created or
	 *            <tt>null</tt>, then an instance resulting from an invokation
	 *            of the no-argument constructor will be returned
	 * @return an instance of <tt>cls</tt>, created from <tt>string</tt>
	 */
	public static Object createFromString(Class<?> cls, final String string) {
		if (cls == String.class) {
			return string == null ? "" : string;
		}
		if (cls == Integer.TYPE && string.length() == 1) {
			return string.charAt(0) - '0';
		}
		if (cls == Font.class) {
			return Font.decode(string);
		}
		final Class<?> nonPrimitive = Utilities.getWrapperClass(cls);
		if (nonPrimitive != null) {
			cls = nonPrimitive;
		}
		try {
			if (string == null) {
				return cls.newInstance();
			}
			Constructor<?> constructor = stringConstructorMap.get(cls);
			if (constructor == null) {
				try {
					constructor = cls
							.getConstructor(new Class[] { String.class });
				} catch (NoSuchMethodException e) {
					constructor = cls.getConstructor(new Class [] { Integer.TYPE });
				}
				stringConstructorMap.put(cls, constructor);
			}
			if (constructor.getParameterTypes()[0] == Integer.TYPE) {
				return constructor.newInstance(Integer.parseInt(string));
			}
			return constructor.newInstance(string);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(cls.getName() + " has no"
					+ " constructor with a single string or integer as a parameter");
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(cls.getName() + " has no"
					+ " constructor with an empty parameter list");
		} catch (Throwable e) {
			throw new IllegalArgumentException(cls.getName() + " has a"
					+ " constructor with a single string or integer as a parameter, but"
					+ " it cannot be invoked with the argument \"" + string
					+ "\", which led to an exception/error of type "
					+ e.getClass().getSimpleName() + " with the message: "
					+ e.getMessage());
		}
	}
}
// {{core}}
