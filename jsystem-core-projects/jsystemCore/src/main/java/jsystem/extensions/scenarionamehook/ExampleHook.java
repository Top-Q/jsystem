/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.scenarionamehook;

public class ExampleHook implements ScenarioNameHook {
	static int index = 0;
	@Override
	public String getProjectName() {
		return "Default Project";
	}

	@Override
	public String getScenarioId() {
		index +=1;
		return "_"+index;
	}
}
