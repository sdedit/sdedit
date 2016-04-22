package net.sf.sdedit.help;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.freehep.graphicsio.pdf.PDFGraphics2D;

public class FontMetricsTest {

	public static void main(String[] argv) throws Exception {
		FileOutputStream stream = new FileOutputStream(new File("C:/Temp/fm.pdf"));
		Dimension dim = new Dimension(1000, 1000);
		String orientation = "Portrait";
		String format = "A4";
		PDFGraphics2D pdf = new PDFGraphics2D(stream, dim);
		Properties properties = new Properties();
		properties.setProperty(PDFGraphics2D.ORIENTATION, orientation);
		properties.setProperty(PDFGraphics2D.PAGE_SIZE, format);
		pdf.setProperties(properties);
        pdf.startExport();
        for (int i = 0; i < 600; i+=10) {
        	pdf.drawLine(i, 0, i, 850);
        	if (i > 0 && i % 50 == 0) {
        		pdf.drawString("" + i/10, i, 15);
        	}
        }
        for (char c = ':'; c <= ':'; c++) {
        	String str = "";
        	for (int i = 0; i < 50; i++) {
        		str+=c;
        	}
        	pdf.drawString(str,0,30+(c-' ')*30);
        }
        pdf.endExport();
        stream.flush();
        stream.close();
        
	}

}
