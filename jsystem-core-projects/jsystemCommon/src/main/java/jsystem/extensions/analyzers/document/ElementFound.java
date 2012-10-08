/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.document;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jsystem.utils.StringUtils;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.NodeList;

public class ElementFound extends DocumentAnalyzer {
	protected String xpath;

	protected int elementsFound = -1;

	protected NodeList nodeList = null;

	protected boolean isFound = true;

	public ElementFound(String xpath) {
		this.xpath = xpath;
	}

	public ElementFound(String xpath, boolean isFound) {
		this.xpath = xpath;
		this.isFound = isFound;
	}

	public void analyze() {
		title = "Looking for xpath: " + xpath;

		status = false;
		if (!isFound) {
			status = true;
		}

		try {
			Source source = new DOMSource(doc);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			// pretty format the XML output
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			// get the XML in a String
			xformer.transform(source, result);
			message = stringWriter.toString();
		} catch (Exception e) {
			message = StringUtils.getStackTrace(e);
			return;
		}
		try {
			nodeList = XPathAPI.selectNodeList(doc, xpath);
			elementsFound = nodeList.getLength();
			if (elementsFound > 0) {
				title = "Looking for xpath: " + xpath + ", " + elementsFound + " elements were found";

				status = true;
				if (!isFound) {
					status = false;
				}
				return;
			} else {
				title = "Looking for xpath: " + xpath + ", no elements were found";
				return;
			}
		} catch (Exception e) {
			message = StringUtils.getStackTrace(e);
			return;
		}

	}

	public int getElementCount() {
		return elementsFound;
	}

	public NodeList getNodeList() {
		return nodeList;
	}
}
