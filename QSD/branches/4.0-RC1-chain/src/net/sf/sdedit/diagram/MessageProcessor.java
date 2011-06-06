// Copyright (c) 2006 - 2011, Markus Strauch.
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// * Redistributions of source code must retain the above copyright notice, 
// this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
// this list of conditions and the following disclaimer in the documentation 
// and/or other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
// THE POSSIBILITY OF SUCH DAMAGE.

package net.sf.sdedit.diagram;

import java.util.LinkedList;

import net.sf.sdedit.error.ObjectNotFound;
import net.sf.sdedit.error.SemanticError;

import net.sf.sdedit.message.Answer;
import net.sf.sdedit.message.BroadcastMessage;
import net.sf.sdedit.message.ConstructorMessage;
import net.sf.sdedit.message.ForwardMessage;
import net.sf.sdedit.message.Message;
import net.sf.sdedit.message.MessageToSelf;
import net.sf.sdedit.message.NullMessage;
import net.sf.sdedit.message.Primitive;

/**
 * This class implements most of the abstract sequence generation logic,
 * abstract in the sense that here appropriate caller and callee lifelines are
 * computed and answers are sent back, but nothing is known about graphical
 * drawing.
 * 
 * @author Markus Strauch
 * 
 */
final class MessageProcessor {

	private final Diagram diagram;

	private final DiagramDataProvider provider;

	/*
	 * The following attributes are valid during one single call of
	 * processMessage(...).
	 */

	private MessageData data;

	/**
	 * The root lifeline of the caller. Is invariantly non-null.
	 */
	private Lifeline rootCaller;

	/**
	 * The root lifeline of the callee. Is null iff the message is a primitive.
	 */
	private Lifeline rootCallee;

	/**
	 * The number of the thread where the message is sent.
	 * 
	 * <ul>
	 * <li>When no threading is used, it is invariantly 0.</li>
	 * <li>Ordinary messages are sent on a certain thread &gt;=0 where the
	 * caller must be active.</li>
	 * <li>Spawning messages, whether sent by an actor or by an object,
	 * including messages to active objects, are sent on the newly spawned
	 * thread.</li>
	 * <li>Messages exchanged between actors are sent on the special thread -1.</li>
	 * </ul>
	 */
	private int callerThread = 0;

	private int calleeThread = 0;

	private Lifeline caller;

	private Lifeline callee;

	private Answer answer;

	private boolean requireReturn;

	private boolean calleeIsActiveObject() {
		return rootCallee != null && rootCallee.isActiveObject();
	}

	private boolean callerIsActor() {
		return rootCaller != null && rootCaller.isAlwaysActive();
	}

	private boolean calleeIsActor() {
		return rootCallee != null && rootCallee.isAlwaysActive();
	}

	private boolean isPrimitiveMessage() {
		return rootCallee == null;
	}

	MessageProcessor(final Diagram diagram) {
		this.diagram = diagram;
		this.provider = diagram.getDataProvider();
		requireReturn = diagram.getConfiguration().isExplicitReturns();
	}

	ForwardMessage processMessage(final MessageData theData,
			final Lifeline precomputedCaller) throws SemanticError {
		this.data = theData;
		initMessage();
		theData.getBroadcastType();

		return _processMessage(theData.getBroadcastType() < 2,
				precomputedCaller);
	}

	Lifeline processReturn(final MessageData ret) throws SemanticError {
		this.data = ret;
		initMessage();
		if (diagram.isThreaded()) {
			findThreadNumbers();
			diagram.setCallerThread(callerThread);
		}
		return findCaller(true);
	}

	private ForwardMessage _processMessage(boolean openFragments,
			Lifeline precomputedCaller) throws SemanticError {
		checkSemantics();
		if (diagram.isThreaded()) {
			findThreadNumbers();
			diagram.setCallerThread(callerThread);
		} else {
			noThreadingChecks();
		}

		if (precomputedCaller != null) {
			caller = precomputedCaller;
		} else {
			caller = findCaller(false);
		}

		if (!isPrimitiveMessage()) {
			callee = findCallee();
		} else {
			callee = null;
		}
		ForwardMessage message = getMessage();

		// fixes bug 2019720
		if (openFragments) {
			diagram.getFragmentManager().openFragments();
		}

		// execute(message);

		return message;
	}

	private void noThreadingChecks() throws SemanticError {
		if (rootCallee != null && rootCallee.isActiveObject()) {
			throw new SemanticError(provider,
					"Active objects are not permitted when multithreading is not enabled.");
		}
		if (data.isSpawnMessage()) {
			throw new SemanticError(provider,
					"Threads cannot be spawned when multithreading is not enabled.");
		}
		if (data.returnsInstantly()) {
			throw new SemanticError(provider,
					"Instant return must not be specified when multithreading is not enabled.");
		}
		if (data.getThread() != -1) {
			throw new SemanticError(provider,
					"A thread number must not be specified when multithreading is not enabled.");
		}
		if (data.getBroadcastType() != 0) {
			throw new SemanticError(provider,
					"Broadcast messages can only be sent when multithreading is enabled");
		}
	}

