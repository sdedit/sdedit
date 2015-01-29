package net.sf.sdedit.diagram;

import java.awt.Font;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.drawable.Line;

public class NullPaintDevice implements IPaintDevice {

    private Diagram diagram;

    public void addExtraordinary(Drawable drawable) {
    }

    public void append(Drawable drawable) {
    }

    public void announce(int height) {
    }

    public void clear() {
    }

    public void close() {
    }

    public void computeBounds() {
    }

    public Diagram getDiagram() {
        return diagram;
    }

    public Font getFont(boolean bold) {
        return Font.decode("Dialog");
    }

    public int getHeight() {
        return 0;
    }

    public Line getRightBound() {
        return null;
    }

    public int getTextHeight(boolean bold) {
        return 0;
    }

    public int getTextHeight() {
        return 0;
    }

    public int getTextWidth(String text, boolean bold) {
        return 0;
    }

    public int getTextWidth(String text) {
        return 0;
    }

    public int getWidth() {
        return 0;
    }

    public boolean isEmpty() {
        return true;
    }

    public void reinitialize() {
    }

    public void setDiagram(Diagram diagram) {
        this.diagram = diagram;
    }

    public Iterator<Drawable> iterator() {
        return new LinkedList<Drawable>().iterator();
    }

    public Object callSpecial(String method, Object argument) {
        return null;
    }

}
