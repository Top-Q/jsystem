/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.report.ExecutionListener;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.actionItems.PlayAction;
import jsystem.treeui.actionItems.SutComboAction;
import jsystem.treeui.actionItems.SwitchProjectAction;
import jsystem.utils.FileUtils;
import jsystem.utils.XmlUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.apache.tools.ant.util.ReaderInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Michael Oziransky
 */
public class RunnerAdvancedCmdExecuter implements ExecutionListener {

	private static Logger log = Logger.getLogger(RunnerAdvancedCmdExecuter.class.getName());
	private final String COMMENT = "#";
	
	private File[] argsFile;
	private boolean proceed;
	private boolean failureFound;
	
	public RunnerAdvancedCmdExecuter(String[] args) {
		// Check whether this is a file that contains a list of 
		// other XML files to run, or this is just a list of files
		if (args.length == 1 && args[0].endsWith(".scenarios")) {
			try {
				String[] namesList = FileUtils.read(args[0]).split("\n");				
				argsFile = new File[namesList.length];
				for (int i = 0; i < namesList.length; i++) {
					if (!namesList[i].startsWith(COMMENT)) {
						argsFile[i] = new File(namesList[i]);
					}
				}
			} catch (IOException e) {
				log.log(Level.SEVERE, "Fail reading scenarios list file", e);
			}			
		} else {
			argsFile = new File[args.length];
			for (int i = 0; i < args.length; i++) {
				argsFile[i] = new File(args[i]);
			}
		}
		
		proceed = false;
		failureFound = false;
	}
	
	public RunnerAdvancedCmdExecuter(String fileName) {
		argsFile = new File[1];
		argsFile[0] = new File(fileName);
		
		proceed = false;
		failureFound = false;
	}
	
	public RunnerAdvancedCmdExecuter() {
		argsFile = new File[1];
		argsFile[0] = new File(System.getProperty("user.dir"), 
				CommonResources.JSYSTEM_COMMAND_LINE_FILE_NAME);
		
		proceed = false;
		failureFound = false;
	}
	
