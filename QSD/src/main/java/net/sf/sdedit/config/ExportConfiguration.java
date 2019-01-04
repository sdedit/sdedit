package net.sf.sdedit.config;

import java.io.File;

import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.DataObject;

public interface ExportConfiguration extends DataObject {

	@Adjustable(editable = true, info = "File type", category = "Export", key = "1", choices = { "ps", "pdf", "emf",
			"svg", "png", "gif", "jpg", "bmp" })
	public void setType(String type);

	public String getType();

	public String getFormat();

	@Adjustable(editable = true, info = "Page format", category = "Export", key = "2", choices = { "A0", "A1", "A2",
			"A3", "A4", "A5", "A6" })
	public void setFormat(String format);

	public String getOrientation();
	
	@Adjustable(editable = true, info = "Orientation", category = "Export", key = "3", choices = { "Portrait", "Landscape"})
	public void setOrientation(String orientation);

	public File getFile();

	@Adjustable(editable = true, info = "File", category = "Export", key = "4")
	public void setFile(File file);

}
