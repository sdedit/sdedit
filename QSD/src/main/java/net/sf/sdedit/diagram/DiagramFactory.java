package net.sf.sdedit.diagram;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.error.DiagramError;

public interface DiagramFactory {

    public abstract DiagramDataProviderFactory getProviderFactory();

    public void generateDiagram(Configuration configuration)
            throws DiagramError;

    public abstract DiagramDataProvider getProvider();

    public IPaintDevice getPaintDevice();

    public Diagram getDiagram();

}
