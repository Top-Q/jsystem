package com.aqua.jsystemobject.handlers;

import java.awt.Component;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.tree.TreePath;

import jsystem.extensions.report.html.Report;
import jsystem.framework.DBProperties;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.report.ExecutionListener;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.TestRunner;
import jsystem.treeui.TestTreeView;
import jsystem.treeui.tree.TestTreePanel;
import jsystem.utils.BackgroundRunnable;
import jsystem.utils.FileLock;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.operators.DialogOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JMenuOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.operators.Operator.StringComparator;

import com.aqua.jsystemobject.JServer;
import com.aqua.jsystemobject.JemmySupport;
import com.aqua.jsystemobject.NameChooser;
import com.aqua.jsystemobject.TipNameButtonFinder;

public class JApplicationHandler extends BaseHandler implements ExecutionListener{

	public JApplicationHandler() {;
		jmap = new JsystemMapping();
		jemmyOperation = new JemmySupport();
		mainWindow = jmap.getJSyetemMain();
	}
	
	/**
	 * returns true if the play button is enabled
	 * @throws Exception 
	 */
	public boolean checkIfPlayIsEnabled() throws Exception{
		JButtonOperator btnOp = jemmyOperation.getButtonOperator(mainFrame, jmap.getPlayButton());
		return btnOp.isEnabled();
	}
	
	/**
	 * calls the launch with the specified sut and true as for log zipping
	 * @param sutFile
	 * @return
	 * @throws Exception
	 */
	public int launch(final String sutFile) throws Exception {
		return launch(sutFile,true);
	}
	
	/**
	 * calls launch with no sut, and a true/false option for log zipping
	 * @param disableZip
	 * @return
	 * @throws Exception
	 */
	public int launch(boolean disableZip) throws Exception {
		System.out.println("MY TEXT!!");
		return launch(null,disableZip);
	}
	
