// Copyright (c) 2006 - 2016, Markus Strauch.
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

package net.sf.sdedit.diagram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.drawable.ExtensibleDrawable;
import net.sf.sdedit.drawable.Fragment;
import net.sf.sdedit.drawable.Line;
import net.sf.sdedit.drawable.SequenceElement;
import net.sf.sdedit.util.Direction;

/**
 * A PaintDevice is an intelligent container of {@linkplain Drawable} objects,
 * computing their layout and displaying them in a way that is to be made
 * concrete by a subclass.
 * 
 * @author Markus Strauch
 */
public class SDPaintDevice extends AbstractPaintDevice {

    private int height;

    /**
     * The i-th entry of this list is the set of sequence elements that appear
     * in the space between the i-th and the (i+1)-th lifeline, where n+1
     * corresponds to rightBound
     */
    private final ArrayList<Set<SequenceElement>> leftOf;

    private final List<Drawable> other;

    private SequenceDiagram diagram;

    private final Line rightBound;
    
    public SDPaintDevice(GraphicDevice graphic) {
        super(graphic);
        height = 0;
        leftOf = new ArrayList<Set<SequenceElement>>();
        other = new LinkedList<Drawable>();
        rightBound = new Line(1, null);
    }
    
    /*
     * @see net.sf.sdedit.diagram.IPaintDevice#setDiagram(net.sf.sdedit.diagram.Diagram)
     */
    public void setDiagram(Diagram diagram) {
        this.diagram = (SequenceDiagram) diagram;
        getGraphicDevice().initialize(diagram);
    }

    /**
     * This method is called by the diagram when all objects/lifelines are
     * known.
     */
    public void reinitialize() {
        leftOf.clear();
        other.clear();
        rightBound.setLeft(0);
        height = 0;
        for (int i = 0; i < diagram.getNumberOfLifelines(); i++) {
            addLifelineSlot();
        }
    }

    private void addLifelineSlot() {
        leftOf.add(new HashSet<SequenceElement>());
    }

    private Line getRightBound() {
        return rightBound;
    }

    public void addExtraordinary(Drawable drawable) {
        other.add(drawable);
        if (drawable.getRight() > rightBound.getLeft()) {
            rightBound.setLeft(drawable.getRight());
        }
        if (drawable.getBottom() > height) {
            height = drawable.getBottom();
        }
    }

    public void append(Drawable drawable) {
        SequenceElement elem = (SequenceElement) drawable;
        int index;
        ExtensibleDrawable left, right;
        if (elem.getAlign() == Direction.RIGHT) {
            index = elem.getLeftEndpoint().getLifeline().getPosition();
            left = elem.getLeftEndpoint().getLifeline().getRightmost()
                    .getView();
            if (index == diagram.getNumberOfLifelines() - 1) {
                right = rightBound;
            } else {
                right = diagram.getLifelineAt(index + 1).getLeftmost()
                        .getView();
            }
        } else { // Direction.LEFT
            index = elem.getRightEndpoint().getLifeline().getPosition() - 1;
            right = elem.getRightEndpoint().getLifeline().getLeftmost()
                    .getView();
            left = diagram.getLifelineAt(index).getRightmost().getView();
        }
        leftOf.get(index).add(elem);
        elem.setLeftLimit(left);
        elem.setRightLimit(right);
        diagram.getFragmentManager().addSequenceElement(elem);
    }

    public boolean isEmpty() {
        return diagram == null || diagram.getNumberOfLifelines() == 0;
    }
    
    private int getMaxHeadWidthAt(int position) {
        int maxHeadWidth = 0;
        List<Lifeline> list = diagram.getLifelinesAt(position);
        for (Lifeline line : list) {
            if (line.getHead().getWidth() > maxHeadWidth) {
                maxHeadWidth = line.getHead().getWidth();
            }
        }
        return maxHeadWidth;
    }

    private void computeAxes(int leftAxis) {
        int n = diagram.getNumberOfLifelines();
        int axis = leftAxis;
        int mainWidth = diagram.getConfiguration().getMainLifelineWidth();
        int subWidth = diagram.getConfiguration().getSubLifelineWidth();

        for (int i = 0; i < n; i++) {
            if (i > 0) {
                axis += diagram.getConfiguration().getGlue();
            }
            int j = 0;
            List<Lifeline> list = diagram.getLifelinesAt(i);
            final int maxHeadWidth = getMaxHeadWidthAt(i);
            for (Lifeline lifeline : list) {

                for (ExtensibleDrawable view : lifeline.getAllViews()) {
                    if (view instanceof Line) {

                        view.setLeft(axis + mainWidth / 2);
                    } else {
                        switch (view.getLifeline().getDirection()) {
                        case CENTER:
                            view.setLeft(axis);
                            break;
                        case LEFT:
                            view.setLeft(axis
                                    - view.getLifeline().getSideLevel()
                                    * subWidth);
                            break;
                        case RIGHT:
                            view.setLeft(axis + mainWidth
                                    + (view.getLifeline().getSideLevel() - 1)
                                    * subWidth);
                        }
                    }
                }

                Drawable head = lifeline.getHead();

                int offset = mainWidth / 2;
                // int offset = lifeline.getView().getWidth() / 2;
                head.setLeft(axis - head.getWidth() / 2 + offset);

                if (lifeline.getCross() != null) {
                    lifeline.getCross().setLeft(
                            axis
                                    - diagram.getConfiguration()
                                            .getDestructorWidth() / 2
                                    + mainWidth / 2);
                }
                
                if (j == list.size()-1) {
                 // the final iteration for the current (i-th) lifeline slot
                    if (i < diagram.getNumberOfLifelines() - 1) {
                        axis += maxHeadWidth
                                / 2
                                + getMaxHeadWidthAt(i+1) / 2;
                    } else {
                        axis += maxHeadWidth / 2 + mainWidth / 2;
                    }
                }
                j++;
            }

            // iterate over the sequence elements with their contents (text)
            // to the left of the (i+1)-th lifeline
            for (SequenceElement arrow : leftOf.get(i)) {
                int left = arrow.getLeftLimit().getRight();
                int level;
                if (arrow.getRightLimit() == rightBound) {
                    level = 0;
                } else {
                    level = arrow.getRightLimit().getLifeline().getSideLevel();
                }
                axis = Math.max(axis,
                        left + arrow.getSpace() + arrow.getWidth() + level
                                * subWidth);
            }
        }
        rightBound.setLeft(axis);
    }



