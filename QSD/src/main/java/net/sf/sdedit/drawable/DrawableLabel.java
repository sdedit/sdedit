package net.sf.sdedit.drawable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sf.sdedit.util.Utilities;

public class DrawableLabel {
	
	private JLabel jlabel;
	
	private boolean bold;
	
	private boolean italic;
	
	private boolean underlined;
	
	private String [] label;

	private Drawable drawable;
	
	public DrawableLabel (Drawable parent) {
		this.drawable = parent;
	}	
	
    protected static JLabel makeLabel(String[] strings, Font font, boolean bold, boolean italic, boolean underlined) {
		JLabel label = new JLabel();
		label.setFont(font);
		String txt = "<html>";
		if (bold) {
			txt += "<b>";
		}
		if (italic) {
			txt += "<i>";
		}
		if (underlined) {
			txt += "<u>";
		}
		txt += Utilities.join("<br>", strings);
		if (bold) {
			txt += "</b>";
		}
		if (italic) {
			txt += "</i>";
		}
		if (underlined) {
			txt += "</u>";
		}
		txt += "</html>";
		label.setText(txt.replaceAll(" ", "&nbsp;"));
		label.setVerticalAlignment(SwingConstants.BOTTOM);
		return label;
    }
    
    public int textWidth() {
    	 return getJLabel().getPreferredSize().width;
    }

    public int textHeight() {
        return getJLabel().getPreferredSize().height;
    }
    
    public int getNumLines () {
    	return label.length;
    }
    
    public int getSimpleHeight() {
    	return textHeight() / getNumLines();
    }
	
    protected JLabel getJLabel() {
    	Font font = drawable.getDiagram().getPaintDevice().getFont();
    	if (jlabel == null) {
    		jlabel = makeLabel(label, font, bold, italic, underlined);
    	}
    	jlabel.setFont(font);
    	return jlabel;
    }
	
    protected void drawLabel(Graphics2D g, int x, int y,Color color, 
            Color background) {
    	JLabel label = getJLabel();
    	if (color != null) {
    		label.setForeground(color);
    	}
    	if (background != null) {
    		label.setBackground(background);
    	}
		label.setSize(label.getPreferredSize());
		Graphics g1 = g.create();
		g1.translate(x, y-label.getHeight());
		label.paint(g1);
		g1.dispose();
	}
    
	public boolean isBold() {
		return bold;
	}

	public DrawableLabel setBold(boolean bold) {
		this.bold = bold;
		return this;
	}

	public boolean isItalic() {
		return italic;
	}

	public DrawableLabel setItalic(boolean italic) {
		this.italic = italic;
		return this;
	}
	
	public boolean isUnderlined() {
		return underlined;
	}
	
	public DrawableLabel setUnderlined(boolean underlined) {
		this.underlined = underlined;
		return this;
	}
	
	protected DrawableLabel setLabel(String... label) {
		this.label = label;
		return this;
	}
	
	protected String[] getLabel() {
		return label;
	}

}
