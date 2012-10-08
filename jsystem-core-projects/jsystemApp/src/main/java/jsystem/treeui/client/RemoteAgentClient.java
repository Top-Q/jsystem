/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.client;

import java.io.File;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.sut.SutFactory;
import jsystem.runner.agent.ProjectComponent;
import jsystem.runner.agent.clients.JSystemAgentClient;
import jsystem.runner.agent.server.RunnerEngine;
import jsystem.runner.agent.server.RunnerEngineImpl;
import jsystem.runner.projectsync.ProjectZip;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.actionItems.SaveScenarioAction;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.ProgressNotifier;
import jsystem.utils.StringUtils;

/**
 * Extension of the {@link JSystemAgentClient} class which which makes needed adjustments to manage and control a remote agent using the
 * runner.<br>
 * 
 * @author goland
 */
public class RemoteAgentClient extends JSystemAgentClient {
	private static Logger log = Logger.getLogger(RemoteAgentClient.class.getName());

	/**
	 * agent synchronization enum
	 */
	public enum SyncOptions {
		yes, no, compare, askUser
	}

	/**
	 * Instance of {@link RunnerEngineImpl} for operations that are common to both scenario studio and runner engine.
	 */
	private RunnerEngineImpl runnerEngine;

	public RemoteAgentClient(String url) throws Exception {
		super(url);
		runnerEngine = new RunnerEngineImpl();
	}

	@Override
	public void run() throws Exception {
		run(SyncOptions.askUser);
	}

	/**
	 * Runs selected scenario with selected sut of the automation project which is currently active in runner client application.<br>
	 * 
	 * Performs the following functionality:<br>
	 * 1. Signals remote agent to change active automation project to the active automation project in runner client application.<br>
	 * 2. Compares the md5 of the various automation project components of the local automation project and the remote automation project.<br>
	 * 3. Packs in a zip file the local project components which are not identical to corresponding components in remote agent. 4. Transfers
	 * zip file to agent and signals the agent to extract the zip.<br>
	 * 5. Sets active sut to currently selected sut in runner client application.<br>
	 * 6. Sets active scenario to currently selected scenario in runner client application.<br>
	 * 7. Signals agent to run scenario.
	 */
	public void run(SyncOptions sync) throws Exception {
		WaitDialog.launchWaitDialog("Initializing run", null);
		try {
			int res = 0;
			if (SyncOptions.askUser.equals(sync)) {
				res = JOptionPane.showOptionDialog(null, "Press yes to copy local project to agent.", "Project synchronization",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
						ImageCenter.getInstance().getImage(ImageCenter.ICON_INFO), new String[] { "Yes", "No" }, "No");
			} else if (SyncOptions.no.equals(sync)) {
				res = 1;
			}
			syncAgentsWithLocalProject(new JSystemAgentClient[] { this }, res == 0 ? true : false, new RemoteAgentClientNotifier(), true);
		} finally {
			WaitDialog.endWaitDialog();
		}
		// run
		getMbeanProxy().run();
		report.report("Agent was signaled to run");
	}

	@Override
	public void initReporters() {
		super.initReporters();
		runnerEngine.initReporters();
		RunnerEngine[] engines = JSystemAgentClientsPool.getClients(null);
		for (RunnerEngine engine : engines) {
			if (engine.getConnectionState().equals(ConnectionState.connected)) {
				engine.initReporters();
			} else {
				log.fine(engine.getId() + " is disconnected. skipping activation");
			}
		}

	}

	/**
	 * Sets active scenario in remote agent.
	 * 
	 * @see RunnerEngine#setActiveScenario(Scenario)
	 */
	@Override
	public void setActiveScenario(Scenario scenario) throws Exception {
		runnerEngine.setActiveScenario(scenario);
	}

	/**
	 * Changes project only on local project.<br>
	 * Remote project is changes when playing the scenario.
	 */
	@Override
	public void changeProject(String projectName) throws Exception {
		runnerEngine.changeProject(projectName);
	}

	@Override
	public void changeSut(String sutFile) throws Exception {
		runnerEngine.changeSut(sutFile);
		super.changeSut(sutFile);
	}

