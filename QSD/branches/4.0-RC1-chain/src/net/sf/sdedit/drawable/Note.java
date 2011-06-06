//Copyright (c) 2006 - 2011, Markus Strauch.
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

package net.sf.sdedit.drawable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.drawable.Strokes.StrokeType;
import net.sf.sdedit.util.Direction;

/**
 * A <tt>Note</tt> is a sequence element that looks like a piece of paper with
 * some text on it. It appears on the right of a lifeline. It can optionally be
 * connected to messages or points in lifelines by dotted lines. If a note is
 * labeled with a string starting with &quot;link:&quot; it represents a
 * hyperlink to another sequence diagram in another tab.
 * 
 * @author Markus Strauch
 * 
 */
public class Note extends SequenceElement
{

    /**
     * The unique number of the note.
     */
    private int number;

    /**
     * The distance between the text and the border of the note.
     */
    private int padding;

    /**
     * The distance between the lifeline on the left of the note and the note.
     */
    private int margin;

    /**
     * A list of points where dotted lines starting at the note are directed
     * onto.
     */
    private List<Point> targets;
    
    /**
     * An array of the four points where the dotted lines may start.
     */
    private Point [] anchors;

    /**
     * The lifeline on the left of the note.
     */
    private Lifeline location;

    /**
     * Flag denoting if the note consumes its own vertical space. If this flag
     * is false, messages that do not concern the lifelines on the left and
     * on the right of the note can consume the space.
     */
    private boolean consuming;

    /**
     * The file that is to opened when the note is clicked (may be <tt>null</tt>).
     */
    private URI link;
    
    private long targetId;
    
    private int subId;

    /**
     * Creates a new <tt>Note</tt>.
     * 
     * @param location the lifeline on the left of the note
     * @param number the unique number of the note
     * @param text the multiline text that appears inside the note box
     * @param consuming flag denoting if the note consumes its own vertical
     * space
     */
    public Note(Lifeline location, int number, String[] text, boolean consuming) {
        super(location.getDiagram(), location, location.getRightNeighbour(),
                text, Direction.RIGHT, 0);
        this.number = number;
        this.consuming = consuming;
        this.location = location;
        padding = configuration().getNotePadding();
        margin = configuration().getNoteMargin();
        int totalTextHeight = textHeight();
        setHeight(margin * 2 + padding + totalTextHeight);
        setWidth(margin * 2 + padding * 2 + leftPadding() + rightPadding()
                + textWidth());
        targets = new LinkedList<Point>();
        targetId = 0;
        subId = -1;
    }
    
    public void setTargetId (long id) {
    	this.targetId = id;
    }
    
    public long getTargetId () {
    	return targetId;
    }
    
    public void setSubId (int id) {
    	this.subId = id;
    }
    
    public int getSubId () {
    	return subId;
    }

    /**
     * Returns the flag denoting if the note consumes its own vertical space. If this flag
     * is false, messages that do not concern the lifelines on the left and
     * on the right of the note can consume the space.
     * 
     * @return the flag denoting if this note consumes its own vertical space.
     */
    public boolean isConsuming() {
        return consuming;
    }

    /**
     * Returns the unique number of the note.
     * 
     * @return the unique number of the note.
     */
    public int getNumber() {
        return number;
    }
    
    public void setNumber (int number) {
    	this.number = number;
    }

    /**
     * Adds a point such that a dotted line from the note to the point appears
     * on the diagram.
     * 
     * @param target a point such that a dotted line from the note to the point appears
     * on the diagram
     */
    public void addTarget(Point target) {
        targets.add(target);
    }

    /**
     * Returns the lifeline to the left of the note.
     * 
     * @return the lifeline to the left of the note
     */
    public Lifeline getLocation() {
        return location;
    }

    /**
     * Sets the diagram file, represented as a <tt>URI</tt> that is to be opened
     * when the note is clicked. The <tt>URI</tt> will be resolved against the
     * location of the file of the diagram to which this note belongs.
     * 
     * @param link the diagram file that is to be opened when the note is clicked
     */
    public void setLink(URI link) {
        this.link = link;
    }

    /**
     * Returns the diagram file, represented as a <tt>URI</tt> that is to be opened
     * when the note is clicked. The <tt>URI</tt> will be resolved against the
     * location of the file of the diagram to which this note belongs.
     * 
     * @return the diagram file, represented as a <tt>URI</tt> that is to be opened
     * when the note is clicked
     */
    public URI getLink() {
        return link;
    }

