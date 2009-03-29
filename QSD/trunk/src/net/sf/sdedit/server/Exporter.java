// Copyright (c) 2006 - 2008, Markus Strauch.
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

package net.sf.sdedit.server;

import java.io.OutputStream;
import java.lang.reflect.Constructor;

import net.sf.sdedit.Constants;
import net.sf.sdedit.ui.Graphics2DPaintDevice;

/**
 * An Exporter is a paint device that can redirect its output to an output
 * stream, using one of various output formats.
 * 
 * @author Markus Strauch
 */
public abstract class Exporter extends Graphics2DPaintDevice {
	
	private static final boolean exportAvailable;

	static {
		boolean avail;
		try {
			Class.forName(Constants.TEST_FREEHEP_CLASSNAME);
			avail = true;
		} catch (RuntimeException re) {
			throw re;
		} catch (ClassNotFoundException e) {
			avail = false;
		} catch (Throwable t) {
			t.printStackTrace();
			avail = false;
		}
		exportAvailable = avail;
	}
	
	public static boolean isAvailable () {
		return exportAvailable;
	}
	
    /**
     * Returns an Exporter object if the exporting library is available,
     * otherwise <tt>null</tt>. The following output types are supported:
     * 
     * ps, pdf, swf, emf, svg, png, gif, jpg, bmp
     * 
     * @param type
     *            describes the output format
     * @param orientation
     *            one of {Portrait,Landscape}
     * @param stream
     *            the stream to redirect the output to
     * @return an instance of an Exporter
     */
    public static Exporter getExporter(String type, String orientation, String format, OutputStream stream) {
        Exporter exporter;

        try {
            // This fails if the export library is not on the class path
            // because ExporterImpl contains symbols that cannot be resolved
            // then
            String exporterClassName = Exporter.class.getName() + "Impl";
            Class<?> exporterClass = Class.forName(exporterClassName);
            Constructor<?> constructor = exporterClass.getConstructor(
                    String.class, String.class);
            exporter = (Exporter) constructor.newInstance(
                    orientation, format);

        } catch (Throwable e) {
            return null;
        }
        exporter.setType(type);
        exporter.setOutputStream(stream);
        return exporter;
    }

    protected Exporter() {
        super();
    }

    protected abstract void setType(String type);

    protected abstract void setOutputStream(OutputStream stream);

    /**
     * This method should be called when the generation of the diagram has
     * finished and {@linkplain #computeBounds()} has been called. It redirects
     * the output to the stream set via
     * {@linkplain #setOutputStream(OutputStream)}.
     */
    public abstract void export();
}