	/**
	 * launch the runner with the specified sut and true/false for log zipping
	 * @param sutFile
	 * @param disableZip
	 * @return
	 * @throws Exception
	 */
	public int launch(final String sutFile,boolean disableZip) throws Exception {
		System.out.println("MY TEXT!!");
		if (launched) {
			return 0;
		}

		Thread t = null;
		BackgroundRunnable runnable = null;
		if (!StringUtils.isEmpty(sutFile)) {
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

//		DebugLogFile.writeToDebug("initiating JMapping");
		
//		DebugLogFile.writeToDebug("initiating Jemmy Operation");
//		DebugLogFile.writeToDebug("adding listener");
		ListenerstManager.getInstance().addListener(this);

		JServer.jsystem.setInitialJsystemProperties();
		JServer.jsystem.setJSystemProperty(FrameworkOptions.HTML_ZIP_DISABLE.toString(),disableZip + "");
		JServer.jsystem.setJSystemProperty(FrameworkOptions.DISABLE_OLD_SCENARIO_CONVERT.toString(),"never-5.1");
		ListenerstManager.getInstance().addListener(this);
//		DebugLogFile.writeToDebug("starting runner");
		waitForLockFileToBeReleased();
		new ClassReference(TestRunner.class.getName()).startApplication();
//		DebugLogFile.writeToDebug("getting mainFrame");
		mainFrame = new JFrameOperator(jmap.getJSyetemMain());
		JTabbedPaneOperator testTreeTab = new JTabbedPaneOperator(mainFrame, jmap.getTestTreeTab());

//		DebugLogFile.writeToDebug("getting test tree");
		testsTree = new JTreeOperator(testTreeTab);
		scenarioTree = new JTreeOperator(mainFrame, 0);
//		DebugLogFile.writeToDebug("set freeze on fail");
		setFreezeOnFail(false);
		if (t != null) {
			t.join();
			if (runnable.getThrowable() != null) {
				throw new Exception("SUT file, " + sutFile + " was not found as expected during launch", runnable.getThrowable());
			}
		}
		launched = true;
		return 0;
	}
	
	/**
	 * checks the runner lock file number of times, in order to check that the last runner was closed properly
	 * 
	 * @throws Exception
	 */
	public void waitForLockFileToBeReleased() throws Exception{
		FileLock lock = FileLock.getFileLock(CommonResources.LOCK_FILE);
		int retries = 12;
		int i=0;
		while (i<retries){
			if (!lock.grabLock()){
				Thread.sleep(5000);
				i++;
			}else {
				lock.releaseLock();
				return;
			}
		}
	}
	
	/**
	 * will extruct the specified zip file to the specified root dir.
	 * @param envZipPath path to zip file to extract
	 * @return
	 * @throws Exception
	 */
	public int extract(String envZipPath) throws Exception {
		FileUtils.extractZipFile(new File(envZipPath), new File("tmpExtractDir"));
		return 0;
	}
	
	
	/**
	 * refresh the reports button to show real status.
	 * @param value
	 */
	public int refreshReportsButton(boolean value){
		TestRunner.treeView.refreshOpenReportsButton(value);
		return 0;
	}
	
	/**
	 * push the Gui play button
	 * @param block
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean play(boolean block) throws Exception {
		JButtonOperator btnOp = jemmyOperation.getButtonOperator(mainFrame, jmap.getPlayButton());
		//if the play button is enabled we can run the tests and wait
		//for run tests
		if(btnOp.isEnabled()){
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
	 * push the pause button
	 * @return
	 * @throws Exception
	 */
	public int playPause() throws Exception {
		jemmyOperation.pushButton(mainFrame, jmap.getPauseButton());
		return 0;
	}
	
	/**
	 * push the refresh button in the runner
	 * @return
	 * @throws Exception
	 */
	public int refresh() throws Exception {
		jemmyOperation.pushButton(mainFrame, jmap.getRefreshButton());
		Thread.sleep(5000);
		// runner.handleEvent(TestTreeView.REFRESH_EVENT, null);
		return 0;
	}
	
	
	/**
	 * saves the current scenario configuration
	 * @throws Exception
	 */
	public int saveScenario()throws Exception{
		jemmyOperation.report("saving the scenario");
		jemmyOperation.pushButton(mainFrame, jmap.getSaveScenarioButton());
		return 0;
	}
	
	/**
	 * calls the selectSut function
	 * @param sutName
	 * @return
	 * @throws Exception
	 */
	public int changeSut(String sutName) throws Exception {
		return selectSut(sutName);
	}
	
	
	/**
	 * return the selected from combo
	 * @param sutName
	 * @return
	 * @throws Exception
	 */
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
	 * @return
	 */
	private JComboBoxOperator getSutOperator(){
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
	 * push the edit sut button and then press save.
	 * @param expectError
	 * @param closeEditor
	 * @return
	 * @throws Exception
	 */
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
	
	/**
	 *  Waits for the runner's error dialog to open and presses the OK button. 
	 */
	private int waitForErrorDialog() throws Exception {
		JDialogOperator dialog = new JDialogOperator(jmap.getErrorDialogWin());
		new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getDialogSelectOKButton())).push();
		return 0;
	}
	
	/**
	 * push the stop operation and stop immediately
	 * @return int
	 * @throws Exception
	 */
	public int stop() throws Exception {
		JDialogOperator gracefulDialog = jemmyOperation.pushButtonAndWaitForDialog(mainFrame, jmap.getStopButton(), jmap
				.getGracefulDialog());
		jemmyOperation.pushButton(gracefulDialog, jmap.getStopImmediatelyButton());
		jemmyOperation.WaitForDialogToClose(jmap.getGracefulDialog());
		return 0;
	}
	
	/**
	 * push the stop Gui stop button and wait for the graceful stop
	 * @return
	 * @throws Exception
	 */
	public int gracefulStop() throws Exception {
		jemmyOperation.pushButton(mainFrame, jmap.getStopButton());
		jemmyOperation.WaitForDialogToClose(jmap.getGracefulDialog());
		return 0;
	}
	
