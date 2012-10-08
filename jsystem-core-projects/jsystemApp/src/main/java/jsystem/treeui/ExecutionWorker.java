/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.net.SocketException;
import java.rmi.UnmarshalException;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;

import jsystem.framework.report.ExecutionListener;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.actionItems.SaveScenarioAction;
import jsystem.treeui.client.RunnerEngineManager;
import jsystem.treeui.error.ErrorPanel;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
/**
 * 
 * @author goland
 */
public class ExecutionWorker extends SwingWorker<Integer, Integer> implements ExecutionListener {
	private static int RUN_END = 1;
	private static int EXECUTION_ENDED = 2;
	private static int REMOTE_PAUSE= 3;
	private TestTreeView treeView;

	public ExecutionWorker(TestTreeView treeView) {
		this.treeView = treeView;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		Throwable operException = null;
		try {
			if (ScenariosManager.isDirty() && ScenariosManager.getInstance().getCurrentScenario().canWrite()){
				SaveScenarioAction.getInstance().saveCurrentScenario();
			}			
			RunnerEngineManager.getRunnerEngine().addListener(this);
			RunnerEngineManager.getRunnerEngine().enableRepeat(treeView.isRepeat());
			RunnerEngineManager.getRunnerEngine().setRepeat(treeView.getNumberOfCycles());
			RunnerEngineManager.getRunnerEngine().run();
		}catch (Throwable e){
			operException = e;
		}finally{
			treeView.setRepeat(false);
			analyzeError(operException);
		}
		return Integer.valueOf(0);
	}

	/**
	 */
	private void analyzeError(Throwable t){
		if (t== null){
			return;
		}
		if (t instanceof UnmarshalException && t.getCause() instanceof SocketException){
			ErrorPanel.showErrorDialog("Connection to the agent was lost", t, ErrorLevel.Warning);
		}else {
			ErrorPanel.showErrorDialog("Failed executing scenario", t, ErrorLevel.Error);
			treeView.configureView(TestTreeView.VIEW_IDLE);
		}								
	}

	@Override
	protected void process(List<Integer> chunks) {
		if (chunks.size() == 0){
			return;
		}
		Iterator<Integer> iter = chunks.iterator();
		while (iter.hasNext()){
			int event = iter.next();
			if (event == RUN_END){
				int leftRuns = treeView.getNumberOfLeftCycles();
				if (leftRuns >= 1) {
					leftRuns--;
					treeView.setNumberOfLeftCycles(leftRuns);
				}			
			}else
			if (event == EXECUTION_ENDED){
				treeView.configureView(TestTreeView.VIEW_IDLE);
			}else
			if (event == REMOTE_PAUSE){
				treeView.configureView(TestTreeView.VIEW_PAUSED);
			}
		}
	}

	public void executionEnded(String scenarioName) {
		publish(new Integer[]{EXECUTION_ENDED});
		RunnerEngineManager.getRunnerEngine().removeListener(this);
	}

	public void remoteExit() {
	}

	public void endRun() {
		publish(new Integer[]{RUN_END});
		WaitDialog.endWaitDialog();
	}

	public void addWarning(Test test) {
		// TODO Auto-generated method stub

	}

	public void startTest(TestInfo testInfo) {
		// TODO Auto-generated method stub

	}

	public void addError(Test arg0, Throwable arg1) {
		// TODO Auto-generated method stub

	}

	public void addFailure(Test arg0, AssertionFailedError arg1) {
		// TODO Auto-generated method stub

	}

	public void endTest(Test arg0) {
		// TODO Auto-generated method stub

	}

	public void startTest(Test arg0) {
		// TODO Auto-generated method stub

	}
	
	public void errorOccured(String title, String message, ErrorLevel level) {
		ErrorPanel.showErrorDialog(title,message,level);
	}

	public void remotePause() {
		publish(new Integer[]{REMOTE_PAUSE});

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
