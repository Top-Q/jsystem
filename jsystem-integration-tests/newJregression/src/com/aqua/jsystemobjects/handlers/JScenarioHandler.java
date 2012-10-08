package com.aqua.jsystemobjects.handlers;

import java.awt.Component;
import java.io.File;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JEditorPane;
import javax.swing.tree.TreePath;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.teststable.ScenarioTreeNode;
import jsystem.treeui.tree.TestTreePanel;

import org.jsystem.jemmyHelpers.TipNameButtonFinder;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.DialogWaiter;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFileChooserOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextAreaOperator;
import org.netbeans.jemmy.operators.JTreeOperator;

import com.aqua.utils.ScenarioModelUtils;

public class JScenarioHandler extends BaseHandler {

	/**
	 * adds a test to scenario tree by pressing the add test button
	 * 
	 * @param methodName
	 * @param className
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public int addTest(String methodName, String className, int amount) throws Exception {
		try {
			return _addTest(methodName, className, amount);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new Exception(t);
		}
	}

	/**
	 * adds a test to scenario tree by pressing the add test button
	 * 
	 * @param node
	 * @param parentNode
	 * @return
	 * @throws Exception
	 */
	public int addTest(String node, String parentNode) throws Exception {
		addTest(node, parentNode, 1);
		return 0;
	}

	/**
	 * adds a test to scenario tree by pressing the add test button
	 * 
	 * @param node
	 * @param parentNode
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	private int _addTest(String node, String parentNode, int amount) throws Exception {
		System.out.println("Adding test " + node + " from class " + parentNode);
		if (checkTestInTestsTree(node, parentNode, true) < 0) {
			return -1;
		}
		moveCheckedToScenarioTree(amount);
		return 0;
	}

	/**
	 * moves checked tests to the scenario tree. if a warning message dialog
	 * appears it must be handled out side the method. only sets the amout of
	 * times to add the tests and press the addButton.
	 * 
	 * @throws Exception
	 */
	public int moveCheckedToScenarioTree(int amount) throws Exception {
		jemmySupport.setSpinnerValue(mainFrame, jmap.getNumOfTestToAddSpinner(), new Integer(amount));
		jemmySupport.pushButton(mainFrame, jmap.getAddTestsButton());
		jemmySupport.WaitForDialogToClose(jmap.getAddTestsDialog());
		return ((Long) TestTreePanel.getCurrentSelectedTests()).intValue();
	}

	/**
	 * removes all tests in scenarioTree
	 * 
	 * @throws Exception
	 */
	public int deleteAllTestsFromScenarioTree() throws Exception {
		final int rowCount = scenarioTree.getRowCount();
		int i = rowCount - 1;
		jemmySupport.report("i= " + i);
		final String txt = jmap.getTestDeleteMenuItem();
		System.out.println("scenarioTree.getRowcount = " + rowCount);

		while (i > 0) {
			TreePath foundPath = scenarioTree.getPathForRow(i);
			JPopupMenuOperator popUpMenu = new JPopupMenuOperator(scenarioTree.callPopupOnPath(foundPath));
			jemmySupport.pushMenuItem(popUpMenu, txt);
			jemmySupport.report("removing test at index " + i);
			Thread.sleep(500);
			i--;
		}
		return 0;
	}

	/**
	 * takes test class name and test name to check, and a boolean value of true
	 * or false and returns an int 0 if succeeds or -1 if such path is not found
	 * in test tree only checks the requested test's checkboxes
	 * 
	 * @param node
	 * @param parentNode
	 * @param check
	 * @return
	 * @throws Exception
	 */
	public int checkTestInTestsTree(String node, String parentNode, boolean check) throws Exception {
		TreePath foundPath = getTreePath(node, parentNode);
		if (foundPath == null) {
			return -1;
		}
		JPopupMenuOperator pp = new JPopupMenuOperator(testsTree.callPopupOnPath(foundPath));
		if (check) {
			pp.pushMenu(jmap.getTestSelection());
		} else {
			pp.pushMenu(jmap.getTestUnSelection());
		}
		return 0;
	}

	public int scenarioNavigateForward() throws Exception {
		new JButtonOperator(mainFrame, new TipNameButtonFinder(jmap.getScenarioNavigateForward())).clickMouse();
		return 0;
	}

	public int scenarioNavigateBackward() throws Exception {
		new JButtonOperator(mainFrame, new TipNameButtonFinder(jmap.getScenarioNavigateBackword())).clickMouse();
		return 0;
	}