	public synchronized boolean chooseMenuItem(Vector<String> menuItems) throws Exception {
		_chooseMenuItem(menuItems);
		return true;
	}
	public synchronized boolean chooseMenuItem(boolean pushLastLevel, Vector<String> menuItems) throws Exception {
		_chooseMenuItem(pushLastLevel, menuItems);
		return true;
	}
	
	public synchronized boolean setToolbarView(String toolbarName) throws Exception {
		_setToolbarView(toolbarName);
		return true;
	}
	
	public synchronized JMenuItemOperator _chooseMenuItem(Vector<String> menuItems) throws Exception {
		return _chooseMenuItem(true, menuItems);
	}
	public synchronized JMenuItemOperator _chooseMenuItem(boolean pushLastLevel, Vector<String> menuItems) throws Exception {
		return jemmyOperation.chooseMenuItem(mainFrame, pushLastLevel, unvectorize(menuItems));
	}
	
	public synchronized JMenuItemOperator _setToolbarView(String toolbarName) throws Exception {
		return _chooseMenuItem(vectorize("View", "Toolbars", toolbarName));
	}
	
	public synchronized Boolean getToolbarViewState (String toolbarName) throws Exception {
		Boolean state = true;

		JMenuItemOperator menuItem = _chooseMenuItem(false, vectorize("View", "Toolbars", toolbarName));
		Component checkBox = menuItem.getSource();
		if (checkBox instanceof JCheckBoxMenuItem) {
			state = ((JCheckBoxMenuItem)checkBox).isSelected();
		} else {
			throw new Exception(toolbarName + "Toolbar menu item is not a checkbox");
		}
		
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_ESCAPE);
		r.keyPress(KeyEvent.VK_ESCAPE);
		
