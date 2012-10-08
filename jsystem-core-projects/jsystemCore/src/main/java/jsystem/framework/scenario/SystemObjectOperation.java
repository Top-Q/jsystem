/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Properties;

import jsystem.framework.system.DefaultSystemObjectAdaptor;
import jsystem.framework.system.SystemObject;
import jsystem.framework.system.SystemObjectAdaptor;
import jsystem.utils.beans.MethodElement;
import junit.framework.SystemTestCase;

public class SystemObjectOperation extends SystemTestCase {
	public void testExecuteOperation() throws Exception{
		String methodName = System.getProperty(RunningProperties.SYSTEM_OBJECT_OPERATION +".method");
		String[] paramNames = System.getProperty(RunningProperties.SYSTEM_OBJECT_OPERATION +".params").split(";");
		String xpath = System.getProperty(RunningProperties.PARAM_PREFIX +"xpath");
		String descriptor = System.getProperty(RunningProperties.SYSTEM_OBJECT_OPERATION +".descriptor");
		Properties properties = new Properties();
		properties.load(new StringReader(System.getProperty(RunningProperties.SYSTEM_OBJECT_OPERATION +".properties")));

		SystemObject obj = system.getSystemObjectByXPath(xpath);
		// find the method in the obj
		Method m = MethodElement.findMethod(obj.getClass(), methodName, descriptor);
		if(m == null){
			report.report("The method " + methodName + " was not found");
			throw new Exception("The method " + methodName + " was not found");
		}
		SystemObjectAdaptor adaptor = new DefaultSystemObjectAdaptor();
		Object[] values = adaptor.getMethodValues(m, properties, paramNames);
		// invoke the the method
		m.invoke(obj, values);
	}
}
