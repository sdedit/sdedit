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
package net.sf.sdedit.ui.impl;

import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.diagram.AbstractPaintDevice;
import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.DiagramDataProviderFactory;
import net.sf.sdedit.diagram.DiagramFactory;
import net.sf.sdedit.diagram.GraphicDevice;
import net.sf.sdedit.diagram.PaintDevice;
import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.editor.plugin.FileActionProvider;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.server.Exporter;
import net.sf.sdedit.ui.ImageGraphicsDevice;
import net.sf.sdedit.ui.PanelGraphicDevice;
import net.sf.sdedit.ui.Tab;
import net.sf.sdedit.ui.components.ZoomPane;
import net.sf.sdedit.ui.components.Zoomable;
import net.sf.sdedit.ui.components.buttons.ActionManager;
import net.sf.sdedit.ui.components.configuration.Bean;

@SuppressWarnings("serial")
public abstract class DiagramTab extends Tab implements PropertyChangeListener,
		Transferable, DiagramDataProviderFactory {

	final protected static DiagramRenderer renderer = new DiagramRenderer();

	public static final DataFlavor EMF_FLAVOR = new DataFlavor("image/emf",
			"Enhanced Meta File");
	
	static {
		try {
			SystemFlavorMap sfm = (SystemFlavorMap) SystemFlavorMap
					.getDefaultFlavorMap();
			sfm.addUnencodedNativeForFlavor(EMF_FLAVOR, "ENHMETAFILE");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ZoomPane zoomPane;

	private Bean<? extends Configuration> configuration;

	private DiagramInteraction interaction;

	private DiagramError error;

	private Diagram diagram;

	private boolean redraw;

	private boolean refreshOnActivate;
	
	private DataFlavor flavor;

	public DiagramTab(UserInterfaceImpl ui) {
		super(ui);
		this.zoomPane = new ZoomPane();
		refreshOnActivate = true;
		zoomPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (interaction != null && SwingUtilities.isRightMouseButton(e)) {
					JPopupMenu menu = createPopupMenu();
					if (menu != null && getDiagram() != null) {
						menu
								.show((Component) e.getSource(), e.getX(), e
										.getY());
						e.consume();
						return;
					}
				}
			}
		});
	}

	protected void setRefreshOnActivate(boolean on) {
		this.refreshOnActivate = on;
	}

	@Override
	public void activate(ActionManager actionManager,
			FileActionProvider faProvider) {
		super.activate(actionManager, faProvider);
		if (refreshOnActivate) {
			refresh(true);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (!evt.getPropertyName().equals("explicitReturns")) {
			refresh(false);
		}
	}

	public synchronized DiagramError getDiagramError() {
		return error;
	}

	public DiagramRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Clears the diagram view.
	 */
	public void clear() {
		// TODO
	}

	public final void setInteraction(DiagramInteraction interaction) {
		this.interaction = interaction;
	}

	protected DiagramInteraction getInteraction() {
		return interaction;
	}

	public void setConfiguration(Bean<? extends Configuration> configuration) {
		if (this.configuration != null) {
			this.configuration.removePropertyChangeListener(this);
		}
		this.configuration = configuration;
		this.configuration.addPropertyChangeListener(this);
	}

	public Bean<? extends Configuration> getConfiguration() {
		return configuration;
	}

	public Diagram getDiagram() {
		return diagram;
	}

	protected JPopupMenu createPopupMenu() {
		List<Action> actions = interaction.getContextActions();
		JPopupMenu menu = null;
		if (actions != null) {
			menu = new JPopupMenu();
			for (Action action : actions) {
				menu.add(action);
			}
		}
		return menu;
	}

	public ZoomPane getZoomPane() {
		return zoomPane;
	}

	public void refresh(boolean redraw) {
		this.redraw = redraw;
		renderer.renderDiagram(this);
	}
	
	protected PanelGraphicDevice graphic () {
        AbstractPaintDevice apd = (AbstractPaintDevice) diagram.getPaintDevice();
        PanelGraphicDevice pg = (PanelGraphicDevice) apd.getGraphicDevice();
        return pg;
	}

	/**
	 * This method is called by {@linkplain DiagramRenderer} when a diagram is
	 * rendered and ready, as requested by this <tt>DiagramTab</tt> via
	 * {@linkplain DiagramRenderer#renderDiagram(DiagramTab)}.
	 * 
	 * @param diagram
	 *            the diagram completely rendered
	 * @param error
	 *            an error that has occurred while rendering the diagram, if any
	 */
	public void displayDiagram(Diagram diagram, DiagramError error) {
	    this.diagram = diagram;
		if (redraw
				|| ConfigurationManager.getGlobalConfiguration().isAutoUpdate()) {
		    getZoomPane().setViewportView(graphic().getPanel());
		}
		this.error = error;
		handleDiagramError(error);
		get_UI().enableComponents();
	}

	protected abstract void handleDiagramError(DiagramError error);

	public void scrollToDrawable(Drawable drawable, boolean highlight) {
		if (highlight) {
			graphic().highlight(drawable);
			repaint();
		}
		getZoomPane().scrollToRectangle(drawable.getRectangle());
	}

	/**
	 * Scrolls the diagram view to the top-left corner.
	 */
	public void goHome() {
		invokeLater(new Runnable() {
			public void run() {
				getZoomPane().home();
			}
		});
	}

	public boolean isEmpty() {
		return diagram == null || diagram.getPaintDevice() == null
				|| diagram.getPaintDevice().isEmpty();
	}
	
	public boolean canZoom () {
		return !isEmpty();
	}


	@Override
	protected Zoomable<? extends JComponent> getZoomable() {
		Diagram diagram = getDiagram();
		if (diagram != null) {
			AbstractPaintDevice ppd = (AbstractPaintDevice) diagram.getPaintDevice();
			if (!ppd.isEmpty()) {
			    return ((PanelGraphicDevice) ppd.getGraphicDevice()).getPanel();
			}
		}
		return null;
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (!isDataFlavorSupported(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		if (flavor.equals(EMF_FLAVOR)) {
			return getTransferDataVector("emf");
		}
		return getTransferDataBitmap();
	}
	
	public abstract DiagramFactory createFactory (PaintDevice paintDevice);
	
	private InputStream getTransferDataVector (String format) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Exporter exporter = Exporter.getExporter(format, "Landscape", "A0", stream);
		PaintDevice paintDevice = createPaintDevice(exporter);
		DiagramFactory factory = createFactory(paintDevice);
		try {
			factory.generateDiagram(getConfiguration().getDataObject());
		} catch (DiagramError e) {
			/* ignored */
		}
		exporter.export();
		byte[] bytes = stream.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		return bais;
	}
	
	private Image getTransferDataBitmap () throws IOException {
		ImageGraphicsDevice ipd = new ImageGraphicsDevice(false);
		PaintDevice paintDevice = createPaintDevice(ipd);
		DiagramFactory factory = createFactory(paintDevice);
		try {
		    factory.generateDiagram(getConfiguration().getDataObject());
		} catch (DiagramError e) {
			/* ignored */
		}
		ipd.drawAll();
		return ipd.getImage();
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { flavor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return Arrays.asList(getTransferDataFlavors()).contains(flavor);
	}

	public void copyToClipboard(DataFlavor flavor) {
		this.flavor = flavor;
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(this, null);
	}

	public boolean canGoHome () {
		return true;
	}
	
	public abstract AbstractPaintDevice createPaintDevice (GraphicDevice graphicDevice);
	

	
}
