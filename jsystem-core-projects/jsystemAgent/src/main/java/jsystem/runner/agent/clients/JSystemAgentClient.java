/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.clients;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.runner.agent.MBeanNames;
import jsystem.runner.agent.ProjectComponent;
import jsystem.runner.agent.server.RunnerAgentMBean;
import jsystem.runner.agent.server.RunnerEngine;
import jsystem.runner.agent.server.RunnerEngineExecutionState;
import jsystem.utils.ProgressNotifier;

import com.aqua.filetransfer.ftp.FTPClient;

/**
 * Agent client.
 * 
 * @author goland
 */
public class JSystemAgentClient extends SystemObjectImpl implements RunnerEngine {
	private static Logger log = Logger.getLogger(JSystemAgentClient.class.getName());
	private final static Pattern URL_PATTERN = Pattern.compile(".*\\:\\d{1,4}\\z");
	private ConnectionState connectionState = ConnectionState.na;
	private String agentHost;
	private int agentPort;
	private JMXConnector connector;
	private RunnerAgentMBean mbeanProxy;
	private ClientEventsHub hub;
	private AgentClientListenersManager listenersManager;
	private Properties agentProps;
	private JMXServiceURL serviceURL;
	private ProgressNotifier notifier;

	public JSystemAgentClient() {
		listenersManager = new AgentClientListenersManager();
	}

	/**
	 */
	public JSystemAgentClient(String url) throws Exception {
		this();
		validateAndInitAgentAddress(url);
	}

	/**
	 */
	public JSystemAgentClient(String agentHost, int port) throws Exception {
		this();
		validateAndInitAgentAddress(agentHost, agentPort);
	}

	/**
	 */
	public void init() throws Exception {
		super.init();
		ConnectionStateListener connectionListener = new ConnectionStateListener();
		try {
			serviceURL = convertToRmiUrl(getAgentHost(), getAgentPort());
			connector = JMXConnectorFactory.connect(serviceURL);
			hub = new ClientEventsHub(listenersManager);
			connector.addConnectionNotificationListener(hub, null, getId());
			addListener(connectionListener);
			MBeanServerConnection remote = connector.getMBeanServerConnection();
			mbeanProxy = JMX.newMBeanProxy(remote, MBeanNames.RUNNER_AGENT, RunnerAgentMBean.class, true);
			remote.addNotificationListener(MBeanNames.RUNNER_AGENT, hub, null, null);
			hub.setAgentMBean(mbeanProxy);
			agentProps = mbeanProxy.getAgentProperties();
			setConnectionState(ConnectionState.connected);
			listenersManager.handleNotification(
					new JMXConnectionNotification(JMXConnectionNotification.OPENED, connector, connector.getConnectionId(), 0,
							"Connection to " + getId() + " was initiated", null), "");
		} catch (Exception e) {
			setConnectionState(ConnectionState.disconnected);
			removeListener(connectionListener);
			// it is important to reset all members, because sometimes
			// even though operation fails, mbeanProxy is valid and user
			// doesn't get notifications later on.
			serviceURL = null;
			connector = null;
			hub = null;
			mbeanProxy = null;
			agentProps = null;
			throw e;
		}
	}

	public void close() {
		try {
			connector.close();
		} catch (Exception e) {
			log.log(Level.FINE, "Failed closing connection to agent", e);
		}
		super.close();
	}

	/**
	 * Signals the agent to shut itself.
	 */
	public void shutAgentDown() throws Exception {
		getConnector().getMBeanServerConnection().invoke(MBeanNames.AGENT_MAIN, "stop", null, null);
	}

	/**
	 * Signals the agent restart
	 */
	public void restartAgent() throws Exception {
		getConnector().getMBeanServerConnection().invoke(MBeanNames.AGENT_MAIN, "restart", null, null);
	}

	/**
	 * synchronized agent project with the runner project
	 */
	public void synchronizeProject(File projectZip, String projectName, String scenario, String sut, int[] selectedTests, Properties props)
			throws Exception {
		if (props != null) {
			sendProgressMessage("Setting jsystem properties", 5);
			getMbeanProxy().setJsystemProperties(props);
			sendProgressMessage("Jsystem properties were set", 7);
		}
		if (projectName != null) {
			sendProgressMessage("Setting active project ", 10);
			mbeanProxy.changeProject(projectName);
		}
		if (projectZip != null) {
			sendProgressMessage("Starting upload to " + getId(), 30);
			uploadProject(projectZip, projectZip.getName(), ProjectComponent.values());
		}
		if (scenario != null) {
			sendProgressMessage("Setting active scenario to " + scenario, 80);
			mbeanProxy.setActiveScenario(scenario);
		}
		if (sut != null) {
			sendProgressMessage("Setting active sut to " + sut, 90);
			changeSut(sut);
		}
	}

