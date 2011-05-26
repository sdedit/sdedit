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
package net.sf.sdedit.util.collection;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * 
 * @author Markus Strauch
 * 
 * @param <T>
 */
public class IndexedList<T> implements Collection<T>, Serializable {

	private static final long serialVersionUID = -2733083143456198060L;

	protected static class Entry implements Serializable {

		private static final long serialVersionUID = 4675147272167494931L;

		transient Entry previous;

		transient Entry next;

		Object content;

		Entry(Object content, Entry previous, Entry next) {
			this.content = content;
			this.previous = previous;
			this.content = content;
		}

		Entry(Object content, Entry previous) {
			this(content, previous, null);
		}

		Entry(Object content) {
			this(content, null, null);
		}

		public String toString() {
			return "[" + (previous == null ? "N" : previous.content.toString())
					+ " < " + content.toString() + " > "
					+ (next == null ? "N" : next.content.toString()) + "]";
		}

	}

	private static int annotationId = 0;

	public class Annotation implements Serializable {

		private static final long serialVersionUID = -6841715770952621120L;

		protected Entry annotationStart;

		protected Entry annotationEnd;

		protected String type;

		protected Object value;

		protected int id;

		Annotation() {
			id = annotationId++;
		}

		public String getType() {
			return type;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		@SuppressWarnings("unchecked")
		public T getStart() {
			return (T) annotationStart.content;
		}

		@SuppressWarnings("unchecked")
		public T getEnd() {
			return (T) annotationEnd.content;
		}

		public String toString() {
			return value.toString();
		}

	}

	protected class Iter implements BulkRemoveIterator<T> {

		private boolean direction;

		private Entry end;

		private Entry previous;

		private Entry current;

		private int maxSteps;

		private int steps;

		Iter(Entry start, Entry end, boolean direction, int maxSteps) {
			this.end = end;
			this.direction = direction;
			this.current = maxSteps == 0 ? null : start;
			this.maxSteps = maxSteps;
			this.steps = 0;
		}

		Iter(Entry start) {
			this(start, true);
		}

		Iter(Entry start, boolean direction) {
			this(start, last, direction, Integer.MAX_VALUE);
		}

		public boolean hasNext() {
			return current != null;
		}

		@SuppressWarnings("unchecked")
		public T next() {
			steps++;
			previous = current;
			if (steps == maxSteps || current == end) {
				current = null;
			} else if (direction) {
				current = current.next;
			} else {
				current = current.previous;
			}
			return (T) previous.content;
		}

		public void remove() {
			remove(1);
		}

		public void remove(int num) {
			// the entry most recently returned by next()
			Entry current = previous;
			for (int i = 0; i < num; i++) {
				// leaves current unchanged
				IndexedList.this.remove(current.content);
				if (direction) {
					// move current
					current = current.previous;
				} else {
					current = current.next;
				}
			}
		}

	}

	protected class AnnotationComparator implements Comparator<Annotation> {

		/**
		 * The entry for which we currently return annotations (
		 * {@linkplain #getAnnotations(Object)}).
		 */
		private Entry annotationBegin;

		/**
		 * Contains the distances from annotationBegin to the preImages.
		 */
		private Map<Entry, Integer> entryDistances;

		AnnotationComparator(Entry begin) {
			annotationBegin = begin;
			entryDistances = new HashMap<Entry, Integer>();
			entryDistances.put(annotationBegin, 0);
		}

		private int getDistance(Entry f) {
			Integer d = 0;
			int s = 0;
			Entry e = f;
			do {
				d = entryDistances.get(e);
				if (d != null) {
					if (s > 0) {
						// we have made at least one step, so
						// the distance was not known before
						entryDistances.put(f, d + s);
					}
					return d + s;
				}
				s++;
				e = e.previous;
				if (e == null) {
					throw new IllegalStateException("Cannot find distance.");
				}
			} while (true);
		}

		public int compare(Annotation a1, Annotation a2) {
			if (a1.annotationStart != annotationBegin
					|| a2.annotationStart != annotationBegin) {
				throw new IllegalStateException("Cannot compare annotations");
			}
			int d1 = getDistance(a1.annotationEnd);
			int d2 = getDistance(a2.annotationEnd);
			if (d1 == d2) {
				return a1.id - a2.id;
			}
			return d2 - d1;
		}

	}

	private transient Map<T, Entry> entryMap;

	private transient Entry first;

	private transient Entry last;

	private transient OntoMap<Annotation, Entry> annotations;

