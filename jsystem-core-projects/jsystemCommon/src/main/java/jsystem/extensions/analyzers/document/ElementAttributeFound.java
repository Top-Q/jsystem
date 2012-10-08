/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.document;

import java.io.StringWriter;
import java.util.Vector;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jsystem.utils.StringUtils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Analyze if document contains element with scpecific attribute(s) If you need
 * to check more than one attribute use 'addAttribute' function
 * 
 * @author arseniy
 * 
 */
public class ElementAttributeFound extends DocumentAnalyzer {
	protected String testName;

	protected int elementsFound = -1;

	protected NodeList nodeList = null;

	private int attributesAmount = 0;

	Vector<String> attributesVector = null;

	Vector<String> valuesVector = null;

	Vector<Integer> placesVector = null;

	Vector<String> nodeAttributes = null;

	Vector<String> nodeAttributesValues = null;

	Node node;

	boolean exist = true;

	public static final int Equal = 0;

	public static final int Contain = 1;

	public static final int StartWith = 2;

	public static final int EndWith = 3;

	/**
	 * 
	 * @param elementName
	 * @param attribute
	 * @param value
	 * @param placeInAtrrbute
	 *            may be Equal,Contain,StartWith,EndWith
	 */
	public ElementAttributeFound(String elementName, String attribute, String value, int placeInAtrrbute) {
		this.testName = elementName;
		attributesAmount++;
		attributesVector = new Vector<String>();
		valuesVector = new Vector<String>();
		placesVector = new Vector<Integer>();
		attributesVector.add(attribute);
		valuesVector.add(value);
		placesVector.add(Integer.valueOf(placeInAtrrbute));
	}

	/**
	 * 
	 * @param elementName
	 * @param attribute
	 * @param value
	 * @param placeInAtrrbute
	 *            may be Equal,Equal,StartWith,StartWith
	 * @param exist
	 *            if false ,check that not exist such element
	 */
	public ElementAttributeFound(String elementName, String attribute, String value, int placeInAtrrbute, boolean exist) {
		this.testName = elementName;
		attributesAmount++;
		attributesVector = new Vector<String>();
		valuesVector = new Vector<String>();
		placesVector = new Vector<Integer>();
		attributesVector.add(attribute);
		valuesVector.add(value);
		placesVector.add(Integer.valueOf(placeInAtrrbute));
		this.exist = exist;
	}

	/**
	 * 
	 * @param attribute
	 * @param value
	 * @param place
	 *            Equal- 0,Contain-1,StartWith-2,EndWith-3
	 */
	public void addAttribute(String attribute, String value, int place) {
		attributesAmount++;
		attributesVector.add(attribute);
		valuesVector.add(value);
		placesVector.add(Integer.valueOf(place));
	}

	@Override
	public void analyze() {
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
		status = false;
		nodeList = doc.getElementsByTagName(testName);
		elementsFound = nodeList.getLength();
		title = attributesToString();
		if (elementsFound > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				node = nodeList.item(i);
				NamedNodeMap attributes = node.getAttributes();
				nodeAttributes = new Vector<String>();
				nodeAttributesValues = new Vector<String>();
				for (int j = 0; j < attributes.getLength(); j++) {
					Node attribute = attributes.item(j);
					nodeAttributes.add(attribute.getNodeName());
					nodeAttributesValues.add(attribute.getNodeValue());

				}
				if (findAttributes() && exist == true) {
					status = true;
					title = title + " Such Element Found";
					message = message + "\n\r********************************\n\r" + nodeToString(node);
					return;
				}

				else if (findAttributes() && exist != true) {
					status = false;
					title = title + " Such Element Found";
					message = message + "\n\r********************************\n\r" + nodeToString(node);
					return;
				}
			}
			if (exist) {
				status = false;
				title = title + " Such Element Not Found";
			} else {
				status = true;
				title = title + " Such Element Not Found";
			}
		}

	}

	private String nodeToString(Node node) {

		String answer = "<" + node.getNodeName() + " ";
		for (int i = 0; i < nodeAttributes.size(); i++) {
			answer = answer + (String) nodeAttributes.elementAt(i) + "=\"" + (String) nodeAttributesValues.elementAt(i)
					+ "\" ";
		}
		answer = answer + "/>";
		return answer;
	}

	private String attributesToString() {
		if (exist) {
			String toString = "  Loking For Element with  Attributes :\n\r";
			for (int i = 0; i < attributesVector.size(); i++) {
				toString = toString + (i + 1) + " . Attribute Name: " + (String) attributesVector.elementAt(i)
						+ ", Attribute Value: " + (String) valuesVector.elementAt(i) + "\n\r";
			}
			return toString;
		} else {
			String toString = "Check That Not Exist Element with  Attributes :\n\r";
			for (int i = 0; i < attributesVector.size(); i++) {
				toString = toString + (i + 1) + " . Attribute Name: " + (String) attributesVector.elementAt(i)
						+ ", Attribute Value: " + (String) valuesVector.elementAt(i) + "\n\r";
			}

			return toString;
		}

	}

	public boolean findAttributes() {

		if (!nodeAttributes.containsAll(attributesVector))
			return false;
		else {
			for (int i = 0; i < attributesVector.size(); i++) {
				int place = nodeAttributes.indexOf(attributesVector.elementAt(i));
				if (!findvalue((String) nodeAttributesValues.elementAt(place), (String) valuesVector.elementAt(i),
						(Integer) placesVector.elementAt(i)))
					return false;
			}
			return true;
		}
	}

	public boolean findvalue(String attribute, String value, Integer place) {
		int placeInElement = place.intValue();
		switch (placeInElement) {
		case Equal:
			return attribute.equals(value);

		case Contain:
			return attribute.contains(value);

		case StartWith:
			return attribute.startsWith(value);

		case EndWith:
			return attribute.endsWith(value);

		default:
			return false;

		}
	}

	public int getElementCount() {
		return elementsFound;
	}

	public NodeList getNodeList() {
		return nodeList;
	}

}
