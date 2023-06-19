/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario.flow_control;

import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.common.CommonResources;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ParametersManager;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import jsystem.utils.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * AntForLoop implements the specifications of Ant Contrib's For task as described in:
 * http://ant-contrib.sourceforge.net/ant-contrib/manual/tasks/for.html
 */
public class AntForLoop extends AntFlowControl {
	private static final String DEFAULT_PARAM_VALUE = "myVar";
	private static final String DEFAULT_LIST_VALUE = "a;b;c;d";
	public static String OLD_XML_TAG = "for";
	public static String XML_TAG = CommonResources.JSYSTEM_FOR;
	public static String XML_CONTAINER_TAG = "sequential";
	private String[] valuesArray;

	// **** For backward compatibility ****
	// We keep the original values from the XML. We want them to support
	// versions that are before 6.0.02 which did not keep the values in the
	// properties file. See issue#233
	private String origXmlListValue;
	private String origXmlParamValue;
	// ************************************

	private static Pattern LOOP_NUMBER_PATTERN = Pattern.compile("(\\d*)\\.\\.(\\d*)((\\:(\\d*))|\\z)",
			Pattern.CASE_INSENSITIVE);

	// XML strings attributes, as we will use in the created XML
	// Each of the attributes will have its relevant "creator" according to the
	// definition
	// of the task
	// Note: There are several attributes that are not supported or not needed.

	// The list of values to process, with the delimiter character, indicated by
	// the "delimiter" attribute, separating each value.
	// TODO: decide if we need setter/getter, or do we need it at all...
	private Parameter list = new Parameter();

	// Name of the parameter to pass the tokens or files in as to the
	// sequential.
	// TODO: param should be taken from the internal loop properties
	private Parameter param = new Parameter();

	/*
	 * If true, all iterations of the called <sequential> will be executed, even
	 * if a task in one or more of them fails. Defaults to false, which forces
	 * execution to stop as soon as a task fails. At the end, if any iterator
	 * has failed, the <for> task will fail, otherwise <for> will succeed. Note
	 * that execution does not proceed keepgoing from one task to the next
	 * within the <sequential>, but rather from one iteration to the next. It is
	 * up to the caller to ensure that keepgoing execution is safe.
	 */
	private Parameter keepGoing = new Parameter();

	public AntForLoop() {
		this(null, null);
	}

	public static AntForLoop fromElement(JTestContainer parent, Element element) {
		final AntForLoop antForLoopContainer = new AntForLoop(parent, null);

		// Keep the values for backward comatability. See issue #233
		antForLoopContainer.origXmlListValue = element.getAttribute("list").toString();
		antForLoopContainer.setLoopValuesList(antForLoopContainer.origXmlListValue);

		antForLoopContainer.origXmlParamValue = element.getAttribute("param").toString();
		antForLoopContainer.setParamValue(antForLoopContainer.origXmlParamValue);

		deserializeAdditionalData(antForLoopContainer, element);
		antForLoopContainer.setTestComment(antForLoopContainer.defaultComment());
		return antForLoopContainer;
	}

	public AntForLoop(JTestContainer parent, String id) {
		super("For", parent, id);

		list.setType(Parameter.ParameterType.STRING);
		setLoopValuesList(DEFAULT_LIST_VALUE);
		list.setName("list");
		list.setDescription("Semicolon separated list of values to loop over");
		// TODO: sync section with comment/name
		list.setSection(getComment());

		addParameter(list);

		keepGoing.setType(Parameter.ParameterType.BOOLEAN);
		keepGoing.setValue(false);
		keepGoing.setName("keep going");
		keepGoing.setDescription("Define whether continue in case of failure");
		// TODO: sync section with comment/name
		keepGoing.setSection(getComment());

		// TODO: add the keepgoing - addParameter(keepGoing);

		// TODO: get the values from the tests inside, use "enum" maybe ? Ask
		// Nizan
		param.setType(Parameter.ParameterType.STRING);
		param.setValue(DEFAULT_PARAM_VALUE);
		param.setName("loop value");
		param.setDescription("Select the property to change in each iteration");
		// TODO: sync section with comment/name
		param.setSection(getComment());

		addParameter(param);
		setTestComment(defaultComment());
	}

