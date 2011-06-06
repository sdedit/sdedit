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
package net.sf.sdedit.drawable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.sun.tools.doclets.internal.toolkit.Configuration;

import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.drawable.Strokes.StrokeType;
import net.sf.sdedit.message.ConstructorMessage;
import net.sf.sdedit.message.Message;
import net.sf.sdedit.util.Pair;

/**
 * A <tt>Fragment</tt> consists of a sequence of messages occuring in a
 * diagram. It appears as a frame surrounding all of the messages. A fragment
 * has a type, appearing in the top-left corner of the frame and a text,
 * commonly interpreted as a condition. It appears in square brackets. Fragments
 * can have sections, separated by dashed lines. Each section has its own
 * condition text.
 * 
 * @author Markus Strauch
 * 
 */
public class Fragment extends Drawable
{
    /**
     * The type of this fragment, appears in the top-left corner.
     */
    private String type;

    /**
     * The text of the condition to appear in square brackets below the type.
     */
    private String condition;

    /**
     * The diagram of which the fragment is a part.
     */
    private Diagram diagram;

    /**
     * The sequence elements that are members of the fragment.
     */
    private Set<SequenceElement> includedElements;

    /**
     * The space between the left border of the frame and the text representing
     * the type.
     */
    private int typeTextPadding;

    /**
     * The number of fragments that surround this fragment.
     */
    private int level;

    /**
     * A list of pairs of vertical positions and texts of dashed separator
     * lines, indicating new sections of the fragment.
     */
    private List<Pair<Integer, String>> separators;

    /**
     * Creates a new <tt>Fragment</tt>. If <tt>type</tt> is empty, the
     * condition appears in the top-left corner of the fragment, so it should
     * rather be interpreted as a comment on the elements inside the fragment
     * frame.
     * 
     * @param type
     *            the type of the fragment, for example &quot;alt&quot;,
     *            &quot;opt&quot; or &quot;loop&quot;
     * @param condition
     *            the condition of the fragment
     * @param diagram
     *            the diagram of which the fragment is a part
     */
    public Fragment(String type, String condition, Diagram diagram) {
        if (type.equals("")) {
            this.type = condition + "  ";
            this.condition = "";
        } else {
            this.type = type + "  ";
            this.condition = condition.equals("") ? "" : "[" + condition + "]";
        }
        this.diagram = diagram;
        includedElements = new HashSet<SequenceElement>();
        typeTextPadding = diagram.getConfiguration().getFragmentTextPadding();
        level = 0;
        separators = new LinkedList<Pair<Integer, String>>();
    }

    /**
     * Sets the level of this fragment (the number of fragments that this
     * fragment is a part of) to the maximum of the current level and the given
     * one.
     * 
     * @param level
     *            the new level of this fragment, if it does not already have a
     *            higher one
     */
    public void setLevel(int level) {
        this.level = Math.max(this.level, level);
    }

    /**
     * Returns the condition of this fragment (to be precise, of the first
     * section of the fragment).
     * 
     * @return the condition of this fragment
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Adds a sequence element that is a member of this fragment.
     * 
     * @param element
     *            a member of this fragment
     */
    public void addElement(SequenceElement element) {
        includedElements.add(element);
    }

    /**
     * Adds a section to this fragment with its own condition. It will be
     * represented by a dashed line appearing at the current vertical position.
     * 
     * @param sectionCondition
     *            the condition of the section, appearing in square brackets
     *            below the dashed line
     */
    public void addSection(String sectionCondition) {
        int h = diagram.getPaintDevice().getTextHeight(true);
        diagram.extendLifelines(diagram.getConfiguration()
                .getSeparatorTopMargin());
        separators.add(new Pair<Integer, String>(diagram.getVerticalPosition(),
                sectionCondition));
        diagram.extendLifelines(h
                + diagram.getConfiguration().getSeparatorBottomMargin() + 5);
    }

    /**
     * Returns true if the given sequence element is a member of this fragment.
     * 
     * @param element
     *            a sequence element
     * @return true if the element is a member of this fragment
     */
    public boolean containsElement(SequenceElement element) {
        return includedElements.contains(element);
    }

