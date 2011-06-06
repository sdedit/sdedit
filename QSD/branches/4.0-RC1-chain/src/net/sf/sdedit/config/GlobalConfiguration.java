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

package net.sf.sdedit.config;

import java.awt.Font;

import javax.swing.KeyStroke;

import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.DataObject;

public interface GlobalConfiguration extends DataObject {

	public int getAutodrawLatency();

	public Font getEditorFont();
	
	public String getFileEncoding();

	public int getGlueChangeAmount();
	
	public Font getGuiFont();

	public String getLookAndFeel();

	public int getMaxNumOfRecentFiles();

	public int getRealtimeServerPort();

	public String getRecentFiles();
	
	public boolean isAutoScroll();

	public boolean isAutostartServer();
	
	public boolean isAutoUpdate();

	public boolean isBackupFiles();
	
	public KeyStroke getCopyKeyStroke();
	
	public KeyStroke getCutKeyStroke();
	
	public KeyStroke getPasteKeyStroke();
	
	public KeyStroke getPreviousTabKeyStroke();
	
	public KeyStroke getNextTabKeyStroke();
	
	public KeyStroke getAcceptHintKeyStroke();
	
	public String getUnusedDecisions();
	
	public int getTooltipDismissDelay();
	
	public boolean isHighlightCurrent();
	
	@Adjustable(dflt = 2, min = 1, max = 999, category = "Automation", info = "Redraw/syntax check delay (20 ms)")
	public void setAutodrawLatency(int autodrawLatency);
	
	@Adjustable(dflt = 1, min = 0, max = 1, category = "Automation", info = "Scroll diagram as you type")
	public void setAutoScroll(boolean autoScroll);
	
	@Adjustable(info = "Autostart RT diagram server", category = "Server")
	public void setAutostartServer(boolean autostartServer);

	@Adjustable(info = "Update diagram as you type", category = "Automation")
	public void setAutoUpdate(boolean autoUpdate);
	
	@Adjustable(info = "Backup files", category = "Files")
	public void setBackupFiles(boolean backupFiles);
	
	@Adjustable(category = "Fonts", info = "Editor font")
	public void setEditorFont(Font editorFont);
	
	@Adjustable(category = "Files", info = "File encoding", stringSelectionProvided = true)
	public void setFileEncoding(String fileEncoding);

	@Adjustable(dflt = 5, min = 1, max = 30, info = "Glue change amount", category = "Misc")
	public void setGlueChangeAmount(int glueChangeAmount);
	
	@Adjustable(category = "Fonts", info = "GUI font")
	public void setGuiFont(Font guiFont);

	@Adjustable(category = "Look & Feel", info = "Look & Feel (requires restart)", stringSelectionProvided = true)	
	public void setLookAndFeel(String lookAndFeel);
	
	@Adjustable(dflt = 6, min = 0, max = 25, category = "Files", info = "Max. number of recent files")
	public void setMaxNumOfRecentFiles(int maxNumOfRecentFiles);

	@Adjustable(dflt = 60001, min = 1, max = 65535, editable = false, category = "Server", info = "Receiver server port number")
	public void setRealtimeServerPort(int receiverServerPort);

	@Adjustable(category = "Files", info = "Recent files", editable = false)
	public void setRecentFiles(String recentFiles);
	
	@Adjustable(category="Key bindings", info="Cut", key="1")
	public void setCutKeyStroke(KeyStroke keyStroke);
	
	@Adjustable(category="Key bindings", info="Copy", key="2")
	public void setCopyKeyStroke(KeyStroke keyStroke);
	
	@Adjustable(category="Key bindings", info="Paste", key="3")
	public void setPasteKeyStroke(KeyStroke keyStroke);
	
	@Adjustable(category="Key bindings", info="Previous tab", key="4")
	public void setPreviousTabKeyStroke(KeyStroke keyStroke);
	
	@Adjustable(category="Key bindings", info="Next tab", key="5")
	public void setNextTabKeyStroke(KeyStroke keyStroke);
	
	@Adjustable(category="Key bindings", info="Accept hint", key="6")
	public void setAcceptHintKeyStroke(KeyStroke keyStroke);
	
	@Adjustable(category="Invisible", editable=false, info="Unused decisions")
	public void setUnusedDecisions(String unused);
	
	@Adjustable(category="Misc", editable=true, min=1,max=100, info="Time (in seconds) before a tooltip disappears")
	public void setTooltipDismissDelay(int seconds);
	
	@Adjustable(category="Misc", editable=true, info="Highlight current message")
	public void setHighlightCurrent(boolean on);

}
//{{core}}
