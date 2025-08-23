/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.File;

import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FromTerm;
import javax.mail.search.SubjectTerm;

import jsystem.framework.system.SystemObjectImpl;

/**
 * this class is used for sending\receiving mail<br>
 * <b>Usage:</b><br>
 * 
 * <b>For Sending:</b><br>
 * <UL>
 * 1) create a new object<br>
 * <br>
 * 2) set the following parameters: 
 * <UL>
 * a) address to send from (user@domain.com)<br> 
 * b) sender user name (user)<br>
 * c) sender password (password)<br>
 * d) mail host (for example: smtp.gmail.com for gmail)<br>
 * e) is host secured (ssl true)<br>
 * f) sending host port number (465 for gmail)<br>
 * g) mail to send to<br>
 *</UL>
 * 3) use <I>sendMail(Title,Message)</I> to send a message with a given title
 * </UL>
 * <br>
 * <br>
 * 
 * <b>For Receiving:</b><br>
 * <UL>
 * 1) create a new object<br>
 * <br>
 * 2) set the following parameters:
 * <UL>
 * a) user name<br> 
 * b) password<br>
 * c) Pop host (for Gmail - pop.gmail.com)<br>
 * d) Pop port (For Gmail - 995)<br>
 * e) Attachments directory - if the email contains attachments file, 
 *    set the directory full path to save email attachments. the default path is: ..\log\current\<current-test>\Attachments<br>
 *</UL>
 * 3) use <I>getMail(amount)</I> to receive amount messages
 * </UL>
 * @author nizanf
 *
 */
public class MailUtil extends SystemObjectImpl{

	/**
	 * secured host port number
	 */
	public final static int SSL_PORT = 465;

	/**
	 * unsecured host port number
	 */
	public final static int NOT_SSL_PORT = 25;
	
	/**
	 * Socket factory const
	 */
	protected final static String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	/**
	 * the sending account host 
	 */
	protected String smtpHostName;

	/**
	 * the port the smtp host works on
	 */
	protected int smtpPort = -1;

	/**
	 * is the connection secured?
	 */
	protected boolean ssl = false;
	
	/**
	 * the address to send from
	 */
	protected String fromAddress = null;

	/**
	 * the addresses to send mail to
	 */
	private String[] sendTo = null;
	
	/**
	 * the addresses to Carbon copy to
	 */
	private String[] sendCc = null;
	
	/**
	 * the addresses to Blind carbon copy to
	 */
	private String[] sendBcc = null;

	/**
	 * the directory full path to save email attachments
	 */
	private String attachmentsDir = null;

	/**
	 * if True will print debug info
	 */
	protected boolean isDebug = true;

	/**
	 * the account to login from user name
	 */
	protected String userName =null; 

	/**
	 * the password of the account sending from 
	 */
	protected String password = null;
	
	protected String popHost;

	protected int popPort;
	
	protected String[] attachments;
	
	protected boolean mailMessageAsHtmlText = false;
	
