package com.aqua.jsystemobject;

import java.io.File;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jsystem.extensions.analyzers.document.DocumentAnalyzer;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.NodeList;
/**
 * Given a reports.xml dom object, test index and Properties
 * The analyzer verifies that the given test actually
 * includes the given properties.
 * 
 * @author goland
 */
public class TestPropertiesAnalyzer extends DocumentAnalyzer {
	private int testIndex;
	private Properties props;

	public TestPropertiesAnalyzer(int testIndex,Properties props){
		this.testIndex = testIndex;
		this.props = props;
	}
	
	public void analyze() {
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
			title = "Error in analyzing test properties";
			message = StringUtils.getStackTrace(e);
			return;
		}
		
	    try {
			NodeList nodeList = XPathAPI.selectNodeList(doc,"/reports/test[@testIndex=\"" + testIndex+ "\"]");
			int elementsFound = nodeList.getLength();
			if(elementsFound == 0){
				status = false;
				title = " test with index "+testIndex + " not found";
				return;
			}
			
			String properties = nodeList.item(0).getAttributes().getNamedItem("properties").getNodeValue();
			Properties actualProps = StringUtils.stringToProperties(properties);
			Enumeration<Object> iter = props.keys();
			while (iter.hasMoreElements()){
				Object key = iter.nextElement();
				Object val = props.get(key);
				
				if (!actualProps.containsKey(key)){
					title = "Property " + key.toString() + " not found in test " + testIndex;
					status = false;
					message = actualProps.toString();
					return;
				}
				Object actualVal = actualProps.get(key);
				if (!actualVal.equals(val)){
					title = "Property " + key.toString() + " of test " + testIndex + " expected value " + val + "  actual value: " + actualVal;
					status = false;
					return;
				}
			}
			title = " Properties found as expected";
			status = true;
		} catch (Exception e) {
			title = "Error in analyzing test properties";
			message = StringUtils.getStackTrace(e);
			return;
		}

	}

	public static void main(String args[]) throws Exception {
		Properties props = new Properties();
		props.put("linkToFile","<A href=\"test_1/MyFile.txt\">MyFile.txt</A>");
		props.put("linkToOne","<A href=\"http://www.one.co.il\">www.one.co.il</A>");
		TestPropertiesAnalyzer analyzer = new TestPropertiesAnalyzer(0,props);
		analyzer.setTestAgainst(FileUtils.readDocumentFromFile(new File("C:/TAS/reports.0.xml")));
		analyzer.analyze();
	}
}
