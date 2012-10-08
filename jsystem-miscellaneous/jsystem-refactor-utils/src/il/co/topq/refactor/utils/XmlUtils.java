package il.co.topq.refactor.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlUtils {
	
	private static Logger log = Logger.getLogger("XmlUtils");

	private XmlUtils() {
		// Utils
	}
	
	public static Document parseDocument(final File xmlFile) throws ParserConfigurationException, SAXException, IOException{
		log.finer("Parsing document from file "+xmlFile.getName());
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
 		return builder.parse(xmlFile);

	}

	/**
	 * Get nodelist from XML document using xpath expression
	 * 
	 * @param doc
	 * @param expression
	 * @return
	 * @throws XPathExpressionException
	 */
	public static NodeList getNodeList(final Document doc, final String expression) throws XPathExpressionException {
		if (null == expression || expression.isEmpty() || null == doc) {
			return null;
		}
		log.finer("Executing xpath xpression " + expression);
		XPath xPath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xPath.compile(expression);
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		return (NodeList) result;
	}
	
	/**
	 * Get node from XML document using xpath expression
	 * 
	 * @param doc
	 * @param expression
	 * @return
	 * @throws XPathExpressionException
	 */
	public static Node getNode(final Document doc, final String expression) throws XPathExpressionException {
		if (null == expression || expression.isEmpty() || null == doc) {
			return null;
		}
		log.finer("Executing xpath xpression " + expression);
		XPath xPath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xPath.compile(expression);
		Object result = expr.evaluate(doc, XPathConstants.NODE);
		return (Node) result;
	}

	

	/**
	 * Get one element from XML document using xpath expression
	 * 
	 * @param doc
	 * @param expression
	 * @return
	 * @throws XPathExpressionException
	 */
	public static Element getElement(final Document doc, final String expression) throws XPathExpressionException {
		if (null == expression || expression.isEmpty() || null == doc) {
			return null;
		}
		log.finer("Executing xpath xpression " + expression);
		XPath xPath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xPath.compile(expression);
		Object result = expr.evaluate(doc, XPathConstants.NODE);
		return (Element) result;
	}

}
