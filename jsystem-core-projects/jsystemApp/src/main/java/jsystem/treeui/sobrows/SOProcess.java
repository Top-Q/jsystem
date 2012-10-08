/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.sobrows;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JOptionPane;

import jsystem.extensions.report.html.HtmlCodeWriter;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.IgnoreMethod;
import jsystem.framework.JSystemProperties;
import jsystem.framework.TestProperties;
import jsystem.framework.TestRunnerFrame;
import jsystem.framework.common.CommonResources;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.sut.Sut;
import jsystem.framework.sut.SutFactory;
import jsystem.framework.system.SystemManagerImpl;
import jsystem.framework.system.SystemObject;
import jsystem.runner.ErrorLevel;
import jsystem.runner.loader.LoadersManager;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.sobrows.Options.Access;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import jsystem.utils.build.BuildUtils;
import junit.framework.SystemTestCase;

import org.w3c.dom.Node;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.Type;

/**
 * The SOProcess contain static method to process
 * 
 * @author guy.arieli
 * 
 */
public class SOProcess {
	private static final Logger log = Logger.getLogger(SOProcess.class.getName());
	/**
	 * The main class method, process a system object. Will create 2 class one
	 * is abstract that is auto generate, the second extends the first and is
	 * for user inputs. By overwrite existing methods.
	 * 
	 * @param testClassBase
	 *            the abstract base class
	 * @param testClass
	 *            the class for user inputs.
	 * @param sut
	 *            the setup sut object.
	 * @param soPath
	 *            the path to the sut in the xml file
	 * @param soClassName
	 *            the system object class name
	 * @param builder
	 *            the builder contain all the project sources
	 * @throws Exception
	 */
	public static void processSystemObject(Class testClassBase, Class testClass, Sut sut, String soPath,
			String soClassName, JavaDocBuilder builder) throws Exception {
		/*
		 * Get the class to be process
		 */
		JavaClass soClass = builder.getClassByName(soClassName);

		/*
		 * Get the class javadoc
		 */
		String comment = soClass.getComment();

		/*
		 * set the package name to be used
		 */
		String packageName = soPath.replace('/', '.');

		/*
		 * the last element in the package name is the system object name
		 */
		String soName = packageName.substring(packageName.lastIndexOf('.') + 1);
		String soNameFirstCharUpper = StringUtils.firstCharToUpper(soName);

		/*
		 * Set the package for the user class and for the base class
		 */
		testClassBase.setPackage(new Package(packageName));
		testClass.setPackage(new Package(packageName));

		/*
		 * Imports SystemTestCase
		 */
		testClassBase.imports.addImport(SystemTestCase.class.getName());

		/*
		 * Set the name of the base and user classes
		 */
		testClassBase.setClassName(soNameFirstCharUpper + "ManagerBase");
		testClass.setClassName(soNameFirstCharUpper + "Manager");

		/*
		 * Set the extended name. The user class extended the base and the base
		 * extended SystemTestCase
		 */
		testClassBase.setExtendsName(StringUtils.getClassName(SystemTestCase.class.getName()));
		testClass.setExtendsName(testClassBase.getClassName());

		/*
		 * Add the system object to the imports and add it as a member
		 */
		testClassBase.imports.addImport(soClassName);
		testClassBase.members.put(soName, new Member(soName, StringUtils.getClassName(soClassName), "null",
				Access.PROTECTED));

		/*
		 * Set the base class to be abstract
		 */
		testClassBase.setAbstract(true);

		/*
		 * Init the javadoc of the base
		 */
		StringBuffer javaDoc = new StringBuffer();
		javaDoc.append("Auto generate management object.\n");
		javaDoc.append("Managed object class: ");
		javaDoc.append(soClassName);
		javaDoc.append("\n");
		javaDoc.append("This file <b>shouldn't</b> be changed, to overwrite methods behavier\n");
		javaDoc.append("change: ");
		javaDoc.append(soNameFirstCharUpper);
		javaDoc.append("Manager.java\n");
		if (comment != null) {
			javaDoc.append("Object javadoc:\n");
			javaDoc.append(comment);
		}
		testClassBase.setJavadoc(javaDoc.toString());

		/*
		 * Add the setUp method that init the system object
		 */
		Method setUpMethod = new Method();
		setUpMethod.setAccess(Access.PUBLIC);
		setUpMethod.setMethodName("setUp");
		setUpMethod.setMethodCode(soName + " = (" + StringUtils.getClassName(soClassName)
				+ ")system.getSystemObject(\"" + soName + "\");");
		setUpMethod.setThrowsName("Exception");
		testClassBase.methods.add(setUpMethod);

		/*
		 * Build the xpath to the system object
		 */
		StringBuffer xpath = new StringBuffer();
		xpath.append("/sut/");
		xpath.append(soName);

		/*
		 * Call the main recuse method
		 */
		processSo("", builder, soClassName, testClassBase, soName, new ArrayList<String>(), xpath, sut);
	}