	/**
	 * used after configurating all parameters<br>
	 * sends a message with a given title
	 * 
	 * @param title	the mail title
	 * @param msgContent	the msg content
	 * @throws Exception
	 */
	public void sendMail(String title,String msgContent) throws Exception {

		Properties props = new Properties();

		if (ssl) {
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.socketFactory.port", ""+smtpPort);
			props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
			props.put("mail.smtp.socketFactory.fallback", "false");
		}

		props.put("mail.smtp.host", smtpHostName);

		props.put("mail.debug", isDebug + "");
		props.put("mail.smtp.port", ""+smtpPort);

		Session session = null;
		
		if (password != null) {
			props.put("mail.smtp.auth", "true");
			session = Session.getInstance(props, new javax.mail.Authenticator() {

				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, password);
				}
			});
		}else{
			session = Session.getInstance(props, null);
		}

		Message msg = new MimeMessage(session);
		InternetAddress addressFrom = null;
		if (userName != null){
			report.report("Sending mail from "+fromAddress+" with user "+userName);
			addressFrom = new InternetAddress(/* from */userName,fromAddress);
		}
		else {
			report.report("Sending mail from "+fromAddress);
			addressFrom = new InternetAddress(/* from */fromAddress);
		}	
		msg.setFrom(addressFrom);

		msg.setRecipients(Message.RecipientType.TO, getAddresses(sendTo));
		msg.setRecipients(Message.RecipientType.CC, getAddresses(sendCc));
		msg.setRecipients(Message.RecipientType.BCC, getAddresses(sendBcc));

		// Setting the Subject and Content Type
		msg.setSubject(title);
		
		/********* add attachments **********/
		Multipart multipart = new MimeMultipart();
		// create the message part 
		MimeBodyPart messageBodyPart = new MimeBodyPart();

		//fill message
		if (mailMessageAsHtmlText){
			messageBodyPart.setContent(msgContent, "text/html");
		}
		else{
			messageBodyPart.setText(msgContent);
		}
		
		multipart.addBodyPart(messageBodyPart);
		if (attachments!=null){	
			for (String file : attachments){
				if (!StringUtils.isEmpty(file)){
				    report.report("Attaching "+file);
					// Part two is attachment
				    messageBodyPart = new MimeBodyPart();
				    DataSource source = new FileDataSource(file);
				    messageBodyPart.setDataHandler(new DataHandler(source));
				    messageBodyPart.setFileName(FileUtils.getFileNameWithoutFullPath(file));
				    multipart.addBodyPart(messageBodyPart);
				}
			}
		}
	    // Put parts in message
	    msg.setContent(multipart);
		/************* attachments end ******/
		Transport.send(msg);
	}
	
	private InternetAddress[] getAddresses(String[] addresses) throws AddressException{
		if (addresses == null){
			return new InternetAddress[0];
		}
		
		InternetAddress[] internetAaddresses = new InternetAddress[addresses.length];
		for (int i = 0; i < addresses.length; i++) {
			internetAaddresses[i] = new InternetAddress(addresses[i]);
		}
		return internetAaddresses;
	}
	
	
	/**
	 * get all messages
	 * 
	 * @return			an array of all messages (Oldest to newest)
	 * @throws Exception
	 */
	public MailMessage[] getMail() throws Exception{
		return getMail(false);
	}
	
	/**
	 * Get all messages
	 * 
	 * @param delete	if true will delete all available messages after reading
	 * @return		an array of all messages (Oldest to newest)
	 * @throws Exception
	 */
	public MailMessage[] getMail(boolean delete) throws Exception{
		return getMail("", 1,Integer.MAX_VALUE, true, delete);
	}
	
	/**
	 * get the first ${amount} messages
	 * 
	 * @param amount	the maximum index of message to get, first is Oldest
	 * @return			an array of all messages from index 1 to amount (Oldest to newest)
	 * @throws Exception
	 */
	public MailMessage[] getMail(int amount) throws Exception{
		return getMail("", 1,amount, true, false);
	}
	
	/**
	 * get all mail messages matching the given subject filter and numbers
	 * 
	 * @param subject	if not null or empty string will filter messages
	 * @param amount	the amount of messages to get
	 * @param firstIsOldest if true then the indexes will be counted from the oldest, otherwise counted from the newest
	 * @return			an array of all messages that match the subject (if requested) and match the numbers range (always sorted from oldest to newest)
	 * @throws Exception
	 */
	public MailMessage[] getMail(String subject ,int amount, boolean firstIsOldest) throws Exception{
		return getMail(subject, 1, amount, firstIsOldest, false);
	}
	
	/**
	 * get all mail messages matching the given subject filter and numbers
	 * @param fromAddress	if not null or empty string will filter messages
	 * @param amount		the amount of messages to get
	 * @param firstIsOldest if true then the indexes will be counted from the oldest, otherwise counted from the newest
	 * @param deleteMessages if True will mark messages for deletion
	 * @return			an array of all messages that match the subject (if requested) and match the numbers range (always sorted from oldest to newest)
	 * @throws Exception
	 */
	public MailMessage[] getMail(String mailFrom,int amount, boolean firstIsOldest, boolean deleteMessages) throws Exception{
		return getMail(fromAddress, "", 1, amount, firstIsOldest, deleteMessages);
	}
	
	/**
	 * get all mail messages matching the given subject filter and numbers
	 * 
	 * @param subject	if not null or empty string will filter messages
	 * @param start		the first message index
	 * @param end		the last message index
	 * @param firstIsOldest if true then the indexes will be counted from the oldest, otherwise counted from the newest
	 * @param deleteMessages if True will mark messages for deletion
	 * @return			an array of all messages that match the subject (if requested) and match the numbers range (always sorted from oldest to newest)
	 * @throws Exception
	 */
	public MailMessage[] getMail(String subject,int start, int end, boolean firstIsOldest, boolean deleteMessages) throws Exception{
		return getMail("", subject, start, end, firstIsOldest, deleteMessages);
	}
	
	private MailMessage[] getMail(String fromAddress, String subject,int start, int end, boolean firstIsOldest, boolean deleteMessages) throws Exception{
		
		// Setup properties
		Properties props = new Properties();
		props = System.getProperties();
		if (ssl){
			props.setProperty( "mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
        props.setProperty( "mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.pop3.port", popPort+"");
        props.setProperty("mail.pop3.socketFactory.port", popPort+"");
        props.setProperty("mail.debug", isDebug + "");
		
        
		// Get session
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		});


		// Get the store
		Store store = session.getStore("pop3");
		store.connect(popHost, userName, password);

		
		// Get folder
		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_WRITE);
		
		Message[] messages;

		// calculate message indexes
		int messageAmount = folder.getMessageCount(); 
		if (isDebug){
			System.out.println("Number of messages found = "+messageAmount);
		}
		int maxMessages = Math.min(end,messageAmount);
		if (!firstIsOldest){ // fix index request
			int tmp = messageAmount-maxMessages+1;
			end = messageAmount-start+1;
			start = tmp;
			maxMessages = end;
		}
		
		// Get directory
		if (!StringUtils.isEmpty(subject)){ // filter by subject
			SubjectTerm subjectTerm = new SubjectTerm(subject);
			messages = folder.search(subjectTerm);
		}else if (!StringUtils.isEmpty(fromAddress)){ // filter by mail from address
			FromTerm fromTerm = new FromTerm(new InternetAddress(fromAddress));
			messages = folder.search(fromTerm);
		}else if (maxMessages>0){
			messages = folder.getMessages(start,maxMessages);
		}else{
			messages = new Message[0];
		}
		
		int numOfMessages = messages.length;
		
		ArrayList<MailMessage> returnMessages = new ArrayList<MailMessage>();
		for (int i=0 ; i<numOfMessages ; i++){
			int num = messages[i].getMessageNumber();
			if (num >= start && num<=end){
				if (attachmentsDir == null) {
					attachmentsDir = report.getCurrentTestFolder() + File.separator + "\\Attachments";
				}
				returnMessages.add(new MailMessage(messages[i], attachmentsDir));
			}
			if (deleteMessages){
				messages[i].setFlag(Flags.Flag.DELETED, true);
			}
		}
		
		// Close connection 
		folder.close(true);
		store.close();

		return returnMessages.toArray(new MailMessage[0]);
	}

	/**
	 * the account to use for sending the message
	 * @param fromAddress
	 */
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	/**
	 * if true will show debug info
	 * @param isDebug
	 */
	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	/**
	 * the password of the sending mail
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * the array of addresses to send to
	 * @param sendTo
	 */
	public void setSendTo(String... sendTo) {
		this.sendTo = sendTo;
	}

	/**
	 * the smtp host name (smtp.gmail.com for example)
	 * @param smtpHostName
	 */
	public void setSmtpHostName(String smtpHostName) {
		this.smtpHostName = smtpHostName;
	}

	/**
	 * the sending user name
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * if True then the connection is secured
	 * @param ssl
	 */
	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	/**
	 * the smtp port to send from
	 * @param smtpPort
	 */
	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	/**
	 * the pop host to receive mail from (pop.gmail.com for Gmail)
	 * @param popHost
	 */
	public void setPopHost(String popHost) {
		this.popHost = popHost;
	}

	/**
	 * the pop port to receive mail through (995 for Gmail)
	 * @param popPort
	 */
	public void setPopPort(int popPort) {
		this.popPort = popPort;
	}


	/**
	 * an array of all File attachments to be attached to mail
	 * @param attachments
	 */
	public void setAttachments(String... attachments) {
		this.attachments = attachments;
	}
	
	/**
	 * set mail message as html text - this option enable to format mail message with html code.
	 * @param mailMessageAsHtmlText
	 */
	public void setMailMessageAsHtmlText(boolean mailMessageAsHtmlText) {
		this.mailMessageAsHtmlText = mailMessageAsHtmlText;
	}

	/**
	 * the array of addresses to send Carbon copy
	 * @param sendCc
	 */
	public void setSendCc(String[] sendCc) {
		this.sendCc = sendCc;
	}

	/**
	 * the array of addresses to send Blind carbon copy
	 * @param sendBcc
	 */
	public void setSendBcc(String[] sendBcc) {
		this.sendBcc = sendBcc;
	}

	/**
	 * the directory full path to save email message attachments
	 * @param attachmentsDir
	 */
	public void setAttachmentsDir(String attachmentsDir) {
		this.attachmentsDir = attachmentsDir;
	}
}