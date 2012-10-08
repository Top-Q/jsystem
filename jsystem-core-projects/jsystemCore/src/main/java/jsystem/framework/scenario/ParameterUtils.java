/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Parameter related utility methods 
 */
public class ParameterUtils {
	
	/**
	 * Returns an array of parameters which is a clone 
	 * of <code>params</code>
	 * if <code>params</code> is null the method returns null
	 */
	public static Parameter[] clone(Parameter[] params){
		if (params == null){
			return null;
		}
		Parameter[] ret = (Parameter[])Array.newInstance(params.getClass().getComponentType(), params.length);
		for (int i = 0;i <ret.length;i++){
			ret[i] = params[i].cloneParameter();
			ret[i].resetDirty();
		}
		return ret;		                
	}
	
	/**
	 * Returns true if at least one of the parameters in the <code>params</code>
	 * array is dirty. 
	 */
	public static boolean isDirty(Parameter[] params){
		for (Parameter p:params){
			if (p.isDirty()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Receives a <code>Properties</code> and converts it
	 * to a String.
	 */
	public static String propertiesToString(Properties props) throws Exception {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		props.store(printWriter,null);
		String s = stringWriter.toString();
		s = s.substring(s.indexOf("\n")+1);
		return s;
	}
	
	/**
	 * Recieves a serialization of <code>Properties</code> instance which was
	 * created using {@link #propertiesToString(Properties)} and converts
	 * it to a String array of the properties.
	 * Each String is in the structure of key=value
	 */
	public static String[] stringToPropertiesArray(String propsAsString)  throws Exception {
		StringReader reader = new StringReader(propsAsString);
		Properties props = new Properties();
		props.load(reader);
		String[] res = new String[props.size()];
		Enumeration<Object> keys = props.keys();
		int counter = 0;
		while (keys.hasMoreElements()){
			String key = keys.nextElement().toString();
			String value = props.getProperty(key);
			res[counter] = key+"="+value;
			counter++;
		}
		return res;
	}
	
	/**
	 * Sets all <code>params</code> to <code>enables</code> 
	 */
	public static void setEnabled(Parameter[] params ,boolean enabled){
		for (Parameter p:params){
			p.setEditable(enabled);
		}
	}
	
	/**
	 * Sets all <code>params</code> to <code>visible</code> 
	 */
	public static void setVisible(Parameter[] params ,boolean visible){
		for (Parameter p:params){
			p.setVisible(visible);
		}
	}
	/**
	 */
	public static void setDirty(Parameter[] params, boolean dirty) {
		if (dirty){
			for (Parameter p : params) {
				p.setDirty();
			}
		}else{
			for (Parameter p : params) {
				p.resetDirty();
			}
		}
	}
}
