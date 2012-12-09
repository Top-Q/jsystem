/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

import jsystem.extensions.report.xml.XmlReportHandler;
import jsystem.extensions.report.xml.XmlReporter;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.ParameterProperties;
import jsystem.framework.TestProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.report.JSystemListeners;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.scenario.Parameter;
import jsystem.runner.agent.publisher.PublisherException;
import jsystem.runner.agent.publisher.PublisherManager;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import junit.framework.SystemTestCase4;

import org.junit.Test;

/**
 * This class contains a pre-defined test that can be used for notification -
 * email or publish to DB, with or without initializing the reports.
 * 
 * @author YuvalO, yoram.shamir, Itai Agmon
 * 
 */
public class PublishTest extends SystemTestCase4 {

	protected static Logger log = Logger.getLogger(PublishTest.class.getName());

	public final static String delimiter = ":ABCDEDCBA:";
	/**
	 * properties to be written to run.properties for publish URL
	 */
	public final static String LAST_PUBLISH_FULL_REPORT_URL = "last.publish.full.report.url";
	public final static String LAST_PUBLISH_DETAIL_URL = "last.publish.detail.url";

	/**
	 * the separator for the files
	 */
	public static final String VALUES_SEPARATOR = CommonResources.DELIMITER;

	private static final String[] EMAIL_PARAMS = { "SendTo", "Attachments", "SummaryAttachment", "MessageHeader",
			"MailSubject" };

	private static final String[] PUBLISH_PARAMS = { "ExecutionPropertiesStr", "UploadLogs", "Description",
			"PublishOptions" };

	private static final String[] EMAIL_AND_PUBLISH_PARAMS = { "Build", "Version" };

	/**
	 * Notification type <br>
	 * publish - only publish, email - only send email, publish_and_email -
	 * publish and send email, init_reporters_only - only initialize reports
	 */
	public enum ActionType {
		publish, email, publish_and_email, init_reporters_only;
	}

	/**
	 * Optional file for different publishing parameters
	 */
	private String valuesFile = System.getProperty("user.dir") + "\\publishEventOptions.properties";

	private Properties valueProperties;

	/************** Test Parameters Start **********************/

	/**
	 * current action type that the user select
	 */
	public ActionType actionType = ActionType.publish;

	/**
	 * description of the publish - input from the user
	 */
	public String description = "";

	public String[] descriptionOptions = null;

	/**
	 * version of the sut - input from the user
	 */
	public String version = "";

	public String[] versionOptions = null;

	private String executionPropertiesStr;

	/**
	 * build of the dut- input from the user
	 */
	public String build = "";

	public boolean uploadLogs = true;

	public String[] buildOptions = null;

	/**
	 * If true, initialize reporter (delete all html/xml/any reporter data)
	 * after publish/email
	 */
	public boolean initReporter = false;

	/* Mail */
	private String sendTo;
	private String attachments;
	private String messageHeader;
	private boolean summaryAttachment = true;

	/**
	 * Implementation specific publish options
	 */
	private String[] publishOptions;

	private String mailSubject;

	/************** Test Parameters End **********************/

	public PublishTest() {
		super();
		parsePublishFile();
	}

	/**
	 * parse the publish properties file
	 * 
	 */
	private void parsePublishFile() {
		if (new File(valuesFile).exists()) {
			try {
				valueProperties = FileUtils.loadPropertiesFromFile(valuesFile);
				versionOptions = getOptionsArray(XmlReporter.VERSION);
				descriptionOptions = getOptionsArray(XmlReporter.DESCRIPTION);
				buildOptions = getOptionsArray(XmlReporter.BUILD);
			} catch (IOException ioException) {
				log.fine("couldn't find file " + valuesFile);
			}
		}
		initParametersValues();
	}

	/**
	 * set initial values to parameters (if there are values in a file)
	 * 
	 */
	protected void initParametersValues() {
		if (versionOptions != null && versionOptions.length > 0) {
			version = versionOptions[0];
		}
		if (descriptionOptions != null && descriptionOptions.length > 0) {
			description = descriptionOptions[0];
		}
		if (buildOptions != null && buildOptions.length > 0) {
			build = buildOptions[0];
		}

	}

