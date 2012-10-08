package com.aqua.jsystemobject.handlers;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.treeui.TestRunner;
import jsystem.treeui.TestTreeView;
import jsystem.treeui.params.ParametersPanel;
import jsystem.treeui.params.ParamsTableModel;
import jsystem.treeui.teststable.ScenarioTreeNode;
import jsystem.treeui.tree.TestTreePanel;
import jsystem.utils.StringUtils;

import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.DialogWaiter;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFileChooserOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTreeOperator;

import utils.ScenarioModelUtils;

import com.aqua.jsystemobject.JSystem;
import com.aqua.jsystemobject.TipNameButtonFinder;

public class JScenarioHandler extends BaseHandler {

	public JScenarioHandler() {
		ListenerstManager.getInstance().addListener(this);
	}

	/**
	 * mark the root scenario as edit local only (Handles XMLRPC request)
	 * 
	 * @throws Exception
	 */
	public int markScenarioAsEditLocalOnly() throws Exception {
		TreePath foundPath = new TreePath(scenarioTree.getRoot());
		Thread.sleep(200);
		JPopupMenuOperator pp = new JPopupMenuOperator(scenarioTree.callPopupOnPath(foundPath));
		String txt = jmap.getScenarioEditOnlyLocallyItem();
		jemmyOperation.pushMenuItem(pp, txt);

		return 0;
	}

	/**
	 * Check if scenario is marked as edit only locally (Handles XMLRPC request)
	 */
	public Boolean isScenarioMarkedAsEditOnlyLocally() throws Exception {

		return ScenariosManager.getInstance().getCurrentScenario().isEditLocalOnly();
	}

	/**
	 * Checks if the parameters of the scenario with the one based index are
	 * editable
	 * 
	 * @param testIndexOneBased
	 * @return
	 * @throws Exception
	 */
	public Boolean isSubScenarioFieldsAreEditable(int testIndexOneBased) throws Exception {
		ScenarioTreeNode rootScenarioTreeNode = (ScenarioTreeNode) scenarioTree.getRoot();
		ScenarioTreeNode subRootScenarioNode = (ScenarioTreeNode) rootScenarioTreeNode.getChildAt(0);
		selectTest(testIndexOneBased);
		ParametersPanel parametersPanel = getParametersPanel();

		return !parametersPanel.isParametersDisable(subRootScenarioNode);
	}

	/**
	 * This method gets index of the depth = 2 and index of the depth = 3
	 * TreeNodes and return whether the parameters of the depth = 3 Scenario are
	 * editable
	 * 
	 * @param scenarioIndexZeroBased
	 * @param innerScenarioIndexZeroBased
	 * @return
	 * @throws Exception
	 */
	public Boolean isSubSubScenarioFieldsAreEditable(int scenarioIndexZeroBased, int innerScenarioIndexZeroBased)
			throws Exception {

		ScenarioTreeNode selectedScenarioTreeNode = selectTestByIndexesPath(scenarioIndexZeroBased,
				innerScenarioIndexZeroBased);
		ParametersPanel parametersPanel = getParametersPanel();
		return !parametersPanel.isParametersDisable(selectedScenarioTreeNode);
	}

	/**
	 * Get the parameter panel of the j system UI
	 * 
	 * @return
	 * @throws Exception
	 */
	private ParametersPanel getParametersPanel() throws Exception {
		final String tab = "General";

		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		testInfoTab.setSelectedIndex(2);

		JTabbedPaneOperator paramTab = new JTabbedPaneOperator(testInfoTab, 1);
		paramTab.selectPage(tab);
		JTableOperator paramTable = new JTableOperator(paramTab);
		JTable jTable = (JTable) paramTable.getSource();
		ParamsTableModel paramsTableModel = (ParamsTableModel) jTable.getModel();
		ParametersPanel parametersPanel = paramsTableModel.getParameterPanel();

		return parametersPanel;
	}

