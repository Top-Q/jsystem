/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.threads;

import java.util.HashMap;
import java.util.Iterator;

import jsystem.framework.report.ListenerstManager;

/**
 * this class is a singleton that manage all the TestThreadGroup.<br>
 * To instantiate the object a static getInstance() method will be used.
 *
 */
public class TestThreadFactory {
	private boolean runInParallel = true;
	private static TestThreadFactory manager = null;
	
	public static synchronized TestThreadFactory getInstance(){
		if(manager == null){
			manager = new TestThreadFactory();
		}
		return manager;
	}
	
	HashMap<String, TestThreadGroup> groups = new HashMap<String, TestThreadGroup>();
	
	private TestThreadFactory(){
		
	}
	
	/**
	 * Enable the creation of TestThreadGroup. 
	 * @param name - group name to get.
	 * @return required group.
	 */
	public TestThreadGroup getTestThreadGroup(String name){
		if(groups.containsKey(name)){
			return groups.get(name);
		}
		TestThreadGroup group = new TestThreadGroup(name);
		groups.put(name, group);
		return group;
	}
	
	/**
	 * Join all the executions.
	 * @param timeout - the time to wait in milliseconds.
	 * @throws Exception
	 */
	public void joinAll(long timeout) throws Exception{
		ListenerstManager.getInstance().report("Joining all running tests");
		Iterator<TestThreadGroup> groupIter = groups.values().iterator();
		while(groupIter.hasNext()){
			groupIter.next().join(timeout);
		}
	}
	
	/**
	 * Remove a group from the factory. 
	 * @param name - group name to remove.
	 */
	public void removeGroup(String name){
		groups.remove(name);
	}
	
	/**
	 * Enable the creation of TestThreadGroup.
	 * @return default group.
	 */
	public TestThreadGroup getTestThreadGroup(){
		return getTestThreadGroup("default");
	}
		
	/**
	 * Enable running tests in parallel.
	 * @return run in parallel mode.
	 */
	public boolean isRunInParallel() {
		return runInParallel;
	}

	/**
	 * Disable running tests in parallel.
	 * @param runInParallel - run in parallel mode.
	 */
	public void setRunInParallel(boolean runInParallel) {
		this.runInParallel = runInParallel;
	}
}
