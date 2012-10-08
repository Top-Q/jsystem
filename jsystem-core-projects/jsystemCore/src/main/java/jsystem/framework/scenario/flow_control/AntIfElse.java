/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario.flow_control;

import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * AntForLoop implements the specifications of Ant Contrib's For task as described in:
 * http://ant-contrib.sourceforge.net/ant-contrib/manual/tasks/for.html
 */
public class AntIfElse extends AntFlowControl {
	public static String XML_TAG = "else";
	public static String XML_CONTAINER_TAG = "else";
	
	public AntIfElse() {
		this(null, null);
	}
	
	public AntIfElse(JTestContainer parent, String id) {
		super("else", parent, id);
		setTestComment(defaultComment());
	}

	public static AntIfElse fromElement(JTestContainer parent, Element element) {
		AntIfElse newContainer = new AntIfElse(parent, null);
		deserializeAdditionalData(newContainer, element);
		return newContainer;
	}
	
	public AntIfElse cloneTest() throws Exception {
		AntIfElse test = new AntIfElse(getParent(), getTestId());
		test.rootTests = cloneRootTests(test); 
		return test;
	}
	
	// TODO: identical to JTestContainer, but since it is not the "super", we should duplicate.
	public Element addExecutorXml(Element targetScenario, Document doc) {
		Element containerElement = doc.createElement(XML_CONTAINER_TAG);
		appendAdditionalData(containerElement);
		/*
		 * Create and add the container content
		 */
		//go over all tests append a target that invokes them 
		//to the ant script and add an antcall to the test in the execute scenario target
		for (JTest jtest : rootTests) {
			jtest.addExecutorXml(containerElement, doc);
		}
		
		targetScenario.appendChild(containerElement);
		
		return containerElement;
	}
	
	public Element getContainerElement (Element xmlDefinition) {
		return xmlDefinition;
	}
	
	public String getXmlContainerTag() {
		return XML_CONTAINER_TAG;
	}
	public String getXmlTag() {
		return XML_TAG;
	}
	
	public String defaultComment() {
		String comment = "Else section";
		return comment;
	}

	@Override
	protected void loadParameters() {
		// NO NEED - no parameters
	}
}
