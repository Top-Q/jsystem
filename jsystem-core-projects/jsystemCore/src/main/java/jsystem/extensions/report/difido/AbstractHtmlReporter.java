package jsystem.extensions.report.difido;

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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.extensions.report.html.ExtendLevelTestReporter;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.RunProperties;
import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter.EnumReportLevel;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.ScenarioHelpers;
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

	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss:");

	private static final SimpleDateFormat TIME_AND_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm:ss");

	private Execution execution;

	private ScenarioNode currentScenario;

	private TestDetails testDetails;

	private HashMap<Integer, Integer> testCounter;

	private TestNode currentTest;

	private int index;

	private long testStartTime;

	private SpecialReportElementsHandler specialReportsElementsHandler;

	private boolean deleteCurrent = true;

	private String executionUid;

	protected abstract void writeTestDetails(TestDetails testDetails);

	protected abstract void writeExecution(Execution execution);

	protected abstract Execution readExecution();

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

	private ReportElement updateTimestampAndTitle(ReportElement element, String title) {
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
		testDetails.addReportElement(element);
		writeTestDetails(testDetails);

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

		addMachineToExecution();
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
	private void addMachineToExecution() {
		MachineNode currentMachine = new MachineNode(getMachineName());
		if (null == execution) {
			execution = new Execution();
			execution.addMachine(currentMachine);
			return;
		}
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

	private static String getMachineName() {
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
		currentTest.setStatus(Status.error);
	}

	@Override
	public void addFailure(Test arg0, AssertionFailedError arg1) {
		currentTest.setStatus(Status.failure);

	}

	@Override
	public void endTest(Test arg0) {
		currentTest.setDuration(System.currentTimeMillis() - testStartTime);
		writeTestDetails(testDetails);
	}

	@Override
	public void startTest(Test arg0) {
		// Not used
	}

	@Override
	public void addWarning(Test test) {
		currentTest.setStatus(Status.warning);
	}

	private void addPropertyIfExist(String propertyName, String property) {
		if (!StringUtils.isEmpty(property)) {
			testDetails.addProperty(propertyName, property);
		}
	}

	@Override
	public void startTest(TestInfo testInfo) {
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
		currentTest = new TestNode(index++, testName, executionUid + "-" + index);
		testStartTime = System.currentTimeMillis();
		currentTest.setTimestamp(TIME_FORMAT.format(new Date(testStartTime)));
		currentScenario.addChild(currentTest);
		testDetails = new TestDetails(testName, currentTest.getUid());
		testDetails.setTimeStamp(TIME_AND_DATE_FORMAT.format(new Date(testStartTime)));
		if (!StringUtils.isEmpty(testInfo.comment)) {
			testDetails.setDescription(testInfo.comment);
		}
		addPropertyIfExist("Class", testInfo.className);
		addPropertyIfExist("Class Documentation", testInfo.classDoc);
		addPropertyIfExist("Code", testInfo.code);
		addPropertyIfExist("Comment", testInfo.comment);
		addPropertyIfExist("Test Documentation", testInfo.testDoc);
		addPropertyIfExist("User Documentation", testInfo.userDoc);
		if (!StringUtils.isEmpty(testInfo.parameters)) {
			try (Scanner scanner = new Scanner(testInfo.parameters)) {
				while (scanner.hasNextLine()) {
					final String parameter = scanner.nextLine();
					testDetails.addParameter(parameter.split("=")[0], parameter.split("=")[1]);
				}

			}
		}
		int numOfAppearances = getAndUpdateTestHistory(testDetails);
		if (numOfAppearances > 0) {
			currentTest.setName(currentTest.getName() + " (" + ++numOfAppearances + ")");
		}
		updateTestDirectory();
		writeExecution(execution);
		writeTestDetails(testDetails);
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
		currentScenario = (ScenarioNode) currentScenario.getParent();
	}

	@Override
	public void startContainer(JTestContainer container) {
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

	private void addScenarioProperties(ScenarioNode scenario) {
		// We will also add all the different execution properties as
		// container properties
		try {
			final String version = RunProperties.getInstance().getRunProperty("summary.Version");
			if (!StringUtils.isEmpty(version)) {
				scenario.addScenarioProperty("version", version);
			}
			final String build = RunProperties.getInstance().getRunProperty("summary.Build");
			if (!StringUtils.isEmpty(build)) {
				scenario.addScenarioProperty("build", build);
			}
			final String station = RunProperties.getInstance().getRunProperty("summary.Station");
			if (!StringUtils.isEmpty(station)) {
				scenario.addScenarioProperty("station", station);
			}
			final String user = RunProperties.getInstance().getRunProperty("summary.User");
			if (!StringUtils.isEmpty(user)) {
				scenario.addScenarioProperty("user", user);
			}
			final String sutFile = JSystemProperties.getInstance().getPreference(FrameworkOptions.USED_SUT_FILE);
			if (!StringUtils.isEmpty(sutFile)) {
				scenario.addScenarioProperty("sutFile", sutFile);
			}
			final String testDir = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER);
			if (!StringUtils.isEmpty(testDir)) {
				scenario.addScenarioProperty("testDir", testDir);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void endContainer(JTestContainer container) {
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
		testDetails.addProperty(key, value);
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
				testDetails.addProperty("Class Documentation", title);
				testDetails.setDescription(title);
				elementData = NONE;
				return false;
			case TEST_DOC:
				testDetails.addProperty("Test Documentation", title);
				testDetails.setDescription(title);
				elementData = NONE;
				return false;
			case USER_DOC:
				testDetails.addProperty("User Documentation", title);
				testDetails.setDescription(title);
				elementData = NONE;
				return false;
			case TEST_BREADCUMBS:
				testDetails.addProperty("Breadcrumb", title.replace("</span>", ""));
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

	protected TestDetails getTestDetails() {
		return testDetails;
	}

}
