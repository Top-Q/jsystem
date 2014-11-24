package jsystem.framework.scenario.flow_control;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jsystem.framework.common.CommonResources;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.utils.XmlUtils;

/**
 * Presentation of the data driven action in the scenario
 * 
 * @author Itai_Agmon
 * 
 */
public class AntDataDriven extends AntFlowControl {

	private Parameter dataSourceFile = new Parameter();

	private Parameter dataSourceType = new Parameter();
	
	private Parameter dataSourceParam = new Parameter();

	public static String XML_TAG = CommonResources.JSYSTEM_DATADRIVEN;
	public static String XML_CONTAINER_TAG = "sequential";

	protected RunnerTest runnerTest;

	public AntDataDriven() {
		this(null, null);
	}

	public AntDataDriven(JTestContainer parent, String id) {
		super("DataDriven", parent, id);
		dataSourceFile.setType(Parameter.ParameterType.FILE);
		dataSourceFile.setValue("");
		dataSourceFile.setName("File");
		dataSourceFile.setDescription("Data Source File");
		// TODO: sync section with comment/name
		dataSourceFile.setSection(getComment());

		addParameter(dataSourceFile);

		dataSourceType.setType(Parameter.ParameterType.STRING);
		dataSourceType.setAsOptions(true);
		dataSourceType.setOptions(new Object[] { "Csv", "Database" });
		dataSourceType.setValue("Csv");
		dataSourceType.setName("Type");
		dataSourceType.setDescription("Data Source Type");
		dataSourceType.setSection(getComment());
		addParameter(dataSourceType);
		
		dataSourceParam.setType(Parameter.ParameterType.STRING);
		dataSourceParam.setValue("");
		dataSourceParam.setName("Parameters");
		dataSourceParam.setDescription("Various data driven mechanism parameters");
		dataSourceParam.setSection(getComment());
		addParameter(dataSourceParam);
		
		setTestComment(defaultComment());

	}

	public Element addExecutorXml(Element targetScenario, Document doc) {
		Element dataDrivenElement = doc.createElement(XML_TAG);
		// ITAI: The actual values are set in the properties file and not in the
		// XML file
		dataDrivenElement.setAttribute("delimiter", CommonResources.DELIMITER);
		addPropertiesToElement(dataDrivenElement);
		appendAdditionalData(dataDrivenElement);

		/*
		 * Create and add the sequential tag and content
		 */
		// TODO: since we need to add the echo into the sequential -
		// we can't use JTestContainer.addExecutorXml...
		// -----------------------------------------------
		Element containerElement = doc.createElement(getXmlContainerTag());

		Element echo2 = doc.createElement("echo");
		echo2.setAttribute("message", "Data Driven");
		containerElement.appendChild(echo2);

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

		dataDrivenElement.appendChild(containerElement);
		// -----------------------------------------------

		targetScenario.appendChild(dataDrivenElement);

		return dataDrivenElement;
	}

	@Override
	public AntDataDriven cloneTest() throws Exception {
		AntDataDriven test = new AntDataDriven(getParent(), getTestId());
		test.rootTests = cloneRootTests(test);
		return test;
	}

	@Override
	public String defaultComment() {
		return "Data driven according to  \"" + dataSourceType.getValue() + "\"";
	}

	@Override
	public String getXmlContainerTag() {
		return XML_CONTAINER_TAG;
	}

	@Override
	protected void loadParameters() {
		setDataSourceType(ScenarioHelpers.getParameterValueFromProperties(this, getFlowFullUUID(), "Type",
				dataSourceType.getValue() == null ? null : dataSourceType.getValue().toString()));
		setDataSourceFile(ScenarioHelpers.getParameterValueFromProperties(this, getFlowFullUUID(), "File",
				dataSourceFile.getValue() == null ? null : dataSourceFile.getValue().toString()));
		setDataSourceFile(ScenarioHelpers.getParameterValueFromProperties(this, getFlowFullUUID(), "Parameters",
				dataSourceParam.getValue() == null ? null : dataSourceParam.getValue().toString()));
		loadAndSetUserDocumentation();
	}

	@Override
	public Element getContainerElement(Element xmlDefinition) {
		Element container = null;
		NodeList nodes = xmlDefinition.getElementsByTagName(getXmlContainerTag());

		Node node = nodes.item(0);
		if ((node instanceof Element) && (((Element) node).getTagName().equals(getXmlContainerTag()))) {
			container = (Element) node;
		}
		return container;
	}

	public static JTestContainer fromElement(JTestContainer parent, Element element) {
		AntDataDriven newContainer = new AntDataDriven(parent, null);
		deserializeAdditionalData(newContainer, element);
		newContainer.setTestComment(newContainer.defaultComment());
		return newContainer;

	}

	public void setDataSourceFile(String dataSourceFileValue) {
		this.dataSourceFile.setValue(dataSourceFileValue);
	}

	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType.setValue(dataSourceType);
	}

	public void setDataSourceParam(String dataSourceParamValue) {
		this.dataSourceParam.setValue(dataSourceParam);
	}
	
	

}
