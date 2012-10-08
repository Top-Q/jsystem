/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.scenario;

import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.TestsContainer;
import junit.framework.SystemTestCase;

public class TestsContainerTest extends SystemTestCase {
	
	TestsContainer container;
	
	protected void setUp() throws Exception {
		container = new TestsContainer();
	}

	public void testIsEmpty(){
		RunnerTest test = new RunnerTest("className", "methodName");
		assertTrue("IsEmpty: New Container is not Empty!!",container.isEmpty());
		container.addTest(test);
		assertFalse("IsEmpty: New Container is Still Empty!!",container.isEmpty());
	}

	public void testAddTest(){
		RunnerTest test = new RunnerTest("className", "methodName");
		RunnerTest test2 = new RunnerTest("className2", "methodName2");
		container.addTest(test);
		assertEquals("AddTest: First test wasn't added correctly!!",1,container.getNumOfTests());
		container.addTest(test);
		assertEquals("AddTest: Same test wast added twice!!",1,container.getNumOfTests());
		container.addTest(test2);
		assertEquals("AddTest: Second test wasn't added correctly!!",2,container.getNumOfTests());
	}

	public void testRemoveTest(){
		RunnerTest test = new RunnerTest("className", "methodName");
		RunnerTest test2 = new RunnerTest("className2", "methodName2");
		container.addTest(test);
		container.addTest(test2);
		container.removeTest(1);
		assertEquals("RemoveTest: First test wasn't removed correctly!!",1,container.getNumOfTests());
		container.removeTest(1);
		assertEquals("RemoveTest: First test was removed twice!!",1,container.getNumOfTests());
		container.removeTest(2);
		assertEquals("RemoveTest: Second test wasn't removed correctly!!",0,container.getNumOfTests());
	}

	public void testGetNumOfTests(){
		
	}

	public void testGetNext(){
		RunnerTest test = new RunnerTest("className", "methodName");
		RunnerTest test2 = new RunnerTest("className2", "methodName2");
		container.addTest(test);
		container.addTest(test2);
		assertEquals("GetNext: First Test isn't correct!!",test,container.getNext());
		assertEquals("GetNext: Second Test isn't correct!!",test2,container.getNext());
		assertNull("GetNext: Empty set isn't correct!!",container.getNext());
		
	}
	
	public void testGetLast(){
		RunnerTest test = new RunnerTest("className", "methodName");
		RunnerTest test2 = new RunnerTest("className2", "methodName2");
		container.addTest(test);
		container.addTest(test2);
		assertEquals("GetNext: Second Test isn't correct!!",test2,container.getLast());
		assertEquals("GetNext: First Test isn't correct!!",test,container.getLast());
		assertNull("GetNext: Empty set isn't correct!!",container.getLast());
		
	}

	protected void tearDown() throws Exception {

	}

}
