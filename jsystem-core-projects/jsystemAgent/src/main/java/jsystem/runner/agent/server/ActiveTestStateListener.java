/*
 * Created on 15/10/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.server;

import java.util.logging.Logger;

import jsystem.framework.RunnerStatePersistencyManager;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

/**
 * Listens for execution events.
 * Used to track active test index.
 * @see RunnerStatePersistencyManager 
 * @author goland
 */
public class ActiveTestStateListener implements TestListener {
	
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(ActiveTestStateListener.class.getName());
	
	public ActiveTestStateListener() {
	}

	public void init() {
	}

	public void addError(Test test, Throwable t) {
	}

	public void addFailure(Test test, AssertionFailedError t) {
	}

	public void endTest(Test test) {
	}

	public void startTest(Test test) {
		int index = getTestIndex(test); 
		RunnerStatePersistencyManager.getInstance().setActiveTestIndex(index);
	}

	private Scenario getScenario() {
		return ScenariosManager.getInstance().getCurrentScenario();
	}
	
	private int getTestIndex(Test t){
		return getScenario().getGeneralIndex(t,true);
	}
}
