package net.sf.sdedit.multipage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import net.sf.sdedit.config.PrintConfiguration;
import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.PaintDevice;
import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.ui.components.ZoomPane;
import net.sf.sdedit.ui.components.Zoomable;

/**
 * 
 * @author Markus Strauch
 */
public class MultipagePaintDevice extends PaintDevice {

	private Dimension pageSize;

	private Graphics2D graphics;

	private Graphics2D boldGraphics;

	private List<MultipagePanel> panels;

	private double scale;

	private PrintConfiguration properties;

	public MultipagePaintDevice(PrintConfiguration properties, Dimension pageSize) {
		super();
		this.pageSize = pageSize;
		panels = new ArrayList<MultipagePanel>();
		this.properties = properties;
	}

	@Override
	public void setDiagram(Diagram diagram) {
		super.setDiagram(diagram);
		graphics = (Graphics2D) new BufferedImage(1, 1,
				BufferedImage.TYPE_USHORT_GRAY).getGraphics();
		graphics.setFont(getFont(false));
		boldGraphics = (Graphics2D) new BufferedImage(1, 1,
				BufferedImage.TYPE_USHORT_GRAY).getGraphics();
		boldGraphics.setFont(getFont(false));
	}

	public Dimension getPageSize() {
		return pageSize;
	}

	private double computeScale() {
		int h = pageSize.height;
		int w = pageSize.width;
		int H = getHeight();
		int W = getWidth();

		double xscale = 1D * w / W;
		if (properties.isMultipage()) {
			// Disabled (see also PrinterProperties dependency)
			// if (properties.isFitToPage()) {
			// return xscale;
			// }
			return Math.min(1, xscale);
		}
		double yscale = 1D * h / H;

		if (properties.isFitToPage()) {
			return Math.min(xscale, yscale);
		}
		return Math.min(1, Math.min(xscale, yscale));
	}
	
	public double getScale () {
		return scale;
	}

	public void close() {
		super.close();
		scale = computeScale();
		int numberOfPages;
		if (properties.isMultipage()) {
			numberOfPages = ((int) (getHeight() * scale)) / pageSize.height + 1;
		} else {
			numberOfPages = 1;
		}
		for (int i = 0; i < numberOfPages; i++) {
			panels.add(new MultipagePanel(i));
		}
	}

	public List<MultipagePaintDevice.MultipagePanel> getPanels() {
		return panels;
	}

	public void announce(int height) {
		if (properties.isMultipage()) {
			int v0 = getDiagram().getVerticalPosition();
			int v1 = v0 + height;
			if (v1 / pageSize.height > v0 / pageSize.height) {
				int diff = pageSize.height - v0 % pageSize.height;
				getDiagram().extendLifelines(diff);
			}
		}
	}

	// @Override
	// public void addSequenceElement (SequenceElement elem) {
	// int y0 = elem.getTop() % pageSize.height;
	// int y1 = y0 + elem.getHeight();
	// int v = getDiagram().getVerticalPosition();
	// if (y1 >= pageSize.height) {
	// int beginOfNextPage = 0; // compute position where content of next
	// page/panel begins
	// // consider margins and heads
	// getDiagram().extendLifelines(beginOfNextPage - v);
	// elem.setTop(beginOfNextPage);
	// }
	// // TODO change Message.updateView such that lifelines are activated
	// // after the arrow has been added (via addSequenceElement)
	// // -- the top position of an arrow is valid only if it has
	// // already been added to the PaintDevice
	// super.addSequenceElement(elem);
	// }

	@Override
	public int getTextHeight(boolean bold) {
		return (bold ? boldGraphics : graphics).getFontMetrics().getHeight();
	}

	@Override
	public int getTextWidth(String text, boolean bold) {
		return (bold ? boldGraphics : graphics).getFontMetrics().stringWidth(
				text);
	}

	public class MultipagePanel extends JPanel implements Zoomable<JPanel> {

		private int index;

		private ZoomPane zoomPane;

		private Dimension dim;

		MultipagePanel(int index) {
			this.index = index;
			dim = pageSize;
		}

		@Override
		public Dimension getSize() {
			return dim;
		}

		public Dimension getPreferredSize() {
			return dim;
		}

		public Dimension getMinimumSize() {
			return dim;
		}

		public Dimension getMaximumSize() {
			return dim;
		}

		public int getHeight() {
			return dim.height;
		}

		public int getWidth() {
			return dim.width;
		}

		// compute the area in the center of the panel
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.scale(scale, scale);
			Rectangle clipBounds = g2d.getClipBounds();
			g2d.setColor(Color.WHITE);
			g2d.fill(clipBounds);
			Rectangle rect = null;
			int w = (int) (pageSize.width / scale);
			int h = (int) (pageSize.height / scale);
			if (properties.isMultipage()) {
				int y0 = index * (int) (pageSize.height / scale);
				g2d.translate(0, -y0);
				rect = new Rectangle(0, y0, w, h);

			} else {
				if (properties.isCenterVertically()) {
					int yg = h - MultipagePaintDevice.this.getHeight();
					if (yg > 0) {
						g2d.translate(0, yg / 2);
					}
				}
			}
			if (properties.isCenterHorizontally()) {
				int xg = w - MultipagePaintDevice.this.getWidth();
				if (xg > 0) {
					g2d.translate(xg / 2, 0);
				}
			}
			for (Drawable drawable : MultipagePaintDevice.this) {
				if (rect == null || drawable.intersects(rect)) {
					drawable.draw(g2d);
				}
			}
			g2d.dispose();
		}

		public JPanel asJComponent() {
			return this;
		}

		public int getAbsoluteHeight() {
			return pageSize.height;
		}

		public int getAbsoluteWidth() {
			return pageSize.width;
		}

		public ZoomPane getZoomPane() {
			return zoomPane;
		}

		public void setZoomPane(ZoomPane zoomPane) {
			this.zoomPane = zoomPane;
		}
	}
}
