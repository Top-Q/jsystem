/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.server;

import jsystem.framework.report.ExecutionListener;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.runner.ErrorLevel;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

/**
 * Listens for execution events.
 * Was created to track engine state.
 * @author goland
 */
public class ExecutionStateListener implements ExecutionListener{

	private RunnerEngineExecutionState executionState = RunnerEngineExecutionState.initiating;
	@Override
	public void errorOccured(String title, String message, ErrorLevel level) {
		executionState = RunnerEngineExecutionState.idleError;
	}

	@Override
	public void executionEnded(String scenarioName) {
		executionState = RunnerEngineExecutionState.idle;
	}

	@Override
	public void remoteExit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remotePause() {
		executionState = RunnerEngineExecutionState.paused;		
	}

	@Override
	public void addWarning(Test test) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endRun() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startTest(TestInfo testInfo) {
		
		executionState = RunnerEngineExecutionState.running;
	}

	@Override
	public void addError(Test arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addFailure(Test arg0, AssertionFailedError arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endTest(Test arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startTest(Test arg0) {
		// TODO Auto-generated method stub
		
	}

	public RunnerEngineExecutionState getExecutionState() {
		return executionState;
	}

	@Override
	public void endContainer(JTestContainer container) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startContainer(JTestContainer container) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub
		
	}
}