	private void checkSemantics() throws SemanticError {
		if (callerIsActor() && data.getCaller().equals(data.getCallee())) {
			throw new SemanticError(provider,
					"an actor cannot send a message to itself");
		}
		if (callerIsActor() && data.getAnswer().length() > 0) {
			throw new SemanticError(
					provider,
					"An actor cannot receive an answer automatically. "
							+ "This should be done by means of an explicit message!");
		}
		if (calleeIsActor() && data.getAnswer().length() > 0) {
			throw new SemanticError(
					provider,
					"There are no automatic answers to messages that reach actors."
							+ "<br>This should be done by means of an explicit message!");
		}
		if (diagram.isThreaded() && data.isSpawnMessage()
				&& data.getAnswer().length() > 0) {
			throw new SemanticError(
					provider,
					"There are no automatic answers to messages that spawn threads."
							+ "<br>This should be done by means of an explicit message!");
		}
		if (rootCallee != null) {
			if (!rootCallee.isAlive() && !data.isNewMessage()) {
				throw new SemanticError(provider, data.getMessage() + ": "
						+ data.getCallee() + " must be created first");
			}
			if (rootCallee.isAlive() && data.isNewMessage()) {
				throw new SemanticError(provider, data.getMessage() + ": "
						+ data.getCallee() + " has already been created");
			}
		}
		if (diagram.isThreaded() && callerIsActor() && data.isSpawnMessage()) {
			throw new SemanticError(provider,
					"Actor messages are spawning by default in threaded mode");

		}
		if (diagram.isThreaded() && calleeIsActor()
				&& data.getBroadcastType() == 0 && data.isSpawnMessage()) {
			throw new SemanticError(provider,
					"Messages sent to actors must not be spawning");

		}
	}

	private void initMessage() throws SemanticError {
		if (data.getCaller().equals("$")) {
			rootCaller = null;
		} else {
			rootCaller = diagram.getLifeline(data.getCaller());
			if (rootCaller == null) {
				throw new ObjectNotFound(provider, data.getCaller());
			}
		}
		if (!data.getCallee().equals("")) {
			rootCallee = diagram.getLifeline(data.getCallee());
			if (rootCallee == null) {
				throw new ObjectNotFound(provider, data.getCallee());

			}
		} else {
			rootCallee = null;
		}
	}

	/**
	 * 
	 * @pre threading is used
	 * @post callerThread and calleeThread are set
	 * 
	 * @throws SemanticError
	 */
	private void findThreadNumbers() throws SemanticError {

		if (data.getCaller().equals("$")) {
			callerThread = -1;
			calleeThread = diagram.spawnThread();
			return;
		}

		if (callerIsActor() && (isPrimitiveMessage() || calleeIsActor())) {
			callerThread = -1;
			calleeThread = -1;
			return;
		}
		if (callerIsActor()) {
			callerThread = -1;
			if (!data.returnsInstantly()) {
				calleeThread = diagram.spawnThread();
			}
			return;
		}

		// The caller is not an actor.

		if (diagram.noThreadIsSpawned()) {
			callerThread = diagram.spawnThread();
		} else {

			Lifeline lineToBeFound = null;

			if (!data.getCallerMnemonic().equals("")) {
				lineToBeFound = diagram.getLifelineByMnemonic(data.getCaller(),
						data.getCallerMnemonic());
				if (lineToBeFound == null) {
					throw new SemanticError(provider,
							"There is no lifeline named \"" + data.getCaller()
									+ "\" associated to mnemonic \""
									+ data.getCallerMnemonic() + "\"");
				}
				callerThread = lineToBeFound.getThread();

			} else {

				/*
				 * If the MessageData specifies a thread number, we'll take it
				 * as the current thread number, otherwise we check if the
				 * caller object (represented by callerRoot) is just by only a
				 * single thread.
				 */
				if (data.getThread() >= 0) {
					if (data.getThread() >= diagram.getNumberOfThreads()) {
						throw new SemanticError(provider,
								"Illegal thread number: " + data.getThread());
					}
					callerThread = data.getThread();
				} else {
					final int uniqueThread = rootCaller.getUniqueThread();
					if (uniqueThread >= 0) {
						callerThread = uniqueThread;
					} else {
						if (calleeThread == -1) {
							throw new SemanticError(provider,
									"Explicit thread number required.");
						}
						callerThread = calleeThread;
					}
				}
			}
		}

		boolean spawning = data.isSpawnMessage();

		if (spawning) {
			if (data.getBroadcastType() > 0) {
				spawning = !calleeIsActor();
			}
		}

		if ((spawning || calleeIsActiveObject()) && !data.returnsInstantly()) {
			calleeThread = diagram.spawnThread();
		} else {
			calleeThread = callerThread;
		}

	}

