package net.sf.sdedit.ui.impl;

import java.util.TimerTask;

public class UITimerTask extends TimerTask {

	private Runnable runnable;
	
	public UITimerTask (Runnable runnable) {
		super();
		this.runnable = runnable;
	}
	
	
	@Override
	public void run() {
		runnable.run();
	}

}
