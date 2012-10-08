/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Common java reflection utils.
 * @author goland
 */
public class ReflectionUtils {
	private static Map<Class<?>,Class<?>> builtInMap = new HashMap<Class<?>,Class<?>>();
	static {
	       builtInMap.put(Integer.class,int.class);
	       builtInMap.put(Long.class, long.class);
	       builtInMap.put(Double.class,double.class  );
	       builtInMap.put(Float.class,float.class  );
	       builtInMap.put(Boolean.class,boolean.class  );
	       builtInMap.put(Character.class,char.class  );
	       builtInMap.put(Byte.class,byte.class  );
	       builtInMap.put(Void.class,void.class  );
	       builtInMap.put(Short.class,short.class  );
	}


	/**
	 * Given a field name and Class, the method returns  
	 * a <code>Field</code> presenting the given field.
	 * The method also fetches private fields.
	 * Returns <code>null</code> if field is not found.
	 */
	public static Field getField(String field,Class<?> cl) throws Exception {
	    final Field fields[] =
	    	cl.getDeclaredFields();
	    for (int i = 0; i < fields.length; ++i) {
	      if (fields[i].toString().endsWith(field)){
	    	  fields[i].setAccessible(true);
	    	  return fields[i];
	      }
	    }
	    return null;
	}

	/**
	 * Create an instance of a class <code>cl</code> from constructor with
	 * arguments <code>args</code>, even if the constructor is a private constructor.
	 * When looking for the constructor, arguments which can be converted to primitives 
	 * are searched as primitives.<br>
	 * For example, if args types are {String,Integer), the following constructor is searched:
	 * constructor(String,int).
	 */
	public static Object newInstance(Class<? extends Object> cl,Object[] args) throws Exception {
		Class<? extends Object>[] classArray = new Class<?>[args == null ? 0 :args.length];
		for (int i = 0 ; i < args.length;i++){
			classArray[i] =  builtInMap.get(args[i].getClass()) == null ? args[i].getClass(): builtInMap.get(args[i].getClass());
		}
		Constructor<? extends Object> constructor = cl.getDeclaredConstructor(classArray);
		constructor.setAccessible(true);
		return constructor.newInstance(args);
	}

}
