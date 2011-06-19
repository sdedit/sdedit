// Copyright (c) 2006 - 2011, Markus Strauch.
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
import net.sf.sdedit.diagram.DiagramDataProvider;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.error.FatalError;
import net.sf.sdedit.ui.PanelPaintDevice;
import net.sf.sdedit.util.Pair;

/**
 * A <tt>DiagramRenderer</tt> (typically a singleton instance) is responsible
 * for rendering diagrams belonging to {@linkplain DiagramTab}s on a separate
 * thread.
 * 
 * @author Markus Strauch
 * 
 */
public class DiagramRenderer implements Runnable {

	private LinkedList<Pair<DiagramTab, Diagram>> queue;

	public DiagramRenderer() {
		queue = new LinkedList<Pair<DiagramTab, Diagram>>();
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.setName("DiagramRenderer-Thread");
		thread.start();
	}

	private synchronized void enqueue(DiagramTab tab, Diagram diagram) {
		Pair<DiagramTab, Diagram> newPair = new Pair<DiagramTab, Diagram>(tab,
				diagram);
		ListIterator<Pair<DiagramTab, Diagram>> iter = queue.listIterator();
		while (iter.hasNext()) {
			Pair<DiagramTab, Diagram> pair = iter.next();
			if (pair.getFirst() == newPair.getFirst()) {
				iter.remove();
				break;
			}
		}
		queue.addFirst(newPair);
		notify();
	}

	public void renderDiagram(DiagramTab tab) {
		DiagramDataProvider provider = tab.getProvider();
		PanelPaintDevice ppd = new PanelPaintDevice(true);
		if (tab.getInteraction() != null) {
			ppd.setPartner(tab.getInteraction());
		}
		Diagram diagram = new Diagram(tab.getConfiguration().getDataObject(),
				provider, ppd);
		enqueue(tab, diagram);
	}

	public void run() {
		while (true) {
			Pair<DiagramTab, Diagram> pair;
			DiagramError err = null;
			synchronized (this) {
				while (queue.isEmpty()) {
					try {
						wait();
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
					}
				}
				pair = queue.removeLast();
			}
			Diagram diagram = pair.getSecond();
			try {
				diagram.generate();
			} catch (RuntimeException e) {
				e.printStackTrace();
				err = new FatalError(diagram.getDataProvider(), e);
			} catch (DiagramError e) {
				err = e;
			}
			doDisplay(pair.getFirst(), diagram, err);
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
