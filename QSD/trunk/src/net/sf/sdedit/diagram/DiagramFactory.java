package net.sf.sdedit.diagram;

import java.util.List;
import java.util.Map;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.text.TextHandler;

public class DiagramFactory implements DiagramDataProviderFactory {

    private DiagramDataProvider provider;

    private final DiagramDataProviderFactory providerFactory;
    
    private final String text;

    private final IPaintDevice paintDevice;
    
    private Diagram diagram;

    public DiagramFactory(DiagramDataProviderFactory providerFactory, IPaintDevice paintDevice) {
        this.providerFactory = providerFactory;
        this.paintDevice = paintDevice;
        text = null;
    }
    
    public DiagramFactory(String text, IPaintDevice paintDevice) {
        this.text = text;
        this.paintDevice = paintDevice;
        this.providerFactory = this;
    }

    public DiagramDataProviderFactory getProviderFactory() {
        return providerFactory;
    }

    public void generateDiagram(Configuration configuration) throws DiagramError {
        Map<Integer, List<String>> map = null;
        if (configuration.isReuseSpace()) {
            provider = providerFactory.createProvider();
            Diagram _diagram = new Diagram(configuration, provider,
                    new NullPaintDevice());
            try {
                _diagram.generate();
            } catch (DiagramError ignored) {

            }
            map = _diagram.makeReverseIdMap();
        }
        provider = providerFactory.createProvider();
        diagram = new Diagram(configuration, provider, paintDevice);
        if (map != null) {
            diagram.setReverseIdMap(map);
        }
        diagram.generate();
    }

    public DiagramDataProvider getProvider() {
        return provider;
    }
    
    public DiagramDataProvider createProvider () {
        return new TextHandler(text);
    }
    
    public IPaintDevice getPaintDevice() {
        return paintDevice;
    }
    
    public Diagram getDiagram () {
        return diagram;
    }

}
