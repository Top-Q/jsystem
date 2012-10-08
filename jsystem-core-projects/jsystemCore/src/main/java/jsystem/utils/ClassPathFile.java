/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * ClassPathFile work with file's class paths.
 */
public class ClassPathFile {
	private static Logger log = Logger.getLogger(ClassPathFile.class.getName());

	private Vector<String> fPathItems;

	/**
	 * Creates ClassPathFile with default Java class path.
	 */
	public ClassPathFile() {
		this(System.getProperty("java.class.path"));
	}

	/**
	 * Constructs a TestCaseLoader. It scans the class path and the excluded
	 * package paths
	 */
	public ClassPathFile(String classPath) {
		scanPath(classPath);
	}

	/**
	 * Read and return file in String form.
	 * 
	 * @param fileName
	 *            file to read
	 */
	public String getFileAsString(String fileName) throws FileNotFoundException {
		return new String(getFile(fileName));
	}

	/**
	 * Read and return file in byte form.
	 * 
	 * @param fileName
	 *            file to read
	 */
	public byte[] getFile(String fileName) throws FileNotFoundException {
		byte[] data = null;
		for (int i = 0; i < fPathItems.size(); i++) {
			String path = (String) fPathItems.elementAt(i);
			// System.out.println(path);
			if (isJar(path)) {
				data = loadJarData(path, fileName);
			} else {
				data = loadFileData(path, fileName);
			}
			if (data != null)
				return data;
		}
		throw new FileNotFoundException(fileName);
	}

	/**
	 * Check if pathEntry is jar/zip file or otherwise.
	 */
	boolean isJar(String pathEntry) {
		return pathEntry.endsWith(".jar") || pathEntry.endsWith(".zip");
	}

	private byte[] loadJarData(String path, String fileName) {
		ZipFile zipFile = null;
		InputStream stream = null;
		File archive = new File(path);
		if (!archive.exists())
			return null;
		try {
			zipFile = new ZipFile(archive);
		} catch (IOException io) {
			log.log(Level.WARNING, "Fail to load file: " + archive.getPath(), io);
			return null;
		}
		ZipEntry entry = zipFile.getEntry(fileName);
		if (entry == null)
			return null;
		int size = (int) entry.getSize();
		try {
			stream = zipFile.getInputStream(entry);
			byte[] data = new byte[size];
			int pos = 0;
			while (pos < size) {
				int n = stream.read(data, pos, data.length - pos);
				pos += n;
			}
			zipFile.close();
			return data;
		} catch (IOException e) {
			log.log(Level.WARNING, "Fail to load data from jar: " + archive.getPath(), e);
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	private byte[] loadFileData(String path, String fileName) {
		File file = new File(path, fileName);
		if (file.exists()) {
			return getClassData(file);
		}
		return null;
	}

	private byte[] getClassData(File f) {
		try {
			FileInputStream stream = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			stream.close();
			out.close();
			return out.toByteArray();

		} catch (IOException e) {
			log.log(Level.WARNING, "Fail to load file: " + f.getPath(), e);
		}
		return null;
	}

	private void scanPath(String classPath) {
		String separator = System.getProperty("path.separator");
		fPathItems = new Vector<String>(10);
		StringTokenizer st = new StringTokenizer(classPath, separator);
		while (st.hasMoreTokens()) {
			fPathItems.addElement(st.nextToken());
		}
	}

	/**
	 * Read file names include ziped files from directory and class paths set in
	 * constructor. If no file was found, then is return array with zero length.
	 * 
	 * @param dir
	 *            Directory to scan
	 * @return file names
	 */
	public String[] listFile(String dir) throws IOException {
		File dirFile = new File(dir);
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < fPathItems.size(); i++) {
			String path = (String) fPathItems.elementAt(i);
			Enumeration<? extends ZipEntry> enum1;
			if (isJar(path)) {
				// System.out.println(path);
				ZipFile zf = new ZipFile(path);
				enum1 = zf.entries();
				while (enum1.hasMoreElements()) {
					String entry = ((ZipEntry) enum1.nextElement()).getName();
					if (dirFile.equals((new File(entry)).getParentFile())) {
						list.add(entry);
					}
				}
			} else {
				String[] files = FileUtils.listFiles(new File(path, dir));
				for (int j = 0; j < files.length; j++) {
					list.add(files[j]);
				}
			}
		}
		return (String[]) list.toArray(new String[0]);
	}

	/**
	 * When given a jar full name (as appears in classpath), 
	 * the method returns jar version data
	 */
	public String getJarVersionData(String jarFullPath) throws Exception {
		File file = new File(jarFullPath);
		if (!file.exists()){
			throw new FileNotFoundException("File not found: " + jarFullPath);
		}
		JarFile jarFile = new JarFile(jarFullPath);
		ZipEntry manifestEntry = jarFile.getEntry("META-INF/MANIFEST.MF");
		if (manifestEntry == null) {
			return "";
		}
		InputStream in = jarFile.getInputStream(manifestEntry);
		try {
			Properties p = new Properties();
			p.load(in);
			Object ver = p.get("Specification-Version");
			if (ver == null || StringUtils.isEmpty(ver.toString())) {
				ver = p.get("Implementation-Version");
			}
			return ver == null || StringUtils.isEmpty(ver.toString()) ? "":ver.toString();
		}finally{
			in.close();
		}
	}
}
