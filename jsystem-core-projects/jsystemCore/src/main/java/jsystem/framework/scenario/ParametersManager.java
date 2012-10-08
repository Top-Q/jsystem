/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import jsystem.framework.JSystemProperties;
import jsystem.framework.RunProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.fixture.FixtureManager;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Summary;
import jsystem.framework.scenario.Parameter.ParameterType;
import jsystem.framework.sut.SutFactory;
import jsystem.utils.DateUtils;
import jsystem.utils.FileUtils;
import jsystem.utils.NumberUtils;
import jsystem.utils.StringUtils;
import junit.framework.SystemTest;

/**
 * Enable execute test from regular runner like eclipse or ant with the
 * parameters that were set from the Scenario manager. To enable the feature set
 * parameters.enable=true in the jsystem.properties. On the first time a test is
 * been executed the a dialog will be shown with all the possible scenarios that
 * can be used as reference. Once a scenario is selected it will be saved to
 * parameters.properties file and the user will not be prompt again for the same
 * test. The test will be executed with the parameters that were set in the
 * selected scenario.
 * 
 * @author guy.arieli
 * 
 */
public class ParametersManager {
	
	private static Logger log = Logger.getLogger(ParametersManager.class.getName());
	
	public static Pattern PARAMETER_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

	/**
	 * if true parameters mode is enable
	 */
	private static boolean parametersEnable = false;

	/**
	 * hold the assignment between tests and scenario info The key is the test
	 * name and the value is the scenario name followed by ; and the test index
	 * in scenario
	 * 
	 * com.aqua.Test1=scenarios/defult;0
	 */
	private static Properties testAssignToScenaro = null;

	/**
	 * used to identify that a value is a reference
	 */
	public static final String REFERENCE_STRING = "${";
	
	/**
	 * current test object
	 */
	private static RunnerTest currentTest;
	/**
	 * a string that represents all scenario names till current test ("." separated)
	 */
	private static String scenarioPathString;
	/**
	 * current test full UUID
	 */
	private static String currentFullUuid;
	
	public static String BAD_PARAMETER = "BAD PARAMETER - SKIP IT";
	
	public enum Reference{
		RUN,SUMMARY,SUT,RANDOM,RANDOM_INT,RANDOM_DOUBLE,INCREMENT,SCENARIO,ANT_PARAMETER(""),ENVIRONMENT("env"),NULL;
		
		private String string;
		
		private Reference(){
			
		}
		
		private Reference(String string){
			this.string = string;
		}
		
		public String getRepresentation(){
			String value = (string == null)? super.toString() : string ;
			return value.toLowerCase();
		}
		
		public static Reference getReferenceByString(String string){
			for (Reference ref : Reference.values()){
				if (ref.getRepresentation().equals(string.toLowerCase())){
					return ref;
				}
			}
			return null;
		}
		
		public String toString(){
			return super.toString().toLowerCase();
		}
	}

