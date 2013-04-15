/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.paramproviders;

import java.awt.Component;

import javax.swing.JOptionPane;

import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ParameterProvider;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;

/**
 * This is the simplest example for <code>ParameterProvider</code>. It enable
 * the use of parameters of <code>StringBuffer</code> type.
 * 
 * @author guy.arieli
 * 
 */
public class StringBufferParameterProvider implements ParameterProvider {

	@Override
	public String getAsString(Object o) {
		if (null == o) {
			return null;
		}
		return ((StringBuffer) o).toString();
	}

	@Override
	public Object getFromString(String stringRepresentation) {
		if (null == stringRepresentation) {
			return null;
		}
		return new StringBuffer(stringRepresentation);
	}

	@Override
	public Object showUI(Component parent, Scenario currentScenario,
			RunnerTest rtest, Class<?> classType, Object object,
			Parameter parameter) {
		String out = JOptionPane.showInputDialog(parent, "Please enter value");
		if (out == null) {
			return null;
		}
		return new StringBuffer(out);
	}

	@Override
	public boolean isFieldEditable() {
		return false;
	}

	@Override
	public void setProviderConfig(String... args) {
	}

}
