/*
 * Created on 22/11/2006
 *
 * Copyright 2005 AQUA Software, LTD. All rights reserved.
 * AQUA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.aqua.jsystemobject;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.extensions.analyzers.text.FindText;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.analyzer.AnalyzerException;
import jsystem.framework.analyzer.AnalyzerParameterImpl;
import jsystem.framework.report.Reporter;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.properties.GUIFrameworkOptions;
import jsystem.treeui.teststable.ScenarioTreeKeyHandler.Keys;
import jsystem.utils.FileUtils;
import jsystem.utils.XmlUtils;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.Commons;
import utils.ScenarioNodes;
import utils.ScenarioUtils;

public class JSystemClient extends SystemObjectImpl {
	JsystemMapping jmap;
	private static final String SCENARIOS_PREFIX = "scenarios/";
	private static final int retryConnectfNumber = 5;
	private static final int sleepDuraionInMilliSeconds = 2000;

	public enum JsystemPropertiesDialogButton {
		SAVE(JsystemMapping.getInstance().getJSystemPropertiesSaveButtonName()), RESTORE_DEFAULTS(JsystemMapping
				.getInstance().getJSystemPropertiesSystemDefaultButtonName()), CANCEL(JsystemMapping.getInstance()
				.getJSystemPropertiesCancleButtonName());

		String name;

		JsystemPropertiesDialogButton(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	String host = "127.0.0.1";

	int port = 8082;

	String reportsPath = null;

	String lastHtmlReportDir;

	boolean maskExit = false;

	/**
	 * The server process
	 */
	private Process serverProcess;

	private String userDir = null;

	public JSystemClient() {
		super();
	}

	public JSystemClient(Process serverProcess, int port, String userDir) {
		this();
		this.serverProcess = serverProcess;
		this.port = port;
		this.userDir = userDir;
	}

	public int getWebServerPort() throws Exception {
		return (Integer) handleXmlCommand("get Web Server port number", "jsystem.getWebServerPort");
	}

	/**
	 * launch runner
	 */
	public void launch() throws Exception {
		launch("", true);
	}

	/**
	 * launch runner
	 * 
	 * @param disableZip
	 *            if True will disable zip
	 * @throws Exception
	 */
	public void launch(boolean disableZip) throws Exception {
		launch("", disableZip);
	}

	/**
	 * launch runner
	 */
	public void launch(String sutFile) throws Exception {
		launch(sutFile, true);
	}

	/**
	 * launch runner
	 * 
	 * @param sutFile
	 *            the sut to start with
	 * @param disableZip
	 *            if True will disable zip
	 * @throws Exception
	 */
	public void launch(final String sutFile, boolean disableZip) throws Exception {
		report.report("Web Server active on port " + getWebServerPort());
		String s = (String) handleXmlCommand("launch Client", "jsystem.launch", sutFile, disableZip);
		if (!s.startsWith("ok")) {
			report.report("Problem with launch", s, false);
			throw new Exception(s);
		} else {
			report.report(s);
		}
		// handleXmlCommand("launch Client", "jsystem.launch2",
		// sutFile,disableZip);
		Thread.sleep(2000);
	}

	public void extract(String envZipPath) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(envZipPath);
		handleXmlCommand("extract", "jsystem.extract", v);
		Thread.sleep(2000);
	}

	/**
	 * close runner
	 */
	public void exit() throws Exception {
		if (maskExit) {
			return;
		}
		handleXmlCommand("exit Client", "jsystem.exit", new Vector<Object>(), true);
		Thread t = new Thread() {
			public void run() {
				if (serverProcess != null) {
					try {
						serverProcess.waitFor();
					} catch (InterruptedException e) {
					}
				}
			}
		};
		t.start();
		t.join();
		if (t.isAlive()) {
			t.interrupt();
		}
		JSystemEnvControllerOld.setUseExistingServer(false);
	}

	/**
	 * Activate export wizard.
	 * 
	 * @param jarPath
	 * @param compiledOutput
	 * @param systemObjects
	 * @param exportRunner
	 * @throws Exception
	 */
	public void activateExportWizard(final String jarPath, final boolean exportTests, final boolean exportScenarios,
			final boolean exportRunner, final boolean exportLog, final boolean exportSut, final boolean exportLib,
			final boolean exportJdk) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(jarPath);
		v.addElement(Boolean.valueOf(exportTests));
		v.addElement(Boolean.valueOf(exportScenarios));
		v.addElement(Boolean.valueOf(exportRunner));
		v.addElement(Boolean.valueOf(exportLog));
		v.addElement(Boolean.valueOf(exportSut));
		v.addElement(Boolean.valueOf(exportLib));
		v.addElement(Boolean.valueOf(exportJdk));
		handleXmlCommand("activateExportWizard", "jsystem.activateExportWizard", v);
	}

	/**
	 * "push" play
	 */
	public void play() throws Exception {
		play(false);
	}

	/**
	 * play ,if block ==false we can add events before play ends
	 */
	public void play(boolean block) throws Exception {
		handleXmlCommand("play", "jsystem.play", block);
	}

	/**
	 * use to set JSystemProperty value Properties names : tests.dir tests.src
	 * htmlReportDir shutdown.threads lib.path reporter.classes sutClassName
	 * sutFile fixture.return sysobj.close html.dir.old reporter.addtime
	 * html.tree ant.home ant.notify.disable repeat
	 */
	public void setJSystemProperty(FrameworkOptions key, String value) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(key.toString());
		v.add(value);

		handleXmlCommand("set jsystem property " + key + " to " + value, "jsystem.setJSystemProperty", v);
	}

	public void setInitialJsystemProperties() throws Exception {
		for (Entry<FrameworkOptions, String> entry : Commons.getBaseJsystemProperties().entrySet()) {
			setJSystemProperty(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String getJSystemProperty(FrameworkOptions key) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(key.toString());

		return (String) handleXmlCommand("get jsystem property: " + key, "jsystem.getJSystemProperty", v);

	}

	/**
	 * get server user directory
	 */
	public String getUserDir() throws Exception {
		if (userDir == null) {
			Vector<Object> v = new Vector<Object>();
			userDir = (String) handleXmlCommand("get user dir", "jsystem.getUserDir", v);
		}
		return userDir;
	}

	private synchronized Object handleXmlCommand(String title, String command, Object... objects) throws Exception {
		Vector<Object> v = new Vector<Object>();
		for (Object object : objects) {
			if (object == null) {
				v.add("");
			} else {
				v.addElement(object);
			}
		}
		return handleXmlCommand(title, command, v);
	}

	/**
	 * transfer xml request to server
	 * 
	 */
	private synchronized Object handleXmlCommand(String title, String command, Vector<Object> v) throws Exception {
		int timeout = 0;
		Object o = null;
		report.report(title + " (" + port + ")");
		while (true) {
			try {
				if (timeout > 0) {
					report.report("trying to send xmlRpc command for the " + timeout + " time");
				}
				o = execute(command, v);
			} catch (Exception e) {
				if (e.getMessage().contains("Connection refused")) {
					report.report("received connection refused, retrying");
					if (timeout < retryConnectfNumber) {
						Thread.sleep(sleepDuraionInMilliSeconds);
						timeout++;
						continue;
					}
				}
				throw e;
			}
			if (o instanceof XmlRpcException) {
				// server exception

				throw (XmlRpcException) o;
			}
			return o;
		}
	}

	private synchronized Object handleXmlCommand(String title, String command, Vector<Object> v, boolean ignoreError)
			throws Exception {
		Object o = null;
		try {
			o = execute(command, v);
		} catch (Exception e) {
			if (!ignoreError) {
				throw e;
			}
		}
		if (!title.equals("get report.0.xml path")) {
			report.report(title, o + "", true);
		}
		if (o instanceof XmlRpcException) {
			// server exception
			if (!ignoreError) {
				throw (XmlRpcException) o;
			}

		}
		setTestAgainstObject(o + "");
		return o;
	}

	/**
	 * creates an XmlRpcClient and calls it's execute method to connect to a
	 * server
	 * 
	 * @param command
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Object execute(String command, Vector<Object> params) throws Exception {
		XmlRpcClient client = new XmlRpcClient("http://" + host + ":" + port + "/RPC2");
		return client.execute(command, params);
	}

	public void waitForAlongTime() throws Exception {
		handleXmlCommand("waitForAlongTime", "jsystem.waitForAlongTime", new Vector<Object>());
	}

	/**
	 * 
	 */
	public void setUserDir(String userDir) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(userDir);
		handleXmlCommand("set user dir: " + userDir, "jsystem.setUserDir", v);
	}

	/**
	 * clean scenario instead use the ScenarioUtils.createAndCleanScenario() and
	 * pass it the String returned from jsystem.getCurrentScenario for example:
	 * String scenarioName = jsystem.getCurrentScenario();
	 * ScenarioUtils.createAndCleanScenario(scenarioName);
	 * 
	 * @deprecated
	 */
	public void cleanCurrentScenario() throws Exception {
		handleXmlCommand("clean current scenario", "jsystem.cleanCurrentScenario", new Vector<Object>());
		Thread.sleep(500);
	}

	/**
	 * will activate the save scenario operation on the remote runner.
	 * 
	 * @throws Exception
	 */
	public void saveScenario() throws Exception {
		handleXmlCommand("save scenario", "jsystem.saveScenario", new Vector<Object>());
	}

	/**
	 * get current scenario name
	 */
	public String getCurrentScenario() throws Exception {
		return (String) handleXmlCommand("get current scenario", "jsystem.getCurrentScenario", new Vector<Object>());
	}

	/**
	 * Waits for the runner's warning dialog to open and presses th OK button.
	 */
	public void waitForWarningDialog() throws Exception {
		handleXmlCommand("Wait for warning dialog", "jsystem.waitForWarningDialog", new Vector<Object>());
		Thread.sleep(500);
	}

	/**
	 * call select scenario on remote runner.
	 */
	public void selectSenario(String scenarioName) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(scenarioName);
		handleXmlCommand("select scenario: " + scenarioName, "jsystem.selectSenario", v);
		Thread.sleep(500);
	}

	public void waitForRunEnd() throws Exception {
		handleXmlCommand("wait for run end", "jsystem.waitForExecutionEnd", new Vector<Object>());
	}

	public void waitForTestToEnd() throws Exception {
		handleXmlCommand("wait For Test To End", "jsystem.waitForTestToEnd");
	}

	// When the Run repeat itself <amount> times
	public void waitForRunEnd(int amount) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(amount);
		handleXmlCommand("wait for run end", "jsystem.waitForRunEnd", v);
	}

	// When the Run repeat itself <amount> times
	public void waitForRunEndUntilLeftRepeatAmountIs(int amount) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(amount);
		handleXmlCommand("wait for run end", "jsystem.waitForRunEndUntilLeftRepeatAmountIs", v);
	}

	public void checkTestLocationInScenariosRoot(String scenarioName, String testName, int index) throws Exception {
		int indexOfTestInScenario = getTestIndexInScenario(scenarioName, testName);
		setTestAgainstObject(indexOfTestInScenario);
		analyze(new NumberCompare(compareOption.EQUAL, index, 0));
	}
	
	public int getTestIndexInScenario(String scenarioName, String testName) throws Exception {
		int indexOfTestInScenario = (Integer) handleXmlCommand("wait for run end", "jsystem.getTestIndexInScenario",
				scenarioName, testName);
		return indexOfTestInScenario;
	}

	/**
	 * 
	 * @param scenarioName
	 * @param isExist
	 * @return
	 * @throws Exception
	 */
	public boolean checkScenarioExist(String scenarioName, boolean isExist) throws Exception {

		String pathToScenarioXml = getJSystemProperty(FrameworkOptions.TESTS_CLASS_FOLDER) + File.separator
				+ "scenarios" + File.separator + scenarioName + ".xml";
		File f1 = new File(pathToScenarioXml);
		if (!isExist) {
			return !f1.exists();
		}
		return f1.exists();
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
	 * This method gets a scenario name and returns the number of tests in this
	 * scenario including tests of its sub-scenarios from the scenario xml
	 * 
	 * @param scenarioName
	 * @return
	 * @throws Exception
	 */
	public int numOfTestsInScenario(String scenarioName) throws Exception {
		int numOfTest = 0;
		String pathToScenarioXml = getJSystemProperty(FrameworkOptions.TESTS_CLASS_FOLDER) + File.separator
				+ "scenarios" + File.separator + scenarioName + ".xml";
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
								numOfTest = numOfTest
										+ numOfTestsInScenario(ScenarioUtils.findScenarioInPath(scenarioPath));
							}
						} // for k
					}
				} // for j
			}
		} // for i

		return numOfTest;
	}

	/**
	 * 
	 * @param scenarioName
	 * @param numOfTests
	 * @throws Exception
	 */
	public void checkNumberOfTestsExistInScenario(String scenarioName, int numOfTests) throws Exception {
		int actualNumberOfTests = getNumOfTestsInScenario(scenarioName);
		if (actualNumberOfTests == numOfTests) {
			report.report("The Number Of Tests In Scenario \"" + scenarioName + "\" is: " + numOfTests);
		} else {
			report.report("The Number Of Tests In Scenario \"" + scenarioName + "\" is: " + actualNumberOfTests
					+ ", it should be: " + numOfTests, false);
		}
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
		String pathToScenarioXml = getJSystemProperty(FrameworkOptions.TESTS_CLASS_FOLDER) + File.separator
				+ "scenarios" + File.separator + scenarioName + ".xml";
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
	 * checks the number of subScenarios under a parent scenario in the scenario
	 * tree
	 * 
	 * @param scenarioName
	 * @param numOfTests
	 * @throws Exception
	 */
	public void checkNumberOfSubScenariosExistInScenario(String parentScenarioName, int numOfSubScenarios)
			throws Exception {
		if (numOfSubScenariosInScenario(parentScenarioName) == numOfSubScenarios) {
			report.report("The Number Of Sub-Scenarios In Scenario \"" + parentScenarioName + "\" is: "
					+ numOfSubScenarios);
		} else {
			report.report("The Number Of Sub-Scenarios In Scenario \"" + parentScenarioName + "\" is: "
					+ numOfSubScenariosInScenario(parentScenarioName) + ", it should be: " + numOfSubScenarios, false);
		}
	}

	public void checkTestExistInScenario(String scenarioName, String testName, boolean exist) throws Exception {
		String pathToScenarioXml = getJSystemProperty(FrameworkOptions.TESTS_CLASS_FOLDER) + File.separator
				+ scenarioName + ".xml";
		setTestAgainstObject(FileUtils.readDocumentFromFile(new File(pathToScenarioXml)));
	}

	public void copyScenario(String newScenarioName) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(newScenarioName);
		handleXmlCommand("copy scenario", "jsystem.copyScenario", v);
	}

	public void stop() throws Exception {
		handleXmlCommand("stop", "jsystem.stop");
	}

	public void gracefulStop() throws Exception {
		handleXmlCommand("stop", "jsystem.gracefulStop");
	}

	public void initReporters() throws Exception {
		handleXmlCommand("init reporters", "jsystem.initReporters");
		Thread.sleep(3000);
	}

	public void addTest(int location, String methodName, String className, boolean equals) throws Exception {
		handleXmlCommand("add test: " + className + "." + methodName, "jsystem.addTest", location, methodName,
				className, equals);
		Thread.sleep(500);
	}

	public void selectTest(int location) throws Exception {
		handleXmlCommand("select tests", "jsystem.selectTest", location);
		Thread.sleep(500);
	}

	public void addTest(String methodName, String className, boolean equals) throws Exception {
		addTest(methodName, className, 1, equals);
	}

	/**
	 * add a given test several times
	 * 
	 * @param methodName
	 *            the test class name
	 * @param className
	 *            the test method
	 * @param amount
	 *            the amount of times to add the test
	 * @param equals
	 *            true->equals to method name ,false->start with method name           
	 * @throws Exception
	 */
	public void addTest(String methodName, String className, int amount, boolean equals) throws Exception {
		handleXmlCommand("add test: " + methodName + "." + className, "jsystem.addTest", methodName, className, amount, equals);
		Thread.sleep(500);
	}

	/**
	 * 1. Gets a location on the Scenario Tree 2. returns the Current Element in
	 * that location on Scenario Tree.
	 * 
	 * @param iIndex
	 * @return String
	 * @throws Exception
	 */
	public String scenarioElement(int iIndex) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(iIndex);

		return (String) handleXmlCommand("return the current element of the tree, index: ", "jsystem.scenarioElement",
				v);
	}

	public void deleteTest(int testIndex) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(Integer.valueOf(testIndex));
		handleXmlCommand("delete test index: " + testIndex, "jsystem.deleteTest", v);
	}

	public void deleteTest(String testName) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(testName);
		handleXmlCommand("delete test : " + testName, "jsystem.deleteTest", v);
	}

	public void moveTestUp(int testIndex) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(Integer.valueOf(testIndex));
		handleXmlCommand("move up, test index: " + testIndex, "jsystem.moveTestUp", v);
	}

	public void moveTestUpByMenuOption(int testIndex) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(Integer.valueOf(testIndex));
		handleXmlCommand("move up by menu option, test index: " + testIndex, "jsystem.moveTestUpByMenuOption", v);
	}

	// /**
	// * select the root scenario, open right click menu and select
	// "Restore Parameters to Default"
	// * @throws Exception
	// */
	// public void restoreScenarioParametersToDefault() throws Exception {
	// handleXmlCommand("restore scenario parameters to default",
	// "jsystem.restoreScenarioParametersToDefault");
	// }
	//
	/**
	 * select the given Scenario (by test index) and check\ uncheck the
	 * "Show Reference Recursively" checkbox
	 * 
	 * @param testIndex
	 *            the test index to identify the scenario (0 is the root)
	 * @param show
	 *            True will check, False will uncheck
	 * @throws Exception
	 */
	// to move to a client-handler
	public void setShowReferenceRecursively(int testIndex, boolean show) throws Exception {
		String check = show ? "check" : "uncheck";
		handleXmlCommand(check + " recursive Reference box for test index: " + testIndex,
				"jsystem.setShowReferenceRecursively", testIndex, show);
	}

	public void moveTestDown(int testIndex) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(Integer.valueOf(testIndex));
		handleXmlCommand("move down, test index: " + testIndex, "jsystem.moveTestDown", v);
	}

	public void moveTestDownByMenuOption(int testIndex) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(Integer.valueOf(testIndex));
		handleXmlCommand("move down by menu option, test index: " + testIndex, "jsystem.moveTestDownByMenuOption", v);
	}

	public void playPause() throws Exception {
		handleXmlCommand("pause", "jsystem.playPause");
	}

	/**
	 * 
	 * @return true if the play button is enabled
	 * @throws Exception
	 */
	public Boolean checkIfPlayIsEnabled() throws Exception {
		return (Boolean) handleXmlCommand("check if play is enabled", "jsystem.checkIfPlayIsEnabled");
	}

	public void changeSut(String sutName) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(sutName);
		handleXmlCommand("change sut: " + sutName, "jsystem.changeSut", v);
	}

	public void refresh() throws Exception {
		handleXmlCommand("refresh", "jsystem.refresh");
	}

	public void showLog() throws Exception {
		handleXmlCommand("show log", "jsystem.showLog");
	}

	/**
	 * use this function after play(false)
	 */
	public void waitForPlayEnd() throws Exception {
		handleXmlCommand("wait for play end", "jsystem.waitForPlayEnd");
	}

	/**
	 * change test directory to the given dir and change sut to the given sut
	 * 
	 * @param dir
	 *            the new classes dir
	 * @param sut
	 *            the sut to select
	 * @param sutComboMustOpen
	 *            if True will verify that the "select sut" dialog opened
	 * @return False if Sut dialog was expected and not opened
	 * @throws Exception
	 */
	public boolean changeTestDir(String dir, String sut, boolean sutComboMustOpen) throws Exception {
		if (sut == null) {
			sut = "";
		}
		return Boolean.parseBoolean(handleXmlCommand("change test directory", "jsystem.changeTestDir", dir, sut,
				sutComboMustOpen).toString());
	}

	/**
	 * set value to the freeze on fail check box
	 */
	public void setFreezeOnFail(boolean freeze) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(freeze);
		// addEventToMap(JSystemEvents.SetFreezeOnFail, v);
		handleXmlCommand("set Freeze On Fail property", "jsystem.setFreezeOnFail", v);
	}

	/**
	 * set value to the repeat on fail check box
	 */
	public void setReapit(boolean reapit) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(reapit);
		handleXmlCommand("set reapit property", "jsystem.setReapit", v);

	}

	/**
	 * set value to the repeat on fail check box and also change repeat amount
	 * value
	 */
	public void setRepAmount(int amount) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(amount);
		handleXmlCommand("set reapit amount", "jsystem.setRepAmount", v);
	}

	public void createScenario(String name) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(name);
		handleXmlCommand("create scenario: " + name, "jsystem.createScenario", v);
	}

	/**
	 * check if staus of repeat checkBox equals to "status"
	 */
	public void checkIsRepeatSet(boolean status) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(status);
		boolean selected = (Boolean) handleXmlCommand("check repeat status", "jsystem.checkIsRepeatSet", v);
		if (selected != status) {
			report.report("repeate checkbox is not in the expected state", false);
			throw new Exception("repeate checkbox is not in the expected state");
		}
	}

	public void checkTest(int testIndex, boolean check) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(testIndex);
		v.addElement(check);
		handleXmlCommand("checkTest, index: " + testIndex + ", check: " + check, "jsystem.checkTest", v);
	}

	public void waitForFreezeDialog() throws Exception {
		Vector<Object> v = new Vector<Object>();
		handleXmlCommand("waitForFreezeDialog", "jsystem.waitForFreezeDialog", v);
	}

	public String getCurrentFixture() throws Exception {
		Vector<Object> v = new Vector<Object>();
		return (String) handleXmlCommand("getCurrentFixture", "jsystem.getCurrentFixture", v);
	}

	public void setDisableFixture(boolean disable) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(Boolean.valueOf(disable));
		handleXmlCommand("setDisableFixture: " + disable, "jsystem.setDisableFixture", v);
	}

	public void goToFixture(String fixtureName) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(fixtureName);
		handleXmlCommand("goToFixture: " + fixtureName, "jsystem.goToFixture", v);
	}

	public void setCurrentFixture(String fixturename) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(fixturename);
		handleXmlCommand("setCurrentFixture: " + fixturename, "jsystem.setCurrentFixture", v);
	}

	public void failToFixture(String fixtureName) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(fixtureName);
		handleXmlCommand("failToFixture: " + fixtureName, "jsystem.failToFixture", v);
	}

	/**
	 * get path for report dir
	 */
	public String getReportXmlFile() throws Exception {
		String dir = (String) handleXmlCommand("get report.0.xml path", "jsystem.getReportDir");
		return dir;
	}

	/**
	 * get path for report dir
	 */
	public Properties getSummaryProperties() throws Exception {
		String dir = (String) handleXmlCommand("get Summary file path", "jsystem.getSummaryFile");
		return FileUtils.loadPropertiesFromFile(dir);
	}

	public Process getServerProcess() {
		return serverProcess;
	}

	public void setServerProcess(Process serverProcess) {
		this.serverProcess = serverProcess;
	}

	public void checkTestPass(int testIndex) throws Exception {
		checkTestStatus(testIndex, true);
	}
	
	public void checkTestStatus(int testIndex, boolean pass) throws Exception {
		File reportXml = new File(getReportXmlFile());
		if (!reportXml.exists()) {
			report.report("checkTestPass file, reporter xml file: " + reportXml.getAbsolutePath() + ", wasn't found",
					false);
			throw new FileNotFoundException(reportXml.getAbsolutePath());
		}
		setTestAgainstObject(FileUtils.readDocumentFromFile(reportXml));
		analyze(new XPathAttribEquals("/reports/test[" + testIndex + "]", "status", pass + ""));
	}

	public void checkNumberOfTestsPass(int numberOfTests) throws Exception {
		File reportXml = new File(getReportXmlFile());
		if (!reportXml.exists()) {
			report.report("checkTestPath file, reporter xml file: " + reportXml.getAbsolutePath() + ", wasn't found",
					false);
			throw new FileNotFoundException(reportXml.getAbsolutePath());
		}
		setTestAgainstObject(FileUtils.readDocumentFromFile(reportXml));
		analyze(new XPathNumberOfElements("/reports/test[@status=\"true\"]", numberOfTests));
	}

	public void checkReporterSummaryProperty(String propertyName, String propertyValue, boolean exists)
			throws Exception {
		File reportXml = new File(getReportXmlFile());
		if (!reportXml.exists()) {
			report.report("checkTestPath file, reporter xml file: " + reportXml.getAbsolutePath() + ", wasn't found",
					false);
			throw new FileNotFoundException(reportXml.getAbsolutePath());
		}
		setTestAgainstObject(FileUtils.readDocumentFromFile(reportXml));
		int number = exists ? 1 : 0;
		analyze(new XPathNumberOfElements("/reports[@" + propertyName + "=\"" + propertyValue + "\"]", number));
	}

	public void checkTestProperties(int testIndex, Properties props) throws Exception {
		File reportXml = new File(getReportXmlFile());
		if (!reportXml.exists()) {
			report.report("checkTestPath file, reporter xml file: " + reportXml.getAbsolutePath() + ", wasn't found",
					false);
			throw new FileNotFoundException(reportXml.getAbsolutePath());
		}
		setTestAgainstObject(FileUtils.readDocumentFromFile(reportXml));
		analyze(new TestPropertiesAnalyzer(testIndex, props));
	}

	public void checkNumberOfTestExecuted(int numberOfTests) throws Exception {
		File reportXml = new File(getReportXmlFile());
		if (!reportXml.exists()) {
			report.report("checkNumberOfTestExecuted file, reporter xml file: " + reportXml.getAbsolutePath()
					+ ", wasn't found", false);
			throw new FileNotFoundException(reportXml.getAbsolutePath());
		}
		setTestAgainstObject(FileUtils.readDocumentFromFile(reportXml));
		analyze(new XPathNumberOfElements("/reports/test", numberOfTests));
	}

	/**
	 * Check if the report.0.xml clean from tests search from the word Setup in
	 * the text of the report log . finding this String will tell us if we have
	 * tests in the log or not.
	 * 
	 * @return - true if the report if empty from tests false if not
	 * @throws Exception
	 */
	public boolean checkReportIsEmpty() throws Exception {
		boolean status;
		setTestAgainstObject((FileUtils.read(getReportXmlFile())));
		FindText findText = new FindText("Setup=");

		if (isAnalyzeSuccess(findText)) {
			status = false;
		} else {
			status = true;
		}

		return status;

	}

	public void checkXmlTestAttribute(int testIndex, String attribute, String value) throws Exception {
		File reportXml = new File(getReportXmlFile());
		if (!reportXml.exists()) {
			report.report("checkXmlTestAttribute file, reporter xml file: " + reportXml.getAbsolutePath()
					+ ", wasn't found", false);
			throw new FileNotFoundException(reportXml.getAbsolutePath());
		}
		setTestAgainstObject(FileUtils.readDocumentFromFile(reportXml));
		analyze(new XPathAttribContainText("/reports/test[" + testIndex + "]", attribute, value));
	}

	public void checkXmlTestAttributeNotExist(int testIndex, String attribute, String value) throws Exception {
		File reportXml = new File(getReportXmlFile());
		if (!reportXml.exists()) {
			report.report("checkXmlTestAttributeNotExist file, reporter xml file: " + reportXml.getAbsolutePath()
					+ ", wasn't found", false);
			throw new FileNotFoundException(reportXml.getAbsolutePath());
		}
		setTestAgainstObject(FileUtils.readDocumentFromFile(reportXml));
		AnalyzerParameterImpl analyze = new XPathAttribContainText("/reports/test[" + testIndex + "]", attribute, value);
		boolean status = !isAnalyzeSuccess(analyze);
		report.report(analyze.getTitle(), analyze.getMessage(), status);
		if (!status) {
			throw new AnalyzerException(analyze.getTitle());
		}
	}

	public void warnOnStdError() throws Exception {
		File error = new File(getUserDir(), "error.txt");
		String err = FileUtils.read(error);
		if (err == null || err.equals("")) {
			return;
		}
		if (err.toLowerCase().indexOf("exception") >= 0) {
			report.report("Found data in std error", err, Reporter.WARNING, false);
		}
	}

	public void screenCapture() throws Exception {
		BufferedImage screencapture = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit()
				.getScreenSize()));

		// Save as JPEG
		File file = new File(report.getCurrentTestFolder(), "screencapture.jpg");
		ImageIO.write(screencapture, "jpg", file);
		report.addLink("screen capture", "screencapture.jpg");

	}

	public void setTestParameter(int testIndex, String tab, String paramName, String value, boolean isCombo)
			throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(new Integer(testIndex));
		v.addElement(tab);
		v.addElement(paramName);
		v.addElement(value);
		v.addElement(isCombo);
		handleXmlCommand("setTestParameter, test index: " + testIndex + " tab: " + tab + ", param name: " + paramName
				+ ", value: " + value + " ,isCombo" + isCombo, "jsystem.setTestParameter", v);
	}

	public void setTestParameter(int testIndex, String tab, String paramName, String value, boolean isCombo,
			boolean isScenario) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(new Integer(testIndex));
		v.addElement(tab);
		v.addElement(paramName);
		v.addElement(value);
		v.addElement(isCombo);
		v.addElement(isScenario);
		handleXmlCommand("setTestParameter", "jsystem.setTestParameter", v);
	}

	public void setTestParameter(int testIndex, String tab, String paramName, String value, boolean isCombo,
			boolean isScenario, boolean approve) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(new Integer(testIndex));
		v.addElement(tab);
		v.addElement(paramName);
		v.addElement(value);
		v.addElement(isCombo);
		v.addElement(isScenario);
		v.addElement(approve);
		handleXmlCommand("setTestParameter", "jsystem.setTestParameter", v);
	}

	/**
	 * set a Scenario reference parameter value
	 * 
	 * @param testIndex
	 *            the scenario test index
	 * @param paramName
	 *            the parameter name (Section is always "Scenario Parameters"
	 * @param value
	 *            the value to set
	 * @param approveRecursive
	 *            approve Confirm recursive apply dialog
	 * @param approveUpdate
	 *            approve confirm regular apply dialog
	 * @throws Exception
	 */
	public void setScenarioParameter(int testIndex, String paramName, String value, boolean approveRecursive,
			boolean approveUpdate) throws Exception {
		handleXmlCommand("Setting Scenario Reference parameter", "jsystem.setScenarioParameter", testIndex, paramName,
				value, approveRecursive, approveUpdate);
	}

	public String getTestUserDocumentation(int testIndex) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(new Integer(testIndex));
		return (String) handleXmlCommand("getTestUserDocumentation, test index: " + testIndex,
				"jsystem.getTestUserDocumentation", v);
	}

	public String getTestJavaDoc(int testIndex) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(new Integer(testIndex));
		return (String) handleXmlCommand("getTestJavaDoc, test index: " + testIndex, "jsystem.getTestJavaDoc", v);
	}

	public void setTestUserDocumentation(int testIndex, String userDoc) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(new Integer(testIndex));
		v.addElement(userDoc);
		handleXmlCommand("setTestUserDocumentation, test index: " + testIndex + " userDoc: " + userDoc,
				"jsystem.setTestUserDocumentation", v);
	}

	public void publishReportWithWait(String description, String SUT, String version, String build, String station)
			throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(description);
		v.addElement(SUT);
		v.addElement(version);
		v.addElement(build);
		v.addElement(station);
		handleXmlCommand("publish Report And Wait Until Dialog Is Open: description-" + description + " SUT-" + SUT
				+ " Version-" + version + " Build-" + build + " Station-" + station, "jsystem.publishReportWithWait", v);
	}

	public void publishReport(String description, String SUT, String version, String build, String station)
			throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(description);
		v.addElement(SUT);
		v.addElement(version);
		v.addElement(build);
		v.addElement(station);
		handleXmlCommand("publish Report: description-" + description + " SUT-" + SUT + " Version-" + version
				+ " Build-" + build + " Station-" + station, "jsystem.publishReport", v);
	}

	public void addPublishResultEvent() throws Exception {
		Vector<Object> v = new Vector<Object>();
		handleXmlCommand("addPublishResultEvent", "jsystem.addPublishResultEvent", v);
	}

	public void publishReportWithoutSaving() throws Exception {
		handleXmlCommand("Publish report without saving", "jsystem.publishReportWithoutSaving");
	}

	public void waitForMessage(String msgType) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(msgType);
		handleXmlCommand("waitForMessage: msgType-" + msgType, "jsystem.waitForMessage", v);
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
		return (Integer) handleXmlCommand("mapTest: test index-" + testIdx, "jsystem.mapTest", v);

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
		return (Integer) handleXmlCommand("unmapTest: test index-" + testIdx, "jsystem.unmapTest", v);
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
		return (Integer) handleXmlCommand("mapTest: test index-" + scenarioIdx, "jsystem.mapTest", v);
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
		return (Integer) handleXmlCommand("unmapTest: test index-" + scenarioIdx, "jsystem.unmapTest", v);
	}

	public void CollapseExpandScenario(int testIndex) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(testIndex);
		handleXmlCommand("Collapse & Expand: test index-" + testIndex, "jsystem.CollapseExpandScenario", v);
	}

	public boolean isMaskExit() {
		return maskExit;
	}

	public void setMaskExit(boolean maskExit) {
		this.maskExit = maskExit;
	}

	public String verifyParameterseExist(String parm, int testIndex, String tab) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(parm);
		v.addElement(testIndex);
		v.addElement(tab);
		return (String) handleXmlCommand("Verify that param-" + parm + " exist in test",
				"jsystem.verifyParameterseExist", v);
	}

	public String getParameterDescription(String parm, int testIndex, String tab) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(parm);
		v.addElement(testIndex);
		v.addElement(tab);
		return (String) handleXmlCommand("Get -" + parm + " description", "jsystem.getParameterDescription", v);
	}

	public String getParameterValue(String parm, int testIndex, String tab) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(parm);
		v.addElement(testIndex);
		v.addElement(tab);
		return (String) handleXmlCommand("Get -" + parm + " value", "jsystem.getParameterValue", v);
	}

	public String setTabSorting(int testIndex, int sortType) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(testIndex);
		v.addElement(sortType);
		return (String) handleXmlCommand("Set -section order to " + sortType, "jsystem.setTabSorting", v);
	}

	public int getTabIndex(int testIndex, String tab) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(testIndex);
		v.addElement(tab);
		return (Integer) handleXmlCommand("Get tab " + tab + " index", "jsystem.getTabIndex", v);
	}

	public String getActiveTabTab(int testIndex) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(testIndex);
		return (String) handleXmlCommand("Get active tab ", "jsystem.getActiveTab", v);
	}

	public void verifyParameterIndexAndEditability(String parm, int testIndex, String tab, int paramIndex,
			boolean isEditable) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(parm);
		v.addElement(testIndex);
		v.addElement(tab);
		v.addElement(paramIndex);
		v.addElement(isEditable);
		String res = (String) handleXmlCommand("Verify that param-" + parm
				+ " exist in test and verify it's index and editablity", "jsystem.verifyParameterIndexAndEditability",
				v);
		if (!"true".equals(res)) {
			throw new Exception(res);
		}

	}

	public void setParameterTableSize(int testIndex, String tab, double[] sizes) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(testIndex);
		v.addElement(tab);
		String sizesAsString = JSystemServer.sizesToString(sizes);
		v.addElement(sizesAsString);
		handleXmlCommand("Set parameters table size", "jsystem.setParameterTableSize", v);
	}

	public double[] getParameterTableSize(int testIndex, String tab) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(testIndex);
		v.addElement(tab);
		String res = (String) handleXmlCommand("Get parameters table size", "jsystem.getParameterTableSize", v);
		return JSystemServer.sizeToDoubleArray(res);
	}

	public String sortParametersTable(int testIndex, String tab, int headerIndex) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(testIndex);
		v.addElement(tab);
		v.addElement(headerIndex);
		String res = (String) handleXmlCommand("Simulates sort operation", "jsystem.sortParametersTable", v);
		return res;
	}

	public String setFileChooserParameter(int testIndex, String tab, int paramIndex, String path) throws Exception {
		String res = (String) handleXmlCommand("Activate file parameter", "jsystem.setFileChooserParameter", testIndex,
				tab, paramIndex, path);
		if (!"okay".equals(res)) {
			throw new Exception("Filed setting file parameter. " + res);
		}
		return res;
	}

	public void selectSut(String name) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(name);
		handleXmlCommand("Select sut " + name, "jsystem.selectSut", v);
	}

	public void openSutEditor(boolean expectError, boolean close) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(expectError);
		v.addElement(close);
		handleXmlCommand("Open sut editor", "jsystem.openSutEditor", v);
	}

	/**
	 * call the remote menu exiting of runner
	 * 
	 * @param exit
	 * @throws Exception
	 */
	public void exitThroughMenu(boolean exit) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(exit);
		handleXmlCommand("Exit through menu", "jsystem.exitThroughMenu", v);
		JSystemEnvControllerOld.setUseExistingServer(false);
	}

	public void waitForConfirmDialog(String title, int expected) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(title);
		v.addElement(expected);
		handleXmlCommand("wait for confirm dialog", "jsystem.waitForConfirmDialog", v);

	}

	/**
	 * takes the content of jar list from gui operation(Tools -> Show jar list)
	 * and return all it's content to caller as String
	 * 
	 * @return String representation of the jar list.
	 * @throws Exception
	 */
	public String openJarList() throws Exception {
		Vector<Object> v = new Vector<Object>();
		String res = (String) handleXmlCommand("openJarList", "jsystem.openJarList", v);
		return res;
	}

	public String sortJarList() throws Exception {
		Vector<Object> v = new Vector<Object>();
		String res = (String) handleXmlCommand("sortJarList", "jsystem.sortJarList", v);
		return res;
	}

	public String jarInList(String jarToFind) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(jarToFind);
		String res = (String) handleXmlCommand("jarInList", "jsystem.jarInList", v);
		return res;
	}

	/**
	 * 
	 * Handles key pressing. use Keys Enum from
	 * jsystem.treeui.teststable.ScenarioTreeKsyHandle;
	 * 
	 * @param key
	 * @throws Exception
	 */
	public void pressKey(Keys key) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(key.keyCode());
		handleXmlCommand("pressButton", "jsystem.pressKey", v);

	}

	/**
	 * Get the mapped tests in current scenario .
	 * 
	 * @return String. Example : int array {1,2,3} will be returned as String
	 *         "1,2,3"
	 * @throws Exception
	 */
	public String getMapedTestsInCurrentScenario() throws Exception {
		Vector<Object> v = new Vector<Object>();
		String res = (String) handleXmlCommand("GettingMappedTests", "jsystem.getMappedTestsInScenario", v);
		report.report("Index of mapped tests in scenario :" + res);
		return res;
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
		return (Integer) handleXmlCommand("jsystem.getNumberOfTests", "jsystem.getNumberOfTests", index, rootOnly,
				markedOnly);
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
		return (Boolean) handleXmlCommand("jsystem.checkTestMappedUnmapped", "jsystem.checkTestMappedUnmapped", index);
	}

	/**
	 * Get the mapped tests in current scenario .
	 * 
	 * @return int.
	 * @throws Exception
	 */
	public int getMapedTestsInCurrentScenario(String scenarioName) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(scenarioName);
		int res = (Integer) handleXmlCommand("GettingMappedTests", "jsystem.getMappedTestsInScenario", v);
		report.report("Index of mapped tests in scenario :" + res);
		return res;
	}

	/**
	 * Get the number of test in the current scenario. This method is a lot more
	 * accurate then the numOfTestsInScenario that parses the XML file because
	 * it accesses the Actual Scenario model and not the file.
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getNumOfTestsInScenario(String scenarioName) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(SCENARIOS_PREFIX + scenarioName);
		Integer res = (Integer) handleXmlCommand("GettingMappedTests", "jsystem.getNumOfTestsInScenario", v);
		report.report("Number of tests in scenario :" + res);
		return res;

	}

	/**
	 * Get the number of test in the current scenario. This method is a lot more
	 * accurate then the numOfTestsInScenario that parses the XML file because
	 * it accesses the Actual Scenario model and not the file.
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getNumOfTestsInScenario() throws Exception {
		Vector<Object> v = new Vector<Object>();
		Integer res = (Integer) handleXmlCommand("GettingMappedTests", "jsystem.getNumOfTestsInScenario", v);
		report.report("Number of tests in scenario :" + res);
		return res;

	}

	public int getNumOfchkBoxChekced() throws Exception {
		int i = (Integer) handleXmlCommand("get Num Of checked Check boxes", "jsystem.getNumOfchkBoxChekced");
		setTestAgainstObject(i);
		return i;
	}

	public void checkTestInTestsTree(String method, String className, boolean equals) throws Exception {
		handleXmlCommand("check the tests in tests tree", "jsystem.checkTestInTestsTree", method, className, true, equals);

	}

	public int moveCheckedToScenarioTree() throws Exception {
		int i = (Integer) handleXmlCommand("move tests to scenario tree", "jsystem.moveCheckedToScenarioTree", 1);
		setTestAgainstObject(i);
		return i;
	}

	// JSystemDialog commands
	// *********************************************************************************************************

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
	public Properties getDialogValues(FrameworkOptions... frameworkOptions) throws Exception {
		Vector<String> vector = convertFrameworkOptionsArrayToTabAndPropNameVector(frameworkOptions);
		String result = (String) handleXmlCommand("Get Dialog Values", "jsystem.getDialogValues", vector);
		return convertLongStringToProperty(result);
	}

	/**
	 * This method receive Key + value parameters, and change the dialog value
	 * 
	 * @param key
	 *            - the key represent the property to be change
	 * @param value
	 *            - the value to be enter into the dialog
	 * @throws Exception
	 */
	public int setJsystemPropertyThroughDialog(String key, String value) throws Exception {
		return setJsystemPropertyThroughDialog(key, value, JsystemPropertiesDialogButton.SAVE);
	}

	/**
	 * This method receive Key + value parameters, and change the dialog value
	 * 
	 * @param key
	 *            - the key represent the property to be change
	 * @param value
	 *            - the value to be enter into the dialog
	 * @param buttonToPress
	 *            - the name of the button to press after changing the dialog
	 *            value
	 * @throws Exception
	 */
	public int setJsystemPropertyThroughDialog(String key, String value, JsystemPropertiesDialogButton buttonToPress)
			throws Exception {
		Properties properties = new Properties();
		properties.put(key, value);
		return setJsystemPropertyThroughDialog(properties, buttonToPress, true);
	}

	/**
	 * This method receive a Property parameter (Key + value), convert it to a
	 * vector containing the following information: - Groups of Strings: - Tab
	 * name - Property name - Property new value The method run over all
	 * properties, change them to the given value, and press "Save" button
	 * 
	 * @param vector
	 *            - A vector of Strings as describe above
	 * @return -1 if an error occurred, 0 - otherwise.
	 * @throws Exception
	 */
	public int setJsystemPropertyThroughDialog(Properties properties) throws Exception {
		return setJsystemPropertyThroughDialog(properties, JsystemPropertiesDialogButton.SAVE, true);
	}

	public int setJsystemPropertyThroughDialog(Properties properties, JsystemPropertiesDialogButton buttonToPress)
			throws Exception {
		return setJsystemPropertyThroughDialog(properties, buttonToPress, true);
	}

	/**
	 * This method receive a Property parameter (Key + value), convert it to a
	 * vector containing the following information: - Groups of Strings: - Tab
	 * name - Property name - Property new value - This method also receive the
	 * name of the button to press after all properties have been changed. The
	 * method run over all properties, change them to the given value, and press
	 * the given button( Save / Restore Defaults / cancel )
	 * 
	 * @param vector
	 *            - A vector of Strings as describe above
	 * @return -1 if an error occurred, 0 - otherwise.
	 * @throws Exception
	 */
	public int setJsystemPropertyThroughDialog(Properties properties, JsystemPropertiesDialogButton buttonToPress,
			boolean restartRunnerIfRequire) throws Exception {
		report.report("Entering Client.setDialogValues()");
		Vector<String> vector = convertPropertiesToStringVector(properties);
		try {
			return (Integer) handleXmlCommand("Set Dialog Values", "jsystem.setDialogValues", vector,
					buttonToPress.toString(), restartRunnerIfRequire);
		} catch (ConnectException e) {
			if (!buttonToPress.equals(JsystemPropertiesDialogButton.RESTORE_DEFAULTS)) {
				if ((buttonToPress.equals(JsystemPropertiesDialogButton.SAVE) && restartRunnerIfRequire) == false) {
					throw e;
				}
			}

			System.out.println(e.getMessage());
			this.setMaskExit(true);
			return 1;
		}
	}

	/**
	 * converting pairs of FrameworkOption + value, to a groups of: Tab name +
	 * property name + [value].
	 * 
	 * @param inputVector
	 *            - The vector with the frameworkOption object
	 * @param valueIncluded
	 *            - A parameter indicate if we need to add the value as well (in
	 *            case we arrive to this method from getDialogValues(), the
	 *            value is not relevant)
	 * @return - Vector<String> that contains groups of: Tab name + property
	 *         name + [value]
	 */
	private Vector<String> convertPropertiesToStringVector(Properties properties) {
		String tabName, name, value;
		FrameworkOptions frameworkOption;
		GUIFrameworkOptions guiFrameworkOption;
		Vector<String> resultVector = new Vector<String>();
		Enumeration<?> names = properties.propertyNames();

		while (names.hasMoreElements()) {
			name = (String) names.nextElement();

			frameworkOption = FrameworkOptions.getFrameworkOptionKeyByStringName(name);
			guiFrameworkOption = GUIFrameworkOptions.valueOf(frameworkOption.name());

			// add the tab name to the vector
			tabName = guiFrameworkOption.getGroup().getValue();
			resultVector.add(tabName);

			// add the property name to the vector
			resultVector.add(name);

			// add value to the vector
			value = properties.getProperty(name);
			resultVector.add(value);
		}

		return resultVector;
	}

	/**
	 * This method convert an array from type FrameworkOptions to a String type
	 * vector that contain pairs of string (tab name + property name) separated
	 * with '#'
	 * 
	 * @param frameworkOptions
	 *            - An array from type FrameworkOptions
	 * @return a String type vector that contain pairs of string (tab name +
	 *         property name)
	 */
	private Vector<String> convertFrameworkOptionsArrayToTabAndPropNameVector(FrameworkOptions[] frameworkOptions) {
		Vector<String> resultVector = new Vector<String>();
		FrameworkOptions frameworkOption;
		GUIFrameworkOptions guiFrameworkOption;
		String propertyName, tabName;
		for (int i = 0; i < frameworkOptions.length; i++) {
			// Add the tab name to the vector
			frameworkOption = frameworkOptions[i];
			guiFrameworkOption = GUIFrameworkOptions.findGuiFrameworkOption(frameworkOption);
			tabName = guiFrameworkOption.getGroup().getValue();
			resultVector.add(tabName);

			// Add the property name to the vector
			propertyName = frameworkOption.toString();
			resultVector.add(propertyName);
		}
		return resultVector;
	}

	/**
	 * Since the Server can return only primitive type, the client receive from
	 * the server a long string containing pairs of property names + value, for
	 * all requiered properties, and convert it to a vector that contain pairs
	 * of: FrameworkOption + value.
	 * 
	 * @param allProperties
	 *            - A string with all properties names and values seperated by
	 *            '#'
	 * @return
	 */
	private Properties convertLongStringToProperty(String allProperties) {
		String[] propertiesList = allProperties.split("#");
		int propertyCounter = propertiesList.length / 2;
		Properties properties = new Properties();
		String key, value;

		for (int i = 0; i < propertyCounter; i++) {
			key = propertiesList[i * 2];
			value = propertiesList[i * 2 + 1];
			properties.put(key, value);
		}
		return properties;
	}

	/**
	 * Restore System Default values into the dialog by pressing the
	 * "Restore Defaults" button
	 */
	public void restoreSystemDefault() throws Exception {
		setJsystemPropertyThroughDialog(new Properties(), JsystemPropertiesDialogButton.RESTORE_DEFAULTS);
		this.setMaskExit(true);
	}
}