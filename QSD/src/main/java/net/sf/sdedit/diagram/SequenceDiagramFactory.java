//Copyright (c) 2006 - 2015, Markus Strauch.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.
package net.sf.sdedit.diagram;

import java.util.List;
import java.util.Map;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.SequenceConfiguration;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.text.TextHandler;

public class SequenceDiagramFactory implements DiagramFactory,
        DiagramDataProviderFactory {

    private String text;

    private PaintDevice paintDevice;

    private DiagramDataProviderFactory providerFactory;

    private Diagram diagram;

    private DiagramDataProvider provider;

    public SequenceDiagramFactory(DiagramDataProviderFactory providerFactory,
            PaintDevice paintDevice) {
        this.providerFactory = providerFactory;
        this.paintDevice = paintDevice;
    }

    public SequenceDiagramFactory(String text, PaintDevice paintDevice) {
        this.paintDevice = paintDevice;
        this.providerFactory = this;
        this.text = text;
    }

    protected SequenceDiagram newDiagram(SequenceConfiguration configuration,
            DiagramDataProvider provider, PaintDevice paintDevice) {
        return new SequenceDiagram(configuration,
                (SequenceDiagramDataProvider) provider, paintDevice);
    }

    public DiagramDataProvider createProvider() {
        return new TextHandler(text);
    }

    public void generateDiagram(Configuration conf)
            throws DiagramError {
    	SequenceConfiguration configuration = conf.cast(SequenceConfiguration.class);
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

    public PaintDevice getPaintDevice() {
        return paintDevice;
    }

    public Diagram getDiagram() {
        return diagram;
    }

}
