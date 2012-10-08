/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.runner.loader.LoadersManager;
import jsystem.utils.FileUtils;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

/**
 * This class give general code base services.
 * To get the service use the static method: <code>HtmlCodeWriter.getInstance()</code>. Use <code>init</code> to reset.<p>
 * Following code services:<br>
 * 1. getClassJavaDoc - return the class documentation.<br>
 * 2. getMethodJavaDoc - return the method documentation.<br>
 * 3. getMethodAnnotation - return the method doclet tag.<br>
 * 4. getCode - take tests sources and convert it to html and present it as
 * part of the report. the attribute 'tests.src' is used to find whare is the
 * tests sources located.<br>
 * 
 * @author guy.arieli
 * 
 */
public class HtmlCodeWriter {
	private static Logger log = Logger.getLogger(HtmlCodeWriter.class.getName());

	private static HtmlCodeWriter writer;

	public static HtmlCodeWriter getInstance() {
		if (writer == null) {
			writer = new HtmlCodeWriter();
		}
		return writer;
	}

	public static void init() {
		if(writer != null){
			writer.close();
		}
		writer = null;
	}
	/**
	 * Contain all the sources to parse.
	 */
	JavaDocBuilder docBuilder = null;
	File srcDir;
	
	/**
	 * Hold all the files that were loaded to the doc builder and the last time
	 * they were modified.
	 * Only updated file will be loaded.
	 */
	HashMap<File, Long> filesTime;
	
