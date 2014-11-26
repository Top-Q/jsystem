/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils.beans;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.IgnoreMethod;
import jsystem.framework.TestBeanClass;
import jsystem.framework.TestBeanMethod;
import jsystem.framework.common.CommonResources;
import jsystem.framework.scenario.Parameter.ParameterType;
import jsystem.framework.scenario.ParameterProvider;
import jsystem.framework.scenario.PropertyValidator;
import jsystem.framework.scenario.UseProvider;
import jsystem.runner.loader.LoadersManager;
import jsystem.utils.DateUtils;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

/**
 * Utilities class for bean exploring and manipulations.
 * 
 * @author guy.arieli
 */
public class BeanUtils {
	private static Logger log = Logger.getLogger(BeanUtils.class.getName());

	/**
	 * Get all the bean elements of a class
	 * 
	 * @param reflectClass
	 *            the class to explore
	 * @param checkForGet
	 *            if set to true will check for getter method as well otherwise
	 *            only the setter is mandatory.
	 * @param fieldsClass
	 *            an array of all the type of classes that are required:
	 *            String.class, Integer.TYPE ...
	 * @return An map with field name as key contain all the bean elements.
	 */
	public static HashMap<String, BeanElement> getBeanMap(Class<?> reflectClass, boolean checkForGet,
			boolean supportEnum, Class<?>... fieldsClass) {
		ArrayList<BeanElement> beans = getBeans(reflectClass, checkForGet, supportEnum, fieldsClass);
		HashMap<String, BeanElement> map = new HashMap<String, BeanElement>() {
			private static final long serialVersionUID = -5404691466641093312L;

			@Override
			public BeanElement put(String key, BeanElement value) {
				return super.put(key.toLowerCase(), value);
			}

			@Override
			public BeanElement get(Object key) {
				return super.get(key.toString().toLowerCase());
			}
		};
		for (BeanElement bean : beans) {
			// insert it were the field is start with lower case.
			map.put(bean.getName().toLowerCase(), bean);
		}
		return map;
	}

