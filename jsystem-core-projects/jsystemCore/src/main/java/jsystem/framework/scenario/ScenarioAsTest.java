/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import junit.framework.SystemTestCase;
import junit.framework.Test;

/**
 * A wrapper to signal that a Scenario is ran as a Test<br>
 * will hold the current running test inside the ScenarioTest
 * 
 * @author Nizan Freedman
 *
 */

public class ScenarioAsTest extends SystemTestCase {

	private RunnerTest currentRunnerTest;
	
	public RunnerTest getCurrentRunnerTest() {
		return currentRunnerTest;
	}

	public void setCurrentRunnerTest(Test test) {
		currentRunnerTest = ScenariosManager.getInstance().getCurrentScenario().findRunnerTest(test);
	}

}
