/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.sut;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.runner.loader.LoadersManager;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import jsystem.utils.XmlUtils;

import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The default SUT implemantation.
 * 
 * @author Guy Arieli
 */
public class SutImpl implements Sut {
	
	private static Logger log = Logger.getLogger(SutImpl.class.getName());

	private Document doc;

	private String fileName;
	
	private int sutFileRetries = 4;
	
	private SutReader sutReader;
	
	private Document originalDoc;

	public SutImpl() {
		loadSutReader();
	}

	public void setSutXml(File sutXml, boolean notify) throws Exception {
		if (sutXml == null){
			throw new Exception("Sut file to set is null!");
		}
		
		if (!FileUtils.existsWithRetry(sutXml.getAbsolutePath(), sutFileRetries, 3000)) {
			log.log(Level.SEVERE,"Couldn't find SUT file: " + sutXml.getAbsolutePath());
			throw new FileNotFoundException(sutXml.getAbsolutePath());
		}
		this.fileName = sutXml.getName();
		//reset document in case loading the document will fail.
		
		originalDoc = XmlUtils.getDocumentBuilder().newDocument();
		originalDoc = FileUtils.readDocumentFromFile(sutXml);
		if (sutReader == null){
			doc = originalDoc;
		}else{
			try{
				doc = sutReader.getDocument(sutXml);
				if (doc == null){
					doc = originalDoc;
				}
			}catch (Exception e) {
				doc = originalDoc;
			}
			
		}
		/**
		 * Fire sut change notification
		 */
		if (notify && ListenerstManager.isInit()) {
			ListenerstManager.getInstance().sutChanged(fileName);
		}
	}
	
	public void setSutXml(File sutXml) throws Exception {
		setSutXml(sutXml, true);
	}

	public void setSutXml(byte[] sutXml, String fileName) throws Exception {
		this.fileName = fileName;
		DocumentBuilder db = XmlUtils.getDocumentBuilder();
		InputStream is = new ByteArrayInputStream(sutXml);
		originalDoc = db.parse(is);
		if (sutReader == null){
			doc = originalDoc;
		}else{
			try{
				doc = sutReader.getDocument(is);
				if (doc == null){
					doc = originalDoc;
				}
			}catch (Exception e) {
				doc = originalDoc;
			}
		}
		
		/**
		 * Fire sut change notification
		 */
		ListenerstManager.getInstance().sutChanged(fileName);
	}

	public byte[] getSutXml() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Result r = new StreamResult(out);
		Source s = new DOMSource(doc);
		try {
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(s, r);
		} catch (Exception e) {
			log.log(Level.WARNING, "Unable to get xml as byte array", e);
		}
		return out.toByteArray();
	}

	public String getValue(String path) throws Exception {
		List<?> list = getAllValues(path);
		if (list.size() == 0) {
			throw new Exception("Object was not found: " + path);
		}
		String text = ((Node) list.get(0)).getTextContent();
		if (text != null && text.indexOf('#') >= 0) { // look like the xerses implemantation.
										// We should fix the class name
			// the inputs look like: [#text: com....]
			text = text.substring(text.lastIndexOf(' ') + 1, text.length() - 1);
		}
		return text;

	}

	public List<Node> getAllValues(String path) throws Exception {
		List<Node> list = new ArrayList<Node>();
		if (doc == null){
			log.warning("No SUT file is defined.");
			return list;
		}
		String xpath = path;
		NodeList nodeList = XPathAPI.selectNodeList(doc, xpath);
		for (int i = 0; i < nodeList.getLength(); i++) {
			list.add(i, nodeList.item(i));
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.sut.Sut#getSetupName()
	 */
	public String getSetupName() {
		try {
			if (doc == null) {
				return null;
			}
			XObject o = XPathAPI.eval(doc, "string(/sut/@name)");
			if (o != null && o.str() != null && !o.str().equals("")) {
				return o.str();
			}
		} catch (TransformerException e) {
		}
		return fileName;
	}

	public String getSetupLink() {
		try {
			if (doc == null) {
				return null;
			}
			XObject o = XPathAPI.eval(doc, "string(/sut/@link)");
			if (o != null && o.str() != null && !o.str().equals("")) {
				return o.str();
			}
		} catch (TransformerException e) {
		}
		return null;
	}
	
	/**
	 * If an sut reader was assigned through the JSystem properties, load it
	 * @return	null if none was defined
	 */
	private void loadSutReader(){
		String reader = JSystemProperties.getInstance().getPreference(FrameworkOptions.SUT_READER_CLASS);
		if (StringUtils.isEmpty(reader)){
			return;
		}
		try{
			Class<?> reporterClass = LoadersManager.getInstance().getLoader().loadClass(reader);
			if (reporterClass != null){
				Object instance = reporterClass.newInstance();
				if (instance instanceof SutReader){
					log.log(Level.INFO,"Sut reader : " + reader + " Was loaded.");
					sutReader = (SutReader) instance;
				}
			}
		}catch (Exception e) {
			log.log(Level.WARNING,"Fail to init Sut reader : " + reader,e);
		}
	}

	public Document getDocument() {
		return doc;
	}
	
	public void setDocument(Document doc){
		if (sutReader != null){
			return;
		}
		this.doc = doc;
	}

	@Override
	public Document getOriginalDocument() {
		return originalDoc;
	}

	@Override
	public boolean isSutReaderUsed() {
		return sutReader != null;
	}

	public static SutValidator[] getSutValidators(Document doc) {
		XObject o;
		try {
			o = XPathAPI.eval(doc, "string(/sut/@validators)");
		} catch (TransformerException e1) {
			e1.printStackTrace();
			return null;
		}
		if (o != null && o.str() != null && !o.str().equals("")) {
			String validatorsString = o.str();
			if(validatorsString != null && !validatorsString.isEmpty()){
				String[] validatorsStringArray = validatorsString.split(";");
				ArrayList<SutValidator> validators = new ArrayList<SutValidator>();
				for(String validatorString: validatorsStringArray){
					try {
						Class<?> c = LoadersManager.getInstance().getLoader().loadClass(validatorString);
						validators.add((SutValidator)c.newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return validators.toArray(new SutValidator[0]);
			}
		}
		return null;
	}

}
