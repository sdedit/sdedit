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

package net.sf.sdedit.util.base64;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

public class Base64 {

	private static int BUFFER_SIZE = 8192;

	private static String encode(byte[] in) {
		return new String(Base64Coder.encode(in));
	}

	public static String encode(InputStream stream) throws IOException {
		List<byte[]> buffers = new LinkedList<byte[]>();
		int mark = 0;
		try {
			stream = new BufferedInputStream(stream);
			byte[] buffer = new byte[BUFFER_SIZE];
			buffers.add(buffer);
			int avail = -1;
			while (avail != 0) {
				avail = stream.available();
				if (avail > 0) {
					int toBeRead = Math.min(avail, BUFFER_SIZE - mark);
					stream.read(buffer, mark, toBeRead);
					mark += toBeRead;
				}
				if (mark == BUFFER_SIZE) {
					buffer = new byte[BUFFER_SIZE];
					buffers.add(buffer);
					mark = 0;
				}
			}
		} finally {
			stream.close();
		}

		byte[] result = new byte[(buffers.size() - 1) * BUFFER_SIZE + mark];
		int i = 0;
		for (byte[] buffer2 : buffers) {
			int len = i == buffers.size() - 1 ? mark : BUFFER_SIZE;
			System.arraycopy(buffer2, 0, result, i * BUFFER_SIZE, len);
			i++;
		}
		return new String(Base64Coder.encode(result));
	}

	public static String createAssignment(InputStream stream, int len)
			throws IOException {
		StringBuffer ass = new StringBuffer();
		String code = encode(stream);
		for (int i = 0; i < code.length(); i += len) {
			int l = Math.min(len, code.length() - i);
			String line = '"' + code.substring(i, i + l) + '"';
			if (ass.length() == 0) {
				ass.append(line);
			} else {
				ass.append("\n+ " + line);
			}
		}
		return ass.toString();
	}

	public static Image decodeBase64EncodedImage(String code) {
		byte[] decoded = Base64Coder.decode(code);
		ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
		Image img;
		try {
			img = ImageIO.read(bais);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Cannot create image from string", e);
		}
		return img;
	}

	public static void main(String[] argv) throws Exception {
		URL url = new URL(argv[0]);
		InputStream in = url.openStream();
		String code = createAssignment(in, 55);
		System.out.println(code);
	}

}
