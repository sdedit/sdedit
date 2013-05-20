package net.sf.sdedit.error;

import net.sf.sdedit.diagram.DiagramDataProvider;

public class DiagramError extends Exception {
    
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
