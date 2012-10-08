/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * Abstract text analyzer for analyzers that uses String as input.
 * 
 * @author guy.arieli
 * 
 */
public abstract class AnalyzeTextParameter extends AnalyzerParameterImpl {
	protected String toFind;

	protected String testText = null;

	public AnalyzeTextParameter(String toFind) {
		this.toFind = toFind;
	}

	public void setTestAgainst(Object o) {
		if (o != null) {
			testText = o.toString();
		}
	}

	public Class<?> getTestAgainstType() {
		return String.class;
	}
}
