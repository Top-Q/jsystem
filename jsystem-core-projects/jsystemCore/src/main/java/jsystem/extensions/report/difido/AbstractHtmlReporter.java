package jsystem.extensions.report.difido;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import il.co.topq.difido.model.Enums.ElementType;
import il.co.topq.difido.model.Enums.Status;
import il.co.topq.difido.model.execution.Execution;
import il.co.topq.difido.model.execution.MachineNode;
import il.co.topq.difido.model.execution.Node;
import il.co.topq.difido.model.execution.NodeWithChildren;
import il.co.topq.difido.model.execution.ScenarioNode;
import il.co.topq.difido.model.execution.TestNode;
import il.co.topq.difido.model.test.ReportElement;
import il.co.topq.difido.model.test.TestDetails;
import jsystem.extensions.report.difido.DifidoConfig.DifidoProperty;
import jsystem.extensions.report.html.ExtendLevelTestReporter;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter.EnumReportLevel;
import jsystem.framework.report.Summary;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.scenario.flow_control.AntFlowControl;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.utils.StringUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

/**
 * 
 * @author Itai Agmon
 * 
 */
public abstract class AbstractHtmlReporter implements ExtendLevelTestReporter, ExtendTestListener {

	private static final Logger log = Logger.getLogger(AbstractHtmlReporter.class.getName());

	protected static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

	private Execution execution;

	private ScenarioNode currentScenario;

	protected TestDetails testDetails;

	private HashMap<Integer, Integer> testCounter;

	private TestNode currentTest;

	private int index;

	private long testStartTime;

	private SpecialReportElementsHandler specialReportsElementsHandler;

	private boolean deleteCurrent = true;

	private String executionUid;

	private boolean firstTest = true;

	protected long lastMessageTime;

	protected abstract void writeTestDetails(TestDetails testDetails);

	protected abstract void writeExecution(Execution execution);

	protected abstract Execution readExecution();

	@Override
	public void init() {
		firstTest = true;
	}

	@Override
	public boolean asUI() {
		return true;
	}

	@Override
	public void report(String title, String message, boolean isPass, boolean bold) {
		report(title, message, isPass ? 0 : 1, bold, false, false);
	}

	@Override
	public void report(String title, String message, int status, boolean bold) {
		report(title, message, status, bold, false, false);
	}

	protected ReportElement updateTimestampAndTitle(ReportElement element, String title) {
		Pattern pattern = Pattern.compile("(\\d{2}:\\d{2}:\\d{2}:)");
		Matcher matcher = pattern.matcher(title);
		if (matcher.find()) {
			// found time stamp in the title. Let's move it to the correct place
			// and delete it from the title.
			String timestamp = matcher.group(1);
			element.setTime(timestamp);
			element.setTitle(title.replace(timestamp, ""));
		} else {
			// No timestamp, let's create one
			element.setTime(TIME_FORMAT.format(new Date()));
			element.setTitle(title);
		}
		return element;

	}

	@Override
	public void report(String title, final String message, int status, boolean bold, boolean html, boolean link) {
		log.fine("Recieved report request with title '" + title + "'");
		if (null == specialReportsElementsHandler) {
			// This never suppose to happen, since it was initialized in the
			// start test event.
			specialReportsElementsHandler = new SpecialReportElementsHandler();
		}
		if (!specialReportsElementsHandler.isValidAndHandleSpecial(title)) {
			return;
		}
		ReportElement element = new ReportElement();
		element = updateTimestampAndTitle(element, title);
		element.setMessage(message);
		switch (status) {
		case 0:
			element.setStatus(Status.success);
			break;
		case 1:
			element.setStatus(Status.failure);
			break;
		case 2:
			element.setStatus(Status.warning);
			break;
		default:
			element.setStatus(Status.success);
		}
		if (bold) {
			element.setType(ElementType.step);
		} else if (html) {
			element.setType(ElementType.html);
		} else if (link) {
			if (message.toLowerCase().endsWith("png") || message.toLowerCase().endsWith("gif")
					|| message.toLowerCase().endsWith("jpg") || message.toLowerCase().endsWith("bmp")) {
				// We have a image
				element.setType(ElementType.img);
			} else {
				element.setType(ElementType.lnk);
			}
			final File[] filesToUpload = getAddedFiles(message);
			if (filesToUpload != null && filesToUpload.length > 0) {
				filesWereAddedToReport(filesToUpload);
			}

		} else {
			element.setType(ElementType.regular);
		}
		addReportElement(element);
		if (System.currentTimeMillis() - lastMessageTime <= DifidoConfig.getInstance()
				.getLong(DifidoProperty.MIN_INTERVAL_BETWEEN_MESSAGES)) {
			// We want to make sure the test does not stress the IO with
			// messages. Issue #271
			return;
		}
		lastMessageTime = System.currentTimeMillis();
		writeTestDetails(testDetails);

	}

