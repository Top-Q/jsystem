/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.io.File;

public interface ScenarioListener {
	
	/**
	 * Signal a scenario change event
	 * 
	 * @param current	the current scenario object
	 * @param changeType	what changed? NEW\DELETE\MODIFY\CURRENT\SAVE\RESET_DIRTY;
	 */
	void scenarioChanged(Scenario current,ScenarioChangeType changeType);

	/**
	 * Signal a directory change for the scenarios files
	 * 
	 * @param directory	the new Directory
	 */
	void scenarioDirectoryChanged(File directory);
	
	/**
	 * Signal a scenario dirty state change (an unsaved scenario is dirty)
	 * 
	 * @param s	the Scenario object
	 * @param isDirty	if True then scenario is dirty
	 */
	void scenarioDirtyStateChanged(Scenario s,boolean isDirty);
	
	/**
	 * Signal an event that test parameters have changed
	 * 
	 * @param testIIUUD	the test unique id
	 * @param oldValues	old parameters array
	 * @param newValues	new parameters arre
	 */
	void testParametersChanged(String testIIUUD,Parameter[] oldValues,Parameter[] newValues);
}
