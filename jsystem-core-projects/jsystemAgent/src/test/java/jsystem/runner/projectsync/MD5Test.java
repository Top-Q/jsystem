/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.projectsync;

import java.io.File;
import java.util.Map;

import jsystem.runner.agent.ProjectComponent;
import jsystem.runner.projectsync.MD5Calculator;
import junit.framework.SystemTestCase;

/**
 * Unit test for MD5 calculation.
 * project1 is identical to project2
 * project3 is different from them in the following files:
 * 1. classes/scenarios changed level0.xml scenario
 * 2. in classes, deleted the class BaseClassToInherit.class
 * 
 * @author goland
 *
 */
public class MD5Test extends SystemTestCase {
	
	private MD5Calculator project1Calculator;
	private MD5Calculator project2Calculator;
	private MD5Calculator project3Calculator;
	
	private File resourcesRoot = new File("resources");
	private File project1TestClassesFolder = new File(resourcesRoot,"project1/classes");
	private File project2TestClassesFolder = new File(resourcesRoot,"project2/classes");
	private File project3TestClassesFolder = new File(resourcesRoot,"project3/classes");
	
	public void setUp() throws Exception {
		project1Calculator = new MD5Calculator(project1TestClassesFolder);
		project2Calculator = new MD5Calculator(project2TestClassesFolder);
		project3Calculator = new MD5Calculator(project3TestClassesFolder);

	}
	public void testCalculateClassesMD5() throws Exception {
		String classesMd5 = project1Calculator.calculateClassesMD5();
		assertEquals("-1cbfd212bde305c38dabdd2c57d4aa9e",classesMd5);
	}

	public void testCalculateScenariosMD5() throws Exception  {
		String scenariosMd5 = project1Calculator.calculateScenariosMD5();
		assertEquals("-39969372c9a248f6d8b4f91c0c646711",scenariosMd5);
	}

	public void testCalculateSUTMD5() throws Exception  {
		String sutMd5 = project1Calculator.calculateSUTMD5();
		assertEquals("-363589fb3ee26975efaf0195b978c323",sutMd5);
	}

	public void testCalculateLibsMD5() throws Exception  {
		String libMd5 = project1Calculator.calculateLibsMD5();
		assertEquals("-3249ca61ca0d092175267e2141a3b8cd",libMd5);
	}

	public void testValidateIdenticalProjects() throws Exception  {
		String project1ClassesMd5 = project1Calculator.calculateClassesMD5();
		String project1ScenariosMd5 = project1Calculator.calculateScenariosMD5();
		String project1SutMd5 = project1Calculator.calculateSUTMD5();
		String project1LibMd5 = project1Calculator.calculateLibsMD5();
		
		String project2ClassesMd5 = project2Calculator.calculateClassesMD5();
		String project2ScenariosMd5 = project2Calculator.calculateScenariosMD5();
		String project2SutMd5 = project2Calculator.calculateSUTMD5();
		String project2LibMd5 = project2Calculator.calculateLibsMD5();
		
		assertEquals(project1ClassesMd5, project2ClassesMd5);
		assertEquals(project1ScenariosMd5, project2ScenariosMd5);
		assertEquals(project1SutMd5, project2SutMd5);
		assertEquals(project1LibMd5, project2LibMd5);
	}

	public void testValidateDifferentProjects() throws Exception  {
		String project3ClassesMd5 = project3Calculator.calculateClassesMD5();
		String project3ScenariosMd5 = project3Calculator.calculateScenariosMD5();
		String project3SutMd5 = project3Calculator.calculateSUTMD5();
		String project3LibMd5 = project3Calculator.calculateLibsMD5();
		
		String project2ClassesMd5 = project2Calculator.calculateClassesMD5();
		String project2ScenariosMd5 = project2Calculator.calculateScenariosMD5();
		String project2SutMd5 = project2Calculator.calculateSUTMD5();
		String project2LibMd5 = project2Calculator.calculateLibsMD5();
		
		assertFalse(project3ClassesMd5.equals(project2ClassesMd5));
		assertFalse(project3ScenariosMd5.equals(project2ScenariosMd5));
		assertEquals(project3SutMd5, project2SutMd5);
		assertEquals(project3LibMd5, project2LibMd5);
	}
	
	public void testIdenticalProjectsDiff() throws Exception  {
		Map<ProjectComponent,String> map1 = project1Calculator.getProjectMD5(ProjectComponent.values());
		Map<ProjectComponent,String> map2 = project2Calculator.getProjectMD5(ProjectComponent.values());
		ProjectComponent[] diff = MD5Calculator.diffProjectsMd5(map1,map2);
		assertTrue(diff.length == 0);
	}

	public void testDifferentProjectsDiff() throws Exception  {
		Map<ProjectComponent,String> map1 = project1Calculator.getProjectMD5(ProjectComponent.values());
		Map<ProjectComponent,String> map3 = project3Calculator.getProjectMD5(ProjectComponent.values());
		ProjectComponent[] diff = MD5Calculator.diffProjectsMd5(map1,map3);
		assertTrue(diff.length == 2);
	}

	public File getResourcesRoot() {
		return resourcesRoot;
	}
	public void setResourcesRoot(File resourcesRoot) {
		this.resourcesRoot = resourcesRoot;
	}

}
