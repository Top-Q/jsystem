/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.undoredo;

import jsystem.framework.scenario.DistributedExecutionParameter;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.MultipleScenarioOps;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ParameterUtils;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenarioParameter;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.treeui.TestRunner;

public class ParameterChangeAction extends BaseUserAction {
	private Parameter[] before;
	private Parameter[] after;
	private String testUUID;
	private boolean recursive = false;
	
	public ParameterChangeAction(String uuid,Parameter[] before, Parameter[] after){
		this.before = ParameterUtils.clone(before);
		this.after = ParameterUtils.clone(after);
		ParameterUtils.setDirty(this.before, true);
		ParameterUtils.setDirty(this.after, true);
		this.testUUID = uuid;
	}

	public ParameterChangeAction(String uuid,Parameter[] before, Parameter[] after,boolean recursive){
		this(uuid,before,after);
		this.recursive = recursive;
	}

	@Override
	public boolean redo() throws Exception {
		JTest test = ScenarioHelpers.getTestById(ScenariosManager.getInstance().getCurrentScenario(),testUUID);
		//check whether this is an flowcontrol element
		if (test == null){
			test = ScenarioHelpers.getFlowElementById(ScenariosManager.getInstance().getCurrentScenario(),testUUID);
			if (test == null){
				throw new IllegalStateException("Test/flow control element was not found");
			}
			test.setParameters(after);
		}
		if (after instanceof DistributedExecutionParameter[]){
			test.setDistributedExecutionParameters((DistributedExecutionParameter[])after);
		}else
		if (after instanceof ScenarioParameter[]){
			((Scenario)test).setScenarioParameters(after,recursive);
		}else {
			MultipleScenarioOps.updateTest(test, after, false);
		}
			
		TestRunner.treeView.tableController.selectTest(test);		
		return true;
	}

	@Override
	public boolean undo() throws Exception {
		JTest test = ScenarioHelpers.getTestById(ScenariosManager.getInstance().getCurrentScenario(),testUUID);
		//check whether this is an flowcontrol element
		if (test == null){
			test = ScenarioHelpers.getFlowElementById(ScenariosManager.getInstance().getCurrentScenario(),testUUID);
			if (test == null){
				throw new IllegalStateException("Test/flow control element was not found");
			}
			test.setParameters(before);
		}

		if (before instanceof DistributedExecutionParameter[]){
			test.setDistributedExecutionParameters((DistributedExecutionParameter[])before);
		}else
		if (before instanceof ScenarioParameter[]){
			((Scenario)test).setScenarioParameters(before,recursive);
		}else {
			MultipleScenarioOps.updateTest(test, before, false);
		}
			
		TestRunner.treeView.tableController.selectTest(test);		
		return true;
	}

}