	public AntForLoop cloneTest() throws Exception {
		AntForLoop test = new AntForLoop(getParent(), getTestId());
		test.rootTests = cloneRootTests(test);
		return test;
	}

	public Element addExecutorXml(Element targetScenario, Document doc) {

		Element forElement = doc.createElement(XML_TAG);
		// ITAI: The actual values are set in the properties file and not in
		// the XML file but we still try not to touch the old values that are in
		// the XML for not breaking backward compatability. See issue #233
		forElement.setAttribute("list", origXmlListValue != null ? origXmlListValue : DEFAULT_LIST_VALUE);
		forElement.setAttribute("param", origXmlParamValue != null ? origXmlParamValue : DEFAULT_PARAM_VALUE);
		forElement.setAttribute("delimiter", CommonResources.DELIMITER);
		addPropertiesToElement(forElement);
		appendAdditionalData(forElement);
		// Add "keepgoing" in case set to true
		if (keepGoing.getValue().equals(true)) {
			forElement.setAttribute("keepgoing", "true");
		}

		/*
		 * Create and add the sequential tag and content
		 */
		// TODO: since we need to add the echo into the sequential -
		// we can't use JTestContainer.addExecutorXml...
		// -----------------------------------------------
		Element containerElement = doc.createElement(getXmlContainerTag());

		Element echo2 = doc.createElement("echo");
		echo2.setAttribute("message",
				"Parameter: " + param.getValue().toString() + "=@{" + param.getValue().toString() + "}");
		containerElement.appendChild(echo2);

		Element paramProperty = doc.createElement("var");
		paramProperty.setAttribute("name", param.getValue().toString());
		paramProperty.setAttribute("value", "@{" + param.getValue().toString() + "}");
		containerElement.appendChild(paramProperty);

		Element setAntProperties = doc.createElement(CommonResources.SET_ANT_PROPERTIES);
		XmlUtils.appendComment(setAntProperties,
				"Task for updating the ant parameters file - used for reference parameters");
		containerElement.appendChild(setAntProperties);

		/*
		 * Create and add the container content
		 */
		// go over all tests append a target that invokes them
		// to the ant script and add an antcall to the test in the execute
		// scenario target
		for (JTest jtest : rootTests) {
			jtest.addExecutorXml(containerElement, doc);
		}

		forElement.appendChild(containerElement);
		// -----------------------------------------------

		targetScenario.appendChild(forElement);

		return forElement;
	}

	public Element getContainerElement(Element xmlDefinition) {
		Element container = null;
		NodeList nodes = xmlDefinition.getElementsByTagName(getXmlContainerTag());

		Node node = nodes.item(0);
		if ((node instanceof Element) && (((Element) node).getTagName().equals(getXmlContainerTag()))) {
			container = (Element) node;
		}
		return container;
	}

	/*
	 */
	public void setParameters(Parameter[] params, boolean recursively) {
		for (Parameter p : params) {
			if (p.getName().equals(list.getName())) {
				checkForLoopNumber(p);
			}
		}
		super.setParameters(params, recursively);
	}