	public IndexedList() {
		entryMap = new DistinctObjectsMap<T, Entry>();
		first = null;
		last = null;
	}

	public void addAnnotation(T from, T to, String type, Object value) {
		if (annotations == null) {
			annotations = new OntoMap<Annotation, Entry>(LinkedList.class);
		}
		Annotation annot = new Annotation();
		annot.annotationStart = entryMap.get(from);
		annot.annotationEnd = entryMap.get(to);
		annot.type = type;
		annot.value = value;
		annotations.add(annot, annot.annotationStart);
	}

	/**
	 * Returns all annotations starting at the given <tt>T</tt>, where
	 * annotations stretching over a longer distance will be returned before
	 * annotations stretching over a shorter distance.
	 * 
	 * @param of
	 * @return
	 */
	public Collection<Annotation> getAnnotations(T of) {
		if (annotations == null) {
			return new LinkedList<Annotation>();
		}
		Entry begin = entryMap.get(of);
		AnnotationComparator comp = new AnnotationComparator(begin);
		SortedSet<Annotation> set = new TreeSet<Annotation>(comp);
		set.addAll(annotations.getPreImages(begin));
		return set;
	}

	protected void _remove(Entry entry) {
		entryMap.remove(entry.content);
		if (entry.previous != null) {
			entry.previous.next = entry.next;
		}
		if (entry.next != null) {
			entry.next.previous = entry.previous;
		}
		if (entry == first) {
			first = entry.next;
		}
		if (entry == last) {
			last = entry.previous;
		}
	}

	public boolean remove(Object elem) {
		Entry entry = entryMap.get(elem);
		if (entry != null) {
			_remove(entry);
			return true;
		}
		return false;

	}

	protected void setContent(Entry e, T t) {
		e.content = t;
	}

	@SuppressWarnings("unchecked")
	protected T getContent(Entry e) {
		return (T) e.content;
	}

	public boolean add(T o) {
		if (entryMap.containsKey(o)) {
			throw new IllegalArgumentException("already in list: " + o);
		}
		Entry entry = new Entry(o);
		if (last == null) {
			first = last = entry;
		} else {
			last.next = entry;
			entry.previous = last;
		}
		last = entry;
		entryMap.put(o, entry);
		return true;
	}

	public boolean addAll(Collection<? extends T> c) {
		if (c == null) {
			return false;
		}
		for (T t : c) {
			add(t);
		}
		return !c.isEmpty();
	}

	public void clear() {
		entryMap.clear();
		first = null;
		last = null;
		if (annotations != null) {
			annotations.clear();
		}
		annotations = null;
	}