	protected void addReportElement(ReportElement element) {
		testDetails.addReportElement(element);
	}

	private File[] getAddedFiles(final String message) {
		// Getting the current test folder
		final File currentTestFolder = new File(ListenerstManager.getInstance().getCurrentTestFolder());
		final File[] filesToUpload = currentTestFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.equals(message)) {
					return true;
				}
				return false;
			}
		});
		return filesToUpload;
	}

	protected abstract void filesWereAddedToReport(File[] files);

	public AbstractHtmlReporter() {
		init();
	}

	public void initModel() {
		if (deleteCurrent) {
			execution = null;
		} else {
			execution = readExecution();
		}
		updateIndex();
		generateUid();
		execution = new Execution();
		addMachineToExecution(execution);
		if (JSystemProperties.getInstance().isExecutedFromIDE()) {
			// We are running from the IDE, so there will be no scenario
			currentScenario = new ScenarioNode("default");
			execution.getLastMachine().addChild(currentScenario);
		} else {
			currentScenario = null;
		}
		currentTest = null;
		// NOTE: DO NOT CALL TO writeExecution() AT THIS STAGE. Writing the
		// execution here will cause the last execution to be deleted from the
		// Difido server
	}

	protected void generateUid() {
		executionUid = String.valueOf(new Random().nextInt(1000)) + String.valueOf(System.currentTimeMillis() / 1000);
	}

	/**
	 * If no execution exists. Meaning, we are not appending to an older
	 * execution; A new execution would be created. If the execution is new,
	 * will add a new reported machine instance. If we are appending to an older
	 * execution, and the machine is the same as the machine the execution were
	 * executed on, will append the results to the last machine and will not
	 * create a new one.
	 * 
	 */
	protected void addMachineToExecution(Execution execution) {
		if (null == execution) {
			throw new NullPointerException("Execution object can't be null");
		}
		MachineNode currentMachine = new MachineNode(getMachineName());
		// We are going to append to existing execution
		MachineNode lastMachine = execution.getLastMachine();
		if (null == lastMachine || null == lastMachine.getName()) {
			// Something is wrong. We don't have machine in the existing
			// execution. We need to add a new one
			execution.addMachine(currentMachine);
			return;
		}
		if (!lastMachine.getName().equals(currentMachine.getName())) {
			// The execution happened on machine different from the current
			// machine, so we will create a new machine
			execution.addMachine(currentMachine);
		}

	}

	protected int calculateNumberOfPlannedTests() {
		final Scenario scenario = ScenariosManager.getInstance().getCurrentScenario();
		if (null == scenario) {
			return 0;
		}
		int[] enabledTestsIds = scenario.getEnabledTestsIndexes();
		if (null == enabledTestsIds) {
			return 0;
		}
		return enabledTestsIds.length;
	}

	private void updateIndex() {
		if (null == execution) {
			index = 0;
			return;
		}
		if (execution.getMachines() == null || execution.getMachines().size() == 0) {
			index = 0;
			return;
		}
		for (MachineNode machine : execution.getMachines()) {
			for (Node child : machine.getChildren(true)) {
				if (!(child instanceof NodeWithChildren)) {
					index++;
				}
			}
		}
	}

	protected static String getMachineName() {
		String machineName;
		try {
			machineName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			machineName = "localhost";
		}
		return machineName;
	}

	@Override
	public void addError(Test arg0, Throwable arg1) {
		log.fine("Received error event");
		if (DifidoConfig.getInstance().getBoolean(DifidoProperty.ERRORS_TO_FAILURES)) {
			// We don't want errors in the report, so we will change each error
			// to failure.
			currentTest.setStatus(Status.failure);
		} else {
			currentTest.setStatus(Status.error);
		}
		updateCalculatedNumberOfTestsDueToFailure();
	}

	@Override
	public void addFailure(Test arg0, AssertionFailedError arg1) {
		log.fine("Received failure event");
		currentTest.setStatus(Status.failure);
		updateCalculatedNumberOfTestsDueToFailure();
	}

	/**
	 * If the test fails it will be shown in the HTML even if it set as hidden
	 * in HTML, So will add it back to the number of tests in the machine
	 * 
	 */
	protected void updateCalculatedNumberOfTestsDueToFailure() {
		if (currentTest.isHideInHtml()) {
			execution.getLastMachine().setPlannedTests(execution.getLastMachine().getPlannedTests() + 1);
		}
	}

	@Override
	public void endTest(Test arg0) {
		log.fine("Received end test");
		currentTest.setDuration(System.currentTimeMillis() - testStartTime);
		// In case scenario properties were added during the test run, we want
		// also to add them to the scenario model
		addScenarioProperties(currentScenario);
		writeTestDetails(testDetails);
	}

	@Override
	public void startTest(Test arg0) {
		// Not used
	}

	@Override
	public void addWarning(Test test) {
		log.fine("Received warning event");
		currentTest.setStatus(Status.warning);
	}

	private void addPropertyIfExist(String propertyName, String property) {
		if (!StringUtils.isEmpty(property)) {
			currentTest.addProperty(propertyName, property);
		}
	}

	@Override
	public void startTest(TestInfo testInfo) {
		if (firstTest) {
			// If we run from IDE, we will not receive startContainer event, so
			// we will need to call the startRun here. Issue #269
			firstTest = false;
			startRun();
		}
		log.fine("Recieved start test event");
		specialReportsElementsHandler = new SpecialReportElementsHandler();
		String testName = testInfo.meaningfulName;
		if (null == testName || "null".equals(testName)) {
			testName = testInfo.methodName;
		}
		if (null == testName || "null".equals(testName)) {
			testName = testInfo.basicName;
		}
		if (null == testName || "null".equals(testName)) {
			testName = testInfo.className;
		}
		log.fine("Test name is " + testName);
		currentTest = new TestNode(index++, testName, executionUid + "-" + index);
		currentTest.setClassName(testInfo.className);
		testStartTime = System.currentTimeMillis();
		currentTest.setTimestamp(TIME_FORMAT.format(new Date(testStartTime)));
		currentTest.setDate(DATE_FORMAT.format(new Date(testStartTime)));
		if (testInfo.isHiddenInHTML) {
			currentTest.setHideInHtml(testInfo.isHiddenInHTML);
			execution.getLastMachine().setPlannedTests(execution.getLastMachine().getPlannedTests() - 1);

		}
		currentScenario.addChild(currentTest);
		testDetails = new TestDetails(currentTest.getUid());
		if (!StringUtils.isEmpty(testInfo.comment)) {
			currentTest.setDescription(testInfo.comment);
		}
		addPropertyIfExist("Class", testInfo.className);
		addPropertyIfExist("Class Documentation", testInfo.classDoc);
		addPropertyIfExist("Code", testInfo.code);
		addPropertyIfExist("Comment", testInfo.comment);
		addPropertyIfExist("Test Documentation", testInfo.testDoc);
		addPropertyIfExist("User Documentation", testInfo.userDoc);
		if (!StringUtils.isEmpty(testInfo.parameters)) {
			log.info("Adding parameters " + testInfo.parameters);
			try (Scanner scanner = new Scanner(testInfo.parameters)) {
				while (scanner.hasNextLine()) {
					final String parameter = scanner.nextLine();
					if (!parameter.contains("=")) {
						log.warning("There is an illegal parameter '" + parameter + "' in test " + testName);
						continue;
					}
					// We are searching only for the first '"' since that in
					// parameters providers there are usually more then one '"'
					// sign if we will just split on the sign, we will lose a
					// lot of the parameter value.
					int equalsIndex = parameter.indexOf("=");
					String key = parameter.substring(0, equalsIndex);
					String value = parameter.substring(equalsIndex + 1);
					addParameterToCurrentTest(key, value);
				}

			}
		}
		int numOfAppearances = getAndUpdateTestHistory(testDetails);
		if (numOfAppearances > 0) {
			currentTest.setName(currentTest.getName() + " (" + ++numOfAppearances + ")");
		}
		try {
			updateTestDirectory();
		} catch (Throwable t) {
			log.severe("Failed updating test directory due to " + t.getMessage());
		}
		try {
			writeExecution(execution);
		} catch (Throwable t) {
			log.severe("Failed writing execution due to " + t.getMessage());
		}
		try {
			writeTestDetails(testDetails);
		} catch (Throwable t) {
			log.severe("Failed writing test details due to " + t.getMessage());
		}
	}

	/**
	 * Adding parameter to the current test. <br>
	 * The importance of this method is that JSystem is adding additional
	 * backslash to special characters like : = # \ .<br>
	 * Since we don't want to see the additional backslashes in the report we
	 * are cleaning them before adding them as parameter to the current test.
	 * 
	 * 
	 * @param key
	 * @param value
	 */
	private void addParameterToCurrentTest(String key, String value) {
		// The regular expression will search for any backslash that has a
		// special character after it or another backslash but do not have a
		// backslash before and will replace it with nothing.
		String noarmalizedValue = value.replaceAll("(?<!\\\\)\\\\(?=[:!=#\\\\])", "");
		currentTest.addParameter(key, noarmalizedValue);
	}

	/**
	 * This method is meant to be override. It is called at the start of the run
	 */
	public void startRun() {
		execution.getLastMachine().setPlannedTests(calculateNumberOfPlannedTests());
	}

	/**
	 * This method will be called at the beginning of each test. If the reporter
	 * is using the file system, it is responsible for updating the current test
	 * folder in the '.testdir.tmp' file. See the HtmlReporter implementation
	 * for example.
	 */
	protected abstract void updateTestDirectory();

	private int getAndUpdateTestHistory(final Object bb) {
		if (testCounter == null) {
			testCounter = new HashMap<>();
		}
		final int key = bb.hashCode();
		if (testCounter.containsKey(key)) {
			testCounter.put(key, testCounter.get(key) + 1);
		} else {
			testCounter.put(key, 0);
		}
		return testCounter.get(key);
	}

	@Override
	public void endRun() {
		firstTest = true;
		writeExecution(execution);
	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		ScenarioNode scenario = new ScenarioNode(loop.getTestName(count));
		currentScenario.addChild(scenario);
		currentScenario = scenario;
		writeExecution(execution);

	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
		log.fine("Recieved end loop event");
		currentScenario = (ScenarioNode) currentScenario.getParent();
	}

	@Override
	public void startContainer(JTestContainer container) {
		log.fine("Recieved start containter event");
		if (firstTest) {
			firstTest = false;
			startRun();
		}
		ScenarioNode scenario = new ScenarioNode(ScenarioHelpers.removeScenarioHeader(container.getName()));
		if (container.isRoot()) {
			// We keep scenario history only for the root scenario;
			int numOfAppearances = getAndUpdateTestHistory(container.getName());
			if (numOfAppearances > 0) {
				scenario.setName(scenario.getName() + " (" + ++numOfAppearances + ")");
			}
			addScenarioProperties(scenario);
			execution.getLastMachine().addChild(scenario);
		} else {
			if (container instanceof AntForLoop) {
				scenario.setName(((AntForLoop) container).getTestName(0));
			} else if (container instanceof AntFlowControl) {
				scenario.setName(container.getTestName());
			}
			currentScenario.addChild(scenario);
		}
		currentScenario = scenario;
		writeExecution(execution);

	}

	/**
	 * Adding the scenario properties to the newly created scenario. <br>
	 * Override this method if you would your reporter to add additional
	 * properties to the scenario.
	 * 
	 * @param scenario
	 *            Newly created scenario.
	 */
	protected void addScenarioProperties(ScenarioNode scenario) {
		// Adding the summary properties. Those also include the run properties
		// that have a 'summary' prefix
		final Properties summaryProperties = Summary.getInstance().getProperties();
		for (Object key : summaryProperties.keySet()) {
			final String value = summaryProperties.getProperty(key + "");
			if (!StringUtils.isEmpty(value)) {
				scenario.addScenarioProperty(key.toString(), value);
			}
		}
		// We are adding some additional information that we may find
		// interesting
		final String sutFile = JSystemProperties.getInstance().getPreference(FrameworkOptions.USED_SUT_FILE);
		if (!StringUtils.isEmpty(sutFile)) {
			scenario.addScenarioProperty("sutFile", sutFile);
		}
		final String testDir = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER);
		if (!StringUtils.isEmpty(testDir)) {
			scenario.addScenarioProperty("testDir", testDir);
		}
	}

	@Override
	public void endContainer(JTestContainer container) {
		log.fine("Recieved end container event");
		if (currentScenario.getParent() instanceof ScenarioNode) {
			currentScenario = (ScenarioNode) currentScenario.getParent();

		}

	}

	@Override
	public void saveFile(String fileName, byte[] content) {

	}

	@Override
	public void startSection() {
	}

	@Override
	public void endSection() {
	}

	@Override
	public void setData(String data) {
	}

	@Override
	public void addProperty(String key, String value) {
		currentTest.addProperty(key, value);
	}

	@Override
	public void setContainerProperties(final int ancestorLevel, String key, String property) {
		if (null == currentScenario) {
			throw new IllegalStateException("Current scenario is null. Can't add container property");
		}
		ScenarioNode scenario = currentScenario;
		int level = ancestorLevel;
		if (ancestorLevel > 0) {
			while (!(scenario.getParent() instanceof MachineNode) && (level-- > 0)) {
				scenario = (ScenarioNode) scenario.getParent();
			}
		}
		scenario.addScenarioProperty(key, property);
	}

	@Override
	public void flush() throws Exception {
	}

	@Override
	public void startLevel(String level, EnumReportLevel place) throws IOException {
		log.fine("Recieved start level event");
		ReportElement element = new ReportElement();
		element.setTime(TIME_FORMAT.format(new Date()));
		element.setTitle(level);
		element.setType(ElementType.startLevel);
		testDetails.addReportElement(element);
	}

	@Override
	public void startLevel(String levelName, int place) throws IOException {
		startLevel(levelName, null);
	}

	@Override
	public void stopLevel() {
		log.fine("Recieved stop level event");
		ReportElement element = new ReportElement();
		element.setTime(TIME_FORMAT.format(new Date()));
		element.setType(ElementType.stopLevel);
		testDetails.addReportElement(element);
	}

	@Override
	public void closeAllLevels() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeLevelsUpTo(String levelName, boolean includeLevel) {
		// TODO Auto-generated method stub

	}

	public boolean isDeleteCurrent() {
		return deleteCurrent;
	}

	public void setDeleteCurrent(boolean deleteCurrent) {
		this.deleteCurrent = deleteCurrent;
	}

	/**
	 * Since JSystem creates a few annoying elements that are messed with HTML
	 * elements, and since some important information like the class
	 * documentation is not in the testInfo but received as a regular report
	 * element, there is a need for a class to handle all the unusual elements.
	 * 
	 * @author Itai Agmon
	 * 
	 */
	private class SpecialReportElementsHandler {

		private final static String SPAN_OPEN_TAG = "<span class=";
		private final static String SPAN_CLOSE_TAG = "</span>";
		private final static String SPAN_OPEN_CLASS_DOC_TAG = "<span class=\"class_doc\">";
		private final static String SPAN_OPEN_TEST_DOC_TAG = "<span class=\"test_doc\">";
		private final static String SPAN_OPEN_USER_DOC_TAG = "<span class=\"user_doc\">";
		private final static String SPAN_OPEN_BREADCRUMBS_TAG = "<span class=\"test_breadcrumbs\">";

		private final static int NONE = 0;
		private final static int USER_DOC = 1;
		private final static int CLASS_DOC = 2;
		private final static int TEST_DOC = 3;
		private final static int TEST_BREADCUMBS = 4;

		private int elementData = NONE;
		private int spanTrace;
		private boolean skipReportElement;

		/**
		 * We don't want to add the span class in the title, so we filter it. We
		 * also add all kind of important information that exists inside the
		 * span, like the user doc and such directly to the test details.
		 * 
		 * @param title
		 * @return true of valid element that should be added to the test
		 *         details.
		 */
		boolean isValidAndHandleSpecial(String title) {
			if (skipReportElement) {
				skipReportElement = false;
				return false;
			}

			switch (elementData) {
			case NONE:
				break;
			case CLASS_DOC:
				currentTest.addProperty("Class Documentation", title);
				elementData = NONE;
				return false;
			case TEST_DOC:
				currentTest.addProperty("Test Documentation", title);
				currentTest.setDescription(title);
				elementData = NONE;
				return false;
			case USER_DOC:
				currentTest.addProperty("User Documentation", title);
				currentTest.setDescription(title);
				elementData = NONE;
				return false;
			case TEST_BREADCUMBS:
				currentTest.addProperty("Breadcrumb", title.replace("</span>", ""));
				elementData = NONE;
				// This also closes the span
				spanTrace--;
				return false;
			default:
				break;
			}
			if (StringUtils.isEmpty(title)) {
				return false;
			}
			if (title.contains(SPAN_OPEN_TAG)) {
				// ITAI: This is a ugly hack, When we execute from the IDE there
				// is a missing span close tag, so we
				// Never increase the number of the span trace above one.
				if (!(JSystemProperties.getInstance().isExecutedFromIDE() && spanTrace == 1)) {
					spanTrace++;
				}
			}
			if (spanTrace > 0) {
				// In span, let's search for that special elements
				switch (title) {
				case SPAN_OPEN_CLASS_DOC_TAG:
					elementData = CLASS_DOC;
					skipReportElement = true;
					break;
				case SPAN_OPEN_TEST_DOC_TAG:
					elementData = TEST_DOC;
					skipReportElement = true;
					break;
				case SPAN_OPEN_USER_DOC_TAG:
					elementData = USER_DOC;
					skipReportElement = true;
					break;
				case SPAN_OPEN_BREADCRUMBS_TAG:
					elementData = TEST_BREADCUMBS;
					break;
				}
			}
			if (title.contains(SPAN_CLOSE_TAG)) {
				spanTrace--;
				return false;
			}

			// ITAI: When running from the IDE, there are missing span closing
			// tags, so we do not increase the span trace after level one. The
			// result is that the span trace may have a negative value
			return spanTrace <= 0;
		}
	}

	protected TestNode getCurrentTest() {
		return currentTest;
	}

	protected Execution getExecution() {
		return execution;
	}

	protected ScenarioNode getCurrentScenario() {
		return currentScenario;
	}

	protected TestDetails getTestDetails() {
		return testDetails;
	}

	protected int getIndex() {
		return index;
	}

	protected String getExecutionUid() {
		return executionUid;
	}

}
