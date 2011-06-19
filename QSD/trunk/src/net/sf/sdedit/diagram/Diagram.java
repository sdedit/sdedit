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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.sdedit.Constants;
import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.drawable.Arrow;
import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.drawable.Fragment;
import net.sf.sdedit.drawable.Text;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.error.SyntaxError;

import net.sf.sdedit.message.Answer;
import net.sf.sdedit.message.BroadcastMessage;
import net.sf.sdedit.message.ForwardMessage;
import net.sf.sdedit.message.Message;
import net.sf.sdedit.util.Bijection;

/**
 * This class encapsulates the data for and the process of the generation of a
 * sequence diagram. That process consists - roughly spoken - of these steps:
 * 
 * <ol>
 * <li>Read object specifications and create lifelines for them</li>
 * <li>For each message specification:</li>
 * <ol>
 * <li>Find the lifeline objects that correspond to the sender and the receiver
 * of the message.</li>
 * <li>If activating the sender implies sending answers, send these answers
 * (which are taken from the answer message stack), so create arrows for them
 * and add them to the PaintDevice. This is applied to all of the answers that
 * lie above the answer with the sender as a receiver.</li>
 * <li>Create a message object representing the message</li>
 * <li>Add a message arrow to the PaintDevice</li>
 * <li>Get the corresponding answer message and push it onto the answer message
 * stack</li>
 * </ol>
 * <li>Pop all remaining answers from the stack and draw the arrows</li> </ol>
 * 
 * When that process has finished, the image of the diagram display can be shown
 * on the user interface.
 * 
 * @sequence.diagram diag:Diagram ddp:DiagramDataProvider pd:PaintDevice
 *                   /ll:Lifeline head:Drawable line:Drawable /msg:Message
 *                   arrow:Drawable gui:GUI[p]
 * 
 *                   [c:loop for all object specifications] diag:spec=ddp.read
 *                   diag:ll.new diag:head=ll.getHead() diag:pd.add(ll)
 *                   diag:line=ll.getView() diag:pd.add(line) [/c] [c:loop for
 *                   all message specifications] diag:spec=ddp.read diag:msg.new
 *                   diag:arrow=msg.getArrow() diag:pd.add(arrow) [/c]
 *                   diag:pd.computeAxes pd:ll.setLeft(...)
 *                   pd:arrow.setLeft(...),setWidth(...) gui:pd.draw(g2d)
 *                   [c:loop for all drawable objects in clip]
 *                   pd:arrow.draw(g2d) pd:line.draw(g2d) [/c]
 * 
 * @author Markus Strauch
 */
public final class Diagram implements Constants {
	/**
	 * Maps names of objects onto their root lifelines.
	 */
	private final Map<String, Lifeline> lifelineMap;

	/**
	 * A list of root lifelines.
	 */
	private final List<Lifeline> lifelineList;

	/**
	 * The current vertical position. For all messages that have already been
	 * read, the corresponding drawable objects' bottoms are above this
	 * position.
	 */
	private int verticalPosition;

	/**
	 * We read the object and message specifications from a DiagramDataProvider.
	 */
	private final DiagramDataProvider provider;

	/**
	 * The configuration object.
	 */
	private final Configuration conf;

	/**
	 * A list of stacks, one for each thread, consisting of the answers that are
	 * yet to be sent.
	 */
	private final ArrayList<LinkedList<Message>> threadStacks;

	/**
	 * first.get(i) is the lifeline belonging to the object that sends the first
	 * message on thread i.
	 */
	private final ArrayList<Lifeline> first;

	/**
	 * The container for the drawable objects created for messages etc.
	 */
	private final IPaintDevice paintDevice;

	/**
	 * Maps a drawable object onto the state the DiagramDataProvider was in when
	 * the specification reflected by the drawable objects was read.
	 */
	private final Bijection<Drawable, Object> drawableBijection;

	/**
	 * The number of the thread where the current message is processed.
	 */
	private int callerThread;

