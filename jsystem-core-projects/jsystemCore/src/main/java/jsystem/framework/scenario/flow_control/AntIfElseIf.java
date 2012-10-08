/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario.flow_control;

import jsystem.framework.scenario.JTestContainer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * AntForLoop implements the specifications of Ant Contrib's For task as described in:
 * http://ant-contrib.sourceforge.net/ant-contrib/manual/tasks/for.html
 */
public class AntIfElseIf extends AntIfCondition {
	public static String XML_TAG = "elseif";
	public static String XML_CONTAINER_TAG = "then";
	
	public AntIfElseIf() {
		this(null, null);
	}
	
	public AntIfElseIf(JTestContainer parent, String id) {
		super(parent, id);
		setTestComment(defaultComment());
		setName("elseif");
	}
	
	public static AntIfCondition fromElement(JTestContainer parent, Element element) {
		AntIfElseIf newContainer = new AntIfElseIf(parent, null);
		initElement(newContainer,element);
		deserializeAdditionalData(newContainer, element);
		return newContainer;
	}
	public Element addExecutorXml(Element targetScenario, Document doc) {
		Element element = super.addExecutorXml(targetScenario, doc);
		appendAdditionalData(element);
		return element;
	}
	public AntIfElseIf cloneTest() throws Exception {
		AntIfElseIf test = new AntIfElseIf(getParent(), getTestId());
		test.rootTests = cloneRootTests(test); 
		return test;
	}
	public String getXmlContainerTag() {
		return XML_CONTAINER_TAG;
	}
	public String getXmlTag() {
		return XML_TAG;
	}
	public String defaultComment() {
		return test.getConditionString();
	}
}
