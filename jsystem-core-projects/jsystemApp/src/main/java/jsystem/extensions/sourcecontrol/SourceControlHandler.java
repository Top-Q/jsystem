package jsystem.extensions.sourcecontrol;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jsystem.extensions.sourcecontrol.SourceControlI.Status;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioChangeType;
import jsystem.framework.scenario.ScenarioListener;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.sut.SutFactory;
import jsystem.framework.sut.SutListener;
import jsystem.treeui.TestRunner;
import jsystem.treeui.actionItems.IgnisAction;
import jsystem.treeui.actionItems.SaveScenarioAction;
import jsystem.treeui.actionItems.ScenarioSCStatusAction;
import jsystem.treeui.actionItems.SutSCStatusAction;
import jsystem.utils.FileUtils;

public class SourceControlHandler implements ScenarioListener, SutListener {

	private static Logger log = Logger.getLogger(SourceControlHandler.class
			.getName());

	private boolean enabled;

	IgnisAction scenarioStatusAction = null;

	IgnisAction sutStatusAction = null;

	private Status scenarioStatus = Status.NONE;

	private Status sutStatus = Status.NONE;

	private SourceControlI sourceControl;

	private String repository;

	private String user;

	private String password;

	/**
	 * Get the required parameters, connect and init working copy.
	 */
	public void init() {
		enabled = true;
		fetchParameters();
		if (!enabled) {
			JOptionPane
					.showConfirmDialog(
							TestRunner.treeView,
							"Please specify all required parameters in JSystem properties file",
							"Source Control Warning",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		instantiateSourceControl();
		connect();
		initWorkingCopy();
		if (!enabled) {
			JOptionPane.showConfirmDialog(TestRunner.treeView,
					"Failed to connect to repository",
					"Source Control Failure", JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		ListenerstManager.getInstance().addListener(this);
		scenarioStatusAction = ScenarioSCStatusAction.getInstance();
		updateScenarioStatus(ScenariosManager.getInstance()
				.getCurrentScenario());
		sutStatusAction = SutSCStatusAction.getInstance();
		updateSutStatus(SutFactory.getInstance().getSutFile().getName());
	}

	/**
	 * Get all the required parameters from the JSystem properties file.
	 */
	private void fetchParameters() {
		repository = JSystemProperties.getInstance().getPreference(
				FrameworkOptions.SCM_REPOSITORY);
		if (null == repository || repository.isEmpty()) {
			log.fine("No SVN repository was defined");
			enabled = false;
		}
		user = JSystemProperties.getInstance().getPreference(
				FrameworkOptions.SCM_USER);
		if (null == user) {
			user = "";
		}
		password = JSystemProperties.getInstance().getPreference(
				FrameworkOptions.SCM_PASSWORD);
		if (null == password) {
			password = "";
		}
	}

	private void initWorkingCopy() {
		if (!enabled) {
			return;
		}
		if (!sourceControl.isWorkingCopyInitialize()) {
			try {
				int answer = JOptionPane
						.showConfirmDialog(
								TestRunner.treeView,
								"All scnearios and SUT files will be deleted and replaced with repository files",
								"Checkout warning", JOptionPane.YES_NO_OPTION);
				if (JOptionPane.NO_OPTION == answer) {
					log.fine("User select not to initialize working copy");
					enabled = false;
					return;
				}
				sourceControl.initWorkingCopy();
			} catch (SourceControlException e) {
				log.log(Level.WARNING, "Failed to initialize working copy", e);
				enabled = false;
				JOptionPane.showConfirmDialog(TestRunner.treeView,
						"Failed to initialize working copy",
						"Source Control Failure", JOptionPane.DEFAULT_OPTION,
						JOptionPane.ERROR_MESSAGE);

			}
		}
	}

	/**
	 * Connect to the remote repository
	 */
	private void connect() {
		if (!enabled) {
			return;
		}
		try {
			sourceControl.connect(repository, user, password);
		} catch (SourceControlException e) {
			enabled = false;
			log.log(Level.WARNING, "Failed to connect to SCM repository", e);
			e.printStackTrace();
		}

	}

	/**
	 * Verifies that the JSystem is connected to the repository. If not, will
	 * pop dialog that allows the user to connect to repository.
	 * 
	 * @return If connected to repository.
	 */
	private boolean verifyEnabled() {
		if (!enabled) {
			int answer = JOptionPane
					.showConfirmDialog(
							TestRunner.treeView,
							"Source control connection is not enabled. Do you want to connect?",
							"Source Control Disabled",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.INFORMATION_MESSAGE);
			if (answer == JOptionPane.YES_OPTION) {
				init();
				return true;
			}
		}
		return enabled;
	}

	/**
	 * Get the source control class name and instantiate it.
	 */
	private void instantiateSourceControl() {
		final String svnClass = JSystemProperties.getInstance().getPreference(
				FrameworkOptions.SCM_PLUGIN_CLASS);
		if (null == svnClass || svnClass.isEmpty()) {
			log.fine("No SVN class was defined");
			enabled = false;
			return;
		}
		try {
			Class<?> myclass = Class.forName(svnClass);
			sourceControl = (SourceControlI) myclass.newInstance();
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed to instanciate source control", e);
			enabled = false;
			e.printStackTrace();
			JOptionPane.showConfirmDialog(TestRunner.treeView,
					"Failed to instanciate source control",
					"Source Control Failure", JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE);
		}

	}

	// ********Scenario Handling***********

	/**
	 * Updates the current root scenario and all sub scenarios.
	 */
	public void updateCurrentScenario() {
		if (!verifyEnabled()) {
			return;
		}
		Scenario scenario = ScenariosManager.getInstance().getCurrentScenario();
		try {
			sourceControl.updateScenario(scenario);
		} catch (SourceControlException e) {
			log.log(Level.WARNING, "Failed to update scenario", e);
			e.printStackTrace();
			JOptionPane.showConfirmDialog(TestRunner.treeView,
					"Failed to update scenario", "Source Control Failure",
					JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);

		}
		loadScenario(scenario);
		updateScenarioStatus(scenario);

	}

	/**
	 * Update the scenarios file.
	 * 
	 * @param scenario
	 */
	private void updateScenarioStatus(Scenario scenario) {
		if (!verifyEnabled()) {
			return;
		}
		try {
			scenarioStatus = sourceControl.getScenarioStatus(scenario);
			scenarioStatusAction.actionPerformed(null);
		} catch (SourceControlException e) {
			System.out.println("Failed to get status of scenario");
			e.printStackTrace();
			JOptionPane.showConfirmDialog(TestRunner.treeView,
					"Failed to update scenario status",
					"Source Control Failure", JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Adds the root scenario and all sub scenarios to the repository
	 */
	public void addCurrentScenario() {
		if (!verifyEnabled()) {
			return;
		}
		Scenario scenario = ScenariosManager.getInstance().getCurrentScenario();
		try {
			sourceControl.addScenario(scenario);
		} catch (SourceControlException e) {
			log.log(Level.WARNING, "Failed to add scenario", e);
			e.printStackTrace();
			JOptionPane.showConfirmDialog(TestRunner.treeView,
					"Failed to add scenario", "Source Control Failure",
					JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);

		}
		updateScenarioStatus(scenario);
	}

	/**
	 * Commit current root scenarios and all sub scenarios to the repository.
	 */
	public void commitCurrentScenario() {
		if (!verifyEnabled()) {
			return;
		}
		Scenario scenario = ScenariosManager.getInstance().getCurrentScenario();
		try {
			sourceControl.commitScenario(scenario);
		} catch (SourceControlException e) {
			log.log(Level.WARNING, "Failed to commit scenario", e);
			e.printStackTrace();
			JOptionPane.showConfirmDialog(TestRunner.treeView,
					"Failed to commit scenario", "Source Control Failure",
					JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		}
		updateScenarioStatus(scenario);
	}

	public void revertCurrentScenario() {
		if (!verifyEnabled()) {
			return;
		}

		if (!revertConfirmation()){
			return;
		}
		final Scenario scenario = ScenariosManager.getInstance()
				.getCurrentScenario();
		try {
			sourceControl.revertScenario(scenario);
		} catch (SourceControlException e) {
			log.log(Level.WARNING, "Failed to revert scenario", e);
			e.printStackTrace();
		}
		loadScenario(scenario);
		updateScenarioStatus(scenario);
		if (Status.NORMAL != scenarioStatus) {
			JOptionPane.showConfirmDialog(TestRunner.treeView,
					"Failed to revert scenario", "Source Control Failure",
					JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadScenario(final Scenario scenario) {
		TestRunner.treeView.getTableController()
				.clearExpandSelected(true, true);
		try {
			TestRunner.treeView.getTableController().selectScenario(
					scenario.getName());
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed to select scenario after reverting",
					e);
			e.printStackTrace();
		}
		TestRunner.treeView.getTableController().expandAll();
		TestRunner.treeView.getTableController()
				.updateEnabledAndDisabledActions(null);
		TestRunner.treeView.getTableController().loadScenario(
				scenario.getName(), false);
		ScenariosManager.setDirty();
		try {
			SaveScenarioAction.getInstance().saveCurrentScenario();
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed to save scenario after change", e);
			e.printStackTrace();
		}

	}

	// ********SUT Handling***********

	private void updateSutStatus(String sutName) {
		if (!verifyEnabled()) {
			return;
		}
		try {
			sutStatus = sourceControl.getSutStatus(sutName);
			sutStatusAction.actionPerformed(null);
		} catch (SourceControlException e) {
			JOptionPane.showConfirmDialog(TestRunner.treeView,
					"Failed to update sut", "Source Control Failure",
					JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);

			System.out.println("Failed to get status of sut");
			e.printStackTrace();
		}

	}

	public void addCurrentSut() {
		if (!verifyEnabled()) {
			return;
		}
		final String sutName = SutFactory.getInstance().getSutFile().getName();
		try {
			sourceControl.addSut(sutName);
		} catch (SourceControlException e) {
			log.log(Level.WARNING, "Failed to add sut", e);
			e.printStackTrace();
			JOptionPane.showConfirmDialog(TestRunner.treeView,
					"Failed to add sut", "Source Control Failure",
					JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);

		}
		updateSutStatus(sutName);

	}

	public void commitCurrentSut() {
		if (!verifyEnabled()) {
			return;
		}
		final String sutName = SutFactory.getInstance().getSutFile().getName();
		try {
			sourceControl.commitSut(sutName);
		} catch (SourceControlException e) {
			log.log(Level.WARNING, "Failed to commit file", e);
			e.printStackTrace();
			JOptionPane.showConfirmDialog(TestRunner.treeView,
					"Failed to commit scenario", "Source Control Failure",
					JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);

		}
		updateSutStatus(sutName);

	}

	public void revertCurrentSut() {
		if (!verifyEnabled()) {
			return;
		}

		if (!revertConfirmation()){
			return;
		}


		final File sutFile = SutFactory.getInstance().getSutFile();
		try {
			sourceControl.revertSut(sutFile.getName());
		} catch (SourceControlException e) {
			log.log(Level.WARNING, "Failed to revert sut", e);
			e.printStackTrace();
		}
		updateSutStatus(sutFile.getName());
		if (Status.NORMAL != sutStatus) {
			JOptionPane.showConfirmDialog(TestRunner.treeView,
					"Failed to revert sut", "Source Control Failure",
					JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		}
		try {
			copySutToTestsFolder(sutFile);
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(TestRunner.treeView,
					"Failed to copy SUT file to tests folder",
					"Source Control Failure", JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE);
			log.severe("Failed to copy SUT file to tests folder");
			e.printStackTrace();
		}
		SutFactory.getInstance().setSut(sutFile.getName());

	}


	public void updateCurrentSut() {
		if (!verifyEnabled()) {
			return;
		}
		final File sutFile = SutFactory.getInstance().getSutFile();
		try {
			sourceControl.updateSut(sutFile.getName());
		} catch (SourceControlException e) {
			log.log(Level.WARNING, "Failed to update sut", e);
			e.printStackTrace();
		}
		updateSutStatus(sutFile.getName());
		try {
			copySutToTestsFolder(sutFile);
		} catch (IOException e) {
			log.severe("Failed to copy SUT file to tests folder");
			e.printStackTrace();
			JOptionPane.showConfirmDialog(TestRunner.treeView,
					"Failed to copy SUT file to tests folder",
					"Source Control Failure", JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE);
		}
		SutFactory.getInstance().setSut(sutFile.getName());

	}

	/**
	 * Copies the SUT file from the classes folder to the source folder.
	 * 
	 * @param sutFile
	 * @throws IOException
	 */
	private void copySutToTestsFolder(File sutFile) throws IOException {
		File classDir = new File(JSystemProperties.getCurrentTestsPath());
		if (sutFile.getAbsolutePath().startsWith(classDir.getAbsolutePath())) {
			File sutSrcFile = new File(JSystemProperties.getInstance()
					.getPreference(FrameworkOptions.RESOURCES_SOURCE_FOLDER)
					+ sutFile.getAbsolutePath().substring(
							classDir.getAbsolutePath().length()));
			FileUtils.copyFile(sutFile, sutSrcFile);
		}
	}
	
	/**
	 * Confirm that the user knows that the revert will cause the local files to
	 * be deleted.
	 * 
	 * @return true if user confirmed
	 */
	private static boolean revertConfirmation() {
		int answer = JOptionPane
				.showConfirmDialog(
						TestRunner.treeView,
						"Local files will be deleted, are you sure you want to proceed?",
						"Revert Warning", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
		if (answer == JOptionPane.NO_OPTION) {
			return false;
		}
		return true;
	}


	// ****** Events Handling**************

	/**
	 * Will update the scenario state when scenario change event is triggered.
	 */
	@Override
	public void scenarioChanged(Scenario current, ScenarioChangeType changeType) {
		updateScenarioStatus(current);

	}

	@Override
	public void scenarioDirectoryChanged(File directory) {
	}

	/**
	 * Will update the scenario state when scenario change event is triggered.
	 */
	@Override
	public void scenarioDirtyStateChanged(Scenario s, boolean isDirty) {
		updateScenarioStatus(s);

	}

	@Override
	public void testParametersChanged(String testIIUUD, Parameter[] oldValues,
			Parameter[] newValues) {

	}

	@Override
	public void sutChanged(String sutName) {
		updateSutStatus(sutName);
	}

	public Status getScenarioStatus() {
		return scenarioStatus;
	}

	public Status getSutStatus() {
		return sutStatus;
	}

	public boolean isEnabled() {
		return enabled;
	}

}
