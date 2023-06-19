/*
 * Created on Nov 15, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.extensions.handlers.UIEventHandler;
import jsystem.extensions.handlers.ValidationHandler;
import jsystem.extensions.report.html.HtmlCodeWriter;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.ParameterProperties;
import jsystem.framework.TestProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.RunnerListenersManager;
import jsystem.framework.scenario.Parameter.ParameterType;
import jsystem.framework.scripts.ScriptEngine;
import jsystem.framework.scripts.ScriptExecutor;
import jsystem.framework.scripts.ScriptsEngineManager;
import jsystem.runner.loader.LoadersManager;
import jsystem.utils.DateUtils;
import jsystem.utils.ObjectUtils;
import jsystem.utils.StringUtils;
import jsystem.utils.XmlUtils;
import junit.framework.SystemTest;
import junit.framework.Test;
import junit.framework.TestResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents a single test in the runner. <br>
 * The class holds all the data related to a runner test in the scenario. -
 * Properties = test properties read from xml file. Parameters = hashmap of the
 * test parameters. These parameters are fetched from an instance of the test
 * class using introspection. Documentation = user test documentation. read from
 * xml file under "user-Doc" attribute. updated through UI. Comment = user
 * comment. read from xml file. modified through UI right-click menu testJavadoc
 * = method javadoc - read from file.
 * 
 * 
 * RunnerTest life-cycle: in order to improve runner performance test parameters
 * are loaded in introspection only on demand (when user wants to se test
 * parameters in the runner, before running test in run mode 2/4, before saving
 * test in file system again)
 * 
 * Test data is divided to three types: Test parameters (saved in Parameters
 * map, fetched by introspecting test class and understanding it's setters and
 * getters) a special parameter is the fixture parameter. It is not visible to
 * the runner's user. it is for internal usage. Also the value of the fixture is
 * saved directly in the task of the test as sysproperty so it won't be possible
 * to override fixture name. Test description data: comment,meaningful name,
 * Documentation, javadoc,isDisabled in the RunnerTest class these are saved in
 * class members. javadoc and meaningful name are fetched by introspecting the
 * class. comment and documentation are fetched from ant xml. isDisabled - when
 * is disabled is true, jsystem task doesn't activate the test.
 * 
 * On adding new test to scenario a unique ID will be added. This ID will
 * uniquely identify the test. Changing the test properties will not change the
 * ID.
 * 
 * Test in ant XML: please see Scenario class documentation to understand how
 * test in saved in ant xml file. as described above, the data that is saved in
 * the ant script file is test parameters, user documentation and comment. this
 * data is saved as ant properties and passed to the jsystem ant task as
 * sysproperty.
 * 
 * test properties are saved in two places: 1. test target (default value for
 * test parameters) 2. in the scenario that invokes test's scenario (if there is
 * one)
 * 
 * Parameters Include/Exclude & sorting:<br>
 * -------------------------------------<br>
 * the user can choose which parameters are visible for which test.<br>
 * this means the chosen parameters will be visible on the parameters tab and
 * also on the HTML report this can be done by using the proper annotation in
 * the test's javadoc.<br>
 * - "@params.exclude" <parameters> - comma or space seperated, indicates using
 * all parameters in class apart of excluded ones.<br>
 * -"@params.include" <parameters> - comma or space seperated, indicates using
 * only these parameters.<br>
 * NOTE: if both annotations are present. the first one on the javadoc is
 * chosen.<br>
 * - sectionOrder = String array which specifies the ordering of parameters
 * sections.<br>
 * - sort = PresentationDefinitions member which holds all the definitions for
 * the sorting (ascending/descending , by Alph-betic order/type....)
 * 
 */
public class RunnerTest implements JTest, UIHandler {

	protected static Logger log = Logger.getLogger(RunnerTest.class.getName());

	public static final int STAT_NOT_RUN = 0;

	public static final int STAT_RUNNING = 1;

	public static final int STAT_FAIL = 2;

	public static final int STAT_ERROR = 3;

	public static final int STAT_SUCCESS = 4;

	public static final int STAT_WARNING = 5;

	protected static final int INCLUDE = 0;

	protected static final int EXCLUDE = 1;

	protected static final int NO_INCLUDE_OR_EXCLUDE = 2;

	public static final String INCLUDE_PARAMS_STRING = "params.include";

	public static final String EXCLUDE_PARAMS_STRING = "params.exclude";

	protected static String testJavadoc = "";

	protected String className;

	protected String methodName;

	String associatedFixtureClassName;

	private String[] sectionOrder = null;

	// kept in test instance to decide on parameters order when needed.
	private String[] paramsOrder;

	/*
	 * Holds parameters values as fetched from scenario's XML.
	 */
	protected Properties properties = new Properties();

	/*
	 * Map of Parameter which holds test parameters and default values as
	 * fetched from test class by introspection.
	 */
	protected HashMap<String, Parameter> parameters = null;

	protected Test test;

	int status = STAT_NOT_RUN;

	protected String testId = null;

	JTestContainer parent = null;

	private PresentationDefinitions sort = null;

	protected String codeMeaningfulName = null;

	protected String meaningfulName = null;

	protected boolean meaningfulNameShouldBeSaved = false;

	private String[] groups = null;

	protected String includeParamsStringAnnotation = null;

	protected String excludeParamsStringAnnotation = null;

	protected String significantParamsStringAnnotation = null;

	private boolean failureOccurred = false;

	protected String uuid = null;

	protected ArrayList<ValidationError> validationErrors = new ArrayList<ValidationError>();

	private static final Pattern SCENARIO_PARAMETERIZATION_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

	public RunnerTest(String className, String methodName) {
		this.className = className;
		this.methodName = methodName;
		uuid = getRandomUUID();
	}

	public RunnerTest cloneTest() throws Exception {
		RunnerTest test = new RunnerTest(className, methodName);
		// Clone the properties as well
		test.setProperties((Properties) getProperties().clone());
		return test;
	}

	/**
	 * Appends test + test settings to XML.
	 */
	public void addTestsXmlToRoot(Document doc, Integer[] indexes) {
		Element target = doc.createElement(RunningProperties.ANT_TARGET);
		target.setAttribute("name", testId);
		// add the properties with test parameters values
		addAntPropertiesToTarget(target, doc);

		/*
		 * Create and add the jsystem task tag and attributes
		 */
		Element jsystem = doc.createElement("jsystem");
		appendPropertiesToJSystemTask(jsystem, doc);
		target.appendChild(jsystem);

		// Add test to root as target
		Element root = doc.getDocumentElement();
		root.appendChild(target);
	}

	/**
	 * Appends test + test settings to XML.
	 */
	public Element addExecutorXml(Element targetScenario, Document parentDoc) {
		// adding antcall to the target from the execute scenario target
		Element antCall = parentDoc.createElement("antcallback");
		antCall.setAttribute(RunningProperties.ANT_TARGET, testId);

		// adding return of all relevant properties
		// TODO: return only output properties - split the list !
		filterProperties(properties);
		// TODO: we might want to change it
		// to use full id in the future - String keyPrefix = getFullTestId() +
		// "/";
		// Set the return attribute to get the properties back
		ArrayList<String> returnArray = new ArrayList<String>();
		String[] returnParameters = getReturnParameters();
		if (returnParameters != null) {
			for (String returnParameter : returnParameters) {
				returnArray.add(returnParameter);
			}
			if (!returnArray.isEmpty()) {
				String returnString = returnArray.toString();
				returnString = (String) returnString.subSequence(1, returnString.length() - 1);
				antCall.setAttribute("return", returnString);
			}
		}

		targetScenario.appendChild(antCall);

		return antCall;
	}

	/**
	 * Adds test properties to the target which calls
	 */
	public void addAntPropertiesToTarget(Element el, Document doc) {
		filterProperties(properties);
		Scenario r = (Scenario) getRoot();
		ScenarioHelpers.updateTestProperties(this, r.getName(), true);
		Properties p = new Properties();
		if (meaningfulNameShouldBeSaved) {
			p.setProperty(RunningProperties.MEANINGFUL_NAME_TAG, meaningfulName == null ? "" : meaningfulName);
		}
		p.setProperty(RunningProperties.UI_SETTINGS_TAG, sort == null ? "" : sort.toAntProperty());

		ScenarioHelpers.setTestInnerProperty(getFullUUID(), r.getName(), p, false);
	}

