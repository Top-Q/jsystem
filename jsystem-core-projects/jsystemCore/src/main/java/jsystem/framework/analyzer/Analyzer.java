/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.analyzer;

/**
 * Any system object implement the <code>Analyzer</code> interface. The Analyzer work in 2
 * steps: In the first the object to analyze is set by using
 * {@see jsystem.framework.analyzer.Analyzer#setTestAgainsObject(Object)}. it's the system object task to do it. 
 * Then the <code>analyze</code>or <code>isAnalyzeSuccess</code> methods are called.
 * The analyze methods accept the {@see AnalyzerParameter} that define the analysis process itself.<p>
 * <b>For example:</b><p>
 * System object code:<p>
 * <p> <blockquote><pre>
 *     public void showVersion() {
 *              String version;
 *              // Do some operation that get the system version
 *              report.report("version: " + version);
 *              <b>setTestAgainsObject(version)</b>;
 *     }
 * </pre></blockquote>
 * <p>
 * Test code:<p>
 * <p> <blockquote><pre>
 *     public void testVersion() {
 *              device.showVersion();
 *              <b>device.analyze(new FindText("4.7"))</b>;
 *     }
 * </pre></blockquote>
 * <p>
 * @author Guy Arieli
 */
public interface Analyzer {
	/**
	 * Analyze the result. Report to the reporter. Send AnalyzerException if
	 * analyze fails.
	 * 
	 * @param parameter
	 *            The analyzer parameter to use in the analysis process.
	 * 
	 * @exception AnalyzerException the analysis process failed.
	 */
	public void analyze(AnalyzerParameter parameter) throws AnalyzerException;

	/**
	 * Analyze the result. Report to the reporter.if not silent Send
	 * AnalyzerException if analyze fails.
	 * 
	 * @param parameter
	 *            The analyzer parameter to use in the analysis process.
	 * @param silent
	 *            if silent no reports will be submit
	 * 
	 * @exception AnalyzerException the analysis process failed.
	 */
	public void analyze(AnalyzerParameter parameter, boolean silent) throws AnalyzerException;

	/**
	 * Analyze the result. Report to the reporter.if not silent Send
	 * AnalyzerException if analyze fails.
	 * 
	 * @param parameter
	 *            The analyzer parameter to use in the analysis process.
	 * @param silent
	 *            if silent no reports will be submit
	 * 
	 * @exception AnalyzerException the analysis process failed.
	 */
	public void analyze(AnalyzerParameter parameter, boolean silent, boolean throwException) throws AnalyzerException;

	/**
	 * Analyze the result. Report to the reporter.if not silent Send
	 * AnalyzerException if analyze fails.
	 * 
	 * @param parameter
	 *            The analyzer parameter to use in the analysis process.
	 * @param silent
	 *            if silent no reports will be submitted if analyze is successfull
	 * @param throwException
	 * 			  if false will not throw an exception if analyze fails
	 * @param showAsWarning
	 * 			  if true will show a warning message instead of error message on analyze failure
	 * 
	 * @exception AnalyzerException the analysis process failed.
	 */
	public void analyze(AnalyzerParameter parameter, boolean silent, boolean throwException, boolean showAsWarning) throws AnalyzerException;
	
	/**
	 * Analyze the result.<br>
	 * print given successMessage (if given) if analyze is successful<br>
	 * print given failMessage (if given) if analyze is not successful<br>
	 * 
	 * @param parameter
	 *            The analyzer parameter to use in the analysis process.
	 * @param successMessage
	 * 			  the title that will be reported on a successful analysis. overrides AnalyzerParameterImpl title if not null
	 * @param failMessage
	 * 			  the title that will be reported on a failed analysis. overrides AnalyzerParameterImpl title if not null
	 * @throws AnalyzerException	the analysis process failed.
	 */
	public void analyze(AnalyzerParameter parameter, String successMessage, String failMessage) throws AnalyzerException;
	
