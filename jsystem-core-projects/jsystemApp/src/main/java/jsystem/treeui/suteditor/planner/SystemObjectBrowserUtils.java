/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.suteditor.planner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.system.SystemObject;
import jsystem.utils.ClassSearchUtil;
import jsystem.utils.PerformanceUtil;
import jsystem.utils.beans.BeanUtils;
import jsystem.utils.exec.Command;
import jsystem.utils.exec.JavaExecute;
/**
 * Utility class with few static methods for system object browsing.
 * @author guy.arieli
 *
 */
public class SystemObjectBrowserUtils {
	
	public static boolean isSystemObjectSetMethod(String setter){
        if (setter.equals("setName") || setter.equals("setCheckStatus")
                || setter.equals("setTagName")
                || setter.equals("setLifeTime")
                || setter.equals("setXPath")
                || setter.equals("setReferenceXPath")
                || setter.equals("setClose")
                || setter.equals("setOpenCloseStatusAll")
                || setter.equals("setThrowException")
                || setter.equals("setSOArrayIndex")) {
            return true;
        }
        return false;
	}
	
		
	/**
	 * Get all the fields that are system object themself of a given class
	 * @param object the class to process.
	 * @return a list of all the system objects fields.
	 */
	public static ArrayList<Field> getSystemObjectField(Class<?> baseClass){
		ArrayList<Field> systemObjectFields = new ArrayList<Field>();
		Field[] allFields = null;
		try {
			allFields = baseClass.getFields();
		} catch (Throwable throwable){
			return systemObjectFields;
		}
		for(Field currentField: allFields){
			/*
			 * It should be assignable from SystemObject or
			 * array that it's component can be assign.
			 */
			if(SystemObject.class.isAssignableFrom(currentField.getType())){
				systemObjectFields.add(currentField);
			} else if(currentField.getType().isArray()
					&& SystemObject.class.isAssignableFrom(currentField.getType().getComponentType())){
				systemObjectFields.add(currentField);
			}
		}
		return systemObjectFields;
	}
	
	/**
	 * Scan the class path and search for potential implementations for a given class.
	 * @param c the class to search implementations for
	 * @param ignoreList a list of string that if found in the class path element will ignore it.
	 * @return a list of all the class names found
	 * @throws Exception when the search process fail.
	 */
	public static ArrayList<String> findPotintialImplementations(Class<?> c, String[] ignoreList, String[] includeList) throws Exception{
		return ClassSearchUtil.searchClasses(c, System.getProperty("java.class.path"), true, ignoreList, includeList);
	}
	
	/**
	 * Return a default value (if found) for a given setter method.
	 * @param systemObject the object class to search for.
	 * @param setterMethodName the setter method name.
	 * @return the value found will executing the getter method.
	 */
	public static String getDefaultValueFor(Class<?> systemObject, String setterMethodName){
		String getterMethodName = "get" + setterMethodName.substring(3);
		String returnValue = getValueFrom(systemObject,getterMethodName);
		getterMethodName = "is" + setterMethodName.substring(3);
		return (returnValue != null?returnValue:getValueFrom(systemObject,getterMethodName));
	}
	
	/**
	 * Return a value (if found) for a given getter method.
	 * @param systemObject the system object class to search for
	 * @param getterMethodName the getter method name.
	 * @return the value from the current SystemObject.
	 */
	private static String getValueFrom(Class<?> systemObject, String getterMethodName){
		try {
			Method currentMethod = systemObject.getMethod(getterMethodName);
			if(currentMethod != null && 
					isParamSupportedClass(currentMethod.getReturnType())){
				Object newSysObjInstance = systemObject.newInstance();
				Object returnValue = currentMethod.invoke(newSysObjInstance);
				if(returnValue != null){
					if(returnValue.getClass().isEnum()){
						return ((Enum<?>)returnValue).name();
					}
					if (returnValue.getClass().isArray()) {
						return Arrays.toString((Object[]) returnValue);
					}
					return returnValue.toString();
				}
			}
		} catch (Exception exception) {
		}
		return null;
	}
	
	public static boolean isParamSupportedClass(Class<?> type){
		Class<?>[] types = BeanUtils.getBasicTypes();
		for(Class<?> currentClazz: types){
			if(currentClazz.isAssignableFrom(type)){
				return true;
			}
		}
		return false;
	}
	
	private static ArrayList<String> foundSOs = null;
	
	static {
		if (!"false".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.SUT_PLANNER))) {
			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run(){
					if(collector != null){
						Process process = collector.getProcess();
						if(process != null){
							process.destroy();
						}
					}
				}
			});
		}
	}
	private static Command collector = null;
	
	
	public static void startCollectSOs(){
		if ("false".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.SUT_PLANNER))) {
			return;
		}
		
		int index = PerformanceUtil.startMeasure();
		
		if(collector != null){
			collector.getProcess().destroy();
			collector = null;
		}
		foundSOs = null;
		try {
			String include = JSystemProperties.getInstance().getPreference(FrameworkOptions.PLANNER_JARS_INCLUDE);
			String[] includes = null;
			if(include != null && !include.trim().equals("")){
				includes = include.split(";");
			}
			collector = JavaExecute.javaExecute(SystemObjectBrowserUtils.class, new Properties(), includes);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		PerformanceUtil.endMeasure(index, "Collecting System objects");
	}
	
	public synchronized static ArrayList<String> getFoundSOs(){		
		if(foundSOs != null){
			return foundSOs;
		}
		if(collector == null){
			startCollectSOs();
		}

		try {
			collector.getProcess().waitFor();
		} catch (InterruptedException e) {
			collector = null;
			foundSOs = null;
			return null;
		}
		String[] founds = collector.getStdout().toString().split("[\r|\n|\r\n]+");
		foundSOs = new ArrayList<String>();
		for(String currentFoundString: founds){
			if(currentFoundString.contains("$")){
				continue;
			}
			foundSOs.add(currentFoundString);
		}
		collector = null;
		return foundSOs;
	}
	
	public static void main(String...args){
		String[] ignoreList = { "thirdparty", "jsystemCore.jar", "jsystemCommon.jar", "jsystemApp.jar", "jsystemAgent.jar", "jsystem-launcher.jar", "jsystemAnt.jar"};
		try {
			String[] params = null;
			if(args != null && args.length > 0){
				params = args;
			}
			ArrayList<String>foundSOs = SystemObjectBrowserUtils
					.findPotintialImplementations(SystemObject.class,
							ignoreList, params);
			for(String s: foundSOs){
				System.out.println(s);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}


