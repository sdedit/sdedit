// Copyright (c) 2006 - 2008, Markus Strauch.
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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.PriorityQueue;

import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.error.SyntaxError;
import net.sf.sdedit.text.TextHandler;

/**
 * This class implements a simple server that uses a TCP socket to receive a
 * diagram source text and to send an image of the resulting diagram.
 * <p>
 * The exchange must follow this simple protocol: <br>
 * <ol>
 * <li>The client sends the mimetype of the image he would like to receive.</li>
 * <li>The client sends the diagram source text.
 * <li>The client sends 'END'</li>
 * <li>
 * <ul>
 * <li> If the diagram cannot be created due to an error, the server sends a
 * message starting with 'ERROR:' and ending with a description of the error.
 * </li>
 * <li> Otherwise the server sends the image data.</li>
 * </ul>
 * </li>
 * </ol>
 * 
 * There are currently two mime-types supported:
 * 
 * <ol>
 * <li>image/png</li>
 * <li>image/svg+xml (requires the SVG plugin)</li>
 * </ol>
 * 
 * @author Markus Strauch
 * 
 */
public class DiagramServer extends Thread {

	private ServerSocket serverSocket;

	private PriorityQueue<ServerThread> threadPool;

	// the number of worker threads for processing requests (i. e. drawing
	// diagrams)
	private static int poolSize = 5;

	/**
	 * Creates and starts a new DiagramServer listening at the given port.
	 * 
	 * @param port
	 *            the TCP port where the diagram server listens
	 * @throws IOException
	 *             if no DiagramServer listening at that port could be created
	 */
	public DiagramServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		setName("DiagramServer");
		setDaemon(true);
		threadPool = new PriorityQueue<ServerThread>();
		for (int i = 0; i < poolSize; i++) {
			ServerThread worker = new ServerThread();
			worker.start();
			threadPool.add(worker);
		}
		start();
	}

	private static class ServerThread extends Thread implements
			Comparable<ServerThread> {

		private LinkedList<Socket> queue;

		ServerThread() {
			queue = new LinkedList<Socket>();
			setName("DiagramServer worker thread");
			setDaemon(true);
		}

		synchronized void addJob(Socket socket) {
			queue.addFirst(socket);
			notify();
		}

		public synchronized int compareTo(ServerThread st) {
			return queue.size() - st.queue.size();
		}

		public synchronized boolean equals(Object o) {
			if (!(o instanceof ServerThread)) {
				return false;
			}
			ServerThread st = (ServerThread) o;
			return compareTo(st) == 0;
		}

		public void run() {
			while (true) {
				Socket socket = null;
				synchronized (this) {
					while (queue.isEmpty()) {
						try {
							wait();
						} catch (InterruptedException ignored) {
							/* empty */
						}
					}
					socket = queue.removeLast();
				}
				try {
					InputStreamReader isr = new InputStreamReader(socket
							.getInputStream(), "utf-8");
					BufferedReader reader = new BufferedReader(isr);
					String type = reader.readLine().trim();
					StringBuffer buffer = new StringBuffer();
					String line;
					while (true) {
						line = reader.readLine();
						if (line == null || line.equals("END")) {
							break;
						}
						buffer.append(line + "\n");
					}
					OutputStreamWriter osr = new OutputStreamWriter(socket
							.getOutputStream(), "utf-8");
					PrintWriter pw = new PrintWriter(osr);
					try {
						Exporter exporter = Exporter
								.getExporter(type, null, "A4",
										socket.getOutputStream());
						if (exporter == null) {
							throw new RuntimeException(
									"FreeHEP library missing.");
						}
						Diagram diagram = new Diagram(ConfigurationManager
								.createNewDefaultConfiguration().getDataObject(),
								new TextHandler(buffer.toString()), exporter);
						diagram.generate();
						exporter.export();
					} catch (SyntaxError se) {
						TextHandler th = (TextHandler) se.getProvider();
						String msg = "ERROR:syntax error in line "
								+ th.getLineNumber() + ": " + se.getMessage();
						pw.println(msg);
						pw.flush();
						pw.close();
					} catch (SemanticError se) {
						TextHandler th = (TextHandler) se.getProvider();
						String msg = "ERROR:semantic error in line "
								+ th.getLineNumber() + ": " + se.getMessage();
						pw.println(msg);
						pw.flush();
						pw.close();
					} catch (Throwable t) {
						pw.println("ERROR:fatal error: " + t.getMessage());
						pw.flush();
						pw.close();
					}
					socket.close();
				} catch (Throwable t) {
					t.printStackTrace();
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (Exception ignored) {
							/* empty */
						}
					}
				}
			}
		}
	}

	/**
	 * Blocks until a connection is made, then creates a socket for the
	 * connection and blocks again, waiting for the next connection.
	 */
	public void run() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				ServerThread worker = threadPool.remove();
				worker.addJob(socket);
				threadPool.add(worker);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
