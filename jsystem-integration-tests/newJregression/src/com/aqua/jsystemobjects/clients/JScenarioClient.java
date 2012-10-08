package com.aqua.jsystemobjects.clients;

import java.util.Vector;

import org.jsystem.objects.xmlrpc.XmlRpcHelper;

import com.aqua.analyzers.StringCompareAnalyzer;
import com.aqua.jsystemobjects.handlers.JScenarioHandler;
import com.aqua.utils.ScenarioModelUtils;

public class JScenarioClient extends BaseClient {

	public JScenarioClient(XmlRpcHelper connectionHandler) {
		super(connectionHandler);
	}

	public void scenarioRedo() throws Exception {
		handleCommand("scenario redo", "scenarioRedo");
		Thread.sleep(500);
	}

	public void scenarioUndo() throws Exception {
		handleCommand("scenario undo", "scenarioUndo");
		Thread.sleep(500);
	}

	public void scenarioNavigateForward() throws Exception {
		handleCommand("scenario navigate forward", "scenarioNavigateForward");
		Thread.sleep(500);
	}

	public void scenarioNavigateBackward() throws Exception {
		handleCommand("scenario navigate backward", "scenarioNavigateBackward");
		Thread.sleep(500);
	}

	/**
	 * will open the given scenarioName, delete it and recreate it
	 * 
	 * @param scenarioName
	 * @throws Exception
	 */
	public void cleanScenario(String scenarioName) throws Exception {
		openScenario(scenarioName);
		deleteCurrentScenario();
		createScenario(scenarioName);
		report.report("Scenario-" + scenarioName + " was cleared");
	}

	/**
	 * call select scenario on remote runner.
	 */
	public void openScenario(String scenarioName) throws Exception {
		handleCommand("select scenario: " + scenarioName, "openScenario", scenarioName);
		Thread.sleep(500);
	}

	/**
	 * delete the scenario will remove the remote current scenario in line 0,
	 * and will restore the default scenario with any tests that are under it.
	 */
	public void deleteCurrentScenario() throws Exception {
		handleCommand("clean current scenario", "deleteCurrentScenario");
		Thread.sleep(500);
	}

	public void checkCurrentScenarioIsMatched(String scenarioToMatchWith) throws Exception {
		setTestAgainstObject(getCurrentRootScenarioName());
		analyze(new StringCompareAnalyzer(ScenarioModelUtils.SCENARIO_HEADER + scenarioToMatchWith));
	}

	public String getCurrentRootScenarioName() throws Exception {
		return (String) handleCommand("get current root scenario name", "getCurrentRootScenarioName");
	}

	/**
	 * create a scenario on the remote runner.
	 * 
	 * @param name
	 * @throws Exception
	 */
	public void createScenario(String name) throws Exception {
		handleCommand("create scenario: " + name, "createScenario", name);
	}

	/**
	 * removes all tests in scenarioTree
	 * 
	 * @throws Exception
	 */
	public void deleteAllTestsFromScenarioTree() throws Exception {
		handleCommand("delete all tests from scenario tree", "deleteAllTestsFromScenarioTree");
	}

	/**
	 * adds a test to scenario tree in the remote runner with a given test class
	 * and method and number of times to add it.
	 * 
	 * @param methodName
	 * @param className
	 * @param amount
	 * @throws Exception
	 */
	public void addTest(String methodName, String className, int amount) throws Exception {
		handleCommand("add test: " + methodName + "." + className, "addTest", methodName, className, amount);
		Thread.sleep(500);
	}

	/**
	 * will add a test with the given class and method name 1 time to the remote
	 * runner.
	 * 
	 * @param methodName
	 * @param className
	 * @throws Exception
	 */
	public void addTest(String methodName, String className) throws Exception {
		addTest(methodName, className, 1);
	}

	@Override
	protected String getHandlerName() {
		return JScenarioHandler.class.getSimpleName();
	}

	/**
	 * select a test in given row
	 * 
	 * @param row
	 *            thw test row (0 is root)
	 * @throws Exception
	 */
	public void selectTestRow(int row) throws Exception {
		handleCommand("select tests rows", "selectTestRow", row);
	}

	/**
	 * this function unmaps all test in scenario tree
	 * 
	 * @return 0 if succeeds
	 * @throws Exception
	 */
	public int unmapAll() throws Exception {
		return (Integer) handleCommand("unmapAll", "unmapAll");
	}

	/**
	 * this function maps all test in scenario tree
	 * 
	 * @return 0 if succeeds
	 * @throws Exception
	 */
	public int mapAll() throws Exception {
		return (Integer) handleCommand("mapAll", "mapAll");
	}