	private void appendPropertiesToJSystemTask(Element jsystemTask, Document parentDoc) {

		jsystemTask.setAttribute("showoutput", "true");
		Element testElement = parentDoc.createElement("test");
		testElement.setAttribute("name", className + "." + methodName);
		addPrivateTags(parentDoc, jsystemTask);

		// remove properties that we got from xml but
		// should be returned from class members
		filterProperties(properties);

		if (!StringUtils.isEmpty(associatedFixtureClassName)) {
			Element p = parentDoc.createElement("sysproperty");
			p.setAttribute("key", RunningProperties.PARAM_PREFIX + RunningProperties.FIXTURE_PROPERTY_NAME);
			p.setAttribute("value", associatedFixtureClassName);
			jsystemTask.appendChild(p);
		}
		Element p = parentDoc.createElement("sysproperty");
		p.setAttribute("key", RunningProperties.UUID_TAG);
		p.setAttribute("value", getUUID());
		jsystemTask.appendChild(p);

		p = parentDoc.createElement("sysproperty");
		p.setAttribute("key", RunningProperties.UUID_PARENT_TAG);
		p.setAttribute("value", "${" + RunningProperties.UUID_PARENT_TAG + "}.${" + RunningProperties.UUID_TAG + "}");
		jsystemTask.appendChild(p);

		p = parentDoc.createElement("sysproperty");
		p.setAttribute("key", RunningProperties.PARENT_NAME);
		p.setAttribute("value", "${" + RunningProperties.PARENT_NAME + "}.${ant.project.name}");
		jsystemTask.appendChild(p);

		jsystemTask.appendChild(testElement);
	}

