/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

import jsystem.runner.ErrorLevel;

/**
 * General execution events.
 * 
 * @author goland
 */
public interface ExecutionListener extends ExtendTestListener {

	void remoteExit();

	void remotePause();

	void executionEnded(String scenarioName);

	void errorOccured(String title, String message, ErrorLevel level);

}