	/**
	 * This method gets an array of indexes and go deep inside the scenario tree
	 * according to the indexes and return the selected ScenarioTreeNode
	 * 
	 * @param indexes
	 * @return
	 */
	public ScenarioTreeNode selectTestByIndexesPath(int... indexes) throws Exception {
		ScenarioTreeNode currentScenarioTreeNode = (ScenarioTreeNode) scenarioTree.getRoot();
		TreePath PathBuild = new TreePath(currentScenarioTreeNode);
		for (int i = 0; i < indexes.length; i++) {

			currentScenarioTreeNode = (ScenarioTreeNode) currentScenarioTreeNode.getChildAt(indexes[i]);
			PathBuild = PathBuild.pathByAddingChild(currentScenarioTreeNode);
		}
		scenarioTree.clickOnPath(PathBuild);

		return currentScenarioTreeNode;
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
				jemmyOperation.report("all tests under the scenario are already mapped");
				return 0;
			}
		} else if (isScenario == false) {
			// if the test to map is already mapped, return without opening the
			// JMenu, since map
			// option will be disabled there.
			if (checkTestMappedUnmapped(testIdx)) {
				log.info("test already mapped");
				System.out.println("test already mapped");
				jemmyOperation.report("test already mapped");
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
			jemmyOperation.pushMenuItem(pp, txt);
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
				jemmyOperation.report("all tests under the scenario are already unmapped");
				return 0;
			}
		} else if (isScenario == false) {
			//
			if (!checkTestMappedUnmapped(testIdx)) {
				log.info("test already unmapped");
				System.out.println("test already unmapped");
				jemmyOperation.report("test already unmapped");
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
			jemmyOperation.pushMenuItem(pp, txt);
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
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public String getJSystemProperty(String key) throws Exception {
		return JSystemProperties.getInstance().getPreference(key);
	}

	/**
	 * deletes a specific test according to it's index in scenarioTree
	 * 
	 * @param testIndex
	 * @throws Exception
	 */
	public int deleteTest(int testIndex) throws Exception {
		selectTestsRows(new int[] { testIndex });
		jemmyOperation.pushButton(mainFrame, jmap.getRemoveTestsButton());
		return 0;
	}

	/**
	 * deletes a test from scenarioTree according to it's name
	 * 
	 * @param testName
	 * @throws Exception
	 */
	public int deleteTest(String testName) throws Exception {
		int amount = ScenariosManager.getInstance().getCurrentScenario().getRootTests().size();
		for (int i = 0; i < amount; i++) {
			RunnerTest test = ScenariosManager.getInstance().getCurrentScenario().getTest(i);
			if (test.getMethodName().equals(testName)) {
				ScenariosManager
						.getInstance()
						.getCurrentScenario()
						.removeTest(
								(JTest) ScenariosManager.getInstance().getCurrentScenario().getRootTests().elementAt(i));
				TestRunner.treeView.tableController.refresh();
				return 0;
			}
		}
		return 0;
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
		jemmyOperation.pushButton(mainFrame, jmap.getMoveTestUpButton());
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
		TreePath foundPath = scenarioTree.getPathForRow(testIndex);
		if (foundPath == null) {
			throw new Exception("Path not found test index: " + testIndex);
		}
		JPopupMenuOperator pp = new JPopupMenuOperator(scenarioTree.callPopupOnPath(foundPath));

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
		jemmyOperation.pushButton(mainFrame, jmap.getMoveTestDownButton());
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
		TreePath foundPath = scenarioTree.getPathForRow(testIndex);
		if (foundPath == null) {
			throw new Exception("Path not found test index: " + testIndex);
		}
		JPopupMenuOperator pp = new JPopupMenuOperator(scenarioTree.callPopupOnPath(foundPath));

		pp.pushMenu(jmap.getTestMoveDownMenuItem());
		return 0;
	}

	/**
	 * removes all tests in scenarioTree
	 * 
	 * @throws Exception
	 */
	public int deleteAllTestsFromScenarioTree() throws Exception {
		final int rowCount = scenarioTree.getRowCount();
		int i = rowCount - 1;
		final String txt = jmap.getTestDeleteMenuItem();
		jemmyOperation.report("scenarioTree.getRowcount = " + rowCount);

		while (i > 0) {
			jemmyOperation.report("deleting test " + i);
			TreePath foundPath = scenarioTree.getPathForRow(i);
			JPopupMenuOperator popUpMenu = new JPopupMenuOperator(scenarioTree.callPopupOnPath(foundPath));
			jemmyOperation.pushMenuItem(popUpMenu, txt);
			Thread.sleep(100);
			jemmyOperation.report("test " + i + " is deleted");
			i--;
		}
		return 0;
	}

	/**
	 * returns the current scenario name
	 * 
	 * @return
	 */
	public String getCurrentScenario() {
		return ScenariosManager.getInstance().getCurrentScenario().getName();
	}

	/**
	 * chooses the current scenario in the root of the tree presses the clear
	 * scenario button click the opened dialog "yes" button selects the current
	 * scenario(which is the default, in the root of the tree if default had
	 * tests, it has the tests under it after this operation.
	 * 
	 * CAUTION: when using this, tests that depand on cleared tests tree might
	 * fail, use the ScenarioUtils.cleanAndCreate method.
	 * 
	 * @deprecated use the ScenarioUtils.cleanAndCreate method.
	 */
	public int cleanCurrentScenario() throws Exception {

		String currentScenario = getCurrentScenario();
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
		new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getDialogSelectYesButton())).push();
		selectSenario(currentScenario);
		return 0;
	}

	public int selectTestsRows(int[] rows) {
		JTreeOperator scenarioTree = new JTreeOperator(mainFrame, 0);
		if (rows.length < 1) {
			return 0;
		}
		for (int i = 0; i < rows.length; i++) {
			//In some cases (switch BB for example), the scenarioTree.selectRow(int) is not working. So it is best to use selectPath
			scenarioTree.selectPath(scenarioTree.getPathForRow(rows[i]));
			scenarioTree.addSelectionRow(rows[i]);
		}
		JSystemProperties.getInstance().setPreference(FrameworkOptions.SUB_SCENARIO_EDIT, "true");
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
	public int selectScenario(String scenarioName) throws Exception {
		if (scenarioName.startsWith(ScenarioModelUtils.SCENARIO_HEADER)) {
			scenarioName = ScenarioModelUtils.getScenarioNameRelativeToScenariosFolder(scenarioName);
		}
		File scenariosFile = new File(JSystemProperties.getInstance()
				.getPreference(FrameworkOptions.TESTS_CLASS_FOLDER), "scenarios");
		jemmyOperation.pushButtonAndWaitForDialog(mainFrame, jmap.getOpenScenarioButton(), jmap.getScenarioSelectWin());
		JFileChooserOperator fileChosser = new JFileChooserOperator();
		fileChosser.setCurrentDirectory(scenariosFile);
		fileChosser.chooseFile(scenarioName);
		new JButtonOperator(fileChosser, new TipNameButtonFinder(jmap.getScenarioDialogSelectButton())).clickMouse();
		return 0;
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
		jemmyOperation.pushButtonAndWaitForDialog(mainFrame, jmap.getNewScenarioButton(), jmap.getNewScenarioWin());
		JFileChooserOperator fileChosser = new JFileChooserOperator();
		fileChosser.setCurrentDirectory(scenariosFile);
		fileChosser.chooseFile(name);
		new JButtonOperator(fileChosser, new TipNameButtonFinder(jmap.getNewScenarioSaveButton())).clickMouse();
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
	 * pushes the addTestsButton to add the already checked tests into scenario
	 * Tree
	 * 
	 * @throws Exception
	 */
	public int moveCheckedToScenarioTree(int amount) throws Exception {
		jemmyOperation.setSpinnerValue(mainFrame, jmap.getNumOfTestToAddSpinner(), new Integer(amount));
		jemmyOperation.pushButton(mainFrame, jmap.getAddTestsButton());
		jemmyOperation.ConfirmDialogIfExists(jmap.getWarningDialogWin(), jmap.getDialogSelectOKButton());
		jemmyOperation.WaitForDialogToClose(jmap.getAddTestsDialog()); // wait
																		// for
																		// progress
																		// bar
																		// to
																		// close
		return ((Long) TestTreePanel.getCurrentSelectedTests()).intValue();
	}

	/**
	 * returns the number of currently selected check boxes in the tests tree.
	 * 
	 * @return numOfselectedTests
	 */
	public int getNumOfchkBoxChekced() {
		return ((Long) TestTreePanel.getCurrentSelectedTests()).intValue();
	}

	/**
	 * Get the mapped tests in current scenario .
	 * 
	 * @return String. Example : int array {1,2,3} will be returned as String
	 *         "1,2,3"
	 * @throws Exception
	 */
	public String getMappedTestsInScenario() throws Exception {
		int[] intRes = ScenariosManager.getInstance().getCurrentScenario().getEnabledTestsIndexes();
		return StringUtils.intArrToString(intRes);
	}

	/**
	 * returns the number of checked test in scenario tree
	 * 
	 * @return int
	 * @throws Exception
	 */
	public int getNumOfMappedTestsInScenario() throws Exception {
		int[] intRes = ScenariosManager.getInstance().getCurrentScenario().getEnabledTestsIndexes();
		return intRes.length;
	}

	/**
	 * This will return the number of tests in the current scenario including
	 * tests in sub scenarios
	 * 
	 * @return
	 * @throws Exception
	 */
	public Integer getNumOfTestsInScenario() throws Exception {
		return ScenariosManager.getInstance().getCurrentScenario().getTests().size();
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
		selectTestsRows(new int[] { testIndex });
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		testInfoTab.setSelectedIndex(2);
		JTabbedPaneOperator paramTab = new JTabbedPaneOperator(testInfoTab, 1);
		paramTab.selectPage(tab);
		JTableOperator paramTable = new JTableOperator(paramTab);
		for (int i = 0; i < paramTable.getRowCount(); i++) {
			Object tableValue = paramTable.getValueAt(i, 0);
			if (tableValue != null && tableValue.toString().startsWith(paramName)) {
				jemmyOperation.setTableCell(paramTable, i, 3, value, isCombo);
				testInfoTab.getSource().repaint();
				break;
			}
		}

		Thread.sleep(200);
		if (isScenario) {
			String buttonText = approve ? "Yes" : "No";
			jemmyOperation.pushButtonAndWaitAndApproveDialog(mainFrame.getTitle(), "Apply for Scenario",
					"Apply Scenario Parameters", buttonText);
		}

		return 0;
	}

	public int selectSenario(String scenarioName) throws Exception {
		File scenariosFile = new File(JSystemProperties.getInstance()
				.getPreference(FrameworkOptions.TESTS_CLASS_FOLDER), "scenarios");
		jemmyOperation.pushButtonAndWaitForDialog(mainFrame, jmap.getOpenScenarioButton(), jmap.getScenarioSelectWin());
		JFileChooserOperator fileChosser = new JFileChooserOperator();
		fileChosser.setCurrentDirectory(scenariosFile);
		fileChosser.chooseFile(scenarioName);
		new JButtonOperator(fileChosser, new TipNameButtonFinder(jmap.getScenarioDialogSelectButton())).clickMouse();
		return 0;
	}

	/**
	 * does Gui save as operation on the scenario
	 * 
	 * @param newScenarioName
	 * @return
	 * @throws Exception
	 */
	public int copyScenario(String newScenarioName) throws Exception {
		jemmyOperation.pushButtonAndWaitForDialog(mainFrame, jmap.getCopyScenarioButton(), jmap.getScenarioCopyWin());
		JFileChooserOperator fileChosser = new JFileChooserOperator();
		fileChosser.chooseFile(newScenarioName);
		new JButtonOperator(fileChosser, new TipNameButtonFinder(jmap.getScenarioCopyButton())).clickMouse();
		return 0;
	}

	public String getReportDir() throws Exception {
		return System.getProperty("user.dir") + File.separator
				+ JSystemProperties.getInstance().getPreference(FrameworkOptions.LOG_FOLDER) + File.separator
				+ "current" + File.separator + "reports.0.xml";
	}

	public synchronized Boolean addLoopObject() throws Exception {
		return jemmyOperation.pushButton(mainFrame, (Object) jmap.getLoopButton());
	}

	public synchronized Boolean addIfObject() throws Exception {
		return jemmyOperation.pushButton(mainFrame, (Object) jmap.getIfButton());
	}

	public synchronized Boolean addElseIfObject() throws Exception {
		return jemmyOperation.pushButton(mainFrame, (Object) jmap.getElseIfButton());
	}

	public synchronized Boolean addSwitchObject() throws Exception {
		return jemmyOperation.pushButton(mainFrame, (Object) jmap.getSwitchButton());
	}

	public synchronized Boolean addCaseObject() throws Exception {
		return jemmyOperation.pushButton(mainFrame, (Object) jmap.getCaseButton());
	}

	public Integer collapseExpandScenario(int testIndex) throws Exception {
		if (scenarioTree.isCollapsed(testIndex)) {
			scenarioTree.expandPath(scenarioTree.getPathForRow(testIndex));
		} else {
			scenarioTree.collapsePath(scenarioTree.getPathForRow(testIndex));
		}

		Thread.sleep(100);
		return 0;
	}

	public Integer selectTest(int row) {
		return selectTestsRows(new int[] { row });
	}
}
