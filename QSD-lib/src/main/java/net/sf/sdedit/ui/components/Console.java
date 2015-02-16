//Copyright (c) 2006 - 2015, Markus Strauch.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.
package net.sf.sdedit.ui.components;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Console extends JTextArea {

	private StreamReader reader1;

	private StreamReader reader2;

	private boolean stopped;

	private class StreamReader implements Runnable {

		private InputStream stream;

		private BufferedReader bufferedReader;

		private boolean errorStream;

		StreamReader(InputStream stream, BufferedReader bufferedReader,
				boolean errorStream) {
			this.bufferedReader = bufferedReader;
			this.stream = stream;
			this.errorStream = errorStream;
			setFont(Font.decode("Monospaced"));
		}

		public void run() {
			try {
				_run();
			} catch (RuntimeException re) {
				throw re;
			} catch (Exception e) {
				if (!stopped) {
					e.printStackTrace();
				}
			}
		}

		public void _run() throws IOException {
			try {
				String line = bufferedReader.readLine();
				while (line != null) {
					_append(line, errorStream);
					line = bufferedReader.readLine();
				}
			} finally {
				stream.close();
			}
		}

	}

	public void setStopped() {
		stopped = true;
	}

	public void stop() throws IOException {
		reader1.stream.close();
		reader2.stream.close();
	}

	public Console(Process process, String encoding) {
		try {
			init(process, encoding);
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			e.printStackTrace();
		}
		setEditable(false);
	}

	private void _append(final String line, boolean errorStream) {
		if (isEnabled() || errorStream) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (getText().length() > 0) {
						append("\n" + line);
					} else {
						append(line);
					}
				}
			});
		}
	}

	public void start() {
		new Thread(reader1).start();
		new Thread(reader2).start();
	}

	private void init(Process process, String encoding)
			throws UnsupportedEncodingException {
		BufferedReader r1 = new BufferedReader(new InputStreamReader(process
				.getInputStream(), encoding));
		BufferedReader r2 = new BufferedReader(new InputStreamReader(process
				.getErrorStream(), encoding));
		reader1 = new StreamReader(process.getInputStream(), r1, false);
		reader2 = new StreamReader(process.getErrorStream(), r2, true);
	}

}
