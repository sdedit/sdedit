package net.sf.sdedit.config;

import java.io.File;

import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.DataObject;

public interface ExportConfiguration extends DataObject {

	@Adjustable(editable = true, info = "Type", category = "File", key = "1", choices = {  "bmp", "emf",
			 "gif", "jpg","png", "svg"}, isComboBoxEditable = false)
	public void setType(String type);

	public String getType();

	public File getFile();

	@Adjustable(editable = true, info = "Name", category = "File", key = "2")
	public void setFile(File file);

}
