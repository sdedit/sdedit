package net.sf.sdedit.diagram;

public interface DiagramDataProvider {
	
    /**
     * Gets the current state of the provider. The state depends on how far it
     * has advanced when reading the data.
     * 
     * @return the current state of the provider or <tt>null</tt> if the
     *         provider does not provide information about its state
     */
    public Object getState();

}
