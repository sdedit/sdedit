package net.sf.sdedit.diagram;

import java.util.List;
import java.util.Map;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.text.TextHandler;

public class SequenceDiagramFactory implements DiagramFactory,
        DiagramDataProviderFactory {

    private String text;

    private IPaintDevice paintDevice;

    private DiagramDataProviderFactory providerFactory;

    private Diagram diagram;

    private DiagramDataProvider provider;

    public SequenceDiagramFactory(DiagramDataProviderFactory providerFactory,
            IPaintDevice paintDevice) {
        this.providerFactory = providerFactory;
        this.paintDevice = paintDevice;
    }

    public SequenceDiagramFactory(String text, IPaintDevice paintDevice) {
        this.paintDevice = paintDevice;
        this.providerFactory = this;
        this.text = text;
    }

    protected SequenceDiagram newDiagram(Configuration configuration,
            DiagramDataProvider provider, IPaintDevice paintDevice) {
        return new SequenceDiagram(configuration,
                (SequenceDiagramDataProvider) provider, paintDevice);
    }

    public DiagramDataProvider createProvider() {
        return new TextHandler(text);
    }

    public void generateDiagram(Configuration configuration)
            throws DiagramError {
        Map<Integer, List<String>> map = null;
        if (configuration.isReuseSpace()) {
            provider = providerFactory.createProvider();
            SequenceDiagram _diagram = newDiagram(configuration, provider,
                    new NullPaintDevice());
            try {
                _diagram.generate();
            } catch (DiagramError ignored) {

            }
            map = _diagram.makeReverseIdMap();
        }
        provider = providerFactory.createProvider();
        diagram = newDiagram(configuration, provider, paintDevice);
        if (map != null) {
            ((SequenceDiagram) getDiagram()).setReverseIdMap(map);
        }
        diagram.generate();
    }

    public DiagramDataProviderFactory getProviderFactory() {
        return providerFactory;
    }

    public DiagramDataProvider getProvider() {
        return provider;
    }

    public IPaintDevice getPaintDevice() {
        return paintDevice;
    }

    public Diagram getDiagram() {
        return diagram;
    }

}
