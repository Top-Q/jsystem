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
public class AntSwitchDefault extends AntFlowControl {
	public static String XML_TAG = "default";
	public static String XML_CONTAINER_TAG = "default";
	
	public AntSwitchDefault() {
		this(null, null);
	}
	
	public AntSwitchDefault(JTestContainer parent, String id) {
		super("Default", parent, id);
		setTestComment(defaultComment());
	}
	
	public static AntSwitchDefault fromElement(JTestContainer parent, Element element) {
		AntSwitchDefault newContainer = new AntSwitchDefault(parent, null);
		deserializeAdditionalData(newContainer, element);
		return newContainer;
	}
	
	public AntSwitchDefault cloneTest() throws Exception {
		AntSwitchDefault test = new AntSwitchDefault(getParent(), getTestId());
		test.rootTests = cloneRootTests(test); 
		return test;
	}
	
	public Element addExecutorXml(Element targetScenario, Document doc) {
		Element element = super.addExecutorXml(targetScenario, doc);
		appendAdditionalData(element);
		return element;
	}
	public String getXmlContainerTag() {
		return XML_CONTAINER_TAG;
	}
	public String defaultComment() {
		String comment = "Default case";
		return comment;
	}

	@Override
	protected void loadParameters() {
		// NO parameters
	}
}
