package net.sf.sdedit.util.collection;

import java.util.Iterator;

/**
 * An iterator that allows for deleting multiple elements.
 * 
 * @author Markus Strauch
 *
 * @param <E>
 */
public interface BulkRemoveIterator<E> extends Iterator<E> {
	
	/**
	 * Removes the <tt>num</tt> elements that have most recently been returned by
	 * <tt>next</tt> and that have not yet been removed.
	 * 
	 * @param num the number of elements to be removed
	 */
	public void remove (int num);

}
