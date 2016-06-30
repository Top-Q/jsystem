/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.agent.client;

import org.junit.Ignore;

import jsystem.framework.report.ExecutionListener;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.runner.ErrorLevel;
import junit.framework.AssertionFailedError;
import junit.framework.SystemTestCase;
import junit.framework.Test;

public class MyListener extends SystemTestCase implements ExecutionListener{

	private int testsThatFailed=0;
	private int testsWithWarning=0;
	private int testsThatThrowException=0;
	private int totalTests=0; 


	@Override
	public void errorOccured(String title, String message, ErrorLevel level) {
		report.report("****************errorOccured**********");
	}

	@Override//works
	public void executionEnded(String scenarioName) {
		report.report("****************executionEnded**********");
	}

	@Override
	public void remoteExit() {
		report.report("****************remoteExit**********");
	}

	@Override
	public void remotePause() {
		report.report("****************remotePause**********");
	}

	@Override
	public void addWarning(Test test) {
		report.report("****************addWarning**********");
		testsWithWarning++;
	}

	@Override
	public void endContainer(JTestContainer container) {
		report.report("****************endContainer**********");
	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
		report.report("****************endLoop**********");
	}

	@Override//works
	public void endRun() {
		report.report("****************endRun with no params**********");
	}

	@Override
	public void startContainer(JTestContainer container) {
		report.report("****************startContainer**********");
	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		report.report("****************startLoop**********");
	}

	@Override//works
	public void startTest(TestInfo testInfo) {
		report.report("****************startTest:   '"+testInfo.methodName+"'  **********");
		totalTests++;
	}


	@Override
	public void addError(Test arg0, Throwable arg1) {
		report.report("****************addError**********");
		testsThatThrowException++;
	}

	@Override
	public void addFailure(Test arg0, AssertionFailedError arg1) {
		report.report("****************addFailure:   '"+arg1.getMessage()+"'    **********");
		testsThatFailed++;
	}

	@Override//works
	public void endTest(Test arg0) {
		report.report("****************endTest that get test name**********");
	}

	@Override//works
	public void startTest(Test arg0) {
		report.report("****************startTest that get test name**********");
	}

	public int getTestsThatFailed() {
		return testsThatFailed;
	}

	public void setTestsThatFailed(int testsThatFailed) {
		this.testsThatFailed = testsThatFailed;
	}

	public int getTestsWithWarning() {
		return testsWithWarning;
	}

	public void setTestsWithWarning(int testsWithWarning) {
		this.testsWithWarning = testsWithWarning;
	}

	public int getTestsThatThrowException() {
		return testsThatThrowException;
	}

	public void setTestsThatThrowException(int testsThatThrowException) {
		this.testsThatThrowException = testsThatThrowException;
	}

	public int getTotalTests() {
		return totalTests;
	}

	public void setTotalTests(int totalTests) {
		this.totalTests = totalTests;
	}

}