	/**
	 * Flag denoting if the diagram is multithreaded.
	 */
	private final boolean threaded;

	/**
	 * The list of thread states.
	 */
	private final ArrayList<String> threadStates;

	/**
	 * Responsible for creating notes from data coming in from the
	 * DiagramDataProvider and for managing these notes.
	 */
	private final NoteManager noteManager;

	/**
	 * Maps a lifeline name onto a map that maps a lifeline mnemonic onto the
	 * corresponding lifeline.
	 */
	private final Map<String, Map<String, Lifeline>> mnemonicMap;
	
	private final List<ForwardMessage> messages;

	private final FragmentManager fragmentManager;

	private final MessageProcessor processor;

	private boolean finished;

	// These attributes are here for performance reasons
	// The hot spot optimizer cannot inline calls to the corresponding configuration
	// get-methods because the configuration object is synthesized
	public final int arrowSize;

	public final int messagePadding;

	public final int subLifelineWidth;

	public final int mainLifelineWidth;

	public final int messageLabelSpace;
	
	public final int arrowThickness;
	
	public final int activationBarBorderThickness;
	
	public final int lifelineThickness;
	
	public final boolean returnArrowVisible;

	public final Color[] threadColors;
	
	public final Color arrowColor;
	
	public final int selfMessageXExtent;
	
	public final boolean opaqueText;
	
	private final boolean requireReturn;
	
	private final boolean slackMode;
	
	

	/**
	 * Creates a new diagram that is to be generated based on the data delivered
	 * by the given <tt>DiagramDataProvider</tt>.
	 * 
	 * @param configuration
	 *            the configuration of the diagram
	 * @param provider
	 *            for reading the object and message specifications
	 * @param paintDevice
	 *            for storing and drawing the boxes, arrows etc.
	 */
	public Diagram(Configuration configuration, DiagramDataProvider provider,
			IPaintDevice paintDevice) {
		arrowSize = configuration.getArrowSize();
		arrowColor = configuration.getArrowColor();
		messagePadding = configuration.getMessagePadding();
		subLifelineWidth = configuration.getMessagePadding();
		selfMessageXExtent = configuration.getSelfMessageHorizontalSpace();
		mainLifelineWidth = configuration.getMainLifelineWidth();
		messageLabelSpace = configuration.getMessageLabelSpace();
		returnArrowVisible = configuration.isReturnArrowVisible();
		arrowThickness = configuration.getArrowThickness();
		activationBarBorderThickness = configuration.getActivationBarBorderThickness();
		lifelineThickness = configuration.getLifelineThickness();
		opaqueText = configuration.isOpaqueMessageText();
		this.paintDevice = paintDevice;
		lifelineMap = new HashMap<String, Lifeline>();
		lifelineList = new ArrayList<Lifeline>();
		conf = configuration;
		paintDevice.setDiagram(this);
		verticalPosition = 0;
		first = new ArrayList<Lifeline>();
		this.provider = provider;
		provider.setDiagram(this);
		threadStacks = new ArrayList<LinkedList<Message>>();
		threadStates = new ArrayList<String>();
		drawableBijection = new Bijection<Drawable, Object>();
		this.threaded = conf.isSlackMode() || conf.isThreaded();
		if (!threaded) {
			/* spawn the only single thread */
			callerThread = spawnThread();
		}
		mnemonicMap = new HashMap<String, Map<String, Lifeline>>();
		noteManager = new NoteManager(this);
		fragmentManager = new FragmentManager(this);
		processor = new MessageProcessor(this);
		finished = false;
		threadColors = new Color[] { configuration.getTc0(),
				configuration.getTc1(), configuration.getTc2(),
				configuration.getTc3(), configuration.getTc4(),
				configuration.getTc5(), configuration.getTc6(),
				configuration.getTc7(), configuration.getTc8(),
				configuration.getTc9(), };
		requireReturn = conf.isExplicitReturns();
		slackMode = conf.isSlackMode();
		messages = new LinkedList<ForwardMessage>();

	}

