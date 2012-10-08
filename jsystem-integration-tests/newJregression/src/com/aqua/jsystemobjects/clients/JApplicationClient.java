package com.aqua.jsystemobjects.clients;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import jsystem.extensions.analyzers.text.FindText;
import jsystem.framework.FrameworkOptions;

import org.jsystem.objects.xmlrpc.XmlRpcHelper;

import com.aqua.jsystemobjects.handlers.JApplicationHandler;
import com.aqua.utils.Commons;
import com.aqua.utils.FileUtils;

/**
 * 
 * @author Dan Hirsch
 * 
 */
public class JApplicationClient extends BaseClient {

	private static final String LOCAL_JSYSTEM_LOG_FILE_NAME = "jsystem0.log";

	public JApplicationClient(XmlRpcHelper connectionHandler) {
		super(connectionHandler);
	}

	/**
	 * transforms the key to a String by calling it's toString() method. and
	 * calls the remote server to set a property in the jsystem.property file.
	 * 
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void setJSystemProperty(FrameworkOptions key, String value) throws Exception {
		handleCommand("set jsystem property", "setJSystemProperty", key.toString(), value);
	}

	/**
	 * 
	 * @param key
	 * @return value
	 * @throws Exception
	 */
	public String getJSystemProperty(FrameworkOptions key) throws Exception {
		report.report("Get jsystem property with key " + key.toString());
		return (String) handleCommand("get jsystem property: " + key.toString(), "getJSystemProperty", key.toString());

	}

