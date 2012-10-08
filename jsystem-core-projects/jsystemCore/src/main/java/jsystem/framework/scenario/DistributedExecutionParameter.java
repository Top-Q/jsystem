/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

/**
 * Distributed execution parameter.
 */
public class DistributedExecutionParameter extends Parameter {
	public DistributedExecutionParameter(){
		super();
	}
	public DistributedExecutionParameter(String name,ParameterType type,Object value){
		super();
		setSection(DistributedExecutionHelper.SCENARIO_HOSTS_PARAMETERS_SECTION);
		setName(name);
		setType(type);
		setValue(value);
	}
	
}
