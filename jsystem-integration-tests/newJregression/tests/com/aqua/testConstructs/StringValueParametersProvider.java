package com.aqua.testConstructs;

import java.awt.Component;

import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ParameterProvider;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;

public class StringValueParametersProvider implements ParameterProvider {

	@Override
	public String getAsString(Object o) {
		return o.toString();
	}

	@Override
	public Object getFromString(String stringRepresentation) throws Exception {
		return (Object)stringRepresentation;
	}

	@Override
	public boolean isFieldEditable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void setProviderConfig(String... args) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object showUI(Component parent, Scenario s, RunnerTest test,
			Class<?> c, Object o, Parameter p) throws Exception {
		StringValueDialog dialog = new StringValueDialog();
		String param = o.toString();
		return dialog.getValue();
	}
}
