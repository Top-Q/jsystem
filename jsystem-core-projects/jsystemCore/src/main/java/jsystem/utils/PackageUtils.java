/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Vector;

import jsystem.runner.loader.LoadersManager;

/**
 * Set of method which works with String and class(packages) names.
 */
public class PackageUtils {
	/**
	 * Cut package name from className.
	 * 
	 * @param className
	 *            name of class
	 * @param removeIndex
	 *            how subpackages will be deleted. 1-> only class name...
	 * @return package's name
	 */
	public static String getPackage(String className, int removeIndex) {
		String packageName = className;
		for (int i = 0; i < removeIndex; i++) {
			int dotIndex = packageName.lastIndexOf('.');
			packageName = packageName.substring(0, dotIndex - 1);
		}
		return packageName;
	}

	/**
	 * Returns classes from package.
	 * 
	 * @param classesList
	 *            String list with classes names
	 * @return clases list from packageName
	 */
	public static ArrayList<String> getClassesInPackage(ArrayList<String> classesList,
			String packageName) {
		ArrayList<String> classesInPackage = new ArrayList<String>();
		for (int i = 0; i < classesList.size(); i++) {
			String className = classesList.get(i);
			if (isClassInPackage(className, packageName)) {
				classesInPackage.add(className);
			}
		}
		return classesInPackage;
	}

	/**
	 * Check if class is from package.
	 * 
	 * @return true if className contains packageName, false otherwise
	 */
	public static boolean isClassInPackage(String className, String packageName) {
		return ((className.startsWith(packageName + ".") && className
				.lastIndexOf('.') == packageName.length()));
	}

	/**
	 * Returns only class name from whole class name.
	 * 
	 * @return from "java.utils.Vector" returns "Vector"
	 */
	public static String getOnlyClassName(String className) {
		int dotIndex = className.lastIndexOf('.');
		if (dotIndex < 0) {
			return new String(className);
		}
		if (className.endsWith(".")) {
			return "";
		}
		return className.substring(dotIndex + 1);
	}

	/**
	 * Check if the given folder is a folder that java sources are compiled
	 * to. It not only check that class files can be found but also that
	 * it is the compilation root.
	 * 
	 * @param folder the classes folder to be checked
	 * @return true if the folder is the compilation root folder.
	 */
	public static boolean isClassFolder(File folder) {
		Vector<File> files = new Vector<File>();
		FileUtils.collectAllFiles(folder, new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".class");
			}

		}, files);
		if (files.size() == 0) {
			return false;
		}
		if(new File(folder,"sut").exists() || new File(folder, "scenarios").exists()){
			return true;
		}
		/*
		 * Take the first class in the list
		 */
		File classFile = files.elementAt(0);
		/*
		 * Build the full class name com.aqua...
		 */
		String className = classFile.getAbsolutePath().substring(
				folder.getAbsolutePath().length() + 1,
				classFile.getAbsolutePath().length() - 6).replace('/', '.')
				.replace('\\', '.');
		/*
		 * Try to load it
		 */
		try {
			LoadersManager.getInstance().getLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		isClassFolder(new File("C:\\work\\workspaces\\new\\jsystem\\classes"));
	}
}
