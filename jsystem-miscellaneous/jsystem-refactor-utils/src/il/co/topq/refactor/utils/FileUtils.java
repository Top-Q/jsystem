package il.co.topq.refactor.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

	public static void copyFile(File source, File destination) throws IOException {
		if ((destination.getParentFile() != null) && (!destination.getParentFile().exists())) {

			destination.getParentFile().mkdirs();
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {

			fis = new FileInputStream(source);
			fos = new FileOutputStream(destination);

			byte[] buffer = new byte[1024 * 4];
			int n = 0;

			while ((n = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, n);
			}

		} finally {
			closeStream(fis);
			closeStream(fos);
		}
	}

	public static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				System.out.println("Failed to close stream !");
			}
		}
	}

	public static void copyDirectory(File source, File destination, String endWith) throws IOException {
		if (source.exists() && source.isDirectory()) {
			if (!destination.exists()) {
				destination.mkdirs();
			}

			File[] fileArray = source.listFiles();

			for (int i = 0; i < fileArray.length; i++) {
				if (fileArray[i].isDirectory()) {
					copyDirectory(fileArray[i],
							new File(destination.getPath() + File.separator + fileArray[i].getName()), endWith);
				} else {
					if (endWith != null) {
						if (!fileArray[i].getPath().toLowerCase().endsWith(endWith.toLowerCase())) {
							continue;
						}
					}
					copyFile(fileArray[i], new File(destination.getPath() + File.separator + fileArray[i].getName()));
				}
			}
		}
	}

	public static void copyDirectory(File source, File destination) throws IOException {
		copyDirectory(source, destination, null);
	}

	/**
	 * Recursive delete of a directory and all it's content
	 * 
	 * @param directory
	 */
	public static void deltree(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File[] fileArray = directory.listFiles();
			if (fileArray != null) {
				for (int i = 0; i < fileArray.length; i++) {
					if (fileArray[i].isDirectory()) {
						deltree(fileArray[i]);
					} else {
						fileArray[i].delete();
					}
				}
			}

			directory.delete();
		}
	}

}