	public boolean contains(Object o) {
		return entryMap.containsKey(o);
	}

	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!entryMap.containsKey(o)) {
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty() {
		return entryMap.isEmpty();
	}

	public BulkRemoveIterator<T> iterator() {
		return new Iter(first);
	}

	public BulkRemoveIterator<T> reverseIterator() {
		return new Iter(last, first, false, Integer.MAX_VALUE);
	}

	public BulkRemoveIterator<T> iterator(T start) {
		return new Iter(entryMap.get(start), last, true, Integer.MAX_VALUE);
	}

	public BulkRemoveIterator<T> reverseIterator(T start) {
		return new Iter(entryMap.get(start), first, false, Integer.MAX_VALUE);
	}

	public BulkRemoveIterator<T> iterator(T start, T end) {
		return new Iter(entryMap.get(start), entryMap.get(end), true,
				Integer.MAX_VALUE);
	}

	public BulkRemoveIterator<T> reverseIterator(T start, T end) {
		return new Iter(entryMap.get(start), entryMap.get(end), false,
				Integer.MAX_VALUE);
	}

	public BulkRemoveIterator<T> boundedIterator(T start, int steps) {
		return new Iter(entryMap.get(start), last, true, steps);
	}

	public BulkRemoveIterator<T> boundedReverseIterator(T start, int steps) {
		return new Iter(entryMap.get(start), first, false, steps);
	}

	@SuppressWarnings("unchecked")
	public T previous(T elem) {
		Entry previous = entryMap.get(elem).previous;
		if (previous == null) {
			return null;
		}
		return (T) previous.content;
	}

	@SuppressWarnings("unchecked")
	public T next(T elem) {
		Entry next = entryMap.get(elem).next;
		if (next == null) {
			return null;
		}
		return (T) next.content;
	}

	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			changed |= remove(o);
		}
		return changed;
	}

	public boolean retainAll(Collection<?> c) {
		Iterator<T> iter = iterator();
		HashSet<Object> set = new HashSet<Object>();
		set.addAll(c);
		int n = size();
		while (iter.hasNext()) {
			T elem = iter.next();
			if (!set.contains(elem)) {
				iter.remove();
			}
		}
		return size() != n;
	}

	public int size() {
		return entryMap.size();
	}

	public String toString() {
		return Arrays.toString(toArray());
	}

	public boolean equals(Object o) {
		IndexedList<?> list = (IndexedList<?>) o;
		return Arrays.equals(toArray(), list.toArray());
	}

	public int hashCode() {
		return Arrays.hashCode(toArray());
	}

	public Object[] toArray() {
		Object[] arr = new Object[size()];
		int i = 0;
		for (T t : this) {
			arr[i++] = t;
		}
		return arr;
	}

	@SuppressWarnings( { "unchecked", "hiding" })
	public <T> T[] toArray(T[] a) {
		T[] arr;
		if (a.length >= size()) {
			arr = a;
		} else {
			arr = (T[]) Array.newInstance(a.getClass().getComponentType(),
					size());
		}
		int i = 0;
		Iterator<?> iter = iterator();
		while (iter.hasNext()) {
			arr[i++] = (T) iter.next();
		}
		return arr;
	}

	@SuppressWarnings("unchecked")
	public T removeFirst() {
		_remove(first);
		return (T) first.content;
	}

	@SuppressWarnings("unchecked")
	public T getFirst() {
		return (T) first.content;
	}
	
	protected Entry insertAfter (Entry _where, T what) {
		Entry newEntry = new Entry(what);
		newEntry.next = _where.next;
		if (_where.next != null) {
			_where.next.previous = newEntry;
		}
		_where.next = newEntry;
		newEntry.previous = _where;
		entryMap.put(what, newEntry);
		if (last == _where) {
			last = newEntry;
		}
		return newEntry;
	}

	public void addFirst(T first) {
		Entry entry = new Entry(first);
		if (this.first != null) {
			this.first.previous = entry;
			entry.next = this.first;
		}
		this.first = entry;
		entryMap.put(first, entry);
		if (this.last == null) {
			this.last = this.first;
		}
	}

	@SuppressWarnings("unchecked")
	public T removeLast() {
		_remove(last);
		return (T) last.content;
	}
	
	@SuppressWarnings("unchecked")
	public T getLast() {
		return (T) last.content;
	}

	public void addLast(T last) {
		add(last);
	}
	
	public void replace(T whom, Collection<T> by) {
		Entry _whom = entryMap.get(whom);
		Entry entry = _whom;
		for (T t : by) {
			entry = insertAfter(entry, t);
		}
		remove(whom);
	}

	public int indexOf(T elem) {
		Entry entry = entryMap.get(elem);
		int i = -1;
		while (entry != null) {
			i++;
			entry = entry.previous;
		}
		return i;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		LinkedList<T> ll = new LinkedList<T>();
		ll.addAll(this);
		out.defaultWriteObject();
		out.writeObject(ll);
		if (annotations == null) {
			out.writeObject(new LinkedList<Annotation>());
		} else {
			out.writeObject(annotations.getPreImages());
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		entryMap = new DistinctObjectsMap<T, Entry>();
		LinkedList<T> ll = (LinkedList<T>) in.readObject();
		addAll(ll);
		Collection<Annotation> annots = (Collection<Annotation>) in
				.readObject();
		for (Annotation a : annots) {
			addAnnotation(a.getStart(), a.getEnd(), a.getType(), a.getValue());
		}
	}
	
	public static void main (String [] argv) {
		IndexedList<Integer> list = new IndexedList<Integer> ();
		list.add(1);
		list.add(2);
		list.add(3);
		Iterator<Integer> i = list.iterator();
		while (i.hasNext()) {
			i.next();
			i.remove();
		}
		System.out.println(list);
		list.add(5);
		System.out.println(list);
		list.add(6);
		System.out.println(list);
		list.add(7);
		System.out.println(list);
		BulkRemoveIterator<Integer> b = list.reverseIterator();
		b.next();
		b.next();
		b.remove(1);
		System.out.println(list);
		list.addFirst(0);
		System.out.println(list);
		list.add(10);
		System.out.println(list);
		List<Integer> rep = Arrays.asList(new Integer [] {12,13,14});
		list.replace(7, rep);
		System.out.println(list);
			
		
		
		
	}

}
