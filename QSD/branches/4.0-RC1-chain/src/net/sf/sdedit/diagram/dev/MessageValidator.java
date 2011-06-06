package net.sf.sdedit.diagram.dev;

import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.diagram.MessageData;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.error.SyntaxError;

public class MessageValidator extends SequenceProcessor<MessageData> {

	protected MessageValidator(SequenceProcessorChain chain) {
		super(chain);
	}

	@Override
	protected Class<MessageData> getElementClass() {
		return MessageData.class;
	}

	@Override
	public <E extends Exception> void processElement() throws SemanticError,
			SyntaxError {
		State state = getState();
		Lifeline rootCallee = state.getRootCallee();
		MessageData data = getElement();
		Diagram diagram = getDiagram();

		if (state.callerIsActor() && data.getCaller().equals(data.getCallee())) {
			semanticError("an actor cannot send a message to itself");
		}
		if (state.callerIsActor() && data.getAnswer().length() > 0) {
			semanticError("An actor cannot receive an answer automatically. "
					+ "This should be done by means of an explicit message!");
		}
		if (state.calleeIsActor() && data.getAnswer().length() > 0) {
			semanticError("There are no automatic answers to messages that reach actors."
					+ "<br>This should be done by means of an explicit message!");
		}
		if (diagram.isThreaded() && data.isSpawnMessage()
				&& data.getAnswer().length() > 0) {
			semanticError("There are no automatic answers to messages that spawn threads."
							+ "<br>This should be done by means of an explicit message!");
		}
		if (rootCallee != null) {
			if (!rootCallee.isAlive() && !data.isNewMessage()) {
				semanticError(data.getMessage() + ": "
						+ data.getCallee() + " must be created first");
			}
			if (rootCallee.isAlive() && data.isNewMessage()) {
				semanticError(data.getMessage() + ": "
						+ data.getCallee() + " has already been created");
			}
		}
		if (diagram.isThreaded() && state.callerIsActor() && data.isSpawnMessage()) {
			semanticError("Actor messages are spawning by default in threaded mode");

		}
		if (diagram.isThreaded() && state.calleeIsActor()
				&& data.getBroadcastType() == 0 && data.isSpawnMessage()) {
			semanticError("Messages sent to actors must not be spawning");

		}

		if (diagram.isThreaded()) {
			if (rootCallee != null && rootCallee.isActiveObject()) {
				semanticError("Active objects are not permitted when multithreading is not enabled.");
			}
			if (data.isSpawnMessage()) {
				semanticError("Threads cannot be spawned when multithreading is not enabled.");
			}
			if (data.returnsInstantly()) {
				semanticError("Instant return must not be specified when multithreading is not enabled.");
			}
			if (data.getThread() != -1) {
				semanticError("A thread number must not be specified when multithreading is not enabled.");
			}
			if (data.getBroadcastType() != 0) {
				semanticError("Broadcast messages can only be sent when multithreading is enabled");
			}
		}

	}

	private void noThreadingChecks() throws SemanticError {

	}

	private void checkSemantics() throws SemanticError {

	}

}
