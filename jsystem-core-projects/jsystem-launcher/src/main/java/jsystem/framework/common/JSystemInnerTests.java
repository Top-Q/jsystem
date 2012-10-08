/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.common;

/**
 * this ENUM holds all In-House tests used by the JRunner.<br>
 * for example: events - changeSut and Publish\Email.<br>
 * 
 * this is used to determine if test should be published, and to allow flexibility with package changing
 * 
 * @author Nizan Freedman
 *
 */
public enum JSystemInnerTests {
	PUBLISH("jsystem.runner.agent.tests","PublishTest","publish"),
	CHANGE_SUT("jsystem.framework.sut","ChangeSutTest","changeSut");
	
	private String className;
	private String methodName;
	private String packageName;
	
	private JSystemInnerTests(String packageName, String className, String methodName){
		this.packageName = packageName;
		this.className = className;
		this.methodName = methodName;
	}
	
	/**
	 * checks if the given package and class String and method name matches an inner test
	 * @param packageAndClass	a String representing the package and class of the test to examine
	 * @param methodName	the method name of the test
	 * @return	True if Package and class and Method match an inner test
	 */
	public static boolean isInnerTest(String packageAndClass, String methodName){
		for (JSystemInnerTests test : JSystemInnerTests.values()){
			if (test.getTestPackageAndClass().equals(packageAndClass) && test.methodName.equals(methodName)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isInnerTestByPackageSeperator(String packageString, String classAndMethodName){
		for (JSystemInnerTests test : JSystemInnerTests.values()){
			if (test.getTestPackage().equals(packageString) && test.getTestClassAndMethod().equals(classAndMethodName)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * get the test String representing the Package, class and method, concatenated with "."
	 * @return
	 */
	public String getTestFullString(){
		return packageName + "." + className + "." + methodName;
	}
	
	/**
	 * get the test package and class concatenated with "."
	 * @return
	 */
	public String getTestPackageAndClass(){
		return packageName + "." + className;
	}
	
	public String getTestPackage(){
		return packageName;
	}
	
	public String getTestClassAndMethod(){
		return className + "." + methodName;
	}
}
