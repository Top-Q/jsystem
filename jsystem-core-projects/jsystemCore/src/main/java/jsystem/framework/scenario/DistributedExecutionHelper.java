/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.distributedexecution.DistributedExecutionPlugin;
import jsystem.framework.distributedexecution.DistributedRunExecutor;
import jsystem.framework.scenario.Parameter.ParameterType;
import jsystem.utils.StringUtils;

/**
 * Used by the platform to manage and perform distributed execution.
 * @author goland
 */
public class DistributedExecutionHelper {
	
	/**
	 * Distributed execution section.
	 */
	public final static String SCENARIO_HOSTS_PARAMETERS_SECTION = "JSystem Agents";
	
	/**
	 * Name of mandatory parameter.
	 */
	public static final String AGENTS = "Agents";
	
	/**
	 * Instance of the mandatory parameter {@link #AGENTS}. should be cloned by the 
	 * plug-in.
	 */
	public static final DistributedExecutionParameter AGENTS_SELECT = new DistributedExecutionParameter(AGENTS,ParameterType.STRING,"");
	
	
	/**
	 * Returns the current DistributedExecutionPlugin.
	 */
	public static DistributedExecutionPlugin getPlugin() throws Exception{
		String pluginClass = JSystemProperties.getInstance().getPreference(FrameworkOptions.DISTRIBUTED_EXECUTION_PLUGIN);
		if (StringUtils.isEmpty(pluginClass)){
			return (DistributedExecutionPlugin)Class.forName("jsystem.runner.agent.clients.DefaultDistributedExecutionPlugin").newInstance();
		}
		return (DistributedExecutionPlugin)Class.forName(pluginClass).newInstance();
	}

	/**
	 * Given a {@link JTest} returns an array of @{link {@link DistributedExecutionParameter}
	 * that should be presented for this test.
	 * If the system find values for these parameters, the returned parameters are
	 * populated with the values, otherwise, the parameters are returned with default values.
	 */
	static DistributedExecutionParameter[] getHostsParameters(JTest test) throws Exception {
		String rootScenario = ScenariosManager.getInstance().getCurrentScenario().getName();
		return getHostParametersForUniqueId(rootScenario,test.getFullUUID());
	}

	/**
	 * Given a test unique id and root scenario of the test,
	 * returns an array of @{link {@link DistributedExecutionParameter}
	 * that should be presented for this test.
	 * If the system find values for these parameters, the returned parameters are
	 * populated with the values, otherwise, the parameters are returned with default values.
	 */
	public static DistributedExecutionParameter[] getHostParametersForUniqueId(String rootScenarioName,String uniqueId) throws Exception {
		String fileName = ScenarioHelpers.getScenarioPropertiesFile(rootScenarioName);
		Properties prop = new Properties();
		if ((new File(fileName).exists())){
			prop = ScenarioHelpers.getScenarioProperties(rootScenarioName);			
		}
		String uuidString = (StringUtils.isEmpty(uniqueId))? "" : uniqueId+".";
		DistributedExecutionParameter[] hostsParameters = getPlugin().getDistributedExecutionParameters();
		for (DistributedExecutionParameter param:hostsParameters){
			if (prop.getProperty(uuidString+param.getName())!= null){
				param.setValue(prop.getProperty(uuidString+param.getName()));
			}
		}
		return hostsParameters;
	}
	
	
	/**
	 * Updates root scenario properties file with parameters values.
	 */
	static void setHostsParameters(JTest test,DistributedExecutionParameter[] params) throws Exception {
		String rootScenario = ScenariosManager.getInstance().getCurrentScenario().getName();
		Properties prop = ScenarioHelpers.getRootProperties();
		String uuid = test.getFullUUID();
		String uuidString = (StringUtils.isEmpty(uuid))? "" : uuid+".";
		for (Parameter p : params){
				prop.setProperty(uuidString+p.getName(), p.getValue() == null ? "" :p.getValue().toString());
		}
		if (prop.size() == 0){ // no scenario parameters
			return;
		}
		ScenarioHelpers.saveScenarioPropertiesToSrcAndClass(prop, rootScenario,true);
	}
	
	
	/**
	 * Returns true if the array of parameters includes the #AGENTS parameters and if
	 * it has a value.
	 */
	public static boolean isAssigned(DistributedExecutionParameter[] parameters) {
		for (DistributedExecutionParameter p:parameters){
			if (p.getName().equals(AGENTS) && !StringUtils.isEmpty((String)p.getValue())){
				return true;
			}
		}
		return false;
	}
	/**
	 * Returns true if test parameters includes the #AGENTS parameters and if
	 * it has a value.
	 */
	public static boolean isAssigned(JTest test) throws Exception{
		return isAssigned(test.getDistributedExecutionParameters());
	}

	/**
	 * Finds the {@link #AGENTS} parameter, if it is not empty, splits  
	 * the value to array of urls.
	 */
	public static String[] getUrls(DistributedExecutionParameter[] parameters) {
		for (DistributedExecutionParameter p:parameters){
			if (p.getName().equals(AGENTS) && !StringUtils.isEmpty((String)p.getValue())){
				return StringUtils.split((String)p.getValue(),";");
			}
		}
		return new String[0];
	}