	public int scenarioRedo() throws Exception {
		new JButtonOperator(mainFrame, new TipNameButtonFinder(jmap.getScenarioRedoButton())).clickMouse();
		return 0;
	}

	public int scenarioUndo() throws Exception {
		new JButtonOperator(mainFrame, new TipNameButtonFinder(jmap.getScenarioUndoButton())).clickMouse();
		return 0;
	}

	/**
	 * imitates the action of pushing the open scenario button choosing a
	 * scenario file, and pressing open.
	 * 
	 * @param scenarioName
	 * @return
	 * @throws Exception
	 */
	public int openScenario(String scenarioName) throws Exception {
		File scenariosFile = new File(JSystemProperties.getInstance()
				.getPreference(FrameworkOptions.TESTS_CLASS_FOLDER), "scenarios");
		jemmySupport.pushButtonAndWaitForDialog(mainWindow, jmap.getOpenScenarioButton(), jmap.getScenarioSelectWin());
		JFileChooserOperator fileChosser = new JFileChooserOperator();
		fileChosser.setCurrentDirectory(scenariosFile);
		fileChosser.chooseFile(scenarioName);
		new JButtonOperator(fileChosser, new TipNameButtonFinder(jmap.getScenarioDialogSelectButton())).clickMouse();
		return 0;
	}

