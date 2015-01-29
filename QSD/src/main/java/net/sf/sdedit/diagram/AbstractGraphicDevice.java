package net.sf.sdedit.diagram;

import java.awt.Font;

import net.sf.sdedit.drawable.Drawable;

public abstract class AbstractGraphicDevice implements GraphicDevice {
    
    private Iterable<Drawable> drawables;
    
    private Font plainFont;
    
    private Font boldFont;

    private int width;

    private int height;
    
    protected Iterable<Drawable> drawables () {
        return drawables;
    }

    public void initialize(Diagram diagram) {
        this.drawables = diagram.getPaintDevice();    
        plainFont = diagram.getConfiguration().getFont();
        boldFont = new Font(plainFont.getName(), Font.BOLD,
                plainFont.getSize() + 1);
    }

    public Font getFont(boolean bold) {
        return bold ? boldFont : plainFont;
    }

    public void close(int width, int height, boolean empty) {
        this.width = width;
        this.height = height;
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
