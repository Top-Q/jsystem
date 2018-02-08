/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.GeneralEnums.CmdExecutor;
import jsystem.framework.JSystemProperties;
import jsystem.framework.TestRunnerFrame;
import jsystem.framework.common.CommonResources;
import jsystem.framework.fixture.FixtureManager;
import jsystem.framework.launcher.StartRunner;
import jsystem.framework.report.JSystemListeners;
import jsystem.framework.report.RunnerListenersManager;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.runner.agent.server.RunnerEngine;
import jsystem.runner.agent.server.RunnerEngineImpl;
import jsystem.treeui.client.JSystemAgentClientsPool;
import jsystem.treeui.client.RunnerEngineManager;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.suteditor.planner.SystemObjectBrowserUtils;
import jsystem.treeui.threads.AutoSaveThread;
import jsystem.treeui.utilities.ApplicationUtilities;
import jsystem.upgrade.UpgradeManager;
import jsystem.utils.ClassSearchUtil;
import jsystem.utils.FileLock;
import jsystem.utils.FileUtils;
import jsystem.utils.PerformanceUtil;
import jsystem.utils.StringUtils;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.runner.BaseTestRunner;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TestRunner This class implements the main entry This class holds the tree
 * view and receives updates from the view
 */
public class TestRunner extends BaseTestRunner implements StartRunner {

	/**
	 * Events that can be passed to the main controller of the view
	 */
	public static final int RUN_EVENT = 0;

	public static final int STOP_EVENT = 1;

	public static final int PAUSE_EVENT = 2;

	public static final int REFRESH_EVENT = 3;

	public static final int REPEAT_EVENT = 5;

	public static final int CONNECT_TO_AGENT = 6;

	public static final int CONTINUE_EVENT = 15;

	private static Logger log = Logger.getLogger(TestRunner.class.getName());

	private static TestRunner runner;

	public static TestTreeView treeView;

	public void startRunner(String[] args) {

		TestRunnerFrame.guiMainFrame = null;

		JSystemProperties.getInstance();
		processSplashScreen();

		log.fine("Tree TestRunner is starting");

		try {
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error setting UI Look and Feel");
		}

		JSystemProperties.getInstance().setJsystemRunner(true);

		// Let the user select tests dir
		// if not selected exit runner.
		try {
			String path = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER);
			if ((path = ApplicationUtilities.chooseClassesDirectory(path, true)) == null) {
				System.exit(1);
			} else {
				RunnerEngineImpl.setTestsPath(path);
			}
			JSystemProperties.getCurrentTestsPath();
		} catch (Throwable e) {
			// we are in the runner and a project was not selected.
			// exiting the runner
			System.exit(1);
		}

		try {
			GuiResourcesManager.getInstance().init();
		} catch (Throwable e1) {
			log.log(Level.SEVERE, "Fail to init GUI resource file", e1);
		}

		UIManager.put("Button.background", new Color(0xf6, 0xf6, 0xf6));
		UIManager.put("Frame.background", new Color(0xf6, 0xf6, 0xf6));
		UIManager.put("Label.background", new Color(0xf6, 0xf6, 0xf6));
		UIManager.put("Panel.background", new Color(0xf6, 0xf6, 0xf6));
		UIManager.put("OptionPane.background", new Color(0xf6, 0xf6, 0xf6));
		UIManager.put("ScrollPane.background", new Color(0xf6, 0xf6, 0xf6));
		UIManager.put("Table.background", new Color(0xf6, 0xf6, 0xf6));

		try {
			RunnerEngineManager.initRunnerEngine(RunnerEngineManager.LOCAL_AGNET);
			updateLock();
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			JSystemAgentClientsPool.initPoolFromRepositoryFile();
		} catch (Throwable e) {
			log.log(Level.SEVERE, "Fail to init agents pool", e);
		}

		try {
			UpgradeManager.upgrade(true);
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to upgrading old scenarios", e);
		}

