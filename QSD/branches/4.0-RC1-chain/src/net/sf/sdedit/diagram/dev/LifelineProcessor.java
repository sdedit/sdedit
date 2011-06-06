package net.sf.sdedit.diagram.dev;

import java.util.HashMap;
import java.util.Map;

import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.error.SyntaxError;

public class LifelineProcessor extends SequenceProcessor<Lifeline>{
    
    private final Map<String, Lifeline> lifelineMap;

    protected LifelineProcessor(SequenceProcessorChain chain) {
        super(chain);
        lifelineMap = new HashMap<String,Lifeline>();
    }

    @Override
    protected Class<Lifeline> getElementClass() {
        return Lifeline.class;
    }

    @Override
    public void processElement() throws SyntaxError, SemanticError {
        Lifeline lifeline = getElement();
        getChain().addDrawable(lifeline.getHead());
        if (lifelineMap.containsKey(lifeline.getName())) {
            syntaxError(lifeline.getName() + " already exists.");
        }
        if (!getDiagram().isThreaded() && lifeline.hasThread()) {
            semanticError(lifeline.getName() + " cannot have its own thread when multithreading is not enabled");
        }
        getDiagram().addLifeline(lifeline);
        
    }

}
