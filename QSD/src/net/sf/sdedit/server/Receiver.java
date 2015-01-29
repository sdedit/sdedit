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

package net.sf.sdedit.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

import net.sf.sdedit.ui.UserInterface;
import net.sf.sdedit.ui.components.DelayedActivity;
import net.sf.sdedit.ui.impl.DiagramTextTab;

/**
 * A <tt>Receiver</tt> receives a diagram specification through a TCP socket.
 * The received code will be entered into the text area of some tab in the
 * {@linkplain UserInterface}.
 * 
 * @author Markus Strauch
 * 
 */
public class Receiver implements Runnable
{
    private BufferedReader reader;

    private int delay = 100;

    private Socket socket;

    private boolean shutDown;

    private DelayedActivity waiter;

    private StringBuffer appendBuffer;

    /**
     * Creates a new Receiver.
     * 
     * @param editor
     *            the control editor
     * @param tabTitle
     *            the name of the title where the code that is received through
     *            the socket is to be entered
     * @param reader
     *            a reader for the socket's input stream, ready to read
     * @param socket
     *            the socket
     */
    public Receiver(final DiagramTextTab tab,
            BufferedReader reader, Socket socket) {
        this.reader = reader;
        this.socket = socket;
        shutDown = false;
        appendBuffer = new StringBuffer();
        waiter = new DelayedActivity()
        {
        	@Override
            protected int getDelay() {
                return delay;
            }

        	@Override
            protected void perform() {
        		tab.append(appendBuffer.toString());
                appendBuffer.setLength(0);
            }
        };
        waiter.start();
    }

    /**
     * Closes the socket of this <tt>Receiver</tt>.
     */
    public void shutDown() {
        shutDown = true;
        try {
            socket.close();
        } catch (IOException e) {
        	/* empty */
        }
    }

    /**
     * Calls <tt>readLine</tt> on the socket's reader, collects the received
     * lines and notifies the <tt>waiter</tt> each time a new line has been
     * received.
     */
    public void run() {
        try {
            String line;
            do {
                line = reader.readLine();
                if (line != null) {
                    line = line.trim();
                    if (line.toLowerCase().equals("end")) {
                        return;
                    }
                    synchronized (waiter) {
                        appendBuffer.append(line + "\n");
                    }
                    waiter.indicateChange();
                }
            } while (line != null);
        } catch (Exception e) {
            if (!shutDown) {
                e.printStackTrace();
            }
        } finally {
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