	/**
	 * Creates a RunnerTest instance from ant XML.
	 * 
	 * callingTarget: the target which invokes the scenario which contains this
	 * test. Could be null if the scenario of the test is root scenario.
	 * testTarget: the target which invokes the jsystem task of this test
	 * 
	 */
	public static RunnerTest fromElement(Element callingTarget, Element testTarget, Test test, JTestContainer parent)
			throws Exception {

		Element testElement = XmlUtils.getElement("test", 0, testTarget);
		String tname = testElement.getAttribute("name");
		String tid = testTarget.getAttribute("name");

		String className = null;
		String methodName = null;
		if (tname != null) {
			int dotIndex = tname.lastIndexOf('.');
			className = tname.substring(0, dotIndex);
			methodName = tname.substring(dotIndex + 1);
		}
		if (methodName == null || className == null) {
			return null;
		}
		NodeList list = testTarget.getElementsByTagName("sysproperty");
		boolean scriptNode = false;
		boolean systemObjectOperationNode = false;
		String scriptTag = null;
		String scriptPath = null;
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (!(n instanceof Element)) {
				continue;
			}
			Element prop = (Element) n;
			if (RunningProperties.SCRIPT_TAG.equals(prop.getAttribute("key"))) {
				scriptNode = true;
				scriptTag = prop.getAttribute("value");
				break;
			} else if (RunningProperties.SYSTEM_OBJECT_OPERATION.equals(prop.getAttribute("key"))) {
				systemObjectOperationNode = true;
				break;
			}
		}
		if (scriptNode || systemObjectOperationNode) {
			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				if (!(n instanceof Element)) {
					continue;
				}
				Element prop = (Element) n;
				if (RunningProperties.SCRIPT_PATH.equals(prop.getAttribute("key"))) {
					scriptPath = prop.getAttribute("value");
					break;
				}
			}

		}
		RunnerTest rTest;
		if (scriptNode) {
			ScriptEngine engine = ScriptsEngineManager.getInstance().findExecutor(className);
			if (engine == null) {
				// the user will get an error indication but he will
				return null;
			}
			ScriptExecutor executor = engine.getExecutor(scriptTag);
			if (executor == null) {
				return null;
			}
			executor.configFilePath(scriptPath);
			rTest = new RunnerScript(executor);
		} else if (systemObjectOperationNode) {
			rTest = RunnerSOTest.initFromNodeList(list);
		} else {
			rTest = new RunnerTest(className, methodName);
		}
		rTest.setParent(parent);
		rTest.setTest(test);
		rTest.setTestId(tid);

		Element myTarget = XmlUtils.getElement("jsystem", 0, testTarget);
		rTest.setUUID(loadUuid(myTarget));

		boolean is49 = isScenarioParameterizationAntFile(testTarget);

		ArrayList<Element> propertiesToUpdateWith;

		// if we are working with a scenario that was created with TAS 4.9 then
		// the values should be fetched from test target or parent of parent
		// scenario target properties
		// otherwise it is fetched directly from the JSystem task
		if (is49) {
			// merge the properties for the parent calling scenario target with
			// the properties
			// of the test target.
			// give priority to parent scenario values.
			ArrayList<Element> testTargetProperties = XmlUtils.getElementsByTag("property", testTarget);
			if (callingTarget != null) {
				propertiesToUpdateWith = XmlUtils.getElementsByTag("property", callingTarget);
			} else {
				propertiesToUpdateWith = new ArrayList<Element>();
			}
			propertiesToUpdateWith = XmlUtils.mergeByAttrib(propertiesToUpdateWith, testTargetProperties, "name");
		} else {
			propertiesToUpdateWith = XmlUtils.getElementsByTag("sysproperty", testTarget);
		}

		setTestProperties(parent, tid, className, methodName, rTest, is49, propertiesToUpdateWith);
		return rTest;
	}

	/**
	 * load the Unique test Id from the XML system property<br>
	 * if no value is found, generate a new one otherwise
	 * 
	 * @param target
	 *            the test target element
	 * @return the unique id String
	 */
	private static String loadUuid(Element target) {
		ArrayList<Element> elements = XmlUtils.getChildElementsByTag("sysproperty", target);
		Element uuidProp = null;
		for (Element e : elements) {
			String key = e.getAttribute("key");
			if (key.equals(RunningProperties.UUID_TAG)) {
				uuidProp = e;
				break;
			}
		}
		if (uuidProp != null) {
			return uuidProp.getAttribute("value");
		}
		log.fine("RunnerTest did not have a UUID, Generating a new one");
		return getRandomUUID();
	}

	/**
	 * create a random Unique ID
	 * 
	 * @return
	 */
	public static String getRandomUUID() {
		return UUID.randomUUID().toString();
	}

	private static void setTestProperties(JTest parent, String tid, String className, String methodName,
			RunnerTest rTest, boolean is49, ArrayList<Element> propertiesToUpdateWith) throws Exception {
		Scenario root = (Scenario) rTest.getRoot();
		String fullUUID = rTest.getFullUUID();

		rTest.initTestProperties();
		Properties currentProperties = new Properties();
		String include = rTest.getIncludeParamsStringAnnotation();
		String exclude = rTest.getExcludeParamsStringAnnotation();

		if ((include == null) && (exclude == null)) {
			include = HtmlCodeWriter.getInstance().getMethodAnnotation(className, methodName, INCLUDE_PARAMS_STRING);
			exclude = HtmlCodeWriter.getInstance().getMethodAnnotation(className, methodName, EXCLUDE_PARAMS_STRING);
		}
		int annotation = compareIncludeAndExclude(include, exclude);
		String name;
		for (Element prop : propertiesToUpdateWith) {
			String key;

			if (is49) {
				key = prop.getAttribute("name");
			} else {
				key = prop.getAttribute("key");
			}
			String value = prop.getAttribute("value");

			if (StringUtils.isEmpty(key)) {
				continue;
			}

			String preFix = ScenarioHelpers.getFirstScenarioAncestor(parent).getName().trim() + "/" + tid + "/";

			// if it is a property of another test in the scenario
			if (!key.startsWith(preFix) && is49) {
				continue;
			}
			// trim scenario/testid prefix
			if (is49) {
				key = key.substring(preFix.length());
			} else {
				if (key.startsWith(RunningProperties.PARAM_PREFIX)) {
					key = key.substring(RunningProperties.PARAM_PREFIX.length());
				}
			}
			if (key.equals(RunningProperties.DOCUMENTATION_TAG)) {
				rTest.setDocumentation(value);
				continue;
			}
			if (key.equals(RunningProperties.COMMENT_TAG)) {
				rTest.setTestComment(value);
				continue;
			}
			if (key.equals(RunningProperties.UI_SETTINGS_TAG)) {
				rTest.sort = PresentationDefinitions.fromAntProperty(value);
				continue;
			}

			if (key.equals(RunningProperties.FIXTURE_PROPERTY_NAME)) {
				rTest.setFixture(value);
				continue;
			}
			if (key.equals(RunningProperties.PARENT_NAME)) {
				continue;
			}

			name = lowerFirstLetter(key);

			if (annotation == NO_INCLUDE_OR_EXCLUDE
					|| (annotation == INCLUDE && checkParamsIncludeExclude(include, name))
					|| (annotation == EXCLUDE && (!checkParamsIncludeExclude(exclude, name)))) {
				currentProperties.put(key, value);
			}
		}

		String orderStr = ScenarioHelpers.getTestProperty(fullUUID, root.getName(), RunningProperties.UI_SETTINGS_TAG);
		if (orderStr != null) {
			rTest.sort = PresentationDefinitions.fromAntProperty(orderStr);
		}

		String fixture = ScenarioHelpers.getTestProperty(fullUUID, root.getName(),
				RunningProperties.FIXTURE_PROPERTY_NAME);
		if (fixture != null) {
			rTest.setFixture(fixture);
		}

		rTest.setParent((JTestContainer) parent);

		Properties fromAllScenarios = null;
		if (JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.CACHE_SCENARIO_PROPERTIES)
				.equals("true")) {
			fromAllScenarios = ScenarioHelpers.getAllTestPropertiesUpTo(rTest, null, false);
		} else {
			fromAllScenarios = ScenarioHelpers.getAllTestPropertiesFromAllScenarios(rTest);
		}
		currentProperties.putAll(fromAllScenarios);
		ScenarioHelpers.loadTestProperties(rTest, currentProperties);
		rTest.setProperties(currentProperties);
		rTest.loadMeaningfulName(true);
	}

	private void loadMeaningfulName(boolean include) {
		Scenario upto = include ? null : getRoot();
		String meaningful = null;
		if (JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.CACHE_SCENARIO_PROPERTIES)
				.equals("true")) {
			meaningful = ScenarioHelpers.getAllTestPropertiesUpTo(this, upto, false).getProperty(
					RunningProperties.MEANINGFUL_NAME_TAG);
		} else {
			meaningful = ScenarioHelpers.getAllTestPropertiesUpTo(this, upto, true).getProperty(
					RunningProperties.MEANINGFUL_NAME_TAG);
		}
		this.meaningfulName = meaningful;
	}

	/**
	 * returns true if this is a scenario which includes scenario
	 * parameterization (was created with TAS 4.9). it checks whether the value
	 * of the sysproperty value exists in test target property. If so, this
	 * means this is a scenario file created with 4.9
	 */
	private static boolean isScenarioParameterizationAntFile(Element testTarget) throws Exception {
		ArrayList<Element> list = XmlUtils.getElementsByTag("sysproperty", testTarget);
		Map<String, Element> map = XmlUtils.mapFromArrayListByAttribute(
				XmlUtils.getElementsByTag("property", testTarget), "name");

		if (list.size() == 0) {
			return true;
		}
		// skip fixture since it gets it's value directly and not from
		// an ant property
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getAttribute("key")
					.equals(RunningProperties.PARAM_PREFIX + RunningProperties.FIXTURE_PROPERTY_NAME)
					|| list.get(i).getAttribute("key").equals(RunningProperties.UUID_TAG)
					|| list.get(i).getAttribute("key").equals(RunningProperties.UUID_PARENT_TAG)) {
				continue;
			}
			String value = list.get(i).getAttribute("value");
			Matcher m = SCENARIO_PARAMETERIZATION_PATTERN.matcher(value);
			if (m.find()) {
				String key = m.group(1);
				if (map.get(key) != null) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * converts the test properties to a String format<br>
	 * if there are references they will be replaced first
	 * 
	 * @return a String of parameters , space separated
	 */
	public String getPropertiesAsString() {
		Properties clone = (Properties) properties.clone();
		clone.remove("disable");
		Properties fields = getAllInnerFields();
		Enumeration<Object> keys = fields.keys();
		while (keys.hasMoreElements()) {
			clone.remove(keys.nextElement());
		}
		try {
			return ParameterUtils.propertiesToString(clone);
		} catch (Exception e) {
			log.warning("Failed converting test parameters to string " + e.getMessage());
			return "";
		}
	}

	/**
	 * get this test Test. If test is null returns a <code>TestLoadError</code>
	 * object which is shown to user
	 */
	public Test getTest() {
		if (test == null) {
			TestLoadError testLoadError = new TestLoadError();
			testLoadError.setClassName(className);
			testLoadError.setName("testError");
			return testLoadError;
		}
		((SystemTest) test).setFullUUID(getFullUUID());
		return test;
	}

	/**
	 * Loads test class and, and initializes test's meaningful name.
	 */
	public void load() throws Exception {
		loadTestClass();
		initTestProperties();
	}

	public void loadParametersAndValues() {
		getTestParameters();
		setParameters();
		updateProperties();
	}

	/**
	 * find a parameter type by it's name
	 * 
	 * @param name
	 * @return
	 */
	public ParameterType getParameterType(String name) {
		if (parameters == null || parameters.size() == 0) {
			loadParameters();
		}
		Object[] arrayObjects = parameters.values().toArray();
		for (int index = 0; index < arrayObjects.length; index++) {
			Parameter parameter = (Parameter) arrayObjects[index];
			if (parameter.getName().equals(name)) {
				return parameter.getType();
			}
		}
		return null;
	}

	private void updateProperties() {
		for (Parameter parameter : parameters.values()) {
			if (parameter.getValue() == null) {
				continue;
			}
			properties.setProperty(parameter.getName(), parameter.getStringValue());
		}
	}

	public void loadParametersAndValuesAndUpdateTestClass() {
		loadParametersAndValues();
		setTestClassParameters();
	}

	/**
	 * Invoke the test case handleUiEvent method.
	 * 
	 * @return True if the testCase is from type handleUiEvent and the
	 *         invocation succeeded.
	 */
	public boolean handleUIEvent(Parameter[] params) {
		HashMap<String, Parameter> map = new HashMap<String, Parameter>();
		for (Parameter currentParameter : params) {
			if (currentParameter.isAsOptions()) {
				updateOptions(null, currentParameter);
			}
			map.put(currentParameter.getName(), currentParameter);
		}
		try {
			if (getTest() instanceof UIEventHandler) {
				((UIEventHandler) getTest()).handleUIEvent(map, getMethodName());
				return true;
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed executing handle ui event on class: " + getTest().getClass(), e);
		}
		return false;
	}

	public ValidationError[] validate(Parameter[] params) {
		HashMap<String, Parameter> map = new HashMap<String, Parameter>();
		for (Parameter currentParameter : params) {
			map.put(currentParameter.getName(), currentParameter);
		}
		try {
			if (getTest() instanceof ValidationHandler) {
				return ((ValidationHandler) getTest()).validate(map, getMethodName());
			} else {
				log.fine("validate was not found");
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed executing validate method", e);
		}
		return null;
	}

	private void loadTestClass() throws Exception {
		if (test != null) {
			return;
		}
		ClassLoader loader = LoadersManager.getInstance().getLoader();
		Class<?> testClass;
		Object o;
		testClass = loader.loadClass(className);

		if (Test.class.isAssignableFrom(testClass)) {
			// JUnit 3 style test
			Constructor<?> constract;
			try {
				constract = testClass.getConstructor(new Class[0]);
				o = constract.newInstance(new Object[0]);
				if (o instanceof SystemTest) {
					((SystemTest) o).setName(methodName);
				}
			} catch (Exception ex) {
				constract = testClass.getConstructor(new Class[] { String.class });
				o = constract.newInstance(new Object[] { methodName });
			}
			if (o instanceof SystemTest && parent != null) {
				((SystemTest) o).setFullUUID(getFullUUID());
			}
			test = (Test) o;
		}
	}

	/**
	 */
	public void initTestProperties(boolean withParameters) throws Exception {
		loadTestClass();
		TestProperties testProperties = getTestProperties();
		if (testProperties != null) {
			// TestProperties tp = m.getAnnotation(TestProperties.class);
			codeMeaningfulName = StringUtils.isEmpty(testProperties.name()) ? null : testProperties.name();
			includeParamsStringAnnotation = null;
			if (!checkIsIgnore(testProperties.paramsInclude())) {
				StringBuilder sb = new StringBuilder("");
				for (String param : testProperties.paramsInclude()) {
					sb.append(param).append(" ");
				}
				includeParamsStringAnnotation = sb.toString();
			}
			groups = testProperties.group();
			if (groups != null && groups.length == 0) {
				groups = null;
			}

			excludeParamsStringAnnotation = null;
			if (!checkIsIgnore(testProperties.paramsExclude())) {
				StringBuilder sb = new StringBuilder("");
				for (String param : testProperties.paramsExclude()) {
					sb.append(param).append(" ");
				}
				excludeParamsStringAnnotation = sb.toString();
			}

			significantParamsStringAnnotation = null;
			if (!checkIsIgnore(testProperties.mandatoryFields())) {
				StringBuffer stringBuffer = new StringBuffer("");
				for (String param : testProperties.mandatoryFields()) {
					stringBuffer.append(param).append(" ");
				}
				significantParamsStringAnnotation = stringBuffer.toString();
			}

			if (withParameters) {
				codeMeaningfulName = processTestName(codeMeaningfulName);
			}

			if (StringUtils.isEmpty(codeMeaningfulName)) {
				codeMeaningfulName = null;
			}
		}
	}

	private TestProperties getTestProperties() {
		try {
			Method method = test.getClass().getMethod(methodName, new Class[0]);
			if (method.isAnnotationPresent(TestProperties.class)) {
				return method.getAnnotation(TestProperties.class);
			}
			return null;
		} catch (Exception e) {
			return null;
		}

	}

	protected String processTestName(String originalName) {
		if (originalName != null && !originalName.equals("")) {
			ArrayList<String> listOfParams = getMeaningfulNameParameters(originalName);
			Map<String, String> map = getValueForParameters(listOfParams);
			for (String key : map.keySet()) {
				String value = map.get(key);
				String keyFirstCharUpperCase = StringUtils.firstCharToUpper(key);
				originalName = StringUtils.replace(originalName, "${" + key + "}", value);
				originalName = StringUtils.replace(originalName, "${" + keyFirstCharUpperCase + "}", value);
			}
		}
		return originalName;
	}

	/**
	 * check if the params include\exclude has no value
	 * 
	 * @param toCheck
	 *            the String array from the annotation
	 * @return False if no value was set
	 */
	private boolean checkIsIgnore(String[] toCheck) {
		return (toCheck.length == 1 && TestProperties.NOT_DEFINED.equals(toCheck[0]));
	}

	/**
	 * Returns tests return parameter. Return parameter is a parameter that is
	 * returned by the test. Return parameters are regular test parameters, at
	 * the end of test execution, the platform fetches parameter's value and
	 * creates an ant property with parameter name and value. If parameter
	 * already exists, it is overridden. To define a parameter to be a return
	 * parameter, use the {@link TestProperties#returnParam()} annotation.
	 */
	public String[] getReturnParameters() {
		Method method;
		try {
			method = test.getClass().getMethod(methodName, new Class[0]);
		} catch (Exception e) {
			return new String[0];
		}
		if (method.isAnnotationPresent(TestProperties.class)) {
			TestProperties testProperties = method.getAnnotation(TestProperties.class);
			// Change first letter to uppercase
			// Currently parameters are shown only with first upper case
			// even if they are lower case in the code.
			// Note - thisParam and ThisParam can't exist together
			String[] params = testProperties.returnParam();
			ArrayList<String> paramsToReturn = new ArrayList<String>();
			for (int i = 0; i < params.length; i++) {
				params[i] = StringUtils.firstCharToUpper(params[i]);
				paramsToReturn.add(params[i]);
			}
			return paramsToReturn.toArray(new String[0]);
		}
		return null;
	}

	private ArrayList<String> getMeaningfulNameParameters(String name) {
		ArrayList<String> result = new ArrayList<String>();
		Matcher matcher = ParametersManager.PARAMETER_PATTERN.matcher(name);
		while (matcher.find()) {
			result.add(StringUtils.firstCharToLower(matcher.group(1)));
		}
		return result;
	}

	private Map<String, String> getValueForParameters(ArrayList<String> parameters) {
		HashMap<String, String> map = new HashMap<String, String>();
		for (String key : parameters) {
			map.put(key, getValueForParameter(key));
		}
		return map;
	}

	protected String getValueForParameter(String parameter) {
		parameter = StringUtils.firstCharToUpper(parameter);
		if (properties.get(parameter) != null) {
			return properties.getProperty(StringUtils.firstCharToUpper(parameter));
		}
		Method getter = null;
		try {
			getter = test.getClass().getMethod("get" + parameter, new Class[0]);
		} catch (Throwable t) {
		}
		if (getter == null) {
			try {
				getter = test.getClass().getMethod("is" + parameter, new Class[0]);
			} catch (Throwable t) {
			}
		}
		if (getter == null) {
			return "${" + parameter + "}";
		}
		try {
			return getter.invoke(test, new Object[0]).toString();
		} catch (Throwable t) {
			return "${" + parameter + "}";
		}
	}

	/**
	 * Return a parameters that should be visible by user Currently fixture name
	 * is the only parameter which should not be visible.
	 */
	public Parameter[] getVisibleParamters() {
		return getVisibleParamters(false);
	}

	/**
	 * Return a parameters that should be visible by user Currently fixture name
	 * is the only parameter which should not be visible.
	 */
	public Parameter[] getVisibleParamters(boolean recursively) {
		if (parameters == null) {
			loadParametersAndValues();
		}
		Parameter fn = getTestParameters().get(RunningProperties.FIXTURE_PROPERTY_NAME);

		if (fn == null) {
			return getParameters();
		}
		try {
			parameters.remove(RunningProperties.FIXTURE_PROPERTY_NAME);
			Parameter[] par = getParameters();
			Parameter[] parToReturn = new Parameter[par.length];
			System.arraycopy(par, 0, parToReturn, 0, par.length);
			return parToReturn;
		} finally {
			parameters.put(fn.getName(), fn);
		}
	}

	/**
	 */
	public void initTestProperties() {
		try {
			initTestProperties(true);
		} catch (Throwable e) {
			log.fine("Problem initiating TestProperties annotation for test " + getMethodName());
		}
	}

	/**
	 * checks if an include/exclude is to be done. assumes the testJavadoc was
	 * already initialized
	 * 
	 * @param include
	 *            the include string array
	 * @param exclude
	 *            the exclude string array
	 * @return NO_INCLUDE_OR_EXCLUDE / INCLUDE / EXCLUDE
	 */
	protected static int compareIncludeAndExclude(String include, String exclude) {
		if (include != null) {
			return INCLUDE;
		}
		if (exclude != null) {
			return EXCLUDE;
		}
		return NO_INCLUDE_OR_EXCLUDE;
	}

	/**
	 * lower case the first letter of a string - for set methods
	 * 
	 * @param name
	 *            the parameter with the upper case name
	 * @return the parameter with the first letter lower-cased
	 */
	protected static String lowerFirstLetter(String name) {
		return StringUtils.firstCharToLower(name);
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
			log.warning("test class could not be loaded. class=" + getClassName() + " method=" + getMethodName());
			return;
		}
		String include = includeParamsStringAnnotation;
		String exclude = excludeParamsStringAnnotation;

		String important = significantParamsStringAnnotation;

		if (include == null && exclude == null) {
			exclude = HtmlCodeWriter.getInstance().getMethodAnnotation(className, methodName, EXCLUDE_PARAMS_STRING);
			include = HtmlCodeWriter.getInstance().getMethodAnnotation(className, methodName, INCLUDE_PARAMS_STRING);
		}

		if (important == null) {
			important = "";
		}

		int annotation = compareIncludeAndExclude(include, exclude);

		for (Method method : test.getClass().getMethods()) {
			String methodName = method.getName();
			if (methodName.equals("setUp") || methodName.equals("setFixture") || methodName.equals("setFixtureName")
					|| methodName.equals("setTearDownFixture") || methodName.equals("setName")
					|| methodName.equals("setTestDocumentation") || methodName.equals("setPass")
					|| methodName.equals("setTestInfo") || methodName.equals("setTestId")
					|| methodName.equals("setParentFixture") || methodName.equals("setFullUUID")) {

				continue;
			}
			/**
			 * Go over all the set methods
			 */
			if (methodName.toLowerCase().startsWith("set") && methodName.length() > 3) {
				Class<?>[] parametersType = method.getParameterTypes();
				/**
				 * work only with single parameter setter
				 */
				if (parametersType.length != 1) {
					log.log(Level.FINE, "Number of setter parameter is not 1 for: " + methodName);
					continue;
				}
				Class<?> type = parametersType[0];
				String paramName = methodName.substring(3);
				Parameter currentParameter = new Parameter();
				currentParameter.setName(paramName);
				currentParameter.setSetMethod(method);
				/**
				 * Init the getter method if exits
				 */
				Method getter = null;
				Object defaultValue = null;
				try {
					try {
						getter = test.getClass().getMethod("get" + paramName, new Class[0]);
					} catch (Throwable tt) {
						// ignored
					}
					if (getter == null) {
						try {
							getter = test.getClass().getMethod("is" + paramName, new Class[0]);
						} catch (Throwable tt) {
							// ignored
						}

					}
					/**
					 * If getter return type not match set the getter to null
					 */
					if (getter != null) {
						if (!getter.getReturnType().equals(type)) {
							getter = null;
							log.warning("Getter return type not match for: " + methodName);
						} else {
							currentParameter.setGetMethod(getter);
							try {
								defaultValue = getter.invoke(test, new Object[0]);
							} catch (Exception e) {
								log.warning("Fail to invoke method: " + methodName);
							}
						}
					} else {
						log.warning("Parameter " + paramName + " doesn't have a getter");
					}
				} catch (Throwable throwable) {
					// ignored
				}

				currentParameter.setMandatory(important.toLowerCase().indexOf(paramName.toLowerCase()) >= 0);

				updateOptions(type, currentParameter);

				// first check if this method uses provider
				UseProvider useProvider = method.getAnnotation(UseProvider.class);
				if (useProvider != null) { // will use provider
					ParameterProvider provider;
					try {
						String[] args = useProvider.config();
						provider = (ParameterProvider) LoadersManager.getInstance().getLoader()
								.loadClass(useProvider.provider().getName()).newInstance();
						provider.setProviderConfig(args);
					} catch (Exception e) {
						log.log(Level.WARNING, "Fail to create new instance of provider", e);
						continue;
					}
					currentParameter.setType(ParameterType.USER_DEFINED);
					currentParameter.setProvider(provider);
					currentParameter.setParamClass(type);
				} else {
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
						if (defaultValue != null) {
							defaultValue = ((Enum<?>) defaultValue).toString();
						}
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
						log.fine("Unknown parameter type: " + type.getName() + " for: " + paramName);
						continue;
					}
				}

				/*
				 * init the value and the default value
				 */
				currentParameter.setDefaultValue(defaultValue);
				currentParameter.setValue(defaultValue);
				try {
					String doc = null;
					Class<?> cls = test.getClass();

					while (true) {
						doc = HtmlCodeWriter.getInstance().getMethodJavaDoc(cls.getName(), methodName);
						if (doc != null) {
							break;
						}
						cls = cls.getSuperclass();
						if (cls == null) {
							doc = "";
							break;
						}
					}
					currentParameter.setDescription(doc);
					if (method.isAnnotationPresent(ParameterProperties.class)) {
						ParameterProperties pp = method.getAnnotation(ParameterProperties.class);
						if (!StringUtils.isEmpty(pp.description())) {
							currentParameter.setDescription(pp.description());
						}
						if (!StringUtils.isEmpty(pp.section())) {
							currentParameter.setSection(pp.section());
						}
					}
				} catch (Exception e) {
					log.log(Level.WARNING, "Fail to get Doc of Param " + currentParameter.getName());
				}

				String name = lowerFirstLetter(currentParameter.getName());

				if (annotation == NO_INCLUDE_OR_EXCLUDE) {
					parameters.put(currentParameter.getName(), currentParameter);
				} else if (annotation == INCLUDE) {
					if (checkParamsIncludeExclude(include, name)) {
						parameters.put(currentParameter.getName(), currentParameter);
					}
				} else if ((!checkParamsIncludeExclude(exclude, name))) {
					parameters.put(currentParameter.getName(), currentParameter);
				}

			} else if (methodName.equals("sectionOrder")) {
				try {
					Method order = null;
					order = test.getClass().getMethod("sectionOrder", new Class[0]);
					if (order != null) {
						if (order.getReturnType().equals(Array.newInstance(String.class, 0).getClass())) {
							String[] orderString = (String[]) order.invoke(test, new Object[0]);
							setSectionOrder(orderString);
						}
					}
				} catch (Exception e) {
					log.log(Level.WARNING, "Fail to get method sectionOrder() from class " + className);
				}
			}
		}

		updateInternalJsystemFlags();

		updateParamsOrder(include);
	}

	protected void updateInternalJsystemFlags() {
		/**
		 * All internal JSystem flags and its default values
		 */
		String[][] keysAndValues = new String[][] { { RunningProperties.HIDDEN_IN_HTML, "false" },
				{ RunningProperties.IS_DISABLED, "false" },
				{ RunningProperties.MARKED_AS_KNOWN_ISSUE, "false" },
				// APPLIED - default value assigned to edit local only
				{ RunningProperties.EDIT_ONLY_LOCALLY, "false" },
				{ RunningProperties.MARKED_AS_NEGATIVE_TEST, "false" }, { RunningProperties.DOCUMENTATION_TAG, "" },
				{ RunningProperties.COMMENT_TAG, "" }, };

		for (String[] keyAndValue : keysAndValues) {
			Parameter p = new Parameter();
			p.setParamClass(String.class);
			p.setType(ParameterType.JSYSTEM_INTERNAL_FLAG);
			p.setName(keyAndValue[0]);
			p.setValue(keyAndValue[1]);
			p.setDefaultValue(keyAndValue[1]);
			p.setVisible(false);
			parameters.put(keyAndValue[0], p);
		}
	}

	private void updateOptions(Class<?> type, Parameter param) {
		try {
			Method options = null;
			options = test.getClass().getMethod("get" + param.getName() + "Options", new Class[0]);
			if (options != null) {
				if (type == null || options.getReturnType().equals(Array.newInstance(type, 0).getClass())
						|| (type.equals(options.getReturnType()) && Object[].class.isAssignableFrom(type))) {
					Object optionsArray = options.invoke(test, new Object[0]);
					if (optionsArray != null) { // there are values
						param.setOptions(optionsArray);
						param.setAsOptions(true);
					}
				}
			}
		} catch (Throwable throwable) {
			// ignored
		}
	}

	protected static boolean checkParamsIncludeExclude(String paramsInclude, String parameter) {
		String[] paramsIncludeArray = StringUtils.split(paramsInclude, " ;,\n\r\t");
		Set<String> mySet = new HashSet<String>();
		for (String s : paramsIncludeArray) {
			mySet.add(lowerFirstLetter(s));
		}
		boolean result = mySet.contains(lowerFirstLetter(parameter));
		return result;
	}

	/**
	 * tries to set a parameters value located the parameter in the hashmap and
	 * changes the value.
	 */
	public void setParameterIfExists(Parameter param) {
		Object[] arrayObject = parameters.values().toArray();
		for (int i = 0; i < arrayObject.length; i++) {
			Parameter currentParameter = (Parameter) arrayObject[i];
			if (currentParameter.getName().equals(param.getName()) && (currentParameter.getType() == param.getType())) {
				currentParameter.setValue(param.getValue());
				return;
			}
		}
	}

	/**
	 * Activates class setters and sets the values of test's members according
	 * to the <code>parameters</code> map.
	 */
	@SuppressWarnings("unchecked")
	public void setTestClassParameters() {
		HashMap<String, Parameter> clone = new HashMap<String, Parameter>(parameters);
		Object[] arrayObjects = clone.values().toArray();
		for (int i = 0; i < arrayObjects.length; i++) {
			Parameter currentParameter = (Parameter) arrayObjects[i];
			// skip internal JSystem flags
			if (currentParameter.getType().equals(ParameterType.JSYSTEM_INTERNAL_FLAG)) {
				continue;
			}
			Method setter = currentParameter.getSetMethod();

			Object ob = currentParameter.getValue();
			if (ob == null || currentParameter.isBadRefernceParameter()) {
				continue;
			}

			Class<?>[] types = setter.getParameterTypes();
			try {
				if (types != null && types.length > 0 && types[0].isEnum()) { // if
					// enum
					Class type = types[0];
					/*
					 * Convert the string value into Enum
					 */
					ob = currentParameter.getEnumValueAsName();
					setter.invoke(test, new Object[] { Enum.valueOf(type, ob.toString()) });
				} else if (types != null && types.length > 0 && File.class.isAssignableFrom(types[0])) { // if
					File f = ParameterFileUtils.convertBeforeTestUpdate(ob.toString());
					setter.invoke(test, new Object[] { f });
				} else if (types != null && types.length > 0 && Date.class.isAssignableFrom(types[0])) { // if
					setter.invoke(test, new Object[] { DateUtils.parseDate(ob.toString()) });
				} else if (types != null && types.length > 0 && String[].class.isAssignableFrom(types[0])) {// in
																											// case
																											// of
					// 'string
					// array'
					if (ob instanceof String) {
						ob = StringUtils.split((String) ob, ";");
					}
					setter.invoke(test, new Object[] { ob });
				} else { // not an enum
					setter.invoke(test, new Object[] { ob });
				}

			} catch (Exception e) {
				// Fixes issue #232. When parameter from type enum is using a
				// value that is longer part of the enum, the value becomes null
				// without proper message
				ListenerstManager.getInstance().report(
						"Failed to set value to parameter with name '" + currentParameter.getName() + "'", 2);
				log.log(Level.WARNING, "Failed to set value to parameter with name '" + currentParameter.getName()
						+ "'", e);

			}
		}
	}

	/**
	 * Get an array of parameters update the original parameters hash only if
	 * the parameter exist and changed TODO: same mechanism is correct for
	 * AntFlowcontrol, so it should be the same code
	 */
	public void setParameters(Parameter[] params) {
		setParameters(params, false);
	}

	/**
	 * Get an array of parameters update the original parameters hash only if
	 * the parameter exist and changed In case that a a parameter value is
	 * cleared in the Runners GUI, the parameter will be taken out of the
	 * scenario property file. TODO: same mechanism is correct for
	 * AntFlowcontrol, so it should be the same code
	 */
	public void setParameters(Parameter[] params, boolean recursively) {
		for (Parameter givenParameter : params) {
			if (!givenParameter.isDirty()) {
				continue;
			}
			Parameter originalParameter = parameters.get(givenParameter.getName());
			if (originalParameter == null) {
				continue;
			}
			Object value = givenParameter.getValue();
			Object value1 = originalParameter.getValue();
			if (ObjectUtils.nullSafeEquals(value, value1)) {
				continue;
			}
			boolean isMandatory = givenParameter.isMandatory();

			RunnerListenersManager.getInstance().testParametersChanged(getFullUUID(),
					new Parameter[] { originalParameter }, new Parameter[] { givenParameter });

			originalParameter.setValue(value);
			originalParameter.signalToSave();
			originalParameter.setMandatory(isMandatory);
			String stringValue = originalParameter.getStringValue();

			if (stringValue == null) {
				properties.remove(originalParameter.getName());
				try {
					ScenarioHelpers.removePropertiesFromScenarioProps(getRoot().getName(), getFullUUID() + "."
							+ givenParameter.getName(), true);
				} catch (Exception e) {
					log.severe("Problem removing a null parameter from Scenario properties file");
				}
			} else {
				properties.put(originalParameter.getName(), stringValue);
			}
		}
		String originalName = meaningfulName;
		if (originalName == null) {
			TestProperties tp = getTestProperties();
			if (tp != null) {
				originalName = StringUtils.isEmpty(tp.name()) ? null : tp.name();
			}
		}
		meaningfulName = processTestName(originalName);
	}

	private void updateTestInnerFlag(Parameter parameter, String parameterValue) {
		parameter.setValue(parameterValue);
		properties.put(parameter.getName(), parameterValue);
	}

	/**
	 * Sets parameters value according to values which are found in supplied
	 * properties
	 */
	private void setParameters() {
		if (parameters == null) {
			log.warning("Try to load properties when the parameters are not init");
			return;
		}
		Enumeration<Object> enum1 = properties.keys();
		while (enum1.hasMoreElements()) {
			String key = (String) enum1.nextElement();
			Parameter param = (Parameter) parameters.get(key);
			if (param != null) {
				String value = properties.getProperty(key);
				boolean badParameter = value.equals(ParametersManager.BAD_PARAMETER);
				param.setBadRefernceParameter(badParameter);
				if (badParameter) {
					continue;
				}
				try {
					param.setValue(value);
				} catch (Exception t) {
					ParametersManager.reportExcption(t,
							"tried to set value " + value + " to parameter " + param.getName());
				}
			}
		}
	}

	private HashMap<String, Parameter> getTestParameters() {
		if (parameters == null || parameters.size() == 0) {
			loadParameters();
		}
		return parameters;
	}

	/**
	 * Returns a <code>Property</code> instance with all test's parameters in
	 * ant canonical format.
	 * 
	 * This method is part of the scenario parameterization implementation. When
	 * running in run.mode 2 or 4, the ant engine is activated with test's
	 * direct scenario as ant script file and with the test (or several tests
	 * when running in run.mode 4 ) as the targets. Since test's parameters are
	 * saved in the parent scenario of the test's scenario (if there is one) ,
	 * they are lost. To solve this problem, when running in run mode 2,4 tests
	 * parameters are not passed through the ant file but through a property
	 * file. This method prepares the properties for the property file.
	 * 
	 */
	public Properties getPropertiesInAntCanonicalFormat() {
		filterProperties(properties);
		Properties toReturn = new Properties();
		Enumeration<Object> enum1 = properties.keys();

		String keyPrefix = getFullTestId() + "/";

		while (enum1.hasMoreElements()) {
			String key = (String) enum1.nextElement();
			toReturn.put(keyPrefix + key, properties.get(key));
		}

		String key = keyPrefix + RunningProperties.IS_DISABLED;
		toReturn.put(key, Boolean.toString(isDisable()));
		return toReturn;
	}

	/**
	 */
	public Properties getAllXmlFields() {
		Properties fields = new Properties();
		String com = getComment();
		if (com == null) {
			com = "";
		}
		fields.setProperty(commentString, com);
		String doc = getDocumentation();
		if (doc == null) {
			doc = "";
		}
		fields.setProperty(userDocString, doc);
		return fields;
	}

	/**
	 * Added to support removal of inner fields that should be removed from test
	 * properties
	 * 
	 * @return
	 */
	public Properties getAllInnerFields() {
		Properties base = getAllXmlFields();
		base.setProperty(RunningProperties.UUID_PARENT_TAG, "");
		base.setProperty(RunningProperties.UUID_TAG, "");
		base.setProperty(RunningProperties.IS_DISABLED, "");
		base.setProperty(RunningProperties.UI_SETTINGS_TAG, "");
		base.setProperty(RunningProperties.HIDDEN_IN_HTML, "");

		// APPLIED - Don't know exactly the purpose of this call
		base.setProperty(RunningProperties.EDIT_ONLY_LOCALLY, "");

		base.setProperty(RunningProperties.DOCUMENTATION_TAG, "");
		base.setProperty(RunningProperties.MARKED_AS_KNOWN_ISSUE, "");
		base.setProperty(RunningProperties.MARKED_AS_NEGATIVE_TEST, "");
		base.setProperty(RunningProperties.COMMENT_TAG, "");
		return base;
	}

	/**
	 */
	public void setXmlFields(Properties fields) {
		setTestComment((String) fields.getProperty(commentString));
		setDocumentation((String) fields.getProperty(userDocString));
	}

	/**
	 * set this test Test
	 * 
	 * @param test
	 *            the Test instance
	 */
	public void setTest(Test test) {
		this.test = test;
	}

	/**
	 * remove the Test reference sets this Test to null
	 */
	public void removeTestReferance() {
		setTest(null);
	}

	public String getClassName() {
		return className;
	}

	/**
	 * set this test class name
	 * 
	 * @param className
	 *            the string with the class name
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * get the test method name
	 * 
	 * @return method name
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * set the method name for this test
	 * 
	 * @param methodName
	 *            string od the method name
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * get the test properties
	 * 
	 * @return properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * set the test properties
	 * 
	 * @param properties
	 *            properties for the test
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Parameter[] getParameters() {
		if (parameters == null) {
			return new Parameter[0];
		}
		Parameter[] parameterArray;
		if (paramsOrder != null) {
			HashMap<String, Parameter> toSort = new HashMap<String, Parameter>(parameters);
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterArray = new Parameter[toSort.size()];

			for (int i = 0; i < paramsOrder.length; i++) {
				Parameter currentParameter = toSort.remove(StringUtils.firstCharToUpper(paramsOrder[i]));
				if (currentParameter != null) {
					parameterList.add(currentParameter);
				}
			}
			for (Parameter currentParameter : toSort.values()) {
				parameterList.add(currentParameter);
			}
			parameterArray = parameterList.toArray(new Parameter[0]);
		} else {
			parameterArray = parameters.values().toArray(new Parameter[0]);
		}
		return parameterArray;
	}

	/**
	 * get the test's status
	 * 
	 * @return STAT_NOT_RUN / STAT_RUNNING / STAT_FAIL / STAT_ERROR /
	 *         STAT_SUCCESS / STAT_WARNING
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * sets this test status
	 * 
	 * @param status
	 *            STAT_NOT_RUN / STAT_RUNNING / STAT_FAIL / STAT_ERROR /
	 *            STAT_SUCCESS / STAT_WARNING
	 */
	public void setStatus(int status) {
		if (isMarkedAsKnownIssue() && (status == STAT_ERROR || status == STAT_FAIL)) {
			status = STAT_WARNING;
		}
		this.status = status;
	}

	public void initFlags() {
		if (test instanceof SystemTest) {
			((SystemTest) test).initFlags();
		}
	}

	public boolean isDisable() {
		if ((parameters == null) || (getTestParameters().get(RunningProperties.IS_DISABLED) == null)) {
			return false;
		}
		return Boolean.parseBoolean(getTestParameters().get(RunningProperties.IS_DISABLED).getStringValue());
	}

	public void setDisable(boolean isDisable) {
		if (isDisable() != isDisable) {
			signalToSave(RunningProperties.IS_DISABLED);
		}
		updateTestInnerFlag(getTestParameters().get(RunningProperties.IS_DISABLED), Boolean.toString(isDisable));
	}

	public String toString() {
		String meaningFul = getMeaningfulName();
		if (StringUtils.isEmpty(meaningFul)
				|| "true"
						.equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.IGNORE_MEANINGFUL_NAME))) {
			return StringUtils.getClassName(getClassName()) + "." + getMethodName();
		}
		return meaningFul;
	}

	public boolean isError() {
		return (status == STAT_ERROR);
	}

	public boolean isFail() {
		return (status == STAT_FAIL);
	}

	public boolean isRunning() {
		return (status == STAT_RUNNING);
	}

	public boolean isWarning() {
		return (status == STAT_WARNING);
	}

	public boolean isNotRunning() {
		return (status == STAT_NOT_RUN);
	}

	public int countTestCases() {
		return 1;
	}

	public void run(TestResult result) {
		test.run(result);
	}

	public boolean isSuccess() {
		return (status == STAT_SUCCESS);
	}

	/**
	 * @return True if test was not yet signaled as Fail\Error or Warning
	 */
	public boolean statusShouldBePass() {
		return status != STAT_ERROR && status != STAT_FAIL && status != STAT_WARNING;
	}

	/**
	 * 
	 * @return true if the test pass assuming all flags (negative, marked,
	 *         warning etc.), false otherwise
	 */
	public boolean isPassAssumingFlags() {
		// Test status based on test execution without any flags
		boolean pass = (status != STAT_ERROR && status != STAT_FAIL);
		if (test instanceof SystemTest) {
			// Check the SystemTest (actual test) in case a failure/error event
			// was not passed on to the RunnerTest.
			if (!((SystemTest) test).isPassAccordingToFlags()) {
				// Test failed
				pass = false;
				// Only change the status if the test status was not changed
				// already
				if (status == STAT_RUNNING) {
					setStatus(STAT_FAIL);
				}
			}
		}
		if (isMarkedAsNegativeTest()) {
			return pass;
		}
		return pass && status != STAT_WARNING; // for Non-Negative test, warning
												// is different from pass
	}

	/**
	 * Get the test Java doc
	 */
	public String getDocumentation() {
		if ((parameters == null) || (getTestParameters().get(RunningProperties.DOCUMENTATION_TAG) == null)) {
			return "";
		}
		String doc = getTestParameters().get(RunningProperties.DOCUMENTATION_TAG).getStringValue();
		if (StringUtils.isEmpty(doc)) {
			setDocumentation(properties.getProperty(userDocString));
			return properties.getProperty(userDocString);
		}
		return doc;
	}

	public void setDocumentation(String documentation) {
		if (StringUtils.isEmpty(getTestParameters().get(RunningProperties.DOCUMENTATION_TAG).getStringValue())
				&& (StringUtils.isEmpty(documentation))) {
			return;
		}
		if (((getTestParameters().get(RunningProperties.DOCUMENTATION_TAG).getStringValue() == null && documentation != null))
				|| (!(getTestParameters().get(RunningProperties.DOCUMENTATION_TAG).getStringValue()
						.equals(documentation)))) {
			signalToSave(RunningProperties.DOCUMENTATION_TAG);
		}
		updateTestInnerFlag(getTestParameters().get(RunningProperties.DOCUMENTATION_TAG), documentation);
	}

	public void setFixture(String className) throws Exception {
		associatedFixtureClassName = className;
	}

	public String getTestName() {
		String comment = getComment();
		if (StringUtils.isEmpty(comment)) {
			return toString();
		}
		return toString() + " - " + comment;
	}

	public void setTestComment(String comment) {
		if (StringUtils.isEmpty(getTestParameters().get(RunningProperties.COMMENT_TAG).getStringValue())
				&& (StringUtils.isEmpty(comment))) {
			return;
		}
		if (((getTestParameters().get(RunningProperties.COMMENT_TAG).getStringValue() == null && comment != null))
				|| (!(getTestParameters().get(RunningProperties.COMMENT_TAG).getStringValue().equals(comment)))) {
			signalToSave(RunningProperties.COMMENT_TAG);
		}

		updateTestInnerFlag(getTestParameters().get(RunningProperties.COMMENT_TAG), comment);
	}

	public String getComment() {
		if ((parameters == null) || (getTestParameters().get(RunningProperties.COMMENT_TAG) == null)) {
			return null;
		}
		return getTestParameters().get(RunningProperties.COMMENT_TAG).getStringValue();
	}

	/**
	 * get the section order String array from the test file
	 * 
	 * @return a string array with the sorting of the sections
	 */
	public String[] getSectionOrder() {
		return sectionOrder;
	}

	/**
	 * sets the read from test file section order
	 * 
	 * @param sectionArray
	 *            the String array of the sorting
	 */
	public void setSectionOrder(String[] sectionArray) {
		this.sectionOrder = sectionArray;
	}

	/**
	 */
	private void updateParamsOrder(String paramsInclude) {
		if (StringUtils.isEmpty(paramsInclude)) {
			return;
		}
		paramsOrder = StringUtils.split(paramsInclude, ",; \n\r\t");
	}

	public PresentationDefinitions getPresentationObject() {
		return sort;
	}

	public void setPresentationObject(PresentationDefinitions sort) {
		this.sort = sort;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getFullTestId() {
		return getParent().getFullTestId() + "/" + getTestId();
	}

	public String getMeaningfulName() {
		if (!StringUtils.isEmpty(meaningfulName)) {
			return meaningfulName;
		}
		return codeMeaningfulName;
	}

	public String[] getGroups() {
		return groups;
	}

	public JTestContainer getParent() {
		return parent;
	}

	public void setParent(JTestContainer parent) {
		this.parent = parent;
	}

	private void filterProperties(Properties props) {
		props.remove(RunningProperties.COMMENT_TAG);
		props.remove(RunningProperties.DOCUMENTATION_TAG);
		props.remove(RunningProperties.IS_DISABLED);
		props.remove(RunningProperties.FIXTURE_PROPERTY_NAME);
		props.remove(RunningProperties.UUID_PARENT_TAG);
		props.remove(RunningProperties.UUID_TAG);
	}

	public void addPrivateTags(Document doc, Element jsystem) {
		// not implemented for RunnerTest (see RunnerScript)
	}

	public String getUUID() {
		if (StringUtils.isEmpty(uuid)) {
			throw new IllegalStateException("Test unique id is empty");
		}
		return uuid;
	}

	public void setUUID(String UUID) {
		this.uuid = UUID;
	}

	public String getFullUUID() {
		if (parent == null) {
			return "";
		}
		String parentFullUuid = parent.getFullUUID();
		String fullTmp = StringUtils.isEmpty(parentFullUuid) ? uuid : parentFullUuid + "." + uuid;
		return fullTmp;
	}

	public String getUUIDUpTo(JTest toStopAt) {
		if (parent == null) {
			return "";
		}
		String parentFullUuid = parent.getUUIDUpTo(toStopAt);
		String fullTmp = StringUtils.isEmpty(parentFullUuid) ? uuid : parentFullUuid + "." + uuid;
		return fullTmp;
	}

	@Override
	public DistributedExecutionParameter[] getDistributedExecutionParameters() throws Exception {
		return DistributedExecutionHelper.getHostsParameters(this);
	}

	@Override
	public void setDistributedExecutionParameters(DistributedExecutionParameter[] parameters) throws Exception {
		DistributedExecutionParameter[] before = getDistributedExecutionParameters();
		DistributedExecutionParameter[] after = parameters;
		DistributedExecutionHelper.setHostsParameters(this, parameters);
		RunnerListenersManager.getInstance().testParametersChanged(getFullUUID(), before, after);
	}

	public String getIncludeParamsStringAnnotation() {
		return includeParamsStringAnnotation;
	}

	public String getExcludeParamsStringAnnotation() {
		return excludeParamsStringAnnotation;
	}

	@Override
	public Scenario getMyScenario() {
		return getParent().getMyScenario();
	}

	@Override
	public Scenario getParentScenario() {
		return getParent().getParentScenario();
	}

	@Override
	public Scenario getRoot() {
		JTest toReturn = this;
		while (toReturn.getParent() != null) {
			toReturn = toReturn.getParent();
		}
		return (Scenario) toReturn;
	}

	@Override
	public void update() throws Exception {
		getMyScenario().update();
	}

	public boolean isMarkedAsKnownIssue() {
		if ((parameters == null) || (getTestParameters().get(RunningProperties.MARKED_AS_KNOWN_ISSUE) == null)) {
			return false;
		}
		return Boolean.parseBoolean(getTestParameters().get(RunningProperties.MARKED_AS_KNOWN_ISSUE).getStringValue());
	}

	public boolean isMarkedAsNegativeTest() {
		if ((parameters == null) || (getTestParameters().get(RunningProperties.MARKED_AS_NEGATIVE_TEST) == null)) {
			return false;
		}
		return Boolean
				.parseBoolean(getTestParameters().get(RunningProperties.MARKED_AS_NEGATIVE_TEST).getStringValue());
	}

	public void markAsKnownIssue(boolean markedAsKnownIssue) {
		if (isMarkedAsKnownIssue() != markedAsKnownIssue) {
			signalToSave(RunningProperties.MARKED_AS_KNOWN_ISSUE);
		}
		updateTestInnerFlag(getTestParameters().get(RunningProperties.MARKED_AS_KNOWN_ISSUE),
				Boolean.toString(markedAsKnownIssue));
	}

	public void markAsNegativeTest(boolean isNegativeTest) {
		if (isMarkedAsNegativeTest() != isNegativeTest) {
			signalToSave(RunningProperties.MARKED_AS_NEGATIVE_TEST);
		}
		updateTestInnerFlag(getTestParameters().get(RunningProperties.MARKED_AS_NEGATIVE_TEST),
				Boolean.toString(isNegativeTest));
	}

	public void addValidationError(ValidationError error) {
		validationErrors.add(error);
	}

	public List<ValidationError> getValidationErrors() {
		return validationErrors;
	}

	public boolean isValidationErrorsFound() {
		return (validationErrors.size() > 0);
	}

	public String getValidationErrorsAsString() {
		StringBuffer buf = new StringBuffer();
		for (ValidationError error : validationErrors) {
			buf.append(error.getTitle());
			buf.append("\n");
		}
		return buf.toString();
	}

	@Override
	public void hideInHTML(boolean isHideInHTML) {
		if (isHiddenInHTML() != isHideInHTML) {
			signalToSave(RunningProperties.HIDDEN_IN_HTML);
		}
		updateTestInnerFlag(getTestParameters().get(RunningProperties.HIDDEN_IN_HTML), Boolean.toString(isHideInHTML));
	}

	private void signalToSave(String parameterName) {
		getTestParameters().get(parameterName).signalToSave();
	}

	@Override
	public boolean isHiddenInHTML() {
		if ((parameters == null) || (getTestParameters().get(RunningProperties.HIDDEN_IN_HTML) == null)) {
			return false;
		}
		return Boolean.parseBoolean(getTestParameters().get(RunningProperties.HIDDEN_IN_HTML).getStringValue());
	}

	public void setFailureOccurred(boolean failureOccurred) {
		this.failureOccurred = failureOccurred;
		if (test instanceof SystemTest) {
			((SystemTest) test).setPass(false);
		}
	}

	public boolean isFailureOccurred() {
		return failureOccurred;
	}

	public void setMeaningfulName(String meaningfulName, boolean saveToPropFile) {
		meaningfulNameShouldBeSaved = saveToPropFile;
		if (StringUtils.isEmpty(meaningfulName)) {
			loadMeaningfulName(false);
		} else {
			this.meaningfulName = meaningfulName;
		}
	}

	/**
	 * Delete the properties of the test from the property file of the current
	 * root scenario
	 */
	// Limor Bortman
	@Override
	public void resetToDefault() throws Exception {
		Scenario root = getRoot();
		root.save();
		String id = getUUIDUpTo(root);
		ScenarioHelpers.removePropertiesFromScenarioProps(root.getName(), id, false);
		root.load();

	}
}