	/**
	 * Returns the Lifeline representation of the caller as specified by the
	 * given MessageData and sets the current thread to the thread that was used
	 * in order to activate the Lifeline.
	 * 
	 * @param data
	 * @return the Lifeline representation of the caller
	 * @throws SemanticError
	 * @precondition caller is not an actor
	 */
	private Lifeline findCaller(boolean dropOneAnswer) throws SemanticError {

		if (rootCaller == null) {
			return null;
		}

		final String callerName = data.getCaller();

		if (callerIsActor()) {

			/*
			 * When multithreading is disabled and an actor sends a message,
			 * finish all ongoing activities.
			 */
			if (!diagram.isThreaded()) {
				diagram.finish();
			}

			return rootCaller;
		}

		final String mnemonic = data.getCallerMnemonic();

		/*
		 * If lineToBeFound is not null, we just look for it, ignoring level and
		 * thread specification of the MessageData.
		 */
		Lifeline lineToBeFound = null;

		/* TODO: prevent mnemonics for actors */

		if (!mnemonic.equals("")) {
			lineToBeFound = diagram.getLifelineByMnemonic(data.getCaller(),
					mnemonic);
			if (lineToBeFound == null) {
				throw new SemanticError(provider,
						"There is no lifeline named \"" + data.getCaller()
								+ "\" associated to mnemonic \"" + mnemonic
								+ "\"");
			}
		}

		final LinkedList<Message> currentStack = diagram.currentStack();

		if (currentStack == null) {
			throw new SemanticError(provider, "Thread " + callerThread
					+ " has died");
		}

		if (callerThread == 0 && currentStack.isEmpty()
				&& diagram.firstCaller() == null) {
			/*
			 * The message described by MessageData is the first that occurs on
			 * the thread 0, no object is yet active. So set the caller active
			 * and declare it the first caller.
			 */
			diagram.setFirstCaller(rootCaller);
			rootCaller.setActive(true);

			return rootCaller;
		}

		/*
		 * This number counts how often we have seen a lifeline with the same
		 * name, but with a wrong level (or not equal to lineToBeFound), as
		 * senders of answers on the stack.
		 */
		int occured = 0;

		while (!currentStack.isEmpty()) {

			final Message theAnswer = currentStack.getLast();

			if (dropOneAnswer) {
				if (theAnswer.isSynchronous() && theAnswer.getCallee().getName().equals(data.getCaller())) {
					diagram.sendAnswer(theAnswer, true);
					return theAnswer.getCallee();
				} else {
					throw new SemanticError(provider, data.getCaller()
							+ " cannot receive an answer here.");
				}
			}

			if (lineToBeFound != null) {
				if (theAnswer.getCaller() == lineToBeFound) {

					return lineToBeFound;
				}
			} else if (theAnswer.getCaller().getName().equals(data.getCaller())) {
				// An answer with the right level and the actual caller
				// as a caller is not sent. It can only be sent when
				// the current message has been executed (including its
				// followers)
				if (occured == data.getLevel()) {
					return theAnswer.getCaller();
				}
				occured++;
			}

			/*
			 * All answers that have non matching lifelines as senders are
			 * popped from the stack and the answer is added to the diagram,
			 * thus, an arrow representation will appear.
			 */
			currentStack.removeLast();

			if (requireReturn) {
				throw new SemanticError (provider, "Explicit answer required.");
			}
			diagram.sendAnswer(theAnswer);
		}

		if (diagram.firstCaller() != null
				&& diagram.firstCaller().getName().equals(callerName)) {
			/*
			 * We have not yet seen the lifeline we are looking for, the last
			 * chance is that it is the first caller on the thread.
			 */
			if (occured == data.getLevel()) {
				return diagram.firstCaller();
			}
			/*
			 * This chance was also missed, we add 1 to occured in order to
			 * count the occurence as first caller.
			 */
			occured++;
		}
		throw objectNotFound(occured, lineToBeFound);

	}

