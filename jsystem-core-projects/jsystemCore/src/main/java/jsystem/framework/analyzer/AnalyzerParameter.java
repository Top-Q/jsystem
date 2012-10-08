/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.analyzer;

/**
 * This object actully do the analyzing work. To define new anlyzer parameter it is
 * recomended you will extends {@see AnalyzerParameterImpl}, and impliment the <code>analyze</code>
 * method, when implementing the <code>analyze</code> method you should set tree fields:<p>
 * <ul>
 * <li>{@link AnalyzerParameterImpl#title}, the title of the anlysis process (this field is mandatory and must be set).
 * <li>{@link AnalyzerParameterImpl#message}, detailed description of the anlysis.
 * <li>{@link AnalyzerParameterImpl#status}, the staut if that analysis were <code>false</code> indicate that the analysis failed.
 * </ul>
 * <p>
 * Following is an example for analyser parameter implementation:<p>
 * <p> <blockquote><pre>
 * public class FindText extends AnalyzerParameterImpl {
 * 
 * 	private String toSearch;
 * 	public FindText(String toSearch){
 * 		this.toSearch = toSearch;
 * 	}
 * 
 * 	public void analyze() {
 * 		// Set the title of the analysis
 * 		setTitle("Find the text: " + toSearch);
 * 		
 * 		// Set the message of the analysis to the text that is been searched
 * 		setMessage(testAgainst.toString());
 * 		
 * 		// Set the status of the analysis (if the string will not be found
 * 		// the analysis will fail).
 * 		setStatus(testAgainst.toString().matches(".*" + toSearch + ".*"));
 * 	}
 * }
 * </pre></blockquote>
 * {@see AnalyzerParameterImpl},{@see jsystem.extensions.analyzers.text.FindText}
 * @author Guy Arieli
 */
public interface AnalyzerParameter {
	/**
	 * Get the reporting title.
	 * 
	 * @return The reporting title.
	 */
	public String getTitle();

	/**
	 * Get message to report.
	 * 
	 * @return The message to report.
	 */
	public String getMessage();

	/**
	 * Get the analyzing status.
	 * 
	 * @return true if success false if not.
	 */
	public boolean getStatus();

	/**
	 * Get any throwable that was thrown in the analyzing phase.
	 * 
	 * @return A Throwable.
	 */
	public Throwable getThrowable();

	/**
	 * Set the test agains object.
	 * 
	 * @param o
	 *            The object to analyze.
	 */
	public void setTestAgainst(Object o);

	/**
	 * The analyze process.
	 */
	public void analyze();

	/**
	 * Set the analyzer, can be use by the analyze process itself.
	 * 
	 * @param analyzer
	 *            the analyzer
	 */
	public void setAnalyzer(Analyzer analyzer);

	/**
	 * Set the report title
	 * 
	 * @param title
	 *            report title
	 */
	public void setTitle(String title);

	/**
	 * Set the report message
	 * 
	 * @param message
	 *            report message
	 */
	public void setMessage(String message);

	/**
	 * Set the report status
	 * 
	 * @param status
	 *            report status
	 */
	public void setStatus(boolean status);

	/**
	 * Get the type of object this analyzer expect to analyze Will be check
	 * before setting it to the analyzer.
	 * 
	 * @return the object class type or null if not set
	 */
	public Class<?> getTestAgainstType();
}
