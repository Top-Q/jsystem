/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.launcher.Locator;

/**
 * Include information on all the resources/directories in the system.
 * 
 * @author guy.arieli
 * 
 */
public class CommonResources {
	
	public static Logger log = Logger
			.getLogger(CommonResources.class.getName());
	
	/**
	 * Properties file for ant script properties.
	 */
	public static final String ANT_INTERNAL_PROPERTIES_FILE = ".ant.properties";
	  
	/**
	 * Pre defined test for Freeze Exception.
	 */
	public static final String FREEZE_ON_FAIL_TITLE = "Freeze Exception";
	
	/**
	 * The name of the folder under which scenarios are found
	 */
	public static final String SCENARIOS_FOLDER_NAME = "scenarios";

	/**
	 * The name of the folder under which scenarios are found
	 */
	public static final String SUT_FOLDER_NAME = "sut";

	/**
	 * The name of the project resources directory
	 */
	public static final String RESOURCES_FOLDER_NAME = "resources";

	/**
	 * The name of the jsystem jars directory
	 */
	public static final String LIB_NAME = "lib";

	/**
	 * The name of the customer jars directory
	 */
	public static final String CUSTOMER_LIB_NAME = "customer_lib";

	/**
	 * The name of the 3'rd party jars directory
	 */
	public static final String EXTERNAL_LIB_NAME = "thirdparty";

	
	public static final String JSYSTEM_PROPERTIES_FILE_NAME = "jsystem.properties";
	
	public static final String JSYSTEM_PROPERTIES_BACKUP_FILE_NAME = "jsystem.properties.bu";

	/**
	 * The name of the JSystem command line configuration file.
	 */
	public static final String JSYSTEM_COMMAND_LINE_FILE_NAME = "run.xml";
	
	public static final String JSYSTEM_BASE_FILE = ".jsystembase";
	
	/**
	 * The startup class property name
	 */
	public static final String JSYSTEM_MAIN = "jsystem.main";

	/**
	 * The run properties file name
	 */
	public static final String RUN_PROPERTIES_FILE_NAME = ".run.properties";

	/**
	 * Mutex file to make sure two runner engine instances are
	 * not opened in the same root dir. 
	 */
	public static final File LOCK_FILE = new File(".runner.lock");
	
	public static final String TEARDOWN_STRING = "tearDown execution"; 
	
	public static final String NEGATIVE_TEST_STRING = "Negative test - Test was supposed to fail but passed";
	
	public static final String TEST_INNER_TEMP_FILENAME = ".testdir.tmp";
	
	public static final String TEST_DIR_KEY = "test.dir.last";
	
	public static final String LAST_REPORT_NAME = "last.report.name";

	/**
	 * The lib directory file
	 */
	private static final File LIB_FILE = new File(getRunnerDir(), LIB_NAME);

	/**
	 * The customer lib directory file
	 */
	private static final File CUSTOMER_LIB_FILE = new File(getRunnerDir(), CUSTOMER_LIB_NAME);

	/**
	 * Thirdparty root. Please read {@link #isNewRunnerStructure()} to learn
	 * about runner folders structure
	 */
	private static final File EXTERNAL_LIB_ROOT_FILE = new File(getRunnerDir(), EXTERNAL_LIB_NAME);

	/**
	 * Thirdparty libraries directory. Please read
	 * {@link #isNewRunnerStructure()} to learn about runner folders structure
	 */
	private static final File EXTERNAL_LIB_FILE = new File(
			EXTERNAL_LIB_ROOT_FILE, "lib");

	/**
	 * Thirdparty common libraries directory. Please read
	 * {@link #isNewRunnerStructure()} to learn about runner folders structure
	 */
	private static final File EXTERNAL_COMMON_LIB_FILE = new File(
			EXTERNAL_LIB_ROOT_FILE, "commonLib");
	
	private static final File EXTERNAL_SELENIUM_LIB_FILE = new File(
			EXTERNAL_LIB_ROOT_FILE, "selenium");

	/**
	 * Ant interpreter path
	 */
	private static final File ANT_FILE = new File(EXTERNAL_LIB_ROOT_FILE, "ant");

