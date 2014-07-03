package com.aqua.anttask.jsystem;

import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenariosManager;

/**
 * Intended for tasks that needs the ForTask loop capabilities and needs to read
 * the various parameters from the scenarios property files
 * 
 * @author Itai Agmon
 * 
 */
public abstract class PropertyReaderTask extends ForTask {

	private String uuid;

	public void setFullUuid(String uuid) {
		while (uuid.startsWith(".")) {
			uuid = uuid.substring(1);
		}
		this.uuid = uuid;
	}

	public void setParentName(String name) {
		if (name.startsWith(".")) {
			name = name.substring(1);
		}
		// ITAI: We currently not using this. But I am keeping it in case we
		// will need it in the future.
	}

	/**
	 * Fetches the specified parameter value from all the scenarios property
	 * files.
	 * 
	 * @param parameterName
	 *            Name of the parameter
	 * @param defaultParameterValue
	 *            If the parameter was not found it will set to this value
	 * @return The parameter value as fetched from the different properties
	 *         files
	 */
	protected String getParameterFromProperties(String parameterName, String defaultParameterValue) {
		final JTest antFlowControlTest = getFlowControlTest();
		return ScenarioHelpers.getParameterValueFromProperties(antFlowControlTest, uuid, parameterName,
				defaultParameterValue);
	}

	/**
	 * 
	 * @return The AntFlowControl object the represents this task
	 */
	private JTest getFlowControlTest() {
		JTestContainer parentScenario = null;
		if (uuid.contains(".")) {
			// ITAI: The scenario that holds the test is nested under another
			// scenario. We need to use a trick to get the test
			final String parentUuid = uuid.substring(0, uuid.lastIndexOf('.'));
			parentScenario = (JTestContainer) ScenariosManager.getInstance().getCurrentScenario()
					.getTestByFullId(parentUuid);
		} else {
			// ITAI: The scenario that holds the test is the root scenario
			parentScenario = ScenariosManager.getInstance().getCurrentScenario();
		}
		if (null == parentScenario) {
			throw new IllegalStateException("Failed to get the parent scenario");
		}
		return parentScenario.getTestByFullId(uuid);
	}

	protected String getUuid() {
		return uuid;
	}

}
