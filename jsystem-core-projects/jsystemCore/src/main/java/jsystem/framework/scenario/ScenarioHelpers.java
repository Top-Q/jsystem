/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.RunProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.scenario.flow_control.AntFlowControl;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.utils.FileUtils;
import jsystem.utils.SortedProperties;
import jsystem.utils.StringUtils;
import jsystem.utils.XmlUtils;
import junit.framework.Test;

/**
 * Utility class for performing {@link Scenario} related manipulations and
 * queries. The helper was created in order to prevent addition of methods to
 * the {@link Scenario} class.
 * 
 * @author goland
 */
public class ScenarioHelpers {

	private static Map<String, Properties> propertiesCache;
	private static Logger log = Logger.getLogger(ScenarioHelpers.class
			.getName());

	static {
		resetCache();
	}

	public synchronized static void resetCache() {
		propertiesCache = Collections
				.synchronizedMap(new HashMap<String, Properties>());
	}

	/**
	 * Expects a {@link Vector} of JTests, and returns the same list but without
	 * tests of type {@link RunnerFixture} If the tests vector is empty or if
	 * all tests in vector are fixtures, an empty list will be returned.
	 */
	public static List<JTest> filterFixtures(Vector<JTest> tests) {
		ArrayList<JTest> list = new ArrayList<JTest>();
		Enumeration<JTest> testsEnum = tests.elements();
		while (testsEnum.hasMoreElements()) {
			JTest test = testsEnum.nextElement();
			if (!(test instanceof RunnerFixture)) {
				list.add(test);
			}
		}
		return list;
	}

	/**
	 * Gets a scenario, and returns a list which includes given scenario and all
	 * the scenarios under this scenario (recursively).
	 */
	public static List<Scenario> getScenarios(JTestContainer root) {
		ArrayList<Scenario> list = new ArrayList<Scenario>();
		addChildScenarios(root, list);
		return list;
	}

	// Recursively collect all of the scenarios in the tree
	public static List<Scenario> getAllScenarios(JTestContainer container) {
		ArrayList<Scenario> allScenarios = new ArrayList<Scenario>();

		for (JTest test : container.getRootTests()) {
			if (test instanceof Scenario) {
				allScenarios.add((Scenario) test);
			}
			if (test instanceof JTestContainer) {
				allScenarios.addAll(getAllScenarios((JTestContainer) test));
			}
		}
		return allScenarios;
	}

	/**
	 * Create the path between a final sub container and the scenario above it
	 * Recurse till a scenario, and build the list top down
	 * 
	 * @throws Exception
	 * 
	 */
	public static Scenario findPath(ArrayList<Integer> containersPath,
			JTestContainer pointer) throws Exception {
		if (!(pointer instanceof Scenario)) {
			JTestContainer parent = pointer.getParent();
			int index = parent.getRootIndex(pointer);

			// In case index == -1 it means either the pointer is null (so we
			// have nothing to find)
			// or that it can't be found in his parent (so maybe it was
			// removed...)
			// Either way - we should not continue what we did till now
			if (index == -1) {
				throw new Exception("path not found to test");
			}

			containersPath.add(0, index);
			pointer = findPath(containersPath, parent);
		}
		return (Scenario) pointer;
	}

	/**
	 * get all sub scenarios in a scenario
	 * 
	 * @param root
	 *            the scenario to search in
	 * @param list
	 *            the vector of sub-scenarios to return
	 */
	private static void addChildScenarios(JTestContainer root,
			List<Scenario> list) {
		if (root instanceof Scenario) {
			list.add((Scenario) root);
		}

		Vector<JTest> rootTests = root.getRootTests();
		Enumeration<JTest> testsEnum = rootTests.elements();
		while (testsEnum.hasMoreElements()) {
			JTest test = (JTest) testsEnum.nextElement();
			if (test instanceof JTestContainer) {
				addChildScenarios((JTestContainer) test, list);
			}
		}
	}

