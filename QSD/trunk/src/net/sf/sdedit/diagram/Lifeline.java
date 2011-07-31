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
import java.util.List;

import net.sf.sdedit.drawable.Cross;
import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.drawable.ExtensibleDrawable;
import net.sf.sdedit.drawable.Figure;
import net.sf.sdedit.drawable.LabeledBox;
import net.sf.sdedit.drawable.Line;
import net.sf.sdedit.drawable.Rectangle;

import net.sf.sdedit.util.Direction;
import net.sf.sdedit.util.Grep.Region;
import net.sf.sdedit.util.Utilities;

/**
 * For each object or actor that appears in a diagram and that has not yet been
 * destroyed, there is at least one <tt>Lifeline</tt> object. It is
 * <i>active</i> (see {@linkplain #isActive()}) if the corresponding object or
 * actor can currently send a message. For objects, being active is implied by
 * having received a message to which no answer has yet been sent. When an
 * object that has already received a message to which it has not yet answered,
 * receives another message, a new <tt>Lifeline</tt> object is created and
 * associated to the original (or &quot;root&quot;) <tt>Lifeline</tt> object.
 * 
 * @author Markus Strauch
 */
public final class Lifeline implements Comparable<Lifeline>{

	/**
	 * The direction of the lifeline, for root lifelines Direction.CENTER, for
	 * other lifelines it depends on the position of the caller that initiated
	 * the creation.
	 */
	private final Direction direction;

	/**
	 * The name of the object.
	 */
	private final String name;

	/**
	 * The type of the object.
	 */
	private final String type;
	
	private final String label;

	/**
	 * A mnemonic for distinguishing between lifelines belonging to the same
	 * object.
	 */
	private String mnemonic;

	/**
	 * The original lifeline belonging to this one.
	 */
	private final Lifeline root;

	/**
	 * The lifeline corresponding to the activity of the object before the
	 * activity that this lifeline represents started.
	 */
	private Lifeline parent;

	// only the root lifeline can have non-null leftChild AND rightChild

	private Lifeline leftChild;

	private Lifeline rightChild;

	/**
	 * Flag relevant for root lifelines
	 */
	private boolean active;

	/**
	 * Flag denoting if the object corresponding to this lifeline has already
	 * been created.
	 */
	private boolean alive;

	/**
	 * The number of activities that have been started before the activity that
	 * this lifeline represents, and that have not yet been finished.
	 */
	private final int level;

	/**
	 * Similar to <tt>level</tt>, but restricted to the lifelines with the same
	 * direction like this one.
	 */
	private final int sideLevel;

	private final Diagram diagram;

	/**
	 * The current graphical representation, changes as time passes (a line for
	 * inactive phases, a rectangle for active phases)
	 */
	private ExtensibleDrawable view;

	/**
	 * The head of the lifeline (a labeled box or a figure)
	 */
	private final Drawable head;

	/**
	 * A cross at the bottom of the lifeline, if present, it denotes destruction
	 */
	private Cross cross;

	/**
	 * The thread where the activity represented by this lifeline occurs
	 */
	private int thread;

	/**
	 * Flag denoting if this lifeline belongs to an active object
	 */
	private final boolean activeObject;

	/**
	 * Flag denoting if this lifeline belongs to a process (a passive actor)
	 */
	private final boolean process;

	/**
	 * Flag denoting if a thread is statically spawned for this lifeline
	 */
	private final boolean hasThread;

	/**
	 * The line representation most recently created for this lifeline
	 */
	private Line lastLine;
	
	private boolean anonymous;

	/**
	 * Flag denoting if the lifeline is to be destroyed when it has performed
	 * its last activity
	 */
	private final boolean autodestroy;

	/**
	 * The vertical position where the most recently created rectangle for
	 * representing this lifeline ends.
	 */
	private int rectangleBottom;

	/**
	 * This list is non-null for the root lifeline (only). It contains all
	 * ExtensibleDrawable objects that are ever used as a view of the root
	 * lifeline or its descendants.
	 */
	private final List<ExtensibleDrawable> allViews;

	private final boolean external;

	private boolean waiting;
	
	private Region nameRegion;
	
