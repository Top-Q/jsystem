/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.runner.loader.LoadersManager;

/**
 * Used to process stack trace
 * @author guy.arieli
 */
public class StackTraceUtil {

	/**
	 * Find the first line a line with a specific class name is found
	 * @param stack the stack trace as String
	 * @param clazz the class to search for
	 * @return a line with the link to the sources or null if not found
	 */
	public static String findTheFirstOfType(String stack, Class<?> clazz){
		if(stack == null || clazz == null){
			return null;
		}
		String[] stackLines = stack.split("[\r\n]+");
		Pattern p = Pattern.compile("\\s*at\\s+((.*)(\\(.*\\)))");
		/*
		 * Go over all the line
		 */
		for (int i = 0; i < stackLines.length; i++){
			Matcher m = p.matcher(stackLines[i]);
			if(m.find()){ // if the line metch the pattern
				String cleanLine = m.group(1);
				String classAndMethod = m.group(2);
				//String fileAndLine = m.group(3);
				int lastDotIndex = classAndMethod.lastIndexOf('.');
				if(lastDotIndex < 0){
					continue;
				}
				try {
					/*
					 * Extract the class name and load it
					 */
					String className = classAndMethod.substring(0, lastDotIndex);
					Class<?> stackClass = LoadersManager.getInstance().getLoader().loadClass(className);
					/*
					 * Check if the class found in the line or one of his supper classes
					 * metch the expected class
					 */
					while(true){
						if(clazz.equals(stackClass)){
							return cleanLine;
						}
						stackClass = stackClass.getSuperclass();
						if(stackClass == null){
							continue;
						}
					}
				} catch (Throwable t){
					continue;
				}
			}
		}
		return null;
	}
	
	
}