	/**
	 * Get all the bean elements of a class
	 * 
	 * @param reflectClass
	 *            the class to explore
	 * @param checkForGet
	 *            if set to true will check for getter method as well otherwise
	 *            only the setter is mandatory.
	 * @param fieldsClass
	 *            an array of all the type of classes that are required:
	 *            String.class, Integer.TYPE ...
	 * @return An array list of contain all the bean elements.
	 */
	public static ArrayList<BeanElement> getBeans(Class<?> reflectClass, boolean checkForGet, boolean supportEnum,
			Class<?>... fieldsClass) {

		ArrayList<BeanElement> beans = new ArrayList<BeanElement>();
		Method[] methods = reflectClass.getMethods();
		for (Method currentMethod : methods) {
			// the method should start with set
			if (currentMethod.getName().toLowerCase().startsWith("set")) {
				if (!Modifier.isPublic(currentMethod.getModifiers())) {
					continue;
				}
				// should be ignored
				if (currentMethod.getAnnotation(IgnoreMethod.class) != null) {
					continue;
				}
				String fieldName = currentMethod.getName().substring("set".length(), currentMethod.getName().length());
				// and should have single parameter
				Class<?>[] types = currentMethod.getParameterTypes();
				if (types.length == 1) {
					UseProvider useProvider = currentMethod.getAnnotation(UseProvider.class);
					Class<?> paramClass = types[0];
					if ((supportEnum && paramClass.isEnum()) || isClassOfTypes(paramClass, fieldsClass)
							|| useProvider != null) {
						Method getter = findGetMethod(methods, paramClass, fieldName);
						if (checkForGet && getter == null) {
							continue;
						}
						TestBeanMethod testBeanMethod = currentMethod.getAnnotation(TestBeanMethod.class);
						BeanElement beanElement = new BeanElement();
						if (testBeanMethod != null) {
							if (testBeanMethod.ignore()) {
								continue;
							}
							Class<? extends PropertyValidator>[] validators = testBeanMethod.validators();
							beanElement.setValidators(validators);
						}
						if (useProvider != null) {
							try {
								String[] args = useProvider.config();
								ParameterProvider provider = (ParameterProvider) LoadersManager.getInstance()
										.getLoader().loadClass(useProvider.provider().getName()).newInstance();
								provider.setProviderConfig(args);
								beanElement.setParameterProvider(provider);
							} catch (Exception e) {
								log.log(Level.WARNING, "Fail to create new instance of provider", e);
								continue;
							}
						}
						if (paramClass.isEnum()) {
							beanElement.setHasOptions(true);
							Object[] array = paramClass.getEnumConstants();
							if (array != null) {
								String[] options = new String[array.length];
								for (int objectIndex = 0; objectIndex < array.length; objectIndex++) {
									options[objectIndex] = ((Enum<?>) array[objectIndex]).name();
								}
								beanElement.setOptions(options);
							}
						} else {
							Method optionsMethod = findGetOptionsMethod(methods, paramClass, fieldName);
							if (optionsMethod != null) {
								beanElement.setHasOptions(true);
								try {
									Object[] array = (Object[]) optionsMethod.invoke(reflectClass.newInstance());
									if (array != null) {
										String[] options = new String[array.length];
										for (int optionIndex = 0; optionIndex < array.length; optionIndex++) {
											options[optionIndex] = array[optionIndex].toString();
										}
										beanElement.setOptions(options);
									}

								} catch (Exception e) {
									e.printStackTrace();
									continue;
								}
							}
							// reflectClass.getMethod("get", parameterTypes)
						}
						if (testBeanMethod != null) {
							String[] options = testBeanMethod.options();
							if (options != null && options.length > 0) {
								beanElement.setHasOptions(true);
								beanElement.setOptions(options);
							}
							beanElement.setGroups(testBeanMethod.group());
							beanElement.setEditable(testBeanMethod.editable());
						}
						beanElement.setName(fieldName);
						beanElement.setSetMethod(currentMethod);
						beanElement.setGetMethod(getter);
						beanElement.setType(paramClass);
						beans.add(beanElement);
					}
				}
			}
		}
		TestBeanClass testBeanClass = reflectClass.getAnnotation(TestBeanClass.class);
		if (testBeanClass != null) {
			String[] includes = testBeanClass.include();
			if (includes != null && includes.length > 0) {
				ArrayList<BeanElement> orderedBeans = new ArrayList<BeanElement>();
				for (String currentInclude : includes) {
					if (currentInclude == null) {
						continue;
					}
					String includeToLower = currentInclude.toLowerCase();
					for (BeanElement currentBeanElement : beans) {
						if (currentBeanElement.getName().toLowerCase().equals(includeToLower)) {
							orderedBeans.add(currentBeanElement);
						}
					}
				}
				return orderedBeans;
			}
		}
		return beans;
	}

	public static boolean isClassOfTypes(Class<?> clazz, Class<?>... fieldsClass) {
		// if not set
		if (fieldsClass == null || fieldsClass.length == 0) {
			return true;
		}
		for (Class<?> className : fieldsClass) {
			if (className.isAssignableFrom(clazz)) {
				return true;
			}
		}
		return false;
	}

	private static Method findGetMethod(Method[] methods, Class<?> returnType, String fieldName) {
		for (Method currentMethod : methods) {
			if (!Modifier.isPublic(currentMethod.getModifiers())) {
				continue;
			}
			if (returnType.equals(Boolean.TYPE)) {
				if (currentMethod.getName().toLowerCase().equals("is" + fieldName.toLowerCase())) {
					if (returnType.equals(currentMethod.getReturnType())) {
						return currentMethod;
					}
				}
			}
			if (currentMethod.getName().toLowerCase().equals("get" + fieldName.toLowerCase())) {
				if (returnType.equals(currentMethod.getReturnType())) {
					return currentMethod;
				}
			}
		}
		return null;
	}

	private static Method findGetOptionsMethod(Method[] methods, Class<?> returnType, String fieldName) {
		for (Method currentMethod : methods) {
			if (!Modifier.isPublic(currentMethod.getModifiers())) {
				continue;
			}
			if (currentMethod.getGenericParameterTypes() == null
					|| currentMethod.getGenericParameterTypes().length != 0) {
				continue;
			}
			if (currentMethod.getName().toLowerCase().equals("get" + fieldName.toLowerCase() + "options")) {
				if (returnType.isArray()) {
					if (currentMethod.getReturnType().equals(returnType)) {
						return currentMethod;
					}
				} else {
					if (currentMethod.getReturnType().isArray()
							&& currentMethod.getReturnType().getComponentType().equals(returnType)) {
						return currentMethod;
					}
				}

			}
		}
		return null;
	}

