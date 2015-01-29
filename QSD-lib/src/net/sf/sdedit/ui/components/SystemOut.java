package net.sf.sdedit.ui.components;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class SystemOut extends JTextArea {

	private static final long serialVersionUID = -5785835682160622983L;

	public final PrintStream OLD_OUT;

	public final PrintStream OLD_ERR;
	
	private OutputStream out = new OutputStream() {

		@Override
		public void write(final int b) throws IOException {
			if (SwingUtilities.isEventDispatchThread()) {
				SystemOut.this.append(String.valueOf((char) b));
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						SystemOut.this.append(String.valueOf((char) b));
					}
				});
			}
		}
	};
	
	public SystemOut() {
		BufferedOutputStream o = new BufferedOutputStream(out);
		PrintStream stream = new PrintStream(o, true);
		OLD_OUT = System.out;
		OLD_ERR = System.err;
		System.setOut(stream);
		System.setErr(stream);
	}

}
