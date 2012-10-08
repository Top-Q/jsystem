package jsystem.runner.agent.tests;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.extensions.report.xml.ReportInformation;
import jsystem.extensions.report.xml.XmlReportHandler;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.utils.Encryptor;
import jsystem.utils.MailUtil;
import jsystem.utils.StringUtils;

/**
 * Sender of e-mails of tests results by using MailUtils
 * 
 * @author Daniel Haimov
 * 
 */
public class MailSender {

	private Reporter report = ListenerstManager.getInstance();
	private Logger log = Logger.getLogger(MailSender.class.getName());

	/**
	 * the separator for the files
	 */
	private String messageHeader;
	private String mailSubject;
	private String sendTo;
	private boolean addSummaryReport = true;

	/**
	 * Get the results of tests and initialize the data for sending e-mail
	 * 
	 * @param reqMsg
	 * @param responseMsg2
	 * @throws Exception
	 */
	public void sendMail(String filesToAttach, boolean isPublished) throws Exception {

		if (!isMailFrameworkOptionsDefined()) {
			report.report("Can't email results, since mail properties are not configured in the JSystem properties",
					false);
			throw new Exception(
					"Can't email results, since mail properties are not configured in the JSystem properties");
		}

		String emailMessage = generateMsgForEmail(isPublished);
		String fromAddress = JSystemProperties.getInstance().getPreference(FrameworkOptions.MAIL_FROM_ACCOUNT_NAME);
		String user = JSystemProperties.getInstance().getPreference(FrameworkOptions.MAIL_FROM_USER_NAME);
		String password = JSystemProperties.getInstance().getPreference(FrameworkOptions.MAIL_FROM_PASSWORD);
		String hostName = JSystemProperties.getInstance().getPreference(FrameworkOptions.MAIL_HOST);
		boolean ssl = Boolean.parseBoolean(JSystemProperties.getInstance().getPreference(FrameworkOptions.MAIL_SSL));
		int smtpPort = Integer.parseInt(JSystemProperties.getInstance().getPreference(FrameworkOptions.MAIL_SMTP_PORT));

		MailUtil mail = new MailUtil();
		mail.setDebug(false);
		mail.setFromAddress(fromAddress);

		String sendToTrim = sendTo.trim();
		mail.setSendTo(sendToTrim.split(CommonResources.DELIMITER));
		report.report("Send to is " + sendToTrim);
		manageAttachments(filesToAttach, mail);
		mail.setSmtpHostName(hostName);
		report.report("Smtp host is " + hostName);
		mail.setSsl(ssl);
		report.report("Ssl is " + ssl);
		mail.setSmtpPort(smtpPort);
		report.report("Smtp port is " + smtpPort);
		mail.setUserName(user);
		report.report("User name is " + user);
		report.step("Sending mail to " + sendTo);
		messageHeader = messageHeader == null ? "" : messageHeader;
		report.report("Message header is " + messageHeader);
		if (!StringUtils.isEmpty(password)){
			mail.setPassword(decryptPassword(password));
		}

		try {
			mail.sendMail(mailSubject, messageHeader + "\n\n" + emailMessage);
		} catch (Exception exception) {
			String stackTraceAsString = StringUtils.getStackTrace(exception);
			log.log(Level.SEVERE, "Problem sending mail!\n\n" + stackTraceAsString);
			report.report("Problem sending mail! \n" + exception.getMessage(), stackTraceAsString, false);
			throw exception;
		}
	}

	private void manageAttachments(String filesToAttach, MailUtil mail) {
		// Add summary report
		if (isAddSummaryReport()) {
			final String summaryFileName = JSystemProperties.getInstance().getPreference(FrameworkOptions.LOG_FOLDER)
					+ File.separator + "current" + File.separator + "summary.html";
			if (!StringUtils.isEmpty(filesToAttach)) {
				filesToAttach += ";" + summaryFileName;
			} else {
				filesToAttach = summaryFileName;
			}
		}

		//Filter non existing files
		if (!StringUtils.isEmpty(filesToAttach)) {
			String[] filesToAttachArr = filesToAttach.split(CommonResources.DELIMITER);
			StringBuilder existingFilesToAttach = new StringBuilder();
			for (String fileName : filesToAttachArr) {
				if (new File(fileName).exists()) {
					existingFilesToAttach.append(fileName).append(CommonResources.DELIMITER);
				}
			}
			filesToAttach = existingFilesToAttach.toString();
		}

		if (!StringUtils.isEmpty(filesToAttach)) {
			mail.setAttachments(filesToAttach.split(CommonResources.DELIMITER));
		}

	}

	private String decryptPassword(String origPassword) throws Exception {
		return (Encryptor.isEncrypted(origPassword)) ? Encryptor.decrypt(origPassword) : origPassword;
	}

	/**
	 * Checks that all the properties were defined by the user
	 * 
	 * @return true if and only if all mail properties were defined by user
	 */
	private boolean isMailFrameworkOptionsDefined() {
		final FrameworkOptions[] optionsToCheck = new FrameworkOptions[] { FrameworkOptions.MAIL_FROM_USER_NAME,
				FrameworkOptions.MAIL_HOST, FrameworkOptions.MAIL_SMTP_PORT, FrameworkOptions.MAIL_FROM_ACCOUNT_NAME };
		for (FrameworkOptions option : optionsToCheck) {
			if (JSystemProperties.getInstance().getPreference(option) == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * generates message that will be send as email message if the user
	 * published the data to the webServer it will add the relevant links
	 * 
	 * @param reqMsg
	 * 
	 * @param msg
	 *            message that was get from the RemoteTestRunner class
	 * @return message that will be send via the email
	 * @throws Exception
	 */
	public String generateMsgForEmail(boolean isPublished) throws Exception {
		ReportInformation info = XmlReportHandler.getInstance();
		info.refresh();
		final String EOL = "\n";
		StringBuilder sb = new StringBuilder("This is automatically email that was sent by the JSystem").append(EOL);
		sb.append("Scenario Name : ").append(info.getScenarioName()).append(EOL);
		sb.append("Setup Name : ").append(info.getSutName()).append(EOL);
		sb.append("Version : ").append(info.getVersion()).append(EOL);
		sb.append("Build : ").append(info.getBuild()).append(EOL);
		sb.append("Station : ").append(info.getStation()).append(EOL);
		sb.append("User Name : ").append(info.getUserName()).append(EOL);
		sb.append("Start Time : ").append(new SimpleDateFormat().format(new Date(info.getStartTime()))).append(EOL)
				.append(EOL);
		sb.append("Run Tests : ").append(info.getNumberOfTests()).append(EOL);
		sb.append("Failed Tests : ").append(info.getNumberOfTestsFail()).append(EOL);
		sb.append("Warning Tests : ").append(info.getNumberOfTestsWarning()).append(EOL).append(EOL);
		if (isPublished) {
			sb.append("This scenario was published to the reports system").append(EOL);
		} else {
			sb.append("This scenario wasn't published to the reports system").append(EOL);
		}

		return sb.toString();
	}

	public String getMessageHeader() {
		return messageHeader;
	}

	public void setMessageHeader(String messageHeader) {
		this.messageHeader = messageHeader;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getSendTo() {
		return sendTo;
	}

	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	public boolean isAddSummaryReport() {
		return addSummaryReport;
	}

	/**
	 * Should the Summary report be attached to the mail?
	 * 
	 * @param addSummaryReport
	 */
	public void setAddSummaryReport(boolean addSummaryReport) {
		this.addSummaryReport = addSummaryReport;
	}

}
