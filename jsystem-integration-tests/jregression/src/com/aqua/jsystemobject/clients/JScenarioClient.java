package com.aqua.jsystemobject.clients;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

import jsystem.framework.FrameworkOptions;
import jsystem.utils.FileUtils;
import jsystem.utils.XmlUtils;

import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.ScenarioNodes;
import utils.ScenarioUtils;

import com.aqua.jsystemobject.XPathAttribEquals;
import com.aqua.jsystemobject.XPathNumberOfElements;
import com.aqua.jsystemobject.handlers.JServerHandlers;

/**
 * this system object is responsible for communicating with remote handler of type
 * JScenarioHandler, to check the functionality related to scenarios
 * @author Dan
 *
 */
public class JScenarioClient extends BaseClient {
	
	public JScenarioClient() {
		super();
		handler = JServerHandlers.SCENARIO;
	}
	
	String getHandlerName() {
		return handler.getHandlerClassName();
	}
	
	/**
	 * Mark the root scenario as edit local only through XMLRPC.
	 * @throws Exception
	 */
	public void markScenarioAsEditLocalOnly() throws Exception {
		
		callHandleXml("Mark scenario as edit local only.", "markScenarioAsEditLocalOnly", new Vector<Object>());
	}
	
	/**
	 * Check if scenario is marked as edit only locally through XMLRPC.
	 */
	public Boolean isScenarioMarkedAsEditOnlyLocally() throws Exception {
		// TODO Auto-generated method stub
		return (Boolean)callHandleXml("Check if scenario is marked as edit only locally.", "isScenarioMarkedAsEditOnlyLocally");
	}

	public Boolean isSubScenarioFieldsAreEditable(int scenarioIndexOneBased) throws Exception {
		return (Boolean)callHandleXml("Check if the test's fields in the inner Scenario are editable, scenario index: " + scenarioIndexOneBased ,
				"isSubScenarioFieldsAreEditable", scenarioIndexOneBased);
	}
	
	public Boolean isSubSubScenarioFieldsAreEditable(int scenarioIndexZeroBased, int innerScenarioIndexZeroBased) throws Exception {
		
		return (Boolean)callHandleXml("Check if the test's fields of the scenario in depth = 3 in the tree are editable, scenario index: " + scenarioIndexZeroBased 
				+ "inner scenario index: " + innerScenarioIndexZeroBased,
				"isSubSubScenarioFieldsAreEditable", scenarioIndexZeroBased, innerScenarioIndexZeroBased);
	}
	
	/**
	 * 
	 * @param scenarioName
	 * @param isExist
	 * @return
	 * @throws Exception
	 */
	public boolean checkScenarioExist(String scenarioName, boolean isExist) throws Exception {

		String pathToScenarioXml = getJSystemProperty(FrameworkOptions.TESTS_CLASS_FOLDER) + File.separator + "scenarios" + File.separator
				+ scenarioName + ".xml";
		File f1 = new File(pathToScenarioXml);
		if (!isExist) {
			return !f1.exists();
		}
		return f1.exists();
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String getJSystemProperty(FrameworkOptions key) throws Exception {
		return (String) callHandleXml("get jsystem property: " + key, "getJSystemProperty", key);

	}
	
	/**
	 * 
	 * @param scenarioName
	 * @return
	 * @throws Exception
	 */
	public boolean checkScenarioExist(String scenarioName) throws Exception {
		return checkScenarioExist(scenarioName, true);
	}
	
	/**
	 * this function unmaps all test in scenario tree
	 * @return 0 if succeeds
	 * @throws Exception
	 */
	public int unmapAll() throws Exception{
		return (Integer)callHandleXml("unmapAll", "unmapAll");
	}
	
	/**
	 * this function maps all test in scenario tree
	 * @return 0 if succeeds
	 * @throws Exception
	 */
	public int mapAll() throws Exception{
		return (Integer)callHandleXml("mapAll", "mapAll");
	}
	
	/**
	 * will map a test in index testIdx.
	 * @param testIdx
	 * @return
	 * @throws Exception
	 */
	public int mapTest(int testIdx) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(testIdx);
		return (Integer)callHandleXml("mapTest: test index-" + testIdx, "mapTest", v);

	}