	/**
	 * chooses the current scenario in the root of the tree presses the clear
	 * scenario button click the opened dialog "yes" button selects the current
	 * scenario(which is the default, in the root of the tree if default had
	 * tests, it has the tests under it after this operation.
	 * 
	 * CAUTION: when using this, tests that depend on cleared tests tree might
	 * fail, use the cleanAndCreate method.
	 */
	public int deleteCurrentScenario() throws Exception {

		String currentScenario = ScenariosManager.getInstance().getCurrentScenario().getName();
		currentScenario = ScenarioModelUtils.getScenarioNameRelativeToScenariosFolder(currentScenario);
		selectTestsRows(new int[] { 0 });

		Thread.sleep(1000);
		new JButtonOperator(mainFrame, new TipNameButtonFinder(jmap.getClearScenarioButton())).push();
		DialogWaiter waiter = new DialogWaiter();
		Timeouts to = new Timeouts();
		to.setTimeout("DialogWaiter.WaitDialogTimeout", 3000);
		waiter.setTimeouts(to);
		try {
			waiter.waitDialog(jmap.getDeleteScenarioWindow(), true, true);
		} catch (Throwable t) {
			return 0;
		}
		JDialogOperator dialog = new JDialogOperator(jmap.getDeleteScenarioWindow());
		new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getDialogSelectOKButton())).push();

		waiter = new DialogWaiter();
		to = new Timeouts();
		to.setTimeout("DialogWaiter.WaitDialogTimeout", 3000);
		waiter.setTimeouts(to);
		try {
			waiter.waitDialog(jmap.getDialogSelectWin(), true, true);
		} catch (Throwable t) {
			return 0;
		}
		dialog = new JDialogOperator(jmap.getDialogSelectWin());
		new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getDialogSelectOKButton())).push();
		openScenario(currentScenario);
		return 0;
	}

	/**
	 * get current scenario name.
	 */
	public String getCurrentRootScenarioName() throws Exception {
		String currentScenario = ScenariosManager.getInstance().getCurrentScenario().getName();
		return currentScenario;
	}

	/**
	 * goes over the tree and selects the rows given to the function for
	 * selection
	 * 
	 * @param rows
	 * @return
	 */
	public int selectTestsRows(int[] rows) throws Exception {
		JTreeOperator scenarioTree = new JTreeOperator(mainFrame, 0);
		if (rows.length < 1) {
			return 0;
		}
		for (int i = 0; i < rows.length; i++) {
			// In some cases (switch BB for example), the
			// scenarioTree.selectRow(int) is not working. So it is best to use
			// selectPath
			TreePath path = scenarioTree.getPathForRow(rows[i]);
			if(scenarioTree.getSelectionPath() != path){
				scenarioTree.selectRow(rows[i]);
			}
			scenarioTree.addSelectionRow(rows[i]);
		}
		JSystemProperties.getInstance().setPreference(FrameworkOptions.SUB_SCENARIO_EDIT, "true");
		return 0;
	}

	public int selectTestRow(int row) throws Exception {
		return selectTestsRows(new int[] { row });
	}

	/**
	 * imitates pushing the new scenario button giving it a name and pressing
	 * the save button
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public int createScenario(String name) throws Exception {
		File scenariosFile = new File(JSystemProperties.getInstance()
				.getPreference(FrameworkOptions.TESTS_CLASS_FOLDER), "scenarios");
		jemmySupport.pushButtonAndWaitForDialog(mainWindow, jmap.getNewScenarioButton(), jmap.getNewScenarioWin());
		JFileChooserOperator fileChosser = new JFileChooserOperator();
		fileChosser.setCurrentDirectory(scenariosFile);
		fileChosser.chooseFile(name);
		new JButtonOperator(fileChosser, new TipNameButtonFinder(jmap.getNewScenarioSaveButton())).clickMouse();
		return 0;
	}

	/**
	 * this function unmaps all test in scenario tree
	 * 
	 * @return 0 if succeeds
	 * @throws Exception
	 */
	public int unmapAll() throws Exception {
		return unmapTest(0, true, false);
	}

	/**
	 * maps all tests in the root scenario
	 * 
	 * @return 0 if succeeds
	 * @throws Exception
	 */
	public int mapAll() throws Exception {
		return mapTest(0, true, false);
	}

	/**
	 * will map a test or a scenario at index testIdx. if the index is 0 it is
	 * by default a scenario hence the mapTest with isSCenario = true is called,
	 * else, if not mentioned other wise by the caller, it is calling the
	 * mapTest version of a test and not a scenario. will also assume that all
	 * tests under the scenario must be checked recursively. return, the result
	 * from the specific mapTest function.
	 */
	public int mapTest(int testIdx) throws Exception {
		boolean isScenario = false;
		if (isScenario(testIdx)) {
			isScenario = true;
		}
		return mapTest(testIdx, isScenario);
	}

	/**
	 * will map a test at index testIdx, if isScenario is true, will map all
	 * tests under the scenario recursively. if other behavior is wanted, please
	 * use the specific method mapTest that takes three arguments.
	 * 
	 * @param testIdx
	 * @param isScenario
	 * @return
	 * @throws Exception
	 */
	public int mapTest(int testIdx, boolean isScenario) throws Exception {
		boolean rootOnly = false;
		if (isScenario == true) {
			rootOnly = true;
		}
		return mapTest(testIdx, isScenario, rootOnly);
	}

	/**
	 * This method mapps a test to on inside a scenario
	 * 
	 * @param testIdx
	 * @return -1 if fails, 0 otherwise
	 * @throws Exception
	 */
	public int mapTest(int testIdx, boolean isScenario, boolean rootOnly) throws Exception {
		if (isScenario == true) {
			// if all tests under the scenario are marked then return without
			// opening the JMenu.
			// because the mapall won't be present.
			if (getNumberOfTests(testIdx, rootOnly, false) == getNumberOfTests(testIdx, rootOnly, true)) {
				log.info("all tests under the scenario are already mapped");
				System.out.println("all tests under the scenario are already mapped");
				jemmySupport.report("all tests under the scenario are already mapped");
				return 0;
			}
		} else if (isScenario == false) {
			// if the test to map is already mapped, return without opening the
			// JMenu, since map
			// option will be disabled there.
			if (checkTestMappedUnmapped(testIdx)) {
				log.info("test already mapped");
				System.out.println("test already mapped");
				jemmySupport.report("test already mapped");
				return 0;
			}
		}
		String txt = "";
		TreePath foundPath = scenarioTree.getPathForRow(testIdx);
		if (foundPath == null) {
			throw new Exception("Path not found test index: " + testIdx);
		}
		Thread.sleep(200);
		JPopupMenuOperator pp = new JPopupMenuOperator(scenarioTree.callPopupOnPath(foundPath));
		Thread.sleep(200);
		if (isScenario) {
			txt = jmap.getTestMapAllMenuItem();
		} else {
			txt = jmap.getTestMapMenuItem();
		}
		try {
			jemmySupport.pushMenuItem(pp, txt);
		} catch (Exception e) {
			log.log(Level.SEVERE, "The test item-" + testIdx + " was already mapped");
			return -1;
		}

		return 0;
	}

	/**
	 * This method maps a test to on inside a scenario
	 * 
	 * @param testIdx
	 * @return -1 if failed, 0 otherwise.
	 * @throws Exception
	 */
	public int unmapTest(int testIdx, boolean isScenario, boolean rootOnly) throws Exception {
		if (isScenario == true) {
			// if all tests in scenario are unmarked, (all - mapped = all), then
			// return without opening the dialog.
			if (getNumberOfTests(testIdx, rootOnly, false) - getNumberOfTests(testIdx, rootOnly, true) == getNumberOfTests(
					testIdx, rootOnly, false)) {
				log.info("all tests under the scenario are already unmapped");
				System.out.println("all tests under the scenario are already unmapped");
				jemmySupport.report("all tests under the scenario are already unmapped");
				return 0;
			}
		} else if (isScenario == false) {
			//
			if (!checkTestMappedUnmapped(testIdx)) {
				log.info("test already unmapped");
				System.out.println("test already unmapped");
				jemmySupport.report("test already unmapped");
				return 0;
			}
		}
		String txt = "";
		TreePath foundPath = scenarioTree.getPathForRow(testIdx);
		if (foundPath == null) {
			throw new Exception("Path not found test index: " + testIdx);
		}
		// create a jemmy operator for a popupmenu component called on a path
		// in scenario tree. popMenuFor the correct line under the tree
		Thread.sleep(200);
		JPopupMenuOperator pp = new JPopupMenuOperator(scenarioTree.callPopupOnPath(foundPath));
		Thread.sleep(200);
		if (isScenario) {
			txt = jmap.getTestUnmapAllMenuItem();// get the value for the test
													// unmap property
		} else {
			txt = jmap.getTestUnmapMenuItem();
		}
		try {
			jemmySupport.pushMenuItem(pp, txt);
		} catch (Exception e) {
			log.log(Level.SEVERE, "The test item-" + testIdx + " was already unmapped");
			return -1;
		}
		return 0;
	}

	/**
	 * will unmap a test at index testIdx. if the test index is 0 then it is a
	 * scenario and will be handled as a scenario by default, which also means
	 * it will be called on all tests under the scenario and not only the root
	 * tests. everything will be unmapped, under the specified scenario. return,
	 * 0 on success.
	 */
	public int unmapTest(int testIdx) throws Exception {
		boolean isScenario = false;
		if (isScenario(testIdx)) {
			isScenario = true;
		}
		return unmapTest(testIdx, isScenario);
	}

	/**
	 * unmaps a test or a scenario as designated. if not told otherwise, assumes
	 * in the case of a scenario that all tests under it should be unmapped
	 * recursively. please use a specific version that takes the rootOnly
	 * argument for different results.
	 * 
	 * @param testIdx
	 * @param isScenario
	 * @return
	 * @throws Exception
	 */
	public int unmapTest(int testIdx, boolean isScenario) throws Exception {
		boolean rootOnly = false;
		if (isScenario == true)
			rootOnly = false;
		return unmapTest(testIdx, isScenario, rootOnly);
	}

	/**
	 * checks if a specific path in scenarioTree is a scenario.
	 * 
	 * @param index
	 * @return
	 * @throws Exception
	 */
	private boolean isScenario(int index) throws Exception {
		TreePath foundPath = scenarioTree.getPathForRow(index);
		ScenarioTreeNode node = (ScenarioTreeNode) foundPath.getLastPathComponent();
		JTest test = node.getTest();
		if (test instanceof Scenario && !((Scenario) test).isScenarioAsTest()) {
			return true;
		}
		return false;
	}

	/**
	 * Get the number of tests inside a container (Scenario\Flow) <br>
	 * if the selected test is not a container, 0 will be returned
	 * 
	 * @param index
	 *            the test index in the Scenario tree (0 is the root)
	 * @param rootOnly
	 *            will return root only , otherwise return all tests in
	 * @param markedOnly
	 *            will return only enabled tests
	 * @return the amount of tests matching the given criteria parameters
	 */
	public int getNumberOfTests(int index, boolean rootOnly, boolean markedOnly) throws Exception {
		TreePath foundPath = scenarioTree.getPathForRow(index);
		if (foundPath == null) {
			throw new Exception("Path not found test index: " + index);
		}
		ScenarioTreeNode node = (ScenarioTreeNode) foundPath.getLastPathComponent();//
		JTest test = node.getTest();// return the object in that path (scenario
									// or test)
		if (!(test instanceof JTestContainer)) {// if returned type is not a
												// scenario then it has 0
												// children tests.
			return 0;
		}
		JTestContainer container = (JTestContainer) test;

		if (rootOnly) { // ROOT TESTS ONLY
			if (markedOnly) {// to get only the number of mapped tests under the
								// scenario
				int enabled = 0;
				Vector<JTest> rootTests = container.getRootTests();
				// iterate over all scenario tests, count the mapped ones.
				for (JTest currentTest : rootTests) {
					if (!currentTest.isDisable()) {
						enabled++;
					}
				}
				return enabled;// return the number of mapped tests.
			} else {// if asked for all the tests under the scenario(not only
					// marked ones.
				return container.getRootTests().size(); // root tests only
			}
		} else { // ALL TESTS
			if (markedOnly) {
				return container.getEnabledTests().size();
			} else {
				return container.getTests().size(); // all tests
			}
		}
	}

	/**
	 * takes a test index in scenario tree and checks if it's enabled (mapped)
	 * or not.
	 * 
	 * @param index
	 * @return true if test is enabled and false otherwise.
	 * @throws Exception
	 */
	public boolean checkTestMappedUnmapped(int index) throws Exception {
		TreePath foundPath = scenarioTree.getPathForRow(index);
		if (foundPath == null) {
			throw new Exception("Path not found test index: " + index);
		}
		ScenarioTreeNode node = (ScenarioTreeNode) foundPath.getLastPathComponent();//
		JTest test = node.getTest();// return the object in that path (scenario
									// or test)
		if (!(test instanceof RunnerTest)) {
			if (!((Scenario) test).isScenarioAsTest()) {
				throw new Exception("type passed must be a RunnerTest");
			}
		}
		boolean result = !test.isDisable();
		return result;
	}

	/**
	 * deletes a specific test according to it's index in scenarioTree
	 * 
	 * @param testIndex
	 *            the test index
	 * @throws Exception
	 */
	public int deleteTest(int testIndex) throws Exception {
		pushMenuItemForTest(testIndex, jmap.getTestDeleteMenuItem());
		return 0;
	}

	public int markRootScenarioAsTest(boolean mark) throws Exception {
		String menuItem = mark ? jmap.getScenarioMarkAsTestMenuItem() : jmap.getScenarioUnMarkAsTestMenuItem();
		pushMenuItemForTest(0, menuItem);
		return 0;
	}

	public int markScenarioAsTest(int index, boolean mark) throws Exception {
		String menuItem = mark ? jmap.getScenarioMarkAsTestMenuItem() : jmap.getScenarioUnMarkAsTestMenuItem();
		pushMenuItemForTest(index, menuItem);
		return 0;
	}

	/**
	 * well mark a test at index testIndex as known issue, using the popup on
	 * path.
	 * 
	 * @param testIndex
	 * @param mark
	 * @return
	 * @throws Exception
	 */
	public int markAsKnownIssue(int testIndex, boolean mark) throws Exception {
		String menuItem = mark ? jmap.getScenarioMarkAsKnownIssueMenuItem() : jmap
				.getScenarioUnMarkAsKnownIssueMenuItem();
		pushMenuItemForTest(testIndex, menuItem);
		return 0;
	}
	
	/**
	 * well mark a test at index testIndex as negative, using the popup on
	 * path.
	 * 
	 * @param testIndex
	 * @param mark
	 * @return
	 * @throws Exception
	 */
	public int markAsNegative(int testIndex, boolean mark) throws Exception {
		String menuItem = mark ? jmap.getMarkAsNegativeTestMenuItem() : jmap
				.getUnMarkAsNegativeTestMenuItem();
		pushMenuItemForTest(testIndex, menuItem);
		return 0;
	}

	/**
	 * will open the selected sub scenario in it's own root
	 * 
	 * @throws Exception
	 */
	public int navigateToSubScenario(int scenarioIndex) throws Exception {
		pushMenuItemForTest(scenarioIndex, jmap.getNavigateToSubScenario());
		return 0;
	}

	/**
	 * 1) locate a test by given index\String.<br>
	 * 2) open pop-up menu<br>
	 * 3) select given menuItem<br>
	 * 
	 * @param identifier
	 *            Integer index \ String name
	 * @param menuItem
	 *            the menu item to push
	 * @throws Exception
	 */
	private void pushMenuItemForTest(int testIndex, String menuItem) throws Exception {
		JPopupMenuOperator pp = rightClickPopUpManu(testIndex);
		jemmySupport.pushMenuItem(pp, menuItem);
	}

	private JPopupMenuOperator rightClickPopUpManu(int testIndex) throws Exception {
		TreePath foundPath = scenarioTree.getPathForRow(testIndex);
		if (foundPath == null) {
			throw new Exception("Path not found test index: " + testIndex);
		}

		// create a jemmy operator for a popupmenu component called on a path
		// in scenario tree. popMenuFor the correct line under the tree
		JPopupMenuOperator pp = new JPopupMenuOperator(scenarioTree.callPopupOnPath(foundPath));
		return pp;
	}

	private void pushMenuItemForTestNoBlock(int testIndex, String menuItem) throws Exception {
		JPopupMenuOperator pp = rightClickPopUpManu(testIndex);
		pp.pushMenuNoBlock(menuItem);

	}

	public int setTestParameter(int testIndex, String tab, String paramName, String value, boolean isCombo)
			throws Exception {
		return setTestParameter(testIndex, tab, paramName, value, isCombo, false);
	}

	public int setTestParameter(int testIndex, String tab, String paramName, String value, boolean isCombo,
			boolean isScenario) throws Exception {
		return setTestParameter(testIndex, tab, paramName, value, isCombo, isScenario, true);
	}

	public int setTestParameter(int testIndex, String tab, String paramName, String value, boolean isCombo,
			boolean isScenario, boolean approve) throws Exception {

		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		JTableOperator paramTable = openParamTabAndGetParamTable(testIndex, tab, testInfoTab);

		int numOfParamRows = paramTable.getRowCount();
		for (int i = 0; i < numOfParamRows; i++) {
			Object tableValue = paramTable.getValueAt(i, 0);
			if (tableValue != null
					&& (tableValue.toString().equalsIgnoreCase(paramName + "*") || tableValue.toString()
							.equalsIgnoreCase(paramName))) {
				jemmySupport.setTableCell(paramTable, i, 3, value, isCombo);
				testInfoTab.getSource().repaint();
				break;
			}
		}

		Thread.sleep(200);
		if (isScenario) {
			String buttonText = approve ? "Yes" : "No";
			jemmySupport.pushButtonAndWaitAndApproveDialog(mainFrame.getTitle(), "Apply for Scenario",
					"Apply Scenario Parameters", buttonText);
		}

		return 0;
	}
	


	/**
	 * Test Flow: 1)Go to the parameter tab. 2)find the parameter row according
	 * to the paramName provided and open its dialog 3)set the values by order
	 * in the table contained inside the dialog
	 * 
	 * @param testIndex
	 *            - The index of the test
	 * @param tab
	 *            - The tab were the Parameter are
	 * @param paramName
	 *            - The name for the Parameter to change
	 * @param values
	 *            - The new value of the Parameter
	 * @return 0 - in case the test pass. Otherwise the test failed
	 * @throws Exception
	 */
	public int setTestUserProviderTestParam(int testIndex, String tab, String paramName, Vector<Object> values)
			throws Exception {

		// Go to the parameter tab.
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		JTableOperator paramTable = openParamTabAndGetParamTable(testIndex, tab, testInfoTab);

		// find the parameter row according to the paramName provided and open
		// its dialog
		int numOfParamRows = paramTable.getRowCount();
		int foundParameterRow = -1;
		for (int i = 0; i < numOfParamRows; i++) {
			Object tableValue = paramTable.getValueAt(i, 0);
			if (tableValue != null
					&& (tableValue.toString().equalsIgnoreCase(paramName + "*") || tableValue.toString()
							.equalsIgnoreCase(paramName))) {
				foundParameterRow = i;
				break;
			}
		}

		if (foundParameterRow == -1) {

			return -2;
		}

		// set the values by order in the table contained inside the dialog
		paramTable.clickOnCell(foundParameterRow, 3);
		new JButtonOperator(mainFrame, new TipNameButtonFinder("...")).pushNoBlock();
		JDialogOperator dialog = new JDialogOperator();
		JTableOperator userProviderParamTable = new JTableOperator(dialog);
		if (userProviderParamTable.isEnabled()) {
			for (int j = 0; j < values.size(); j++) {
				userProviderParamTable.clickOnCell(j, 1);
				jemmySupport.setTableCell(userProviderParamTable, j, 1, (String) values.get(j), false);
			}
		} else {
			jemmySupport.pushButton(dialog, "OK");
			return -1;
		}

		jemmySupport.pushButton(dialog, "OK");

		return 0;
	}

	/**
	 * Test Flow: 1)Go to the parameter tab. 2)find the parameter row according
	 * to the paramName provided and open its dialog 3)add values by order in
	 * the table contained inside the dialog
	 * 
	 * @param testIndex
	 *            - The index of the test
	 * @param tab
	 *            - The tab were the Parameter are
	 * @param paramName
	 *            - The name for the Parameter to change
	 * @param values
	 *            - The new value of the Parameter
	 * @return 0 - in case the test pass. Otherwise the test failed
	 * @throws Exception
	 */
	public int setTestArrayParam(int testIndex, String tab, String paramName, Vector<?> values) throws Exception {

		// Go to the parameter tab.
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		JTableOperator paramTable = openParamTabAndGetParamTable(testIndex, tab, testInfoTab);

		// find the parameter row according to the paramName provided and open
		// its dialog
		int numOfParamRows = paramTable.getRowCount();
		int foundParameterRow = -1;
		for (int i = 0; i < numOfParamRows; i++) {
			Object tableValue = paramTable.getValueAt(i, 0);
			if (tableValue != null
					&& (tableValue.toString().equalsIgnoreCase(paramName + "*") || tableValue.toString()
							.equalsIgnoreCase(paramName))) {
				foundParameterRow = i;
				break;
			}
		}
		if (foundParameterRow == -1) {

			return -2;
		}

		// set the values by order in the table contained inside the dialog
		paramTable.clickOnCell(foundParameterRow, 3);
		new JButtonOperator(mainFrame, new TipNameButtonFinder("...")).pushNoBlock();
		JDialogOperator dialog = new JDialogOperator();
		JTableOperator userProviderParamTable = new JTableOperator(dialog);
		if (userProviderParamTable.isEnabled()) {
			for (int j = 0; j < values.size(); j++) {
				jemmySupport.pushButton(dialog, "Add...");
				userProviderParamTable.clickOnCell(j, 0);
				jemmySupport.setTableCell(userProviderParamTable, j, 0, (String) values.get(j), false);
			}
			jemmySupport.pushButton(dialog, "OK");
		} else {
			jemmySupport.pushButton(dialog, "OK");
			return -1;
		}
		return 0;
	}

	public int resetToDefault(int testIndex) throws Exception {
		pushMenuItemForTestNoBlock(testIndex, jmap.getScenarResetToDefault());

		JDialogOperator dialog = jemmySupport.getDialogIfExists(JsystemMapping.getInstance().getResetToDefaultWindow(),
				5);
		jemmySupport.pushButton(dialog, "OK");

		return 0;
	}

	/**
	 * Right click on the test->"Edit Only Locally"
	 * 
	 * @param testIndex
	 * @return
	 * @throws Exception
	 */
	public int editOnlyLocally(int testIndex) throws Exception {

		pushMenuItemForTest(testIndex, jmap.getScenarioEditOnlyLocallyItem());
		return 0;
	}

	/**
	 * the function will return the value of parameter paramName if can't be
	 * found it returns an empty string;
	 * 
	 * @param testIndex
	 * @param tab
	 * @param paramName
	 * @param isCombo
	 * @return
	 * @throws Exception
	 */
	public String getTestParameter(int testIndex, String tab, String paramName) throws Exception {
		String returnValue = "";
		selectTestRow(testIndex);
		jemmySupport.report("the testIndex is: " + testIndex);
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		testInfoTab.setSelectedIndex(2);
		JTabbedPaneOperator paramTab = new JTabbedPaneOperator(testInfoTab, 1);
		paramTab.selectPage(tab);
		JTableOperator paramTable = new JTableOperator(paramTab);
		int numOfParamRows = paramTable.getRowCount();
		for (int i = 0; i < numOfParamRows; i++) {
			jemmySupport.report("row index is:  " + i);
			jemmySupport.report("paramName is:  " + paramName);
			Object tableVal = paramTable.getValueAt(i, 0);
			jemmySupport.report("tableVal is:  " + tableVal);
			if (tableVal != null
					&& (tableVal.toString().equalsIgnoreCase(paramName + "*") || tableVal.toString().equalsIgnoreCase(
							paramName))) {
				jemmySupport.report("tables value " + (String) tableVal + " was found at row " + i
						+ " with test index " + testIndex);
				Object param = jemmySupport.getTableCellValue(paramTable, i, 3);
				if (param instanceof String) {
					jemmySupport.report("value is: " + param.toString().substring(0, ((String) param).length() - 1));
					returnValue = param.toString();
					break;
				}
			}
		}
		return returnValue;
	}

	/**
	 * select the test in index testIndex and push the moveTestUpButton
	 * 
	 * @param testIndex
	 * @return
	 * @throws Exception
	 */
	public int moveTestUp(int testIndex) throws Exception {
		selectTestsRows(new int[] { testIndex });
		jemmySupport.pushButton(mainFrame, jmap.getMoveTestUpButton());
		return 0;
	}

	/**
	 * call the pop up on the selected test and psh the moveup coise
	 * 
	 * @param testIndex
	 * @return
	 * @throws Exception
	 */
	public int moveTestUpByMenuOption(int testIndex) throws Exception {
		JPopupMenuOperator pp = rightClickPopUpManu(testIndex);

		pp.pushMenu(jmap.getTestMoveUpMenuItem());
		return 0;
	}

	/**
	 * select the test at index testIndex and press the moveTestUp button
	 * 
	 * @param testIndex
	 * @return
	 * @throws Exception
	 */
	public int moveTestDown(int testIndex) throws Exception {
		selectTestsRows(new int[] { testIndex });
		jemmySupport.pushButton(mainFrame, jmap.getMoveTestDownButton());
		return 0;
	}

	/**
	 * select the test at index testIndex and push the testMoveUp option on
	 * popup menu opened when pressing the right click button in mouse
	 * 
	 * @param testIndex
	 * @return
	 * @throws Exception
	 */
	public int moveTestDownByMenuOption(int testIndex) throws Exception {
		JPopupMenuOperator pp = rightClickPopUpManu(testIndex);

		pp.pushMenu(jmap.getTestMoveDownMenuItem());
		return 0;
	}

	public int setTestUserDocumentation(int testIndex, String documentation) throws Exception {
		JTabbedPaneOperator docTab = getTestUserDocumentationTabbedPane(testIndex);
		JTextAreaOperator textAreaOperator = getTestUserDocumentationTextArea(docTab);
		textAreaOperator.setText(documentation);
		Thread.sleep(1000);
		jemmySupport.pushButton(docTab, "Apply");
		return 0;
	}

	public String getTestUserDocumentation(int testIndex) throws Exception {
		JTabbedPaneOperator docTab = getTestUserDocumentationTabbedPane(testIndex);
		JTextAreaOperator textAreaOperator = getTestUserDocumentationTextArea(docTab);
		return textAreaOperator.getText();
	}

	public String getTestJavaDoc(int testIndex) throws Exception {
		selectTestsRows(new int[] { testIndex });
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		testInfoTab.setSelectedIndex(2);
		JTabbedPaneOperator docTab = new JTabbedPaneOperator(testInfoTab, 0);
		docTab.selectPage(0);
		ComponentSearcher searcher = new ComponentSearcher(docTab.getRootPane());
		Component comp = searcher.findComponent(new ComponentChooser() {
			public boolean checkComponent(Component comp) {
				return (comp instanceof JEditorPane);
			}

			public String getDescription() {
				return "";
			}
		});
		if (comp == null) {
			return "javadoc panel was not found";
		}
		return ((JEditorPane) comp).getText();
	}

	private JTabbedPaneOperator getTestUserDocumentationTabbedPane(int testIndex) throws Exception {
		selectTestsRows(new int[] { testIndex });
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		testInfoTab.setSelectedIndex(2);
		JTabbedPaneOperator docTab = new JTabbedPaneOperator(testInfoTab, 0);
		docTab.selectPage(1);
		return docTab;
	}

	private JTextAreaOperator getTestUserDocumentationTextArea(JTabbedPaneOperator docPane) throws Exception {
		JTextAreaOperator textAreaOperator = new JTextAreaOperator(docPane, 0);
		return textAreaOperator;
	}

	private JTableOperator openParamTabAndGetParamTable(int testIndex, String tab, JTabbedPaneOperator testInfoTab)
			throws Exception {
		selectTestsRows(new int[] { testIndex });
		testInfoTab.setSelectedIndex(2);
		JTabbedPaneOperator paramTab = new JTabbedPaneOperator(testInfoTab, 1);
		paramTab.selectPage(tab);
		return new JTableOperator(paramTab);
	}

}