	/**
	 * if parameters mode is enable will look for test assignment in
	 * parameters.properties file and load the test params from the file. If not
	 * set will prompt the user to select optional scenario.
	 * 
	 * @param test
	 */
	public static void initTestParameters(SystemTest test) throws Exception {
		boolean scenarioExists = (System.getProperty(RunningProperties.CURRENT_SCENARIO_NAME) !=null);
		
		/*
		 * Init the current fixture from system properties
		 * When running test from the eclipse as junit -Djsystem.current.fixture=<MyFixture>
		 * can be used to set the current fixture.
		 */
		String currentFixture = System.getProperty(RunningProperties.CURRENT_FIXTURE);
		if (currentFixture != null) {
			FixtureManager.getInstance().setCurrentFixture(currentFixture);
		}
		/*
		 * Init the disable status from system properties
		 */
		FixtureManager.getInstance().setDisableFixture(
				"true".equals(System.getProperty(RunningProperties.FIXTURE_DISABLE_TAG)));
		if (scenarioExists){
			scenarioPathString = System.getProperty(RunningProperties.PARENT_NAME).replaceFirst(".", "");
		}
		/*
		 * Init test properties
		 */
		loadTestParametersFromEnvironment(test);
		if (parametersEnable) {
			String testKey = test.getClass().getName() + "." + test.getName();
			String assignment = (String) testAssignToScenaro.get(testKey);
			while (true) { // will retry in case of problematic assignment
				if (assignment == null) {
					/*
					 * Collect all the tests of the current test type that can
					 * be found in all the scenarios
					 */
					ArrayList<String> testFound = ScenariosManager.getInstance().collectTestsOfType(test);
					if (testFound.size() == 0) { // if no test are found
						// return
						return;
					}
					/*
					 * Init the list of possible scenarios
					 */
					Object[] svalues = new Object[testFound.size() + 1];
					/*
					 * The first option will always be No parameters
					 */
					svalues[0] = "No parameters";
					System.arraycopy(testFound.toArray(), 0, svalues, 1, testFound.size());
					/*
					 * Let the user select the scenario
					 */
					assignment = (String) JOptionPane.showInputDialog(null,
							"Select the scenario for the test parameters", "Select parameters",
							JOptionPane.QUESTION_MESSAGE, null, svalues, svalues[0]);
					if (assignment == null) { // If the user cancel return
						return;
					}
					/*
					 * Put the user selection in the properties and save it to
					 * the file
					 */
					testAssignToScenaro.setProperty(testKey, assignment);
					testAssignToScenaro.store(new FileOutputStream("parameters.properties"), null);
				}
				if (assignment.equals("No parameters")) { // if the user
					// selected
					// No parameters return
					return;
				}
				/*
				 * extract the scenario name and the file index
				 */
				String[] values = assignment.split(";");
				String scenarioName = values[0];
				int indexInScenario = Integer.parseInt(values[1]);
				/*
				 * Init the scenario object and load the test with the
				 * parameters in the scenario
				 */
				Scenario scenario = ScenariosManager.getInstance().getScenario(scenarioName);
				ScenariosManager.getInstance().setCurrentScenario(scenario);
				try {
					scenario.initTestFromScenario(test, indexInScenario);
					return;
				} catch (Exception e) {
					/*
					 * if the original assignment is problematic (scenario file
					 * changed) retry with no assignment
					 */
					assignment = null;
					continue;
				}
			}
		}
	}

	public static void retrieveTestParameters(SystemTest test) throws Exception {
		/*
		 * Retrieve test properties
		 */
		retrieveTestParametersToEnvironment(test);
		// TODO: maybe need the "parametersEnable" issue
	}	
	
	/**
	 * Load test parameters from system properties. Any system properties (System.getProperties())
	 * that start with jsystem.params.XYZ consider as a parameter.
	 * It can be used from eclipse when using junit execute by setting the -D option 
	 * (-Djsystem.params.PacketSize=64)
	 * 
	 * @param test the test to set the parameters to.
	 * @throws Exception
	 */
	private static void loadTestParametersFromEnvironment(SystemTest test) throws Exception {
		boolean scenarioExists = (System.getProperty(RunningProperties.CURRENT_SCENARIO_NAME) !=null);
		
		Properties map = System.getProperties();
		Iterator<Object> iter = map.keySet().iterator();
		/*
		 * Extract all the properties start with the prefix (jsystem.params.).
		 */
		Properties properties = new Properties();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if (key != null && key.startsWith(RunningProperties.PARAM_PREFIX)) {
				properties.setProperty(key.substring(RunningProperties.PARAM_PREFIX.length()), map.getProperty(key));
			}
		}
		if (properties.get(RunningProperties.FIXTURE_PROPERTY_NAME) != null){
			test.setFixtureName(properties.get(RunningProperties.FIXTURE_PROPERTY_NAME).toString());
		}
		
		/*
		 * Init the param to the test
		 */
		
		currentTest = new RunnerTest(test.getClass().getName(), test.getName());
		currentTest.setTest(test);
		if (scenarioExists){ // run from JRunner
			currentFullUuid = test.getFullUUID();
			int lastIndex = currentFullUuid.indexOf(".");
			String testUuid = lastIndex<0? currentFullUuid : currentFullUuid.substring(lastIndex+1);
			currentTest.setUUID(testUuid);
			/**
			 * 
			 */			
			Properties props = ScenarioHelpers.getTestPropertiesFromScenarioProps(scenarioPathString, currentFullUuid);
			properties.putAll(props);
			/**
			 * 
			 */
			replaceReferenceProperties(properties,currentTest);
		}
		
