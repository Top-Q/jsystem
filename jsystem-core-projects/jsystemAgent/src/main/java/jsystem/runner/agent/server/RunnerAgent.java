/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.server;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

import javax.management.NotificationBroadcasterSupport;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.JSystemListeners;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.RunnerListenersManager;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.sut.SutFactory;
import jsystem.runner.agent.ProjectComponent;
import jsystem.runner.agent.mediators.ExecutionListenerMediator;
import jsystem.runner.agent.mediators.ExtendsTestListenerMediator;
import jsystem.runner.agent.mediators.FixtureListenerMediator;
import jsystem.runner.agent.mediators.InteractiveReporterMediator;
import jsystem.runner.agent.mediators.TestListenerMediator;
import jsystem.runner.agent.mediators.TestReporterMediator;
import jsystem.runner.agent.notifications.ShowMessageDialogNotification;
import jsystem.runner.projectsync.AutomationProjectUtils;
import jsystem.runner.projectsync.JsystemPropertiesUtils;
import jsystem.runner.projectsync.MD5Calculator;
import jsystem.runner.projectsync.ProjectUnZip;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

/**
 * The MBean that manages the runner engine.
 */
public class RunnerAgent extends  NotificationBroadcasterSupport implements RunnerAgentMBean {
	/**
	 * Prefix of agent related properties. 
	 * All properties reside in jsystem.properties file.
	 */
	private static final String AGENT_PROPERTIES_PREFIX = "agent.server";
	/**
	 * The name of the folder where automation projects zip files are transferred to
	 * from the agent client.
	 * The folder is created under agent root. 
	 */
	private static final String FILES_ROOT = "externalFiles";
	/**
	 * The name of the folder where automation projects are copied to.
	 * The folder is created under agent root. 
	 */
	private static final String PROJECTS_ROOT = "projects";
	/**
	 * Name of a default project.
	 * If there are no projects in {@value #PROJECTS_ROOT}, a default project
	 * is automatically created and set to be the current project.
	 * This is done since if no project is defined in the jsystem.properties file,
	 * the engine pops a file chooser dialog to select automation project. when
	 * running the engine in an agent we want to avoid it.
	 */
	private static final String DEFAULT_PROJECT = "defaultProject/classes";
	
	/**
	 */
	private RunnerEngineImpl runnerEngine;
	
	/**
	 */
	private File agentRootDir;
	/**
	 * @see #PROJECTS_ROOT
	 */
	private File projectsRootDir;
	
	/**
	 * @see #FILES_ROOT
	 */	
	private File filesRootDir;
	private static Logger log = Logger.getLogger(RunnerAgent.class.getName());
	
	/**
	 * members for confirmation dialog  synchronization.
	 * @see #waitForConfirmDialogResults(long) 
	 */
	private String messageDialogMutex = "messageDialogMutex";
	private volatile int confirmResults;
	private volatile long messageIndex;
		
	/**
	 * Default and only constructor.
	 * Initializes agent environment and adds relevant listeners
	 * to {@link RunnerListenersManager}
	 */	
	public RunnerAgent() throws Exception {
		initAgentEnvironment();		
		filesRootDir = new File(FILES_ROOT);
		runnerEngine = new RunnerEngineImpl();
		RunnerListenersManager.getInstance().addListener(new ExecutionListenerMediator(this));
		RunnerListenersManager.getInstance().addListener(new FixtureListenerMediator(this));		
		RunnerListenersManager.getInstance().addListener(new TestListenerMediator(this));		
		RunnerListenersManager.getInstance().addListener(new TestReporterMediator(this));
		RunnerListenersManager.getInstance().addListener(new ExtendsTestListenerMediator(this));
		RunnerListenersManager.getInstance().addListener(new InteractiveReporterMediator(this));
	}		

	/**
	 * Returns agent execution state.
	 * @see RunnerEngineExecutionState
	 */
	public String getEngineExecutionState() throws Exception {
		return runnerEngine.getEngineExecutionState().name();
	}

