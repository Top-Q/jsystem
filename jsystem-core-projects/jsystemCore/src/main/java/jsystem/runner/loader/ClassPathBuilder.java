/*
 * Created on Jun 24, 2005
 * 
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.loader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.utils.FileUtils;

/**
 * @author guy.arieli
 * 
 */
public class ClassPathBuilder {
	private static String originalClassPath = System.getProperty("java.class.path");

	private static void refreshClassPath() {
		System.setProperty("java.class.path", CommonResources.getClassPath());
		originalClassPath = System.getProperty("java.class.path");

	}

	/**
	 * Order of classes in the classpath:
	 * 
	 * 1. Thirdparty ant/lib 2. Thirdparty commonLib 3. Thirdparty lib 4.
	 * Thirdparty/selenium 5. runner/customer_lib 6. runner/so_lib 7. runner/lib
	 * 8. user additional libs 9. automation project lib file 10. automation
	 * project tests
	 */
	public static String getClassPath() {
		refreshClassPath();
		StringBuffer classPath = new StringBuffer(originalClassPath);
		classPath.append(File.pathSeparatorChar);
		String libDirs = JSystemProperties.getInstance().getPreference(FrameworkOptions.LIB_DIRS);
		if (libDirs != null) {
			StringTokenizer st = new StringTokenizer(libDirs, ";");
			while (st.hasMoreElements()) {
				classPath.append(findJars(st.nextToken()));
			}
		}
		// append tests project jars
		File testProjectLibFile = new File(JSystemProperties.getCurrentTestsPath() + "/../lib");
		if (testProjectLibFile.exists()) {
			classPath.append(findJars(FileUtils.getCannonicalPath(testProjectLibFile)));
		}
		// ITAI: If our project is in Maven structure then we need to travel up
		// another folder
		File mavenTestProjectLibFile = new File(JSystemProperties.getCurrentTestsPath() + "/../../lib");
		if (mavenTestProjectLibFile.exists()) {
			classPath.append(findJars(FileUtils.getCannonicalPath(mavenTestProjectLibFile)));
		}
		classPath.append(JSystemProperties.getCurrentTestsPath());
		System.setProperty("java.class.path", classPath.toString());
		return classPath.toString();
	}

	private static String findJars(String lib) {
		StringBuffer jars = new StringBuffer();
		File libFile = new File(lib);
		if (libFile.exists() && libFile.isDirectory()) {
			File[] files = libFile.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.toLowerCase().endsWith(".jar")) {
						return true;
					}
					return false;
				}
			});
			for (int i = 0; i < files.length; i++) {
				jars.append(files[i].getPath());
				jars.append(File.pathSeparatorChar);
			}

		}
		return jars.toString();
	}
}
