/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.extensions.analyzers.document;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import jsystem.extensions.analyzers.document.ElementFound;
import jsystem.framework.system.SystemObjectImpl;
import junit.framework.SystemTestCase;

public class ElementFoundTest extends SystemTestCase {
	Document doc;
	public void setUp() throws Exception{
		byte[] xml = sut().getSutXml();
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db=dbf.newDocumentBuilder();
        doc=db.parse(new ByteArrayInputStream( xml));
	}
	
	public void testElementFound(){
		MySystemObject mso = new MySystemObject();
		mso.setTestAgainstObject(doc);
		mso.analyze(new ElementFound("/sut"));
	}
	public void testElementFound2(){
		MySystemObject mso = new MySystemObject();
		mso.setTestAgainstObject(doc);
		mso.analyze(new ElementFound("//port"));
	}
	public void testElementFound3(){
		MySystemObject mso = new MySystemObject();
		mso.setTestAgainstObject(doc);
		mso.analyze(new ElementFound("//port[@index=\"0\"]"));
	}
}

class MySystemObject extends SystemObjectImpl{
	
}