	/**
	 * Collect all the methods of the input class include the method of it
	 * supper classes.
	 * 
	 * @param cls
	 *            the class to collect the method of.
	 * @return an array of all the methods found
	 */
	private static JavaMethod[] collectMethods(JavaClass cls) {
		ArrayList<JavaMethod> methods = new ArrayList<JavaMethod>();

		/*
		 * Add all the class method
		 */
		JavaMethod[] mlist = cls.getMethods();
		for (JavaMethod m : mlist) {
			methods.add(m);
		}

		/*
		 * Add all the super class methods
		 */
		JavaClass superClass = null;
		while ((superClass = cls.getSuperJavaClass()) != null) {
			String cn = superClass.getName();
			/*
			 * Stop when the super clas is SystemObjectImpl or java Object.
			 */
			if (cn.equals("Object") || cn.equals("SystemObjectImpl")) {
				break;
			}
			JavaMethod[] mm = superClass.getMethods();
			for (JavaMethod m : mm) {
				methods.add(m);
			}
			cls = superClass;
		}
		return methods.toArray(new JavaMethod[0]);
	}

	/**
	 * Collect all the fields of the input class include the method of it supper
	 * classes.
	 * 
	 * @param cls
	 *            the class to collect the fields of.
	 * @return an array of all the fields found
	 */
	private static JavaField[] collectFields(JavaClass cls) {
		ArrayList<JavaField> fields = new ArrayList<JavaField>();

		/*
		 * Add all the class fields
		 */
		JavaField[] flist = cls.getFields();
		for (JavaField f : flist) {
			fields.add(f);
		}

		/*
		 * Add all the super class fields
		 */
		JavaClass superClass = null;
		while ((superClass = cls.getSuperJavaClass()) != null) {
			String cn = superClass.getName();
			/*
			 * Stop when the super clas is SystemObjectImpl or java Object.
			 */
			if (cn.equals("Object") || cn.equals("SystemObjectImpl")) {
				break;
			}
			JavaField[] ff = superClass.getFields();
			for (JavaField f : ff) {
				fields.add(f);
			}
			cls = superClass;
		}
		return fields.toArray(new JavaField[0]);
	}