	/**
	 * 
	 * @return A list of all the basic types: String, int, long, float, short
	 *         and double.
	 */
	public static Class<?>[] getBasicTypes() {
		return new Class[] { String.class, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Short.TYPE, Boolean.TYPE,
				Byte.TYPE, File.class, Date.class, Enum.class, String[].class };
	}

	/**
	 * Get an object bean elements value in Properties format
	 * 
	 * @param object
	 *            The object to explore
	 * @param beanElements
	 *            The array of bean element to explore.
	 * @return <code>Properties</code> object contain the field name and values.
	 * @throws Exception
	 */
	public static Properties objectToProperties(Object object, ArrayList<BeanElement> beanElements) throws Exception {
		Properties properties = new Properties();
		for (BeanElement currentBeanElement : beanElements) {
			if (currentBeanElement.getGetMethod() == null) {
				continue;
			}
			Object value = currentBeanElement.getGetMethod().invoke(object, new Object[0]);
			if (value != null) {
				properties.put(currentBeanElement.getName(), StringUtils.advancedToString(value));
			}
		}
		return properties;
	}

	/**
	 * Create an object based on is <code>Class</code> and properties object.
	 * 
	 * @param clazz
	 *            the class type of the object.
	 * @param properties
	 *            properties object to init the object with.
	 * @return the instantiated object
	 * @throws Exception
	 */
	public static Object propertiesToObject(Class<?> clazz, HashMap<String, String> properties) throws Exception {
		Object objectInstance = clazz.newInstance();
		ArrayList<BeanElement> beans = getBeans(clazz, false, true, getBasicTypes());
		for (BeanElement currentBeanElement : beans) {
			invoke(objectInstance, currentBeanElement.getSetMethod(), properties.get(currentBeanElement.getName()),
					currentBeanElement.getType());
		}
		return objectInstance;
	}

	/**
	 * Invoke setter method of an object using a string value translated to the
	 * expected value types.
	 * 
	 * @param object
	 *            the object to operate on.
	 * @param method
	 *            the method to invoke.
	 * @param value
	 *            the string value to use.
	 * @param type
	 *            the type of the input.
	 * @throws Exception
	 */
	public static void invoke(Object object, Method method, String value, Class<?> type) throws Exception {
		if (value == null && !String.class.equals(type)) {
			return;
		}
		try {
			method.invoke(object, getObjects(type, value));
		} catch (Throwable t) {
			log.warning("Unknown type: " + type.getName());
		}
	}

	public static CellEditorType getBeanType(BeanElement element) {
		Class<?> elementType = element.getType();
		if (element.getParameterProvider() != null) {
			return CellEditorType.USER_DEFINED;
		} else if (elementType.equals(String[].class) && element.isHasOptions()) {
			return CellEditorType.MULTI_SELECTION_LIST;
		} else if (element.isHasOptions()) {
			return CellEditorType.LIST;
		} else if (elementType.equals(Integer.TYPE)) {
			return CellEditorType.INT;
		} else if (elementType.equals(Long.TYPE)) {
			return CellEditorType.LONG;
		} else if (elementType.equals(Double.TYPE)) {
			return CellEditorType.DOUBLE;
		} else if (elementType.equals(Short.TYPE)) {
			return CellEditorType.SHORT;
		} else if (elementType.equals(Float.TYPE)) {
			return CellEditorType.FLOAT;
		} else if (elementType.equals(Boolean.TYPE)) {
			return CellEditorType.BOOLEAN;
		} else if (elementType.equals(String.class)) {
			return CellEditorType.STRING;
		} else if (elementType.equals(Byte.TYPE)) {
			return CellEditorType.BYTE;
		} else if (elementType.equals(File.class)) {
			return CellEditorType.FILE;
		} else if (elementType.equals(Date.class)) {
			return CellEditorType.DATE;
		}
		return CellEditorType.UNKNOWN;
	}