	/**
	 * Ant lib directory
	 */
	private static final File ANT_LIB_FILE = new File(ANT_FILE, "lib");

	public static final String GUI_RESOURCE_FILE = "jsystem/treeui/gui.properties";
	
	public static final String DELIMITER = ";";
	
	public static final String SCRIPT_CONDITION = "jsystemscriptcondition";
	
	public static final String JSYSTEM_SWITCH = "jsystemswitch";
	
	public static final String JSYSTEM_FOR = "jsystemfor";
	
	public static final String SET_ANT_PROPERTIES = "jsystemsetantproperties";
	
	/**
	 * Test status flag - return parameters usage
	 */
	public static final String TEST_STATUS = "Pass";
	
	/**
	 * @return the location of the lib directory
	 */
	public static File getLibDirectory() {
		return LIB_FILE;
	}

	/**
	 * @return the location of the customer lib directory
	 */
	public static File getCustomerLibDirectory() {
		return CUSTOMER_LIB_FILE;
	}

	/**
	 * @return get 3'rd party lib directory
	 */
	public static File getThirdPartyLibDirectory() {
		return EXTERNAL_LIB_FILE;
	}

	/**
	 * @return ant library directory
	 */
	public static File getAntLibDirectory() {
		return ANT_LIB_FILE;
	}

	/**
	 * @return get 3'rd party common lib directory
	 */
	public static File getThirdPartyCommonLibDirectory() {
		return EXTERNAL_COMMON_LIB_FILE;
	}
	
	/**
	 * @return	get the Selenium directory 
	 */
	public static File getSeleniumLibDirectory() {
		return EXTERNAL_SELENIUM_LIB_FILE;
	}
	

	/**
	 * @return the used lib directories taken from the classpath.
	 */
	public static File[] getUsedLibDirectories() {
		HashMap<File, File> files = new HashMap<File, File>();
		String[] paths = System.getProperty("java.class.path").split(
				File.pathSeparator);
		for (String path : paths) {
			File fpath = new File(path);
			if (fpath.exists() && fpath.isFile()) {
				File parent = fpath.getParentFile();
				files.put(parent, parent);
			}
		}
		ArrayList<File> toReturn = new ArrayList<File>(files.values());
		return toReturn.toArray(new File[0]);
	}

	/**
	 * sets the system proprety named java.class.basicclasspath to the value of
	 * java.class.path this is suppose to be done only one time at the begining
	 * of the run
	 */
	public static void setBasicClasspath() {
		String baseClassPath = System.getProperty("java.class.path");
		System.setProperty("java.class.basicclasspath", baseClassPath);

	}

	/**
	 * returns the classpath with all the jars that are contained in the user
	 * dirs eg:/lib/ ad so on
	 * 
	 * @return
	 */
	public static String getClassPath() {
		URL[] userJars = getUserJars();
		// now update the class.path property
		StringBuffer baseClassPath = new StringBuffer();
		if (System.getProperty("java.class.basicclasspath") != null) {
			baseClassPath = new StringBuffer(System
					.getProperty("java.class.basicclasspath"));
		} else {
			CommonResources.setBasicClasspath();
			baseClassPath = new StringBuffer(System
					.getProperty("java.class.basicclasspath"));

		}
		if (baseClassPath.charAt(baseClassPath.length() - 1) == File.pathSeparatorChar) {
			baseClassPath.setLength(baseClassPath.length() - 1);
		}
		for (int i = 0; i < userJars.length; ++i) {
			baseClassPath.append(File.pathSeparatorChar);
			baseClassPath.append(Locator.fromURI(userJars[i].toString()
					.replaceAll("/\\./", "/")));
		}
		return baseClassPath.toString();
	}

