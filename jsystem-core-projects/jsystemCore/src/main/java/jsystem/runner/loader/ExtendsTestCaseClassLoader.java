/*
 * Created on 16/05/2005
 * 
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.runner.TestCaseClassLoader; 

/**
 * @author guy.arieli
 */
public class ExtendsTestCaseClassLoader extends ClassLoader {

	/** scanned class path */
	private Vector<String> fPathItems;

	/** default excluded paths */
	private String[] defaultExclusions = { "junit.framework.", "junit.extensions.", "junit.runner.", "jsystem.",
			"org.apache.xpath.", "org.apache.commons.", "org.apache.log4j." , "java.","sun.","javax.", "org.junit.","jdk.internal.reflect." 
	// fix to support use of xpath in java 1.4
	};

	/** name of excluded properties file */
	static final String EXCLUDED_FILE = "excluded.properties";

	/** excluded paths */
	private Vector<String> fExcluded;

	private Vector<String> fIncluded = null;

	private ClassLoader parent;

	/**
	 * Constructs a TestCaseLoader. It scans the class path and the excluded
	 * package paths
	 */
	public ExtendsTestCaseClassLoader(ClassLoader parent) {
		this(System.getProperty("java.class.path"), parent);
	}

	/**
	 * Constructs a TestCaseLoader. It scans the class path and the excluded
	 * package paths
	 */
	public ExtendsTestCaseClassLoader(String classPath, ClassLoader parent) {
		scanPath(classPath);
		readExcludedPackages();
		this.parent = parent;
	}

	private void scanPath(String classPath) {
		String separator = System.getProperty("path.separator");
		fPathItems = new Vector<String>(10);
		StringTokenizer st = new StringTokenizer(classPath, separator);
		while (st.hasMoreTokens()) {
			fPathItems.addElement(st.nextToken());
		}
	}

	public URL getResource(String name) {
		return parent.getResource(name);
	}

	public InputStream getResourceAsStream(String name) {
		try {
			byte[] data = lookupClassData(name, false);
			if (data != null) {
				return new ByteArrayInputStream(data);
			}
		} catch (ClassNotFoundException e) {
		}
		return parent.getResourceAsStream(name);
	}

	public boolean isExcluded(String name) {
		if (fIncluded != null) {
			for (int i = 0; i < fIncluded.size(); i++) {
				if (name.startsWith((String) fIncluded.elementAt(i))) {
					return false;
				}
			}
			return true;
		}

		for (int i = 0; i < fExcluded.size(); i++) {
			if (name.startsWith((String) fExcluded.elementAt(i))) {
				return true;
			}
		}
		return false;
	}

	public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

		Class<?> c = findLoadedClass(name);
		if (c != null)
			return c;
		//
		// Delegate the loading of excluded classes to the
		// standard class loader.
		//
		if (isExcluded(name)) {
			try {
				c = parent.loadClass(name);
				return c;
			} catch (ClassNotFoundException e) {
				// keep searching
			}
		}
		if (c == null) {
			byte[] data = lookupClassData(name, true);
			if (data == null)
				throw new ClassNotFoundException();
			c = defineClass(name, data, 0, data.length);
		}
		if (resolve)
			resolveClass(c);
		return c;
	}

	private byte[] lookupClassData(String className, boolean addClass) throws ClassNotFoundException {
		byte[] data = null;
		String fileName;
		if (addClass) {
			fileName = className.replace('.', '/') + ".class";
		} else {
			fileName = className;
		}
		for (int i = 0; i < fPathItems.size(); i++) {
			String path = fPathItems.elementAt(i);
			if (isJar(path)) {
				data = loadJarData(path, fileName);
			} else {
				data = loadFileData(path, fileName);
			}
			if (data != null)
				return data;
		}
		throw new ClassNotFoundException(className);
	}

	boolean isJar(String pathEntry) {
		return pathEntry.endsWith(".jar") || pathEntry.endsWith(".zip");
	}

	private byte[] loadFileData(String path, String fileName) {
		File file = new File(path, fileName);
		if (file.exists()) {
			return getClassData(file);
		}
		return null;
	}

	private byte[] getClassData(File file) {
		try {
			FileInputStream stream = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			stream.close();
			out.close();
			return out.toByteArray();

		} catch (IOException e) {
		}
		return null;
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
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	private void readExcludedPackages() {
		fExcluded = new Vector<String>(10);
		for (int i = 0; i < defaultExclusions.length; i++)
			fExcluded.addElement(defaultExclusions[i]);

		String loadExclude = JSystemProperties.getInstance().getPreference(FrameworkOptions.LOAD_EXCLUDE);
		if (loadExclude != null) {
			String[] toExclued = loadExclude.split(";");
			for (int i = 0; i < toExclued.length; i++) {
				fExcluded.addElement(toExclued[i]);
			}
		}
		String loadInclude = JSystemProperties.getInstance().getPreference(FrameworkOptions.LOAD_INCLUDE);
		if (loadInclude != null) {
			fIncluded = new Vector<String>(10);
			String[] toInclude = loadInclude.split(";");
			for (int i = 0; i < toInclude.length; i++) {
				fIncluded.addElement(toInclude[i]);
			}
		}
		InputStream is = TestCaseClassLoader.class.getResourceAsStream(EXCLUDED_FILE);
		if (is == null)
			return;
		Properties p = new Properties();
		try {
			p.load(is);
		} catch (IOException e) {
			return;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		for (Enumeration<?> e = p.propertyNames(); e.hasMoreElements();) {
			String key = e.nextElement().toString();
			if (key.startsWith("excluded.")) {
				String path = p.getProperty(key);
				path = path.trim();
				if (path.endsWith("*"))
					path = path.substring(0, path.length() - 1);
				if (path.length() > 0)
					fExcluded.addElement(path);
			}
		}
	}

}
