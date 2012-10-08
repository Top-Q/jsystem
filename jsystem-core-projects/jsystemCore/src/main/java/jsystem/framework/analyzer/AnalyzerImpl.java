/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.analyzer;

import jsystem.framework.IgnoreMethod;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;

/**
 * Any system object implement an Analyzer as well. The Analyzer work in 2
 * steps: In the first the object to analyze is set by using
 * setTestAgainsObject. it's the system object task to do it. Then the analyze
 * or the silenceAnalyze method are called.
 * 
 * @author Guy Arieli
 */
public class AnalyzerImpl implements Analyzer {
	
	private Object testAgainstObject;

	private Reporter report = ListenerstManager.getInstance();

	private boolean throwException = true;

	public void analyze(AnalyzerParameter parameter) throws AnalyzerException {
		analyze(parameter, false);
	}

	public void analyze(AnalyzerParameter parameter, boolean silent) throws AnalyzerException {
		analyze(parameter, silent, throwException);
	}

	public void analyze(AnalyzerParameter parameter, boolean silent, boolean throwException) throws AnalyzerException {
		analyze(parameter, silent, throwException, false);
	}
	
	public void analyze(AnalyzerParameter parameter, String successMessage, String failMessage) throws AnalyzerException {
		analyze(parameter, false, successMessage, failMessage);
	}

	public void analyze(AnalyzerParameter parameter, boolean silent, String successMessage, String failMessage) throws AnalyzerException {
		analyze(parameter, silent, throwException, successMessage, failMessage);
	}

	public void analyze(AnalyzerParameter parameter, boolean silent, boolean throwException, String successMessage, String failMessage) throws AnalyzerException {
		analyze(parameter, silent, throwException, false, successMessage, failMessage);
	}

	public void analyze(AnalyzerParameter parameter, boolean silent, boolean throwException, boolean showAsWarning) throws AnalyzerException {
		analyze(parameter, silent, throwException, showAsWarning, null, null);
	}
	
	public void analyze(AnalyzerParameter parameter, boolean silent, boolean throwException, boolean showAsWarning, String successMessage, String failMessage) throws AnalyzerException {
		parameter.setTitle(null);
		parameter.setMessage(null);
		parameter.setStatus(false);

		parameter.setAnalyzer(this);
		String title = null;
		try {
			if (testAgainstObject != null) {
				Class<?> c = parameter.getTestAgainstType();
				if (c != null && !(c.isAssignableFrom(testAgainstObject.getClass()))) {
					parameter.setTitle("Use of wrong analyzer");
					parameter.setMessage("The analyzer that you used requires input in " + c.getName()
							+ " type, but the object to analyze is of " + testAgainstObject.getClass().getName()
							+ " type.");
					parameter.setStatus(false);
				} else {
					parameter.setTestAgainst(testAgainstObject);
					parameter.analyze();
				}
			} else { // if the test against is null
				parameter.setTitle("The object to analyze is null");
				parameter
						.setMessage("The object to analyze is null, please check that you run the analyze method on the right object");
				parameter.setStatus(false);
			}
			if (!silent || (silent && (!parameter.getStatus()))) {
				int status = Reporter.PASS;
				title = (successMessage != null) ? successMessage : parameter.getTitle();
				if (!parameter.getStatus()){
					title = (failMessage != null) ? failMessage : parameter.getTitle();
					if (showAsWarning) {
						status = Reporter.WARNING;
					} else {
						status = Reporter.FAIL;
					}
				}
				report.report(title, parameter.getMessage(), status);
			}
		} catch (Throwable t) {
			if (!silent) {
				report.report("Analyze proccess failed", t);
			}
		}
		if (!parameter.getStatus()) {
			if (throwException) {
				throw new AnalyzerException(title);
			}
		}
	}

	public boolean isAnalyzeSuccess(AnalyzerParameter parameter) {
		parameter.setTitle(null);
		parameter.setMessage(null);
		parameter.setStatus(false);

		parameter.setAnalyzer(this);
		try {
			if (testAgainstObject != null) {
				Class<?> c = parameter.getTestAgainstType();
				if (c != null && !(c.isAssignableFrom(testAgainstObject.getClass()))) {
					parameter.setTitle("Use of wrong analyzer");
					parameter.setMessage("The analyzer that you used requires input in " + c.getName()
							+ " type, but the object to analyze is of " + testAgainstObject.getClass().getName()
							+ " type.");
					parameter.setStatus(false);
					report.report(parameter.getTitle(), parameter.getMessage(), parameter.getStatus());
					return false;
				} else {
					parameter.setTestAgainst(testAgainstObject);
					parameter.analyze();
				}
			} else { // if the test against is null
				parameter.setTitle("The object to analyze is null");
				parameter
						.setMessage("The object to analyze is null, please check that you run the analyze method on the right object");
				parameter.setStatus(false);
				report.report(parameter.getTitle(), parameter.getMessage(), parameter.getStatus());
				return false;
			}
		} catch (Throwable t) {
			report.report("Analyze proccess failed", t);
			return false;
		}
		return parameter.getStatus();
	}

	/**
	 * @deprecated please use {@link #testAgainstObject}
	 */
	public void setTestAgainsObject(Object o) {
		this.testAgainstObject = o;
	}

	public void setTestAgainstObject(Object o) {
		this.testAgainstObject = o;
	}

	/**
	 * @deprecated please use {@link #getTestAgainstObject()}
	 */
	public Object getTestAgainsObject() {
		return testAgainstObject;
	}

	public Object getTestAgainstObject() {
		return testAgainstObject;
	}

	@IgnoreMethod
	public boolean isThrowException() {
		return throwException;
	}

	@IgnoreMethod
	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}
}
