package com.aqua.jsystemobject;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jsystem.extensions.analyzers.document.DocumentAnalyzer;
import jsystem.utils.StringUtils;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.NodeList;

public class XPathNumberOfElements extends DocumentAnalyzer {
	String xpath;
	int expectedNumber = 0;
	public XPathNumberOfElements(String xpath,int expectedNumber){
		this.xpath = xpath;
		this.expectedNumber = expectedNumber;
	}
	public void analyze() {
		title = "XPathAttribEquals: xpath: " + xpath +",expectedNumber: " + expectedNumber;
		try {
			Source source = new DOMSource(doc);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			//pretty format the XML output
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			//get the XML in a String
			xformer.transform(source, result);
			message = stringWriter.toString();
		} catch (Exception e){
			message = StringUtils.getStackTrace(e);
			return;
		}
		
	    try {
			NodeList nodeList = XPathAPI.selectNodeList(doc,xpath);
			int elementsFound = nodeList.getLength();
			if(elementsFound == expectedNumber){
				
	        	status = true;
				return;
			} else {
				status = false;
				title = title + ", actual count: " + elementsFound;
				return;
			}
		} catch (Exception e) {
			message = StringUtils.getStackTrace(e);
			return;
		}

	}

}
