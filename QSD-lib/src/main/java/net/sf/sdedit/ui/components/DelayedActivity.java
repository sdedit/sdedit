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

package net.sf.sdedit.ui.components;

import javax.swing.SwingUtilities;

/**
 * A <tt>DelayedActivity</tt> is performed when a certain (changing) event has
 * happened at some time and then has not happened again for a certain amount of
 * time. That time is given by {@linkplain #getDelay()}, the activity to be
 * performed is given by {@linkplain #perform()}. In order to indicate that the
 * certain event has occurred, some caller must invoke
 * {@linkplain #indicateChange()}.
 * 
 * @author Markus Strauch
 * 
 */
public abstract class DelayedActivity extends Thread {
	private boolean notified;

	protected DelayedActivity() {
		super();
		setDaemon(true);
	}

	public synchronized void indicateChange() {
		notified = true;
		notify();
	}

	@Override
	public void run() {
		boolean changed = false;
		synchronized (this) {
			while (true) {
				try {
					wait(Math.max(1, getDelay()));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				if (notified) {
					changed = true;
				} else if (changed) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							synchronized (DelayedActivity.this) {
								perform();
							}

						}
					});
					changed = false;
				}
				notified = false;
			}
		}
	}

	protected abstract int getDelay();

	/**
	 * Performs the desired activity if something has changed (as indicated by
	 * {@linkplain #indicateChange()}, the delay has passed and no change has
	 * been indicated in that time. This method is always invoked in a monitor
	 * that is guarded by the lock associated with this <tt>DelayedActivity</tt>
	 * .
	 */
	protected abstract void perform();
}
