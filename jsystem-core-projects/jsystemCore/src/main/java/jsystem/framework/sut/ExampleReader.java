package jsystem.framework.sut;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import jsystem.framework.sut.SutReader;
import jsystem.utils.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A SutReader example implementation with a simple manipulation, adding an attribute to all
 * SUT tags named "attribute".
 * 
 * @author Nizan Freedman
 *
 */
public class ExampleReader implements SutReader {

	@Override
	public Document getDocument(File sutXml) throws Exception {
		Document doc =  XmlUtils.getDocumentBuilder().parse(sutXml);
		return manipulateDocument(doc);
	}

	@Override
	public Document getDocument(InputStream sutInputStream) throws Exception {
		Document doc = XmlUtils.getDocumentBuilder().parse(sutInputStream);
		return manipulateDocument(doc);
	}
	
	private Document manipulateDocument(Document doc){
		ArrayList<Element> tags = XmlUtils.getElementsByTag("attribute", doc.getDocumentElement());
		int number = 0;
		for (Element tag : tags){
			tag.setAttribute("number", ++number + "");
		}
		
		return doc;
	}

}