	/**
	 * Generates the diagram, based on the data of the
	 * <tt>DiagramDataProvider</tt> passed to the constructor.
	 * 
	 * @throws SyntaxError
	 *             if a message or object specification is syntactically wrong
	 * @throws SemanticError
	 *             if a message or object specification is semantically wrong
	 */
	public void generate() throws SemanticError, SyntaxError {


		Fragment frame = null;
		Text text = null;
		String title = provider.getTitle();

		String description[] = provider.getDescription();
		if (description != null) {
			text = new Text(description, paintDevice);
			text.setTop(conf.getUpperMargin());
			text.setLeft(conf.getLeftMargin());
			verticalPosition = text.getBottom() + 3;
		} else {
			verticalPosition = conf.getUpperMargin();
		}

		if (title != null) {
			frame = new Fragment(title, "", this);
			frame.setTop(verticalPosition);
			verticalPosition += frame.getLabelHeight() + 5;
		}

		readObjects();
		if (lifelineList.isEmpty()) {
			return;
		}

		paintDevice.reinitialize();
		try {

			for (Lifeline lifeline : lifelineList) {
				paintDevice.addOtherDrawable(lifeline.getHead());
				verticalPosition = Math.max(verticalPosition, lifeline
						.getHead().getTop()
						+ lifeline.getHead().getHeight());
			}
			for (Lifeline lifeline : lifelineList) {
				lifeline.getView().setBottom(verticalPosition);
			}
			extendLifelines(conf.getInitialSpace());

			for (Lifeline lifeline : lifelineList) {
				if (lifeline.hasThread()) {
					lifeline.setActive(true);
				}
			}

			readMessages();

		} finally {

			fragmentManager.finishFragments();

			if (getNumberOfLifelines() > 0) {

				paintDevice.computeAxes(conf.getLeftMargin() + 6
						+ getLifelineAt(0).getHead().getWidth() / 2);
				paintDevice.computeBounds();

				// fixes bug 2019730 (notes appear outside of diagram)
				for (Lifeline lifeline : getAllLifelines()) {
					noteManager.closeNote(lifeline.getName());
				}
				//

				noteManager.computeArrowAssociations();

				if (frame != null) {
					frame.setLeft(conf.getLeftMargin());
					frame.setRight(paintDevice.getWidth()
							- conf.getRightMargin() + 6);
					frame.setBottom(verticalPosition + 4);
					paintDevice.addOtherDrawable(frame);
				}
				if (text != null) {
					paintDevice.addOtherDrawable(text);
				}
			}
			finished = true;
			paintDevice.close();
		}
	}

	public final boolean isFinished() {
		return finished;
	}

	/**
	 * Reads lines in which objects are declared using the reader given, until
	 * an empty line is found. Creates lifelines for them. If all object
	 * declarations have been successfully read, draws them on the diagram.
	 * 
	 * @throws SyntaxError
	 *             if an object declaration is not well-formed
	 * @throws SemanticError
	 *             if an object is declared twice
	 */
	private void readObjects() throws SyntaxError, SemanticError {
		while (provider.advance()) {
			Lifeline lifeline = provider.nextObject();
			if (lifeline == null) {
				return;
			}
			if (lifeline.isActiveObject() && !threaded) {
				throw new SemanticError(provider,
						"v flag for active object cannot be set when multithreading is disabled");
			}
			if (provider.getState() != null) {
				drawableBijection.add(lifeline.getHead(), provider.getState());
			}
			addLifeline(lifeline);
		}
	}