	private SemanticError objectNotFound(final int occured,
			final Lifeline lineToBeFound) {
		final String msg;

		if (lineToBeFound != null) {
			msg = data.getCaller() + "[" + data.getCallerMnemonic()
					+ "] is not active";
		} else if (occured == 0) {
			msg = data.getCaller() + " is not active at all";
		} else if (occured == 1) {
			msg = data.getCaller() + "[" + data.getLevel() + "]"
					+ "is not active, but " + data.getCaller() + "[0] is";
		} else {
			msg = data.getCaller() + "[" + data.getLevel() + "]"
					+ "is not active, but " + data.getCaller() + "[0]"
					+ (occured == 2 ? ", " : " - ") + data.getCaller() + "["
					+ (occured - 1) + "] are";
		}

		return new SemanticError(provider, msg);
	}

	/**
	 * If the callee, as described in the <tt>data</tt>, is not active on the
	 * given thread, this method returns the root lifeline of the callee. If
	 * there is already a callee lifeline for the thread, this lifeline is
	 * returned - for creating a higher-level activity via
	 * {@linkplain Lifeline#addActivity(Lifeline, int)}.
	 * 
	 * @param data
	 * @param thread
	 * @return
	 * @throws SemanticError
	 */
	private Lifeline findCallee() throws SemanticError {
		if (calleeIsActor()) {
			return rootCallee;
		}
		Lifeline theCallee = rootCallee.getLastInThread(calleeThread);
		if (theCallee == null) {
			theCallee = rootCallee;
		}

		if (!data.returnsInstantly() || !callerIsActor()
				&& !data.isSpawnMessage()) {
			if (!theCallee.isAlive() && data.isNewMessage()) {
				theCallee.setThread(calleeThread);

			} else if (theCallee.isActive()) {
				theCallee = theCallee.addActivity(caller, calleeThread);
			} else { // theCallee == rootCallee
				theCallee.setThread(calleeThread);
			}
		}

		final String calleeMnemonic = data.getCalleeMnemonic();
		if (!calleeMnemonic.equals("")) {
			diagram.associateLifeline(data.getCallee(), calleeMnemonic,
					theCallee);
			theCallee.setMnemonic(calleeMnemonic);
		}

		return theCallee;
	}

	private ForwardMessage getMessage() throws SemanticError {

		if (rootCaller == null) {
			if (!data.isSpawnMessage()) {
				throw new SemanticError(provider,
						"messages from caller $ must be spawning");
			}
			if (callee == null) {
				throw new SemanticError(provider,
						"messages from caller $ must have a callee");
			}
			return new NullMessage(callee, diagram, data);

		}

		if (data.isDestroyMessage() && rootCallee != null
				&& rootCallee.isActive()) {
			throw new SemanticError(provider, "cannot destroy active object");
		}

		if (isPrimitiveMessage()) {
			return new Primitive(caller, diagram, data);
		}

		if (data.getBroadcastType() != 0) {
			return new BroadcastMessage(caller, callee, diagram, data);
		}

		if (!callee.isAlive() && data.isNewMessage()) {
			return new ConstructorMessage(caller, callee, diagram, data);
		}

		if (caller.getName().equals(callee.getName())) {
			return new MessageToSelf(caller, callee, diagram, data);
		}
		return new ForwardMessage(caller, callee, diagram, data);

	}

	void execute(final ForwardMessage message) throws SemanticError {

		// if (message.getCaller().isWaiting()) {
		// if (!(message instanceof Primitive) || !((Primitive)
		// message).isSynchronizing()) {
		// throw new SemanticError (provider, "the thread is blocked");
		// }
		// }
		if (message instanceof Primitive && diagram.isThreaded()
				&& message.getText().equals("stop") && !caller.isAlwaysActive()) {
			diagram.finish(diagram.getCallerThread());
			diagram.setThreadState("dead");
			caller.finish();
			diagram.deleteStack();
			return;
		}

		message.updateView();
		if (provider.getState() != null) {
			diagram.addToStateMap(message.getArrow(), provider.getState());
		}
		answer = message.getAnswerMessage();

		diagram.setCallerThread(calleeThread);

		if (diagram.isThreaded()
				&& !calleeIsActor()
				&& !data.returnsInstantly()
				&& !(message instanceof Primitive)
				&& (calleeIsActiveObject() || data.isSpawnMessage() || caller
						.isAlwaysActive())) {
			diagram.setFirstCaller(callee);
			diagram.setThreadState("running");
		}

		if (answer != null) {
			if (data.returnsInstantly()) {
				diagram.sendAnswer(answer);
			} else {
				diagram.currentStack().add(answer);
			}
		}

		int dnum = data.getNoteNumber();
		if (dnum > 0) {
			diagram.associateMessage(dnum, message);
		}
		dnum = data.getAnswerNoteNumber();
		if (dnum > 0) {
			if (answer == null) {
				throw new SemanticError(provider,
						"You cannot associate a note to an answer when there is none.");
			}
			diagram.associateMessage(dnum, answer);
		}

	}
}
// {{core}}
