package net.sf.sdedit.diagram;

import java.awt.Font;

public interface GraphicDevice {

    public int getTextHeight(boolean bold);

    public int getTextWidth(String text, boolean bold);
    
    public void close(int width, int height, boolean empty);
    
    public void initialize (Diagram diagram);
    
    public Font getFont(boolean bold);
    
}
