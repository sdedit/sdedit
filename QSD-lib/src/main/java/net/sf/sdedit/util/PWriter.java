package net.sf.sdedit.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PWriter extends PrintWriter {

	private String lineSeparator = System.getProperty("line.separator");

	private String fieldSeparator;

	private StringWriter stringWriter;

	private boolean newLine = true;

	private SimpleDateFormat dateFormat;
	
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	
	public static PWriter create() {
		StringWriter stringWriter = new StringWriter();
		PWriter printWriter = new PWriter(stringWriter);
		printWriter.stringWriter = stringWriter;
		return printWriter;
	}

	public static PWriter forFile(File file, Charset charset)
			throws IOException {
		OutputStream stream = new FileOutputStream(file);
		stream = new BufferedOutputStream(stream);
		OutputStreamWriter writer = new OutputStreamWriter(stream, charset);
		PWriter pwriter = new PWriter(writer);
		return pwriter;
	}
	
	@Override
	public void close () {
		try {
			flush();
		} finally {
			super.close();
		}
	}

	public static PWriter forFile(File file) throws IOException {
		return forFile(file, Charset.defaultCharset());
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

	public String toString() {
		if (stringWriter != null) {
			return stringWriter.toString();
		}
		return super.toString();
	}

	public void setDateFormat(String dateFormat) {
		if (dateFormat == null) {
			this.dateFormat = null;
		} else {
			this.dateFormat = new SimpleDateFormat(dateFormat);
		}
	}

	public PWriter(Writer out, boolean autoFlush, String lineSeparator) {
		super(out, autoFlush);
		this.lineSeparator = lineSeparator;
	}

	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	public String getLineSeparator() {
		return this.lineSeparator;
	}

	public void print(String s) {
		if (newLine) {
			if (dateFormat != null) {
				super.print(dateFormat.format(new Date()));
				super.print(": ");
			}
			newLine = false;
		}
		super.print(s);
	}

	/**
	 * @see java.io.PrintWriter#println()
	 */
	public void println() {
		super.print(lineSeparator);
		newLine = true;
	}

	/**
	 * @see java.io.PrintWriter#println(boolean)
	 */
	public void println(boolean arg0) {
		print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(char)
	 */
	public void println(char arg0) {
		print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(char[])
	 */
	public void println(char[] arg0) {
		print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(double)
	 */
	public void println(double arg0) {
		print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(float)
	 */
	public void println(float arg0) {
		print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(int)
	 */
	public void println(int arg0) {
		print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(long)
	 */
	public void println(long arg0) {
		print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(Object)
	 */
	public void println(Object arg0) {
		print(arg0);
		println();
	}

	/**
	 * @see java.io.PrintWriter#println(String)
	 */
	public void println(String arg0) {
		print(arg0);
		println();
	}

	public String getFieldSeparator() {
		return fieldSeparator;
	}

	public void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

	public void printFields(Object... fields) {
		boolean first = true;
		for (Object field : fields) {
			if (!first) {
				print(fieldSeparator);
			}
			first = false;
			print(field);
		}
		println();
	}
	
}
