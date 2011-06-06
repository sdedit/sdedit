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

import java.io.File;

import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.DataObject;

public interface PrintConfiguration extends DataObject {
	
	public static final String EXPORT = "Export file";
	
	public static final String EXPORT_AND_PRINT = "Export and print";
	
	public static final String PIPE = "Pipe to printer";

	public String getAction();

	@Adjustable(editable = true, info = "Action", category = "Print/Export", choices = {
			EXPORT, EXPORT_AND_PRINT, PIPE }, key = "0", forceComboBox = true, gap=2)
	public void setAction(String command);

	public File getExportFile();

	@Adjustable(editable = true, info = "File to export", category = "Print/Export", depends = "action!=" + PIPE, key = "1",
			tooltip = "The name of the file to be exported")
	public void setExportFile(File exportFile);
	
	public boolean isEraseExportFile();
	
	@Adjustable(editable = true, info = "Erase file when printed", category = "Print/Export", key = "11", depends="action=" +
			EXPORT_AND_PRINT)
	public void setEraseExportFile(boolean eraseExportFile);

	public String getCommand();

	@Adjustable(editable = true, info = "Printer", category = "Print/Export", depends = "action!=" + EXPORT, key = "2",
			tooltip = "The command for printing the file", gap=2)
	public void setCommand(String command);

	public String getFormat();

	@Adjustable(editable = true, info = "Page format", category = "Print/Export", key = "3", choices = {
			"A0", "A1", "A2", "A3", "A4", "A5", "A6" })
	public void setFormat(String format);

	public String getOrientation();

	@Adjustable(editable = true, info = "Orientation", category = "Print/Export", key = "4", choices = {
			"Portrait", "Landscape" })
	public void setOrientation(String orientation);

	public boolean isMultipage();

	@Adjustable(editable = true, info = "Multiple pages", category = "Print/Export", key = "5")
	public void setMultipage(boolean multipage);

	public boolean isRepeatHeads();

	@Adjustable(editable = false, info = "Repeat lifeline heads", category = "Print/Export", key = "6", depends = "multipage=true")
	public void setRepeatHeads(boolean repeatHeads);

	public boolean isCenterVertically();

	@Adjustable(editable = true, info = "Center vertically", category = "Print/Export", key = "7", depends="multipage=false")
	public void setCenterVertically(boolean centerVertically);

	public boolean isCenterHorizontally();

	@Adjustable(editable = true, info = "Center horizontally", category = "Print/Export", key = "8")
	public void setCenterHorizontally(boolean centerHorizontally);

	public boolean isFitToPage();

	@Adjustable(editable = true, info = "Fit to page", category = "Print/Export", key = "9", depends="multipage=false")
	public void setFitToPage(boolean fitToPage);
}
//{{core}}
