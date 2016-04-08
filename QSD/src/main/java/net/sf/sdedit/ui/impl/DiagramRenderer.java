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

import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.SwingUtilities;

import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.DiagramFactory;
import net.sf.sdedit.diagram.PaintDevice;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.error.FatalError;

/**
 * A <tt>DiagramRenderer</tt> (typically a singleton instance) is responsible
 * for rendering diagrams belonging to {@linkplain DiagramTab}s on a separate
 * thread.
 * 
 * @author Markus Strauch
 * 
 */
public class DiagramRenderer implements Runnable {

	private LinkedList<DiagramFactory> queue;

	public DiagramRenderer() {
		queue = new LinkedList<DiagramFactory>();
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.setName("DiagramRenderer-Thread");
		thread.start();
	}

	private synchronized void enqueue(DiagramFactory factory) {
		ListIterator<DiagramFactory> iter = queue.listIterator();
		while (iter.hasNext()) {
			DiagramFactory theFactory = iter.next();
			if (theFactory.getProvider() == factory.getProvider()) {
				iter.remove();
				break;
			}
		}
		queue.addFirst(factory);
		notify();
	}

	public void renderDiagram(DiagramTab tab) {
        PaintDevice ppd = tab.createPaintDevice(null);
        DiagramFactory factory = tab.createFactory(ppd);
	    enqueue(factory);
	}

	public void run() {
		while (true) {
			DiagramFactory factory;
			DiagramError err = null;
			synchronized (this) {
				while (queue.isEmpty()) {
					try {
						wait();
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
					}
				}
				factory = queue.removeLast();
			}
			DiagramTab tab = (DiagramTab) factory.getProviderFactory();
			try {
				factory.generateDiagram(tab.getConfiguration().getDataObject());
			} catch (RuntimeException e) {
				e.printStackTrace();
				err = new FatalError(factory.getProvider(), e);
			} catch (DiagramError e) {
				err = e;
			}
			doDisplay(tab, factory.getDiagram(), err);
		}
	}

	private void doDisplay(final DiagramTab tab, final Diagram diagram,
			final DiagramError err) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tab.displayDiagram(diagram, err);
			}
		});
	}

}
