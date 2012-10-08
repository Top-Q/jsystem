/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jsystem.framework.JSystemProperties;
import jsystem.framework.fixture.Fixture;
import jsystem.runner.loader.LoadersManager;

public class ClassPathFixtureCollector extends ClassPathTestCollector {
	private static Logger log = Logger.getLogger(ClassPathFixtureCollector.class.getName());

	static final int SUFFIX_LENGTH = ".class".length();

	/**
	 * collect Fixtures only the the test class folder.
	 */
	public Enumeration<String> collectTests() {
		String classPath= JSystemProperties.getCurrentTestsPath();
		Hashtable<String,String> result = collectFilesInPath(classPath);
		return result.elements();
	}

	public boolean isTestClass(String classFileName) {
		/*
		 * If the file don't end with .class of $ is found it's not a fixture class
		 */
		if(!classFileName.endsWith(".class") || classFileName.indexOf('$') >= 0){
			return false;
		}
		/*
		 * Create the class name
		 */
		String className = classNameFromFile(classFileName);
		try {
			/*
			 * Load the class and check Fixture is assignable for it.
			 */
			Class<?> c = LoadersManager.getInstance().getLoader().loadClass(className);
			if(c == null){
				return false;
			}
			if(Fixture.class.isAssignableFrom(c)){
				return true;
			}
		} catch (Throwable e) {
			return false;
		}
		
		return false;
	}

	public boolean isJarFile(String fileName) {
		return fileName.endsWith(".jar");
	}

	public Hashtable<String,String> collectFilesInPath(String classPath) {
		Hashtable<String,String> result = _collectFilesInRoots(_splitClassPath(classPath));
		return result;
	}

	Hashtable<String,String> _collectFilesInRoots(Vector<String> roots) {
		Hashtable<String,String> result = new Hashtable<String,String>(100);
		Enumeration<String> e = roots.elements();
		while (e.hasMoreElements())
			_gatherFiles(new File((String) e.nextElement()), "", result);
		return result;
	}

	void _gatherFiles(File classRoot, String classFileName, Hashtable<String, String> result) {
		File thisRoot = new File(classRoot, classFileName);
		if (thisRoot.isFile()) {
			if (isTestClass(classFileName)) {
				String className = classNameFromFile(classFileName);
				result.put(className, className);
			} else if (isJarFile(thisRoot.getPath())) {
				try {
					ZipFile zipFile = new ZipFile(thisRoot.getPath());
					Enumeration<? extends ZipEntry> enum1 = zipFile.entries();
					while (enum1.hasMoreElements()) {
						ZipEntry entry = (ZipEntry) enum1.nextElement();
						String fileName = entry.getName();
						if (isTestClass(fileName)) {
							String name = classNameFromFile(fileName);
							result.put(name, name);
						}
					}
				} catch (IOException e) {
					log.log(Level.WARNING, "fail to open zip file " + thisRoot.getPath(), e);
				}

			}
			return;
		}
		String[] contents = thisRoot.list();
		if (contents != null) {
			for (int i = 0; i < contents.length; i++)
				_gatherFiles(classRoot, classFileName + File.separatorChar + contents[i], result);
		}
	}

	Vector<String> _splitClassPath(String classPath) {
		Vector<String> result = new Vector<String>();
		String separator = System.getProperty("path.separator");
		StringTokenizer tokenizer = new StringTokenizer(classPath, separator);
		while (tokenizer.hasMoreTokens())
			result.addElement(tokenizer.nextToken());
		return result;
	}

	protected String classNameFromFile(String classFileName) {
		// convert /a/b.class to a.b
		String s = classFileName.substring(0, classFileName.length() - SUFFIX_LENGTH);
		String s2 = s.replace('/', '.');
		String s3 = s2.replace('\\', '.');
		if (s3.startsWith("."))
			return s3.substring(1);
		return s3;
	}

	public static void main(String[] args) {
		ClassPathFixtureCollector fc = new ClassPathFixtureCollector();
		Enumeration<String> enum1 = fc.collectTests();
		while (enum1.hasMoreElements()) {
			System.out.println(enum1.nextElement());
		}
	}
}
