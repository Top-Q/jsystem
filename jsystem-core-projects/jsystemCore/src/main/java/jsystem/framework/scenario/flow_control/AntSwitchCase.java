/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario.flow_control;

import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ScenarioHelpers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AntSwitchCase extends AntFlowControl {
	public static String XML_TAG = "case";
	public static String XML_CONTAINER_TAG = "case";
	private Parameter value = new Parameter();

	public AntSwitchCase() {
		this(null, null);
	}
	
	public AntSwitchCase(JTestContainer parent, String id) {
		super("Case", parent, id);

		value.setType(Parameter.ParameterType.STRING);
		value.setValue("setValue");
		value.setName("Value");
		value.setDescription("What to switch on ?");
		// TODO: sync section with comment/name
		value.setSection(getComment());
		
		addParameter(value);
		setTestComment(defaultComment());
	}
	
	public static AntSwitchCase fromElement(JTestContainer parent, Element element) {
		AntSwitchCase newContainer = new AntSwitchCase(parent, null);
		
		String valueValue = newContainer.value.getValue().toString();
		valueValue = element.getAttribute("value").toString();
		newContainer.value.setValue(valueValue);
		
		newContainer.setTestComment(newContainer.defaultComment());
		deserializeAdditionalData(newContainer, element);
		return newContainer;
	}

	public AntSwitchCase cloneTest() throws Exception {
		AntSwitchCase test = new AntSwitchCase(getParent(), getTestId());
		test.rootTests = cloneRootTests(test);
		return test;
	}
		
	public Element addExecutorXml(Element targetScenario, Document doc) {
		Element caseElement = super.addExecutorXml(targetScenario, doc);
		appendAdditionalData(caseElement);
		caseElement.setAttribute("value", value.getValue().toString());
		addPropertiesToElement(caseElement);
		targetScenario.appendChild(caseElement);
		
		return caseElement;
	}
	
	public String getXmlContainerTag() {
		return XML_CONTAINER_TAG;
	}

	public String defaultComment() {
		String comment = "Execute in case \"" + this.value.getValue().toString() + "\"";
		return comment;
	}

	@Override
	protected void loadParameters() {
		value.setValue(ScenarioHelpers.getParameterValueFromProperties(this,getFlowFullUUID(),"Value",value.getValue().toString()));
		loadAndSetUserDocumentation();
	}
}
