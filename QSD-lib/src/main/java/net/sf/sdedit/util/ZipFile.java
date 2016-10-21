// Copyright (c) 2006 - 2016, Markus Strauch.
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
package net.sf.sdedit.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFile {

	private File file;

	private ZipOutputStream zos;

	private ZipInputStream zis;

	private Charset charset;

	private File rootDir;

	public ZipFile(File file, boolean createNew, Charset charset) throws IOException {
		this.file = file;
		this.charset = charset;
		if (createNew) {
			zos = openNew();
		} else {
			zis = openExisting();
		}
	}

	public ZipFile(File file, boolean createNew) throws IOException {
		this(file, createNew, null);
	}

	public void setRootDirectory(File rootDirectory) {
		this.rootDir = rootDirectory;
	}

	public static void createFromFiles(File file, File... files) throws IOException {
		if (files != null) {
			ZipFile zipFile = new ZipFile(file, true);
			for (File f : files) {
				zipFile.addFile(f);
			}
			zipFile.close();
		}
	}

	@Deprecated
	public static void createFromFlatDirectory(File file, File dir) throws IOException {
		createFromDirectory(file, dir, Charset.defaultCharset());
	}
	
	public static void createFromDirectory(File zipFile, File dir, Charset charset) throws IOException {
		ZipFile zip = new ZipFile(zipFile, true);
		zip.setRootDirectory(dir);
		zip.addFile(dir);
		zip.close();
	}

	protected ZipOutputStream openNew() throws IOException {
		OutputStream os = new FileOutputStream(file);
		ZipOutputStream stream;
		if (charset == null) {
			stream = new ZipOutputStream(os);
		} else {
			stream = Utilities.newInstance(ZipOutputStream.class, os, charset);
		}
		return stream;
	}

	protected ZipInputStream openExisting() throws IOException {
		InputStream is = new FileInputStream(file);
		ZipInputStream stream;
		if (charset == null) {
			stream = new ZipInputStream(is);
		} else {
			stream = Utilities.newInstance(ZipInputStream.class, is, charset);
		}
		return stream;
	}

	public void addFile(File file) throws IOException {
		if (zos == null) {
			throw new IllegalStateException("ZipFile in is read-file mode");
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					addFile(f);
				}
			}
		} else {
			InputStream fis = new FileInputStream(file);
			try {
				BufferedInputStream bis = new BufferedInputStream(fis);
				String name = file.getName();
				if (rootDir != null) {
					name = file.getAbsolutePath().substring(rootDir.getAbsolutePath().length() + 1);
				}
				ZipEntry zipEntry = new ZipEntry(name);
				zos.putNextEntry(zipEntry);
				Utilities.pipe(bis, zos);
				zos.closeEntry();
			} finally {
				fis.close();
			}
		}
	}

	public void close() throws IOException {
		if (zos != null) {
			zos.flush();
			zos.close();
		}
	}

	public String getNextEntry() throws IOException {
		if (zis == null) {
			throw new IllegalStateException("ZipFile is in create-new-file mode");
		}
		for (;;) {
			ZipEntry zipEntry = zis.getNextEntry();
			if (zipEntry == null) {
				return null;
			}
			if (!zipEntry.isDirectory()) {
				return zipEntry.getName();
			}
		}
	}

	public void storeNextEntry(OutputStream out) throws IOException {
		if (zis == null) {
			throw new IllegalStateException("ZipFile is in create-new-file mode");
		}
		Utilities.pipe(zis, out);
	}

	public void storeFiles(File directory, String regex, String replacement) throws IOException {
		if (zis == null) {
			throw new IllegalStateException("ZipFile is in create-new-file mode");
		}
		directory.mkdirs();
		try {
			String name;
			while ((name = getNextEntry()) != null) {
				if (regex != null) {
					name = name.replaceAll(regex, replacement);
				}
				File outFile = new File(directory, name);
				outFile.getParentFile().mkdirs();
				OutputStream out = new FileOutputStream(outFile);
				try {
					storeNextEntry(out);
				} finally {
					out.close();
				}
			}
		} finally {
			zis.close();
		}
	}
	
	public void storeFiles(File directory) throws IOException {
		storeFiles(directory, null, null);
	}
}
