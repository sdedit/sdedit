package net.sf.sdedit.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MultiIterator<E> implements Iterator<E> {
    
    private LinkedList<Iterator<E>> iterators;
    
    private E next;
    
    public MultiIterator(List<Iterator<E>> iterators) {
        this.iterators = new LinkedList<Iterator<E>>();
        this.iterators.addAll(iterators);
    }
    
    private Iterator<E> i() {
        return iterators.isEmpty() ? null : iterators.getFirst();
    }

    public boolean hasNext() {
        E next = null;
        for (;;) {
            if (i() == null) {
                return false;
            }
            if (i().hasNext()) {
                next = i().next();
                break;
            } else {
                iterators.removeFirst();
            }
        }
        this.next = next;
        return true;
    }

    public E next() {
        return next;
    }

    public void remove() {
        i().remove();       
    }

}
