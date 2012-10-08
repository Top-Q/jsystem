/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.client;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import jsystem.runner.agent.server.RunnerEngine;
import jsystem.runner.agent.server.RunnerEngineImpl;

/**
 * Extension of the {@link RunnerEngineImpl} to encapsulate activation of agents 
 * in addition to local activation of the engine.<br>
 * 
 * @author goland
 */
public class ApplicationRunnerEngineImpl extends RunnerEngineImpl {
	private static Logger log = Logger.getLogger(ApplicationRunnerEngineImpl.class.getName());

	public ApplicationRunnerEngineImpl() throws Exception {
		super();
	}
	
	/**
	 * @see RunnerEngine#pause()
	 */
	public void pause() throws Exception {
		super.pause();
		executeOnAgents("pause");
	}

	/**
	 * @see RunnerEngine#stop()
	 */
	public void stop() throws Exception {
		super.stop();
		executeOnAgents("stop");
	}

	/**
	 * @see RunnerEngine#gracefulStop()
	 */
	public void gracefulStop() throws Exception {
		super.gracefulStop();
		executeOnAgents("gracefulStop");
	}

	/**
	 * @see RunnerEngine#resume()
	 */
	public void resume() throws Exception {
		super.resume();
		executeOnAgents("resume");
	}

	private void executeOnAgents(String operation) {
		Method m = null;
		try {
			m = RunnerEngine.class.getMethod(operation, (Class[]) null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		RunnerEngine[] engines = JSystemAgentClientsPool.getClients(null);
		for (RunnerEngine engine : engines) {
			if (engine.getConnectionState().equals(ConnectionState.connected)) {
				try {
					m.invoke(engine, (Object[]) null);
				} catch (Exception e) {
					log.warning("Failed performing " + operation +" on " + engine.getId() + " " + e.getMessage());
				}
			} else {
				log.fine(engine.getId() + " is disconnected. skipping activation");
			}
		}

	}
}

/**
 * 
 * 
 * 
 */