    /**
     * @see net.sf.sdedit.drawable.Drawable#computeLayoutInformation()
     */
    public void computeLayoutInformation() {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (SequenceElement element : includedElements) {
            int left = element.getLeft();
            int right = element.getLeft() + element.getWidth();
            if (element instanceof Arrow) {
                Message msg = ((Arrow) element).getMessage();
                if (msg instanceof ConstructorMessage) {
                    if (msg.getCallee().getPosition() < msg.getCaller()
                            .getPosition()) {
                        left = msg.getCallee().getHead().getLeft();
                    } else {
                        right = msg.getCallee().getHead().getLeft()
                                + msg.getCallee().getHead().getWidth();
                    }
                }
                if (((Arrow) element).getStroke() != ArrowStroke.NONE) {
                    right += element.getRightEndpoint().getWidth();
                } // nothing to add for pseudo arrows (primitive messages)
                // with
                // ArrowStroke.NONE
            }
            min = Math.min(min, left);
            max = Math.max(max, right);
        }
        int width = max - min;
        int padding = diagram.getConfiguration().getFragmentPadding();
        int margin = diagram.getConfiguration().getFragmentMargin();
        setLeft(min - padding - level * padding);
        setWidth(2 * level * padding + width + 2 * padding + margin);
    }
    
    /**
     * Computes the height of the label of the fragment.
     * 
     * @return the height of the label of the fragment
     */
    public int getLabelHeight () {
        return diagram.getPaintDevice().getTextHeight(true) + 2;
    }

    /**
     * @see net.sf.sdedit.drawable.Drawable#drawObject(java.awt.Graphics2D)
     */
    protected void drawObject(Graphics2D g2d) {
        int typeWidth = diagram.getPaintDevice().getTextWidth(type, true);
        int textWidth = diagram.getPaintDevice().getTextWidth(condition, true);
        int textHeight = diagram.getPaintDevice().getTextHeight(true);

        int leftMargin = typeWidth + 4 + typeTextPadding;

        
        g2d.setColor(diagram.getConfiguration().getFragmentLabelBgColor());
        // clear the type corner
        g2d.fillRect(getLeft(), getTop(), typeWidth + 4, textHeight + 2);

        int thickness = diagram.getConfiguration().getFragmentBorderThickness();
   
        g2d.setStroke(Strokes.getStroke(StrokeType.SOLID, thickness));

        g2d.setFont(diagram.getPaintDevice().getFont(true));

        g2d.setColor(diagram.getConfiguration().getFragmentEdgeColor());        
        g2d.drawRect(getLeft(), getTop(), getWidth(), getHeight());
        


        g2d.setColor(Color.BLACK);
        if (!type.equals("")) {
            g2d.drawLine(getLeft(), getTop() + textHeight + 2, getLeft()
                    + typeWidth + typeTextPadding, getTop() + textHeight + 2);
            g2d.drawLine(getLeft() + typeWidth + typeTextPadding, getTop()
                    + textHeight + 2, getLeft() + typeWidth + typeTextPadding
                    + 2, getTop() + textHeight - 2);
            g2d.drawLine(getLeft() + typeWidth + typeTextPadding + 2, getTop()
                    + textHeight - 2, getLeft() + typeWidth + typeTextPadding
                    + 2, getTop());
            g2d.drawString(type, getLeft() + typeTextPadding, getTop()
                    + textHeight - 1);
        }

        if (condition.length() > 0) {
            // clear the canvas under the text
        	g2d.setColor(diagram.getConfiguration().getFragmentLabelBgColor());
            g2d.fillRect(getLeft() + leftMargin, getTop() + textHeight + 2,
                    textWidth, textHeight);

            g2d.setColor(Color.BLACK);
            g2d.drawString(condition, getLeft() + leftMargin, getTop() + 2
                    * textHeight - 1);

        }

        for (Pair<Integer, String> sep : separators) {
            int y = sep.getFirst();
            g2d.setStroke(Strokes.getStroke(StrokeType.DASHED, thickness));
            g2d.drawLine(getLeft(), y, getRight(), y);
            g2d.setStroke(Strokes.defaultStroke());
            g2d.drawString(sep.getSecond(), getLeft() + leftMargin, y
                    + textHeight
                    + diagram.getConfiguration().getSeparatorBottomMargin());
        }

    }

}
//{{core}}
