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
