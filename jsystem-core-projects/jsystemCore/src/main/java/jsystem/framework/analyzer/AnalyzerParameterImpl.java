/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.analyzer;

/**
 * This object actually does the analysis work. In order to create a report the
 * title, message and status should be set.
 * 
 * @author Guy Arieli
 */
public abstract class AnalyzerParameterImpl implements AnalyzerParameter {
	
	/**
	 * The title of the analysis process (this field is mandatory and must be set).
	 */
	protected String title = null;

	/**
	 * Detailed description of the analysis.
	 */
	protected String message = null;

	/**
	 * The status if that analysis were <code>false</code> indicate that the analysis failed.
	 */
	protected boolean status = false;

	protected Throwable throwable = null;

	protected Object testAgainst = null;

	protected Analyzer analyzer = null;

	/**
	 * Get the reporting title.
	 * 
	 * @return The reporting title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Get message to report.
	 * 
	 * @return The message to report.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Get the analyzing status.
	 * 
	 * @return true if success false if not.
	 */
	public boolean getStatus() {
		return status;
	}

	/**
	 * Get any throwable that was thrown in the analyzing phase.
	 * 
	 * @return A Throwable.
	 */
	public Throwable getThrowable() {
		return throwable;
	}

	/**
	 * Set the test against object.
	 * 
	 * @param o
	 *            The object to analyze.
	 */
	public void setTestAgainst(Object o) {
		this.testAgainst = o;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Class<?> getTestAgainstType() {
		return null;
	}

}