	/**
	 * Reads lines in which messages exchanged between objects are described.
	 * Draws for each message a corresponding arrow (and before that, possibly
	 * several return arrows) on the diagram just after the message has been
	 * successfully read. Stops when there is nothing left to read.
	 * 
	 * @param draw
	 *            flag denoting if message arrows are to be drawn
	 * @throws SyntaxError
	 *             if a description of a message is not well-formed
	 * @throws SemanticError
	 *             if a message between objects that are not existing or that
	 *             are not active is described
	 */
	private void readMessages() throws SyntaxError, SemanticError {

		while (provider.advance()) {

			Lifeline caller = null;



			if (fragmentManager.readFragments()) {

				continue;
			}

			if (!noteManager.step()) {
				MessageData data = provider.nextMessage();

				noteManager.closeNote(data.getCaller());
				String[] callees = data.getCallees(); // returns an empty array
				// when there is 1
				// caller
				if (callees.length == 1) {
					throw new SyntaxError(provider,
							"A broadcast message must have at least two receivers");
				}
				if (callees.length >= 2) {
					if (data.isSpawnMessage()) {
						throw new SyntaxError(provider,
								"Broadcast messages are spawning by default");
					}
					Set<String> calleeSet = new HashSet<String>();
					Lifeline[] allButLast = new Lifeline[callees.length - 1];
					for (int i = 0; i < callees.length; i++) {

						String callee = callees[i];
						if (callee.length() == 0) {
							throw new SyntaxError(provider,
									"Malformed broadcast message");
						}
						if (!calleeSet.add(callee)) {
							throw new SyntaxError(provider,
									"Duplicate receiver: " + callee);
						}
						if (callee.equals(data.getCaller())) {
							throw new SyntaxError(provider, "The sender "
									+ callee + " cannot be a "
									+ "receiver of the broadcast message");
						}
						noteManager.closeNote(callee);
						MessageData part = new MessageData();
						// TODO mnemonics
						part.setCaller(data.getCaller());
						part.setCallee(callee);
						part.setLevel(data.getLevel());
						part.setThread(data.getThread());
						if (getLifeline(data.getCaller()) != null) {
							if (!getLifeline(data.getCaller()).isAlwaysActive()) {
								part.setSpawnMessage(true);
							}
						}
						part.setReturnsInstantly(slackMode || data.returnsInstantly());
						if (i == 0) {
							part.setNoteNumber(data.getNoteNumber());
							part.setMessage(data.getMessage());
							part.setBroadcastType(BroadcastMessage.FIRST);
						} else if (i == callees.length - 1) {
							part.setBroadcastType(BroadcastMessage.LAST);
						} else {
							part.setBroadcastType(BroadcastMessage.OTHER);
						}
						BroadcastMessage msg = (BroadcastMessage) processor
								.processMessage(part, null);
						if (i < callees.length - 1) {
							allButLast[i] = msg.getCallee();
						} else {
							msg.setOtherCallees(allButLast);
						}
						processor.execute(msg);
					}
				} else {
					noteManager.closeNote(data.getCallee());
					if (data.isReturning() && requireReturn) {
						caller = processor.processReturn(data);
					} else {
						ForwardMessage msg = processor.processMessage(data,
								caller);
						messages.add(msg);
						processor.execute(msg);
						caller = null;
					}
				}
				fragmentManager.clearLabels();
			}
			fragmentManager.clearSectionLabel();

		}

		finish();
		for (Lifeline line : getLifelines()) {
			if (!line.isAlwaysActive()) {
				line.terminate();
			}
		}
	}
	
	public List<ForwardMessage> getMessages() {
	    return messages;
	}

	public int getNextFreeNoteNumber() {
		return noteManager.getNextFreeNoteNumber();
	}

	public FragmentManager getFragmentManager() {
		return fragmentManager;
	}

	/**
	 * Returns the state, represented by an Object, the
	 * <tt>DiagramDataProvider</tt> used by this Diagram was in when the data
	 * from which the given Drawable object has been created, was read.
	 * 
	 * @param drawable
	 *            a drawable corresponding to some data
	 * @return the state the DiagramDataProvider was in when the data has been
	 *         provided
	 */
	public Object getStateForDrawable(Drawable drawable) {
		if (drawable instanceof Arrow) {
			Message msg = ((Arrow) drawable).getMessage();
			if (msg instanceof Answer) {
				drawable = ((Answer) msg).getForwardMessage().getArrow();
			}
		}
		return drawableBijection.getImage(drawable);
	}

