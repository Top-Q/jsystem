/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario.flow_control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import jsystem.framework.common.CommonResources;
import jsystem.framework.report.RunnerListenersManager;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ParameterUtils;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.UIHandler;
//import jsystem.framework.scenario.flow_control.FlowIfTest.CompareOptions;
import jsystem.framework.scenario.flow_control.FlowIfTest.ScriptLanguage;
import jsystem.utils.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * AntForLoop implements the specifications of Ant Contrib's For task as described in:
 * http://ant-contrib.sourceforge.net/ant-contrib/manual/tasks/for.html
 */
public class AntIfCondition extends AntFlowControl implements UIHandler{
	public static String XML_TAG = "if";
	public static String XML_CONTAINER_TAG = "then";
	
	private Parameter caseSensitive = new Parameter();
	
	private Parameter conditionType = new Parameter();
	
	protected FlowIfTest test;
	
	protected RunnerTest runnerTest;
	
	protected final static String SCRIPT_PATH = "ifScriptCondition.js";
	
	public AntIfCondition() {
		this(null, null);
	}
	
	public AntIfCondition(JTestContainer parent, String id) {
		super("if", parent, id);
		initInnerTest();
		
		setTestComment(defaultComment());
	}
	
	private void initInnerTest(){
		test = new FlowIfTest();
		runnerTest = new RunnerTest(test.getClass().getName(), "");
		runnerTest.setTest(test);
		runnerTest.setParent(this);
	}
	
	@Override
	public void setTestClassParameters() {
		runnerTest.setTestClassParameters();
	}
	
	/**
	 * get all tests parameters
	 * 
	 * @param doFilter	get only visible parameters
	 * @return
	 */
	protected Parameter[] getParameters(boolean doFilter) {
		if (doFilter) {
			return runnerTest.getVisibleParamters(false);
		} else {
			return runnerTest.getParameters();
		}
	}
	
	/*
	 * @see jsystem.framework.scenario.JTestContainer#setParameters(jsystem.framework.scenario.Parameter[])
	 * + first set the flow control parameters, and only then the other params
	 */
	public void setParameters(Parameter[] params, boolean recursively) {
		Parameter[] before = ParameterUtils.clone(getParameters());
		Parameter[] after = ParameterUtils.clone(params);
		RunnerListenersManager.getInstance().testParametersChanged(getFlowFullUUID(), before,after);
		runnerTest.setParameters(params);
		setTestClassParameters();
		setTestComment(defaultComment());
	}
	
	public static AntIfCondition fromElement(JTestContainer parent, Element element) {
		AntIfCondition newContainer = new AntIfCondition(parent, null);
		initElement(newContainer, element);
		deserializeAdditionalData(newContainer, element);
		return newContainer;
	}
	
	protected static void initElement(AntIfCondition ifCondition, Element element){
		NodeList conditionNode = element.getElementsByTagName(CommonResources.SCRIPT_CONDITION);
		Element condition = (Element)conditionNode.item(0);

		String params,src,language;
		if (condition != null){
			params = condition.getAttribute("params").toString();
			src = condition.getAttribute("src").toString();
			language = condition.getAttribute("language").toString();
		}else{
			params = getParamsFromOldVersions(element);
			src = SCRIPT_PATH;
			language = ScriptLanguage.JAVASCRIPT.getValue();
		}
		
		ifCondition.test.parseParamsString(params,src,language);
		ifCondition.setTestComment(ifCondition.defaultComment());
	}
	
	/**
	 * Backward compatibility support for older versions
	 * 
	 * @param element	the older element to parse
	 * @return	the new parameters representation String
	 */
	private static String getParamsFromOldVersions(Element element){
		NodeList equalsNode = element.getElementsByTagName("equals");
		Element equals = (Element)equalsNode.item(0);
		String arg1 = equals.getAttribute("arg1");
		String arg2 = equals.getAttribute("arg2");
		
		FlowIfTest test = new FlowIfTest();
		
		return test.getParametersStringForOldVersions(arg1, arg2);
	}
	
