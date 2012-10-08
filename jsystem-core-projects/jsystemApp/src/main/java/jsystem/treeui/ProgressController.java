/*
 * Created on Jul 1, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioChangeType;
import jsystem.framework.scenario.ScenarioListener;
import jsystem.framework.scenario.ScenariosManager;
import junit.framework.AssertionFailedError;
import junit.framework.SystemTest;
import junit.framework.Test;
import junit.framework.TestListener;

/**
 * @author guy.arieli
 * 
 */
public class ProgressController extends Thread implements ScenarioListener, TestListener {
	private static Logger log = Logger.getLogger(ProgressController.class.getName());

	ProgressListener view;

	static Properties times = new Properties();

	long testStartTime = 0;

	boolean isPass = true;

	boolean running = false;

	long testMaxTime = 0;

	long suiteStartTime = 0;

	public ProgressController(ProgressListener view) {
		super("ProgressController");
		this.view = view;
		try {
			times.load(new FileInputStream("testtimes.properties"));
		} catch (IOException e) {
		}
	}

	public static long getTestTime(String className, String methodName) {
		String t = times.getProperty(className + ";" + methodName);
		if (t != null) {
			try {
				return Long.parseLong(t);
			} catch (Throwable th) {

			}
		}
		return -1;
	}

	public void run() {
		while (true) {
			Vector<?> tests = ScenariosManager.getInstance().getCurrentScenario().getTests();
			long suiteMaxTime = calcRunningTime(tests);
			view.setCurrentSuiteRunningTime(suiteMaxTime);
			suiteStartTime = System.currentTimeMillis();
			while (running) {
				if (testStartTime > 0) {
					long runTime = System.currentTimeMillis() - testStartTime;
					if (runTime > testMaxTime) {
						view.setCurrentTestRunningTime(runTime);
					}
					long suiteRunningTime = System.currentTimeMillis() - suiteStartTime;
					if (suiteRunningTime > suiteMaxTime) {
						view.setCurrentSuiteRunningTime(suiteRunningTime);
					}
					view.updateTimes(runTime, suiteRunningTime);
				}

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public synchronized void setRunning(boolean running) {
		this.running = running;
		notify();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.treeui.ScenarioListener#selectionChanged(jsystem.treeui.Scenario)
	 */
	public void scenarioChanged(Scenario current,ScenarioChangeType type) {
		view.setCurrentSuiteRunningTime(calcRunningTime(current.getTests()));
		view.updateTimes(0, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#addError(junit.framework.Test,
	 *      java.lang.Throwable)
	 */
	public void addError(Test test, Throwable t) {
		isPass = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#addFailure(junit.framework.Test,
	 *      junit.framework.AssertionFailedError)
	 */
	public void addFailure(Test test, AssertionFailedError t) {
		isPass = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#endTest(junit.framework.Test)
	 */
	public void endTest(Test test) {
		long runTime = System.currentTimeMillis() - testStartTime;
		long suiteRunningTime = System.currentTimeMillis() - suiteStartTime;
		view.setCurrentTestRunningTime(runTime);
		view.updateTimes(runTime, suiteRunningTime);
		if (test instanceof SystemTest) {
			if (isPass) {
				long time = System.currentTimeMillis() - testStartTime;
				times.setProperty(test.getClass().getName() + ";" + ((SystemTest) test).getName(), Long.toString(time));
				try {
					times.store(new FileOutputStream("testtimes.properties"), null);
				} catch (IOException e) {
					log.log(Level.INFO,
							"Fail to store data to testtimes.properties please check it is not source controled");
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#startTest(junit.framework.Test)
	 */
	public void startTest(Test test) {
		testStartTime = System.currentTimeMillis();
		isPass = true;
		String t = times.getProperty(test.getClass().getName() + ";" + ((SystemTest) test).getName());
		if (t != null) {
			try {
				testMaxTime = Long.parseLong(t);
			} catch (Throwable th) {

			}
		}
		view.setCurrentTestRunningTime(testMaxTime);
	}

	public long calcRunningTime(Vector<?> tests) {
		long runningTime = 0;
		for (int i = 0; i < tests.size(); i++) {
			RunnerTest test = (RunnerTest) tests.elementAt(i);
			String t = times.getProperty(test.getClassName() + ";" + test.getMethodName());
			try {
				runningTime += Long.parseLong(t);
			} catch (Exception e) {

			}
		}
		return runningTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.treeui.ScenarioListener#scenarioDirectoryChanged(java.io.File)
	 */
	public void scenarioDirectoryChanged(File directory) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scenarioDirtyStateChanged(Scenario s, boolean isDirty) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testParametersChanged(String testIIUUD, Parameter[] oldValues,
			Parameter[] newValues) {
		// TODO Auto-generated method stub
		
	}

}
