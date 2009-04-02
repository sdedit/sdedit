// Copyright (c) 2006 - 2008, Markus Strauch.
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Utilities {

	private Utilities() {

	}

	public static <T> T pollFirst(Collection<T> set) {
		Iterator<T> iterator = set.iterator();
		if (!iterator.hasNext()) {
			throw new IllegalStateException("The set is empty");
		}
		T first = iterator.next();
		iterator.remove();
		return first;

	}
	
	public static String substring (String string, int pos, int length) {
		if (string.length() - pos <= length) {
			return string.substring(pos);
		}
		return string.substring(pos, pos+length);
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
		for (T t : array) {
			if (t.equals(elem)) {
				return true;
			}
		}
		return false;
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
	
	@SuppressWarnings("unchecked")
	public static <T> T [] reverse(T[] array) {
		Class<?> elemClass = array.getClass().getComponentType();
		int l = array.length;
		T [] reverse = (T []) Array.newInstance(elemClass, l);
		for (int i = 0; i < array.length; i++) {
			reverse[l-i-1] = array[i];			
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
	
	public static String classesString (Object [] objects, boolean simple) {
		String [] strings = new String [objects.length];
		for (int i = 0; i < objects.length; i++) {
			if (simple) {
				strings [i] = objects[i].getClass().getSimpleName();
			} else {
				strings [i] = objects[i].getClass().getName();
			}
		}
		return "[" + join(",", strings) + "]";
	}
	
	public static String classesString (Collection<?> objects, boolean simple) {
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

	public static void pipe(InputStream from, OutputStream to)
			throws IOException {
		while (from.available() > 0) {
			byte b = (byte) from.read();
			to.write(b);
		}
	}

	public static Iterable<String> readLines(String command,
			Ref<InputStream> stream) throws IOException {
		System.out.println(command);
		Process proc = Runtime.getRuntime().exec(command);
		stream.t = proc.getInputStream();
		return readLines(stream);
	}

	public static Iterable<String> readLines(File file, Ref<InputStream> stream)
			throws IOException {
		stream.t = new FileInputStream(file);
		return readLines(stream);
	}

	public static Iterable<String> readLines(Ref<InputStream> stream)
			throws IOException {

		try {
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(stream.t));
			return new Iterable<String>() {

				public Iterator<String> iterator() {
					return new Iterator<String>() {

						private String currentLine;

						public boolean hasNext() {
							try {
								currentLine = reader.readLine();
							} catch (IOException e) {
								throw new IllegalStateException(
										"Cannot read file", e);
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
		} finally {
			stream.t.close();
		}
	}
	
	public static void main (String [] args) throws Throwable {
		Ref<InputStream> stream = new Ref<InputStream>();
		for (String line : Utilities.readLines("REG QUERY HKLM\\Software", stream)) {
			System.out.println(line);
		}
	}
}
