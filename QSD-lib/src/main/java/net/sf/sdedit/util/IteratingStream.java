package net.sf.sdedit.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;

public class IteratingStream<T> extends InputStream implements Enumeration<InputStream> {
	
	private final Iterator<T> iterator;
	
	private boolean done;
	
	private boolean first;	
	
	private InputStream stream;

	private final byte[] prefix;

	private final byte[] separator;

	private final byte[] suffix;
	
	private final byte[] suffixOfEmptyStream;

	private final Function<T, byte[]> serializer;

	private Runnable close;
	
	public IteratingStream(Iterator<T> iterator, byte[] prefix, byte[] separator, byte[] suffix, Function<T,byte[]> serializer, byte[] suffixOfEmptyStream) {
		this.iterator = iterator;
		first = true;
		this.prefix = prefix;
		this.separator = separator;
		this.suffix = suffix;
		this.serializer = serializer;
		this.suffixOfEmptyStream = suffixOfEmptyStream;
		stream = new SequenceInputStream(this);
	}
	
	public IteratingStream(Iterator<T> iterator, byte[] prefix, byte[] separator, byte[] suffix, Function<T,byte[]> serializer) {
		this(iterator,prefix,separator,suffix,serializer,null);
	}
	
	public void onClose(Runnable close) {
		this.close = close;
	}

	@Override
	public boolean hasMoreElements() {
		if (done) {
			return false;
		}
		done = !iterator.hasNext();
		if (done && close != null) {
			close.run();
		}
		return true;
	}
	
    private static byte[] addAll(byte[] array1, byte[] array2) {
        byte[] joinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
	
	@Override
	public InputStream nextElement() {
		byte[] dat;
		if (done) {
			if (first && suffixOfEmptyStream != null) {
				dat = suffixOfEmptyStream;
			} else {
				dat = suffix;		
			}
		} else {
			dat = this.serializer.apply(iterator.next());
		}
		if (first) {
			dat = addAll(prefix, dat);
		} else if (!done) {
			dat = addAll(separator, dat);
		}
		first = false;
		return new ByteArrayInputStream(dat);
	}
	
	////////////////////////////////////////////////
	
	public int read() throws IOException {
		return stream.read();
	}

	public int read(byte[] b) throws IOException {
		return stream.read(b);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return stream.read(b, off, len);
	}

	public long skip(long n) throws IOException {
		return stream.skip(n);
	}

	public int available() throws IOException {
		return stream.available();
	}

	public void close() throws IOException {
		stream.close();
	}

	public void mark(int readlimit) {
		stream.mark(readlimit);
	}

	public void reset() throws IOException {
		stream.reset();
	}

	public boolean markSupported() {
		return stream.markSupported();
	}

}
