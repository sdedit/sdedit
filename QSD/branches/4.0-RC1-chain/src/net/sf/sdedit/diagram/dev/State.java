package net.sf.sdedit.diagram.dev;

import net.sf.sdedit.diagram.Lifeline;

public class State {
	
	
	
	/**
	 * The root lifeline of the caller. Is invariantly non-null.
	 */
	private Lifeline rootCaller;

	/**
	 * The root lifeline of the callee. Is null iff the message is a primitive.
	 */
	private Lifeline rootCallee;
	
	private Object inputState;
	
	public State () {
		
	}

	public void setRootCaller(Lifeline rootCaller) {
		this.rootCaller = rootCaller;
	}

	public Lifeline getRootCaller() {
		return rootCaller;
	}

	public void setRootCallee(Lifeline rootCallee) {
		this.rootCallee = rootCallee;
	}

	public Lifeline getRootCallee() {
		return rootCallee;
	}

	public void setInputState(Object inputState) {
		this.inputState = inputState;
	}

	public Object getInputState() {
		return inputState;
	}
	
	public boolean callerIsActor() {
		return rootCaller != null && rootCaller.isAlwaysActive();
	}

	public boolean calleeIsActor() {
		return rootCallee != null && rootCallee.isAlwaysActive();
	}

}
