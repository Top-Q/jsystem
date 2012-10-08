/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.properties;

import java.util.ArrayList;

import jsystem.utils.ClassSearchUtil;

/**
 * This utility class support class search for the use of all jsystem properties package
 * 
 * @author Dror Voulichman
 *
 */
public class JSystemPropertiesUtilities {
	static String classPath = System.getProperty("java.class.path");

	/**
	 * 
	 * @param classesToSearch - An array of strings holding the classes to search
	 * @return an array of string holding all classes that extends or implements one of the 
	 * classes in the input parameter (classesToSearch)
	 */
	public static String[] getSearchResults(String[] classesToSearch) {
		int numberOfsearchParameters = classesToSearch.length;
		ArrayList<String> classesSearchResult = new ArrayList<String>();
		ArrayList<String> mergedResults = new ArrayList<String>();
		String className = null;
		
		for (int i = 0; i < numberOfsearchParameters; i++) {
			className = classesToSearch[i];
			Class<?> classToSearch = null;
			try {
				classToSearch = Class.forName(className);
			} catch (Exception e) {
				
			}
			classesSearchResult = searchClasses(classToSearch);
			mergedResults.addAll(classesSearchResult);
		}

		// Convert the ArrayList into String[]
		int resultCounter = mergedResults.size();
		String[] searchResults = new String[resultCounter];
		for (int i = 0; i < mergedResults.size(); i++) {
			searchResults[i] = new String(mergedResults.get(i));
		}
		
		return searchResults;
	}
	
	
	/**
	 * 
	 * @param classToSearc - A name of class / interface.
	 * 		We search for classes that: extends this class / implements this interface
	 * @return - An array list of all classes that extends this class / implements this interface
	 */
	public static ArrayList<String> searchClasses(Class<?> classToSearc) {
		ArrayList<String> searchList = new ArrayList<String>();
		 
		boolean filterAbstractClasses = true;
		String[] ignoreList = {"thirdparty"};
		
		try {
			searchList = ClassSearchUtil.searchClasses(classToSearc, classPath, filterAbstractClasses, ignoreList,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return searchList;
	}
}