	/**
	 * Event notification from different types <br>
	 * <b>Publish</b><br>
	 * <b>Send Email</b><br>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(name = "Send notification. type = ${ActionType}")
	public void publish() throws Exception {
		report.step("Notifying...");
		final Map<String, String> executionPropertiesMap = parseExecutionProperties();
		// Just to make sure that all information is written to the reports
		ListenerstManager.getInstance().flushReporters();
		try {

			switch (actionType) {
			case init_reporters_only:
				// The init will be handled at the end of the method
				break;
			case publish:
				setReportInfo(executionPropertiesMap);
				setContainerProperties(executionPropertiesMap);
				PublisherManager.getInstance().getPublisher()
						.publish(getDescription(), isUploadLogs(), getPublishOptions());
				break;
			case email:
				setReportInfo(executionPropertiesMap);
				setContainerProperties(executionPropertiesMap);
				sendMail(getAttachments(), null, false);
				break;
			case publish_and_email:
				setReportInfo(executionPropertiesMap);
				setContainerProperties(executionPropertiesMap);
				Map<String, String> publisherReturnedMap = null;
				try {
					publisherReturnedMap = PublisherManager.getInstance().getPublisher()
							.publish(getDescription(), isUploadLogs(), getPublishOptions());
				} finally {
					// Even we didn't succeed publishing the reports, we still
					// would like to send the mail.
					sendMail(getAttachments(), publisherReturnedMap, true);
				}
				break;
			default:
				break;
			}
			if (isInitReporter()) {
				ListenerstManager.getInstance().initReporters();
			}
		} catch (PublisherException e) {
			report.report("Publishing process failed", StringUtils.getStackTrace(e), Reporter.WARNING);
		} catch (IllegalStateException e) {
			report.report("Operation aborted due to illegal state: " + e.getMessage(), Reporter.WARNING);
		} catch (Exception e) {
			report.report("Notification process failed", StringUtils.getStackTrace(e), Reporter.WARNING);
		}

	}

	public void handleUIEvent(HashMap<String, Parameter> map, String methodName) throws Exception {
		if (!"publish".equals(methodName)) {
			return;
		}
		Parameter param = map.get("ActionType");
		param.setSection("General");

		ActionType currentActionType = ActionType.valueOf(param.getStringValue());
		switch (currentActionType) {
		case init_reporters_only:
			// init reports only
			map.get("InitReporter").setValue(Boolean.TRUE);
			map.get("InitReporter").setEditable(Boolean.FALSE);
			setParametersVisibility(map, EMAIL_AND_PUBLISH_PARAMS, false);
			setParametersVisibility(map, EMAIL_PARAMS, false);
			setParametersVisibility(map, PUBLISH_PARAMS, false);
			break;

		case publish_and_email:
			map.get("InitReporter").setEditable(Boolean.TRUE);
			setParametersVisibility(map, EMAIL_AND_PUBLISH_PARAMS, true);
			setParametersVisibility(map, EMAIL_PARAMS, true);
			setParametersVisibility(map, PUBLISH_PARAMS, true);
			break;
		case publish:
			map.get("InitReporter").setEditable(Boolean.TRUE);
			setParametersVisibility(map, EMAIL_AND_PUBLISH_PARAMS, true);
			setParametersVisibility(map, EMAIL_PARAMS, false);
			setParametersVisibility(map, PUBLISH_PARAMS, true);
			break;
		case email:
			map.get("InitReporter").setEditable(Boolean.TRUE);
			setParametersVisibility(map, EMAIL_AND_PUBLISH_PARAMS, true);
			setParametersVisibility(map, EMAIL_PARAMS, true);
			setParametersVisibility(map, PUBLISH_PARAMS, false);
			break;
		default:
			log.warning("Unknown action type: " + currentActionType);
			break;
		}

		param = map.get("ExecutionProperties");
	}

	private void setParametersVisibility(final HashMap<String, Parameter> params, String[] parameterNamesToSet,
			final boolean visible) {
		Parameter param = null;
		for (String paramName : parameterNamesToSet) {
			param = params.get(paramName);
			if (param != null) {
				param.setVisible(visible);
			} else {
				System.err.println("ERROR: PublishTest.setAllVisible() - there is no parameter with the name '"
						+ paramName + "' within the map of test info parameters!");
			}
		}

	}

	/**
	 * get the options from the properties
	 * 
	 * @param key
	 *            the key to get options for
	 * @return
	 */
	private String[] getOptionsArray(String key) {
		String values = valueProperties.getProperty(key);
		if (values == null || "".equals(values = values.trim())) {
			return null;
		}
		String[] tmpValues = values.split(VALUES_SEPARATOR);
		ArrayList<String> valuesCollection = new ArrayList<String>();
		for (String value : tmpValues) {
			if (value != null && !"".equals(value = value.trim())) {
				valuesCollection.add(value);
			}
		}
		return valuesCollection.toArray(new String[valuesCollection.size()]);
	}

