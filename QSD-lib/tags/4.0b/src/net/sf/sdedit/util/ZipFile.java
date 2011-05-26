package net.sf.sdedit.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFile {

	private File file;

	private boolean createNew;

	private ZipOutputStream zos;

	private ZipInputStream zis;

	public ZipFile(File file, boolean createNew) throws IOException {
		this.file = file;
		this.createNew = createNew;
		if (createNew) {
			openNew();

		} else {
			openExisting();
		}
	}

	protected void openNew() throws IOException {
		OutputStream os = new FileOutputStream(file);
		zos = new ZipOutputStream(os);
	}

	protected void openExisting() throws IOException {
		InputStream is = new FileInputStream(file);
		zis = new ZipInputStream(is);
	}

	public void addFile(File file) throws IOException {
		if (!createNew) {
			throw new IllegalStateException("ZipFile in is read-file mode");
		}
		InputStream fis = new FileInputStream(file);
		try {
			BufferedInputStream bis = new BufferedInputStream(fis);
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zos.putNextEntry(zipEntry);
			Utilities.pipe(bis, zos);
			zos.closeEntry();
		} finally {
			fis.close();
		}
	}

	public void close() throws IOException {
		if (!createNew) {
			throw new IllegalStateException("ZipFile is in read-file mode");
		}
		zos.flush();
		zos.close();
	}

	public String getNextEntry() throws IOException {
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
		Utilities.pipe(zis, out);
	}

	public void storeFiles(File directory) throws IOException {
		if (createNew) {
			throw new IllegalStateException(
					"ZipFile is in create-new-file mode");
		}
		try {
			String name;
			while ((name = getNextEntry()) != null) {
				File outFile = new File(directory, name);
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

	public static void main(String[] argv) throws Exception {

		ZipFile f = new ZipFile(new File("C:/Temp/ZipFile.Zip"), false);

		f.storeFiles(new File("C:/Temp"));

		// f.addFile(new File("C:/Temp/log.txt"));
		// f.addFile(new File("C:/Temp/og.txt"));

		// f.close();
		// f.storeFiles(new File("C:/Temp"));

	}

}