	public void init() {
		ListenerstManager.getInstance().addListener(this);
		
		String currentProjectPath = null;
		String currentSutFile = null;
		String currentScenarioFile = null;
		
		boolean haltExecution = false;
		
		for (File currentFile : argsFile) {
			if (haltExecution) {
				break;
			}
			
			try {
				ArrayList<RunnerCmd> runCommands = new ArrayList<RunnerCmd>();
				
				// Parse the input file
				Document doc = XmlUtils.getDocumentBuilder().parse(
						new ReaderInputStream(
								new FileReader(currentFile)));
				
				// Get general attributes
				boolean haltOnStop = Boolean.parseBoolean(doc.getDocumentElement().getAttribute("stop"));
				boolean exitOnFinish = Boolean.parseBoolean(doc.getDocumentElement().getAttribute("exit"));
				
				ArrayList<Element> list = 
					XmlUtils.getElementsByTag("command", doc.getDocumentElement());
				
				for (Element e : list) {
					RunnerCmd cmd = new RunnerCmd();
					
					// Set command attributes
					cmd.setRepetition(Integer.parseInt(e.getAttribute("repetitions")));
					cmd.setDependOnPrevious(Boolean.parseBoolean(e.getAttribute("dependOnPrevious")));
					cmd.setSaveRunProperties(Boolean.parseBoolean(e.getAttribute("saveRunProperties")));
					cmd.setFreezeOnFail(Boolean.parseBoolean(e.getAttribute("freezeOnFail")));
					cmd.setStopSuiteExecution(Boolean.parseBoolean(e.getAttribute("stopSuiteExecution")));
					cmd.setStopEntireExecution(Boolean.parseBoolean(e.getAttribute("stopEntireExecution")));
					
					// Set main features
					Element projectPath = XmlUtils.getChildElementsByTag("projectPath", e).get(0);
					cmd.setProjectPath(projectPath.getTextContent());
					Element sutFile = XmlUtils.getChildElementsByTag("sutFile", e).get(0);
					cmd.setSutFile(sutFile.getTextContent());
					Element scenarioName = XmlUtils.getChildElementsByTag("scenarioName", e).get(0);
					cmd.setScenarioFile(scenarioName.getTextContent());
					
					// Add to the list of commands
					runCommands.add(cmd);
				}
				
				failureFound = false;
				
				// Go over all commands and start execution
				for (RunnerCmd cmd : runCommands) {
					// If this scenario depends on previous and there were errors, just skip it
					if (cmd.isDependOnPrevious() && failureFound) {
						failureFound = false;
						continue;
					}
					
					failureFound = false;
					
					// See if we have a different project root directory
					if (!cmd.getProjectPath().equals(currentProjectPath)) {
						currentProjectPath = cmd.getProjectPath();
						currentSutFile = cmd.getSutFile();
						currentScenarioFile = cmd.getScenarioFile();
						JSystemProperties.getInstance().setPreference(FrameworkOptions.USED_SUT_FILE, cmd.getSutName());
						SwitchProjectAction.getInstance().changeTestDir(currentProjectPath, cmd.getSutFullPath(), true);
						TestRunner.treeView.tableController.loadScenario(cmd.getScenarioName(), true);
					} else {
						// We are in the same project, let's see if we have a different SUT file
						if (!cmd.getSutFile().equals(currentSutFile)) {
							currentSutFile = cmd.getSutFile();
							currentScenarioFile = cmd.getScenarioFile();
							SutComboAction.getInstance().changeSut(cmd.getSutFullPath());
							TestRunner.treeView.tableController.loadScenario(cmd.getScenarioName(), true);
						} else {
							// We are in the same project with the same SUT file, let's see if we have a different scenario
							if (!cmd.getScenarioFile().equals(currentScenarioFile)) {
								currentScenarioFile = cmd.getScenarioFile();
								TestRunner.treeView.tableController.loadScenario(cmd.getScenarioName(), true);
							} else {
								// There no change from previous execution, leave everything as is
							}
						}
					}
									
					// See if we need to execute more than once
					int repetitions = cmd.getRepetition();
					if (repetitions > 1) {
						TestRunner.treeView.setRepeat(true);
						TestRunner.treeView.setNumberOfCycles(repetitions);
					}
					
					// See if we need to freeze on fail
					TestRunner.treeView.setFreezeOnFail(cmd.isFreezeOnFail());
					
					// See if we need to save run properties (unless this is the first command)
					if (runCommands.indexOf(cmd) != 0) {
						if (cmd.isSaveRunProperties()) {
							JSystemProperties.getInstance().setPreference(FrameworkOptions.SAVE_RUN_PROPERTIES, "true");
						}
					}
					
					// See if this is the last command, make sure that we exit if required
					if (exitOnFinish) {
						if (runCommands.indexOf(cmd) == runCommands.size()-1) {
							JSystemProperties.getInstance().setExitOnRunEnd(true);
						}
					}

					// Finally let's execute
					PlayAction.getInstance().actionPerformed(null);
					
					synchronized (this) {
						while(!proceed) {
							wait();
						}
					}
									
					proceed = false;		
					
					// See if this is the last command, make sure to restore run properties flag
					if (runCommands.indexOf(cmd) == runCommands.size()-1) {
						JSystemProperties.getInstance().setPreference(FrameworkOptions.SAVE_RUN_PROPERTIES, "false");
					}
					
					// Check if the user has stopped execution, in this case we might need to break the whole execution
					if (haltOnStop) {
						if (TestRunner.treeView.isStopped()) {
							// Make sure that we exit the whole XML list loop
							haltExecution = true;
							// Make sure that we exit current XML loop
							break;
						}
					}
					
					if (cmd.isStopEntireExecution() && failureFound) {
						// Make sure that we exit the whole XML list loop
						haltExecution = true;
						// Make sure that we exit current XML loop
						break;
					}
					
					// If this scenario is marked to stop in case there were errors, just break the loop
					if (cmd.isStopSuiteExecution() && failureFound) {
						failureFound = false;
						break;
					}
				} 				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	public synchronized void executionEnded(String scenarioName) {
		proceed = true;
		notifyAll();
	}

	public void addFailure(Test arg0, AssertionFailedError arg1) {
		failureFound = true;
	}

	public void addError(Test arg0, Throwable arg1) {
		failureFound = true;
	}

	public void errorOccured(String title, String message, ErrorLevel level) {
		failureFound = true;
	}

	public void remoteExit() {}
	
	public void remotePause() {}
	
	public void addWarning(Test test) {}

	public void endContainer(JTestContainer container) {}

	public void endLoop(AntForLoop loop, int count) {}

	public void endRun() {}

	public void startContainer(JTestContainer container) {}

	public void startLoop(AntForLoop loop, int count) {}

	public void startTest(TestInfo testInfo) {}

	public void endTest(Test arg0) {}

	public void startTest(Test arg0) {}
}
