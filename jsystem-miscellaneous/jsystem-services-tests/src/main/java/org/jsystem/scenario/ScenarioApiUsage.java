/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package org.jsystem.scenario;

import java.io.File;

import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.scenario.Parameter.ParameterType;
import jsystem.framework.scenario.flow_control.AntForLoop;
import junit.framework.SystemTestCase4;

import org.junit.Assert;
import org.junit.Test;

/**
 * Exemplifies how to create JSystem scenario programmatically  
 * @author gderazon
 */
public class ScenarioApiUsage extends SystemTestCase4 {

	@Test
	public void createSimpleScenario() throws Exception {
		//
		// Deleting scenarios if they already exist.
		//
		File f = new File(ScenariosManager.getInstance().getScenariosDirectoryFiles(),"scenarios/ApiScenario.xml");
		f.delete();
		f = new File(ScenariosManager.getInstance().getScenariosDirectoryFiles(),"scenarios/ApiScenarioParent.xml");
		f.delete();
		
		//Creating test elements
		RunnerTest t1 = new RunnerTest("com.aqua.services.multiuser.TestParametersExample","testPing");
		RunnerTest t2 = new RunnerTest("com.aqua.services.junit4.JUnit4Example","myTestMethod");
		
		//Creating scenario
		Scenario s = new Scenario(ScenariosManager.getInstance().getScenariosDirectoryFiles(),"scenarios/ApiScenario");
		
		//adding tests to scenario
		s.addTest(t1);
		s.addTest(t2);
		
		//saving scenario
		s.save();
		
		//creating parent scenario
		RunnerTest s2t1 = t1.cloneTest();
		Scenario s2 = new Scenario(ScenariosManager.getInstance().getScenariosDirectoryFiles(),"scenarios/ApiScenarioParent");
		s2.addTest(s2t1);
		s2.addTest(s);
		s2.save();
	}
	
	/**
	 * Creating simple scenario and loading it.
	 * @throws Exception
	 */
	@Test
	public void loadScenario() throws Exception {
		createSimpleScenario();
		Scenario s = new Scenario(ScenariosManager.getInstance().getScenariosDirectoryFiles(),"scenarios/ApiScenario");
		RunnerTest test = s.getTest(0);
		Assert.assertEquals("com.aqua.services.multiuser.TestParametersExample", test.getClassName());		
	}
	
	/**
	 * Creating scenario with flow control.
	 * @throws Exception
	 */
	@Test
	public void createScenarioWithFlowControl() throws Exception {
		File f = new File(ScenariosManager.getInstance().getScenariosDirectoryFiles(),"scenarios/ApiScenarioWithFor.xml");
		f.delete();
		
		Scenario s = new Scenario(ScenariosManager.getInstance().getScenariosDirectoryFiles(),"scenarios/ApiScenarioWithFor");
		AntForLoop loop = new AntForLoop();
		loop.setLoopValuesList("1;2;3;4");
		RunnerTest t1 = new RunnerTest("com.aqua.services.multiuser.TestParametersExample","testPing");
		RunnerTest t2 = new RunnerTest("com.aqua.services.junit4.JUnit4Example","myTestMethod");
		loop.addTest(t1);
		loop.addTest(t2);
		s.addTest(loop);
		s.save();
	}
	
	@Test
	public void createScenarioWithTestAndParameters() throws Exception {
		//
		// Deleting scenario if it already exists.
		//
		File f = new File(ScenariosManager.getInstance().getScenariosDirectoryFiles(),"scenarios/ApiScenarioWithParam.xml");
		f.delete();
		
		//Note: it is important to create scenario and add test to it before setting test parameters
		Scenario s = new Scenario(ScenariosManager.getInstance().getScenariosDirectoryFiles(),"scenarios/ApiScenarioWithParam");
		RunnerTest t1 = new RunnerTest("com.aqua.services.multiuser.TestParametersExample","testPing");
		s.addTest(t1);		

		//those two operation are important to initialize test
		t1.load();
		t1.loadParametersAndValues();

		//defining parameters
		Parameter p = new Parameter();
		p.setName("PingDestination");
		p.setType(ParameterType.STRING);
		p.setValue("127.0.0.1");

		Parameter p1 = new Parameter();
		p1.setName("PacketSize");
		p1.setType(ParameterType.INT);
		p1.setValue(350);
		
		//setting test parameters
		t1.setParameters(new Parameter[]{p,p1});
		
		//saving scenario
		s.save();
	}
}
