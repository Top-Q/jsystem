/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.remote;

import java.util.Properties;

import jsystem.framework.report.ExecutionListener;
import jsystem.framework.scenario.JTestContainer;

public interface RemoteExecutor {

	/**
	 * Execute the remote ant vm that will execute the tests
	 * @param antFile the ant file to execute
	 * @param targets the targets to execute
	 * @param walker the scenario walker
	 * @throws Exception
	 */
	void run(String antFile, String[] targets, Properties additionalProps) throws Exception;

	/**
	 * Close the executor
	 *
	 */
	void exit();

	/**
	 * Interrupt the execution
	 *
	 */
	void interruptTest();

	/**
	 * Pause the execution
	 *
	 */
	void pause();

	/**
	 * Stop the execution
	 *
	 */
	void gracefulStop();
	
	/**
	 * Resume the execution
	 *
	 */
	void resume();

	/**
	 * sets the listener to notify when running of tests ended
	 * 
	 * @param runEndListener
	 *            the listener
	 */
	void setRunEndListener(ExecutionListener runEndListener);
	
	/**
	 * check if a test has started
	 * @return	true if a startTest event was received
	 */
	boolean isTestStarted();
	
	void startLoop(String name, int count);

	void endLoop(String name, int count);

	void startContainer(JTestContainer container);

	void endContainer(JTestContainer container);



}