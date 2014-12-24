package jsystem.framework.scenario.flow_control;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.Parameter.ParameterType;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.flow_control.datadriven.CsvDataCollector;
import jsystem.framework.scenario.flow_control.datadriven.DataProvider;
import jsystem.utils.XmlUtils;
import jsystem.utils.beans.BeanUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Presentation of the data driven action in the scenario
 * 
 * @author Itai_Agmon
 * 
 */
public class AntDataDriven extends AntFlowControl {

	private Parameter dataSourceFile = new Parameter();

	private Parameter dataSourceParam = new Parameter();

	private Parameter dataSourceLineIndexes = new Parameter();

	private Parameter dataSourceShuffle = new Parameter();

	private Parameter dataSourceShuffleSeed = new Parameter();

	private Parameter dataSourceReverseOrder = new Parameter();

	private Parameter dataSourceType = new Parameter();

	public static String XML_TAG = CommonResources.JSYSTEM_DATADRIVEN;
	public static String XML_CONTAINER_TAG = "sequential";

	protected RunnerTest runnerTest;

	public AntDataDriven() {
		this(null, null);
	}

	public AntDataDriven(JTestContainer parent, String id) {
		super("DataDriven", parent, id);
		dataSourceType.setType(ParameterType.STRING);
		dataSourceType.setAsOptions(true);
		
		List<String> options = new ArrayList<>();
		final String[] providersClassName = JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.DATA_DRIVEN_DATA_PROVIDER).split(";");
		for (int i = 0; i < providersClassName.length; i++) {
			DataProvider provider = BeanUtils.createInstanceFromClassName(providersClassName[i], DataProvider.class);
			if (null == provider) {
				log.log(Level.WARNING, "Fail to init provider: " + providersClassName[i]);
				provider = new CsvDataCollector();
			}
			String providerName = ((DataProvider) provider).getName();
			options.add(providerName);
		}
		
		dataSourceType.setOptions(options.toArray());
		dataSourceType.setValue("");
		dataSourceType.setName("Type");
		dataSourceType.setDescription("Data Provider Type");
		dataSourceType.setSection(getComment());
		addParameter(dataSourceType);

		dataSourceFile.setType(Parameter.ParameterType.FILE);
		dataSourceFile.setValue("");
		dataSourceFile.setName("File");
		dataSourceFile.setDescription("Data Source File");
		// TODO: sync section with comment/name
		dataSourceFile.setSection(getComment());
		addParameter(dataSourceFile);

		dataSourceParam.setType(Parameter.ParameterType.STRING);
		dataSourceParam.setValue("");
		dataSourceParam.setName("Parameter");
		dataSourceParam.setDescription("Open data provider parameter");
		dataSourceParam.setSection(getComment());
		addParameter(dataSourceParam);

		dataSourceLineIndexes.setType(Parameter.ParameterType.STRING);
		dataSourceLineIndexes.setValue("");
		dataSourceLineIndexes.setName("LineIndexes");
		dataSourceLineIndexes
				.setDescription("One-based, comma separated list of the required line indexes. You can use '-' for ranges");
		dataSourceLineIndexes.setSection(getComment());
		addParameter(dataSourceLineIndexes);

		dataSourceShuffle.setType(ParameterType.BOOLEAN);
		dataSourceShuffle.setValue(false);
		dataSourceShuffle.setName("Shuffle");
		dataSourceShuffle.setDescription("Data will be provided in pseudorandom order using the specified seed");
		dataSourceShuffle.setSection(getComment());
		addParameter(dataSourceShuffle);

		dataSourceShuffleSeed.setType(ParameterType.LONG);
		dataSourceShuffleSeed.setValue(0);
		dataSourceShuffleSeed.setName("ShuffleSeed");
		dataSourceShuffleSeed.setDescription("Random seed. Use '0' for default");
		dataSourceShuffleSeed.setSection(getComment());
		addParameter(dataSourceShuffleSeed);

		dataSourceReverseOrder.setType(ParameterType.BOOLEAN);
		dataSourceReverseOrder.setValue(false);
		dataSourceReverseOrder.setName("ReverseOrder");
		dataSourceReverseOrder.setDescription("Data will be providerd in reverse order");
		dataSourceReverseOrder.setSection(getComment());
		addParameter(dataSourceReverseOrder);

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
		return "Data driven ";
	}

	@Override
	public String getXmlContainerTag() {
		return XML_CONTAINER_TAG;
	}

	@Override
	protected void loadParameters() {
		setDataSourceFile(ScenarioHelpers.getParameterValueFromProperties(this, getFlowFullUUID(), "File",
				dataSourceFile.getValue() == null ? null : dataSourceFile.getValue().toString()));

		setDataSourceParam(ScenarioHelpers.getParameterValueFromProperties(this, getFlowFullUUID(), "Parameter",
				dataSourceParam.getValue() == null ? null : dataSourceParam.getValue().toString()));

		setDataSourceLineIndexes(ScenarioHelpers.getParameterValueFromProperties(this, getFlowFullUUID(),
				"LineIndexes", dataSourceLineIndexes.getValue() == null ? null : dataSourceLineIndexes.getValue()
						.toString()));

		setDataSourceShuffle(Boolean.valueOf(ScenarioHelpers.getParameterValueFromProperties(this, getFlowFullUUID(),
				"Shuffle", dataSourceShuffle.getValue() == null ? null : dataSourceShuffle.getValue().toString())));

		setDataSourceShuffleSeed(Integer.valueOf(ScenarioHelpers.getParameterValueFromProperties(this,
				getFlowFullUUID(), "ShuffleSeed", dataSourceShuffleSeed.getValue() == null ? null
						: dataSourceShuffleSeed.getValue().toString())));

		setDataSourceReverseOrder(Boolean.valueOf(ScenarioHelpers.getParameterValueFromProperties(this,
				getFlowFullUUID(), "ReverseOrder", dataSourceReverseOrder.getValue() == null ? null
						: dataSourceReverseOrder.getValue().toString())));

		setDataSourceType(ScenarioHelpers.getParameterValueFromProperties(this, getFlowFullUUID(), "Type",
				dataSourceType.getValue() == null ? null : dataSourceType.getValue().toString()));

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

	public void setDataSourceParam(String dataSourceParamValue) {
		this.dataSourceParam.setValue(dataSourceParamValue);
	}

	public void setDataSourceLineIndexes(String dataSourceLineIndexesValue) {
		this.dataSourceLineIndexes.setValue(dataSourceLineIndexesValue);
	}

	public void setDataSourceShuffle(boolean dataSourceShuffleValue) {
		this.dataSourceShuffle.setValue(dataSourceShuffleValue);
	}

	public void setDataSourceShuffleSeed(long dataSourceShuffleSeedValue) {
		this.dataSourceShuffleSeed.setValue(dataSourceShuffleSeedValue);
	}

	public void setDataSourceReverseOrder(boolean dataSourceReverseOrderValue) {
		this.dataSourceReverseOrder.setValue(dataSourceReverseOrderValue);
	}

	public void setDataSourceType(String dataSourceTypeValue) {
		this.dataSourceType.setValue(dataSourceTypeValue);
	}

}