	private HtmlCodeWriter() {
		/*
		 * Find the source folder
		 */
		String testsSourceFolder = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_SOURCE_FOLDER);
		if (testsSourceFolder == null) {
			String testsClassFolderName = null;
			try {
				testsClassFolderName = JSystemProperties.getCurrentTestsPath();
			} catch (Exception e1) {
				// can't find the current test pass
				log.log(Level.WARNING,"Failed to get current tests path");
			}
			if (testsClassFolderName != null) {
				File testsClassFolder = new File(testsClassFolderName);
				if (new File(testsClassFolder.getParent(),"tests").exists()){
					//We are in a Ant structured project
					testsSourceFolder = (new File(testsClassFolder.getParent(), "tests")).getPath();
					JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_SOURCE_FOLDER, testsSourceFolder);
					JSystemProperties.getInstance().setPreference(FrameworkOptions.RESOURCES_SOURCE_FOLDER, testsSourceFolder);					
				}else {
					//We are in a Maven structured project
					testsSourceFolder = (new File(testsClassFolder.getParentFile().getParentFile(), "src/main/java")).getPath();
					JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_SOURCE_FOLDER, testsSourceFolder);
					String resourcesSourceFolder = (new File(testsClassFolder.getParentFile().getParentFile(), "src/main/resources")).getPath();
					JSystemProperties.getInstance().setPreference(FrameworkOptions.RESOURCES_SOURCE_FOLDER, resourcesSourceFolder);
				}
			} else {
				testsSourceFolder = System.getProperty("user.dir");
			}
		}
		srcDir = new File(testsSourceFolder);
		docBuilder = new JavaDocBuilder();
		filesTime = new HashMap<File, Long>();
	}
	public void close (){
		docBuilder = null;
		filesTime = null;
	}
	/**
	 * Get the test code formated as HTML.
	 * @param className the test class name.
	 * @return an html with the code formated.
	 * @throws Exception when java2html class is missing, the file is not found or other error occurs
	 */
	public String getCode(String className) throws FileNotFoundException, ClassNotFoundException, Exception {
		File srcFile = new File(srcDir.getPath(), className.replace('.', File.separatorChar) + ".java");
		if (!srcFile.exists()) {
			srcFile = new File(srcDir.getPath(), className.replace('.', File.separatorChar) + ".groovy");
			if (!srcFile.exists()) {
				throw new FileNotFoundException(srcFile.getPath());
			}
		}
		// Create a reader of the raw input text

		// Parse the raw text to a JavaSource object

		Class<?> sourceParserClass = LoadersManager.getInstance().getLoader().loadClass("de.java2html.javasource.JavaSourceParser");
		Object sourceParser = sourceParserClass.newInstance();
		Method parseMethod = sourceParserClass.getMethod("parse", File.class);
		if (parseMethod == null) {
			return "";
		}
		Object source = parseMethod.invoke(sourceParser, srcFile);
		if (source == null) {
			return "";
		}
		Class<?> converterClass = LoadersManager.getInstance().getLoader().loadClass("de.java2html.converter.JavaSource2HTMLConverter");
		StringWriter writer = new StringWriter();
		Object converter = converterClass.getConstructor(source.getClass()).newInstance(source);
		converterClass.getMethod("convert", Writer.class).invoke(converter, writer);
		
		
//		JavaSource source = null;
//		source = new JavaSourceParser().parse(srcFile);

		// Create a converter and write the JavaSource object as Html
//		JavaSource2HTMLConverter converter = new JavaSource2HTMLConverter(source);
//		StringWriter writer = new StringWriter();
//		converter.convert(writer);
		String toReturn = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" + "<html><head>\n" +
		// "<title></title>\n" +
				"</head>\n" + "<body>\n" + writer.toString() + "</body>\n" + "</html>\n";

		return toReturn;

	}
	
	/**
	 * Get the class javadoc
	 * @param className the class name
	 * @return the class javadoc or null if not exist or not found
	 * @throws Exception
	 */
	public String getClassJavaDoc(String className) throws Exception {
		processSource(className);
		JavaClass cls = docBuilder.getClassByName(className);
		if(cls == null){
			return null;
		}
		return cls.getComment();
	}

	/**
	 * get the method javadoc
	 * @param className the class name to look for
	 * @param methodName the method to look for
	 * @return the documenation of the method or null if not exist
	 * @throws Exception
	 */
	public String getMethodJavaDoc(String className, String methodName) throws Exception {
		processSource(className);
		JavaClass cls = docBuilder.getClassByName(className);
		if(cls == null){
			return null;
		}
		JavaMethod[] methods = cls.getMethods();
		for(JavaMethod method: methods){
			if(method.getName().equals(methodName)){
				return method.getComment();
			}
		}
		return null;
	}
	
	/**
	 * Get the doclet tag for a specifc class and method
	 * @param className the class to look for.
	 * @param methodName the method to look for.
	 * @param annotation the doclet to look for.
	 * @return the doclet if exist of null if not.
	 */
	public String getMethodAnnotation(String className, String methodName, String annotation){
		processSource(className);
		JavaClass cls = docBuilder.getClassByName(className);
		if(cls == null){
			return null;
		}
		JavaMethod[] methods = cls.getMethods();
		for(JavaMethod method: methods){
			if(method.getName().equals(methodName)){
				DocletTag tag = method.getTagByName(annotation);
				if(tag == null){
					return null;
				}
				return tag.getValue();
			}
		}
		
		// method not found
		Class<?> c;
		try {
			c = LoadersManager.getInstance().getLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
		if(c != null && !c.equals(Object.class)){
			return getMethodAnnotation(c.getSuperclass().getName(), methodName, annotation);
		}
		return null;
		
	}
	/**
	 * Process the class and reload it if it changed.
	 * @param className
	 * @throws Exception
	 */
	private void processSource(String className){
		File testSrc = new File(srcDir, className.replace('.', File.separatorChar) + ".java");
		if(!testSrc.exists()){ // if the file doesn't exist return
			//Added support for Groovy tests
			testSrc = new File(srcDir, className.replace('.', File.separatorChar) + ".groovy");
			if(!testSrc.exists()){
				return;
			}
		}
		/*
		 * Check if the last modified time changed
		 * if not return
		 */
		Long time = filesTime.get(testSrc);
		if(time == null || !time.equals(testSrc.lastModified())){ // not exist or changed
			try {
				docBuilder.addSource(preProcessCode(FileUtils.read(testSrc)));
			} catch (Throwable e) {
				// ignore process fail
				log.log(Level.FINE, "Fail to process file: " + testSrc.getAbsolutePath(), e);
			}
			filesTime.put(testSrc, testSrc.lastModified());
		}
		
		
	}
	/**
	 * Remove enumeration definition
	 * @param code the original code
	 * @return Reader from the changed code
	 */
	public static Reader preProcessCode(String code){
		return new StringReader(code);
		
	}

}
