package com.aqua.jsystemobjects.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.Properties;
import java.util.Map.Entry;

import jsystem.framework.DBProperties;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.treeui.TestRunner;
import jsystem.utils.BackgroundRunnable;
import jsystem.utils.StringUtils;

import org.jsystem.jemmyHelpers.TipNameButtonFinder;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.DialogOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFileChooserOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JMenuOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTreeOperator;

import com.aqua.utils.Commons;

/**
 * 
 * @author Dan Hirsch
 * 
 */
public class JApplicationHandler extends BaseHandler {

	/**
	 * sets the specified value in the specified key in jsystem.properties file.
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public int setJSystemProperty(String key, String value) throws Exception {
		jemmySupport.report("going to add " + key + " = " + value + " to jsystem properties file");
		System.out.println("checking system out printline");
		JSystemProperties.getInstance().setPreference(key, value);
		jemmySupport.report("Added " + key + " = " + value + " to jsystem properties file");
		return 0;
	}

	/**
	 * Return the value of attached to the specified key in the JSystem
	 * properties.
	 * 
	 * @param key
	 * @return The value attached to the key.
	 * @throws Exception
	 */
	public String getJSystemProperty(String key) throws Exception {
		jemmySupport.report("going to get value of " + key + " from jsystem properties file");
		final String value = JSystemProperties.getInstance().getPreference(key);
		jemmySupport.report("got from jsystem properties: " + key + " = " + value);
		return value != null ? value : "";
	}

	public void setInitialJsystemProperties() throws Exception {
		for (Entry<FrameworkOptions, String> entry : Commons.getBaseJsystemProperties().entrySet()) {
			setJSystemProperty(entry.getKey().toString(), entry.getValue());
		}
	}

	/**
	 * if no parameters are specified, will run with null sut file and true to
	 * disable Zip.
	 * 
	 * @throws Exception
	 */
	public int launch() throws Exception {
		return launch(null, true);
	}

	public int keepAlive() {
		return 0;
	}

	/**
	 * if this version is called it will activate the runner with specified sut
	 * and true in disableZip
	 * 
	 * @param sutFile
	 * @throws Exception
	 */
	public int launch(final String sutFile) throws Exception {
		return launch(sutFile, true);
	}

	public int launch(boolean disableZip) throws Exception {
		return launch(null, disableZip);
	}

	public int launch(final String sutFile, boolean disableZip) throws Exception {
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

		ListenerstManager.getInstance().addListener(this);
		setInitialJsystemProperties();
		setJSystemProperty(FrameworkOptions.HTML_ZIP_DISABLE.toString(), disableZip + "");
		setJSystemProperty(FrameworkOptions.DISABLE_OLD_SCENARIO_CONVERT.toString(), "never-5.1");
		new ClassReference(TestRunner.class.getName()).startApplication();
		mainFrame = new JFrameOperator(jmap.getJSyetemMain());
		JTabbedPaneOperator testTreeTab = new JTabbedPaneOperator(mainFrame, jmap.getTestTreeTab());

		testsTree = new JTreeOperator(testTreeTab);
		scenarioTree = new JTreeOperator(mainFrame, 0);
		setFreezeOnFail(false);
		if (t != null) {
			t.join();
			if (runnable.getThrowable() != null) {
				throw new Exception("SUT file, " + sutFile + " was not found as expected during launch",
						runnable.getThrowable());
			}
		}
		launched = true;
		return 0;
	}