	public Drawable getDrawableForState(Object state) {
		return drawableBijection.getPreImage(state);
	}

	/**
	 * Adds a new (root) lifeline for an object with the name and type given.
	 */
	private boolean addLifeline(Lifeline lifeline) throws SemanticError {
		if (lifelineMap.get(lifeline.getName()) != null) {
			throw new SemanticError(provider, lifeline.getName()
					+ " already exists");
		}
		if (lifeline.hasThread()) {
			if (!threaded) {
				throw new SemanticError(
						provider,
						lifeline.getName()
								+ " cannot have its own thread when multithreading is not enabled");
			}
			int thread = spawnThread();
			first.set(thread, lifeline);
			lifeline.setThread(thread);
		}
		lifelineList.add(lifeline);
		lifelineMap.put(lifeline.getName(), lifeline);
		return true;
	}

	public final void extendLifelines(final int amount) {

		for (final Lifeline lifeline : getLifelines()) {
			if (lifeline.isAlive()) {
				for (final Lifeline line : lifeline.getAllLifelines()) {
					line.getView().extend(amount);
				}
			}
		}
		verticalPosition += amount;
	}

	int getPositionOf(Lifeline lifeline) {
		return lifelineList.indexOf(lifeline.getRoot());
	}

	public boolean isThreaded() {
		return threaded;
	}

	public int getCallerThread() {
		return callerThread;
	}

	public void setCallerThread(int callerThread) {

		this.callerThread = callerThread;
	}

	public IPaintDevice getPaintDevice() {
		return paintDevice;
	}

	/**
	 * Returns the text handler that reads the text line by line and creates
	 * objects and message data from them.
	 * 
	 * @return the text handler that reads the text line by line and creates
	 *         messages and objects from them
	 */
	public DiagramDataProvider getDataProvider() {
		return provider;
	}

	/**
	 * Returns the diagram configuration
	 * 
	 * @return the diagram configuration
	 */
	public Configuration getConfiguration() {
		return conf;
	}

	/**
	 * Returns a collection of the (not yet destroyed) lifelines appearing in
	 * the diagram.
	 * 
	 * @return a collection of the (not yet destroyed) lifelines appearing in
	 *         the diagram
	 */
	public Collection<Lifeline> getLifelines() {
		return lifelineMap.values();
	}

	/**
	 * Returns a collection of all lifelines, whether visible or not, and
	 * whether already destroyed or not.
	 * 
	 * @return a collection of all lifelines, whether visible or not, and
	 *         whether already destroyed or not
	 */
	public Collection<Lifeline> getAllLifelines() {
		return lifelineList;
	}

	/**
	 * Removes a lifeline, denoted by its name, from the diagram, including all
	 * of its sub lifelines.
	 * 
	 * @param name
	 *            the name of the object of which the lifeline is to be removed
	 */
	public void removeLifeline(String name) {

		if (lifelineMap.remove(name) == null) {
			throw new IllegalArgumentException("lifeline " + name
					+ " should be removed, but does not exist");
		}
	}

	LinkedList<Message> currentStack() {
		return threadStacks.get(callerThread);
	}

	Lifeline firstCaller() {
		return first.get(callerThread);
	}

	public void setFirstCaller(Lifeline caller) {

		first.set(callerThread, caller);
	}

	int spawnThread() {
		int num = threadStacks.size();

		threadStacks.add(new LinkedList<Message>());
		first.add(null);
		threadStates.add("new");
		return num;
	}

	boolean noThreadIsSpawned() {
		return threadStacks.isEmpty();
	}

	String threadState() {
		return threadStates.get(callerThread);
	}

	void setThreadState(String state) {
		threadStates.set(callerThread, state);
	}

