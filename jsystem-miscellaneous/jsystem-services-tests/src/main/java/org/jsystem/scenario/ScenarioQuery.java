/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package org.jsystem.scenario;

import java.util.HashMap;

import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainerVisitor;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ParameterUtils;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenariosManager;
import junit.framework.SystemTestCase4;

import org.junit.Test;

/**
 * Exemplifies how to make queries on a scenario when 
 * test in instantiated in the context of the runner.
 * @author gderazon
 */
public class ScenarioQuery extends SystemTestCase4 {
	
	
	
	@Test
	public void getTestParameters() throws Exception {
	
	}
	
	public void handleUIEvent(HashMap<String, Parameter> map, String methodName) throws Exception {
		Scenario currentScenario = ScenariosManager.getInstance().getCurrentScenario();
		report.report("Current scenario is: " + currentScenario.getName() );
		//see below other ways to find a test
		RunnerTest test = currentScenario.getTest(0);
		Parameter params[] = test.getVisibleParamters();
		report.report("Test name: " +test.getTestName() );
		report.report("Number of params: " +params.length);
		report.report("value of param 0: " + params[0].getValue());
		
		//
		// If you want to make changes to the parameters in the handleUIEvent do the 
		// following:
		//
		params = ParameterUtils.clone(params);
		params[0].setValue("Handle UI Event Value");
		test.setParameters(params);
		ScenariosManager.getInstance().getCurrentScenario().save();
	}
	
	private RunnerTest findTestByUUID(String uuid) throws Exception{
		return (RunnerTest)ScenarioHelpers.getTestById(ScenariosManager.getInstance().getCurrentScenario(), "full uuid here");
	}

	private RunnerTest findTestByClassMethodName(String className,String methodName) throws Exception {
		class TestFinder implements JTestContainerVisitor{
			private RunnerTest foundTest;
			private String className;
			private String methodName;
			TestFinder(String className,String methodName){
				this.className = className;
				this.methodName = methodName;
			}
			@Override
			public void visitScenarioElement(JTest t1) throws Exception {
				if (!(t1 instanceof RunnerTest) ){
					return;
				}
				if (((RunnerTest)t1).getClassName().equals(className) && 
					((RunnerTest)t1).getMethodName().equals(methodName)){
					foundTest = (RunnerTest)t1;
				}
			};		
		}

		TestFinder tf = new TestFinder(className,methodName);
		ScenarioHelpers.iterateContainer(ScenariosManager.getInstance().getCurrentScenario(),tf);
		return tf.foundTest;
	}
	
	
}