	/**
	 * unmaps a test at index testIdx.
	 * @param testIdx
	 * @return
	 * @throws Exception
	 */
	public int unmapTest(int testIdx) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(testIdx);
		return (Integer)callHandleXml("unmapTest: test index-" + testIdx, "unmapTest", v);
	}
	
	/**
	 * will map a scenario at index scenarioIdx.
	 * will assume all tests should be recursively be marked under
	 * the scenario. if otherwise, use the more explicit version of mapScenario.
	 * @param scenarioIdx
	 * @return
	 * @throws Exception
	 */
	public int mapScenario(int scenarioIdx)throws Exception{
		return mapScenario(scenarioIdx, false);
	}
	
	/**
	 * will map a scenario at index scenarioIdx. if rootOnly is true, 
	 * will only map direct children of that scenario, else, all children recursively, 
	 * like useing mapScenario(scnearioIdx);
	 * @param scenarioIdx
	 * @param rootOnly
	 * @return
	 * @throws Exception
	 */
	public int mapScenario(int scenarioIdx, boolean rootOnly)throws Exception{
		Vector<Object> v = new Vector<Object>();
		v.addElement(scenarioIdx);
		v.addElement(true);//isScenario == true
		v.addElement(rootOnly);
		return (Integer)callHandleXml("mapTest: test index-" + scenarioIdx, "mapTest", v);
	}
	
	/**
	 * will unmap a scenario at index scenarioIdx.
	 * will assume a recursive unmap is required on all children.
	 * @param scenarioIdx
	 * @return
	 * @throws Exception
	 */
	public int unmapScenario(int scenarioIdx)throws Exception{
		return unmapScenario(scenarioIdx, false);
	}

	/**
	 * will unmap a scenario at index scenarioIdx.
	 * if rootOnly is true, will only unmap the direct children of the scenario
	 * otherwise, will recursively unmap all children.
	 * @param scenarioIdx
	 * @return
	 * @throws Exception
	 */
	public int unmapScenario(int scenarioIdx, boolean rootOnly)throws Exception{
		Vector<Object> v = new Vector<Object>();
		v.addElement(scenarioIdx);
		v.addElement(true);//isScenario == true
		v.addElement(rootOnly);
		return (Integer)callHandleXml("unmapTest: test index-" + scenarioIdx, "unmapTest", v);
	}
	
	/**
	 * deletes a specific test according to it's index in scenarioTree
	 * @param testIndex
	 * @throws Exception
	 */
	public void deleteTest(int testIndex) throws Exception {
		callHandleXml("delete test index: " + testIndex, "deleteTest", testIndex);
	}

	/**
	 * deletes a test from scenarioTree according to it's name
	 * @param testName
	 * @throws Exception
	 */
	public void deleteTest(String testName) throws Exception {
		callHandleXml("delete test : " + testName, "deleteTest", testName);
	}
	
	/**
	 * calls the move test up method in jscenario handler
	 * @param testIndex
	 * @throws Exception
	 */
	public void moveTestUp(int testIndex) throws Exception {
		callHandleXml("move up, test index: " + testIndex, "moveTestUp", testIndex);
	}
	
	/**
	 * call moveTestUpByMenuOption on handler
	 * @param testIndex
	 * @throws Exception
	 */
	public void moveTestUpByMenuOption(int testIndex) throws Exception {
		callHandleXml("move up by menu option, test index: " + testIndex, "moveTestUpByMenuOption", testIndex);
	}
	
	/**
	 * call movetestdown on handler
	 * @param testIndex
	 * @throws Exception
	 */
	public void moveTestDown(int testIndex) throws Exception {
		callHandleXml("move down, test index: " + testIndex, "moveTestDown", testIndex);
	}
	
	/**
	 * call the movetestdownbymenuoption in handler
	 * @param testIndex
	 * @throws Exception
	 */
	public void moveTestDownByMenuOption(int testIndex) throws Exception {
		callHandleXml("move down by menu option, test index: " + testIndex, "jsystem.moveTestDownByMenuOption", testIndex);
	}
	
	/**
	 * call the restoreScenarioParametersToDefault on JScenarioHandler
	 * @throws Exception
	 */
	public void restoreScenarioParametersToDefault() throws Exception {
		callHandleXml("restore scenario parameters to default", "restoreScenarioParametersToDefault");
	}
	
	public void addTest(String methodName, String className, int amount) throws Exception {
		callHandleXml("add test: " + methodName + "." + className, "addTest", methodName,className,amount);
		Thread.sleep(500);
	}
	
	public void addTest(String methodName, String className) throws Exception {
		addTest(methodName, className, 1);
	}
	
	/**
	 * removes all tests in scenarioTree
	 * @throws Exception
	 */
	public void deleteAllTestsFromScenarioTree() throws Exception{
		callHandleXml("delete all tests from scenario tree", "deleteAllTestsFromScenarioTree");
	}
	
	/**
	 * returns the current scenario in scenarioTree
	 * @return
	 * @throws Exception
	 */
	public String getCurrentScenario() throws Exception{
		return (String)callHandleXml("get current scenario from scenario tree", "getCurrentScenario");
	}
	
	/**
	 * cleans current scenario by pressing the clearCurrentScenaio button while standing
	 * on the first path in scenario tree;
	 * @throws Exception
	 * @deprecated	use ScenarioUtils.createAndCleanScenario(JScenarioClient scenarioClient,String scenarioName)
	 */
	public void cleanCurrentScenario() throws Exception {
		callHandleXml("clean current scenario", "cleanCurrentScenario");
		Thread.sleep(500);
	}
	
	/**
	 * 
	 * @param scenarioName
	 * @throws Exception
	 */
	public void selectScenario(String scenarioName) throws Exception {
		callHandleXml("select scenario: " + scenarioName, "selectScenario", scenarioName);
		Thread.sleep(500);
	}
	
	/**
	 * 
	 * @param name
	 * @throws Exception
	 */
	public void createScenario(String name) throws Exception {
		callHandleXml("create scenario: " + name, "createScenario", name);
	}
	
	
	/**
	 * returns the number of currently selected check boxes
	 * in the tests tree.
	 * @return numOfselectedTests
	 * @throws Exception 
	 */
	public int getNumOfchkBoxChekced() throws Exception{
		return (Integer)callHandleXml("get the number of checkboxes checked in tests tree", "getNumOfchkBoxChekced");
	}
	
	/**
	 * Get the mapped tests in current scenario .
	 * 
	 * @return String. Example : int array {1,2,3} will be returned as String
	 *         "1,2,3"
	 * @throws Exception
	 */
	public String getMappedTestsInScenario() throws Exception {
		return (String)callHandleXml("get string representation of tests checked in scenario tree","getMappedTestsInScenario");
	}
	
	/**
	 * 
	 * @return int
	 * @throws Exception
	 */
	public int getNumOfMappedTestsInScenario() throws Exception {
		return (Integer)callHandleXml("get the number of tests checked in scenario tree","getNumOfMappedTestsInScenario");
	}
	
	/**
	 * This method gets a scenario name and returns the number of tests in this
	 * scenario including tests of its sub-scenarios from the scenario xml
	 * 
	 * @param scenarioName
	 * @return
	 * @throws Exception
	 */
	public int numOfTestsInScenario(String scenarioName) throws Exception {
		int numOfTest = 0;
		String pathToScenarioXml = getJSystemProperty(FrameworkOptions.TESTS_CLASS_FOLDER) + File.separator + "scenarios" + File.separator
				+ scenarioName + ".xml";
		// File scenarioFile = new File(pathToScenarioXml);
		Document doc = XmlUtils.parseFile(pathToScenarioXml);
		Node node = doc.getDocumentElement();
		// write all child nodes recursively
		NodeList children = node.getChildNodes();
		String[] childName = new String[children.getLength()];
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			childName[i] = child.getNodeName();
			if (childName[i].equals(ScenarioNodes.TARGET.getName())) {
				NodeList targetChildren = child.getChildNodes();
				for (int j = 0; j < targetChildren.getLength(); j++) {
					String targetChildName = targetChildren.item(j).getNodeName();
					if (targetChildName.equals(ScenarioNodes.JSYSTEM.getName())) {
						NodeList jsystemChildren = targetChildren.item(j).getChildNodes();
						for (int k = 0; k < jsystemChildren.getLength(); k++) {
							String jsystemChildName = jsystemChildren.item(k).getNodeName();

							if (jsystemChildName.equals(ScenarioNodes.TEST.getName())) {
								// NamedNodeMap antAttributesSpecial =
								// jsystemChildren.item(k).getAttributes();
								// report.report("test name = " +
								// antAttributesSpecial.item(0).getNodeValue());
								numOfTest = numOfTest + 1;
							}
						} // for k
					}
					if (targetChildName.equals(ScenarioNodes.ANT.getName())) {
						NamedNodeMap antAttributes = targetChildren.item(j).getAttributes();
						for (int k = 0; k < antAttributes.getLength(); k++) {
							if (antAttributes.item(k).getNodeName().equals(ScenarioNodes.ANTFILE.getName())) {
								String scenarioPath = antAttributes.item(k).getNodeValue();
								numOfTest = numOfTest + numOfTestsInScenario(ScenarioUtils.findScenarioInPath(scenarioPath));
							}
						} // for k
					}
				} // for j
			}
		} // for i

		return numOfTest;
	}
	
	/**
	 * This method gets a scenario name and returns the number of sub-scenarios
	 * in this scenario only in first level (not including sub-sub-scenarios)
	 * 
	 * @param scenarioName
	 * @return
	 * @throws Exception
	 */
	public int numOfSubScenariosInScenario(String scenarioName) throws Exception {
		int numOfSubScenarios = 0;
		String pathToScenarioXml = getJSystemProperty(FrameworkOptions.TESTS_CLASS_FOLDER) + File.separator + "scenarios" + File.separator
				+ scenarioName + ".xml";
		// File scenarioFile = new File(pathToScenarioXml);
		Document doc = XmlUtils.parseFile(pathToScenarioXml);
		Node node = doc.getDocumentElement();
		// write all child nodes recursively
		NodeList children = node.getChildNodes();
		String[] childName = new String[children.getLength()];
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			childName[i] = child.getNodeName();
			if (childName[i].equals(ScenarioNodes.TARGET.getName())) {
				NodeList targetChildren = child.getChildNodes();
				for (int j = 0; j < targetChildren.getLength(); j++) {
					String targetChildName = targetChildren.item(j).getNodeName();
					if (targetChildName.equals(ScenarioNodes.ANT.getName())) {
						numOfSubScenarios = numOfSubScenarios + 1;
					}
				} // for j
			}
		} // for i

		return numOfSubScenarios;
	}
	
	
	/**
	 * calls to copy scenario in scenario handler.
	 * @param newScenarioName
	 * @throws Exception
	 */
	public void copyScenario(String newScenarioName) throws Exception {
		callHandleXml("copy scenario", "copyScenario", newScenarioName);
	}
	
	/**
	 * This will return the number of tests in the current scenario including tests in sub scenarios
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getNumOfTestsInScenario() throws Exception{
		return (Integer)callHandleXml("get the number of tests in the scenario tree", "getNumOfTestsInScenario");
	}
	
	public void setTestParameter(int testIndex, String tab, String paramName, String value, boolean isCombo) throws Exception {
		callHandleXml("setTestParameter, test index: " + testIndex + " tab: " + tab + ", param name: " + paramName + ", value: " + value
				+ " ,isCombo" + isCombo, "setTestParameter", testIndex, tab, paramName, value, isCombo);
	}

	public void setTestParameter(int testIndex, String tab, String paramName, String value, boolean isCombo, boolean isScenario)
			throws Exception {
		callHandleXml("setTestParameter, test index: " + testIndex + " tab: " + tab + ", param name: " + paramName + ", value: " + value
				+ " ,isCombo" + isCombo, "setTestParameter", testIndex, tab, paramName, value, isCombo, isScenario);
	}

	public void setTestParameter(int testIndex, String tab, String paramName, String value, boolean isCombo, boolean isScenario,
			boolean approve) throws Exception {
		callHandleXml("setTestParameter, test index: " + testIndex + " tab: " + tab + ", param name: " + paramName + ", value: " + value
				+ " ,isCombo" + isCombo, "setTestParameter", testIndex, tab, paramName, value, isCombo, isScenario, approve);
	}
	public Boolean addLoopObject() throws Exception {
		return (Boolean)callHandleXml("Add Loop object to the Scenario", "addLoopObject");
	}
	public Boolean addIfObject() throws Exception {
		return (Boolean)callHandleXml("Add If object to the Scenario", "addIfObject");
	}
	public Boolean addElseIfObject() throws Exception {
		return (Boolean)callHandleXml("Add Else-If object to the Scenario", "addElseIfObject");
	}
	public Boolean addSwitchObject() throws Exception {
		return (Boolean)callHandleXml("Add Switch object to the Scenario", "addSwitchObject");
	}
	public Boolean addCaseObject() throws Exception {
		return (Boolean)callHandleXml("Add Case object to the Scenario", "addCaseObject");
	}
	public Integer selectTest(int testIndex) throws Exception {
		return (Integer)callHandleXml("Select test", "selectTest", testIndex);
	}
	public Integer collapseExpandScenario(int testIndex) throws Exception {
		return (Integer)callHandleXml("Expand test", "collapseExpandScenario", testIndex);
	}
	
	public void checkTestPass(int testIndex) throws Exception {
		checkReportValue(testIndex, "status", "true");
	}
	public void checkAssertionFailure(int testIndex, String cause) throws Exception {
		checkReportValue(testIndex, "status", "false");
		// Note: XML includes the newline and the prefix
		checkTestFailCause(testIndex, "Fail: " + cause + "\n");
	}
	public void checkTestFailCause(int testIndex, String cause) throws Exception {
		checkReportValue(testIndex, "failCause", cause);
	}
	public void checkReportValue(int testIndex, String attibute, String value) throws Exception {
		File reportXml = new File(getReportDir());
		if (!reportXml.exists()) {
			report.report("checkReportValue file, reporter xml file: " + reportXml.getAbsolutePath() + ", wasn't found", false);
			throw new FileNotFoundException(reportXml.getAbsolutePath());
		}
		setTestAgainstObject(FileUtils.readDocumentFromFile(reportXml));
		analyze(new XPathAttribEquals("/reports/test[" + testIndex + "]", attibute, value));
	}
	/**
	 * get path for report dir
	 */
	public String getReportDir() throws Exception {
		String dir = (String) callHandleXml("get report.0.xml path", "getReportDir");
		return dir;
	}
	
	public void checkNumberOfTestExecuted(int numberOfTests) throws Exception {
		File reportXml = new File(getReportDir());
		if (!reportXml.exists()) {
			report.report("checkNumberOfTestExecuted file, reporter xml file: " + reportXml.getAbsolutePath() + ", wasn't found", false);
			throw new FileNotFoundException(reportXml.getAbsolutePath());
		}
		setTestAgainstObject(FileUtils.readDocumentFromFile(reportXml));
		analyze(new XPathNumberOfElements("/reports/test", numberOfTests));
	}
	
	/**
	 * call select scenario on handler
	 * @param scenarioName
	 * @throws Exception
	 */
	public void selectSenario(String scenarioName) throws Exception {
		callHandleXml("select scenario: " + scenarioName, "selectSenario", scenarioName);
	}
}
