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

package net.sf.sdedit.util;

public abstract class Cancellable implements Runnable {
	
	private boolean done;

	private Thread thread;

	private Throwable throwable;

	protected Cancellable() {
		done = false;
	}
	
	protected abstract void perform () throws Throwable;

	public void run() {
		try {
			perform();
			synchronized (this) {
				done = true;
			}
		} catch (RuntimeException re) {
			throw re;
		} catch (ThreadDeath td) {
			/* ignored */
		} catch (Throwable t) {
			throwable = t;
		}
	}

	public boolean isDone() {
		return done;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	@SuppressWarnings("deprecation")
	public void execute(int timeout) {
		thread = new Thread(this);
		thread.start();
		try {
			thread.join(timeout);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
		boolean kill;
		synchronized (this) {
			kill = !done;
		}
		if (kill) {
			thread.stop();
		}
	}
	
}
