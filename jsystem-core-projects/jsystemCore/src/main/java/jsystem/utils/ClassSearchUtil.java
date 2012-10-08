/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jsystem.runner.loader.ExtendsTestCaseClassLoader;

/**
 * Utility class for classpath related operations.
 * 
 * @author guy.arieli & Golan Derazon
 */
public class ClassSearchUtil {
	/**
	 * Used to scan a class path (jars and folders) and search for a specific implementation for a class.
	 * @param ofType the class to search implementations for.
	 * @param classPath the class path to search in.
	 * @param filterAbstract if set to true will filter abstract implementations.
	 * @param ignoreList a list of string that if found in the class path element name will ignore it.
	 * @return an array with all the class name implementations found.
	 * @throws Exception when the search process fail.
	 */
	public static ArrayList<String> searchClasses(Class<?> ofType, String classPath, boolean filterAbstract, String[] ignoreList, String[] includeList) throws Exception{
		GeneralCollector gc = new GeneralCollector(ofType, classPath, filterAbstract, ignoreList, includeList);
		ArrayList<String> tv = gc.collectTestsVector();
		return tv;
	}
	
	/**
	 * Fetches a property from a properties file in the classpath.
	 * If properties file not founf in class path the method throws an <code>IOException</code>.
	 * If property doesn't exist in properties file, <code>null</code> is returned.
	 * @param propertyFileResourcePath path of the properties file in the class path
	 * @param name of property to fetch
	 */
	public static String getPropertyFromClassPath(String propertyFileResourcePath,String propertyName ) throws Exception {
		Properties p = new Properties();
		InputStream inStream = null;
		try {
			inStream = ClassSearchUtil.class.getClassLoader().getResourceAsStream(propertyFileResourcePath);
			if (inStream == null){
				throw new IOException("Property file not found in classpath. " + propertyFileResourcePath );
			}
			p.load(inStream);
		} finally{
			if (inStream != null){
				inStream.close();
			}
		}
		return p.getProperty(propertyName);
	}

}

class GeneralCollector {
	private static Logger log = Logger.getLogger(GeneralCollector.class.getName());
	private Class<?> ofType = null;
	private String classPath;
	private boolean filterAbstract;
	private ClassLoader loader;
	private String[] ignoreList = null;
	private String[] includeList = null;
	public GeneralCollector(Class<?> ofType, String classPath, boolean filterAbstract, String[] ignoreList, String[] includeList) throws Exception{
		this.classPath = classPath;
		this.filterAbstract = filterAbstract;
		loader = new ExtendsTestCaseClassLoader(classPath, getClass().getClassLoader().getParent());
		this.ignoreList = ignoreList;
		this.includeList = includeList;
		this.ofType = loader.loadClass(ofType.getName());
	}

	public Enumeration<String> collectTests() {
		Hashtable<String, String> result = collectFilesInPath(classPath);
		return result.elements();
	}

	public ArrayList<String> collectTestsVector() {
		Enumeration<String> en = collectTests();
		ArrayList<String> v = new ArrayList<String>(10);
		
		while (en.hasMoreElements()) {
			v.add(en.nextElement());
		}

		return v;
	}

	public boolean isTestClass(String classFileName) {
		Class<?> c;
		if(classFileName == null || classFileName.length() < 7){
			return false;
		}
		String className = classFileName.substring(1, classFileName.length() - 6).replace('\\', '.').replace('/', '.');
		try {
			c = loader.loadClass(className);
		} catch (Throwable e) {
			return false;
		}
		if(filterAbstract && Modifier.isAbstract(c.getModifiers())){
			return false;
		}
		if(c == null){
			return false;
		}
		return ofType.isAssignableFrom(c);
	}

	protected String classNameFromFile(String classFileName) {
		return classFileName.substring(1, classFileName.length() - 6).replace('/', '.').replace('\\', '.');
	}
	
	public Hashtable<String,String> collectFilesInPath(String classPath) {
		Hashtable<String, String> result= collectFilesInRoots(splitClassPath(classPath));
		return result;
	}
	
	private Hashtable<String, String> collectFilesInRoots(Vector<String> roots) {
		Hashtable<String, String> result= new Hashtable<String, String>(100);
		Enumeration<String> e= roots.elements();
		whilecont:
		while (e.hasMoreElements()) {
			String classElement = (String)e.nextElement();
			if(includeList != null){
				boolean found = false;
				for(String toInclude: includeList){
					if(classElement.toLowerCase().endsWith(".jar")){
						if(classElement.toLowerCase().contains(toInclude.toLowerCase())){
							found = true;
							break;
						}
					} else{
						found = true;
						break;
					}
				}
				if(!found){
					continue;
				}
			} else if(ignoreList != null){
				for(String toIgnore: ignoreList){
					if(classElement.toLowerCase().contains(toIgnore.toLowerCase())){
						continue whilecont;
					}
				}
			}
			try {
				gatherFiles(new File(classElement), "", result);
			} catch (Exception e1) {
				log.log(Level.INFO, "Fail to process file", e1);
			}
		}
		return result;
	}

	private void gatherFiles(File classRoot, String classFileName, Hashtable<String, String> result) throws Exception {
		File thisRoot= new File(classRoot, classFileName);
		if(Thread.interrupted()){
			throw new InterruptedException();
		}
		if (thisRoot.isFile()) {
			if(thisRoot.getName().toLowerCase().endsWith(".jar")){
				processJar(thisRoot, result);
			}
			if (isTestClass(classFileName)) {
				String className= classNameFromFile(classFileName);
				result.put(className, className);
			}
			return;
		}		
		String[] contents= thisRoot.list();
		if (contents != null) { 
			for (int i= 0; i < contents.length; i++) 
				gatherFiles(classRoot, classFileName+File.separatorChar+contents[i], result);		
		}
	}
	
	private void processJar(File jarFile, Hashtable<String, String> result) throws Exception{
		ZipFile zipFile = new ZipFile(jarFile);
		Enumeration<? extends ZipEntry> enties = zipFile.entries();
		while (enties.hasMoreElements()){
			if(Thread.interrupted()){
				throw new InterruptedException();
			}
			ZipEntry entry = (ZipEntry)enties.nextElement();
			if(entry.isDirectory()){
				continue;
			}
			if (isTestClass("/" + entry.getName())) {
				String className= classNameFromFile("/" + entry.getName());
				result.put(className, className);
			}
		}
	}
	
	private Vector<String> splitClassPath(String classPath) {
		Vector<String> result= new Vector<String>();
		String separator= System.getProperty("path.separator");
		StringTokenizer tokenizer= new StringTokenizer(classPath, separator);
		while (tokenizer.hasMoreTokens()) 
			result.addElement(tokenizer.nextToken());
		return result;
	}
	
}
