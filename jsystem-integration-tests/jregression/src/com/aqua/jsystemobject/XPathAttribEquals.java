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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XPathAttribEquals extends DocumentAnalyzer {
	String xpath;
	String value;
	String attrib;
	public XPathAttribEquals(String xpath,String attrib, String value){
		this.xpath = xpath;
		this.value = value;
		this.attrib = attrib;
	}
	public void analyze() {
		title = "XPathAttribEquals: xpath: " + xpath +",attrib: " + attrib + ", value: " + value;
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
			if(elementsFound > 0){
				
				String text = ((Element)nodeList.item(0)).getAttribute(attrib);
		        if(value.equals(text)){
		        	status = true;
		        } else {
		        	status = false;
		        	title = title + ", actual: " + text;
		        }
				return;
			} else {
				status = false;
				title = title + ", element wasn't found";
				return;
			}
		} catch (Exception e) {
			message = StringUtils.getStackTrace(e);
			return;
		}

	}

}
