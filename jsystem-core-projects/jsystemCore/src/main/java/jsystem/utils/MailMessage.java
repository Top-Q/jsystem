/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

/**
 * A representation of a Mail Message.
 * used by the MailUtil
 * 
 * @author Nizan
 */
public class MailMessage {

	private String subject,description,content = "";
	private String[] from,to;
	private int number;
	private Date date;
	private ArrayList<File> attachments = new ArrayList<File>();
	private String attachmentsDir = null;
	private Message message;
	
	/**
	 * Process email message, extract email message attributes (subject, description, content, etc.) 
	 * @param message - email message to analyze
	 * @throws MessagingException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public MailMessage(Message message) throws MessagingException, IOException, FileNotFoundException {
		this.message = message;
		mailMessage();
	}
	
	/**
	 * Process email message, extract email message attributes (subject, description, content, etc.) 
	 * @param message - email message to analyze
	 * @param attachmentsDir - directory full path to save email attachments
	 * @throws MessagingException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public MailMessage(Message message, String attachmentsDir) throws MessagingException, IOException, FileNotFoundException {
		this.message = message;
		this.attachmentsDir = attachmentsDir;
		mailMessage();
	}
	
	private void mailMessage() throws MessagingException, IOException, FileNotFoundException {
		Address[] addresses = message.getFrom();
		from = new String[0];
		if (addresses != null) {
			from = new String[addresses.length];
			for (int i = 0; i < addresses.length; i++) {
				from[i] = addresses[i].toString();
			}
		}

		addresses = message.getAllRecipients();
		to = new String[0];
		if (addresses != null) {
			to = new String[addresses.length];
			for (int i = 0; i < addresses.length; i++) {
				to[i] = addresses[i].toString();
			}
		}

		subject = message.getSubject();
		description = message.getDescription();
		Object o = message.getContent();

		/*
		 *  A message body formatted according to the Multipurpose Internet Mail Extensions (MIME) 
		 *  specification is subdivided into parts that are organized as a hierarchy. 
		 *  As with any hierarchy, some parts contain other parts called descendants, 
		 *  and all parts have ancestors except for the top part, called the root. 
		 *  Each part or subdivision of the body is defined as a body part. 
		 *  Body parts that do not contain other body parts are defined as content body parts, 
		 *  and body parts that do contain other body parts are defined as multipart body parts.
		 */
		if (o instanceof Multipart) {
			Multipart multi = (Multipart) o;
			/*
			 * 	Multipart is a container that holds multiple body parts. so, if 'o' is instance of Multipart, 
			 *  we need to get each part of the Multipart and process it
			 */
			for (int i = 0; i < multi.getCount(); i++) { 
				Part part = multi.getBodyPart(i);
				//The disposition describes how the part should be presented to the user.
				String disposition = part.getDisposition();
				/*
				 * looking for attachment parts...
				 * (INLINE attachment usually is an attachment that we can see direclty within the email message body.)
				 */
				if ((disposition != null)&& ((disposition.equals(Part.ATTACHMENT) || 
						disposition.equals(Part.INLINE))))  { 
					//Save attachment file into attachments directory
					saveFile(part, attachmentsDir);
				}
				else {
					//Concatenate text/plain into mail message content
					setContent(part);
				}
			}
		}
		else {
			content = o.toString();
		}
		number = message.getMessageNumber();
		date = message.getSentDate();
	}
	
	/**
	 * get all from addresses array
	 * @return
	 */
	public String[] getFromAddresses() {
		return from;
	}

	/**
	 * get all recipients array
	 * @return
	 */
	public String[] getRecipients() {
		return to;
	}

	/**
	 * get the message subject
	 * @return
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * get the message description
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * get the message content
	 * @return
	 */
	public String getContent() {
		return content;
	}

	/**
	 * the message index
	 * @return
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * the date the message was sent
	 * @return
	 */
	public Date getDate() {
		return date;
	}
	
	public ArrayList<File> getAttachments() {
		return attachments;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n*************************************************\n");
		buffer.append("\nMessage number: "+number+"\n");
		buffer.append("Subject: "+subject+"\n");
		buffer.append("Date: "+date+"\n");
		buffer.append("From: "+StringUtils.objectArrayToString(",", (Object[])from)+"\n");
		buffer.append("To: "+StringUtils.objectArrayToString(",", (Object[])to)+"\n");
		buffer.append("\n: "+content+"\n");
		buffer.append("\n*************************************************\n");
		return buffer.toString();
	}

	private void saveFile(Part part, String attachmentsDir) throws MessagingException, IOException, FileNotFoundException {
		if (attachmentsDir == null) {
			//set default path to save attachment
			attachmentsDir = "C:\\MailMessage\\Attachments";
		}
		File destinationDir = new File(attachmentsDir);
		if (!destinationDir.exists()) {
			destinationDir.mkdir();
		}
		File tempFile = new File(attachmentsDir + File.separator + part.getFileName());
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(tempFile);
			is = part.getInputStream();
			int byteCount = 0;
			byte[] bytes = new byte[128];
			while ((byteCount = is.read(bytes, 0, bytes.length)) > -1) { 
				fos.write(bytes, 0, byteCount);
			}
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException ioe) {
			}
			try {
				if (is != null)
					is.close();
			} catch (IOException ioe) {
			}
			attachments.add(tempFile);
		}
    }

	/**
	 * Set email text content.
	 * @param part
	 * @throws MessagingException
	 * @throws IOException
	 */
	private void setContent(Part part) throws MessagingException, IOException {		
		if (part.isMimeType("text/*")) {
			String s = (String)part.getContent();
			if (!part.isMimeType("text/html") && s != null) { //Jump over html text
				//Concatenate only 'text/plain' into content
				content += s;
			}
		}
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart)part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				setContent(mp.getBodyPart(i));
			}
		}
	}

}
