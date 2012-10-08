package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.KeyStroke;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.GeneralEnums.RunMode;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.scenario.DistributedExecutionHelper;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.scenario.ValidationError;
import jsystem.framework.sut.SutFactory;
import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.runner.agent.clients.JSystemAgentClient;
import jsystem.runner.agent.server.RunnerEngine;
import jsystem.treeui.TestRunner;
import jsystem.treeui.agents.DistributedExecutionConfirmation;
import jsystem.treeui.client.JSystemAgentClientsPool;
import jsystem.treeui.client.RemoteAgentClient;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.publisher.PublisherTreePanel;
import jsystem.utils.ProgressNotifier;
import jsystem.utils.StringUtils;


public class ToggleDebugOptionAction extends IgnisAction {


	private static final long serialVersionUID = 1L;
	
	private static ToggleDebugOptionAction action;
	
	private ToggleDebugOptionAction(){
		super();
		putValue(Action.NAME, "Toggle Debug Option");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getToggleDebugOptionButton());
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "toggle-debug-option");
	}
	
	public static ToggleDebugOptionAction getInstance(){
		if (action == null){
			action =  new ToggleDebugOptionAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String debug = "-classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=${8787},server=y,suspend=y";
		
   		String vmParams = JSystemProperties.getInstance().getPreference(FrameworkOptions.TEST_VM_PARMS);
        if(vmParams==null || vmParams.length()<3){
        	vmParams = new String(debug);
        }
		
        JSystemProperties.getInstance().setPreference(FrameworkOptions.TEST_VM_PARMS, debug);
        
        
        //Call Run - PlayAction
        
        RunPlayButton();
        
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        JSystemProperties.getInstance().setPreference(FrameworkOptions.TEST_VM_PARMS, " ");
		
	}

	private void RunPlayButton() {
		boolean wasDirty = ScenariosManager.isDirty();
		try {
			SaveScenarioAction.getInstance().saveCurrentScenarioWithConfirmation();
		} catch (Exception e1) {
			ErrorPanel.showErrorDialog("Problem saving scenario before execution",StringUtils.getStackTrace(e1),ErrorLevel.Error);
			if (wasDirty){
				ScenariosManager.setDirty();
			}
			return;
		}
		
		/**
		 * if there are flow elements in a scenario, they can only run on Run Mode 1
		 */
		Scenario scenario = ScenariosManager.getInstance().getCurrentScenario();
		ArrayList<ValidationError> errors = new ArrayList<ValidationError>();
		scenario.collectValidationErrors(errors);
		if(!errors.isEmpty()){
			if(ErrorPanel.showErrorDialogOkCancel(
					"Scenario validation errors found:\n" + 
					ValidationError.collectErrorsDescriptions(errors, false) +"\n" +
					"Continue?", 
					ValidationError.collectErrorsDescriptions(errors, true), ErrorLevel.Warning)){
				return;
			}
		}
		String runmode = JSystemProperties.getInstance().getPreference(FrameworkOptions.RUN_MODE);
		if (runmode == null){ //  not configured yet
			runmode = RunMode.DROP_EVERY_RUN.toString();
		}
		RunMode mode = RunMode.getMatchingEnum(runmode);
		boolean wrongRunMode = !RunMode.DROP_EVERY_RUN.equals(mode);
		if (wrongRunMode && scenario.containsMappedFlowControlElements()){
			ErrorPanel.showErrorDialog("Flow control elements can only run on run mode:\n"+RunMode.DROP_EVERY_RUN+"\ncurrently configured to "+mode+"","Possible solutions:\n1) Unmap flow elements\n2) Change run mode in through JSystem properties panel",ErrorLevel.Error);
			
			return;
		}
		TestRunner.treeView.getTabbes().setSelectedIndex(1);
		run();

		 // After The run ended, enable publish button.
		PublisherTreePanel.setPublishBtnEnable(true);
		
	}
	
	public void run() {
		String agentAutoSync = JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.AGENT_AUTO_SYNC);
		if (TestRunner.treeView.isPaused()) {
			TestRunner.treeView.setPaused(false);
			TestRunner.treeView.getRunner().handleEvent(TestRunner.CONTINUE_EVENT, null);
		} else {
			TestRunner.treeView.setStopped(false);
			try {
				String[] hosts = DistributedExecutionHelper.getParticipatingHosts();
				if (hosts.length > 0){
					RunnerEngine[] engines = JSystemAgentClientsPool.getClients(hosts);
					if (engines != null && engines.length >0){
						if ("false".equalsIgnoreCase(agentAutoSync)){
							boolean run = DistributedExecutionConfirmation.showConfirmationDialog(engines);
							if (!run){
								return;
							}
							//updates selected tests
							int[] indices = ScenariosManager.getInstance().getCurrentScenario().getEnabledTestsIndexes();
							String sutName = SutFactory.getInstance().getSutFile().getName();
							for (RunnerEngine engine:engines){
								if (engine == null){
									continue;
								}
								if (engine.getConnectionState().equals(RunnerEngine.ConnectionState.connected)){
									((JSystemAgentClient)engine).synchronizeProject(null,null,null, sutName, indices,JSystemProperties.getInstance().getPreferences());
									ListenerstManager.getInstance().report("Updated " +engine.getId());
								}
							}
						}
						else{
							RemoteAgentClient.syncAgentsWithLocalProject((JSystemAgentClient[]) engines, true, new SyncNotifier(), false);
						}
					}
				}
			}catch (Exception e){
				ErrorPanel.showErrorDialog("Failed to show the confirmation dialog", e, ErrorLevel.Error);
			}
			TestRunner.treeView.getRunner().handleEvent(TestRunner.RUN_EVENT, null);
		}
	}
	
	static class SyncNotifier implements ProgressNotifier{
		@Override
		public void done() {
		}
		@Override
		public void notifyProgress(String message, int progress) {
			ListenerstManager.getInstance().report(message);
		}
	}

}
