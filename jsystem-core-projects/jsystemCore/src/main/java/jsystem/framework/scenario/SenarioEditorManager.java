/*
 * Created on 01/06/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;

public class SenarioEditorManager {
	private static ScenarioEditor duplicator;

	public static ScenarioEditor getDuplicator() throws Exception {
		if (duplicator != null) {
			return duplicator;
		}
		String duplicatorClassName = JSystemProperties.getInstance().getPreference(FrameworkOptions.SCENARIO_EDITOR);

		if (duplicatorClassName == null) {
			duplicatorClassName = "jsystem.treeui.exceleditor.ExcelScenarioEditor";
		}
		duplicator = (ScenarioEditor) Class.forName(duplicatorClassName).newInstance();
		return duplicator;
	}
}