	/**
	 * Analyze the result. Report to the reporter.if not silent Send
	 * AnalyzerException if analyze fails.<br>
	 * print given successMessage (if given) if analyze is successful<br>
	 * print given failMessage (if given) if analyze is not successful<br>
	 * 
	 * @param parameter
	 *            The analyzer parameter to use in the analysis process.
	 * @param silent
	 *            if silent no reports will be submitted if analyze is successfull
	 * @param successMessage
	 * 			  the title that will be reported on a successful analysis. overrides AnalyzerParameterImpl title if not null
	 * @param failMessage
	 * 			  the title that will be reported on a failed analysis. overrides AnalyzerParameterImpl title if not null
	 * @throws AnalyzerException	the analysis process failed.
	 */
	public void analyze(AnalyzerParameter parameter, boolean silent, String successMessage, String failMessage) throws AnalyzerException;
	
	/**
	 * Analyze the result. Report to the reporter.if not silent Send
	 * AnalyzerException if analyze fails.<br>
	 * print given successMessage (if given) if analyze is successful<br>
	 * print given failMessage (if given) if analyze is not successful<br>
	 * 
	 * @param parameter
	 *            The analyzer parameter to use in the analysis process.
	 * @param silent
	 *            if silent no reports will be submitted if analyze is successfull
	 * @param throwException
	 * 			  if false will not throw an exception if analyze fails
	 * @param successMessage
	 * 			  the title that will be reported on a successful analysis. overrides AnalyzerParameterImpl title if not null
	 * @param failMessage
	 * 			  the title that will be reported on a failed analysis. overrides AnalyzerParameterImpl title if not null
	 * @throws AnalyzerException	the analysis process failed.
	 */
	public void analyze(AnalyzerParameter parameter, boolean silent, boolean throwException, String successMessage, String failMessage) throws AnalyzerException;
	
	/**
	 * Analyze the result. Report to the reporter.if not silent Send
	 * AnalyzerException if analyze fails.<br>
	 * print given successMessage (if given) if analyze is successful<br>
	 * print given failMessage (if given) if analyze is not successful<br>
	 * 
	 * @param parameter
	 *            The analyzer parameter to use in the analysis process.
	 * @param silent
	 *            if silent no reports will be submitted if analyze is successfull
	 * @param throwException
	 * 			  if false will not throw an exception if analyze fails
	 * @param showAsWarning
	 * 			  if true will show a warning message instead of error message on analyze failure
	 * @param successMessage
	 * 			  the title that will be reported on a successful analysis. overrides AnalyzerParameterImpl title if not null
	 * @param failMessage
	 * 			  the title that will be reported on a failed analysis. overrides AnalyzerParameterImpl title if not null
	 * @throws AnalyzerException	the analysis process failed.
	 */
	public void analyze(AnalyzerParameter parameter, boolean silent, boolean throwException, boolean showAsWarning, String successMessage, String failMessage) throws AnalyzerException;
	
	/**
	 * @deprecated please use {@link #setTestAgainstObject(Object)}
	 */
	public void setTestAgainsObject(Object o);

	/**
	 * Set the object to analyze. This method is called by the system object.
	 * 
	 * @param o
	 *            The object to analyze. can be String ResultSet or any other
	 *            object.
	 */
	public void setTestAgainstObject(Object o);

	/**
	 * If set to false an exception will not be thrown
	 * 
	 * @param throwException
	 */
	public void setThrowException(boolean throwException);

	/**
	 * @deprecated please use {@link #getTestAgainstObject()}
	 */
	public Object getTestAgainsObject();

	/**
	 * Get the tests against object
	 * 
	 * @return the object to test
	 */
	public Object getTestAgainstObject();

	/**
	 * Check if the analyze success
	 * 
	 * @param parameter
	 *            the analyzer to test
	 * @return true if the analyze sucess false if not
	 */
	public boolean isAnalyzeSuccess(AnalyzerParameter parameter);

}
