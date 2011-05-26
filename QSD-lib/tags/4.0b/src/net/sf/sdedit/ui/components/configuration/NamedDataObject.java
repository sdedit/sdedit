package net.sf.sdedit.ui.components.configuration;

public interface NamedDataObject extends DataObject {
	
	public String getName ();
	
    @Adjustable(editable=true,info="Name",category="Undefined")
	public void setName (String name);
	

}