	/**
	 * Two examples which explain what the method does: if the user enters
	 * 1..10:2 the text will be replaced with 1;3;5;7;9; if the user enters
	 * 10..1:3 the text will be replaced with 10;7;4;1;
	 */
	private void checkForLoopNumber(Parameter p) {
		String value = p.getValue().toString();
		Matcher m = LOOP_NUMBER_PATTERN.matcher(value);
		if (!m.find()) {
			return;
		}
		String firstNum = m.group(1);
		String secondNum = m.group(2);

		int iFirstNum = Integer.parseInt(firstNum);
		int iSecondNum = Integer.parseInt(secondNum);
		int increment = 1;
		if (!StringUtils.isEmpty(m.group(5))) {
			increment = Integer.parseInt(m.group(5));
		}
		StringBuffer b = new StringBuffer();
		if (iFirstNum <= iSecondNum) {
			for (int i = iFirstNum; i <= iSecondNum; i += increment) {
				b.append(i).append(CommonResources.DELIMITER);
			}

		} else {
			for (int i = iFirstNum; i >= iSecondNum; i -= increment) {
				b.append(i).append(CommonResources.DELIMITER);
			}
		}
		p.setValue(b.toString());
	}

	public String getXmlContainerTag() {
		return XML_CONTAINER_TAG;
	}

	public String defaultComment() {
		return defaultComment(false);
	}

	public String defaultComment(boolean full) {
		String list = this.list.getValue().toString();
		if (!full) {
			if (list.length() > 20) {
				list = list.substring(0, 4) + "..." + list.substring(list.length() - 5, list.length() - 1);
			}
		}
		String result = "Loop over \"" + list + "\" setting \"" + this.param.getValue().toString() + "\" parameter";
		return result;
	}

	/**
	 * get the number of loops for this loop, according to the loop values
	 * String<br>
	 * if the value string is a reference will return -1
	 * 
	 * @return
	 */
	public int getNumOfLoops() {
		if (isValuesReferenceParameter()) {
			return -1;
		}
		String[] values = list.getValue().toString().split(CommonResources.DELIMITER);
		return values.length;
	}

	/**
	 * get the value the loop runs on for a given loop number (first loop is 1,
	 * 0 is out of bounds)
	 * 
	 * @param loopNum
	 *            the loop number to get loop for
	 * @return the loop variable value or "DYNAMIC VALUE" if values is a
	 *         reference (Dynamic)
	 */
	public String getLoopValue(int loopNum) {
		if (isValuesReferenceParameter()) {
			return getDynamicValue();
		}
		valuesArray = list.getValue().toString().split(CommonResources.DELIMITER);
		return valuesArray[loopNum - 1];
	}

	/**
	 * 
	 * @param values
	 */
	public void setLoopValuesList(String values) {
		list.setValue(values);
	}

	private void setParamValue(String paramValue) {
		param.setValue(paramValue);
	}

	public String getLoopParamName() {
		return param.getValue() + "";
	}

	public void setLoopParamName(String loopParamName) {
		param.setValue(loopParamName);
	}

	public boolean isValuesReferenceParameter() {
		return ParametersManager.isReferenceValue(list.getValue());
	}

	public String getTestName(int count) {
		// count ==0 means we reporter is rendering loop main container
		if (count == 0) {
			if (getMeaningfulName() != null) {
				return getMeaningfulName();
			}
			return "Loop over " + list.getValue();
		} else {
			// reporter is rendering loop execution.
			if (getMeaningfulName() != null) {
				return getTestName();
			}
			return "Loop number " + count + ", " + getLoopParamName() + " = " + getLoopValue(count);
		}
	}

	private String getDynamicValue() {
		try {
			Properties props = FileUtils.loadPropertiesFromFile(CommonResources.ANT_INTERNAL_PROPERTIES_FILE);
			String val = (String) props.get(param.getValue());
			return StringUtils.isEmpty(val) ? "DYNAMIC VALUE" : val;
		} catch (Exception e) {
			log.log(Level.FINE, "Failed loading dynamic ant values");
			return "DYNAMIC VALUE";
		}
	}

	@Override
	protected void loadParameters() {
		setLoopValuesList(ScenarioHelpers.getParameterValueFromProperties(this, getFlowFullUUID(), "list",
				list.getValue().toString()));
		setLoopParamName(ScenarioHelpers.getParameterValueFromProperties(this, getFlowFullUUID(), "loop value",
				param.getValue().toString()));
		loadAndSetUserDocumentation();
	}
}