    /**
     * Returns the distance to the power of 2 between two points
     * 
     * @param p0 a point
     * @param p1 another point
     * @return the distance to the power of 2 between the points
     */
    private int distance2(Point p0, Point p1) {
        int d0 = Math.abs(p0.x - p1.x);
        int d1 = Math.abs(p0.y - p1.y);
        return d0 * d0 + d1 * d1;
    }

    
    private Point findStart(Point anch) {
        Point a = null;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            int d = distance2(anch, anchors[i]);
            if (d <= min) {
                a = anchors[i];
                min = d;
            }
        }
        return a;
    }

    @Override
    public void computeLayoutInformation() {
        int left = getLeftEndpoint().getLeft() + getLeftEndpoint().getWidth();
        setLeft(left);
        anchors = new Point[4];
        anchors[0] = new Point(getLeft() + margin, getTop() + getHeight() / 2);
        anchors[1] = new Point(getLeft() + getWidth() / 2, getTop() + margin);
        anchors[2] = new Point(getLeft() + getWidth() / 2, getTop()
                + getHeight() - margin);
        anchors[3] = new Point(getLeft() + getWidth() - margin, getTop()
                + getHeight() / 2);
    }

    @Override
    protected void drawObject(Graphics2D g2d) {
        int left = getLeft();
        int cornerx = left + getWidth() - margin - padding;
        int cornery = getTop() + margin + padding;
        
        
        g2d.setStroke(Strokes.getStroke(StrokeType.SOLID, diagram.getConfiguration().getNoteBorderThickness()));
        
        
        Polygon p1 = new Polygon();
        
        
        
        p1.addPoint(left + margin, getTop() + margin);
        p1.addPoint(cornerx, getTop() + margin);
        // schraeg runter
        p1.addPoint(left + getWidth() - margin, cornery);
        // ganz runter
        p1.addPoint(left + getWidth() - margin, getTop() + getHeight() - margin);
        // nach links zurueck
        p1.addPoint(left + margin, getTop() + getHeight() - margin);
        
        
        Polygon p2 = new Polygon();
        
        p2.addPoint(cornerx, getTop() + margin);
        p2.addPoint(cornerx, cornery);
        p2.addPoint(left + getWidth() - margin, cornery);
        
        g2d.setColor(configuration().getNoteBgColor());
        
        g2d.fillPolygon(p1);
        g2d.fillPolygon(p2);
        
        g2d.setColor(Color.BLACK);
        
        g2d.draw(p1);
        g2d.draw(p2);
        
        
        /*
        
        
        
        
        g2d.drawLine(left + margin, getTop() + margin, cornerx, getTop()
                + margin);
        g2d.drawLine(left + margin, getTop() + margin, left + margin, getTop()
                + getHeight() - margin);
        g2d.drawLine(left + margin, getTop() + getHeight() - margin, left
                + getWidth() - margin, getTop() + getHeight() - margin);
        g2d.drawLine(left + getWidth() - margin, getTop() + getHeight()
                - margin, left + getWidth() - margin, cornery);
        g2d.drawLine(cornerx, cornery, cornerx, getTop() + margin);
        g2d.drawLine(cornerx, cornery, left + getWidth() - margin, cornery);
        g2d.drawLine(cornerx, getTop() + margin, left + getWidth() - margin,
                cornery);
                */
        drawMultilineString(g2d, left + margin + padding, getTop()
                + getHeight() - margin - padding, null);
        for (Point anchor : targets) {
            Point start = findStart(anchor);
            g2d.setStroke(Strokes.getStroke(StrokeType.DOTTED, 1));
            g2d.drawLine(anchor.x, anchor.y, start.x, start.y);
        }
    }

    @Override
    public boolean intersects(java.awt.Rectangle rectangle) {
        if (targets.isEmpty()) {
            return super.intersects(rectangle);
        }
        int left = getLeft(), right = left + getWidth(), top = getTop(), bottom = top
                + getHeight();
        for (Point a : targets) {
            left = Math.min(left, a.x);
            right = Math.max(right, a.x);
            top = Math.min(top, a.y);
            bottom = Math.max(bottom, a.y);
        }
        return rectangle.intersects(left - 10, top - 10, right - left + 20,
                bottom - top + 20);
    }

}
//{{core}}
