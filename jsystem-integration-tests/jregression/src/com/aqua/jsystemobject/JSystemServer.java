/*
 * Created on 22/11/2006
 *
 * Copyright 2005 AQUA Software, LTD. All rights reserved.
 * AQUA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.aqua.jsystemobject;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.tree.TreePath;

import jsystem.extensions.report.html.summary.HtmlSummaryReporter;
import jsystem.framework.DataType;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.fixture.FixtureManager;
import jsystem.framework.report.ExecutionListener;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioParameter;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.SutEditorManager;
import jsystem.treeui.TestRunner;
import jsystem.treeui.TestTreeView;
import jsystem.treeui.actionItems.InitReportersAction;
import jsystem.treeui.params.ParametersPanel;
import jsystem.treeui.params.ParametersTableFileChooser;
import jsystem.treeui.teststable.ScenarioTreeNode;
import jsystem.treeui.tree.TestTreePanel;
import jsystem.utils.BackgroundRunnable;
import jsystem.utils.FileLock;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.DialogWaiter;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.DialogOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFileChooserOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JMenuOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextAreaOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.operators.Operator.StringComparator;

import utils.Commons;
import utils.DebugLogFile;
import utils.ScenarioModelUtils;

import com.aqua.jsystemobject.JSystemClient.JsystemPropertiesDialogButton;

public class JSystemServer implements JSystem, ExecutionListener {

	TestRunner runner;

	JsystemMapping jmap;

	String mainWindow;

	volatile boolean runEnd = false;

	volatile int runEndCount;

	volatile boolean executionEnd = false;

	volatile boolean testEnded = false;

	JFrameOperator mainFrame;

	JTreeOperator testsTree;

	JTreeOperator scenarioTree;

	JemmySupport jemmyOperation;

	private static Logger log = Logger.getLogger(HtmlSummaryReporter.class.getName());

	boolean launched = false;

	private DebugLogFile logFile = DebugLogFile.getInstance();

	public JSystemServer() {
		jemmyOperation = new JemmySupport();
		try {
			setInitialJsystemProperties();
			setJSystemProperty(FrameworkOptions.HTML_ZIP_DISABLE.toString(), true + "");
			setJSystemProperty(FrameworkOptions.DISABLE_OLD_SCENARIO_CONVERT.toString(), "never-5.1");
		} catch (Exception e) {
			log.warning("Failed to set initial values for Jsystem Properties!");
		}
	}

	public int getWebServerPort() {
		return JServer.getPort();
	}

	public String toString() {
		return "jsystem server";

	}

	public String launch(final String sutFile) throws Exception {
		return launch(sutFile, true);
	}

	public String launch(boolean disableZip) throws Exception {
		return launch(null, disableZip);
	}

	private Thread t;
	BackgroundRunnable runnable;

	public String launch(final String sutFile, boolean disableZip) throws Exception {
		try {

			// System.out.println("the debug fileis: "+DebugLogFile.getInstance().getLogFileName());
			logFile.writeToDebug("\nStarting server launch");
			if (launched) {
				logFile.writeToDebug("Runner Already launched!");
				return "ok. Runner Already Launched";
			}

			t = null;
			runnable = null;
			if (!StringUtils.isEmpty(sutFile)) {
				logFile.writeToDebug("sut file = " + sutFile);
				runnable = new BackgroundRunnable() {
					public void internalRun() throws Exception {
						DialogOperator operator = new DialogOperator("Select SUT");
						JComboBoxOperator comboOperator = new JComboBoxOperator(operator);
						comboOperator.selectItem(sutFile);
						JButtonOperator buttonOperator = new JButtonOperator(operator, "OK");
						buttonOperator.push();
					}
				};
				t = new Thread(runnable);
				t.start();
			}

			logFile.writeToDebug("initiating JMapping");
			jmap = new JsystemMapping();
			mainWindow = jmap.getJSyetemMain();
			logFile.writeToDebug("initiating Jemmy Operation");
			logFile.writeToDebug("adding listener");
			setJSystemProperty(FrameworkOptions.HTML_ZIP_DISABLE.toString(), disableZip + "");
			ListenerstManager.getInstance().addListener(this);
			logFile.writeToDebug("starting runner");
			waitForLockFileToBeReleased();

			new ClassReference(TestRunner.class.getName()).startApplication();
			logFile.writeToDebug("getting mainFrame");
			mainFrame = new JFrameOperator(jmap.getJSyetemMain());
			JTabbedPaneOperator testTreeTab = new JTabbedPaneOperator(mainFrame, jmap.getTestTreeTab());

			logFile.writeToDebug("getting test tree");
			testsTree = new JTreeOperator(testTreeTab);
			scenarioTree = new JTreeOperator(mainFrame, 0);
			logFile.writeToDebug("set freeze on fail");
			setFreezeOnFail(false);
			if (t != null) {
				t.join();
				if (runnable.getThrowable() != null) {
					throw new Exception("SUT file, " + sutFile + " was not found as expected during launch",
							runnable.getThrowable());
				}
			}
			launched = true;
			JServer.isRunnerActive = true;
		} catch (Exception e) {
			return StringUtils.getStackTrace(e);
		}
		return "ok";

	}

	// public int launch2(final String sutFile,boolean disableZip) throws
	// Exception {
	// if (launched) {
	// return 0;
	// }
	// logFile.writeToDebug("initiating JMapping");
	// jmap = new JsystemMapping();
	// mainWindow = jmap.getJSyetemMain();
	// logFile.writeToDebug("initiating Jemmy Operation");
	// logFile.writeToDebug("adding listener");
	// setJSystemProperty(FrameworkOptions.HTML_ZIP_DISABLE.toString(),disableZip+"");
	// ListenerstManager.getInstance().addListener(this);
	// logFile.writeToDebug("starting runner");
	// waitForLockFileToBeReleased();
	//
	// new ClassReference(TestRunner.class.getName()).startApplication();
	// logFile.writeToDebug("getting mainFrame");
	// mainFrame = new JFrameOperator(jmap.getJSyetemMain());
	// JTabbedPaneOperator testTreeTab = new JTabbedPaneOperator(mainFrame,
	// jmap.getTestTreeTab());
	//
	// logFile.writeToDebug("getting test tree");
	// testsTree = new JTreeOperator(testTreeTab);
	// scenarioTree = new JTreeOperator(mainFrame, 0);
	// logFile.writeToDebug("set freeze on fail");
	// setFreezeOnFail(false);
	// if (t != null) {
	// t.join();
	// if (runnable.getThrowable() != null) {
	// throw new Exception("SUT file, " + sutFile +
	// " was not found as expected during launch", runnable.getThrowable());
	// }
	// }
	// launched = true;
	// JServer.isRunnerActive = true;
	// return 0;
	// }

	/**
	 * checks the runner lock file number of times, in order to check that the
	 * last runner was closed properly
	 * 
	 * @throws Exception
	 */
	public void waitForLockFileToBeReleased() throws Exception {
		FileLock lock = FileLock.getFileLock(CommonResources.LOCK_FILE);
		int retries = 12;
		int i = 0;
		while (i < retries) {
			if (!lock.grabLock()) {
				Thread.sleep(5000);
				i++;
			} else {
				lock.releaseLock();
				return;
			}
		}
	}

	public String launch() throws Exception {
		return launch(null);
	}

	public int extract(String envZipPath) throws Exception {
		FileUtils.extractZipFile(new File(envZipPath), new File("tmpExtractDir"));
		return 0;
	}

	public int exit() throws Exception {
		JServer.exit();
		return 0;
	}

	@Override
	public int activateExportWizard(final String jarPath, final boolean exportTests, final boolean exportScenarios,
			final boolean exportRunner, final boolean exportLog, final boolean exportSut, final boolean exportLib,
			final boolean exportJdk) throws Exception {
		new JButtonOperator(mainFrame, new TipNameButtonFinder(jmap.getExportButton())).push();
		JDialogOperator dialog = new JDialogOperator(jmap.getExportWin());
		new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getExportNextButton())).push();
		jemmyOperation.setCheckBox(dialog, jmap.getExportTestsCheckbox(), exportTests);
		jemmyOperation.setCheckBox(dialog, jmap.getExportScenariosCheckbox(), exportScenarios);
		jemmyOperation.setCheckBox(dialog, jmap.getExportRunnerCheckbox(), exportRunner);
		jemmyOperation.setCheckBox(dialog, jmap.getExportLogCheckbox(), exportLog);
		jemmyOperation.setCheckBox(dialog, jmap.getExportSutCheckbox(), exportSut);
		jemmyOperation.setCheckBox(dialog, jmap.getExportLibCheckbox(), exportLib);
		jemmyOperation.setCheckBox(dialog, jmap.getExportJdkCheckbox(), exportJdk);
		Thread.sleep(2000);
		new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getExportNextButton())).push();
		new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getExportFinishButton())).push();
		waitForWaitExportProcessDialogWin();
		verifyNoErrorWindow("Failed in export wizard");
		return 0;
	}

	public synchronized boolean play(boolean block) throws Exception {
		JButtonOperator btnOp = jemmyOperation.getButtonOperator(mainFrame, jmap.getPlayButton());
		// if the play button is enabled we can run the tests and wait
		// for run tests
		if (btnOp.isEnabled()) {
			jemmyOperation.pushButton(mainFrame, jmap.getPlayButton());
			runEnd = false;
			runEndCount = 0;
			executionEnd = false;

			if (block) {
				waitForExecutionEnd();
			}
			return true;
		}

		return false;
	}

	/**
	 * saves the current scenario configuration
	 * 
	 * @throws Exception
	 */
	public int saveScenario() throws Exception {
		jemmyOperation.report("saving the scenario");
		jemmyOperation.pushButton(mainFrame, jmap.getSaveScenarioButton());
		return 0;
	}

	public int setJSystemProperty(String key, String value) throws Exception {
		JSystemProperties.getInstance().setPreference(key, value);
		jemmyOperation.report("Added " + key + " = " + value + " to jsystem properties file");
		return 0;
	}

	public void setInitialJsystemProperties() throws Exception {
		for (Entry<FrameworkOptions, String> entry : Commons.getBaseJsystemProperties().entrySet()) {
			setJSystemProperty(entry.getKey().toString(), entry.getValue());
		}
	}

	public String getUserDir() {
		return System.getProperty("user.dir");
	}

	public synchronized int waitForExecutionEnd() throws Exception {
		while (!executionEnd) {
			wait();
		}
		return 0;
	}

	private synchronized int waitForRunEnd() throws Exception {
		while (!runEnd) {
			wait();
		}
		return 0;
	}

	// When the Run repeat itself <amount> times
	public synchronized int waitForRunEnd(int amount) throws Exception {
		while (runEndCount < amount) {
			waitForRunEnd();
			runEnd = false;
		}
		return 0;
	}

	// When the Run repeat itself <amount> times
	public synchronized int waitForRunEndUntilLeftRepeatAmountIs(int amount) throws Exception {
		do {
			waitForRunEnd();
			runEnd = false;
			// give the ui some time to refresh
			Thread.sleep(1000);
		} while (amount != getLeftRepeatAmount());
		return 0;
	}

	public int setUserDir(String userDir) {
		System.setProperty("user.dir", userDir);
		return 0;
	}

	public void addWarning(Test test) {
		// TODO Auto-generated method stub

	}

	public void startTest(TestInfo testInfo) {
		// TODO Auto-generated method stub

	}

	public synchronized void endRun() {
		runEnd = true;
		runEndCount++;
		notifyAll();
	}

	public void addError(Test arg0, Throwable arg1) {
		System.out.println("err");

	}

	public void addFailure(Test arg0, AssertionFailedError arg1) {
		System.out.println("failure");

	}

	/**
	 * wait for test to end, and reset the testEnded flag to false.
	 * 
	 * @return
	 * @throws Exception
	 */
	public synchronized int waitForTestToEnd() throws Exception {
		while (!testEnded) {
			wait();
		}
		testEnded = false;
		return 0;
	}

	/**
	 * when a test ends, set testEnded to true and notify all threads to recheck
	 * their flags.
	 */
	public synchronized void endTest(Test arg0) {
		System.out.println("end test");
		testEnded = true;
		notifyAll();
	}

	/**
	 * Deletes current scenario and reselects it.
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
		createScenario(currentScenario);
		return 0;
	}

	/**
	 * get current scenario name.
	 */
	public String getCurrentScenario() throws Exception {
		String currentScenario = ScenariosManager.getInstance().getCurrentScenario().getName();
		return currentScenario;
	}

	/**
	 * Waits for the runner's warning dialog to open and presses the OK button.
	 */
	public int waitForWarningDialog() throws Exception {
		Thread.sleep(500);
		JDialogOperator dialog = new JDialogOperator(jmap.getWarningDialogWin());
		jemmyOperation.report("dialog reference value = " + dialog);
		JButtonOperator jbo = new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getDialogSelectOKButton()));
		jbo.push();
		return 0;
	}

	/**
	 * Waits for the runner's error dialog to open and presses th OK button.
	 */
	public int waitForErrorDialog() throws Exception {
		JDialogOperator dialog = new JDialogOperator(jmap.getErrorDialogWin());
		new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getDialogSelectOKButton())).push();
		return 0;
	}

	private int waitForWaitExportProcessDialogWin() throws Exception {
		JDialogOperator dialog = new JDialogOperator(jmap.getWaitExportProcessDialogWin());
		while (dialog.isShowing()) {
		}
		return 0;
	}

	private int verifyNoErrorWindow(String message) throws Exception {
		try {
			JDialogOperator dialog = new JDialogOperator(jmap.getErrorDialogWin());
			new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getDialogSelectOKButton())).push();
			throw new Exception(message + " Error dialog was found");
		} catch (Exception e) {
			if (e.toString().contains("Timeout")) {
				System.out.println("Error dialog was not found");
			} else {
				throw e;
			}
		}
		return 0;
	}

	public int waitForAlongTime() throws Exception {
		Thread.sleep(10 * 1000);
		return 0;
	}

	public int selectSenario(String scenarioName) throws Exception {
		if (scenarioName.startsWith(ScenarioModelUtils.SCENARIO_HEADER)) {
			scenarioName = ScenarioModelUtils.getScenarioNameRelativeToScenariosFolder(scenarioName);
		}
		log.info(">>>>> selectScenario + " + scenarioName + " >>>>>");
		File scenariosFile = new File(JSystemProperties.getInstance()
				.getPreference(FrameworkOptions.TESTS_CLASS_FOLDER), "scenarios");
		jemmyOperation
				.pushButtonAndWaitForDialog(mainWindow, jmap.getOpenScenarioButton(), jmap.getScenarioSelectWin());
		JFileChooserOperator fileChosser = new JFileChooserOperator();
		fileChosser.setCurrentDirectory(scenariosFile);
		fileChosser.chooseFile(scenarioName);
		jemmyOperation.pushButton(fileChosser, jmap.getScenarioDialogSelectButton());
		waitForScenarioLoadingToFinish();
		return 0;
	}

	public int stop() throws Exception {
		jemmyOperation.report("\n\n >>>>>>>>>>> inside stop() function <<<<<<<<<<<<<<<<\n\n");
		log.info("\n\n >>>>>>>>>>> inside stop() function <<<<<<<<<<<<<<<<\n\n");
		JDialogOperator gracefulDialog = jemmyOperation.pushButtonAndWaitForDialog(mainFrame, jmap.getStopButton(),
				jmap.getGracefulDialog());
		log.info("\n\n >>>>>>>>>>> after pushing the stop dialog and waiting for dialog to open <<<<<<<<<<<<<<<<\n\n");
		jemmyOperation
				.report("\n\n >>>>>>>>>>> after pushing the stop dialog and waiting for dialog to open <<<<<<<<<<<<<<<<\n\n");
		jemmyOperation.pushButton(gracefulDialog, jmap.getStopImmediatelyButton());
		jemmyOperation.report("\n\n >>>>>>>>>>> after pushing the stop immediately button <<<<<<<<<<<<<<<<\n\n");
		jemmyOperation.WaitForDialogToClose(jmap.getGracefulDialog());
		jemmyOperation.report("\n\n\nreturning from JSystemServer.stop() successfully!!!\n\n\n");
		return 0;
	}

	public int gracefulStop() throws Exception {
		jemmyOperation.pushButton(mainFrame, jmap.getStopButton());
		jemmyOperation.WaitForDialogToClose(jmap.getGracefulDialog());
		return 0;
	}

	public int initReporters() throws Exception {
		JMenuBarOperator bar = new JMenuBarOperator(mainFrame);
		bar.pushMenu(jmap.getInitReportMenu(), "|");
		jemmyOperation.WaitForDialogToClose(jmap.getInitReportDialogTitle());
		jemmyOperation.report("inside initReporters_1");
		waitForInitReportersToEnd();
		jemmyOperation.report("inside initReporters_2");
		return 0;
	}

	private void waitForInitReportersToEnd() throws Exception {
		jemmyOperation.report("inside JSystemServer.waitForInitReortersToEnd_1");
		InitReportersAction.getInstance().waitForInitReportersToEnd();
		jemmyOperation.report("inside JSystemServer.waitForInitReortersToEnd_1");
	}

	@SuppressWarnings("deprecation")
	public String getCurrentVersion() throws Exception {
		String RC = null;
		JMenuBarOperator JSystemMenu = new JMenuBarOperator(mainFrame);
		JSystemMenu.pushMenu(jmap.getHelpMenu(), "|");

		JDialogOperator JSysDialog = new JDialogOperator("About");
		JSysDialog.activateWindow();
		// JTextFieldOperator JText = new JTextFieldOperator(null);
		//
		// RC = JText.getDisplayedText();

		return RC;
	}

	/**
	 * 
	 * @param location
	 * @param className
	 * @param methodName
	 * @return
	 * @throws Exception
	 */
	public int addTest(int location, String methodName, String className,boolean equals) throws Exception {
		selectTestsRows(new int[] { location });
		return addTest(methodName, className, equals);
	}

	/**
	 * add a single test amount times
	 * 
	 * @param className
	 * @param methodName
	 * @param amount
	 *            how many times to add to scenario
	 * @return
	 * @throws Exception
	 */
	public int addTest(String methodName, String className, int amount, boolean equals) throws Exception {
		try {
			return _addTest(methodName, className, amount, equals);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new Exception(t);
		}
	}

	public int addTest(String node, String parentNode, boolean equals) throws Exception {
		addTest(node, parentNode, 1, equals);
		return 0;
	}

	/**
	 * 
	 * @param node
	 *            = class name
	 * @param parentNode
	 *            = method name
	 * @param amount
	 * @return int
	 * @throws Exception
	 */
	private int _addTest(String node, String parentNode, int amount, boolean equals) throws Exception {
		System.out.println("Adding test " + node + " from class " + parentNode);
		if (checkTestInTestsTree(node, parentNode, true, equals) < 0) {
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
		jemmyOperation.WaitForDialogToClose(jmap.getAddTestsDialog()); // wait
																		// for
																		// progress
																		// bar
																		// to
																		// close
		return ((Long) TestTreePanel.getCurrentSelectedTests()).intValue();
	}

	/**
	 * gets a test name and a test class name and returns the path to the
	 * requested test.
	 * 
	 * @param node
	 * @param parentNode
	 * @return
	 * @throws Exception
	 */
	private TreePath getTreePath(String node, String parentNode, boolean equals) throws Exception {
		// if parent node or method name are empty string, throw exception
		if (StringUtils.isEmpty(node) || StringUtils.isEmpty(parentNode)) {
			throw new Exception("empty test Parent node or method is not aloud: Give both parent and method name!!!");
		}
		// if parent node is "script" parentNode will be ""
		if (JSystem.SCENARIO.equals(parentNode) || JSystem.SCRIPT.equals(parentNode)
				|| JSystem.RANDOM.equals(parentNode)) {
			parentNode = "";
		}
		JTabbedPaneOperator testTreeTab = new JTabbedPaneOperator(mainFrame, 0);
		testTreeTab.selectPage(jmap.getTestTreeTab());// give control on
														// relevant tab
		int c = testsTree.getRowCount();
		System.out.println("Row count: " + c);
		TreePath foundPath = null;
		// method is not null and is equal to Random
		if (node != null && node.equals("Random")) { // Then we want to select a
														// test Randomly

			Random generator = new Random();
			TreePath path = testsTree.getPathForRow(generator.nextInt(c));// get
																			// path
																			// to
																			// a
																			// random
																			// row
																			// in
																			// row
																			// count
			Object[] pathElements = path.getPath();
			String CurrentPath = pathElements[pathElements.length - 1].toString();

			while (CurrentPath.indexOf("test") == -1) { // If we didn't find a
														// test
				path = testsTree.getPathForRow(generator.nextInt(c));
				pathElements = path.getPath();
				CurrentPath = pathElements[pathElements.length - 1].toString();
				System.out.println("CurrentPath " + CurrentPath);

			}
			System.out.println("selected random Node");
			foundPath = path;
		}
		// if a parentNode and a node had been passed, run the node under that
		// specific Parent.
		else if (!node.equals("") && !parentNode.trim().equals("")) {
			try {
				for (int i = 0; i < c; i++) {
					TreePath path = testsTree.getPathForRow(i);
					System.out.println("Path: " + path);
					Object[] pathElements = path.getPath();

					// if path to test has less then two elements it surely
					// doesn't have test and testClass in it to run.
					if (pathElements == null || pathElements.length < 2) {
						continue;
					}

					Object node1 = pathElements[pathElements.length - 1];
					Object node2 = pathElements[pathElements.length - 2];
					if (node1 == null || node2 == null) {
						continue;
					}

					//TODO handle the situation when an equals required and startWith required 
					// check that node and parent node exist.
					if(equals) {
						if (node1.toString().equals(node) && parentNode.startsWith(node2.toString()) && node1 != null
								&& node2 != null) {
							foundPath = path;
							break;
						}
					}
					else{
						if (node1.toString().startsWith(node) && parentNode.startsWith(node2.toString()) && node1 != null
								&& node2 != null) {
							foundPath = path;
							break;
						}
					}
				}
			} catch (Exception e) {
				throw new Exception("My Exception \n\n" + StringUtils.getStackTrace(e));
			}
			System.out.println("searched for test With parent ->" + parentNode + " , is empty= "
					+ parentNode.trim().isEmpty());
		} else if (!node.equals("") && parentNode.trim().equals("")) {
			System.out.println("search for test Without parent");
			for (int i = 0; i < c; i++) {
				TreePath path = testsTree.getPathForRow(i);
				System.out.println("Path: " + path);
				Object[] pathElements = path.getPath();
				if (pathElements == null || pathElements.length == 0) {
					continue;
				}

				if (pathElements[pathElements.length - 1].toString().equals(node)) {
					foundPath = path;
					break;
				}
			}
			System.out.println("searched for test Without parent");
		}

		if (foundPath == null) {
			throw new Exception("Path not found node: " + node + ", parent: " + parentNode);
		}
		return foundPath;
	}

	public int selectTestsRows(int... rows) {
		JTreeOperator scenarioTree = new JTreeOperator(mainFrame, 0);
		TreePath[] pathes = new TreePath[rows.length];

		if (rows.length < 1) {
			return 0;
		}
		pathes[0] = scenarioTree.getPathForRow(rows[0]);
		scenarioTree.clickOnPath(pathes[0], 1);
		if (scenarioTree.isLargeModel()) {
			// From some reason, when using large model, we don't get
			// verification that the selection was successful.
			scenarioTree.setVerification(false);
		}
		for (int i = 0; i < pathes.length; i++) {
			scenarioTree.selectRow(rows[i]);
			scenarioTree.addSelectionRow(rows[i]);
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			log.warning("Failed to sleep after tests selection");
		}
		return 0;
	}

	public int selectTest(int row) {
		System.out.println("****1. Selecting test");
		return selectTestsRows(row);
	}

	public int deleteTest(int testIndex) throws Exception {
		selectTestsRows(new int[] { testIndex });
		jemmyOperation.pushButton(mainFrame, jmap.getRemoveTestsButton());
		return 0;
	}

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
	 * iterates over all tests in scenarioTree looking for test testName. when
	 * finds it, returns the index for the test under the scenario. the check is
	 * made against the model and not the Gui because of restrictions. DO NOT
	 * choose this method of operation regularly.
	 * 
	 * @param scenarioName
	 * @param testName
	 * @return
	 * @throws Exception
	 */
	public int getTestIndexInScenario(String scenarioName, String testName) throws Exception {
		Vector<JTest> allTests = ScenariosManager.getInstance().getScenario(scenarioName).getRootTests();
		Iterator<JTest> iter = allTests.iterator();
		int i = 1;
		while (iter.hasNext()) {
			RunnerTest test = (RunnerTest) iter.next();
			if (test.getMethodName().equals(testName)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public int moveTestUp(int testIndex) throws Exception {
		selectTestsRows(new int[] { testIndex });
		jemmyOperation.pushButton(mainFrame, jmap.getMoveTestUpButton());
		return 0;
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

	// /**
	// * Right click on root scenario and select "Restore Parameters to default"
	// * @return
	// * @throws Exception
	// */
	// public int restoreScenarioParametersToDefault() throws Exception {
	// TreePath foundPath = scenarioTree.getPathForRow(0);
	// if (foundPath == null) {
	// throw new Exception("Path not found test index: " + 0);
	// }
	// JPopupMenuOperator pp = new
	// JPopupMenuOperator(scenarioTree.callPopupOnPath(foundPath));
	//
	// pp.pushMenu(jmap.getScenarioRestoreDefaultsMenuItem());
	// return 0;
	// }
	//
	public int moveTestDown(int testIndex) throws Exception {
		selectTestsRows(new int[] { testIndex });
		jemmyOperation.pushButton(mainFrame, jmap.getMoveTestDownButton());
		return 0;
	}

	/**
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

	public int CollapseExpandScenario(int testIndex) throws Exception {
		if (scenarioTree.isCollapsed(testIndex)) {
			scenarioTree.expandPath(scenarioTree.getPathForRow(testIndex));
		} else {
			scenarioTree.collapsePath(scenarioTree.getPathForRow(testIndex));
		}

		Thread.sleep(100);
		return 0;
	}

	public int filterSuccess() throws Exception {
		TestRunner.treeView.tableController.filterSuccess(ScenariosManager.getInstance().getCurrentScenario());
		return 0;
	}

	public int copyScenario(String newScenarioName) throws Exception {
		jemmyOperation.pushButtonAndWaitForDialog(mainWindow, jmap.getCopyScenarioButton(), jmap.getScenarioCopyWin());
		JFileChooserOperator fileChosser = new JFileChooserOperator();
		fileChosser.chooseFile(newScenarioName);
		new JButtonOperator(fileChosser, new TipNameButtonFinder(jmap.getScenarioCopyButton())).clickMouse();
		return 0;
	}

	public int playPause() throws Exception {
		jemmyOperation.pushButton(mainFrame, jmap.getPauseButton());
		return 0;
	}

	/**
	 * returns true if the play button is enabled
	 * 
	 * @throws Exception
	 */
	public boolean checkIfPlayIsEnabled() throws Exception {
		JButtonOperator btnOp = jemmyOperation.getButtonOperator(mainFrame, jmap.getPlayButton());
		return btnOp.isEnabled();
	}

	public int changeSut(String sutName) throws Exception {
		return selectSut(sutName);
	}

	public int refresh() throws Exception {
		jemmyOperation.pushButton(mainFrame, jmap.getRefreshButton());
		Thread.sleep(5000);
		// runner.handleEvent(TestTreeView.REFRESH_EVENT, null);
		return 0;
	}

	public int showLog() throws Exception {
		jemmyOperation.pushButton(mainFrame, jmap.getLogButton());

		return 0;
	}

	public int editSut() throws Exception {
		SutEditorManager.getInstance().launchEditor();
		return 0;
	}

	public boolean changeTestDir(String dir, String sut, boolean sutComboMustOpen) throws Exception {
		jemmyOperation
				.pushButtonAndWaitForDialog(mainWindow, jmap.getSwitchProjectButton(), jmap.getSwitchProjectWin());
		JDialogOperator dialog = new JDialogOperator(jmap.getSwitchProjectWin());
		new JFileChooserOperator().chooseFile(dir);
		if (sut == null || sut.equals("")) { // no sut to select
			return true;
		}
		dialog = jemmyOperation.getDialogIfExists(jmap.getSUTWin(), 2);
		if (dialog == null) { // select sut dialog did not open
			if (sutComboMustOpen) { // sut wasn't open - error
				return false;
			}
			if (!sut.equals(getCurrentSutName())) { // current sut is not what
													// the user requested
				changeSut(sut); // sut changed to user requested sut
			}
			return true;
		}
		JComboBoxOperator combo = new JComboBoxOperator(dialog);
		int index = combo.findItemIndex(sut, new StringComparator() {
			public boolean equals(String arg0, String arg1) {
				return arg0.trim().toLowerCase().equals(arg1.trim().toLowerCase());
			}
		});
		combo.selectItem(index);
		new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getSUTSelectButton())).push();
		return true;
	}

	public int selectSut(String sutName) throws Exception {
		JComboBoxOperator combo = getSutOperator();
		int index = combo.findItemIndex(sutName, new StringComparator() {
			public boolean equals(String arg0, String arg1) {
				return arg0.trim().toLowerCase().equals(arg1.trim().toLowerCase());
			}
		});
		combo.selectItem(index);
		return 0;
	}

	/**
	 * get the sut combo box operator
	 * 
	 * @return
	 */
	private JComboBoxOperator getSutOperator() {
		JComboBoxOperator combo = new JComboBoxOperator(mainFrame, new ComponentChooser() {
			public boolean checkComponent(Component comp) {
				return TestTreeView.SUT_COMBO_NAME.equals(comp.getName());
			}

			public String getDescription() {
				return TestTreeView.SUT_COMBO_NAME;
			}
		});

		return combo;
	}

	/**
	 * get the current selected sut name
	 * 
	 * @return
	 */
	public String getCurrentSutName() {
		return getSutOperator().getSelectedItem().toString();
	}

	public int openSutEditor(boolean expectError, boolean closeEditor) throws Exception {
		JButtonOperator button = new JButtonOperator(mainFrame, new ComponentChooser() {
			public boolean checkComponent(Component comp) {
				if (!(comp instanceof JButton)) {
					return false;
				}
				return "Edit Sut".equals(((JButton) comp).getToolTipText());
			}

			public String getDescription() {
				return "Edit Sut";
			}
		});

		button.push();

		if (expectError) {
			waitForErrorDialog();
			return 0;
		}

		if (closeEditor) {
			DialogOperator dialog = new DialogOperator("Edit SUT fields");
			dialog.waitComponentShowing(true);
			button = new JButtonOperator(dialog, new ComponentChooser() {
				public boolean checkComponent(Component comp) {
					if (!(comp instanceof JButton)) {
						return false;
					}
					return "Save".equals(((JButton) comp).getText());
				}

				public String getDescription() {
					return "";
				}
			});

			button.push();
		}
		return 0;
	}

	public int setFreezeOnFail(boolean freeze) throws Exception {
		jemmyOperation.setCheckBox(freeze, mainWindow, 2);
		return 0;
	}

	public int setReapit(boolean reapit) throws Exception {
		jemmyOperation.setCheckBox(reapit, mainWindow, 1);
		return 0;
	}

	public int setRepAmount(int amount) throws Exception {
		JTextFieldOperator jt = new JTextFieldOperator(mainFrame, jmap.getRepeatAmountEdit());
		jt.setText(Integer.toString(amount));
		return 0;
	}

	public boolean checkIsRepeatSet(boolean status) throws Exception {
		JCheckBoxOperator jc = new JCheckBoxOperator(mainFrame, 1);
		return jc.isSelected();

	}

	private int getLeftRepeatAmount() throws Exception {
		JTextFieldOperator jt = new JTextFieldOperator(mainFrame, new NameChooser(TestTreeView.REPEAT_LEFT_NAME));
		System.out.println("repeat amont left " + Integer.parseInt(jt.getText()));
		return Integer.parseInt(jt.getText());
	}

	public int createScenario(String name) throws Exception {
		File scenariosFile = new File(JSystemProperties.getInstance()
				.getPreference(FrameworkOptions.TESTS_CLASS_FOLDER), "scenarios");
		jemmyOperation.pushButtonAndWaitForDialog(mainWindow, jmap.getNewScenarioButton(), jmap.getNewScenarioWin());
		JFileChooserOperator fileChosser = new JFileChooserOperator();
		fileChosser.setCurrentDirectory(scenariosFile);
		fileChosser.chooseFile(name);
		jemmyOperation.pushButton(fileChosser, jmap.getNewScenarioSaveButton());
		return 0;
	}

	/**
	 * added to solve long scenarios loading or new scenario creating that takes
	 * some time
	 * 
	 * @throws Exception
	 */
	private void waitForScenarioLoadingToFinish() throws Exception {
		jemmyOperation.WaitForDialogToClose(jmap.getLoadScenarioDialog());
		Thread.sleep(2000);
	}

	public String getReportDir() throws Exception {
		return System.getProperty("user.dir") + File.separator
				+ JSystemProperties.getInstance().getPreference(FrameworkOptions.LOG_FOLDER) + File.separator
				+ "current" + File.separator + "reports.0.xml";
	}

	/**
	 * get the Summary file path
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getSummaryFile() throws Exception {
		return System.getProperty("user.dir") + File.separator + "summary.properties";
	}

	public int checkTest(int testIndex, boolean check) throws Exception {
		TreePath foundPath = scenarioTree.getPathForRow(testIndex);
		if (foundPath == null) {
			throw new Exception("Path not found test index: " + testIndex);
		}
		JPopupMenuOperator pp = new JPopupMenuOperator(scenarioTree.callPopupOnPath(foundPath));

		pp.pushMenu(check ? jmap.getTestMapMenuItem() : jmap.getTestUnmapMenuItem());
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
	public int checkTestInTestsTree(String node, String parentNode, boolean check, boolean equals) throws Exception {
		TreePath foundPath = getTreePath(node, parentNode, equals );
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
	 * returns the number of currently selected check boxes in the tests tree.
	 * 
	 * @return numOfselectedTests
	 */
	public int getNumOfchkBoxChekced() {
		return ((Long) TestTreePanel.getCurrentSelectedTests()).intValue();
	}

	public String getJSystemProperty(String key) throws Exception {
		return JSystemProperties.getInstance().getPreference(key);
	}

	public int waitForFreezeDialog() throws Exception {
		Thread.sleep(500);
		JDialogOperator dialog = new JDialogOperator(jmap.getFreezeDialogWin());
		new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getFreezeDialogButton())).push();
		return 0;
	}

	/**
	 * compares a fixture name given to all fixture names in fixtures tab on the
	 * right side of the runner. if and when fined the fixture calls the jemmy
	 * select path on fixtureTree
	 * 
	 * @param fixtureName
	 * @throws Exception
	 */
	private void selectFixture(String fixtureName) throws Exception {
		JTabbedPaneOperator fixtureTab = new JTabbedPaneOperator(mainFrame, 0);// create
																				// an
																				// operator
																				// for
																				// a
																				// JTabbedPane
		fixtureTab.setSelectedIndex(3);// tab 3 in right side of runner
										// (fixtures tab)
		JTreeOperator fixtureTree = new JTreeOperator(fixtureTab);
		int c = fixtureTree.getRowCount();// take the num of rows in fixture
											// tree
		TreePath foundPath = null;
		// for each row, take the relevant path for that row, into an array of
		// objects
		// and compare the name of given fixture with each name in the
		// path(which is the last object in the array)
		for (int i = 0; i < c; i++) {
			TreePath path = fixtureTree.getPathForRow(i);
			// System.out.println(path);
			Object[] pathElements = path.getPath();
			if (pathElements == null || pathElements.length == 0) {
				continue;
			}
			if (fixtureName.equals(pathElements[pathElements.length - 1].toString())) {
				foundPath = path;
				break;
			}
		}
		if (foundPath == null) {
			throw new Exception("Path not found fixture: " + fixtureName);
		}
		fixtureTree.selectPath(foundPath);// select the correct path for the
											// fixture

	}

	public int failToFixture(String fixtureName) throws Exception {
		selectFixture(fixtureName);
		jemmyOperation.pushButton(mainFrame, jmap.getFixtureFailToButton());
		return 0;
	}

	public String getCurrentFixture() throws Exception {
		return FixtureManager.getInstance().getCurrentFixture();
	}

	/**
	 * selects the specified fixture in tree then presses the fixture goto
	 * button
	 */
	public int goToFixture(String fixtureName) throws Exception {
		selectFixture(fixtureName);
		jemmyOperation.pushButton(mainFrame, jmap.getFixtureGoToButton());
		return 0;
	}

	public int setCurrentFixture(String fixtureName) throws Exception {
		selectFixture(fixtureName);
		jemmyOperation.pushButton(mainFrame, jmap.getFixtureSetCurrentButton());
		return 0;
	}

	public int setDisableFixture(boolean disable) throws Exception {
		JTabbedPaneOperator fixtureTab = new JTabbedPaneOperator(mainFrame, 0);
		fixtureTab.selectPage("Fixture");
		JCheckBoxOperator jc = new JCheckBoxOperator(fixtureTab, 0);
		if (jc.isSelected() != disable) {
			jc.push();
		}
		return 0;
	}

	public int setTestParameter(int testIndex, String tab, String paramName, String value, boolean isCombo)
			throws Exception {
		return setTestParameter(testIndex, tab, paramName, value, isCombo, false);
	}

	public int setTestParameter(int testIndex, String tab, String paramName, String value, boolean isCombo,
			boolean isScenario) throws Exception {
		return setTestParameter(testIndex, tab, paramName, value, isCombo, isScenario, true);
	}

	public int setShowReferenceRecursively(int testIndex, boolean show) {
		selectTestsRows(new int[] { testIndex });
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		testInfoTab.setSelectedIndex(2);

		jemmyOperation.setCheckBox(testInfoTab, jmap.getRecursiveReferenceCheckBox(), show);
		return 0;
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

	public int setScenarioParameter(int testIndex, String paramName, String value, boolean approveRecursive,
			boolean approveUpdate) throws Exception {

		selectTestsRows(new int[] { testIndex });
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		testInfoTab.setSelectedIndex(2);

		JTabbedPaneOperator paramTab = new JTabbedPaneOperator(testInfoTab, 1);
		paramTab.selectPage(ScenarioParameter.SCENARIO_PARAMETERS_SECTION);
		JTableOperator paramTable = new JTableOperator(paramTab);
		for (int i = 0; i < paramTable.getRowCount(); i++) {
			Object tableValue = paramTable.getValueAt(i, 0);
			if (tableValue != null && tableValue.toString().startsWith(paramName)) {
				jemmyOperation.setTableCell(paramTable, i, 3, value, false);
				testInfoTab.getSource().repaint();
				break;
			}
		}

		Thread.sleep(200);
		String buttonText = approveRecursive ? jmap.getRecursiveDialogApprove() : jmap.getRecursiveDialogReject();
		jemmyOperation.pushButtonAndWaitAndApproveDialog(mainFrame.getTitle(), "Apply for Scenario",
				jmap.getRecursiveDialogTitle(), buttonText);
		if (approveRecursive) {
			buttonText = approveUpdate ? jmap.getRecursiveDialogApprove() : jmap.getRecursiveDialogReject();
			jemmyOperation.ConfirmDialogIfExists("Apply Scenario Parameters", buttonText);
		}

		return 0;
	}

	public int setTestUserDocumentation(int testIndex, String documentation) throws Exception {
		JTabbedPaneOperator docTab = getTestUserDocumentationTabbedPane(testIndex);
		JTextAreaOperator textAreaOperator = getTestUserDocumentationTextArea(docTab);
		textAreaOperator.setText(documentation);
		Thread.sleep(1000);
		jemmyOperation.pushButton(docTab, "Apply");
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

	public int publishReport(String description, String SUT, String version, String build, String station)
			throws Exception {
		return publishReportWithWait(description, SUT, version, build, station);
	}

	/**
	 * Metod name: publishReport Descriptin: This method publish the report of
	 * the last test activated. The method navigate to the publisher tab,
	 * presses the Publish button, The publish forum is opens, the method fill
	 * the report according to the parameters it recieves and presses the save
	 * for publishing the report. AND WAIT UNTIL PUBLISH DIALOG IS OPEN Params:
	 * 
	 * @param Description
	 *            - The Description to publish the report with
	 * @param SUT
	 *            - The version to publish the report with
	 * @param version
	 *            - The version to publish the report with
	 * @param Station
	 *            - The Station to publish the report with
	 * @throws Exception
	 * 
	 *             Retrun Values: None Author: Yaron Addar
	 */

	public int publishReportWithWait(String description, String SUT, String version, String build, String station)
			throws Exception {
		// Running publisg operations
		JTabbedPaneOperator testTreeTab = new JTabbedPaneOperator(mainFrame, 0);
		testTreeTab.selectPage(jmap.getPublisherTAB());
		jemmyOperation.report("sleep for 1 sec");
		Thread.sleep(1000);
		jemmyOperation.pushButton(mainFrame, jmap.getRefreshPublishButton());
		jemmyOperation.report("sleep for 1 sec");
		Thread.sleep(1000);
		jemmyOperation.pushButton(mainFrame, jmap.getPublishButton());
		JDialogOperator dialog = jemmyOperation.getDialogIfExists(jmap.getPublisherWin(), 5);
		// Setting publish report parameters
		new JTextFieldOperator(dialog, jmap.getPublisherDescriptionField()).setText(description); // Description
		new JTextFieldOperator(dialog, jmap.getPublisherSUTField()).setText(SUT); // Sut
																					// Name
		new JTextFieldOperator(dialog, jmap.getPublisherVersionField()).setText(version); // Version
		new JTextFieldOperator(dialog, jmap.getPublisherBuildField()).setText(build); // Build
		new JTextFieldOperator(dialog, jmap.getPublisherStationField()).setText(station); // Station

		int count = 0;
		JButtonOperator JB = new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getPublisherSaveButton()));
		while (!JB.isEnabled()) {
			Thread.sleep(1000);
			count++;
			if (count == 30) {
				throw new Exception("Failed publishing results. save button is disabled");
			}
		}

		// Activatig the publish
		jemmyOperation.pushDialogButton(dialog, jmap.getPublisherSaveButton(), true);
		verifyNoErrorWindow("Failed in publish. ");
		count = 0;
		while (dialog.isVisible()) {
			verifyNoErrorWindow("Failed in publish. ");
			count++;
			Thread.sleep(1000);
			if (count == 30) {
				throw new Exception("Time out in waiting for publish dialog to close");
			}
		}
		return 0;
	}

	public int addPublishResultEvent() throws Exception {
		jemmyOperation.pushButton(mainFrame, jmap.getPublishEventButton());
		return 0;
	}

	/**
	 * Metod name: publishReportWithoutSaving() Descriptin: This method run a
	 * test , open the publish form , supply all needed information and tnan
	 * cancel the publish operation. Params:
	 * 
	 * Retrun Values: None Author: Yaron Addar
	 */

	public int publishReportWithoutSaving() throws Exception {
		// Running publisg operations
		JTabbedPaneOperator testTreeTab = new JTabbedPaneOperator(mainFrame, 0);
		testTreeTab.selectPage(jmap.getPublisherTAB());
		Thread.sleep(1000);
		jemmyOperation.pushButton(mainFrame, jmap.getPublishButton());
		JDialogOperator dialog = new JDialogOperator(jmap.getPublisherWin());

		// Setting publish report parameters
		new JTextFieldOperator(dialog, jmap.getPublisherDescriptionField()).setText("ToBeNotSaved"); // Description
		new JTextFieldOperator(dialog, jmap.getPublisherSUTField()).setText("ToBeNotSaved"); // Sut
																								// Name
		new JTextFieldOperator(dialog, jmap.getPublisherVersionField()).setText("ToBeNotSaved"); // Version
		new JTextFieldOperator(dialog, jmap.getPublisherBuildField()).setText("ToBeNotSaved"); // Build
		new JTextFieldOperator(dialog, jmap.getPublisherStationField()).setText("ToBeNotSaved"); // Station

		JButtonOperator JB = new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getPublisherCancelButton()));
		int count = 0;
		while (!JB.isEnabled()) {
			Thread.sleep(1000);
			count++;
			if (count == 30) {
				throw new Exception("Failed publishing results. save button is disabled");
			}
		}

		// Cancling the publish operation
		jemmyOperation.pushDialogButton(dialog, jmap.getPublisherCancelButton(), true);

		return 0;
	}

	public void handleScenarioEditingWarning() throws Exception {
		JDialogOperator dialog = new JDialogOperator(jmap.getScenarioChangeWin());
		new JButtonOperator(dialog, jmap.getScenarioChangeButton()).clickMouse();
	}

	public int waitForMessage(String msgType) throws Exception {
		try {
			jemmyOperation.pushDialogButton(new JDialogOperator(msgType), jmap.getSystemMessageButton(), true);
		} catch (Exception e) {
			System.out.println("No message was found");
		}
		return 0;
	}

	/**
	 * This method is used to verify that parameters exist on a specific test in
	 * the runner
	 */
	public String verifyParameterIndexAndEditability(String param, int testIndex, String tab, int paramIndex,
			boolean isEditable) throws Exception {
		JTableOperator paramTable = getParamsTable(testIndex, tab);
		if (paramIndex >= paramTable.getRowCount()) {
			return "parameter index exceed number of parameters";
		}
		Object tableValue = paramTable.getValueAt(paramIndex, 0);
		String paramNameInTable = tableValue.toString();
		if (paramNameInTable.endsWith("*")) {
			paramNameInTable = paramNameInTable.substring(0, paramNameInTable.length() - 1);
		}
		if (tableValue == null || !paramNameInTable.equals(param)) {
			return "not expected index. actual parameter at index is " + tableValue.toString();
		}
		boolean editable = false;
		int rowCount = ((JTable) paramTable.getSource()).getRowCount();
		for (int i = 0; i < rowCount; i++) {
			System.out.println("is enabled ------------"
					+ i
					+ "  "
					+ ((JLabel) paramTable.getCellRenderer(i, 3).getTableCellRendererComponent(
							(JTable) paramTable.getSource(), "", true, true, i, 3)).isEnabled());
		}
		editable = ((JLabel) paramTable.getCellRenderer(paramIndex, 3).getTableCellRendererComponent(
				(JTable) paramTable.getSource(), "", true, true, paramIndex, 3)).isEnabled();
		if (editable != isEditable) {
			return "not expected editability. param index = " + paramIndex + " (" + editable + ")";
		}
		return "true";
	}

	public String sortParametersTable(int testIndex, String tab, int headerIndex) throws Exception {
		getParamsTable(testIndex, tab);
		ComponentSearcher searcher = new ComponentSearcher(mainFrame.getContentPane());
		Component comp = searcher.findComponent(new ComponentChooser() {
			public boolean checkComponent(Component comp) {
				return (comp instanceof ParametersPanel);
			}

			public String getDescription() {
				return "";
			}
		});
		if (comp == null) {
			return "parameters panel was not found";
		}
		((ParametersPanel) comp).headerChanged(headerIndex);
		return "okay";
	}

	public int setParameterTableSize(int testIndex, String tab, String size) throws Exception {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		JTableOperator paramTable = getParamsTable(testIndex, tab);
		double[] sizes = sizeToDoubleArray(size);
		paramTable.getColumnModel().getColumn(0).setPreferredWidth((int) (dim.getWidth() * sizes[0]));
		paramTable.getColumnModel().getColumn(1).setPreferredWidth((int) (dim.getWidth() * sizes[1]));
		paramTable.getColumnModel().getColumn(2).setPreferredWidth((int) (dim.getWidth() * sizes[2]));
		paramTable.getColumnModel().getColumn(3).setPreferredWidth((int) (dim.getWidth() * sizes[3]));
		return 0;
	}

	public String getParameterTableSize(int testIndex, String tab) throws Exception {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		JTableOperator paramTable = getParamsTable(testIndex, tab);
		double[] sizes = new double[4];
		sizes[0] = paramTable.getColumnModel().getColumn(0).getWidth() / dim.getWidth();
		sizes[1] = paramTable.getColumnModel().getColumn(1).getWidth() / dim.getWidth();
		sizes[2] = paramTable.getColumnModel().getColumn(2).getWidth() / dim.getWidth();
		sizes[3] = paramTable.getColumnModel().getColumn(3).getWidth() / dim.getWidth();
		return sizesToString(sizes);
	}

	public String setFileChooserParameter(int testIndex, String tab, int paramIndex, String path) throws Exception {
		JTableOperator paramTable = getParamsTable(testIndex, tab);
		paramTable.clickOnCell(paramIndex, 3);
		ComponentSearcher searcher = new ComponentSearcher(paramTable.getParent());
		Component comp = searcher.findComponent(new ComponentChooser() {
			public boolean checkComponent(Component comp) {
				if (!(comp instanceof JButton)) {
					return false;
				}
				return ParametersTableFileChooser.SELECT_FILE_BUTTON_NAME.equals(comp.getName());
			}

			public String getDescription() {
				return "";
			}
		});
		if (comp == null) {
			throw new Exception("File chooser button was not found");
		}
		JButton fileChooserButton = (JButton) comp;
		JButtonOperator buttonOperator = new JButtonOperator(fileChooserButton);
		buttonOperator.push();
		Thread.sleep(1000);
		JDialogOperator dialog = new JDialogOperator("Select File");
		if (!dialog.isActive()) {
			return "Failed activating select file window";
		}
		JFileChooserOperator fileChooser = new JFileChooserOperator();
		fileChooser.setCurrentDirectory(new File(""));
		fileChooser.chooseFile(path);
		new JButtonOperator(fileChooser, new TipNameButtonFinder("Select")).clickMouse();
		return "okay";
	}

	public static double[] sizeToDoubleArray(String sizesAsString) {
		String[] sizes = StringUtils.split(sizesAsString, ";");
		double[] res = new double[sizes.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = Double.parseDouble(sizes[i]);
		}
		return res;
	}

	public static String sizesToString(double[] sizes) {
		String res = "";
		for (int i = 0; i < sizes.length; i++) {
			res += sizes[i];
			res += ";";
		}
		return res.substring(0, res.length() - 1);
	}

	/**
	 * This method is used to verify that parameters exist on a specific test in
	 * the runner
	 */
	public String verifyParameterseExist(String param, int testIndex, String tab) throws Exception {
		// Selecting test
		selectTestsRows(new int[] { testIndex });
		// Navigating to parameters tab
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		testInfoTab.setSelectedIndex(2);
		// Navigating to parameteres tab
		JTabbedPaneOperator paramTab = new JTabbedPaneOperator(testInfoTab, 1);
		paramTab.selectPage(tab);
		JTableOperator paramTable = getParamsTable(testIndex, tab);
		for (int i = 0; i < paramTable.getRowCount(); i++) {
			Object tableValue = paramTable.getValueAt(i, 0);
			if (tableValue != null && tableValue.toString().startsWith(param)) {
				System.out.println("The Item was found");
				return "true";
			}
		}
		return "false";
	}

	/**
	 * This method is used to verify that parameters exist on a specific test in
	 * the runner
	 */
	public String getParameterDescription(String param, int testIndex, String tab) throws Exception {
		jemmyOperation.report("---------------------------------------1");
		// Selecting test
		selectTestsRows(new int[] { testIndex });
		jemmyOperation.report("---------------------------------------2");
		// Navigating to parameters tab
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		jemmyOperation.report("---------------------------------------3");
		testInfoTab.setSelectedIndex(2);
		jemmyOperation.report("---------------------------------------4");
		// Navigating to parameteres tab
		JTabbedPaneOperator paramTab = new JTabbedPaneOperator(testInfoTab, 1);
		jemmyOperation.report("---------------------------------------5");
		paramTab.selectPage(tab);
		jemmyOperation.report("---------------------------------------6");
		JTableOperator paramTable = getParamsTable(testIndex, tab);
		jemmyOperation.report("---------------------------------------7");
		for (int i = 0; i < paramTable.getRowCount(); i++) {
			jemmyOperation.report("---------------------------------------8");
			Object tableValue = paramTable.getValueAt(i, 0);
			jemmyOperation.report("---------------------------------------9");
			if (tableValue != null && tableValue.toString().startsWith(param)) {
				jemmyOperation.report("---------------------------------------10");
				return paramTable.getValueAt(i, 1).toString();
			}
		}
		return "false";
	}

	/**
	 * This method is used to verify that parameters exist on a specific test in
	 * the runner
	 */
	public String getParameterValue(String param, int testIndex, String tab) throws Exception {
		// Selecting test
		selectTestsRows(new int[] { testIndex });
		// Navigating to parameters tab
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		testInfoTab.setSelectedIndex(2);
		// Navigating to parameteres tab
		JTabbedPaneOperator paramTab = new JTabbedPaneOperator(testInfoTab, 1);
		paramTab.selectPage(tab);
		JTableOperator paramTable = getParamsTable(testIndex, tab);
		for (int i = 0; i < paramTable.getRowCount(); i++) {
			Object tableValue = paramTable.getValueAt(i, 0);
			if (tableValue != null && tableValue.toString().startsWith(param)) {
				return paramTable.getValueAt(i, 3).toString();
			}
		}
		return "value not found";
	}

	public String setTabSorting(int testIndex, int sortType) throws Exception {
		// Selecting test
		selectTestsRows(new int[] { testIndex });
		// Navigating to parameters tab
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		testInfoTab.setSelectedIndex(2);
		ComponentSearcher searcher = new ComponentSearcher(testInfoTab.getParent());
		Component comp = searcher.findComponent(new ComponentChooser() {
			public boolean checkComponent(Component comp) {
				if (!(comp instanceof ParametersPanel)) {
					return false;
				}
				return true;
			}

			public String getDescription() {
				return "";
			}
		});
		ParametersPanel ppanel = (ParametersPanel) comp;
		ppanel.sectionChanged(sortType);
		return "okay";
	}

	public String getActiveTab(int testIndex) throws Exception {
		// Selecting test
		selectTestsRows(new int[] { testIndex });
		// Navigating to parameters tab
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		testInfoTab.setSelectedIndex(2);
		// Navigating to parameteres tab
		JTabbedPaneOperator paramTab = new JTabbedPaneOperator(testInfoTab, 1);
		String tabText = paramTab.getTitleAt(paramTab.getSelectedIndex());
		return tabText;
	}

	public int getTabIndex(int testIndex, String tab) throws Exception {
		// Selecting test
		selectTestsRows(new int[] { testIndex });
		// Navigating to parameters tab
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		testInfoTab.setSelectedIndex(2);
		// Navigating to parameteres tab
		JTabbedPaneOperator paramTab = new JTabbedPaneOperator(testInfoTab, 1);
		int tabCount = paramTab.getTabCount();
		for (int i = 0; i < tabCount; i++) {
			String tabText = paramTab.getTitleAt(i);
			if (tabText.equals(tab)) {
				return i;
			}
		}
		return -1;
	}

	private JTableOperator getParamsTable(int testIndex, String tab) {
		// Selecting test
		selectTestsRows(new int[] { testIndex });
		// Navigating to parameters tab
		JTabbedPaneOperator testInfoTab = new JTabbedPaneOperator(mainFrame, 0);
		testInfoTab.setSelectedIndex(2);
		// Navigating to parameteres tab
		JTabbedPaneOperator paramTab = new JTabbedPaneOperator(testInfoTab, 1);
		paramTab.selectPage(tab);
		return new JTableOperator(paramTab);
	}

	public int exitThroughMenu(boolean exit) throws Exception {
		JMenuOperator menuOperator = new JMenuOperator(mainFrame, "File");
		menuOperator.push();
		JMenuItemOperator exitMenuOperator = new JMenuItemOperator(mainFrame, "Exit");
		exitMenuOperator.push();
		JDialogOperator dialog = new JDialogOperator("Exit Confirmation");
		new JButtonOperator(dialog, new TipNameButtonFinder(exit ? "Yes" : "No")).push();
		return 0;
	}

	public void startTest(String className, String methodName, String meaningfulName, String comment,
			String paramString, int count, String uuid) {
		// TODO Auto-generated method stub

	}

	public String scenarioElement(int iIndex) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public synchronized void executionEnded(String scenarioName) {
		executionEnd = true;
		notifyAll();
	}

	public void remoteExit() {
		// TODO Auto-generated method stub

	}

	public void remotePause() {
		// TODO Auto-generated method stub

	}

	public void errorOccured(String title, String message, ErrorLevel level) {
		// TODO Auto-generated method stub

	}

	public int waitForConfirmDialog(String title, int expected) throws Exception {
		JDialogOperator dialog = new JDialogOperator(title);
		new JButtonOperator(dialog, new TipNameButtonFinder((expected == 0) ? "Yes" : "No")).pushNoBlock();
		return 0;
	}

	/**
	 * takes the content of jar list from gui operation(Tools -> Show jar list)
	 * and return all it's content to caller as String
	 * 
	 * @return String representation of the jar list.
	 * @throws Exception
	 */
	public String openJarList() throws Exception {
		StringBuffer jarListTable = new StringBuffer();
		JMenuOperator toolsMenuOperator = new JMenuOperator(mainFrame, "Tools");
		toolsMenuOperator.push();
		JMenuItemOperator jarListsMenuOperator = new JMenuItemOperator(mainFrame, "Show Jar List");
		jarListsMenuOperator.push();
		Thread.sleep(5000); // give the table sometime to open
		JDialogOperator dialog = new JDialogOperator("Jar List");
		JTableOperator jto = new JTableOperator(dialog);
		int rowCount = jto.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			jarListTable.append(String.valueOf(jto.getValueAt(i, 0)));
		}
		return jarListTable.toString();
	}

	public String sortJarList() throws Exception {
		StringBuffer jarListTable = new StringBuffer();
		JMenuOperator toolsMenuOperator = new JMenuOperator(mainFrame, "Tools");
		toolsMenuOperator.push();
		JMenuItemOperator jarListsMenuOperator = new JMenuItemOperator(mainFrame, "Show Jar List");
		jarListsMenuOperator.push();
		Thread.sleep(5000); // give the table sometime to open
		JDialogOperator dialog = new JDialogOperator("Jar List");
		jemmyOperation.pushDialogButton(dialog, "Sort Jars", false);
		JDialogOperator dialog1 = new JDialogOperator("Jar List");
		JTableOperator jto = new JTableOperator(dialog1);
		// needs to start in row 0
		for (int i = 0; i < jto.getRowCount(); i++) {
			jarListTable.append(String.valueOf(jto.getValueAt(i, 0)));
		}
		return new String(jarListTable);
	}

	/**
	 * open jar list, search from specific creteria jars and return a String
	 * representing all relevant jars in the list
	 * 
	 * @param jarToFind
	 * @return
	 * @throws Exception
	 */
	public String jarInList(String jarToFind) throws Exception {
		StringBuffer jarListTable = new StringBuffer();
		JMenuOperator toolsMenuOperator = new JMenuOperator(mainFrame, "Tools");
		toolsMenuOperator.push();
		JMenuItemOperator jarListsMenuOperator = new JMenuItemOperator(mainFrame, "Show Jar List");
		jarListsMenuOperator.push();
		Thread.sleep(5000); // give the table sometime to open
		JDialogOperator dialog = new JDialogOperator("Jar List");
		JTextFieldOperator jt = new JTextFieldOperator(dialog);
		jt.setText(jarToFind);
		jemmyOperation.pushDialogButton(dialog, "Find Jars", false);
		JDialogOperator dialog1 = new JDialogOperator("Jar List");
		JTableOperator jto = new JTableOperator(dialog1);
		for (int i = 0; i < jto.getRowCount(); i++) {
			jarListTable.append(String.valueOf(jto.getValueAt(i, 0)));
		}
		return new String(jarListTable);
	}

	/**
	 * Simulates pressing a key
	 */
	public String pressKey(int keyCode) throws Exception {
		ComponentOperator component = new ComponentOperator(mainFrame);
		component.pressKey(keyCode);
		return "Success";
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
	 * This will return the number of tests in the current scenario including
	 * tests in sub scenarios
	 * 
	 * @return
	 * @throws Exception
	 */
	public Integer getNumOfTestsInScenario() throws Exception {
		return ScenariosManager.getInstance().getCurrentScenario().getTests().size();
	}

	/**
	 * This will return the number of tests in the current scenario including
	 * tests in sub scenarios
	 * 
	 * @return
	 * @throws Exception
	 */
	public Integer getNumOfTestsInScenario(String scenarioName) throws Exception {
		return ScenariosManager.getInstance().getScenario(scenarioName).getTests().size();
	}

	// JSystemProperties Dialog commands
	// *********************************************************************************************************

	public int restoreJsystemPropertiesDefaults() throws Exception {
		return setDialogValues(new Vector<String>(), JsystemPropertiesDialogButton.RESTORE_DEFAULTS.toString());
	}

	/**
	 * This method receive a vector containing the following information: -
	 * Groups of Strings: - Tab name - Property name - Property new value - The
	 * name of the button to press after all properties have been changed. The
	 * method run over all properties, change them to the given value, and press
	 * the given button( Save / Restore Defaults / cancel )
	 * 
	 * @param buttonToPress
	 *            - the name of the button to press after changing the data
	 * @param vector
	 *            - A vector of Strings as describe above
	 * @return -1 if an error occured, 0 - otherwise.
	 * @throws Exception
	 */
	public int setDialogValues(Vector<String> vector, String buttonToPress) throws Exception {
		return setDialogValues(vector, buttonToPress, true);
	}

	/**
	 * This method receive a vector containing the following information: -
	 * Groups of Strings: - Tab name - Property name - Property new value - The
	 * name of the button to press after all properties have been changed. The
	 * method run over all properties, change them to the given value, and press
	 * the given button( Save / Restore Defaults / cancel )
	 * 
	 * @param vector
	 *            - A vector of Strings as describe above
	 * @param buttonToPress
	 *            - the name of the button to press after changing the data
	 * @param restartRunnerIfRequire
	 *            - Some properties require to reload the runner in order the
	 *            change will take affect. when the user press "Save", a pop up
	 *            message asks the user if he would like to restart the Runner
	 *            now. if the user choose - "Yes" the dialog will be close, and
	 *            the runner will be restarted. If the user press - "No", the
	 *            dialog will be close, but the runner will keep on running. The
	 *            restartRunnerIfRequire parameter indicates if to restart
	 *            runner or not
	 * @return -1 if an error occur, 0 - otherwise.
	 * @throws Exception
	 */
	public int setDialogValues(Vector<String> vector, String buttonToPress, boolean restartRunnerIfRequire)
			throws Exception {
		String tabName, name, value, message;
		int length = vector.size();
		int propertyCount = length / 3;

		JDialogOperator dialog = openJSystemPropertiesDialogFromMenu();

		if (length > 0) {
			String previousTabName = (String) vector.get(0);
			JTableOperator tableOperator = getJSystemPropertiesTableOperator(previousTabName);

			for (int i = 0; i < propertyCount; i++) {
				tabName = (String) vector.get(3 * i);
				name = (String) vector.get(3 * i + 1);
				value = (String) vector.get(3 * i + 2);
				message = "------- setDialogValues(" + tabName + ", " + name + ") to: " + value;

				if (!tabName.equals(previousTabName)) {
					jemmyOperation.report("Change tab to " + tabName);
					previousTabName = tabName;
					tableOperator = getJSystemPropertiesTableOperator(tabName);
				}

				if (setValue(tableOperator, name, value) == 0) {
					jemmyOperation.report("Successfully " + message);
				} else {
					jemmyOperation.report("Fail to " + message);
				}
			}
		}

		if (buttonToPress.equals(JsystemPropertiesDialogButton.SAVE.toString())) {
			JButtonOperator saveButton = new JButtonOperator(dialog, jmap.getJSystemPropertiesSaveButtonName());
			saveButton.push();
			Thread.sleep(2000);
			if (restartRunnerIfRequire) {
				jemmyOperation.report("Going to restart Runner after Save button was pressed, if restart is needed");
				return (jemmyOperation.ConfirmDialogIfExists(jmap.getJSystemPropertiesConfirmRestartDialogTitle(),
						jmap.getJSystemPropertiesConfirmRestartYesButtonName())) ? 1 : 0;
			} else {
				jemmyOperation.report("Runner will not be restarted after Save button was pressed");
				jemmyOperation.ConfirmDialogIfExists(jmap.getJSystemPropertiesConfirmRestartDialogTitle(),
						jmap.getJSystemPropertiesConfirmRestartNoButtonName());
				return 0;
			}
		}
		if (buttonToPress.equals(JsystemPropertiesDialogButton.RESTORE_DEFAULTS.toString())) {
			JButtonOperator systemDefaultButton = new JButtonOperator(dialog,
					jmap.getJSystemPropertiesSystemDefaultButtonName());
			systemDefaultButton.push();
			Thread.sleep(2000);
			jemmyOperation.ConfirmDialogIfExists(jmap.getJSystemPropertiesRestoreDefaultsDiallogTitle(),
					jmap.getJSystemPropertiesConfirmRestoreDefaultButtonName());
			Thread.sleep(2000);
			return 1;
		}
		if (buttonToPress.equals(JsystemPropertiesDialogButton.CANCEL.toString())) {
			JButtonOperator cancleButton = new JButtonOperator(dialog, jmap.getJSystemPropertiesCancleButtonName());
			cancleButton.push();
			return 0;
		}

		return 0;
	}

	/**
	 * This method receive a vector containing the following information: -
	 * Groups of Strings: - Tab name - Property name The method run over all
	 * properties, concatenate them into a long string together with their
	 * values from the dialog. and press the given button( Save / Restore
	 * Defaults / cancel )
	 * 
	 * @param vector
	 *            - A vector of Strings as describe above
	 * @return a long string containing the following: propertyName#value#...
	 *         for all the properties in the received vector
	 * @throws Exception
	 */
	public String getDialogValues(Vector<String> vector) throws Exception {
		StringBuffer buffer = new StringBuffer();
		String tabName, name, value;
		int propertyCount = vector.size() / 2;

		openJSystemPropertiesDialogFromMenu();

		String previousTabName = (String) vector.get(0);

		JTableOperator tableOperator = getJSystemPropertiesTableOperator(previousTabName);

		for (int i = 0; i < propertyCount; i++) {
			tabName = (String) vector.get(2 * i);
			name = (String) vector.get(2 * i + 1);
			if (!tabName.equals(previousTabName)) {
				jemmyOperation.report("Change tab to " + tabName);
				previousTabName = tabName;
				tableOperator = getJSystemPropertiesTableOperator(tabName);
			}
			value = getValue(tableOperator, name);
			if (value == null || value.isEmpty()) {
				value = new String("EMPTY_FIELD");
			}
			buffer.append(name);
			buffer.append("#");
			buffer.append(value);
			buffer.append("#");
		}

		closeJSystemPropertiesDialog();
		return buffer.toString();
	}

	/**
	 * This method return the value of a specific table property.
	 * 
	 * @param tableOperator
	 *            - The table from which we would like to get a value
	 * @param stringName
	 *            - The propery for which we would like to get the value
	 * @return a String holding the value of the given property
	 * @throws Exception
	 */
	public String getValue(JTableOperator tableOperator, String stringName) throws Exception {
		int propertiesIndex = jemmyOperation.getTableRowIndexOfValue(tableOperator, stringName, 0);
		String value = (String) tableOperator.getValueAt(propertiesIndex, 2);
		return value;
	}

	/**
	 * Sets a value to the dialog according to a specific property in a specific
	 * table
	 * 
	 * @param tableOperator
	 *            - The table holding the given property
	 * @param key
	 *            - The Key of the property to be change
	 * @param value
	 *            - the value requiered to be set into the given property.
	 * @return 0 if the table was updated successfully, -1 - otherwise.
	 * @throws Exception
	 */
	public int setValue(JTableOperator tableOperator, String key, String value) throws Exception {
		int propertiesIndex;
		FrameworkOptions frameworkOption;
		DataType dataType;
		Boolean isComboBox = false;

		try {
			propertiesIndex = jemmyOperation.getTableRowIndexOfValue(tableOperator, key, 0);
			frameworkOption = FrameworkOptions.getFrameworkOptionKeyByStringName(key);
			isComboBox = false;
			if (frameworkOption != null) {
				dataType = frameworkOption.getDataType();

				if ((dataType == DataType.BOOLEAN) || (dataType == DataType.LIST)) {
					isComboBox = true;
				}
			}
			jemmyOperation.setTableCell(tableOperator, propertiesIndex, 2, value, isComboBox);
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Return a reference to a JTableOperator according to the given title name
	 * 
	 * @param tabName
	 *            - The title of the tab to be return
	 * @return a reference to a JTableOperator
	 * @throws Exception
	 */
	private JTableOperator getJSystemPropertiesTableOperator(String tabName) throws Exception {
		JDialogOperator dialog = getJSystemDialog();
		JTabbedPaneOperator tab = jemmyOperation.getJTableFromTab(dialog, tabName);
		JTableOperator tableOperator = new JTableOperator(tab);
		return tableOperator;
	}

	/**
	 * This method open the JSystem Properties Dialog using the tools menu.
	 * 
	 * @return a reference to JDialogOperator
	 * @throws Exception
	 */
	private JDialogOperator openJSystemPropertiesDialogFromMenu() throws Exception {
		String menueItemName = jmap.getJSystemPropertiesMenuItem();
		String dialogName = jmap.getJSystemPropertiesMenuItem();
		jemmyOperation.chooseMenuItemAndWaitForDialog(mainFrame, "Tools", menueItemName, dialogName);
		JDialogOperator jsystemPropertiesDialog = new JDialogOperator(dialogName);
		return jsystemPropertiesDialog;
	}

	/**
	 * Search for the JSystem Properties Dialog for maximum 3 seconds, and
	 * return a reference to this dialog
	 * 
	 * @return a reference to JDialogOperator
	 */
	private JDialogOperator getJSystemDialog() {
		JDialogOperator jsystemPropertiesDialog = jemmyOperation.getDialogIfExists(jmap.getJSystemPropertiesMenuItem(),
				3);
		if (jsystemPropertiesDialog == null) {
			jemmyOperation.report("\n JSystemServer.getJSystemDialog() - Dialog == null");
		}
		return jsystemPropertiesDialog;
	}

	/**
	 * Close the dialog
	 * 
	 * @return 0 if the operation ended successfully, -1 - otherwise
	 * @throws Exception
	 */
	private int closeJSystemPropertiesDialog() throws Exception {
		JDialogOperator jsystemPropertiesDialog = getJSystemDialog();

		if (jsystemPropertiesDialog == null) {
			return -1;
		}

		jsystemPropertiesDialog.dispose();
		return 0;
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
		if (!(test instanceof JTest)) {
			if (!((Scenario) test).isScenarioAsTest()) {
				throw new Exception("type passed must be a test");
			}
		}
		return !test.isDisable();
	}

	@Override
	public void endContainer(JTestContainer container) {
		// TODO Auto-generated method stub
	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startContainer(JTestContainer container) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startTest(Test arg0) {
		System.out.println("public void startTest(Test arg0)");

	}

}
