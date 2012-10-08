/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.sut;

import java.io.File;
import java.io.InputStream;

import org.w3c.dom.Document;

/**
 * All SUT handling is done against an XML file, using org.w3c.dom.Document object.<br>
 * This interface was created in order to support different implementations for the SUT representations,
 * which will eventually be translated into a Document object.<br><br>
 * 
 * To add this ability, create an object that implements the SutReader interface and add an entry
 * to the jsystem properties:  sut.reader.class=<your reader namespace>
 * 
 * @author Nizan Freedman
 *
 */
public interface SutReader {

	/**
	 * Parse given SutXml file and return a Document object (Commonly used)
	 * 
	 * @param sutXml	The SUT file handler
	 * @return	The Document object after processing
	 * @throws Exception
	 */
	public Document getDocument(File sutXml) throws Exception;
	
	/**
	 * Parse given Input stream and return a Document object (Less commonly used)
	 * 
	 * @param sutInputStream	input stream to the sut
	 * @return	The Document object after processing
	 * @throws Exception
	 */
	public Document getDocument(InputStream sutInputStream) throws Exception;
	
}