	/**
	 * if runner state allows pressing the play button then it will.
	 * 
	 * @param block
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean play(boolean block) throws Exception {
		JButtonOperator btnOp = jemmySupport.getButtonOperator(mainFrame, jmap.getPlayButton());
		// if the play button is enabled we can run the tests and wait
		// for run tests
		if (btnOp.isEnabled()) {
			jemmySupport.pushButton(mainFrame, jmap.getPlayButton());
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
		jemmySupport.report("saving the scenario");
		jemmySupport.pushButton(mainFrame, jmap.getSaveScenarioButton());
		return 0;
	}

	public int saveScenarioAs(String newScenarioName) throws Exception {
		jemmySupport.pushButtonAndWaitForDialog(mainWindow, jmap.getCopyScenarioButton(), jmap.getScenarioCopyWin());
		JFileChooserOperator fileChosser = new JFileChooserOperator();
		fileChosser.chooseFile(newScenarioName);
		new JButtonOperator(fileChosser, new TipNameButtonFinder(jmap.getScenarioCopyButton())).clickMouse();
		return 0;
	}

	public int copyScenario(String newScenarioName) throws Exception {
		return saveScenarioAs(newScenarioName);

	}

	public int undo() throws Exception {
		jemmySupport.pushButton(mainFrame, jmap.getScenarioUndoButton());
		return 0;
	}

	public int redo() throws Exception {
		jemmySupport.pushButton(mainFrame, jmap.getScenarioRedoButton());
		return 0;
	}

	public int setFreezeOnFail(boolean freeze) throws Exception {
		jemmySupport.setCheckBox(freeze, mainWindow, jmap.getFreezeOnFailCheckbox());
		return 0;
	}

	public boolean killRunnerProcess() throws Exception {

		String pid = ManagementFactory.getRuntimeMXBean().getName();
		pid = pid.split("@")[0];
		jemmySupport.report("pid is " + pid);
		if ("linux".equalsIgnoreCase(System.getProperty("os.name"))) {
			String s = null;
			Process p = Runtime.getRuntime().exec("kill -9 " + pid);
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			if ((s = stdError.readLine()) != null) {
				jemmySupport.report("an error occured trying to execute kill process.");
				jemmySupport.report("the error details are: " + s);
				jemmySupport.report("error were printed to error stream.");
				return false;
			}
			jemmySupport.report("on linux, returning successfully after kill the process");
			return true;
		} else if ("windows".equalsIgnoreCase(System.getProperty("os.name"))) {
			String s = null;
			Process p = Runtime.getRuntime().exec("taskkill /PID " + pid);
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			if ((s = stdError.readLine()) != null) {
				jemmySupport.report("an error occured trying to execute kill process.");
				jemmySupport.report("the error details are: " + s);
				jemmySupport.report("error were printed to error stream.");
				return false;
			}
			jemmySupport.report("on linux, returning successfully after kill the process");
			return true;
		} else {
			throw new Exception("system is not supported yes.\nonly Windows and Linux are supported");
		}
	}

	public boolean checkNavigateButtonIsEnabled(String direction) throws Exception {
		if (direction.equalsIgnoreCase("right")) {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getScenarioNavigateForward());
		} else {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getScenarioNavigateBackword());
		}
	}

	/**
	 * returns true if the play button is enabled
	 * 
	 * @throws Exception
	 */
	public boolean checkIfPlayIsEnabled() throws Exception {
		JButtonOperator btnOp = jemmySupport.getButtonOperator(mainFrame, jmap.getPlayButton());
		return btnOp.isEnabled();
	}

