/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.clients;

import java.net.SocketException;
import java.rmi.UnmarshalException;
import java.util.Vector;
import java.util.logging.Logger;

import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ExecutionListener;
import jsystem.framework.report.InteractiveReporter;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.runner.ErrorLevel;
import jsystem.runner.agent.server.RunnerEngineExecutionState;
import jsystem.utils.BackgroundRunnable;
import jsystem.utils.ParallelExecutionUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

public class DefaultDistributedExecutor extends BaseJSystemDistributedExecutor {
	private static Logger log = Logger.getLogger(DefaultDistributedExecutor.class.getName());
	private Vector<JSystemAgentClient> agentsList;
	
	@Override
	public void execute() throws Exception  {	
		agentsList = new Vector<JSystemAgentClient>();
		final ExecutionListener listener = getDistributedExecutionListener();
		BackgroundRunnable[] runnables = new BackgroundRunnable[getUrls().length];
		for (int i = 0; i < getUrls().length;i++) {
			AgentRunnable runnable = new AgentRunnable(getUrls()[i],listener);
			runnables[i] = runnable;
		}
		ParallelExecutionUtils.run(runnables);
	}

	protected ExecutionListener getDistributedExecutionListener() {
		return new DistributedExecutionListener(); 
	}

	public class AgentRunnable extends BackgroundRunnable {
		private String url;
		private ExecutionListener listener;
		
		
		public AgentRunnable(String url,ExecutionListener listener){
			this.url = url;
			this.listener = listener;
		}
		@Override
		public void internalRun() throws Exception {
			JSystemAgentClient client = new JSystemAgentClient(url);
			agentsList.add(client);
			InternalListener internalListener = new InternalListener();
			client.init();
			client.addListener(listener);
			client.addListener(internalListener);
			try {
				client.run(getRootScenario().getName(),getTestToExecute().getFullUUID());
			}catch (Exception e){
				if (e instanceof UnmarshalException && e.getCause() instanceof SocketException){
					client.waitForExecutionState(-1,RunnerEngineExecutionState.idle);
				}else {
					throw e;
				}
			}finally{
				internalListener.waitForEndEvent(JSystemProperties.getInstance().getLongPreference("agen.client.wait_for_events",30000));
				long waitTimeForNotification = JSystemProperties.getInstance().getLongPreference("agen.client.wait_time_for_notifications",5000);
				Thread.sleep(waitTimeForNotification);
				log.fine("Finished waiting for run end");
				client.removeListener(internalListener);
				client.removeListener(listener);
				client = null;
			}
		}
		
		@Override
		public synchronized void setThrowable(Throwable t) {
			super.setThrowable(t);
			ListenerstManager.getInstance().report("Failed executing scenario on " + url + ". " + t.getMessage(), t);
		}
	}

	public class InternalListener implements ExecutionListener{
		private volatile boolean executionEnded = false;

		public void waitForEndEvent(long millisec){
			long startTime = System.currentTimeMillis();
			long endTime = System.currentTimeMillis();
			while (!executionEnded && (endTime - startTime < millisec)){
				try {Thread.sleep(100);}catch(Exception e){}
				endTime = System.currentTimeMillis();
			}
		}
		@Override
		public void errorOccured(String title, String message, ErrorLevel level) {
			executionEnded = true;
			
		}

		@Override
		public void executionEnded(String scenarioName) {
			log.fine("Execution ended " + scenarioName);
			executionEnded = true;
			
		}

		@Override
		public void remoteExit() {
			executionEnded = true;
			
		}

		@Override
		public void remotePause() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addWarning(Test test) {
			// TODO Auto-generated method stub
			
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
		public void endRun() {
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

		@Override
		public void startTest(TestInfo testInfo) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addError(Test arg0, Throwable arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addFailure(Test arg0, AssertionFailedError arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void endTest(Test arg0) {
		}

		@Override
		public void startTest(Test arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public class DistributedExecutionListener implements ExecutionListener,InteractiveReporter{
		
		@Override
		public void errorOccured(String title, String message, ErrorLevel level) {
			
		}

		@Override
		public void executionEnded(String scenarioName) {
			
		}

		@Override
		public void remoteExit() {
		
		}

		@Override
		public void remotePause() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addWarning(Test test) {
			ListenerstManager.getInstance().addWarning(test);
		}

		@Override
		public void endRun() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void startTest(TestInfo testInfo) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addError(Test arg0, Throwable arg1) {
			ListenerstManager.getInstance().addError(arg0, arg1);
		}

		@Override
		public void addFailure(Test arg0, AssertionFailedError arg1) {
			ListenerstManager.getInstance().addFailure(arg0, arg1);
			
		}

		@Override
		public void endTest(Test arg0) {
			ListenerstManager.getInstance().endTest(arg0);
			
		}

		@Override
		public void startTest(Test arg0) {
			ListenerstManager.getInstance().startTest(arg0);
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

		@Override
		public int showConfirmDialog(String title, String message,
				int optionType, int messageType) {
			return ListenerstManager.getInstance().showConfirmDialog(title, message, optionType, messageType);
		}

	}
	
	protected JSystemAgentClient[] getClients(){
		return agentsList.toArray(new JSystemAgentClient[0]);
	}
}