	public String toString () {
		String string = name + ":" + type;
		if (label.length() > 0) {
			string += " \"" + label + "\" ";
		}
		if (!alive) {
			string = "/" + string;
		}
		String flags = "";
		if (activeObject) flags += "v";
		if (process) flags += "p";
		if (external) flags += "e";
		if (autodestroy) flags += "x";
		if (hasThread) flags += "t";
		
		if (flags.length() > 0) {
			string += " [" + flags + "]";
		}
		
		return string;
		
	}

	/**
	 * Creates a new root <tt>Lifeline</tt> object that corresponds to an object
	 * and is ready to receive messages. If the object receives a message while
	 * it is active, this 'higher level' activity will be represented by a
	 * dependent lifeline which must be created via the
	 * {@linkplain #Lifeline(Lifeline, Direction, int)} constructor with the
	 * main lifeline as the first argument.
	 * 
	 * @param name
	 *            the name of the object
	 * @param type
	 *            the type of the object
	 * @param label
	 *            the label of the lifeline, if it is the empty string, the
	 *            label depends on the name and type
	 * @param alive
	 *            flag denoting whether the object is visible from the start (
	 *            <tt>true</tt>) or it will come into existence by receiving a
	 *            new message
	 * @param anonymous
	 *            flag denoting whether the name of the instance will be shown
	 *            on the diagram
	 * @param role
	 *            flag denoting whether the object is a role rather, so its
	 *            place could be taken by another object of another class, with
	 *            a similar behaviour; the visible effect of this is that the
	 *            name of the instance and the class will not be underlined
	 * @param activeObject
	 *            flag denoting if the object is active, i. e. all messages sent
	 *            to the object spawn a new thread
	 * @param diagram
	 *            the diagram to which the lifeline belongs
	 */
	public Lifeline(String name, String type, String label, boolean alive,
			boolean anonymous, boolean role, boolean activeObject,
			boolean process, boolean hasThread, boolean autodestroy,
			boolean external, Diagram diagram) {
		this.diagram = diagram;
		this.name = name;
		this.type = type;
		this.direction = Direction.CENTER;
		this.alive = alive;
		this.thread = 0;
		this.level = 0;
		this.activeObject = activeObject;
		this.process = process;
		this.hasThread = hasThread;
		this.autodestroy = autodestroy;
		this.external = external;
		this.label = label;
		this.anonymous = anonymous;
		parent = null;
		root = this;
		sideLevel = 0;
		rectangleBottom = 0;
		waiting = false;
		allViews = new LinkedList<ExtensibleDrawable>();
		if (type.equals("Actor")) {
			head = new Figure(this, label, diagram.getVerticalPosition(), !role);
			view = new Rectangle(computeDrawableWidth(), this);
			active = true;
		} else if (process) {
			head = new LabeledBox(this, label, diagram.getVerticalPosition(),
					anonymous, !role);
			view = new Rectangle(computeDrawableWidth(), this);
			active = true;
		} else {
			head = new LabeledBox(this, label, diagram.getVerticalPosition(),
					anonymous, !role);
			// view = new Line(computeDrawableWidth(), this);
			view = new Line(1, this);
			active = false;
		}
		view.setTop(head.getTop() + head.getHeight());
		head.setVisible(alive);
		view.setVisible(alive);
		addView(view);
	}

	/**
	 * If a main lifeline is active because it received a message, and then it
	 * receives another message, a dependent lifeline must be created. This is
	 * done by this constructor.
	 * 
	 * @param root
	 *            the root lifeline
	 * @param direction
	 *            <tt>Direction.LEFT</tt>, if the message is sent from a
	 *            lifeline with a lower position, otherwise
	 *            <tt>Direction.RIGHT</tt>
	 * @param thread
	 *            the number of the thread where the message that produced the
	 *            activity represented by the new lifeline occured
	 */
	private Lifeline(Lifeline root, Direction direction, int thread) {
		this.name = root.name;
		this.type = root.type;
		this.direction = direction;
		this.activeObject = root.activeObject;
		this.root = root;
		this.thread = thread;
		this.external = root.external;
		this.label = root.label;
		this.anonymous = root.anonymous;
		autodestroy = false;
		hasThread = false;
		diagram = root.diagram;
		alive = true;
		parent = root;
		level = root.getAllLifelines().size();
		allViews = null;
		waiting = false;
		if (direction == Direction.LEFT) {
			while (parent.leftChild != null) {
				parent = parent.leftChild;
			}
			parent.leftChild = this;
		} else {
			while (parent.rightChild != null) {
				parent = parent.rightChild;
			}
			parent.rightChild = this;
		}
		sideLevel = parent.sideLevel + 1;
		active = false;
		view = new Rectangle(computeDrawableWidth(), this);
		head = null;
		process = false;
		// view's top will be set inside setActive(true)
	}

