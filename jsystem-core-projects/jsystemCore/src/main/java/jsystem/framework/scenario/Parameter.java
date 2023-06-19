/*
 * Created on Dec 15, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.extensions.report.html.HtmlCodeWriter;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.utils.ObjectUtils;
import jsystem.utils.StringUtils;
import jsystem.utils.beans.BeanUtils;

/**
 * POJO which models a test/scenario parameter. Parameters are created by the
 * RunnerTest object when test class is loaded.
 * 
 * in TAS 4.9 the dirty flag was added, the purpose of this flag is to help the
 * system avoid unneeded updates of scenario/tests. when parameters are fetched
 * from the JTest object, the dirtyFlag is reset. When ever an update is made to
 * a parameter, if the new value of the parameter is different from the current
 * value, the dirty flag is raised. When saving parameters the system ignores
 * Parameters which are not dirty.
 * 
 * @author guy.arieli, golan.derazon
 */
public class Parameter {
	private static Logger log = Logger.getLogger(Parameter.class.getName());

	public enum ParameterType {
		STRING, INT, LONG, BOOLEAN, FLOAT, DOUBLE, SHORT, ENUM, FILE, DATE(
				"Date/Time"), REFERENCE, STRING_ARRAY, USER_DEFINED, JSYSTEM_INTERNAL_FLAG;

		private String description;

		private ParameterType() {
			description = toString().toLowerCase();
		}

		private ParameterType(String description) {
			this.description = description;
		}

		/**
		 * used for the parameters panel
		 * 
		 * @return
		 */
		public String getDescription() {
			return description;
		}
	}

	private boolean dirty = false;

	/**
	 * Added to support parameters changing in sub-scenarios. Only a parameter
	 * that has a raised save flag will be saved.
	 */
	private boolean shouldBeSaved = false;

	private ParameterType type = ParameterType.STRING;

	private String originalDescription = null;

	private String description = null;

	private String name = null;

	private String section = null;// "General";

	protected Object value = null;

	private Object defaultValue = null;

	private boolean asOptions = false;

	private Method setMethod = null;

	Method getMethod = null;

	private Object[] options;

	private boolean visible = true;

	private boolean editable = true;

	private boolean badRefernceParameter = false;

	private Class<?> paramClass = null;

	private ParameterProvider provider;
	/**
	 * added for support for ENUMS with customizable toString method
	 */
	private HashMap<String, String> enumStringsAndNames;

	public String getDescription() {
		return description;
	}

	private boolean isMandatory = false;

