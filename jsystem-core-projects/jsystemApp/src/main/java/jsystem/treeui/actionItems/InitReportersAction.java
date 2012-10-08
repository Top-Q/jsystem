/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.runner.agent.server.RunnerEngine;
import jsystem.treeui.TestRunner;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.client.JSystemAgentClientsPool;
import jsystem.treeui.client.RunnerEngineManager;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.publisher.PublisherTreePanel;

/**
 * Init reporters action implementation. Sends init reporter event to all agents. 
 *
 */
public class InitReportersAction extends IgnisAction {
	
	private volatile boolean initReportersEnded = true;
	
	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(InitReportersAction.class.getName());
	
	private static InitReportersAction action;
	
	public InitReportersAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getInitReportsMenuItem());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getInitReportsMenuItem());
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "init-reporters");
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_INIT_REPORTS));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_INIT_REPORTS));
	}
	
	public static InitReportersAction getInstance(){
		if (action == null){
			action =  new InitReportersAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//in case the runner is connected to remote agents, giving the user notification message.
		if (JSystemAgentClientsPool.getClients(null).length > 0){
			try {
				RunnerEngine[] engines = JSystemAgentClientsPool.getClients(null);
				if (engines != null && engines.length >0){
					int res  = JOptionPane.showOptionDialog(null,
							"This action will initialize reporters on all agents.", "Initialize reporters on distributed environment",
							JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE, ImageCenter.getInstance().getImage(ImageCenter.ICON_INFO), new String[]{"Yes","No"}, "Yes");
					if (res != 0){
						return;
					}
				}
			}catch (Exception e1){
				ErrorPanel.showErrorDialog("Failed showing confirmation dialog", e1, ErrorLevel.Error);
			}
		}
		
//		InitReportersAction.getInstance().setEnabled(false);
		WaitDialog.launchWaitDialog(JsystemMapping.getInstance().getInitReportDialogTitle(), null);
		Executors.newCachedThreadPool().execute(new InitReporters());
	}
	
	private void initReporters() {
		RunnerEngineManager.getRunnerEngine().initReporters();
    	RunnerEngine[] engines = JSystemAgentClientsPool.getClients(null);
		if (engines != null && engines.length >0){
			for (RunnerEngine engine : engines) {
				if (engine == null){
					continue;
				}
				try {
					engine.initReporters();
				}catch (Exception e){
					log.warning("Failed initializing reports on " + engine.getId());
				}
			}
		}		
	}
	
	/**
	 * will run initReporters function and then run the done function after finishing the initReporters.
	 * @author aqua
	 *
	 */
	class InitReporters extends SwingWorker<String, Object> {
		//the default state of initReportersEnded is true to make sure that system doesn't get stuck
		//on a call to waitForInitReportersToEnd, if it's made without calling the initReporters action itself.
		//in that case, the value of initReportersEnded == true and wait will stay for ever in the loop.
		public InitReporters() {
			initReportersEnded = false;
		}
        public String doInBackground() {
        	initReporters();
        	return "";
        } 
        protected void done() {
        	//We removed the publisher panel
//        	TestRunner.treeView.getPublishPanel().refreshAndSelect(true);
			 // After Init Reporters pushed, disable Publish button.
			PublisherTreePanel.setPublishBtnEnable(false);
			log.fine("InitReportersAction - closing waitDialog");
			WaitDialog.endWaitDialog();
			
			//setting back value to true, here to prevent a calling thread to wait, to be stuck because
			//initReporters ended before it (the waitForInit... func) got executed.
			synchronized(this){
				initReportersEnded = true;
				notifyAll();//wake waiting threads for initReporters to end
			}
        }
    }
	
	public synchronized void waitForInitReportersToEnd() throws InterruptedException{
		//waiting for initReporters to end.
		while(initReportersEnded == false){
			wait();
		}
		return;
	}
}