	public void addView(ExtensibleDrawable view) {
		root.allViews.add(view);
	}

	public List<ExtensibleDrawable> getAllViews() {
	    
	    int lineTop = Integer.MAX_VALUE;
	    int lineBottom = -1;
	    Line mainLine = null;
	    
	    
	    
	    for (ExtensibleDrawable view : allViews) {
	        if (view instanceof Line) {
	            
	            Line line = (Line) view;
	            if (mainLine == null) {
	                mainLine = line;
	                line.setMainLine(true);
	            }
	            lineTop = Math.min(lineTop, line.getTop());
	            lineBottom = Math.max(lineBottom, line.getBottom());
	        }
	        
	    }
	    if (mainLine != null) {
	        mainLine.setTop(lineTop);
	        mainLine.setBottom(lineBottom);
	    }
	    
		return allViews;
	}

	public int getCallLevel() {
		int callLevel = 0;
		for (Lifeline line : getRoot().getAllLifelines()) {
			if (line != this && thread == line.thread) {
				callLevel++;
			}
			
		}
		return callLevel;
	}

	public boolean isActiveObject() {
		return activeObject;
	}

	public boolean hasThread() {
		return hasThread;
	}

	public void setThread(int thread) {
		this.thread = thread;
	}

	public int getThread() {
		return thread;
	}