	/**
	 * Setting the parameters to the XML report handler. This parameters will be
	 * read by the publisher later on
	 * 
	 * @param executionPropertiesMap
	 */
	private void setReportInfo(Map<String, String> executionPropertiesMap) {
		final XmlReportHandler handler = XmlReportHandler.getInstance();
		if (null == handler) {
			throw new IllegalStateException("XML Reporter was not found");
		}
		handler.refresh();
		handler.setBuild(getBuild());
		handler.setVersion(getVersion());
		handler.setDescription(getDescription());
		handler.addExecutionProperties(executionPropertiesMap);

	}

	private void sendMail(String filesToAttach, Map<String, String> publisherReturnedMap, boolean isPublished)
			throws Exception {
		/**
		 * email clients addresses can contains more than one email client. if
		 * we want send email to more than one client we should separate it with
		 * ; . for example jsystemtest@gmail.com; info@ignissoft.com
		 */

		if (StringUtils.isEmpty(getMailSubject())) {
			setMailSubject("Automatic message from Jsystem");
		}

		final MailSender mailSender = new MailSender();

		mailSender.setMailSubject(getMailSubject());
		mailSender.setMessageHeader(getMessageHeader());
		mailSender.setSendTo(getSendTo());
		mailSender.setAddSummaryReport(isSummaryAttachment());
		mailSender.setMailContentMap(publisherReturnedMap);
		mailSender.sendMail(filesToAttach, isPublished);
	}

	/**
	 * extract parameters from the message that we get from RemoteTestRunner and
	 * put each parameter in HashMap.
	 * 
	 * @param msg
	 *            message that we get from RemoteTestRunner
	 */

	final JSystemListeners listenersMngr = ListenerstManager.getInstance();

	private void setContainerProperties(final Map<String, String> executionPropertiesMap) {
		if (executionPropertiesMap != null)
			for (String key : executionPropertiesMap.keySet()) {
				listenersMngr.setContainerProperties(Integer.MAX_VALUE, key, executionPropertiesMap.get(key));
			}
		listenersMngr.setContainerProperties(Integer.MAX_VALUE, "build", getBuild());
		listenersMngr.setContainerProperties(Integer.MAX_VALUE, "version", getVersion());
	}

	/**
	 * Parsed the execution properties string to map.
	 * 
	 * @return map of execution properties string.
	 */
	private Map<String, String> parseExecutionProperties() {
		if (null == executionPropertiesStr) {
			return null;
		}
		Map<String, String> executionPropertiesMap = new TreeMap<String, String>();
		try {
			String[] keyValueArr = executionPropertiesStr.split(";");
			for (String keyValue : keyValueArr) {
				String key = keyValue.split("=")[0];
				String value = keyValue.split("=")[1];
				executionPropertiesMap.put(key, value);
			}

		} catch (Throwable e) {
			report.report("Problem parsing execution properties");
		}
		return executionPropertiesMap;
	}

