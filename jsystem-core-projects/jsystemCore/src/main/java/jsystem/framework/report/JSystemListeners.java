/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

import jsystem.framework.RunnerStatePersistencyManager;
import jsystem.framework.fixture.FixtureListener;
import jsystem.framework.scenario.ScenarioListener;
import jsystem.framework.sut.SutListener;
import junit.framework.Test;

public interface JSystemListeners extends FixtureListener, SutListener, ScenarioListener, ExtendTestListener, Reporter,ExecutionListener {

	public void addListener(Object listener);

	public void removeListener(Object listener);

	public void addError(Test test, String message, String stack);

	public void addFailure(Test test, String message, String stack, boolean analyzerException);

	public void blockReporters(boolean block);
	
	/**
	 * Send init event to all the reporters
	 */
	public void initReporters();
	
	public void flushReporters();

	public boolean getLastTestFailed();

	public boolean isPause();

	public void setDate(String date);
	
	/**
	 * Signals the runner engine to save it's state.
 	 * @see RunnerStatePersistencyManager
	 */
	public void saveState(Test t) throws Exception;
}