	/**
	 * Check if the method parameters are of primitive type that supported by
	 * jsystem parameters.
	 * 
	 * @param method
	 *            the method to check
	 * @return true if all parameters are ok.
	 */
	private static boolean isMethodParamsTypeSupported(JavaMethod method) {
		JavaParameter[] params = method.getParameters();
		for (JavaParameter p : params) {
			String type = p.getType().getValue();
			if (p.getType().isArray()) {
				return false;
			}
			if ("int".equals(type) || "long".equals(type) || "java.lang.String".equals(type) || "float".equals(type)
					|| "double".equals(type) || "java.io.File".equals(type)) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	private static void processSo(String lead, JavaDocBuilder builder, String soClassName, Class testClass,
			String soName, ArrayList<String> extParams, StringBuffer xpath, Sut sut) throws Exception {
		/*
		 * First init the system object class
		 */
		JavaClass cls = builder.getClassByName(soClassName);
		/*
		 * collect all the method include the methods of the super classes
		 */
		JavaMethod[] methods = collectMethods(cls);
		method: for (int mindex = 0; mindex < methods.length; mindex++) {
			/*
			 * ignore init/close/check (from system object), ignore constructors
			 * and method that are not public
			 */
			Annotation[] annotations = methods[mindex].getAnnotations();
			if(annotations != null){
				for(Annotation annotation: annotations){
					if(annotation.getType().getValue().equals(IgnoreMethod.class.getName())){
						continue method;
					}
				}
			}
			if ((!methods[mindex].getName().matches("init")) && (!methods[mindex].getName().matches("close"))
					&& (!methods[mindex].getName().matches("check")) && (!methods[mindex].isConstructor())
					&& (methods[mindex].isPublic())) {
				String mname = methods[mindex].getName();
				/*
				 * If the method is a getter or setter will ignore it, if it can
				 * be found in the sut as an xml tag. so setIp will be ignore if
				 * the tag <ip></ip> is found in the xml. Or if it dosn't throw exception.
				 */
				if (mname.toLowerCase().startsWith("set") || mname.toLowerCase().startsWith("get")
						|| mname.toLowerCase().startsWith("is")) {
					Type[] types = methods[mindex].getExceptions();
					if(types == null || types.length == 0){
						continue;
					}
					String xpathOfMember = xpath.toString() + "/" + StringUtils.firstCharToLower(mname.substring(3))
							+ "/text()";
					try {
						if (sut.getValue(xpathOfMember) != null) {
							continue;
						}
					} catch (Exception ignore) {
						// Ignored
					}

				}
				/*
				 * Check that all the method member are supported
				 */
				if (!isMethodParamsTypeSupported(methods[mindex])) {
					continue;
				}
				JavaParameter[] parameters = methods[mindex].getParameters();

				/*
				 * Build the test method
				 */
				Method method = new Method();
				method.setAccess(Access.PUBLIC);

				/*
				 * Set the name of the test, if exist will try new name with
				 * increment number testSomething, then testSomething2 ...
				 */
				int mfoundIndex = 1;
				String mn = null;
				while (true) {
					if (mfoundIndex == 1) {
						mn = "test" + StringUtils.firstCharToUpper(lead)
								+ StringUtils.firstCharToUpper(methods[mindex].getName());
						;
					} else {
						mn = "test" + StringUtils.firstCharToUpper(lead)
								+ StringUtils.firstCharToUpper(methods[mindex].getName()) + mfoundIndex;
					}
					if (testClass.isMethodExist(mn)) {
						mfoundIndex++;
					} else {
						break;
					}

				}
				method.setMethodName(mn);
				method.setReturnType("void");
				method.setThrowsName("Exception");

				StringBuffer paramsAsInfo = new StringBuffer();
				/*
				 * Go over the method parameters
				 */
				for (int pindex = 0; pindex < parameters.length; pindex++) {
					String pName = StringUtils.firstCharToLower(lead
							+ StringUtils.firstCharToUpper(parameters[pindex].getName()));
					Member p = (Member) testClass.members.get(pName);
					Type type = parameters[pindex].getType();

					/*
					 * Ignore method types that contain $
					 */
					if (type.getValue().indexOf('$') >= 0) {
						continue method;
					}

					/*
					 * Create the member
					 */
					p = new Member();
					p.setAccess(Access.PROTECTED);
					if (!type.isPrimitive()) {
						testClass.imports.addImport(type.getValue());
					}
					p.setType(StringUtils.getClassName(type.getValue()));
					p.setName(pName);
					p.setArray(type.isArray());
					p.setValue(getDefultValue(type.getValue()));

					/*
					 * If the member wasn't add by other method
					 */
					if (testClass.members.get(pName) == null) {
						testClass.members.put(pName, p);

						/*
						 * Add the getter and setter of the parameter
						 */
						testClass.methods.add(p.getSetter());
						testClass.methods.add(p.getGetter());
					}
					method.addParameter(p);
					if(paramsAsInfo.length() > 0){// not first
						paramsAsInfo.append(" ");
					}
					paramsAsInfo.append(p.getName() + "=${" + p.getName() +"}");
				}

				/*
				 * Set the documentation
				 */
				StringBuffer doc = new StringBuffer();
				String orginalDoc = methods[mindex].getComment();
				String methodDescription = null;
				if (orginalDoc != null && !orginalDoc.trim().equals("")) { // will take the description from the documentation
					doc.append(orginalDoc);
					String firstLine = orginalDoc.split("[\\r\\n]+")[0];
					int dotIndex = firstLine.indexOf('.');
					if(dotIndex >= 0){ // if a dot was found take it up to the dot.
						firstLine = firstLine.substring(0, dotIndex);
					}
					methodDescription = firstLine;
				} else {
					methodDescription = soName + " " + SOProcess.getMethodAsLine(mn);
				}
				if(paramsAsInfo.length() > 0){
					methodDescription = methodDescription + ", where " + paramsAsInfo.toString();
				}
				method.addAnnotation("@TestProperties(name=\"" + methodDescription.replaceAll("\"", "\\\"") + "\")");
				testClass.imports.addImport(TestProperties.class.getName());

				/*
				 * build the documentation @include annotation
				 */
				StringBuffer paramString = new StringBuffer(method.getParametersName());
				for (int i = 0; i < extParams.size(); i++) {
					if (!paramString.toString().equals("")) {
						paramString.append(",");
					}
					paramString.append(extParams.get(i));
				}
				String pString = "";
				if (paramString != null && !paramString.toString().equals("")) {
					pString = paramString.toString();
				}
				doc.append("\n");
				doc.append("@" + RunnerTest.INCLUDE_PARAMS_STRING + " " + pString);
				method.setJavadoc(doc.toString());

				/*
				 * Set the method code
				 */
				method.setMethodCode(soName + "." + methods[mindex].getName() + "(" + method.getParametersString(false)
						+ ");");
				method.setParameters(new LinkedHashMap<String, Member>());
				testClass.methods.add(method);
			}
		}
		/*
		 * Go over all the fieds (also in the super class)
		 */
		JavaField[] fields = collectFields(cls);
		for (int i = 0; i < fields.length; i++) {
			/*
			 * Look for public members that are system object
			 */
			if (fields[i].isPublic()
					&& !fields[i].getType().isPrimitive()
					&& SystemObject.class.isAssignableFrom(LoadersManager.getInstance().getLoader().loadClass(
							fields[i].getType().getValue()))) {
				/*
				 * If an array of system objects
				 */
				if (fields[i].getType().isArray()) {
					/*
					 * Create an index for the array
					 */
					String indexMember = lead + fields[i].getName() + "Index";
					extParams.add(indexMember);
					Member m = new Member();
					m.setAccess(Access.PROTECTED);
					m.setName(indexMember);
					m.setType("int");
					m.setValue("0");
					/*
					 * If not exit will add the array index setter and getter
					 */
					if (testClass.members.get(indexMember) == null) {
						testClass.members.put(indexMember, m);
						testClass.methods.add(m.getGetter());
						testClass.methods.add(m.getSetter());
					}
					/*
					 * add to the next object xpath
					 */
					xpath.append("/");
					xpath.append(fields[i].getName());
					xpath.append("[0]");
					/*
					 * Call to the requrs with the new system object field
					 */
					processSo(StringUtils.firstCharToLower(lead + StringUtils.firstCharToUpper(fields[i].getName())),
							builder, fields[i].getType().getValue(), testClass, soName + "." + fields[i].getName()
									+ "[" + indexMember + "]", extParams, xpath, sut);
				} else {
					/*
					 * add to the next object xpath
					 */
					xpath.append("/");
					xpath.append(fields[i].getName());

					/*
					 * Call to the requrs with the new system object field
					 */
					processSo(StringUtils.firstCharToLower(lead + StringUtils.firstCharToUpper(fields[i].getName())),
							builder, fields[i].getType().getValue(), testClass, soName + "." + fields[i].getName(),
							extParams, xpath, sut);
				}

			}
		}
	}
	
	private static String getMethodAsLine(String methodName){
		StringBuffer line = new StringBuffer();
		int currentIndex = 0;
		String toProcess = methodName;
		if(methodName.startsWith("test")){
			toProcess = methodName.substring("test".length());
		}
		boolean firstWord = true;
		while(true){
			String nextWord = getNextWord(toProcess, currentIndex);
			if(nextWord == null){
				return line.toString();
			}
			if(!firstWord){
				line.append(' ');
			} else {
				firstWord = false;
			}
			line.append(nextWord);
			currentIndex += nextWord.length();
		}
	}
	
	private static String getNextWord(String fromString, int from){
		StringBuffer buf = new StringBuffer();
		boolean firstUpper = true;
		boolean allUpper = false;
		
		for(int i = from; i < fromString.length(); i++ ){
			char c = fromString.charAt(i);
			boolean currentCharIsUpper = Character.isUpperCase(c);
			if(i == from){ // first char
				firstUpper = currentCharIsUpper;
				buf.append(c);
				continue;
			} else if(i == from + 1){ // second char
				if(firstUpper && currentCharIsUpper){
					allUpper = true;
				} else if(!firstUpper && currentCharIsUpper){ // first low and second upper
					return buf.toString().toLowerCase();
				}
				buf.append(c);
				continue;
			}
			
			if(allUpper){
				if(!currentCharIsUpper){ // if all upper when find lowwer char go 2 char back
					return buf.subSequence(0, buf.length() - 1).toString();
				}
			} else {
				if(currentCharIsUpper){
					return buf.toString().toLowerCase();
				}
			}
			buf.append(c);
		}
		if(buf.length() == 0){
			return null;
		} else {
			if(allUpper){
				return buf.toString();
			} else {
				return buf.toString().toLowerCase();
			}
		}
	}

	/**
	 * Init the builder object. init will load all the source zip file and,
	 * tests source directory to the builder.
	 * 
	 * @param soDirs
	 *            list of directories contain zip files with java sources to
	 *            load
	 * @param srcDirs
	 *            the tests source dir to load
	 * @return the init builder
	 * @throws Exception
	 */
	public static JavaDocBuilder initBuilder(File[] soDirs, String[] srcDirs) throws Exception {
		JavaDocBuilder builder = new JavaDocBuilder();
		ArrayList<File> files = new ArrayList<File>();
		/*
		 * Collect all the source folders
		 */
		for (int i = 0; i < srcDirs.length; i++) {
			files.add(new File(srcDirs[i]));
		}
		String include = JSystemProperties.getInstance().getPreference(FrameworkOptions.PLANNER_JARS_INCLUDE);
		
		final String[] includeList = (include != null && !include.equals("")) ? include.split(";") : null;
		/*
		 * Collect all the zip files
		 */
		for (int i = 0; i < soDirs.length; i++) {
			File soDirFile = soDirs[i];
			File[] srcZips = soDirFile.listFiles(new FileFilter() {

				public boolean accept(File pathname) {
					if(includeList != null){
						for(String inc: includeList){
							if(pathname.getName().contains(inc)){
								return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".zip");
							}
						}
					} else {
						return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".zip");
					}
					return false;
				}

			});
			if (srcZips != null) {
				for (int j = 0; j < srcZips.length; j++) {
					files.add(srcZips[j]);
				}
			}
		}
		File[] srcZipFull = new File[files.size()];
		for (int i = 0; i < srcZipFull.length; i++) {
			srcZipFull[i] = files.get(i);
		}

		/*
		 * Init the builder with all the sources
		 */
		initBuilder(builder, srcZipFull);
		return builder;
	}

