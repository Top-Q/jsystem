package com.aqua.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUtils extends jsystem.utils.FileUtils {

	private FileUtils() {
		// Utils class
	}

	public static String readFromPosition(final File file, final long fromPosition) {
		RandomAccessFile rfile = null;
		StringBuilder sb = null;
		try {
			rfile = new RandomAccessFile(file, "r");
			rfile.seek(fromPosition);
			sb = new StringBuilder();
			String line = null;
			while ((line = rfile.readLine()) != null) {
				sb.append(line);
			}

		} catch (Exception e) {
		}

		finally {
			try {
				rfile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

}
