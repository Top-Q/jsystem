/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario.flow_control;

import java.util.ArrayList;
import java.util.HashMap;

import jsystem.framework.common.CommonResources;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.utils.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AntSwitch extends AntFlowControl {
	public static String OLD_XML_TAG = "switch";
	public static String XML_TAG = CommonResources.JSYSTEM_SWITCH;
	public static String XML_CONTAINER_TAG = CommonResources.JSYSTEM_SWITCH;
	
	private Parameter value = new Parameter();
	private Parameter caseInsensitive = new Parameter();
	
	public AntSwitch() {
		this(null, null);
	}
	
	public AntSwitch(JTestContainer parent, String id) {
		super("Switch", parent, id);

		value.setType(Parameter.ParameterType.STRING);
		value.setValue("setValue");
		value.setName("Value");
		value.setDescription("What to switch on ?");
		// TODO: sync section with comment/name
		value.setSection(getComment());
		
		addParameter(value);
		
		caseInsensitive.setType(Parameter.ParameterType.BOOLEAN);
		caseInsensitive.setValue(false);
		caseInsensitive.setName("Case insensitive ?");
		caseInsensitive.setDescription("Should we do case insensitive comparisons?");
		// TODO: sync section with comment/name
		caseInsensitive.setSection(getComment());

		// TODO: add case sensitive - addParameter(caseInsensitive);	
		setTestComment(defaultComment());
	}

	public static AntSwitch fromElement(JTestContainer parent, Element element) {
		AntSwitch newContainer = new AntSwitch(parent, null);

		String valueValue = newContainer.value.getValue().toString();
		valueValue = element.getAttribute("value").toString();
		newContainer.value.setValue(valueValue);
		deserializeAdditionalData(newContainer, element);
		// TODO: case sensitive
		newContainer.setTestComment(newContainer.defaultComment());
		return newContainer;
	}
	
	public AntSwitch cloneTest() throws Exception {
		AntSwitch test = new AntSwitch(getParent(), getTestId());
		test.rootTests = cloneRootTests(test); 
		return test;
	}
	
	public Element addExecutorXml(Element targetScenario, Document doc) {
		Element switchElement = super.addExecutorXml(targetScenario, doc);
		appendAdditionalData(switchElement);
		switchElement.setAttribute("value", value.getValue().toString());
		switchElement.setAttribute("caseinsensitive", caseInsensitive.getValue().toString());
		addPropertiesToElement(switchElement);

		targetScenario.appendChild(switchElement);
		
		return switchElement;
	}

	public String getXmlContainerTag() {
		return XML_CONTAINER_TAG;
	}
	
	public void createTestsFromElement (Element xmlDefinition, HashMap<String, JTestContainer> targetAndParent,
			HashMap<String, Integer> targetAndPlace) {
		// Create the "case" tests, which might exist
		ArrayList<Element> nodes = XmlUtils.getChildElementsByTag(AntSwitchCase.XML_TAG, xmlDefinition);
		for (Element node : nodes) {
				Element caseXml = (Element)node;

				JTestContainer caseContainer = AntSwitchCase.fromElement(this, caseXml);
				caseContainer.setLoadVersion(loadVersion);
				((AntFlowControl)caseContainer).loadUuidAndParameters(caseXml);
				caseContainer.createTestsFromElement(caseXml, targetAndParent, targetAndPlace);
				rootTests.add(caseContainer);
		}
		
		// Create the "default"
		nodes = XmlUtils.getChildElementsByTag(AntSwitchDefault.XML_TAG, xmlDefinition);
		
		if (nodes.size() > 0){
			Element node = nodes.get(0);
			AntFlowControl defaultContainer = AntSwitchDefault.fromElement(this, node);
			defaultContainer.setLoadVersion(loadVersion);
			defaultContainer.createTestsFromElement(node, targetAndParent, targetAndPlace);
			rootTests.add(defaultContainer);
		}
	}
	
	public String defaultComment() {
		String comment = "Switch according to  \"" + this.value.getValue().toString() + "\"";
		return comment;
	}

	public boolean canMoveUp(JTest test) {
		boolean canMoveUp = super.canMoveUp(test);
		if (canMoveUp) {
			if (test instanceof AntSwitchDefault) {
				canMoveUp = false;
			} else if (test instanceof AntSwitchCase) {
				int index = getRootIndex(test);
				if (!(rootTests.elementAt(index - 1) instanceof AntSwitchCase)) {
					// Can move AntSwitchCase up only if the one before it is another AntSwitchCase
					canMoveUp = false;
				}
			}
		}
		return canMoveUp;
	}

	public boolean canMoveDown(JTest test) {
		boolean canMoveDown = super.canMoveDown(test);
		if (canMoveDown) {
			if (test instanceof AntSwitchDefault) {
				canMoveDown = false;
			} else if (test instanceof AntSwitchCase) {
				int index = getRootIndex(test);
				if (!(rootTests.elementAt(index + 1) instanceof AntSwitchCase)) {
					// Can move ElseIf up only if the one before it is another ElseIf
					canMoveDown = false;
				}
			}
		}
		return canMoveDown;
	}

	@Override
	protected void loadParameters() {
		value.setValue(ScenarioHelpers.getParameterValueFromProperties(this,getFlowFullUUID(),"Value",value.getValue().toString()));
		loadAndSetUserDocumentation();
	}
}
