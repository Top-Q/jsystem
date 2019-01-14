package jsystem.extensions.report.difido;

import java.util.Date;
import java.util.logging.Logger;

import il.co.topq.difido.model.Enums.ElementType;
import il.co.topq.difido.model.Enums.Status;
import il.co.topq.difido.model.execution.Execution;
import il.co.topq.difido.model.execution.ScenarioNode;
import il.co.topq.difido.model.execution.TestNode;
import il.co.topq.difido.model.test.ReportElement;
import il.co.topq.difido.model.test.TestDetails;
import jsystem.extensions.report.difido.DifidoConfig.DifidoProperty;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

public class SingleTestRemoteHtmlReporter extends RemoteHtmlReporter {
	private static final Logger log = Logger.getLogger(SingleTestRemoteHtmlReporter.class.getName());

	private static final int MAX_VALUE_LENGTH = 64;

	private TestDetails singleTestDetails;

	private TestNode singleTest;

	private Execution singleExecution;

	private ScenarioNode singleScenario;

	public SingleTestRemoteHtmlReporter() {
		init();
	}

	@Override
	public void startTest(TestInfo testInfo) {
		super.startTest(testInfo);
		ReportElement element = new ReportElement();
		element.setType(ElementType.startLevel);
		element = updateTimestampAndTitle(element, getCurrentTest().getName());
		addReportElement(element);

		if (getCurrentTest().getParameters() != null) {
			element = new ReportElement();
			element.setType(ElementType.startLevel);
			element = updateTimestampAndTitle(element, "Parameters");
			addReportElement(element);
			for (String parameter : getCurrentTest().getParameters().keySet()) {
				String value = getCurrentTest().getParameters().get(parameter);
				if (value.length() > MAX_VALUE_LENGTH) {
					value = value.substring(0, MAX_VALUE_LENGTH) + "...";
				}
				element = new ReportElement();
				element.setType(ElementType.regular);
				element = updateTimestampAndTitle(element, parameter + " = " + value);
				addReportElement(element);
			}
			element = new ReportElement();
			element.setType(ElementType.stopLevel);
			element = updateTimestampAndTitle(element, "");
			addReportElement(element);

		}

		if (getCurrentTest().getDescription() != null) {
			element = new ReportElement();
			element.setType(ElementType.regular);
			element = updateTimestampAndTitle(element, getCurrentTest().getDescription());
			addReportElement(element);
		}

	}

	@Override
	public void endTest(Test arg0) {
		super.endTest(arg0);
		singleTest.setDuration(singleTest.getDuration() + getCurrentTest().getDuration());
		ReportElement element = new ReportElement();
		element.setType(ElementType.stopLevel);
		addReportElement(element);
	}

	@Override
	public void startContainer(JTestContainer container) {
		if (container.isRoot()) {
			initModel();
			initSingleModel();
			startSingleScenario(container);
			startSingleTest(container);
		}
		super.startContainer(container);
		if (!container.isRoot()) {
			ReportElement element = new ReportElement();
			element.setType(ElementType.startLevel);
			element = updateTimestampAndTitle(element, getCurrentScenario().getName());
			addReportElement(element);
		}
	}

	private void startSingleScenario(JTestContainer container) {
		singleScenario = new ScenarioNode(container.getName());
		singleExecution.getLastMachine().addChild(singleScenario);
	}

	@Override
	public void endContainer(JTestContainer container) {
		ReportElement element = new ReportElement();
		element.setType(ElementType.stopLevel);
		element = updateTimestampAndTitle(element, "");
		addReportElement(element);
	}

	private void startSingleTest(JTestContainer container) {
		singleTest = new TestNode(1, container.getName(), getExecutionUid() + "-1");
		long startTime = System.currentTimeMillis();
		singleTest.setTimestamp(TIME_FORMAT.format(new Date(startTime)));
		singleTest.setDate(DATE_FORMAT.format(new Date(startTime)));
		singleScenario.addChild(singleTest);
		singleTestDetails = new TestDetails(getExecutionUid() + "-1");
	}

	@Override
	protected void addReportElement(ReportElement element) {
		singleTestDetails.addReportElement(element);
		super.addReportElement(element);
	}

	public void initSingleModel() {
		singleExecution = new Execution();
		addMachineToExecution(singleExecution);
		if (JSystemProperties.getInstance().isExecutedFromIDE()) {
			// We are running from the IDE, so there will be no scenario
			singleScenario = new ScenarioNode("default");
			singleExecution.getLastMachine().addChild(singleScenario);
		} else {
			singleScenario = null;
		}
		singleScenario = null;
		// NOTE: DO NOT CALL TO writeExecution() AT THIS STAGE. Writing the
		// execution here will cause the last execution to be deleted from the
		// Difido server
	}

	@Override
	public void addError(Test arg0, Throwable arg1) {
		log.fine("Received error event");
		if (DifidoConfig.getInstance().getBoolean(DifidoProperty.ERRORS_TO_FAILURES)) {
			// We don't want errors in the report, so we will change each error
			// to failure.
			singleTest.setStatus(Status.failure);
		} else {
			singleTest.setStatus(Status.error);
		}
	}

	@Override
	public void addFailure(Test arg0, AssertionFailedError arg1) {
		log.fine("Received failure event");
		singleTest.setStatus(Status.failure);
	}

	@Override
	public String getName() {
		return "DifidoRemoteSingleTestHtmlReporter";
	}

	@Override
	protected void updateTestDirectory() {
		// Unused
	}

	@Override
	protected void writeTestDetails(TestDetails testDetails) {
		super.writeTestDetails(this.singleTestDetails);
	}

	@Override
	protected void writeExecution(Execution execution) {
		super.writeExecution(this.singleExecution);
	}

	@Override
	protected int calculateNumberOfPlannedTests() {
		return 1;
	}

}