		currentTest.setProperties(properties);
		currentTest.load();
		currentTest.loadParametersAndValuesAndUpdateTestClass();		
	}
	 
	private static void retrieveTestParametersToEnvironment(SystemTest test) throws Exception {
		if (StringUtils.isEmpty(test.getFullUUID())){
			if (JSystemProperties.getInstance().isReporterVm()){
				log.fine("running from dev studio, no need to return parameters");
				return;
			}else {
				throw new Exception("Empty test id. " +test.getName());
			}
		}
		/*
		 * Extract all the properties start with the prefix (jsystem.params.out).
		 */
		/*
		 * Retrieve the param from the test
		 */
		Parameter[] parameters = currentTest.getParameters();
		if (currentTest.getReturnParameters()== null || currentTest.getReturnParameters().length == 0){
			return;
		}
		Set<String> resturnParams = StringUtils.stringArrayToSet(currentTest.getReturnParameters());
		for (Parameter param : parameters) {
			if (!resturnParams.contains(param.getName())){
				continue;
			}
			Method getter = param.getGetMethod();
			if (getter != null) {
				String fullKey = RunningProperties.OUT_PARAM_PREFIX + param.getName();
				String value = param.getDefaultValue() == null ? "" : param.getDefaultValue().toString();
				value = getter.invoke(test) + "";
				if (param.getType().equals(ParameterType.STRING_ARRAY)) {
					value = (String)param.getValue();
				}
				System.setProperty(fullKey, value);
				// Adding return parameter to run properties for future analysis
				RunProperties.getInstance().setRunProperty(param.getName(), value);
				ListenerstManager.getInstance().report("Setting return parameter. Name=" + param.getName() + " value="+value);
			}
		}
	}
	
	/**
	 * replace a reference variable with it's actual value
	 * @param value	the reference value (${...)
	 * @param type	the type of the parameter to get value for (used for randomize reasons)
	 * @return	the actual value
	 * @throws Exception 
	 */
	public static Object replaceReferenceWithValue(Object value,ParameterType type) throws Exception{
		if (type == ParameterType.REFERENCE){
			throw new Exception("Reference to Reference is currently not supported!");
		}
		
		if (!isReferenceValue(value)){
			return value;
		}
		String valueString = value.toString();
		int index = valueString.lastIndexOf("}");
		String referenceString = valueString.substring(REFERENCE_STRING.length(),index);
		index = referenceString.indexOf(":");
		
		// In case there is : inside the reference string - split in accordingly
		// If not - it is an Ant parameter without value
		String enumString;
		String key;
		if (index != -1) {
			enumString = referenceString.substring(0,index);
			key = referenceString.substring(index+1);
		} else {
			enumString = "";
			key = referenceString;
		}

		// Check if we need to clean the reference string 
		// (might be required after coming from de-serialization)
		if (enumString.contains("\\")) {
			enumString = enumString.replace("\\", "");
		}
		
		Reference reference = Reference.getReferenceByString(enumString);
		switch (reference) {
		case RUN: // get from RunProperties
			value = RunProperties.getInstance().getRunProperty(key);
			break;

		case SUMMARY: // get from Summary
			value = Summary.getInstance().getProperty(key);
			break;

		case SUT: // get from the current SUT file
			String path = (key.startsWith("sut"))? key : "sut/"+key;
			path += "/text()";
			value = SutFactory.getInstance().getSutInstance().getValue(path);
			break;

		case SCENARIO: // get from the Scenario defined parameters
			value = ScenarioHelpers.getScenarioParametersValue(scenarioPathString,
					ScenarioHelpers.getScenarioFullUuid(currentFullUuid), key);
			break;

		case RANDOM: // choose the value randomly from the given range\group
		case RANDOM_INT:
		case RANDOM_DOUBLE:
			
			if (reference == Reference.RANDOM_INT){
				type = ParameterType.INT;
			}else if (reference == Reference.RANDOM_DOUBLE){
				type = ParameterType.DOUBLE;
			}
			
			if (key.indexOf(":")>=0){ // choose from a range
				index = key.indexOf(":");
				String lowValue = key.substring(0,index);
				String highValue = key.substring(index+1);
				double start = Double.parseDouble(lowValue);
				double end = Double.parseDouble(highValue);

				switch(type){
				case BOOLEAN:
				case ENUM:
					throw new Exception("Random in range is not supported for this type");
				case INT:
				case SHORT:
					value = NumberUtils.getRandomIntegerValueInRange(start, end);
					break;
				case LONG:
					value = NumberUtils.getRandomLongValueInRange(start, end);
					break;
				case FLOAT:
					value = NumberUtils.getRandomFloatValueInRange(start, end);
					break;
				case FILE:
				case DATE:
				case STRING:
				case DOUBLE:
					value = NumberUtils.getRandomDoubleValueInRange(start, end);
					break;
				}
			}else if (key.indexOf("(")>=0){ // choose from a group of values
				index = key.indexOf("(");
				key = key.substring(index+1);
				index = key.indexOf(")");
				key = key.substring(0,index);
				String[] values = key.split(";");
				index = (int)(Math.random() * values.length); 
				value = values[index];
			}else{
				throw new Exception("Random reference syntax is incorrect");
			}

			break;

		case INCREMENT:  // not implemented yet

			break;
		case ANT_PARAMETER:
			Properties props = FileUtils.loadPropertiesFromFile(CommonResources.ANT_INTERNAL_PROPERTIES_FILE);
			if (!props.containsKey(key)){ // a fix to allow using of first upper case in lower case too
				key = StringUtils.firstCharToUpper(key);
			}
			value = props.get(key);
			break;
		case ENVIRONMENT:
			value = System.getenv(key);
			break;
		}

		if (type == ParameterType.DATE){
			return DateUtils.parseDate(value.toString());
		}
		ListenerstManager.getInstance().report(reference+" Reference parameter \""+key+"\" = "+value);
		return value;
	}
	
	/**
	 * checks if a given value contains reference - for authentication
	 * 
	 * @param value	the value to check
	 * @return	True if the value contains a match to a reference option from the Reference ENUM
	 */
	public static boolean isReferenceValue(Object value){
		if (value == null){
			return false;
		}
		for (Reference ref : Reference.values()){
			if (value.toString().toLowerCase().contains(REFERENCE_STRING+ref.getRepresentation())){
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * go over all properties and replace all reference values with actual values
	 * 
	 * @param properties	the properties object to be modified
	 * @param	rt	the runnerTest to get parameters type from
	 */
	private static void replaceReferenceProperties(Properties properties,RunnerTest rt){
		if (properties == null){
			return;
		}
		Enumeration<Object> e = properties.keys();
		Object key = null;
		Object value = null;
		while (e.hasMoreElements()){
			try{
				key = e.nextElement();
				value = properties.get(key);
				String valueString = value.toString();
				valueString = replaceAllReferenceValues(valueString, rt.getParameterType(key.toString()));
				properties.setProperty(key.toString(),valueString);
			}catch (Exception e1) {
				reportExcption(e1, "tried to set value "+value+" to property "+key+" Reference= "+value);
				properties.setProperty(key.toString(),BAD_PARAMETER);
			}
		}
	}
	
	public static String replaceAllReferenceValues(String valueString, ParameterType type) throws Exception{
		while (isReferenceValue(valueString)){
			int startIndex = valueString.indexOf(REFERENCE_STRING);
			int endIndex = valueString.indexOf("}");
			if (endIndex>0){
				String before = valueString.substring(0,startIndex);
				String after = valueString.length()<endIndex+1? "" : valueString.substring(endIndex+1);
				// TODO: Since the value might be null, we might want to check it and send empty string instead
				String newValue = replaceReferenceWithValue(valueString.substring(startIndex,endIndex+1),type) + "";
				valueString = before + newValue + after;
			}
		}
		
		return valueString;
	}
	
	/**
	 * report an error while parsing a reference value
	 * @param e	the exception
	 * @param referenceString	the reference string that was being parsed
	 */
	public static void reportExcption(Exception e,String referenceString){
		String title = "Reference syntax is incorrect!  "+referenceString;
		String msg = StringUtils.getStackTrace(e);
		ListenerstManager.getInstance().report(title,msg,false);
	}
	
	/**
	 * checks if a given value is a scenario reference (used for adding scenario parameters)
	 * @param value	the value to check
	 * @return	true if the value format is (${scenario...})
	 */
	public static boolean isScenarioReference(Object value){
		if (value == null){
			return false;
		}
		String key = value.toString();
		return key.toLowerCase().contains(REFERENCE_STRING+Reference.SCENARIO.getRepresentation());
	}
	
	/**
	 * get the values of all referenced strings 
	 * 
	 * @param value
	 * @return
	 */
	public static String[] getReferenceKeys(Object value){
		String valueString = value.toString();
		ArrayList<String> keys = new ArrayList<String>();
		while (isScenarioReference(valueString)){
			String fullRef = REFERENCE_STRING+Reference.SCENARIO.getRepresentation();
			int startIndex = valueString.indexOf(fullRef);
			int endIndex = valueString.indexOf("}",startIndex);
			String referenceString = valueString.substring(startIndex+REFERENCE_STRING.length(),endIndex);
			int referenceEnd = referenceString.indexOf(":");
			String key = referenceString.substring(referenceEnd+1);
			keys.add(key);
			valueString = valueString.length()<endIndex+1? "" : valueString.substring(endIndex+1);
		}
		return keys.toArray(new String[0]);
	}
	
}
