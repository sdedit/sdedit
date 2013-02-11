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

import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class Utilities {

	/*
	 * Maps the Class representations of the primitive classes onto their
	 * wrapper classes.
	 */
	static private Map<Class<?>, Class<?>> primitiveClasses;

	private static CL cl;

	static {
		primitiveClasses = new HashMap<Class<?>, Class<?>>();
		primitiveClasses.put(Integer.TYPE, Integer.class);
		primitiveClasses.put(Boolean.TYPE, Boolean.class);
		primitiveClasses.put(Character.TYPE, Character.class);
		primitiveClasses.put(Byte.TYPE, Byte.class);
		primitiveClasses.put(Short.TYPE, Short.class);
		primitiveClasses.put(Integer.TYPE, Integer.class);
		primitiveClasses.put(Long.TYPE, Long.class);
		primitiveClasses.put(Float.TYPE, Float.class);
		primitiveClasses.put(Double.TYPE, Double.class);
	}

	private static final String CR = "\r";

	private static final String LF = "\n";

	private static final String CRLF = CR + LF;

	private Utilities() {

	}

	private static CL cl() {
		if (cl == null) {
			cl = new CL();
		}
		return cl;
	}

	public static PrintWriter createPrintWriter() {
		return PWriter.create();
	}

	public static PrintWriter createPrintWriter(File file, String encoding)
			throws IOException {
		OutputStream outputStream = new FileOutputStream(file);
		try {
			OutputStreamWriter osw = new OutputStreamWriter(outputStream,
					encoding);
			PrintWriter pw = new PrintWriter(osw);
			return pw;
		} catch (IOException e) {
			outputStream.close();
			throw e;
		}
	}

	protected static class CL extends ClassLoader {

		private HashMap<String, byte[]> classes;

		CL() {
			super(CL.class.getClassLoader());
			classes = new HashMap<String, byte[]>();
		}

		public void addClass(String name, byte[] code) {
			classes.put(name, code);
		}

		protected Class<?> findClass(String name) throws ClassNotFoundException {
			try {
				byte[] code = classes.remove(name);
				Class<?> clazz = defineClass(name, code, 0, code.length);
				return clazz;
			} catch (RuntimeException re) {
				throw re;
			} catch (Throwable t) {
				t.printStackTrace();
				throw new ClassNotFoundException(t.getMessage());
			}
		}
	}

	public static String toString(PrintWriter pw) {
		pw.flush();
		return pw.toString();
	}

	public static void erase(File file, boolean recursive) {
		if (recursive) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File _file : files) {
					erase(_file, true);
				}
			}
		}
		file.delete();
	}

	private static int cast(byte b) {
		if (b >= 0) {
			return b;
		}
		return 256 + b;
	}

	public static byte[] toByteArray(String hexString) {
		byte[] bytes = new byte[hexString.length() / 2];
		for (int i = 0; i < hexString.length(); i += 2) {
			String hex = hexString.substring(i, i + 2);
			bytes[i / 2] = (byte) Integer.parseInt(hex, 16);
		}
		return bytes;
	}

	public static String toHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String h = Integer.toHexString(cast(bytes[i]));
			if (h.length() == 1) {
				h = "0" + h;
			}
			sb.append(h);
		}
		return sb.toString();
	}

	public static String toString(Date date) {
		return toString(date, null);
	}

	public static Class<?> loadClass(String name, byte[] code)
			throws ClassNotFoundException {
		cl().addClass(name, code);
		return cl().loadClass(name);
	}

	public static byte[] zip(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(bos);
		ZipEntry ze = new ZipEntry("X");
		zos.putNextEntry(ze);
		pipe(is, zos);
		zos.closeEntry();
		zos.flush();
		zos.close();
		return bos.toByteArray();
	}

	public static byte[] unzip(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ZipInputStream zis = new ZipInputStream(bis);
			zis.getNextEntry();
			return toByteArray(zis, -1);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static Date toDate(String string, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setLenient(true);
		Date d;
		try {
			d = dateFormat.parse(string);
		} catch (ParseException pe) {
			throw new IllegalArgumentException(pe);
		}
		return d;
	}

	public static String toString(Date date, String format) {
		DateFormat dateFormat;
		if (format != null) {
			dateFormat = new SimpleDateFormat(format);
		} else {
			dateFormat = DateFormat.getInstance();
		}
		return dateFormat.format(date);
	}

	private static <T> T getFirst(Collection<T> set, boolean remove) {
		Iterator<T> iterator = set.iterator();
		if (!iterator.hasNext()) {
			throw new IllegalStateException("The set is empty");
		}
		T first = iterator.next();
		if (remove) {
			iterator.remove();
		}
		return first;
	}

	public static <T> T pollFirst(Collection<T> set) {
		return getFirst(set, true);
	}

	public static <T> T peekFirst(Collection<T> set) {
		return getFirst(set, false);
	}

	public static <T, S extends T, U extends T> T nvl(S obj, U nullObject) {
		if (obj != null) {
			return obj;
		}
		return nullObject;
	}

	public static String lpad(String str, char pad, int len) {
		while (str.length() < len) {
			str = pad + "" + str;
		}
		return str;
	}

	public static String hourDiff(Date firstDate, Date lastDate) {
		Calendar cal1 = new GregorianCalendar(TimeZone.getDefault());
		Calendar cal2 = new GregorianCalendar(TimeZone.getDefault());
		cal1.setTime(firstDate);
		cal2.setTime(lastDate);
		int d1 = cal1.get(Calendar.DAY_OF_YEAR);
		int d2 = cal2.get(Calendar.DAY_OF_YEAR);
		int h1 = cal1.get(Calendar.HOUR_OF_DAY);
		int h2 = cal2.get(Calendar.HOUR_OF_DAY);
		int m1 = cal1.get(Calendar.MINUTE);
		int m2 = cal2.get(Calendar.MINUTE);
		int diff = d2 * 24 * 60 + h2 * 60 + m2 - d1 * 24 * 60 - h1 * 60 - m1;
		String min = String.valueOf(diff % 60);
		return (diff / 60) + ":" + lpad(min, '0', 2);

	}

	public static int[] makeInts(int from, int to) {
		if (to < from) {
			throw new IllegalArgumentException("from=" + from + ", to=" + to);
		}
		int[] ints = new int[to - from + 1];
		for (int i = from; i <= to; i++) {
			ints[i] = from + i;
		}
		return ints;
	}

	public static Object nvl2(Object obj, Object obj1, Object obj2) {
		return obj == null ? obj2 : obj1;
	}

	public static <T> LinkedList<T> singletonList(T element) {
		LinkedList<T> list = new LinkedList<T>();
		list.add(element);
		return list;
	}

	public static String pad(char c, int length) {
		char[] characters = new char[length];
		Arrays.fill(characters, c);
		return new String(characters);
	}

	public static String getSimpleName(File file) {
		String name = file.getName();
		int i = name.lastIndexOf('.');
		if (i > 0) {
			name = name.substring(0, i);
		}
		return name;
	}

	public static <T> T peek(Collection<T> set) {
		Iterator<T> iterator = set.iterator();
		if (!iterator.hasNext()) {
			throw new IllegalStateException("The set is empty");
		}
		T first = iterator.next();
		return first;
	}

	public static String substring(String string, int pos, int length) {
		if (string.length() - pos <= length) {
			return string.substring(pos);
		}
		return string.substring(pos, pos + length);
	}

	public static String join(String token, Object[] objects) {
		if (objects != null) {
			String[] strings = new String[objects.length];
			for (int i = 0; i < objects.length; i++) {
				strings[i] = objects[i].toString();
			}
			return join(token, strings);
		}
		return "";
	}

	public static String join(String token, Collection<String> strings) {
		return join(token, strings.toArray(new String[strings.size()]));
	}

	public static String join(String token, String[] strings) {
		if (strings == null || strings.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();

		for (int x = 0; x < strings.length - 1; x++) {
			sb.append(strings[x]);
			sb.append(token);
		}
		sb.append(strings[strings.length - 1]);

		return (sb.toString());
	}

	public static String findMainClass(File jarFile) throws IOException {
		JarFile jf = new JarFile(jarFile);
		try {
			Manifest manifest = jf.getManifest();
			if (manifest == null) {
				return null;
			}
			String mainClass = manifest.getMainAttributes().getValue(
					"Main-Class");
			return mainClass;
		} finally {
			jf.close();
		}
	}

	public static String findUniqueName(String name, List<String> names) {
		Set<String> nameSet = new HashSet<String>(names);
		if (!nameSet.contains(name)) {
			return name;
		}
		int i = 1;
		while (true) {
			String trial = name + "-" + i;
			if (!names.contains(trial)) {
				return trial;
			}
			i++;
		}
	}

	public static <T> boolean contains(T[] array, T elem) {
		return indexOf(array, elem) >= 0;
	}

	public static <T> int indexOf(T[] array, T elem) {
		int i = 0;
		for (T t : array) {
			if (t.equals(elem)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public static URL getResource(String name) {
		URL res = Utilities.class.getResource("/resource/" + name);
		return res;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] reverse(T[] array) {
		Class<?> elemClass = array.getClass().getComponentType();
		int l = array.length;
		T[] reverse = (T[]) Array.newInstance(elemClass, l);
		for (int i = 0; i < array.length; i++) {
			reverse[l - i - 1] = array[i];
		}
		return reverse;
	}

	public static File toFile(URL url) {
		File file;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
		return file;
	}

	public static <T, C extends Collection<T>> Collection<T> toCollection(
			Class<C> cls, T[] array) {
		try {
			Collection<T> collection = cls.newInstance();
			for (T t : array) {
				collection.add(t);
			}
			return collection;
		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable t) {
			throw new IllegalArgumentException(t);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T, S extends T, U extends T> T[] joinArrays(S[] array0,
			U[] array1, Class<T> elementClass) {
		int l0 = Array.getLength(array0);
		int l1 = Array.getLength(array1);
		T[] array = (T[]) Array.newInstance(elementClass, l0 + l1);
		System.arraycopy(array0, 0, array, 0, l0);
		System.arraycopy(array1, 0, array, l0, l1);
		return array;
	}

	public static <T, C extends Collection<T>> Collection<T> flatten(
			Class<C> cls, Collection<? extends Collection<T>> collections) {
		try {
			Collection<T> flatCollection = cls.newInstance();
			for (Collection<T> collection : collections) {
				flatCollection.addAll(collection);
			}
			return flatCollection;
		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable t) {
			throw new IllegalArgumentException(t);
		}
	}

	/**
	 * Converts (a portion) of the given stream to a byte array.
	 * 
	 * @param inputStream
	 *            the stream from which to read bytes
	 * @param size
	 *            the number of bytes to be read, or -1 if the whole of the
	 *            (remaining) bytes should be read
	 * @return a byte array of length <tt>size</tt> containing bytes read from
	 *         the given stream, starting at the current position
	 * @throws IOException
	 */
	public static byte[] toByteArray(InputStream inputStream, final int size)
			throws IOException {
		int totalSize = 0;
		List<byte[]> bufferList = null;
		byte[] result = null;
		if (size == -1) {
			bufferList = new LinkedList<byte[]>();
		} else {
			result = new byte[size];
		}
		int avail = inputStream.available();
		while (avail > 0) {
			byte[] buffer = new byte[1024];
			int len;
			if (size == -1) {
				len = 1024;
			} else {
				len = Math.min(1024, size - totalSize);
			}
			int r = inputStream.read(buffer, 0, len);
			if (r == -1) {
				break;
			}
			if (size == -1) {
				if (r < 1024) {
					byte[] smallerBuffer = new byte[r];
					System.arraycopy(buffer, 0, smallerBuffer, 0, r);
					bufferList.add(smallerBuffer);
				} else {
					bufferList.add(buffer);
				}
			} else {
				System.arraycopy(buffer, 0, result, totalSize, r);
			}
			totalSize += r;
			if (size != -1 && totalSize == size) {
				break;
			}
		}
		if (size == -1) {
			result = new byte[totalSize];
			int offset = 0;
			for (byte[] buffer : bufferList) {
				System.arraycopy(buffer, 0, result, offset, buffer.length);
				offset += buffer.length;
			}
		}
		return result;
	}

	public static byte[] load(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			in = new BufferedInputStream(in);
			return toByteArray(in, -1);
		} finally {
			in.close();
		}
	}

	/**
	 * Saves a byte array to a file.
	 * 
	 * @param file
	 *            the file
	 * @param bytes
	 *            the byte array
	 * @throws IOException
	 */
	public static void save(File file, byte[] bytes) throws IOException {
		OutputStream os = new FileOutputStream(file);
		try {
			os = new BufferedOutputStream(os);
			for (int i = 0; i < bytes.length; i += 2048) {
				int len = Math.min(bytes.length - i, 2048);
				os.write(bytes, i, len);
			}
		} finally {
			os.close();
		}
	}

	/**
	 * Returns a string containing the (simple) names of the objects in the
	 * array, separated by ';', enclosed by square brackets.
	 * 
	 * @param objects
	 *            the object array
	 * @param simple
	 *            flag denoting if simple class names should be used
	 * @return a string containing the names of the objects' classes
	 */
	public static String classesString(Object[] objects, boolean simple) {
		String[] strings = new String[objects.length];
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] == null) {
				strings[i] = "null";
			} else if (simple) {
				strings[i] = objects[i].getClass().getSimpleName();
			} else {
				strings[i] = objects[i].getClass().getName();
			}
		}
		return "[" + join(",", strings) + "]";
	}

	/**
	 * The same as {@linkplain #classesString(Object[], boolean)} for
	 * collections.
	 * 
	 * @param objects
	 * @param simple
	 * @return
	 */
	public static String classesString(Collection<?> objects, boolean simple) {
		return classesString(objects.toArray(), simple);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] castArray(Object[] array, Class<T> componentType) {
		T[] result = (T[]) Array.newInstance(componentType, array.length);
		for (int i = 0; i < array.length; i++) {
			result[i] = componentType.cast(array[i]);
		}
		return result;
	}

	public static <T> Iterable<T> castIterable(final Object iterable,
			final Class<T> itemClass) {
		if (iterable == null) {
			return null;
		}
		if (iterable.getClass().isArray()) {
			return new Iterable<T>() {

				public Iterator<T> iterator() {
					return new Iterator<T>() {

						int i = 0;

						public boolean hasNext() {
							return i < Array.getLength(iterable);
						}

						public T next() {
							return itemClass.cast(Array.get(iterable, i++));
						}

						public void remove() {
							throw new UnsupportedOperationException();

						}

					};
				}

			};
		} else if (Iterable.class.isAssignableFrom(iterable.getClass())) {
			return new Iterable<T>() {

				public Iterator<T> iterator() {
					return new Iterator<T>() {

						Iterator<?> iter = ((Iterable<?>) iterable).iterator();

						public boolean hasNext() {
							return iter.hasNext();
						}

						public T next() {
							return itemClass.cast(iter.next());
						}

						public void remove() {
							iter.remove();
						}

					};
				}

			};
		}
		throw new IllegalArgumentException(iterable.getClass().getName()
				+ " is not iterable");

	}

	public static <T> Iterable<T> iteratorToIterable(
			final Iterator<?> iterator, final Class<T> elemClass) {
		final Iterator<T> iter = new Iterator<T>() {

			public boolean hasNext() {
				return iterator.hasNext();
			}

			public T next() {
				return elemClass.cast(iterator.next());
			}

			public void remove() {
				iterator.remove();

			}

		};

		return new Iterable<T>() {

			public Iterator<T> iterator() {
				return iter;
			}

		};
	}

	public static <T> Iterable<T> wrap(final Iterator<?> iterator,
			final Class<T> elemClass) {
		return new Iterable<T>() {

			public Iterator<T> iterator() {
				return new Iterator<T>() {

					public boolean hasNext() {
						return iterator.hasNext();
					}

					public T next() {
						return elemClass.cast(iterator.next());
					}

					public void remove() {
						iterator.remove();
					}

				};
			}

		};
	}

	public static String toString(URL url, Charset charset) throws IOException {
		InputStream stream = url.openStream();
		try {
			PWriter pw = PWriter.create();
			for (String line : readLines(stream, charset)) {
				pw.println(line);
			}
			pw.flush();
			return pw.toString();
		} finally {
			stream.close();
		}
	}

	/**
	 * Reads bytes from the <tt>from</tt> input stream and writes them to the
	 * <tt>to</tt> output stream.
	 * 
	 * @param from
	 * @param to
	 * @throws IOException
	 */
	public static void pipe(InputStream from, OutputStream to)
			throws IOException {
		byte[] buffer = new byte[1024];
		BufferedInputStream bis = new BufferedInputStream(from);
		BufferedOutputStream bos = new BufferedOutputStream(to);
		int avail;
		while ((avail = from.available()) > 0) {
			for (int off = 0; off < avail; off += 1024) {
				int length = Math.min(avail - off, 1024);
				bis.read(buffer, 0, length);
				bos.write(buffer, 0, length);
			}
		}
		bos.flush();
	}

	public static Iterable<String> readLines(String command,
			Ref<InputStream> streamRef, Charset charset) throws IOException {
		Process proc = Runtime.getRuntime().exec(command);
		InputStream stream = proc.getInputStream();
		if (streamRef != null) {
			streamRef.t = stream;
		}
		return readLines(stream, charset);
	}

	public static Iterable<String> readLines(String command, Charset charset)
			throws IOException {
		return readLines(command, null, charset);
	}

	public static Iterable<String> readLines(File file,
			Ref<InputStream> streamRef, Charset charset) throws IOException {
		InputStream stream = new FileInputStream(file);
		if (streamRef != null) {
			streamRef.t = stream;
		}
		return readLines(stream, charset);
	}

	public static Iterable<String> readLines(File file, Charset charset)
			throws IOException {
		return readLines(file, null, charset);
	}

	public static Iterable<String> readLines(final InputStream stream,
			Charset charset) throws IOException {

		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				stream, charset));
		return new Iterable<String>() {

			public Iterator<String> iterator() {
				return new Iterator<String>() {

					private String currentLine;

					public boolean hasNext() {
						try {
							currentLine = reader.readLine();
							if (currentLine == null) {
								stream.close();
							}
						} catch (IOException e) {
							try {
								stream.close();
							} catch (IOException ignored) {
								/* empty */
							}
							throw new IllegalStateException("Cannot read file",
									e);
						}
						return currentLine != null;
					}

					public String next() {
						return currentLine;
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	// /**
	// * Calls a method that fits the given parameters inside a new thread
	// *
	// * @param object
	// * the object on which the method is called
	// * @param methodName
	// * name of the method
	// * @param args
	// * array of parameter Objects
	// * @param listener
	// * listener object that is notified when the method returns
	// * @param lock
	// * lock to avoid a call while another one has not finished
	// * @throws IllegalArgumentException
	// * if no fitting method is found
	// */
	// public static void invoke (Object object, String methodName,
	// Object [] args, MethodReturnedListener listener, Object lock)
	// {
	// Method m = resolveMethod (object.getClass (), methodName, args);
	// if (m == null)
	// {
	// throw new IllegalArgumentException ("there is no method"
	// + " named " + methodName + " taking arguments "
	// + Arrays.asList (args) + " declared in "
	// + object.getClass ());
	// }
	// MethodInvoker invoker = new MethodInvoker (object, m, args, listener,
	// lock);
	// Thread thread = new Thread (invoker);
	// threadSet.add (thread);
	// thread.start ();
	// }

	public static Class<?> getWrapperClass(Class<?> primitiveClass) {
		return primitiveClasses.get(primitiveClass);
	}

	public static boolean isPrimitiveClass(Class<?> cls) {
		return getWrapperClass(cls) != null;
	}

	public static String getDuration(long milliseconds, String format) {
		long ms = milliseconds - 3600000;
		return toString(new Date(ms), format);
	}

	public static <S, T> Pair<S, T> pair(S arg1, T arg2) {
		return new Pair<S, T>(arg1, arg2);
	}

	public static Object invoke(String methodName, Object object, Object[] args)
			throws Throwable {
		Method method;
		if (object instanceof Class<?>) {
			method = resolveMethod((Class<?>) object, methodName, args);
		} else {
			method = resolveMethod(object.getClass(), methodName, args);
		}
		method.setAccessible(true);
		Object result;
		try {
			result = method.invoke(object, args);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("cannot access method "
					+ methodName + " of " + object.getClass().getName());
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
		return result;

	}

	@SuppressWarnings("unchecked")
	public static <T> T clone(T t) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(t);
			out.flush();

			ByteArrayInputStream in = new ByteArrayInputStream(
					out.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(in);
			T copy = (T) ois.readObject();
			in.close();
			out.close();
			return copy;
		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable th) {
			throw new IllegalStateException(th);
		}
	}

	/**
	 * Convenience method for finding a method that fits a name and the types of
	 * the objects inside the parameter array.
	 * 
	 * @param clazz
	 *            a class that declares the method that is looked for
	 * @param name
	 *            the name of the method
	 * @param args
	 *            array of arguments with which the method should be called
	 * @return a method that has the given names and can be called using the
	 *         arguments given, <code>null</code> if no such method exists
	 */
	public static Method resolveMethod(Class<?> clazz, String name,
			Object[] args) {
		Method[] methods = // clazz.getMethods();
		Utilities.joinArrays(clazz.getMethods(), clazz.getDeclaredMethods(),
				Method.class);
		int i;
		for (i = 0; i < methods.length; i++) {
			Method m = methods[i];
			Class<?>[] classes = m.getParameterTypes();
			boolean match = m.getName().equals(name)
					&& classes.length == args.length;
			for (int j = 0; match && j < classes.length; j++) {
				match = classes[j].isAssignableFrom(args[j].getClass())
						|| primitiveClasses.get(classes[j]) == args[j]
								.getClass();
			}
			if (match) {
				return m;
			}
		}
		return null;
	}

	public static <T> boolean in(T element, T... set) {
		for (T s : set) {
			if (element.equals(s)) {
				return true;
			}
		}
		return false;
	}

	public static Method findMethod(Class<?> clazz, String name,
			boolean declared) {
		for (Method method : declared ? clazz.getDeclaredMethods() : clazz
				.getMethods()) {
			if (method.getName().equals(name)) {
				return method;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param <T>
	 * @param set0
	 * @param set1
	 * @param set0Only
	 * @param set1Only
	 * @return true iff both sets are equal
	 */
	@SuppressWarnings("unchecked")
	public static <T> boolean computeDifference(Set<T> set0, Set<T> set1,
			Ref<Set<T>> set0Only, Ref<Set<T>> set1Only) {
		try {
			set0Only.t = set0.getClass().newInstance();
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot instantiate another "
					+ set0.getClass().getName(), e);

		}
		try {
			set1Only.t = set1.getClass().newInstance();
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot instantiate another "
					+ set1.getClass().getName(), e);

		}

		set0Only.t.addAll(set0);
		set1Only.t.addAll(set1);

		set0Only.t.removeAll(set1);
		set1Only.t.removeAll(set0);

		return set0Only.t.isEmpty() && set1Only.t.isEmpty();

	}

	public static Number round(Number number, Number unit) {
		double n = number.doubleValue();
		double u = unit.doubleValue();

		double r = Math.IEEEremainder(n, u);

		if (r < n / 2) {
			n -= r;
		} else {
			n = n + u - r;
		}

		return new Double(n);

	}

	public static InputStream filter(String command, InputStream input,
			File workingDir) throws IOException {

		if (workingDir == null) {
			workingDir = new File(".");
		}

		Process process = Runtime.getRuntime().exec(command, new String[0],
				workingDir);
		try {
			process.waitFor();
		} catch (InterruptedException ie) {

		}

		if (input != null) {
			pipe(input, process.getOutputStream());
		}

		return process.getInputStream();
	}

	public static int iIn(Object object, Object... objects) {
		return indexOf(objects, object);
	}

	private static abstract class MyFileFilter extends FileFilter {
		String suffix;
	}

	public static File[] chooseFiles(JFileChooser fileChooser,
			Component component, boolean open, boolean multiple,
			String message, String file, String... filter) {

		fileChooser.setMultiSelectionEnabled(multiple);

		for (FileFilter fileFilter : fileChooser.getChoosableFileFilters()) {
			if (fileFilter instanceof MyFileFilter) {
				fileChooser.removeChoosableFileFilter(fileFilter);
			}
		}

		if (filter.length > 0) {
			for (int i = 0; i < filter.length; i += 2) {
				final String description = filter[i];
				final String suffix = filter[i + 1].toLowerCase();
				MyFileFilter fileFilter = new MyFileFilter() {

					@Override
					public boolean accept(File f) {
						if (f.isDirectory()) {
							return true;
						}
						String name = f.getName().toLowerCase();
						return name.endsWith(suffix);
					}

					@Override
					public String getDescription() {
						return description;
					}
				};
				fileFilter.suffix = suffix;
				fileChooser.addChoosableFileFilter(fileFilter);
			}

			if (file != null) {
				int dot = file.lastIndexOf('.');
				if (dot >= 0) {
					String type = file.substring(dot + 1);
					for (FileFilter _filter : fileChooser
							.getChoosableFileFilters()) {
						if (_filter instanceof MyFileFilter) {
							if (((MyFileFilter) _filter).suffix.equals(type)) {
								fileChooser.setFileFilter(_filter);
								break;
							}

						}
					}
				}
			}
		}
		fileChooser.setDialogTitle(message);
		int ret;
		if (open) {
			ret = fileChooser.showOpenDialog(component);
		} else {
			ret = fileChooser.showSaveDialog(component);
		}
		File[] files;
		if (ret == JFileChooser.APPROVE_OPTION) {
			if (multiple) {
				if (fileChooser.getSelectedFiles() == null
						|| fileChooser.getSelectedFiles().length == 0) {
					files = null;
				} else {
					files = fileChooser.getSelectedFiles();
				}
			} else {
				File selectedFile = fileChooser.getSelectedFile();
				if (!open) {
					FileFilter selectedFilter = fileChooser.getFileFilter();
					if (selectedFilter instanceof MyFileFilter) {
						String type = ((MyFileFilter) selectedFilter).suffix;
						selectedFile = UIUtilities
								.affixType(selectedFile, type);
					}
				}
				files = new File[] { selectedFile };
			}
		} else {
			files = null;
		}
		return files;
	}

	public static class Record6<T1, T2, T3, T4, T5, T6> {

		public T1 getField1() {
			return field1;
		}

		public void setField1(T1 field1) {
			this.field1 = field1;
		}

		public T2 getField2() {
			return field2;
		}

		public void setField2(T2 field2) {
			this.field2 = field2;
		}

		public T3 getField3() {
			return field3;
		}

		public void setField3(T3 field3) {
			this.field3 = field3;
		}

		public T4 getField4() {
			return field4;
		}

		public void setField4(T4 field4) {
			this.field4 = field4;
		}

		public T5 getField5() {
			return field5;
		}

		public void setField5(T5 field5) {
			this.field5 = field5;
		}

		public T6 getField6() {
			return field6;
		}

		public void setField6(T6 field6) {
			this.field6 = field6;
		}

		private T1 field1;

		private T2 field2;

		private T3 field3;

		private T4 field4;

		private T5 field5;

		private T6 field6;

	}

	public static class Record5<T1, T2, T3, T4, T5> {

		public T1 getField1() {
			return field1;
		}

		public void setField1(T1 field1) {
			this.field1 = field1;
		}

		public T2 getField2() {
			return field2;
		}

		public void setField2(T2 field2) {
			this.field2 = field2;
		}

		public T3 getField3() {
			return field3;
		}

		public void setField3(T3 field3) {
			this.field3 = field3;
		}

		public T4 getField4() {
			return field4;
		}

		public void setField4(T4 field4) {
			this.field4 = field4;
		}

		public T5 getField5() {
			return field5;
		}

		public void setField5(T5 field5) {
			this.field5 = field5;
		}

		private T1 field1;

		private T2 field2;

		private T3 field3;

		private T4 field4;

		private T5 field5;

	}

	public static class Record4<T1, T2, T3, T4> {

		private T1 field1;

		private T2 field2;

		private T3 field3;

		private T4 field4;

		public T1 getField1() {
			return field1;
		}

		public void setField1(T1 field1) {
			this.field1 = field1;
		}

		public T2 getField2() {
			return field2;
		}

		public void setField2(T2 field2) {
			this.field2 = field2;
		}

		public T3 getField3() {
			return field3;
		}

		public void setField3(T3 field3) {
			this.field3 = field3;
		}

		public T4 getField4() {
			return field4;
		}

		public void setField4(T4 field4) {
			this.field4 = field4;
		}

	}

	public static class Record3<T1, T2, T3> {

		public T1 getField1() {
			return field1;
		}

		public void setField1(T1 field1) {
			this.field1 = field1;
		}

		public T2 getField2() {
			return field2;
		}

		public void setField2(T2 field2) {
			this.field2 = field2;
		}

		public T3 getField3() {
			return field3;
		}

		public void setField3(T3 field3) {
			this.field3 = field3;
		}

		private T1 field1;

		private T2 field2;

		private T3 field3;

	}

	public static String generateKey() {
		KeyGenerator keygen;
		try {
			keygen = KeyGenerator.getInstance("DESede");
			return toHexString(keygen.generateKey().getEncoded());
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	public static byte[] encrypt(byte[] bytes, String hexKey)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException {
		SecretKey key = new SecretKeySpec(toByteArray(hexKey), "DESede");
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DESede");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchPaddingException e) {
			throw new IllegalStateException(e);
		}
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(bytes);
	}

	public static List<Integer> getHashCodes(
			Collection<? extends Object> objects) {
		List<Integer> hashCodes = new LinkedList<Integer>();
		for (Object obj : objects) {
			hashCodes.add(System.identityHashCode(obj));
		}
		return hashCodes;
	}

	public static byte[] decrypt(byte[] bytes, String hexKey)
			throws NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		SecretKey key = new SecretKeySpec(toByteArray(hexKey), "DESede");
		try {
			Cipher cipher = Cipher.getInstance("DESede");
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(bytes);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	public static PropertyDescriptor[] getProperties(Class<?> cls) {
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(cls);
			return beanInfo.getPropertyDescriptors();
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException("Unable to introspect "
					+ cls.getName(), e);
		}

	}

	public static <T> T newInstance(String cls, Class<T> interfaceType) {
		Class<?> clazz;
		try {
			clazz = Class.forName(cls);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("class not found: " + cls, e);
		}
		Object obj;
		try {
			obj = clazz.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("cannot instantiate class: "
					+ cls, e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(
					"cannot access no-args constructor of class: " + cls, e);
		}
		T t;
		try {
			t = interfaceType.cast(obj);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("cannot cast new instance of "
					+ cls + " to interface type " + interfaceType.getName());
		}

		return t;
	}

	/**
	 * If <tt>string</tt> contains <tt>oldString</tt> as a substring, returns
	 * <tt>string</tt> with the first occurrence of <tt>oldString</tt> replaced
	 * by <tt>newString</tt>. Otherwise returns <tt>string</tt>.
	 * <p>
	 * Note that in contrast to the <tt>replaceFirst</tt> method of
	 * <tt>java.lang.String</tt>, <tt>oldString</tt> is interpreted literally,
	 * not as a regular expression.
	 * 
	 * @param string
	 * @param oldString
	 * @param newString
	 * @return
	 */
	public static String replaceFirst(String string, String oldString,
			String newString) {
		String result = string;
		if (string != null) {
			int i = string.indexOf(oldString);
			if (i >= 0) {
				result = string.substring(0, i) + newString;
				if (i + oldString.length() < string.length() - 1) {
					result += string.substring(i + oldString.length());
				}
			}
		}
		return result;

	}

	public static URL asURL(Class<?> clazz) {
		int pkg = clazz.getPackage() == null ? 0 : clazz.getPackage().getName()
				.length() + 1;
		String className = clazz.getName().substring(pkg);
		String resourceName = className.replace('.', '$') + ".class";
		URL url = clazz.getResource(resourceName);
		return url;
	}

	public static <T> T replicate(T object, final ClassLoader classloader) {
		if (object == null) {
			return null;
		}
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			os.writeObject(object);
			os.flush();
			os.close();
			byte[] bytes = bos.toByteArray();
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			final HashMap<String, Class<?>> primClasses = new HashMap<String, Class<?>>(
					8, 1.0F);
			primClasses.put("boolean", boolean.class);
			primClasses.put("byte", byte.class);
			primClasses.put("char", char.class);
			primClasses.put("short", short.class);
			primClasses.put("int", int.class);
			primClasses.put("long", long.class);
			primClasses.put("float", float.class);
			primClasses.put("double", double.class);
			primClasses.put("void", void.class);
			ObjectInputStream is = new ObjectInputStream(bis) {

				protected Class<?> resolveClass(ObjectStreamClass desc)
						throws IOException, ClassNotFoundException {
					String name = desc.getName();
					try {
						return Class.forName(name, false, classloader);
					} catch (ClassNotFoundException ex) {
						@SuppressWarnings("rawtypes")
						Class cl = (Class) primClasses.get(name);
						if (cl != null) {
							return cl;
						} else {
							throw ex;
						}
					}
				}
			};
			@SuppressWarnings("unchecked")
			T t = (T) is.readObject();
			is.close();
			return t;
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Cannot replicate object of type "
							+ object.getClass().getName(), e);
		}

	}

	private static String changeNewlines(String string, String newline) {
		string = string.replaceAll(CR, "");
		string = string.replaceAll(LF, newline);
		return string;
	}

	public static String unixEncode(String string) {
		return changeNewlines(string, LF);
	}

	public static String dosEncode(String string) {
		return changeNewlines(string, CRLF);
	}

	public static String platformEncode(String string) {
		return changeNewlines(string, System.getProperty("line.separator"));
	}

	public static String toString(byte[] bytes, String encoding) {
		Charset cs = Charset.forName(encoding);
		return new String(bytes, cs);
	}

	public static byte[] getBytes(String string, String encoding) {
		Charset cs = Charset.forName(encoding);
		return string.getBytes(cs);
	}

}