    public int getWidth() {
        return diagram == null ? 0 : rightBound.getLeft() + 6
                + diagram.getConfiguration().getRightMargin();
    }

    public int getHeight() {
        return height;
    }

    public void clear() {
        // as a result of this, iterator() will return an empty iterator
        diagram = null;
    }

    /**
     * Computes the width and height of this PaintDevice (this is necessary
     * before a frame and a descriptive text can be set).
     */
    public void computeBounds() {
        for (int i = 0; i < diagram.getNumberOfLifelines(); i++) {
            for (Drawable view : getAllViewsAt(i)) {
                processDrawable(view);
            }
        }
        for (int i = 0; i < leftOf.size(); i++) {
            for (SequenceElement arrow : leftOf.get(i)) {
                processDrawable(arrow);
            }
        }
        for (Drawable d : other) {
            processDrawable(d);
        }
        height += diagram.getConfiguration().getLowerMargin();
    }

    /**
     * This method is called once when no {@linkplain Drawable} object will be
     * added anymore. Its default implementation is empty.
     */


    private void processDrawable(Drawable drawable) {
        height = Math.max(height, drawable.getTop() + drawable.getHeight());
        drawable.computeLayoutInformation();
        if (drawable instanceof Fragment) {
            int r = rightBound.getLeft();
            rightBound.setLeft(Math.max(r,
                    drawable.getLeft() + drawable.getWidth()));
        }
    }

    public void announce(int height) {
        /* empty */
    }

    public SequenceDiagram getDiagram() {
        return diagram;
    }

    /**
     * Returns an iterator over the <i>visible</i> drawable elements that are
     * displayed by this <tt>PanelPaintDevice</tt>.
     * 
     * @return an iterator over the <i>visible</i> drawable elements that are
     *         displayed by this <tt>PanelPaintDevice</tt>
     */
    public Iterator<Drawable> iterator() {
        return new Iter();
    }
    
    private List<ExtensibleDrawable> getAllViewsAt(int position) {
        List<ExtensibleDrawable> allViews = new ArrayList<ExtensibleDrawable>();
        for (Lifeline line : diagram.getLifelinesAt(position)) {
            allViews.addAll(line.getAllViews());
        }
        return allViews;
    }

    private class Iter implements Iterator<Drawable> {

        // counts through indices of lifelines and sets in the leftOf list
        private int counter;

        // the current iterator, suitable for the counter value
        private Iterator<? extends Drawable> iterator;

        private Drawable next;

        private final SequenceDiagram diagram;

        private void nextIterator() {
            if (diagram == null || !diagram.isFinished()) {
                iterator = null;
                return;
            }
            counter++;
            if (counter < diagram.getNumberOfLifelines()) {
                iterator = getAllViewsAt(counter)
                        .iterator();
            } else if (counter < 2 * diagram.getNumberOfLifelines()) {
                iterator = leftOf.get(counter - diagram.getNumberOfLifelines())
                        .iterator();
            } else if (counter == 2 * diagram.getNumberOfLifelines()) {
                iterator = other.iterator();
            } else {
                iterator = null;
            }
        }

        Iter() {
            this.diagram = SDPaintDevice.this.diagram;
            counter = -1;
            nextIterator();
        }

        public boolean hasNext() {
            if (diagram != null && diagram != SDPaintDevice.this.diagram) {
                return false;
            }
            if (next == null) {
                findNext();
            }
            return next != null;
        }

        /**
         * Sets <tt>next</tt> to the next element, if there is one. As a
         * precondition, next must be null.
         */
        private void findNext() {
            while (iterator != null) {
                while (iterator.hasNext()) {
                    next = iterator.next();
                    if (next.isVisible()) {
                        return;
                    }
                }
                nextIterator();
            }
            if (next != null && !next.isVisible()) {
                next = null;
            }
        }

        public Drawable next() {
            if (next == null) {
                findNext();
            }
            if (next != null) {
                Drawable ret = next;
                next = null;
                return ret;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public Object callSpecial(String method, Object argument) {
        if ("addLifelineSlot".equals(method)) {
           addLifelineSlot();
           return null;
        } 
        if ("computeAxes".equals(method)) {
            int leftAxis = (Integer) argument;
            this.computeAxes(leftAxis);
            return null;
        } 
        if ("getRightBound".equals(method)) {
            return getRightBound();
        }
        throw new IllegalArgumentException("special method not supported: " + method);
 
    }

}
// {{core}}
