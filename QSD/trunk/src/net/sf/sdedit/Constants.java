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
package net.sf.sdedit;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.io.File;

import net.sf.sdedit.util.OS;

/**
 * 
 * 
 * @author Markus Strauch
 */
public interface Constants
{
    /*
     * Strokes
     */
    
    /*
    public static final Stroke dashed = new BasicStroke(1,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, new float[] { 5,
                    5 }, 0f);

    public static final Stroke dotted = new BasicStroke(1,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, new float[] { 1,
                    2 }, 0f);

    public static final Stroke emptyStroke = new BasicStroke(1,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, new float[] { 0,
                    1 }, 0f);

    public static final Stroke solid = new BasicStroke(1, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_ROUND, 1f, null, 0);

    public static final Stroke thick = new BasicStroke(2, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_ROUND, 1f, null, 0);
    
    public static final Stroke thick_dashed = new BasicStroke(2,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, new float[] { 5,
                    5 }, 0f);
    */

    /*
     * Method names for using FreeHEP ExportDialog class via reflection (in
     * Actions)
     */
    
    public static final String TEST_FREEHEP_CLASSNAME = "org.freehep.util.export.ExportFileType";
    
    public static final String SAVE_AS_FILE_PROPERTY = "org.freehep.util.export.ExportDialog.SaveAsFile";
    
    public static final String SAVE_AS_TYPE_PROPERTY = "org.freehep.util.export.ExportDialog.SaveAsType";
    
    /*
     * Miscellaneous
     */
    
    public static final Cursor HAND_CURSOR = new Cursor (Cursor.HAND_CURSOR);
    
    public static final Cursor DEFAULT_CURSOR = new Cursor (Cursor.DEFAULT_CURSOR);
    
    public static final Cursor MOVE_CURSOR = new Cursor (Cursor.MOVE_CURSOR);
    
    
    public final static RenderingHints ANTI_ALIAS = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    public static final File GLOBAL_CONF_FILE = new File(
    		OS.getUserDirectory(), ".sdedit.conf");
    
    public final static String DEFAULT_CODE_FONT = "Monospace";
    
    public final static String DEFAULT_ENCODING_SCHEME = System.getProperty("file.encoding");
}
//{{core}}
