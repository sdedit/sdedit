package net.sf.sdedit.message;

import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.diagram.MessageData;

public class NullMessage extends ForwardMessage {

	public NullMessage(Lifeline receiver, Diagram diagram, MessageData data) {
		super(null, receiver, diagram, data);
	}

	@Override
	public String getText() {
		return "";
	}

	@Override
	public void updateView() {
		extendLifelines(getConfiguration().getSpaceBeforeActivation());
		getDiagram().setCallerThread(getCallee().getThread());
		getDiagram().setFirstCaller(getCallee());
		getCallee().setActive(true);
	}
	
	@Override
	public int getThread () {
		return -1;
	}
	
	@Override
	public Answer getAnswerMessage () {
		return null;
	}

}
