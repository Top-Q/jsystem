//package jsystem.extensions.report.difido;
//
//import il.co.topq.difido.client.DifidoClient;
//import il.co.topq.difido.model.Enums.ElementType;
//import il.co.topq.difido.model.Enums.Status;
//import il.co.topq.difido.model.execution.MachineNode;
//import il.co.topq.difido.model.execution.ScenarioNode;
//import il.co.topq.difido.model.execution.TestNode;
//import il.co.topq.difido.model.test.ReportElement;
//import il.co.topq.difido.model.test.TestDetails;
//
//import java.io.File;
//import java.io.FilenameFilter;
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Queue;
//import java.util.Scanner;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.logging.Logger;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import jsystem.extensions.report.html.ExtendLevelTestReporter;
//import jsystem.framework.FrameworkOptions;
//import jsystem.framework.JSystemProperties;
//import jsystem.framework.report.ExtendTestListener;
//import jsystem.framework.report.ListenerstManager;
//import jsystem.framework.report.Reporter.EnumReportLevel;
//import jsystem.framework.report.TestInfo;
//import jsystem.framework.scenario.JTestContainer;
//import jsystem.framework.scenario.ScenarioHelpers;
//import jsystem.framework.scenario.flow_control.AntForLoop;
//import jsystem.utils.StringUtils;
//import junit.framework.AssertionFailedError;
//import junit.framework.Test;
//
//public class RemoteHtmlTestReporter implements ExtendLevelTestReporter, ExtendTestListener {
//	private static final Logger log = Logger.getLogger(HtmlReporter.class.getName());
//
//	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss:");
//
//	private static final SimpleDateFormat TIME_AND_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm:ss");
//
//	private static final String BASE_URI_TEMPLATE = "http://%s:%d/api/";
//
//	private DifidoClient client;
//	private Map<Integer, Integer> testCounter;
//	private Queue<Integer> scenarioIdsBuffer;
//
//	private SpecialReportElementsHandler specialReportsElementsHandler;
//	private int executionId;
//	private int machineId;
//	private int testId;
//	private TestDetails currentTestDetails;
//	private TestNode currentTest;
//
//	private long testStartTime;
//	private boolean enabled;
//
//	private boolean appendToExistingExecution = true;
//
//	public RemoteHtmlTestReporter() {
//		init();
//	}
//
//	@Override
//	public void init() {
//		try {
//			final String host = JSystemProperties.getInstance().getPreferenceOrDefault(
//					FrameworkOptions.REPORTS_PUBLISHER_HOST);
//			final int port = Integer.parseInt(JSystemProperties.getInstance().getPreferenceOrDefault(
//					FrameworkOptions.REPORTS_PUBLISHER_PORT));
//			// TODO: Read from properties file
//			client = DifidoClient.build(String.format(BASE_URI_TEMPLATE, host, port));
//			enabled = true;
//			log.fine(RemoteHtmlTestReporter.class.getName() + " was initilized successfully");
//		} catch (Throwable t) {
//			enabled = false;
//			log.fine("Failed to init " + RemoteHtmlTestReporter.class.getName() + " due to " + t.getMessage());
//		}
//		scenarioIdsBuffer = new ArrayBlockingQueue<Integer>(500);
//
//	}
//
//	private static String getMachineName() {
//		String machineName;
//		try {
//			machineName = InetAddress.getLocalHost().getHostName();
//		} catch (UnknownHostException e) {
//			machineName = "localhost";
//		}
//		return machineName;
//	}
//
//	@Override
//	public void saveFile(String fileName, byte[] content) {
//	}
//
//	@Override
//	public void report(String title, final String message, int status, boolean bold, boolean html, boolean link) {
//		if (!enabled) {
//			return;
//		}
//		if (!specialReportsElementsHandler.isValidAndHandleSpecial(title)) {
//			return;
//		}
//		ReportElement element = new ReportElement();
//		element = updateTimestampAndTitle(element, title);
//		element.setMessage(message);
//		switch (status) {
//		case 0:
//			element.setStatus(Status.success);
//			break;
//		case 1:
//			element.setStatus(Status.failure);
//			break;
//		case 2:
//			element.setStatus(Status.warning);
//			break;
//		default:
//			element.setStatus(Status.success);
//		}
//		if (bold) {
//			element.setType(ElementType.step);
//		} else if (html) {
//			element.setType(ElementType.html);
//		} else if (link) {
//			if (message.toLowerCase().endsWith("png") || message.toLowerCase().endsWith("gif")
//					|| message.toLowerCase().endsWith("jpg") || message.toLowerCase().endsWith("bmp")) {
//				// We have a image
//				element.setType(ElementType.img);
//			} else {
//				element.setType(ElementType.lnk);
//			}
//			// Getting the current test folder
//			final File currentTestFolder = new File(ListenerstManager.getInstance().getCurrentTestFolder());
//			final File[] filesToUpload = currentTestFolder.listFiles(new FilenameFilter() {
//
//				@Override
//				public boolean accept(File dir, String name) {
//					if (name.equals(message)) {
//						return true;
//					}
//					return false;
//				}
//			});
//			if (filesToUpload != null && filesToUpload.length > 0) {
//				for (File file : filesToUpload) {
//					client.addFile(executionId, machineId, scenarioIdsBuffer.peek(), testId, file);
//				}
//			}
//
//			// TODO: Check if files exists in the folder. If true, copy it to
//			// the server.
//		} else {
//			element.setType(ElementType.regular);
//		}
//		addReportElement(element);
//	}
//
//	private void addReportElement(ReportElement element) {
//		try {
//			client.addReportElement(executionId, machineId, scenarioIdsBuffer.peek(), testId, element);
//			updateTestStatusIfChanged(element.getStatus());
//		} catch (Exception e) {
//			log.warning("Failed adding report element due to " + e.getMessage());
//		}
//	}
//
//	private void updateTestStatusIfChanged(Status newStatus) {
//		final Status beforeTestStatus = currentTest.getStatus();
//		currentTest.setStatus(newStatus);
//		if (beforeTestStatus != currentTest.getStatus()) {
//			client.updateTest(executionId, machineId, scenarioIdsBuffer.peek(), testId, currentTest);
//		}
//	}
//
//	private ReportElement updateTimestampAndTitle(ReportElement element, String title) {
//		Pattern pattern = Pattern.compile("(\\d{2}:\\d{2}:\\d{2}:)");
//		Matcher matcher = pattern.matcher(title);
//		if (matcher.find()) {
//			// found time stamp in the title. Let's move it to the correct place
//			// and delete it from the title.
//			String timestamp = matcher.group(1);
//			element.setTime(timestamp);
//			element.setTitle(title.replace(timestamp, ""));
//		} else {
//			// No timestamp, let's create one
//			element.setTime(TIME_FORMAT.format(new Date()));
//			element.setTitle(title);
//		}
//		return element;
//
//	}
//
//	@Override
//	public void startSection() {
//
//	}
//
//	@Override
//	public void endSection() {
//
//	}
//
//	@Override
//	public void setData(String data) {
//
//	}
//
//	@Override
//	public void addProperty(String key, String value) {
//		if (!enabled) {
//			return;
//		}
//
//		currentTestDetails.addProperty(key, value);
//	}
//
//	@Override
//	public void setContainerProperties(int ancestorLevel, String key, String value) {
//
//	}
//
//	@Override
//	public void flush() throws Exception {
//
//	}
//
//	@Override
//	public void initReporterManager() throws IOException {
//
//	}
//
//	@Override
//	public boolean asUI() {
//		return true;
//	}
//
//	@Override
//	public void report(String title, String message, boolean isPass, boolean bold) {
//		report(title, message, isPass ? 0 : 1, bold, false, false);
//
//	}
//
//	@Override
//	public void report(String title, String message, int status, boolean bold) {
//		report(title, message, status, bold, false, false);
//
//	}
//
//	@Override
//	public String getName() {
//		return "RemoteHtmlTestReporter";
//	}
//
//	@Override
//	public void addError(Test test, Throwable t) {
//		if (!enabled) {
//			return;
//		}
//		updateTestStatusIfChanged(Status.error);
//	}
//
//	@Override
//	public void addFailure(Test test, AssertionFailedError t) {
//		if (!enabled) {
//			return;
//		}
//		updateTestStatusIfChanged(Status.failure);
//	}
//
//	@Override
//	public void endTest(Test test) {
//		if (!enabled) {
//			return;
//		}
//		currentTest.setDuration(System.currentTimeMillis() - testStartTime);
//		try {
//			// We need to update the test details again since the special report
//			// element handler may added additional properties.
//			client.updateTestDetails(executionId, machineId, scenarioIdsBuffer.peek(), testId, currentTestDetails);
//			client.updateTest(executionId, machineId, scenarioIdsBuffer.peek(), testId, currentTest);
//			client.endTest(executionId, machineId, scenarioIdsBuffer.peek(), testId);
//		} catch (Exception e) {
//			log.warning("Failed updating end of test due to " + e.getMessage());
//		}
//	}
//
//	@Override
//	public void startTest(Test test) {
//		// Unused
//	}
//
//	@Override
//	public void addWarning(Test test) {
//		if (!enabled) {
//			return;
//		}
//		updateTestStatusIfChanged(Status.warning);
//	}
//
//	@Override
//	public void startTest(TestInfo testInfo) {
//		if (!enabled) {
//			return;
//		}
//
//		String testName = testInfo.meaningfulName;
//		if (null == testName || "null".equals(testName)) {
//			testName = testInfo.methodName;
//		}
//		if (null == testName || "null".equals(testName)) {
//			testName = testInfo.basicName;
//		}
//		if (null == testName || "null".equals(testName)) {
//			testName = testInfo.className;
//		}
//		int numOfAppearances = getAndUpdateTestHistory(testName);
//		if (numOfAppearances > 0) {
//			testName += " (" + ++numOfAppearances + ")";
//		}
//		try {
//			testId = client.addTest(executionId, machineId, scenarioIdsBuffer.peek(), new TestNode(testName));
//			currentTest = client.getTest(executionId, machineId, scenarioIdsBuffer.peek(), testId);
//		} catch (Exception e) {
//			log.warning("Failed to notify on test start due to " + e.getMessage());
//			return;
//		}
//		currentTestDetails = new TestDetails(testName);
//		specialReportsElementsHandler = new SpecialReportElementsHandler(currentTestDetails);
//		testStartTime = System.currentTimeMillis();
//		currentTest.setTimestamp(TIME_FORMAT.format(new Date(testStartTime)));
//		currentTestDetails.setTimeStamp(TIME_AND_DATE_FORMAT.format(new Date(testStartTime)));
//		if (!StringUtils.isEmpty(testInfo.comment)) {
//			currentTestDetails.setDescription(testInfo.comment);
//		}
//		addPropertyIfExist(currentTestDetails, "Class", testInfo.className);
//		addPropertyIfExist(currentTestDetails, "Class Documentation", testInfo.classDoc);
//		addPropertyIfExist(currentTestDetails, "Code", testInfo.code);
//		addPropertyIfExist(currentTestDetails, "Comment", testInfo.comment);
//		addPropertyIfExist(currentTestDetails, "Test Documentation", testInfo.testDoc);
//		addPropertyIfExist(currentTestDetails, "User Documentation", testInfo.userDoc);
//		if (!StringUtils.isEmpty(testInfo.parameters)) {
//			try (Scanner scanner = new Scanner(testInfo.parameters)) {
//				while (scanner.hasNextLine()) {
//					final String parameter = scanner.nextLine();
//					currentTestDetails.addParameter(parameter.split("=")[0], parameter.split("=")[1]);
//				}
//
//			}
//		}
//
//		try {
//			client.updateTest(executionId, machineId, scenarioIdsBuffer.peek(), testId, currentTest);
//			client.addTestDetails(executionId, machineId, scenarioIdsBuffer.peek(), testId, currentTestDetails);
//		} catch (Exception e) {
//			log.warning("Failed notifying about test start due to " + e.getMessage());
//		}
//
//	}
//
//	private int getAndUpdateTestHistory(final Object bb) {
//		if (testCounter == null) {
//			testCounter = new HashMap<>();
//		}
//		final int key = bb.hashCode();
//		if (testCounter.containsKey(key)) {
//			testCounter.put(key, testCounter.get(key) + 1);
//		} else {
//			testCounter.put(key, 0);
//		}
//		return testCounter.get(key);
//	}
//
//	private static void addPropertyIfExist(TestDetails details, String propertyName, String property) {
//		if (!StringUtils.isEmpty(property)) {
//			details.addProperty(propertyName, property);
//		}
//	}
//
//	@Override
//	public void endRun() {
//		if (!appendToExistingExecution) {
//			client.endExecution(executionId);
//		}
//	}
//
//	@Override
//	public void startLoop(AntForLoop loop, int count) {
//
//	}
//
//	@Override
//	public void endLoop(AntForLoop loop, int count) {
//
//	}
//
//	@Override
//	public void startContainer(JTestContainer container) {
//		if (!enabled) {
//			return;
//		}
//		String scenarioName = ScenarioHelpers.removeScenarioHeader(container.getName());
//		int scenarioId = -1;
//		if (container.isRoot()) {
//			try {
//				if (appendToExistingExecution) {
//					executionId = client.getLastExecutionId();
//				} else {
//					executionId = client.addExecution();
//				}
//				machineId = client.addMachine(executionId, new MachineNode(getMachineName()));
//				// If the scenario appeared more then once we want to append the
//				// number of appearences to the scnenario name
//				int numOfAppearances = getAndUpdateTestHistory(container.getName());
//				if (numOfAppearances > 0) {
//					scenarioName = (scenarioName + " (" + ++numOfAppearances + ")");
//				}
//				scenarioId = client.addRootScenario(executionId, machineId, new ScenarioNode(scenarioName));
//				enabled = true;
//			} catch (Throwable t) {
//				log.fine("Failed to start new execution due to " + t.getMessage());
//				enabled = false;
//			}
//
//		} else {
//			scenarioId = client.addSubScenario(executionId, machineId, scenarioIdsBuffer.peek(), new ScenarioNode(
//					scenarioName));
//		}
//		scenarioIdsBuffer.add(scenarioId);
//
//	}
//
//	@Override
//	public void endContainer(JTestContainer container) {
//		if (!enabled) {
//			return;
//		}
//
//		scenarioIdsBuffer.remove();
//
//	}
//
//	@Override
//	public void startLevel(String level, EnumReportLevel place) throws IOException {
//		ReportElement element = new ReportElement();
//		element.setTime(TIME_FORMAT.format(new Date()));
//		element.setTitle(level);
//		element.setType(ElementType.startLevel);
//		addReportElement(element);
//
//	}
//
//	@Override
//	public void startLevel(String levelName, int place) throws IOException {
//		startLevel(levelName, null);
//
//	}
//
//	@Override
//	public void stopLevel() {
//		ReportElement element = new ReportElement();
//		element.setTime(TIME_FORMAT.format(new Date()));
//		element.setType(ElementType.stopLevel);
//		addReportElement(element);
//
//	}
//
//	@Override
//	public void closeAllLevels() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void closeLevelsUpTo(String levelName, boolean includeLevel) {
//		// TODO Auto-generated method stub
//
//	}
//
//}
