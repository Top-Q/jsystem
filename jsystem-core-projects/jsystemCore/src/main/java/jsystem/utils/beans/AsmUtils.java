/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils.beans;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import jsystem.runner.loader.LoadersManager;

public class AsmUtils {
	
	private static AsmParameterNameLoader nameLoader = new AsmParameterNameLoader();
	
	public static String[] getParameterNames(Method m) throws IOException {
		Class<?> declaringClass = m.getDeclaringClass();
		Map<Method,List<String>> map = nameLoader.getAllMethodParameters(declaringClass, m.getName());
		return map.get(m).toArray(new String[0]);
	}
	
	public static String getMethodDescriptor(Method method){
		try {
			Class<?> clazz = LoadersManager.getInstance().getLoader().loadClass("org.objectweb.asm.Type");
			Method m = clazz.getMethod("getMethodDescriptor", Method.class);
			return (String)m.invoke(null, method);
		} catch (Exception e){
			return null;
		}
	}

}