	/**
	 * Waits <code>timeToWaitInMilliSec</code> milliseconds for agent to be available.<br>
	 * if <code>timeToWaitInMilliSec</code> <=0 waits forever.
	 */
	public boolean waitForAgentToBeAvailable(long timeToWaitInMilliSec) throws Exception {
		long startTime = java.lang.System.currentTimeMillis();
		while (true) {
			if (timeToWaitInMilliSec > 0 && java.lang.System.currentTimeMillis() - startTime > timeToWaitInMilliSec) {
				return false;
			}
			try {
				init();
				return true;
			} catch (Throwable t) {
				log.fine("Attempt to connect agent failed");
			}
			Thread.sleep(500);
		}
	}

	/**
	 * 
	 */
	public boolean waitForAgentToFinishInitiation(long timeToWaitInMilliSec) throws Exception {
		long startTime = java.lang.System.currentTimeMillis();
		while (true) {
			if (timeToWaitInMilliSec > 0 && java.lang.System.currentTimeMillis() - startTime > timeToWaitInMilliSec) {
				return false;
			}
			if (!getEngineExecutionState().equals(RunnerEngineExecutionState.initiating)) {
				return true;
			}
			Thread.sleep(3000);
		}
	}

	/**
	 * Waits <code>timeToWaitInMilliSec</code> milliseconds for agent execution state to be <code>state</code>.<br>
	 * if <code>timeToWaitInMilliSec</code> <=0 waits forever.
	 * 
	 * @see RunnerEngineExecutionState
	 */
	public boolean waitForExecutionState(long timeToWaitInMilliSec, RunnerEngineExecutionState state) throws Exception {
		long startTime = java.lang.System.currentTimeMillis();
		while (true) {
			if (timeToWaitInMilliSec > 0 && java.lang.System.currentTimeMillis() - startTime > timeToWaitInMilliSec) {
				return false;
			}
			try {
				if (getEngineExecutionState().equals(state)) {
					return true;
				}
			} catch (Exception e) {
				waitForAgentToBeAvailable(500);
			}
			Thread.sleep(500);
		}
	}

	@Override
	public String getCurrentProjectName() throws Exception {
		return mbeanProxy.getCurrentProjectName();
	}

	@Override
	public String getActiveScenario() throws Exception {
		return ScenarioHelpers.removeScenarioHeader(mbeanProxy.getActiveScenario());
	}

	@Override
	public void setActiveScenario(Scenario scenario) throws Exception {
		mbeanProxy.setActiveScenario(scenario.getName());
	}

	public void setActiveScenario(String scenarioName) throws Exception {
		mbeanProxy.setActiveScenario(scenarioName);
	}

	public String getCurrentProjectMD5() throws Exception {
		return mbeanProxy.getCurrentProjectMD5();
	}

	@Override
	public void changeSut(String sutFile) throws Exception {
		mbeanProxy.setSutFile(sutFile);
	}

	@Override
	public void addListener(Object eventListsner) {
		listenersManager.addListener(eventListsner);
	}

	@Override
	public void removeListener(Object eventListsner) {
		listenersManager.removeListener(eventListsner);
	}

	@Override
	public void changeProject(String testsPath) throws Exception {
		mbeanProxy.changeProject(testsPath);

	}

	@Override
	public void initReporters() {
		mbeanProxy.initReporters();
		listenersManager.initReporters();
	}

	@Override
	public void refresh() throws Exception {
		mbeanProxy.refresh();
	}

	@Override
	public void run() throws Exception {
		mbeanProxy.run();
	}

	@Override
	public void run(String uuid) throws Exception {
		mbeanProxy.run(mbeanProxy.getActiveScenario(), uuid);

	}

	public void run(String rootScenario, String uuid) throws Exception {
		mbeanProxy.run(rootScenario, uuid);
	}

	@Override
	public void gracefulStop() throws Exception {
		mbeanProxy.gracefulStop();
	}

	@Override
	public void stop() throws Exception {
		mbeanProxy.stop();

	}

