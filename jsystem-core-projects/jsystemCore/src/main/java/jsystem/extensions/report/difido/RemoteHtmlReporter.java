package jsystem.extensions.report.difido;

import il.co.topq.difido.model.execution.Execution;
import il.co.topq.difido.model.remote.ExecutionDetails;
import il.co.topq.difido.model.test.TestDetails;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import jsystem.extensions.report.difido.RemoteDifidoProperties.RemoteDifidoOptions;

public class RemoteHtmlReporter extends AbstractHtmlReporter {

	private static final Logger log = Logger.getLogger(RemoteHtmlReporter.class.getName());

	private static final int MAX_NUM_OF_ALLOWED_FAILURES = 10;

	private boolean enabled;

	private DifidoClient client;

	private int executionId;

	private int machineId;

	private int numOfFailures;

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
		super.initModel();
		String host = null;
		int port = 0;
		try {
			enabled = Boolean.parseBoolean(RemoteDifidoProperties.getInstance().getPropertyAsString(
					RemoteDifidoOptions.ENABLED));
			if (!enabled) {
				return;
			}
			host = RemoteDifidoProperties.getInstance().getPropertyAsString(RemoteDifidoOptions.HOST);
			port = Integer.parseInt(RemoteDifidoProperties.getInstance().getPropertyAsString(RemoteDifidoOptions.PORT));
			client = new DifidoClient(host, port);
			closeOldExecutionIfNeeded();
			executionId = prepareExecution();
			machineId = client.addMachine(executionId, getExecution().getLastMachine());
			enabled = true;
			log.fine(RemoteHtmlReporter.class.getName() + " was initialized successfully");
		} catch (Throwable t) {
			enabled = false;
			log.warning("Failed to init " + RemoteHtmlReporter.class.getName() + "connection with host '" + host + ":"
					+ port + "' due to " + t.getMessage());
		}

	}

	private void closeOldExecutionIfNeeded() throws Exception {
		// We are not using shared execution, that means that we are the only
		// one that are using it and we just ended with it, so let's set it to
		// not active
		if (executionId > 0
				&& !RemoteDifidoProperties.getInstance().getPropertyAsBoolean(RemoteDifidoOptions.USE_SHARED_EXECUTION)) {
			client.endExecution(executionId);
		}
	}

	private int prepareExecution() throws Exception {
		// Fetching properties
		final RemoteDifidoProperties props = RemoteDifidoProperties.getInstance();
		final boolean appendToExistingExecution = props
				.getPropertyAsBoolean(RemoteDifidoOptions.APPEND_TO_EXISTING_EXECUTION);
		final boolean useSharedExecution = props.getPropertyAsBoolean(RemoteDifidoOptions.USE_SHARED_EXECUTION);
		final String description = props.getPropertyAsString(RemoteDifidoOptions.DESCRIPTION);
		final int id = props.getPropertyAsInt(RemoteDifidoOptions.EXISTING_EXECUTION_ID);
		final boolean forceNewExecution = props.getPropertyAsBoolean(RemoteDifidoOptions.FORCE_NEW_EXECUTION);
		final Map<String, String> properties = props.getPropertyAsMap(RemoteDifidoOptions.EXECUTION_PROPETIES);

		if (appendToExistingExecution && id >= 0 && !forceNewExecution) {
			return id;
		}
		ExecutionDetails details = new ExecutionDetails(description, useSharedExecution);
		details.setForceNew(forceNewExecution);
		details.setExecutionProperties(properties);
		return client.addExecution(details);
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