	/**
	 * returns an array of urls with the user jars
	 * @return
	 */
	public static URL[] getUserJars() {
		File[] libsDir = CommonResources.getAllOptionalLibDirectories();
		ArrayList<URL> jars = new ArrayList<URL>();
		for (int i = 0; i < libsDir.length; i++) {
			URL[] uJars = null;
			try {
				uJars = Locator.getLocationURLs(libsDir[i]);
			} catch (MalformedURLException e) {
				log.log(Level.WARNING, libsDir[i].getName(), e);
			}
			if (uJars != null) {
				for (int j = 0; j < uJars.length; j++) {
					jars.add(uJars[j]);
				}
			}
		}
		Object[] jo = jars.toArray();
		URL[] userJars = new URL[jo.length];
		System.arraycopy(jo, 0, userJars, 0, userJars.length);

		return userJars;
	}

	/**
	 * Collect all the optional lib directory inlcue the jsystem lib, system
	 * object lib, customer lib, 3'rd party lib and user lib.
	 * 
	 * @return the array of directory
	 */
	public static File[] getAllOptionalLibDirectories() {
		Properties p = new Properties();
		FileInputStream is = null;
		try {
			is = new FileInputStream(JSYSTEM_PROPERTIES_FILE_NAME);
			p.load(is);
		} catch (Exception ignore) {
		}finally{
			if (is!=null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		String userLib = p.getProperty("user.lib");
		ArrayList<File> libsDir = new ArrayList<File>();
		libsDir.add(getAntLibDirectory());
		libsDir.add(getThirdPartyCommonLibDirectory());
		libsDir.add(getThirdPartyLibDirectory());
		libsDir.add(getCustomerLibDirectory());
		libsDir.add(getLibDirectory());
		libsDir.add(getSeleniumLibDirectory());
		if (userLib != null) {
			String[] libDirs = userLib.split(";");
			for (int i = 0; i < libDirs.length; i++) {
				File lib = new File(libDirs[i]);
				if (lib.exists() && lib.isDirectory()) {
					libsDir.add(lib);
				}
			}
		}
		return libsDir.toArray(new File[0]);
	}

	/**
	 * Returns Ant interpreter directory
	 */
	public static File getAntDirectory() {
		if (isNewRunnerStructure()) {
			return ANT_FILE;
		}
		File antPlaceBeforeStructureChange = new File("3party", "ant");
		if (antPlaceBeforeStructureChange.exists()) {
			return antPlaceBeforeStructureChange;
		}
		return findAntDirectory();
	}

	/**
	 * Find the ant directory by recursive search for the runner dir
	 * 
	 * @return the ant directory
	 */
	private static File findAntDirectory() {
		/*
		 * search the runner directory from the user.dir and down.
		 */
		File baseDir = new File(getRunnerDir());
		while (true) {
			File runnerDir = new File(baseDir, "runner");
			if (runnerDir.exists()
					&& new File(runnerDir, "thirdparty").exists()) {
				break;
			}
			baseDir = baseDir.getParentFile();
			if (baseDir == null) {
				throw new RuntimeException("Could not find ant interpreter");
			}
		}
		
		/*
		 * In the runner directory locate the ant dir
		 */
		File tp = new File(baseDir.getAbsolutePath() + File.separator
				+ "runner" + File.separator + "thirdparty", "ant");
		if (tp.exists()) {
			return tp;
		}
		throw new RuntimeException("Could not find ant interpreter");

	}

	/**
	 * Starting at TAS 4.6 and the transition to SVN, The directory structure of
	 * the runner application is changing.
	 * 
	 * The new structure is:
	 * 
	 * runner lib so_lib customer_lib thirdparty jdk lib ant commonLib
	 * 
	 * run.bat .. .. Some of the runner functionality depends on the runner
	 * structure. Since I want the code to be backward compatible to the old
	 * structure This method checks in which structure we are running.
	 * 
	 * If the file thirdparty/ant exists this means we are running in the new
	 * structure and the method returns true.
	 * 
	 */
	private static boolean isNewRunnerStructure() {
		return ANT_FILE.exists();
	}

	public static String getResourcesFolderName() {
		return RESOURCES_FOLDER_NAME;
	}
	
	public static String getRunnerDir(){
		String currentDir = System.getenv("current_dir");
		if(currentDir != null){
			return currentDir;
		}
		String userDir = System.getProperty("user.dir"); 
		if (userDir.contains("jsystemApp")){
			return System.getenv("RUNNER_ROOT");
		}
		return userDir;
	}
}