	void deleteStack() {
		threadStacks.set(callerThread, null);
		first.set(callerThread, null);
	}

	void finish(int thread) throws SemanticError {

		LinkedList<Message> threadStack = threadStacks.get(thread);
		if (!conf.isExplicitReturns()) {
			while (threadStack != null && !threadStack.isEmpty()) {
				Message answer = threadStack.removeLast();

				sendAnswer(answer);
			}
		}
		Lifeline firstLifeline = first.get(thread);
		if (firstLifeline != null) {
			firstLifeline.finish();
			if (firstLifeline.getRoot() != firstLifeline) {
				firstLifeline.dispose();
			}
		}
	}

	public void sendAnswer(Message answer) {
		sendAnswer(answer, false);
	}

	public void sendAnswer(Message answer, boolean removeFromStack) {

		if (removeFromStack) {
			int thread = answer.getThread();
			threadStacks.get(thread).removeLast();

		}
		noteManager.closeNote(answer.getCaller().getName());
		noteManager.closeNote(answer.getCallee().getName());
		answer.updateView();
	}

	/**
	 * All pending answers are sent back to the callers, all lifelines become
	 * inactive.
	 */
	public void finish() throws SemanticError {

		for (int t = 0; t < threadStacks.size(); t++) {
			finish(t);
		}
		for (Lifeline line : getLifelines()) {
			if (line != null && !(line.isAlwaysActive()) && line.isActive()) {
				line.finish();
			}
		}

	}

	public Lifeline getLifelineAt(int position) {
		return lifelineList.get(position);
	}

	public Lifeline getLifeline(String name) {
		return lifelineMap.get(name);
	}

	public int getNumberOfLifelines() {
		return lifelineList.size();
	}

	void setVerticalPosition(int verticalPosition) {
		this.verticalPosition = verticalPosition;
	}

	public int getVerticalPosition() {
		return verticalPosition;
	}

	public int getNumberOfThreads() {
		return threadStacks.size();
	}

	void addToStateMap(Drawable drawable, Object state) {
		drawableBijection.add(drawable, state);
	}

	public Lifeline getLifelineByMnemonic(String lifelineName, String mnemonic) {
		Map<String, Lifeline> map = mnemonicMap.get(lifelineName);
		return map == null ? null : map.get(mnemonic);
	}

	public void associateLifeline(String lifelineName, String mnemonic,
			Lifeline lifeline) throws SemanticError {
		Map<String, Lifeline> map = mnemonicMap.get(lifelineName);
		if (map == null) {
			map = new HashMap<String, Lifeline>();
			mnemonicMap.put(lifelineName, map);
		}
		if (map.put(mnemonic, lifeline) != null) {
			throw new SemanticError(provider, "The mnemonic \"" + mnemonic
					+ "\" is already defined for the lifeline " + "\""
					+ lifeline.getName());
		}

	}

	public void associateMessage(int number, Message msg) {
		noteManager.associateMessage(number, msg);
	}

	public void clearMnemonic(Lifeline lifeline) {
		String mnemonic = lifeline.getMnemonic();
		if (mnemonic == null) {
			return;
		}
		Map<String, Lifeline> map = mnemonicMap.get(lifeline.getName());
		map.remove(mnemonic);
	}

	public void toggleWaitingStatus(int thread) {
		for (Lifeline lifeline : getLifelines(thread)) {
			lifeline.toggleWaitingStatus();
		}
	}

	private Set<Lifeline> getLifelines(int thread) {
		Set<Lifeline> lifelines = new HashSet<Lifeline>();
		Lifeline firstCaller = first.get(thread);
		if (!firstCaller.isAlwaysActive()) {
			lifelines.add(firstCaller);
		}
		for (Message msg : threadStacks.get(thread)) {
			lifelines.add(msg.getCaller());
			if (msg.getCallee() != null) {
				lifelines.add(msg.getCallee());
			}
		}
		return lifelines;
	}
}
// {{core}}
