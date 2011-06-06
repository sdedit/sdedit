package net.sf.sdedit.diagram.dev;

import net.sf.sdedit.diagram.MessageData;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.error.SyntaxError;

public class MessageProcessor extends SequenceProcessor<MessageData> {

	protected MessageProcessor(SequenceProcessorChain chain) {
		super(chain);
	}

	@Override
	protected Class<MessageData> getElementClass() {
		return MessageData.class;
	}

	@Override
	public void processElement() throws SemanticError,
			SyntaxError {
		MessageData messageData = getElement();
	}

}