	/**
	 * if a warning dialog is opened, returns true and closese dialog
	 * 
	 * @return
	 */
	public boolean checkIfWarningDialogOpenedAndCloseIt() {
		JDialogOperator dialogOP = jemmySupport.getDialogIfExists("Warning", 3000);
		if (dialogOP != null) {
			dialogOP.close();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param buttonName
	 * @return
	 * @throws Exception
	 */
	public boolean checkIfButtonIsEnabled(String buttonName) throws Exception {
		if (buttonName == "play") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getPlayButton());
		} else if (buttonName == "stop") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getStopButton());
		} else if (buttonName == "pause") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getPauseButton());
		} else if (buttonName == "refresh") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getRefreshButton());
		} else if (buttonName == "new_scenario") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getNewScenarioButton());
		} else if (buttonName == "save_scenario") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getSaveScenarioButton());
		} else if (buttonName == "save_scenario_as") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getScenarioCopyButton());
		} else if (buttonName == "delete") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getClearScenarioButton());
		} else if (buttonName == "open_scenario") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getOpenScenarioButton());
		} else if (buttonName == "previous_scenario") {
			return checkNavigateButtonIsEnabled("left");
		} else if (buttonName == "next_scenario") {
			return checkNavigateButtonIsEnabled("right");
		} else if (buttonName == "move_item_up") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getMoveTestUpButton());
		} else if (buttonName == "move_item_down") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getMoveTestDownButton());
		} else if (buttonName == "remove") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getRemoveTestsButton());
		} else if (buttonName == "view_test_code") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getViewTestCodeButton());
		} else if (buttonName == "log") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getLogButton());
		} else if (buttonName == "switch_project") {
			return jemmySupport.isButtonEnabled(mainFrame, jmap.getSwitchProjectButton());
		} else {
			return false;
		}
	}

	public int pushButton(String buttonName, int numberOfPresses) throws Exception {
		jemmySupport.report("****************** button name is: " + buttonName + " +++++++++++++++++++++++");
		int counter = 0;
		if (buttonName.equalsIgnoreCase("play")) {
			jemmySupport.pushButton(mainFrame, jmap.getPlayButton());
			return 0;
		} else if (buttonName.equalsIgnoreCase("stop")) {
			jemmySupport.pushButton(mainFrame, jmap.getStopButton());
			return 0;
		} else if (buttonName.equalsIgnoreCase("pause")) {
			jemmySupport.pushButton(mainFrame, jmap.getPauseButton());
			return 0;
		} else if (buttonName.equalsIgnoreCase("refresh")) {
			jemmySupport.report("inside refresh if: ");
			jemmySupport.report("numberOfPresses is: " + numberOfPresses);
			while (counter < numberOfPresses) {
				jemmySupport.pushButton(mainFrame, jmap.getRefreshButton());
				jemmySupport.WaitForDialogToClose(jmap.getRefreshRunnerDialog());
				jemmySupport.report("pushed button " + buttonName);
				counter++;
			}
			jemmySupport.report("pushed button " + counter + " times");
			return 0;
		} else if (buttonName.equalsIgnoreCase("new_scenario")) {
			jemmySupport.pushButton(mainFrame, jmap.getNewScenarioButton());
			return 0;
		} else if (buttonName.equalsIgnoreCase("save_scenario")) {
			jemmySupport.pushButton(mainFrame, jmap.getSaveScenarioButton());
			return 0;
		} else if (buttonName.equalsIgnoreCase("save_scenario_as")) {
			jemmySupport.pushButton(mainFrame, jmap.getScenarioCopyButton());
			return 0;
		} else if (buttonName.equalsIgnoreCase("delete")) {
			jemmySupport.pushButton(mainFrame, jmap.getClearScenarioButton());
			return 0;
		} else if (buttonName.equalsIgnoreCase("open_scenario")) {
			jemmySupport.pushButton(mainFrame, jmap.getOpenScenarioButton());
			return 0;
		} else if (buttonName.equalsIgnoreCase("previous_scenario")) {
			jemmySupport.report("inside previous_scenario if: ");
			jemmySupport.report("numberOfPresses is: " + numberOfPresses);
			jemmySupport.report("" + checkNavigateButtonIsEnabled("left"));
			while (counter < numberOfPresses && checkNavigateButtonIsEnabled("left")) {
				jemmySupport.pushButton(mainFrame, jmap.getScenarioNavigateBackword());
				Thread.sleep(500);
				counter++;
			}
			jemmySupport.report("pushed button " + counter + " times");
			return 0;
		} else if (buttonName.equalsIgnoreCase("next_scenario")) {
			jemmySupport.report("inside previous_scenario if: ");
			jemmySupport.report("numberOfPresses is: " + numberOfPresses);
			jemmySupport.report("" + checkNavigateButtonIsEnabled("right"));
			while (counter < numberOfPresses && checkNavigateButtonIsEnabled("right")) {
				jemmySupport.pushButton(mainFrame, jmap.getScenarioNavigateForward());
				Thread.sleep(500);
				counter++;
			}
			jemmySupport.report("pushed button " + counter + " times");
			return 0;
		} else if (buttonName.equalsIgnoreCase("move_item_up")) {
			while (counter < numberOfPresses && checkIfButtonIsEnabled(jmap.getMoveTestUpButton())) {
				jemmySupport.pushButton(mainFrame, jmap.getMoveTestUpButton());
				Thread.sleep(500);
				counter++;
			}
			return 0;
		} else if (buttonName.equalsIgnoreCase("move_item_down")) {
			while (counter < numberOfPresses && checkIfButtonIsEnabled(jmap.getMoveTestDownButton())) {
				jemmySupport.pushButton(mainFrame, jmap.getMoveTestDownButton());
				Thread.sleep(500);
				counter++;
			}
			return 0;
		} else if (buttonName.equalsIgnoreCase("remove")) {
			jemmySupport.pushButton(mainFrame, jmap.getRemoveTestsButton());
			return 0;
		} else if (buttonName.equalsIgnoreCase("view_test_code")) {
			jemmySupport.pushButton(mainFrame, jmap.getViewTestCodeButton());
			return 0;
		} else if (buttonName.equalsIgnoreCase("log")) {
			jemmySupport.pushButton(mainFrame, jmap.getLogButton());
			return 0;
		} else if (buttonName.equalsIgnoreCase("switch_project")) {
			jemmySupport.pushButton(mainFrame, jmap.getSwitchProjectButton());
			return 0;
		} else {
			return -1;
		}
	}


	/**
	 * push the refresh button in the runner
	 * 
	 * @return
	 * @throws Exception
	 */
	public int refresh() throws Exception {
		jemmySupport.pushButton(mainFrame, jmap.getRefreshButton());
		Thread.sleep(5000);
		// runner.handleEvent(TestTreeView.REFRESH_EVENT, null);
		return 0;
	}

	public void exitThroughMenu(boolean exit) throws Exception {
		JMenuOperator menuOperator = new JMenuOperator(mainFrame, "File");
		menuOperator.push();
		JMenuItemOperator exitMenuOperator = new JMenuItemOperator(mainFrame, "Exit");
		exitMenuOperator.push();
		JDialogOperator dialog = new JDialogOperator("Exit Confirmation");
		new JButtonOperator(dialog, new TipNameButtonFinder(exit ? "Yes" : "No")).push();
	}

	/**
	 * closes the runner by pressing the close button.
	 * 
	 * @return
	 * @throws Exception
	 */
	public int closeApp() throws Exception {
		exitThroughMenu(true);
		return 0;
	}

	public int waitForExecutionEnd(int num) throws InterruptedException {
		while (num > 0) {
			waitForExecutionEnd();
			num--;
		}

		return 0;
	}

	private synchronized void waitForExecutionEnd() throws InterruptedException {
		while (!executionEnded) {
			jemmySupport.report("stuck on wait");
			wait();
		}
	}
}