	public AntIfCondition cloneTest() throws Exception {
		AntIfCondition test = new AntIfCondition(getParent(), getTestId());
		test.rootTests = cloneRootTests(test); 
		return test;
	}

	public Element addExecutorXml(Element targetScenario, Document doc) {
		Element ifElement = doc.createElement(getXmlTag());
		appendAdditionalData(ifElement);
		
		Element scriptCondition = getScriptConditionElement(doc);
		
		ifElement.appendChild(scriptCondition);

		if (!(this instanceof AntIfElseIf)) {
			Element echo1 = doc.createElement("echo");
			echo1.setAttribute("message", "Check condition " + test.getConditionString());
			targetScenario.appendChild(echo1);
		}
			
		/*
		 * Create and add the sequential tag and content
		 */
		Element then = doc.createElement(XML_CONTAINER_TAG);
		
		//go over all tests append a target that invokes them 
		//to the ant script and add an antcall to the test in the execute scenario target
		ifElement.appendChild(then);
		for (JTest jtest : rootTests) {
			// Add echos for AntIfElseIf...
			if (jtest instanceof AntIfElseIf) {
				AntIfElseIf elseIf = (AntIfElseIf)jtest;
				Element elseIf1 = doc.createElement("echo");
				elseIf1.setAttribute("message", "Else if " + elseIf.getIfTest().getConditionString());
				targetScenario.appendChild(elseIf1);
			}
			if ((jtest instanceof AntIfElseIf) || (jtest instanceof AntIfElse)) {
				jtest.addExecutorXml(ifElement, doc);
			} else {
				jtest.addExecutorXml(then, doc);
			}
		}
		
		targetScenario.appendChild(ifElement);
		
		return ifElement;
	}
	
	public Element getContainerElement (Element xmlDefinition) {
		Element container = null;
		NodeList nodes = xmlDefinition.getElementsByTagName(getXmlContainerTag());
				
		Node node = nodes.item(0);
		if ((node instanceof Element) && 
			(((Element)node).getTagName().equals(getXmlContainerTag()))) {
			container = (Element)node;
		}
		return container;
	}
	
	public void createTestsFromElement (Element xmlDefinition, HashMap<String, JTestContainer> targetAndParent,
			HashMap<String, Integer> targetAndPlace) {
		// Create the "then" section
		super.createTestsFromElement(xmlDefinition, targetAndParent, targetAndPlace);
		
		// Create the "else if" tests, which might exist
		ArrayList<Element> nodes = XmlUtils.getChildElementsByTag(AntIfElseIf.XML_TAG, xmlDefinition);
		for (Element node : nodes) {
			JTestContainer elseifContainer = AntIfElseIf.fromElement(this, node);
			elseifContainer.setLoadVersion(loadVersion);
			elseifContainer.createTestsFromElement(node, targetAndParent, targetAndPlace);
			rootTests.add(elseifContainer);
		}
		
		
		// Create the "else" section - each "if" must have an "else"
		nodes = XmlUtils.getChildElementsByTag(AntIfElse.XML_TAG, xmlDefinition);
		
		if (nodes.size() > 0) {
			Element node = nodes.get(0);
			AntFlowControl elseContainer = AntIfElse.fromElement(this, node);
			elseContainer.setLoadVersion(loadVersion);
			elseContainer.createTestsFromElement(node, targetAndParent, targetAndPlace);
			rootTests.add(elseContainer);
		}
	}
	public String getXmlContainerTag() {
		return XML_CONTAINER_TAG;
	}
	public String getXmlTag() {
		return XML_TAG;
	}

