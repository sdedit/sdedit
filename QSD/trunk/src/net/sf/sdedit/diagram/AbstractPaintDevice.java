package net.sf.sdedit.diagram;

import java.awt.Font;


public abstract class AbstractPaintDevice implements IPaintDevice {
    
    private final GraphicDevice graphicDevice;
    
    public GraphicDevice getGraphicDevice () {
        return graphicDevice;
    }
    
    protected AbstractPaintDevice (GraphicDevice graphicDevice) {
        this.graphicDevice = graphicDevice;
    }
    
    public Font getFont (boolean bold) {
        return graphicDevice.getFont(bold);
    }
    
    public int getTextWidth(String text, boolean bold) {
        return getGraphicDevice().getTextWidth(text, bold);
    }

    public int getTextWidth(String text) {
        return getTextWidth(text, false);
    }

    public int getTextHeight(boolean bold) {
        return getGraphicDevice().getTextHeight(bold);
    }

    public int getTextHeight() {
        return getTextHeight(false);
    }
    
    public void close() {
        graphicDevice.close(getWidth(), getHeight(), isEmpty());
    }
    
}
