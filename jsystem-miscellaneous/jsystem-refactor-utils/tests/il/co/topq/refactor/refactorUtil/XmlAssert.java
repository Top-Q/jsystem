package il.co.topq.refactor.refactorUtil;

import il.co.topq.refactor.utils.XmlUtils;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.Assert;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlAssert {

	public static void assertNodeNumber(File xmlFile, String expression, int expectedNumOfNodes)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		final Document doc = XmlUtils.parseDocument(xmlFile);
		NodeList nodeList = XmlUtils.getNodeList(doc, expression);
		Assert.assertEquals(expectedNumOfNodes, nodeList.getLength());
	}

	public static void assertNodeNumber(String xmlFileName, String expression, int expectedNumOfNodes)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		assertNodeNumber(new File(xmlFileName), expression, expectedNumOfNodes);
	}

}
