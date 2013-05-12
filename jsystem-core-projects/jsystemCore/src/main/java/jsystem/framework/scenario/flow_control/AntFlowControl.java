/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario.flow_control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;

import jsystem.framework.common.CommonResources;
import jsystem.framework.report.RunnerListenersManager;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ParameterUtils;
import jsystem.framework.scenario.ParametersManager;
import jsystem.framework.scenario.RunnerFixture;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.RunningProperties;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import jsystem.utils.XmlUtils;
import junit.framework.TestResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * AntFlowControl is an abstract class for all of the Ant flow control classes, defining
 * the basic implementation.  
 *
 * @author Shushu
 */
public abstract class AntFlowControl extends JTestContainer {
	/*
	 * Flow control has its own parameters...
	 */
	protected HashMap<String, Parameter> parameters = new HashMap<String, Parameter>();
	
	public AntFlowControl() {
	}
	
	public AntFlowControl (String name, JTestContainer parent, String id) {
		super(name, parent, id,getRandomUUID());
	}
	
	/* (non-Javadoc)
	 * @see jsystem.framework.scenario.JTest#load()
	 * TODO: in global, this load() suppose to be similar to the Scenario's one, with some changes
	 * It suppose to fill all of the allTests from the already defined content (XML ?)
	 */
	@Override
	public void load() throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see jsystem.framework.scenario.JTest#setTestClassParameters()
	 */
	@Override
	public void setTestClassParameters() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see jsystem.framework.scenario.JTest#setXmlFields(java.util.Properties)
	 */
	@Override
	public void setXmlFields(Properties fields) {
		// TODO: this feature seems to be relevant only for import mechanism,
		//       which is not supported for flow control for now.
		throw new RuntimeException("setXmlFields method should not be called");
	}

	/* (non-Javadoc)
	 * @see junit.framework.Test#run(junit.framework.TestResult)
	 */
	@Override
	public void run(TestResult arg0) {
		// Flow control cannot be executed by itself (like Scenario can't)
		throw new RuntimeException("Flow control run should not be invoked");
	}

	/* Gets a chunk of XML, and return a new AntFlowControl object
	 * TODO: Maybe should be abstract and implemented separately
	 */
	public static AntFlowControl fromElement(
			Element parentScenarioCallingTarget, Element scenarioElementTarget,
			Object object, Scenario scenario) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * get all tests parameters
	 * 
	 * @param doFilter	get only visible parameters
	 * @return
	 */
	protected Parameter[] getParameters(boolean doFilter, boolean recursive) {
		ArrayList<Parameter> allParams = new ArrayList<Parameter>();
		for (Parameter param : getFlowControlParams().values()) {
			allParams.add(param);
		}
		
		Parameter[] paramsArray = new Parameter[allParams.size()];
		return (allParams.toArray(paramsArray));
	}
	
	/**
	 * Get all test parameters
	 * 
	 * @param doFilter	True will filter parameters that are not visible
	 * @param recursive	will go recursively into all sub-scenarios\containers
	 * @return	all Parameters array
	 */
	public Parameter[] getTestParameters(boolean doFilter,boolean recursive){
		return getTestParameters(doFilter, recursive,false);
	}
	
	/**
	 * Get all test parameters
	 * 
	 * @param doFilter	True will filter parameters that are not visible
	 * @param recursive	will go recursively into all sub-scenarios\containers
	 * @param includingFlow	if True then flow parameters will also be returned 
	 * @return	all Parameters array
	 */
	public Parameter[] getTestParameters(boolean doFilter,boolean recursive, boolean includingFlow){
		ArrayList<Parameter> allParams = new ArrayList<Parameter>();
		
		if (includingFlow){
			for (Parameter param : getFlowControlParams().values()) {
				allParams.add(param);
			}
		}
		
		//	Get all of the parameters except to the control flow ones
		for (Parameter param : super.getParameters(doFilter,recursive)) {
			allParams.add(param);
		}
		return (allParams.toArray(new Parameter[0]));
	}

