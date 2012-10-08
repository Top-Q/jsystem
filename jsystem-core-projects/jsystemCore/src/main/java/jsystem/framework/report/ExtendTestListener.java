/*
 * Created on Oct 22, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import junit.framework.Test;
import junit.framework.TestListener;

/**
 * Used to give additional notification for TestListener's, like endRun
 * on scenario execution end.
 * 
 * @author guy.arieli
 * 
 */
public interface ExtendTestListener extends TestListener {

	/**
	 * Notify that a warning was added to the test.
	 * @param test
	 */
	public void addWarning(Test test);

	/**
	 * Serializable start test (used to move the notification over network connection)
	 * @param testInfo include all the test info parameters
	 */
	public void startTest(TestInfo testInfo);

	/**
	 * Notify on run execution end.
	 *
	 */
	public void endRun();
	
	/**
	 * notify that a loop started with a number of the loop
	 * 
	 * @param loop	the container object
	 * @param count	the loop number
	 */
	public void startLoop(AntForLoop loop,int count);
	
	/**
	 *  notify that a loop ended with a number of the loop
	 * 
	 * @param loop	the container object
	 * @param count	the loop number
	 */
	public void endLoop(AntForLoop loop,int count);
	
	/**
	 * notify that a container element was started
	 * 
	 * @param container	the container object
	 */
	public void startContainer(JTestContainer container);
	
	/**
	 * notify that a container element was finished
	 * 
	 * @param container	the container object
	 */
	public void endContainer(JTestContainer container);
}
