/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MethodNotSupportedException;

import jsystem.framework.scenario.UpgradeAndBackwardCompatibility.AntElement;
import jsystem.framework.scenario.UpgradeAndBackwardCompatibility.Version;
import jsystem.framework.scenario.flow_control.AntDataDriven;
import jsystem.framework.scenario.flow_control.AntFlowControl;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.framework.scenario.flow_control.AntIfCondition;
import jsystem.framework.scenario.flow_control.AntIfElse;
import jsystem.framework.scenario.flow_control.AntIfElseIf;
import jsystem.framework.scenario.flow_control.AntSwitch;
import jsystem.framework.scenario.flow_control.AntSwitchCase;
import jsystem.framework.scenario.flow_control.AntSwitchDefault;
import jsystem.framework.scenario.flow_control.FlowControlHelpers;
import jsystem.utils.IntegerWrapper;
import jsystem.utils.StringUtils;
import junit.framework.NamedTest;
import junit.framework.SystemTest;
import junit.framework.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A JTestContainer represents a container of JTests
 * 
 */
public abstract class JTestContainer implements JTest {
	protected static Logger log = Logger.getLogger(JTestContainer.class.getName());

	private String name;
	protected Vector<JTest> rootTests = new Vector<JTest>();
	protected Vector<JTest> allTests = new Vector<JTest>();
	private JTestContainer parent = null;
	private String documentation;
	private String comment;
	private String meaningfulName = null;

	private PresentationDefinitions sort = null;
	private String testId = null;

	protected boolean markedAsKnownIssue;

	protected boolean markedAsNegativeTest;

	protected String uuid = "";

	/**
	 * A HashMap of all tests in current container (including containers)and
	 * it's Unique ID
	 */
	protected HashMap<String, JTest> testsHash;
	/**
	 * A HashMap of all containers in current container and it's Unique ID
	 */
	protected HashMap<String, JTestContainer> containersHash;
	protected Version loadVersion;
	protected ArrayList<ValidationError> validationErrors = new ArrayList<ValidationError>();
	private boolean isHiddenInHTML;

	public JTestContainer() {
		this(null, null, null, null);
	}

	public JTestContainer(String name, JTestContainer parent, String id, String uuid) {
		log.fine("JTest container : " + name);
		setParent(parent);
		if (!(this instanceof Scenario)) {
			setName(name);
		}
		setTestId(id);
		setUUID(uuid);
		testsHash = new HashMap<String, JTest>();
		containersHash = new HashMap<String, JTestContainer>();
	}

