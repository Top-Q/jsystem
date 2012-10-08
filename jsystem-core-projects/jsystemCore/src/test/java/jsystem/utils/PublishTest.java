/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import junit.framework.Assert;
import junit.framework.SystemTestCase4;
import org.junit.Test;

public class PublishTest extends SystemTestCase4 {
	public PublishTest() {
		super();
	}
	
	private void testSendMail() throws Exception{
		report.step("remove all messages from inbox that might have been sent before without being read properly");
		report.step("Read old mails, "+getGmailMessages(-1).length +" Messages found");
		MailUtil mail = new MailUtil();
		mail.setSsl(false);
		mail.setSmtpHostName("localhost");
		mail.setSmtpPort(25);
		mail.setFromAddress("ignis@localhost");
		mail.setUserName("ignis");
		mail.setPassword("ignissoft");
		mail.setSendTo("ignis2@localhost");
		mail.sendMail("title", "this is a test mail");
		report.step("wait for 10 seconds to let mail get to inbox");
		sleep(10000);
	}
	
	/**
	 * runs the testSendMail() method to send a mail to ignisTesting mail account
	 * and then pulls the mail from the mail box and checks the mail arrived.
	 * @throws Exception
	 */
	@Test
	public void checkMailArrived() throws Exception{
		testSendMail();
		boolean mailArrived = false;
		MailUtil mail = new MailUtil();
		mail.setSsl(false);
		mail.setPopPort(110);
		mail.setUserName("ignis2");
		mail.setPassword("ignissoft");
		mail.setPopHost("localhost");
		MailMessage[] messeges = mail.getMail();
		report.report("number of messages extracted is "+messeges.length);
		for (MailMessage message : messeges){
			if("this is a test mail".equals(message.getContent()) && "title".equals(message.getSubject())){
				mailArrived = true;
				break;
			}
			else{
				mailArrived = false;
			}
		}
		if(mailArrived == false){
			report.report("mail wasn't found in inbox");
		}
		report.step("Testing result of fetching the mail");
		Assert.assertEquals(true, mailArrived);
	}
	
	private MailMessage[] getGmailMessages(int amount) throws Exception{
		MailUtil mail = new MailUtil();
		mail.setUserName("ignis2");
		mail.setPassword("ignissoft");
		mail.setPopHost("localhost");
		mail.setSsl(false);
		mail.setPopPort(110);
		MailMessage[] messages;
		messages = mail.getMail();
		report.report("the number of mail fetched before send is: "+messages.length);
		return messages;
	}
}
