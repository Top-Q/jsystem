/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.remote;

import java.util.Properties;

import jsystem.framework.report.ExecutionListener;

public interface RemoteExecutor {

	/**
	 * Execute the remote ant vm that will execute the tests
	 * @param antFile the ant file to execute
	 * @param targets the targets to execute
	 * @param walker the scenario walker
	 * @throws Exception
	 */
	public abstract void run(String antFile, String[] targets, Properties additionalProps) throws Exception;

	/**
	 * Close the executor
	 *
	 */
	public abstract void exit();

	/**
	 * Interrupt the execution
	 *
	 */
	public abstract void interruptTest();

	/**
	 * Pause the execution
	 *
	 */
	public abstract void pause();

	/**
	 * Stop the execution
	 *
	 */
	public abstract void gracefulStop();
	
	/**
	 * Resume the execution
	 *
	 */
	public abstract void resume();

	/**
	 * sets the listener to notify when running of tests ended
	 * 
	 * @param runEndListener
	 *            the listener
	 */
	public abstract void setRunEndListener(ExecutionListener runEndListener);
	
	/**
	 * check if a test has started
	 * @return	true if a startTest event was received
	 */
	public boolean isTestStarted();


}