	public void setDescription(String description) {
		StringBuilder stringBuilder = new StringBuilder(isMandatory ? "Mandatory" : "");
		if (!StringUtils.isEmpty(description)) {
			stringBuilder.append(isMandatory ? " : " : "").append(description);
			this.originalDescription = description;
			initSection();
		}
		this.description = stringBuilder.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ParameterType getType() {
		return type;
	}

	public void setType(ParameterType type) {
		this.type = type;
	}

	public Object getValue() {
		if (value == null) {
			return null;
		}
		// The param class can be null in primitive parameters. Fixes issue #239
		if (null == paramClass && ParametersManager.isReferenceValue(value)) {
			return value;
		}
		// Added as fix for issue #214
		if (paramClass != null && !paramClass.isArray() && ParametersManager.isReferenceValue(value)) {
			return value;
		}

		if (getProvider() != null) {
			if (value instanceof String) {
				try {
					return provider.getFromString((String) value);
				} catch (Exception e) {
					log.log(Level.WARNING, "Fail to convert to object: " + value, e);
				}
			}
		}
		return BeanUtils.getMatchingTypeObject(value, type);
	}

	/**
	 * sets the value for a parameter
	 * 
	 * @param inValue
	 */
	public void setValue(Object inValue) throws NumberFormatException {
		if (isAsOptions()
				&& "false".equals(
						JSystemProperties.getInstance().getPreference(FrameworkOptions.ADD_DEFAULTS_CURRENT_TO_PARAM))
				&& !isValueInOptions(inValue) && getOptions() != null && getOptions().length > 0) {
			inValue = getOptions()[0];
		}

		inValue = normalizeInValue(inValue);

		// if it is a reference value than it won't be checked for format
		// compatibility
		if (!ParametersManager.isReferenceValue(inValue)) {
			inValue = BeanUtils.getMatchingTypeObject(inValue, type);
		}

		// We are ready to check if the value changed
		if (!type.equals(ParameterType.USER_DEFINED)) {
			if (ObjectUtils.nullSafeEquals(inValue, value)) {
				return;
			}
		} else {
			// Comparing parameter from type user defined is a little bit more
			// complicated.
			if (isUserDefinedEquals(inValue)) {
				return;
			}
		}

		// The value was changed.
		value = inValue;
		setDirty();
	}

	/**
	 * Checks if parameter from type user defined was changed. <br>
	 * if current and new parameter are null will return true <br>
	 * if one of the parameters is null and the other one is not will return
	 * false<br>
	 * 
	 * @param inValue
	 *            new value from type parameter provider
	 * @return true, and only if the new value is different from the current
	 *         one.
	 */
	private boolean isUserDefinedEquals(final Object inValue) {
		if (!type.equals(ParameterType.USER_DEFINED)) {
			log.warning("Parameter is not from type user defined");
			return false;
		}
		if (value == null && inValue == null) {
			return true;
		}
		if ((value == null && inValue != null) || (value != null && inValue == null)) {
			return false;
		}

		if (inValue instanceof String && value instanceof String) {
			// We need the following regular expression for cleaning the time
			// stamp from the string presentation of objects.
			// [A-Z][a-z]{2}\s[A-Z][a-z]{2}\s\d{2}\s\d{2}:\d{2}:\d{2}\s[A-Z]{3}\s\d{4}
			final String cleanTimestampRegex = "[A-Z][a-z]{2}\\s[A-Z][a-z]{2}\\s\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\s[A-Z]{3}\\s\\d{4}";
			if (value.toString().replaceFirst(cleanTimestampRegex, "")
					.equals(inValue.toString().replaceFirst(cleanTimestampRegex, ""))) {
				// The parameter is from type parameter provider and the value
				// was not changed.
				return true;
			}
		}
		return false;
	}

	/**
	 * Normalize the parameter we want to set
	 * 
	 * @param inValue
	 * @return normalize object.
	 */
	private Object normalizeInValue(final Object inValue) {
		Object normalizeValue = inValue;
		switch (type) {
		case DATE:
		case FILE:
			if (StringUtils.isEmpty(inValue + "")) {
				normalizeValue = null;
			}
			break;
		case STRING_ARRAY:
			if (StringUtils.isEmpty(inValue + "")) {
				normalizeValue = null;
			} else if ((inValue instanceof String[])) {
				if (!isAsOptions()) {
					normalizeValue = StringUtils.objectArrayToString(CommonResources.DELIMITER, (Object[]) inValue);
				}
			}
			break;
		case STRING:
			if (inValue != null) {
				if (defaultValue == null && inValue.toString().isEmpty()) {
					normalizeValue = null;
				}
			}
			break;
		case USER_DEFINED:
			if (StringUtils.isEmpty(inValue + "")) {
				normalizeValue = null;
			}
			break;
		case BOOLEAN:
			if (null == inValue) {
				normalizeValue = "false";
			}
			break;

		default:
			break;
		}
		return normalizeValue;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isAsOptions() {
		return asOptions;
	}

	public void setAsOptions(boolean asOptions) {
		this.asOptions = asOptions;
	}

	public Object[] getOptions() {
		// if parameter not set to disable then perform the option add
		if (!"false".equals(
				JSystemProperties.getInstance().getPreference(FrameworkOptions.ADD_DEFAULTS_CURRENT_TO_PARAM))) {
			/**
			 * if the defaultValue is not part of the options add it to the
			 * options.
			 */
			if (!isDefaultValueInOptions()) {
				addValueToOptions(defaultValue);
			}

			/**
			 * if the value is not part of the options add it to the options.
			 */
			if (!isCurrentValueInOptions()) {
				addValueToOptions(value);
			}
		}

		return options;
	}

	private void addValueToOptions(Object value) {
		if (value == null) {
			return;
		}
		Object[] toAdd = null;
		if (value.getClass().isArray()) {
			toAdd = (Object[]) value;
		} else {
			toAdd = new Object[] { value };
		}
		Object[] newOptions = new Object[options.length + toAdd.length];
		System.arraycopy(toAdd, 0, newOptions, 0, toAdd.length);
		System.arraycopy(options, 0, newOptions, toAdd.length, options.length);
		options = newOptions;
	}

	/**
	 * Check if the default value is part of the options
	 * 
	 * @return true if null or found in options
	 */
	private boolean isDefaultValueInOptions() {
		return isValueInOptions(defaultValue);
	}

	/**
	 * Check if the default value is part of the options
	 * 
	 * @return true if null or found in options
	 */
	private boolean isCurrentValueInOptions() {
		return isValueInOptions(value);
	}

	/**
	 * Check if the _value is part of the options. If _value is array an array
	 * verifies that all array elements are in options.
	 */
	private boolean isValueInOptions(Object _value) {
		if (options == null || _value == null) {
			return true;
		}
		Object[] objArr = null;
		if (_value.getClass().isArray()) {
			objArr = (Object[]) _value;
		} else {
			objArr = new Object[] { _value };
		}

		for (Object val : objArr) {
			boolean found = false;
			for (Object op : options) {
				if (val.equals(op)) {
					found = true;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	public void setOptions(Object o) {
		if (o instanceof Object[]) {
			this.options = (Object[]) o;
		} else if (o instanceof int[]) {
			int[] d = (int[]) o;
			Integer[] dd = new Integer[d.length];
			for (int i = 0; i < d.length; i++) {
				dd[i] = Integer.valueOf(d[i]);
			}
			options = dd;
		} else if (o instanceof long[]) {
			long[] d = (long[]) o;
			Long[] dd = new Long[d.length];
			for (int i = 0; i < d.length; i++) {
				dd[i] = Long.valueOf(d[i]);
			}
			options = dd;
		} else if (o instanceof float[]) {
			float[] d = (float[]) o;
			Float[] dd = new Float[d.length];
			for (int i = 0; i < d.length; i++) {
				dd[i] = Float.valueOf(d[i]);
			}
			options = dd;
		} else if (o instanceof double[]) {
			double[] d = (double[]) o;
			Double[] dd = new Double[d.length];
			for (int i = 0; i < d.length; i++) {
				dd[i] = Double.valueOf(d[i]);
			}
			options = dd;
		} else if (o instanceof short[]) {
			short[] d = (short[]) o;
			Short[] dd = new Short[d.length];
			for (int i = 0; i < d.length; i++) {
				dd[i] = Short.valueOf(d[i]);
			}
			options = dd;
		}
	}

	public String getParamTypeString() {
		return type.getDescription();
	}

	public Method getSetMethod() {
		return setMethod;
	}

	public Method getGetMethod() {
		return getMethod;
	}

	public void setSetMethod(Method setMethod) {
		this.setMethod = setMethod;
	}

	public void setGetMethod(Method getMethod) {
		this.getMethod = getMethod;
	}

	public String getSection() {
		if (StringUtils.isEmpty(section)) {
			return "General";
		}
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public void initSection() {
		// TODO: do we need to add getMethod here ?
		if (setMethod == null) {
			return;
		}
		String section = HtmlCodeWriter.getInstance().getMethodAnnotation(setMethod.getDeclaringClass().getName(),
				setMethod.getName(), "section");
		setSection(section);
	}

	@Override
	public String toString() {
		return "Parametr Name : " + name + "\nParametr section : " + section + "\nParametr value : " + value
				+ "\nClass: " + paramClass;
	}

	/**
	 * Check if the value was changed from the default value
	 * 
	 * @return the change value status
	 */
	public boolean isChanged() {
		if (defaultValue == null && value == null) { // both null = no change
			return false;
		} else if (defaultValue == null && value.toString().isEmpty()) {
			return false;
		} else {
			if (defaultValue != null && value != null) {
				return !defaultValue.toString().equals(value.toString());
			} else { // one is null and the other is not = changed
				return true;
			}
		}
	}

	/**
	 * Compartor for comparing between Parameters, created for Arrays.sort.
	 */
	public static Comparator<Parameter> ParameterNameComparator = new Comparator<Parameter>() {
		@Override
		public int compare(Parameter parameter, Parameter anotherParameter) {
			String paramName1 = parameter.getName();
			String paramName2 = anotherParameter.getName();
			return paramName1.compareTo(paramName2);
		}
	};

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return True if parameter value was changed and not saved to FileSystem
	 *         yet
	 */
	public boolean isDirty() {
		return dirty;
	}

	public void resetDirty() {
		dirty = false;
	}

	/**
	 * Signal a parameter was changed and save is needed to FileSystem
	 */
	public void setDirty() {
		dirty = true;
	}

	public Parameter cloneParameter() {
		Parameter param;
		try {
			param = (Parameter) getClass().newInstance();
		} catch (Exception e) {
			param = new Parameter();
		}
		param.dirty = dirty;
		param.type = type;
		param.originalDescription = originalDescription;
		param.description = description;
		param.isMandatory = isMandatory;
		param.name = name;
		param.section = section;
		param.value = value;
		param.defaultValue = defaultValue;
		param.asOptions = asOptions;
		param.setMethod = setMethod;
		param.getMethod = getMethod;
		param.provider = provider;
		param.paramClass = paramClass;
		if (options != null) {
			param.options = new Object[options.length];
			System.arraycopy(options, 0, param.options, 0, options.length);
		}
		param.visible = visible;
		param.editable = editable;
		return param;
	}

	public String getStringValue() {
		if (getValue() == null) {
			return null;
		}
		return getProvider() != null ? getProvider().getAsString(getValue()) : getValue().toString();

	}

	/**
	 * checks if the parameter value is a reference
	 * 
	 * @return
	 */
	public boolean isReferenceParam() {
		return ParametersManager.isReferenceValue(value);
	}

	/**
	 * if True then the reference threw an exception
	 */
	public boolean isBadRefernceParameter() {
		return badRefernceParameter;
	}

	public void setBadRefernceParameter(boolean badRefernceParameter) {
		this.badRefernceParameter = badRefernceParameter;
	}

	/**
	 * convert enum String to enum Name (using the HashMap) (Used only for enum
	 * type)
	 * 
	 * @return the enum matching name
	 */
	public String getEnumValueAsName() {
		if (getType().equals(ParameterType.ENUM) && enumStringsAndNames != null) {
			String tmp = enumStringsAndNames.get(value); // replace toString
															// value with name
			if (tmp != null) {
				return tmp;
			}
		}
		return value.toString();
	}

	public void setEnumStringsAndNames(HashMap<String, String> enumStringsAndNames) {
		this.enumStringsAndNames = enumStringsAndNames;
	}

	public ParameterProvider getProvider() {
		return provider;
	}

	public void setProvider(ParameterProvider provider) {
		this.provider = provider;
	}

	public Class<?> getParamClass() {
		return paramClass;
	}

	public void setParamClass(Class<?> paramClass) {
		this.paramClass = paramClass;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	/**
	 * Check if this parameter needs saving
	 * 
	 * @return True if this parameter should be saved
	 */
	public boolean shouldBeSaved() {
		return shouldBeSaved;
	}

	/**
	 * Signal this parameter should not be saved
	 */
	public void signalNotToSave() {
		this.shouldBeSaved = false;
	}

	/**
	 * Raise the save flag to save parameter on next save event
	 */
	public void signalToSave() {
		this.shouldBeSaved = true;
	}
}
