/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.publisher;

/**
 * The publisher is used to manage the output results. You can move them to an
 * Ftp server like FtpPublisher or put them in an SQL server ...
 * 
 * @author guy.arieli
 * 
 */
public interface Publisher {

	void publish(String description, boolean uploadLogs) throws Exception;
	
	/**
	 * 
	 * @param description
	 *            Description of the execution
	 * @param uploadLogs
	 *            Publish also the logs
	 * @throws Exception
	 */
	void publish(String description, boolean uploadLogs, String[] publishOptions) throws Exception;

	/**
	 * This method will validate the publisher according to the setting received
	 * from the DB settings dialog.
	 * 
	 * @param Object
	 *            need to be cast to DBConnectionListener in case of
	 *            DefaultPublisher else if report server publisher will not use
	 *            this parameter. Passed as object due to dependency problem
	 *            between agent and app;
	 * @param dbSettingParams
	 *            represents the following: (host, port, driver, type, dbHost,
	 *            dbName, dbUser, dbPassword);
	 * @return
	 */
	boolean isUp();

	/**
	 * Implementation specific publish options
	 * 
	 * @return
	 */
	String[] getAllPublishOptions();
}