	/**
	 * Init the builder from a set of source pathes. The source path could be a
	 * zip of java source files or a directory include java source files
	 * 
	 * @param builder
	 *            the builder to init
	 * @param sourcePaths
	 *            an array fo source files (zip file or source directory)
	 */	
	public static void initBuilder(JavaDocBuilder builder, File[] sourcePaths) throws Exception {
		for (int i = 0; i < sourcePaths.length; i++) {
			if (!sourcePaths[i].exists()) { // check if the path exist
				continue;
			}
			if (sourcePaths[i].isFile()) { // A zip file
				try {
					ZipFile zipFile = new ZipFile(sourcePaths[i]);
					Enumeration<? extends ZipEntry> zipEnteries = zipFile.entries();
					while (zipEnteries.hasMoreElements()) {
						ZipEntry ze = (ZipEntry) zipEnteries.nextElement();
						if (ze.getName().toLowerCase().endsWith(".java")) {
							try {
								String code = readInputStreamToString(zipFile.getInputStream(ze));
								builder.addSource(HtmlCodeWriter.preProcessCode(code));
							} catch (Exception e) {
								log.log(Level.WARNING, "Fail to load source: " + ze.getName());
								log.log(Level.FINE, "Fail to load source: " + ze.getName(), e);
							}
						}
					}
				} catch (Exception e){
					log.log(Level.WARNING, "Fail to process file: " + sourcePaths[i].getAbsolutePath(), e);
					continue;
				}
			} else { // A source directory
				Vector<File> allFiles = new Vector<File>();
				FileUtils.collectAllFiles(sourcePaths[i], new FilenameFilter() {

					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".java");
					}

				}, allFiles);
				for (int j = 0; j < allFiles.size(); j++) {
					try {
						String code = readInputStreamToString(new FileInputStream(allFiles.elementAt(j)));
						builder.addSource(HtmlCodeWriter.preProcessCode(code));
					} catch (Exception e) {
						log.log(Level.WARNING, "Fail to load source: " + allFiles.elementAt(j), e);
					}
				}
			}
		}
	}
	private static String readInputStreamToString(InputStream in) throws Exception{
		StringBuffer buf = new StringBuffer();
		int c;
		while ((c = in.read()) >= 0){
			buf.append((char)c);
		}
		in.close();
		return buf.toString();
	}

	/**
	 * Scan the sut and collect all the base system object to the map
	 * 
	 * @param sut
	 *            the sut object
	 * @param sutMap
	 *            the map to collect to
	 * @throws Exception
	 */
	public static void scanSut(Sut sut, HashMap<String, String> sutMap) throws Exception {

		List<?> list = sut.getAllValues("sut/*/class");

		for (int i = 0; i < list.size(); i++) {

			String node = ((Node) list.get(i)).getParentNode().getNodeName();

			String clsName = sut.getValue("sut/" + node + "/class/text()");

			sutMap.put("sut/" + node, clsName);
		}

	}

	/**
	 * Get the default value depend on the input type. For int it will be 0: int
	 * x = 0; for String it will be null: String s = null;
	 * 
	 * @param type
	 *            the type of the object
	 * @return the defualt value
	 */
	private static String getDefultValue(String type) {
		if ("java.lang.String".equals(type)) {
			return "null";
		}
		if ("long".equals(type) || "int".equals(type) || "byte".equals(type) || "char".equals(type)
				|| "double".equals(type) || "float".equals(type)) {
			return "0";
		}
		if ("boolean".equals(type)) {
			return "true";
		}
		return "null";
	}

	/**
	 * The method will generate unit tests for all the base system object, in
	 * the current SUT file. The tests can be use to create basic test scenario.
	 * 
	 */
	public static void processSOGenerator() {
		/*
		 * Collect all the base system object from the current SUT.
		 */
		Vector<String> soNames = SystemManagerImpl.getAllObjects(true);
		/*
		 * if no objects were found
		 */
		if(soNames == null || (soNames.size() == 0)){
			ErrorPanel.showErrorDialog( "No system object were found in the current SUT file",(String) null, ErrorLevel.Info);
			return;
		}
		String soName = null;
		if(soNames.size() == 1){ // only single SO was found
			soName = soNames.elementAt(0);
		} else {
			soName = (String)JOptionPane.showInputDialog(
					TestRunnerFrame.guiMainFrame, 
					"Please select the System Object you would like to process", 
					"System Object Browser", 
					JOptionPane.INFORMATION_MESSAGE,
					null,
					soNames.toArray(),
					soNames.elementAt(0));
			if (soName == null){ // it was cancled
				return;
			}
		}
		
		WaitDialog.launchWaitDialog("Process SUT System Objects", null);

		/*
		 * Initiate the builder. The builder load all the sources and enable
		 * object queries base on the sources.
		 */
		JavaDocBuilder builder = null;
		File testDir = null;
		try {
			testDir = new File(JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_SOURCE_FOLDER));
			builder = SOProcess.initBuilder(CommonResources.getAllOptionalLibDirectories(), new String[] {
					testDir.getAbsolutePath(), (new File(testDir.getParentFile(), "src")).getAbsolutePath() });
		} catch (Exception e1) {
			ErrorPanel.showErrorDialog("Failed to initiate source builder", e1, ErrorLevel.Error);
			return;
		} finally {
			WaitDialog.endWaitDialog();
		}

		Sut sut = SutFactory.getInstance().getSutInstance();

		try {
			String soClass = sut.getValue("/sut/" + soName +"/class/text()");
			WaitDialog.launchWaitDialog("Process: " + soName, null);

			/*
			 * For every system object generate the base class - will
			 * include all the auto-generated tests. and the test class that
			 * can be used by the user to change the basic auto-generated
			 * implementation.
			 */
			Class testClassBase = new Class();
			Class testClass = new Class();
			SOProcess.processSystemObject(testClassBase, testClass, sut, "autogen/" + soName, soClass,
					builder);

			/*
			 * Save the 2 calsses as java file
			 */
			File srcJavaBase = new File(testDir.getAbsolutePath() + File.separatorChar
					+ testClassBase.getPackage().getPackageName().replace('.', File.separatorChar), testClassBase
					.getClassName()
					+ ".java");
			File srcJava = new File(testDir.getAbsolutePath() + File.separatorChar
					+ testClass.getPackage().getPackageName().replace('.', File.separatorChar), testClass
					.getClassName()
					+ ".java");
			Code c = new Code();
			testClassBase.addToCode(c);
			String src = c.toString();
			FileUtils.write(srcJavaBase, src, false);
			/*
			 * The tests file will be save only if it's not exists
			 */
			if (!srcJava.exists()) {
				c = new Code();
				testClass.addToCode(c);
				src = c.toString();
				FileUtils.write(srcJava, src, false);
			}
			BuildUtils.compile(
					JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_SOURCE_FOLDER),
					JSystemProperties.getCurrentTestsPath(), System.getProperty("java.class.path"), 
					testClassBase.getPackage().getPackageName().replace('.', File.separatorChar) + 
					File.separatorChar + testClass.getClassName() + ".java");
		} catch (Exception er) {
			ErrorPanel.showErrorDialog("Fail to write to src file", er, ErrorLevel.Error);
			return;

		} finally {
			WaitDialog.endWaitDialog();
			builder = null;
		}
	}
	public static void main(String[] args){
		System.out.println(SOProcess.getMethodAsLine("testFindNewBug"));
		System.out.println(SOProcess.getMethodAsLine("testFindBUGInSystem"));
		
	}
}