		try {
			// shutting down scenario change events
			// while runner is loading
			ScenariosManager.setDirtyStateEventsSilent(true);
			treeView = new TestTreeView(this);

			TestRunnerFrame.guiMainFrame = treeView;
			treeView.init();
		} catch (Throwable e) {
			log.log(Level.SEVERE, "Failed initiating main view", e);
			System.exit(1);
		} finally {
			// turning on events
			ScenariosManager.setDirtyStateEventsSilent(false);
		}

		treeView.configureView(TestTreeView.VIEW_IDLE);

		JSystemListeners lm = RunnerListenersManager.getInstance();
		FixtureManager.getInstance().addListener(lm);

		// if we have command line parameters this means that we are running
		// using a command
		// line executor engine. See what is the type of the executor.
		if (args != null && args.length > 0) {
			String executor = JSystemProperties.getInstance().getPreference(FrameworkOptions.CMD_LINE_EXECUTER);
			if (CmdExecutor.SIMPLE_EXECUTOR.toString().equalsIgnoreCase(executor)) {
				RunnerCmdExecutor runCmd = new RunnerCmdExecutor(args);
				runCmd.init();
			} else {
				RunnerAdvancedCmdExecuter runCmd = new RunnerAdvancedCmdExecuter(args);
				runCmd.init();
			}
		}
		runnerStartAdditionalOperations();
	}

	private void runnerStartAdditionalOperations() {
		try {
			AutoSaveThread.getInstance().startThread();
		} catch (Exception e) {
			log.log(Level.SEVERE, "ERROR getting instance of AutoSaveThread manager.");
			log.log(Level.SEVERE, StringUtils.getStackTrace(e));
		}

		// Extract JSystem script file for "if" condition from the jsystemAnt
		// jar
		String destination = null;
		try { // Extract the script file from the jsystemAnt jar file
			String userDir = System.getProperty("user.dir");
			String runnerLib;
			if (userDir.contains("jsystemApp")) {
				runnerLib = System.getenv("RUNNER_ROOT") + File.separator + "lib" + File.separator;
			} else {
				runnerLib = System.getProperty("user.dir") + File.separator + "lib" + File.separator;
			}
			String antJarFile = runnerLib + "jsystemAnt.jar";
			// destination = runnerLib + "scripts"+File.separator;
			// APPLIED MATERIALS support for if. - should make no change to the
			// default behaviour
			destination = System.getProperty("user.dir") + File.separator + "scripts" + File.separator;

			new File(destination).mkdirs();
			FileUtils.extractOneZipFile("ifScriptCondition.js", new File(antJarFile), new File(destination));
		} catch (IOException e) {
			log.log(Level.WARNING, "Fail to locate script file for if execution: " + destination);
		}
	}

	public void exit() {
		try {
			RunnerEngineManager.getRunnerEngine().close();
		} catch (Exception e) {
			log.warning("Failed disconnecting on exit " + e.getMessage());
		}
		if (RunnerListenersManager.hadFailure) {
			log.info("System exit 101");
			System.exit(101);
		} else if (RunnerListenersManager.hadWarning) {
			log.info("System exit 102");
			System.exit(102);
		} else {
			log.info("System exit 0");
			System.exit(0);
		}
	}

	/**
	 * This method will be called from the view to update the controller of view
	 * events
	 * 
	 * @param event
	 *            - evnts triggered on the view
	 * @param data
	 *            - data (if any)
	 */
	public void handleEvent(int event, Object data) {
		switch (event) {
		case RUN_EVENT:
			treeView.configureView(TestTreeView.VIEW_WAIT_FOR_PAUSE);
			treeView.getTableController().expandAll();
			runSuite();
			treeView.configureView(TestTreeView.VIEW_RUNNING);
			break;
		case CONTINUE_EVENT:
			treeView.configureView(TestTreeView.VIEW_RUNNING);
			try {
				RunnerEngineManager.getRunnerEngine().resume();
			} catch (Exception e) {
				ErrorPanel.showErrorDialog("Failed resuming execution", e, ErrorLevel.Error);
			}
			break;
		case STOP_EVENT:
			treeView.setRepeat(false);
			try {
				RunnerEngine engine = RunnerEngineManager.getRunnerEngine();
				GracefulStopListener cancel = new GracefulStopListener(engine);
				WaitDialog.launchWaitDialog(JsystemMapping.getInstance().getGracefulDialog(), cancel, JsystemMapping
						.getInstance().getStopImmediatelyButton());
				engine.gracefulStop();
			} catch (Exception e) {
				ErrorPanel.showErrorDialog("Failed stopping execution", e, ErrorLevel.Error);
			}
			break;
		case PAUSE_EVENT:
			treeView.configureView(TestTreeView.VIEW_WAIT_FOR_PAUSE);
			try {
				RunnerEngineManager.getRunnerEngine().pause();
			} catch (Exception e) {
				ErrorPanel.showErrorDialog("Failed pausing execution", e, ErrorLevel.Error);
			}
			break;
		case REFRESH_EVENT:
			handleRefresh();
			break;
		case REPEAT_EVENT:
			try {
				RunnerEngineManager.getRunnerEngine().enableRepeat((Boolean) data);
			} catch (Exception e) {
				ErrorPanel.showErrorDialog("Failed pausing execution", e, ErrorLevel.Error);
			}
			break;
		case CONNECT_TO_AGENT:
			try {
				FileLock lock = FileLock.getFileLock(CommonResources.LOCK_FILE);
				lock.releaseLock();
				RunnerEngineManager.initRunnerEngine("" + data);
				RemoteAgentUIComponents.refreshAgentList();
				treeView.registerOnAgentEvents();
			} catch (Exception e) {
				ErrorPanel.showErrorDialog("Failed connecting to " + data, e, ErrorLevel.Error);
			} finally {
				try {
					updateLock();
				} catch (Exception e) {
					log.warning("Failed updating file lock. " + e.getMessage());
				}
			}
			break;
		}
	}

	private void runSuite() {

		JSystemProperties.getInstance().rereadPropertiesFile();

		Scenario scenario = null;
		try {
			scenario = ScenariosManager.getInstance().getCurrentScenario();
			scenario.setStatusNotRunning();
		} catch (Exception e) {
			treeView.setRepeat(false);
			treeView.configureView(TestTreeView.VIEW_IDLE);
			JOptionPane.showMessageDialog(treeView, "Fail to load tests:\n" + StringUtils.getStackTrace(e),
					"Fail to load tests", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (scenario.getTests().size() == 0) {
			treeView.setRepeat(false);
			treeView.configureView(TestTreeView.VIEW_IDLE);
			JOptionPane.showMessageDialog(treeView, "Please add one or more tests to the scenario.",
					"No tests selected", JOptionPane.INFORMATION_MESSAGE);
		}

		int numberOfCycles = treeView.getNumberOfCycles();
		if (numberOfCycles < 1) {
			treeView.setNumberOfCycles(0);
		}
		treeView.setNumberOfLeftCycles(0);
		if (treeView.isRepeat()) {
			treeView.setNumberOfLeftCycles(treeView.getNumberOfCycles());
		}

		ExecutionWorker runWorker = new ExecutionWorker(treeView);
		runWorker.execute();
	}

	/**
	 * This method handles the refresh event on the controller side. Implement
	 * the non UI operations that will be done on refresh
	 */
	private void handleRefresh() {
		try {
			RunnerEngineManager.getRunnerEngine().refresh();
		} catch (Exception e) {
			ErrorPanel.showErrorDialog("Failed refreshing engine", e, ErrorLevel.Error);
		}
		int index = PerformanceUtil.startMeasure();
		treeView.refreshInternals();
		TestTreeView.treeController.refreshView();
		TestTreeView.treeController.expandTree();
		treeView.refreshOpenReportsButton();
		treeView.tableController.refresh();
		PerformanceUtil.endMeasure(index, "Refreshing scenario");
		SystemObjectBrowserUtils.startCollectSOs();
	}

	/**
	 * 
	 */
	private void updateLock() throws Exception {
		FileLock lock = FileLock.getFileLock(CommonResources.LOCK_FILE);
		if (RunnerEngineManager.getRunnerEngine() instanceof RunnerEngineImpl) {
			while (true) {
				if (!lock.grabLock()) {
					int res = JOptionPane.showOptionDialog(null,
							"Another JRunner instance is using the same directory\n"
									+ "Running two JRunners on the same directory will cause logs problems\n"
									+ "Close the other JRunner instance and press on 'Check Again' or 'exit'.",
							"A JRunner instance is already active", JOptionPane.YES_NO_OPTION,
							JOptionPane.INFORMATION_MESSAGE, ImageCenter.getInstance().getImage(ImageCenter.ICON_INFO),
							new String[] { "Check Again", "Exit" }, "Check Again");
					if (res == 1) {
						exit();
					}
				} else {
					return;
				}
			}
		} else {
			lock.releaseLock();
		}
	}

	/**
	 * getting the SplashScreen object in runtime, and creating a new thread
	 * that will take the splash screen and write on it the required
	 * information. the splash will be closed when the application is up.
	 */
	private void processSplashScreen() {
		(new Thread() {
			public void run() {

				SplashScreen ss = SplashScreen.getSplashScreen();

				if (ss != null) {
					try {
						Graphics2D g2d = ss.createGraphics();

						// Set text color black and font type sansserif

						// Set string location just above the separator line
						g2d.setColor(new Color(0x23, 0x1f, 0x20));
						g2d.setFont(new Font("arial", Font.BOLD, 14));
						String version = "Version ";
						version += ClassSearchUtil.getPropertyFromClassPath(
								"META-INF/maven/org.jsystemtest/jsystemApp/pom.properties", "version");
						g2d.drawString(version, 101, 209);
						ss.update();

						// Set wait message
						g2d.setColor(new Color(0x50, 0x93, 0xca));
						g2d.setFont(new Font("arial", Font.BOLD, 14));
						g2d.drawString("JSystem initializing, please wait...", 101, 230);
						ss.update();

						// Set copyright message
						g2d.setColor(new Color(0x23, 0x1f, 0x20));
						g2d.setFont(new Font("arial", Font.PLAIN, 10));
						g2d.drawString(" Copyright 2005-2018 Ignis Software Tools Ltd. All rights reserved.", 101, 330);
						ss.update();

						for (int j = 0; true; j++) {
							if (j % 2 == 0) {
								g2d.setColor(new Color(0xf6, 0xf6, 0xf6));
							} else {
								g2d.setColor(new Color(0xa8, 0xb7, 0xe2));
							}
							/**
							 * create the runing square's
							 */
							for (int i = 0; i < 10; i++) {
								g2d.fillRect(101 + (i * 20), 264, 10, 10);
								ss.update();
								Thread.sleep(200);
							}
						}
					} catch (Exception e) {
						log.log(Level.FINE, "Splash Screen Closed");
					}
				} else {
					log.log(Level.WARNING, "Splash screen not found");

				}

			}
		}).start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			return;
		}

	}

	protected TestResult createTestResult() {
		return new TestResult();
	}

	public void testStarted(String s) {
		// ignore
	}

	public void testEnded(String s) {
		// ignore
	}

	public void testFailed(int i, Test test, Throwable throwable) {
		// ignore
	}

	protected void runFailed(String s) {
		// handeled by the runner
	}

	public static void main(String[] args) {
		runner = new TestRunner();
		runner.startRunner(args);
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		TestRunner.log = log;
	}
}