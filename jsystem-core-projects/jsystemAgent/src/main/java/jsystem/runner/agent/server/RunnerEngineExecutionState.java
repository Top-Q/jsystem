/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.server;

/**
 * Enumeration  for runner engine execution state.
 * @author goland
 */
public enum RunnerEngineExecutionState {
	/**
	 * Engine is in initiation stage
	 */
	initiating,
	/**
	 * Idle, waiting for a job
	 */
	idle,

	/**
	 * Idle, waiting for a job, last execution ended with an unexpected error
	 */
	idleError,
	
	/**
	 * Executing a job
	 */
	running,
	
	/**
	 * In stop process
	 */	
	stopping,
	
	/**
	 * Paused
	 */
	paused
}
