package net.sf.sdedit.error;

import net.sf.sdedit.diagram.DiagramDataProvider;

public class DiagramError extends Exception {
    
    private static final long serialVersionUID = -1955161654338606676L;
    
    private DiagramDataProvider provider;
    
    public DiagramError (String message){
        super(message);
    }

    public void setProvider(DiagramDataProvider provider) {
        this.provider = provider;
    }

    public DiagramDataProvider getProvider() {
        return provider;
    }

}
