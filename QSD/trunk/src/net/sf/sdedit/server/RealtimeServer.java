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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sf.sdedit.Constants;
import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.editor.Editor;
import net.sf.sdedit.ui.impl.DiagramTextTab;
import net.sf.sdedit.util.Grep;

public class RealtimeServer extends Thread implements Constants {
	
	private ServerSocket serverSocket;

	private Editor editor;

	private List<Receiver> receivers;

	private boolean shutDown;

	public RealtimeServer(int port, Editor editor) throws IOException {
		serverSocket = new ServerSocket(port);
		this.editor = editor;
		receivers = new LinkedList<Receiver>();
	}

	public void shutDown() {
		shutDown = true;
		try {
			serverSocket.close();
		} catch (IOException e) {
			/* empty */
		}
		for (Receiver receiver : receivers) {
			receiver.shutDown();
		}
	}

	public int getPort() {
		if (serverSocket == null) {
			return 0;
		}
		return serverSocket.getLocalPort();
	}

	private void createReceiver(final Socket socket) throws IOException {
		InputStream stream = socket.getInputStream();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				stream, ConfigurationManager.getGlobalConfiguration().getFileEncoding()));
		String line = reader.readLine();
		if (line != null) {
			final BufferedReader decodingReader;
			final String title;
			line = line.trim();
			String[] parts = Grep.parse("^(.*)\\[(.*)\\]$", line);
			if (parts != null) {
				String enc = parts[1];
				title = parts[0];
				BufferedReader theReader = null;
				try {
					theReader = new BufferedReader(new InputStreamReader(
							stream, enc));
				} catch (Exception failed) {
					failed.printStackTrace();
					theReader = null;
				}
				decodingReader = theReader == null ? reader : theReader;
			} else {
				title = line;
				decodingReader = reader;
			}
			if (title.length() > 0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						DiagramTextTab tab = editor.getUI().addDiagramTextTab(title,
								ConfigurationManager.createNewDefaultConfiguration(), true);
						Receiver receiver = new Receiver(tab,
								decodingReader, socket);
						receivers.add(receiver);
						Thread receiverThread = new Thread(receiver);
						receiverThread.setDaemon(true);
						receiverThread.start();
					}
				});
			}
		}
	}

	public void run() {
		Socket socket;
		while (true) {
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				if (shutDown) {
					return;
				}
				editor
						.getUI()
						.errorMessage(e,null,
								"Exception caught while waiting for a client to be connected.");
				return;
			}

			try {
				createReceiver(socket);
			} catch (Exception e) {
				editor
						.getUI()
						.errorMessage(e,null,
								"Exception caught while establishing a connection to a client.");
			}
		}
	}
}