	@Override
	public void pause() {
		try {
			mbeanProxy.pause();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void resume() {
		try {
			mbeanProxy.resume();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setRepeat(int numOfRepeats) throws Exception {
		mbeanProxy.setRepeat(numOfRepeats);
	}

	@Override
	public void enableRepeat(boolean enable) throws Exception {
		mbeanProxy.enableRepeat(enable);

	}

	@Override
	public RunnerEngineExecutionState getEngineExecutionState() throws Exception {
		return RunnerEngineExecutionState.valueOf(mbeanProxy.getEngineExecutionState());
	}

	@Override
	public String getId() {
		return getAgentHost() + ":" + getAgentPort();
	}

	@Override
	public URL getLogUrl() throws Exception {
		int webPort = Integer.parseInt(agentProps.getProperty(FrameworkOptions.AGENT_WEB_PORT.toString()));
		String logPath = agentProps.getProperty(FrameworkOptions.LOG_FOLDER.toString());
		logPath = logPath.replace("\\", "/");
		return new URL("http://" + serviceURL.getHost() + ":" + webPort + "/" + logPath);
	}

	@Override
	public String getEngineVersion() throws Exception {
		return mbeanProxy.getAgentVersion();
	}

	public String getAgentHost() {
		return agentHost;
	}

	public int getAgentPort() {
		return agentPort;
	}

	public synchronized ConnectionState getConnectionState() {
		return connectionState;
	}

	public void setNotifier(ProgressNotifier notifier) {
		this.notifier = notifier;
	}

	protected void sendProgressMessage(String message, int progress) {
		if (notifier != null) {
			// If foreground mode - send report to pop-up message
			notifier.notifyProgress(message, progress);
		} else {
			// If run in background mode - send report to reporter
			report.report(message);
		}
	}

	protected void sendDoneProgressMessage() {
		if (notifier != null) {
			notifier.done();
		}
	}

	protected void uploadProject(File zippedProject, String name, ProjectComponent[] components) throws Exception {
		FTPClient client = new FTPClient();
		client.setServer(serviceURL.getHost());
		client.setPort(Integer.parseInt(agentProps.getProperty(FrameworkOptions.AGENT_FTP_PORT.toString())));
		client.setUsername("aqua");
		client.setPassword("aqua");
		client.init();
		client.connect();
		client.changeToBinary();
		try {
			client.putFile(zippedProject.getAbsolutePath(), name);
		} finally {
			client.disconnect();
		}
		mbeanProxy.extractProjectZip(name, components);
	}

	protected JMXConnector getConnector() {
		return connector;
	}

	protected RunnerAgentMBean getMbeanProxy() {
		return mbeanProxy;
	}

	private static JMXServiceURL convertToRmiUrl(String host, int port) throws Exception {
		String url = MessageFormat.format("/jndi/rmi://{0}/jmxrmi", host + ":" + port);
		JMXServiceURL serviceUrl = new JMXServiceURL("rmi", host, port, url);
		return serviceUrl;
	}

	private synchronized void setConnectionState(ConnectionState state) {
		connectionState = state;
	}

	/**
	 * 
	 */
	private void validateAndInitAgentAddress(String url) throws Exception {
		Matcher m = URL_PATTERN.matcher(url);
		if (!m.find()) {
			throw new Exception("Agent URL has to be in the following format: host:port");
		}
		String _agentHost = url.substring(0, url.indexOf(":"));
		int _agentPort = Integer.parseInt(url.substring(url.indexOf(":") + 1));
		validateAndInitAgentAddress(_agentHost, _agentPort);
	}

	/**
	 * Due to limitation of the ftp client, the address of the agent MUST NOT be localhost (127.0.0.1). If the agent is installed locally,
	 * the IP address of the machine should be used.
	 */
	private void validateAndInitAgentAddress(String host, int port) throws Exception {
		InetAddress address = InetAddress.getByName(host);
		if ("127.0.0.1".equals(address.getHostAddress())) {
			throw new Exception("Local host address is not allowed, please enter agent external IP. You can try "
					+ InetAddress.getLocalHost().getHostAddress());
		}
		this.agentHost = host;
		this.agentPort = port;
	}

	/**
	 * 
	 */
	class ConnectionStateListener implements NotificationListener {
		@Override
		public void handleNotification(Notification notification, Object handback) {
			if (!(notification instanceof JMXConnectionNotification)) {
				return;
			}
			String notificationType = notification.getType();
			if (notificationType.equals(JMXConnectionNotification.CLOSED) || notificationType.equals(JMXConnectionNotification.FAILED)) {
				setConnectionState(ConnectionState.disconnected);

			}

			if (notificationType.equals(JMXConnectionNotification.OPENED)) {
				setConnectionState(ConnectionState.connected);
			}
		}

	}

	@Override
	public Properties getRunProperties() throws Exception {
		return mbeanProxy.getRunProperties();
	}
}