		return state;
	}
	
	/* 
	 * Finding the "real" state of the toolbar -
	 * The right way to do that is by finding an operator to JToolBar, but since it does not exist
	 * the simpler way was to find a button in the toolbar, and look for its parent.
	 * Note: since finding a button is done via its tooltip - in case the tooltip
	 * is dynamic (like the status button...) - it won't work.
	 */
	public synchronized Boolean getFlowControlToolbarState() throws Exception {
		JButtonOperator button = jemmyOperation.getButtonOperator(mainFrame, jmap.getLoopButton());
		boolean visible = button.getSource().getParent().isVisible();
		
		//double escape key press sequence to immediately close the sub menus 
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_ESCAPE);
		r.keyPress(KeyEvent.VK_ESCAPE);
		return visible;
	}
	public synchronized Boolean getAgentToolbarState() throws Exception {
		
		JButtonOperator button = jemmyOperation.getButtonOperator(mainFrame, jmap.getConnectToAgentButton());
		
		// Agent toolbar is actually set inside a panel, so till we find a nicer
		// way to find it, we will ask about the grandparent
		//boolean visible = button.getSource().getParent().isVisible();
		
		//double escape key press sequence to immediately close the sub menus 
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_ESCAPE);
		r.keyPress(KeyEvent.VK_ESCAPE);
		return button.getSource().getParent().getParent().isVisible();
	}
	
	public synchronized Boolean getMainToolbarState() throws Exception {
		JButtonOperator button = jemmyOperation.getButtonOperator(mainFrame, jmap.getNewScenarioButton());
		boolean visible = button.getSource().getParent().isVisible();
		
		//double escape key press sequence to immediately close the sub menus 
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_ESCAPE);
		r.keyPress(KeyEvent.VK_ESCAPE);
		return visible;
	}
	
	public synchronized int waitForExecutionEnd() throws Exception {
		while (!executionEnd) {
			wait();
		}
		return 0;
	}

	/**
	 * will wait for amount runs to end
	 * @param amount = the number of runs to wait
	 * @return
	 * @throws Exception
	 */
	public synchronized int waitForRunEnd(int amount) throws Exception {
		while (runEndCount < amount) {
			while (!runEnd) {
				wait();
			}
			runEnd = false;
		}
		return 0;
	}

	/**
	 * runs from the repeat amount set for the runner to the amount given of times
	 * say repeat is set to 10 and amount = 6 it will run 4 times.
	 * if amount is bigger then the set repeat value to begin with, it will
	 * not wait at all
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public synchronized int waitForRunEndUntilLeftRepeatAmountIs(int amount) throws Exception {
		do {
			waitForRunEnd(1);
			runEnd = false;
			//give the UI some time to refresh
			Thread.sleep(1000);
		} while (amount <= getLeftRepeatAmount());
		return 0;
	}
	
	private int getLeftRepeatAmount() throws Exception {
		JTextFieldOperator jt = new JTextFieldOperator(mainFrame, new NameChooser(TestTreeView.REPEAT_LEFT_NAME));
		log.info("repeat amont left " + Integer.parseInt(jt.getText()));
		return Integer.parseInt(jt.getText());
	}
	
	public int setFreezeOnFail(boolean freeze) throws Exception {
		jemmyOperation.setCheckBox(freeze, mainWindow, 2);
		return 0;
	}
	
	/**
	 * call the JServer exit method
	 * @return
	 * @throws Exception
	 */
	public int exit() throws Exception{
		jemmyOperation.report("inside exit of application handler");
		JServer.exit();
		return 0;
	}

	/**
	 * goto file -> exit, confirm close of runner.
	 * does a runner exit through Gui.
	 * able to specify the confirmation choise.
	 * @param exit
	 * @return
	 * @throws Exception
	 */
	public int exitThroughMenu(boolean exit) throws Exception {
		JMenuOperator menuOperator = new JMenuOperator(mainFrame, "File");
		menuOperator.push();
		JMenuItemOperator exitMenuOperator = new JMenuItemOperator(mainFrame, "Exit");
		exitMenuOperator.push();
		JDialogOperator dialog = new JDialogOperator("Exit Confirmation");
		new JButtonOperator(dialog, new TipNameButtonFinder(exit ? "Yes" : "No")).push();
		return 0;
	}
	
	/**
	 * 
	 * push export button, on dialog push the next button
	 * set the checkboxes for compiled output, and export runner, jdk, log.
	 * push next -> finish.
	 * @param jarPath
	 * @param exportTests
	 * @param exportScenarios
	 * @param exportRunner
	 * @param exportLog
	 * @param exportSut
	 * @param exportLib
	 * @param exportJdk
	 * @return
	 * @throws Exception
	 */
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
	
	/**
	 * sets the property in the specified key to the specified value,
	 * and writes the value to the file.
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public int setJSystemProperty(String key, String value) throws Exception {
		JSystemProperties.getInstance().setPreference(key, value);
		jemmyOperation.report("Added "+key+" = "+value+" to jsystem properties file");
		return 0;
	}
	
	/**
	 * gets the value of a key from properties file.
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String getJSystemProperty(String key) throws Exception {
		return JSystemProperties.getInstance().getPreference(key);
	}
	
	/**
	 * get the system runner directory property
	 * @return
	 */
	public String getUserDir() {
		return System.getProperty("user.dir");
	}
	
	/**
	 * 
	 * @param userDir
	 * @return
	 */
	public int setUserDir(String userDir) {
		System.setProperty("user.dir", userDir);
		return 0;
	}
	
	/**
	 *  Waits for the runner's warning dialog to open and presses the OK button. 
	 */
	public int waitForWarningDialog() throws Exception {
		Thread.sleep(500);
		JDialogOperator dialog = new JDialogOperator(jmap.getWarningDialogWin());
		jemmyOperation.report("dialog reference value = "+dialog);
		JButtonOperator jbo = new JButtonOperator(dialog, new TipNameButtonFinder(jmap.getDialogSelectOKButton()));
		jbo.push();
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
				if (e.toString().equals("Error dialog was found")) {
					throw new Exception(message+ " Error dialog was found");
				} else {
					throw e;
				}
			}
		}
		return 0;
	}
	
	@Override
	public void errorOccured(String title, String message, ErrorLevel level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void executionEnded(String scenarioName) {
		executionEnd = true;
		notifyAll();
		
	}

	@Override
	public void remoteExit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remotePause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addWarning(Test test) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void endRun() {
		runEnd = true;
		runEndCount++;

		notifyAll();
	}

	@Override
	public void startTest(TestInfo testInfo) {
	}
	@Override
	public void addError(Test test, Throwable t) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void addFailure(Test test, AssertionFailedError t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endTest(Test test) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startTest(Test test) {
		// TODO Auto-generated method stub
		
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
	
	/**
	 * pushes the addTestsButton to add the already checked tests into scenario Tree
	 * @throws Exception
	 */
	public int moveCheckedToScenarioTree(int amount)throws Exception{
		jemmyOperation.setSpinnerValue(mainFrame, jmap.getNumOfTestToAddSpinner(), new Integer(amount));
		jemmyOperation.pushButton(mainFrame, jmap.getAddTestsButton());
		jemmyOperation.WaitForDialogToClose(jmap.getAddTestsDialog()); // wait for progress bar to close
		return ((Long)TestTreePanel.getCurrentSelectedTests()).intValue();
	}
	/**
	 * takes test class name and test name to check, and a boolean value of true or false
	 * and returns an int 0 if succeeds or -1 if such path is not found in test tree
	 * only checks the requested test's checkboxes 
	 * @param node
	 * @param parentNode
	 * @param check
	 * @return
	 * @throws Exception
	 */
	public int checkTestInTestsTree(String node, String parentNode,boolean check) throws Exception {
		TreePath foundPath = getTreePath(node, parentNode);
		if (foundPath == null){
			return -1;
		}
		JPopupMenuOperator pp = new JPopupMenuOperator(testsTree.callPopupOnPath(foundPath));
		if (check){
			pp.pushMenu(jmap.getTestSelection());
		}
		else{
			pp.pushMenu(jmap.getTestUnSelection());
		}
		return 0;
	}
	
	/**
	 * gets the current version of jsystem
	 * @return
	 * @throws Exception
	 */
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
	 * push the init reporters.
	 * @return
	 * @throws Exception
	 */
	public int initReporters() throws Exception {
		JMenuBarOperator bar = new JMenuBarOperator(mainFrame);
		bar.pushMenu(jmap.getInitReportMenu(), "|");
		jemmyOperation.WaitForDialogToClose(jmap.getInitReportDialogTitle());
		return 0;
	}
	public synchronized Boolean isLoopButtonEnabled() throws Exception {
		return jemmyOperation.isButtonEnabled(mainFrame, (Object)jmap.getLoopButton());
	}
	public synchronized Boolean isIfButtonEnabled() throws Exception {
		return jemmyOperation.isButtonEnabled(mainFrame, (Object)jmap.getIfButton());
	}
	public synchronized Boolean isElseIfButtonEnabled() throws Exception {
		return jemmyOperation.isButtonEnabled(mainFrame, (Object)jmap.getElseIfButton());
	}
	public synchronized Boolean isSwitchButtonEnabled() throws Exception {
		return jemmyOperation.isButtonEnabled(mainFrame, (Object)jmap.getSwitchButton());
	}
	public synchronized Boolean isCaseButtonEnabled() throws Exception {
		return jemmyOperation.isButtonEnabled(mainFrame, (Object)jmap.getCaseButton());
	}
	public synchronized Boolean isRemoveTestsButtonEnabled() throws Exception {
		return jemmyOperation.isButtonEnabled(mainFrame, (Object)jmap.getRemoveTestsButton());
	}
	public synchronized Boolean isMoveTestsDownButtonEnabled() throws Exception {
		return jemmyOperation.isButtonEnabled(mainFrame, (Object)jmap.getMoveTestDownButton());
	}
	public synchronized Boolean isMoveTestsUpButtonEnabled() throws Exception {
		return jemmyOperation.isButtonEnabled(mainFrame, (Object)jmap.getMoveTestUpButton());
	}
}
