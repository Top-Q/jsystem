package jsystem.extensions.report.difido;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import il.co.topq.difido.model.execution.Execution;
import il.co.topq.difido.model.execution.ScenarioNode;
import il.co.topq.difido.model.remote.ExecutionDetails;
import il.co.topq.difido.model.test.TestDetails;
import jsystem.extensions.report.difido.RemoteDifidoProperties.RemoteDifidoOptions;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;

public class RemoteHtmlReporter extends AbstractHtmlReporter {

	private static final Logger log = Logger.getLogger(RemoteHtmlReporter.class.getName());

	private static final int MAX_NUM_OF_ALLOWED_FAILURES = 10;

	private boolean enabled;

	private DifidoClient client;
	private int executionId;

	private int machineId;

	private int numOfFailures;

	protected RemoteDifidoProperties difidoProps;

	private ExecutionDetails details;

	public RemoteHtmlReporter() {
		super();
	}

	@Override
	public void initReporterManager() throws IOException {
	}

	@Override
	public String getName() {
		return "RemoteHtmlReporter";
	}

	@Override
	public void init() {
		super.init();

		// We are doing it because we need that the file of the Difido
		// properties to be created if it is not exists.
		new RemoteDifidoProperties();

	}

	public void endRun() {
		super.endRun();
		// We are not using shared execution, that means that we are the only
		// one that are using it and we just ended with it, so let's set it to
		// not active
		log.fine("endRun, executionId: " + executionId);
		if (executionId > 0 && !difidoProps.getPropertyAsBoolean(RemoteDifidoOptions.USE_SHARED_EXECUTION)) {
			try {
				client.endExecution(executionId);
				client.updateSerialNumber(executionId);
				
//              // PUT AND THEN CALL THIS FUNCTION INSIDE YOUR TEST!!!
//				
//				public void updateSerialNumber(String serialNum, boolean realTime) throws Exception {
//					Properties execProps = new Properties();
//					final String EXEC_PROPS_FILE = "execution.properties";
//					try {
//				
//						File f = new File(EXEC_PROPS_FILE);
//						if (f.exists()) 			
//							execProps.load(new FileInputStream(EXEC_PROPS_FILE));
//	
//						execProps.setProperty("execution.serial", serialNum);
//						execProps.store(new FileOutputStream(EXEC_PROPS_FILE), null);
//						if (realTime) {
//							RemoteDifidoProperties difidoProps = new RemoteDifidoProperties();;
//							String host = difidoProps.getPropertyAsString(RemoteDifidoOptions.HOST);
//							int port = Integer.parseInt(difidoProps.getPropertyAsString(RemoteDifidoOptions.PORT));
//							String BASE_URI_TEMPLATE = "http://%s:%d/api/";
//							String baseUri = String.format(BASE_URI_TEMPLATE, host, port);
//							HttpClient client = new HttpClient();
//							final PutMethod method = new PutMethod(baseUri + "executions/" + execProps.getProperty("execution.id") + "?serial=" + serialNum);
//							method.setRequestHeader(new Header("Content-Type", "text/plain"));
//							final int responseCode = client.executeMethod(method);
//							if (responseCode != 200 && responseCode != 204) {
//								throw new Exception("Request was not successful. Response is: " + responseCode + ".\n Response body: "
//										+ method.getResponseBodyAsString());
//							}
//						}
//					} catch (IOException e) {
//						e.printStackTrace();
//						log.warning("Update Serial number failed!");
//					}	
//				}
				
			} catch (Exception e) {
				log.warning("Failed to close execution with id " + executionId);

			}
			executionId = -1;
		}

	}

	@Override
	public void startRun() {
		super.initModel();
		super.startRun();
		difidoProps = new RemoteDifidoProperties();
		String host = null;
		int port = 0;
		try {
			enabled = Boolean.parseBoolean(difidoProps.getPropertyAsString(RemoteDifidoOptions.ENABLED));
			if (!enabled) {
				return;
			}
			host = difidoProps.getPropertyAsString(RemoteDifidoOptions.HOST);
			port = Integer.parseInt(difidoProps.getPropertyAsString(RemoteDifidoOptions.PORT));
			client = new DifidoClient(host, port);
			executionId = prepareExecution();
			log.fine("startRun: current executionId = " + executionId);
			machineId = client.addMachine(executionId, getExecution().getLastMachine());
			enabled = true;
			log.fine(RemoteHtmlReporter.class.getName() + " was initialized successfully");
		} catch (Throwable t) {
			enabled = false;
			log.warning("Failed to init " + RemoteHtmlReporter.class.getName() + " connection with host '" + host + ":"
					+ port + "' due to " + t.getMessage());
		}

	}

