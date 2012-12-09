/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.publisher;

import java.util.Map;

/**
 * The publisher is used to manage the output results. You can move them to an
 * Ftp server like FtpPublisher or put them in an SQL server or publish to a
 * dedicated report server.
 * 
 * @author Itai.Agmon
 * 
 */
public interface Publisher {

	Map<String, String> publish(String description, boolean uploadLogs) throws PublisherException;

	/**
	 * 
	 * @param description
	 *            Description of the execution
	 * @param uploadLogs
	 *            Publish also the logs
	 * @return General purpose list of values. The most common usage of those
	 *         values is adding them to the body of the mail.
	 * @throws Exception
	 */
	Map<String, String> publish(String description, boolean uploadLogs, String[] publishOptions)
			throws PublisherException;

	/**
	 * This method will validate that the publisher server is up. The publisher
	 * settings can be kept in the jsystem.properties.
	 * 
	 * @return true if and only if the server is up
	 */
	boolean isUp();

	/**
	 * Implementation specific publish options
	 * 
	 * @return 
	 */
	String[] getAllPublishOptions();
}