	/**
	 * will map a test in index testIdx.
	 * 
	 * @param testIdx
	 * @return
	 * @throws Exception
	 */
	public int mapTest(int testIdx) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(testIdx);
		return (Integer) handleCommand("mapTest: test index-" + testIdx, "mapTest", v);

	}

	/**
	 * unmaps a test at index testIdx.
	 * 
	 * @param testIdx
	 * @return
	 * @throws Exception
	 */
	public int unmapTest(int testIdx) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(testIdx);
		return (Integer) handleCommand("unmapTest: test index-" + testIdx, "unmapTest", v);
	}

	/**
	 * will map a scenario at index scenarioIdx. will assume all tests should be
	 * recursively be marked under the scenario. if otherwise, use the more
	 * explicit version of mapScenario.
	 * 
	 * @param scenarioIdx
	 * @return
	 * @throws Exception
	 */
	public int mapScenario(int scenarioIdx) throws Exception {
		return mapScenario(scenarioIdx, false);
	}

	/**
	 * will map a scenario at index scenarioIdx. if rootOnly is true, will only
	 * map direct children of that scenario, else, all children recursively,
	 * like useing mapScenario(scnearioIdx);
	 * 
	 * @param scenarioIdx
	 * @param rootOnly
	 * @return
	 * @throws Exception
	 */
	public int mapScenario(int scenarioIdx, boolean rootOnly) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(scenarioIdx);
		v.addElement(true);// isScenario == true
		v.addElement(rootOnly);
		return (Integer) handleCommand("mapTest: test index-" + scenarioIdx, "mapTest", v);
	}

	/**
	 * will unmap a scenario at index scenarioIdx. will assume a recursive unmap
	 * is required on all children.
	 * 
	 * @param scenarioIdx
	 * @return
	 * @throws Exception
	 */
	public int unmapScenario(int scenarioIdx) throws Exception {
		return unmapScenario(scenarioIdx, false);
	}

	/**
	 * will unmap a scenario at index scenarioIdx. if rootOnly is true, will
	 * only unmap the direct children of the scenario otherwise, will
	 * recursively unmap all children.
	 * 
	 * @param scenarioIdx
	 * @return
	 * @throws Exception
	 */
	public int unmapScenario(int scenarioIdx, boolean rootOnly) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(scenarioIdx);
		v.addElement(true);// isScenario == true
		v.addElement(rootOnly);
		return (Integer) handleCommand("unmapTest: test index-" + scenarioIdx, "unmapTest", v);
	}

	/**
	 * deletes a specific test according to it's index in scenarioTree
	 * 
	 * @param testIndex
	 *            the test index
	 * @throws Exception
	 */
	public void deleteTest(int testIndex) throws Exception {
		handleCommand("delete test index: " + testIndex, "deleteTest", testIndex);
	}

	public void markRootScenarioAsTest(boolean mark) throws Exception {
		String str = mark ? "Mark" : "UnMark";
		handleCommand(str + " Root Scenario as test", "markRootScenarioAsTest", mark);
	}

	public void markScenarioAsTest(int index, boolean mark) throws Exception {
		String str = mark ? "Mark" : "UnMark";
		handleCommand(str + " Scenario as test", "markScenarioAsTest", index, mark);
	}

	public void navigateToSubScenario(int scenarioIndex) throws Exception {
		handleCommand("navigate to the sub scenario selected", "navigateToSubScenario", scenarioIndex);
	}

	public void markAsKnownIssue(int testIndex, boolean mark) throws Exception {
		String str = mark ? "Mark" : "UnMark";
		handleCommand(str + " a test as know issue", "markAsKnownIssue", testIndex, mark);
	}
	
	public void markAsNegative(int testIndex, boolean mark) throws Exception {
		String str = mark ? "Mark" : "UnMark";
		handleCommand(str + " a test as negative", "markAsNegative", testIndex, mark);
	}

	/**
	 * will set the remote test at index testIndex, parameter with name
	 * paramName to value of value.
	 * 
	 * @param testIndex
	 * @param tab
	 * @param paramName
	 * @param value
	 * @param isCombo
	 * @throws Exception
	 */
	public void setTestParameter(int testIndex, String tab, String paramName, String value, boolean isCombo)
	throws Exception {
		handleCommand("setTestParameter, test index: " + testIndex + " tab: " + tab + ", param name: " + paramName
				+ ", value: " + value + " ,isCombo" + isCombo, "setTestParameter", testIndex, tab, paramName, value,
				isCombo);

	}
	

	/**
	 * This method is used in order to change parameters in the JSystem GUI that are provided by the class GenericObjectParameterProvider  
	 * The method flow is :
	 * 1)Go to the parameter tab.
	 * 2)get the parameter with the name paramName and open the set parameter dialog
	 * 3)set the values in the dialog in the same order they appear in the provided array
	 * 
	 * only for: GenericObjectParameterProvider
	 * 
	 * @param testIndex
	 * @param tab
	 * @param paramName
	 * @param value
	 * @param isCombo
	 * @throws Exception
	 */
    public void setTestUserProviderTestParam(int testIndex, String tab, String paramName, String[] values) throws Exception {
        String allValues="";
        for(String s : values){
            allValues += s+",";
        }
     int resolt = (Integer) handleCommand("setTestParameter, test index: " + testIndex + " tab: " + tab + ", param name: " + paramName
				+ ", values: " + allValues,"setTestUserProviderTestParam",testIndex,tab,paramName,values);
     report.report("resolt is "+resolt);
     if(-1 == resolt){
    	report.report("Can't change Parameter "+paramName+" to "+allValues+" because the table parameter is disabled",false); 
     }else if(-2 == resolt){
    	 report.report("Can't find Parameter "+paramName,false); 
     }
   }
    
    /**
	 * will set the remote test at index testIndex, parameter with name
	 * paramName to value of value.
	 * 
	 * only for: ObjectArrayParameterProvider
	 * 
	 * @param testIndex
	 * @param tab
	 * @param paramName
	 * @param value
	 * @param isCombo
	 * @throws Exception
	 */
    public void setTestArrayParam(int testIndex, String tab, String paramName, String[] values) throws Exception {
        String allValues="";
        for(String s : values){
            allValues+=s+",";
        }
     int resolt = (Integer) handleCommand("setTestParameter, test index: " + testIndex + " tab: " + tab + ", param name: " + paramName
				+ ", values: " + allValues,"setTestArrayParam",testIndex,tab,paramName,values);
     report.report("resolt is "+resolt);
     if(-1 == resolt){
     	report.report("Can't change Parameter "+paramName+" to "+allValues+" because the table parameter is disabled",false); 
      }else if(-2 == resolt){
     	 report.report("Can't find Parameter "+paramName,false); 
      }
   }

	/**
	 * will reset to default the remote test at index testIndex, 
	 * by calling to the remote resetToDefault method in the calss JScenarioHandler 
	 * @param testIndex
	 * @throws Exception
	 */
	public void resetToDefault(int testIndex)throws Exception {
		handleCommand("Reset To Default, test index: " + testIndex , "resetToDefault", testIndex);
	}

	/**
	 * 
	 * @param testIndex
	 * @param tab
	 * @param paramName
	 * @param value
	 * @param isCombo
	 * @param isScenario
	 * @throws Exception
	 */
	public void setTestParameter(int testIndex, String tab, String paramName, String value, boolean isCombo,
			boolean isScenario) throws Exception {
		handleCommand("setTestParameter, test index: " + testIndex + " tab: " + tab + ", param name: " + paramName
				+ ", value: " + value + " ,isCombo" + isCombo, "setTestParameter", testIndex, tab, paramName, value,
				isCombo, isScenario);
	}

	/**
	 * 
	 * @param testIndex
	 * @param tab
	 * @param paramName
	 * @param value
	 * @param isCombo
	 * @param isScenario
	 * @param approve
	 * @throws Exception
	 */
	public void setTestParameter(int testIndex, String tab, String paramName, String value, boolean isCombo,
			boolean isScenario, boolean approve) throws Exception {
		handleCommand("setTestParameter, test index: " + testIndex + " tab: " + tab + ", param name: " + paramName
				+ ", value: " + value + " ,isCombo" + isCombo, "setTestParameter", testIndex, tab, paramName, value,
				isCombo, isScenario, approve);
	}

	public String getTestParameter(int testIndex, String tab, String paramName) throws Exception {
		return (String) handleCommand("get a test parameter", "getTestParameter", testIndex, tab, paramName);
	}

	public int moveTestDown(int testIndex) throws Exception {
		return (Integer) handleCommand("move test down", "moveTestDown", testIndex);
	}

	public int moveTestUp(int testIndex) throws Exception {
		return (Integer) handleCommand("move test up", "moveTestUp", testIndex);
	}

	public int moveTestUpByMenuOption(int testIndex) throws Exception {
		return (Integer) handleCommand("move test up by menu option", "moveTestUpByMenuOption", testIndex);
	}

	public int moveTestDownByMenuOption(int testIndex) throws Exception {
		return (Integer) handleCommand("move test down by menu option", "moveTestDownByMenuOption", testIndex);
	}

	/**
	 * Sets the user test documentation of test or scenario.
	 * 
	 * @param testIndex
	 *            The index of the test in the scenario tree. 0 is the root
	 *            scenario
	 * @param documentation
	 *            Documentation to set as user documentation.
	 * @throws Exception
	 */
	public void setTestUserDocumentation(final int testIndex, final String documentation) throws Exception {
		handleCommand("set test user documentation", "setTestUserDocumentation", testIndex, documentation);
	}

	/**
	 * Returns the user test documentation of test or scenario
	 * 
	 * @param testIndex
	 *            The index of the test in the scenario tree. 0 is the root
	 * 
	 * @return The BB user test documentation.
	 * @throws Exception
	 */
	public String getTestUserDocumentation(final int testIndex) throws Exception {
		return (String) handleCommand("get test user documentation", "getTestUserDocumentation", testIndex);
	}

	// ****** NOT TESTED ******
	public String getTestJavaDoc(final int testIndex) throws Exception {
		return (String) handleCommand("get test java doc", "getTestJavaDoc", testIndex);
	}

	// ***********************
	
	public void editOnlyLocally(int testIndex) throws Exception {
        handleCommand("Edit Only Locally, test index: " + testIndex, "editOnlyLocally", testIndex);
    }

}