	/**
	 * performs refresh operation on local environment and signals agent to refresh engine environment.
	 * 
	 * @see RunnerEngine#refresh()
	 */
	@Override
	public void refresh() throws Exception {
		runnerEngine.refresh();
		super.refresh();
	}

	/**
	 * 
	 */
	public static void syncAgentsWithLocalProject(JSystemAgentClient[] clients, boolean loadProjectZip, ProgressNotifier notifier,
			boolean haltOnError) throws Exception {
		int progress = 1;

		if (notifier == null) {
			return;
		}

		if (ScenariosManager.isDirty()) {
			notifier.notifyProgress("Saving scenario ", progress += 3);
			SaveScenarioAction.getInstance().saveCurrentScenario();
		}
		String projectClasses = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER);
		String currentScenario = ScenariosManager.getInstance().getCurrentScenario().getName();
		String sutFile = SutFactory.getInstance().getSutInstance().getSetupName();
		int[] selectedTests = ScenariosManager.getInstance().getCurrentScenario().getEnabledTestsIndexes();
		File zippedProject = null;
		if (loadProjectZip) {
			notifier.notifyProgress("Zipping local project ", progress += 3);
			ProjectZip zipper = new ProjectZip(new File(projectClasses));
			zippedProject = zipper.zipProject(ProjectComponent.values());
			notifier.notifyProgress("Finished Zipping local project ", progress += 3);
		}
		notifier.notifyProgress("Starting synchronization ", progress += 3);
		for (JSystemAgentClient client : clients) {
			if (client != null) {
				if (client.getConnectionState() == ConnectionState.connected) {
					try {
						client.setNotifier(notifier);
						client.synchronizeProject(zippedProject, ProjectZip.getProjectNameFromClassesPath(new File(projectClasses)),
								currentScenario, sutFile, selectedTests, JSystemProperties.getInstance().getPreferences());

					} catch (Throwable t) {
						notifier.notifyProgress("Failed synchronizing " + client.getId(), progress);
						notifier.notifyProgress(StringUtils.getStackTrace(t), progress);
						if (haltOnError) {
							throw new Exception(t);
						}
					}
					notifier.notifyProgress("Ended synchronizing " + client.getId(), progress += 3);
				} else {
					notifier.notifyProgress("no connection to  " + client.getId() + " skipping synchronization", progress += 3);
				}
			}
		}
	}

	public static void syncAgentsWithLocalProject(JSystemAgentClient[] clients, boolean loadProjectZip, boolean haltOnError)
			throws Exception {
		
		Reporter report = ListenerstManager.getInstance();

		if (ScenariosManager.isDirty()) {
			report.report("Saving scenario ");
			SaveScenarioAction.getInstance().saveCurrentScenario();
		}
		String projectClasses = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER);
		String currentScenario = ScenariosManager.getInstance().getCurrentScenario().getName();
		String sutFile = SutFactory.getInstance().getSutInstance().getSetupName();
		int[] selectedTests = ScenariosManager.getInstance().getCurrentScenario().getEnabledTestsIndexes();
		File zippedProject = null;
		if (loadProjectZip) {
			report.report("Zipping local project ");
			ProjectZip zipper = new ProjectZip(new File(projectClasses));
			zippedProject = zipper.zipProject(ProjectComponent.values());
			report.report("Finished Zipping local project ");
		}
		report.report("Starting synchronization ");
		for (JSystemAgentClient client : clients) {
			if (client != null) {
				if (client.getConnectionState() == ConnectionState.connected) {
					try {
						client.synchronizeProject(zippedProject, ProjectZip.getProjectNameFromClassesPath(new File(projectClasses)),
								currentScenario, sutFile, selectedTests, JSystemProperties.getInstance().getPreferences());

					} catch (Throwable t) {
						report.report("Failed synchronizing " + client.getId());
						report.report(StringUtils.getStackTrace(t));
						if (haltOnError) {
							throw new Exception(t);
						}
					}
					report.report("Ended synchronizing " + client.getId());
				} else {
					report.report("no connection to  " + client.getId() + " skipping synchronization");
				}
			}
		}
	}

	class RemoteAgentClientNotifier implements ProgressNotifier {
		@Override
		public void done() {
		}

		@Override
		public void notifyProgress(String message, int progress) {
			report.report(message);
		}
	}
}