	/**
	 * convert the given value to a matching object according to the parameter
	 * type
	 * 
	 * @param value
	 *            the value to convert
	 * @param type
	 *            the Parameter Type
	 * @return the Object matching value by type or null if the value specified
	 *         is null
	 */
	public static Object getMatchingTypeObject(Object value, ParameterType type) {
		if (null == value) {
			return null;
		}
		switch (type) {
		case FILE:
			return FileUtils.replaceSeparator(value.toString());
		case STRING:
		case ENUM:
		case DATE:
		case REFERENCE:
			return value.toString();
		case BOOLEAN:
			return Boolean.valueOf(value.toString());
		case FLOAT:
			return Float.valueOf(value.toString());
		case INT:
			return Integer.valueOf(value.toString());
		case LONG:
			return Long.valueOf(value.toString());
		case DOUBLE:
			return Double.valueOf(value.toString());
		case SHORT:
			return Short.valueOf(value.toString());
		case STRING_ARRAY:
			if (value instanceof String) {
				return value.toString().split(CommonResources.DELIMITER);
			} else if (value instanceof String[]) {
				StringBuffer toReturn = new StringBuffer();
				for (int i = 0; i < ((String[]) value).length; i++) {
					if (toReturn.length() == 0) {
						toReturn = new StringBuffer(((String[]) value)[i]);
					} else {
						toReturn.append(CommonResources.DELIMITER + ((String[]) value)[i]);
					}
				}
				return toReturn.toString();
			}
		case USER_DEFINED:
			return value;
		}
		return value;
	}

	public static Object getObjects(Class<?> clazz, String value) {
		if (clazz.equals(String.class)) {
			return value;
		} else if (clazz.equals(Integer.TYPE)) {
			if (value == null) {
				return -1;
			}
			return Integer.parseInt(value);
		} else if (clazz.equals(Long.TYPE)) {
			if (value == null) {
				return -1L;
			}
			return Long.parseLong(value);
		} else if (clazz.equals(Float.TYPE)) {
			if (value == null) {
				return (float) -1;
			}
			return Float.parseFloat(value);
		} else if (clazz.equals(Double.TYPE)) {
			if (value == null) {
				return (double) -1;
			}
			return Double.parseDouble(value);
		} else if (clazz.equals(Short.TYPE)) {
			if (value == null) {
				return (short) -1;
			}
			return Short.parseShort(value);
		} else if (clazz.equals(Boolean.TYPE)) {
			if (value == null) {
				return false;
			}
			return Boolean.parseBoolean(value);
		} else if (clazz.equals(Byte.TYPE)) {
			if (value == null) {
				return false;
			}
			return Byte.parseByte(value);
		} else if (clazz.equals(Character.TYPE)) {
			if (value == null) {
				return null;
			}
			if (value.length() > 0) {
				return value.charAt(0);
			}
		} else if (clazz.equals(File.class)) {
			if (value == null) {
				return null;
			}
			return new File(value);
		} else if (Enum.class.isAssignableFrom(clazz)) {
			if (value == null) {
				return null;
			}
			for (Object currentObject : clazz.getEnumConstants()) {
				if (currentObject.toString().equals(value)) {
					return currentObject;
				}
			}
			return null;
		} else if (clazz.equals(Date.class)) {
			if (value == null) {
				return null;
			}
			try {
				return DateUtils.parseDate(value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new Date(System.currentTimeMillis());
		} else if (String[].class.isAssignableFrom(clazz)) {
			if (value == null) {
				return null;
			}
			return value.split(CommonResources.DELIMITER);
		}
		throw new RuntimeException("Unknown class type: " + clazz.getName());
	}

	/**
	 * Initialize class using it name
	 * 
	 * @param type
	 *            the class name
	 * @return the class instance
	 * @throws Exception
	 */
	public static Class<?> getClassType(String type) throws Exception {
		if (type.equals("int")) {
			return Integer.TYPE;
		} else if (type.equals("long")) {
			return Long.TYPE;
		} else if (type.equals("float")) {
			return Float.TYPE;
		} else if (type.equals("double")) {
			return Double.TYPE;
		} else if (type.equals("short")) {
			return Short.TYPE;
		} else if (type.equals("byte")) {
			return Byte.TYPE;
		} else if (type.equals("char")) {
			return Character.TYPE;
		} else if (type.equals("boolean")) {
			return Boolean.TYPE;
		} else {
			return LoadersManager.getInstance().getLoader().loadClass(type);
		}
	}

	/**
	 * Create instance from the specified class name and cast it to the
	 * specified class type.
	 * 
	 * @param className
	 * @param type
	 * @return instance from the specified class or null if failed
	 * @author Itai
	 */
	public static <T> T createInstanceFromClassName(final String className, final Class<T> type) {
		Class<?> clazz = null;
		try {
			clazz = LoadersManager.getInstance().getLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
		if (clazz == null) {
			return null;
		}
		Object instance = null;
		try {
			instance = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
		return type.cast(instance);

	}
}
