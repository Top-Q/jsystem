/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.runner.agent.clients.JSystemAgentClient;
import jsystem.runner.agent.server.RunnerEngine;
import jsystem.runner.agent.server.RunnerEngine.ConnectionState;

/**
 * Establishing connections to remote agent and maintaining
 * their status is an expensive operation.
 * The purpose of this class is to open the connections to the agents and  
 * maintain their status.
 * 
 * @author goland
 */
public class JSystemAgentClientsPool {
	
	private static Logger log = Logger.getLogger(JSystemAgentClientsPool.class.getName());
	private static Map<String,RunnerEngine> clients;
	
	/**
	 */
	public static void initPoolFromRepositoryFile() throws Exception {
		init();
		final List<String> urls = AgentList.getAgentsList();
		for (String url:urls){
			if (RunnerEngine.LOCAL_ENGINE.equals(url)){
				continue;
			}
			try {
				JSystemAgentClient client = new JSystemAgentClient(url);
				clients.put(url,client);
			}catch (Exception e){
				log.warning("Failed adding agent " + url + " to agents list. " + e.getMessage() + " removing agent from database.");
				AgentList.removeFromAgentList(url);
			}
		}
		Thread t = new Thread("JSystem agent connection initialization"){
			public void run(){
				while (true){
					String[] urls = clients.keySet().toArray(new String[0]);
					for (String url:urls){
						try {	
							RunnerEngine client = clients.get(url);
							if (client.getConnectionState() != ConnectionState.connected){
								client.init();
							}
						}catch (Throwable t){
							log.log(Level.FINE,"Failed opening connection to " + "",t);
						}					
					}
					try{Thread.sleep(5000);}catch(Exception e){}
				}
			}
		};
		
		t.start();
	}

	/**
	 */
	public static RunnerEngine getClient(String url) {
		return clients.get(url);
	}

	/**
	 */
	public static void removeClient(String url) {
		AgentList.removeFromAgentList(url);
		RunnerEngine client = clients.remove(url);
		if (client != null){
			client.close();
		}
	}

	/**
	 */
	public static RunnerEngine addClient(String url,boolean wait) throws Exception {
		if (clients.get(url) != null){
			return clients.get(url);
		}
		RunnerEngine client = new JSystemAgentClient(url);
		try {
			if (wait){
				client.init();
			}
		}finally {
			clients.put(url,client);
			AgentList.addToAgentList(url);
		}
		return client;
	}
	
	/**
	 */
	public static RunnerEngine[] getClients(String[] urls) {
		ArrayList<RunnerEngine> list = new ArrayList<RunnerEngine>();
		if (urls == null){
			urls = clients.keySet().toArray(new String[0]);
		}
		for (String url:urls){
			list.add(getClient(url));
		}
		return list.toArray(new JSystemAgentClient[0]);
	}

	private static void init() throws Exception {
		clients = Collections.synchronizedMap(new HashMap<String,RunnerEngine>());
	}
}
