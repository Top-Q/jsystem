/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

/**
 * use this class to upload log files to http server (instead of ftp server)
 * Object of this class create zip file from log directory and after that send
 * multipart request to http server
 */
public class UploadRunner {

	private String serverUrl = null;

	private String filePath = null;

	private long logIndex;

	private File logDir;

	private PostMethod filePost;

	private static Logger log = Logger.getLogger(UploadRunner.class.getName());

	/**
	 * 
	 * @param logDir
	 *            -log files directory
	 * @param logIndex
	 *            -current log index
	 */
	public UploadRunner(File logDir, long logIndex)

	{
		super();
		this.logDir = logDir;
		this.logIndex = logIndex;
	}

	public void zipFile() throws Exception {

		/**
		 * zip log directory
		 */
		filePath = System.getProperty("user.dir") + File.separator + String.valueOf(logIndex) + ".zip";

		FileUtils.zipDirectory(logDir.getAbsolutePath(), null, filePath);

		/**
		 * get server properties from db.properties file
		 */
		this.serverUrl = "http://" + getServerUrl() + "/reports/upload";
	}

	public void setFilePath() throws Exception {

		/**
		 * zip log directory
		 */
		filePath = System.getProperty("user.dir") + File.separator + String.valueOf(logIndex) + ".zip";

		/**
		 * get server properties from db.properties file
		 */
		this.serverUrl = "http://" + getServerUrl() + "/reports/upload";
	}

	/**
	 * Gets the server URL according to the parameters specified in the JSystem
	 * properties
	 */
	private static String getServerUrl() throws Exception {
		return JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.REPORTS_PUBLISHER_HOST) + ":"
				+ JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.REPORTS_PUBLISHER_PORT);
	}

	/**
	 * get reports application url.
	 */
	public static String getReportsApplicationUrl() throws Exception {
		final String host = JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.REPORTS_PUBLISHER_HOST);
		final String port = JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.REPORTS_PUBLISHER_PORT);
		return "http://" + host + ":" + port + "/report-service/index.html";
	}

	/**
	 * get server properties from db.properties file
	 */
	public static boolean validateUrl(String url) {
		try {
			URL _url = new URL(url);
			_url.openConnection().connect();
			return true;
		} catch (Exception e) {
			log.log(Level.FINE, "Failed validating url " + url, e);
			return false;
		}
	}

	/**
	 * use jacarta http client Send to the server(servlet)multipart request
	 * server IP must be writen in db.properties -serverIP="you server ip"
	 * 
	 * @throws Exception
	 */
	public void upload() throws Exception {
		filePost = new PostMethod(serverUrl);

		/**
		 * create multipart request
		 */
		try {
			File targetFile = new File(filePath);
			Part[] parts = { new FilePart(targetFile.getName(), targetFile) };

			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));

			HttpClient client = new HttpClient();

			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

			/**
			 * send request
			 */
			int status = client.executeMethod(filePost);

			/**
			 * upload fail
			 */
			if (status != HttpStatus.SC_OK) {

				throw new Exception("Publish error : fail upload files to " + serverUrl + " \n\n"
						+ "Unable upload file " + filePath + "\n" + "HTTP Status " + status + "\n");

			}
		}
		/**
		 * release connection-must delete zip file in client log directory
		 */
		finally {
			if (filePost != null) {
				filePost.releaseConnection();
			}
			File file = new File(filePath);
			file.delete();
		}

	}
}