	public ActionType getActionType() {
		return actionType;
	}

	/**
	 * publish, send email or publish and send email
	 * 
	 * @param actionType
	 */
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public String getBuild() {
		return build;
	}

	public String[] getBuildOptions() {
		return buildOptions;
	}

	/**
	 * set build
	 * 
	 * @param build
	 */
	@ParameterProperties(description = "Build options can be set from external properties file - publishEventOptions.properties")
	public void setBuild(String build) {
		this.build = build;
	}

	public String getDescription() {
		return description;
	}

	public String[] getDescriptionOptions() {
		return descriptionOptions;
	}

	/**
	 * set description
	 */
	@ParameterProperties(description = "Publish description", section = "Publish")
	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public String[] getVersionOptions() {
		return versionOptions;
	}

	/**
	 * set version
	 * 
	 * @param version
	 */
	@ParameterProperties(description = "Versions options can be set from external properties file - publishEventOptions.properties")
	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isInitReporter() {
		return initReporter;
	}

	/**
	 * @param isInitReporter
	 *            if true, init reporter after publish tests
	 */
	public void setInitReporter(boolean initReporter) {
		this.initReporter = initReporter;
	}

	public String getExecutionPropertiesStr() {
		return executionPropertiesStr;
	}

	@ParameterProperties(description = "Execution properties: key0=value0;key1=value1;key2=value2", section = "Publish")
	public void setExecutionPropertiesStr(String executionPropertiesStr) {
		this.executionPropertiesStr = executionPropertiesStr;
	}

	public boolean isUploadLogs() {
		return uploadLogs;
	}

	@ParameterProperties(description = "Upload HTML logs to the report server", section = "Publish")
	public void setUploadLogs(boolean uploadLogs) {
		this.uploadLogs = uploadLogs;
	}

	// ****Start Mail Parameters ***/
	public String[] getPublishOptions() {
		return publishOptions;
	}

	public String[] getPublishOptionsOptions() {
		return PublisherManager.getInstance().getPublisher().getAllPublishOptions();
	}

	@ParameterProperties(description = "Implementation specific publish parameters", section = "Publish")
	public void setPublishOptions(String[] configProperties) {
		this.publishOptions = configProperties;
	}

	public String getSendTo() {
		if (StringUtils.isEmpty(sendTo)) {
			sendTo = JSystemProperties.getInstance().getPreference(FrameworkOptions.MAIL_SEND_TO);
		}
		return sendTo;
	}

	@ParameterProperties(description = "", section = "Email")
	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	public String getAttachments() {
		if (StringUtils.isEmpty(attachments)) {
			attachments = JSystemProperties.getInstance().getPreference(FrameworkOptions.MAIL_ATTACHMENTS);
		}
		return attachments;
	}

	@ParameterProperties(description = "Files to attach to email. Use ';' as delimiter", section = "Email")
	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	public boolean isSummaryAttachment() {
		return summaryAttachment;
	}

	@ParameterProperties(description = "Attach the summary page of the HTML report to the mail", section = "Email")
	public void setSummaryAttachment(boolean summaryAttachment) {
		this.summaryAttachment = summaryAttachment;
	}

	public String getMessageHeader() {
		if (StringUtils.isEmpty(messageHeader)) {
			messageHeader = JSystemProperties.getInstance().getPreference(FrameworkOptions.MAIL_HEADER);
		}
		return messageHeader;
	}

	@ParameterProperties(description = "The first line the of email message", section = "Email")
	public void setMessageHeader(String messageHeader) {
		this.messageHeader = messageHeader;
	}

	public String getMailSubject() {
		if (StringUtils.isEmpty(mailSubject)) {
			mailSubject = JSystemProperties.getInstance().getPreference(FrameworkOptions.MAIL_SUBJECT);
		}
		return mailSubject;
	}

	@ParameterProperties(description = "Subject of email message", section = "Email")
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	// ****End Mail Parameters ***/

}
