package net.sf.sdedit.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class PWriter extends PrintWriter {

	private String lineSeparator = System.getProperty("line.separator");
	
	private StringWriter stringWriter;
	
	public static PWriter create() {
		StringWriter stringWriter = new StringWriter();
		PWriter printWriter = new PWriter(stringWriter);
		printWriter.stringWriter = stringWriter;
		return printWriter;
	}

	public PWriter(OutputStream out) {
		super(out);
	}

	public PWriter(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}

	public PWriter(Writer out) {
		super(out);
	}
	
	public String toString () {
		if (stringWriter != null) {
			return stringWriter.toString();
		}
		return super.toString();
	}

	public PWriter(Writer out, boolean autoFlush,
			String lineSeparator) {
		super(out, autoFlush);
		this.lineSeparator = lineSeparator;
	}
	
	public void setLineSeparator (String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}
	
	public String getLineSeparator () {
		return this.lineSeparator;
	}

	/**
	 * @see java.io.PrintWriter#println()
	 */
	public void println() {
		super.print(lineSeparator);
	}	

	/**
	 * @see java.io.PrintWriter#println(boolean)
	 */
	public void println(boolean arg0) {
		super.print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(char)
	 */
	public void println(char arg0) {
		super.print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(char[])
	 */
	public void println(char[] arg0) {
		super.print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(double)
	 */
	public void println(double arg0) {
		super.print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(float)
	 */
	public void println(float arg0) {
		super.print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(int)
	 */
	public void println(int arg0) {
		super.print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(long)
	 */
	public void println(long arg0) {
		super.print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(Object)
	 */
	public void println(Object arg0) {
		super.print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(String)
	 */
	public void println(String arg0) {
		super.print(arg0);
		println();
	}

}

