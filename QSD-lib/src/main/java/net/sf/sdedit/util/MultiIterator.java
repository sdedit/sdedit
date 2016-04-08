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
