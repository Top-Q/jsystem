/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.scenario.Parameter.ParameterType;
import jsystem.utils.StringUtils;
import jsystem.utils.beans.MethodElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RunnerSOTest extends RunnerTest {
	String xpath;
	String[] parametersName;
	Class<?>[] parametersType;
	String methodName;
	String descriptor;
	public RunnerSOTest(String methodName, String[] parametersName, Class<?>[] parametersType, String descriptor) {
		super(SystemObjectOperation.class.getName(), "testExecuteOperation");
		this.parametersName = parametersName;
		this.parametersType = parametersType;
		this.methodName = methodName;
		this.descriptor = descriptor;
	}

	public RunnerSOTest cloneTest() {
		RunnerSOTest test = new RunnerSOTest(methodName, parametersName, parametersType, descriptor);
		// Clone the properties as well
		
		test.setProperties((Properties) getProperties().clone());
		test.setXpath(xpath);
		return test;
	}

	public String toString() {
		String meaningful = super.getMeaningfulName();
		if (meaningful == null
				|| "true".equals(JSystemProperties.getInstance().getPreference(
						FrameworkOptions.IGNORE_MEANINGFUL_NAME))) {
			return processTestName("${xpath} " + methodName + " " + getParametersAsString());
		}
		return meaningful;
	}
	public String getMeaningfulName() {
		return toString();
	}

	public String getParametersAsString(){
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < parametersName.length; i++){
			if(i != 0){
				buf.append(", ");
			}
			buf.append(parametersName[i]);
			buf.append("=${");
			buf.append(parametersName[i]);
			buf.append("}");
		}
		return buf.toString();
	}

	protected String getValueForParameter(String parameter) {
		Parameter p = parameters.get(parameter);
		if(p != null && p.getValue() != null){
			return p.getValue().toString();
		}
		return "${" + parameter + "}";
	}
	public void addPrivateTags(Document doc, Element jsystem) {
		Element p = doc.createElement("sysproperty");
		p.setAttribute("key", RunningProperties.SYSTEM_OBJECT_OPERATION);
		p.setAttribute("value", "true");
		jsystem.appendChild(p);
		
		p = doc.createElement("sysproperty");
		p.setAttribute("key", RunningProperties.SYSTEM_OBJECT_OPERATION +".method");
		p.setAttribute("value", methodName);
		jsystem.appendChild(p);

		p = doc.createElement("sysproperty");
		p.setAttribute("key", RunningProperties.SYSTEM_OBJECT_OPERATION +".params");
		p.setAttribute("value", StringUtils.objectArrayToString(";", (Object[])parametersName));
		jsystem.appendChild(p);

		p = doc.createElement("sysproperty");
		p.setAttribute("key", RunningProperties.SYSTEM_OBJECT_OPERATION +".descriptor");
		p.setAttribute("value", descriptor);
		jsystem.appendChild(p);
		
		p = doc.createElement("sysproperty");
		p.setAttribute("key", RunningProperties.SYSTEM_OBJECT_OPERATION +".properties");
		p.setAttribute("value", getPropertiesAsString());
		jsystem.appendChild(p);

		p = doc.createElement("sysproperty");
		p.setAttribute("key", RunningProperties.PARAM_PREFIX +"xpath");
		p.setAttribute("value", xpath);
		jsystem.appendChild(p);
	}
	

	public static String getParametersAsString(String[] params) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < params.length; i++) {
			if (i != 0) {
				buf.append(", ");
			}
			buf.append(params[i]);
		}
		return buf.toString();
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	/**
	 * creates the parameters HashMap for the parameters tab in the UI uses the
	 * class and tests javadocs.
	 * 
	 * possible notation: ------------------ -
	 * 
	 * @exclude - excluding parameters - extended explenation at class javadoc -
	 * @include - including parameters - extended explenation at class javadoc
	 * 
	 *          special supported method names: -------------------------------
	 *          - set<Parameter> = enables a parameter value setting -
	 *          get<Parameter> = enables a parameter value getting -
	 *          get<Parameter>Options = enables a selection menu with the
	 *          specified options array in the method.
	 */
	protected void loadParameters() {
		parameters = new HashMap<String, Parameter>();
		if (test == null) {
			log.warning("test class could not be loaded. class="
					+ getClassName() + " method=" + getMethodName());
			return;
		}

		for (int i = 0; i < parametersName.length; i++) {
			/**
			 * Go over all the set methods
			 */
			Class<?> type = parametersType[i];
			String paramName = parametersName[i];
			Parameter currentParameter = new Parameter();
			currentParameter.setName(paramName);

			currentParameter.setMandatory(true);

			if (type.equals(Date.class)) {
				currentParameter.setType(ParameterType.DATE);
			} else if (type.equals(File.class)) {
				currentParameter.setType(ParameterType.FILE);
			} else if (type.equals(String.class)) {
				currentParameter.setType(ParameterType.STRING);
			} else if (type.equals(String[].class)) {
				currentParameter.setType(ParameterType.STRING_ARRAY);
				if (currentParameter.isAsOptions()) {
					ParameterProvider provider;
					try {
						provider = (ParameterProvider) getClass()
								.getClassLoader()
								.loadClass(
										"jsystem.extensions.paramproviders.StringArrayOptionsParameterProvider")
								.newInstance();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					currentParameter.setProvider(provider);
					currentParameter.setParamClass(type);
				}
			} else if (type.equals(Integer.TYPE)) {
				currentParameter.setType(ParameterType.INT);
			} else if (type.equals(Boolean.TYPE)) {
				currentParameter.setType(ParameterType.BOOLEAN);
			} else if (type.equals(Long.TYPE)) {
				currentParameter.setType(ParameterType.LONG);
			} else if (type.equals(Float.TYPE)) {
				currentParameter.setType(ParameterType.FLOAT);
			} else if (type.equals(Double.TYPE)) {
				currentParameter.setType(ParameterType.DOUBLE);
			} else if (type.equals(Short.TYPE)) {
				currentParameter.setType(ParameterType.SHORT);
			} else if (type.isEnum()) {
				currentParameter.setType(ParameterType.ENUM);

				currentParameter.setAsOptions(true);
				/*
				 * convert the enums options into string array
				 */
				Object[] enumConstants = type.getEnumConstants();
				String[] enumStrings = new String[enumConstants.length];
				HashMap<String, String> enumStringsAndNames = new HashMap<String, String>();
				for (int ii = 0; ii < enumConstants.length; ii++) {
					String name = ((Enum<?>) enumConstants[ii]).name();
					String toString = ((Enum<?>) enumConstants[ii]).toString();
					enumStrings[ii] = toString;
					enumStringsAndNames.put(toString, name);
				}
				currentParameter.setOptions(enumStrings);
				currentParameter.setEnumStringsAndNames(enumStringsAndNames);
			} else {
				// TODO throw exception
				log.fine("Unknown parameter type: " + type.getName() + " for: "
						+ paramName);
				continue;
			}

			/*
			 * init the value and the default value
			 */
			// currentParameter.setDefaultValue(defaultValue);
			// currentParameter.setValue(defaultValue);
			parameters.put(currentParameter.getName(), currentParameter);
		}
		
		Parameter xpathParam = new Parameter();
		xpathParam.setName("xpath");
		xpathParam.setType(ParameterType.STRING);
		xpathParam.setValue(xpath);
		parameters.put(xpathParam.getName(), xpathParam);
	}
	public static RunnerSOTest initFromNodeList(NodeList properties) throws Exception{
		String methodName = null;
		String descriptor = null;
		String parameterNames = null;
		for(int i = 0; i < properties.getLength(); i++){
			if(properties.item(i) instanceof Element){
				Element prop = (Element)properties.item(i);
				String key = prop.getAttribute("key");
				if(key == null){
					continue;
				}
				if(key.equals(RunningProperties.SYSTEM_OBJECT_OPERATION +".params")){
					parameterNames = prop.getAttribute("value");
				} else if (key.equals(RunningProperties.SYSTEM_OBJECT_OPERATION +".method")){
					methodName = prop.getAttribute("value");
				} else if (key.equals(RunningProperties.SYSTEM_OBJECT_OPERATION +".descriptor")){
					descriptor = prop.getAttribute("value");
				}
				if(parameterNames != null && methodName != null && descriptor != null){
					String[] pnames = new String[0];
					if(parameterNames != null && !parameterNames.isEmpty()){
						pnames = parameterNames.split(";");
					}
					RunnerSOTest soTest = new RunnerSOTest(methodName, pnames,MethodElement.getMethodClassFromDescriptor(descriptor), descriptor);
					//soTest.setXpath(xpath);
					return soTest;
				}
			}
		}
		throw new Exception("Fail to find class method and descriptor");
	}

}