	private int prepareExecution() throws Exception {
		// Fetching properties
		final boolean appendToExistingExecution = difidoProps
				.getPropertyAsBoolean(RemoteDifidoOptions.APPEND_TO_EXISTING_EXECUTION);
		final boolean useSharedExecution = difidoProps.getPropertyAsBoolean(RemoteDifidoOptions.USE_SHARED_EXECUTION);
		String description = difidoProps.getPropertyAsString(RemoteDifidoOptions.DESCRIPTION);

		final int id = difidoProps.getPropertyAsInt(RemoteDifidoOptions.EXISTING_EXECUTION_ID);
		final boolean forceNewExecution = difidoProps.getPropertyAsBoolean(RemoteDifidoOptions.FORCE_NEW_EXECUTION);
		final Map<String, String> properties = difidoProps.getPropertyAsMap(RemoteDifidoOptions.EXECUTION_PROPETIES);

		//add SUT=<sutFileName> - remove the .xml 
		String sutFile = JSystemProperties.getInstance().getPreference(FrameworkOptions.USED_SUT_FILE);
		log.fine("prepareExecution: current sutFile = " + sutFile);
		properties.put("SUT", (null == sutFile) ? "" : sutFile.replaceAll(".xml", ""));
		//add Scenario=<scenarioName> - remove preceding scenarios/ path
		String scen = JSystemProperties.getInstance().getPreference(FrameworkOptions.CURRENT_SCENARIO);
		log.fine("prepareExecution: current scenarioName = " + scen);
		properties.put("Scenario", (null == scen) ? "" : scen.replace("scenarios/",""));
			
		if (appendToExistingExecution && !forceNewExecution) {
			if (id >= 0) {
				log.fine("prepareExecution: current id = " + id);
				return id;
			}
			if (executionId > 0) {
				log.fine("prepareExecution: current executionId = " + executionId);
				return executionId;
			}

		}
		details = new ExecutionDetails(description, useSharedExecution);
		details.setForceNew(forceNewExecution);
		details.setExecutionProperties(new HashMap<String,String>(properties));
		log.fine("ADD DIFIDO EXECUTION : appendToExistingExecution:" + appendToExistingExecution + 
				", useSharedExecution: " + useSharedExecution + ", description: " + description + 
				", id: " + id + ", forceNewExecution: " + forceNewExecution + ", executionId: " + executionId);
		return client.addExecution(details);
	}

	/**
	 * We want to add all the execution properties for each scenario. This will
	 * eventually appear in the ElasticSearch
	 * 
	 * @param scenario
	 */
	protected void addScenarioProperties(ScenarioNode scenario) {
		super.addScenarioProperties(scenario);
		// If the execution is shared, and we were not responsible for creating
		// the execution, the execution details in this stage will be null.
		if (details != null && details.getExecutionProperties() != null) {
			for (String key : details.getExecutionProperties().keySet()) {
				scenario.addScenarioProperty(key, details.getExecutionProperties().get(key));
			}
		}
	}

	@Override
	protected void writeTestDetails(TestDetails testDetails) {
		if (!enabled) {
			return;
		}
		try {
			client.addTestDetails(executionId, testDetails);
		} catch (Exception e) {
			log.warning("Failed updating test details to remote server due to " + e.getMessage());
			checkIfNeedsToDisable();
		}

	}

	@Override
	protected void writeExecution(Execution execution) {
		if (!enabled) {
			return;
		}

		try {
			client.updateMachine(executionId, machineId, execution.getLastMachine());
		} catch (Exception e) {
			log.warning("Failed updating test details to remote server due to " + e.getMessage());
			checkIfNeedsToDisable();
		}
	}

	private void checkIfNeedsToDisable() {
		numOfFailures++;
		if (numOfFailures > MAX_NUM_OF_ALLOWED_FAILURES) {
			log.warning("Communication to server has failed more then " + MAX_NUM_OF_ALLOWED_FAILURES
					+ ". Disabling report reporter");
			enabled = false;
		}
	}

	@Override
	protected Execution readExecution() {
		return null;
	}

	@Override
	protected void filesWereAddedToReport(File[] files) {
		if (!enabled) {
			return;
		}
		if (files == null || files.length == 0) {
			return;
		}
		for (File file : files) {
			try {
				client.addFile(executionId, getTestDetails().getUid(), file);
			} catch (Exception e) {
				log.warning("Failed uploading file " + file.getName() + " to remote server due to " + e.getMessage());
			}
		}

	}

	@Override
	protected void updateTestDirectory() {
		// Since we are not using the file system, there is no point in setting
		// the current test folder
	}

}
