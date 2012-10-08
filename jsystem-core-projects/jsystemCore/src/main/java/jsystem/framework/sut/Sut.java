/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.sut;

import java.io.File;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * SUT stands for System/Software Under Test. Usually in your testing lab you
 * will have more then one setup and you would like your test to run on all the
 * setups without making any change to it. Lets say you are testing a web
 * application, in your lab you have few application servers with deferent URLs,
 * if you hard coded the URL to the test it will be able to run only on one of
 * your application servers.
 * 
 * To solve this problem we ask you to define your System/Setup using an XML
 * file.
 * 
 * @author Guy Arieli
 */
public interface Sut {
	/**
	 * Set the SUT file.
	 * 
	 * @param sutXml
	 *            The SUT file.
	 * 
	 * @exception Exception
	 */
	public void setSutXml(File sutXml) throws Exception;
	
	/**
	 * Set the SUT file
	 * @param sutXml The SUT file.
	 * @param notify if true will notify to listeners.
	 * @throws Exception
	 */
	public void setSutXml(File sutXml, boolean notify) throws Exception;


	/**
	 * Set the SUT file
	 * 
	 * @param sutXml
	 *            The SUT file as a byte array.
	 * 
	 * @exception Exception
	 */
	public void setSutXml(byte[] sutXml, String fileName) throws Exception;

	/**
	 * return the sut xml.
	 * 
	 * @return the sut xml as byte array
	 */
	public byte[] getSutXml();

	/**
	 * Get a value from the XML file. In the default implemantation XPath is
	 * used.
	 * 
	 * @param path
	 *            XPath to the requested value.
	 * 
	 * @return The requested value or null if not found.
	 * @exception Exception
	 */
	public String getValue(String path) throws Exception;

	/**
	 * Get a list of all the values found.
	 * 
	 * @param path
	 *            XPath to the values.
	 * 
	 * @return A list of the values.
	 * @exception Exception
	 */
	public List<Node> getAllValues(String path) throws Exception;

	/**
	 * Get the setup name
	 * 
	 * @return setup name
	 */
	public String getSetupName();

	/**
	 * Return the setup link
	 * 
	 * @return String
	 */
	public String getSetupLink();

	/**
	 * 
	 * @return The document in the base of the SUT
	 */
	public Document getDocument();
	
	/**
	 * 
	 * @return The original document in case there is a SutReader in use.<br>
	 * will return the same object as <I>getDocument()</I> if no SutReader is used
	 */
	public Document getOriginalDocument();
	
	/**
	 * Set the document model of the SUT
	 * @param doc
	 */
	public void setDocument(Document doc);
	
	/**
	 * @return True if a SutReader is used
	 */
	public boolean isSutReaderUsed();

}
