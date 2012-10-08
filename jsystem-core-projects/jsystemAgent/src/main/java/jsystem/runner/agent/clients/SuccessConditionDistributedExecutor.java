/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.clients;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.RunProperties;
import jsystem.framework.report.ExecutionListener;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.DistributedExecutionParameter;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.runner.ErrorLevel;
import junit.framework.AssertionFailedError;
import junit.framework.SystemTest;
import junit.framework.Test;


/**
 * Executor which implements the PassIfOnePass condition.
 * 
 * 
 *               THIS CLASS IS NOT OPERATIONAL 
 * 
 * 
 * 
 * 
 * 
 * @see SuccessConditionDistributedExecutionListener
 * @see DistributedExecutionPluginWithSuccessControl
 * @author goland
 */
public class SuccessConditionDistributedExecutor extends DefaultDistributedExecutor{
	private static Logger log = Logger.getLogger(SuccessConditionDistributedExecutor.class.getName());
	
	protected ExecutionListener getDistributedExecutionListener() {
		boolean passIfOnePass = false;
		boolean getRunProps = false;
		DistributedExecutionParameter[] params = getParameters();
		for (DistributedExecutionParameter p:params){
			if (p.getName().equals(DistributedExecutionPluginWithSuccessControl.PASS_IF_ONE_PASS)){
				passIfOnePass = (Boolean)p.getValue();
			}
			if (p.getName().equals(DistributedExecutionPluginWithSuccessControl.GET_RUN_PROPERTIES)){
				getRunProps = (Boolean)p.getValue();
			}

		}
		return new SuccessConditionDistributedExecutionListener(passIfOnePass,getRunProps,getUrls().length);
	}

	
	/**
	 * If passIfOnePass is set to true, the listener gathers all test failures and error, and
	 * counts number of test execution.<br>
	 * When the number of execution equals the number of agents, this means that test
	 * was executed on all agents, at this stage the listener counts the number
	 * of errors and failures. IF the number is smaller then number of executions, this
	 * means that at least one test execution passed, otherwise all test execution failed
	 * and last error/failure is sent to the runner.
	 * @author goland
	 */
	public class SuccessConditionDistributedExecutionListener implements ExecutionListener{
		volatile private boolean passIfOnePass = false;
		volatile private boolean getRunProps = false;
		private Map<String,Throwable> hasFail;
		private Map<String,Integer> count;
		private Map<String,Integer> errorCount;
		private int numberOfAgents;
		
		SuccessConditionDistributedExecutionListener(boolean passIfOnePass,boolean getRunProps,int numberOfAgents){
			this.passIfOnePass = passIfOnePass;
			this.numberOfAgents = numberOfAgents;
			this.getRunProps = getRunProps;
			hasFail = Collections.synchronizedMap(new HashMap<String,Throwable>());
			count = Collections.synchronizedMap(new HashMap<String,Integer>());
			errorCount = Collections.synchronizedMap(new HashMap<String,Integer>());
		}
		
		
		@Override
		public void errorOccured(String title, String message, ErrorLevel level) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void executionEnded(String scenarioName) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void remoteExit() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void remotePause() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addWarning(Test test) {
			
		}

		@Override
		public void endRun() {
			if (getRunProps){
				try {
					getAndMergeRunProperties();
				}catch (Exception e) {
					log.log(Level.WARNING, "Failed getting agent run properties",e );
				}
			}			
		}

		@Override
		public void startTest(TestInfo testInfo) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public synchronized void addError(Test arg0, Throwable arg1) {
			if (passIfOnePass){
				SystemTest jtest = (SystemTest)arg0;
				hasFail.put(jtest.getFullUUID(), arg1);
				incrementError(jtest.getFullUUID());
			}else {
				ListenerstManager.getInstance().addError(arg0, arg1);
			}
		}

		@Override
		public synchronized void addFailure(Test arg0, AssertionFailedError arg1) {
			if (passIfOnePass){
				SystemTest jtest = (SystemTest)arg0;
				Throwable t = hasFail.get(jtest.getFullUUID());
				if (t == null){
					hasFail.put(jtest.getFullUUID(), arg1);				
				}
				incrementError(jtest.getFullUUID());
			}else {
				ListenerstManager.getInstance().addFailure(arg0, arg1);
			}
		}

		@Override
		public synchronized void endTest(Test arg0) {
			if (passIfOnePass){
				SystemTest jtest = (SystemTest)arg0;
				incrementCount(jtest.getFullUUID());
				int numberOfExecutions = getNumberExecutions(jtest.getFullUUID());
				if (numberOfExecutions == numberOfAgents){
					int numberOfErrors = getNumberOfErrors(jtest.getFullUUID());
					if (numberOfErrors == numberOfExecutions){
						Throwable t = hasFail.get(jtest.getFullUUID());
						if (t != null){
							if (t instanceof AssertionFailedError ){
								ListenerstManager.getInstance().addFailure(arg0, (AssertionFailedError)t);
							}else {
								ListenerstManager.getInstance().addError(arg0, t);
							}
						}						
					}
				}
			}
			ListenerstManager.getInstance().endTest(arg0);
		}

		@Override
		public synchronized  void startTest(Test arg0) {
			ListenerstManager.getInstance().startTest(arg0);
		}

		private void incrementCount(String uuid){
			if (count.get(uuid) == null){
				count.put(uuid,0);
			}
			int numberOfExecutions = count.get(uuid);
			numberOfExecutions++;
			count.put(uuid,numberOfExecutions);		
		}
		
		private void incrementError(String uuid){
			if (errorCount.get(uuid) == null){
				errorCount.put(uuid,0);
			}
			int numberOfExecutions = errorCount.get(uuid);
			numberOfExecutions++;
			errorCount.put(uuid,numberOfExecutions);		
			
		}
		
		private int getNumberExecutions(String uuid){
			if (count.get(uuid) == null){
				count.put(uuid,0);
			}
			int numberOfExecutions = count.get(uuid);
			return numberOfExecutions;
		}
		
		private int getNumberOfErrors(String uuid){
			if (errorCount.get(uuid) == null){
				errorCount.put(uuid,0);
			}
			int numberOfExecutions = errorCount.get(uuid);
			return numberOfExecutions;
		}
		
		private void getAndMergeRunProperties() throws Exception {
			for (JSystemAgentClient client:getClients()){
				Properties p = client.getRunProperties();
				Enumeration<Object> iter = p.keys();
				while (iter.hasMoreElements()){
					Object key = iter.nextElement();
					String newKey = client.getId()+"_" + key.toString();
					RunProperties.getInstance().setRunProperty(newKey, p.get(key).toString());
				}
			}
		}

		@Override
		public void endContainer(JTestContainer container) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void endLoop(AntForLoop loop, int count) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void startContainer(JTestContainer container) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void startLoop(AntForLoop loop, int count) {
			// TODO Auto-generated method stub
			
		}

	}
	

}
