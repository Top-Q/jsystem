/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.system;

import java.lang.reflect.Method;
import java.util.Properties;

import jsystem.framework.analyzer.AnalyzerImpl;
import jsystem.utils.beans.BeanUtils;

public class DefaultSystemObjectAdaptor implements SystemObjectAdaptor {

	@Override
	public boolean isMethodSupported(Method method) {
		if(method.getName().toLowerCase().startsWith("set") || method.getName().toLowerCase().startsWith("get") ||
				method.getName().equals("close") || method.getName().equals("init") || method.getName().equals("check")){
			return false;
		}
		if(method.getDeclaringClass().equals(SystemObjectImpl.class) || method.getDeclaringClass().equals(Object.class) ||
				method.getDeclaringClass().equals(AnalyzerImpl.class)){
			return false;
		}
		Class<?>[] classes = method.getParameterTypes();
		for(Class<?> clazz: classes){
			if(!BeanUtils.isClassOfTypes(clazz, BeanUtils.getBasicTypes())){
				return false;
			}
		}
		return true;
	}

	@Override
	public Object[] getMethodValues(Method m, Properties properties, String[] paramNames) {
		Class<?>[] clazzes = m.getParameterTypes();
		Object[] values = new Object[clazzes.length];
		for(int i = 0; i < values.length; i++){
			values[i] = BeanUtils.getObjects(clazzes[i], properties.getProperty(paramNames[i]));
		}
		return values;
	}

}
