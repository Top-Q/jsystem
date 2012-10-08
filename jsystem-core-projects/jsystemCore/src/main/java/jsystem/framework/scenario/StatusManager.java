/*
 * Created on 15/10/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.TestInfo;
import jsystem.framework.report.TestStatusListener;
import jsystem.framework.scenario.flow_control.AntForLoop;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;


/**
 * the previous class has been separate to two classe's because the previous
 * code didn't manage the STAT_WARNING situation.  
 */

public class StatusManager{
	
	public static RegularStatusListener regularStatusListener;
	public static ExtendedStatusListener extendedTestListener;
	
	/**
	 * Locate a given test RunnerTest object and set it's status
	 * 
	 * @param test	the test to match
	 * @param status	the status to set (STAT_SUCCESS/STAT_FAIL/STAT_WARNING...)
	 */
	private void setTestStatus(Test test, int status){
		RunnerTest runnerTest = ScenarioHelpers.getRunnerTest(test);
		if (runnerTest != null){
			runnerTest.setStatus(status);
		}
	}
	
	/**
	 * the previous method. 
	 */
	public static RegularStatusListener getRegularStatusListener(){
		if (regularStatusListener == null){
			StatusManager status = new StatusManager();
			regularStatusListener = status.new RegularStatusListener();
		}
		return regularStatusListener;
	}
	/**
	 * the method that handle the STAT_WARNING situation. 
	 */
	public static ExtendedStatusListener getExtendedStatusListener(){
		if (extendedTestListener == null){
			StatusManager status = new StatusManager();
			extendedTestListener = status.new ExtendedStatusListener();
		}
		return extendedTestListener;
	}
	public class RegularStatusListener implements TestListener, TestStatusListener{
		
		
		public void addError(Test test, Throwable t) {
			setTestStatus(test, RunnerTest.STAT_ERROR);
		}

		public void addFailure(Test test, AssertionFailedError t) {
			setTestStatus(test, RunnerTest.STAT_FAIL);
		}
		
		public void endTest(Test test) {
			if (test == null) {
				return;
			}
			
			RunnerTest runnerTest= ScenarioHelpers.getRunnerTest(test);
			
			if (runnerTest.statusShouldBePass()) {
				runnerTest.setStatus(RunnerTest.STAT_SUCCESS);
			}
		}

		public void startTest(Test test) {
			setTestStatus(test, RunnerTest.STAT_RUNNING);
		}
	}

	
	public class ExtendedStatusListener implements ExtendTestListener, TestStatusListener{

		public void addWarning(Test test) {
			setTestStatus(test, RunnerTest.STAT_WARNING);
		}

		public void endRun() {
			// TODO Auto-generated method stub
			
		}

		public void startTest(TestInfo testInfo) {
			// TODO Auto-generated method stub
			
		}

		public void addError(Test arg0, Throwable arg1) {
			// TODO Auto-generated method stub
			
		}

		public void addFailure(Test arg0, AssertionFailedError arg1) {
			// TODO Auto-generated method stub
			
		}

		public void endTest(Test arg0) {
			// TODO Auto-generated method stub
			
		}

		public void startTest(Test arg0) {
			// TODO Auto-generated method stub
			
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
}