	/**
	 * @return the caseSensitive
	 */
	protected Parameter getCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @param caseSensitive the caseSensitive to set
	 */
	protected void setCaseSensitive(Parameter caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * @return the conditionType
	 */
	protected Parameter getConditionType() {
		return conditionType;
	}

	/**
	 * @param conditionType the conditionType to set
	 */
	protected void setConditionType(Parameter conditionType) {
		this.conditionType = conditionType;
	}

	public String defaultComment() {
		return  test.getConditionString();
	}
	
	public boolean canMoveUp(JTest test) {
		boolean canMoveUp = super.canMoveUp(test);
		if (canMoveUp) {
			if (test instanceof AntIfElse) {
				canMoveUp = false;
			} else if (test instanceof AntIfElseIf) {
				int index = getRootIndex(test);
				if (!(rootTests.elementAt(index - 1) instanceof AntIfElseIf)) {
					// Can move ElseIf up only if the one before it is another ElseIf
					canMoveUp = false;
				}
			} else if (test instanceof RunnerTest) {
				int index = getRootIndex(test);
				//Changed in order to resolve bug #170
				if ((rootTests.elementAt(index - 1) instanceof AntIfElse)) {
					// Can't move test down if the one before it is ElseIf
					canMoveUp = false;
				}
			}
		}
		return canMoveUp;
	}

	public boolean canMoveDown(JTest test) {
		boolean canMoveDown = super.canMoveDown(test);
		if (canMoveDown) {
			if (test instanceof AntIfElse) {
				canMoveDown = false;
			} else if (test instanceof AntIfElseIf) {
				int index = getRootIndex(test);
				if (!(rootTests.elementAt(index + 1) instanceof AntIfElseIf)) {
					// Can move ElseIf up only if the one after it is another ElseIf
					canMoveDown = false;
				}
			} else if (test instanceof RunnerTest) {
				int index = getRootIndex(test);
				//Changed in order to resolve bug #170
				if ((rootTests.elementAt(index + 1) instanceof AntIfElse)) {
					// Can't move test down only if the one after it is ElseIf
					canMoveDown = false;
				}
			}
		}
		return canMoveDown;
	}

	protected Element getScriptConditionElement(Document doc){
		Element scriptCondition = doc.createElement(CommonResources.SCRIPT_CONDITION);
		scriptCondition.setAttribute("value", "false");
		
//		if (test.getCompareOption() == CompareOptions.CUSTOM){
//			if (test.getScriptFile() == null){
//				scriptCondition.setAttribute("src", "");
//			}else{
//				scriptCondition.setAttribute("src", test.getScriptFile().getAbsolutePath());
//			}
//			scriptCondition.setAttribute("language", test.getScriptLanguage().getValue());
//		}else{
			scriptCondition.setAttribute("src", SCRIPT_PATH );
			scriptCondition.setAttribute("language", ScriptLanguage.JAVASCRIPT.getValue());
//		}
		scriptCondition.setAttribute("params", test.getParametersString());
		
		addPropertiesToElement(scriptCondition);
		
		return scriptCondition;
	}
	
	public boolean handleUIEvent(Parameter[] params){
		boolean result = runnerTest.handleUIEvent(params);
		updateParametersMap();
		return result;
	}
	
	private void updateParametersMap(){
		parameters.clear();
		for (Parameter currentParameter : runnerTest.getParameters()) {
			parameters.put(currentParameter.getName(), currentParameter);
		}
		setTestComment(defaultComment());
	}
	
	public void loadParametersAndValues(){
		runnerTest.loadParametersAndValues();
		Properties props = runnerTest.getProperties();
		ScenarioHelpers.loadTestProperties(runnerTest, props);
		runnerTest.setProperties(props);
		runnerTest.loadParametersAndValues();
		runnerTest.setTestClassParameters();
		for (Parameter p : runnerTest.getParameters()){
			addParameter(p);
		}
		setTestComment(defaultComment());
		loadAndSetUserDocumentation();
	}
	
	protected void updateScenarioPropertiesFile(){
		super.updateScenarioPropertiesFile();
		ScenarioHelpers.setTestProperty(getFlowFullUUID() ,((Scenario)getRoot()).getName(), "Parameters", test.getParametersString() , false);
	}

	public FlowIfTest getIfTest() {
		return test;
	}

	public String loadUuid(Element target) {
		NodeList conditionNode = target.getElementsByTagName(CommonResources.SCRIPT_CONDITION);
		Element condition = (Element)conditionNode.item(0);
		
		if (condition != null){
			return super.loadUuid(condition);
		}
		return super.loadUuid(target);
	}

	@Override
	protected void loadParameters() {
		runnerTest.setUUID(getUUID());
		loadParametersAndValues();
	}
}
