/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

/**
 * used in reference parameters
 */


public class ScenarioParameter extends Parameter {
	/**
	 * used for Scenario parameters reference
	 */
	public final static String SCENARIO_PARAMETERS_SECTION = "Scenario Parameters";
	
	public ScenarioParameter(){
		super();
		setSection(SCENARIO_PARAMETERS_SECTION);
		setType(ParameterType.REFERENCE);
	}
}