	public void setInitialJsystemProperties() throws Exception {
		for (Entry<FrameworkOptions, String> entry : Commons.getBaseJsystemProperties().entrySet()) {
			setJSystemProperty(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * launch the remote jsystem.
	 * 
	 * @param params
	 * @throws Exception
	 */
	public void launch(Object... params) throws Exception {
		handleCommand("launch", "launch", params);
	}

	/**
	 * "push" play. Block till execution finishes
	 */
	public void play() throws Exception {
		play(true);
	}

	/**
	 * play ,if block ==false we can add events before play ends
	 */
	public void play(boolean block) throws Exception {
		handleCommand("play", "play", block);
	}

	/**
	 * get a property value from db.properties of the remote machine that
	 * matches the specified key.
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String getDBProperty(String key) throws Exception {
		return (String) handleCommand("get remote DB property", "getDBProperty", key);
	}

	/**
	 * changes a specific value in the remote db.property to the specified
	 * value.
	 * 
	 * @param propertyKey
	 * @param newVal
	 * @return
	 * @throws Exception
	 */
	public boolean changeDbProperty(String propertyKey, String newVal) throws Exception {
		return (Boolean) handleCommand("change a property value in db.properties", "changeDbProperty", propertyKey,
				newVal);
	}

	/**
	 * refreshes the remote runner view.
	 * 
	 * @throws Exception
	 */
	public void refresh() throws Exception {
		handleCommand("refresh the runner", "refresh");
	}

	/**
	 * will activate the save scenario operation on the remote runner.
	 * 
	 * @throws Exception
	 */
	public void saveScenario() throws Exception {
		report.report("save scenario");
		handleCommand("save scenario", "saveScenario");
	}

	/**
	 * Will activate the save as scenario operation on the remote runner.
	 * 
	 * @param newScenarioName
	 *            The new name on which the scenario will saved to.
	 * @throws Exception
	 */
	public void saveScenarioAs(String newScenarioName) throws Exception {
		report.report("Save scenario as " + newScenarioName);
		handleCommand("Save scenario as", "saveScenarioAs", newScenarioName);
	}

	/**
	 * 
	 * @param scenarioName
	 * @param isExist
	 * @return
	 * @throws Exception
	 */
	public List<File> getScenarioFiles(String scenarioName) throws Exception {
		// TODO: Can be replaced with scenario.getScenarioFiles() call in the
		// handler side
		List<File> scenarioFileList = new ArrayList<File>();
		final String testsClassFolder = getJSystemProperty(FrameworkOptions.TESTS_CLASS_FOLDER);
		scenarioFileList.add(new File(testsClassFolder + File.separator + "scenarios", scenarioName + ".xml"));
		scenarioFileList.add(new File(testsClassFolder + File.separator + "scenarios", scenarioName + ".properties"));
		return scenarioFileList;
	}

	/**
	 * Changes the read permissions of the files of the specified scenario. <br>
	 * Notice that only setting the permissions to false is supported in this
	 * stage. <br>
	 * 
	 * @param scenarioName
	 *            The name of the scenario on which the files are suppose to be
	 *            changed.
	 * 
	 * @param readable
	 *            if <i>true</i> the files permissions will be set with write
	 *            permissions, if <i>false</i> they will be set as read only
	 * 
	 * @return true if and only if all the files permissions were set.
	 * @throws Exception
	 */
	public boolean setScenarioFilesReadable(final String scenarioName, final boolean readable) throws Exception {
		List<File> scenarioFiles = getScenarioFiles(scenarioName);
		for (File scenarioFile : scenarioFiles) {
			report.report("Setting scenario file " + scenarioFile + " permissions");
			if (!scenarioFile.exists()) {
				report.report("Scenario file " + scenarioFile + " is not exist");
				return false;
			}
			if (!readable) {
				if (!scenarioFile.setReadOnly()) {
					report.report("Failed to set file to read only");
					return false;
				}
			} else {
				report.report("Unsupported operation");
				return false;
			}
		}
		return true;
	}

	/**
	 * operate the undo operation on the reomte jsystem
	 * 
	 * @throws Exception
	 */
	public void undo() throws Exception {
		handleCommand("undo", "undo");
	}

	/**
	 * operate the redo operation on the remote jsystem.
	 * 
	 * @throws Exception
	 */
	public void redo() throws Exception {
		handleCommand("redo", "redo");
	}

	/**
	 * will close an opened dialog if it opens.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean checkIfWarningDialogOpenedAndCloseIt() throws Exception {
		return (Boolean) handleCommand("checks if a warning dialog was opened", "checkIfWarningDialogOpenedAndCloseIt");
	}

	/**
	 * pushes the button specified on the remote machine, numberOfPresses times.
	 * the number of presses is only relevant for the operations: refresh,
	 * next_scenario, previous_scenario, move_item_up, move_item_down
	 * 
	 * @param numberOfPresses
	 *            - number of times to press the button
	 * @param buttonName
	 * @throws Exception
	 */
	public void pushButton(String buttonName, int numberOfPresses) throws Exception {
		handleCommand("push a button with \"" + buttonName + "\" button name", "pushButton", buttonName,
				numberOfPresses);
	}

	public void pushButton(String buttonName) throws Exception {
		pushButton(buttonName, 1);
	}

	public boolean checkIfButtonIsEnabled(String buttonName) throws Exception {
		return (Boolean) handleCommand("check if button \"" + buttonName + "\" is enabled", "checkIfButtonIsEnabled",
				buttonName);
	}

	/**
	 * 
	 * @return true if the play button is enabled
	 * @throws Exception
	 */
	public Boolean checkIfPlayIsEnabled() throws Exception {
		return (Boolean) handleCommand("check if play is enabled", "checkIfPlayIsEnabled");
	}

	public Boolean checkNavigateButtonIsEnabled(String direction) throws Exception {
		return (Boolean) handleCommand("check Navigate " + direction + " ButtonIsEnabled",
				"checkNavigateButtonIsEnabled", direction);
	}

	@Override
	protected String getHandlerName() {
		return JApplicationHandler.class.getSimpleName();
	}

	public void keepAlive() throws Exception {
		File logfile = new File(System.getProperty("user.home") + File.separatorChar + "logfileKeepAlive.txt");
		FileWriter fw = new FileWriter(logfile);
		int result = (Integer) handleCommand("keep alive against xmlRpcServer", "keepAlive");
		if (result == 0) {
			report.report("server is alive");
			fw.write("server is Alive");
		} else {
			report.report("server is not up");
			fw.write("server is not up");
		}
	}

	/**
	 * 
	 * @return The JSystem error level as retrieved from the JSystem log file
	 * @throws Exception
	 */
	public int closeApp() throws Exception {
		File logFile = new File(getUserDir(), LOCAL_JSYSTEM_LOG_FILE_NAME);
		long mark = 0;
		if (logFile.exists()) {
			mark = logFile.length();
		}

		try {
			// closeApp throws and exception so catch it and if after 3 secs
			// trying to send request to server(refresh) and with connection
			// refuse then server is down.
			return (Integer) handleCommand("close the remote application", "closeApp");
		} catch (Exception e) {
			Thread.sleep(3000);
			if (e.getMessage().contains("Connection refused")) {
				report.report("successfully closed remote JSystem");
			}
		}
		int errorLevel = 0;
		if (logFile.exists()) {
			setTestAgainstObject(FileUtils.readFromPosition(logFile, mark));
			FindText analyzer = new FindText("System exit\\s(\\d+)", true, true, 2);
			if (isAnalyzeSuccess(analyzer)) {
				errorLevel = Integer.parseInt(analyzer.getCounter());
			}
		}
		report.report("System exit with error level: " + errorLevel);
		return errorLevel;
	}

	public void killRunnerProcess() throws Exception {
		try {
			handleCommand("kill the runner process", "killRunnerProcess");
		} catch (Exception e) {
			Thread.sleep(3000);
			try {
				refresh();
			} catch (Exception ex) {
				if (ex.getMessage().contains("Connection refused")) {
					report.report("successfully killed remote JSystem process");
				}
			}
		}
		return;
	}

	public void waitForExecutionEnd() throws Exception {
		handleCommand("Wait for Execution to end", "waitForExecutionEnd", 1);
	}

}
