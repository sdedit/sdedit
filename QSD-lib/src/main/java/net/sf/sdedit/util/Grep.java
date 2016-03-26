// Copyright (c) 2006 - 2015, Markus Strauch.
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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for matching strings against regular expressions and accessing
 * substrings that match groups of the regular expression.
 * 
 * @author Markus Strauch
 * 
 */
public class Grep {

	private final Map<String, Pattern> patternCache;

	private final Map<Class<?>, Map<String, PropertyDescriptor>> descriptorCache;
	
	private final Unescape unescape;

	public Grep(Unescape unescape) {
		patternCache = new HashMap<String, Pattern>();
		descriptorCache = new HashMap<Class<?>, Map<String, PropertyDescriptor>>();
		this.unescape = unescape;
	}

	public static interface Unescape {
		public String unescape(String str);
	}
	
	public static Unescape NO_UNESCAPE = null;

	public static Unescape DEFAULT_UNESCAPE = new Unescape() {
		
		public final String unescape(String string) {
			if (string == null) {
				return null;
			}
			StringBuilder unescaped = new StringBuilder();
			int state = 0;

			for (int i = 0; i < string.length(); i++) {
				char c = string.charAt(i);
				switch (state) {
				case 0:
					if (c != '\\') {
						unescaped.append(c);
					} else {
						state = 1;
					}
					break;
				case 1:
					unescaped.append(c);
					state = 0;
					break;
				default:

				}
			}
			if (state == 1) {
				unescaped.append('\\');
			}
			return unescaped.toString();
		}
		
	};

	public static class Region {

		private final int start;

		private final int end;

		private final String text;

		protected Region(String text, int start, int end) {
			this.start = start;
			this.end = end;
			this.text = text;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}

		public String getText() {
			return text;
		}
	}
	
	private Pattern getPattern (String regexp) {
		Pattern pattern = patternCache.get(regexp);
		if (pattern == null) {
			pattern = Pattern.compile(regexp);
			patternCache.put(regexp, pattern);
		}
		return pattern;
	}

	/**
	 * Matches a string against a regular expression and returns an array of the
	 * substrings corresponding to the groups of the regular expression, or
	 * <tt>null</tt>, if the string does not match the regular expression.
	 * 
	 * @param regexp
	 *            a regular expression
	 * @param string
	 *            a string
	 * @return an array of the substrings corresponding to the groups of the
	 *         regular expression, or <tt>null</tt>, if the string does not
	 *         match the regular expression
	 */
	public String [] parse(final String regexp, final String string) {
		return parse(regexp, string, null);
	}
	
	public String[] parse(final Pattern pattern, final String string) {
		return parse(pattern, string, null);
	}
	
	public String[] parse(final String regexp, final String string, List<Region> regions) {
		return parse(getPattern(regexp), string, regions);
	}

	public String[] parse(final Pattern pattern, final String string,
			List<Region> regions) {
		final Matcher matcher = pattern.matcher(string);
		if (!matcher.matches()) {
			return null;
		}

		final String[] groups = new String[matcher.groupCount()];
		for (int i = 0; i < groups.length; i++) {
			if (unescape != null) {
				groups[i] = unescape.unescape(matcher.group(i + 1));
			} else {
				groups[i] = matcher.group(i + 1);
			}
			if (regions != null) {
				regions.add(new Region(matcher.group(i + 1), matcher
						.start(i + 1), matcher.end(i + 1)));
			}
		}
		return groups;
	}
	
	public boolean parseAndSetProperties(final Object bean,
			final String regexp, final String string,
			final Map<String, Region> propertyRegions,
			final String... properties)
	{
		return parseAndSetProperties(bean, getPattern(regexp), string, propertyRegions, properties);
	}

	/**
	 * Matches a string against a regular expression and uses the resulting
	 * groups (corresponding to portions of the regular expression between
	 * braces) for setting properties of a bean. The write method for the i-th
	 * property is called with an object of the write method's parameter type,
	 * that object is created by calling the type's string constructor with the
	 * string of the i-th group.
	 * 
	 * @param bean
	 *            a bean
	 * @param pattern
	 *            a regular expression
	 * @param string
	 *            a string to match the regular expression against
	 * @param properties
	 *            names of properties of the bean
	 * @return true if the string matched and if the properties could be set
	 * @throws IntrospectionException
	 *             if the bean could not be introspected
	 */
	public boolean parseAndSetProperties(final Object bean,
			final Pattern pattern, final String string,
			final Map<String, Region> propertyRegions,
			final String... properties) {

		ArrayList<Region> regions = null;
		if (propertyRegions != null) {
			regions = new ArrayList<Region>();
		}

		final String[] parsed = parse(pattern, string, regions);
		if (parsed == null) {
			return false;
		}
		if (properties.length != parsed.length) {
			throw new IllegalArgumentException("number of groups does not"
					+ " match number of properties");
		}
		Map<String, PropertyDescriptor> descriptors = descriptorCache.get(bean
				.getClass());
		if (descriptors == null) {
			BeanInfo beanInfo;
			try {
				beanInfo = Introspector.getBeanInfo(bean.getClass());
			} catch (IntrospectionException ie) {
				throw new IllegalStateException("Cannot introspect "
						+ bean.getClass().getName(), ie);
			}
			descriptors = new HashMap<String, PropertyDescriptor>();
			for (PropertyDescriptor descriptor : beanInfo
					.getPropertyDescriptors()) {
				if (descriptor.getWriteMethod() != null) {
					descriptors.put(descriptor.getName(), descriptor);
				}
			}
			descriptorCache.put(bean.getClass(), descriptors);
		}

		for (int i = 0; i < properties.length; i++) {
			final PropertyDescriptor pd = descriptors.get(properties[i]);
			if (pd == null) {
				throw new IllegalArgumentException("property " + properties[i]
						+ " does not exist");
			}
			if (propertyRegions != null) {
				propertyRegions.put(pd.getName(), regions.get(i));
			}

			final Object value = ObjectFactory.createFromString(
					pd.getPropertyType(), parsed[i]);
			try {
				pd.getWriteMethod().invoke(bean, value);
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException("cannot write property "
						+ properties[i]);
			}
		}
		return true;
	}
}
// {{core}}
