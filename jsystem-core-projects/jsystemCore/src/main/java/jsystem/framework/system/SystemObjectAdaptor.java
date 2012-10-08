/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.system;

import java.lang.reflect.Method;
import java.util.Properties;

public interface SystemObjectAdaptor {
	/**
	 * Check if the input method is supported
	 * @param method the method to check
	 * @return
	 */
	public boolean isMethodSupported(Method method);
	
	/**
	 * Init the method input values
	 * @param m the method to invoke
	 * @param properties a properties contain methods value
	 * @param paramNames parameters names in the correct order
	 * @return an array of the initialized objects
	 */
	public Object[] getMethodValues(Method m, Properties properties, String[] paramNames);
}
