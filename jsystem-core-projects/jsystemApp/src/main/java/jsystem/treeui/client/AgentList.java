/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.client;

import java.util.ArrayList;
import java.util.List;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.runner.agent.server.RunnerEngine;
import jsystem.utils.StringUtils;

/**
 * Manages the repository of the agents that the runner application worked with.<br>
 * Agents list is saves in the {@link FrameworkOptions#AGENT_LIST} jsystem property.
 * The {@link FrameworkOptions#AGENT_LIST} property holds a list of the agents (host:port) to which 
 * the runner application connected since the last time the property was reset until now.<br>
 * Property format is "host:port;host1:port ...". agents are ordered by the time to which the
 * runner application connected to the agent i.e. the agent the the runner application last successfully
 * connected to will be first in the list. 
 * @author goland
 */
public class AgentList {
	
	/**
	 * Returns the list of agents that the runner application connected since
	 * last property reset.<br>
	 * @see AgentList
	 */
	public static List<String> getAgentsList() {
		List<String> toReturn = new ArrayList<String>();
		String agentList = JSystemProperties.getInstance().getPreference(FrameworkOptions.AGENT_LIST);
		if ( StringUtils.isEmpty(agentList) ){
			toReturn.add(RunnerEngine.LOCAL_ENGINE);
			return toReturn;
		}
		String[] list =  StringUtils.split(agentList,";");
		for (String s:list){
			toReturn.add(s);
		}
		return toReturn;
	}
	
	/**
	 * Adds an agent to the beginning of the agents list.<br>
	 * If agent already exists in the list it is moved to the beginning of the list.
	 */
	public static void addToAgentList(String agent) {
		if (agent == null){
			return;
		}
		agent = agent.trim();
		List<String> l = getAgentsList();
		l.remove(agent);
		l.add(0,agent);
		String newListProp = StringUtils.objectArrayToString(";",l.toArray());
		JSystemProperties.getInstance().setPreference(FrameworkOptions.AGENT_LIST, newListProp);
	}

	/**
	 * Removes an agent from agents list.
	 */
	public static void removeFromAgentList(String agent) {
		if (agent == null){
			return;
		}
		List<String> l = getAgentsList();
		l.remove(agent);
		String newListProp = StringUtils.objectArrayToString(";",l.toArray());
		JSystemProperties.getInstance().setPreference(FrameworkOptions.AGENT_LIST, newListProp);
	}
}
