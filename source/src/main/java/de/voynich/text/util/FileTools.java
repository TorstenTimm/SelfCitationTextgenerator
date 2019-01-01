package de.voynich.text.util;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * helper class that provides methods for reading and writing files
 */
public final class FileTools {

	/**
	 * Hides the public constructor.
	 */
	private FileTools() {
		// empty
	}

	public static String getBaseName(final String resourceName) {

		// if the resource name indicates a folder
		if (resourceName.charAt(resourceName.length() - 1) == File.separatorChar) {
			// folders have no extensions
			return resourceName;
		}
		// get the position of the last dot
		int pos = resourceName.lastIndexOf('.');
		// if no dot or if no chars after the dot
		if ((pos < 0) || ((pos + 1) == resourceName.length())) {
			return resourceName;
		}
		// return the extension
		return resourceName.substring(0, pos ).toLowerCase();
	}

	public static File writeFile(final File file, final byte[] content) throws IOException {
		// write file contents
		OutputStream fs = null;
		try {
			fs = decorateWithBuffer(new FileOutputStream(file));
			fs.write(content);
			fs.flush();
			return file;
		} finally {
			saveClose(fs);
		}
	}

	public static OutputStream decorateWithBuffer(final OutputStream outStream) {
		if (outStream == null)
			throw new IllegalStateException("NullPointer");

		if (! (outStream instanceof BufferedOutputStream)) {
			return new BufferedOutputStream(outStream);
		}

		return outStream;
	}

	public static void saveClose(final Closeable closeable) {
		if (closeable == null)
			return;

		try {
			closeable.close();
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}

	public static boolean saveFile(final String fileName, final String daten) {
		File file = new File(fileName);
		if (file.exists()) {
			System.out.println("The file " + fileName + "' already exists.");
		}
		try {
			writeFile(file, daten.getBytes());
			return true;
		} catch (IOException e) {
			System.out.println("ERROR: Unable to write file '" + fileName + "': " + e.getMessage());
		}
		return false;
	}
}