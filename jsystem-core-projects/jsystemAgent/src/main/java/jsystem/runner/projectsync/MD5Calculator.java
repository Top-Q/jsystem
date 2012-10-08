/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.projectsync;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.runner.agent.ProjectComponent;
import jsystem.utils.FileUtils;

/**
 * Service class which performs automation project MD5 calculations.<br>
 * For more information please reas {@link }
 * @author goland
 */
public class MD5Calculator {

	private File projectClassesPath;
	
	/**
	 * Constructs a {@link MD5Calculator}
	 * @param testsClassesPath full path to tests classes folder.
	 */
	public MD5Calculator(File testsClassesPath) throws Exception{
		this.projectClassesPath = testsClassesPath;
	}
	
	/**
	 * Calculates classes md5.<br>
	 * the md5 of all packages under tests classes folder is calculates
	 * besides sut package folder and scenarios package folder.
	 */
	public String calculateClassesMD5() throws Exception {
		File[] subFolders = projectClassesPath.listFiles();
		MessageDigest md = MessageDigest.getInstance("MD5");
		for (File file:subFolders){
			if (file.getName().equals("sut")){
				continue;
			}
			if (file.getName().equals("scenarios")){
				continue;
			}
			FileUtils.updateMessageDigest(file, md);
		}
		byte[] hash = md.digest();
		BigInteger result = new BigInteger(hash);
		String rc = result.toString(16);
		return rc;
	}
	
	/**
	 * Calculates automation project scenarios package folder md5.
	 */	
	public String calculateScenariosMD5() throws Exception {
		File scenariosPath = new File(projectClassesPath,"scenarios");
		if (!scenariosPath.exists()){
			return "";
		}
		return FileUtils.getMD5(scenariosPath);
	}
	
	/**
	 * Calculates automation project sut package folder md5.
	 */	
	public String calculateSUTMD5() throws Exception {
		File sutPath = new File(projectClassesPath,"sut");
		if (!sutPath.exists()){
			return "";
		}
		return FileUtils.getMD5(sutPath);
	}
	
	/**
	 * Calculates automation project lib folder md5.
	 */	
	public String calculateLibsMD5() throws Exception {
		File libsPath = new File(projectClassesPath.getParent(),"lib");
		if (!libsPath.exists()){
			return "";
		}
		return FileUtils.getMD5(libsPath);
	}

	/**
	 * Calculates automation project resources folder md5.
	 */	
	public String calculateResourcesMD5() throws Exception {
		File resourcesPath = new File(projectClassesPath.getParent(),"resources");
		if (!resourcesPath.exists()){
			return "";
		}
		return FileUtils.getMD5(resourcesPath);
	}
	
	/**
	 * Returns a map with project components md5.<br>
	 * @param components - automation project components for which md5 is calculate.  
	 */
	public Map<ProjectComponent, String> getProjectMD5(ProjectComponent[] components) throws Exception {
		HashMap<ProjectComponent,String> map = new HashMap<ProjectComponent, String>();

		for (ProjectComponent component:components){
			String value = "";
			if (component.equals(ProjectComponent.classes)){
				value = calculateClassesMD5();
			}else
			if (component.equals(ProjectComponent.libs)){
				value = calculateLibsMD5();
			}else
			if (component.equals(ProjectComponent.suts)){
				value = calculateSUTMD5();
			}else
			if (component.equals(ProjectComponent.scenarios)){
				value = calculateScenariosMD5();
			}else
			if (component.equals(ProjectComponent.resources)){
				value = calculateResourcesMD5();
			}
			map.put(component,value); 
		}
		return map;
	
	}

	/**
	 * Returns whole project digest with special treatment to current scenario.<br>
	 * @see #calculateScenarioMD5
	 */
	public String calculateProjectMD5(String scenarioName) throws Exception {
		Scenario s = ScenariosManager.getInstance().getScenario(scenarioName);		
		s.loadParametersAndValues();
		String scenarioMD5 = calculateScenarioMD5(s);
		Map<ProjectComponent,String> map = 
			getProjectMD5(new ProjectComponent[]{ProjectComponent.resources,ProjectComponent.classes,ProjectComponent.suts,ProjectComponent.libs});
		map.put(ProjectComponent.currentScenario,scenarioMD5);
		MessageDigest md = MessageDigest.getInstance("MD5");
		Iterator<String> iter = map.values().iterator();
		while (iter.hasNext()){
			md.update(iter.next().getBytes());
		}
		byte[] hash = md.digest();
		BigInteger result = new BigInteger(hash);
		String rc = result.toString(16);
		return rc;
	}

	/**
	 * Calculates scenario MD5.<br>
	 * The method was added to support special treatment to current scenario:
	 * When calculating project MD5, I want to ignore tests selections.<br>
	 * Regular file MD5 calculation will calculate also selection, so to workaround
	 * it, I load the scenario and calculate the MD5 of each test, and ignore selection.
	 * 
	 * TODO method is not operational yet.
	 */
	private static String calculateScenarioMD5(Scenario scenario) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		Vector<JTest> allTests = scenario.getTests();
		for (JTest t:allTests){
			String name = t.getTestName()+t.getTestId();
			String parameters = "";
			if (t instanceof RunnerTest){
				parameters = ((RunnerTest)t).getPropertiesAsString();
			}
			md.update(name.getBytes());
			md.update(parameters.getBytes());
		}
		byte[] hash = md.digest();
		BigInteger result = new BigInteger(hash);
		String rc = result.toString(16);
		return rc;
	}
	
	/**
	 * Compares the md5 of two projects and returns and array of the components
	 * which are different between two projects.
	 */	
	public static ProjectComponent[] diffProjectsMd5(Map<ProjectComponent,String> project1,Map<ProjectComponent,String> project2){
		HashSet<ProjectComponent> set = new HashSet<ProjectComponent>(); 
		Iterator<ProjectComponent> proj1Iter = project1.keySet().iterator();
		while (proj1Iter.hasNext()){
			ProjectComponent componentString = proj1Iter.next();
			String proj1Val = project1.get(componentString);
			String proj2Val = project2.get(componentString);
			if (!proj1Val.equals(proj2Val)){
				set.add(componentString);
			}
		}

		Iterator<ProjectComponent> proj2Iter = project2.keySet().iterator();
		while (proj2Iter.hasNext()){
			ProjectComponent componentString = proj2Iter.next();
			String proj1Val = project1.get(componentString);
			String proj2Val = project2.get(componentString);
			if (!proj2Val.equals(proj1Val)){
				set.add(componentString);
			}
		}
		return set.toArray(new ProjectComponent[0]);
	}
}
