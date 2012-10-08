/*
 * Created on Dec 5, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * XML related common functionality
 */
public class XmlUtils {

	private static Logger log = Logger.getLogger(XmlUtils.class.getName());
	private static DocumentBuilder db; 
	
	/**
	 * Creates a <code>DocumentBuilder</code> with error handler
	 * which logs errors to log file.
	 */
	public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		if (db == null){
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			db = dbf.newDocumentBuilder();
			db.setErrorHandler(new ErrorHandler(){
				public void error(SAXParseException exception) throws SAXException {
					log.log(Level.FINE, "Failed parsing xml file",exception);
				}
				public void fatalError(SAXParseException exception) throws SAXException {
					log.log(Level.FINE, "Failed parsing xml file",exception);
				}
				public void warning(SAXParseException exception) throws SAXException {
					log.log(Level.FINE, "Failed parsing xml file",exception);					
				}
			});
		}
		return db;
	}
	
	/**
	 */
	public static Element getElement(String name,int index, Element parent){
		ArrayList<Element> toReturn = getElementsByTag(name, parent);
		if (index > toReturn.size()-1){
			return null;
		}
		return toReturn.get(index);
	}

	/**
	 */
	public static ArrayList<Element> getElementsByTag(String tag,Element searchIn){
		NodeList list = searchIn.getElementsByTagName(tag);
		ArrayList<Element> toReturn = new ArrayList<Element>();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (!(n instanceof Element)) {
				continue;
			}
			toReturn.add((Element)n);
		}
		return toReturn;
	}
	
	
	/**
	 * returns the first element with the given tag which attribute "name" equals name string.<br>
	 * search only in Element child nodes
	 * @param tagName	the tag to search
	 * @param name		the "name" attribute value
	 * @param searchIn	the element, in which child Nodes will be searched
	 * @return	the first element found that matches
	 * @throws Exception
	 */
	public static Element getChildElementWithName(String tagName, String name, Element searchIn){
		String attribute = "name";
		ArrayList<Element> list = getChildElementsByTag(tagName,searchIn);
		for (Element e:list){
			if (name.equals(e.getAttribute(attribute))) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * get all child elements matching the given tag 
	 * 
	 * @param tag
	 * @param searchIn
	 * @return
	 */
	public static ArrayList<Element> getChildElementsByTag(String tag,Element searchIn){
		ArrayList<Element> toReturn = new ArrayList<Element>();
		NodeList list = searchIn.getChildNodes();
		
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n instanceof Element && ((Element)n).getTagName().equals(tag)){
				toReturn.add((Element)n);
			}
			
		}
		return toReturn;
	}
	
	/**
	 */
	public static Element getElementWithName(String tagName, String name, Element searchIn){
		return getElementWithAttribute(tagName,"name",name,searchIn);
	}

	/**
	 */
	public static Element getElementWithAttribute(String tagName, String attribute, String attributeValue, Element searchIn) {
		ArrayList<Element> list = getElementsByTag(tagName,searchIn);
		for (Element e:list){
			if (attributeValue.equals(e.getAttribute(attribute))) {
				return e;
			}
		}
		return null;
	}

	/**
	 */
	public static ArrayList<Element> mergeByAttrib(ArrayList<Element> master,ArrayList<Element> slave,String attrib)  throws Exception{
		Map<String, Element> masterMap = mapFromArrayListByAttribute(master, attrib);
		Map<String, Element> slaveMap = mapFromArrayListByAttribute(slave, attrib);
		slaveMap.putAll(masterMap);
		
		ArrayList<Element> res = new ArrayList<Element>();
		Collection<Element> collection = slaveMap.values();
		for (Element e:collection){
			res.add(e);
		}
		return res;
	}

	/**
	 */
	public static String[] getTextElements(List<Element> elements) throws Exception{
		ArrayList<String> list = new ArrayList<String>();
		Iterator<Element> iter = elements.iterator();
		while (iter.hasNext()){
			String s = iter.next().getFirstChild().getTextContent();
			list.add(s);
		}
		return list.toArray(new String[0]);
	}
	
	public static Map<String,Element> mapFromArrayListByAttribute(ArrayList<Element> list,String attribName) {
		Map<String, Element> map = new HashMap<String, Element>();
		for (Element e:list){
			map.put(e.getAttribute(attribName), e);
		}
		return map;
	}
	
	/**
	 */
	public static String getComment(Element searchIn,int commentIndex){
		NodeList list = searchIn.getChildNodes();
		int counter = 0;
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeType()== Node.COMMENT_NODE){
				if (counter == commentIndex){
					return n.getTextContent();
				}
				counter++;
			}
		}
		return null;
	}
	
	/**
	 */
	public static void appendComment(Element element,String comment) {
		Node commentNode = element.getOwnerDocument().createComment(comment);
		element.appendChild(commentNode);
	}

	
	/**
	 * Parses XML file and returns XML document.
	 * 
	 * @param fileName
	 *            XML file to parse
	 * @return XML document or <B>null</B> if error occurred
	 */
	public static Document parseFile(String fileName) {
		DocumentBuilder docBuilder;
		Document doc = null;
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.warning("Wrong parser configuration: " + e.getMessage());
			return null;
		}
		File sourceFile = new File(fileName);
		try {
			doc = docBuilder.parse(sourceFile);
		} catch (SAXException e) {
			log.warning("Wrong XML file structure: " + e.getMessage());
			return null;
		} catch (IOException e) {
			log.warning("Could not read source file: " + e.getMessage());
			return null;
		}
		log.fine("XML file parsed");
		return doc;
	}
	
    public static boolean isSubTagExist(Node node, String tagName) {
        if (node == null) {
            return false;
        }
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n instanceof Element) {
                if (((Element) n).getTagName().equals(tagName)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Get sub tag content.
     * @param node to process
     * @param tagName the tag name
     * @return the child content
     */
    public static String getSubTagValue(Node node, String tagName) {
        if (node == null) {
            return null;
        }
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n instanceof Element) {
                if (((Element) n).getTagName().equals(tagName)) {
                    Node nn = n.getFirstChild();
                    if (nn instanceof Text) {
                        return ((Text) nn).getData();
                    }
                }
            }
        }
        return null;
    }

}