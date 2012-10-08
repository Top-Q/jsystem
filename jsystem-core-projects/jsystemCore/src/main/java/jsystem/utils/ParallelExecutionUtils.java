/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.util.ArrayList;
import java.util.Iterator;

import jsystem.framework.JSystemProperties;

/**
 * Parallel execution utilities
 * @author goland
 */
public class ParallelExecutionUtils {

	/**
	 * Invokes each {@link Runnable} in <code>runnables</code>
	 * in a separate thread, and waits for all threads to finish execution. 
	 */
	public static void run(Runnable[] runnables) throws Exception {
		ArrayList<Thread> listOfThreads = new ArrayList<Thread>();
		for (Runnable r:runnables){
			Thread t = new Thread(r);
			listOfThreads.add(t);
			t.start();
			long sleepBeforeNextAgentActivation = JSystemProperties.getInstance().getLongPreference("agen.client.sleep_before_agent_activation",5000);
			Thread.sleep(sleepBeforeNextAgentActivation);
		}
		Iterator<Thread> iter = listOfThreads.iterator();
		while (iter.hasNext()){
			Thread t = iter.next();
			t.join();
		}
	}

}