	/**
	 * 
	 */
	public void createTestsFromElement(Element xmlDefinition, HashMap<String, JTestContainer> targetAndParent,
			HashMap<String, Integer> targetAndPlace) {

		// Find the right XML element to work on
		xmlDefinition = getContainerElement(xmlDefinition);

		// Create the root tests
		NodeList children = xmlDefinition.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (!(node instanceof Element)) {
				continue;
			}
			Element element = (Element) node;
			String tag = element.getTagName();

			if (tag.equals(UpgradeAndBackwardCompatibility.getEntityForVersion(AntElement.antCall, loadVersion))) {
				String targetName = element.getAttribute(RunningProperties.ANT_TARGET);
				boolean isFixture = targetName.startsWith("f");
				if (!isFixture) {
					targetAndParent.put(targetName, this);

					// We must put a dummy test into place in order to keep the
					// right order
					// of tests
					RunnerTest tempTest = new RunnerTest("TEMP", "TEMP");
					targetAndPlace.put(targetName, rootTests.size());
					rootTests.add(tempTest);
				}
			} else {
				JTestContainer newContainer = null;
				if (tag.equals(AntForLoop.OLD_XML_TAG) || tag.equals(AntForLoop.XML_TAG)) {
					newContainer = AntForLoop.fromElement(this, element);
				} else if (tag.equals(AntIfCondition.XML_TAG)) {
					newContainer = AntIfCondition.fromElement(this, element);
				}else if (tag.equals(AntDataDriven.XML_TAG)) {
					newContainer = AntDataDriven.fromElement(this, element);
				}else if (tag.equals(AntIfElse.XML_TAG)) {
					newContainer = AntIfElse.fromElement(this, element);
				} else if (tag.equals(AntIfElseIf.XML_TAG)) {
					newContainer = AntIfElseIf.fromElement(this, element);
				} else if (tag.equals(AntSwitch.OLD_XML_TAG) || tag.equals(AntSwitch.XML_TAG)) {
					newContainer = AntSwitch.fromElement(this, element);
				} else if (tag.equals(AntSwitchCase.XML_TAG)) {
					newContainer = AntSwitchCase.fromElement(this, element);
				} else if (tag.equals(AntSwitchDefault.XML_TAG)) {
					newContainer = AntSwitchDefault.fromElement(this, element);
				}
				if (newContainer != null) { // common for all JContainers
					newContainer.loadVersion = loadVersion; // load version is
															// passed on for
															// upgrade and
															// backward
															// compatibility
					if (newContainer instanceof AntFlowControl) {
						((AntFlowControl) newContainer).loadUuidAndParameters(element);
					}
					newContainer.createTestsFromElement(element, targetAndParent, targetAndPlace);
					rootTests.add(newContainer);
				}
			}
		}
	}

	public Vector<JTest> cloneRootTests(JTestContainer newParent) throws Exception {
		Vector<JTest> clonedTests = new Vector<JTest>();
		for (JTest subTest : getRootTests()) {
			JTest clonedTest = subTest.cloneTest();
			clonedTest.setParent(newParent);
			clonedTests.add(clonedTest);
		}

		return clonedTests;
	}

	/**********************************************************************************************
	 * 
	 * Ant (XML) transformation methods
	 * 
	 * ********************************************************************************************/
	public static String getRandomUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * TODO: verify the right "update" will be executed !
	 */
	public void update() throws Exception {
		update(false);
	}

	/**
	 * updates scenario and write the selection to a file and save it to the
	 * test classes dir and to the test src dir.
	 * 
	 */
	public void update(boolean recursive) throws Exception {
		updateAllTests();
		if (recursive) {
			for (int i = 0; i < rootTests.size(); i++) {
				JTest t = (JTest) rootTests.elementAt(i);
				if (t instanceof JTestContainer) {
					((JTestContainer) t).update(recursive);
				}
			}
		}
	}

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
	 * Triggers loading of parameters in all scenario's children
	 * 
	 */
	public void loadParametersAndValues() {
		for (int i = 0; i < rootTests.size(); i++) {
			if ((rootTests.elementAt(i) instanceof RunnerTest)) {
				((RunnerTest) rootTests.elementAt(i)).loadParametersAndValues();
			}
		}
	}

	/**********************************************************************************************
	 * 
	 * Scenario model update and querying methods
	 * 
	 * ********************************************************************************************/
	public int getRootIndex(JTest test) {
		if (test == null) {
			return -1;
		}
		for (int i = 0; i < rootTests.size(); i++) {
			if (test.equals(rootTests.elementAt(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * gets the index of the test in all scenario's offsprings, Fixtures are
	 * ommited from offsprings count. -1 if test is not found. Test is
	 * identified according to it's handler in the jvm's memory.
	 */
	public int getGeneralIndex(JTest o) {
		/**
		 * removing Fixtures from allTests
		 */
		Vector<JTest> noFixtures = new Vector<JTest>();
		for (int i = 0; i < allTests.size(); i++) {
			if (!(allTests.elementAt(i) instanceof RunnerFixture)) {
				noFixtures.add(allTests.elementAt(i));
			}
		}

		for (int i = 0; i < noFixtures.size(); i++) {
			if (o == noFixtures.elementAt(i)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets the index of the test in all scenario's offsprings. If
	 * filterFixtures is set to true, fixtures are filtered from general tests
	 * list.<br>
	 * -1 if test is not found.<br>
	 * Test is identified according to it's handler in the jvm's memory.<br>
	 * 
	 */
	public int getGeneralIndex(Test o, boolean filterFixtures) {
		Vector<RunnerTest> testsToCount = new Vector<RunnerTest>();
		for (int i = 0; i < allTests.size(); i++) {
			if (!(allTests.elementAt(i) instanceof RunnerFixture) || !filterFixtures) {
				testsToCount.add((RunnerTest) allTests.elementAt(i));
			}
		}
		for (int i = 0; i < testsToCount.size(); i++) {
			if (o == testsToCount.elementAt(i).getTest()) {
				return i;
			}
		}
		return -1;
	}

	public int findTestIndexInAll(JTest test) {
		if (test == null) {
			return -1;
		}
		for (int i = 0; i < allTests.size(); i++) {
			if (test.equals(allTests.elementAt(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the test as should be presented in the scenario tree
	 * (and as appears in our static reports files)
	 */
	public int getPresentationIndex(JTest o) {
		return getGeneralIndex(o) + 1;
	}

	/**
	 */
	public int getMaxTestIndex() {
		return rootTests.size() - 1;
	}

	public void addTest(JTest test) throws Exception {
		addTest(test, -2);
	}

	public void addTest(JTest test, int index) throws Exception {
		addTest(test, index, true);
	}

	/**
	 * Add test to scenario at a specific index
	 * 
	 * @param test
	 *            test to add
	 * @param index
	 *            the index to add in the tree (-1 is root)
	 * @throws Exception
	 */
	public void addTest(JTest test, int index, boolean load) throws Exception {
		// if test has no unique id , create one - solves Excel issue...
		if (StringUtils.isEmpty(test.getUUID())) {
			test.setUUID(getRandomUUID());
		}
		test.setParent(this);
		if (load)
			test.load();
		if (test instanceof RunnerTest) {
			((RunnerTest) test).loadParametersAndValues();
		}

		/**
		 * remove the previous Fixture if exists and add the new one.
		 */
		if (test instanceof RunnerFixture) {
			setFixtureNode((RunnerFixture) test);
			ScenarioHelpers.setDirtyFlag();
		} else {
			if (index < -1) {
				rootTests.addElement(test);
			} else {
				if (hasFixture() && (index == 0 || index == -1)) {
					index = rootTests.size() > 1 ? index + 1 : 0; // If there
																	// are no
																	// tests -
																	// handle
																	// special
																	// case
				}
				rootTests.add(index + 1, test);

			}
			if (hasFixture()) {
				test.setFixture(getFixtureName());
			}
			ScenarioHelpers.setDirtyFlag();
		}

	}

	/**
	 * Remove a test from the scenario.
	 * 
	 * @param test
	 *            the test index.
	 * @throws Exception
	 */
	public void removeTest(JTest test) throws Exception {
		if (test == null) {
			log.log(Level.WARNING, "Test to remove is null, inside container: " + this.getName());
			return;
		}

		// several JTest types cannot be deleted directly, only their parent...
		if ((test instanceof AntIfElse) || (test instanceof AntSwitchDefault)) {
			// TODO: maybe add a pop-up message
			log.log(Level.WARNING, "Test " + test.getTestName() + " cannot be removed directly");
			return;
		}

		int index = getRootIndex(test);
		if (index == -1) {
			log.log(Level.WARNING, "Test " + test.getTestName() + " wasn't found in container " + this.getName());
			return;
		}
		rootTests.remove(index);
		String uuid = (test instanceof AntFlowControl) ? ((AntFlowControl) test).getFlowFullUUID() : test.getFullUUID();
		ScenarioHelpers.removePropertiesFromScenarioProps(ScenarioHelpers.getRoot(this).getName(), uuid, true);
		/**
		 * if test is RunnerFixture, load all the scenario again to update the
		 * fixture property for each test in the scenario.
		 */
		if (test instanceof RunnerFixture) {
			setFixture("");
			ScenarioHelpers.setDirtyFlag();
		} else {
			ScenarioHelpers.setDirtyFlag();
		}
	}

	public boolean hasFixture() {
		if (rootTests.size() == 0) {
			return false;
		}
		return rootTests.get(0) instanceof RunnerFixture;
	}

	/**
	 * Removes all tests in the container from the scenario
	 * 
	 * @param container
	 *            the tests container
	 * @throws Exception
	 *             public void removeTest(TestsContainer container) throws
	 *             Exception { while (container.hasMore()) {
	 *             removeTest(container.getNext()); } }
	 */

	/**
	 * Move a test up
	 * 
	 * @param test
	 *            the test to move index
	 * @throws Exception
	 * @return true if manged to move
	 */
	public boolean moveUp(JTest test, boolean updatePersistenc) throws Exception {
		int index = getRootIndex(test);
		if (index == 0 || index == -1) {
			return false;
		}
		if (hasFixture() && index == 1) {
			return false;
		}
		JTest toMove = rootTests.elementAt(index);
		rootTests.setElementAt(rootTests.elementAt(index - 1), index);
		rootTests.setElementAt(toMove, index - 1);

		if (updatePersistenc) {
			ScenarioHelpers.setDirtyFlag();
		}
		updateAllTests();

		return true;
	}

	public boolean moveUp(JTest test) throws Exception {
		return moveUp(test, true);
	}

	/**
	 * Move a multi-test up
	 * 
	 * @param container
	 *            the container with the tests
	 * @throws Exception
	 * 
	 */
	public void moveUp(TestsContainer container) throws Exception {
		// first the top one then the rest
		if (container.hasMore() && moveUp(container.getNext()))
			while (container.hasMore()) {
				moveUp(container.getNext());
			}
	}

	/**
	 * check if a container can move up
	 * 
	 * @param container
	 *            the container of tests
	 * @return true if possible to move up
	 */
	public boolean canMoveUp(TestsContainer container) {
		JTest[] tests = container.getTests();
		for (JTest test : tests) {
			JTestContainer testParent = test.getParent();
			if (testParent == null) { // root scenario is in the container
				return false;
			}
			if (!testParent.canMoveUp(test)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * check if single JTest can move up
	 * 
	 * @param test
	 * @return true if the test can be move up
	 */
	public boolean canMoveUp(JTest test) {
		int index = getRootIndex(test);
		if (index == -1 || index == 0) {
			return false;
		}
		if (index == 1 && hasFixture()) {
			return false;
		}
		return true;
	}

	public boolean moveToTop(JTest test, boolean updatePersistency) throws Exception {
		int index = getRootIndex(test);
		if (index == 0 || index == -1 || (index == 1 && hasFixture())) {
			return false;
		}

		JTest toMove = rootTests.elementAt(index);
		rootTests.remove(index);
		rootTests.insertElementAt(toMove, 0);

		if (updatePersistency) {
			ScenarioHelpers.setDirtyFlag();
		}
		updateAllTests();

		return true;
	}

	public boolean moveToTop(JTest test) throws Exception {
		return moveToTop(test, true);
	}

	public void moveToTop(TestsContainer container) throws Exception {
		// first the bottom one then the rest
		if (container.hasMore() && moveToTop(container.getLast()))
			while (container.hasMore()) {
				moveToTop(container.getLast());
			}
		updateAllTests();
	}

	public boolean canMoveToTop(TestsContainer container) {
		JTest[] tests = container.getTests();
		for (JTest test : tests) {
			JTestContainer testParent = test.getParent();
			if (testParent == null || !testParent.canMoveToTop(test)) { // root
																		// scenario
																		// is in
																		// the
																		// container
				return false;
			}
		}
		return true;
	}

	public boolean canMoveToTop(JTest test) {
		int index = getRootIndex(test);
		if (index == -1 || index == 0 || (index == 1 && hasFixture())) {
			return false;
		}
		return true;
	}

	public boolean moveToBottom(JTest test, boolean updatePersistency) throws Exception {

		int index = getRootIndex(test);
		if (index == -1 || index == (rootTests.size() - 1) || (index == 0 && hasFixture())) {
			return false;
		}

		JTest toMove = rootTests.elementAt(index);
		rootTests.remove(index);
		rootTests.add(toMove);

		if (updatePersistency) {
			ScenarioHelpers.setDirtyFlag();
		}
		updateAllTests();

		return true;
	}

	public boolean moveToBottom(JTest test) throws Exception {
		return moveToBottom(test, true);
	}

	public void moveToBottom(TestsContainer container) throws Exception {
		// first the bottom one then the rest
		if (container.hasMore() && moveToBottom(container.getLast()))
			while (container.hasMore()) {
				moveToBottom(container.getLast());
			}
		updateAllTests();
	}

	public boolean canMoveToBottom(TestsContainer container) {
		JTest[] tests = container.getTests();
		for (JTest test : tests) {
			JTestContainer testParent = test.getParent();
			if (testParent == null || !testParent.canMoveToBottom(test)) { // root
																			// scenario
																			// is
																			// in
																			// the
																			// container
				return false;
			}
		}
		return true;
	}

	public boolean canMoveToBottom(JTest test) {
		int index = getRootIndex(test);
		if (index == -1 || index == (rootTests.size() - 1) || (index == 0 && hasFixture())) {
			return false;
		}
		return true;
	}

	/**
	 * Move test down
	 * 
	 * @param test
	 *            the test to move index
	 * @throws Exception
	 * @return true if manged to move
	 */
	public boolean moveDown(JTest test, boolean updatePersistency) throws Exception {

		int index = getRootIndex(test);
		if (index == -1 || index == (rootTests.size() - 1)) {
			return false;
		}
		if (index == 0 && hasFixture()) {
			return false;
		}
		JTest toMove = rootTests.elementAt(index);
		rootTests.setElementAt(rootTests.elementAt(index + 1), index);
		rootTests.setElementAt(toMove, index + 1);

		if (updatePersistency) {
			ScenarioHelpers.setDirtyFlag();
		}
		updateAllTests();

		return true;
	}

	/**
	 * Move test down
	 * 
	 * @param test
	 *            the test to move index
	 * @throws Exception
	 * @return true if manged to move
	 */
	public boolean moveDown(JTest test) throws Exception {
		return moveDown(test, true);
	}

	/**
	 * Move a multi-test down
	 * 
	 * @param container
	 *            the container with the tests
	 * @throws Exception
	 * 
	 */
	public void moveDown(TestsContainer container) throws Exception {
		// first the bottom one then the rest
		if (container.hasMore() && moveDown(container.getLast()))
			while (container.hasMore()) {
				moveDown(container.getLast());
			}
		updateAllTests();
	}

	/**
	 * check if a container can move down
	 * 
	 * @param container
	 *            the container of tests
	 * @return true if possible to move down
	 */
	public boolean canMoveDown(TestsContainer container) {
		JTest[] tests = container.getTests();
		for (JTest test : tests) {
			JTestContainer testParent = test.getParent();
			if (testParent == null) { // root scenario is in the container
				return false;
			}
			if (!testParent.canMoveDown(test)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * check if single JTest can move down.
	 * 
	 * @param test
	 * @return true if the test can be moved down
	 */
	public boolean canMoveDown(JTest test) {
		int index = getRootIndex(test);
		if (index == -1 || index == (rootTests.size() - 1)) {
			return false;
		}
		if (index == 0 && hasFixture()) {
			return false;
		}
		return true;
	}

	/**
	 * checks if all tests in container are in the first path
	 * 
	 * @param container
	 *            container to check
	 * @return true if all the tests in the container are in the first path (-1
	 *         is the root or not in the first path) TODO: recursion is "COOL"
	 *         but hard to debug. Should be changed ?
	 */
	public boolean isNodeInMainPath(TestsContainer container) {
		container.initRun();
		JTest test = container.getNext();
		int ind = getRootIndex(test);
		if (ind == -1) {
			return false;
		}
		container.initRun();
		return true;
	}

	public boolean isNodeInMainPath(JTest test) {
		int ind = getRootIndex(test);
		if (ind == -1) {
			return false;
		}
		return true;

	}

	/**
	 * updating all tests indexes
	 * 
	 */
	public void updateAllTests() {
		allTests = new Vector<JTest>();
		testsHash = new HashMap<String, JTest>();
		containersHash = new HashMap<String, JTestContainer>();
		for (int i = 0; i < rootTests.size(); i++) {
			JTest test = rootTests.elementAt(i);
			String uuid = test.getFullUUID();
			if (test instanceof JTestContainer) {
				if (test instanceof AntFlowControl) {
					uuid = ((AntFlowControl) test).getFlowFullUUID();
				}
				if (!StringUtils.isEmpty(uuid)) {
					containersHash.put(uuid, (JTestContainer) test);
				}

				testsHash.put(uuid, test);

				// update sub-scenarios tests numbers
				((JTestContainer) test).updateAllTests();
				Vector<JTest> v = ((JTestContainer) test).getTests();
				allTests.addAll(v);
				for (JTest t : v) {
					RunnerTest rt = (RunnerTest) t;
					uuid = rt.getFullUUID();
					testsHash.put(uuid, rt);
				}

				List<Scenario> allScenarios = ScenarioHelpers.getAllScenarios((JTestContainer) test);
				for (Scenario t : allScenarios) {
					uuid = t.getFullUUID();
					testsHash.put(uuid, t);
				}
				List<AntFlowControl> allFlowControls = FlowControlHelpers.getAllFlowControls((JTestContainer) test);
				for (AntFlowControl t : allFlowControls) {
					uuid = t.getFlowFullUUID();
					testsHash.put(uuid, t);
				}				
			} else if (test instanceof RunnerFixture) {
				RunnerTest rt = (RunnerFixture) test;
				testsHash.put(uuid, rt);
				allTests.addElement(rt);
			} else {
				RunnerTest rt = (RunnerTest) test;
				testsHash.put(uuid, rt);
				allTests.addElement(rt);
			}
		}
	}

	public boolean exists(RunnerTest test) {
		return (allTests.indexOf(test) >= 0);
	}

	public boolean isRootTest(JTest test) {
		for (int i = 0; i < rootTests.size(); i++) {
			if (rootTests.elementAt(i).equals(test)) {
				return true;
			}
		}
		return false;
	}

	public int[] getEnabledTestsIndexes() {
		Vector<Integer> integerVector = new Vector<Integer>();
		for (int testIndex = 0; testIndex < allTests.size(); testIndex++) {
			RunnerTest test = (RunnerTest) allTests.get(testIndex);
			if (!test.isDisable() && !(test instanceof RunnerFixture)) {
				integerVector.add(Integer.valueOf(testIndex));
			}
		}
		int[] tindexes = new int[integerVector.size()];
		for (int i = 0; i < integerVector.size(); i++) {
			tindexes[i] = integerVector.elementAt(i).intValue();
		}
		return tindexes;
	}

	public void setEnabledTestsIndexes(int[] indices) {
		updateAllTests();
		setDisable(true);
		for (int index : indices) {
			if (index >= allTests.size()) {
				throw new RuntimeException("Text index (" + index + ") is bigger then number of tests in scenario("
						+ allTests.size() + ")");
			}
			allTests.get(index).setDisable(false);
		}
	}

	public Vector<JTest> getTests() {
		return allTests;
	}

	public Vector<JTest> getRootTests() {
		return rootTests;
	}

	public void cleanAll() throws Exception {
		rootTests = new Vector<JTest>();
		allTests = new Vector<JTest>();
		testsHash = new HashMap<String, JTest>();
		containersHash = new HashMap<String, JTestContainer>();
		ScenarioHelpers.setDirtyFlag();
	}

	public RunnerTest getTest(int index) {
		if (index < 0 || index >= allTests.size()) {
			return null;
		}
		return (RunnerTest) allTests.elementAt(index);
	}

	/**
	 * get a RunnerTest object identified by a full Id
	 * 
	 * @param fullUUID
	 *            the unique id of the runnerTest
	 * @return the found RunnerTest or null if doesn't exist
	 */
	public RunnerTest getRunnerTestByFullId(String fullUUID) {

		return (RunnerTest) testsHash.get(fullUUID);
	}

	/**
	 * get a JTest object identified by a full Id
	 * 
	 * @param fullUUID
	 *            the unique id of the runnerTest
	 * @return the found JTest or null if doesn't exist
	 */
	public JTest getTestByFullId(String fullUUID) {

		return testsHash.get(fullUUID);
	}

	/**
	 * get a JTestContainer object identified by a full Id
	 * 
	 * @param fullUUID
	 *            the unique id of the JTestContainer
	 * @return the found JTestContainer or null if doesn't exist
	 */
	public JTestContainer getContainerByFullId(String fullUUID) {
		return containersHash.get(fullUUID);
	}

	public JTest getTestFromRoot(int index) {
		if (index >= rootTests.size() || index < 0) {
			return null;
		}
		return (JTest) rootTests.elementAt(index);
	}

	protected void setFixtureNode(RunnerFixture test) throws Exception {
		removePreviousFixture();
		setFixture(test.getClassName());
		rootTests.add(0, test);
	}

	/**
	 * Sets the fixture which is associated with this scenario's tests. Please
	 * note, that this method doesn't add fixure entity to the scenario. In
	 * order to add/set the fixure entity of this scenario, please use the
	 * addTest method and add a test of type fixture. This method is used by the
	 * addTest in order to actually set the fixture which is associated with the
	 * tests. It also sets the fixtures of the tests of the sub scenarios.
	 */
	public void setFixture(String fixtureClassName) throws Exception {
		if (hasFixture()) {
			return;
		}
		for (int i = 0; i < rootTests.size(); i++) {
			JTest test = rootTests.get(i);
			test.setFixture(fixtureClassName);
		}
	}

	public RunnerTest findTestInformation(Test test) {
		for (int i = 0; i < allTests.size(); i++) {
			RunnerTest t = (RunnerTest) allTests.elementAt(i);
			if (t.getTest().equals(test)) {
				return t;
			}
		}
		return null;
	}

	public int countTestCases() {
		return allTests.size();
	}

	public String toString() {
		return name;
	}

	public JTestContainer getParent() {
		return parent;
	}

	public void setParent(JTestContainer parent) {
		this.parent = parent;
	}

	/*
	 * Add all of the names of the test containers reqursively into a given
	 * vector
	 */
	public void getSubContainersNames(Vector<String> v) {
		for (int i = 0; i < rootTests.size(); i++) {
			JTest test = rootTests.elementAt(i);
			if (test instanceof JTestContainer) {
				JTestContainer container = (JTestContainer) test;
				v.addElement(container.getName());
				container.getSubContainersNames(v);
			}
		}
	}

	public boolean isRunning() {
		for (int i = 0; i < rootTests.size(); i++) {
			if (rootTests.elementAt(i).isRunning()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * check that all the test run's and they all pass. note : the order is
	 * first checking if theres isError (Exception) next if theres isFail and
	 * then warning and only at the end check isSuccess.
	 */

	public boolean isSuccess() {
		boolean testRan = false;
		for (int i = 0; i < rootTests.size(); i++) { // if a test ran and did
														// not pass
			if (!rootTests.elementAt(i).isNotRunning()) {
				testRan = true;
				if (!rootTests.elementAt(i).isSuccess()) {
					return false;
				}
			}
		}
		if (rootTests.size() == 0 || !testRan) {
			return false;
		}
		return true;

	}

	/**
	 * check if there's at least one test with warning status
	 */
	public boolean isWarning() {
		for (int i = 0; i < rootTests.size(); i++) {
			if (rootTests.elementAt(i).isWarning()) {
				return true;
			}
		}
		return false;
	}

	public boolean isDisable() {
		return false;
	}

	public boolean isError() {
		for (int i = 0; i < rootTests.size(); i++) {
			if (rootTests.elementAt(i).isError()) {
				return true;
			}
		}
		return false;
	}

	public boolean isFail() {
		for (int i = 0; i < rootTests.size(); i++) {
			if (rootTests.elementAt(i).isFail()) {
				return true;
			}
		}
		return false;
	}

	public void setStatusNotRunning() {
		for (int i = 0; i < rootTests.size(); i++) {
			Object obj = rootTests.elementAt(i);
			if (obj instanceof JTestContainer) {
				((JTestContainer) obj).setStatusNotRunning();
			} else {
				((RunnerTest) obj).setStatus(RunnerTest.STAT_NOT_RUN);
				((RunnerTest) obj).initFlags();
			}
		}
	}

	/**
	 * Finds the instance of the specified test in the container.
	 * 
	 * @param test
	 *            JSystem junit3 runner test or JUnit4 test info or JUnit test.
	 * @return RunnerTest if found. null if not.
	 */
	public RunnerTest findRunnerTest(Test test) {
		for (int i = 0; i < allTests.size(); i++) {
			if (allTests.elementAt(i) instanceof RunnerTest) {
				RunnerTest rt = (RunnerTest) allTests.elementAt(i);
				if (test instanceof SystemTest) {
					//ITAI: There are cases in which the test is without uuid. 
					//This can happen if we failed to initialize it. It would be
					//Handled later on in the flow so don't worry about it.
					if (((SystemTest) test).getFullUUID() == null
							|| ((SystemTest) test).getFullUUID().equals(rt.getFullUUID())) {
						return rt;
					}
				}
				// Support for JUnit4 tests
				else if (test instanceof NamedTest) {
					if (((NamedTest) test).getFullUUID().equals(rt.getFullUUID())) {
						return rt;
					}
				} else {
					if (test.equals(rt.getTest())) {
						return rt;
					}
				}
			}
		}
		// This happens when we execute runScenario.bat on junit4 tests.
		log.warning("Failed to find runner test for test " + test.getClass().getSimpleName() + " in jcontainter "
				+ this.getClass().getSimpleName());
		return null;
	}

	/**
	 * return Vector of enabled test (check box checked tests) on scenario
	 * 
	 * @return Vector of enabled RunnerTest
	 */
	public Vector<JTest> getEnabledTests() {
		Vector<JTest> enabledTests = new Vector<JTest>();

		for (Iterator<JTest> iter = rootTests.iterator(); iter.hasNext();) {
			JTest currentTest = iter.next();
			if (!currentTest.isDisable()) {
				if (currentTest instanceof JTestContainer) {
					enabledTests.addAll(((JTestContainer) currentTest).getEnabledTests());
				} else if (!(currentTest instanceof RunnerFixture)) {
					enabledTests.add(currentTest);
				}
			}
		}
		return enabledTests;
	}

	/**
	 * Count Fixtures until a given test
	 * 
	 * @param currentTest
	 *            - the test to stop on
	 * @param wrapper
	 *            - saves the result
	 */
	public void getFixtureAmountTillTest(JTest currentTest, IntegerWrapper wrapper) {

		for (Iterator<JTest> iter = allTests.iterator(); iter.hasNext() && wrapper.allowTowrite;) {
			JTest test = iter.next();

			if (currentTest == test) {
				return;
			}

			if (test instanceof RunnerFixture) {
				wrapper.value++;
			}
		}
	}

	public void setParameters(Parameter[] params) {
		setParameters(params, false);
	}

	public void setParameters(Parameter[] params, boolean recursively) {
		Vector<JTest> tests = recursively ? allTests : rootTests;
		for (int i = 0; i < tests.size(); i++) {
			if (tests.elementAt(i) instanceof Scenario) {
				continue;
			}
			JTest test = tests.elementAt(i);
			test.loadParametersAndValues();
			test.setParameters(params, false);
		}
	}

	public Parameter[] getParameters() {
		return getParameters(false);
	}

	/**
	 * filter known Parameters from all Parameters list and return the filtered
	 * array
	 */
	public Parameter[] getVisibleParamters() {
		return getVisibleParamters(false);
	}

	/**
	 * filter known Parameters from all Parameters list and return the filtered
	 * array
	 */
	public Parameter[] getVisibleParamters(boolean recursively) {
		return getParameters(true, recursively);
	}

	protected Parameter[] getParameters(boolean doFilter) {
		return getParameters(doFilter, false);
	}

	/**
	 * get all tests parameters
	 * 
	 * @param doFilter
	 *            get only visible parameters
	 * @param recursive
	 *            if True will gather parameters from allTests, otherwise from
	 *            rootTests
	 * @return
	 */
	protected Parameter[] getParameters(boolean doFilter, boolean recursive) {
		HashMap<String, Parameter> params = new HashMap<String, Parameter>();
		Vector<JTest> tests = recursive ? allTests : rootTests;

		for (int i = 0; i < tests.size(); i++) {
			Parameter[] tparams;
			JTest jt = (JTest) tests.elementAt(i);
			if (jt instanceof Scenario) {// scenario has no parameters
				continue;
			} else if (jt instanceof AntFlowControl) { // view flow control
				tparams = ((AntFlowControl) jt).getTestParameters(doFilter, recursive);
			} else { // RunnerTest
				if (doFilter) {
					tparams = jt.getVisibleParamters(false);
				} else {
					tparams = jt.getParameters();
				}
			}

			for (int j = 0; j < tparams.length; j++) {
				params.put(tparams[j].getName(), tparams[j]);
				// tparams[j].resetDirty();
			}
		}
		return (Parameter[]) params.values().toArray(new Parameter[0]);
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public void setTestComment(String comment) {
		this.comment = comment;
	}

	public void setDisable(boolean disable) {
		for (int i = 0; i < rootTests.size(); i++) {
			((JTest) rootTests.elementAt(i)).setDisable(disable);
		}
	}

	public String getComment() {
		return comment;
	}

	public PresentationDefinitions getPresentationObject() {
		if (sort == null) {
			sort = new PresentationDefinitions();
		}
		return sort;
	}

	public void setPresentationObject(PresentationDefinitions sort) {
		this.sort = sort;

	}

	public void setTestId(String testId) {
		this.testId = testId;

	}

	public String getTestId() {
		return testId;
	}

	public String getFullTestId() {
		return getParent().getFullTestId() + "/" + getTestId();
	}

	public String getName() {
		return name;
	}

	private void removePreviousFixture() throws Exception {
		if (!rootTests.isEmpty() && hasFixture()) {
			removeTest(rootTests.get(0));
		}
	}

	private String getFixtureName() {
		if (hasFixture()) {
			return ((RunnerFixture) rootTests.get(0)).getClassName();
		}
		return null;
	}

	public String getUUID() {
		return uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public String getFullUUID() {
		String parentFullUuid = getParentFullUUID();
		String fullTmp = StringUtils.isEmpty(parentFullUuid) ? uuid : parentFullUuid + "." + uuid;
		return fullTmp;
	}

	public String getUUIDUpTo(JTest toStopAt) {
		if (this == toStopAt || isRoot()) {
			return "";
		}
		String parentFullUuid = getParent().getUUIDUpTo(toStopAt);
		String fullTmp = StringUtils.isEmpty(parentFullUuid) ? uuid : parentFullUuid + "." + uuid;
		return fullTmp;
	}

	public String getParentFullUUID() {
		if (isRoot()) {
			return "";
		}
		return getParent().getFullUUID();
	}

	/**
	 * checks if current scenario is the root scenario
	 * 
	 * @return True if this scenario has no parent
	 */
	public boolean isRoot() {
		return getParent() == null;
	}

	public boolean isNotRunning() {
		return !isRunning();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void getSubScenariosNames(Vector<String> v) {
		for (int i = 0; i < rootTests.size(); i++) {
			JTest test = (JTest) rootTests.elementAt(i);
			if (test instanceof JTestContainer) {
				JTestContainer sen = (JTestContainer) test;
				v.addElement(sen.getName());
				sen.getSubScenariosNames(v);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.scenario.JTest#getTestName() TODO: get meaningful
	 * name from the flow control type ?
	 */
	@Override
	public String getTestName() {
		if (!StringUtils.isEmpty(getMeaningfulName())) {
			return getMeaningfulName();
		}
		return getName() + " - " + getComment();
	}

	public Element addExecutorXml(Element targetScenario, Document doc) {
		Element containerElement = doc.createElement(getXmlContainerTag());

		/*
		 * Create and add the container content
		 */
		// go over all tests append a target that invokes them
		// to the ant script and add an antcall to the test in the execute
		// scenario target
		for (JTest jtest : rootTests) {
			jtest.addExecutorXml(containerElement, doc);
		}

		targetScenario.appendChild(containerElement);
		return containerElement;
	}

	public Element getContainerElement(Element xmlDefinition) {
		return xmlDefinition;
	}

	/*
	 * TODO: find a nicer solution OO-wise
	 */
	public abstract String getXmlContainerTag();

	public void setDistributedExecutionParameters(DistributedExecutionParameter[] parameters) throws Exception {
		throw new MethodNotSupportedException("method Should not be called for JTestContainer");
	}

	public DistributedExecutionParameter[] getDistributedExecutionParameters() throws Exception {
		return getParent().getDistributedExecutionParameters();
	}

	public Properties getPropertiesInAntCanonicalFormat() throws Exception {
		Properties result = new Properties();
		Iterator<JTest> iter = allTests.iterator();
		while (iter.hasNext()) {
			JTest t = iter.next();
			result.putAll(t.getPropertiesInAntCanonicalFormat());
		}
		return result;
	}

	@Override
	public void addAntPropertiesToTarget(Element ant, Document doc) {
		// Basic container should not add properties...
	}

	@Override
	public Scenario getMyScenario() {
		return getParent().getMyScenario();
	}

	@Override
	public Scenario getParentScenario() {
		return getParent().getParentScenario();
	}

	/**
	 * check if there are mapped tests inside flow control elements in this
	 * container (recursively)
	 * 
	 * @return True if a flow container with a mapped test inside is found
	 */
	public boolean containsMappedFlowControlElements() {
		for (JTest test : rootTests) {
			if (test instanceof JTestContainer) {
				JTestContainer container = (JTestContainer) test;
				if (container instanceof AntFlowControl && container.getEnabledTests().size() > 0) {
					return true;
				}
				return container.containsMappedFlowControlElements();
			}
		}
		return false;
	}

	public Scenario getRoot() {
		JTest toReturn = this;
		while (toReturn.getParent() != null) {
			toReturn = toReturn.getParent();
		}
		return (Scenario) toReturn;
	}

	public boolean isMarkedAsKnownIssue() {
		return false;
	}

	public boolean isMarkedAsNegativeTest() {
		return false;
	}

	public void hideInHTML(boolean isHideInHTML) {
		this.isHiddenInHTML = isHideInHTML;

		for (JTest test : rootTests) {
			test.hideInHTML(isHideInHTML);
		}
	}

	public boolean isHiddenInHTML() {
		return isHiddenInHTML;
	}

	public void markAsKnownIssue(boolean markedAsKnownIssue) {
		this.markedAsKnownIssue = markedAsKnownIssue;
	}

	public void markAsNegativeTest(boolean isNegativeTest) {
		this.markedAsNegativeTest = isNegativeTest;
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

	public void collectValidationErrors(ArrayList<ValidationError> errors) {
		for (JTest test : rootTests) {
			if (test instanceof JTestContainer) {
				// if the container contain errors will not collect from his
				// childs
				if (test.isValidationErrorsFound()) {
					errors.addAll(test.getValidationErrors());
				} else {
					// collect all the child errors
					((JTestContainer) test).collectValidationErrors(errors);
				}
			} else {
				if (test.isValidationErrorsFound()) {
					errors.addAll(test.getValidationErrors());
				}
			}
		}
	}

	public void setMeaningfulName(String meaningfulName, boolean saveToPropFile) {
		this.meaningfulName = meaningfulName;
	}

	public String getMeaningfulName() {
		return meaningfulName;
	}

	public void setLoadVersion(Version loadVersion) {
		this.loadVersion = loadVersion;
	}

	// For future needs
	public void resetToDefault() throws Exception {
	}

}
