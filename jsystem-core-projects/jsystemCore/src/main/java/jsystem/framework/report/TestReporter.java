/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

import java.io.IOException;

/**
 * TestReporter define the methods that should be implemented by all the system
 * reporter. The reporter should register to the ListenerManager, and will be
 * notify by the report method when a report is added.
 * 
 * @author guy.arieli
 * 
 */
public interface TestReporter {
	/**
	 * Launch the manager GUI.
	 * @throws IOException TODO
	 * 
	 */
	public void initReporterManager() throws IOException;

	/**
	 * 
	 * @return true if suport GUI false if not
	 */
	public boolean asUI();

	/**
	 * This method will be called by the ListenerManager when new report is
	 * added.
	 * 
	 * @param title
	 *            the report title.
	 * @param message
	 *            the report message
	 * @param isPass
	 *            true if pass
	 * @param bold
	 *            true if bold
	 */
	void report(String title, String message, boolean isPass, boolean bold);
	
	
	/**
	 * This method will be called by the ListenerManager when new report is
	 * added.
	 * note: this method added to manage situation with warning status
	 * 
	 * @param title
	 *            the report title.
	 * @param message
	 *            the report message
	 * @param status
	 *            pass/fail/warning
	 * @param bold
	 *            true if bold
	 */
	void report(String title, String message, int status, boolean bold);
	
	/**
	 * Get the reporter unick name.
	 * 
	 * @return the reporter name.
	 */
	public String getName();

	/**
	 * Init the reporter
	 * 
	 * @author guy.arieli
	 * 
	 */
	public void init();

}
