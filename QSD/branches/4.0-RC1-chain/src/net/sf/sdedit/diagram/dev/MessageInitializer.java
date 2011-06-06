package net.sf.sdedit.diagram.dev;

import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.diagram.MessageData;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.error.SyntaxError;

public class MessageInitializer extends SequenceProcessor<MessageData> {

	protected MessageInitializer(SequenceProcessorChain chain) {
		super(chain);
	}

	@Override
	protected Class<MessageData> getElementClass() {
		return MessageData.class;
	}

	@Override
	public <E extends Exception> void processElement() throws SemanticError,
			SyntaxError {
		MessageData data = getElement();
		Lifeline rootCaller;
		Lifeline rootCallee;
		if (data.getCaller().equals("$")) {
			rootCaller = null;
		} else {
			rootCaller = getDiagram().getLifeline(data.getCaller());
			if (rootCaller == null) {
				objectNotFound(data.getCaller());
			}
		}
		if (!"".equals(data.getCallee())) {
			rootCallee = getDiagram().getLifeline(data.getCallee());
			if (rootCallee == null) {
				objectNotFound(data.getCallee());
			}
		} else {
			rootCallee = null;
		}
		getState().setRootCaller(rootCaller);
		getState().setRootCallee(rootCallee);
	}

}