	/**
	 * Extracts automation project zip file.<br>
	 * Project is extracted to current project folder.
	 * The parts of the project which are extracted from the project zip file are first<br>
	 * deleted from local project.<br>
	 * <u>Expected zip file structure</u><br>
	 * zipFile<br>
	 * ------- classes<br>
	 * ------------scenarios<br>
	 * ------------sut<br>
	 * --------lib<br>
	 * --------resources<br>
	 * 
	 * @param projectFileRelativePath project zip file relative to {@value #FILES_ROOT}
	 * @param components the components that should be extracted for the zip file.
	 */
	public void extractProjectZip(String projectFileRelativePath, ProjectComponent[] components) throws Exception {
		//get current project.
		String classesPath = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER);
		//verify that one is defined
		if (StringUtils.isEmpty(classesPath)){
			throw new IllegalStateException("Current automation project is not defined.");
		}
		
		if (components == null){
			components = ProjectComponent.values();
		}
		File zipFile = new File(filesRootDir,projectFileRelativePath);
		Exception e = null;
		for (int i = 0; i < 4;i++){
			try {
				ProjectUnZip unZip = new ProjectUnZip(new File(classesPath),zipFile);
				unZip.unzipProject(components);
				e = null;
				break;
			}catch (Exception e1){
				e = e1;
				refresh();
				System.gc();
			}
		}
		zipFile.delete();
		if (e!=null){
			throw e;
		}
	}
	
	/**
	 * Given array of {@link ProjectComponent}, the method returns a map of ProjectComponent and its<br>
	 * MD5 digest.<br>
	 * If <code>components</code> is <code>null</code> a map with all project components MD5<br>
	 * is returned.<br>
	 *  
	 * This method is used to compare between the project on client machine <br>
	 * the the project is agent side side.
	 * 
	 */
	public Map<ProjectComponent, String> getProjectMD5(ProjectComponent[] components) throws Exception {
		if (components == null){
			components = ProjectComponent.values();
		}
		String classesFolder = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER);
		MD5Calculator calculator = new MD5Calculator(new File(classesFolder));
		if (!AutomationProjectUtils.isValidProject(new File(classesFolder))){
			return new HashMap<ProjectComponent, String>();
		}
		Map<ProjectComponent, String> map = calculator.getProjectMD5(components); 
		return map;
	}

	
	/**
	 * Switches active project to <code>classesPath</code>.<br>
	 * <code>classesPath</code> is expected to be projectName/classesFolderName.<br>
	 * If project doesn't exist in agent,empty project is created.
	 */
	public void changeProject(String classesPath) throws Exception {
		File classesPathAsFile = switchToAgentProject(classesPath);
		runnerEngine.changeProject(classesPathAsFile.getPath());
	}

	/**
	 * 
	 */
	@Override
	public String getCurrentProjectName() throws Exception {
		String classesFolder = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER);
		File classesPath = new File(classesFolder);
		if (!FileUtils.isAncestor(classesPath,projectsRootDir)){
			switchToAgentProject(DEFAULT_PROJECT);
			return DEFAULT_PROJECT;
		}
		String projectName = FileUtils.getRelativePath(classesPath.getParentFile(),projectsRootDir);
		return projectName;
	}

	/**
	 * Sets <code>scenarioName</code> to be active scenario.<br>
	 * If scenario with name <code>scenarioName</code> desn't exist,<br>
	 * an empty scenario is created. 
	 */
	public void setActiveScenario(String scenarioName) throws Exception {
		Scenario scenario = ScenariosManager.getInstance().getScenario(scenarioName);
		runnerEngine.setActiveScenario(scenario);
	}

	/**
	 * 
	 */
	@Override
	public String getActiveScenario() throws Exception {
		return runnerEngine.getActiveScenario();
	}

	/**
	 * see {@link RunnerEngineImpl#checkAndRunOnStart()}
	 */
	public void init() throws Exception {
		runnerEngine.checkAndRunOnStart();
		runnerEngine.init();
	}

	/**
	 * 
	 */
	public Properties getAgentProperties() throws Exception  {
		Properties retProps = new Properties();
		Properties props = JSystemProperties.getInstance().getPreferences();
		Enumeration<Object> propsEnum = props.keys();
		while (propsEnum.hasMoreElements()){
			String key = propsEnum.nextElement().toString();
			if (key.startsWith(AGENT_PROPERTIES_PREFIX)){
				retProps.put(key, props.get(key));
			}
		}
		String logDir = FileUtils.getRelativePath(new File(runnerEngine.getLogUrl().getFile()),agentRootDir);
		retProps.put(FrameworkOptions.LOG_FOLDER.toString(), logDir);
		return retProps;
	}
	
	/**
	 * Returns agent software version.
	 */
	public String getAgentVersion() throws Exception {
		return runnerEngine.getEngineVersion();
	}

	/**
	 * see {@link RunnerEngine#run()}
	 */
	@Override
	public void run() throws Exception {
		verifySUTFileIsSelected();
		runnerEngine.run();
	}
	/**
	 * see {@link RunnerEngine#run(String,String)}
	 */	
	@Override
	public void run(String rootScenarioName, String id) throws Exception {
		verifySUTFileIsSelected();
		//make sure scenario name starts with scenarios folder prefix.
		ScenarioHelpers.addScenarioHeader(rootScenarioName);
		//1. get and select scenario
		Scenario s = ScenariosManager.getInstance().getScenario(rootScenarioName);
		ScenariosManager.getInstance().setCurrentScenario(s);
		JTest t = ScenarioHelpers.getTestById(s, id);
		if (t == null){
			throw new Exception("Test with id " + id + " was not found");
		}
		
		int[] indices = s.getEnabledTestsIndexes();
		RunnerListenersManager.getInstance().report("Enabled tests: " + StringUtils.intArrToString(indices));
		runnerEngine.run(id);
	}

	/**
	 * see {@link RunnerEngine#stop()}
	 */
	public void stop() throws Exception {
		runnerEngine.stop();
	}

	/**
	 * see {@link RunnerEngine#pause()}
	 */
	public void pause() throws Exception {
		runnerEngine.pause();
	}
	
	/**
	 * see {@link RunnerEngine#gracefulStop()}
	 */
	public void gracefulStop() throws Exception {
		runnerEngine.gracefulStop();
	}
	
	/**
	 * see {@link RunnerEngine#resume()}
	 */
	public void resume() throws Exception {
		runnerEngine.resume();
		
	}
	
	/**
	 * see {@link RunnerEngine#enableRepeat(boolean)}
	 */
	public void enableRepeat(boolean enable) throws Exception {
		runnerEngine.enableRepeat(enable);
	}
	
	/**
	 * see {@link RunnerEngine#setRepeat(int)}
	 */
	public void setRepeat(int number) throws Exception {
		runnerEngine.setRepeat(number);	
	}
	
	/**
	 * see {@link RunnerEngine#refresh()}
	 */
	public void refresh() throws Exception {
		runnerEngine.refresh();
	}
	
	/**
	 * see {@link RunnerEngine#initReporters()}
	 */
	public void initReporters() {
		runnerEngine.initReporters();
	}
	
	/**
	 * see {@link RunnerEngine#changeSut(String)}
	 */
	public void setSutFile(String sutFile) throws Exception {
		runnerEngine.changeSut(sutFile);	
	}

	/**
	 * Called by {@link ShowMessageDialogNotification#invokeDispatcher(JSystemListeners)}<br>
	 * after user responded to confirm dialog.
	 * @see ShowMessageDialogNotification
	 */
	public void returnMessageConfirmationResult(int result, long notificationIndex) throws Exception {
		synchronized (messageDialogMutex) {
			confirmResults = result;
			messageIndex = notificationIndex;
			messageDialogMutex.notifyAll();
		}
	}

	/**
	 * Called by {@link InteractiveReporterMediator#showConfirmDialog(String, String, int, int)} when<br>
	 * getting {@link Reporter#showConfirmDialog(String, String, int, int)} event.<br>
	 * The method waits for use input and activation of {@link #returnMessageConfirmationResult(int, long)} by the<br>
	 * client.
	 * @see ShowMessageDialogNotification
	 */
	public int waitForConfirmDialogResults(long messageNumber) throws Exception  {
		synchronized (messageDialogMutex) {
			while (messageIndex < messageNumber){
				messageDialogMutex.wait();
			}
			return confirmResults;
		}
	}

	/**
	 * Updates jsystem.properties with <code>props</code>
	 * This method is invoked as part of the synchronization process
	 * between the client and the agent.<br>
	 * The  properties which are received from the client are filtered to prevent
	 * override of properties which are critical for proper run of the agent.<br>
	 * @see JsystemPropertiesUtils#filterJsystemProperties(Properties)  
	 */
	public void setJsystemProperties(Properties props) throws Exception {
		JsystemPropertiesUtils.filterJsystemProperties(props);
		Properties origProps = JSystemProperties.getInstance().getPreferences();
		origProps.putAll(props);
		JSystemProperties.getInstance().setPreferences(origProps);
		refresh();
	}
	
	/**
	 * Initializes agent environment.<br>
	 * <u>Initialization includes the following:</u><br>
	 * 1. Making sure projects and files folders exist.<br>
	 * 2. Making sure that {@link FrameworkOptions#TESTS_CLASS_FOLDER} in jsystem.properties<br>
	 *    is not empty and that it points to a project that exists.<br>
	 * 3. Making sure agent has an id.
	 */
	private void initAgentEnvironment() throws Exception {
		
		//initializing projects folders 
		//and default folder
		agentRootDir = new File(System.getProperty("user.dir"));
		projectsRootDir = new File(agentRootDir,PROJECTS_ROOT);
		if (!projectsRootDir.exists() && ! projectsRootDir.mkdir()){
			throw new Exception("Failed creating projects root dir. " + projectsRootDir.getAbsolutePath());
		}

		//create default project
		File defaultProjectDir = new File (projectsRootDir,DEFAULT_PROJECT);
		if (!defaultProjectDir.exists() && ! defaultProjectDir.mkdirs()){
			throw new Exception("Failed creating default project. " + defaultProjectDir.getAbsolutePath());
		}
		
		//if current project is not defined, select default project.
		String path = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER);
		if (path == null) {
			RunnerEngineImpl.setTestsPath(defaultProjectDir.getPath());
		}		


       //verifying/initializing agent.id
       String agentId = JSystemProperties.getInstance().getPreference(FrameworkOptions.AGENT_ID);
       if (StringUtils.isEmpty(agentId)){
               agentId = UUID.randomUUID().toString();
               log.info("Agent id property is empty. Agent id is initialized to " + agentId);
               JSystemProperties.getInstance().setPreference(FrameworkOptions.AGENT_ID,agentId);
       }

       //trigger log folder creation
       ListenerstManager.getInstance().report("Agent id is" + agentId );		
	}

	/**
	 * get a location to a projects class path and return
	 */
	private File switchToAgentProject(String classesPath) throws Exception {
		File projectsRootDir = new File(new File(System.getProperty("user.dir")),"projects");
		
		log.info("projectsRootDir = " + System.getProperty("user.dir") + "projects");
		log.info("classesPath = " + classesPath);
		
		//create project folder
		String classesFolderName = new File(classesPath).getName();
		
		log.info("classesFolderName = " + classesFolderName);
		
		String projectName = new File(classesPath).getParentFile().getName();
		
		log.info("projectName = " + projectName);
		
		File projectRoot = new File(projectsRootDir,projectName);
		if (!projectRoot.exists()){
			boolean mkdir = projectRoot.mkdir();
			if (!mkdir){
				throw new Exception("Failed creating new project folder : " + projectRoot.getAbsolutePath());
			}
		}
		//create classes folder
		File testsClassesPathFile;
		testsClassesPathFile = new File(projectRoot,classesFolderName);
		if (!testsClassesPathFile.exists()){
			boolean mkdir = testsClassesPathFile.mkdir();
			if (!mkdir){
				throw new Exception("Failed creating new project classes folder");
			}
			
		}
		return testsClassesPathFile;
	}
	
	@Override
	public String getCurrentProjectMD5() throws Exception {
		return runnerEngine.calculateCurrentProjectMD5();
	}

	@Override
	public Properties getRunProperties() throws Exception {
		return runnerEngine.getRunProperties();
	}

	@Override
	public void setEnabledTests(int[] selectedTests) throws Exception {
		runnerEngine.setEnabledTests(selectedTests);
	}
	
	private void verifySUTFileIsSelected(){
		SutFactory.getInstance().getSutFile();
	}

}