	private HashMap<String, Parameter> getFlowControlParams() {
		return parameters;
	}

	protected void setFlowControlParameters(Parameter[] params) {
		for (Parameter givenParameter : params) {
			if ((!givenParameter.isDirty()) || (givenParameter.getValue() == null)) {
				continue;
			}
			Parameter originalParameter = parameters.get(givenParameter.getName());
			if (originalParameter == null){
				continue;
			}
			Object value = givenParameter.getValue();
			originalParameter.signalToSave();
			originalParameter.setValue(value);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see jsystem.framework.scenario.JTestContainer#setParameters(jsystem.framework.scenario.Parameter[])
	 * + first set the flow control parameters, and only then the other params
	 */
	public void setParameters(Parameter[] params, boolean recursively) {
		Parameter[] before = ParameterUtils.clone(getParameters());
		Parameter[] after = ParameterUtils.clone(params);
		setFlowControlParameters(params);
		RunnerListenersManager.getInstance().testParametersChanged(getFlowFullUUID(), before,after);
		setTestComment(defaultComment());
	}
	
	protected void addParameter(Parameter parameter) {
		this.parameters.put(parameter.getName(), parameter);
	}
	
	protected void updateScenarioPropertiesFile(){
		Scenario r = (Scenario) getRoot();
		ScenarioHelpers.updateTestProperties(getFlowFullUUID() + ".", getParameters(), r.getName(), true);
	}
	
	// TODO: maybe move to JTestContainer
	public void addTestsXmlToRoot(Document doc, Integer[] indexes) {
		updateScenarioPropertiesFile();
		for (JTest jtest : rootTests) {
			String targetName = null;
			if (jtest instanceof RunnerFixture) {
				targetName = "f" + indexes[0] + "_" + ((RunnerFixture) jtest).getClassName();
				indexes[0]++;
				jtest.setTestId(targetName);
			} else if ((jtest instanceof RunnerTest) || (jtest instanceof Scenario)) {
				targetName = "t" + indexes[1];
				indexes[1]++;
				jtest.setTestId(targetName);
			}
			
			//appending test target to xml
			jtest.addTestsXmlToRoot(doc, indexes);	
		}
	}
	
	public String getFullUUID() {
		return getParentFullUUID();
	}
	
	/**
	 * get the fullUuid (similar to RunnerTest\Scenario fullUuid)
	 * 
	 * @return
	 */
	public String getFlowFullUUID(){
		return super.getFullUUID();
	}
	
	/**
	 * Since Flow has no Unique ID in Ant context,
	 * It should not have a unique ID in model too
	 */
	public String getUUIDUpTo(JTest toStopAt){
		return getParent().getUUIDUpTo(toStopAt);
	}
	
	public String getFullTestId() {
		return getParent().getFullTestId();
	}

	public abstract String defaultComment();
	
	
	@Override
	public void addAntPropertiesToTarget(Element ant, Document doc) {
		for (Object testObject : rootTests) {
			JTest test = (JTest)testObject;
			
			test.addAntPropertiesToTarget(ant, doc);
		}
	}
	
	/**
	 * Appends flow control entity additional properties 
	 * (for now it is only meaningful name) as an element comment.
	 */
	public void appendAdditionalData(Element element) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("#Jsystem#");
		serializeMeaningfulName(buffer);
		XmlUtils.appendComment(element,buffer.toString());
	}

	/**
	 * Reads additional properties from element comment
	 */
	public static void  deserializeAdditionalData(JTestContainer container,Element element) {
		String comment = XmlUtils.getComment(element, 0);
		if (comment == null){
			return;
		}
		extractMeaningfulName(container, comment);
	}

	
	private void serializeMeaningfulName(StringBuffer buffer) {
		if (!StringUtils.isEmpty(getMeaningfulName())){
			buffer.append(MEANINGFULNAME_FORMAT.format(new Object[]{getMeaningfulName()}));			
		}
	}
	
	private static void extractMeaningfulName(JTestContainer container,String comment) {
		Matcher m = MEANINGFULNAME_PATTERN.matcher(comment);
		if (m.find()){
			String meaningfulName = m.group(1);
			container.setMeaningfulName(meaningfulName, false);
		}
	}
	
	/**
	 * Get name now checks whether there is a meaningful name, if so
	 * it returns it, otherwise returns name and comment.
	 * 
	 * Note that meaningful name supports embedding ant parameters
	 * in the following format: ${param}
	 * 
	 */
	@Override
	public String getTestName() {
		if (!StringUtils.isEmpty(getMeaningfulName())){
			return updateNameWithAntParameters(getMeaningfulName());
		}
		return getName() + " - " + getComment();
	}
	
	/**
	 */
	private String  updateNameWithAntParameters(String name){
		try {
			Matcher matcher = ParametersManager.PARAMETER_PATTERN.matcher(name);
			Properties props = FileUtils.loadPropertiesFromFile(CommonResources.ANT_INTERNAL_PROPERTIES_FILE);
			while (matcher.find()) {
				String param = matcher.group(1);
				String value = props.getProperty(param);
				String match = matcher.group(0);
				if (value != null){
					name = StringUtils.replace(name, match,value);
				}			
			}
		}catch (Exception e){
			log.log(Level.FINE,"Failed loading ant properties file.",e);
		}
		
		return name;
	}
	
	/**
	 * load the Unique test Id from the XML system property<br>
	 * if no value is found, generate a new one otherwise
	 * 
	 * @param target
	 *            the test target element
	 * @return the unique id String
	 */
	public String loadUuid(Element target) {
		String uuid = target.getAttribute("fullUuid");
		if (!StringUtils.isEmpty(uuid)){
			String toReturn = uuid.substring(uuid.lastIndexOf(".") + 1); 
			return toReturn;
		}
		log.fine("Flow element did not have a UUID, Generating a new one");
		return getRandomUUID();
	}
	
	/**
	 * load the Unique test Id from the XML system property<br>
	 * if no value is found, generate a new one otherwise
	 * 
	 * @param target
	 *            the test target element
	 * @return the unique id String
	 */
	public void loadUuidAndParameters(Element target) {
		setUUID(loadUuid(target));
		loadParameters();
		setTestComment(defaultComment());
	}
	
	protected abstract void loadParameters();
	
	/**
	 * Add Unique ID and Scenario parent name values to Xml element
	 * 	
	 * @param element	the element to add attributes to
	 */
	protected void addPropertiesToElement(Element element){
		element.setAttribute("parentName", "${jsystem.parent.name}.${ant.project.name}");
		element.setAttribute("fullUuid", "${jsystem.parent.uuid}.${jsystem.uuid}." + getUUID());
		saveFlowDocumentation();
	}
	
	/**
	 * User documentation
	 */
	protected void loadAndSetUserDocumentation() {
		if (StringUtils.isEmpty(this.getFlowFullUUID())){
			return;
		}		
		String documentation = 
			ScenarioHelpers.getTestProperty(this.getFlowFullUUID(), 
				                            ScenarioHelpers.getRoot(this).getName(),
				                            RunningProperties.DOCUMENTATION_TAG);
		if (documentation != null) {
			setDocumentation(documentation);
		}
	}
	
	/**
	 * 
	 */
	private void saveFlowDocumentation() {
		Properties p = new Properties();
		p.setProperty(RunningProperties.DOCUMENTATION_TAG,getDocumentation() == null? "" : getDocumentation());
		ScenarioHelpers.setTestInnerProperty(getFlowFullUUID(),
				                        ScenarioHelpers.getRoot(this).getName(),p,true);
	}
}