	public void setMnemonic(String mnemnonic) {
		this.mnemonic = mnemnonic;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	public boolean isWaiting() {
		return waiting;
	}

	/**
	 * If all activities that correspond to this lifeline, its root and its
	 * root's descendants belong to the same unique thread, the number of this
	 * thread is returned, otherwise -1
	 * 
	 * @return the number of the unique thread where all activities belonging to
	 *         this lifeline, its root and its root's descendants occur, it
	 *         there is such a thread, otherwise -1
	 */
	public int getUniqueThread() {
		int t = -1;
		for (Lifeline line : getAllLifelines()) {
			if (line.isActive()) {
				if (t == -1) {
					t = line.getThread();
				} else {
					if (t != line.getThread()) {
						return -1;
					}
				}
			}
		}
		return t;
	}

	public Lifeline getLeftmost() {
		Lifeline left = root;
		while (left.leftChild != null) {
			left = left.leftChild;
		}
		return left;
	}

	public Lifeline getRightmost() {
		Lifeline right = root;
		while (right.rightChild != null) {
			right = right.rightChild;
		}
		return right;
	}

	/**
	 * Returns the main or root lifeline that represents the first activity of
	 * the object to which this lifeline belongs.
	 * 
	 * @return the main or root lifeline that represents the first activity of
	 *         the object to which this lifeline belongs
	 */
	public Lifeline getRoot() {
		return root;
	}

	public boolean isAlwaysActive() {
		return type.equals("Actor") || process;
	}

	/**
	 * Returns a list containing this lifeline and all of its sub lifelines.
	 * 
	 * @return a list containing this lifeline and all of its sub lifelines
	 */
	public LinkedList<Lifeline> getAllLifelines() {
		LinkedList<Lifeline> list = new LinkedList<Lifeline>();
		list.add(this);
		Lifeline line = leftChild;
		while (line != null) {
			list.add(line);
			line = line.leftChild;
		}
		line = rightChild;
		while (line != null) {
			list.add(line);
			line = line.rightChild;
		}
		return list;
	}

	public Lifeline getLastInThread(int thread) {
		Lifeline last = null;
		for (Lifeline lifeline : getAllLifelines()) {
			if (lifeline.getThread() == thread
					&& (last == null || lifeline.level > last.level)) {
				last = lifeline;
			}
		}
		return last;
	}

	/**
	 * If this is a root or sub lifeline that belongs to the object which is
	 * displayed rightmost, this method returns <tt>null</tt>, otherwise it
	 * returns (with respect to the current state of activities) the leftmost
	 * lifeline belonging to the object that is displayed to the right of the
	 * object to which this lifeline belongs. If the object on the right is not
	 * yet active, this method returns <tt>null</tt>.
	 * 
	 * @return the leftmost lifeline belonging to the object that is displayed
	 *         to the right of the object to which this lifeline belongs or
	 *         <tt>null</tt> if no such object or lifeline exists
	 */
	public Lifeline getRightNeighbour() {
		int pos = getPosition();
		if (pos < diagram.getNumberOfLifelines() - 1) {
			Lifeline right = diagram.getLifelineAt(pos + 1);
			while (right.leftChild != null) {
				right = right.leftChild;
			}
			return right;
		}
		return null;
	}

	/**
	 * If this is a root or sub lifeline that belongs to the object which is
	 * displayed leftmost, this method returns <tt>null</tt>, otherwise it
	 * returns (with respect to the current state of activities) the rightmost
	 * lifeline belonging to the object that is displayed to the left of the
	 * object to which this lifeline belongs. If the object on the left is not
	 * yet active, this method returns <tt>null</tt>.
	 * 
	 * @return the rightmost lifeline belonging to the object that is displayed
	 *         to the left of the object to which this lifeline belongs or
	 *         <tt>null</tt> if no such object or lifeline exists
	 */
	public Lifeline getLeftNeighbour() {
		int pos = getPosition();
		if (pos > 0) {
			Lifeline left = diagram.getLifelineAt(pos - 1);
			while (left.rightChild != null) {
				left = left.rightChild;
			}
			return left;
		}
		return null;
	}

	/**
	 * Returns a flag denoting whether the object is visible and ready to
	 * receive a message (<tt>true</tt>) or still waiting for a 'new' message.
	 * 
	 * @return a flag denoting whether the object is visible and ready to
	 *         receive message (<tt>true</tt>) or still waiting for a 'new'
	 *         message
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Call this method when an object is created via a 'new' message.
	 */
	public void giveBirth() {
		alive = true;
		head.setVisible(true);
		view.setVisible(true);
	}

	/**
	 * Returns <tt>Direction.CENTER</tt>, if this is a lifeline,
	 * <tt>Direction.LEFT</tt>, if this is a dependent lifeline that has been
	 * activated by a message from an object with a lower position, otherwise
	 * <tt>Direction.RIGHT</tt>
	 * 
	 * @return <tt>Direction.CENTER</tt>, if this is a lifeline,
	 *         <tt>Direction.LEFT</tt>, if this is a dependent lifeline that has
	 *         been activated by a message from an object with a lower position,
	 *         otherwise <tt>Direction.RIGHT</tt>
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Returns this lifeline's object's position in the object section where it
	 * is declared.
	 * 
	 * @return this lifeline's position
	 */
	public int getPosition() {
		return diagram.getPositionOf(this);
	}

	/**
	 * Returns 0, if this is a root lifeline; if it is a sub lifeline, returns
	 * the number of sub lifelines that have the same direction like this one.
	 * 
	 * @return the level of this lifeline on its side
	 */
	public int getSideLevel() {
		return sideLevel;
	}

	public boolean isActive() {
		return active;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	// caller determines the direction
	public Lifeline addActivity(Lifeline caller, int thread) {
		Direction theDirection;
		if (caller == null) {
			theDirection = Direction.RIGHT;
		} else {
			if (caller.getName().equals(getName())) {
				if (caller.getDirection() == Direction.CENTER) {
					theDirection = Direction.RIGHT;
				} else {
					if (getDirection() == Direction.CENTER) {
						theDirection = caller.getDirection();
					} else {
						theDirection = getDirection();
					}
				}
			} else {
				theDirection = caller.getPosition() < getPosition() ? Direction.LEFT
						: Direction.RIGHT;
			}
		}
		return new Lifeline(root, theDirection, thread);
	}

	/**
	 * This method is called when a message was read and the control flow
	 * returns to an object that has been called before the object corresponding
	 * to this lifeline was called. Thus its active flag is set false.
	 */
	public void finish() {

		setActive(false);
		getRoot().setRectangleBottom(diagram.getVerticalPosition());
	}

	public void terminate() {
		ExtensibleDrawable _view = lastLine != null ? lastLine : view;
		if (alive && autodestroy) {
			int lengthOfLastLine = Math
					.max(6, rectangleBottom - _view.getTop());
			cross = new Cross(this);
			int y = _view.getTop() + lengthOfLastLine + cross.getHeight();
			if (y > diagram.getVerticalPosition()) {
				diagram.extendLifelines(y - diagram.getVerticalPosition());
			}
			cross.setTop(_view.getTop() + lengthOfLastLine);
			diagram.getPaintDevice().addOtherDrawable(cross);
			_view.setHeight(lengthOfLastLine);
		}
		alive = false;
	}

	/**
	 * Disposes this lifeline, which means that it is taken from the stack of
	 * the main lifeline it belongs to. If it is a main lifeline, it cannot be
	 * disposed and thus an <tt>IllegalStateException</tt> is thrown. A lifeline
	 * must not be active in order to dispose it.
	 * 
	 * @throws IllegalStateException
	 *             if this is a main lifeline or if this lifeline is still
	 *             active
	 */
	public void dispose() {
		if (active) {
			throw new IllegalStateException("lifeline is still active");
		}
		if (getRoot() == this) {
			throw new IllegalStateException("lifeline cannot be disposed");
		}
		switch (direction) {
		case LEFT:
			parent.leftChild = leftChild;
			if (leftChild != null) {
				leftChild.parent = parent;
			}
			break;
		case RIGHT:
			parent.rightChild = rightChild;
			if (rightChild != null) {
				rightChild.parent = parent;
			}
			break;
		case CENTER:
			throw new IllegalStateException("The lifeline is not root, but"
					+ " has center direction");
		}
		getRoot().setRectangleBottom(diagram.getVerticalPosition());
	}

	public void toggleWaitingStatus() {
		if (!active) {
			throw new IllegalArgumentException(
					"an inactive lifeline cannot change its waiting status");
		}
		waiting = !waiting;
		int y = view.getTop() + view.getHeight();

		if (waiting) {
			lastLine = new Line(1, this);
			lastLine.setLeft(view.getLeft() + view.getWidth() / 2);

			view = lastLine;
		} else {
			view = new Rectangle(computeDrawableWidth(), this);
		}
		view.setTop(y);
	}

	public void setActive(boolean active) {
		if (this.active == active) {
			return;
		}
		this.active = active;
		if (this != getRoot()) {
			if (active) {
				view.setTop(parent.getView().getTop()
						+ parent.getView().getHeight());
				view.setHeight(0);
			} else {
				diagram.clearMnemonic(this);
			}
			return;
		}
		int y = view.getTop() + view.getHeight();
		if (active) {
			view = new Rectangle(computeDrawableWidth(), this);

		} else {
			// view = new Line(computeDrawableWidth(), this);
			view = new Line(1, this);
			diagram.clearMnemonic(this);
			lastLine = (Line) view;
		}
		view.setTop(y);
	}

	public void setRectangleBottom(int bottom) {
		rectangleBottom = Math.max(rectangleBottom, bottom);
	}

	public Diagram getDiagram() {
		return diagram;
	}

	public ExtensibleDrawable getView() {
		return view;
	}

	public Drawable getHead() {
		return head;
	}
	
	public boolean isAnonymous () {
        return anonymous;
	}

	private int computeDrawableWidth() {
		return sideLevel == 0 ? diagram.getConfiguration()
				.getMainLifelineWidth() : diagram.getConfiguration()
				.getSubLifelineWidth();
	}

	public boolean isExternal() {
		return external;
	}

	public void destroy() {
		diagram.removeLifeline(getName());
		cross = new Cross(this);
		diagram.getPaintDevice().addOtherDrawable(cross);
	}

	public Cross getCross() {
		return cross;
	}

    public int compareTo(Lifeline lifeline) {
        return this.name.compareTo(lifeline.name);
    }

    public void setNameRegion(Region region) {
        this.nameRegion = region;
    }

    public Region getNameRegion() {
        return nameRegion;
    }
}
// {{core}}