	/**
	 * get a scenario parameters from a scenario properties file<br>
	 * if file doesn't exist, return an empty properties object
	 * 
	 * @param scenarioName
	 *            the scenario to get parameters from
	 * @return
	 */
	public static Properties getScenarioParameters(JTestContainer scenario) {
		String scenarioFileName = scenario.getName();
		String rootScenarioFile = ScenariosManager.getInstance()
				.getCurrentScenario().getName();
		Properties rootProp = getScenarioProperties(rootScenarioFile);
		if (scenario.isRoot()) {
			return rootProp;
		}

		Properties scenarioProp = getScenarioProperties(scenarioFileName);

		String uuid = scenario.getUUID();
		Enumeration<Object> e = rootProp.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			if (key.toString().startsWith(uuid)) {
				Object val = rootProp.get(key);
				scenarioProp.put(key.toString().substring(uuid.length() + 1),
						val);
			}
		}
		return scenarioProp;
	}

	/**
	 * get scenario properties by a file name
	 * 
	 * @param fileName
	 * @return
	 */
	public static Properties getScenarioProperties(String scenarioName) {
		return getScenarioProperties(scenarioName, false);
	}

	/**
	 * get scenario properties by a file name
	 * 
	 * @param fileName
	 * @return
	 */
	public static synchronized Properties getScenarioProperties(
			String scenarioName, boolean ignoreCache) {
		Properties prop = null;
		String scenarioPropsFile = getScenarioPropertiesFile(scenarioName);
		prop = propertiesCache.get(normalizeScenarioName(scenarioName));
		if (prop == null || ignoreCache) {
			try {
				if (new File(scenarioPropsFile).exists()) {
					prop = FileUtils.loadPropertiesFromFile(scenarioPropsFile);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		if (prop == null) {
			return new Properties();
		}
		propertiesCache.put(normalizeScenarioName(scenarioName), prop);
		return prop;
	}

	/**
	 * get a scenario parameter value by reference<br>
	 * check current root from bottom up, then go to lower sub-scenario...
	 * 
	 * @param scenarioPathString
	 * @param key
	 * @return
	 */
	public static String getScenarioParametersValue(String scenarioPathString,
			String scenarioFullUuid, String key) {
		String[][] namesAndIds = convertScenariosNameAndIds(scenarioPathString,
				scenarioFullUuid);
		Properties myProperties = getScenarioProperties(namesAndIds[0][0]);
		String myValue = myProperties.getProperty(key);
		if (myValue == null) {
			myValue = myProperties.getProperty("root." + key);
		}
		if (StringUtils.isEmpty(scenarioFullUuid)) {
			return myValue;
		}

		String value;

		// check from root down
		for (int i = 0; i < namesAndIds.length; i++) {
			String currentRoot = namesAndIds[i][0];
			Properties prop = getScenarioProperties(currentRoot);
			String[] relativeId = getAllFullUuid(currentRoot, namesAndIds);
			for (int j = relativeId.length - 1; j >= 0; j--) {
				String idAndKey = (i == j) ? key : relativeId[j] + "." + key;
				value = prop.getProperty(idAndKey);
				if (value != null) {
					return value;
				}
			}
		}
		return myValue;
	}

	/**
	 * Convert a Scenario full path String and Full Unique ID to an array of all
	 * names and ids of each scenario on the way
	 * 
	 * @param names
	 *            A string of all scenarios on path concatenated with a "."
	 * @param fullId
	 *            a Full unique ID
	 * @return
	 */
	private static String[][] convertScenariosNameAndIds(String names,
			String fullId) {
		String mySplit = "--MY SPLIT--";
		String[] allNames = splitScenarioFullName(names);
		String[] allIds = fullId.replace(".", mySplit).split(mySplit);
		String[][] namesAndIds = new String[allNames.length][2];
		for (int i = 0; i < namesAndIds.length; i++) {
			namesAndIds[i][0] = allNames[i];
			if (i == 0) {
				namesAndIds[i][1] = "";
			} else {
				namesAndIds[i][1] = allIds[i - 1];
			}
		}

		return namesAndIds;
	}

	/**
	 * Split a scenario path String to all scenarios names on the path array
	 * 
	 * @param scenarioFullName
	 * @return
	 */
	private static String[] splitScenarioFullName(String scenarioFullName) {
		String[] splitRes = scenarioFullName
				.split(CommonResources.SCENARIOS_FOLDER_NAME + "/");
		ArrayList<String> res = new ArrayList<String>();
		for (String s : splitRes) {
			if (StringUtils.isEmpty(s)) {
				continue;
			}
			if (s.endsWith(".")) {
				s = s.substring(0, s.length() - 1);
			}
			res.add(CommonResources.SCENARIOS_FOLDER_NAME + "/" + s);
		}
		return res.toArray(new String[0]);
	}

	/**
	 * 
	 * @param currentScenarioName
	 * @param rootScenarioName
	 * @param namesAndIds
	 * @return
	 */
	private static String[] getAllFullUuid(String rootScenarioName,
			String[][] namesAndIds) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < namesAndIds.length; i++) {
			String fullUuid = getFullUUIDPath(namesAndIds[i][0],
					rootScenarioName, namesAndIds);
			list.add(fullUuid);
		}
		return list.toArray(new String[0]);
	}

	/**
	 * get a scenario parameter value by reference<br>
	 * check current root from bottom up, then go to lower sub-scenario...
	 * 
	 * @param current
	 * @param key
	 * @return
	 */
	public static String getScenarioParametersValue(JTestContainer current,
			String key) {
		Properties myProperties = getScenarioProperties(current.getName());
		String myValue = myProperties.getProperty(key);
		if (myValue == null) {
			myValue = myProperties.getProperty("root." + key);
		}
		if (current.isRoot()) {
			return myValue;
		}
		String[][] namesAndIds = getAllFullUUID(current, null);
		String value;

		// first check from root down
		for (int i = namesAndIds.length - 1; i > 0; i--) {
			String currentRoot = namesAndIds[i][0];
			Properties prop = getScenarioProperties(currentRoot);
			String[][] relativeNameAndId = getAllFullUUID(current, currentRoot);
			for (int j = 0; j < relativeNameAndId.length; j++) {
				String idAndKey = (i == j) ? key : relativeNameAndId[j][1]
						+ "." + key;
				value = prop.getProperty(idAndKey);
				if (value != null) {
					return value;
				}
				if (j == 0) { // current selected scenario
					value = prop.getProperty("root." + idAndKey);
					if (value != null) {
						return value;
					}
				}

			}
		}
		return myValue;
	}

	/**
	 * get all UID and scenario name from current scenario up to root
	 * 
	 * @param current
	 * @return
	 */
	private static String[][] getAllUID(JTestContainer current) {
		ArrayList<String[]> list = new ArrayList<String[]>();

		while (current != null) {
			if (!(current instanceof AntFlowControl)) {
				list.add(new String[] { current.getName(), current.getUUID() });
			}
			current = (JTestContainer) current.getParent();
		}
		return list.toArray(new String[0][0]);
	}

	/**
	 * get all UID and scenario name from current scenario up to root
	 * 
	 * @param current
	 * @return
	 */
	private static String[][] getAllFullUUID(JTestContainer current,
			String scenarioRoot) {
		ArrayList<String[]> list = new ArrayList<String[]>();
		while (current != null) {
			list.add(new String[] { current.getName(),
					getFullUUIDPath(current, scenarioRoot) });
			current = (JTestContainer) current.getParent();
		}
		return list.toArray(new String[0][0]);
	}

	/**
	 * get the full UUID string for the current scenario, till the given
	 * scenario name
	 * 
	 */
	public static String getFullUUIDPath(JTestContainer scenario,
			String scenarioRoot) {
		String[][] nameAndUuid = getAllUID(scenario);
		String s = "";
		for (int i = 0; i < nameAndUuid.length; i++) {
			String name = nameAndUuid[i][0];
			if (name.equals(scenarioRoot)) {
				return s;
			}
			String uuid = nameAndUuid[i][1];
			if (!StringUtils.isEmpty(uuid)) {
				if (s == "") {
					s += uuid;
				} else {
					s = uuid + "." + s;
				}
			}
		}
		return s;
	}

	/**
	 * get the full UUID string for the current scenario, till the given
	 * scenario name The array is from root to current
	 */
	private static String getFullUUIDPath(String scenarioName,
			String scenarioRoot, String[][] namesAndIds) {
		String s = "";
		boolean start = false;
		// since the first is root and last is the last son the loop is reversed
		for (int i = namesAndIds.length - 1; i >= 0; i--) {
			String name = namesAndIds[i][0];
			if (!start && !name.equals(scenarioName)) {
				continue;
			}

			start = true;

			if (name.equals(scenarioRoot)) {
				return s;
			}
			String uuid = namesAndIds[i][1];
			if (!StringUtils.isEmpty(uuid)) {
				if (s.equals("")) {
					s += uuid;
				} else {
					s = uuid + "." + s;
				}
			}
		}
		return s;
	}

	/**
	 * get the full path name of a scenario properties file in the classes
	 * folder
	 * 
	 * @param scenarioName
	 *            the scenario to get path for
	 * @return
	 */
	public static String getScenarioPropertiesFile(String scenarioName) {
		return JSystemProperties.getInstance().getPreference(
				FrameworkOptions.TESTS_CLASS_FOLDER)
				+ "/" + scenarioName + ".properties";
	}

	/**
	 * get the full path name of a scenario properties file in the tests folder
	 * 
	 * @param scenarioName
	 *            the scenario to get path for
	 * @return
	 */
	public static String getScenarioSrcPropertiesFile(String scenarioName) {
		return JSystemProperties.getInstance().getPreference(
				FrameworkOptions.RESOURCES_SOURCE_FOLDER)
				+ "/" + scenarioName + ".properties";
	}

	/**
	 * get Scenario source file in the classes directory
	 * 
	 * @param scenarioName
	 *            the scenario to get file for
	 * @return
	 */
	public static String getScenarioFile(String scenarioName) {
		return JSystemProperties.getInstance().getPreference(
				FrameworkOptions.TESTS_CLASS_FOLDER)
				+ "/" + scenarioName + ".xml";
	}

	/**
	 * get Scenario source file
	 * 
	 * @param scenarioName
	 *            the scenario to get file for
	 * @return
	 */
	public static String getScenarioSrcFile(String scenarioName) {
		return JSystemProperties.getInstance().getPreference(
				FrameworkOptions.RESOURCES_SOURCE_FOLDER)
				+ "/" + scenarioName + ".xml";
	}

	/**
	 * save the Scenario parameters (used for reference) to root scenario file.<br>
	 * 
	 * @param params
	 *            only scenario parameters!
	 * @param scenario
	 *            the scenario to save for
	 * @param recursive
	 *            if True then also apply to all sub-scenarios
	 */
	public static void saveScenarioParametersToFile(Parameter[] params,
			Scenario scenario, boolean recursive, boolean onlyCache)
			throws Exception {
		String rootScenario = ScenariosManager.getInstance()
				.getCurrentScenario().getName();
		String fileName = ScenarioHelpers
				.getScenarioPropertiesFile(rootScenario);
		Properties prop = new Properties();
		ArrayList<Scenario> scenarios = new ArrayList<Scenario>();
		if (recursive) {
			addChildScenarios((JTestContainer) scenario, scenarios);
		} else {
			scenarios.add(scenario);
		}

		if ((new File(fileName).exists())) {
			prop = getScenarioProperties(rootScenario);
		}

		for (Parameter p : params) {
			for (Scenario current : scenarios) {
				Properties actualParameters = convertParametersToProperties(current
						.getScenarioParameters(true, true));
				String uuid = getFullUUIDPath(current, rootScenario);
				String uuidString = (StringUtils.isEmpty(uuid)) ? "" : uuid
						+ ".";

				if (!actualParameters.containsKey(p.getName())) {
					continue;
				}
				prop.setProperty(uuidString + p.getName(), p.getStringValue());
			}
		}
		if (prop.size() == 0) { // no scenario parameters
			return;
		}
		saveScenarioPropertiesToSrcAndClass(prop, rootScenario, onlyCache);
	}

	public static void addScenarioPropertyToFile(String scenarioName,
			String key, String value, boolean onlyCache) throws Exception {
		Properties p = getScenarioProperties(scenarioName);
		p.setProperty(key, value);
		saveScenarioPropertiesToSrcAndClass(p, scenarioName, onlyCache);
	}

	public static int counter = 0;
	/**
	 * save the given properties to the given scenario file (both source and
	 * class files)
	 * 
	 * @param p
	 *            the properties to save
	 * @param scenarioName
	 *            the scenario properties file to save to
	 */
	public static synchronized void saveScenarioPropertiesToSrcAndClass(
			Properties p, String scenarioName, boolean onlyCache)
			throws Exception {
		if (!onlyCache) {
			removeRunningPropsWithDefaultValues(p);
			String classFileName = ScenarioHelpers
					.getScenarioPropertiesFile(scenarioName);
			String srcFileName = ScenarioHelpers
					.getScenarioSrcPropertiesFile(scenarioName);
			File srcFile = new File(srcFileName);
			if (srcFile.getParent() != null && srcFile.getParentFile().exists()) {
				FileUtils.saveSortedPropertiesToFile(p, srcFileName, false); // copy
																				// to
																				// tests
																				// source
																				// folder
			}
			FileUtils.saveSortedPropertiesToFile(p, classFileName, false); // copy
																			// to
																			// classes
																			// folder
		} else {
			ScenariosManager.setDirty();
		}
		propertiesCache.put(normalizeScenarioName(scenarioName), p);
	}

	/**
	 * There is no need to save running properties with value 'false'. Saving
	 * them to file is causing a lot of IO operations and in large scenarios can
	 * cause major performance penalty.
	 * 
	 * @author itai_a
	 * @param properties
	 */
	private static void removeRunningPropsWithDefaultValues(
			Properties properties) {
		Enumeration<Object> e = properties.keys();
		while (e.hasMoreElements()) {
			final Object key = e.nextElement();
			if (properties.get(key) == null) {
				continue;
			}
			if (!properties.get(key).equals("false")) {
				// All default values are only 'false'
				continue;
			}
			// ITAI: This is not possible to save the properties values only in
			// case the value is not the default one. The reason is that when we
			// have sub scenarios and we want to override one of the parameters
			// that is set to true we can't set it to false in the root scenario
			// context because false value is not saved.
			if (key.toString().endsWith(RunningProperties.EDIT_ONLY_LOCALLY)
			// || key.toString().endsWith(RunningProperties.HIDDEN_IN_HTML)
			// ||
			// key.toString().endsWith(RunningProperties.MARKED_AS_KNOWN_ISSUE)
			// ||
			// key.toString().endsWith(RunningProperties.MARKED_AS_NEGATIVE_TEST)
					|| key.toString().endsWith(
							RunningProperties.SCENARIO_AS_TEST_TAG)) {
				properties.remove(key);
			}
		}
	}

	public static Properties removeDeletedScenarioProperties(Properties properties, String scenarioName) throws Exception {
		long startTime = System.currentTimeMillis();
		String lastTestFullUuid = "";
		Properties sortedProperties = new SortedProperties();
		sortedProperties.putAll(properties);
		Enumeration<Object> e1 = sortedProperties.keys();
		while (e1.hasMoreElements()) {
			final Object key1 = e1.nextElement();
			if (sortedProperties.get(key1) == null) {
				continue;
			}
			String testFullUuid = key1.toString().substring(0, key1.toString().indexOf("."));
			if (!testFullUuid.equals(lastTestFullUuid)) {
				lastTestFullUuid = testFullUuid;
				JTest test = ScenarioHelpers.getTestById(ScenariosManager.getInstance().getCurrentScenario(), testFullUuid);
				if (test instanceof RunnerTest) {
					Properties testProps = ScenarioHelpers.getTestPropertiesFromScenarioProps(scenarioName, testFullUuid);
					Parameter[] testParams = ((RunnerTest) test).getParameters();
					List<String> testParamsNames = new ArrayList<>();
					for (int i = 0; i < testParams.length; i++) {
						testParamsNames.add(testParams[i].getName());
					}
					Enumeration<Object> e2 = testProps.keys();	
					while (e2.hasMoreElements()) {
						final Object key2 = e2.nextElement();
						if (testProps.get(key2) == null ) {
							continue;
						}
						String testParamName = key2.toString();
						if (testParamName.startsWith("jsystem.")) {
							continue;
						}
						if (!testParamsNames.contains(testParamName)) {
							sortedProperties.remove(testFullUuid + "." + testParamName);
						}
					}
				}
			}
		}
		long endTime = System.currentTimeMillis();
		double diff = endTime-startTime;
		log.info("Removing deleted properties took " + (double)diff/1000 + " seconds.");
		return sortedProperties;
	}
	
	public static void deleteScenario(Scenario scenario) {
		if (scenario == null) {
			return;
		}
		File sf = scenario.getScenarioFile();
		FileUtils.deleteFile(sf.getAbsolutePath()); // delete scenario classes
													// file
		String scenarioName = scenario.getName();
		FileUtils.deleteFile(getScenarioSrcFile(scenarioName)); // delete
																// scenario
																// source file
		deletePropertiesFile(scenarioName); // delete properties file in serc
											// and classes
	}

	/**
	 * delete scenario properties file , if exists
	 * 
	 * @param scenarioName
	 *            the scenario to get path for
	 */
	public static synchronized void deletePropertiesFile(String scenarioName) {
		File classesFile = new File(
				ScenarioHelpers.getScenarioPropertiesFile(scenarioName));
		File srcFile = new File(
				ScenarioHelpers.getScenarioSrcPropertiesFile(scenarioName));
		if (classesFile.exists() && classesFile.isFile()) {
			classesFile.delete();
		}
		if (srcFile.exists() && srcFile.isFile()) {
			srcFile.delete();
		}
		propertiesCache.remove(normalizeScenarioName(scenarioName));
	}

	/**
	 * remove scenario "scenarios/" header
	 */
	public static String removeScenarioHeader(String scenarioName) {
		if (scenarioName.startsWith(CommonResources.SCENARIOS_FOLDER_NAME)) {
			return scenarioName.substring(CommonResources.SCENARIOS_FOLDER_NAME
					.length() + 1);
		}
		return scenarioName;
	}

	/**
	 * add scenario "scenarios/" header if name is not already in the correct
	 * format
	 */
	public static String addScenarioHeader(String scenarioName) {
		if (scenarioName.startsWith(CommonResources.SCENARIOS_FOLDER_NAME)) {
			return scenarioName;
		}
		return CommonResources.SCENARIOS_FOLDER_NAME + "/" + scenarioName;
	}

	/**
	 * Iterates over all offsprings of <code>s</code> for each offspring,
	 * invokes the <code>visitor</code>
	 */
	public static void iterateContainer(JTestContainer s,
			JTestContainerVisitor visitor) throws Exception {
		Vector<JTest> rootElements = s.getRootTests();
		Iterator<JTest> iter = rootElements.iterator();
		while (iter.hasNext()) {
			JTest t = iter.next();
			if (t instanceof JTestContainer) {
				iterateContainer((JTestContainer) t, visitor);
			}
			visitor.visitScenarioElement(t);
		}
	}

	/**
	 * Returns <code>test</code> root scenario. The method assumes that the
	 * <code>test</code> is part of a populated tests tree created by the
	 * {@link ScenrioManager}
	 * 
	 */
	public static Scenario getRoot(JTest test) {
		while (test.getParent() != null) {
			test = test.getParent();
		}
		return (Scenario) test;
	}

	/**
	 * Searches for test with id <code>id</code> in <code>root</code><br>
	 * If test is found it is returned, otherwise <code>null</code> is returned.
	 */
	public static JTest getTestById(final JTestContainer root, final String id)
			throws Exception {
		class TestFinder implements JTestContainerVisitor {
			private JTest foundTest;
			private String idToFind;

			TestFinder(String idToFind) {
				this.idToFind = idToFind;
			}

			@Override
			public void visitScenarioElement(JTest t1) throws Exception {
				String uuid = t1.getFullUUID();
				if (uuid.equals(idToFind)) {
					foundTest = t1;
				}
			};
		}

		TestFinder tf = new TestFinder(id);
		ScenarioHelpers.iterateContainer(root, tf);
		return tf.foundTest;
	}

	/**
	 * Searches for test with id <code>id</code> in <code>root</code><br>
	 * If test is found it is returned, otherwise <code>null</code> is returned.
	 */
	public static JTest getFlowElementById(final JTestContainer root,
			final String id) throws Exception {

		class TestFinder implements JTestContainerVisitor {
			private JTest foundTest;
			private String idToFind;

			TestFinder(String idToFind) {
				this.idToFind = idToFind;
			}

			@Override
			public void visitScenarioElement(JTest t1) throws Exception {
				if (!(t1 instanceof AntFlowControl)) {
					return;
				}
				String uuid = ((AntFlowControl) t1).getFlowFullUUID();
				if (uuid.equals(idToFind)) {
					foundTest = t1;
				}
			};
		}

		TestFinder tf = new TestFinder(id);
		ScenarioHelpers.iterateContainer(root, tf);
		return tf.foundTest;
	}

	/**
	 * Returns current scenario properties.
	 */
	public static Properties getRootProperties() throws Exception {
		String rootScenario = ScenariosManager.getInstance()
				.getCurrentScenario().getName();
		Properties prop = getScenarioProperties(rootScenario);
		return prop;
	}

	/**
	 * Returns a map with all offsprings <code>root</code><br>
	 * The returned map key is test uuid, value is the test.
	 */
	public static Map<String, JTest> getAllElements(JTestContainer root)
			throws Exception {
		class AllTestsGraber implements JTestContainerVisitor {
			public Map<String, JTest> m = new HashMap<String, JTest>();

			@Override
			public void visitScenarioElement(JTest t) throws Exception {
				m.put(t.getFullUUID(), t);

			}

		}
		AllTestsGraber graber = new AllTestsGraber();
		iterateContainer(root, graber);
		return graber.m;
	}

	/**
	 * Returns the first ancestor of <code>t</code> which is a scenario. if
	 * <code>t</code> is instance of Scenario, returns <code>t</code> if
	 * <code>t</code> is <code>null</code>, returns null.
	 */
	public static Scenario getFirstScenarioAncestor(JTest t) {
		if ((t instanceof Scenario || t == null)
				&& !isScenarioAsTestAndNotRoot(t)) {
			return (Scenario) t;
		}
		JTest toRet = t.getParent();
		while (toRet != null) {
			if (toRet instanceof Scenario && !isScenarioAsTestAndNotRoot(toRet)) {
				break;
			}
			toRet = toRet.getParent();
		}
		return (Scenario) toRet;
	}

	/**
	 * check if a given test is a Scenario marked as test and is not the root
	 * Scenario
	 * 
	 * @param test
	 *            the Test to check
	 * @return
	 */
	public static boolean isScenarioAsTestAndNotRoot(JTest test) {
		if (!(test instanceof Scenario)) {
			return false;
		}
		Scenario s = (Scenario) test;
		return s.isScenarioAsTest() && !s.isRoot();
	}

	private static Properties convertParametersToProperties(Parameter[] param) {
		Properties prop = new Properties();
		for (Parameter p : param) {
			prop.setProperty(p.getName(), p.getStringValue());
		}
		return prop;
	}

	/**
	 * if the scenario has a properties file, make a copy of it for the new
	 * Scenario name
	 * 
	 * @param oldScenarioName
	 *            scenario to copy properties from
	 * @param newScenarioName
	 *            scenario to copy properties to
	 * @throws IOException
	 */
	public static void copyScenarioPropertiesFileToNewScenario(
			String oldScenarioName, String newScenarioName) throws IOException {
		String oldPropertiesFilePath = getScenarioSrcPropertiesFile(oldScenarioName);
		if (!new File(oldPropertiesFilePath).exists()) {
			return;
		}
		String newPropertiesFilePath = getScenarioSrcPropertiesFile(newScenarioName);
		FileUtils.copyFile(oldPropertiesFilePath, newPropertiesFilePath);
		String newPropertiesFilePathClasses = getScenarioPropertiesFile(newScenarioName);
		FileUtils.copyFile(newPropertiesFilePath, newPropertiesFilePathClasses);
	}

	public static String getScenarioFullUuid(String testFullUuid) {
		int lastIndex = testFullUuid.lastIndexOf(".");
		String scenarioFullUuid = lastIndex < 0 ? "" : testFullUuid.substring(
				0, lastIndex);
		return scenarioFullUuid;
	}

	/**
	 * Backup current default scenario to new file (with addition time to name)
	 * 
	 * @return the new file name (not full path)
	 * @throws IOException
	 */
	public static String backupDefaultScenarioFile() throws IOException {
		String defaultName = "scenarios" + File.separator + "default";
		long time = System.currentTimeMillis();
		String newName = defaultName + "_" + time;
		File scenarioDirectoryFile = ScenariosManager.getInstance()
				.getScenariosDirectoryFiles();
		String defaultClassFile = new File(scenarioDirectoryFile, defaultName
				+ ".xml").getAbsolutePath();
		String scenariosSrc = JSystemProperties.getInstance().getPreference(
				FrameworkOptions.RESOURCES_SOURCE_FOLDER);
		copyScenarioPropertiesFileToNewScenario(defaultName, newName);
		FileUtils.copyFile(defaultClassFile, new File(scenariosSrc, newName
				+ ".xml").getAbsolutePath());
		FileUtils.copyFile(defaultClassFile, new File(scenarioDirectoryFile,
				newName + ".xml").getAbsolutePath());
		FileUtils.deleteFile(new File(scenariosSrc, defaultName + ".xml")
				.getAbsolutePath());
		FileUtils.deleteFile(defaultClassFile);
		return newName;
	}

	public static boolean isPackedScenario(String scenarioName) {
		String value = getScenarioProperties(scenarioName).getProperty(
				RunningProperties.SCENARIO_AS_TEST_TAG);
		return (value != null && value.toLowerCase().equals("true"));
	}

	public static boolean isMarkedAsKnownIssue(String uuid, String scenarioName) {
		return "true".equals(getTestProperty(uuid, scenarioName,
				RunningProperties.MARKED_AS_KNOWN_ISSUE));
	}

	public static boolean isMarkedAsNegativeTest(String uuid,
			String scenarioName) {
		return "true".equals(getTestProperty(uuid, scenarioName,
				RunningProperties.MARKED_AS_NEGATIVE_TEST));
	}

	public static boolean isHiddenInHTML(String uuid, String scenarioName) {
		return "true".equals(getTestProperty(uuid, scenarioName,
				RunningProperties.HIDDEN_IN_HTML));
	}

	/**
	 * Get parameter from all hierarchical levels
	 */
	public static boolean isMarkedAsKnownIssue(String fullUuid,
			Scenario rootScenario) {
		return "true".equals(getParameterValueFromProperties(fullUuid,
				rootScenario, RunningProperties.MARKED_AS_KNOWN_ISSUE, null));
	}

	/**
	 * Get parameter from all hierarchical levels
	 */
	public static boolean isMarkedAsNegativeTest(String fullUuid,
			Scenario rootScenario) {
		return "true".equals(getParameterValueFromProperties(fullUuid,
				rootScenario, RunningProperties.MARKED_AS_NEGATIVE_TEST, null));
	}

	/**
	 * Get parameter from all hierarchical levels
	 */
	public static boolean isHiddenInHTML(String fullUuid, Scenario rootScenario) {
		return "true".equals(getParameterValueFromProperties(fullUuid,
				rootScenario, RunningProperties.HIDDEN_IN_HTML, null));
	}

	/**
	 * 
	 */
	public static boolean isParentScenarioPackedScenario(String uuid) {
		String mainScenarioName = System
				.getProperty(RunningProperties.CURRENT_SCENARIO_NAME);
		int lastIndexOfDot = uuid.lastIndexOf('.');
		String parentScenarioUUID = "";
		if (lastIndexOfDot > 0) {
			parentScenarioUUID = uuid.substring(0, lastIndexOfDot);
		}
		String value = ScenarioHelpers.getTestProperty(parentScenarioUUID,
				mainScenarioName, RunningProperties.SCENARIO_AS_TEST_TAG);
		return (value != null && value.toLowerCase().equals("true"));
	}

	/**
	 * Update test properties from parameters (not inner properties)
	 * 
	 * @param t
	 * @param rootScenarioName
	 * @param onlyCache
	 */
	public static void updateTestProperties(JTest t, String rootScenarioName,
			boolean onlyCache) {
		Parameter[] params = t.getParameters();
		String uniqueId = t.getFullUUID() + ".";
		updateTestProperties(uniqueId, params, rootScenarioName, onlyCache);
	}

	public static synchronized void updateTestProperties(String uniqueId,
			Parameter[] params, String rootScenarioName, boolean onlyCache) {
		Properties props = getScenarioProperties(rootScenarioName);
		for (Parameter p : params) {
			if (p.getValue() == null || !p.shouldBeSaved()) {
				continue;
			}
			String key = uniqueId + p.getName();
			props.put(key, p.getStringValue());
		}
		try {
			saveScenarioPropertiesToSrcAndClass(props, rootScenarioName,
					onlyCache);
			for (Parameter p : params) {
				p.signalNotToSave(); // since it is now saved
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Load test properties from scenario properties file
	 * 
	 * @param test
	 *            the test Object
	 * @param currentProps
	 *            current test properties
	 * @throws Exception
	 */
	public static void loadTestProperties(JTest test, Properties currentProps) {
		Scenario s = test.getMyScenario();
		while (s != null) {
			Properties props = getScenarioProperties(s.getName());
			String testUUID = test.getFullUUID();
			String scenarioFullUUID = s.getFullUUID();
			String uuid = testUUID.substring(scenarioFullUUID.length());
			if (uuid.startsWith(".")) {
				uuid = uuid.substring(1);
			}
			String uniqueId = uuid + ".";
			Enumeration<Object> keyEnum = currentProps.keys();
			while (keyEnum.hasMoreElements()) {
				String keyEnumStr = keyEnum.nextElement().toString();
				String key = uniqueId + keyEnumStr;
				if (props.get(key) == null) {
					continue;
				}
				String value = props.get(key).toString();
				currentProps.put(keyEnumStr, value);
			}
			if (s.getParent() == null) {
				s = null;
			} else {
				s = s.getParent().getMyScenario();
			}
		}
	}

	public static String getParameterValueFromProperties(String testFullUuid,
			Scenario rootScenario, String key, String currentValue) {
		JTest test = rootScenario.getTestByFullId(testFullUuid);
		return getParameterValueFromProperties(test, testFullUuid, key,
				currentValue);
	}

	/**
	 * Get a single parameter value from the Scenario properties file
	 * 
	 * @param test
	 * @param testFullUuid
	 * @param key
	 * @param currentValue
	 * @return
	 */
	public static String getParameterValueFromProperties(JTest test,
			String testFullUuid, String key, String currentValue) {
		Scenario s = test.getMyScenario();
		String toReturn = currentValue;
		while (s != null) {
			Properties props = getScenarioProperties(s.getName());
			String scenarioFullUUID = s.getFullUUID();
			String uuid = testFullUuid.substring(scenarioFullUUID.length());
			if (uuid.startsWith(".")) {
				uuid = uuid.substring(1);
			}
			String uniqueId = uuid + ".";
			String keyWithId = uniqueId + key;
			if (props.get(keyWithId) != null) {
				toReturn = props.get(keyWithId).toString();
			}

			if (s.getParent() == null) {
				s = null;
			} else {
				s = s.getParent().getMyScenario();
			}
		}

		return toReturn;
	}

	/**
	 * Get test properties from scenario properties file
	 * 
	 * @param scenariosNameString
	 *            the full path scenarios name string
	 * @param fullUUID
	 *            test full unique ID
	 * @return all properties relevant to the test
	 * @throws Exception
	 */
	public static Properties getTestPropertiesFromScenarioProps(
			String scenariosNameString, String fullUUID) throws Exception {
		String[][] namesAndIds = convertScenariosNameAndIds(
				scenariosNameString, fullUUID);
		Properties props = new Properties();
		for (int i = namesAndIds.length - 1; i >= 0; i--) {
			String[] scenario = namesAndIds[i];
			String uuid = fullUUID.substring(fullUUID.indexOf(scenario[1])
					+ scenario[1].length());
			while (uuid.startsWith(".")) {
				uuid = uuid.substring(1);
			}
			Properties allProps = getScenarioProperties(scenario[0]);
			Enumeration<Object> iter = allProps.keys();
			while (iter.hasMoreElements()) {
				String key = iter.nextElement().toString();
				if (!key.startsWith(uuid)) {
					continue;
				}
				String updatedKey = key.substring(uuid.length() + 1);
				if (allProps.get(key) != null) {
					String value = allProps.get(key).toString();

					props.put(updatedKey, value);
				}
			}
		}
		return props;
	}

	/**
	 * Go over all scenarios from parent up and retrieve all properties related
	 * to the given test
	 * 
	 * @param test
	 *            The test to get properties for
	 * @return
	 */
	public static Properties getAllTestPropertiesFromAllScenarios(JTest test) {
		return getAllTestPropertiesUpTo(test, null, true);
	}

	/**
	 * Go over all scenarios from parent up and retrieve all properties related
	 * to the given test
	 * 
	 * @param fullUuid
	 *            The runnerTest full unique ID
	 * @param ignoreCache
	 *            True will go to filesystem instead of cache HashMap
	 * 
	 * @return
	 */
	public static Properties getAllTestPropertiesFromAllScenarios(
			String fullUuid, boolean ignoreCache) {
		RunnerTest rt = ScenariosManager.getInstance().getCurrentScenario()
				.getRunnerTestByFullId(fullUuid);
		return getAllTestPropertiesUpTo(rt, null, ignoreCache);
	}

	/**
	 * Go over all scenarios from parent up and retrieve all properties related
	 * to the given test
	 * 
	 * @param test
	 *            The test to get properties for
	 * @param ignoreCache
	 *            True will go to filesystem instead of cache HashMap
	 * @return
	 */
	public static Properties getAllTestPropertiesUpTo(JTest test,
			Scenario toStopAt, boolean ignoreCache) {
		Properties props = new Properties();
		if (null == test){
			//ITAI: This can happen in case which we failed to initialize the test
			//Class. We would report on it later on but we need to pass
			//This part. See issue #193
			return props;
		}

		// check from closest Scenario up
		Scenario parent = test.getMyScenario();
		while (parent != null && !parent.equals(toStopAt)) {
			Properties prop = getScenarioProperties(parent.getName(),
					ignoreCache);
			String fullUUIDUpTo = test.getUUIDUpTo(parent);
			for (Object key : prop.keySet()) {
				if (key.toString().startsWith(fullUUIDUpTo)) {
					String name;
					if (!StringUtils.isEmpty(fullUUIDUpTo)) {
						name = key.toString().replace(fullUUIDUpTo + ".", "");
					} else {
						name = key.toString();
					}
					String value = prop.getProperty(key.toString());
					props.setProperty(name, value);
				}
			}
			parent = parent.getParentScenario();
		}

		return props;
	}

	public static void removePropertiesFromScenarioProps(
			String rootScenarioName, String fullUUID, boolean onlyCache)
			throws Exception {
		Properties props = getScenarioProperties(rootScenarioName);
		Properties propsClone = (Properties) props.clone();
		Enumeration<Object> keyEnum = propsClone.keys();
		while (keyEnum.hasMoreElements()) {
			String key = keyEnum.nextElement().toString();
			if (key.startsWith(fullUUID)) {
				props.remove(key);
			}
		}
		saveScenarioPropertiesToSrcAndClass(props, rootScenarioName, onlyCache);
	}

	/**
	 * Check if a test is marked as disabled
	 * 
	 * @param fullUUID
	 *            the Test full unique ID
	 * @param rootScenarioName
	 *            the root name
	 * @return True if disabled, false otherwise
	 * @throws Exception
	 */
	public static boolean testHasDisabledProperty(String fullUUID,
			String rootScenarioName) throws Exception {
		if (StringUtils.isEmpty(rootScenarioName)) {
			return false;
		}
		Properties allProps = getScenarioProperties(rootScenarioName);
		String key = fullUUID + "." + RunningProperties.IS_DISABLED;
		return allProps.get(key) == null ? false : true;
	}

	/**
	 * Check if a test is marked as disabled
	 * 
	 * @param fullUUID
	 *            the Test full unique ID
	 * @param rootScenarioName
	 *            the root name
	 * @return True if disabled, false otherwise
	 * @throws Exception
	 */
	public static boolean isTestDisabled(String fullUUID,
			String rootScenarioName) {
		Properties allProps = getScenarioProperties(rootScenarioName);
		String key = fullUUID + "." + RunningProperties.IS_DISABLED;
		String val = allProps.get(key) == null ? "" : allProps.get(key)
				.toString();
		return "true".equals(val);
	}

	/**
	 * Mark test as disabled\enabled
	 * 
	 * @param fullUUID
	 *            the Test full unique ID
	 * @param rootScenarioName
	 *            the root name
	 * @param disabled
	 *            if True then mark as disabled, otherwise mark as enabled
	 * @param onlyCache
	 *            if True then change will not be saved to the file system
	 */
	public static void setTestDisabled(String fullUUID,
			String rootScenarioName, boolean disabled, boolean onlyCache) {
		Properties allProps = getScenarioProperties(rootScenarioName);
		String key = fullUUID + "." + RunningProperties.IS_DISABLED;
		allProps.put(key, "" + disabled);
		try {
			saveScenarioPropertiesToSrcAndClass(allProps, rootScenarioName,
					onlyCache);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Set a test property in the scenario properties file
	 * 
	 * @param fullUUID
	 *            the Test full unique ID
	 * @param rootScenarioName
	 *            the root name
	 * @param property
	 *            the property name
	 * @param value
	 *            the property value
	 * @param onlyCache
	 *            if True then change will not be saved to the file system
	 */
	public static void setTestProperty(String fullUUID,
			String rootScenarioName, String property, String value,
			boolean onlyCache) {
		Properties allProps = getScenarioProperties(rootScenarioName);

		String key = StringUtils.isEmpty(fullUUID) ? property : fullUUID + "."
				+ property;
		allProps.put(key, value);
		try {
			saveScenarioPropertiesToSrcAndClass(allProps, rootScenarioName,
					onlyCache);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set test inner properties - no parameters!
	 * 
	 * @param fullUUID
	 * @param rootScenarioName
	 * @param p
	 * @param onlyCache
	 */
	public static void setTestInnerProperty(String fullUUID,
			String rootScenarioName, Properties p, boolean onlyCache) {
		Properties allProps = getScenarioProperties(rootScenarioName);

		for (Object o : p.keySet()) {
			String key = StringUtils.isEmpty(fullUUID) ? o.toString()
					: fullUUID + "." + o;
			setProperty(allProps, key, p.getProperty(o.toString()));
		}

		try {
			saveScenarioPropertiesToSrcAndClass(allProps, rootScenarioName,
					onlyCache);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Remove empty values before saving
	 * 
	 * @param p
	 * @param key
	 * @param value
	 */
	private static void setProperty(Properties p, String key, String value) {
		if (StringUtils.isEmpty(value)) {
			p.remove(key);
		} else {
			p.setProperty(key, value);
		}
	}

	/**
	 * Get a test property from the scenario properties file
	 * 
	 * @param fullUUID
	 *            the Test full unique ID
	 * @param rootScenarioName
	 *            the root name
	 * @param property
	 *            the property name
	 * @return the property value or null if doesn't exist
	 */
	public static String getTestProperty(String fullUUID,
			String rootScenarioName, String property) {
		Properties allProps = getScenarioProperties(rootScenarioName);
		String key = StringUtils.isEmpty(fullUUID) ? property : fullUUID + "."
				+ property;
		return (String) allProps.get(key);
	}

	private static Set<String> gatherFullUUID(Scenario s) throws Exception {
		final HashSet<String> ret = new HashSet<String>();
		ret.add(s.getFullUUID());
		class TestUUIDCollector implements JTestContainerVisitor {
			@Override
			public void visitScenarioElement(JTest t1) throws Exception {
				String uuid = null;
				// ITAI: This is a disgusting hacking used for flow controls.
				// Without it the gathering of the uuid's would be wrong
				// When cleaning redundant parameters and the parameters
				// Would be deleted from the properties files.
				if (t1 instanceof AntFlowControl) {
					uuid = ((AntFlowControl) t1).getFlowFullUUID();
				} else {
					uuid = t1.getFullUUID();

				}
				ret.add(uuid);
			};
		}
		TestUUIDCollector uuidCollector = new TestUUIDCollector();
		ScenarioHelpers.iterateContainer(s, uuidCollector);
		return ret;
	}

	/**
	 * Clean old properties from scenario properties file
	 * 
	 * @param s
	 *            The scenario to clean entries from
	 * @throws Exception
	 */
	public static void cleanScenarioPropertiesFromRedundantEntries(Scenario s)
			throws Exception {
		Properties props = getScenarioProperties(s.getName(), true);
		Properties propsClone = (Properties) props.clone();
		String[] idsSet = gatherFullUUID(s).toArray(new String[0]);
		Enumeration<Object> keyEnum = propsClone.keys();
		// ITAI: Added this flag to spare some redundant IO operation.
		boolean changed = false;
		while (keyEnum.hasMoreElements()) {
			String key = keyEnum.nextElement().toString();
			if (key.startsWith("jsystem.")) { // inner properties
				continue;
			}
			boolean toRemove = true;
			for (String id : idsSet) {
				if (StringUtils.isEmpty(id)) {
					continue;
				}
				if (key.indexOf(".") < 0 || removePropertyName(key).equals(id)) {
					toRemove = false;
					break;
				}
			}
			if (toRemove) {
				props.remove(key);
				changed = true;
			}
		}
		if (changed && s.canWrite()) {
			saveScenarioPropertiesToSrcAndClass(props, s.getName(), false);
		}
	}

	/**
	 * Returns the full UUID of the property key
	 * 
	 * @param properyKey
	 * @return
	 */
	private static String removePropertyName(String properyKey) {
		if (properyKey.contains(".jsystem.")) {
			return properyKey.substring(0, properyKey.indexOf(".jsystem."));
		} else {
			Pattern pattern = Pattern
					.compile("([\\d\\w\\-\\.]+\\-[\\d\\w]+)\\.");
			Matcher matcher = pattern.matcher(properyKey);
			if (matcher.find()) {
				return matcher.group(1);
			} else {
				return properyKey;
			}
		}
	}

	public static String buildFullPathName(JTest test) {
		if (test == null) {
			return "";
		}
		Scenario s = test.getMyScenario();
		ArrayList<String> list = new ArrayList<String>();
		while (s != null) {
			list.add("." + s.getName());
			JTest t = s.getParent();
			if (t != null) {
				s = t.getMyScenario();
			} else {
				s = null;
			}
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = list.size() - 1; i >= 0; i--) {
			buffer.append(list.get(i));
		}
		String result = buffer.toString();
		return result;
	}

	/**
	 * Mark current scenario as dirty
	 */
	public static void setDirtyFlag() {
		ScenariosManager.setDirty();
	}

	/**
	 * 
	 */
	private static String normalizeScenarioName(String name) {
		if (StringUtils.isEmpty(name)) {
			return name;
		}
		name = FileUtils.replaceSeparator(name);
		if (name.startsWith("/", 0)) {
			name = name.substring(1);
		}
		return name;
	}

	/**
	 * Builds a String with test's full hierarchical name.
	 */
	public static String getTestHierarchyInPresentableFormat(JTest test) {
		StringBuffer buffer = new StringBuffer();
		getTestHierarchyInPresentableFormat(test, buffer);
		return buffer.toString();
	}

	/**
	 * Utility method for getTestHierarchyInPresentableFormat(JTest test)
	 */
	private static void getTestHierarchyInPresentableFormat(JTest test,
			StringBuffer sb) {
		if (test.getParent() == null) {
			sb.append(test.getTestName());
			return;
		}
		getTestHierarchyInPresentableFormat(test.getParent(), sb);
		sb.append("-->").append(test.getTestName());
	}

	/**
	 * Get the RunnerTest matching given test
	 * 
	 * @param test
	 *            the test to match
	 * @return a RunnerTest object
	 */
	public static RunnerTest getRunnerTest(Test test) {
		RunnerTest runnerTest;
		if (test instanceof ScenarioAsTest) {
			runnerTest = ((ScenarioAsTest) test).getCurrentRunnerTest();
		} else {
			runnerTest = ScenariosManager.getInstance().getCurrentScenario()
					.findRunnerTest(test);
		}
		return runnerTest;
	}

	/**
	 * Raise the scenarioAsTestStart flag if not already raised
	 * 
	 * @param scenarioAsTestUUID
	 *            Scenario unique id
	 */
	public static boolean signalScenarioAsTestStart(boolean isScenarioAsTest,
			String scenarioAsTestUUID) {
		try {
			String value = RunProperties.getInstance().getRunProperty(
					RunningProperties.SCENARIO_AS_TEST_START);
			if (value == null
					|| (value != null && !scenarioAsTestUUID.startsWith(value))) { // second
																					// condition
																					// is
																					// for
																					// edge
																					// case
																					// in
																					// which
																					// flag
																					// was
																					// not
																					// deleted!
				if (isScenarioAsTest) {
					RunProperties.getInstance().setRunProperty(
							RunningProperties.SCENARIO_AS_TEST_START,
							scenarioAsTestUUID);
					return true;
				}
				if (value != null) {
					removeScenarioAsTestFlag();
				}
				return false;
			}
			return false;
		} catch (Exception e1) {
			log.warning("failed updating scenario as test flag");
			return true;
		}
	}

	/**
	 * Remove scenario as test flag
	 * 
	 * @param isScenarioAsTest
	 *            flag value to mark
	 */
	public static void removeScenarioAsTestFlag() {
		try {
			RunProperties.getInstance().removeRunProperty(
					RunningProperties.SCENARIO_AS_TEST_START);
			removeFailFlag();
		} catch (Exception e1) {
			log.warning("failed updating scenario as test flag");
		}
	}

	/**
	 * A test is skipped if any parent scenario is marked as test, a failure
	 * occurred and the terminate flag is true
	 * 
	 * @return True if test should be skipped
	 */
	public static boolean shouldTestBeSkipped() {
		return isFailFlagUp() && shouldTerminateOnFail()
				&& insideScenarioAsTest();
	}

	private static boolean shouldTerminateOnFail() {
		String isScenarioTerminateOnFail = JSystemProperties.getInstance()
				.getPreference(
						FrameworkOptions.SCEANRIO_AS_TEST_TERMINATE_ON_FAIL);
		if (null == isScenarioTerminateOnFail) {
			isScenarioTerminateOnFail = FrameworkOptions.SCEANRIO_AS_TEST_TERMINATE_ON_FAIL
					.getDefaultValue().toString();
		}
		return "true".equalsIgnoreCase(isScenarioTerminateOnFail);
	}

	private static boolean lastTestFailed() {
		return ListenerstManager.getInstance().getLastTestFailed();
	}

	public static void updateFailFlag() {
		if (lastTestFailed() && insideScenarioAsTest()) {
			try {
				RunProperties.getInstance().setRunProperty(
						RunningProperties.SCENARIO_AS_TEST_FAILURE, "true");
			} catch (IOException e) {
				log.warning("failed updating scenario as test FAIL flag");
			}
		}
	}

	private static boolean isFailFlagUp() {
		try {
			return RunProperties.getInstance().getRunProperty(
					RunningProperties.SCENARIO_AS_TEST_FAILURE) != null;
		} catch (IOException e) {
			log.warning("failed checking scenario as test FAIL flag");
			return false;
		}
	}

	private static void removeFailFlag() {
		try {
			RunProperties.getInstance().removeRunProperty(
					RunningProperties.SCENARIO_AS_TEST_FAILURE);
		} catch (IOException e) {
			log.warning("failed updating scenario as test FAIL flag");
		}
	}

	private static boolean insideScenarioAsTest() {
		try {
			return RunProperties.getInstance().getRunProperty(
					RunningProperties.SCENARIO_AS_TEST_START) != null;
		} catch (IOException e) {
			log.fine("failed checking scenario as test flag");
			return false;
		}
	}

	/**
	 * Check if the abort flag was raised for current scenario
	 * 
	 * @return True if the flag is "true"
	 */
	public static boolean wasCurrentScenarioSignaledToAbort() {
		try {
			return "true".equals(RunProperties.getInstance().getRunProperty(
					RunningProperties.ABORT_CURRENT_SCENARIO_EXECUTION));
		} catch (IOException e) {
			log.fine("Problem checking scenario as test flag");
			return false;
		}
	}

	/**
	 * Signal if the current scenario should abort
	 * 
	 * @param abort
	 *            True will signal the scenario should abort
	 */
	public static void setScenarioAbortFlag(String uuid, boolean abort) {
		try {
			if (abort) {
				RunProperties.getInstance().setRunProperty(
						RunningProperties.ABORT_CURRENT_SCENARIO_EXECUTION,
						uuid);
			} else {
				if (uuid.equals(RunProperties.getInstance().getRunProperty(
						RunningProperties.ABORT_CURRENT_SCENARIO_EXECUTION))) {
					RunProperties.getInstance().setRunProperty(
							RunningProperties.ABORT_CURRENT_SCENARIO_EXECUTION,
							"false");
				}
			}
		} catch (IOException e) {
			log.fine("Problem checking scenario as test flag");
		}
	}

	/**
	 * Signal the current scenario should abort
	 */
	public static void abortCurrentScenario() {
		try {
			RunProperties.getInstance().setRunProperty(
					RunningProperties.ABORT_CURRENT_SCENARIO_EXECUTION, "true");
			ListenerstManager.getInstance().report(
					"Scenario abort flag was raised");
		} catch (IOException e) {
			log.fine("Problem checking scenario as test flag");
		}
	}

	/**
	 * Copy parameters values from Scenario XML to Scenario Properties file
	 * 
	 * @param scenario
	 *            The scenario to copy parameters value for
	 * @return True if values were moved from XML to Properties file
	 * @throws Exception
	 */
	public static boolean moveParamsFromXmlToPropFile(Scenario scenario)
			throws Exception {
		File scenarioXml = scenario.getScenarioFile();
		String scenarioName = scenario.getName();
		String propFile = scenarioXml.getAbsolutePath().replace(".xml",
				".properties");

		if (!(FileUtils.exists(scenarioXml.getAbsolutePath()))) { // No scenario
																	// file
																	// (should
																	// not
																	// happen)
			return false;
		}
		Document doc;
		DocumentBuilder db = XmlUtils.getDocumentBuilder();
		FileInputStream fis = new FileInputStream(scenarioXml);
		Element root;
		try {
			doc = db.parse(fis);
			root = doc.getDocumentElement();
		} catch (Exception e) {
			log.log(Level.SEVERE, "fail to load scenario from file "
					+ scenarioXml, e);
			throw e;
		} finally {
			fis.close();
		}

		// from 5.6 there is no need for this, since all values are saved to
		// properties file only
		if (!isUnder5_6(root)) {
			return false;
		}

		boolean werePropMovedFromXmlTpPropFile = false;
		Properties oldProp = new Properties();
		if (FileUtils.exists(propFile)) {
			oldProp = FileUtils.loadPropertiesFromFile(propFile);
		}

		ArrayList<Element> tergetsList = XmlUtils.getElementsByTag(
				RunningProperties.ANT_TARGET, root);
		// For all tests
		for (int i = 0; i < tergetsList.size(); i++) {
			Element target = tergetsList.get(i);
			if (XmlUtils.getElementsByTag("test", target).size() > 0) {

				ArrayList<Element> sysproperties = XmlUtils.getElementsByTag(
						"sysproperty", target);
				ArrayList<Element> propeties = XmlUtils.getElementsByTag(
						"property", target);

				// for all tests sysproperty
				Properties testsXmlProp = new Properties();
				String uuid = new String();
				for (int j = 0; j < sysproperties.size(); j++) {

					Element sysproperty = sysproperties.get(j);
					if (sysproperty.getAttribute("key").equals(
							RunningProperties.UUID_TAG)) {
						uuid = sysproperty.getAttribute("value");
					} else {
						if (sysproperty.getAttribute("key").startsWith(
								RunningProperties.PARAM_PREFIX)) {
							testsXmlProp
									.put(sysproperty
											.getAttribute("key")
											.replace(
													RunningProperties.PARAM_PREFIX,
													""), sysproperty
											.getAttribute("value"));
						}
					}
				}
				if (!StringUtils.isEmpty(uuid)) {
					for (Object key : testsXmlProp.keySet()) {
						if (!(oldProp.containsKey(uuid + "." + key))) {
							oldProp.put(uuid + "." + key, testsXmlProp.get(key));
							werePropMovedFromXmlTpPropFile = true;
						}
					}
				}

				// Special treatment for isDisabled flag which is in the XML
				// file
				for (int j = 0; j < propeties.size(); j++) {
					Element property = propeties.get(j);
					if ((property.getAttribute("name").endsWith("isdisabled"))
							&& (property.getAttribute("value").equals("true"))) {
						oldProp.put(uuid + "." + RunningProperties.IS_DISABLED,
								"true");
						werePropMovedFromXmlTpPropFile = true;
					}
				}
			}
		}
		if (werePropMovedFromXmlTpPropFile) {
			saveScenarioPropertiesToSrcAndClass(oldProp, scenarioName, false);
		}
		return werePropMovedFromXmlTpPropFile;
	}

	static boolean isUnder5_6(Element root) throws Exception {
		// if(UpgradeAndBackwardCompatibility.getScenarioVersion(root).getValue()
		// <= Version.JSystem5_6.getValue()){
		return true;
		// }
		// return false;
	}
}