	/**
	 * Returns to if <code>t</code> has an ancestor which is associated 
	 * with agents to be executed on.
	 */
	public static boolean ancestorIsAssignedWithHosts(JTest t) throws Exception{
		while (t.getParent()!=null ){
			t = t.getParent();
			if (DistributedExecutionHelper.isAssigned(t.getDistributedExecutionParameters())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gathers all the agents URLs which are associated 
	 * with current scenario.<br>
	 */
	public static String[] getParticipatingHosts() throws Exception {
		Properties props = ScenarioHelpers.getRootProperties();
		Set<String> results = new HashSet<String>();
		gatherHosts(ScenariosManager.getInstance().getCurrentScenario(), props, results);
		return results.toArray(new String[0]);
	}
	
	/**
	 * 
	 */
	private static void gatherHosts(JTestContainer s,Properties props, Set<String> results) throws Exception {
		Iterator<JTest> iter = s.getRootTests().iterator();
		while (iter.hasNext()){
			JTest t = iter.next();
			String uuid = t.getFullUUID();
			String hosts = props.getProperty(uuid+"."+AGENTS);
			if (!StringUtils.isEmpty(hosts)){
				String[] splitedHosts = StringUtils.split(hosts,";");
				for (String h:splitedHosts){
					results.add(h);
				}
			}else {
				if (t instanceof JTestContainer) {
					gatherHosts((JTestContainer)t, props, results);
				}				
			}
		}
	}
		
	/**
	 * Invoked in the test jvm side.
	 * Returns true if the {@value RunningProperties.JSYSTEM_AGENT} is set to  
	 * true.
	 * 
	 * The {@value RunningProperties.JSYSTEM_AGENT} is passed to the test jvm execution command line.
	 * 
	 */
	public static boolean isAgent(){
		String invokeRemoteAgent = System.getProperty(RunningProperties.JSYSTEM_AGENT);
		if ("true".equals(invokeRemoteAgent)){
			return true;
		}
		return false;
	}	
	
	/**
	 * Returns true if the {@value FrameworkOptions.IGNORE_DISTRIBUTED_EXECUTION} is set to  
	 * true.<br>
	 * Invoked in the test jvm side.<br>
	 * The {@value FrameworkOptions.IGNORE_DISTRIBUTED_EXECUTION} is a jsystem property, it can be
	 * set to true by the user.<br>
	 * The purpose of the property is to enable the user to execute scenario locally while 
	 * planing/developing, once the scenario is ready to be executed in a distributed manner,
	 * the flag can be set to false.
	 */
	public static boolean ignoreDistributedExecution(){
		String ignore = JSystemProperties.getInstance().getPreference(FrameworkOptions.IGNORE_DISTRIBUTED_EXECUTION);
		if ("true".equals(ignore)){
			return true;
		}
		return false;
	}

	/**
	 * Returns true if test with <code>uuid</code> is associated with remote
	 * execution parameters.<br>
	 * The method is invoked in test jvm side.
	 * Root scenario name is fetched from a system property named {@value RunningProperties.CURRENT_SCENARIO_NAME}
	 * The value is passed to the test execution jvm by the runner.  
	 */
	public static boolean hasRemoteExecutionProperties(String uuid) throws Exception  {
		String rootScenarioName = System.getProperty(RunningProperties.CURRENT_SCENARIO_NAME);
		DistributedExecutionParameter[] parameters =  DistributedExecutionHelper.getHostParametersForUniqueId(rootScenarioName,uuid);
		if (DistributedExecutionHelper.isAssigned(parameters)){
			return true;
		}
		return false;
	}
	
	/**
	 * Returns a fully populated {@link DistributedRunExecutor}.<br>
	 * Invoked in test execution jvm.<br>
	 * Executor instance is created by the plug-in, and populated from
	 * root scenario properties.
	 */
	public static DistributedRunExecutor getPopulatedExecutor(String uuidToExecute) throws Exception{
		String rootScenarioName = System.getProperty(RunningProperties.CURRENT_SCENARIO_NAME);
		DistributedExecutionPlugin plugin  = DistributedExecutionHelper.getPlugin();
		DistributedRunExecutor executor = plugin.getDistributedRunExecutor();
		DistributedExecutionParameter[] parameters =  DistributedExecutionHelper.getHostParametersForUniqueId(rootScenarioName,uuidToExecute);
		executor.setHostsParameters(parameters);
		Scenario s = ScenariosManager.getInstance().getScenario(rootScenarioName);
		s.loadParametersAndValues();
		JTest t = ScenarioHelpers.getTestById(s,uuidToExecute);
		executor.setTestToExecute(t);
		return executor;
	}
	
	/**
	 * Return true if test/scenario with id <code>uuid</code> should
	 * be executed remotely.
	 */
	public static boolean doRemoteExecution(String uuid) throws Exception {
		return !isAgent()&& hasRemoteExecutionProperties(uuid) && !ignoreDistributedExecution();
	}
	
}
